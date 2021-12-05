package nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip;

import android.content.Context;
import android.net.Uri;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmazfitBipLiteCoordinator extends AmazfitBipCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AmazfitBipLiteCoordinator.class);

    public DeviceType getDeviceType() {
        return DeviceType.AMAZFITBIP_LITE;
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        try {
            String name = candidate.getDevice().getName();
            if (name != null && name.equalsIgnoreCase("Amazfit Bip Lite")) {
                return DeviceType.AMAZFITBIP_LITE;
            }
        } catch (Exception ex) {
            LOG.error("unable to check device support", (Throwable) ex);
        }
        return DeviceType.UNKNOWN;
    }

    public InstallHandler findInstallHandler(Uri uri, Context context) {
        AmazfitBipLiteFWInstallHandler handler = new AmazfitBipLiteFWInstallHandler(uri, context);
        if (handler.isValid()) {
            return handler;
        }
        return null;
    }

    public int getBondingStyle() {
        return 3;
    }
}
