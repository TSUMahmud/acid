package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattService;

public class MiBandService {
    public static final byte ALIAS_LEN = 10;
    public static final byte COMMAND_CONFIRM_ACTIVITY_DATA_TRANSFER_COMPLETE = 10;
    public static final byte COMMAND_FACTORYRESET = 9;
    public static final byte COMMAND_FETCH_DATA = 6;
    public static final byte COMMAND_GET_SENSOR_DATA = 18;
    public static final byte COMMAND_REBOOT = 12;
    public static final byte COMMAND_SEND_FIRMWARE_INFO = 7;
    public static final byte COMMAND_SEND_NOTIFICATION = 8;
    public static final byte COMMAND_SET_FITNESS_GOAL = 5;
    public static final byte COMMAND_SET_HR_MANUAL = 2;
    public static final byte COMMAND_SET_HR_SLEEP = 0;
    public static final byte COMMAND_SET_REALTIME_STEP = 16;
    public static final byte COMMAND_SET_REALTIME_STEPS_NOTIFICATION = 3;
    public static final byte COMMAND_SET_TIMER = 4;
    public static final byte COMMAND_SET_WEAR_LOCATION = 15;
    public static final byte COMMAND_SET__HR_CONTINUOUS = 1;
    public static final byte COMMAND_STOP_MOTOR_VIBRATE = 19;
    public static final byte COMMAND_STOP_SYNC_DATA = 17;
    public static final byte COMMAND_SYNC = 11;
    public static final String MAC_ADDRESS_FILTER_1S = "C8:0F:10";
    public static final String MAC_ADDRESS_FILTER_1_1A = "88:0F:10";
    private static final Map<UUID, String> MIBAND_DEBUG = new HashMap();
    public static final byte MODE_REGULAR_DATA_LEN_BYTE = 0;
    public static final byte MODE_REGULAR_DATA_LEN_MINUTE = 1;
    public static final byte MSG_BATTERY_STATUS_CHANGED = 7;
    public static final byte MSG_CONNECTED = 0;
    public static final byte MSG_CONNECTION_FAILED = 2;
    public static final byte MSG_DEVICE_STATUS_CHANGED = 6;
    public static final byte MSG_DISCONNECTED = 1;
    public static final byte MSG_INITIALIZATION_FAILED = 3;
    public static final byte MSG_INITIALIZATION_SUCCESS = 4;
    public static final byte MSG_STEPS_CHANGED = 5;
    public static final byte NOTIFY_AUTHENTICATION_FAILED = 6;
    public static final byte NOTIFY_AUTHENTICATION_SUCCESS = 5;
    public static final byte NOTIFY_CONN_PARAM_UPDATE_FAILED = 3;
    public static final byte NOTIFY_CONN_PARAM_UPDATE_SUCCESS = 4;
    public static final int NOTIFY_DEVICE_MALFUNCTION = 255;
    public static final byte NOTIFY_FIRMWARE_UPDATE_FAILED = 1;
    public static final byte NOTIFY_FIRMWARE_UPDATE_SUCCESS = 2;
    public static final byte NOTIFY_FITNESS_GOAL_ACHIEVED = 7;
    public static final byte NOTIFY_FW_CHECK_FAILED = 11;
    public static final byte NOTIFY_FW_CHECK_SUCCESS = 12;
    public static final byte NOTIFY_NORMAL = 0;
    public static final int NOTIFY_PAIR_CANCEL = 239;
    public static final byte NOTIFY_RESET_AUTHENTICATION_FAILED = 9;
    public static final byte NOTIFY_RESET_AUTHENTICATION_SUCCESS = 10;
    public static final byte NOTIFY_SET_LATENCY_SUCCESS = 8;
    public static final byte NOTIFY_STATUS_MOTOR_ALARM = 17;
    public static final byte NOTIFY_STATUS_MOTOR_AUTH = 19;
    public static final byte NOTIFY_STATUS_MOTOR_AUTH_SUCCESS = 21;
    public static final byte NOTIFY_STATUS_MOTOR_CALL = 14;
    public static final byte NOTIFY_STATUS_MOTOR_DISCONNECT = 15;
    public static final byte NOTIFY_STATUS_MOTOR_GOAL = 18;
    public static final byte NOTIFY_STATUS_MOTOR_NOTIFY = 13;
    public static final byte NOTIFY_STATUS_MOTOR_SHUTDOWN = 20;
    public static final byte NOTIFY_STATUS_MOTOR_SMART_ALARM = 16;
    public static final byte NOTIFY_STATUS_MOTOR_TEST = 22;
    public static final byte NOTIFY_UNKNOWN = -1;
    public static final byte TEST_DISCONNECTED_REMINDER = 5;
    public static final byte TEST_NOTIFICATION = 3;
    public static final byte TEST_REMOTE_DISCONNECT = 1;
    public static final byte TEST_SELFTEST = 2;
    public static final UUID UUID_CHARACTERISTIC_ACTIVITY_DATA = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FF07"}));
    public static final UUID UUID_CHARACTERISTIC_BATTERY = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FF0C"}));
    public static final UUID UUID_CHARACTERISTIC_CONTROL_POINT = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FF05"}));
    public static final UUID UUID_CHARACTERISTIC_DATE_TIME = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FF0A"}));
    public static final UUID UUID_CHARACTERISTIC_DEVICE_INFO = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FF01"}));
    public static final UUID UUID_CHARACTERISTIC_DEVICE_NAME = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FF02"}));
    public static final UUID UUID_CHARACTERISTIC_FIRMWARE_DATA = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FF08"}));
    public static final UUID UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT = GattCharacteristic.UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT;
    public static final UUID UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT = GattCharacteristic.UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT;
    public static final UUID UUID_CHARACTERISTIC_LE_PARAMS = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FF09"}));
    public static final UUID UUID_CHARACTERISTIC_NOTIFICATION = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FF03"}));
    public static final UUID UUID_CHARACTERISTIC_PAIR = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FF0F"}));
    public static final UUID UUID_CHARACTERISTIC_REALTIME_STEPS = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FF06"}));
    public static final UUID UUID_CHARACTERISTIC_SENSOR_DATA = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FF0E"}));
    public static final UUID UUID_CHARACTERISTIC_STATISTICS = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FF0B"}));
    public static final UUID UUID_CHARACTERISTIC_TEST = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FF0D"}));
    public static final UUID UUID_CHARACTERISTIC_USER_INFO = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FF04"}));
    public static final UUID UUID_SERVICE_HEART_RATE = GattService.UUID_SERVICE_HEART_RATE;
    public static final UUID UUID_SERVICE_MIBAND2_SERVICE = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FEE1"}));
    public static final UUID UUID_SERVICE_MIBAND_SERVICE = UUID.fromString(String.format(AbstractBTLEDeviceSupport.BASE_UUID, new Object[]{"FEE0"}));
    public static final String UUID_SERVICE_WEIGHT_SERVICE = "00001530-0000-3512-2118-0009af100700";

    static {
        MIBAND_DEBUG.put(UUID_SERVICE_MIBAND_SERVICE, "MiBand Service");
        MIBAND_DEBUG.put(UUID_SERVICE_HEART_RATE, "MiBand HR Service");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_DEVICE_INFO, "Device Info");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_DEVICE_NAME, "Device Name");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_NOTIFICATION, "Notification");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_USER_INFO, "User Info");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_CONTROL_POINT, "Control Point");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_REALTIME_STEPS, "Realtime Steps");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_ACTIVITY_DATA, "Activity Data");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_FIRMWARE_DATA, "Firmware Data");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_LE_PARAMS, "LE Params");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_DATE_TIME, "Date/Time");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_STATISTICS, "Statistics");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_BATTERY, "Battery");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_TEST, "Test");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_SENSOR_DATA, "Sensor Data");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_PAIR, "Pair");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT, "Heart Rate Control Point");
        MIBAND_DEBUG.put(UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT, "Heart Rate Measure");
    }

    public static String lookup(UUID uuid, String fallback) {
        String name = MIBAND_DEBUG.get(uuid);
        if (name == null) {
            return fallback;
        }
        return name;
    }
}
