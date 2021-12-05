package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class ActivityPointGetRequest extends Request {
    public int activityPoint;

    public byte[] getStartSequence() {
        return new byte[]{1, 6};
    }

    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        super.handleResponse(characteristic);
        byte[] value = characteristic.getValue();
        if (value.length == 6) {
            ByteBuffer wrap = ByteBuffer.wrap(value);
            wrap.order(ByteOrder.LITTLE_ENDIAN);
            this.activityPoint = wrap.getInt(2) >> 8;
        }
    }
}
