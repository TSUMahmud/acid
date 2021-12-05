package nodomain.freeyourgadget.gadgetbridge.service.btle.actions;

import android.bluetooth.BluetoothGatt;
import android.content.Context;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

public class SetDeviceBusyAction extends PlainAction {
    private final String busyTask;
    private final Context context;
    private final GBDevice device;

    public SetDeviceBusyAction(GBDevice device2, String busyTask2, Context context2) {
        this.device = device2;
        this.busyTask = busyTask2;
        this.context = context2;
    }

    public boolean run(BluetoothGatt gatt) {
        this.device.setBusyTask(this.busyTask);
        this.device.sendDeviceUpdateIntent(this.context);
        return true;
    }

    public String toString() {
        return getCreationTime() + ": " + getClass().getName() + ": " + this.busyTask;
    }
}
