package nodomain.freeyourgadget.gadgetbridge.model;

import java.io.Serializable;

public interface Alarm extends Serializable {
    public static final byte ALARM_FRI = 16;
    public static final byte ALARM_MON = 1;
    public static final byte ALARM_ONCE = 0;
    public static final byte ALARM_SAT = 32;
    public static final byte ALARM_SUN = 64;
    public static final byte ALARM_THU = 8;
    public static final byte ALARM_TUE = 2;
    public static final byte ALARM_WED = 4;
    public static final String EXTRA_ALARM = "alarm";

    boolean getEnabled();

    int getHour();

    int getMinute();

    int getPosition();

    int getRepetition();

    boolean getRepetition(int i);

    boolean getSmartWakeup();

    boolean getSnooze();

    boolean getUnused();

    boolean isRepetitive();
}
