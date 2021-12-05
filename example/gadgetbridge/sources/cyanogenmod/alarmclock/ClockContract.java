package cyanogenmod.alarmclock;

import android.net.Uri;
import android.provider.BaseColumns;

public final class ClockContract {
    public static final String AUTHORITY = "com.android.deskclock";

    public interface AlarmSettingColumns extends BaseColumns {
        public static final String INCREASING_VOLUME = "incvol";
        public static final String LABEL = "label";
        public static final String NO_RINGTONE = NO_RINGTONE_URI.toString();
        public static final Uri NO_RINGTONE_URI = Uri.EMPTY;
        public static final String PROFILE = "profile";
        public static final String RINGTONE = "ringtone";
        public static final String VIBRATE = "vibrate";
    }

    public interface AlarmsColumns extends AlarmSettingColumns, BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/alarms");
        public static final String DAYS_OF_WEEK = "daysofweek";
        public static final String DELETE_AFTER_USE = "delete_after_use";
        public static final String ENABLED = "enabled";
        public static final String HOUR = "hour";
        public static final String MINUTES = "minutes";
    }

    public interface CitiesColumns {
        public static final String CITY_ID = "city_id";
        public static final String CITY_NAME = "city_name";
        public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/cities");
        public static final String TIMEZONE_NAME = "timezone_name";
        public static final String TIMEZONE_OFFSET = "timezone_offset";
    }

    public interface InstancesColumns extends AlarmSettingColumns, BaseColumns {
        public static final String ALARM_ID = "alarm_id";
        public static final String ALARM_STATE = "alarm_state";
        public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/instances");
        public static final String DAY = "day";
        public static final int DISMISSED_STATE = 7;
        public static final int FIRED_STATE = 5;
        public static final int HIDE_NOTIFICATION_STATE = 2;
        public static final int HIGH_NOTIFICATION_STATE = 3;
        public static final String HOUR = "hour";
        public static final int LOW_NOTIFICATION_STATE = 1;
        public static final String MINUTES = "minutes";
        public static final int MISSED_STATE = 6;
        public static final String MONTH = "month";
        public static final int POWER_OFF_ALARM_STATE = -1;
        public static final int PREDISMISSED_STATE = 8;
        public static final int SILENT_STATE = 0;
        public static final int SNOOZE_STATE = 4;
        public static final String YEAR = "year";
    }

    private ClockContract() {
    }
}
