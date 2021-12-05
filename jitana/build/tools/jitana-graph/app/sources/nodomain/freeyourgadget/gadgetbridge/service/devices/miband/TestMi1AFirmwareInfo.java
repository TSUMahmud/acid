package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMi1AFirmwareInfo extends CompositeMiFirmwareInfo {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) TestMi1AFirmwareInfo.class);

    private TestMi1AFirmwareInfo(byte[] wholeFirmwareBytes) {
        super(wholeFirmwareBytes, new Mi1AFirmwareInfo(wholeFirmwareBytes), new Mi1AFirmwareInfo(wholeFirmwareBytes));
    }

    public void checkValid() throws IllegalArgumentException {
        if (getFirst().getFirmwareOffset() != getSecond().getFirmwareOffset()) {
            throw new IllegalArgumentException("Test firmware offsets should be the same: " + getLengthsOffsetsString());
        } else if (getFirst().getFirmwareOffset() < 0 || getSecond().getFirmwareOffset() < 0 || getFirst().getFirmwareLength() <= 0 || getSecond().getFirmwareLength() <= 0) {
            throw new IllegalArgumentException("Illegal test firmware offsets/lengths: " + getLengthsOffsetsString());
        } else if (getFirst().getFirmwareLength() == getSecond().getFirmwareLength()) {
            int firstEndIndex = getFirst().getFirmwareOffset() + getFirst().getFirmwareLength();
            int secondEndIndex = getSecond().getFirmwareOffset();
            if (this.wholeFirmwareBytes.length < firstEndIndex || this.wholeFirmwareBytes.length < secondEndIndex) {
                throw new IllegalArgumentException("Invalid test firmware size, or invalid test offsets/lengths: " + getLengthsOffsetsString());
            }
            getFirst().checkValid();
            getSecond().checkValid();
        } else {
            throw new IllegalArgumentException("Illegal test firmware lengths: " + getLengthsOffsetsString());
        }
    }

    public boolean isGenerallyCompatibleWith(GBDevice device) {
        return getFirst().isGenerallyCompatibleWith(device);
    }

    public boolean isSingleMiBandFirmware() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isHeaderValid() {
        return getFirst().isHeaderValid();
    }

    public static TestMi1AFirmwareInfo getInstance(byte[] wholeFirmwareBytes) {
        TestMi1AFirmwareInfo info = new TestMi1AFirmwareInfo(wholeFirmwareBytes);
        if (info.isGenerallySupportedFirmware()) {
            return info;
        }
        LOG.info("firmware not supported");
        return null;
    }
}
