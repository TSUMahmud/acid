package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.AbstractMiFirmwareInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CompositeMiFirmwareInfo<T extends AbstractMiFirmwareInfo> extends AbstractMiFirmwareInfo {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) CompositeMiFirmwareInfo.class);
    private final T fw1Info;
    private final T fw2Info;

    protected CompositeMiFirmwareInfo(byte[] wholeFirmwareBytes, T info1, T info2) {
        super(wholeFirmwareBytes);
        this.fw1Info = info1;
        this.fw2Info = info2;
    }

    public void checkValid() throws IllegalArgumentException {
        super.checkValid();
        if (getFirst().getFirmwareOffset() == getSecond().getFirmwareOffset()) {
            throw new IllegalArgumentException("Illegal firmware offsets: " + getLengthsOffsetsString());
        } else if (getFirst().getFirmwareOffset() < 0 || getSecond().getFirmwareOffset() < 0 || getFirst().getFirmwareLength() <= 0 || getSecond().getFirmwareLength() <= 0) {
            throw new IllegalArgumentException("Illegal firmware offsets/lengths: " + getLengthsOffsetsString());
        } else {
            int firstEndIndex = getFirst().getFirmwareOffset() + getFirst().getFirmwareLength();
            if (getSecond().getFirmwareOffset() >= firstEndIndex) {
                int secondEndIndex = getSecond().getFirmwareOffset();
                if (this.wholeFirmwareBytes.length < firstEndIndex || this.wholeFirmwareBytes.length < secondEndIndex) {
                    throw new IllegalArgumentException("Invalid firmware size, or invalid offsets/lengths: " + getLengthsOffsetsString());
                }
                getFirst().checkValid();
                getSecond().checkValid();
                return;
            }
            throw new IllegalArgumentException("Invalid firmware, second fw starts before first fw ends: " + firstEndIndex + "," + getSecond().getFirmwareOffset());
        }
    }

    /* access modifiers changed from: protected */
    public String getLengthsOffsetsString() {
        return getFirst().getFirmwareOffset() + "," + getFirst().getFirmwareLength() + "; " + getSecond().getFirmwareOffset() + "," + getSecond().getFirmwareLength();
    }

    public T getFirst() {
        return this.fw1Info;
    }

    public T getSecond() {
        return this.fw2Info;
    }

    /* access modifiers changed from: protected */
    public boolean isGenerallySupportedFirmware() {
        try {
            if (!isHeaderValid()) {
                LOG.info("unrecognized header");
                return false;
            } else if (!this.fw1Info.isGenerallySupportedFirmware() || !this.fw2Info.isGenerallySupportedFirmware() || this.fw1Info.getFirmwareBytes().length <= 0 || this.fw2Info.getFirmwareBytes().length <= 0) {
                return false;
            } else {
                return true;
            }
        } catch (IndexOutOfBoundsException ex) {
            Logger logger = LOG;
            logger.warn("invalid firmware or bug: " + ex.getLocalizedMessage(), (Throwable) ex);
            return false;
        } catch (IllegalArgumentException ex2) {
            Logger logger2 = LOG;
            logger2.warn("not supported 1S firmware: " + ex2.getLocalizedMessage(), (Throwable) ex2);
            return false;
        }
    }

    public int getFirmwareOffset() {
        throw new UnsupportedOperationException("call this method on getFirmwareXInfo()");
    }

    public int getFirmwareLength() {
        throw new UnsupportedOperationException("call this method on getFirmwareXInfo()");
    }

    public int getFirmwareVersion() {
        throw new UnsupportedOperationException("call this method on getFirmwareXInfo()");
    }
}
