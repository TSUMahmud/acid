package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import java.nio.ByteBuffer;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class SetCountdownSettings extends Request {
    public SetCountdownSettings(int startTime, int endTime, short offset) {
        ByteBuffer buffer = createBuffer();
        buffer.putInt(startTime);
        buffer.putInt(endTime);
        buffer.putShort(offset);
        this.data = buffer.array();
    }

    public byte[] getStartSequence() {
        return new byte[]{2, 19, 1};
    }

    public int getPayloadLength() {
        return 13;
    }
}
