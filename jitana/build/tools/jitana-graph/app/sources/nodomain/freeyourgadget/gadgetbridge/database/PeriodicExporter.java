package nodomain.freeyourgadget.gadgetbridge.database;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.util.GBPrefs;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeriodicExporter extends BroadcastReceiver {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) PeriodicExporter.class);

    public static void enablePeriodicExport(Context context) {
        Prefs prefs = GBApplication.getPrefs();
        sheduleAlarm(context, Integer.valueOf(prefs.getInt(GBPrefs.AUTO_EXPORT_INTERVAL, 0)), prefs.getBoolean(GBPrefs.AUTO_EXPORT_ENABLED, false));
    }

    public static void sheduleAlarm(Context context, Integer autoExportInterval, boolean autoExportEnabled) {
        int exportPeriod;
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(context, PeriodicExporter.class), 0);
        AlarmManager am = (AlarmManager) context.getSystemService("alarm");
        am.cancel(pi);
        if (autoExportEnabled && (exportPeriod = autoExportInterval.intValue() * 60 * 60 * 1000) != 0) {
            LOG.info("Enabling periodic export");
            am.setInexactRepeating(3, SystemClock.elapsedRealtime() + ((long) exportPeriod), (long) exportPeriod, pi);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0046, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0047, code lost:
        if (r4 != null) goto L_0x0049;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x004d, code lost:
        r7 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        r5.addSuppressed(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0051, code lost:
        throw r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0054, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0055, code lost:
        if (r0 != null) goto L_0x0057;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x005f, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onReceive(android.content.Context r9, android.content.Intent r10) {
        /*
            r8 = this;
            org.slf4j.Logger r0 = LOG
            java.lang.String r1 = "Exporting DB"
            r0.info(r1)
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x0060 }
            nodomain.freeyourgadget.gadgetbridge.database.DBHelper r1 = new nodomain.freeyourgadget.gadgetbridge.database.DBHelper     // Catch:{ all -> 0x0052 }
            r1.<init>(r9)     // Catch:{ all -> 0x0052 }
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r2 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getPrefs()     // Catch:{ all -> 0x0052 }
            java.lang.String r3 = "auto_export_location"
            r4 = 0
            java.lang.String r2 = r2.getString(r3, r4)     // Catch:{ all -> 0x0052 }
            if (r2 != 0) goto L_0x002a
            org.slf4j.Logger r3 = LOG     // Catch:{ all -> 0x0052 }
            java.lang.String r4 = "Unable to export DB, export location not set"
            r3.info(r4)     // Catch:{ all -> 0x0052 }
            if (r0 == 0) goto L_0x0029
            r0.close()     // Catch:{ Exception -> 0x0060 }
        L_0x0029:
            return
        L_0x002a:
            android.net.Uri r3 = android.net.Uri.parse(r2)     // Catch:{ all -> 0x0052 }
            android.content.ContentResolver r4 = r9.getContentResolver()     // Catch:{ all -> 0x0052 }
            java.io.OutputStream r4 = r4.openOutputStream(r3)     // Catch:{ all -> 0x0052 }
            r1.exportDB((nodomain.freeyourgadget.gadgetbridge.database.DBHandler) r0, (java.io.OutputStream) r4)     // Catch:{ all -> 0x0044 }
            if (r4 == 0) goto L_0x003e
            r4.close()     // Catch:{ all -> 0x0052 }
        L_0x003e:
            if (r0 == 0) goto L_0x0043
            r0.close()     // Catch:{ Exception -> 0x0060 }
        L_0x0043:
            goto L_0x0072
        L_0x0044:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x0046 }
        L_0x0046:
            r6 = move-exception
            if (r4 == 0) goto L_0x0051
            r4.close()     // Catch:{ all -> 0x004d }
            goto L_0x0051
        L_0x004d:
            r7 = move-exception
            r5.addSuppressed(r7)     // Catch:{ all -> 0x0052 }
        L_0x0051:
            throw r6     // Catch:{ all -> 0x0052 }
        L_0x0052:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0054 }
        L_0x0054:
            r2 = move-exception
            if (r0 == 0) goto L_0x005f
            r0.close()     // Catch:{ all -> 0x005b }
            goto L_0x005f
        L_0x005b:
            r3 = move-exception
            r1.addSuppressed(r3)     // Catch:{ Exception -> 0x0060 }
        L_0x005f:
            throw r2     // Catch:{ Exception -> 0x0060 }
        L_0x0060:
            r0 = move-exception
            r1 = 2131755451(0x7f1001bb, float:1.9141782E38)
            java.lang.String r1 = r9.getString(r1)
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.updateExportFailedNotification(r1, r9)
            org.slf4j.Logger r1 = LOG
            java.lang.String r2 = "Exception while exporting DB: "
            r1.info((java.lang.String) r2, (java.lang.Throwable) r0)
        L_0x0072:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.database.PeriodicExporter.onReceive(android.content.Context, android.content.Intent):void");
    }
}
