package cyanogenmod.profiles;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import cyanogenmod.p007os.Concierge;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class StreamSettings implements Parcelable {
    public static final Parcelable.Creator<StreamSettings> CREATOR = new Parcelable.Creator<StreamSettings>() {
        public StreamSettings createFromParcel(Parcel in) {
            return new StreamSettings(in);
        }

        public StreamSettings[] newArray(int size) {
            return new StreamSettings[size];
        }
    };
    private boolean mDirty;
    private boolean mOverride;
    private int mStreamId;
    private int mValue;

    public StreamSettings(Parcel parcel) {
        readFromParcel(parcel);
    }

    public StreamSettings(int streamId) {
        this(streamId, 0, false);
    }

    public StreamSettings(int streamId, int value, boolean override) {
        this.mStreamId = streamId;
        this.mValue = value;
        this.mOverride = override;
        this.mDirty = false;
    }

    public int getStreamId() {
        return this.mStreamId;
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

    public static StreamSettings fromXml(XmlPullParser xpp, Context context) throws XmlPullParserException, IOException {
        int event = xpp.next();
        StreamSettings streamDescriptor = new StreamSettings(0);
        while (true) {
            if (event == 3 && xpp.getName().equals("streamDescriptor")) {
                return streamDescriptor;
            }
            if (event == 2) {
                String name = xpp.getName();
                if (name.equals("streamId")) {
                    streamDescriptor.mStreamId = Integer.parseInt(xpp.nextText());
                } else if (name.equals("value")) {
                    streamDescriptor.mValue = Integer.parseInt(xpp.nextText());
                } else if (name.equals("override")) {
                    streamDescriptor.mOverride = Boolean.parseBoolean(xpp.nextText());
                }
            } else if (event == 1) {
                throw new IOException("Premature end of file while parsing stream settings");
            }
            event = xpp.next();
        }
    }

    public void getXmlString(StringBuilder builder, Context context) {
        builder.append("<streamDescriptor>\n<streamId>");
        builder.append(this.mStreamId);
        builder.append("</streamId>\n<value>");
        builder.append(this.mValue);
        builder.append("</value>\n<override>");
        builder.append(this.mOverride);
        builder.append("</override>\n</streamDescriptor>\n");
        this.mDirty = false;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(dest);
        dest.writeInt(this.mStreamId);
        dest.writeInt(this.mOverride ? 1 : 0);
        dest.writeInt(this.mValue);
        dest.writeInt(this.mDirty ? 1 : 0);
        parcelInfo.complete();
    }

    public void readFromParcel(Parcel in) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(in);
        if (parcelInfo.getParcelVersion() >= 2) {
            this.mStreamId = in.readInt();
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
