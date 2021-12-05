package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.notification;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.zip.CRC32;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.NotificationConfiguration;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file.FilePutRequest;

public class NotificationFilterPutRequest extends FilePutRequest {
    public NotificationFilterPutRequest(NotificationConfiguration[] configs, FossilWatchAdapter adapter) {
        super(3072, createFile(configs), adapter);
    }

    public NotificationFilterPutRequest(ArrayList<NotificationConfiguration> configs, FossilWatchAdapter adapter) {
        super(3072, createFile((NotificationConfiguration[]) configs.toArray(new NotificationConfiguration[0])), adapter);
    }

    private static byte[] createFile(NotificationConfiguration[] configs) {
        ByteBuffer buffer = ByteBuffer.allocate(configs.length * 27);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (NotificationConfiguration config : configs) {
            buffer.putShort(25);
            CRC32 crc = new CRC32();
            crc.update(config.getPackageName().getBytes());
            buffer.put(PacketID.PACKAGE_NAME_CRC.f199id);
            buffer.put((byte) 4);
            buffer.putInt((int) crc.getValue());
            buffer.put(PacketID.GROUP_ID.f199id);
            buffer.put((byte) 1);
            buffer.put((byte) 2);
            buffer.put(PacketID.PRIORITY.f199id);
            buffer.put((byte) 1);
            buffer.put((byte) -1);
            buffer.put(PacketID.MOVEMENT.f199id);
            buffer.put((byte) 8);
            buffer.putShort(config.getHour()).putShort(config.getMin()).putShort(config.getSubEye()).putShort(5000);
            buffer.put(PacketID.VIBRATION.f199id);
            buffer.put((byte) 1);
            buffer.put(config.getVibration().getValue());
        }
        return buffer.array();
    }

    enum PacketID {
        PACKAGE_NAME((byte) 1),
        SENDER_NAME((byte) 2),
        PACKAGE_NAME_CRC((byte) 4),
        GROUP_ID(Byte.MIN_VALUE),
        APP_DISPLAY_NAME((byte) -127),
        PRIORITY((byte) -63),
        MOVEMENT(PebbleColor.DukeBlue),
        VIBRATION((byte) -61);
        

        /* renamed from: id */
        byte f199id;

        private PacketID(byte id) {
            this.f199id = id;
        }
    }

    enum VibrationType {
        SINGLE_SHORT((byte) 5),
        DOUBLE_SHORT((byte) 6),
        TRIPLE_SHORT((byte) 7),
        SINGLE_LONG((byte) 8),
        SILENT((byte) 9);
        

        /* renamed from: id */
        byte f200id;

        private VibrationType(byte id) {
            this.f200id = id;
        }
    }
}
