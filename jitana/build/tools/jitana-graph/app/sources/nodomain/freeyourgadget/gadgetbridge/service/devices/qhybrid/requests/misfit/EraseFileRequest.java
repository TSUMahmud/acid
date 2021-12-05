package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EraseFileRequest extends FileRequest {
    public short deletedHandle;
    public short fileHandle;

    public EraseFileRequest(short handle) {
        this.fileHandle = handle;
        ByteBuffer buffer = createBuffer();
        buffer.putShort(handle);
        this.data = buffer.array();
    }

    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        super.handleResponse(characteristic);
        if (!characteristic.getUuid().toString().equals(getRequestUUID().toString())) {
            log("wrong descriptor");
            return;
        }
        ByteBuffer buffer = ByteBuffer.wrap(characteristic.getValue());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        this.deletedHandle = buffer.getShort(1);
        this.status = buffer.get(3);
        log("file " + this.deletedHandle + " erased: " + this.status);
    }

    public int getPayloadLength() {
        return 3;
    }

    public byte[] getStartSequence() {
        return new byte[]{3};
    }
}
