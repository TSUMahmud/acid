package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.battery;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.AbstractBleProfile;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.ValueDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatteryInfoProfile<T extends AbstractBTLEDeviceSupport> extends AbstractBleProfile {
    public static final String ACTION_BATTERY_INFO = (ACTION_PREFIX + EXTRA_BATTERY_INFO);
    private static final String ACTION_PREFIX;
    public static final String EXTRA_BATTERY_INFO = "BATTERY_INFO";
    private static final Logger LOG;
    public static final UUID SERVICE_UUID = GattService.UUID_SERVICE_BATTERY_SERVICE;
    public static final UUID UUID_CHARACTERISTIC_BATTERY_LEVEL = GattCharacteristic.UUID_CHARACTERISTIC_BATTERY_LEVEL;
    private final BatteryInfo batteryInfo = new BatteryInfo();

    static {
        Class<BatteryInfoProfile> cls = BatteryInfoProfile.class;
        LOG = LoggerFactory.getLogger((Class<?>) cls);
        ACTION_PREFIX = cls.getName() + "_";
    }

    public BatteryInfoProfile(T support) {
        super(support);
    }

    public void requestBatteryInfo(TransactionBuilder builder) {
        builder.read(getCharacteristic(UUID_CHARACTERISTIC_BATTERY_LEVEL));
    }

    public void enableNotifiy() {
    }

    public boolean onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status != 0) {
            Logger logger = LOG;
            logger.warn("error reading from characteristic:" + GattCharacteristic.toString(characteristic));
            return false;
        } else if (characteristic.getUuid().equals(UUID_CHARACTERISTIC_BATTERY_LEVEL)) {
            handleBatteryLevel(gatt, characteristic);
            return true;
        } else {
            Logger logger2 = LOG;
            logger2.info("Unexpected onCharacteristicRead: " + GattCharacteristic.toString(characteristic));
            return false;
        }
    }

    private void handleBatteryLevel(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        this.batteryInfo.setPercentCharged(ValueDecoder.decodePercent(characteristic));
        notify(createIntent(this.batteryInfo));
    }

    private Intent createIntent(BatteryInfo batteryInfo2) {
        Intent intent = new Intent(ACTION_BATTERY_INFO);
        intent.putExtra(EXTRA_BATTERY_INFO, batteryInfo2);
        return intent;
    }
}
