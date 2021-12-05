package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ListFilesRequest extends FileRequest {
    private ByteBuffer buffer = null;
    public int fileCount = -1;
    private int length = 0;
    public int size = 0;

    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        String uuid = characteristic.getUuid().toString();
        byte[] value = characteristic.getValue();
        if (uuid.equals("3dda0004-957f-7d4a-34a6-74696673696d")) {
            this.buffer.put(value, 1, value.length - 1);
            this.length += value.length - 1;
            if ((value[0] & Byte.MIN_VALUE) != 0) {
                ByteBuffer buffer2 = ByteBuffer.wrap(this.buffer.array(), 0, this.length);
                buffer2.order(ByteOrder.LITTLE_ENDIAN);
                this.fileCount = buffer2.get(0);
                this.size = buffer2.getInt(1);
            }
        } else if (!uuid.equals("3dda0003-957f-7d4a-34a6-74696673696d")) {
        } else {
            if (this.buffer == null) {
                this.buffer = ByteBuffer.allocate(128);
            } else {
                this.completed = true;
            }
        }
    }

    public byte[] getStartSequence() {
        return new byte[]{5};
    }
}
