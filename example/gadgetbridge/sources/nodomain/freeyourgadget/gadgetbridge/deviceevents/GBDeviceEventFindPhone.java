package nodomain.freeyourgadget.gadgetbridge.deviceevents;

public class GBDeviceEventFindPhone extends GBDeviceEvent {
    public Event event = Event.UNKNOWN;

    public enum Event {
        UNKNOWN,
        START,
        STOP
    }
}
