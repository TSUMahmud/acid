package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitgts;

import java.util.HashMap;
import java.util.Map;
import nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.no1f1.No1F1Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareInfo;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareType;
import nodomain.freeyourgadget.gadgetbridge.util.ArrayUtils;

public class AmazfitGTSFirmwareInfo extends HuamiFirmwareInfo {
    private static final byte[] FW_HEADER = {32, ZeTimeConstants.CMD_PUSH_CALENDAR_DAY, 18, 1, 8};
    private static final int FW_OFFSET = 3;
    private static final byte[] GPS_ALMANAC_HEADER = {No1F1Constants.CMD_DISPLAY_SETTINGS, Byte.MIN_VALUE, 8, 0, -117, 7};
    private static final byte[] GPS_CEP_HEADER = {42, 18, No1F1Constants.CMD_DISPLAY_SETTINGS, 2};
    private static final byte[][] GPS_HEADERS = {new byte[]{MakibesHR3Constants.CMD_SET_ALARM_REMINDER, MakibesHR3Constants.CMD_SET_SEDENTARY_REMINDER, 104, -48, ZeTimeConstants.CMD_REQUEST, MakibesHR3Constants.CMD_SET_ALARM_REMINDER, -69, 90, 62, -61, -45, 9, -98, 29, -45, PebbleColor.JaegerGreen}};
    private static Map<Integer, String> crcToVersion = new HashMap();

    static {
        crcToVersion.put(62532, "18344,eb2f43f,126");
    }

    public AmazfitGTSFirmwareInfo(byte[] bytes) {
        super(bytes);
    }

    /* access modifiers changed from: protected */
    public HuamiFirmwareType determineFirmwareType(byte[] bytes) {
        if (ArrayUtils.equals(bytes, NEWRES_HEADER, 13)) {
            return HuamiFirmwareType.RES_COMPRESSED;
        }
        if (ArrayUtils.equals(bytes, FW_HEADER, 3)) {
            if (searchString32BitAligned(bytes, "Amazfit GTS")) {
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
            if (ArrayUtils.startsWith(bytes, GPS_ALMANAC_HEADER)) {
                return HuamiFirmwareType.GPS_ALMANAC;
            }
            if (ArrayUtils.startsWith(bytes, GPS_CEP_HEADER)) {
                return HuamiFirmwareType.GPS_CEP;
            }
            for (byte[] gpsHeader : GPS_HEADERS) {
                if (ArrayUtils.startsWith(bytes, gpsHeader)) {
                    return HuamiFirmwareType.GPS;
                }
            }
            return HuamiFirmwareType.INVALID;
        }
    }

    public boolean isGenerallyCompatibleWith(GBDevice device) {
        return isHeaderValid() && device.getType() == DeviceType.AMAZFITGTS;
    }

    /* access modifiers changed from: protected */
    public Map<Integer, String> getCrcMap() {
        return crcToVersion;
    }
}
