package nodomain.freeyourgadget.gadgetbridge.devices.huami;

import android.app.Activity;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.ParcelUuid;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.activities.SettingsActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.DateTimeDisplay;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.DoNotDisturb;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBand2SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandPairingActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandService;
import nodomain.freeyourgadget.gadgetbridge.entities.AbstractActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p008de.greenrobot.dao.query.WhereCondition;

public abstract class HuamiCoordinator extends AbstractDeviceCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) HuamiCoordinator.class);

    public Class<? extends Activity> getPairingActivity() {
        return MiBandPairingActivity.class;
    }

    public Collection<? extends ScanFilter> createBLEScanFilters() {
        return Collections.singletonList(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(MiBandService.UUID_SERVICE_MIBAND2_SERVICE)).build());
    }

    /* access modifiers changed from: protected */
    public void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) throws GBException {
        session.getMiBandActivitySampleDao().queryBuilder().where(MiBandActivitySampleDao.Properties.DeviceId.mo14989eq(device.getId()), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public String getManufacturer() {
        return "Huami";
    }

    public boolean supportsAppsManagement() {
        return false;
    }

    public Class<? extends Activity> getAppsManagementActivity() {
        return null;
    }

    public boolean supportsCalendarEvents() {
        return false;
    }

    public boolean supportsRealtimeData() {
        return true;
    }

    public int getAlarmSlotCount() {
        return 10;
    }

    public boolean supportsActivityDataFetching() {
        return true;
    }

    public boolean supportsActivityTracking() {
        return true;
    }

    public int[] getSupportedDeviceSpecificSettings(GBDevice device) {
        return new int[]{C0889R.C0894xml.devicesettings_pairingkey};
    }

    public SampleProvider<? extends AbstractActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        return new MiBand2SampleProvider(device, session);
    }

    public static DateTimeDisplay getDateDisplay(Context context, String deviceAddress) throws IllegalArgumentException {
        SharedPreferences sharedPrefs = GBApplication.getDeviceSpecificSharedPrefs(deviceAddress);
        String dateFormatTime = context.getString(C0889R.string.p_dateformat_time);
        if (dateFormatTime.equals(sharedPrefs.getString(MiBandConst.PREF_MI2_DATEFORMAT, dateFormatTime))) {
            return DateTimeDisplay.TIME;
        }
        return DateTimeDisplay.DATE_TIME;
    }

    public static ActivateDisplayOnLift getActivateDisplayOnLiftWrist(Context context, String deviceAddress) {
        SharedPreferences prefs = GBApplication.getDeviceSpecificSharedPrefs(deviceAddress);
        String liftOff = context.getString(C0889R.string.p_off);
        String liftOn = context.getString(C0889R.string.p_on);
        String liftScheduled = context.getString(C0889R.string.p_scheduled);
        String pref = prefs.getString("activate_display_on_lift_wrist", liftOff);
        if (liftOn.equals(pref)) {
            return ActivateDisplayOnLift.ON;
        }
        if (liftScheduled.equals(pref)) {
            return ActivateDisplayOnLift.SCHEDULED;
        }
        return ActivateDisplayOnLift.OFF;
    }

    public static Date getDisplayOnLiftStart(String deviceAddress) {
        return getTimePreference(HuamiConst.PREF_DISPLAY_ON_LIFT_START, "00:00", deviceAddress);
    }

    public static Date getDisplayOnLiftEnd(String deviceAddress) {
        return getTimePreference(HuamiConst.PREF_DISPLAY_ON_LIFT_END, "00:00", deviceAddress);
    }

    public static DisconnectNotificationSetting getDisconnectNotificationSetting(Context context, String deviceAddress) {
        Prefs prefs = new Prefs(GBApplication.getDeviceSpecificSharedPrefs(deviceAddress));
        String liftOff = context.getString(C0889R.string.p_off);
        String liftOn = context.getString(C0889R.string.p_on);
        String liftScheduled = context.getString(C0889R.string.p_scheduled);
        String pref = prefs.getString("disconnect_notification", liftOff);
        if (liftOn.equals(pref)) {
            return DisconnectNotificationSetting.ON;
        }
        if (liftScheduled.equals(pref)) {
            return DisconnectNotificationSetting.SCHEDULED;
        }
        return DisconnectNotificationSetting.OFF;
    }

    public static Date getDisconnectNotificationStart(String deviceAddress) {
        return getTimePreference(HuamiConst.PREF_DISCONNECT_NOTIFICATION_START, "00:00", deviceAddress);
    }

    public static Date getDisconnectNotificationEnd(String deviceAddress) {
        return getTimePreference(HuamiConst.PREF_DISCONNECT_NOTIFICATION_END, "00:00", deviceAddress);
    }

    public static Set<String> getDisplayItems(String deviceAddress) {
        return GBApplication.getDeviceSpecificSharedPrefs(deviceAddress).getStringSet(HuamiConst.PREF_DISPLAY_ITEMS, (Set) null);
    }

    public static boolean getUseCustomFont(String deviceAddress) {
        return GBApplication.getDeviceSpecificSharedPrefs(deviceAddress).getBoolean(HuamiConst.PREF_USE_CUSTOM_FONT, false);
    }

    public static boolean getGoalNotification() {
        return GBApplication.getPrefs().getBoolean(MiBandConst.PREF_MI2_GOAL_NOTIFICATION, false);
    }

    public static boolean getRotateWristToSwitchInfo(String deviceAddress) {
        return GBApplication.getDeviceSpecificSharedPrefs(deviceAddress).getBoolean(MiBandConst.PREF_MI2_ROTATE_WRIST_TO_SWITCH_INFO, false);
    }

    public static boolean getInactivityWarnings() {
        return GBApplication.getPrefs().getBoolean(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS, false);
    }

    public static int getInactivityWarningsThreshold() {
        return GBApplication.getPrefs().getInt(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_THRESHOLD, 60);
    }

    public static boolean getInactivityWarningsDnd() {
        return GBApplication.getPrefs().getBoolean(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_DND, false);
    }

    public static Date getInactivityWarningsStart() {
        return getTimePreference(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_START, "06:00");
    }

    public static Date getInactivityWarningsEnd() {
        return getTimePreference(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_END, "22:00");
    }

    public static Date getInactivityWarningsDndStart() {
        return getTimePreference(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_DND_START, "12:00");
    }

    public static Date getInactivityWarningsDndEnd() {
        return getTimePreference(MiBandConst.PREF_MI2_INACTIVITY_WARNINGS_DND_END, "14:00");
    }

    public static Date getDoNotDisturbStart(String deviceAddress) {
        return getTimePreference(MiBandConst.PREF_DO_NOT_DISTURB_START, "01:00", deviceAddress);
    }

    public static Date getDoNotDisturbEnd(String deviceAddress) {
        return getTimePreference(MiBandConst.PREF_DO_NOT_DISTURB_END, "06:00", deviceAddress);
    }

    public static boolean getBandScreenUnlock(String deviceAddress) {
        return new Prefs(GBApplication.getDeviceSpecificSharedPrefs(deviceAddress)).getBoolean(MiBandConst.PREF_SWIPE_UNLOCK, false);
    }

    public static boolean getExposeHRThirdParty(String deviceAddress) {
        return new Prefs(GBApplication.getDeviceSpecificSharedPrefs(deviceAddress)).getBoolean(HuamiConst.PREF_EXPOSE_HR_THIRDPARTY, false);
    }

    protected static Date getTimePreference(String key, String defaultValue, String deviceAddress) {
        Prefs prefs;
        if (deviceAddress == null) {
            prefs = GBApplication.getPrefs();
        } else {
            prefs = new Prefs(GBApplication.getDeviceSpecificSharedPrefs(deviceAddress));
        }
        try {
            return new SimpleDateFormat("HH:mm").parse(prefs.getString(key, defaultValue));
        } catch (Exception e) {
            Logger logger = LOG;
            logger.error("Unexpected exception in MiBand2Coordinator.getTime: " + e.getMessage());
            return new Date();
        }
    }

    protected static Date getTimePreference(String key, String defaultValue) {
        return getTimePreference(key, defaultValue, (String) null);
    }

    public static MiBandConst.DistanceUnit getDistanceUnit() {
        if (GBApplication.getPrefs().getString(SettingsActivity.PREF_MEASUREMENT_SYSTEM, GBApplication.getContext().getString(C0889R.string.p_unit_metric)).equals(GBApplication.getContext().getString(C0889R.string.p_unit_metric))) {
            return MiBandConst.DistanceUnit.METRIC;
        }
        return MiBandConst.DistanceUnit.IMPERIAL;
    }

    public static DoNotDisturb getDoNotDisturb(String deviceAddress) {
        String pref = GBApplication.getDeviceSpecificSharedPrefs(deviceAddress).getString(MiBandConst.PREF_DO_NOT_DISTURB, "off");
        if (MiBandConst.PREF_DO_NOT_DISTURB_AUTOMATIC.equals(pref)) {
            return DoNotDisturb.AUTOMATIC;
        }
        if ("scheduled".equals(pref)) {
            return DoNotDisturb.SCHEDULED;
        }
        return DoNotDisturb.OFF;
    }

    public boolean supportsScreenshots() {
        return false;
    }

    public boolean supportsSmartWakeup(GBDevice device) {
        return false;
    }

    public boolean supportsFindDevice() {
        return true;
    }

    public boolean supportsAlarmSnoozing() {
        return true;
    }
}
