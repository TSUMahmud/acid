package nodomain.freeyourgadget.gadgetbridge.service.btle.actions;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import java.util.Objects;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractGattCallback;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEQueue;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCallback;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattListenerAction;

public abstract class AbstractGattListenerWriteAction extends WriteAction implements GattListenerAction {
    private final BtLEQueue queue;

    /* access modifiers changed from: protected */
    public abstract boolean onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic);

    public AbstractGattListenerWriteAction(BtLEQueue queue2, BluetoothGattCharacteristic characteristic, byte[] value) {
        super(characteristic, value);
        this.queue = queue2;
        Objects.requireNonNull(queue2, "queue must not be null");
    }

    public GattCallback getGattCallback() {
        return new AbstractGattCallback() {
            public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                return AbstractGattListenerWriteAction.this.onCharacteristicChanged(gatt, characteristic);
            }
        };
    }
}
