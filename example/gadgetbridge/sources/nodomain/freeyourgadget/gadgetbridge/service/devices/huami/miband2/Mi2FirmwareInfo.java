package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband2;

import java.util.HashMap;
import java.util.Map;
import nodomain.freeyourgadget.gadgetbridge.devices.no1f1.No1F1Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareInfo;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareType;
import nodomain.freeyourgadget.gadgetbridge.util.ArrayUtils;

public class Mi2FirmwareInfo extends HuamiFirmwareInfo {
    private static final byte[] FW_HEADER = {No1F1Constants.CMD_DATETIME, 104, 4, 59, 2, PebbleColor.PictonBlue, PebbleColor.IslamicGreen, 88, -48, 80, -6, PebbleColor.LavenderIndigo, 12, 52, -13, PebbleColor.LavenderIndigo};
    private static final int FW_HEADER_OFFSET = 336;
    private static final byte FW_MAGIC = -8;
    private static final int FW_MAGIC_OFFSET = 381;
    private static Map<Integer, String> crcToVersion = new HashMap();

    static {
        crcToVersion.put(41899, "1.0.0.39");
        crcToVersion.put(49197, "1.0.0.53");
        crcToVersion.put(32450, "1.0.1.28");
        crcToVersion.put(51770, "1.0.1.34");
        crcToVersion.put(3929, "1.0.1.39");
        crcToVersion.put(47364, "1.0.1.54");
        crcToVersion.put(44776, "1.0.1.59");
        crcToVersion.put(27318, "1.0.1.67");
        crcToVersion.put(54702, "1.0.1.69");
        crcToVersion.put(31698, "1.0.1.81");
        crcToVersion.put(53474, "1.0.1.81 (tph)");
        crcToVersion.put(46048, "1.0.1.81 (tph as7000)");
        crcToVersion.put(19930, "1.0.1.81 (tph india)");
        crcToVersion.put(45624, "Font");
        crcToVersion.put(6377, "Font (En)");
    }

    public Mi2FirmwareInfo(byte[] bytes) {
        super(bytes);
    }

    /* access modifiers changed from: protected */
    public HuamiFirmwareType determineFirmwareType(byte[] bytes) {
        if (ArrayUtils.startsWith(bytes, FT_HEADER)) {
            if (bytes[9] == 0 || bytes[9] == -1) {
                return HuamiFirmwareType.FONT;
            }
            return HuamiFirmwareType.INVALID;
        } else if (!ArrayUtils.equals(bytes, FW_HEADER, FW_HEADER_OFFSET) || bytes[FW_MAGIC_OFFSET] != -8) {
            return HuamiFirmwareType.INVALID;
        } else {
            return HuamiFirmwareType.FIRMWARE;
        }
    }

    public boolean isGenerallyCompatibleWith(GBDevice device) {
        return isHeaderValid() && device.getType() == DeviceType.MIBAND2;
    }

    /* access modifiers changed from: protected */
    public Map<Integer, String> getCrcMap() {
        return crcToVersion;
    }

    /* access modifiers changed from: protected */
    public String searchFirmwareVersion(byte[] fwbytes) {
        return null;
    }
}
