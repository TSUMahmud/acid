package nodomain.freeyourgadget.gadgetbridge.service.devices.huami;

import java.util.GregorianCalendar;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandDateConverter;
import nodomain.freeyourgadget.gadgetbridge.model.BatteryState;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.AbstractInfo;

public class HuamiBatteryInfo extends AbstractInfo {
    public static final byte DEVICE_BATTERY_CHARGING = 1;
    public static final byte DEVICE_BATTERY_NORMAL = 0;

    public HuamiBatteryInfo(byte[] data) {
        super(data);
    }

    public int getLevelInPercent() {
        if (this.mData.length >= 2) {
            return this.mData[1];
        }
        return 50;
    }

    public BatteryState getState() {
        if (this.mData.length >= 3) {
            byte value = this.mData[2];
            if (value == 0) {
                return BatteryState.BATTERY_NORMAL;
            }
            if (value == 1) {
                return BatteryState.BATTERY_CHARGING;
            }
        }
        return BatteryState.UNKNOWN;
    }

    public int getLastChargeLevelInParcent() {
        if (this.mData.length >= 20) {
            return this.mData[19];
        }
        return 50;
    }

    public GregorianCalendar getLastChargeTime() {
        GregorianCalendar lastCharge = MiBandDateConverter.createCalendar();
        if (this.mData.length < 18) {
            return lastCharge;
        }
        return BLETypeConversions.rawBytesToCalendar(new byte[]{this.mData[10], this.mData[11], this.mData[12], this.mData[13], this.mData[14], this.mData[15], this.mData[16], this.mData[17]});
    }

    public int getNumCharges() {
        return -1;
    }
}
