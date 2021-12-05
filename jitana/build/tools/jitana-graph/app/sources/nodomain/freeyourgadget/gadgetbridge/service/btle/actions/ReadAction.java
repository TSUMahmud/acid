package nodomain.freeyourgadget.gadgetbridge.service.btle.actions;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEAction;

public class ReadAction extends BtLEAction {
    public ReadAction(BluetoothGattCharacteristic characteristic) {
        super(characteristic);
    }

    public boolean run(BluetoothGatt gatt) {
        if ((getCharacteristic().getProperties() & 2) > 0) {
            return gatt.readCharacteristic(getCharacteristic());
        }
        return false;
    }

    public boolean expectsResult() {
        return true;
    }
}
