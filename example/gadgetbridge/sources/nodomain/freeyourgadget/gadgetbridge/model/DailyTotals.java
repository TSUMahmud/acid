package nodomain.freeyourgadget.gadgetbridge.model;

import android.content.Context;
import java.util.Calendar;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.AbstractActivitySample;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DailyTotals {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DailyTotals.class);

    public int[] getDailyTotalsForAllDevices(Calendar day) {
        Context context = GBApplication.getContext();
        int all_steps = 0;
        int all_sleep = 0;
        if (context instanceof GBApplication) {
            for (GBDevice device : ((GBApplication) context).getDeviceManager().getDevices()) {
                if (DeviceHelper.getInstance().getCoordinator(device).supportsActivityDataFetching()) {
                    int[] all_daily = getDailyTotalsForDevice(device, day);
                    all_steps += all_daily[0];
                    all_sleep += all_daily[1] + all_daily[2];
                }
            }
        }
        Logger logger = LOG;
        logger.debug("gbwidget daily totals, all steps:" + all_steps);
        Logger logger2 = LOG;
        logger2.debug("gbwidget  daily totals, all sleep:" + all_sleep);
        return new int[]{all_steps, all_sleep};
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003b, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003c, code lost:
        if (r2 != null) goto L_0x003e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0046, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int[] getDailyTotalsForDevice(nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r12, java.util.Calendar r13) {
        /*
            r11 = this;
            r0 = 3
            r1 = 0
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r2 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x0047 }
            nodomain.freeyourgadget.gadgetbridge.activities.charts.ActivityAnalysis r3 = new nodomain.freeyourgadget.gadgetbridge.activities.charts.ActivityAnalysis     // Catch:{ all -> 0x0039 }
            r3.<init>()     // Catch:{ all -> 0x0039 }
            java.util.List r4 = r11.getSamplesOfDay(r2, r13, r1, r12)     // Catch:{ all -> 0x0039 }
            nodomain.freeyourgadget.gadgetbridge.model.ActivityAmounts r4 = r3.calculateActivityAmounts(r4)     // Catch:{ all -> 0x0039 }
            r5 = -12
            java.util.List r5 = r11.getSamplesOfDay(r2, r13, r5, r12)     // Catch:{ all -> 0x0039 }
            nodomain.freeyourgadget.gadgetbridge.model.ActivityAmounts r5 = r3.calculateActivityAmounts(r5)     // Catch:{ all -> 0x0039 }
            int[] r6 = r11.getTotalsSleepForActivityAmounts(r5)     // Catch:{ all -> 0x0039 }
            int r7 = r11.getTotalsStepsForActivityAmounts(r4)     // Catch:{ all -> 0x0039 }
            int[] r8 = new int[r0]     // Catch:{ all -> 0x0039 }
            r8[r1] = r7     // Catch:{ all -> 0x0039 }
            r9 = r6[r1]     // Catch:{ all -> 0x0039 }
            r10 = 1
            r8[r10] = r9     // Catch:{ all -> 0x0039 }
            r9 = 2
            r10 = r6[r10]     // Catch:{ all -> 0x0039 }
            r8[r9] = r10     // Catch:{ all -> 0x0039 }
            if (r2 == 0) goto L_0x0038
            r2.close()     // Catch:{ Exception -> 0x0047 }
        L_0x0038:
            return r8
        L_0x0039:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x003b }
        L_0x003b:
            r4 = move-exception
            if (r2 == 0) goto L_0x0046
            r2.close()     // Catch:{ all -> 0x0042 }
            goto L_0x0046
        L_0x0042:
            r5 = move-exception
            r3.addSuppressed(r5)     // Catch:{ Exception -> 0x0047 }
        L_0x0046:
            throw r4     // Catch:{ Exception -> 0x0047 }
        L_0x0047:
            r2 = move-exception
            java.lang.String r3 = "Error loading activity summaries."
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((java.lang.String) r3, (int) r1, (int) r0, (java.lang.Throwable) r2)
            int[] r0 = new int[r0]
            r0 = {0, 0, 0} // fill-array
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.model.DailyTotals.getDailyTotalsForDevice(nodomain.freeyourgadget.gadgetbridge.impl.GBDevice, java.util.Calendar):int[]");
    }

    private int[] getTotalsSleepForActivityAmounts(ActivityAmounts activityAmounts) {
        long totalSecondsDeepSleep = 0;
        long totalSecondsLightSleep = 0;
        for (ActivityAmount amount : activityAmounts.getAmounts()) {
            if (amount.getActivityKind() == 4) {
                totalSecondsDeepSleep += amount.getTotalSeconds();
            } else if (amount.getActivityKind() == 2) {
                totalSecondsLightSleep += amount.getTotalSeconds();
            }
        }
        return new int[]{(int) (totalSecondsDeepSleep / 60), (int) (totalSecondsLightSleep / 60)};
    }

    private int getTotalsStepsForActivityAmounts(ActivityAmounts activityAmounts) {
        int totalSteps = 0;
        for (ActivityAmount amount : activityAmounts.getAmounts()) {
            totalSteps = (int) (((long) totalSteps) + amount.getTotalSteps());
        }
        return totalSteps;
    }

    private List<? extends ActivitySample> getSamplesOfDay(DBHandler db, Calendar day, int offsetHours, GBDevice device) {
        Calendar day2 = (Calendar) day.clone();
        day2.set(11, 0);
        day2.set(12, 0);
        day2.set(13, 0);
        day2.add(10, offsetHours);
        int startTs = (int) (day2.getTimeInMillis() / 1000);
        return getSamples(db, device, startTs, (86400 + startTs) - 1);
    }

    /* access modifiers changed from: protected */
    public List<? extends ActivitySample> getSamples(DBHandler db, GBDevice device, int tsFrom, int tsTo) {
        return getAllSamples(db, device, tsFrom, tsTo);
    }

    /* access modifiers changed from: protected */
    public SampleProvider<? extends AbstractActivitySample> getProvider(DBHandler db, GBDevice device) {
        return DeviceHelper.getInstance().getCoordinator(device).getSampleProvider(device, db.getDaoSession());
    }

    /* access modifiers changed from: protected */
    public List<? extends ActivitySample> getAllSamples(DBHandler db, GBDevice device, int tsFrom, int tsTo) {
        return getProvider(db, device).getAllActivitySamples(tsFrom, tsTo);
    }
}
