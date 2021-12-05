package nodomain.freeyourgadget.gadgetbridge.devices.hplus;

import android.app.Activity;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.ParcelUuid;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.activities.SettingsActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.devicesettings.DeviceSettingsPreferenceConst;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiConst;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.util.GBPrefs;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p008de.greenrobot.dao.query.WhereCondition;

public class HPlusCoordinator extends AbstractDeviceCoordinator {
    protected static final Logger LOG = LoggerFactory.getLogger((Class<?>) HPlusCoordinator.class);
    protected static Prefs prefs = GBApplication.getPrefs();

    public Collection<? extends ScanFilter> createBLEScanFilters() {
        return Collections.singletonList(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(HPlusConstants.UUID_SERVICE_HP)).build());
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        if (name == null || !name.startsWith("HPLUS")) {
            return DeviceType.UNKNOWN;
        }
        return DeviceType.HPLUS;
    }

    public int getBondingStyle() {
        return 0;
    }

    public boolean supportsCalendarEvents() {
        return false;
    }

    public boolean supportsRealtimeData() {
        return true;
    }

    public boolean supportsWeather() {
        return false;
    }

    public boolean supportsFindDevice() {
        return true;
    }

    public DeviceType getDeviceType() {
        return DeviceType.HPLUS;
    }

    public Class<? extends Activity> getPairingActivity() {
        return null;
    }

    public InstallHandler findInstallHandler(Uri uri, Context context) {
        return null;
    }

    public boolean supportsActivityDataFetching() {
        return true;
    }

    public boolean supportsActivityTracking() {
        return true;
    }

    public SampleProvider<? extends ActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        return new HPlusHealthSampleProvider(device, session);
    }

    public boolean supportsScreenshots() {
        return false;
    }

    public int getAlarmSlotCount() {
        return 3;
    }

    public boolean supportsSmartWakeup(GBDevice device) {
        return false;
    }

    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return true;
    }

    public String getManufacturer() {
        return "Zeblaze";
    }

    public boolean supportsAppsManagement() {
        return false;
    }

    public Class<? extends Activity> getAppsManagementActivity() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) throws GBException {
        session.getHPlusHealthActivitySampleDao().queryBuilder().where(HPlusHealthActivitySampleDao.Properties.DeviceId.mo14989eq(device.getId()), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public static byte getLanguage(String address) {
        Locale locale;
        String language = prefs.getString(HuamiConst.PREF_LANGUAGE, "default");
        if (language.equals("default")) {
            locale = Locale.getDefault();
        } else {
            locale = new Locale(language);
        }
        if (locale.getLanguage().equals(new Locale("cn").getLanguage())) {
            return 1;
        }
        return 2;
    }

    public static byte getTimeMode(String deviceAddress) {
        if ("24h".equals(new GBPrefs(new Prefs(GBApplication.getDeviceSpecificSharedPrefs(deviceAddress))).getTimeFormat())) {
            return 1;
        }
        return 0;
    }

    public static byte getUnit(String address) {
        if (prefs.getString(SettingsActivity.PREF_MEASUREMENT_SYSTEM, GBApplication.getContext().getString(C0889R.string.p_unit_metric)).equals(GBApplication.getContext().getString(C0889R.string.p_unit_metric))) {
            return 0;
        }
        return 1;
    }

    public static byte getUserWeight() {
        return (byte) (new ActivityUser().getWeightKg() & 255);
    }

    public static byte getUserHeight() {
        return (byte) (new ActivityUser().getHeightCm() & 255);
    }

    public static byte getUserAge() {
        return (byte) (new ActivityUser().getAge() & 255);
    }

    public static byte getUserGender() {
        if (new ActivityUser().getGender() == 1) {
            return 0;
        }
        return 1;
    }

    public static int getGoal() {
        return new ActivityUser().getStepsGoal();
    }

    public static byte getScreenTime(String address) {
        return (byte) (prefs.getInt(HPlusConstants.PREF_HPLUS_SCREENTIME, 5) & 255);
    }

    public static byte getAllDayHR(String address) {
        if (prefs.getBoolean(HPlusConstants.PREF_HPLUS_ALLDAYHR, true)) {
            return 10;
        }
        return -1;
    }

    public static byte getSocial(String address) {
        return -1;
    }

    public static byte getUserWrist(String deviceAddress) {
        if ("left".equals(GBApplication.getDeviceSpecificSharedPrefs(deviceAddress).getString(DeviceSettingsPreferenceConst.PREF_WEARLOCATION, "left"))) {
            return 0;
        }
        return 1;
    }

    public static int getSITStartTime(String address) {
        return prefs.getInt(HPlusConstants.PREF_HPLUS_SIT_START_TIME, 0);
    }

    public static int getSITEndTime(String address) {
        return prefs.getInt(HPlusConstants.PREF_HPLUS_SIT_END_TIME, 0);
    }

    public static void setUnicodeSupport(String address, boolean state) {
        SharedPreferences.Editor editor = prefs.getPreferences().edit();
        editor.putBoolean("hplus_unicode_" + address, state);
        editor.apply();
    }

    public static boolean getUnicodeSupport(String address) {
        Prefs prefs2 = prefs;
        return prefs2.getBoolean("hplus_unicode_" + address, false);
    }

    public int[] getSupportedDeviceSpecificSettings(GBDevice device) {
        return new int[]{C0889R.C0894xml.devicesettings_timeformat};
    }
}
