package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file;

import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest;

public class FileCloseRequest extends FossilRequest {
    private short handle;
    private boolean isFinished = false;

    public FileCloseRequest(short fileHandle) {
        this.handle = fileHandle;
        ByteBuffer buffer = createBuffer();
        buffer.putShort(fileHandle);
        this.data = buffer.array();
    }

    public short getHandle() {
        return this.handle;
    }

    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        super.handleResponse(characteristic);
        if (characteristic.getUuid().toString().equals(getRequestUUID().toString())) {
            byte[] value = characteristic.getValue();
            if (((byte) (value[0] & 15)) != 9) {
                throw new RuntimeException("wrong response type");
            } else if (value.length == 4) {
                ByteBuffer buffer = ByteBuffer.wrap(value);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                if (this.handle != buffer.getShort(1)) {
                    throw new RuntimeException("wrong response handle");
                } else if (buffer.get(3) == 0) {
                    this.isFinished = true;
                    onPrepare();
                } else {
                    throw new RuntimeException("wrong response status");
                }
            } else {
                throw new RuntimeException("wrong response length");
            }
        } else {
            throw new RuntimeException("wrong response UUID");
        }
    }

    public void onPrepare() {
    }

    public byte[] getStartSequence() {
        return new byte[]{9};
    }

    public int getPayloadLength() {
        return 3;
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public UUID getRequestUUID() {
        return UUID.fromString("3dda0003-957f-7d4a-34a6-74696673696d");
    }
}
