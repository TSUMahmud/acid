package cyanogenmod.media;

import android.os.Parcel;
import android.os.Parcelable;
import cyanogenmod.p007os.Concierge;
import java.util.Objects;

public final class AudioSessionInfo implements Parcelable {
    public static final Parcelable.Creator<AudioSessionInfo> CREATOR = new Parcelable.Creator<AudioSessionInfo>() {
        public AudioSessionInfo createFromParcel(Parcel source) {
            return new AudioSessionInfo(source);
        }

        public AudioSessionInfo[] newArray(int size) {
            return new AudioSessionInfo[size];
        }
    };
    private final int mChannelMask;
    private final int mFlags;
    private final int mSessionId;
    private final int mStream;
    private final int mUid;

    public AudioSessionInfo(int sessionId, int stream, int flags, int channelMask, int uid) {
        this.mSessionId = sessionId;
        this.mStream = stream;
        this.mFlags = flags;
        this.mChannelMask = channelMask;
        this.mUid = uid;
    }

    private AudioSessionInfo(Parcel in) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(in);
        int parcelVersion = parcelInfo.getParcelVersion();
        this.mSessionId = in.readInt();
        this.mStream = in.readInt();
        this.mFlags = in.readInt();
        this.mChannelMask = in.readInt();
        this.mUid = in.readInt();
        parcelInfo.complete();
    }

    public int getSessionId() {
        return this.mSessionId;
    }

    public int getStream() {
        return this.mStream;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public int getChannelMask() {
        return this.mChannelMask;
    }

    public int getUid() {
        return this.mUid;
    }

    public String toString() {
        return String.format("audioSessionInfo[sessionId=%d, stream=%d, flags=%d, channelMask=%d, uid=%d", new Object[]{Integer.valueOf(this.mSessionId), Integer.valueOf(this.mStream), Integer.valueOf(this.mFlags), Integer.valueOf(this.mChannelMask), Integer.valueOf(this.mUid)});
    }

    public int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(this.mSessionId), Integer.valueOf(this.mStream), Integer.valueOf(this.mFlags), Integer.valueOf(this.mChannelMask), Integer.valueOf(this.mUid)});
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AudioSessionInfo)) {
            return false;
        }
        AudioSessionInfo other = (AudioSessionInfo) obj;
        if (this == other || (this.mSessionId == other.mSessionId && this.mStream == other.mStream && this.mFlags == other.mFlags && this.mChannelMask == other.mChannelMask && this.mUid == other.mUid)) {
            return true;
        }
        return false;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(dest);
        dest.writeInt(this.mSessionId);
        dest.writeInt(this.mStream);
        dest.writeInt(this.mFlags);
        dest.writeInt(this.mChannelMask);
        dest.writeInt(this.mUid);
        parcelInfo.complete();
    }
}
