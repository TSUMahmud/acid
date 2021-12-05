package nodomain.freeyourgadget.gadgetbridge.deviceevents.pebble;

import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;

public class GBDeviceEventDataLogging extends GBDeviceEvent {
    public static final int COMMAND_FINISH_SESSION = 2;
    public static final int COMMAND_RECEIVE_DATA = 1;
    public UUID appUUID;
    public int command;
    public Object[] data;
    public byte pebbleDataType;
    public long tag;
    public long timestamp;
}
