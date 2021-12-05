package nodomain.freeyourgadget.gadgetbridge.service.devices.huami;

import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;

public class HuamiIcon {
    public static final byte ALARM_CLOCK = 10;
    public static final byte APP_11 = 11;
    public static final byte APP_18 = 18;
    public static final byte CALENDAR = 21;
    public static final byte CHAT_BLUE_13 = 13;
    public static final byte CHINESE_15 = 15;
    public static final byte CHINESE_16 = 16;
    public static final byte CHINESE_19 = 19;
    public static final byte CHINESE_20 = 20;
    public static final byte CHINESE_32 = 32;
    public static final byte CHINESE_33 = 33;
    public static final byte CHINESE_9 = 9;
    public static final byte COW_14 = 14;
    public static final byte EMAIL = 34;
    public static final byte FACEBOOK = 3;
    public static final byte FACEBOOK_MESSENGER = 22;
    public static final byte HANGOUTS = 30;
    public static final byte HR_WARNING_36 = 36;
    public static final byte INSTAGRAM = 12;
    public static final byte KAKAOTALK = 26;
    public static final byte LINE = 24;
    public static final byte MI_31 = 31;
    public static final byte MI_APP_5 = 5;
    public static final byte MI_CHAT_2 = 2;
    public static final byte PENGUIN_1 = 1;
    public static final byte POKEMONGO = 29;
    public static final byte RED_WHITE_FIRE_8 = 8;
    public static final byte SKYPE = 27;
    public static final byte SNAPCHAT = 6;
    public static final byte STAR_17 = 17;
    public static final byte TELEGRAM = 25;
    public static final byte TWITTER = 4;
    public static final byte VIBER = 23;
    public static final byte VKONTAKTE = 28;
    public static final byte WEATHER = 35;
    public static final byte WECHAT = 0;
    public static final byte WHATSAPP = 7;

    public static byte mapToIconId(NotificationType type) {
        switch (type) {
            case UNKNOWN:
                return 11;
            case CONVERSATIONS:
            case RIOT:
            case HIPCHAT:
            case KONTALK:
            case ANTOX:
                return 0;
            case GENERIC_EMAIL:
            case GMAIL:
            case YAHOO_MAIL:
            case OUTLOOK:
                return 34;
            case GENERIC_NAVIGATION:
                return 11;
            case GENERIC_SMS:
                return 0;
            case GENERIC_CALENDAR:
            case BUSINESS_CALENDAR:
                return 21;
            case FACEBOOK:
                return 3;
            case FACEBOOK_MESSENGER:
                return 22;
            case GOOGLE_HANGOUTS:
            case GOOGLE_MESSENGER:
                return 30;
            case INSTAGRAM:
            case GOOGLE_PHOTOS:
                return 12;
            case KAKAO_TALK:
                return 26;
            case LINE:
                return 24;
            case SIGNAL:
                return 13;
            case TWITTER:
                return 4;
            case SKYPE:
                return 27;
            case SNAPCHAT:
                return 6;
            case TELEGRAM:
                return 25;
            case THREEMA:
                return 13;
            case VIBER:
                return 23;
            case WECHAT:
                return 0;
            case WHATSAPP:
                return 7;
            case GENERIC_ALARM_CLOCK:
                return 10;
            default:
                return 11;
        }
    }
}
