package nodomain.freeyourgadget.gadgetbridge.model;

import java.util.HashMap;

public class AppNotificationType extends HashMap<String, NotificationType> {
    private static AppNotificationType _instance;

    public static AppNotificationType getInstance() {
        AppNotificationType appNotificationType = _instance;
        if (appNotificationType != null) {
            return appNotificationType;
        }
        AppNotificationType appNotificationType2 = new AppNotificationType();
        _instance = appNotificationType2;
        return appNotificationType2;
    }

    private AppNotificationType() {
        put("com.fsck.k9", NotificationType.GENERIC_EMAIL);
        put("com.fsck.k9.material", NotificationType.GENERIC_EMAIL);
        put("com.imaeses.squeaky", NotificationType.GENERIC_EMAIL);
        put("com.android.email", NotificationType.GENERIC_EMAIL);
        put("ch.protonmail.android", NotificationType.GENERIC_EMAIL);
        put("security.pEp", NotificationType.GENERIC_EMAIL);
        put("eu.faircode.email", NotificationType.GENERIC_EMAIL);
        put("com.moez.QKSMS", NotificationType.GENERIC_SMS);
        put("com.android.mms", NotificationType.GENERIC_SMS);
        put("com.android.messaging", NotificationType.GENERIC_SMS);
        put("com.sonyericsson.conversations", NotificationType.GENERIC_SMS);
        put("org.smssecure.smssecure", NotificationType.GENERIC_SMS);
        put("com.android.calendar", NotificationType.GENERIC_CALENDAR);
        put("com.google.android.gm", NotificationType.GMAIL);
        put("com.google.android.apps.inbox", NotificationType.GOOGLE_INBOX);
        put("com.google.android.calendar", NotificationType.GENERIC_CALENDAR);
        put("com.google.android.apps.messaging", NotificationType.GOOGLE_MESSENGER);
        put("com.google.android.talk", NotificationType.GOOGLE_HANGOUTS);
        put("com.google.android.apps.maps", NotificationType.GOOGLE_MAPS);
        put("com.google.android.apps.photos", NotificationType.GOOGLE_PHOTOS);
        put("eu.siacs.conversations", NotificationType.CONVERSATIONS);
        put("de.pixart.messenger", NotificationType.CONVERSATIONS);
        put("im.vector.alpha", NotificationType.RIOT);
        put("org.thoughtcrime.securesms", NotificationType.SIGNAL);
        put("org.telegram.messenger", NotificationType.TELEGRAM);
        put("org.telegram.messenger.beta", NotificationType.TELEGRAM);
        put("org.telegram.plus", NotificationType.TELEGRAM);
        put("org.thunderdog.challegram", NotificationType.TELEGRAM);
        put("ch.threema.app", NotificationType.THREEMA);
        put("org.kontalk", NotificationType.KONTALK);
        put("chat.tox.antox", NotificationType.ANTOX);
        put("org.mariotaku.twidere", NotificationType.TWITTER);
        put("com.twitter.android", NotificationType.TWITTER);
        put("org.andstatus.app", NotificationType.TWITTER);
        put("org.mustard.android", NotificationType.TWITTER);
        put("me.zeeroooo.materialfb", NotificationType.FACEBOOK);
        put("it.rignanese.leo.slimfacebook", NotificationType.FACEBOOK);
        put("me.jakelane.wrapperforfacebook", NotificationType.FACEBOOK);
        put("com.facebook.katana", NotificationType.FACEBOOK);
        put("org.indywidualni.fblite", NotificationType.FACEBOOK);
        put("com.facebook.orca", NotificationType.FACEBOOK_MESSENGER);
        put("com.facebook.mlite", NotificationType.FACEBOOK_MESSENGER);
        put("com.whatsapp", NotificationType.WHATSAPP);
        put("com.hipchat", NotificationType.HIPCHAT);
        put("com.skype.raider", NotificationType.SKYPE);
        put("com.microsoft.office.lync15", NotificationType.SKYPE);
        put("com.mailboxapp", NotificationType.MAILBOX);
        put("com.snapchat.android", NotificationType.SNAPCHAT);
        put("com.tencent.mm", NotificationType.WECHAT);
        put("com.viber.voip", NotificationType.VIBER);
        put("com.instagram.android", NotificationType.INSTAGRAM);
        put("kik.android", NotificationType.KIK);
        put("jp.naver.line.android", NotificationType.LINE);
        put("com.bbm", NotificationType.BBM);
        put("com.microsoft.office.outlook", NotificationType.OUTLOOK);
        put("com.appgenix.bizcal", NotificationType.BUSINESS_CALENDAR);
        put("com.yahoo.mobile.client.android.mail", NotificationType.YAHOO_MAIL);
        put("com.kakao.talk", NotificationType.KAKAO_TALK);
        put("com.amazon.mshop.android.shopping", NotificationType.AMAZON);
        put("com.linkedin.android", NotificationType.LINKEDIN);
        put("com.slack", NotificationType.SLACK);
        put("com.thetransitapp.droid", NotificationType.TRANSIT);
        put("ws.xsoh.etar", NotificationType.GENERIC_CALENDAR);
    }
}
