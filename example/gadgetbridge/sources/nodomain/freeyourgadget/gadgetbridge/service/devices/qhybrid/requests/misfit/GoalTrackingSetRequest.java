package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class GoalTrackingSetRequest extends Request {
    public GoalTrackingSetRequest(int id, boolean state) {
    }

    public byte[] getStartSequence() {
        return new byte[]{2, 20, 1};
    }

    public int getPayloadLength() {
        return 5;
    }
}
