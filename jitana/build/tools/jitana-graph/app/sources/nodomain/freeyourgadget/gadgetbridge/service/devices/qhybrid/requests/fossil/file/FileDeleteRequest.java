package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file;

import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest;

public class FileDeleteRequest extends FossilRequest {
    private boolean finished = false;
    private short handle;

    public FileDeleteRequest(short handle2) {
        this.handle = handle2;
        ByteBuffer buffer = createBuffer();
        buffer.putShort(handle2);
        this.data = buffer.array();
    }

    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        super.handleResponse(characteristic);
        if (characteristic.getUuid().toString().equals("3dda0003-957f-7d4a-34a6-74696673696d")) {
            byte[] value = characteristic.getValue();
            if (value.length != 4) {
                throw new RuntimeException("wrong response length");
            } else if (value[0] == -117) {
                ByteBuffer buffer = ByteBuffer.wrap(value);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                if (buffer.getShort(1) != this.handle) {
                    throw new RuntimeException("wrong response handle");
                } else if (buffer.get(3) == 0) {
                    this.finished = true;
                } else {
                    throw new RuntimeException("wrong response status: " + buffer.get(3));
                }
            } else {
                throw new RuntimeException("wrong response start");
            }
        } else {
            throw new RuntimeException("wrong response UUID");
        }
    }

    public boolean isFinished() {
        return this.finished;
    }

    public byte[] getStartSequence() {
        return new byte[]{11};
    }

    public int getPayloadLength() {
        return 3;
    }
}
