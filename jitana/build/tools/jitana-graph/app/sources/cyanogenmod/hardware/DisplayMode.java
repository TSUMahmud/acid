package cyanogenmod.hardware;

import android.os.Parcel;
import android.os.Parcelable;
import cyanogenmod.p007os.Concierge;

public class DisplayMode implements Parcelable {
    public static final Parcelable.Creator<DisplayMode> CREATOR = new Parcelable.Creator<DisplayMode>() {
        public DisplayMode createFromParcel(Parcel in) {
            return new DisplayMode(in);
        }

        public DisplayMode[] newArray(int size) {
            return new DisplayMode[size];
        }
    };

    /* renamed from: id */
    public final int f105id;
    public final String name;

    public DisplayMode(int id, String name2) {
        this.f105id = id;
        this.name = name2;
    }

    private DisplayMode(Parcel parcel) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(parcel);
        int tmpId = -1;
        String tmpName = null;
        if (parcelInfo.getParcelVersion() >= 2) {
            tmpId = parcel.readInt();
            if (parcel.readInt() != 0) {
                tmpName = parcel.readString();
            }
        }
        this.f105id = tmpId;
        this.name = tmpName;
        parcelInfo.complete();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(out);
        out.writeInt(this.f105id);
        if (this.name != null) {
            out.writeInt(1);
            out.writeString(this.name);
        } else {
            out.writeInt(0);
        }
        parcelInfo.complete();
    }
}
