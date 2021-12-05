package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification;

public class AlertStatus {
    public static final int DISPLAY_ALERT_ACTIVE = 4;
    public static final int RINGER_ACTIVE_BIT = 1;
    public static final int VIBRATE_ACTIVE = 2;

    public static boolean isRingerActive(int status) {
        return (status & 1) == 1;
    }

    public static boolean isVibrateActive(int status) {
        return (status & 2) == 2;
    }

    public static boolean isDisplayAlertActive(int status) {
        return (status & 4) == 4;
    }
}
