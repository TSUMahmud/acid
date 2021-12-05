package cyanogenmod.hardware;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.Locale;

public class HSIC implements Parcelable {
    public static final Parcelable.Creator<HSIC> CREATOR = new Parcelable.Creator<HSIC>() {
        public HSIC createFromParcel(Parcel in) {
            float[] fromParcel = new float[5];
            in.readFloatArray(fromParcel);
            return HSIC.fromFloatArray(fromParcel);
        }

        public HSIC[] newArray(int size) {
            return new HSIC[size];
        }
    };
    private final float mContrast;
    private final float mHue;
    private final float mIntensity;
    private final float mSaturation;
    private final float mSaturationThreshold;

    public HSIC(float hue, float saturation, float intensity, float contrast, float saturationThreshold) {
        this.mHue = hue;
        this.mSaturation = saturation;
        this.mIntensity = intensity;
        this.mContrast = contrast;
        this.mSaturationThreshold = saturationThreshold;
    }

    public float getHue() {
        return this.mHue;
    }

    public float getSaturation() {
        return this.mSaturation;
    }

    public float getIntensity() {
        return this.mIntensity;
    }

    public float getContrast() {
        return this.mContrast;
    }

    public float getSaturationThreshold() {
        return this.mSaturationThreshold;
    }

    public String flatten() {
        return String.format(Locale.US, "%f|%f|%f|%f|%f", new Object[]{Float.valueOf(this.mHue), Float.valueOf(this.mSaturation), Float.valueOf(this.mIntensity), Float.valueOf(this.mContrast), Float.valueOf(this.mSaturationThreshold)});
    }

    public static HSIC unflattenFrom(String flat) throws NumberFormatException {
        String[] unflat = TextUtils.split(flat, "\\|");
        if (unflat.length == 4 || unflat.length == 5) {
            return new HSIC(Float.parseFloat(unflat[0]), Float.parseFloat(unflat[1]), Float.parseFloat(unflat[2]), Float.parseFloat(unflat[3]), unflat.length == 5 ? Float.parseFloat(unflat[4]) : 0.0f);
        }
        throw new NumberFormatException("Failed to unflatten HSIC values: " + flat);
    }

    public int[] toRGB() {
        int c = Color.HSVToColor(toFloatArray());
        return new int[]{Color.red(c), Color.green(c), Color.blue(c)};
    }

    public float[] toFloatArray() {
        return new float[]{this.mHue, this.mSaturation, this.mIntensity, this.mContrast, this.mSaturationThreshold};
    }

    public static HSIC fromFloatArray(float[] hsic) {
        if (hsic.length == 5) {
            return new HSIC(hsic[0], hsic[1], hsic[2], hsic[3], hsic[4]);
        }
        if (hsic.length == 4) {
            return new HSIC(hsic[0], hsic[1], hsic[2], hsic[3], 0.0f);
        }
        return null;
    }

    public String toString() {
        return String.format(Locale.US, "HSIC={ hue=%f saturation=%f intensity=%f contrast=%f saturationThreshold=%f }", new Object[]{Float.valueOf(this.mHue), Float.valueOf(this.mSaturation), Float.valueOf(this.mIntensity), Float.valueOf(this.mContrast), Float.valueOf(this.mSaturationThreshold)});
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloatArray(toFloatArray());
    }
}
