package cyanogenmod.providers;

import android.content.ContentResolver;
import android.content.IContentProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AndroidException;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import org.apache.commons.lang3.StringUtils;

public final class CMSettings {
    public static final String ACTION_DATA_USAGE = "cyanogenmod.settings.ACTION_DATA_USAGE";
    public static final String ACTION_LIVEDISPLAY_SETTINGS = "cyanogenmod.settings.LIVEDISPLAY_SETTINGS";
    public static final String AUTHORITY = "cmsettings";
    public static final String CALL_METHOD_GET_GLOBAL = "GET_global";
    public static final String CALL_METHOD_GET_SECURE = "GET_secure";
    public static final String CALL_METHOD_GET_SYSTEM = "GET_system";
    public static final String CALL_METHOD_MIGRATE_SETTINGS = "migrate_settings";
    public static final String CALL_METHOD_MIGRATE_SETTINGS_FOR_USER = "migrate_settings_for_user";
    public static final String CALL_METHOD_PUT_GLOBAL = "PUT_global";
    public static final String CALL_METHOD_PUT_SECURE = "PUT_secure";
    public static final String CALL_METHOD_PUT_SYSTEM = "PUT_system";
    public static final String CALL_METHOD_USER_KEY = "_user";
    private static final boolean LOCAL_LOGV = false;
    private static final String TAG = "CMSettings";
    /* access modifiers changed from: private */
    public static final Validator sAlwaysTrueValidator = new Validator() {
        public boolean validate(String value) {
            return true;
        }
    };
    /* access modifiers changed from: private */
    public static final Validator sBooleanValidator = new DiscreteValueValidator(new String[]{"0", MiBandConst.MI_1});
    /* access modifiers changed from: private */
    public static final Validator sColorValidator = new InclusiveIntegerRangeValidator(Integer.MIN_VALUE, Integer.MAX_VALUE);
    /* access modifiers changed from: private */
    public static final Validator sNonNegativeIntegerValidator = new Validator() {
        public boolean validate(String value) {
            try {
                return Integer.parseInt(value) >= 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    };
    private static final Validator sUriValidator = new Validator() {
        public boolean validate(String value) {
            try {
                Uri.decode(value);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    };

    public interface Validator {
        boolean validate(String str);
    }

    public static class CMSettingNotFoundException extends AndroidException {
        public CMSettingNotFoundException(String msg) {
            super(msg);
        }
    }

    private static class NameValueCache {
        private static final String NAME_EQ_PLACEHOLDER = "name=?";
        private static final String[] SELECT_VALUE = {"value"};
        private final String mCallGetCommand;
        private final String mCallSetCommand;
        private IContentProvider mContentProvider = null;
        private final Uri mUri;
        private final HashMap<String, String> mValues = new HashMap<>();
        private long mValuesVersion = 0;
        private final String mVersionSystemProperty;

        public NameValueCache(String versionSystemProperty, Uri uri, String getCommand, String setCommand) {
            this.mVersionSystemProperty = versionSystemProperty;
            this.mUri = uri;
            this.mCallGetCommand = getCommand;
            this.mCallSetCommand = setCommand;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:7:0x0013, code lost:
            return r0;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private android.content.IContentProvider lazyGetProvider(android.content.ContentResolver r3) {
            /*
                r2 = this;
                monitor-enter(r2)
                android.content.IContentProvider r0 = r2.mContentProvider     // Catch:{ all -> 0x0014 }
                if (r0 != 0) goto L_0x0012
                android.net.Uri r1 = r2.mUri     // Catch:{ all -> 0x0018 }
                java.lang.String r1 = r1.getAuthority()     // Catch:{ all -> 0x0018 }
                android.content.IContentProvider r1 = r3.acquireProvider(r1)     // Catch:{ all -> 0x0018 }
                r2.mContentProvider = r1     // Catch:{ all -> 0x0018 }
                r0 = r1
            L_0x0012:
                monitor-exit(r2)     // Catch:{ all -> 0x0018 }
                return r0
            L_0x0014:
                r1 = move-exception
                r0 = 0
            L_0x0016:
                monitor-exit(r2)     // Catch:{ all -> 0x0018 }
                throw r1
            L_0x0018:
                r1 = move-exception
                goto L_0x0016
            */
            throw new UnsupportedOperationException("Method not decompiled: cyanogenmod.providers.CMSettings.NameValueCache.lazyGetProvider(android.content.ContentResolver):android.content.IContentProvider");
        }

        public boolean putStringForUser(ContentResolver cr, String name, String value, int userId) {
            try {
                Bundle arg = new Bundle();
                arg.putString("value", value);
                arg.putInt(CMSettings.CALL_METHOD_USER_KEY, userId);
                lazyGetProvider(cr).call(cr.getPackageName(), this.mCallSetCommand, name, arg);
                return true;
            } catch (RemoteException e) {
                Log.w(CMSettings.TAG, "Can't set key " + name + " in " + this.mUri, e);
                return false;
            }
        }

        /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
            java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0
            	at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
            	at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
            	at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:248)
            	at java.base/java.util.Objects.checkIndex(Objects.java:372)
            	at java.base/java.util.ArrayList.get(ArrayList.java:458)
            	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
            	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
            	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
            	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
            	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
            */
        public java.lang.String getStringForUser(android.content.ContentResolver r19, java.lang.String r20, int r21) {
            /*
                r18 = this;
                r1 = r18
                r2 = r20
                r3 = r21
                int r0 = android.os.UserHandle.myUserId()
                r4 = 1
                r5 = 0
                if (r3 != r0) goto L_0x0010
                r0 = 1
                goto L_0x0011
            L_0x0010:
                r0 = 0
            L_0x0011:
                r6 = r0
                if (r6 == 0) goto L_0x0041
                java.lang.String r0 = r1.mVersionSystemProperty
                r7 = 0
                long r7 = android.os.SystemProperties.getLong(r0, r7)
                monitor-enter(r18)
                long r9 = r1.mValuesVersion     // Catch:{ all -> 0x003e }
                int r0 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
                if (r0 == 0) goto L_0x002a
                java.util.HashMap<java.lang.String, java.lang.String> r0 = r1.mValues     // Catch:{ all -> 0x003e }
                r0.clear()     // Catch:{ all -> 0x003e }
                r1.mValuesVersion = r7     // Catch:{ all -> 0x003e }
            L_0x002a:
                java.util.HashMap<java.lang.String, java.lang.String> r0 = r1.mValues     // Catch:{ all -> 0x003e }
                boolean r0 = r0.containsKey(r2)     // Catch:{ all -> 0x003e }
                if (r0 == 0) goto L_0x003c
                java.util.HashMap<java.lang.String, java.lang.String> r0 = r1.mValues     // Catch:{ all -> 0x003e }
                java.lang.Object r0 = r0.get(r2)     // Catch:{ all -> 0x003e }
                java.lang.String r0 = (java.lang.String) r0     // Catch:{ all -> 0x003e }
                monitor-exit(r18)     // Catch:{ all -> 0x003e }
                return r0
            L_0x003c:
                monitor-exit(r18)     // Catch:{ all -> 0x003e }
                goto L_0x0041
            L_0x003e:
                r0 = move-exception
                monitor-exit(r18)     // Catch:{ all -> 0x003e }
                throw r0
            L_0x0041:
                android.content.IContentProvider r7 = r18.lazyGetProvider(r19)
                java.lang.String r0 = r1.mCallGetCommand
                if (r0 == 0) goto L_0x007c
                r0 = 0
                if (r6 != 0) goto L_0x0059
                android.os.Bundle r8 = new android.os.Bundle     // Catch:{ RemoteException -> 0x007b }
                r8.<init>()     // Catch:{ RemoteException -> 0x007b }
                r0 = r8
                java.lang.String r8 = "_user"
                r0.putInt(r8, r3)     // Catch:{ RemoteException -> 0x007b }
                r8 = r0
                goto L_0x005a
            L_0x0059:
                r8 = r0
            L_0x005a:
                java.lang.String r0 = r19.getPackageName()     // Catch:{ RemoteException -> 0x007b }
                java.lang.String r9 = r1.mCallGetCommand     // Catch:{ RemoteException -> 0x007b }
                android.os.Bundle r0 = r7.call(r0, r9, r2, r8)     // Catch:{ RemoteException -> 0x007b }
                r9 = r0
                if (r9 == 0) goto L_0x007a
                java.lang.String r0 = r9.getPairValue()     // Catch:{ RemoteException -> 0x007b }
                r10 = r0
                if (r6 == 0) goto L_0x0079
                monitor-enter(r18)     // Catch:{ RemoteException -> 0x007b }
                java.util.HashMap<java.lang.String, java.lang.String> r0 = r1.mValues     // Catch:{ all -> 0x0076 }
                r0.put(r2, r10)     // Catch:{ all -> 0x0076 }
                monitor-exit(r18)     // Catch:{ all -> 0x0076 }
                goto L_0x0079
            L_0x0076:
                r0 = move-exception
                monitor-exit(r18)     // Catch:{ all -> 0x0076 }
                throw r0     // Catch:{ RemoteException -> 0x007b }
            L_0x0079:
                return r10
            L_0x007a:
                goto L_0x007c
            L_0x007b:
                r0 = move-exception
            L_0x007c:
                r8 = 0
                r17 = 0
                java.lang.String r10 = r19.getPackageName()     // Catch:{ RemoteException -> 0x00e0 }
                android.net.Uri r11 = r1.mUri     // Catch:{ RemoteException -> 0x00e0 }
                java.lang.String[] r12 = SELECT_VALUE     // Catch:{ RemoteException -> 0x00e0 }
                java.lang.String r13 = "name=?"
                java.lang.String[] r14 = new java.lang.String[r4]     // Catch:{ RemoteException -> 0x00e0 }
                r14[r5] = r2     // Catch:{ RemoteException -> 0x00e0 }
                r15 = 0
                r16 = 0
                r9 = r7
                android.database.Cursor r0 = r9.query(r10, r11, r12, r13, r14, r15, r16)     // Catch:{ RemoteException -> 0x00e0 }
                r8 = r0
                if (r8 != 0) goto L_0x00bf
                java.lang.String r0 = "CMSettings"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x00e0 }
                r4.<init>()     // Catch:{ RemoteException -> 0x00e0 }
                java.lang.String r5 = "Can't get key "
                r4.append(r5)     // Catch:{ RemoteException -> 0x00e0 }
                r4.append(r2)     // Catch:{ RemoteException -> 0x00e0 }
                java.lang.String r5 = " from "
                r4.append(r5)     // Catch:{ RemoteException -> 0x00e0 }
                android.net.Uri r5 = r1.mUri     // Catch:{ RemoteException -> 0x00e0 }
                r4.append(r5)     // Catch:{ RemoteException -> 0x00e0 }
                java.lang.String r4 = r4.toString()     // Catch:{ RemoteException -> 0x00e0 }
                android.util.Log.w(r0, r4)     // Catch:{ RemoteException -> 0x00e0 }
                if (r8 == 0) goto L_0x00be
                r8.close()
            L_0x00be:
                return r17
            L_0x00bf:
                boolean r0 = r8.moveToNext()     // Catch:{ RemoteException -> 0x00e0 }
                if (r0 == 0) goto L_0x00ca
                java.lang.String r0 = r8.getString(r5)     // Catch:{ RemoteException -> 0x00e0 }
                goto L_0x00cc
            L_0x00ca:
                r0 = r17
            L_0x00cc:
                r4 = r0
                monitor-enter(r18)     // Catch:{ RemoteException -> 0x00e0 }
                java.util.HashMap<java.lang.String, java.lang.String> r0 = r1.mValues     // Catch:{ all -> 0x00db }
                r0.put(r2, r4)     // Catch:{ all -> 0x00db }
                monitor-exit(r18)     // Catch:{ all -> 0x00db }
                if (r8 == 0) goto L_0x00da
                r8.close()
            L_0x00da:
                return r4
            L_0x00db:
                r0 = move-exception
                monitor-exit(r18)     // Catch:{ all -> 0x00db }
                throw r0     // Catch:{ RemoteException -> 0x00e0 }
            L_0x00de:
                r0 = move-exception
                goto L_0x0108
            L_0x00e0:
                r0 = move-exception
                java.lang.String r4 = "CMSettings"
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00de }
                r5.<init>()     // Catch:{ all -> 0x00de }
                java.lang.String r9 = "Can't get key "
                r5.append(r9)     // Catch:{ all -> 0x00de }
                r5.append(r2)     // Catch:{ all -> 0x00de }
                java.lang.String r9 = " from "
                r5.append(r9)     // Catch:{ all -> 0x00de }
                android.net.Uri r9 = r1.mUri     // Catch:{ all -> 0x00de }
                r5.append(r9)     // Catch:{ all -> 0x00de }
                java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00de }
                android.util.Log.w(r4, r5, r0)     // Catch:{ all -> 0x00de }
                if (r8 == 0) goto L_0x0107
                r8.close()
            L_0x0107:
                return r17
            L_0x0108:
                if (r8 == 0) goto L_0x010d
                r8.close()
            L_0x010d:
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: cyanogenmod.providers.CMSettings.NameValueCache.getStringForUser(android.content.ContentResolver, java.lang.String, int):java.lang.String");
        }
    }

    private static final class DiscreteValueValidator implements Validator {
        private final String[] mValues;

        public DiscreteValueValidator(String[] values) {
            this.mValues = values;
        }

        public boolean validate(String value) {
            return ArrayUtils.contains(this.mValues, value);
        }
    }

    private static final class InclusiveIntegerRangeValidator implements Validator {
        private final int mMax;
        private final int mMin;

        public InclusiveIntegerRangeValidator(int min, int max) {
            this.mMin = min;
            this.mMax = max;
        }

        public boolean validate(String value) {
            try {
                int intValue = Integer.parseInt(value);
                if (intValue < this.mMin || intValue > this.mMax) {
                    return false;
                }
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    private static final class InclusiveFloatRangeValidator implements Validator {
        private final float mMax;
        private final float mMin;

        public InclusiveFloatRangeValidator(float min, float max) {
            this.mMin = min;
            this.mMax = max;
        }

        public boolean validate(String value) {
            try {
                float floatValue = Float.parseFloat(value);
                if (floatValue < this.mMin || floatValue > this.mMax) {
                    return false;
                }
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    private static final class DelimitedListValidator implements Validator {
        private final boolean mAllowEmptyList;
        private final String mDelimiter;
        private final ArraySet<String> mValidValueSet;

        public DelimitedListValidator(String[] validValues, String delimiter, boolean allowEmptyList) {
            this.mValidValueSet = new ArraySet<>(Arrays.asList(validValues));
            this.mDelimiter = delimiter;
            this.mAllowEmptyList = allowEmptyList;
        }

        public boolean validate(String value) {
            ArraySet<String> values = new ArraySet<>();
            if (!TextUtils.isEmpty(value)) {
                for (String item : TextUtils.split(value, Pattern.quote(this.mDelimiter))) {
                    if (!TextUtils.isEmpty(item)) {
                        values.add(item);
                    }
                }
            }
            if (values.size() <= 0) {
                return this.mAllowEmptyList;
            }
            values.removeAll(this.mValidValueSet);
            if (values.size() == 0) {
                return true;
            }
            return false;
        }
    }

    public static final class System extends Settings.NameValueTable {
        public static final String APP_SWITCH_WAKE_SCREEN = "app_switch_wake_screen";
        public static final Validator APP_SWITCH_WAKE_SCREEN_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String ASSIST_WAKE_SCREEN = "assist_wake_screen";
        public static final Validator ASSIST_WAKE_SCREEN_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String BACK_WAKE_SCREEN = "back_wake_screen";
        public static final Validator BACK_WAKE_SCREEN_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String BATTERY_LIGHT_ENABLED = "battery_light_enabled";
        public static final Validator BATTERY_LIGHT_ENABLED_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String BATTERY_LIGHT_FULL_COLOR = "battery_light_full_color";
        public static final Validator BATTERY_LIGHT_FULL_COLOR_VALIDATOR = CMSettings.sColorValidator;
        public static final String BATTERY_LIGHT_LOW_COLOR = "battery_light_low_color";
        public static final Validator BATTERY_LIGHT_LOW_COLOR_VALIDATOR = CMSettings.sColorValidator;
        public static final String BATTERY_LIGHT_MEDIUM_COLOR = "battery_light_medium_color";
        public static final Validator BATTERY_LIGHT_MEDIUM_COLOR_VALIDATOR = CMSettings.sColorValidator;
        public static final String BATTERY_LIGHT_PULSE = "battery_light_pulse";
        public static final Validator BATTERY_LIGHT_PULSE_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String BLUETOOTH_ACCEPT_ALL_FILES = "bluetooth_accept_all_files";
        public static final Validator BLUETOOTH_ACCEPT_ALL_FILES_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String CALL_RECORDING_FORMAT = "call_recording_format";
        public static final Validator CALL_RECORDING_FORMAT_VALIDATOR = new InclusiveIntegerRangeValidator(0, 1);
        public static final String CAMERA_LAUNCH = "camera_launch";
        public static final Validator CAMERA_LAUNCH_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String CAMERA_SLEEP_ON_RELEASE = "camera_sleep_on_release";
        public static final Validator CAMERA_SLEEP_ON_RELEASE_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String CAMERA_WAKE_SCREEN = "camera_wake_screen";
        public static final Validator CAMERA_WAKE_SCREEN_VALIDATOR = CMSettings.sBooleanValidator;
        public static final Uri CONTENT_URI = Uri.parse("content://cmsettings/system");
        public static final String DIALER_OPENCNAM_ACCOUNT_SID = "dialer_opencnam_account_sid";
        public static final Validator DIALER_OPENCNAM_ACCOUNT_SID_VALIDATOR = CMSettings.sAlwaysTrueValidator;
        public static final String DIALER_OPENCNAM_AUTH_TOKEN = "dialer_opencnam_auth_token";
        public static final Validator DIALER_OPENCNAM_AUTH_TOKEN_VALIDATOR = CMSettings.sAlwaysTrueValidator;
        public static final String DISPLAY_AUTO_CONTRAST = "display_auto_contrast";
        public static final Validator DISPLAY_AUTO_CONTRAST_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String DISPLAY_AUTO_OUTDOOR_MODE = "display_auto_outdoor_mode";
        public static final Validator DISPLAY_AUTO_OUTDOOR_MODE_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String DISPLAY_CABC = "display_low_power";
        public static final Validator DISPLAY_CABC_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String DISPLAY_COLOR_ADJUSTMENT = "display_color_adjustment";
        public static final Validator DISPLAY_COLOR_ADJUSTMENT_VALIDATOR = new Validator() {
            public boolean validate(String value) {
                String[] colorAdjustment = value == null ? null : value.split(StringUtils.SPACE);
                if (colorAdjustment != null && colorAdjustment.length != 3) {
                    return false;
                }
                Validator floatValidator = new InclusiveFloatRangeValidator(0.0f, 1.0f);
                if (colorAdjustment == null || (floatValidator.validate(colorAdjustment[0]) && floatValidator.validate(colorAdjustment[1]) && floatValidator.validate(colorAdjustment[2]))) {
                    return true;
                }
                return false;
            }
        };
        public static final String DISPLAY_COLOR_ENHANCE = "display_color_enhance";
        public static final Validator DISPLAY_COLOR_ENHANCE_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String DISPLAY_LOW_POWER = "display_low_power";
        public static final String DISPLAY_PICTURE_ADJUSTMENT = "display_picture_adjustment";
        public static final Validator DISPLAY_PICTURE_ADJUSTMENT_VALIDATOR = new Validator() {
            public boolean validate(String value) {
                if (TextUtils.isEmpty(value)) {
                    return true;
                }
                for (String s : TextUtils.split(value, ",")) {
                    if (TextUtils.split(s, ":").length != 2) {
                        return false;
                    }
                }
                return true;
            }
        };
        public static final String DISPLAY_TEMPERATURE_DAY = "display_temperature_day";
        public static final Validator DISPLAY_TEMPERATURE_DAY_VALIDATOR = new InclusiveIntegerRangeValidator(0, 100000);
        public static final String DISPLAY_TEMPERATURE_MODE = "display_temperature_mode";
        public static final Validator DISPLAY_TEMPERATURE_MODE_VALIDATOR = new InclusiveIntegerRangeValidator(0, 4);
        public static final String DISPLAY_TEMPERATURE_NIGHT = "display_temperature_night";
        public static final Validator DISPLAY_TEMPERATURE_NIGHT_VALIDATOR = new InclusiveIntegerRangeValidator(0, 100000);
        public static final String DOUBLE_TAP_SLEEP_GESTURE = "double_tap_sleep_gesture";
        public static final Validator DOUBLE_TAP_SLEEP_GESTURE_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String ENABLE_FORWARD_LOOKUP = "enable_forward_lookup";
        public static final Validator ENABLE_FORWARD_LOOKUP_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String ENABLE_MWI_NOTIFICATION = "enable_mwi_notification";
        public static final Validator ENABLE_MWI_NOTIFICATION_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String ENABLE_PEOPLE_LOOKUP = "enable_people_lookup";
        public static final Validator ENABLE_PEOPLE_LOOKUP_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String ENABLE_REVERSE_LOOKUP = "enable_reverse_lookup";
        public static final Validator ENABLE_REVERSE_LOOKUP_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String FORWARD_LOOKUP_PROVIDER = "forward_lookup_provider";
        public static final Validator FORWARD_LOOKUP_PROVIDER_VALIDATOR = CMSettings.sAlwaysTrueValidator;
        public static final String HEADSET_CONNECT_PLAYER = "headset_connect_player";
        public static final Validator HEADSET_CONNECT_PLAYER_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String HIGH_TOUCH_SENSITIVITY_ENABLE = "high_touch_sensitivity_enable";
        public static final Validator HIGH_TOUCH_SENSITIVITY_ENABLE_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String HOME_WAKE_SCREEN = "home_wake_screen";
        public static final Validator HOME_WAKE_SCREEN_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String INCREASING_RING = "increasing_ring";
        public static final String INCREASING_RING_RAMP_UP_TIME = "increasing_ring_ramp_up_time";
        public static final Validator INCREASING_RING_RAMP_UP_TIME_VALIDATOR = new InclusiveIntegerRangeValidator(5, 60);
        public static final String INCREASING_RING_START_VOLUME = "increasing_ring_start_vol";
        public static final Validator INCREASING_RING_START_VOLUME_VALIDATOR = new InclusiveFloatRangeValidator(0.0f, 1.0f);
        public static final Validator INCREASING_RING_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String KEY_APP_SWITCH_ACTION = "key_app_switch_action";
        public static final Validator KEY_APP_SWITCH_ACTION_VALIDATOR = new InclusiveIntegerRangeValidator(0, 8);
        public static final String KEY_APP_SWITCH_LONG_PRESS_ACTION = "key_app_switch_long_press_action";
        public static final Validator KEY_APP_SWITCH_LONG_PRESS_ACTION_VALIDATOR = new InclusiveIntegerRangeValidator(0, 8);
        public static final String KEY_ASSIST_ACTION = "key_assist_action";
        public static final Validator KEY_ASSIST_ACTION_VALIDATOR = new InclusiveIntegerRangeValidator(0, 8);
        public static final String KEY_ASSIST_LONG_PRESS_ACTION = "key_assist_long_press_action";
        public static final Validator KEY_ASSIST_LONG_PRESS_ACTION_VALIDATOR = new InclusiveIntegerRangeValidator(0, 8);
        public static final String KEY_HOME_DOUBLE_TAP_ACTION = "key_home_double_tap_action";
        public static final Validator KEY_HOME_DOUBLE_TAP_ACTION_VALIDATOR = new InclusiveIntegerRangeValidator(0, 8);
        public static final String KEY_HOME_LONG_PRESS_ACTION = "key_home_long_press_action";
        public static final Validator KEY_HOME_LONG_PRESS_ACTION_VALIDATOR = new InclusiveIntegerRangeValidator(0, 8);
        public static final String KEY_MENU_ACTION = "key_menu_action";
        public static final Validator KEY_MENU_ACTION_VALIDATOR = new InclusiveIntegerRangeValidator(0, 8);
        public static final String KEY_MENU_LONG_PRESS_ACTION = "key_menu_long_press_action";
        public static final Validator KEY_MENU_LONG_PRESS_ACTION_VALIDATOR = new InclusiveIntegerRangeValidator(0, 8);
        public static final String[] LEGACY_SYSTEM_SETTINGS = {NAV_BUTTONS, KEY_HOME_LONG_PRESS_ACTION, KEY_HOME_DOUBLE_TAP_ACTION, BACK_WAKE_SCREEN, MENU_WAKE_SCREEN, VOLUME_WAKE_SCREEN, KEY_MENU_ACTION, KEY_MENU_LONG_PRESS_ACTION, KEY_ASSIST_ACTION, KEY_ASSIST_LONG_PRESS_ACTION, KEY_APP_SWITCH_ACTION, KEY_APP_SWITCH_LONG_PRESS_ACTION, HOME_WAKE_SCREEN, ASSIST_WAKE_SCREEN, APP_SWITCH_WAKE_SCREEN, CAMERA_WAKE_SCREEN, CAMERA_SLEEP_ON_RELEASE, CAMERA_LAUNCH, SWAP_VOLUME_KEYS_ON_ROTATION, BATTERY_LIGHT_ENABLED, BATTERY_LIGHT_PULSE, BATTERY_LIGHT_LOW_COLOR, BATTERY_LIGHT_MEDIUM_COLOR, BATTERY_LIGHT_FULL_COLOR, ENABLE_MWI_NOTIFICATION, PROXIMITY_ON_WAKE, ENABLE_FORWARD_LOOKUP, ENABLE_PEOPLE_LOOKUP, ENABLE_REVERSE_LOOKUP, FORWARD_LOOKUP_PROVIDER, PEOPLE_LOOKUP_PROVIDER, REVERSE_LOOKUP_PROVIDER, DIALER_OPENCNAM_ACCOUNT_SID, DIALER_OPENCNAM_AUTH_TOKEN, DISPLAY_TEMPERATURE_DAY, DISPLAY_TEMPERATURE_NIGHT, DISPLAY_TEMPERATURE_MODE, DISPLAY_AUTO_OUTDOOR_MODE, "display_low_power", DISPLAY_COLOR_ENHANCE, DISPLAY_COLOR_ADJUSTMENT, LIVE_DISPLAY_HINTED, DOUBLE_TAP_SLEEP_GESTURE, STATUS_BAR_SHOW_WEATHER, RECENTS_SHOW_SEARCH_BAR, NAVBAR_LEFT_IN_LANDSCAPE, T9_SEARCH_INPUT_LOCALE, BLUETOOTH_ACCEPT_ALL_FILES, LOCKSCREEN_PIN_SCRAMBLE_LAYOUT, SHOW_ALARM_ICON, STATUS_BAR_IME_SWITCHER, "qs_show_brightness_slider", STATUS_BAR_BRIGHTNESS_CONTROL, VOLBTN_MUSIC_CONTROLS, SWAP_VOLUME_KEYS_ON_ROTATION, USE_EDGE_SERVICE_FOR_GESTURES, STATUS_BAR_NOTIF_COUNT, CALL_RECORDING_FORMAT, NOTIFICATION_LIGHT_BRIGHTNESS_LEVEL, NOTIFICATION_LIGHT_MULTIPLE_LEDS_ENABLE, NOTIFICATION_LIGHT_SCREEN_ON, NOTIFICATION_LIGHT_PULSE_DEFAULT_COLOR, NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_ON, NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_OFF, NOTIFICATION_LIGHT_PULSE_CALL_COLOR, NOTIFICATION_LIGHT_PULSE_CALL_LED_ON, NOTIFICATION_LIGHT_PULSE_CALL_LED_OFF, NOTIFICATION_LIGHT_PULSE_VMAIL_COLOR, NOTIFICATION_LIGHT_PULSE_VMAIL_LED_ON, NOTIFICATION_LIGHT_PULSE_VMAIL_LED_OFF, NOTIFICATION_LIGHT_PULSE_CUSTOM_ENABLE, NOTIFICATION_LIGHT_PULSE_CUSTOM_VALUES, STATUS_BAR_QUICK_QS_PULLDOWN, VOLUME_ADJUST_SOUNDS_ENABLED, SYSTEM_PROFILES_ENABLED, INCREASING_RING, INCREASING_RING_START_VOLUME, INCREASING_RING_RAMP_UP_TIME, STATUS_BAR_CLOCK, STATUS_BAR_AM_PM, STATUS_BAR_BATTERY_STYLE, STATUS_BAR_SHOW_BATTERY_PERCENT, VOLUME_KEYS_CONTROL_RING_STREAM, NAVIGATION_BAR_MENU_ARROW_KEYS, HEADSET_CONNECT_PLAYER, ZEN_ALLOW_LIGHTS, TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK};
        public static final String LIVE_DISPLAY_HINTED = "live_display_hinted";
        public static final Validator LIVE_DISPLAY_HINTED_VALIDATOR = new InclusiveIntegerRangeValidator(-3, 1);
        public static final String LOCKSCREEN_PIN_SCRAMBLE_LAYOUT = "lockscreen_scramble_pin_layout";
        public static final Validator LOCKSCREEN_PIN_SCRAMBLE_LAYOUT_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String LOCKSCREEN_ROTATION = "lockscreen_rotation";
        public static final Validator LOCKSCREEN_ROTATION_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String MENU_WAKE_SCREEN = "menu_wake_screen";
        public static final Validator MENU_WAKE_SCREENN_VALIDATOR = CMSettings.sBooleanValidator;
        protected static final ArraySet<String> MOVED_TO_SECURE = new ArraySet<>(1);
        public static final String NAVBAR_LEFT_IN_LANDSCAPE = "navigation_bar_left";
        public static final Validator NAVBAR_LEFT_IN_LANDSCAPE_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String NAVIGATION_BAR_MENU_ARROW_KEYS = "navigation_bar_menu_arrow_keys";
        public static final Validator NAVIGATION_BAR_MENU_ARROW_KEYS_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String NAV_BUTTONS = "nav_buttons";
        public static final Validator NAV_BUTTONS_VALIDATOR = new DelimitedListValidator(new String[]{"empty", "home", "back", "search", "recent", "menu0", "menu1", "menu2", "dpad_left", "dpad_right"}, "|", true);
        public static final String NOTIFICATION_LIGHT_BRIGHTNESS_LEVEL = "notification_light_brightness_level";
        public static final Validator NOTIFICATION_LIGHT_BRIGHTNESS_LEVEL_VALIDATOR = new InclusiveIntegerRangeValidator(1, 255);
        public static final String NOTIFICATION_LIGHT_COLOR_AUTO = "notification_light_color_auto";
        public static final Validator NOTIFICATION_LIGHT_COLOR_AUTO_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String NOTIFICATION_LIGHT_MULTIPLE_LEDS_ENABLE = "notification_light_multiple_leds_enable";
        public static final Validator NOTIFICATION_LIGHT_MULTIPLE_LEDS_ENABLE_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String NOTIFICATION_LIGHT_PULSE_CALL_COLOR = "notification_light_pulse_call_color";
        public static final Validator NOTIFICATION_LIGHT_PULSE_CALL_COLOR_VALIDATOR = CMSettings.sColorValidator;
        public static final String NOTIFICATION_LIGHT_PULSE_CALL_LED_OFF = "notification_light_pulse_call_led_off";
        public static final Validator NOTIFICATION_LIGHT_PULSE_CALL_LED_OFF_VALIDATOR = CMSettings.sNonNegativeIntegerValidator;
        public static final String NOTIFICATION_LIGHT_PULSE_CALL_LED_ON = "notification_light_pulse_call_led_on";
        public static final Validator NOTIFICATION_LIGHT_PULSE_CALL_LED_ON_VALIDATOR = CMSettings.sNonNegativeIntegerValidator;
        public static final String NOTIFICATION_LIGHT_PULSE_CUSTOM_ENABLE = "notification_light_pulse_custom_enable";
        public static final Validator NOTIFICATION_LIGHT_PULSE_CUSTOM_ENABLE_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String NOTIFICATION_LIGHT_PULSE_CUSTOM_VALUES = "notification_light_pulse_custom_values";
        public static final Validator NOTIFICATION_LIGHT_PULSE_CUSTOM_VALUES_VALIDATOR = new Validator() {
            public boolean validate(String value) {
                if (TextUtils.isEmpty(value)) {
                    return true;
                }
                String[] arr$ = value.split("\\|");
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    String[] packageValues = arr$[i$].split("=");
                    if (packageValues.length != 2 || TextUtils.isEmpty(packageValues[0])) {
                        return false;
                    }
                    String[] values = packageValues[1].split(";");
                    if (values.length != 3) {
                        return false;
                    }
                    try {
                        if (!CMSettings.sColorValidator.validate(values[0]) || !CMSettings.sNonNegativeIntegerValidator.validate(values[1]) || !CMSettings.sNonNegativeIntegerValidator.validate(values[2])) {
                            return false;
                        }
                        i$++;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
                return true;
            }
        };
        public static final String NOTIFICATION_LIGHT_PULSE_DEFAULT_COLOR = "notification_light_pulse_default_color";
        public static final Validator NOTIFICATION_LIGHT_PULSE_DEFAULT_COLOR_VALIDATOR = CMSettings.sColorValidator;
        public static final String NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_OFF = "notification_light_pulse_default_led_off";
        public static final Validator NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_OFF_VALIDATOR = CMSettings.sNonNegativeIntegerValidator;
        public static final String NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_ON = "notification_light_pulse_default_led_on";
        public static final Validator NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_ON_VALIDATOR = CMSettings.sNonNegativeIntegerValidator;
        public static final String NOTIFICATION_LIGHT_PULSE_VMAIL_COLOR = "notification_light_pulse_vmail_color";
        public static final Validator NOTIFICATION_LIGHT_PULSE_VMAIL_COLOR_VALIDATOR = CMSettings.sColorValidator;
        public static final String NOTIFICATION_LIGHT_PULSE_VMAIL_LED_OFF = "notification_light_pulse_vmail_led_off";
        public static final Validator NOTIFICATION_LIGHT_PULSE_VMAIL_LED_OFF_VALIDATOR = CMSettings.sNonNegativeIntegerValidator;
        public static final String NOTIFICATION_LIGHT_PULSE_VMAIL_LED_ON = "notification_light_pulse_vmail_led_on";
        public static final Validator NOTIFICATION_LIGHT_PULSE_VMAIL_LED_ON_VALIDATOR = CMSettings.sNonNegativeIntegerValidator;
        public static final String NOTIFICATION_LIGHT_SCREEN_ON = "notification_light_screen_on_enable";
        public static final Validator NOTIFICATION_LIGHT_SCREEN_ON_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String NOTIFICATION_PLAY_QUEUE = "notification_play_queue";
        public static final Validator NOTIFICATION_PLAY_QUEUE_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String PEOPLE_LOOKUP_PROVIDER = "people_lookup_provider";
        public static final Validator PEOPLE_LOOKUP_PROVIDER_VALIDATOR = CMSettings.sAlwaysTrueValidator;
        public static final String PROXIMITY_ON_WAKE = "proximity_on_wake";
        public static final Validator PROXIMITY_ON_WAKE_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String QS_SHOW_BRIGHTNESS_SLIDER = "qs_show_brightness_slider";
        public static final Validator QS_SHOW_BRIGHTNESS_SLIDER_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String RECENTS_SHOW_SEARCH_BAR = "recents_show_search_bar";
        public static final Validator RECENTS_SHOW_SEARCH_BAR_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String REVERSE_LOOKUP_PROVIDER = "reverse_lookup_provider";
        public static final Validator REVERSE_LOOKUP_PROVIDER_VALIDATOR = CMSettings.sAlwaysTrueValidator;
        public static final String SHOW_ALARM_ICON = "show_alarm_icon";
        public static final Validator SHOW_ALARM_ICON_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String STATUS_BAR_AM_PM = "status_bar_am_pm";
        public static final Validator STATUS_BAR_AM_PM_VALIDATOR = new InclusiveIntegerRangeValidator(0, 2);
        public static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
        public static final Validator STATUS_BAR_BATTERY_STYLE_VALIDATOR = new DiscreteValueValidator(new String[]{"0", MiBandConst.MI_PRO, "4", "5", "6"});
        public static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";
        public static final Validator STATUS_BAR_BRIGHTNESS_CONTROL_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String STATUS_BAR_CLOCK = "status_bar_clock";
        public static final Validator STATUS_BAR_CLOCK_VALIDATOR = new InclusiveIntegerRangeValidator(0, 3);
        public static final String STATUS_BAR_IME_SWITCHER = "status_bar_ime_switcher";
        public static final Validator STATUS_BAR_IME_SWITCHER_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
        public static final Validator STATUS_BAR_NOTIF_COUNT_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String STATUS_BAR_QUICK_QS_PULLDOWN = "qs_quick_pulldown";
        public static final Validator STATUS_BAR_QUICK_QS_PULLDOWN_VALIDATOR = new InclusiveIntegerRangeValidator(0, 2);
        public static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
        public static final Validator STATUS_BAR_SHOW_BATTERY_PERCENT_VALIDATOR = new InclusiveIntegerRangeValidator(0, 2);
        public static final String STATUS_BAR_SHOW_WEATHER = "status_bar_show_weather";
        public static final Validator STATUS_BAR_SHOW_WEATHER_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String SWAP_VOLUME_KEYS_ON_ROTATION = "swap_volume_keys_on_rotation";
        public static final Validator SWAP_VOLUME_KEYS_ON_ROTATION_VALIDATOR = new InclusiveIntegerRangeValidator(0, 2);
        public static final String SYSTEM_PROFILES_ENABLED = "system_profiles_enabled";
        public static final Validator SYSTEM_PROFILES_ENABLED_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String SYS_PROP_CM_SETTING_VERSION = "sys.cm_settings_system_version";
        public static final String T9_SEARCH_INPUT_LOCALE = "t9_search_input_locale";
        public static final Validator T9_SEARCH_INPUT_LOCALE_VALIDATOR = new Validator() {
            public boolean validate(String value) {
                return ArrayUtils.contains(Locale.getAvailableLocales(), new Locale(value));
            }
        };
        public static final String TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK = "touchscreen_gesture_haptic_feedback";
        public static final Validator TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String USE_EDGE_SERVICE_FOR_GESTURES = "edge_service_for_gestures";
        public static final Validator USE_EDGE_SERVICE_FOR_GESTURES_VALIDATOR = CMSettings.sBooleanValidator;
        public static final Map<String, Validator> VALIDATORS = new ArrayMap();
        public static final String VOLBTN_MUSIC_CONTROLS = "volbtn_music_controls";
        public static final Validator VOLBTN_MUSIC_CONTROLS_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String VOLUME_ADJUST_SOUNDS_ENABLED = "volume_adjust_sounds_enabled";
        public static final Validator VOLUME_ADJUST_SOUNDS_ENABLED_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String VOLUME_KEYS_CONTROL_RING_STREAM = "volume_keys_control_ring_stream";
        public static final Validator VOLUME_KEYS_CONTROL_RING_STREAM_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String VOLUME_WAKE_SCREEN = "volume_wake_screen";
        public static final Validator VOLUME_WAKE_SCREEN_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String ZEN_ALLOW_LIGHTS = "allow_lights";
        public static final Validator ZEN_ALLOW_LIGHTS_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String ZEN_PRIORITY_ALLOW_LIGHTS = "zen_priority_allow_lights";
        public static final Validator ZEN_PRIORITY_ALLOW_LIGHTS_VALIDATOR = CMSettings.sBooleanValidator;
        public static final String __MAGICAL_TEST_PASSING_ENABLER = "___magical_test_passing_enabler";
        public static final Validator __MAGICAL_TEST_PASSING_ENABLER_VALIDATOR = CMSettings.sAlwaysTrueValidator;
        private static final NameValueCache sNameValueCache = new NameValueCache(SYS_PROP_CM_SETTING_VERSION, CONTENT_URI, CMSettings.CALL_METHOD_GET_SYSTEM, CMSettings.CALL_METHOD_PUT_SYSTEM);

        static {
            MOVED_TO_SECURE.add("dev_force_show_navbar");
            VALIDATORS.put(NOTIFICATION_PLAY_QUEUE, NOTIFICATION_PLAY_QUEUE_VALIDATOR);
            VALIDATORS.put(HIGH_TOUCH_SENSITIVITY_ENABLE, HIGH_TOUCH_SENSITIVITY_ENABLE_VALIDATOR);
            VALIDATORS.put(SYSTEM_PROFILES_ENABLED, SYSTEM_PROFILES_ENABLED_VALIDATOR);
            VALIDATORS.put(STATUS_BAR_CLOCK, STATUS_BAR_CLOCK_VALIDATOR);
            VALIDATORS.put(STATUS_BAR_AM_PM, STATUS_BAR_AM_PM_VALIDATOR);
            VALIDATORS.put(STATUS_BAR_BATTERY_STYLE, STATUS_BAR_BATTERY_STYLE_VALIDATOR);
            VALIDATORS.put(STATUS_BAR_SHOW_BATTERY_PERCENT, STATUS_BAR_SHOW_BATTERY_PERCENT_VALIDATOR);
            VALIDATORS.put(INCREASING_RING, INCREASING_RING_VALIDATOR);
            VALIDATORS.put(INCREASING_RING_START_VOLUME, INCREASING_RING_START_VOLUME_VALIDATOR);
            VALIDATORS.put(INCREASING_RING_RAMP_UP_TIME, INCREASING_RING_RAMP_UP_TIME_VALIDATOR);
            VALIDATORS.put(VOLUME_ADJUST_SOUNDS_ENABLED, VOLUME_ADJUST_SOUNDS_ENABLED_VALIDATOR);
            VALIDATORS.put(NAV_BUTTONS, NAV_BUTTONS_VALIDATOR);
            VALIDATORS.put(VOLUME_KEYS_CONTROL_RING_STREAM, VOLUME_KEYS_CONTROL_RING_STREAM_VALIDATOR);
            VALIDATORS.put(NAVIGATION_BAR_MENU_ARROW_KEYS, NAVIGATION_BAR_MENU_ARROW_KEYS_VALIDATOR);
            VALIDATORS.put(KEY_HOME_LONG_PRESS_ACTION, KEY_HOME_LONG_PRESS_ACTION_VALIDATOR);
            VALIDATORS.put(KEY_HOME_DOUBLE_TAP_ACTION, KEY_HOME_DOUBLE_TAP_ACTION_VALIDATOR);
            VALIDATORS.put(BACK_WAKE_SCREEN, BACK_WAKE_SCREEN_VALIDATOR);
            VALIDATORS.put(MENU_WAKE_SCREEN, MENU_WAKE_SCREENN_VALIDATOR);
            VALIDATORS.put(VOLUME_WAKE_SCREEN, VOLUME_WAKE_SCREEN_VALIDATOR);
            VALIDATORS.put(KEY_MENU_ACTION, KEY_MENU_ACTION_VALIDATOR);
            VALIDATORS.put(KEY_MENU_LONG_PRESS_ACTION, KEY_MENU_LONG_PRESS_ACTION_VALIDATOR);
            VALIDATORS.put(KEY_ASSIST_ACTION, KEY_ASSIST_ACTION_VALIDATOR);
            VALIDATORS.put(KEY_ASSIST_LONG_PRESS_ACTION, KEY_ASSIST_LONG_PRESS_ACTION_VALIDATOR);
            VALIDATORS.put(KEY_APP_SWITCH_ACTION, KEY_APP_SWITCH_ACTION_VALIDATOR);
            VALIDATORS.put(KEY_APP_SWITCH_LONG_PRESS_ACTION, KEY_APP_SWITCH_LONG_PRESS_ACTION_VALIDATOR);
            VALIDATORS.put(HOME_WAKE_SCREEN, HOME_WAKE_SCREEN_VALIDATOR);
            VALIDATORS.put(ASSIST_WAKE_SCREEN, ASSIST_WAKE_SCREEN_VALIDATOR);
            VALIDATORS.put(APP_SWITCH_WAKE_SCREEN, APP_SWITCH_WAKE_SCREEN_VALIDATOR);
            VALIDATORS.put(CAMERA_WAKE_SCREEN, CAMERA_WAKE_SCREEN_VALIDATOR);
            VALIDATORS.put(CAMERA_SLEEP_ON_RELEASE, CAMERA_SLEEP_ON_RELEASE_VALIDATOR);
            VALIDATORS.put(CAMERA_LAUNCH, CAMERA_LAUNCH_VALIDATOR);
            VALIDATORS.put(SWAP_VOLUME_KEYS_ON_ROTATION, SWAP_VOLUME_KEYS_ON_ROTATION_VALIDATOR);
            VALIDATORS.put(BATTERY_LIGHT_ENABLED, BATTERY_LIGHT_ENABLED_VALIDATOR);
            VALIDATORS.put(BATTERY_LIGHT_PULSE, BATTERY_LIGHT_PULSE_VALIDATOR);
            VALIDATORS.put(BATTERY_LIGHT_LOW_COLOR, BATTERY_LIGHT_LOW_COLOR_VALIDATOR);
            VALIDATORS.put(BATTERY_LIGHT_MEDIUM_COLOR, BATTERY_LIGHT_MEDIUM_COLOR_VALIDATOR);
            VALIDATORS.put(BATTERY_LIGHT_FULL_COLOR, BATTERY_LIGHT_FULL_COLOR_VALIDATOR);
            VALIDATORS.put(ENABLE_MWI_NOTIFICATION, ENABLE_MWI_NOTIFICATION_VALIDATOR);
            VALIDATORS.put(PROXIMITY_ON_WAKE, PROXIMITY_ON_WAKE_VALIDATOR);
            VALIDATORS.put(ENABLE_FORWARD_LOOKUP, ENABLE_FORWARD_LOOKUP_VALIDATOR);
            VALIDATORS.put(ENABLE_PEOPLE_LOOKUP, ENABLE_PEOPLE_LOOKUP_VALIDATOR);
            VALIDATORS.put(ENABLE_REVERSE_LOOKUP, ENABLE_REVERSE_LOOKUP_VALIDATOR);
            VALIDATORS.put(FORWARD_LOOKUP_PROVIDER, FORWARD_LOOKUP_PROVIDER_VALIDATOR);
            VALIDATORS.put(PEOPLE_LOOKUP_PROVIDER, PEOPLE_LOOKUP_PROVIDER_VALIDATOR);
            VALIDATORS.put(REVERSE_LOOKUP_PROVIDER, REVERSE_LOOKUP_PROVIDER_VALIDATOR);
            VALIDATORS.put(DIALER_OPENCNAM_ACCOUNT_SID, DIALER_OPENCNAM_ACCOUNT_SID_VALIDATOR);
            VALIDATORS.put(DIALER_OPENCNAM_AUTH_TOKEN, DIALER_OPENCNAM_AUTH_TOKEN_VALIDATOR);
            VALIDATORS.put(DISPLAY_TEMPERATURE_DAY, DISPLAY_TEMPERATURE_DAY_VALIDATOR);
            VALIDATORS.put(DISPLAY_TEMPERATURE_NIGHT, DISPLAY_TEMPERATURE_NIGHT_VALIDATOR);
            VALIDATORS.put(DISPLAY_TEMPERATURE_MODE, DISPLAY_TEMPERATURE_MODE_VALIDATOR);
            VALIDATORS.put(DISPLAY_AUTO_CONTRAST, DISPLAY_AUTO_CONTRAST_VALIDATOR);
            VALIDATORS.put(DISPLAY_AUTO_OUTDOOR_MODE, DISPLAY_AUTO_OUTDOOR_MODE_VALIDATOR);
            VALIDATORS.put("display_low_power", DISPLAY_CABC_VALIDATOR);
            VALIDATORS.put(DISPLAY_COLOR_ENHANCE, DISPLAY_COLOR_ENHANCE_VALIDATOR);
            VALIDATORS.put(DISPLAY_COLOR_ADJUSTMENT, DISPLAY_COLOR_ADJUSTMENT_VALIDATOR);
            VALIDATORS.put(LIVE_DISPLAY_HINTED, LIVE_DISPLAY_HINTED_VALIDATOR);
            VALIDATORS.put(DOUBLE_TAP_SLEEP_GESTURE, DOUBLE_TAP_SLEEP_GESTURE_VALIDATOR);
            VALIDATORS.put(STATUS_BAR_SHOW_WEATHER, STATUS_BAR_SHOW_WEATHER_VALIDATOR);
            VALIDATORS.put(RECENTS_SHOW_SEARCH_BAR, RECENTS_SHOW_SEARCH_BAR_VALIDATOR);
            VALIDATORS.put(NAVBAR_LEFT_IN_LANDSCAPE, NAVBAR_LEFT_IN_LANDSCAPE_VALIDATOR);
            VALIDATORS.put(T9_SEARCH_INPUT_LOCALE, T9_SEARCH_INPUT_LOCALE_VALIDATOR);
            VALIDATORS.put(BLUETOOTH_ACCEPT_ALL_FILES, BLUETOOTH_ACCEPT_ALL_FILES_VALIDATOR);
            VALIDATORS.put(LOCKSCREEN_PIN_SCRAMBLE_LAYOUT, LOCKSCREEN_PIN_SCRAMBLE_LAYOUT_VALIDATOR);
            VALIDATORS.put(LOCKSCREEN_ROTATION, LOCKSCREEN_ROTATION_VALIDATOR);
            VALIDATORS.put(SHOW_ALARM_ICON, SHOW_ALARM_ICON_VALIDATOR);
            VALIDATORS.put(STATUS_BAR_IME_SWITCHER, STATUS_BAR_IME_SWITCHER_VALIDATOR);
            VALIDATORS.put(STATUS_BAR_QUICK_QS_PULLDOWN, STATUS_BAR_QUICK_QS_PULLDOWN_VALIDATOR);
            VALIDATORS.put("qs_show_brightness_slider", QS_SHOW_BRIGHTNESS_SLIDER_VALIDATOR);
            VALIDATORS.put(STATUS_BAR_BRIGHTNESS_CONTROL, STATUS_BAR_BRIGHTNESS_CONTROL_VALIDATOR);
            VALIDATORS.put(VOLBTN_MUSIC_CONTROLS, VOLBTN_MUSIC_CONTROLS_VALIDATOR);
            VALIDATORS.put(USE_EDGE_SERVICE_FOR_GESTURES, USE_EDGE_SERVICE_FOR_GESTURES_VALIDATOR);
            VALIDATORS.put(STATUS_BAR_NOTIF_COUNT, STATUS_BAR_NOTIF_COUNT_VALIDATOR);
            VALIDATORS.put(CALL_RECORDING_FORMAT, CALL_RECORDING_FORMAT_VALIDATOR);
            VALIDATORS.put(NOTIFICATION_LIGHT_BRIGHTNESS_LEVEL, NOTIFICATION_LIGHT_BRIGHTNESS_LEVEL_VALIDATOR);
            VALIDATORS.put(NOTIFICATION_LIGHT_MULTIPLE_LEDS_ENABLE, NOTIFICATION_LIGHT_MULTIPLE_LEDS_ENABLE_VALIDATOR);
            VALIDATORS.put(NOTIFICATION_LIGHT_SCREEN_ON, NOTIFICATION_LIGHT_SCREEN_ON_VALIDATOR);
            VALIDATORS.put(NOTIFICATION_LIGHT_PULSE_DEFAULT_COLOR, NOTIFICATION_LIGHT_PULSE_DEFAULT_COLOR_VALIDATOR);
            VALIDATORS.put(NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_ON, NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_ON_VALIDATOR);
            VALIDATORS.put(NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_OFF, NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_OFF_VALIDATOR);
            VALIDATORS.put(NOTIFICATION_LIGHT_PULSE_CALL_COLOR, NOTIFICATION_LIGHT_PULSE_CALL_COLOR_VALIDATOR);
            VALIDATORS.put(NOTIFICATION_LIGHT_PULSE_CALL_LED_ON, NOTIFICATION_LIGHT_PULSE_CALL_LED_ON_VALIDATOR);
            VALIDATORS.put(NOTIFICATION_LIGHT_PULSE_CALL_LED_OFF, NOTIFICATION_LIGHT_PULSE_CALL_LED_OFF_VALIDATOR);
            VALIDATORS.put(NOTIFICATION_LIGHT_PULSE_VMAIL_COLOR, NOTIFICATION_LIGHT_PULSE_VMAIL_COLOR_VALIDATOR);
            VALIDATORS.put(NOTIFICATION_LIGHT_PULSE_VMAIL_LED_ON, NOTIFICATION_LIGHT_PULSE_VMAIL_LED_ON_VALIDATOR);
            VALIDATORS.put(NOTIFICATION_LIGHT_PULSE_VMAIL_LED_OFF, NOTIFICATION_LIGHT_PULSE_VMAIL_LED_OFF_VALIDATOR);
            VALIDATORS.put(NOTIFICATION_LIGHT_PULSE_CUSTOM_ENABLE, NOTIFICATION_LIGHT_PULSE_CUSTOM_ENABLE_VALIDATOR);
            VALIDATORS.put(NOTIFICATION_LIGHT_PULSE_CUSTOM_VALUES, NOTIFICATION_LIGHT_PULSE_CUSTOM_VALUES_VALIDATOR);
            VALIDATORS.put(NOTIFICATION_LIGHT_COLOR_AUTO, NOTIFICATION_LIGHT_COLOR_AUTO_VALIDATOR);
            VALIDATORS.put(HEADSET_CONNECT_PLAYER, HEADSET_CONNECT_PLAYER_VALIDATOR);
            VALIDATORS.put(ZEN_ALLOW_LIGHTS, ZEN_ALLOW_LIGHTS_VALIDATOR);
            VALIDATORS.put(ZEN_PRIORITY_ALLOW_LIGHTS, ZEN_PRIORITY_ALLOW_LIGHTS_VALIDATOR);
            VALIDATORS.put(TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK, TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK_VALIDATOR);
            VALIDATORS.put(DISPLAY_PICTURE_ADJUSTMENT, DISPLAY_PICTURE_ADJUSTMENT_VALIDATOR);
            VALIDATORS.put("___magical_test_passing_enabler", __MAGICAL_TEST_PASSING_ENABLER_VALIDATOR);
        }

        public static void putListAsDelimitedString(ContentResolver resolver, String name, String delimiter, List<String> list) {
            putString(resolver, name, TextUtils.join(delimiter, list));
        }

        public static List<String> getDelimitedStringAsList(ContentResolver resolver, String name, String delimiter) {
            String baseString = getString(resolver, name);
            List<String> list = new ArrayList<>();
            if (!TextUtils.isEmpty(baseString)) {
                for (String item : TextUtils.split(baseString, Pattern.quote(delimiter))) {
                    if (!TextUtils.isEmpty(item)) {
                        list.add(item);
                    }
                }
            }
            return list;
        }

        public static Uri getUriFor(String name) {
            return Settings.NameValueTable.getUriFor(CONTENT_URI, name);
        }

        public static String getString(ContentResolver resolver, String name) {
            return getStringForUser(resolver, name, UserHandle.myUserId());
        }

        public static String getStringForUser(ContentResolver resolver, String name, int userId) {
            if (!MOVED_TO_SECURE.contains(name)) {
                return sNameValueCache.getStringForUser(resolver, name, userId);
            }
            Log.w(CMSettings.TAG, "Setting " + name + " has moved from CMSettings.System" + " to CMSettings.Secure, value is unchanged.");
            return Secure.getStringForUser(resolver, name, userId);
        }

        public static boolean putString(ContentResolver resolver, String name, String value) {
            return putStringForUser(resolver, name, value, UserHandle.myUserId());
        }

        public static boolean putStringForUser(ContentResolver resolver, String name, String value, int userId) {
            if (!MOVED_TO_SECURE.contains(name)) {
                return sNameValueCache.putStringForUser(resolver, name, value, userId);
            }
            Log.w(CMSettings.TAG, "Setting " + name + " has moved from CMSettings.System" + " to CMSettings.Secure, value is unchanged.");
            return false;
        }

        public static int getInt(ContentResolver cr, String name, int def) {
            return getIntForUser(cr, name, def, UserHandle.myUserId());
        }

        public static int getIntForUser(ContentResolver cr, String name, int def, int userId) {
            String v = getStringForUser(cr, name, userId);
            if (v == null) {
                return def;
            }
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
                return def;
            }
        }

        public static int getInt(ContentResolver cr, String name) throws CMSettingNotFoundException {
            return getIntForUser(cr, name, UserHandle.myUserId());
        }

        public static int getIntForUser(ContentResolver cr, String name, int userId) throws CMSettingNotFoundException {
            try {
                return Integer.parseInt(getStringForUser(cr, name, userId));
            } catch (NumberFormatException e) {
                throw new CMSettingNotFoundException(name);
            }
        }

        public static boolean putInt(ContentResolver cr, String name, int value) {
            return putIntForUser(cr, name, value, UserHandle.myUserId());
        }

        public static boolean putIntForUser(ContentResolver cr, String name, int value, int userId) {
            return putStringForUser(cr, name, Integer.toString(value), userId);
        }

        public static long getLong(ContentResolver cr, String name, long def) {
            return getLongForUser(cr, name, def, UserHandle.myUserId());
        }

        public static long getLongForUser(ContentResolver cr, String name, long def, int userId) {
            long value;
            String valString = getStringForUser(cr, name, userId);
            if (valString != null) {
                try {
                    value = Long.parseLong(valString);
                } catch (NumberFormatException e) {
                    return def;
                }
            } else {
                value = def;
            }
            return value;
        }

        public static long getLong(ContentResolver cr, String name) throws CMSettingNotFoundException {
            return getLongForUser(cr, name, UserHandle.myUserId());
        }

        public static long getLongForUser(ContentResolver cr, String name, int userId) throws CMSettingNotFoundException {
            try {
                return Long.parseLong(getStringForUser(cr, name, userId));
            } catch (NumberFormatException e) {
                throw new CMSettingNotFoundException(name);
            }
        }

        public static boolean putLong(ContentResolver cr, String name, long value) {
            return putLongForUser(cr, name, value, UserHandle.myUserId());
        }

        public static boolean putLongForUser(ContentResolver cr, String name, long value, int userId) {
            return putStringForUser(cr, name, Long.toString(value), userId);
        }

        public static float getFloat(ContentResolver cr, String name, float def) {
            return getFloatForUser(cr, name, def, UserHandle.myUserId());
        }

        public static float getFloatForUser(ContentResolver cr, String name, float def, int userId) {
            String v = getStringForUser(cr, name, userId);
            if (v == null) {
                return def;
            }
            try {
                return Float.parseFloat(v);
            } catch (NumberFormatException e) {
                return def;
            }
        }

        public static float getFloat(ContentResolver cr, String name) throws CMSettingNotFoundException {
            return getFloatForUser(cr, name, UserHandle.myUserId());
        }

        public static float getFloatForUser(ContentResolver cr, String name, int userId) throws CMSettingNotFoundException {
            String v = getStringForUser(cr, name, userId);
            if (v != null) {
                try {
                    return Float.parseFloat(v);
                } catch (NumberFormatException e) {
                    throw new CMSettingNotFoundException(name);
                }
            } else {
                throw new CMSettingNotFoundException(name);
            }
        }

        public static boolean putFloat(ContentResolver cr, String name, float value) {
            return putFloatForUser(cr, name, value, UserHandle.myUserId());
        }

        public static boolean putFloatForUser(ContentResolver cr, String name, float value, int userId) {
            return putStringForUser(cr, name, Float.toString(value), userId);
        }

        public static boolean isLegacySetting(String key) {
            return ArrayUtils.contains(LEGACY_SYSTEM_SETTINGS, key);
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x0028 A[ADDED_TO_REGION] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static boolean shouldInterceptSystemProvider(java.lang.String r4) {
            /*
                int r0 = r4.hashCode()
                r1 = 410781692(0x187c07fc, float:3.2574268E-24)
                r2 = 0
                r3 = 1
                if (r0 == r1) goto L_0x001b
                r1 = 1726524052(0x66e8aa94, float:5.493678E23)
                if (r0 == r1) goto L_0x0011
            L_0x0010:
                goto L_0x0025
            L_0x0011:
                java.lang.String r0 = "dev_force_show_navbar"
                boolean r0 = r4.equals(r0)
                if (r0 == 0) goto L_0x0010
                r0 = 1
                goto L_0x0026
            L_0x001b:
                java.lang.String r0 = "system_profiles_enabled"
                boolean r0 = r4.equals(r0)
                if (r0 == 0) goto L_0x0010
                r0 = 0
                goto L_0x0026
            L_0x0025:
                r0 = -1
            L_0x0026:
                if (r0 == 0) goto L_0x002b
                if (r0 == r3) goto L_0x002b
                return r2
            L_0x002b:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: cyanogenmod.providers.CMSettings.System.shouldInterceptSystemProvider(java.lang.String):boolean");
        }
    }

    public static final class Secure extends Settings.NameValueTable {
        public static final String ADB_NOTIFY = "adb_notify";
        public static final String ADB_PORT = "adb_port";
        public static final String ADVANCED_MODE = "advanced_mode";
        public static final String ADVANCED_REBOOT = "advanced_reboot";
        public static final String APP_PERFORMANCE_PROFILES_ENABLED = "app_perf_profiles_enabled";
        public static final String BUTTON_BACKLIGHT_TIMEOUT = "button_backlight_timeout";
        public static final String BUTTON_BRIGHTNESS = "button_brightness";
        public static final String CM_SETUP_WIZARD_COMPLETED = "cm_setup_wizard_completed";
        public static final Uri CONTENT_URI = Uri.parse("content://cmsettings/secure");
        public static final String DEFAULT_LIVE_LOCK_SCREEN_COMPONENT = "default_live_lock_screen_component";
        public static final String DEFAULT_THEME_COMPONENTS = "default_theme_components";
        public static final String DEFAULT_THEME_PACKAGE = "default_theme_package";
        public static final String DEVELOPMENT_SHORTCUT = "development_shortcut";
        public static final String DEVICE_HOSTNAME = "device_hostname";
        public static final String DEV_FORCE_SHOW_NAVBAR = "dev_force_show_navbar";
        public static final String DISPLAY_GAMMA_CALIBRATION_PREFIX = "display_gamma_";
        public static final String ENABLED_EVENT_LIVE_LOCKS_KEY = "live_lockscreens_events_enabled";
        public static final String FEATURE_TOUCH_HOVERING = "feature_touch_hovering";
        public static final String KEYBOARD_BRIGHTNESS = "keyboard_brightness";
        public static final String KILL_APP_LONGPRESS_BACK = "kill_app_longpress_back";
        public static final String[] LEGACY_SECURE_SETTINGS;
        public static final String LIVE_DISPLAY_COLOR_MATRIX = "live_display_color_matrix";
        public static final String LIVE_LOCK_SCREEN_ENABLED = "live_lock_screen_enabled";
        public static final String LOCKSCREEN_INTERNALLY_ENABLED = "lockscreen_internally_enabled";
        public static final String LOCKSCREEN_TARGETS = "lockscreen_target_actions";
        public static final String LOCKSCREEN_VISUALIZER_ENABLED = "lockscreen_visualizer";
        public static final String LOCK_PASS_TO_SECURITY_VIEW = "lock_screen_pass_to_security_view";
        public static final String LOCK_SCREEN_BLUR_ENABLED = "lock_screen_blur_enabled";
        public static final String LOCK_SCREEN_WEATHER_ENABLED = "lock_screen_weather_enabled";
        protected static final ArraySet<String> MOVED_TO_GLOBAL = new ArraySet<>(1);
        public static final String[] NAVIGATION_RING_TARGETS = {"navigation_ring_targets_0", "navigation_ring_targets_1", "navigation_ring_targets_2"};
        public static final String PERFORMANCE_PROFILE = "performance_profile";
        public static final String POWER_MENU_ACTIONS = "power_menu_actions";
        public static final String PRIVACY_GUARD_DEFAULT = "privacy_guard_default";
        public static final String PRIVACY_GUARD_NOTIFICATION = "privacy_guard_notification";
        public static final String PROTECTED_COMPONENTS = "protected_components";
        public static final Validator PROTECTED_COMPONENTS_MANAGER_VALIDATOR = new Validator() {
            private final String mDelimiter = "|";

            public boolean validate(String value) {
                if (TextUtils.isEmpty(value)) {
                    return true;
                }
                for (String item : TextUtils.split(value, Pattern.quote("|"))) {
                    if (TextUtils.isEmpty(item)) {
                        return false;
                    }
                }
                return true;
            }
        };
        public static final Validator PROTECTED_COMPONENTS_VALIDATOR = new Validator() {
            private final String mDelimiter = "|";

            public boolean validate(String value) {
                if (TextUtils.isEmpty(value)) {
                    return true;
                }
                for (String item : TextUtils.split(value, Pattern.quote("|"))) {
                    if (TextUtils.isEmpty(item)) {
                        return false;
                    }
                }
                return true;
            }
        };
        public static final String PROTECTED_COMPONENT_MANAGERS = "protected_component_managers";
        public static final String QS_LOCATION_ADVANCED = "qs_location_advanced";
        public static final String QS_SHOW_BRIGHTNESS_SLIDER = "qs_show_brightness_slider";
        public static final String QS_TILES = "sysui_qs_tiles";
        public static final String QS_USE_MAIN_TILES = "sysui_qs_main_tiles";
        public static final String RECENTS_LONG_PRESS_ACTIVITY = "recents_long_press_activity";
        public static final String RING_HOME_BUTTON_BEHAVIOR = "ring_home_button_behavior";
        public static final int RING_HOME_BUTTON_BEHAVIOR_ANSWER = 2;
        public static final int RING_HOME_BUTTON_BEHAVIOR_DEFAULT = 1;
        public static final int RING_HOME_BUTTON_BEHAVIOR_DO_NOTHING = 1;
        public static final String STATS_COLLECTION = "stats_collection";
        public static final String STATS_COLLECTION_REPORTED = "stats_collection_reported";
        public static final String SYS_PROP_CM_SETTING_VERSION = "sys.cm_settings_secure_version";
        public static final String THEME_PREV_BOOT_API_LEVEL = "theme_prev_boot_api_level";
        public static final Map<String, Validator> VALIDATORS = new ArrayMap();
        public static final String VIBRATOR_INTENSITY = "vibrator_intensity";
        public static final String WEATHER_PROVIDER_SERVICE = "weather_provider_service";
        public static final String __MAGICAL_TEST_PASSING_ENABLER = "___magical_test_passing_enabler";
        private static final NameValueCache sNameValueCache = new NameValueCache(SYS_PROP_CM_SETTING_VERSION, CONTENT_URI, CMSettings.CALL_METHOD_GET_SECURE, CMSettings.CALL_METHOD_PUT_SECURE);

        static {
            MOVED_TO_GLOBAL.add("dev_force_show_navbar");
            String[] strArr = NAVIGATION_RING_TARGETS;
            LEGACY_SECURE_SETTINGS = new String[]{ADVANCED_MODE, BUTTON_BACKLIGHT_TIMEOUT, BUTTON_BRIGHTNESS, DEFAULT_THEME_COMPONENTS, DEFAULT_THEME_PACKAGE, "dev_force_show_navbar", KEYBOARD_BRIGHTNESS, POWER_MENU_ACTIONS, STATS_COLLECTION, "qs_show_brightness_slider", QS_TILES, QS_USE_MAIN_TILES, strArr[0], strArr[1], strArr[2], RECENTS_LONG_PRESS_ACTIVITY, ADB_NOTIFY, ADB_PORT, DEVICE_HOSTNAME, KILL_APP_LONGPRESS_BACK, PROTECTED_COMPONENTS, LIVE_DISPLAY_COLOR_MATRIX, ADVANCED_REBOOT, THEME_PREV_BOOT_API_LEVEL, LOCKSCREEN_TARGETS, RING_HOME_BUTTON_BEHAVIOR, PRIVACY_GUARD_DEFAULT, PRIVACY_GUARD_NOTIFICATION, DEVELOPMENT_SHORTCUT, PERFORMANCE_PROFILE, APP_PERFORMANCE_PROFILES_ENABLED, QS_LOCATION_ADVANCED, LOCKSCREEN_VISUALIZER_ENABLED, LOCK_PASS_TO_SECURITY_VIEW};
            VALIDATORS.put(PROTECTED_COMPONENTS, PROTECTED_COMPONENTS_VALIDATOR);
            VALIDATORS.put(PROTECTED_COMPONENT_MANAGERS, PROTECTED_COMPONENTS_MANAGER_VALIDATOR);
        }

        public static void putListAsDelimitedString(ContentResolver resolver, String name, String delimiter, List<String> list) {
            putString(resolver, name, TextUtils.join(delimiter, list));
        }

        public static List<String> getDelimitedStringAsList(ContentResolver resolver, String name, String delimiter) {
            String baseString = getString(resolver, name);
            List<String> list = new ArrayList<>();
            if (!TextUtils.isEmpty(baseString)) {
                for (String item : TextUtils.split(baseString, Pattern.quote(delimiter))) {
                    if (!TextUtils.isEmpty(item)) {
                        list.add(item);
                    }
                }
            }
            return list;
        }

        public static Uri getUriFor(String name) {
            return Settings.NameValueTable.getUriFor(CONTENT_URI, name);
        }

        public static String getString(ContentResolver resolver, String name) {
            return getStringForUser(resolver, name, UserHandle.myUserId());
        }

        public static String getStringForUser(ContentResolver resolver, String name, int userId) {
            if (!MOVED_TO_GLOBAL.contains(name)) {
                return sNameValueCache.getStringForUser(resolver, name, userId);
            }
            Log.w(CMSettings.TAG, "Setting " + name + " has moved from CMSettings.Secure" + " to CMSettings.Global, value is unchanged.");
            return Global.getStringForUser(resolver, name, userId);
        }

        public static boolean putString(ContentResolver resolver, String name, String value) {
            return putStringForUser(resolver, name, value, UserHandle.myUserId());
        }

        public static boolean putStringForUser(ContentResolver resolver, String name, String value, int userId) {
            if (!MOVED_TO_GLOBAL.contains(name)) {
                return sNameValueCache.putStringForUser(resolver, name, value, userId);
            }
            Log.w(CMSettings.TAG, "Setting " + name + " has moved from CMSettings.Secure" + " to CMSettings.Global, value is unchanged.");
            return false;
        }

        public static int getInt(ContentResolver cr, String name, int def) {
            return getIntForUser(cr, name, def, UserHandle.myUserId());
        }

        public static int getIntForUser(ContentResolver cr, String name, int def, int userId) {
            String v = getStringForUser(cr, name, userId);
            if (v == null) {
                return def;
            }
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
                return def;
            }
        }

        public static int getInt(ContentResolver cr, String name) throws CMSettingNotFoundException {
            return getIntForUser(cr, name, UserHandle.myUserId());
        }

        public static int getIntForUser(ContentResolver cr, String name, int userId) throws CMSettingNotFoundException {
            try {
                return Integer.parseInt(getStringForUser(cr, name, userId));
            } catch (NumberFormatException e) {
                throw new CMSettingNotFoundException(name);
            }
        }

        public static boolean putInt(ContentResolver cr, String name, int value) {
            return putIntForUser(cr, name, value, UserHandle.myUserId());
        }

        public static boolean putIntForUser(ContentResolver cr, String name, int value, int userId) {
            return putStringForUser(cr, name, Integer.toString(value), userId);
        }

        public static long getLong(ContentResolver cr, String name, long def) {
            return getLongForUser(cr, name, def, UserHandle.myUserId());
        }

        public static long getLongForUser(ContentResolver cr, String name, long def, int userId) {
            long value;
            String valString = getStringForUser(cr, name, userId);
            if (valString != null) {
                try {
                    value = Long.parseLong(valString);
                } catch (NumberFormatException e) {
                    return def;
                }
            } else {
                value = def;
            }
            return value;
        }

        public static long getLong(ContentResolver cr, String name) throws CMSettingNotFoundException {
            return getLongForUser(cr, name, UserHandle.myUserId());
        }

        public static long getLongForUser(ContentResolver cr, String name, int userId) throws CMSettingNotFoundException {
            try {
                return Long.parseLong(getStringForUser(cr, name, userId));
            } catch (NumberFormatException e) {
                throw new CMSettingNotFoundException(name);
            }
        }

        public static boolean putLong(ContentResolver cr, String name, long value) {
            return putLongForUser(cr, name, value, UserHandle.myUserId());
        }

        public static boolean putLongForUser(ContentResolver cr, String name, long value, int userId) {
            return putStringForUser(cr, name, Long.toString(value), userId);
        }

        public static float getFloat(ContentResolver cr, String name, float def) {
            return getFloatForUser(cr, name, def, UserHandle.myUserId());
        }

        public static float getFloatForUser(ContentResolver cr, String name, float def, int userId) {
            String v = getStringForUser(cr, name, userId);
            if (v == null) {
                return def;
            }
            try {
                return Float.parseFloat(v);
            } catch (NumberFormatException e) {
                return def;
            }
        }

        public static float getFloat(ContentResolver cr, String name) throws CMSettingNotFoundException {
            return getFloatForUser(cr, name, UserHandle.myUserId());
        }

        public static float getFloatForUser(ContentResolver cr, String name, int userId) throws CMSettingNotFoundException {
            String v = getStringForUser(cr, name, userId);
            if (v != null) {
                try {
                    return Float.parseFloat(v);
                } catch (NumberFormatException e) {
                    throw new CMSettingNotFoundException(name);
                }
            } else {
                throw new CMSettingNotFoundException(name);
            }
        }

        public static boolean putFloat(ContentResolver cr, String name, float value) {
            return putFloatForUser(cr, name, value, UserHandle.myUserId());
        }

        public static boolean putFloatForUser(ContentResolver cr, String name, float value, int userId) {
            return putStringForUser(cr, name, Float.toString(value), userId);
        }

        public static boolean isLegacySetting(String key) {
            return ArrayUtils.contains(LEGACY_SECURE_SETTINGS, key);
        }

        public static boolean shouldInterceptSystemProvider(String key) {
            if (((key.hashCode() == 1726524052 && key.equals("dev_force_show_navbar")) ? (char) 0 : 65535) != 0) {
                return false;
            }
            return true;
        }
    }

    public static final class Global extends Settings.NameValueTable {
        public static final Uri CONTENT_URI = Uri.parse("content://cmsettings/global");
        public static final String DEV_FORCE_SHOW_NAVBAR = "dev_force_show_navbar";
        public static final String[] LEGACY_GLOBAL_SETTINGS = {WAKE_WHEN_PLUGGED_OR_UNPLUGGED, POWER_NOTIFICATIONS_VIBRATE, POWER_NOTIFICATIONS_RINGTONE, ZEN_DISABLE_DUCKING_DURING_MEDIA_PLAYBACK, WIFI_AUTO_PRIORITIES_CONFIGURATION};
        @Deprecated
        public static final String POWER_NOTIFICATIONS_ENABLED = "power_notifications_enabled";
        public static final String POWER_NOTIFICATIONS_RINGTONE = "power_notifications_ringtone";
        public static final String POWER_NOTIFICATIONS_VIBRATE = "power_notifications_vibrate";
        public static final String SYS_PROP_CM_SETTING_VERSION = "sys.cm_settings_global_version";
        public static final String WAKE_WHEN_PLUGGED_OR_UNPLUGGED = "wake_when_plugged_or_unplugged";
        public static final String WEATHER_TEMPERATURE_UNIT = "weather_temperature_unit";
        public static final String WIFI_AUTO_PRIORITIES_CONFIGURATION = "wifi_auto_priority";
        public static final String ZEN_DISABLE_DUCKING_DURING_MEDIA_PLAYBACK = "zen_disable_ducking_during_media_playback";
        public static final String __MAGICAL_TEST_PASSING_ENABLER = "___magical_test_passing_enabler";
        private static final NameValueCache sNameValueCache = new NameValueCache(SYS_PROP_CM_SETTING_VERSION, CONTENT_URI, CMSettings.CALL_METHOD_GET_GLOBAL, CMSettings.CALL_METHOD_PUT_GLOBAL);

        public static void putListAsDelimitedString(ContentResolver resolver, String name, String delimiter, List<String> list) {
            putString(resolver, name, TextUtils.join(delimiter, list));
        }

        public static List<String> getDelimitedStringAsList(ContentResolver resolver, String name, String delimiter) {
            String baseString = getString(resolver, name);
            List<String> list = new ArrayList<>();
            if (!TextUtils.isEmpty(baseString)) {
                for (String item : TextUtils.split(baseString, Pattern.quote(delimiter))) {
                    if (!TextUtils.isEmpty(item)) {
                        list.add(item);
                    }
                }
            }
            return list;
        }

        public static Uri getUriFor(String name) {
            return Settings.NameValueTable.getUriFor(CONTENT_URI, name);
        }

        public static String getString(ContentResolver resolver, String name) {
            return getStringForUser(resolver, name, UserHandle.myUserId());
        }

        public static String getStringForUser(ContentResolver resolver, String name, int userId) {
            return sNameValueCache.getStringForUser(resolver, name, userId);
        }

        public static boolean putString(ContentResolver resolver, String name, String value) {
            return putStringForUser(resolver, name, value, UserHandle.myUserId());
        }

        public static boolean putStringForUser(ContentResolver resolver, String name, String value, int userId) {
            return sNameValueCache.putStringForUser(resolver, name, value, userId);
        }

        public static int getInt(ContentResolver cr, String name, int def) {
            return getIntForUser(cr, name, def, UserHandle.myUserId());
        }

        public static int getIntForUser(ContentResolver cr, String name, int def, int userId) {
            String v = getStringForUser(cr, name, userId);
            if (v == null) {
                return def;
            }
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
                return def;
            }
        }

        public static int getInt(ContentResolver cr, String name) throws CMSettingNotFoundException {
            return getIntForUser(cr, name, UserHandle.myUserId());
        }

        public static int getIntForUser(ContentResolver cr, String name, int userId) throws CMSettingNotFoundException {
            try {
                return Integer.parseInt(getStringForUser(cr, name, userId));
            } catch (NumberFormatException e) {
                throw new CMSettingNotFoundException(name);
            }
        }

        public static boolean putInt(ContentResolver cr, String name, int value) {
            return putIntForUser(cr, name, value, UserHandle.myUserId());
        }

        public static boolean putIntForUser(ContentResolver cr, String name, int value, int userId) {
            return putStringForUser(cr, name, Integer.toString(value), userId);
        }

        public static long getLong(ContentResolver cr, String name, long def) {
            return getLongForUser(cr, name, def, UserHandle.myUserId());
        }

        public static long getLongForUser(ContentResolver cr, String name, long def, int userId) {
            long value;
            String valString = getStringForUser(cr, name, userId);
            if (valString != null) {
                try {
                    value = Long.parseLong(valString);
                } catch (NumberFormatException e) {
                    return def;
                }
            } else {
                value = def;
            }
            return value;
        }

        public static long getLong(ContentResolver cr, String name) throws CMSettingNotFoundException {
            return getLongForUser(cr, name, UserHandle.myUserId());
        }

        public static long getLongForUser(ContentResolver cr, String name, int userId) throws CMSettingNotFoundException {
            try {
                return Long.parseLong(getStringForUser(cr, name, userId));
            } catch (NumberFormatException e) {
                throw new CMSettingNotFoundException(name);
            }
        }

        public static boolean putLong(ContentResolver cr, String name, long value) {
            return putLongForUser(cr, name, value, UserHandle.myUserId());
        }

        public static boolean putLongForUser(ContentResolver cr, String name, long value, int userId) {
            return putStringForUser(cr, name, Long.toString(value), userId);
        }

        public static float getFloat(ContentResolver cr, String name, float def) {
            return getFloatForUser(cr, name, def, UserHandle.myUserId());
        }

        public static float getFloatForUser(ContentResolver cr, String name, float def, int userId) {
            String v = getStringForUser(cr, name, userId);
            if (v == null) {
                return def;
            }
            try {
                return Float.parseFloat(v);
            } catch (NumberFormatException e) {
                return def;
            }
        }

        public static float getFloat(ContentResolver cr, String name) throws CMSettingNotFoundException {
            return getFloatForUser(cr, name, UserHandle.myUserId());
        }

        public static float getFloatForUser(ContentResolver cr, String name, int userId) throws CMSettingNotFoundException {
            String v = getStringForUser(cr, name, userId);
            if (v != null) {
                try {
                    return Float.parseFloat(v);
                } catch (NumberFormatException e) {
                    throw new CMSettingNotFoundException(name);
                }
            } else {
                throw new CMSettingNotFoundException(name);
            }
        }

        public static boolean putFloat(ContentResolver cr, String name, float value) {
            return putFloatForUser(cr, name, value, UserHandle.myUserId());
        }

        public static boolean putFloatForUser(ContentResolver cr, String name, float value, int userId) {
            return putStringForUser(cr, name, Float.toString(value), userId);
        }

        public static boolean isLegacySetting(String key) {
            return ArrayUtils.contains(LEGACY_GLOBAL_SETTINGS, key);
        }

        public static boolean shouldInterceptSystemProvider(String key) {
            return false;
        }
    }
}
