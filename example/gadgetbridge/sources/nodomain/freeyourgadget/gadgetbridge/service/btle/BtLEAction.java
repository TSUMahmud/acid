package nodomain.freeyourgadget.gadgetbridge.service.btle;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import java.util.Date;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;

public abstract class BtLEAction {
    private final BluetoothGattCharacteristic characteristic;
    private final long creationTimestamp = System.currentTimeMillis();

    public abstract boolean expectsResult();

    public abstract boolean run(BluetoothGatt bluetoothGatt);

    public BtLEAction(BluetoothGattCharacteristic characteristic2) {
        this.characteristic = characteristic2;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return this.characteristic;
    }

    /* access modifiers changed from: protected */
    public String getCreationTime() {
        return DateTimeUtils.formatDateTime(new Date(this.creationTimestamp));
    }

    public String toString() {
        BluetoothGattCharacteristic characteristic2 = getCharacteristic();
        String uuid = characteristic2 == null ? "(null)" : characteristic2.getUuid().toString();
        return getCreationTime() + ": " + getClass().getSimpleName() + " on characteristic: " + uuid;
    }
}
