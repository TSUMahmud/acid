package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitcor2;

import java.util.HashMap;
import java.util.Map;
import nodomain.freeyourgadget.gadgetbridge.devices.no1f1.No1F1Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareInfo;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareType;
import nodomain.freeyourgadget.gadgetbridge.util.ArrayUtils;

public class AmazfitCor2FirmwareInfo extends HuamiFirmwareInfo {
    private static final byte[] FW_HEADER = {0, -104, 0, 32, -91, 4, 0, 32, No1F1Constants.CMD_FACTORY_RESET, 4, 0, 32, PebbleColor.MidnightGreen, 4, 0, 32};
    private static Map<Integer, String> crcToVersion = new HashMap();

    static {
        crcToVersion.put(61054, "8");
        crcToVersion.put(62291, "9 (Latin)");
    }

    public AmazfitCor2FirmwareInfo(byte[] bytes) {
        super(bytes);
    }

    /* access modifiers changed from: protected */
    public HuamiFirmwareType determineFirmwareType(byte[] bytes) {
        if (ArrayUtils.equals(bytes, RES_HEADER, 9) || ArrayUtils.equals(bytes, NEWRES_HEADER, 13) || ArrayUtils.equals(bytes, NEWRES_HEADER, 9)) {
            return HuamiFirmwareType.RES_COMPRESSED;
        }
        if (ArrayUtils.startsWith(bytes, FW_HEADER)) {
            if (searchString32BitAligned(bytes, "Amazfit Cor 2")) {
                return HuamiFirmwareType.FIRMWARE;
            }
            if (searchString32BitAligned(bytes, "Amazfit Band 2")) {
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
        return isHeaderValid() && device.getType() == DeviceType.AMAZFITCOR2;
    }

    /* access modifiers changed from: protected */
    public Map<Integer, String> getCrcMap() {
        return crcToVersion;
    }
}
