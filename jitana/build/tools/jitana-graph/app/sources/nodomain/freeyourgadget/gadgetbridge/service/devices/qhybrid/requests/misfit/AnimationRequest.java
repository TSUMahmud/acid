package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class AnimationRequest extends Request {
    public byte[] getStartSequence() {
        return new byte[]{2, PebbleColor.Folly, 5};
    }
}
