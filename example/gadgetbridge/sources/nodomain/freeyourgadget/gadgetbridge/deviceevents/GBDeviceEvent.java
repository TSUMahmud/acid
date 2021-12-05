package nodomain.freeyourgadget.gadgetbridge.deviceevents;

public abstract class GBDeviceEvent {
    public String toString() {
        return getClass().getSimpleName() + ": ";
    }
}
