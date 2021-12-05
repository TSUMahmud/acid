package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import nodomain.freeyourgadget.gadgetbridge.util.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMi1FirmwareInfo extends AbstractMiFirmwareInfo {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractMi1FirmwareInfo.class);
    private static final int MI1_FW_BASE_OFFSET = 1056;
    private static final byte[] SINGLE_FW_HEADER = {0, -104, 0, 32, -119, 4, 0, 32};
    private static final int SINGLE_FW_HEADER_OFFSET = 0;

    /* access modifiers changed from: protected */
    public abstract int getSupportedMajorVersion();

    protected AbstractMi1FirmwareInfo(byte[] wholeFirmwareBytes) {
        super(wholeFirmwareBytes);
    }

    public boolean isSingleMiBandFirmware() {
        return true;
    }

    public int getFirmwareOffset() {
        return 0;
    }

    public int getFirmwareLength() {
        return this.wholeFirmwareBytes.length;
    }

    public int getFirmwareVersion() {
        return (this.wholeFirmwareBytes[getOffsetFirmwareVersionMajor()] << 24) | (this.wholeFirmwareBytes[getOffsetFirmwareVersionMinor()] << 16) | (this.wholeFirmwareBytes[getOffsetFirmwareVersionRevision()] << 8) | this.wholeFirmwareBytes[getOffsetFirmwareVersionBuild()];
    }

    private int getOffsetFirmwareVersionMajor() {
        return 1059;
    }

    private int getOffsetFirmwareVersionMinor() {
        return 1058;
    }

    private int getOffsetFirmwareVersionRevision() {
        return 1057;
    }

    private int getOffsetFirmwareVersionBuild() {
        return MI1_FW_BASE_OFFSET;
    }

    /* access modifiers changed from: protected */
    public boolean isGenerallySupportedFirmware() {
        try {
            if (!isHeaderValid()) {
                LOG.info("unrecognized header");
                return false;
            }
            int majorVersion = getFirmwareVersionMajor();
            if (majorVersion == getSupportedMajorVersion()) {
                return true;
            }
            Logger logger = LOG;
            logger.info("Only major version " + getSupportedMajorVersion() + " is supported: " + majorVersion);
            return false;
        } catch (IllegalArgumentException ex) {
            Logger logger2 = LOG;
            logger2.warn("invalid firmware or bug: " + ex.getLocalizedMessage(), (Throwable) ex);
        } catch (IndexOutOfBoundsException ex2) {
            Logger logger3 = LOG;
            logger3.warn("not supported firmware: " + ex2.getLocalizedMessage(), (Throwable) ex2);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isHeaderValid() {
        return ArrayUtils.equals(this.wholeFirmwareBytes, SINGLE_FW_HEADER, 0);
    }

    public void checkValid() throws IllegalArgumentException {
        super.checkValid();
        if (this.wholeFirmwareBytes.length < SINGLE_FW_HEADER.length) {
            throw new IllegalArgumentException("firmware too small: " + this.wholeFirmwareBytes.length);
        }
    }
}
