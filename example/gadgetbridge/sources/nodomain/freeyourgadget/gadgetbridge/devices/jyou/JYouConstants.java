package nodomain.freeyourgadget.gadgetbridge.devices.jyou;

import java.util.UUID;

public final class JYouConstants {
    public static final byte CMD_ACTION_HEARTRATE_SWITCH = 13;
    public static final byte CMD_ACTION_REBOOT_DEVICE = 14;
    public static final byte CMD_ACTION_SHOW_NOTIFICATION = 44;
    public static final byte CMD_GET_SLEEP_TIME = 50;
    public static final byte CMD_GET_STEP_COUNT = 29;
    public static final byte CMD_SET_ALARM_1 = 9;
    public static final byte CMD_SET_ALARM_2 = 34;
    public static final byte CMD_SET_ALARM_3 = 35;
    public static final byte CMD_SET_DATE_AND_TIME = 8;
    public static final byte CMD_SET_DND_SETTINGS = 57;
    public static final byte CMD_SET_HEARTRATE_AUTO = 56;
    public static final byte CMD_SET_HEARTRATE_WARNING_VALUE = 1;
    public static final byte CMD_SET_INACTIVITY_WARNING_TIME = 36;
    public static final byte CMD_SET_NOON_TIME = 38;
    public static final byte CMD_SET_SLEEP_TIME = 39;
    public static final byte CMD_SET_TARGET_STEPS = 3;
    public static final byte ICON_CALL = 0;
    public static final byte ICON_FACEBOOK = 4;
    public static final byte ICON_LINE = 8;
    public static final byte ICON_QQ = 3;
    public static final byte ICON_SKYPE = 5;
    public static final byte ICON_SMS = 1;
    public static final byte ICON_TWITTER = 6;
    public static final byte ICON_WECHAT = 2;
    public static final byte ICON_WHATSAPP = 7;
    public static final byte RECEIVE_BATTERY_LEVEL = -9;
    public static final byte RECEIVE_BLOOD_PRESSURE = -24;
    public static final byte RECEIVE_DEVICE_INFO = -10;
    public static final byte RECEIVE_GET_PHOTO = -13;
    public static final byte RECEIVE_HEARTRATE = -4;
    public static final byte RECEIVE_HISTORY_SLEEP_COUNT = 50;
    public static final byte RECEIVE_STEPS_DATA = -7;
    public static final byte RECEIVE_WATCH_MAC = -20;
    public static final UUID UUID_CHARACTERISTIC_CONTROL = UUID.fromString("000033f3-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHARACTERISTIC_MEASURE = UUID.fromString("000033f4-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_SERVICE_JYOU = UUID.fromString("000056ff-0000-1000-8000-00805f9b34fb");
}
