package nodomain.freeyourgadget.gadgetbridge.service.devices.casiogb6900.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import java.io.IOException;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.devices.casiogb6900.CasioGB6900Constants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEOperation;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceStateAction;
import nodomain.freeyourgadget.gadgetbridge.service.devices.casiogb6900.CasioGB6900DeviceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitOperation extends AbstractBTLEOperation<CasioGB6900DeviceSupport> {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) InitOperation.class);
    private final TransactionBuilder builder;
    private byte[] mBleSettings = null;

    public InitOperation(CasioGB6900DeviceSupport support, TransactionBuilder builder2) {
        super(support);
        this.builder = builder2;
        builder2.setGattCallback(this);
    }

    /* access modifiers changed from: protected */
    public void doPerform() throws IOException {
        this.builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
        TransactionBuilder builder2 = ((CasioGB6900DeviceSupport) getSupport()).createTransactionBuilder("readBleSettings");
        builder2.setGattCallback(this);
        builder2.read(getCharacteristic(CasioGB6900Constants.CASIO_SETTING_FOR_BLE_CHARACTERISTIC_UUID));
        ((CasioGB6900DeviceSupport) getSupport()).performImmediately(builder2);
    }

    public TransactionBuilder performInitialized(String taskName) throws IOException {
        throw new UnsupportedOperationException("This IS the initialization class, you cannot call this method");
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        UUID characteristicUUID = characteristic.getUuid();
        Logger logger = LOG;
        logger.info("Unhandled characteristic changed: " + characteristicUUID);
        return super.onCharacteristicChanged(gatt, characteristic);
    }

    private void configureBleSettings() {
        byte[] bArr = this.mBleSettings;
        bArr[5] = (byte) (300 & 255);
        bArr[6] = (byte) ((300 >> 8) & 255);
        bArr[7] = (byte) (2 & 255);
        bArr[8] = (byte) ((2 >> 8) & 255);
        bArr[9] = 0;
    }

    private void writeBleSettings() {
        try {
            TransactionBuilder builder2 = ((CasioGB6900DeviceSupport) getSupport()).createTransactionBuilder("writeBleInit");
            builder2.setGattCallback(this);
            builder2.write(getCharacteristic(CasioGB6900Constants.CASIO_SETTING_FOR_BLE_CHARACTERISTIC_UUID), this.mBleSettings);
            ((CasioGB6900DeviceSupport) getSupport()).performImmediately(builder2);
        } catch (IOException e) {
            Logger logger = LOG;
            logger.error("Error writing BLE settings: " + e.getMessage());
        }
    }

    public boolean onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        UUID characteristicUUID = characteristic.getUuid();
        byte[] data = characteristic.getValue();
        if (data.length == 0) {
            return true;
        }
        if (!characteristicUUID.equals(CasioGB6900Constants.CASIO_SETTING_FOR_BLE_CHARACTERISTIC_UUID)) {
            return super.onCharacteristicRead(gatt, characteristic, status);
        }
        this.mBleSettings = data;
        String str = "Read Casio Setting for BLE: ";
        for (int i = 0; i < data.length; i++) {
            str = str + String.format("0x%1x ", new Object[]{Byte.valueOf(data[i])});
        }
        configureBleSettings();
        writeBleSettings();
        return true;
    }
}
