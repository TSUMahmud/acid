package nodomain.freeyourgadget.gadgetbridge.devices.xwatch;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class XWatchService {
    public static final byte COMMAND_ACTION_BUTTON = 76;
    public static final byte COMMAND_ACTIVITY_DATA = 67;
    public static final byte COMMAND_ACTIVITY_TOTALS = 70;
    public static final byte COMMAND_CONNECTED = 1;
    public static final UUID UUID_NOTIFY = UUID.fromString("0000fff7-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_WRITE = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");
    private static final Map<UUID, String> XWATCH_DEBUG = new HashMap();

    static {
        XWATCH_DEBUG.put(UUID_NOTIFY, "Read data");
        XWATCH_DEBUG.put(UUID_WRITE, "Write data");
        XWATCH_DEBUG.put(UUID_SERVICE, "Get service");
    }

    public static String lookup(UUID uuid, String fallback) {
        String name = XWATCH_DEBUG.get(uuid);
        if (name == null) {
            return fallback;
        }
        return name;
    }
}
