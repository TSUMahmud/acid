package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class OTAEraseRequest extends Request {
    public OTAEraseRequest(int pageOffset) {
        ByteBuffer buffer = createBuffer();
        buffer.putShort(23131);
        buffer.putInt(pageOffset);
        this.data = buffer.array();
    }

    public byte[] getStartSequence() {
        return new byte[]{18};
    }

    public int getPayloadLength() {
        return 7;
    }

    public UUID getRequestUUID() {
        return UUID.fromString("3dda0003-957f-7d4a-34a6-74696673696d");
    }

    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        ByteBuffer wrap = ByteBuffer.wrap(characteristic.getValue());
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        short s = wrap.getShort(1);
        byte b = wrap.get(3);
        int i = wrap.getInt(4);
    }
}
