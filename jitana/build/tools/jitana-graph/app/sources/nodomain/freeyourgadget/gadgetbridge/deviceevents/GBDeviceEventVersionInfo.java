package nodomain.freeyourgadget.gadgetbridge.deviceevents;

import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;

public class GBDeviceEventVersionInfo extends GBDeviceEvent {
    public String fwVersion = GBApplication.getContext().getString(C0889R.string.n_a);
    public String hwVersion = GBApplication.getContext().getString(C0889R.string.n_a);

    public String toString() {
        return super.toString() + "fwVersion: " + this.fwVersion + "; hwVersion: " + this.hwVersion;
    }
}
