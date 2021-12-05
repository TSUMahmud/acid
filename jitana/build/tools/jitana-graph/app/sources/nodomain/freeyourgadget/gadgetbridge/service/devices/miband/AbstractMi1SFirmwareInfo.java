package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

public abstract class AbstractMi1SFirmwareInfo extends AbstractMiFirmwareInfo {
    public AbstractMi1SFirmwareInfo(byte[] wholeFirmwareBytes) {
        super(wholeFirmwareBytes);
    }

    public boolean isGenerallyCompatibleWith(GBDevice device) {
        return MiBandConst.MI_1S.equals(device.getModel());
    }

    public boolean isSingleMiBandFirmware() {
        return false;
    }
}
