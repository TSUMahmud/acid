package nodomain.freeyourgadget.gadgetbridge.devices.huami.miband3;

import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiService;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;

public class MiBand3Service {
    public static final byte[] COMMAND_CHANGE_SCREENS = {HuamiService.ENDPOINT_DISPLAY_ITEMS, HuamiService.DISPLAY_ITEM_BIT_CLOCK, ZeTimeConstants.CMD_USER_INFO, 0, 0, 0, 6, 0, 0, 0, 0, 0};
    public static final byte[] COMMAND_DISABLE_BAND_SCREEN_UNLOCK = {HuamiService.ENDPOINT_DISPLAY, 22, 0, 0};
    public static final byte[] COMMAND_ENABLE_BAND_SCREEN_UNLOCK = {HuamiService.ENDPOINT_DISPLAY, 22, 0, 1};
    public static final byte[] COMMAND_NIGHT_MODE_OFF = {26, 0};
    public static final byte[] COMMAND_NIGHT_MODE_SCHEDULED = {26, 1, 16, 0, 7, 0};
    public static final byte[] COMMAND_NIGHT_MODE_SUNSET = {26, 2};
}
