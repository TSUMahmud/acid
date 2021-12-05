package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import java.nio.ByteBuffer;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class RequestHandControlRequest extends Request {
    public RequestHandControlRequest(byte priority, boolean moveCompleteNotify, boolean controlLostNOtify) {
        init(priority, moveCompleteNotify, controlLostNOtify);
    }

    public RequestHandControlRequest() {
        init((byte) 1, false, false);
    }

    private void init(byte priority, boolean moveCompleteNotify, boolean controlLostNOtify) {
        ByteBuffer buffer = createBuffer();
        buffer.put(priority);
        buffer.put(moveCompleteNotify);
        buffer.put(controlLostNOtify);
        this.data = buffer.array();
    }

    public byte[] getStartSequence() {
        return new byte[]{2, 21, 1};
    }

    public int getPayloadLength() {
        return 6;
    }
}
