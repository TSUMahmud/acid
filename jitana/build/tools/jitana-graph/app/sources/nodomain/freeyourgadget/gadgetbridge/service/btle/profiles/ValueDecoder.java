package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles;

import android.bluetooth.BluetoothGattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCharacteristic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValueDecoder {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) ValueDecoder.class);

    public static int decodePercent(BluetoothGattCharacteristic characteristic) {
        int percent = characteristic.getIntValue(17, 0).intValue();
        if (percent <= 100 && percent >= 0) {
            return percent;
        }
        Logger logger = LOG;
        logger.warn("Unexpected percent value: " + percent + ": " + GattCharacteristic.toString(characteristic));
        return Math.min(100, Math.max(0, percent));
    }
}
