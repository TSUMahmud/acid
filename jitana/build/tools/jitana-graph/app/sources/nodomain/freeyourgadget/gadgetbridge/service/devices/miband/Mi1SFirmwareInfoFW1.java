package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mi1SFirmwareInfoFW1 extends AbstractMi1SFirmwareInfo {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) Mi1SFirmwareInfoFW1.class);
    private static final int MI1S_FW_BASE_OFFSET = 1092;

    Mi1SFirmwareInfoFW1(byte[] wholeFirmwareBytes) {
        super(wholeFirmwareBytes);
    }

    /* access modifiers changed from: protected */
    public boolean isHeaderValid() {
        return true;
    }

    public int getFirmwareOffset() {
        return ((this.wholeFirmwareBytes[12] & 255) << 24) | ((this.wholeFirmwareBytes[13] & 255) << 16) | ((this.wholeFirmwareBytes[14] & 255) << 8) | (this.wholeFirmwareBytes[15] & 255);
    }

    public int getFirmwareLength() {
        return ((this.wholeFirmwareBytes[16] & 255) << 24) | ((this.wholeFirmwareBytes[17] & 255) << 16) | ((this.wholeFirmwareBytes[18] & 255) << 8) | (this.wholeFirmwareBytes[19] & 255);
    }

    public int getFirmwareVersion() {
        return ((this.wholeFirmwareBytes[8] & 255) << 24) | ((this.wholeFirmwareBytes[9] & 255) << 16) | ((this.wholeFirmwareBytes[10] & 255) << 8) | (this.wholeFirmwareBytes[11] & 255);
    }

    /* access modifiers changed from: protected */
    public boolean isGenerallySupportedFirmware() {
        try {
            int majorVersion = getFirmwareVersionMajor();
            if (majorVersion == 4) {
                return true;
            }
            Logger logger = LOG;
            logger.warn("Only major version 4 is supported for 1S fw1: " + majorVersion);
            return false;
        } catch (IllegalArgumentException ex) {
            Logger logger2 = LOG;
            logger2.warn("not supported 1S firmware 1: " + ex.getLocalizedMessage(), (Throwable) ex);
            return false;
        }
    }
}
