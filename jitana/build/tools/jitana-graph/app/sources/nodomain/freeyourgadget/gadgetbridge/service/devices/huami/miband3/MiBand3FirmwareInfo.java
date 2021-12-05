package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband3;

import java.util.HashMap;
import java.util.Map;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.devices.no1f1.No1F1Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareInfo;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareType;
import nodomain.freeyourgadget.gadgetbridge.util.ArrayUtils;

public class MiBand3FirmwareInfo extends HuamiFirmwareInfo {
    private static final byte[] FW_HEADER = {No1F1Constants.CMD_DATETIME, 104, 4, 59, 2, PebbleColor.PictonBlue, PebbleColor.IslamicGreen, 88, -48, 80, -6, PebbleColor.LavenderIndigo, 12, 52, -13, PebbleColor.LavenderIndigo};
    private static final int FW_HEADER_OFFSET = 336;
    private static final byte FW_MAGIC = -7;
    private static final int FW_MAGIC_OFFSET = 381;
    private static Map<Integer, String> crcToVersion = new HashMap();

    static {
        crcToVersion.put(55852, "1.2.0.8");
        crcToVersion.put(14899, "1.3.0.4");
        crcToVersion.put(20651, "1.3.0.8");
        crcToVersion.put(60781, "1.4.0.12");
        crcToVersion.put(30045, "1.5.0.2");
        crcToVersion.put(38254, "1.5.0.7");
        crcToVersion.put(46985, "1.5.0.11");
        crcToVersion.put(31330, "1.6.0.16");
        crcToVersion.put(10930, "1.8.0.0");
        crcToVersion.put(59800, "2.0.0.4");
        crcToVersion.put(10023, "2.2.0.12");
        crcToVersion.put(40344, "2.2.0.14");
        crcToVersion.put(4467, "2.2.0.42");
        crcToVersion.put(61657, "2.3.0.2");
        crcToVersion.put(62735, "2.3.0.6");
        crcToVersion.put(40949, "2.3.0.28");
        crcToVersion.put(59213, "2.4.0.12");
        crcToVersion.put(10810, "2.4.0.20");
        crcToVersion.put(18271, "2.4.0.32");
        crcToVersion.put(46724, "1.7.0.4");
        crcToVersion.put(54724, "1.2.0.8");
        crcToVersion.put(52589, "1.3.0.4");
        crcToVersion.put(34642, "1.3.0.8");
        crcToVersion.put(25278, "1.4.0.12-1.6.0.16");
        crcToVersion.put(23249, "1.8.0.0");
        crcToVersion.put(1815, "2.0.0.4");
        crcToVersion.put(7225, "2.2.0.12-2.3.0.6");
        crcToVersion.put(52754, "2.3.0.28");
        crcToVersion.put(17930, "2.4.0.12-32");
        crcToVersion.put(19775, MiBandConst.MI_1);
        crcToVersion.put(42959, "2 (old Jap/Kor)");
        crcToVersion.put(12052, "1 (Jap/Kor)");
    }

    public MiBand3FirmwareInfo(byte[] bytes) {
        super(bytes);
    }

    /* access modifiers changed from: protected */
    public HuamiFirmwareType determineFirmwareType(byte[] bytes) {
        if (ArrayUtils.startsWith(bytes, FT_HEADER)) {
            if (bytes[9] < 3 || bytes[9] > 5) {
                return HuamiFirmwareType.INVALID;
            }
            return HuamiFirmwareType.FONT;
        } else if (ArrayUtils.startsWith(bytes, RES_HEADER)) {
            if (bytes.length > 150000) {
                return HuamiFirmwareType.INVALID;
            }
            return HuamiFirmwareType.RES;
        } else if (!ArrayUtils.equals(bytes, FW_HEADER, FW_HEADER_OFFSET) || bytes[FW_MAGIC_OFFSET] != -7) {
            return HuamiFirmwareType.INVALID;
        } else {
            return HuamiFirmwareType.FIRMWARE;
        }
    }

    public boolean isGenerallyCompatibleWith(GBDevice device) {
        return isHeaderValid() && device.getType() == DeviceType.MIBAND3;
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
