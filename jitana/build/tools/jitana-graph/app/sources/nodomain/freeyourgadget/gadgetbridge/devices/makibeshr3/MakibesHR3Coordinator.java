package nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.MakibesHR3ActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.util.GBPrefs;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p008de.greenrobot.dao.query.WhereCondition;

public class MakibesHR3Coordinator extends AbstractDeviceCoordinator {
    public static final int FindPhone_OFF = 0;
    public static final int FindPhone_ON = -1;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) MakibesHR3Coordinator.class);

    public static boolean shouldEnableHeadsUpScreen(SharedPreferences sharedPrefs) {
        return !sharedPrefs.getString("activate_display_on_lift_wrist", GBApplication.getContext().getString(C0889R.string.p_on)).equals(GBApplication.getContext().getString(C0889R.string.p_off));
    }

    public static boolean shouldEnableLostReminder(SharedPreferences sharedPrefs) {
        return !sharedPrefs.getString("disconnect_notification", GBApplication.getContext().getString(C0889R.string.p_on)).equals(GBApplication.getContext().getString(C0889R.string.p_off));
    }

    public static byte getTimeMode(SharedPreferences sharedPrefs) {
        if (new GBPrefs(new Prefs(sharedPrefs)).getTimeFormat().equals(GBApplication.getContext().getString(C0889R.string.p_timeformat_24h))) {
            return 0;
        }
        return 1;
    }

    public static boolean getQuiteHours(SharedPreferences sharedPrefs, Calendar startOut, Calendar endOut) {
        if (sharedPrefs.getString(MakibesHR3Constants.PREF_DO_NOT_DISTURB, GBApplication.getContext().getString(C0889R.string.p_off)).equals(GBApplication.getContext().getString(C0889R.string.p_off))) {
            return false;
        }
        String start = sharedPrefs.getString(MakibesHR3Constants.PREF_DO_NOT_DISTURB_START, "00:00");
        String end = sharedPrefs.getString(MakibesHR3Constants.PREF_DO_NOT_DISTURB_END, "00:00");
        DateFormat df = new SimpleDateFormat("HH:mm");
        try {
            startOut.setTime(df.parse(start));
            endOut.setTime(df.parse(end));
            return true;
        } catch (Exception e) {
            Logger logger = LOG;
            logger.error("Unexpected exception in MiBand2Coordinator.getTime: " + e.getMessage());
            return false;
        }
    }

    public static int getFindPhone(SharedPreferences sharedPrefs) {
        String findPhone = sharedPrefs.getString(MakibesHR3Constants.PREF_FIND_PHONE, GBApplication.getContext().getString(C0889R.string.p_off));
        if (findPhone.equals(GBApplication.getContext().getString(C0889R.string.p_off))) {
            return 0;
        }
        if (findPhone.equals(GBApplication.getContext().getString(C0889R.string.p_on))) {
            return -1;
        }
        try {
            return Integer.valueOf(sharedPrefs.getString(MakibesHR3Constants.PREF_FIND_PHONE_DURATION, "0")).intValue();
        } catch (Exception ex) {
            try {
                LOG.warn(ex.getMessage());
                return 60;
            } catch (Exception e) {
                Logger logger = LOG;
                logger.error("Unexpected exception in MiBand2Coordinator.getTime: " + e.getMessage());
                return -1;
            }
        }
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        List<String> deviceNames = new ArrayList<String>() {
            {
                add("Y808");
                add("MAKIBES HR3");
            }
        };
        if (name == null || !deviceNames.contains(name)) {
            return DeviceType.UNKNOWN;
        }
        return DeviceType.MAKIBESHR3;
    }

    /* access modifiers changed from: protected */
    public void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) throws GBException {
        session.getMakibesHR3ActivitySampleDao().queryBuilder().where(MakibesHR3ActivitySampleDao.Properties.DeviceId.mo14989eq(device.getId()), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public int getBondingStyle() {
        return 1;
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
        return DeviceType.MAKIBESHR3;
    }

    public Class<? extends Activity> getPairingActivity() {
        return null;
    }

    public boolean supportsActivityDataFetching() {
        return false;
    }

    public boolean supportsActivityTracking() {
        return true;
    }

    public SampleProvider<? extends ActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        return new MakibesHR3SampleProvider(device, session);
    }

    public InstallHandler findInstallHandler(Uri uri, Context context) {
        return null;
    }

    public boolean supportsScreenshots() {
        return false;
    }

    public int getAlarmSlotCount() {
        return 8;
    }

    public boolean supportsSmartWakeup(GBDevice device) {
        return false;
    }

    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return true;
    }

    public String getManufacturer() {
        return "Makibes";
    }

    public boolean supportsAppsManagement() {
        return false;
    }

    public Class<? extends Activity> getAppsManagementActivity() {
        return null;
    }

    public int[] getSupportedDeviceSpecificSettings(GBDevice device) {
        return new int[]{C0889R.C0894xml.devicesettings_timeformat, C0889R.C0894xml.devicesettings_liftwrist_display, C0889R.C0894xml.devicesettings_disconnectnotification, C0889R.C0894xml.devicesettings_donotdisturb_no_auto, C0889R.C0894xml.devicesettings_find_phone};
    }
}
