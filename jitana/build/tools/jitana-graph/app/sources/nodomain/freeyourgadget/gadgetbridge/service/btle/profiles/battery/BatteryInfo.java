package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.battery;

import android.os.Parcel;
import android.os.Parcelable;

public class BatteryInfo implements Parcelable {
    public static final Parcelable.Creator<BatteryInfo> CREATOR = new Parcelable.Creator<BatteryInfo>() {
        public BatteryInfo createFromParcel(Parcel in) {
            return new BatteryInfo(in);
        }

        public BatteryInfo[] newArray(int size) {
            return new BatteryInfo[size];
        }
    };
    private int percentCharged;

    public BatteryInfo() {
    }

    protected BatteryInfo(Parcel in) {
        this.percentCharged = in.readInt();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.percentCharged);
    }

    public int describeContents() {
        return 0;
    }

    public int getPercentCharged() {
        return this.percentCharged;
    }

    public void setPercentCharged(int percentCharged2) {
        this.percentCharged = percentCharged2;
    }
}
