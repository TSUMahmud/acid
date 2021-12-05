package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mi1FirmwareInfo extends AbstractMi1FirmwareInfo {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) Mi1FirmwareInfo.class);

    public static Mi1FirmwareInfo getInstance(byte[] wholeFirmwareBytes) {
        Mi1FirmwareInfo info = new Mi1FirmwareInfo(wholeFirmwareBytes);
        if (info.isGenerallySupportedFirmware()) {
            return info;
        }
        LOG.info("firmware not supported");
        return null;
    }

    protected Mi1FirmwareInfo(byte[] wholeFirmwareBytes) {
        super(wholeFirmwareBytes);
    }

    /* access modifiers changed from: protected */
    public int getSupportedMajorVersion() {
        return 1;
    }

    public boolean isGenerallyCompatibleWith(GBDevice device) {
        return MiBandConst.MI_1.equals(device.getModel());
    }
}
