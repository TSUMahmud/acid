package nodomain.freeyourgadget.gadgetbridge.util;

import android.content.Context;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import org.apache.commons.lang3.StringUtils;

public class NotificationUtils {
    public static String getPreferredTextFor(NotificationSpec notificationSpec, int lengthBody, int lengthSubject, Context context) {
        switch (notificationSpec.type) {
            case GENERIC_ALARM_CLOCK:
                return StringUtils.getFirstOf(notificationSpec.title, notificationSpec.subject);
            case GENERIC_SMS:
            case GENERIC_EMAIL:
                return formatText(notificationSpec.sender, notificationSpec.subject, notificationSpec.body, lengthBody, lengthSubject, context);
            case GENERIC_NAVIGATION:
                return StringUtils.getFirstOf(notificationSpec.title, notificationSpec.body);
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
                return StringUtils.ensureNotNull(notificationSpec.body);
            default:
                return "";
        }
    }

    public static String formatSender(String sender, Context context) {
        if (sender == null || sender.length() == 0) {
            return "";
        }
        return context.getString(C0889R.string.StringUtils_sender, new Object[]{sender});
    }

    public static String formatText(String sender, String subject, String body, int lengthBody, int lengthSubject, Context context) {
        return StringUtils.join(StringUtils.SPACE, StringUtils.truncate(body, lengthBody), StringUtils.truncate(subject, lengthSubject), formatSender(sender, context)).toString().trim();
    }

    public static String getPreferredTextFor(CallSpec callSpec) {
        return StringUtils.getFirstOf(callSpec.name, callSpec.number);
    }
}
