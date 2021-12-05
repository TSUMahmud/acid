package cyanogenmod.profiles;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import cyanogenmod.p007os.Concierge;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class BrightnessSettings implements Parcelable {
    public static final Parcelable.Creator<BrightnessSettings> CREATOR = new Parcelable.Creator<BrightnessSettings>() {
        public BrightnessSettings createFromParcel(Parcel in) {
            return new BrightnessSettings(in);
        }

        public BrightnessSettings[] newArray(int size) {
            return new BrightnessSettings[size];
        }
    };
    private boolean mDirty;
    private boolean mOverride;
    private int mValue;

    public BrightnessSettings(Parcel parcel) {
        readFromParcel(parcel);
    }

    public BrightnessSettings() {
        this(0, false);
    }

    public BrightnessSettings(int value, boolean override) {
        this.mValue = value;
        this.mOverride = override;
        this.mDirty = false;
    }

    public int getValue() {
        return this.mValue;
    }

    public void setValue(int value) {
        this.mValue = value;
        this.mDirty = true;
    }

    public void setOverride(boolean override) {
        this.mOverride = override;
        this.mDirty = true;
    }

    public boolean isOverride() {
        return this.mOverride;
    }

    public boolean isDirty() {
        return this.mDirty;
    }

    public void processOverride(Context context) {
        if (isOverride()) {
            boolean automatic = false;
            if (Settings.System.getInt(context.getContentResolver(), "screen_brightness_mode", 0) == 1) {
                automatic = true;
            }
            if (automatic) {
                float current = Settings.System.getFloat(context.getContentResolver(), "screen_auto_brightness_adj", -2.0f);
                float adj = (((float) this.mValue) / 127.5f) - 1.0f;
                if (current != adj) {
                    Settings.System.putFloat(context.getContentResolver(), "screen_auto_brightness_adj", adj);
                }
            } else if (Settings.System.getInt(context.getContentResolver(), "screen_brightness", -1) != this.mValue) {
                Settings.System.putInt(context.getContentResolver(), "screen_brightness", this.mValue);
            }
        }
    }

    public static BrightnessSettings fromXml(XmlPullParser xpp, Context context) throws XmlPullParserException, IOException {
        int event = xpp.next();
        BrightnessSettings brightnessDescriptor = new BrightnessSettings();
        while (true) {
            if (event == 3 && xpp.getName().equals("brightnessDescriptor")) {
                return brightnessDescriptor;
            }
            if (event == 2) {
                String name = xpp.getName();
                if (name.equals("value")) {
                    brightnessDescriptor.mValue = Integer.parseInt(xpp.nextText());
                } else if (name.equals("override")) {
                    brightnessDescriptor.mOverride = Boolean.parseBoolean(xpp.nextText());
                }
            }
            event = xpp.next();
        }
    }

    public void getXmlString(StringBuilder builder, Context context) {
        builder.append("<brightnessDescriptor>\n<value>");
        builder.append(this.mValue);
        builder.append("</value>\n<override>");
        builder.append(this.mOverride);
        builder.append("</override>\n</brightnessDescriptor>\n");
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(dest);
        dest.writeInt(this.mOverride ? 1 : 0);
        dest.writeInt(this.mValue);
        dest.writeInt(this.mDirty ? 1 : 0);
        parcelInfo.complete();
    }

    public void readFromParcel(Parcel in) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(in);
        if (parcelInfo.getParcelVersion() >= 2) {
            boolean z = true;
            this.mOverride = in.readInt() != 0;
            this.mValue = in.readInt();
            if (in.readInt() == 0) {
                z = false;
            }
            this.mDirty = z;
        }
        parcelInfo.complete();
    }
}
