package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import java.nio.ByteBuffer;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class SettingsFilePutRequest extends Request {
    public byte[] file;
    public int fileLength;

    public SettingsFilePutRequest(byte[] file2) {
        this.fileLength = file2.length;
        this.file = file2;
        ByteBuffer buffer = createBuffer();
        buffer.putShort(1, 2048);
        buffer.putInt(3, 0);
        buffer.putInt(7, this.fileLength - 10);
        buffer.putInt(11, this.fileLength - 10);
        this.data = buffer.array();
    }

    public int getPayloadLength() {
        return 15;
    }

    public byte[] getStartSequence() {
        return new byte[]{17};
    }

    public UUID getRequestUUID() {
        return UUID.fromString("3dda0007-957f-7d4a-34a6-74696673696d");
    }
}
