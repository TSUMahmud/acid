package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.notification;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file.FileGetRequest;
import nodomain.freeyourgadget.gadgetbridge.util.CRC32C;

public class NotificationFilterGetRequest extends FileGetRequest {
    public NotificationFilterGetRequest(FossilWatchAdapter adapter) {
        super(3072, adapter);
    }

    public void handleFileData(byte[] fileData) {
        log("handleFileData");
        ByteBuffer buffer = ByteBuffer.wrap(fileData);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        byte[] data = new byte[((fileData.length - 12) - 4)];
        System.arraycopy(fileData, 12, data, 0, data.length);
        CRC32C crc32c = new CRC32C();
        crc32c.update(data, 0, data.length);
        if (((int) crc32c.getValue()) != buffer.getInt(fileData.length - 4)) {
            throw new RuntimeException("CRC invalid");
        }
    }
}
