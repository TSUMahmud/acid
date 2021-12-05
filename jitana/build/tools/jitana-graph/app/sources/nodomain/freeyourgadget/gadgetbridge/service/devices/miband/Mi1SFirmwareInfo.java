package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.util.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mi1SFirmwareInfo extends CompositeMiFirmwareInfo {
    private static final byte[] DOUBLE_FW_HEADER = {MakibesHR3Constants.CMD_78, MakibesHR3Constants.CMD_SET_SEDENTARY_REMINDER, 99, 107};
    private static final int DOUBLE_FW_HEADER_OFFSET = 0;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) Mi1SFirmwareInfo.class);

    private Mi1SFirmwareInfo(byte[] wholeFirmwareBytes) {
        super(wholeFirmwareBytes, new Mi1SFirmwareInfoFW1(wholeFirmwareBytes), new Mi1SFirmwareInfoFW2(wholeFirmwareBytes));
    }

    public boolean isGenerallyCompatibleWith(GBDevice device) {
        return MiBandConst.MI_1S.equals(device.getModel());
    }

    public boolean isSingleMiBandFirmware() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isHeaderValid() {
        return ArrayUtils.equals(this.wholeFirmwareBytes, DOUBLE_FW_HEADER, 0);
    }

    public static Mi1SFirmwareInfo getInstance(byte[] wholeFirmwareBytes) {
        Mi1SFirmwareInfo info = new Mi1SFirmwareInfo(wholeFirmwareBytes);
        if (info.isGenerallySupportedFirmware()) {
            return info;
        }
        LOG.info("firmware not supported");
        return null;
    }
}
