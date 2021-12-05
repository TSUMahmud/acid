package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.buttonconfig;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusConstants;
import nodomain.freeyourgadget.gadgetbridge.devices.jyou.BFH16Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.liveview.LiveviewConstants;
import nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;

public enum ConfigPayload {
    FORWARD_TO_PHONE("forward to phone", new byte[]{1, 1, 12, 0}, new byte[]{1, 0, 1, 1, 12, HPlusConstants.DATA_VERSION1, 0, 0, 0, 1, 0, 6, 0, 1, 1, 1, 3, 0, 2, 1, 15, 0, -117, 0, 0, MakibesHR3Constants.CMD_SET_DATE_TIME, 0, 1, 8, 1, 20, 0, 1, 0, -2, 8, 0, MakibesHR3Constants.CMD_SET_DATE_TIME, 0, 2, 1, 0, -65, PebbleColor.DarkGray, ZeTimeConstants.CMD_GET_STEP_COUNT, PebbleColor.ImperialPurple}),
    FORWARD_TO_PHONE_MULTI("forward to phone (multifunction)", new byte[]{1, 6, 18, 0}, new byte[]{1, 0, 1, 6, 18, 99, 0, 0, 0, 1, 0, 6, 0, 1, 1, 1, 3, 0, 5, 1, 29, 0, MakibesHR3Constants.CMD_85, 1, -10, 0, 0, MakibesHR3Constants.CMD_85, 1, LiveviewConstants.MSG_GETSCREENMODE, 2, 0, MakibesHR3Constants.CMD_85, 1, 67, 3, 0, MakibesHR3Constants.CMD_85, 1, 68, 4, 0, 8, 1, 30, 0, 1, 0, 2, 13, 0, -116, 1, PebbleColor.Malachite, 0, 1, MakibesHR3Constants.CMD_SET_DATE_TIME, 0, 1, 1, 0, 3, 13, 0, -116, 1, HPlusConstants.CMD_FACTORY_RESET, 0, 1, MakibesHR3Constants.CMD_SET_DATE_TIME, 0, 1, 1, 0, 4, 13, 0, -116, 1, -75, 0, 1, MakibesHR3Constants.CMD_SET_DATE_TIME, 0, 1, 1, 0, -2, 8, 0, MakibesHR3Constants.CMD_SET_DATE_TIME, 0, 2, 1, 0, HPlusConstants.INCOMING_CALL_STATE_DISABLED_THRESHOLD, ZeTimeConstants.CMD_GET_SLEEP_DATA, HPlusConstants.CMD_SET_BLOOD, ZeTimeConstants.CMD_REMINDERS}),
    STOPWATCH("stopwatch", new byte[]{2, 1, 32, 1}, new byte[]{1, 0, 2, 1, 32, 32, 0, 0, 0, 1, 1, 7, 0, 3, 0, 0, 7, 1, 0, 1, 1, 8, 0, MakibesHR3Constants.RPRT_SOFTWARE, 0, 1, 1, 0, 15, PebbleColor.Black, 95, 42}),
    DATE("show date", new byte[]{1, 1, 20, 0}, new byte[]{1, 0, 1, 1, 20, HPlusConstants.CMD_SET_GENDER, 0, 0, 0, 1, 0, 6, 0, 2, 0, 0, 7, 0, 1, 1, 22, 0, -119, 5, 1, 7, -80, 0, 0, -80, 0, 0, -80, 0, 0, 8, 1, 80, 0, 1, 0, -48, -119, PebbleColor.MediumAquamarine, 110}),
    LAST_NOTIFICATION("show last notification", new byte[]{1, 1, 24, 0}, new byte[]{1, 0, 1, 1, 24, 47, 0, 0, 0, 1, 0, 8, 0, 4, 0, 0, 7, 2, 1, 0, 1, 1, 22, 0, -119, 5, 1, 7, -80, 2, 0, -80, 2, 0, -80, 2, 0, 8, 1, 80, 0, 1, 0, 107, -99, ZeTimeConstants.CMD_DELETE_SLEEP_DATA, BFH16Constants.CMD_SWITCH_METRIC_IMPERIAL}),
    SECOND_TIMEZONE("show second timezone", new byte[]{1, 1, 22, 0}, new byte[]{1, 0, 1, 1, 22, 47, 0, 0, 0, 1, 0, 8, 0, 4, 0, 0, 7, 2, 2, 0, 1, 1, 22, 0, -119, 5, 1, 7, -80, 1, 0, -80, 1, 0, -80, 1, 0, 8, 1, 80, 0, 1, 0, 61, 7, LiveviewConstants.MSG_SETLED, 1});
    
    private byte[] data;
    private String description;
    private byte[] header;

    public static ConfigPayload fromId(short id) throws RuntimeException {
        for (ConfigPayload payload : values()) {
            ByteBuffer buffer = ByteBuffer.wrap(payload.header);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            if (id == buffer.getShort(1)) {
                return payload;
            }
        }
        throw new RuntimeException("app " + id + " not found");
    }

    public byte[] getHeader() {
        return this.header;
    }

    public byte[] getData() {
        return this.data;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean equals(ConfigPayload p1, ConfigPayload p2) {
        return Arrays.equals(p1.getData(), p2.getData());
    }

    private ConfigPayload(String description2, byte[] header2, byte[] data2) {
        this.description = description2;
        this.header = header2;
        this.data = data2;
    }
}
