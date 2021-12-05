package nodomain.freeyourgadget.gadgetbridge.deviceevents;

import java.util.UUID;

public class GBDeviceEventAppManagement extends GBDeviceEvent {
    public Event event = Event.UNKNOWN;
    public int token = -1;
    public EventType type = EventType.UNKNOWN;
    public UUID uuid = null;

    public enum Event {
        UNKNOWN,
        SUCCESS,
        ACKNOWLEDGE,
        FAILURE,
        REQUEST
    }

    public enum EventType {
        UNKNOWN,
        INSTALL,
        DELETE,
        START,
        STOP
    }
}
