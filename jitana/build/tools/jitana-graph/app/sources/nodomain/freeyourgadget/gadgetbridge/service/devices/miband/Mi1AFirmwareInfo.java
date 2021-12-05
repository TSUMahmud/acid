package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mi1AFirmwareInfo extends AbstractMi1FirmwareInfo {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) Mi1AFirmwareInfo.class);

    public static Mi1AFirmwareInfo getInstance(byte[] wholeFirmwareBytes) {
        Mi1AFirmwareInfo info = new Mi1AFirmwareInfo(wholeFirmwareBytes);
        if (info.isGenerallySupportedFirmware()) {
            return info;
        }
        LOG.info("firmware not supported");
        return null;
    }

    protected Mi1AFirmwareInfo(byte[] wholeFirmwareBytes) {
        super(wholeFirmwareBytes);
    }

    /* access modifiers changed from: protected */
    public int getSupportedMajorVersion() {
        return 5;
    }

    public boolean isGenerallyCompatibleWith(GBDevice device) {
        return MiBandConst.MI_1A.equals(device.getModel());
    }
}
