package nodomain.freeyourgadget.gadgetbridge.devices.hplus;

import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;

public class Q8Coordinator extends HPlusCoordinator {
    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        if (name == null || !name.startsWith("Q8")) {
            return DeviceType.UNKNOWN;
        }
        return DeviceType.Q8;
    }

    public DeviceType getDeviceType() {
        return DeviceType.Q8;
    }

    public String getManufacturer() {
        return "Makibes";
    }
}
