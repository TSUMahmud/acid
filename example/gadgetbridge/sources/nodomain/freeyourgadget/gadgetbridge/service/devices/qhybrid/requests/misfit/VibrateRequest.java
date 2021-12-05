package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import java.nio.ByteBuffer;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class VibrateRequest extends Request {
    public VibrateRequest(boolean longVibration, short repeats, short millisBetween) {
        ByteBuffer buffer = createBuffer();
        buffer.put(longVibration);
        buffer.put((byte) repeats);
        buffer.putShort(millisBetween);
        this.data = buffer.array();
    }

    public int getPayloadLength() {
        return 7;
    }

    public byte[] getStartSequence() {
        return new byte[]{2, 15, 5};
    }
}
