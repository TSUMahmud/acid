package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.alarm;

import org.apache.commons.lang3.StringUtils;

public class Alarm {
    public final int WEEKDAY_FRIDAY = 5;
    public final int WEEKDAY_MONDAY = 1;
    public final int WEEKDAY_SATURDAY = 6;
    public final int WEEKDAY_SUNDAY = 0;
    public final int WEEKDAY_THURSDAY = 3;
    public final int WEEKDAY_TUESDAY = 2;
    public final int WEEKDAY_WEDNESDAY = 4;
    private byte days = 0;
    private byte hour;
    private byte minute;
    private boolean repeat;

    public Alarm(byte minute2, byte hour2) {
        this.minute = minute2;
        this.hour = hour2;
        this.repeat = false;
    }

    public Alarm(byte minute2, byte hour2, boolean repeat2) {
        this.minute = minute2;
        this.hour = hour2;
        this.repeat = repeat2;
    }

    public Alarm(byte minute2, byte hour2, byte days2) {
        this.minute = minute2;
        this.hour = hour2;
        this.repeat = true;
        this.days = days2;
    }

    public void setDayEnabled(int day, boolean enabled) {
        if (enabled) {
            this.days = (byte) ((1 << day) | this.days);
            return;
        }
        this.days = (byte) (((1 << day) ^ -1) & this.days);
    }

    public byte[] getData() {
        byte first = -1;
        if (this.repeat) {
            first = (byte) (this.days | 128);
        }
        byte second = this.minute;
        if (this.repeat) {
            second = (byte) (second | 128);
        }
        return new byte[]{first, second, this.hour};
    }

    public static Alarm fromBytes(byte[] bytes) {
        if (bytes.length == 3) {
            boolean repeat2 = false;
            byte days2 = bytes[0];
            byte minutes = (byte) (bytes[1] & Byte.MAX_VALUE);
            if ((bytes[1] & 128) == 128) {
                repeat2 = true;
            }
            if (repeat2) {
                return new Alarm(minutes, bytes[2], days2);
            }
            return new Alarm(minutes, bytes[2]);
        }
        throw new RuntimeException("alarm bytes length must be 3");
    }

    public String toString() {
        String description = this.hour + ":" + this.minute + "  ";
        if (this.repeat) {
            String[] dayNames = {"sunday", "monday", "tuesday", "thursday", "wednesday", "friday", "saturday"};
            for (int i = 0; i <= 6; i++) {
                if ((this.days & (1 << i)) != 0) {
                    description = description + dayNames[i] + StringUtils.SPACE;
                }
            }
            return description;
        }
        return description + "not repeating";
    }
}
