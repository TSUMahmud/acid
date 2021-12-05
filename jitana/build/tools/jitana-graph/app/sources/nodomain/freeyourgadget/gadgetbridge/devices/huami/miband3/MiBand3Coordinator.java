package nodomain.freeyourgadget.gadgetbridge.devices.huami.miband3;

import android.content.Context;
import android.net.Uri;
import java.util.Date;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiConst;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiBand3Coordinator extends HuamiCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) MiBand3Coordinator.class);

    public DeviceType getDeviceType() {
        return DeviceType.MIBAND3;
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        try {
            String name = candidate.getDevice().getName();
            if (name != null && (name.equalsIgnoreCase(HuamiConst.MI_BAND3_NAME) || name.equalsIgnoreCase(HuamiConst.MI_BAND3_NAME_2))) {
                return DeviceType.MIBAND3;
            }
        } catch (Exception ex) {
            LOG.error("unable to check device support", (Throwable) ex);
        }
        return DeviceType.UNKNOWN;
    }

    public InstallHandler findInstallHandler(Uri uri, Context context) {
        MiBand3FWInstallHandler handler = new MiBand3FWInstallHandler(uri, context);
        if (handler.isValid()) {
            return handler;
        }
        return null;
    }

    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return true;
    }

    public boolean supportsWeather() {
        return true;
    }

    public boolean supportsActivityTracks() {
        return true;
    }

    public static String getNightMode(String deviceAddress) {
        return new Prefs(GBApplication.getDeviceSpecificSharedPrefs(deviceAddress)).getString(MiBandConst.PREF_NIGHT_MODE, "off");
    }

    public static Date getNightModeStart(String deviceAddress) {
        return getTimePreference(MiBandConst.PREF_NIGHT_MODE_START, "16:00", deviceAddress);
    }

    public static Date getNightModeEnd(String deviceAddress) {
        return getTimePreference(MiBandConst.PREF_NIGHT_MODE_END, "07:00", deviceAddress);
    }

    public int[] getSupportedDeviceSpecificSettings(GBDevice device) {
        return new int[]{C0889R.C0894xml.devicesettings_miband3, C0889R.C0894xml.devicesettings_wearlocation, C0889R.C0894xml.devicesettings_timeformat, C0889R.C0894xml.devicesettings_dateformat, C0889R.C0894xml.devicesettings_nightmode, C0889R.C0894xml.devicesettings_donotdisturb_withauto, C0889R.C0894xml.devicesettings_liftwrist_display, C0889R.C0894xml.devicesettings_swipeunlock, C0889R.C0894xml.devicesettings_expose_hr_thirdparty, C0889R.C0894xml.devicesettings_pairingkey};
    }
}
