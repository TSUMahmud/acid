package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file;

import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest;

public class FileVerifyRequest extends FossilRequest {
    private short handle;
    private boolean isFinished = false;

    public FileVerifyRequest(short fileHandle) {
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
            byte type = (byte) (value[0] & 15);
            if (type != 10) {
                if (type != 4) {
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
            }
        } else {
            throw new RuntimeException("wrong response UUID");
        }
    }

    public void onPrepare() {
    }

    public byte[] getStartSequence() {
        return new byte[]{4};
    }

    public int getPayloadLength() {
        return 3;
    }

    public boolean isFinished() {
        return this.isFinished;
    }
}
