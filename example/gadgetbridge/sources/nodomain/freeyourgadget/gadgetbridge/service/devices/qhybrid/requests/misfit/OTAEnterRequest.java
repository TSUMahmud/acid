package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import android.bluetooth.BluetoothGattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class OTAEnterRequest extends Request {
    public boolean success = false;

    public byte[] getStartSequence() {
        return new byte[]{2, PebbleColor.Folly, 8};
    }

    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        this.success = characteristic.getValue()[2] == 9;
    }
}
