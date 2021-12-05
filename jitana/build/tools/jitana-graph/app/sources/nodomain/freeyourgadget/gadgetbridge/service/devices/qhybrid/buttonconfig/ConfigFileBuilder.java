package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.buttonconfig;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.CRC32;

public class ConfigFileBuilder {
    private ConfigPayload[] configs;

    public ConfigFileBuilder(ConfigPayload[] configs2) {
        this.configs = configs2;
    }

    public byte[] build(boolean appendChecksum) {
        int payloadSize = 0;
        for (ConfigPayload payload : this.configs) {
            payloadSize += payload.getData().length;
        }
        int headerSize = 0;
        for (ConfigPayload payload2 : this.configs) {
            headerSize += payload2.getHeader().length + 3;
        }
        int i = 4;
        ByteBuffer buffer = ByteBuffer.allocate(headerSize + 4 + 1 + payloadSize + 1 + (appendChecksum ? 4 : 0));
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(new byte[]{1, 0, 0});
        buffer.put((byte) this.configs.length);
        int buttonIndex = 0;
        for (ConfigPayload payload3 : this.configs) {
            buttonIndex += 16;
            buffer.put((byte) buttonIndex);
            buffer.put((byte) 1);
            buffer.put(payload3.getHeader());
            buffer.put((byte) 0);
        }
        ArrayList<ConfigPayload> distinctPayloads = new ArrayList<>(3);
        for (int payloadIndex = 0; payloadIndex < this.configs.length; payloadIndex++) {
            int compareTo = 0;
            while (true) {
                if (compareTo >= distinctPayloads.size()) {
                    distinctPayloads.add(this.configs[payloadIndex]);
                    break;
                } else if (this.configs[payloadIndex].equals(distinctPayloads.get(compareTo))) {
                    break;
                } else {
                    compareTo++;
                }
            }
        }
        buffer.put((byte) distinctPayloads.size());
        Iterator<ConfigPayload> it = distinctPayloads.iterator();
        while (it.hasNext()) {
            buffer.put(it.next().getData());
        }
        buffer.put((byte) 0);
        int position = buffer.position();
        if (!appendChecksum) {
            i = 0;
        }
        ByteBuffer buffer2 = ByteBuffer.allocate(position + i);
        buffer2.order(ByteOrder.LITTLE_ENDIAN);
        buffer2.put(buffer.array(), 0, buffer.position());
        if (!appendChecksum) {
            return buffer2.array();
        }
        CRC32 crc = new CRC32();
        crc.update(buffer.array(), 0, buffer.position());
        buffer2.putInt((int) crc.getValue());
        return buffer2.array();
    }
}
