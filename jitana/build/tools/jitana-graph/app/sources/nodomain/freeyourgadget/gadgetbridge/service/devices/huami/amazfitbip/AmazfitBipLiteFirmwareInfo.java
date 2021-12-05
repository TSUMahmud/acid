package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitbip;

import java.util.HashMap;
import java.util.Map;
import nodomain.freeyourgadget.gadgetbridge.devices.no1f1.No1F1Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareInfo;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareType;
import nodomain.freeyourgadget.gadgetbridge.util.ArrayUtils;

public class AmazfitBipLiteFirmwareInfo extends HuamiFirmwareInfo {
    private static final byte[] FW_HEADER = {0, -104, 0, 32, -91, 4, 0, 32, No1F1Constants.CMD_FACTORY_RESET, 4, 0, 32, PebbleColor.MidnightGreen, 4, 0, 32};
    private static Map<Integer, String> crcToVersion = new HashMap();

    static {
        crcToVersion.put(11059, "1.1.6.02");
        crcToVersion.put(57510, "1.1.6.02");
        crcToVersion.put(61054, "8");
        crcToVersion.put(59577, "9 (Latin)");
    }

    public AmazfitBipLiteFirmwareInfo(byte[] bytes) {
        super(bytes);
    }

    /* access modifiers changed from: protected */
    public HuamiFirmwareType determineFirmwareType(byte[] bytes) {
        if (ArrayUtils.startsWith(bytes, NEWRES_HEADER)) {
            if (bytes.length <= 100000 || bytes.length > 700000) {
                return HuamiFirmwareType.INVALID;
            }
            return HuamiFirmwareType.RES;
        } else if (ArrayUtils.startsWith(bytes, FW_HEADER)) {
            if (searchString32BitAligned(bytes, "Amazfit Bip Lite")) {
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
                if (bytes[10] == 2 || bytes[10] == 10) {
                    return HuamiFirmwareType.FONT_LATIN;
                }
            }
            return HuamiFirmwareType.INVALID;
        }
    }

    public boolean isGenerallyCompatibleWith(GBDevice device) {
        return isHeaderValid() && device.getType() == DeviceType.AMAZFITBIP_LITE;
    }

    /* access modifiers changed from: protected */
    public Map<Integer, String> getCrcMap() {
        return crcToVersion;
    }
}
