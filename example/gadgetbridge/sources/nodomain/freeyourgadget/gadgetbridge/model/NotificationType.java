package nodomain.freeyourgadget.gadgetbridge.model;

import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;

public enum NotificationType {
    UNKNOWN(1, PebbleColor.DarkCandyAppleRed),
    AMAZON(111, (byte) -8),
    BBM(58, PebbleColor.DarkGray),
    CONVERSATIONS(77, PebbleColor.Inchworm),
    FACEBOOK(11, PebbleColor.CobaltBlue),
    FACEBOOK_MESSENGER(10, PebbleColor.BlueMoon),
    GENERIC_ALARM_CLOCK(13, (byte) -16),
    GENERIC_CALENDAR(21, PebbleColor.BlueMoon),
    GENERIC_EMAIL(19, PebbleColor.Orange),
    GENERIC_NAVIGATION(82, PebbleColor.Orange),
    GENERIC_PHONE(49, PebbleColor.JaegerGreen),
    GENERIC_SMS(45, PebbleColor.VividViolet),
    GMAIL(9, (byte) -16),
    GOOGLE_HANGOUTS(8, PebbleColor.JaegerGreen),
    GOOGLE_INBOX(61, PebbleColor.BlueMoon),
    GOOGLE_MAPS(112, PebbleColor.BlueMoon),
    GOOGLE_MESSENGER(76, PebbleColor.VividCerulean),
    GOOGLE_PHOTOS(113, PebbleColor.BlueMoon),
    HIPCHAT(77, PebbleColor.CobaltBlue),
    INSTAGRAM(59, PebbleColor.CobaltBlue),
    KAKAO_TALK(79, (byte) -4),
    KIK(80, PebbleColor.IslamicGreen),
    LIGHTHOUSE(81, PebbleColor.PictonBlue),
    LINE(67, PebbleColor.IslamicGreen),
    LINKEDIN(115, PebbleColor.CobaltBlue),
    MAILBOX(60, PebbleColor.VividCerulean),
    OUTLOOK(64, PebbleColor.BlueMoon),
    BUSINESS_CALENDAR(21, PebbleColor.BlueMoon),
    RIOT(77, PebbleColor.LavenderIndigo),
    SIGNAL(77, PebbleColor.BlueMoon),
    SKYPE(68, PebbleColor.VividCerulean),
    SLACK(116, PebbleColor.Folly),
    SNAPCHAT(69, (byte) -3),
    TELEGRAM(7, PebbleColor.VividCerulean),
    THREEMA(77, PebbleColor.JaegerGreen),
    KONTALK(77, PebbleColor.JaegerGreen),
    ANTOX(77, PebbleColor.JaegerGreen),
    TRANSIT(82, PebbleColor.JaegerGreen),
    TWITTER(6, PebbleColor.BlueMoon),
    VIBER(70, PebbleColor.VividViolet),
    WECHAT(71, PebbleColor.KellyGreen),
    WHATSAPP(5, PebbleColor.IslamicGreen),
    YAHOO_MAIL(72, PebbleColor.Indigo);
    
    public final byte color;
    public final int icon;

    private NotificationType(int icon2, byte color2) {
        this.icon = icon2;
        this.color = color2;
    }

    public String getFixedValue() {
        return name().toLowerCase();
    }

    public String getGenericType() {
        switch (this) {
            case GENERIC_EMAIL:
            case GENERIC_NAVIGATION:
            case GENERIC_SMS:
            case GENERIC_ALARM_CLOCK:
                return getFixedValue();
            case FACEBOOK:
            case TWITTER:
            case SNAPCHAT:
            case INSTAGRAM:
            case LINKEDIN:
                return "generic_social";
            case CONVERSATIONS:
            case FACEBOOK_MESSENGER:
            case RIOT:
            case SIGNAL:
            case TELEGRAM:
            case THREEMA:
            case KONTALK:
            case ANTOX:
            case WHATSAPP:
            case GOOGLE_MESSENGER:
            case GOOGLE_HANGOUTS:
            case HIPCHAT:
            case SKYPE:
            case WECHAT:
            case KIK:
            case KAKAO_TALK:
            case SLACK:
            case LINE:
            case VIBER:
                return "generic_chat";
            case GMAIL:
            case GOOGLE_INBOX:
            case MAILBOX:
            case OUTLOOK:
            case YAHOO_MAIL:
                return "generic_email";
            default:
                return "generic";
        }
    }
}
