package nodomain.freeyourgadget.gadgetbridge.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.core.app.NavUtils;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.database.PeriodicExporter;
import nodomain.freeyourgadget.gadgetbridge.util.AndroidUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.FileUtils;
import nodomain.freeyourgadget.gadgetbridge.util.GBPrefs;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbManagementActivity extends AbstractGBActivity {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DbManagementActivity.class);
    private static SharedPreferences sharedPrefs;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_db_management);
        TextView dbPath = (TextView) findViewById(C0889R.C0891id.activity_db_management_path);
        dbPath.setText(getExternalPath());
        Button exportDBButton = (Button) findViewById(C0889R.C0891id.exportDBButton);
        exportDBButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DbManagementActivity.this.exportDB();
            }
        });
        ((Button) findViewById(C0889R.C0891id.importDBButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DbManagementActivity.this.importDB();
            }
        });
        int testExportVisibility = 8;
        int oldDBVisibility = hasOldActivityDatabase() ? 0 : 8;
        ((TextView) findViewById(C0889R.C0891id.mergeOldActivityDataTitle)).setVisibility(oldDBVisibility);
        Button deleteOldActivityDBButton = (Button) findViewById(C0889R.C0891id.deleteOldActivityDB);
        deleteOldActivityDBButton.setVisibility(oldDBVisibility);
        deleteOldActivityDBButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DbManagementActivity.this.deleteOldActivityDbFile();
            }
        });
        ((Button) findViewById(C0889R.C0891id.emptyDBButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DbManagementActivity.this.deleteActivityDatabase();
            }
        });
        Prefs prefs = GBApplication.getPrefs();
        boolean autoExportEnabled = prefs.getBoolean(GBPrefs.AUTO_EXPORT_ENABLED, false);
        if (prefs.getInt(GBPrefs.AUTO_EXPORT_INTERVAL, 0) > 0 && autoExportEnabled) {
            testExportVisibility = 0;
        }
        ((TextView) findViewById(C0889R.C0891id.autoExportLocation_label)).setVisibility(testExportVisibility);
        ((TextView) findViewById(C0889R.C0891id.autoExportLocation_intro)).setVisibility(testExportVisibility);
        TextView autoExportLocation_path = (TextView) findViewById(C0889R.C0891id.autoExportLocation_path);
        autoExportLocation_path.setVisibility(testExportVisibility);
        autoExportLocation_path.setText(getAutoExportLocationSummary());
        final Context context = getApplicationContext();
        TextView textView = dbPath;
        Button testExportDBButton = (Button) findViewById(C0889R.C0891id.testExportDBButton);
        testExportDBButton.setVisibility(testExportVisibility);
        Button button = exportDBButton;
        testExportDBButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DbManagementActivity.this.sendBroadcast(new Intent(context, PeriodicExporter.class));
                Context context = context;
                C1238GB.toast(context, context.getString(C0889R.string.activity_DB_test_export_message), 0, 1);
            }
        });
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private String getAutoExportLocationSummary() {
        String autoExportLocation = GBApplication.getPrefs().getString(GBPrefs.AUTO_EXPORT_LOCATION, (String) null);
        if (autoExportLocation == null) {
            return "";
        }
        Uri uri = Uri.parse(autoExportLocation);
        try {
            return AndroidUtils.getFilePath(getApplicationContext(), uri);
        } catch (IllegalArgumentException e) {
            IllegalArgumentException illegalArgumentException = e;
            try {
                Cursor cursor = getContentResolver().query(uri, new String[]{"_display_name"}, (String) null, (String[]) null, (String) null, (CancellationSignal) null);
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex("_display_name"));
                }
            } catch (Exception e2) {
                LOG.warn("fuck");
            }
            return "";
        }
    }

    private boolean hasOldActivityDatabase() {
        return new DBHelper(this).existsDB("ActivityDatabase");
    }

    private String getExternalPath() {
        try {
            return FileUtils.getExternalFilesDir().getAbsolutePath();
        } catch (Exception ex) {
            LOG.warn("Unable to get external files dir", (Throwable) ex);
            return getString(C0889R.string.dbmanagementactivvity_cannot_access_export_path);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x007c, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x007d, code lost:
        if (r3 != null) goto L_0x007f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0087, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void exportShared() {
        /*
            r12 = this;
            r0 = 0
            r1 = 3
            r2 = 0
            java.io.File r3 = nodomain.freeyourgadget.gadgetbridge.util.FileUtils.getExternalFilesDir()     // Catch:{ IOException -> 0x0014 }
            java.io.File r4 = new java.io.File     // Catch:{ IOException -> 0x0014 }
            java.lang.String r5 = "Export_preference"
            r4.<init>(r3, r5)     // Catch:{ IOException -> 0x0014 }
            android.content.SharedPreferences r5 = sharedPrefs     // Catch:{ IOException -> 0x0014 }
            nodomain.freeyourgadget.gadgetbridge.util.ImportExportSharedPreferences.exportToFile(r5, r4, r0)     // Catch:{ IOException -> 0x0014 }
            goto L_0x0028
        L_0x0014:
            r3 = move-exception
            r4 = 2131755200(0x7f1000c0, float:1.9141273E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r7 = r3.getMessage()
            r6[r2] = r7
            java.lang.String r4 = r12.getString(r4, r6)
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r12, r4, r5, r1, r3)
        L_0x0028:
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r3 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x0088 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r4 = r3.getDaoSession()     // Catch:{ all -> 0x007a }
            java.util.List r4 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getActiveDevices(r4)     // Catch:{ all -> 0x007a }
            java.util.Iterator r5 = r4.iterator()     // Catch:{ all -> 0x007a }
        L_0x0038:
            boolean r6 = r5.hasNext()     // Catch:{ all -> 0x007a }
            if (r6 == 0) goto L_0x0074
            java.lang.Object r6 = r5.next()     // Catch:{ all -> 0x007a }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r6 = (nodomain.freeyourgadget.gadgetbridge.entities.Device) r6     // Catch:{ all -> 0x007a }
            java.lang.String r7 = r6.getIdentifier()     // Catch:{ all -> 0x007a }
            android.content.SharedPreferences r7 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getDeviceSpecificSharedPrefs(r7)     // Catch:{ all -> 0x007a }
            android.content.SharedPreferences r8 = sharedPrefs     // Catch:{ all -> 0x007a }
            if (r8 == 0) goto L_0x0073
            java.io.File r8 = nodomain.freeyourgadget.gadgetbridge.util.FileUtils.getExternalFilesDir()     // Catch:{ all -> 0x007a }
            java.io.File r9 = new java.io.File     // Catch:{ all -> 0x007a }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x007a }
            r10.<init>()     // Catch:{ all -> 0x007a }
            java.lang.String r11 = "Export_preference_"
            r10.append(r11)     // Catch:{ all -> 0x007a }
            java.lang.String r11 = r6.getIdentifier()     // Catch:{ all -> 0x007a }
            r10.append(r11)     // Catch:{ all -> 0x007a }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x007a }
            r9.<init>(r8, r10)     // Catch:{ all -> 0x007a }
            nodomain.freeyourgadget.gadgetbridge.util.ImportExportSharedPreferences.exportToFile(r7, r9, r0)     // Catch:{ Exception -> 0x0072 }
            goto L_0x0073
        L_0x0072:
            r10 = move-exception
        L_0x0073:
            goto L_0x0038
        L_0x0074:
            if (r3 == 0) goto L_0x0079
            r3.close()     // Catch:{ Exception -> 0x0088 }
        L_0x0079:
            goto L_0x008e
        L_0x007a:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x007c }
        L_0x007c:
            r4 = move-exception
            if (r3 == 0) goto L_0x0087
            r3.close()     // Catch:{ all -> 0x0083 }
            goto L_0x0087
        L_0x0083:
            r5 = move-exception
            r0.addSuppressed(r5)     // Catch:{ Exception -> 0x0088 }
        L_0x0087:
            throw r4     // Catch:{ Exception -> 0x0088 }
        L_0x0088:
            r0 = move-exception
            java.lang.String r3 = "Error exporting device specific preferences"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r3, r2, r1)
        L_0x008e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity.exportShared():void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x007c, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x007d, code lost:
        if (r2 != null) goto L_0x007f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0087, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void importShared() {
        /*
            r11 = this;
            r0 = 3
            r1 = 0
            java.io.File r2 = nodomain.freeyourgadget.gadgetbridge.util.FileUtils.getExternalFilesDir()     // Catch:{ Exception -> 0x0014 }
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x0014 }
            java.lang.String r4 = "Export_preference"
            r3.<init>(r2, r4)     // Catch:{ Exception -> 0x0014 }
            android.content.SharedPreferences r4 = sharedPrefs     // Catch:{ Exception -> 0x0014 }
            nodomain.freeyourgadget.gadgetbridge.util.ImportExportSharedPreferences.importFromFile(r4, r3)     // Catch:{ Exception -> 0x0014 }
            goto L_0x0028
        L_0x0014:
            r2 = move-exception
            r3 = 2131755201(0x7f1000c1, float:1.9141275E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r6 = r2.getMessage()
            r5[r1] = r6
            java.lang.String r3 = r11.getString(r3, r5)
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r11, r3, r4, r0, r2)
        L_0x0028:
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r2 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x0088 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r3 = r2.getDaoSession()     // Catch:{ all -> 0x007a }
            java.util.List r3 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getActiveDevices(r3)     // Catch:{ all -> 0x007a }
            java.util.Iterator r4 = r3.iterator()     // Catch:{ all -> 0x007a }
        L_0x0038:
            boolean r5 = r4.hasNext()     // Catch:{ all -> 0x007a }
            if (r5 == 0) goto L_0x0074
            java.lang.Object r5 = r4.next()     // Catch:{ all -> 0x007a }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r5 = (nodomain.freeyourgadget.gadgetbridge.entities.Device) r5     // Catch:{ all -> 0x007a }
            java.lang.String r6 = r5.getIdentifier()     // Catch:{ all -> 0x007a }
            android.content.SharedPreferences r6 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getDeviceSpecificSharedPrefs(r6)     // Catch:{ all -> 0x007a }
            android.content.SharedPreferences r7 = sharedPrefs     // Catch:{ all -> 0x007a }
            if (r7 == 0) goto L_0x0073
            java.io.File r7 = nodomain.freeyourgadget.gadgetbridge.util.FileUtils.getExternalFilesDir()     // Catch:{ all -> 0x007a }
            java.io.File r8 = new java.io.File     // Catch:{ all -> 0x007a }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x007a }
            r9.<init>()     // Catch:{ all -> 0x007a }
            java.lang.String r10 = "Export_preference_"
            r9.append(r10)     // Catch:{ all -> 0x007a }
            java.lang.String r10 = r5.getIdentifier()     // Catch:{ all -> 0x007a }
            r9.append(r10)     // Catch:{ all -> 0x007a }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x007a }
            r8.<init>(r7, r9)     // Catch:{ all -> 0x007a }
            nodomain.freeyourgadget.gadgetbridge.util.ImportExportSharedPreferences.importFromFile(r6, r8)     // Catch:{ Exception -> 0x0072 }
            goto L_0x0073
        L_0x0072:
            r9 = move-exception
        L_0x0073:
            goto L_0x0038
        L_0x0074:
            if (r2 == 0) goto L_0x0079
            r2.close()     // Catch:{ Exception -> 0x0088 }
        L_0x0079:
            goto L_0x008e
        L_0x007a:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x007c }
        L_0x007c:
            r4 = move-exception
            if (r2 == 0) goto L_0x0087
            r2.close()     // Catch:{ all -> 0x0083 }
            goto L_0x0087
        L_0x0083:
            r5 = move-exception
            r3.addSuppressed(r5)     // Catch:{ Exception -> 0x0088 }
        L_0x0087:
            throw r4     // Catch:{ Exception -> 0x0088 }
        L_0x0088:
            r2 = move-exception
            java.lang.String r3 = "Error importing device specific preferences"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r3, r1, r0)
        L_0x008e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity.importShared():void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0030, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0031, code lost:
        if (r2 != null) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003b, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void exportDB() {
        /*
            r9 = this;
            r0 = 0
            r1 = 1
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r2 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x003c }
            r9.exportShared()     // Catch:{ all -> 0x002e }
            nodomain.freeyourgadget.gadgetbridge.database.DBHelper r3 = new nodomain.freeyourgadget.gadgetbridge.database.DBHelper     // Catch:{ all -> 0x002e }
            r3.<init>(r9)     // Catch:{ all -> 0x002e }
            java.io.File r4 = nodomain.freeyourgadget.gadgetbridge.util.FileUtils.getExternalFilesDir()     // Catch:{ all -> 0x002e }
            java.io.File r5 = r3.exportDB((nodomain.freeyourgadget.gadgetbridge.database.DBHandler) r2, (java.io.File) r4)     // Catch:{ all -> 0x002e }
            r6 = 2131755203(0x7f1000c3, float:1.9141279E38)
            java.lang.Object[] r7 = new java.lang.Object[r1]     // Catch:{ all -> 0x002e }
            java.lang.String r8 = r5.getAbsolutePath()     // Catch:{ all -> 0x002e }
            r7[r0] = r8     // Catch:{ all -> 0x002e }
            java.lang.String r6 = r9.getString(r6, r7)     // Catch:{ all -> 0x002e }
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r9, (java.lang.String) r6, (int) r1, (int) r1)     // Catch:{ all -> 0x002e }
            if (r2 == 0) goto L_0x002d
            r2.close()     // Catch:{ Exception -> 0x003c }
        L_0x002d:
            goto L_0x0050
        L_0x002e:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0030 }
        L_0x0030:
            r4 = move-exception
            if (r2 == 0) goto L_0x003b
            r2.close()     // Catch:{ all -> 0x0037 }
            goto L_0x003b
        L_0x0037:
            r5 = move-exception
            r3.addSuppressed(r5)     // Catch:{ Exception -> 0x003c }
        L_0x003b:
            throw r4     // Catch:{ Exception -> 0x003c }
        L_0x003c:
            r2 = move-exception
            r3 = 2131755199(0x7f1000bf, float:1.914127E38)
            java.lang.Object[] r4 = new java.lang.Object[r1]
            java.lang.String r5 = r2.getMessage()
            r4[r0] = r5
            java.lang.String r0 = r9.getString(r3, r4)
            r3 = 3
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r9, r0, r1, r3, r2)
        L_0x0050:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity.exportDB():void");
    }

    /* access modifiers changed from: private */
    public void importDB() {
        new AlertDialog.Builder(this).setCancelable(true).setTitle(C0889R.string.dbmanagementactivity_import_data_title).setMessage(C0889R.string.dbmanagementactivity_overwrite_database_confirmation).setPositiveButton(C0889R.string.dbmanagementactivity_overwrite, new DialogInterface.OnClickListener() {
            /* JADX WARNING: Code restructure failed: missing block: B:11:0x0039, code lost:
                r3 = move-exception;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:12:0x003a, code lost:
                if (r1 != null) goto L_0x003c;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
                r1.close();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:18:0x0044, code lost:
                throw r3;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onClick(android.content.DialogInterface r10, int r11) {
                /*
                    r9 = this;
                    r0 = 1
                    nodomain.freeyourgadget.gadgetbridge.database.DBHandler r1 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x0045 }
                    nodomain.freeyourgadget.gadgetbridge.database.DBHelper r2 = new nodomain.freeyourgadget.gadgetbridge.database.DBHelper     // Catch:{ all -> 0x0037 }
                    nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity r3 = nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity.this     // Catch:{ all -> 0x0037 }
                    r2.<init>(r3)     // Catch:{ all -> 0x0037 }
                    java.io.File r3 = nodomain.freeyourgadget.gadgetbridge.util.FileUtils.getExternalFilesDir()     // Catch:{ all -> 0x0037 }
                    android.database.sqlite.SQLiteOpenHelper r4 = r1.getHelper()     // Catch:{ all -> 0x0037 }
                    java.io.File r5 = new java.io.File     // Catch:{ all -> 0x0037 }
                    java.lang.String r6 = r4.getDatabaseName()     // Catch:{ all -> 0x0037 }
                    r5.<init>(r3, r6)     // Catch:{ all -> 0x0037 }
                    r2.importDB(r1, r5)     // Catch:{ all -> 0x0037 }
                    r2.validateDB(r4)     // Catch:{ all -> 0x0037 }
                    nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity r6 = nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity.this     // Catch:{ all -> 0x0037 }
                    nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity r7 = nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity.this     // Catch:{ all -> 0x0037 }
                    r8 = 2131755205(0x7f1000c5, float:1.9141283E38)
                    java.lang.String r7 = r7.getString(r8)     // Catch:{ all -> 0x0037 }
                    nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r6, (java.lang.String) r7, (int) r0, (int) r0)     // Catch:{ all -> 0x0037 }
                    if (r1 == 0) goto L_0x0036
                    r1.close()     // Catch:{ Exception -> 0x0045 }
                L_0x0036:
                    goto L_0x005c
                L_0x0037:
                    r2 = move-exception
                    throw r2     // Catch:{ all -> 0x0039 }
                L_0x0039:
                    r3 = move-exception
                    if (r1 == 0) goto L_0x0044
                    r1.close()     // Catch:{ all -> 0x0040 }
                    goto L_0x0044
                L_0x0040:
                    r4 = move-exception
                    r2.addSuppressed(r4)     // Catch:{ Exception -> 0x0045 }
                L_0x0044:
                    throw r3     // Catch:{ Exception -> 0x0045 }
                L_0x0045:
                    r1 = move-exception
                    nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity r2 = nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity.this
                    r3 = 2131755201(0x7f1000c1, float:1.9141275E38)
                    java.lang.Object[] r4 = new java.lang.Object[r0]
                    r5 = 0
                    java.lang.String r6 = r1.getMessage()
                    r4[r5] = r6
                    java.lang.String r3 = r2.getString(r3, r4)
                    r4 = 3
                    nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r2, r3, r0, r4, r1)
                L_0x005c:
                    nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity r0 = nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity.this
                    r0.importShared()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity.C09267.onClick(android.content.DialogInterface, int):void");
            }
        }).setNegativeButton(C0889R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    /* access modifiers changed from: private */
    public void deleteActivityDatabase() {
        new AlertDialog.Builder(this).setCancelable(true).setTitle(C0889R.string.dbmanagementactivity_delete_activity_data_title).setMessage(C0889R.string.dbmanagementactivity_really_delete_entire_db).setPositiveButton(C0889R.string.Delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (GBApplication.deleteActivityDatabase(DbManagementActivity.this)) {
                    DbManagementActivity dbManagementActivity = DbManagementActivity.this;
                    C1238GB.toast((Context) dbManagementActivity, dbManagementActivity.getString(C0889R.string.dbmanagementactivity_database_successfully_deleted), 0, 1);
                    return;
                }
                DbManagementActivity dbManagementActivity2 = DbManagementActivity.this;
                C1238GB.toast((Context) dbManagementActivity2, dbManagementActivity2.getString(C0889R.string.dbmanagementactivity_db_deletion_failed), 0, 1);
            }
        }).setNegativeButton(C0889R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    /* access modifiers changed from: private */
    public void deleteOldActivityDbFile() {
        new AlertDialog.Builder(this).setCancelable(true);
        new AlertDialog.Builder(this).setTitle(C0889R.string.dbmanagementactivity_delete_old_activity_db);
        new AlertDialog.Builder(this).setMessage(C0889R.string.dbmanagementactivity_delete_old_activitydb_confirmation);
        new AlertDialog.Builder(this).setPositiveButton(C0889R.string.Delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (GBApplication.deleteOldActivityDatabase(DbManagementActivity.this)) {
                    DbManagementActivity dbManagementActivity = DbManagementActivity.this;
                    C1238GB.toast((Context) dbManagementActivity, dbManagementActivity.getString(C0889R.string.dbmanagementactivity_old_activity_db_successfully_deleted), 0, 1);
                    return;
                }
                DbManagementActivity dbManagementActivity2 = DbManagementActivity.this;
                C1238GB.toast((Context) dbManagementActivity2, dbManagementActivity2.getString(C0889R.string.dbmanagementactivity_old_activity_db_deletion_failed), 0, 1);
            }
        });
        new AlertDialog.Builder(this).setNegativeButton(C0889R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        new AlertDialog.Builder(this).show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
}
