package nodomain.freeyourgadget.gadgetbridge.model;

public class CalendarEventSpec {
    public static final byte TYPE_SUNRISE = 1;
    public static final byte TYPE_SUNSET = 2;
    public static final byte TYPE_UNKNOWN = 0;
    public boolean allDay;
    public String description;
    public int durationInSeconds;

    /* renamed from: id */
    public long f156id;
    public String location;
    public int timestamp;
    public String title;
    public byte type;
}
