package nodomain.freeyourgadget.gadgetbridge.devices.no1f1;

import java.util.UUID;

public final class No1F1Constants {
    public static final byte CMD_ALARM = -85;
    public static final byte CMD_BATTERY = -94;
    public static final byte CMD_DATETIME = -93;
    public static final byte CMD_DEVICE_SETTINGS = -45;
    public static final byte CMD_DISPLAY_SETTINGS = -96;
    public static final byte CMD_FACTORY_RESET = -83;
    public static final byte CMD_FETCH_HEARTRATE = -26;
    public static final byte CMD_FETCH_SLEEP = -77;
    public static final byte CMD_FETCH_STEPS = -78;
    public static final byte CMD_FIRMWARE_VERSION = -95;
    public static final byte CMD_HEARTRATE_SETTINGS = -42;
    public static final byte CMD_ICON = -61;
    public static final byte CMD_NOTIFICATION = -63;
    public static final byte CMD_REALTIME_HEARTRATE = -27;
    public static final byte CMD_REALTIME_STEPS = -79;
    public static final byte CMD_USER_DATA = -87;
    public static final byte ICON_ALARM = 4;
    public static final byte ICON_QQ = 1;
    public static final byte ICON_WECHAT = 2;
    public static final byte NOTIFICATION_CALL = 2;
    public static final byte NOTIFICATION_HEADER = 1;
    public static final byte NOTIFICATION_SMS = 3;
    public static final byte NOTIFICATION_STOP = 4;
    public static final UUID UUID_CHARACTERISTIC_CONTROL = UUID.fromString("000033f1-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHARACTERISTIC_MEASURE = UUID.fromString("000033f2-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_SERVICE_NO1 = UUID.fromString("000055ff-0000-1000-8000-00805f9b34fb");
}
