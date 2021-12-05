package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class GoalTrackingGetRequest extends Request {
    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        if (value.length == 5) {
            ByteBuffer buffer = ByteBuffer.wrap(value);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            short s = (short) buffer.get(3);
            boolean z = true;
            if (buffer.get(4) != 1) {
                z = false;
            }
            boolean z2 = z;
        }
    }

    public byte[] getStartSequence() {
        return new byte[]{1, 20, 1};
    }
}
