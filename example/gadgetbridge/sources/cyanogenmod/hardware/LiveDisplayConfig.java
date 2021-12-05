package cyanogenmod.hardware;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Range;
import cyanogenmod.p007os.Concierge;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class LiveDisplayConfig implements Parcelable {
    public static final Parcelable.Creator<LiveDisplayConfig> CREATOR = new Parcelable.Creator<LiveDisplayConfig>() {
        public LiveDisplayConfig createFromParcel(Parcel in) {
            return new LiveDisplayConfig(in);
        }

        public LiveDisplayConfig[] newArray(int size) {
            return new LiveDisplayConfig[size];
        }
    };
    private final BitSet mAllModes;
    private final BitSet mCapabilities;
    private final Range<Integer> mColorBalanceRange;
    private final Range<Integer> mColorTemperatureRange;
    private final Range<Float> mContrastRange;
    private final boolean mDefaultAutoContrast;
    private final boolean mDefaultAutoOutdoorMode;
    private final boolean mDefaultCABC;
    private final boolean mDefaultColorEnhancement;
    private final int mDefaultDayTemperature;
    private final int mDefaultMode;
    private final int mDefaultNightTemperature;
    private final Range<Float> mHueRange;
    private final Range<Float> mIntensityRange;
    private final Range<Float> mSaturationRange;
    private final Range<Float> mSaturationThresholdRange;

    public LiveDisplayConfig(BitSet capabilities, int defaultMode, int defaultDayTemperature, int defaultNightTemperature, boolean defaultAutoOutdoorMode, boolean defaultAutoContrast, boolean defaultCABC, boolean defaultColorEnhancement, Range<Integer> colorTemperatureRange, Range<Integer> colorBalanceRange, Range<Float> hueRange, Range<Float> saturationRange, Range<Float> intensityRange, Range<Float> contrastRange, Range<Float> saturationThresholdRange) {
        this.mAllModes = new BitSet();
        this.mCapabilities = (BitSet) capabilities.clone();
        this.mAllModes.set(0, 4);
        this.mDefaultMode = defaultMode;
        this.mDefaultDayTemperature = defaultDayTemperature;
        this.mDefaultNightTemperature = defaultNightTemperature;
        this.mDefaultAutoContrast = defaultAutoContrast;
        this.mDefaultAutoOutdoorMode = defaultAutoOutdoorMode;
        this.mDefaultCABC = defaultCABC;
        this.mDefaultColorEnhancement = defaultColorEnhancement;
        this.mColorTemperatureRange = colorTemperatureRange;
        this.mColorBalanceRange = colorBalanceRange;
        this.mHueRange = hueRange;
        this.mSaturationRange = saturationRange;
        this.mIntensityRange = intensityRange;
        this.mContrastRange = contrastRange;
        this.mSaturationThresholdRange = saturationThresholdRange;
    }

    private LiveDisplayConfig(Parcel parcel) {
        int defaultMode;
        int maxColorBalance;
        int minColorBalance;
        int maxColorTemperature;
        boolean defaultColorEnhancement;
        boolean defaultColorEnhancement2;
        boolean defaultCABC;
        boolean defaultAutoOutdoorMode;
        int defaultNightTemperature;
        int defaultNightTemperature2;
        int defaultDayTemperature;
        this.mAllModes = new BitSet();
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(parcel);
        int parcelableVersion = parcelInfo.getParcelVersion();
        long capabilities = 0;
        float[] paRanges = new float[10];
        if (parcelableVersion >= 6) {
            capabilities = parcel.readLong();
            int defaultMode2 = parcel.readInt();
            int defaultDayTemperature2 = parcel.readInt();
            int defaultNightTemperature3 = parcel.readInt();
            boolean defaultAutoContrast = parcel.readInt() == 1;
            boolean defaultAutoOutdoorMode2 = parcel.readInt() == 1;
            boolean defaultCABC2 = parcel.readInt() == 1;
            boolean defaultColorEnhancement3 = parcel.readInt() == 1;
            int minColorTemperature = parcel.readInt();
            int maxColorTemperature2 = parcel.readInt();
            int minColorBalance2 = parcel.readInt();
            int maxColorBalance2 = parcel.readInt();
            parcel.readFloatArray(paRanges);
            int i = defaultNightTemperature3;
            defaultNightTemperature2 = defaultDayTemperature2;
            defaultDayTemperature = defaultMode2;
            defaultMode = maxColorBalance2;
            maxColorBalance = minColorBalance2;
            minColorBalance = maxColorTemperature2;
            maxColorTemperature = minColorTemperature;
            defaultColorEnhancement = defaultColorEnhancement3;
            defaultColorEnhancement2 = defaultCABC2;
            defaultCABC = defaultAutoOutdoorMode2;
            defaultAutoOutdoorMode = defaultAutoContrast;
            defaultNightTemperature = i;
        } else {
            Parcel parcel2 = parcel;
            defaultNightTemperature2 = -1;
            defaultDayTemperature = 0;
            defaultMode = 0;
            maxColorBalance = 0;
            minColorBalance = 0;
            maxColorTemperature = 0;
            defaultColorEnhancement = false;
            defaultColorEnhancement2 = false;
            defaultCABC = false;
            defaultAutoOutdoorMode = false;
            defaultNightTemperature = -1;
        }
        int i2 = parcelableVersion;
        this.mCapabilities = BitSet.valueOf(new long[]{capabilities});
        this.mAllModes.set(0, 4);
        this.mDefaultMode = defaultDayTemperature;
        this.mDefaultDayTemperature = defaultNightTemperature2;
        this.mDefaultNightTemperature = defaultNightTemperature;
        this.mDefaultAutoContrast = defaultAutoOutdoorMode;
        this.mDefaultAutoOutdoorMode = defaultCABC;
        this.mDefaultCABC = defaultColorEnhancement2;
        this.mDefaultColorEnhancement = defaultColorEnhancement;
        this.mColorTemperatureRange = Range.create(Integer.valueOf(maxColorTemperature), Integer.valueOf(minColorBalance));
        this.mColorBalanceRange = Range.create(Integer.valueOf(maxColorBalance), Integer.valueOf(defaultMode));
        this.mHueRange = Range.create(Float.valueOf(paRanges[0]), Float.valueOf(paRanges[1]));
        this.mSaturationRange = Range.create(Float.valueOf(paRanges[2]), Float.valueOf(paRanges[3]));
        this.mIntensityRange = Range.create(Float.valueOf(paRanges[4]), Float.valueOf(paRanges[5]));
        this.mContrastRange = Range.create(Float.valueOf(paRanges[6]), Float.valueOf(paRanges[7]));
        this.mSaturationThresholdRange = Range.create(Float.valueOf(paRanges[8]), Float.valueOf(paRanges[9]));
        parcelInfo.complete();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("capabilities=");
        sb.append(this.mCapabilities.toString());
        sb.append(" defaultMode=");
        sb.append(this.mDefaultMode);
        sb.append(" defaultDayTemperature=");
        sb.append(this.mDefaultDayTemperature);
        sb.append(" defaultNightTemperature=");
        sb.append(this.mDefaultNightTemperature);
        sb.append(" defaultAutoOutdoorMode=");
        sb.append(this.mDefaultAutoOutdoorMode);
        sb.append(" defaultAutoContrast=");
        sb.append(this.mDefaultAutoContrast);
        sb.append(" defaultCABC=");
        sb.append(this.mDefaultCABC);
        sb.append(" defaultColorEnhancement=");
        sb.append(this.mDefaultColorEnhancement);
        sb.append(" colorTemperatureRange=");
        sb.append(this.mColorTemperatureRange);
        if (this.mCapabilities.get(16)) {
            sb.append(" colorBalanceRange=");
            sb.append(this.mColorBalanceRange);
        }
        if (this.mCapabilities.get(17)) {
            sb.append(" hueRange=");
            sb.append(this.mHueRange);
            sb.append(" saturationRange=");
            sb.append(this.mSaturationRange);
            sb.append(" intensityRange=");
            sb.append(this.mIntensityRange);
            sb.append(" contrastRange=");
            sb.append(this.mContrastRange);
            sb.append(" saturationThresholdRange=");
            sb.append(this.mSaturationThresholdRange);
        }
        return sb.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(out);
        long[] caps = this.mCapabilities.toLongArray();
        out.writeLong((caps == null || caps.length <= 0) ? 0 : caps[0]);
        out.writeInt(this.mDefaultMode);
        out.writeInt(this.mDefaultDayTemperature);
        out.writeInt(this.mDefaultNightTemperature);
        out.writeInt(this.mDefaultAutoContrast ? 1 : 0);
        out.writeInt(this.mDefaultAutoOutdoorMode ? 1 : 0);
        out.writeInt(this.mDefaultCABC ? 1 : 0);
        out.writeInt(this.mDefaultColorEnhancement ? 1 : 0);
        out.writeInt(this.mColorTemperatureRange.getLower().intValue());
        out.writeInt(this.mColorTemperatureRange.getUpper().intValue());
        out.writeInt(this.mColorBalanceRange.getLower().intValue());
        out.writeInt(this.mColorBalanceRange.getUpper().intValue());
        out.writeFloatArray(new float[]{this.mHueRange.getLower().floatValue(), this.mHueRange.getUpper().floatValue(), this.mSaturationRange.getLower().floatValue(), this.mSaturationRange.getUpper().floatValue(), this.mIntensityRange.getLower().floatValue(), this.mIntensityRange.getUpper().floatValue(), this.mContrastRange.getLower().floatValue(), this.mContrastRange.getUpper().floatValue(), this.mSaturationThresholdRange.getLower().floatValue(), this.mSaturationThresholdRange.getUpper().floatValue()});
        parcelInfo.complete();
    }

    public boolean hasFeature(int feature) {
        return ((feature >= 0 && feature <= 4) || (feature >= 10 && feature <= 17)) && (feature == 0 || this.mCapabilities.get(feature));
    }

    public boolean isAvailable() {
        return !this.mCapabilities.isEmpty();
    }

    public boolean hasModeSupport() {
        return isAvailable() && this.mCapabilities.intersects(this.mAllModes);
    }

    public int getDefaultDayTemperature() {
        return this.mDefaultDayTemperature;
    }

    public int getDefaultNightTemperature() {
        return this.mDefaultNightTemperature;
    }

    public int getDefaultMode() {
        return this.mDefaultMode;
    }

    public boolean getDefaultAutoContrast() {
        return this.mDefaultAutoContrast;
    }

    public boolean getDefaultAutoOutdoorMode() {
        return this.mDefaultAutoOutdoorMode;
    }

    public boolean getDefaultCABC() {
        return this.mDefaultCABC;
    }

    public boolean getDefaultColorEnhancement() {
        return this.mDefaultColorEnhancement;
    }

    public Range<Integer> getColorTemperatureRange() {
        return this.mColorTemperatureRange;
    }

    public Range<Integer> getColorBalanceRange() {
        return this.mColorBalanceRange;
    }

    public Range<Float> getHueRange() {
        return this.mHueRange;
    }

    public Range<Float> getSaturationRange() {
        return this.mSaturationRange;
    }

    public Range<Float> getIntensityRange() {
        return this.mIntensityRange;
    }

    public Range<Float> getContrastRange() {
        return this.mContrastRange;
    }

    public Range<Float> getSaturationThresholdRange() {
        return this.mSaturationThresholdRange;
    }

    public List<Range<Float>> getPictureAdjustmentRanges() {
        return Arrays.asList(new Range[]{this.mHueRange, this.mSaturationRange, this.mIntensityRange, this.mContrastRange, this.mSaturationThresholdRange});
    }
}
