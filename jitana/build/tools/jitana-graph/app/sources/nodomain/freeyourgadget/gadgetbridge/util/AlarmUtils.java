package nodomain.freeyourgadget.gadgetbridge.util;

import java.util.Calendar;
import java.util.Comparator;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.User;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;

public class AlarmUtils {
    public static Alarm createSingleShot(int index, boolean smartWakeup, boolean snooze, Calendar calendar) {
        Calendar calendar2 = calendar;
        return new nodomain.freeyourgadget.gadgetbridge.entities.Alarm(-1, -1, index, true, smartWakeup, snooze, 0, calendar2.get(11), calendar2.get(12), false);
    }

    public static Calendar toCalendar(Alarm alarm) {
        Calendar result = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        result.set(11, alarm.getHour());
        result.set(12, alarm.getMinute());
        if (now.after(result) && alarm.getRepetition() == 0) {
            result.add(5, 1);
        }
        return result;
    }

    public static int createRepetitionMassk(boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun) {
        int i = 0;
        int i2 = (tue ? 2 : 0) | mon | (wed ? 4 : 0) | (thu ? 8 : 0) | (fri ? 16 : 0) | (sat ? 32 : 0);
        if (sun) {
            i = 64;
        }
        return (int) (i | i2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0051, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0052, code lost:
        if (r3 != null) goto L_0x0054;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x005c, code lost:
        throw r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<nodomain.freeyourgadget.gadgetbridge.entities.Alarm> readAlarmsFromPrefs(nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r10) {
        /*
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getPrefs()
            java.util.HashSet r1 = new java.util.HashSet
            r1.<init>()
            java.lang.String r2 = "mi_alarms"
            java.util.Set r1 = r0.getStringSet(r2, r1)
            java.util.ArrayList r2 = new java.util.ArrayList
            int r3 = r1.size()
            r2.<init>(r3)
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r3 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x005d }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r4 = r3.getDaoSession()     // Catch:{ all -> 0x004f }
            nodomain.freeyourgadget.gadgetbridge.entities.User r5 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getUser(r4)     // Catch:{ all -> 0x004f }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r6 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getDevice(r10, r4)     // Catch:{ all -> 0x004f }
            java.util.Iterator r7 = r1.iterator()     // Catch:{ all -> 0x004f }
        L_0x002c:
            boolean r8 = r7.hasNext()     // Catch:{ all -> 0x004f }
            if (r8 == 0) goto L_0x0041
            java.lang.Object r8 = r7.next()     // Catch:{ all -> 0x004f }
            java.lang.String r8 = (java.lang.String) r8     // Catch:{ all -> 0x004f }
            nodomain.freeyourgadget.gadgetbridge.entities.Alarm r9 = createAlarmFromPreference(r8, r6, r5)     // Catch:{ all -> 0x004f }
            r2.add(r9)     // Catch:{ all -> 0x004f }
            goto L_0x002c
        L_0x0041:
            java.util.Comparator r7 = createComparator()     // Catch:{ all -> 0x004f }
            java.util.Collections.sort(r2, r7)     // Catch:{ all -> 0x004f }
            if (r3 == 0) goto L_0x004e
            r3.close()     // Catch:{ Exception -> 0x005d }
        L_0x004e:
            return r2
        L_0x004f:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x0051 }
        L_0x0051:
            r5 = move-exception
            if (r3 == 0) goto L_0x005c
            r3.close()     // Catch:{ all -> 0x0058 }
            goto L_0x005c
        L_0x0058:
            r6 = move-exception
            r4.addSuppressed(r6)     // Catch:{ Exception -> 0x005d }
        L_0x005c:
            throw r5     // Catch:{ Exception -> 0x005d }
        L_0x005d:
            r3 = move-exception
            r4 = 3
            java.lang.String r5 = "Error accessing database"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.log(r5, r4, r3)
            java.util.List r4 = java.util.Collections.emptyList()
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.util.AlarmUtils.readAlarmsFromPrefs(nodomain.freeyourgadget.gadgetbridge.impl.GBDevice):java.util.List");
    }

    private static nodomain.freeyourgadget.gadgetbridge.entities.Alarm createAlarmFromPreference(String fromPreferences, Device device, User user) {
        String[] tokens = fromPreferences.split(",");
        return new nodomain.freeyourgadget.gadgetbridge.entities.Alarm(device.getId().longValue(), user.getId().longValue(), Integer.parseInt(tokens[0]), Boolean.parseBoolean(tokens[1]), Boolean.parseBoolean(tokens[2]), false, Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), false);
    }

    private static Comparator<nodomain.freeyourgadget.gadgetbridge.entities.Alarm> createComparator() {
        return new Comparator<nodomain.freeyourgadget.gadgetbridge.entities.Alarm>() {
            public int compare(nodomain.freeyourgadget.gadgetbridge.entities.Alarm o1, nodomain.freeyourgadget.gadgetbridge.entities.Alarm o2) {
                return Integer.compare(o1.getPosition(), o2.getPosition());
            }
        };
    }
}
