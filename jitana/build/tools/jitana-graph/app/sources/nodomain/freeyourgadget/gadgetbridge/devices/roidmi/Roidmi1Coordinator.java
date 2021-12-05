package nodomain.freeyourgadget.gadgetbridge.devices.roidmi;

import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Roidmi1Coordinator extends RoidmiCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) Roidmi1Coordinator.class);

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        try {
            String name = candidate.getDevice().getName();
            if (name != null && name.contains("睿米车载蓝牙播放器")) {
                return DeviceType.ROIDMI;
            }
        } catch (Exception ex) {
            LOG.error("unable to check device support", (Throwable) ex);
        }
        return DeviceType.UNKNOWN;
    }

    public DeviceType getDeviceType() {
        return DeviceType.ROIDMI;
    }
}
