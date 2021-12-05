package nodomain.freeyourgadget.gadgetbridge.service.btle.actions;

import android.bluetooth.BluetoothGattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEAction;

public abstract class PlainAction extends BtLEAction {
    public PlainAction() {
        super((BluetoothGattCharacteristic) null);
    }

    public boolean expectsResult() {
        return false;
    }

    public String toString() {
        return getCreationTime() + ": " + getClass().getSimpleName();
    }
}
