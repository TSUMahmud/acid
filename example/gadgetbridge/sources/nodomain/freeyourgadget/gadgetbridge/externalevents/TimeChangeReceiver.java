package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Date;
import java.util.GregorianCalendar;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeChangeReceiver extends BroadcastReceiver {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) TimeChangeReceiver.class);

    public void onReceive(Context context, Intent intent) {
        Prefs prefs = GBApplication.getPrefs();
        String action = intent.getAction();
        if (!prefs.getBoolean("datetime_synconconnect", true)) {
            return;
        }
        if (action.equals("android.intent.action.TIME_SET") || action.equals("android.intent.action.TIMEZONE_CHANGED")) {
            Date newTime = GregorianCalendar.getInstance().getTime();
            Logger logger = LOG;
            logger.info("Time or Timezone changed, syncing with device: " + DateTimeUtils.formatDate(newTime) + " (" + newTime.toGMTString() + "), " + intent.getAction());
            GBApplication.deviceService().onSetTime();
        }
    }
}
