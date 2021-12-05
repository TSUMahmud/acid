package nodomain.freeyourgadget.gadgetbridge.database;

import android.content.Context;
import android.os.AsyncTask;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;

public abstract class DBAccess extends AsyncTask {
    private final Context mContext;
    private Exception mError;
    private final String mTask;

    /* access modifiers changed from: protected */
    public abstract void doInBackground(DBHandler dBHandler);

    public DBAccess(String task, Context context) {
        this.mTask = task;
        this.mContext = context;
    }

    public Context getContext() {
        return this.mContext;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x000f, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0010, code lost:
        if (r0 != null) goto L_0x0012;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x001a, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object doInBackground(java.lang.Object[] r5) {
        /*
            r4 = this;
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x001b }
            r4.doInBackground((nodomain.freeyourgadget.gadgetbridge.database.DBHandler) r0)     // Catch:{ all -> 0x000d }
            if (r0 == 0) goto L_0x000c
            r0.close()     // Catch:{ Exception -> 0x001b }
        L_0x000c:
            goto L_0x001e
        L_0x000d:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x000f }
        L_0x000f:
            r2 = move-exception
            if (r0 == 0) goto L_0x001a
            r0.close()     // Catch:{ all -> 0x0016 }
            goto L_0x001a
        L_0x0016:
            r3 = move-exception
            r1.addSuppressed(r3)     // Catch:{ Exception -> 0x001b }
        L_0x001a:
            throw r2     // Catch:{ Exception -> 0x001b }
        L_0x001b:
            r0 = move-exception
            r4.mError = r0
        L_0x001e:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.database.DBAccess.doInBackground(java.lang.Object[]):java.lang.Object");
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(Object o) {
        Exception exc = this.mError;
        if (exc != null) {
            displayError(exc);
        }
    }

    /* access modifiers changed from: protected */
    public void displayError(Throwable error) {
        C1238GB.toast(getContext(), getContext().getString(C0889R.string.dbaccess_error_executing, new Object[]{error.getMessage()}), 1, 3, error);
    }
}
