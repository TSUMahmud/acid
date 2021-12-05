package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import java.nio.ByteBuffer;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class SetStepGoalRequest extends Request {
    public SetStepGoalRequest(int goal) {
        init(goal);
    }

    private void init(int goal) {
        ByteBuffer buffer = createBuffer();
        buffer.putInt(goal);
        this.data = buffer.array();
    }

    public int getPayloadLength() {
        return 6;
    }

    public byte[] getStartSequence() {
        return new byte[]{2, 16};
    }
}
