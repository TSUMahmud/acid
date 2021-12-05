package nodomain.freeyourgadget.gadgetbridge.deviceevents;

import java.util.GregorianCalendar;
import nodomain.freeyourgadget.gadgetbridge.model.BatteryState;

public class GBDeviceEventBatteryInfo extends GBDeviceEvent {
    public GregorianCalendar lastChargeTime = null;
    public short level = 50;
    public int numCharges = -1;
    public BatteryState state = BatteryState.UNKNOWN;
    public float voltage = -1.0f;

    public boolean extendedInfoAvailable() {
        if (this.numCharges == -1 || this.lastChargeTime == null) {
            return false;
        }
        return true;
    }
}
