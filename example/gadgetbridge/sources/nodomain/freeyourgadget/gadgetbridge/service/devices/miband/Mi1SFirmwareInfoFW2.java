package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mi1SFirmwareInfoFW2 extends AbstractMi1SFirmwareInfo {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) Mi1SFirmwareInfoFW2.class);

    Mi1SFirmwareInfoFW2(byte[] wholeFirmwareBytes) {
        super(wholeFirmwareBytes);
    }

    /* access modifiers changed from: protected */
    public boolean isHeaderValid() {
        return true;
    }

    public int getFirmwareOffset() {
        return ((this.wholeFirmwareBytes[26] & 255) << 24) | ((this.wholeFirmwareBytes[27] & 255) << 16) | ((this.wholeFirmwareBytes[28] & 255) << 8) | (this.wholeFirmwareBytes[29] & 255);
    }

    public int getFirmwareLength() {
        return ((this.wholeFirmwareBytes[30] & 255) << 24) | ((this.wholeFirmwareBytes[31] & 255) << 16) | ((this.wholeFirmwareBytes[32] & 255) << 8) | (this.wholeFirmwareBytes[33] & 255);
    }

    /* access modifiers changed from: protected */
    public boolean isGenerallySupportedFirmware() {
        try {
            int majorVersion = getFirmwareVersionMajor();
            if (majorVersion == 1) {
                return true;
            }
            Logger logger = LOG;
            logger.warn("Only major version 1 is supported for 1S fw2: " + majorVersion);
            return false;
        } catch (IllegalArgumentException ex) {
            Logger logger2 = LOG;
            logger2.warn("not supported 1S firmware 2: " + ex.getLocalizedMessage(), (Throwable) ex);
            return false;
        }
    }

    public int getFirmwareVersion() {
        return ((this.wholeFirmwareBytes[22] & 255) << 24) | ((this.wholeFirmwareBytes[23] & 255) << 16) | ((this.wholeFirmwareBytes[24] & 255) << 8) | (this.wholeFirmwareBytes[25] & 255);
    }
}
