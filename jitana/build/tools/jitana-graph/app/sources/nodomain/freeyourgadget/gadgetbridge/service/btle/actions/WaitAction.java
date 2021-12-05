package nodomain.freeyourgadget.gadgetbridge.service.btle.actions;

import android.bluetooth.BluetoothGatt;

public class WaitAction extends PlainAction {
    private final int mMillis;

    public WaitAction(int millis) {
        this.mMillis = millis;
    }

    public boolean run(BluetoothGatt gatt) {
        try {
            Thread.sleep((long) this.mMillis);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }
}
