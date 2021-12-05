package nodomain.freeyourgadget.gadgetbridge.devices.huami;

public class HuamiConst {
    public static final String MI_BAND2_NAME = "MI Band 2";
    public static final String MI_BAND2_NAME_HRX = "Mi Band HRX";
    public static final String MI_BAND3_NAME = "Mi Band 3";
    public static final String MI_BAND3_NAME_2 = "Xiaomi Band 3";
    public static final String MI_BAND4_NAME = "Mi Smart Band 4";
    public static final String PREF_ACTIVATE_DISPLAY_ON_LIFT = "activate_display_on_lift_wrist";
    public static final String PREF_BUTTON_ACTION_BROADCAST = "button_action_broadcast";
    public static final String PREF_BUTTON_ACTION_BROADCAST_DELAY = "button_action_broadcast_delay";
    public static final String PREF_BUTTON_ACTION_ENABLE = "button_action_enable";
    public static final String PREF_BUTTON_ACTION_PRESS_COUNT = "button_action_press_count";
    public static final String PREF_BUTTON_ACTION_PRESS_MAX_INTERVAL = "button_action_press_max_interval";
    public static final String PREF_BUTTON_ACTION_VIBRATE = "button_action_vibrate";
    public static final String PREF_DISCONNECT_NOTIFICATION = "disconnect_notification";
    public static final String PREF_DISCONNECT_NOTIFICATION_END = "disconnect_notification_end";
    public static final String PREF_DISCONNECT_NOTIFICATION_START = "disconnect_notification_start";
    public static final String PREF_DISPLAY_ITEMS = "display_items";
    public static final String PREF_DISPLAY_ON_LIFT_END = "display_on_lift_end";
    public static final String PREF_DISPLAY_ON_LIFT_START = "display_on_lift_start";
    public static final String PREF_EXPOSE_HR_THIRDPARTY = "expose_hr_thirdparty";
    public static final String PREF_LANGUAGE = "language";
    public static final String PREF_USE_CUSTOM_FONT = "use_custom_font";
    public static final int TYPE_ACTIVITY = 1;
    public static final int TYPE_CHARGING = 6;
    public static final int TYPE_DEEP_SLEEP = 11;
    public static final int TYPE_IGNORE = 10;
    public static final int TYPE_LIGHT_SLEEP = 9;
    public static final int TYPE_NONWEAR = 3;
    public static final int TYPE_NO_CHANGE = 0;
    public static final int TYPE_RIDE_BIKE = 4;
    public static final int TYPE_RUNNING = 2;
    public static final int TYPE_UNSET = -1;
    public static final int TYPE_WAKE_UP = 12;

    public static int toActivityKind(int rawType) {
        if (!(rawType == 1 || rawType == 2)) {
            if (rawType == 3) {
                return 8;
            }
            if (rawType == 4) {
                return 128;
            }
            if (rawType == 6) {
                return 8;
            }
            if (rawType == 9) {
                return 2;
            }
            if (rawType == 11) {
                return 4;
            }
            if (rawType != 12) {
                return 0;
            }
        }
        return 1;
    }

    public static int toRawActivityType(int activityKind) {
        if (activityKind == 1) {
            return 1;
        }
        if (activityKind == 2) {
            return 9;
        }
        if (activityKind == 4) {
            return 11;
        }
        if (activityKind != 8) {
            return -1;
        }
        return 3;
    }
}
