package nodomain.freeyourgadget.gadgetbridge.service.btle.actions;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEAction;

public class RequestMtuAction extends BtLEAction {
    private int mtu;

    public RequestMtuAction(int mtu2) {
        super((BluetoothGattCharacteristic) null);
        this.mtu = mtu2;
    }

    public boolean expectsResult() {
        return false;
    }

    public boolean run(BluetoothGatt gatt) {
        return gatt.requestMtu(this.mtu);
    }
}
