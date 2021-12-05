package cyanogenmod.profiles;

import android.content.Context;
import android.media.AudioManager;
import android.os.Parcel;
import android.os.Parcelable;
import cyanogenmod.p007os.Concierge;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class RingModeSettings implements Parcelable {
    public static final Parcelable.Creator<RingModeSettings> CREATOR = new Parcelable.Creator<RingModeSettings>() {
        public RingModeSettings createFromParcel(Parcel in) {
            return new RingModeSettings(in);
        }

        public RingModeSettings[] newArray(int size) {
            return new RingModeSettings[size];
        }
    };
    public static final String RING_MODE_MUTE = "mute";
    public static final String RING_MODE_NORMAL = "normal";
    public static final String RING_MODE_VIBRATE = "vibrate";
    private boolean mDirty;
    private boolean mOverride;
    private String mValue;

    public RingModeSettings(Parcel parcel) {
        readFromParcel(parcel);
    }

    public RingModeSettings() {
        this(RING_MODE_NORMAL, false);
    }

    public RingModeSettings(String value, boolean override) {
        this.mValue = value;
        this.mOverride = override;
        this.mDirty = false;
    }

    public String getValue() {
        return this.mValue;
    }

    public void setValue(String value) {
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
            int ringerMode = 2;
            if (this.mValue.equals(RING_MODE_MUTE)) {
                ringerMode = 0;
            } else if (this.mValue.equals("vibrate")) {
                ringerMode = 1;
            }
            ((AudioManager) context.getSystemService("audio")).setRingerModeInternal(ringerMode);
        }
    }

    public static RingModeSettings fromXml(XmlPullParser xpp, Context context) throws XmlPullParserException, IOException {
        int event = xpp.next();
        RingModeSettings ringModeDescriptor = new RingModeSettings();
        while (true) {
            if ((event == 3 || event == 1) && xpp.getName().equals("ringModeDescriptor")) {
                return ringModeDescriptor;
            }
            if (event == 2) {
                String name = xpp.getName();
                if (name.equals("value")) {
                    ringModeDescriptor.mValue = xpp.nextText();
                } else if (name.equals("override")) {
                    ringModeDescriptor.mOverride = Boolean.parseBoolean(xpp.nextText());
                }
            } else if (event == 1) {
                throw new IOException("Premature end of file while parsing ring mode settings");
            }
            event = xpp.next();
        }
    }

    public void getXmlString(StringBuilder builder, Context context) {
        builder.append("<ringModeDescriptor>\n<value>");
        builder.append(this.mValue);
        builder.append("</value>\n<override>");
        builder.append(this.mOverride);
        builder.append("</override>\n</ringModeDescriptor>\n");
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(dest);
        dest.writeInt(this.mOverride ? 1 : 0);
        dest.writeString(this.mValue);
        dest.writeInt(this.mDirty ? 1 : 0);
        parcelInfo.complete();
    }

    public void readFromParcel(Parcel in) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(in);
        if (parcelInfo.getParcelVersion() >= 2) {
            boolean z = true;
            this.mOverride = in.readInt() != 0;
            this.mValue = in.readString();
            if (in.readInt() == 0) {
                z = false;
            }
            this.mDirty = z;
        }
        parcelInfo.complete();
    }
}
