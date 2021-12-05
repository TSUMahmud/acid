package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class FileRequest extends Request {
    public boolean completed = false;
    public int status;

    public UUID getRequestUUID() {
        return UUID.fromString("3dda0003-957f-7d4a-34a6-74696673696d");
    }

    public byte[] getStartSequence() {
        return null;
    }
}
