package cyanogenmod.providers;

import android.net.Uri;

public class ThemesContract {
    public static final String AUTHORITY = "com.cyanogenmod.themes";
    public static final Uri AUTHORITY_URI = Uri.parse("content://com.cyanogenmod.themes");

    public static class PreviewColumns {
        public static final Uri APPLIED_URI = Uri.withAppendedPath(ThemesContract.AUTHORITY_URI, "applied_previews");
        public static final String BOOTANIMATION_THUMBNAIL = "bootanimation_thumbnail";
        public static final String COL_KEY = "key";
        public static final String COL_VALUE = "value";
        public static final Uri COMPONENTS_URI = Uri.withAppendedPath(ThemesContract.AUTHORITY_URI, "components_previews");
        public static final String COMPONENT_ID = "component_id";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ThemesContract.AUTHORITY_URI, "previews");
        public static final String ICON_PREVIEW_1 = "icon_preview_1";
        public static final String ICON_PREVIEW_2 = "icon_preview_2";
        public static final String ICON_PREVIEW_3 = "icon_preview_3";
        public static final String LIVE_LOCK_SCREEN_PREVIEW = "live_lock_screen_preview";
        public static final String LIVE_LOCK_SCREEN_THUMBNAIL = "live_lock_screen_thumbnail";
        public static final String LOCK_WALLPAPER_PREVIEW = "lock_wallpaper_preview";
        public static final String LOCK_WALLPAPER_THUMBNAIL = "lock_wallpaper_thumbnail";
        public static final String NAVBAR_BACKGROUND = "navbar_background";
        public static final String NAVBAR_BACK_BUTTON = "navbar_back_button";
        public static final String NAVBAR_HOME_BUTTON = "navbar_home_button";
        public static final String NAVBAR_RECENT_BUTTON = "navbar_recent_button";
        public static final String STATUSBAR_BACKGROUND = "statusbar_background";
        public static final String STATUSBAR_BATTERY_CIRCLE = "statusbar_battery_circle";
        public static final String STATUSBAR_BATTERY_LANDSCAPE = "statusbar_battery_landscape";
        public static final String STATUSBAR_BATTERY_PORTRAIT = "statusbar_battery_portrait";
        public static final String STATUSBAR_BLUETOOTH_ICON = "statusbar_bluetooth_icon";
        public static final String STATUSBAR_CLOCK_TEXT_COLOR = "statusbar_clock_text_color";
        public static final String STATUSBAR_SIGNAL_ICON = "statusbar_signal_icon";
        public static final String STATUSBAR_WIFI_COMBO_MARGIN_END = "wifi_combo_margin_end";
        public static final String STATUSBAR_WIFI_ICON = "statusbar_wifi_icon";
        public static final String STYLE_PREVIEW = "style_preview";
        public static final String STYLE_THUMBNAIL = "style_thumbnail";
        public static final String THEME_ID = "theme_id";
        public static final String[] VALID_KEYS = {STATUSBAR_BACKGROUND, STATUSBAR_BLUETOOTH_ICON, STATUSBAR_WIFI_ICON, STATUSBAR_SIGNAL_ICON, STATUSBAR_BATTERY_PORTRAIT, STATUSBAR_BATTERY_LANDSCAPE, STATUSBAR_BATTERY_CIRCLE, STATUSBAR_CLOCK_TEXT_COLOR, STATUSBAR_WIFI_COMBO_MARGIN_END, NAVBAR_BACKGROUND, NAVBAR_BACK_BUTTON, NAVBAR_HOME_BUTTON, NAVBAR_RECENT_BUTTON, ICON_PREVIEW_1, ICON_PREVIEW_2, ICON_PREVIEW_3, WALLPAPER_FULL, WALLPAPER_PREVIEW, WALLPAPER_THUMBNAIL, LOCK_WALLPAPER_PREVIEW, LOCK_WALLPAPER_THUMBNAIL, STYLE_PREVIEW, STYLE_THUMBNAIL, BOOTANIMATION_THUMBNAIL, LIVE_LOCK_SCREEN_PREVIEW, LIVE_LOCK_SCREEN_THUMBNAIL};
        public static final String WALLPAPER_FULL = "wallpaper_full";
        public static final String WALLPAPER_PREVIEW = "wallpaper_preview";
        public static final String WALLPAPER_THUMBNAIL = "wallpaper_thumbnail";
        public static final String _ID = "_id";
    }

    public static class ThemesColumns {
        public static final String AUTHOR = "author";
        public static final String BOOT_ANIM_URI = "bootanim_uri";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ThemesContract.AUTHORITY_URI, "themes");
        public static final String DATE_CREATED = "created";
        public static final String FONT_URI = "font_uri";
        public static final String HOMESCREEN_URI = "homescreen_uri";
        public static final String ICON_URI = "icon_uri";
        public static final String INSTALL_STATE = "install_state";
        public static final String INSTALL_TIME = "install_time";
        public static final String IS_DEFAULT_THEME = "is_default_theme";
        public static final String IS_LEGACY_ICONPACK = "is_legacy_iconpack";
        public static final String IS_LEGACY_THEME = "is_legacy_theme";
        public static final String LAST_UPDATE_TIME = "updateTime";
        public static final String LOCKSCREEN_URI = "lockscreen_uri";
        public static final String MODIFIES_ALARMS = "mods_alarms";
        public static final String MODIFIES_BOOT_ANIM = "mods_bootanim";
        public static final String MODIFIES_FONTS = "mods_fonts";
        public static final String MODIFIES_ICONS = "mods_icons";
        public static final String MODIFIES_LAUNCHER = "mods_homescreen";
        public static final String MODIFIES_LIVE_LOCK_SCREEN = "mods_live_lock_screen";
        public static final String MODIFIES_LOCKSCREEN = "mods_lockscreen";
        public static final String MODIFIES_NAVIGATION_BAR = "mods_navigation_bar";
        public static final String MODIFIES_NOTIFICATIONS = "mods_notifications";
        public static final String MODIFIES_OVERLAYS = "mods_overlays";
        public static final String MODIFIES_RINGTONES = "mods_ringtones";
        public static final String MODIFIES_STATUS_BAR = "mods_status_bar";
        public static final String OVERLAYS_URI = "overlays_uri";
        public static final String PKG_NAME = "pkg_name";
        public static final String PRESENT_AS_THEME = "present_as_theme";
        public static final String PRIMARY_COLOR = "primary_color";
        public static final String SECONDARY_COLOR = "secondary_color";
        public static final String STATUSBAR_URI = "status_uri";
        public static final String STYLE_URI = "style_uri";
        public static final String TARGET_API = "target_api";
        public static final String TITLE = "title";
        public static final String WALLPAPER_URI = "wallpaper_uri";
        public static final String _ID = "_id";

        public static class InstallState {
            public static final int INSTALLED = 3;
            public static final int INSTALLING = 1;
            public static final int UNKNOWN = 0;
            public static final int UPDATING = 2;
        }
    }

    public static class MixnMatchColumns {
        public static final String COL_COMPONENT_ID = "component_id";
        public static final String COL_KEY = "key";
        public static final String COL_PREV_VALUE = "previous_value";
        public static final String COL_UPDATE_TIME = "update_time";
        public static final String COL_VALUE = "value";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ThemesContract.AUTHORITY_URI, "mixnmatch");
        public static final String KEY_ALARM = "mixnmatch_alarm";
        public static final String KEY_BOOT_ANIM = "mixnmatch_boot_anim";
        public static final String KEY_FONT = "mixnmatch_font";
        public static final String KEY_HOMESCREEN = "mixnmatch_homescreen";
        public static final String KEY_ICONS = "mixnmatch_icons";
        public static final String KEY_LIVE_LOCK_SCREEN = "mixnmatch_live_lock_screen";
        public static final String KEY_LOCKSCREEN = "mixnmatch_lockscreen";
        public static final String KEY_NAVIGATION_BAR = "mixnmatch_navigation_bar";
        public static final String KEY_NOTIFICATIONS = "mixnmatch_notifications";
        public static final String KEY_OVERLAYS = "mixnmatch_overlays";
        public static final String KEY_RINGTONE = "mixnmatch_ringtone";
        public static final String KEY_STATUS_BAR = "mixnmatch_status_bar";
        public static final String[] ROWS = {KEY_HOMESCREEN, KEY_LOCKSCREEN, KEY_ICONS, KEY_STATUS_BAR, KEY_BOOT_ANIM, KEY_FONT, KEY_NOTIFICATIONS, KEY_RINGTONE, KEY_ALARM, KEY_OVERLAYS, KEY_NAVIGATION_BAR, KEY_LIVE_LOCK_SCREEN};

        public static String componentToImageColName(String component) {
            if (component.equals(KEY_HOMESCREEN)) {
                return ThemesColumns.HOMESCREEN_URI;
            }
            if (component.equals(KEY_LOCKSCREEN)) {
                return ThemesColumns.LOCKSCREEN_URI;
            }
            if (component.equals(KEY_BOOT_ANIM)) {
                return ThemesColumns.BOOT_ANIM_URI;
            }
            if (component.equals(KEY_FONT)) {
                return ThemesColumns.FONT_URI;
            }
            if (component.equals(KEY_ICONS)) {
                return ThemesColumns.ICON_URI;
            }
            if (component.equals(KEY_STATUS_BAR)) {
                return ThemesColumns.STATUSBAR_URI;
            }
            if (component.equals(KEY_NOTIFICATIONS)) {
                throw new IllegalArgumentException("Notifications mixnmatch component does not have a related column");
            } else if (component.equals(KEY_RINGTONE)) {
                throw new IllegalArgumentException("Ringtone mixnmatch component does not have a related column");
            } else if (component.equals(KEY_OVERLAYS)) {
                return ThemesColumns.OVERLAYS_URI;
            } else {
                if (component.equals(KEY_ALARM)) {
                    throw new IllegalArgumentException("Alarm mixnmatch component does not have a related column");
                } else if (component.equals(KEY_NAVIGATION_BAR)) {
                    throw new IllegalArgumentException("Navigation bar mixnmatch component does not have a related column");
                } else if (!component.equals(KEY_LIVE_LOCK_SCREEN)) {
                    return null;
                } else {
                    throw new IllegalArgumentException("Live lock screen mixnmatch component does not have a related column");
                }
            }
        }

        public static String componentToMixNMatchKey(String component) {
            if (component.equals(ThemesColumns.MODIFIES_LAUNCHER)) {
                return KEY_HOMESCREEN;
            }
            if (component.equals(ThemesColumns.MODIFIES_ICONS)) {
                return KEY_ICONS;
            }
            if (component.equals(ThemesColumns.MODIFIES_LOCKSCREEN)) {
                return KEY_LOCKSCREEN;
            }
            if (component.equals(ThemesColumns.MODIFIES_FONTS)) {
                return KEY_FONT;
            }
            if (component.equals(ThemesColumns.MODIFIES_BOOT_ANIM)) {
                return KEY_BOOT_ANIM;
            }
            if (component.equals(ThemesColumns.MODIFIES_ALARMS)) {
                return KEY_ALARM;
            }
            if (component.equals(ThemesColumns.MODIFIES_NOTIFICATIONS)) {
                return KEY_NOTIFICATIONS;
            }
            if (component.equals(ThemesColumns.MODIFIES_RINGTONES)) {
                return KEY_RINGTONE;
            }
            if (component.equals(ThemesColumns.MODIFIES_OVERLAYS)) {
                return KEY_OVERLAYS;
            }
            if (component.equals(ThemesColumns.MODIFIES_STATUS_BAR)) {
                return KEY_STATUS_BAR;
            }
            if (component.equals(ThemesColumns.MODIFIES_NAVIGATION_BAR)) {
                return KEY_NAVIGATION_BAR;
            }
            if (component.equals(ThemesColumns.MODIFIES_LIVE_LOCK_SCREEN)) {
                return KEY_LIVE_LOCK_SCREEN;
            }
            return null;
        }

        public static String mixNMatchKeyToComponent(String mixnmatchKey) {
            if (mixnmatchKey.equals(KEY_HOMESCREEN)) {
                return ThemesColumns.MODIFIES_LAUNCHER;
            }
            if (mixnmatchKey.equals(KEY_ICONS)) {
                return ThemesColumns.MODIFIES_ICONS;
            }
            if (mixnmatchKey.equals(KEY_LOCKSCREEN)) {
                return ThemesColumns.MODIFIES_LOCKSCREEN;
            }
            if (mixnmatchKey.equals(KEY_FONT)) {
                return ThemesColumns.MODIFIES_FONTS;
            }
            if (mixnmatchKey.equals(KEY_BOOT_ANIM)) {
                return ThemesColumns.MODIFIES_BOOT_ANIM;
            }
            if (mixnmatchKey.equals(KEY_ALARM)) {
                return ThemesColumns.MODIFIES_ALARMS;
            }
            if (mixnmatchKey.equals(KEY_NOTIFICATIONS)) {
                return ThemesColumns.MODIFIES_NOTIFICATIONS;
            }
            if (mixnmatchKey.equals(KEY_RINGTONE)) {
                return ThemesColumns.MODIFIES_RINGTONES;
            }
            if (mixnmatchKey.equals(KEY_OVERLAYS)) {
                return ThemesColumns.MODIFIES_OVERLAYS;
            }
            if (mixnmatchKey.equals(KEY_STATUS_BAR)) {
                return ThemesColumns.MODIFIES_STATUS_BAR;
            }
            if (mixnmatchKey.equals(KEY_NAVIGATION_BAR)) {
                return ThemesColumns.MODIFIES_NAVIGATION_BAR;
            }
            if (mixnmatchKey.equals(KEY_LIVE_LOCK_SCREEN)) {
                return ThemesColumns.MODIFIES_LIVE_LOCK_SCREEN;
            }
            return null;
        }
    }
}
