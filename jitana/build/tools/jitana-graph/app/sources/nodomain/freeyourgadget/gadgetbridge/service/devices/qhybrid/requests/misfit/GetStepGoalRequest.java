package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class GetStepGoalRequest extends Request {
    public int stepGoal = -1;

    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        super.handleResponse(characteristic);
        byte[] value = characteristic.getValue();
        if (value.length >= 6) {
            ByteBuffer buffer = ByteBuffer.wrap(value);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            this.stepGoal = buffer.getInt(2);
        }
    }

    public byte[] getStartSequence() {
        return new byte[]{1, 16};
    }
}
