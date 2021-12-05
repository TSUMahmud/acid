package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband4;

import java.util.HashMap;
import java.util.Map;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiConst;
import nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareInfo;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareType;
import nodomain.freeyourgadget.gadgetbridge.util.ArrayUtils;

public class MiBand4FirmwareInfo extends HuamiFirmwareInfo {
    private static final byte[] FW_HEADER = {49, 0, 0, 0, 0, 0, 0, 0, 0, 0, -100, PebbleColor.VividViolet, MakibesHR3Constants.RPRT_REVERSE_FIND_DEVICE, ZeTimeConstants.CMD_AUTO_HEARTRATE, 0, 4};
    private static final int FW_HEADER_OFFSET = 16;
    private static Map<Integer, String> crcToVersion = new HashMap();

    static {
        crcToVersion.put(8969, "1.0.5.22");
        crcToVersion.put(43437, "1.0.5.66");
        crcToVersion.put(31632, "1.0.6.00");
        crcToVersion.put(6856, "1.0.7.14");
        crcToVersion.put(50145, "1.0.7.60");
        crcToVersion.put(27412, "1.0.5.22");
        crcToVersion.put(5466, "1.0.5.66");
        crcToVersion.put(20047, "1.0.6.00");
        crcToVersion.put(62914, "1.0.7.14");
        crcToVersion.put(17303, "1.0.7.60");
        crcToVersion.put(31978, MiBandConst.MI_1);
    }

    public MiBand4FirmwareInfo(byte[] bytes) {
        super(bytes);
    }

    /* access modifiers changed from: protected */
    public HuamiFirmwareType determineFirmwareType(byte[] bytes) {
        if (ArrayUtils.equals(bytes, RES_HEADER, 9) || ArrayUtils.equals(bytes, NEWRES_HEADER, 13) || ArrayUtils.equals(bytes, NEWRES_HEADER, 9)) {
            return HuamiFirmwareType.RES_COMPRESSED;
        }
        if (ArrayUtils.equals(bytes, FW_HEADER, 16)) {
            if (searchString32BitAligned(bytes, HuamiConst.MI_BAND4_NAME)) {
                return HuamiFirmwareType.FIRMWARE;
            }
            return HuamiFirmwareType.INVALID;
        } else if (ArrayUtils.startsWith(bytes, WATCHFACE_HEADER)) {
            return HuamiFirmwareType.WATCHFACE;
        } else {
            if (ArrayUtils.startsWith(bytes, NEWFT_HEADER) && (bytes[10] == 3 || bytes[10] == 6)) {
                return HuamiFirmwareType.FONT;
            }
            if (ArrayUtils.startsWith(bytes, RES_HEADER)) {
                return HuamiFirmwareType.RES;
            }
            return HuamiFirmwareType.INVALID;
        }
    }

    public boolean isGenerallyCompatibleWith(GBDevice device) {
        return isHeaderValid() && device.getType() == DeviceType.MIBAND4;
    }

    /* access modifiers changed from: protected */
    public Map<Integer, String> getCrcMap() {
        return crcToVersion;
    }
}
