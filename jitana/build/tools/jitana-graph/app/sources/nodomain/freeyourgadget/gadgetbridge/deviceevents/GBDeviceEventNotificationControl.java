package nodomain.freeyourgadget.gadgetbridge.deviceevents;

public class GBDeviceEventNotificationControl extends GBDeviceEvent {
    public Event event = Event.UNKNOWN;
    public long handle;
    public String phoneNumber;
    public String reply;
    public String title;

    public enum Event {
        UNKNOWN,
        DISMISS,
        DISMISS_ALL,
        OPEN,
        MUTE,
        REPLY
    }
}
