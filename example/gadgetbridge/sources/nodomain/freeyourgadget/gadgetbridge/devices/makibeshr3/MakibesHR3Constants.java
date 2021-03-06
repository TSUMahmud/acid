package nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3;

import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.devices.no1f1.No1F1Constants;

public final class MakibesHR3Constants {
    public static final byte ARG_HEARTRATE_NO_READING = 0;
    public static final byte ARG_HEARTRATE_NO_TARGET = -1;
    public static final byte ARG_SEND_NOTIFICATION_SOURCE_CALL = 1;
    public static final byte ARG_SEND_NOTIFICATION_SOURCE_FACEBOOK = 16;
    public static final byte ARG_SEND_NOTIFICATION_SOURCE_FACEBOOK2 = 17;
    public static final byte ARG_SEND_NOTIFICATION_SOURCE_KAKOTALK = 20;
    public static final byte ARG_SEND_NOTIFICATION_SOURCE_LINE = 14;
    public static final byte ARG_SEND_NOTIFICATION_SOURCE_MESSAGE = 3;
    public static final byte ARG_SEND_NOTIFICATION_SOURCE_QQ = 7;
    public static final byte ARG_SEND_NOTIFICATION_SOURCE_STOP_CALL = 2;
    public static final byte ARG_SEND_NOTIFICATION_SOURCE_TWITTER = 15;
    public static final byte ARG_SEND_NOTIFICATION_SOURCE_WECHAT = 9;
    public static final byte ARG_SEND_NOTIFICATION_SOURCE_WEIBO = 19;
    public static final byte ARG_SEND_NOTIFICATION_SOURCE_WHATSAPP = 10;
    public static final byte ARG_SET_ALARM_REMINDER_REPEAT_EVERY_DAY = Byte.MAX_VALUE;
    public static final byte ARG_SET_ALARM_REMINDER_REPEAT_FRIDAY = 16;
    public static final byte ARG_SET_ALARM_REMINDER_REPEAT_MONDAY = 1;
    public static final byte ARG_SET_ALARM_REMINDER_REPEAT_ONE_TIME = Byte.MIN_VALUE;
    public static final byte ARG_SET_ALARM_REMINDER_REPEAT_SATURDAY = 32;
    public static final byte ARG_SET_ALARM_REMINDER_REPEAT_SUNDAY = 64;
    public static final byte ARG_SET_ALARM_REMINDER_REPEAT_THURSDAY = 8;
    public static final byte ARG_SET_ALARM_REMINDER_REPEAT_TUESDAY = 2;
    public static final byte ARG_SET_ALARM_REMINDER_REPEAT_WEDNESDAY = 4;
    public static final byte ARG_SET_ALARM_REMINDER_REPEAT_WEEKDAY = 31;
    public static final byte ARG_SET_PERSONAL_INFORMATION_UNIT_DISTANCE_KILOMETERS = 1;
    public static final byte ARG_SET_PERSONAL_INFORMATION_UNIT_DISTANCE_MILES = 0;
    public static final byte ARG_SET_TIMEMODE_12H = 1;
    public static final byte ARG_SET_TIMEMODE_24H = 0;
    public static final byte CMD_52 = 82;
    public static final byte CMD_78 = 120;
    public static final byte CMD_7e = 126;
    public static final byte CMD_85 = -123;
    public static final byte CMD_95 = -107;
    public static final byte CMD_96 = -106;
    public static final byte CMD_FACTORY_RESET = 35;
    public static final byte CMD_FIND_DEVICE = 113;
    public static final byte CMD_REBOOT = -1;
    public static final byte CMD_REQUEST_FITNESS = 81;
    public static final byte CMD_SEND_NOTIFICATION = 114;
    public static final byte CMD_SET_ALARM_REMINDER = 115;
    public static final byte CMD_SET_DATE_TIME = -109;
    public static final byte CMD_SET_HEADS_UP_SCREEN = 119;
    public static final byte CMD_SET_LOST_REMINDER = 122;
    public static final byte CMD_SET_PERSONAL_INFORMATION = 116;
    public static final byte CMD_SET_PHOTOGRAPH_MODE = 121;
    public static final byte CMD_SET_QUITE_HOURS = 118;
    public static final byte[] CMD_SET_REAL_TIME_BLOOD_OXYGEN = {49, 18};
    public static final byte CMD_SET_REAL_TIME_HEART_RATE = -124;
    public static final byte CMD_SET_SEDENTARY_REMINDER = 117;
    public static final byte[] CMD_SET_SINGLE_BLOOD_OXYGEN = {49, 17};
    public static final byte CMD_SET_SLEEP_TIME = Byte.MAX_VALUE;
    public static final byte CMD_SET_TIMEMODE = 124;
    public static final byte CMD_e5 = -27;
    public static final int DATA_ARGUMENTS_INDEX = 6;
    public static final int DATA_ARGUMENT_COUNT_INDEX = 2;
    public static final int DATA_COMMAND_INDEX = 4;
    public static final byte[] DATA_TEMPLATE = {No1F1Constants.CMD_ALARM, 0, 0, -1, 0, Byte.MIN_VALUE};
    public static final String PREF_DO_NOT_DISTURB = "do_not_disturb_no_auto";
    public static final String PREF_DO_NOT_DISTURB_END = "do_not_disturb_no_auto_end";
    public static final String PREF_DO_NOT_DISTURB_START = "do_not_disturb_no_auto_start";
    public static final String PREF_FIND_PHONE = "prefs_find_phone";
    public static final String PREF_FIND_PHONE_DURATION = "prefs_find_phone_duration";
    public static final String PREF_HEADS_UP_SCREEN = "activate_display_on_lift_wrist";
    public static final String PREF_LOST_REMINDER = "disconnect_notification";
    public static final byte RPRT_BATTERY = -111;
    public static final byte[] RPRT_BLOOD_OXYGEN = {49, 18};
    public static final byte[] RPRT_FITNESS = {81, 8};
    public static final byte RPRT_HEARTRATE = -124;
    public static final byte[] RPRT_HEART_RATE_SAMPLE = {81, 17};
    public static final byte RPRT_REVERSE_FIND_DEVICE = 125;
    public static final byte[] RPRT_SINGLE_BLOOD_OXYGEN = {49, 17};
    public static final byte RPRT_SOFTWARE = -110;
    public static final byte[] RPRT_STEPS_SAMPLE = {81, 32};
    public static final UUID UUID_CHARACTERISTIC_CONTROL = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID UUID_CHARACTERISTIC_REPORT = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID UUID_SERVICE = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
}
