package nodomain.freeyourgadget.gadgetbridge.adapter;

import android.content.Context;
import java.util.Date;
import nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummary;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityKind;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;

public class ActivitySummariesAdapter extends AbstractItemAdapter<BaseActivitySummary> {
    private final GBDevice device;

    public ActivitySummariesAdapter(Context context, GBDevice device2) {
        super(context);
        this.device = device2;
        loadItems();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0048, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0049, code lost:
        if (r1 != null) goto L_0x004b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0053, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadItems() {
        /*
            r9 = this;
            r0 = 0
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r1 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x0054 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r2 = r1.getDaoSession()     // Catch:{ all -> 0x0046 }
            nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummaryDao r2 = r2.getBaseActivitySummaryDao()     // Catch:{ all -> 0x0046 }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r3 = r9.device     // Catch:{ all -> 0x0046 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r4 = r1.getDaoSession()     // Catch:{ all -> 0x0046 }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r3 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.findDevice(r3, r4)     // Catch:{ all -> 0x0046 }
            de.greenrobot.dao.query.QueryBuilder r4 = r2.queryBuilder()     // Catch:{ all -> 0x0046 }
            de.greenrobot.dao.Property r5 = nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummaryDao.Properties.DeviceId     // Catch:{ all -> 0x0046 }
            java.lang.Long r6 = r3.getId()     // Catch:{ all -> 0x0046 }
            de.greenrobot.dao.query.WhereCondition r5 = r5.mo14989eq(r6)     // Catch:{ all -> 0x0046 }
            de.greenrobot.dao.query.WhereCondition[] r6 = new p008de.greenrobot.dao.query.WhereCondition[r0]     // Catch:{ all -> 0x0046 }
            de.greenrobot.dao.query.QueryBuilder r5 = r4.where(r5, r6)     // Catch:{ all -> 0x0046 }
            r6 = 1
            de.greenrobot.dao.Property[] r7 = new p008de.greenrobot.dao.Property[r6]     // Catch:{ all -> 0x0046 }
            de.greenrobot.dao.Property r8 = nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummaryDao.Properties.StartTime     // Catch:{ all -> 0x0046 }
            r7[r0] = r8     // Catch:{ all -> 0x0046 }
            r5.orderDesc(r7)     // Catch:{ all -> 0x0046 }
            de.greenrobot.dao.query.Query r5 = r4.build()     // Catch:{ all -> 0x0046 }
            java.util.List r5 = r5.list()     // Catch:{ all -> 0x0046 }
            r9.setItems(r5, r6)     // Catch:{ all -> 0x0046 }
            if (r1 == 0) goto L_0x0045
            r1.close()     // Catch:{ Exception -> 0x0054 }
        L_0x0045:
            goto L_0x005b
        L_0x0046:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0048 }
        L_0x0048:
            r3 = move-exception
            if (r1 == 0) goto L_0x0053
            r1.close()     // Catch:{ all -> 0x004f }
            goto L_0x0053
        L_0x004f:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ Exception -> 0x0054 }
        L_0x0053:
            throw r3     // Catch:{ Exception -> 0x0054 }
        L_0x0054:
            r1 = move-exception
            r2 = 3
            java.lang.String r3 = "Error loading activity summaries."
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((java.lang.String) r3, (int) r0, (int) r2, (java.lang.Throwable) r1)
        L_0x005b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.adapter.ActivitySummariesAdapter.loadItems():void");
    }

    /* access modifiers changed from: protected */
    public String getName(BaseActivitySummary item) {
        String name = item.getName();
        if (name != null && name.length() > 0) {
            return name;
        }
        Date startTime = item.getStartTime();
        if (startTime != null) {
            return DateTimeUtils.formatDateTime(startTime);
        }
        return "Unknown activity";
    }

    /* access modifiers changed from: protected */
    public String getDetails(BaseActivitySummary item) {
        return ActivityKind.asString(item.getActivityKind(), getContext());
    }

    /* access modifiers changed from: protected */
    public int getIcon(BaseActivitySummary item) {
        return ActivityKind.getIconId(item.getActivityKind());
    }
}
