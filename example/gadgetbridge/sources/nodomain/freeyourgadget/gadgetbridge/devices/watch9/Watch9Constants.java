package nodomain.freeyourgadget.gadgetbridge.devices.watch9;

import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusConstants;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;

public final class Watch9Constants {
    public static final String ACTION_CALIBRATION = "nodomain.freeyourgadget.gadgetbridge.devices.action.watch9.start_calibration";
    public static final String ACTION_CALIBRATION_HOLD = "nodomain.freeyourgadget.gadgetbridge.devices.action.watch9.keep_calibrating";
    public static final String ACTION_CALIBRATION_SEND = "nodomain.freeyourgadget.gadgetbridge.devices.action.watch9.send_calibration";
    public static final String ACTION_ENABLE = "action.watch9.enable";
    public static final byte[] CMD_ALARM_SETTINGS = {1, 10};
    public static final byte[] CMD_AUTHORIZATION_TASK = {1, 5};
    public static final byte[] CMD_BATTERY_INFO = {1, 20};
    public static final byte[] CMD_CALIBRATION_INIT_TASK = {3, 49};
    public static final byte[] CMD_CALIBRATION_KEEP_ALIVE = {3, 52};
    public static final byte[] CMD_CALIBRATION_TASK = {3, HPlusConstants.DATA_STATS, 1};
    public static final byte[] CMD_DO_NOT_DISTURB_SETTINGS = {3, ZeTimeConstants.CMD_GET_HEARTRATE_EXDATA};
    public static final byte[] CMD_FIRMWARE_INFO = {1, 2};
    public static final byte[] CMD_FITNESS_GOAL_SETTINGS = {16, 2};
    public static final byte[] CMD_HEADER = {35, 1, 0, 0, 0};
    public static final byte[] CMD_NOTIFICATION_SETTINGS = {3, 2};
    public static final byte[] CMD_NOTIFICATION_TASK = {3, 1};
    public static final byte[] CMD_TIME_SETTINGS = {1, 8};
    public static final byte KEEP_ALIVE = Byte.MIN_VALUE;
    public static final int NOTIFICATION_CHANNEL_DEFAULT = 128;
    public static final int NOTIFICATION_CHANNEL_PHONE_CALL = 1024;
    public static final byte READ_VALUE = 2;
    public static final byte REQUEST = 49;
    public static final byte RESPONSE = 19;
    public static final byte[] RESP_ALARM_INDICATOR = {Byte.MIN_VALUE, 1, 10};
    public static final byte[] RESP_AUTHORIZATION_TASK = {1, 1, 5};
    public static final byte[] RESP_BATTERY_INFO = {8, 1, 20};
    public static final byte[] RESP_BUTTON_INDICATOR = {4, 3, 17};
    public static final byte[] RESP_FIRMWARE_INFO = {8, 1, 2};
    public static final byte[] RESP_NOTIFICATION_SETTINGS = {8, 3, 2};
    public static final byte[] RESP_TIME_SETTINGS = {8, 1, 8};
    public static final byte TASK = 4;
    public static final UUID UUID_CHARACTERISTIC_UNKNOWN_2 = UUID.fromString("0000a802-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHARACTERISTIC_UNKNOWN_3 = UUID.fromString("0000a803-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHARACTERISTIC_UNKNOWN_4 = UUID.fromString("0000a804-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHARACTERISTIC_WRITE = UUID.fromString("0000a801-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_SERVICE_WATCH9 = UUID.fromString("0000a800-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_UNKNOWN_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final String VALUE_CALIBRATION_HOUR = "value.watch9.calibration_hour";
    public static final String VALUE_CALIBRATION_MINUTE = "value.watch9.calibration_minute";
    public static final String VALUE_CALIBRATION_SECOND = "value.watch9.calibration_second";
    public static final byte WRITE_VALUE = 1;
}
