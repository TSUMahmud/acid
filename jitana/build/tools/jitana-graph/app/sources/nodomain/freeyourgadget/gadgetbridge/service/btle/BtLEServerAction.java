package nodomain.freeyourgadget.gadgetbridge.service.btle;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServer;
import java.util.Date;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;

public abstract class BtLEServerAction {
    private final long creationTimestamp = System.currentTimeMillis();
    private final BluetoothDevice device;

    public abstract boolean expectsResult();

    public abstract boolean run(BluetoothGattServer bluetoothGattServer);

    public BtLEServerAction(BluetoothDevice device2) {
        this.device = device2;
    }

    public BluetoothDevice getDevice() {
        return this.device;
    }

    /* access modifiers changed from: protected */
    public String getCreationTime() {
        return DateTimeUtils.formatDateTime(new Date(this.creationTimestamp));
    }

    public String toString() {
        return getCreationTime() + ":" + getClass().getSimpleName() + " on device: " + getDevice().getAddress();
    }
}
