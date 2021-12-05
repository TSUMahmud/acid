package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.util.CheckSums;
import p005ch.qos.logback.classic.spi.CallerData;
import p005ch.qos.logback.core.CoreConstants;

public class DeviceInfo extends AbstractInfo {
    public final int appearance;
    public final String deviceId;
    public final int feature;
    public final int fw2Version;
    public final int fwVersion;
    public final int hwVersion;
    public final int profileVersion;
    private boolean test1AHRMode;

    private boolean isChecksumCorrect(byte[] data) {
        if ((data[7] & 255) == ((data[3] & 255) ^ CheckSums.getCRC8(new byte[]{data[0], data[1], data[2], data[3], data[4], data[5], data[6]}))) {
            return true;
        }
        return false;
    }

    public DeviceInfo(byte[] data) {
        super(data);
        if ((data.length == 16 || data.length == 20) && isChecksumCorrect(data)) {
            this.deviceId = String.format("%02X%02X%02X%02X%02X%02X%02X%02X", new Object[]{Byte.valueOf(data[0]), Byte.valueOf(data[1]), Byte.valueOf(data[2]), Byte.valueOf(data[3]), Byte.valueOf(data[4]), Byte.valueOf(data[5]), Byte.valueOf(data[6]), Byte.valueOf(data[7])});
            this.profileVersion = getInt(data, 8);
            this.fwVersion = getInt(data, 12);
            this.hwVersion = data[6] & 255;
            this.appearance = data[5] & 255;
            this.feature = data[4] & 255;
            if (data.length == 20) {
                int s = 0;
                for (int i = 0; i < 4; i++) {
                    s |= (data[i + 16] & 255) << (i * 8);
                }
                this.fw2Version = s;
                return;
            }
            this.fw2Version = -1;
            return;
        }
        this.deviceId = "crc error";
        this.profileVersion = -1;
        this.fwVersion = -1;
        this.hwVersion = -1;
        this.feature = -1;
        this.appearance = -1;
        this.fw2Version = -1;
    }

    public static int getInt(byte[] data, int from, int len) {
        int ret = 0;
        for (int i = 0; i < len; i++) {
            ret |= (data[from + i] & 255) << (i * 8);
        }
        return ret;
    }

    private int getInt(byte[] data, int from) {
        return getInt(data, from, 4);
    }

    public int getFirmwareVersion() {
        return this.fwVersion;
    }

    public int getHeartrateFirmwareVersion() {
        if (this.test1AHRMode) {
            return this.fwVersion;
        }
        return this.fw2Version;
    }

    public int getProfileVersion() {
        return this.profileVersion;
    }

    public void setTest1AHRMode(boolean enableTestMode) {
        this.test1AHRMode = enableTestMode;
    }

    public boolean supportsHeartrate() {
        return isMili1S() || (this.test1AHRMode && isMili1A());
    }

    public String toString() {
        return "DeviceInfo{deviceId='" + this.deviceId + CoreConstants.SINGLE_QUOTE_CHAR + ", profileVersion=" + this.profileVersion + ", fwVersion=" + this.fwVersion + ", hwVersion=" + this.hwVersion + ", feature=" + this.feature + ", appearance=" + this.appearance + ", fw2Version (hr)=" + this.fw2Version + CoreConstants.CURLY_RIGHT;
    }

    public boolean isMili1() {
        int i = this.hwVersion;
        return i == 2 || (this.feature == 0 && this.appearance == 0 && i == 8 && this.fw2Version == -1);
    }

    public boolean isMili1A() {
        return (this.feature == 5 && this.appearance == 0) || (this.feature == 0 && this.hwVersion == 208);
    }

    public boolean isMili1S() {
        return (this.feature == 4 && this.appearance == 0) || this.hwVersion == 4;
    }

    public boolean isAmazFit() {
        return this.hwVersion == 6;
    }

    public String getHwVersion() {
        if (isMili1()) {
            return MiBandConst.MI_1;
        }
        if (isMili1A()) {
            return MiBandConst.MI_1A;
        }
        if (isMili1S()) {
            return MiBandConst.MI_1S;
        }
        if (isAmazFit()) {
            return MiBandConst.MI_AMAZFIT;
        }
        return CallerData.f49NA;
    }
}
