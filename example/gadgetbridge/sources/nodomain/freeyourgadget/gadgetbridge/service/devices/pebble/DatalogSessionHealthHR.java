package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import java.nio.ByteBuffer;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DatalogSessionHealthHR extends DatalogSessionPebbleHealth {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DatalogSessionHealthHR.class);

    DatalogSessionHealthHR(byte id, UUID uuid, int timestamp, int tag, byte item_type, short item_size, GBDevice device) {
        super(id, uuid, timestamp, tag, item_type, item_size, device);
        this.taginfo = "(Health - HR " + tag + " )";
    }

    public GBDeviceEvent[] handleMessage(ByteBuffer datalogMessage, int length) {
        Logger logger = LOG;
        logger.info("DATALOG " + this.taginfo + C1238GB.hexdump(datalogMessage.array(), datalogMessage.position(), length));
        if (!isPebbleHealthEnabled()) {
            return null;
        }
        return new GBDeviceEvent[]{null};
    }
}
