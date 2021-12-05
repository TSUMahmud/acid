package nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip;

import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiService;

public class AmazfitBipService {
    public static final byte[] COMMAND_ACK_FIND_PHONE_IN_PROGRESS = {HuamiService.ENDPOINT_DISPLAY, 20, 0, 0};
    public static final byte COMMAND_ACTIVITY_DATA_TYPE_DEBUGLOGS = 7;
    public static final byte COMMAND_ACTIVITY_DATA_TYPE_SPORTS_DETAILS = 6;
    public static final byte COMMAND_ACTIVITY_DATA_TYPE_SPORTS_SUMMARIES = 5;
    public static final byte[] COMMAND_CHANGE_SCREENS = {HuamiService.ENDPOINT_DISPLAY_ITEMS, HuamiService.DISPLAY_ITEM_BIT_CLOCK, 16, 0, 1, 2, 3, 4, 5, 6, 7, 8};
    public static final byte[] COMMAND_SET_LANGUAGE_ENGLISH = {HuamiService.ENDPOINT_DISPLAY, 19, 0, 2};
    public static final byte[] COMMAND_SET_LANGUAGE_SIMPLIFIED_CHINESE = {HuamiService.ENDPOINT_DISPLAY, 19, 0, 0};
    public static final byte[] COMMAND_SET_LANGUAGE_SPANISH = {HuamiService.ENDPOINT_DISPLAY, 19, 0, 3};
    public static final byte[] COMMAND_SET_LANGUAGE_TRADITIONAL_CHINESE = {HuamiService.ENDPOINT_DISPLAY, 19, 0, 1};
    public static final UUID UUID_CHARACTERISTIC_WEATHER = UUID.fromString("0000000e-0000-3512-2118-0009af100700");
}
