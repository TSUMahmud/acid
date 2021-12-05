package cyanogenmod.profiles;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import cyanogenmod.p007os.Concierge;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class AirplaneModeSettings implements Parcelable {
    public static final Parcelable.Creator<AirplaneModeSettings> CREATOR = new Parcelable.Creator<AirplaneModeSettings>() {
        public AirplaneModeSettings createFromParcel(Parcel in) {
            return new AirplaneModeSettings(in);
        }

        public AirplaneModeSettings[] newArray(int size) {
            return new AirplaneModeSettings[size];
        }
    };
    private boolean mDirty;
    private boolean mOverride;
    private int mValue;

    public static class BooleanState {
        public static final int STATE_DISALED = 0;
        public static final int STATE_ENABLED = 1;
    }

    public AirplaneModeSettings(Parcel parcel) {
        readFromParcel(parcel);
    }

    public AirplaneModeSettings() {
        this(0, false);
    }

    public AirplaneModeSettings(int value, boolean override) {
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
            boolean z = false;
            if (Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) != this.mValue) {
                Settings.Global.putInt(context.getContentResolver(), "airplane_mode_on", this.mValue);
                Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
                if (this.mValue == 1) {
                    z = true;
                }
                intent.putExtra("state", z);
                context.sendBroadcast(intent);
            }
        }
    }

    public static AirplaneModeSettings fromXml(XmlPullParser xpp, Context context) throws XmlPullParserException, IOException {
        int event = xpp.next();
        AirplaneModeSettings airplaneModeDescriptor = new AirplaneModeSettings();
        while (true) {
            if ((event == 3 || event == 1) && xpp.getName().equals("airplaneModeDescriptor")) {
                return airplaneModeDescriptor;
            }
            if (event == 2) {
                String name = xpp.getName();
                if (name.equals("value")) {
                    airplaneModeDescriptor.mValue = Integer.parseInt(xpp.nextText());
                } else if (name.equals("override")) {
                    airplaneModeDescriptor.mOverride = Boolean.parseBoolean(xpp.nextText());
                }
            } else if (event == 1) {
                throw new IOException("Premature end of file while parsing airplane mode settings");
            }
            event = xpp.next();
        }
    }

    public void getXmlString(StringBuilder builder, Context context) {
        builder.append("<airplaneModeDescriptor>\n<value>");
        builder.append(this.mValue);
        builder.append("</value>\n<override>");
        builder.append(this.mOverride);
        builder.append("</override>\n</airplaneModeDescriptor>\n");
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
