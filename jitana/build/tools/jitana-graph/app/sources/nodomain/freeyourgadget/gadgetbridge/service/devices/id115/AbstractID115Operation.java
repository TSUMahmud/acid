package nodomain.freeyourgadget.gadgetbridge.service.devices.id115;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.devices.id115.ID115Constants;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEOperation;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCallback;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.operations.OperationStatus;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;

public abstract class AbstractID115Operation extends AbstractBTLEOperation<ID115Support> {
    protected BluetoothGattCharacteristic controlCharacteristic = null;
    protected BluetoothGattCharacteristic notifyCharacteristic = null;

    /* access modifiers changed from: package-private */
    public abstract void handleResponse(byte[] bArr);

    /* access modifiers changed from: package-private */
    public abstract boolean isHealthOperation();

    protected AbstractID115Operation(ID115Support support) {
        super(support);
        if (isHealthOperation()) {
            this.controlCharacteristic = getCharacteristic(ID115Constants.UUID_CHARACTERISTIC_WRITE_HEALTH);
            this.notifyCharacteristic = getCharacteristic(ID115Constants.UUID_CHARACTERISTIC_NOTIFY_HEALTH);
            return;
        }
        this.controlCharacteristic = getCharacteristic(ID115Constants.UUID_CHARACTERISTIC_WRITE_NORMAL);
        this.notifyCharacteristic = getCharacteristic(ID115Constants.UUID_CHARACTERISTIC_NOTIFY_NORMAL);
    }

    /* access modifiers changed from: protected */
    public void prePerform() throws IOException {
        super.prePerform();
        getDevice().setBusyTask("AbstractID115Operation starting...");
        TransactionBuilder builder = performInitialized("disabling some notifications");
        enableNotifications(builder, true);
        builder.queue(getQueue());
    }

    /* access modifiers changed from: protected */
    public void operationFinished() {
        this.operationStatus = OperationStatus.FINISHED;
        if (getDevice() != null && getDevice().isConnected()) {
            unsetBusy();
            try {
                TransactionBuilder builder = performInitialized("reenabling disabled notifications");
                enableNotifications(builder, false);
                builder.setGattCallback((GattCallback) null);
                builder.queue(getQueue());
            } catch (IOException ex) {
                C1238GB.toast(getContext(), "Error enabling ID115 notifications, you may need to connect and disconnect", 1, 3, ex);
            }
        }
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (!this.notifyCharacteristic.getUuid().equals(characteristic.getUuid())) {
            return super.onCharacteristicChanged(gatt, characteristic);
        }
        handleResponse(characteristic.getValue());
        return true;
    }

    /* access modifiers changed from: package-private */
    public void enableNotifications(TransactionBuilder builder, boolean enable) {
        if (isHealthOperation()) {
            builder.notify(getCharacteristic(ID115Constants.UUID_CHARACTERISTIC_NOTIFY_HEALTH), enable);
        } else {
            builder.notify(getCharacteristic(ID115Constants.UUID_CHARACTERISTIC_NOTIFY_NORMAL), enable);
        }
    }
}
