package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import java.util.Arrays;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

public abstract class AbstractMiFirmwareInfo {
    protected byte[] wholeFirmwareBytes;

    public abstract int getFirmwareLength();

    public abstract int getFirmwareOffset();

    public abstract int getFirmwareVersion();

    public abstract boolean isGenerallyCompatibleWith(GBDevice gBDevice);

    /* access modifiers changed from: protected */
    public abstract boolean isGenerallySupportedFirmware();

    /* access modifiers changed from: protected */
    public abstract boolean isHeaderValid();

    public abstract boolean isSingleMiBandFirmware();

    public static AbstractMiFirmwareInfo determineFirmwareInfoFor(byte[] wholeFirmwareBytes2) {
        AbstractMiFirmwareInfo[] candidates = getFirmwareInfoCandidatesFor(wholeFirmwareBytes2);
        if (candidates.length == 0) {
            throw new IllegalArgumentException("Unsupported data (maybe not even a firmware?).");
        } else if (candidates.length == 1) {
            return candidates[0];
        } else {
            throw new IllegalArgumentException("don't know for which device the firmware is, matches multiple devices");
        }
    }

    private static AbstractMiFirmwareInfo[] getFirmwareInfoCandidatesFor(byte[] wholeFirmwareBytes2) {
        AbstractMiFirmwareInfo[] candidates = new AbstractMiFirmwareInfo[3];
        int i = 0;
        Mi1FirmwareInfo mi1Info = Mi1FirmwareInfo.getInstance(wholeFirmwareBytes2);
        if (mi1Info != null) {
            candidates[0] = mi1Info;
            i = 0 + 1;
        }
        Mi1AFirmwareInfo mi1aInfo = Mi1AFirmwareInfo.getInstance(wholeFirmwareBytes2);
        if (mi1aInfo != null) {
            candidates[i] = mi1aInfo;
            i++;
        }
        Mi1SFirmwareInfo mi1sInfo = Mi1SFirmwareInfo.getInstance(wholeFirmwareBytes2);
        if (mi1sInfo != null) {
            candidates[i] = mi1sInfo;
            i++;
        }
        return (AbstractMiFirmwareInfo[]) Arrays.copyOfRange(candidates, 0, i);
    }

    public AbstractMiFirmwareInfo(byte[] wholeFirmwareBytes2) {
        this.wholeFirmwareBytes = wholeFirmwareBytes2;
    }

    public byte[] getFirmwareBytes() {
        return Arrays.copyOfRange(this.wholeFirmwareBytes, getFirmwareOffset(), getFirmwareOffset() + getFirmwareLength());
    }

    public int getFirmwareVersionMajor() {
        int version = getFirmwareVersion();
        if (version > 0) {
            return version >> 24;
        }
        throw new IllegalArgumentException("bad firmware version: " + version);
    }

    public void checkValid() throws IllegalArgumentException {
    }

    public AbstractMiFirmwareInfo getFirst() {
        if (isSingleMiBandFirmware()) {
            return this;
        }
        throw new UnsupportedOperationException(getClass().getName() + " must override getFirst() and getSecond()");
    }

    public AbstractMiFirmwareInfo getSecond() {
        if (isSingleMiBandFirmware()) {
            throw new UnsupportedOperationException(getClass().getName() + " only supports on firmware");
        }
        throw new UnsupportedOperationException(getClass().getName() + " must override getFirst() and getSecond()");
    }
}
