package nodomain.freeyourgadget.gadgetbridge.devices.hplus;

import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;

public class EXRIZUK8Coordinator extends HPlusCoordinator {
    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        if (name == null || !name.startsWith("iRun ")) {
            return DeviceType.UNKNOWN;
        }
        return DeviceType.EXRIZUK8;
    }

    public DeviceType getDeviceType() {
        return DeviceType.EXRIZUK8;
    }

    public String getManufacturer() {
        return "EXRIZU";
    }
}
