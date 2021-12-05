package nodomain.freeyourgadget.gadgetbridge.devices.hplus;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.no1f1.No1F1Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;

public final class HPlusConstants {
    public static final byte ARG_ALARM_DISABLE = -1;
    public static final byte ARG_FACTORY_RESET_EN = 90;
    public static final byte ARG_FINDME_OFF = 2;
    public static final byte ARG_FINDME_ON = 1;
    public static final byte ARG_GENDER_FEMALE = 1;
    public static final byte ARG_GENDER_MALE = 0;
    public static final byte ARG_HEARTRATE_ALLDAY_OFF = -1;
    public static final byte ARG_HEARTRATE_ALLDAY_ON = 10;
    public static final byte ARG_HEARTRATE_MEASURE_OFF = 22;
    public static final byte ARG_HEARTRATE_MEASURE_ON = 11;
    public static final byte ARG_INCOMING_CALL = -86;
    public static final byte ARG_INCOMING_MESSAGE = -86;
    public static final byte ARG_LANGUAGE_CN = 1;
    public static final byte ARG_LANGUAGE_EN = 2;
    public static final byte ARG_SHUTDOWN_EN = 90;
    public static final byte ARG_TIMEMODE_12H = 0;
    public static final byte ARG_TIMEMODE_24H = 1;
    public static final byte ARG_UNIT_IMPERIAL = 1;
    public static final byte ARG_UNIT_METRIC = 0;
    public static final byte ARG_WRIST_LEFT = 0;
    public static final byte ARG_WRIST_RIGHT = 1;
    public static final byte CMD_ACTION_DISPLAY_TEXT = 67;
    public static final byte CMD_ACTION_DISPLAY_TEXT_NAME = 63;
    public static final byte CMD_ACTION_DISPLAY_TEXT_NAME_CN = 62;
    public static final byte[] CMD_ACTION_HELLO = {1, 0};
    public static final byte CMD_ACTION_INCOMING_CALL = 65;
    public static final byte CMD_ACTION_INCOMING_SOCIAL = 49;
    public static final byte CMD_FACTORY_RESET = -74;
    public static final byte CMD_GET_ACTIVE_DAY = 39;
    public static final byte CMD_GET_CURR_DATA = 22;
    public static final byte CMD_GET_DAY_DATA = 21;
    public static final byte CMD_GET_DEVICE_ID = 36;
    public static final byte CMD_GET_SLEEP = 25;
    public static final byte CMD_GET_VERSION = 23;
    public static final byte CMD_HEIGHT = 4;
    public static final byte CMD_SET_AGE = 44;
    public static final byte CMD_SET_ALARM = 12;
    public static final byte CMD_SET_ALLDAY_HRM = 53;
    public static final byte CMD_SET_BLOOD = 78;
    public static final byte CMD_SET_CONF_END = 79;
    public static final byte CMD_SET_DATE = 8;
    public static final byte CMD_SET_END = 79;
    public static final byte CMD_SET_FINDME = 10;
    public static final byte CMD_SET_GENDER = 45;
    public static final byte CMD_SET_GOAL = 38;
    public static final byte CMD_SET_HEARTRATE_STATE = 50;
    public static final byte CMD_SET_INCOMING_CALL = 6;
    public static final byte CMD_SET_INCOMING_CALL_NUMBER = 35;
    public static final byte CMD_SET_INCOMING_MESSAGE = 7;
    public static final byte CMD_SET_LANGUAGE = 34;
    public static final byte CMD_SET_PREFS = 80;
    public static final byte CMD_SET_PREF_SIT = 30;
    public static final byte[] CMD_SET_PREF_START = {79, 90};
    public static final byte[] CMD_SET_PREF_START1 = {DATA_UNKNOWN};
    public static final byte CMD_SET_SCREENTIME = 11;
    public static final byte CMD_SET_SIT_INTERVAL = 81;
    public static final byte CMD_SET_TIME = 9;
    public static final byte CMD_SET_TIMEMODE = 71;
    public static final byte CMD_SET_UNITS = 72;
    public static final byte CMD_SET_WEEK = 42;
    public static final byte CMD_SET_WEIGHT = 5;
    public static final byte CMD_SHUTDOWN = 91;
    public static final byte DATA_DAY_SUMMARY = 56;
    public static final byte DATA_DAY_SUMMARY_ALT = 57;
    public static final byte DATA_DAY_UNKNOWN = 82;
    public static final byte DATA_SLEEP = 26;
    public static final byte DATA_STATS = 51;
    public static final byte DATA_STEPS = 54;
    public static final byte DATA_UNKNOWN = 77;
    public static final byte DATA_VERSION = 24;
    public static final byte DATA_VERSION1 = 46;
    public static final byte INCOMING_CALL_STATE_DISABLED_THRESHOLD = 123;
    public static final byte INCOMING_CALL_STATE_ENABLED = -86;
    public static final String PREF_HPLUS_ALLDAYHR = "hplus_alldayhr";
    public static final String PREF_HPLUS_SCREENTIME = "hplus_screentime";
    public static final String PREF_HPLUS_SIT_END_TIME = "hplus_sit_end_time";
    public static final String PREF_HPLUS_SIT_START_TIME = "hplus_sit_start_time";
    public static final String PREF_HPLUS_UNICODE = "hplus_unicode";
    public static final UUID UUID_CHARACTERISTIC_CONTROL = UUID.fromString("14702856-620a-3973-7c78-9cfff0876abd");
    public static final UUID UUID_CHARACTERISTIC_MEASURE = UUID.fromString("14702853-620a-3973-7c78-9cfff0876abd");
    public static final UUID UUID_SERVICE_HP = UUID.fromString("14701820-620a-3973-7c78-9cfff0876abd");
    public static final Map<Character, byte[]> transliterateMap = new HashMap<Character, byte[]>() {
        {
            put(243, new byte[]{ZeTimeConstants.CMD_PREAMBLE});
            put(211, new byte[]{79});
            put(237, new byte[]{105});
            put(205, new byte[]{73});
            put(250, new byte[]{MakibesHR3Constants.CMD_SET_SEDENTARY_REMINDER});
            put(218, new byte[]{ZeTimeConstants.CMD_DELETE_SLEEP_DATA});
            put(199, new byte[]{Byte.MIN_VALUE});
            put(252, new byte[]{-127});
            put(233, new byte[]{-126});
            put(226, new byte[]{-125});
            put(228, new byte[]{-124});
            put(224, new byte[]{MakibesHR3Constants.CMD_85});
            put(227, new byte[]{-122});
            put(231, new byte[]{-121});
            put(234, new byte[]{-120});
            put(235, new byte[]{-119});
            put(207, new byte[]{-117});
            put(232, new byte[]{-118});
            put(206, new byte[]{-116});
            put(204, new byte[]{-115});
            put(195, new byte[]{-114});
            put(196, new byte[]{ZeTimeConstants.CMD_END});
            put(201, new byte[]{ZeTimeConstants.CMD_SWITCH_SETTINGS});
            put(230, new byte[]{MakibesHR3Constants.RPRT_BATTERY});
            put(198, new byte[]{MakibesHR3Constants.RPRT_SOFTWARE});
            put(244, new byte[]{MakibesHR3Constants.CMD_SET_DATE_TIME});
            put(246, new byte[]{-108});
            put(242, new byte[]{MakibesHR3Constants.CMD_95});
            put(251, new byte[]{MakibesHR3Constants.CMD_96});
            put(249, new byte[]{ZeTimeConstants.CMD_REMINDERS});
            put(255, new byte[]{-104});
            put(214, new byte[]{ZeTimeConstants.CMD_PUSH_CALENDAR_DAY});
            put(220, new byte[]{-102});
            put(162, new byte[]{-101});
            put(163, new byte[]{-100});
            put(165, new byte[]{-99});
            put(402, new byte[]{-97});
            put(225, new byte[]{No1F1Constants.CMD_DISPLAY_SETTINGS});
            put(241, new byte[]{-92});
            put(209, new byte[]{-91});
            put(170, new byte[]{-90});
            put(186, new byte[]{-89});
            put(191, new byte[]{-88});
            put(172, new byte[]{-86});
            put(189, new byte[]{No1F1Constants.CMD_ALARM});
            put(188, new byte[]{-84});
            put(161, new byte[]{No1F1Constants.CMD_FACTORY_RESET});
            put(171, new byte[]{-82});
            put(187, new byte[]{-81});
            put(176, new byte[]{No1F1Constants.CMD_FIRMWARE_VERSION, PebbleColor.VividViolet});
        }
    };
}
