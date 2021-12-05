package nodomain.freeyourgadget.gadgetbridge.devices.hplus;

import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;

public class MakibesF68Coordinator extends HPlusCoordinator {
    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        if (name == null || !name.startsWith("SPORT") || name.startsWith("SPORTAGE")) {
            return DeviceType.UNKNOWN;
        }
        return DeviceType.MAKIBESF68;
    }

    public DeviceType getDeviceType() {
        return DeviceType.MAKIBESF68;
    }

    public String getManufacturer() {
        return "Makibes";
    }
}
