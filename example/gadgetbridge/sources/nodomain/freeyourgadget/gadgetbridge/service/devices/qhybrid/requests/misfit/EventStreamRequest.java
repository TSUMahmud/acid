package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import java.nio.ByteBuffer;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class EventStreamRequest extends Request {
    public EventStreamRequest(short handle) {
        ByteBuffer buffer = createBuffer();
        buffer.putShort(1, handle);
        this.data = buffer.array();
    }

    public UUID getRequestUUID() {
        return UUID.fromString("3dda0006-957f-7d4a-34a6-74696673696d");
    }

    public byte[] getStartSequence() {
        return new byte[]{1};
    }

    public int getPayloadLength() {
        return 3;
    }
}
