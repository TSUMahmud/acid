package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.microapp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file.FilePutRequest;

public class PlayCrazyShitRequest extends FilePutRequest {
    public PlayCrazyShitRequest(byte[] appData, FossilWatchAdapter adapter) {
        super(1536, createPayload(appData), adapter);
    }

    private static byte[] createPayload(byte[] appData) {
        List<MicroAppCommand> commands = new ArrayList<>();
        commands.add(new StartCriticalCommand());
        commands.add(new VibrateCommand(VibrationType.NORMAL));
        commands.add(new DelayCommand(1.0d));
        commands.add(new CloseCommand());
        int length = 0;
        for (MicroAppCommand command : commands) {
            length += command.getData().length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(length + 14 + 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 1);
        buffer.put((byte) 0);
        buffer.put((byte) 8);
        buffer.put(appData, 3, 8);
        buffer.put((byte) -1);
        buffer.putShort((short) (length + 3));
        for (MicroAppCommand command2 : commands) {
            buffer.put(command2.getData());
        }
        CRC32 crc = new CRC32();
        crc.update(buffer.array(), 0, buffer.position());
        buffer.putInt((int) crc.getValue());
        return buffer.array();
    }
}
