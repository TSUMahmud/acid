package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import java.util.GregorianCalendar;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandDateConverter;
import nodomain.freeyourgadget.gadgetbridge.model.BatteryState;

public class BatteryInfo extends AbstractInfo {
    public static final byte DEVICE_BATTERY_CHARGE_OFF = 4;
    public static final byte DEVICE_BATTERY_CHARGING = 2;
    public static final byte DEVICE_BATTERY_CHARGING_FULL = 3;
    public static final byte DEVICE_BATTERY_LOW = 1;
    public static final byte DEVICE_BATTERY_NORMAL = 0;

    public BatteryInfo(byte[] data) {
        super(data);
    }

    public int getLevelInPercent() {
        if (this.mData.length >= 1) {
            return this.mData[0];
        }
        return 50;
    }

    public BatteryState getState() {
        if (this.mData.length >= 10) {
            byte value = this.mData[9];
            if (value == 0) {
                return BatteryState.BATTERY_NORMAL;
            }
            if (value == 1) {
                return BatteryState.BATTERY_LOW;
            }
            if (value == 2) {
                return BatteryState.BATTERY_CHARGING;
            }
            if (value == 3) {
                return BatteryState.BATTERY_CHARGING_FULL;
            }
            if (value == 4) {
                return BatteryState.BATTERY_NOT_CHARGING_FULL;
            }
        }
        return BatteryState.UNKNOWN;
    }

    public GregorianCalendar getLastChargeTime(String deviceAddress) {
        GregorianCalendar lastCharge = MiBandDateConverter.createCalendar();
        if (this.mData.length < 10) {
            return lastCharge;
        }
        return MiBandDateConverter.rawBytesToCalendar(new byte[]{this.mData[1], this.mData[2], this.mData[3], this.mData[4], this.mData[5], this.mData[6]}, deviceAddress);
    }

    public int getNumCharges() {
        if (this.mData.length >= 10) {
            return (this.mData[7] & 255) | ((this.mData[8] & 255) << 8);
        }
        return -1;
    }
}
