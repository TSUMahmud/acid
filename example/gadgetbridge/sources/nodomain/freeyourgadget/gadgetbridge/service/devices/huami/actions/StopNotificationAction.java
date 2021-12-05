package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.actions;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.AbortTransactionAction;

public abstract class StopNotificationAction extends AbortTransactionAction {
    private final BluetoothGattCharacteristic alertLevelCharacteristic;

    public StopNotificationAction(BluetoothGattCharacteristic alertLevelCharacteristic2) {
        this.alertLevelCharacteristic = alertLevelCharacteristic2;
    }

    public boolean run(BluetoothGatt gatt) {
        if (super.run(gatt)) {
            return true;
        }
        this.alertLevelCharacteristic.setValue(new byte[]{0});
        gatt.writeCharacteristic(this.alertLevelCharacteristic);
        return false;
    }
}
