package nodomain.freeyourgadget.gadgetbridge.util;

import android.text.format.DateFormat;
import java.text.ParseException;
import java.util.Date;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.devicesettings.DeviceSettingsPreferenceConst;

public class GBPrefs {
    public static final String AUTO_EXPORT_ENABLED = "auto_export_enabled";
    public static final String AUTO_EXPORT_INTERVAL = "auto_export_interval";
    public static final String AUTO_EXPORT_LOCATION = "auto_export_location";
    public static final String AUTO_RECONNECT = "general_autocreconnect";
    public static boolean AUTO_RECONNECT_DEFAULT = AUTO_START_DEFAULT;
    private static final String AUTO_START = "general_autostartonboot";
    private static final boolean AUTO_START_DEFAULT = true;
    private static final String BG_JS_ENABLED = "pebble_enable_background_javascript";
    private static final boolean BG_JS_ENABLED_DEFAULT = false;
    public static final String CALENDAR_BLACKLIST = "calendar_blacklist";
    public static final String CHART_MAX_HEART_RATE = "chart_max_heart_rate";
    public static final String CHART_MIN_HEART_RATE = "chart_min_heart_rate";
    public static final String PACKAGE_BLACKLIST = "package_blacklist";
    public static final String PACKAGE_PEBBLEMSG_BLACKLIST = "package_pebblemsg_blacklist";
    public static final String RTL_CONTEXTUAL_ARABIC = "contextualArabic";
    public static final String RTL_SUPPORT = "rtl";
    private static final String USER_BIRTHDAY = "";
    public static final String USER_NAME = "mi_user_alias";
    public static final String USER_NAME_DEFAULT = "gadgetbridge-user";
    private final Prefs mPrefs;

    public GBPrefs(Prefs prefs) {
        this.mPrefs = prefs;
    }

    public boolean getAutoReconnect() {
        return this.mPrefs.getBoolean(AUTO_RECONNECT, AUTO_RECONNECT_DEFAULT);
    }

    public boolean getAutoStart() {
        return this.mPrefs.getBoolean(AUTO_START, AUTO_START_DEFAULT);
    }

    public boolean isBackgroundJsEnabled() {
        return this.mPrefs.getBoolean(BG_JS_ENABLED, false);
    }

    public String getUserName() {
        return this.mPrefs.getString("mi_user_alias", USER_NAME_DEFAULT);
    }

    public Date getUserBirthday() {
        String date = this.mPrefs.getString("", (String) null);
        if (date == null) {
            return null;
        }
        try {
            return DateTimeUtils.dayFromString(date);
        } catch (ParseException ex) {
            C1238GB.log("Error parsing date: " + date, 3, ex);
            return null;
        }
    }

    public int getUserGender() {
        return 0;
    }

    public String getTimeFormat() {
        String timeFormat = this.mPrefs.getString(DeviceSettingsPreferenceConst.PREF_TIMEFORMAT, "auto");
        if (!"auto".equals(timeFormat)) {
            return timeFormat;
        }
        if (DateFormat.is24HourFormat(GBApplication.getContext())) {
            return "24h";
        }
        return "am/pm";
    }
}
