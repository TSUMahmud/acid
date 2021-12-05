package nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitcor;

import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiService;

public class AmazfitCorService {
    public static final byte[] COMMAND_CHANGE_SCREENS = {HuamiService.ENDPOINT_DISPLAY_ITEMS, HuamiService.DISPLAY_ITEM_BIT_CLOCK, 32, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
}
