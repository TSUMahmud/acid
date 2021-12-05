package nodomain.freeyourgadget.gadgetbridge.service.btle;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.AlertCategory;
import p005ch.qos.logback.core.CoreConstants;

public class BLETypeConversions {
    public static final int TZ_FLAG_INCLUDE_DST_IN_TZ = 1;
    public static final int TZ_FLAG_NONE = 0;

    public static byte[] calendarToRawBytes(Calendar timestamp) {
        byte[] year = fromUint16(timestamp.get(1));
        return new byte[]{year[0], year[1], fromUint8(timestamp.get(2) + 1), fromUint8(timestamp.get(5)), fromUint8(timestamp.get(11)), fromUint8(timestamp.get(12)), fromUint8(timestamp.get(13)), dayOfWeekToRawBytes(timestamp), 0};
    }

    public static byte[] shortCalendarToRawBytes(Calendar timestamp) {
        byte[] year = fromUint16(timestamp.get(1));
        return new byte[]{year[0], year[1], fromUint8(timestamp.get(2) + 1), fromUint8(timestamp.get(5)), fromUint8(timestamp.get(11)), fromUint8(timestamp.get(12))};
    }

    private static int getMiBand2TimeZone(int rawOffset) {
        int offsetMinutes = (rawOffset / 1000) / 60;
        int rawOffset2 = offsetMinutes < 0 ? -1 : 1;
        int offsetMinutes2 = Math.abs(offsetMinutes);
        return rawOffset2 * (((offsetMinutes2 % 60) / 15) + ((offsetMinutes2 / 60) * 4));
    }

    private static byte dayOfWeekToRawBytes(Calendar cal) {
        int calValue = cal.get(7);
        if (calValue != 1) {
            return (byte) (calValue - 1);
        }
        return 7;
    }

    public static GregorianCalendar rawBytesToCalendar(byte[] value) {
        if (value.length < 7) {
            return createCalendar();
        }
        GregorianCalendar timestamp = new GregorianCalendar(toUint16(value[0], value[1]), (value[2] & 255) - 1, value[3] & 255, value[4] & 255, value[5] & 255, value[6] & 255);
        if (value.length > 7) {
            TimeZone timeZone = TimeZone.getDefault();
            timeZone.setRawOffset(value[7] * 15 * 60 * 1000);
            timestamp.setTimeZone(timeZone);
        }
        return timestamp;
    }

    public static long toUnsigned(int unsignedInt) {
        return ((long) unsignedInt) & 4294967295L;
    }

    public static int toUnsigned(short value) {
        return 65535 & value;
    }

    public static int toUnsigned(byte value) {
        return value & 255;
    }

    public static int toUint16(byte value) {
        return toUnsigned(value);
    }

    public static int toUint16(byte... bytes) {
        return (bytes[0] & 255) | ((bytes[1] & 255) << 8);
    }

    public static int toInt16(byte... bytes) {
        return (short) ((bytes[0] & 255) | ((bytes[1] & 255) << 8));
    }

    public static int toUint32(byte... bytes) {
        return (bytes[0] & 255) | ((bytes[1] & 255) << 8) | ((bytes[2] & 255) << 16) | ((bytes[3] & 255) << 24);
    }

    public static byte[] fromUint16(int value) {
        return new byte[]{(byte) (value & 255), (byte) ((value >> 8) & 255)};
    }

    public static byte[] fromUint24(int value) {
        return new byte[]{(byte) (value & 255), (byte) ((value >> 8) & 255), (byte) ((value >> 16) & 255)};
    }

    public static byte[] fromUint32(int value) {
        return new byte[]{(byte) (value & 255), (byte) ((value >> 8) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 24) & 255)};
    }

    public static byte fromUint8(int value) {
        return (byte) (value & 255);
    }

    public static GregorianCalendar createCalendar() {
        return new GregorianCalendar();
    }

    public static byte[] join(byte[] start, byte[] end) {
        if (start == null || start.length == 0) {
            return end;
        }
        if (end == null || end.length == 0) {
            return start;
        }
        byte[] result = new byte[(start.length + end.length)];
        System.arraycopy(start, 0, result, 0, start.length);
        System.arraycopy(end, 0, result, start.length, end.length);
        return result;
    }

    public static byte[] calendarToLocalTimeBytes(GregorianCalendar now) {
        return new byte[]{mapTimeZone(now.getTimeZone()), mapDstOffset(now)};
    }

    public static byte mapTimeZone(TimeZone timeZone) {
        return mapTimeZone(timeZone, 0);
    }

    public static byte mapTimeZone(TimeZone timeZone, int timezoneFlags) {
        return (byte) ((timeZone.getRawOffset() / CoreConstants.MILLIS_IN_ONE_HOUR) * 4);
    }

    public static byte mapDstOffset(Calendar now) {
        TimeZone timeZone = now.getTimeZone();
        int dstSavings = timeZone.getDSTSavings();
        if (dstSavings == 0 || !timeZone.inDaylightTime(now.getTime())) {
            return 0;
        }
        int dstInMinutes = dstSavings / CoreConstants.MILLIS_IN_ONE_MINUTE;
        if (dstInMinutes == 30) {
            return 2;
        }
        if (dstInMinutes == 60) {
            return 4;
        }
        if (dstInMinutes != 120) {
            return fromUint8(255);
        }
        return 8;
    }

    public static byte[] toUtf8s(String message) {
        return message.getBytes(StandardCharsets.UTF_8);
    }

    public static AlertCategory toAlertCategory(NotificationType type) {
        switch (type) {
            case GENERIC_ALARM_CLOCK:
                return AlertCategory.HighPriorityAlert;
            case GENERIC_SMS:
                return AlertCategory.SMS;
            case GENERIC_EMAIL:
            case GMAIL:
            case OUTLOOK:
            case YAHOO_MAIL:
                return AlertCategory.Email;
            case GENERIC_NAVIGATION:
                return AlertCategory.Simple;
            case CONVERSATIONS:
            case FACEBOOK_MESSENGER:
            case GOOGLE_MESSENGER:
            case GOOGLE_HANGOUTS:
            case HIPCHAT:
            case KAKAO_TALK:
            case LINE:
            case RIOT:
            case SIGNAL:
            case SKYPE:
            case SNAPCHAT:
            case TELEGRAM:
            case THREEMA:
            case KONTALK:
            case ANTOX:
            case TWITTER:
            case WHATSAPP:
            case VIBER:
            case WECHAT:
                return AlertCategory.InstantMessage;
            default:
                return AlertCategory.Simple;
        }
    }
}
