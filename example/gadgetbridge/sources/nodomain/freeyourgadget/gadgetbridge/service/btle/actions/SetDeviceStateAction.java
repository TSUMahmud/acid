package nodomain.freeyourgadget.gadgetbridge.service.btle.actions;

import android.bluetooth.BluetoothGatt;
import android.content.Context;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

public class SetDeviceStateAction extends PlainAction {
    private final Context context;
    private final GBDevice device;
    private final GBDevice.State deviceState;

    public SetDeviceStateAction(GBDevice device2, GBDevice.State deviceState2, Context context2) {
        this.device = device2;
        this.deviceState = deviceState2;
        this.context = context2;
    }

    public boolean run(BluetoothGatt gatt) {
        this.device.setState(this.deviceState);
        this.device.sendDeviceUpdateIntent(getContext());
        return true;
    }

    public Context getContext() {
        return this.context;
    }

    public String toString() {
        return super.toString() + " to " + this.deviceState;
    }
}
