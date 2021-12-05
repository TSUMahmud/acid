package nodomain.freeyourgadget.gadgetbridge.deviceevents;

public class GBDeviceEventDisplayMessage {
    public int duration;
    public String message;
    public int severity;

    public GBDeviceEventDisplayMessage(String message2, int duration2, int severity2) {
        this.message = message2;
        this.duration = duration2;
        this.severity = severity2;
    }
}
