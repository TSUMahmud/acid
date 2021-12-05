package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import nodomain.freeyourgadget.gadgetbridge.BuildConfig;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p005ch.qos.logback.core.spi.AbstractComponentTracker;

public class AlarmReceiver extends BroadcastReceiver {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AlarmReceiver.class);

    public AlarmReceiver() {
        Context context = GBApplication.getContext();
        Intent intent = new Intent("DAILY_ALARM");
        intent.setPackage(BuildConfig.APPLICATION_ID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService("alarm");
        if (am != null) {
            am.setInexactRepeating(0, Calendar.getInstance().getTimeInMillis() + AbstractComponentTracker.LINGERING_TIMEOUT, DateUtils.MILLIS_PER_DAY, pendingIntent);
            return;
        }
        LOG.warn("could not get alarm manager!");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x009f, code lost:
        r6 = (android.location.LocationManager) r0.getSystemService("location");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onReceive(android.content.Context r17, android.content.Intent r18) {
        /*
            r16 = this;
            r0 = r17
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r1 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getPrefs()
            r2 = 0
            java.lang.String r3 = "send_sunrise_sunset"
            boolean r1 = r1.getBoolean(r3, r2)
            if (r1 != 0) goto L_0x0017
            org.slf4j.Logger r1 = LOG
            java.lang.String r2 = "won't send sunrise and sunset events (disabled in preferences)"
            r1.info(r2)
            return
        L_0x0017:
            org.slf4j.Logger r1 = LOG
            java.lang.String r3 = "will resend sunrise and sunset events"
            r1.info(r3)
            java.util.GregorianCalendar r1 = new java.util.GregorianCalendar
            java.lang.String r3 = "UTC"
            java.util.TimeZone r3 = java.util.TimeZone.getTimeZone(r3)
            r1.<init>(r3)
            r3 = 10
            r1.set(r3, r2)
            r3 = 12
            r1.set(r3, r2)
            r3 = 13
            r1.set(r3, r2)
            r3 = 14
            r1.set(r3, r2)
            r3 = 5
            r11 = 1
            r1.add(r3, r11)
            long r3 = r1.getTimeInMillis()
            r5 = 86400000(0x5265c00, double:4.2687272E-316)
            long r3 = r3 / r5
            r5 = 3
            long r3 = r3 % r5
            int r4 = (int) r3
            byte r3 = (byte) r4
            nodomain.freeyourgadget.gadgetbridge.model.DeviceService r4 = nodomain.freeyourgadget.gadgetbridge.GBApplication.deviceService()
            long r5 = (long) r3
            r4.onDeleteCalendarEvent(r11, r5)
            nodomain.freeyourgadget.gadgetbridge.model.DeviceService r4 = nodomain.freeyourgadget.gadgetbridge.GBApplication.deviceService()
            long r5 = (long) r3
            r12 = 2
            r4.onDeleteCalendarEvent(r12, r5)
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r13 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getPrefs()
            r4 = 0
            java.lang.String r5 = "location_latitude"
            float r5 = r13.getFloat(r5, r4)
            java.lang.String r6 = "location_longitude"
            float r4 = r13.getFloat(r6, r4)
            org.slf4j.Logger r6 = LOG
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "got longitude/latitude from preferences: "
            r7.append(r8)
            r7.append(r5)
            java.lang.String r8 = "/"
            r7.append(r8)
            r7.append(r4)
            java.lang.String r7 = r7.toString()
            r6.info(r7)
            java.lang.String r6 = "android.permission.ACCESS_COARSE_LOCATION"
            int r6 = androidx.core.app.ActivityCompat.checkSelfPermission(r0, r6)
            if (r6 != 0) goto L_0x00e1
            java.lang.String r6 = "use_updated_location_if_available"
            boolean r6 = r13.getBoolean(r6, r2)
            if (r6 == 0) goto L_0x00e1
            java.lang.String r6 = "location"
            java.lang.Object r6 = r0.getSystemService(r6)
            android.location.LocationManager r6 = (android.location.LocationManager) r6
            android.location.Criteria r7 = new android.location.Criteria
            r7.<init>()
            java.lang.String r9 = r6.getBestProvider(r7, r2)
            if (r9 == 0) goto L_0x00e1
            android.location.Location r10 = r6.getLastKnownLocation(r9)
            if (r10 == 0) goto L_0x00e1
            double r14 = r10.getLatitude()
            float r5 = (float) r14
            double r14 = r10.getLongitude()
            float r4 = (float) r14
            org.slf4j.Logger r14 = LOG
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r12 = "got longitude/latitude from last known location: "
            r15.append(r12)
            r15.append(r5)
            r15.append(r8)
            r15.append(r4)
            java.lang.String r8 = r15.toString()
            r14.info(r8)
            r14 = r4
            r12 = r5
            goto L_0x00e3
        L_0x00e1:
            r14 = r4
            r12 = r5
        L_0x00e3:
            double r5 = (double) r12
            double r7 = (double) r14
            double r9 = net.e175.klaus.solarpositioning.DeltaT.estimate(r1)
            r4 = r1
            java.util.GregorianCalendar[] r4 = net.e175.klaus.solarpositioning.SPA.calculateSunriseTransitSet(r4, r5, r7, r9)
            nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec r5 = new nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec
            r5.<init>()
            r5.durationInSeconds = r2
            r6 = 0
            r5.description = r6
            r5.type = r11
            java.lang.String r6 = "Sunrise"
            r5.title = r6
            r6 = r4[r2]
            r7 = 1000(0x3e8, double:4.94E-321)
            if (r6 == 0) goto L_0x0118
            long r9 = (long) r3
            r5.f156id = r9
            r2 = r4[r2]
            long r9 = r2.getTimeInMillis()
            long r9 = r9 / r7
            int r2 = (int) r9
            r5.timestamp = r2
            nodomain.freeyourgadget.gadgetbridge.model.DeviceService r2 = nodomain.freeyourgadget.gadgetbridge.GBApplication.deviceService()
            r2.onAddCalendarEvent(r5)
        L_0x0118:
            r2 = 2
            r5.type = r2
            java.lang.String r6 = "Sunset"
            r5.title = r6
            r6 = r4[r2]
            if (r6 == 0) goto L_0x0137
            long r9 = (long) r3
            r5.f156id = r9
            r2 = r4[r2]
            long r9 = r2.getTimeInMillis()
            long r9 = r9 / r7
            int r2 = (int) r9
            r5.timestamp = r2
            nodomain.freeyourgadget.gadgetbridge.model.DeviceService r2 = nodomain.freeyourgadget.gadgetbridge.GBApplication.deviceService()
            r2.onAddCalendarEvent(r5)
        L_0x0137:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.externalevents.AlarmReceiver.onReceive(android.content.Context, android.content.Intent):void");
    }
}
