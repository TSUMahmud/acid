package nodomain.freeyourgadget.gadgetbridge.devices.roidmi;

import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Roidmi3Coordinator extends RoidmiCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) Roidmi3Coordinator.class);

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        try {
            String name = candidate.getDevice().getName();
            if (name != null && name.contains("Roidmi Music Blue C")) {
                return DeviceType.ROIDMI3;
            }
        } catch (Exception ex) {
            LOG.error("unable to check device support", (Throwable) ex);
        }
        return DeviceType.UNKNOWN;
    }

    public DeviceType getDeviceType() {
        return DeviceType.ROIDMI3;
    }

    public boolean supportsRgbLedColor() {
        return true;
    }
}
