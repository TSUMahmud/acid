package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelUuid;
import java.util.Collection;
import java.util.Collections;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.activities.devicesettings.DeviceSettingsPreferenceConst;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.AbstractActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p008de.greenrobot.dao.query.WhereCondition;

public class MiBandCoordinator extends AbstractDeviceCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) MiBandCoordinator.class);

    public Collection<? extends ScanFilter> createBLEScanFilters() {
        return Collections.singletonList(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(MiBandService.UUID_SERVICE_MIBAND_SERVICE)).build());
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name;
        String macAddress = candidate.getMacAddress().toUpperCase();
        if (macAddress.startsWith(MiBandService.MAC_ADDRESS_FILTER_1_1A) || macAddress.startsWith(MiBandService.MAC_ADDRESS_FILTER_1S)) {
            return DeviceType.MIBAND;
        }
        if (candidate.supportsService(MiBandService.UUID_SERVICE_MIBAND_SERVICE) && !candidate.supportsService(MiBandService.UUID_SERVICE_MIBAND2_SERVICE)) {
            return DeviceType.MIBAND;
        }
        try {
            BluetoothDevice device = candidate.getDevice();
            if (isHealthWearable(device) && (name = device.getName()) != null && name.toUpperCase().startsWith(MiBandConst.MI_GENERAL_NAME_PREFIX.toUpperCase())) {
                return DeviceType.MIBAND;
            }
        } catch (Exception ex) {
            LOG.error("unable to check device support", (Throwable) ex);
        }
        return DeviceType.UNKNOWN;
    }

    /* access modifiers changed from: protected */
    public void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) throws GBException {
        session.getMiBandActivitySampleDao().queryBuilder().where(MiBandActivitySampleDao.Properties.DeviceId.mo14989eq(device.getId()), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public DeviceType getDeviceType() {
        return DeviceType.MIBAND;
    }

    public Class<? extends Activity> getPairingActivity() {
        return MiBandPairingActivity.class;
    }

    public SampleProvider<? extends AbstractActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        return new MiBandSampleProvider(device, session);
    }

    public InstallHandler findInstallHandler(Uri uri, Context context) {
        MiBandFWInstallHandler handler = new MiBandFWInstallHandler(uri, context);
        if (handler.isValid()) {
            return handler;
        }
        return null;
    }

    public boolean supportsActivityDataFetching() {
        return true;
    }

    public boolean supportsScreenshots() {
        return false;
    }

    public int getAlarmSlotCount() {
        return 3;
    }

    public boolean supportsSmartWakeup(GBDevice device) {
        return true;
    }

    public boolean supportsActivityTracking() {
        return true;
    }

    public String getManufacturer() {
        return "Xiaomi";
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

    public boolean supportsWeather() {
        return false;
    }

    public boolean supportsFindDevice() {
        return true;
    }

    public static boolean hasValidUserInfo() {
        try {
            UserInfo configuredUserInfo = getConfiguredUserInfo("88:0F:10:00:00:00");
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static UserInfo getAnyUserInfo(String miBandAddress) {
        try {
            return getConfiguredUserInfo(miBandAddress);
        } catch (Exception ex) {
            Logger logger = LOG;
            logger.error("Error creating user info from settings, using default user instead: " + ex);
            return UserInfo.getDefault(miBandAddress);
        }
    }

    public static UserInfo getConfiguredUserInfo(String miBandAddress) throws IllegalArgumentException {
        ActivityUser activityUser = new ActivityUser();
        return UserInfo.create(miBandAddress, GBApplication.getPrefs().getString("mi_user_alias", (String) null), activityUser.getGender(), activityUser.getAge(), activityUser.getHeightCm(), activityUser.getWeightKg(), 0);
    }

    public static int getWearLocation(String deviceAddress) throws IllegalArgumentException {
        if ("right".equals(new Prefs(GBApplication.getDeviceSpecificSharedPrefs(deviceAddress)).getString(DeviceSettingsPreferenceConst.PREF_WEARLOCATION, "left"))) {
            return 1;
        }
        return 0;
    }

    public static int getDeviceTimeOffsetHours(String deviceAddress) throws IllegalArgumentException {
        return new Prefs(GBApplication.getDeviceSpecificSharedPrefs(deviceAddress)).getInt(MiBandConst.PREF_MIBAND_DEVICE_TIME_OFFSET_HOURS, 0);
    }

    public static boolean getHeartrateSleepSupport(String miBandAddress) throws IllegalArgumentException {
        return GBApplication.getPrefs().getBoolean(MiBandConst.PREF_MIBAND_USE_HR_FOR_SLEEP_DETECTION, false);
    }

    public static int getReservedAlarmSlots(String miBandAddress) throws IllegalArgumentException {
        return new Prefs(GBApplication.getDeviceSpecificSharedPrefs(miBandAddress)).getInt(DeviceSettingsPreferenceConst.PREF_RESERVER_ALARMS_CALENDAR, 0);
    }

    public boolean supportsHeartRateMeasurement(GBDevice device) {
        String hwVersion = device.getModel();
        return isMi1S(hwVersion) || isMiPro(hwVersion);
    }

    public int[] getSupportedDeviceSpecificSettings(GBDevice device) {
        return new int[]{C0889R.C0894xml.devicesettings_wearlocation, C0889R.C0894xml.devicesettings_lowlatency_fwupdate, C0889R.C0894xml.devicesettings_reserve_alarms_calendar, C0889R.C0894xml.devicesettings_fake_timeoffset};
    }

    private boolean isMi1S(String hardwareVersion) {
        return MiBandConst.MI_1S.equals(hardwareVersion);
    }

    private boolean isMiPro(String hardwareVersion) {
        return MiBandConst.MI_PRO.equals(hardwareVersion);
    }
}
