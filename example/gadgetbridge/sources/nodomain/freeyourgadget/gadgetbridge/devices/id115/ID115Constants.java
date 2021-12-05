package nodomain.freeyourgadget.gadgetbridge.devices.id115;

import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;

public class ID115Constants {
    public static final byte CMD_ARG_HORIZONTAL = 0;
    public static final byte CMD_ARG_LEFT = 0;
    public static final byte CMD_ARG_RIGHT = 1;
    public static final byte CMD_ARG_VERTICAL = 2;
    public static final byte CMD_ID_APP_CONTROL = 6;
    public static final byte CMD_ID_BIND_UNBIND = 4;
    public static final byte CMD_ID_BLE_CONTROL = 7;
    public static final byte CMD_ID_DEVICE_RESTART = -16;
    public static final byte CMD_ID_DUMP_STACK = 32;
    public static final byte CMD_ID_FACTORY = -86;
    public static final byte CMD_ID_GET_INFO = 2;
    public static final byte CMD_ID_HEALTH_DATA = 8;
    public static final byte CMD_ID_LOG = 33;
    public static final byte CMD_ID_NOTIFY = 5;
    public static final byte CMD_ID_SETTINGS = 3;
    public static final byte CMD_ID_WARE_UPDATE = 1;
    public static final byte CMD_KEY_FETCH_ACTIVITY_TODAY = 3;
    public static final byte CMD_KEY_NOTIFY_CALL = 1;
    public static final byte CMD_KEY_NOTIFY_MSG = 3;
    public static final byte CMD_KEY_NOTIFY_STOP = 2;
    public static final byte CMD_KEY_REBOOT = 1;
    public static final byte CMD_KEY_SET_DISPLAY_MODE = 43;
    public static final byte CMD_KEY_SET_GOAL = 3;
    public static final byte CMD_KEY_SET_HAND = 34;
    public static final byte CMD_KEY_SET_TIME = 1;
    public static final UUID UUID_CHARACTERISTIC_NOTIFY_HEALTH = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"0AF2"}));
    public static final UUID UUID_CHARACTERISTIC_NOTIFY_NORMAL = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"0AF7"}));
    public static final UUID UUID_CHARACTERISTIC_WRITE_HEALTH = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"0AF1"}));
    public static final UUID UUID_CHARACTERISTIC_WRITE_NORMAL = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"0AF6"}));
    public static final UUID UUID_SERVICE_ID115 = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"0AF0"}));

    public static byte getNotificationType(NotificationType type) {
        switch (type) {
            case WECHAT:
                return 3;
            case FACEBOOK:
                return 6;
            case TWITTER:
                return 7;
            case WHATSAPP:
                return 8;
            case FACEBOOK_MESSENGER:
                return 9;
            case INSTAGRAM:
                return 10;
            case LINKEDIN:
                return 11;
            default:
                return 1;
        }
    }
}
