package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitcor;

import java.util.HashMap;
import java.util.Map;
import nodomain.freeyourgadget.gadgetbridge.devices.no1f1.No1F1Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareInfo;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareType;
import nodomain.freeyourgadget.gadgetbridge.util.ArrayUtils;

public class AmazfitCorFirmwareInfo extends HuamiFirmwareInfo {
    private static final int COMPRESSED_RES_HEADER_OFFSET = 9;
    private static final byte[] FW_HEADER = {0, -104, 0, 32, -91, 4, 0, 32, No1F1Constants.CMD_FACTORY_RESET, 4, 0, 32, PebbleColor.MidnightGreen, 4, 0, 32};
    private static Map<Integer, String> crcToVersion = new HashMap();

    static {
        crcToVersion.put(39948, "1.0.5.60");
        crcToVersion.put(62147, "1.0.5.78");
        crcToVersion.put(54213, "1.0.6.76");
        crcToVersion.put(9458, "1.0.7.52");
        crcToVersion.put(51575, "1.0.7.88");
        crcToVersion.put(6346, "1.2.5.00");
        crcToVersion.put(24277, "1.2.7.20");
        crcToVersion.put(10078, "1.2.7.32");
        crcToVersion.put(46341, "RES 1.0.5.60");
        crcToVersion.put(21770, "RES 1.0.5.78");
        crcToVersion.put(64977, "RES 1.0.6.76");
        crcToVersion.put(60501, "RES 1.0.7.52-71");
        crcToVersion.put(31263, "RES 1.0.7.77-91");
        crcToVersion.put(20920, "RES 1.2.5.00-69");
        crcToVersion.put(25397, "RES 1.2.7.20");
        crcToVersion.put(54167, "RES 1.2.7.32");
        crcToVersion.put(61054, "8");
        crcToVersion.put(62291, "9 (Latin)");
    }

    public AmazfitCorFirmwareInfo(byte[] bytes) {
        super(bytes);
    }

    /* access modifiers changed from: protected */
    public HuamiFirmwareType determineFirmwareType(byte[] bytes) {
        if (ArrayUtils.equals(bytes, RES_HEADER, 9) || ArrayUtils.equals(bytes, NEWRES_HEADER, 9)) {
            return HuamiFirmwareType.RES_COMPRESSED;
        }
        if (ArrayUtils.startsWith(bytes, FW_HEADER)) {
            if (searchString32BitAligned(bytes, "Amazfit Cor")) {
                return HuamiFirmwareType.FIRMWARE;
            }
            return HuamiFirmwareType.INVALID;
        } else if (ArrayUtils.startsWith(bytes, WATCHFACE_HEADER)) {
            return HuamiFirmwareType.WATCHFACE;
        } else {
            if (ArrayUtils.startsWith(bytes, NEWFT_HEADER)) {
                if (bytes[10] == 1) {
                    return HuamiFirmwareType.FONT;
                }
                if (bytes[10] == 2) {
                    return HuamiFirmwareType.FONT_LATIN;
                }
            }
            if (ArrayUtils.startsWith(bytes, RES_HEADER)) {
                return HuamiFirmwareType.RES;
            }
            return HuamiFirmwareType.INVALID;
        }
    }

    public boolean isGenerallyCompatibleWith(GBDevice device) {
        return isHeaderValid() && device.getType() == DeviceType.AMAZFITCOR;
    }

    /* access modifiers changed from: protected */
    public Map<Integer, String> getCrcMap() {
        return crcToVersion;
    }
}
