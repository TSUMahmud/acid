package nodomain.freeyourgadget.gadgetbridge.service.devices.huami;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusConstants;
import nodomain.freeyourgadget.gadgetbridge.devices.xwatch.XWatchService;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.util.ArrayUtils;
import nodomain.freeyourgadget.gadgetbridge.util.CheckSums;

public abstract class HuamiFirmwareInfo {
    protected static final int COMPRESSED_RES_HEADER_OFFSET = 9;
    protected static final int COMPRESSED_RES_HEADER_OFFSET_NEW = 13;
    protected static final int FONT_TYPE_OFFSET = 9;
    protected static final byte[] FT_HEADER = {HPlusConstants.CMD_SET_UNITS, HPlusConstants.DATA_UNKNOWN, 90, 75};
    protected static final byte[] NEWFT_HEADER = {HPlusConstants.CMD_SET_BLOOD, 69, 90, 75};
    protected static final byte[] NEWRES_HEADER = {HPlusConstants.CMD_SET_BLOOD, 69, 82, 69, ZeTimeConstants.CMD_DELETE_STEP_COUNT};
    protected static final byte[] RES_HEADER = {HPlusConstants.CMD_SET_UNITS, HPlusConstants.DATA_UNKNOWN, 82, 69, ZeTimeConstants.CMD_DELETE_STEP_COUNT};
    protected static final byte[] WATCHFACE_HEADER = {HPlusConstants.CMD_SET_UNITS, HPlusConstants.DATA_UNKNOWN, 68, 73, 65, XWatchService.COMMAND_ACTION_BUTTON};
    private byte[] bytes;
    private final int crc16;
    private final int crc32;
    private HuamiFirmwareType firmwareType;

    /* access modifiers changed from: protected */
    public abstract HuamiFirmwareType determineFirmwareType(byte[] bArr);

    /* access modifiers changed from: protected */
    public abstract Map<Integer, String> getCrcMap();

    public abstract boolean isGenerallyCompatibleWith(GBDevice gBDevice);

    public String toVersion(int crc162) {
        String version = getCrcMap().get(Integer.valueOf(crc162));
        if (version == null) {
            int i = C11741.f176xc8117066[this.firmwareType.ordinal()];
            if (i == 1) {
                version = searchFirmwareVersion(this.bytes);
            } else if (i == 2) {
                version = "RES " + this.bytes[5];
            } else if (i == 3) {
                version = "RES " + this.bytes[14];
            } else if (i == 4) {
                version = "FONT " + this.bytes[4];
            } else if (i == 5) {
                version = "FONT LATIN " + this.bytes[4];
            }
        }
        if (version != null) {
            return version;
        }
        int i2 = C11741.f176xc8117066[this.firmwareType.ordinal()];
        if (i2 == 1) {
            return "(unknown)";
        }
        switch (i2) {
            case 4:
            case 5:
                return "(unknown font)";
            case 6:
                return "(unknown GPS)";
            case 7:
                return "(unknown CEP)";
            case 8:
                return "(unknown ALM)";
            case 9:
                return "(unknown watchface)";
            default:
                return version;
        }
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareInfo$1 */
    static /* synthetic */ class C11741 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$service$devices$huami$HuamiFirmwareType */
        static final /* synthetic */ int[] f176xc8117066 = new int[HuamiFirmwareType.values().length];

        static {
            try {
                f176xc8117066[HuamiFirmwareType.FIRMWARE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f176xc8117066[HuamiFirmwareType.RES.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f176xc8117066[HuamiFirmwareType.RES_COMPRESSED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f176xc8117066[HuamiFirmwareType.FONT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f176xc8117066[HuamiFirmwareType.FONT_LATIN.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f176xc8117066[HuamiFirmwareType.GPS.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f176xc8117066[HuamiFirmwareType.GPS_CEP.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f176xc8117066[HuamiFirmwareType.GPS_ALMANAC.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f176xc8117066[HuamiFirmwareType.WATCHFACE.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    public int[] getWhitelistedVersions() {
        return ArrayUtils.toIntArray(getCrcMap().keySet());
    }

    public HuamiFirmwareInfo(byte[] bytes2) {
        this.bytes = bytes2;
        this.crc16 = CheckSums.getCRC16(bytes2);
        this.crc32 = CheckSums.getCRC32(bytes2);
        this.firmwareType = determineFirmwareType(bytes2);
    }

    public boolean isHeaderValid() {
        return getFirmwareType() != HuamiFirmwareType.INVALID;
    }

    public void checkValid() throws IllegalArgumentException {
    }

    public int getSize() {
        return this.bytes.length;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public int getCrc16() {
        return this.crc16;
    }

    public int getCrc32() {
        return this.crc32;
    }

    public int getFirmwareVersion() {
        return getCrc16();
    }

    public HuamiFirmwareType getFirmwareType() {
        return this.firmwareType;
    }

    /* access modifiers changed from: protected */
    public String searchFirmwareVersion(byte[] fwbytes) {
        ByteBuffer buf = ByteBuffer.wrap(fwbytes);
        buf.order(ByteOrder.BIG_ENDIAN);
        while (buf.remaining() > 3) {
            if (buf.getInt() == 1445291054 && buf.getInt() == 627322405 && buf.getInt() == 1680745828 && buf.getInt() == 0) {
                byte[] version = new byte[8];
                buf.get(version);
                return new String(version);
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean searchString32BitAligned(byte[] fwbytes, String findString) {
        ByteBuffer stringBuf = ByteBuffer.wrap((findString + "\u0000").getBytes());
        stringBuf.order(ByteOrder.BIG_ENDIAN);
        int[] findArray = new int[(stringBuf.remaining() / 4)];
        for (int i = 0; i < findArray.length; i++) {
            findArray[i] = stringBuf.getInt();
        }
        ByteBuffer buf = ByteBuffer.wrap(fwbytes);
        buf.order(ByteOrder.BIG_ENDIAN);
        while (buf.remaining() > 3) {
            int arrayPos = 0;
            while (arrayPos < findArray.length && buf.remaining() > 3 && buf.getInt() == findArray[arrayPos]) {
                arrayPos++;
            }
            if (arrayPos == findArray.length) {
                return true;
            }
        }
        return false;
    }
}
