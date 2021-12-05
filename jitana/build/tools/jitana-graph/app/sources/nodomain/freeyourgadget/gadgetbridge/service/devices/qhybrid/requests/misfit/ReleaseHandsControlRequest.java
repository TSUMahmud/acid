package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import java.nio.ByteBuffer;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class ReleaseHandsControlRequest extends Request {
    public ReleaseHandsControlRequest() {
        init(0);
    }

    private void init(short delayBeforeRelease) {
        ByteBuffer buffer = createBuffer();
        buffer.putShort(3, delayBeforeRelease);
        this.data = buffer.array();
    }

    public byte[] getStartSequence() {
        return new byte[]{2, 21, 2};
    }

    public int getPayloadLength() {
        return 5;
    }
}
