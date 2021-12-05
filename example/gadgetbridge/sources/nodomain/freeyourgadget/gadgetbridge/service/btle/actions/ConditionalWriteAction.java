package nodomain.freeyourgadget.gadgetbridge.service.btle.actions;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public abstract class ConditionalWriteAction extends WriteAction {
    /* access modifiers changed from: protected */
    public abstract byte[] checkCondition();

    public ConditionalWriteAction(BluetoothGattCharacteristic characteristic) {
        super(characteristic, (byte[]) null);
    }

    /* access modifiers changed from: protected */
    public boolean writeValue(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] value) {
        byte[] conditionalValue = checkCondition();
        if (conditionalValue != null) {
            return super.writeValue(gatt, characteristic, conditionalValue);
        }
        return true;
    }
}
