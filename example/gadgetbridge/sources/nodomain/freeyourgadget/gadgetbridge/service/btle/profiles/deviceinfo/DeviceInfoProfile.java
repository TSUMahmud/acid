package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.deviceinfo;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.AbstractBleProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceInfoProfile<T extends AbstractBTLEDeviceSupport> extends AbstractBleProfile {
    public static final String ACTION_DEVICE_INFO = (ACTION_PREFIX + EXTRA_DEVICE_INFO);
    private static final String ACTION_PREFIX;
    public static final String EXTRA_DEVICE_INFO = "DEVICE_INFO";
    private static final Logger LOG;
    public static final UUID SERVICE_UUID = GattService.UUID_SERVICE_DEVICE_INFORMATION;
    public static final UUID UUID_CHARACTERISTIC_FIRMWARE_REVISION_STRING = GattCharacteristic.UUID_CHARACTERISTIC_FIRMWARE_REVISION_STRING;
    public static final UUID UUID_CHARACTERISTIC_HARDWARE_REVISION_STRING = GattCharacteristic.UUID_CHARACTERISTIC_HARDWARE_REVISION_STRING;

    /* renamed from: UUID_CHARACTERISTIC_IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST */
    public static final UUID f173x32bb8283 = GattCharacteristic.f167x32bb8283;
    public static final UUID UUID_CHARACTERISTIC_MANUFACTURER_NAME_STRING = GattCharacteristic.UUID_CHARACTERISTIC_MANUFACTURER_NAME_STRING;
    public static final UUID UUID_CHARACTERISTIC_MODEL_NUMBER_STRING = GattCharacteristic.UUID_CHARACTERISTIC_MODEL_NUMBER_STRING;
    public static final UUID UUID_CHARACTERISTIC_PNP_ID = GattCharacteristic.UUID_CHARACTERISTIC_PNP_ID;
    public static final UUID UUID_CHARACTERISTIC_SERIAL_NUMBER_STRING = GattCharacteristic.UUID_CHARACTERISTIC_SERIAL_NUMBER_STRING;
    public static final UUID UUID_CHARACTERISTIC_SOFTWARE_REVISION_STRING = GattCharacteristic.UUID_CHARACTERISTIC_SOFTWARE_REVISION_STRING;
    public static final UUID UUID_CHARACTERISTIC_SYSTEM_ID = GattCharacteristic.UUID_CHARACTERISTIC_SYSTEM_ID;
    private final DeviceInfo deviceInfo = new DeviceInfo();

    static {
        Class<DeviceInfoProfile> cls = DeviceInfoProfile.class;
        LOG = LoggerFactory.getLogger((Class<?>) cls);
        ACTION_PREFIX = cls.getName() + "_";
    }

    public DeviceInfoProfile(T support) {
        super(support);
    }

    public void requestDeviceInfo(TransactionBuilder builder) {
        builder.read(getCharacteristic(UUID_CHARACTERISTIC_MANUFACTURER_NAME_STRING)).read(getCharacteristic(UUID_CHARACTERISTIC_MODEL_NUMBER_STRING)).read(getCharacteristic(UUID_CHARACTERISTIC_SERIAL_NUMBER_STRING)).read(getCharacteristic(UUID_CHARACTERISTIC_HARDWARE_REVISION_STRING)).read(getCharacteristic(UUID_CHARACTERISTIC_FIRMWARE_REVISION_STRING)).read(getCharacteristic(UUID_CHARACTERISTIC_SOFTWARE_REVISION_STRING)).read(getCharacteristic(UUID_CHARACTERISTIC_SYSTEM_ID)).read(getCharacteristic(f173x32bb8283)).read(getCharacteristic(UUID_CHARACTERISTIC_PNP_ID));
    }

    public boolean onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status == 0) {
            UUID charUuid = characteristic.getUuid();
            if (charUuid.equals(UUID_CHARACTERISTIC_MANUFACTURER_NAME_STRING)) {
                handleManufacturerName(gatt, characteristic);
                return true;
            } else if (charUuid.equals(UUID_CHARACTERISTIC_MODEL_NUMBER_STRING)) {
                handleModelNumber(gatt, characteristic);
                return true;
            } else if (charUuid.equals(UUID_CHARACTERISTIC_SERIAL_NUMBER_STRING)) {
                handleSerialNumber(gatt, characteristic);
                return true;
            } else if (charUuid.equals(UUID_CHARACTERISTIC_HARDWARE_REVISION_STRING)) {
                handleHardwareRevision(gatt, characteristic);
                return true;
            } else if (charUuid.equals(UUID_CHARACTERISTIC_FIRMWARE_REVISION_STRING)) {
                handleFirmwareRevision(gatt, characteristic);
                return true;
            } else if (charUuid.equals(UUID_CHARACTERISTIC_SOFTWARE_REVISION_STRING)) {
                handleSoftwareRevision(gatt, characteristic);
                return true;
            } else if (charUuid.equals(UUID_CHARACTERISTIC_SYSTEM_ID)) {
                handleSystemId(gatt, characteristic);
                return true;
            } else if (charUuid.equals(f173x32bb8283)) {
                handleRegulatoryCertificationData(gatt, characteristic);
                return true;
            } else if (charUuid.equals(UUID_CHARACTERISTIC_PNP_ID)) {
                handlePnpId(gatt, characteristic);
                return true;
            } else {
                Logger logger = LOG;
                logger.info("Unexpected onCharacteristicRead: " + GattCharacteristic.toString(characteristic));
                return false;
            }
        } else {
            Logger logger2 = LOG;
            logger2.warn("error reading from characteristic:" + GattCharacteristic.toString(characteristic));
            return false;
        }
    }

    private void handleManufacturerName(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        this.deviceInfo.setManufacturerName(characteristic.getStringValue(0).trim());
        notify(createIntent(this.deviceInfo));
    }

    private void handleModelNumber(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        this.deviceInfo.setModelNumber(characteristic.getStringValue(0).trim());
        notify(createIntent(this.deviceInfo));
    }

    private void handleSerialNumber(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        this.deviceInfo.setSerialNumber(characteristic.getStringValue(0).trim());
        notify(createIntent(this.deviceInfo));
    }

    private void handleHardwareRevision(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        this.deviceInfo.setHardwareRevision(characteristic.getStringValue(0).trim());
        notify(createIntent(this.deviceInfo));
    }

    private void handleFirmwareRevision(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        this.deviceInfo.setFirmwareRevision(characteristic.getStringValue(0).trim());
        notify(createIntent(this.deviceInfo));
    }

    private void handleSoftwareRevision(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        this.deviceInfo.setSoftwareRevision(characteristic.getStringValue(0).trim());
        notify(createIntent(this.deviceInfo));
    }

    private void handleSystemId(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        this.deviceInfo.setSystemId(characteristic.getStringValue(0).trim());
        notify(createIntent(this.deviceInfo));
    }

    private void handleRegulatoryCertificationData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
    }

    private void handlePnpId(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (characteristic.getValue().length == 7) {
            notify(createIntent(this.deviceInfo));
        }
    }

    private Intent createIntent(DeviceInfo deviceInfo2) {
        Intent intent = new Intent(ACTION_DEVICE_INFO);
        intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo2);
        return intent;
    }
}
