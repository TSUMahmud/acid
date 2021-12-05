package nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip;

import android.content.Context;
import android.net.Uri;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiCoordinator;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmazfitBipCoordinator extends HuamiCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AmazfitBipCoordinator.class);

    public DeviceType getDeviceType() {
        return DeviceType.AMAZFITBIP;
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        try {
            String name = candidate.getDevice().getName();
            if (name != null && name.equalsIgnoreCase("Amazfit Bip Watch")) {
                return DeviceType.AMAZFITBIP;
            }
        } catch (Exception ex) {
            LOG.error("unable to check device support", (Throwable) ex);
        }
        return DeviceType.UNKNOWN;
    }

    public InstallHandler findInstallHandler(Uri uri, Context context) {
        AmazfitBipFWInstallHandler handler = new AmazfitBipFWInstallHandler(uri, context);
        if (handler.isValid()) {
            return handler;
        }
        return null;
    }

    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return true;
    }

    public boolean supportsActivityTracks() {
        return true;
    }

    public boolean supportsWeather() {
        return true;
    }

    public int[] getSupportedDeviceSpecificSettings(GBDevice device) {
        return new int[]{C0889R.C0894xml.devicesettings_amazfitbip, C0889R.C0894xml.devicesettings_timeformat, C0889R.C0894xml.devicesettings_wearlocation, C0889R.C0894xml.devicesettings_custom_emoji_font, C0889R.C0894xml.devicesettings_liftwrist_display, C0889R.C0894xml.devicesettings_disconnectnotification, C0889R.C0894xml.devicesettings_expose_hr_thirdparty, C0889R.C0894xml.devicesettings_buttonactions_with_longpress, C0889R.C0894xml.devicesettings_pairingkey};
    }
}
