package nodomain.freeyourgadget.gadgetbridge.model;

import nodomain.freeyourgadget.gadgetbridge.devices.EventHandler;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

public interface DeviceService extends EventHandler {
    public static final String ACTION_ADD_CALENDAREVENT = "nodomain.freeyourgadget.gadgetbridge.devices.action.add_calendarevent";
    public static final String ACTION_APP_CONFIGURE = "nodomain.freeyourgadget.gadgetbridge.devices.action.app_configure";
    public static final String ACTION_APP_REORDER = "nodomain.freeyourgadget.gadgetbridge.devices.action.app_reorder";
    public static final String ACTION_CALLSTATE = "nodomain.freeyourgadget.gadgetbridge.devices.action.callstate";
    public static final String ACTION_CONNECT = "nodomain.freeyourgadget.gadgetbridge.devices.action.connect";
    public static final String ACTION_DELETEAPP = "nodomain.freeyourgadget.gadgetbridge.devices.action.deleteapp";
    public static final String ACTION_DELETE_CALENDAREVENT = "nodomain.freeyourgadget.gadgetbridge.devices.action.delete_calendarevent";
    public static final String ACTION_DELETE_NOTIFICATION = "nodomain.freeyourgadget.gadgetbridge.devices.action.delete_notification";
    public static final String ACTION_DISCONNECT = "nodomain.freeyourgadget.gadgetbridge.devices.action.disconnect";
    public static final String ACTION_ENABLE_HEARTRATE_SLEEP_SUPPORT = "nodomain.freeyourgadget.gadgetbridge.devices.action.enable_heartrate_sleep_support";
    public static final String ACTION_ENABLE_REALTIME_HEARTRATE_MEASUREMENT = "nodomain.freeyourgadget.gadgetbridge.devices.action.realtime_hr_measurement";
    public static final String ACTION_ENABLE_REALTIME_STEPS = "nodomain.freeyourgadget.gadgetbridge.devices.action.enable_realtime_steps";
    public static final String ACTION_FETCH_RECORDED_DATA = "nodomain.freeyourgadget.gadgetbridge.devices.action.fetch_activity_data";
    public static final String ACTION_FIND_DEVICE = "nodomain.freeyourgadget.gadgetbridge.devices.action.find_device";
    public static final String ACTION_HEARTRATE_TEST = "nodomain.freeyourgadget.gadgetbridge.devices.action.heartrate_test";
    public static final String ACTION_INSTALL = "nodomain.freeyourgadget.gadgetbridge.devices.action.install";
    public static final String ACTION_NOTIFICATION = "nodomain.freeyourgadget.gadgetbridge.devices.action.notification";
    public static final String ACTION_READ_CONFIGURATION = "nodomain.freeyourgadget.gadgetbridge.devices.action.read_configuration";
    public static final String ACTION_REALTIME_SAMPLES = "nodomain.freeyourgadget.gadgetbridge.devices.action.realtime_samples";
    public static final String ACTION_REQUEST_APPINFO = "nodomain.freeyourgadget.gadgetbridge.devices.action.request_appinfo";
    public static final String ACTION_REQUEST_DEVICEINFO = "nodomain.freeyourgadget.gadgetbridge.devices.action.request_deviceinfo";
    public static final String ACTION_REQUEST_SCREENSHOT = "nodomain.freeyourgadget.gadgetbridge.devices.action.request_screenshot";
    public static final String ACTION_RESET = "nodomain.freeyourgadget.gadgetbridge.devices.action.reset";
    public static final String ACTION_SAVE_ALARMS = "nodomain.freeyourgadget.gadgetbridge.devices.action.save_alarms";
    public static final String ACTION_SEND_CONFIGURATION = "nodomain.freeyourgadget.gadgetbridge.devices.action.send_configuration";
    public static final String ACTION_SEND_WEATHER = "nodomain.freeyourgadget.gadgetbridge.devices.action.send_weather";
    public static final String ACTION_SETCANNEDMESSAGES = "nodomain.freeyourgadget.gadgetbridge.devices.action.setcannedmessages";
    public static final String ACTION_SETMUSICINFO = "nodomain.freeyourgadget.gadgetbridge.devices.action.setmusicinfo";
    public static final String ACTION_SETMUSICSTATE = "nodomain.freeyourgadget.gadgetbridge.devices.action.setmusicstate";
    public static final String ACTION_SETTIME = "nodomain.freeyourgadget.gadgetbridge.devices.action.settime";
    public static final String ACTION_SET_ALARMS = "nodomain.freeyourgadget.gadgetbridge.devices.action.set_alarms";
    public static final String ACTION_SET_CONSTANT_VIBRATION = "nodomain.freeyourgadget.gadgetbridge.devices.action.set_constant_vibration";
    public static final String ACTION_SET_FM_FREQUENCY = "nodomain.freeyourgadget.gadgetbridge.devices.action.set_fm_frequency";
    public static final String ACTION_SET_HEARTRATE_MEASUREMENT_INTERVAL = "nodomain.freeyourgadget.gadgetbridge.devices.action.set_heartrate_measurement_intervarl";
    public static final String ACTION_SET_LED_COLOR = "nodomain.freeyourgadget.gadgetbridge.devices.action.set_led_color";
    public static final String ACTION_START = "nodomain.freeyourgadget.gadgetbridge.devices.action.start";
    public static final String ACTION_STARTAPP = "nodomain.freeyourgadget.gadgetbridge.devices.action.startapp";
    public static final String ACTION_TEST_NEW_FUNCTION = "nodomain.freeyourgadget.gadgetbridge.devices.action.test_new_function";
    public static final String EXTRA_ALARMS = "alarms";
    public static final String EXTRA_APP_CONFIG = "app_config";
    public static final String EXTRA_APP_CONFIG_ID = "app_config_id";
    public static final String EXTRA_APP_START = "app_start";
    public static final String EXTRA_APP_UUID = "app_uuid";
    public static final String EXTRA_BOOLEAN_ENABLE = "enable_realtime_steps";
    public static final String EXTRA_CALENDAREVENT_DESCRIPTION = "calendarevent_description";
    public static final String EXTRA_CALENDAREVENT_DURATION = "calendarevent_duration";
    public static final String EXTRA_CALENDAREVENT_ID = "calendarevent_id";
    public static final String EXTRA_CALENDAREVENT_LOCATION = "calendarevent_location";
    public static final String EXTRA_CALENDAREVENT_TIMESTAMP = "calendarevent_timestamp";
    public static final String EXTRA_CALENDAREVENT_TITLE = "calendarevent_title";
    public static final String EXTRA_CALENDAREVENT_TYPE = "calendarevent_type";
    public static final String EXTRA_CALL_COMMAND = "call_command";
    public static final String EXTRA_CALL_DISPLAYNAME = "call_displayname";
    public static final String EXTRA_CALL_PHONENUMBER = "call_phonenumber";
    public static final String EXTRA_CANNEDMESSAGES = "cannedmessages";
    public static final String EXTRA_CANNEDMESSAGES_TYPE = "cannedmessages_type";
    public static final String EXTRA_CONFIG = "config";
    public static final String EXTRA_CONNECT_FIRST_TIME = "connect_first_time";
    public static final String EXTRA_FIND_START = "find_start";
    public static final String EXTRA_FM_FREQUENCY = "fm_frequency";
    @Deprecated
    public static final String EXTRA_HEART_RATE_VALUE = "hr_value";
    public static final String EXTRA_INTERVAL_SECONDS = "interval_seconds";
    public static final String EXTRA_LED_COLOR = "led_color";
    public static final String EXTRA_MUSIC_ALBUM = "music_album";
    public static final String EXTRA_MUSIC_ARTIST = "music_artist";
    public static final String EXTRA_MUSIC_DURATION = "music_duration";
    public static final String EXTRA_MUSIC_POSITION = "music_position";
    public static final String EXTRA_MUSIC_RATE = "music_rate";
    public static final String EXTRA_MUSIC_REPEAT = "music_repeat";
    public static final String EXTRA_MUSIC_SHUFFLE = "music_shuffle";
    public static final String EXTRA_MUSIC_STATE = "music_state";
    public static final String EXTRA_MUSIC_TRACK = "music_track";
    public static final String EXTRA_MUSIC_TRACKCOUNT = "music_trackcount";
    public static final String EXTRA_MUSIC_TRACKNR = "music_tracknr";
    public static final String EXTRA_NOTIFICATION_ACTIONS = "notification_actions";
    public static final String EXTRA_NOTIFICATION_BODY = "notification_body";
    public static final String EXTRA_NOTIFICATION_FLAGS = "notification_flags";
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";
    public static final String EXTRA_NOTIFICATION_PEBBLE_COLOR = "notification_pebble_color";
    public static final String EXTRA_NOTIFICATION_PHONENUMBER = "notification_phonenumber";
    public static final String EXTRA_NOTIFICATION_SENDER = "notification_sender";
    public static final String EXTRA_NOTIFICATION_SOURCEAPPID = "notification_sourceappid";
    public static final String EXTRA_NOTIFICATION_SOURCENAME = "notification_sourcename";
    public static final String EXTRA_NOTIFICATION_SUBJECT = "notification_subject";
    public static final String EXTRA_NOTIFICATION_TITLE = "notification_title";
    public static final String EXTRA_NOTIFICATION_TYPE = "notification_type";
    public static final String EXTRA_REALTIME_SAMPLE = "realtime_sample";
    @Deprecated
    public static final String EXTRA_REALTIME_STEPS = "realtime_steps";
    public static final String EXTRA_RECORDED_DATA_TYPES = "data_types";
    public static final String EXTRA_RESET_FLAGS = "reset_flags";
    public static final String EXTRA_TIMESTAMP = "timestamp";
    public static final String EXTRA_URI = "uri";
    public static final String EXTRA_VIBRATION_INTENSITY = "vibration_intensity";
    public static final String EXTRA_WEATHER = "weather";
    public static final String PREFIX = "nodomain.freeyourgadget.gadgetbridge.devices";

    void connect();

    void connect(GBDevice gBDevice);

    void connect(GBDevice gBDevice, boolean z);

    void disconnect();

    void quit();

    void requestDeviceInfo();

    void start();
}
