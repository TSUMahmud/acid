package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import java.nio.ByteBuffer;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class SetCurrentStepCountRequest extends Request {
    public SetCurrentStepCountRequest(int steps) {
        ByteBuffer buffer = createBuffer();
        buffer.putInt(steps);
        this.data = buffer.array();
    }

    public int getPayloadLength() {
        return 6;
    }

    public byte[] getStartSequence() {
        return new byte[]{2, 17};
    }
}
