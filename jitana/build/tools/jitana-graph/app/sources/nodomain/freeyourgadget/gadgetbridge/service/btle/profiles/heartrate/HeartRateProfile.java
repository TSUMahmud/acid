package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.heartrate;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.AbstractBleProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartRateProfile<T extends AbstractBTLEDeviceSupport> extends AbstractBleProfile<T> {
    public static final int ERR_CONTROL_POINT_NOT_SUPPORTED = 128;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) HeartRateProfile.class);

    public HeartRateProfile(T support) {
        super(support);
    }

    public void resetEnergyExpended(TransactionBuilder builder) {
        writeToControlPoint((byte) 1, builder);
    }

    /* access modifiers changed from: protected */
    public void writeToControlPoint(byte value, TransactionBuilder builder) {
        writeToControlPoint(new byte[]{value}, builder);
    }

    /* access modifiers changed from: protected */
    public void writeToControlPoint(byte[] value, TransactionBuilder builder) {
        builder.write(getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT), value);
    }

    public void requestBodySensorLocation(TransactionBuilder builder) {
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        int format;
        if (!GattCharacteristic.UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            return false;
        }
        if ((characteristic.getProperties() & 1) != 0) {
            format = 18;
        } else {
            format = 17;
        }
        int heartRate = characteristic.getIntValue(format, 1).intValue();
        Logger logger = LOG;
        logger.info("Heart rate: " + heartRate);
        return false;
    }
}
