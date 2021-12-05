package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import cyanogenmod.providers.ThemesContract;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PebbleReceiver extends BroadcastReceiver {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) PebbleReceiver.class);

    public void onReceive(Context context, Intent intent) {
        Prefs prefs = GBApplication.getPrefs();
        if (!"never".equals(prefs.getString("notification_mode_pebblemsg", "when_screen_off"))) {
            if ("when_screen_off".equals(prefs.getString("notification_mode_pebblemsg", "when_screen_off")) && ((PowerManager) context.getSystemService("power")).isScreenOn()) {
                return;
            }
            if (!intent.getStringExtra("messageType").equals("PEBBLE_ALERT")) {
                LOG.info("non PEBBLE_ALERT message type not supported");
            } else if (!intent.hasExtra("notificationData")) {
                LOG.info("missing notificationData extra");
            } else {
                NotificationSpec notificationSpec = new NotificationSpec();
                try {
                    JSONArray notificationJSON = new JSONArray(intent.getStringExtra("notificationData"));
                    notificationSpec.title = notificationJSON.getJSONObject(0).getString(ThemesContract.ThemesColumns.TITLE);
                    notificationSpec.body = notificationJSON.getJSONObject(0).getString("body");
                    if (notificationSpec.title != null) {
                        notificationSpec.type = NotificationType.UNKNOWN;
                        String sender = intent.getStringExtra("sender");
                        if (GBApplication.appIsPebbleBlacklisted(sender)) {
                            Logger logger = LOG;
                            logger.info("Ignoring Pebble message, application " + sender + " is blacklisted");
                            return;
                        }
                        if ("Conversations".equals(sender)) {
                            notificationSpec.type = NotificationType.CONVERSATIONS;
                        } else if ("OsmAnd".equals(sender)) {
                            notificationSpec.type = NotificationType.GENERIC_NAVIGATION;
                        }
                        GBApplication.deviceService().onNotification(notificationSpec);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
