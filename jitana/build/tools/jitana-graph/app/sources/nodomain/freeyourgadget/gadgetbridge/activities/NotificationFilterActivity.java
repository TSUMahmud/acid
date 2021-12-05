package nodomain.freeyourgadget.gadgetbridge.activities;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.BuildConfig;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilter;
import nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilterDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationFilterActivity extends AbstractGBActivity {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) NotificationFilterActivity.class);
    public static final int NOTIFICATION_FILTER_MODE_BLACKLIST = 2;
    public static final int NOTIFICATION_FILTER_MODE_NONE = 0;
    public static final int NOTIFICATION_FILTER_MODE_WHITELIST = 1;
    public static final int NOTIFICATION_FILTER_SUBMODE_ALL = 1;
    public static final int NOTIFICATION_FILTER_SUBMODE_ANY = 0;
    private Button mButtonSave;
    /* access modifiers changed from: private */
    public EditText mEditTextWords;
    private List<Long> mFilterEntryIds = new ArrayList();
    private NotificationFilter mNotificationFilter;
    private Spinner mSpinnerFilterMode;
    /* access modifiers changed from: private */
    public Spinner mSpinnerFilterSubMode;
    private List<String> mWordsList = new ArrayList();

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00d6, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x00d7, code lost:
        if (r1 != null) goto L_0x00d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00e1, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r13) {
        /*
            r12 = this;
            super.onCreate(r13)
            r0 = 2131492914(0x7f0c0032, float:1.8609293E38)
            r12.setContentView((int) r0)
            android.content.Intent r0 = r12.getIntent()
            java.lang.String r1 = "packageName"
            java.lang.String r0 = r0.getStringExtra(r1)
            boolean r1 = org.apache.commons.lang3.StringUtils.isBlank(r0)
            if (r1 == 0) goto L_0x001c
            r12.finish()
        L_0x001c:
            java.lang.String r0 = r0.toLowerCase()
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r1 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x00e2 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r2 = r1.getDaoSession()     // Catch:{ all -> 0x00d4 }
            nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilterDao r2 = r2.getNotificationFilterDao()     // Catch:{ all -> 0x00d4 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r3 = r1.getDaoSession()     // Catch:{ all -> 0x00d4 }
            nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilterEntryDao r3 = r3.getNotificationFilterEntryDao()     // Catch:{ all -> 0x00d4 }
            de.greenrobot.dao.query.QueryBuilder r4 = r2.queryBuilder()     // Catch:{ all -> 0x00d4 }
            de.greenrobot.dao.Property r5 = nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilterDao.Properties.AppIdentifier     // Catch:{ all -> 0x00d4 }
            de.greenrobot.dao.query.WhereCondition r5 = r5.mo14989eq(r0)     // Catch:{ all -> 0x00d4 }
            r6 = 0
            de.greenrobot.dao.query.WhereCondition[] r7 = new p008de.greenrobot.dao.query.WhereCondition[r6]     // Catch:{ all -> 0x00d4 }
            de.greenrobot.dao.query.QueryBuilder r4 = r4.where(r5, r7)     // Catch:{ all -> 0x00d4 }
            de.greenrobot.dao.query.Query r4 = r4.build()     // Catch:{ all -> 0x00d4 }
            java.lang.Object r5 = r4.unique()     // Catch:{ all -> 0x00d4 }
            nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilter r5 = (nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilter) r5     // Catch:{ all -> 0x00d4 }
            r12.mNotificationFilter = r5     // Catch:{ all -> 0x00d4 }
            nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilter r5 = r12.mNotificationFilter     // Catch:{ all -> 0x00d4 }
            if (r5 != 0) goto L_0x0069
            nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilter r5 = new nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilter     // Catch:{ all -> 0x00d4 }
            r5.<init>()     // Catch:{ all -> 0x00d4 }
            r12.mNotificationFilter = r5     // Catch:{ all -> 0x00d4 }
            nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilter r5 = r12.mNotificationFilter     // Catch:{ all -> 0x00d4 }
            r5.setAppIdentifier(r0)     // Catch:{ all -> 0x00d4 }
            org.slf4j.Logger r5 = LOG     // Catch:{ all -> 0x00d4 }
            java.lang.String r6 = "New Notification Filter"
            r5.debug(r6)     // Catch:{ all -> 0x00d4 }
            goto L_0x00cb
        L_0x0069:
            org.slf4j.Logger r5 = LOG     // Catch:{ all -> 0x00d4 }
            java.lang.String r7 = "Loaded existing notification filter"
            r5.debug(r7)     // Catch:{ all -> 0x00d4 }
            de.greenrobot.dao.query.QueryBuilder r5 = r3.queryBuilder()     // Catch:{ all -> 0x00d4 }
            de.greenrobot.dao.Property r7 = nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilterEntryDao.Properties.NotificationFilterId     // Catch:{ all -> 0x00d4 }
            nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilter r8 = r12.mNotificationFilter     // Catch:{ all -> 0x00d4 }
            java.lang.Long r8 = r8.getId()     // Catch:{ all -> 0x00d4 }
            de.greenrobot.dao.query.WhereCondition r7 = r7.mo14989eq(r8)     // Catch:{ all -> 0x00d4 }
            de.greenrobot.dao.query.WhereCondition[] r6 = new p008de.greenrobot.dao.query.WhereCondition[r6]     // Catch:{ all -> 0x00d4 }
            de.greenrobot.dao.query.QueryBuilder r5 = r5.where(r7, r6)     // Catch:{ all -> 0x00d4 }
            de.greenrobot.dao.query.Query r5 = r5.build()     // Catch:{ all -> 0x00d4 }
            java.util.List r6 = r5.list()     // Catch:{ all -> 0x00d4 }
            java.util.Iterator r7 = r6.iterator()     // Catch:{ all -> 0x00d4 }
        L_0x0092:
            boolean r8 = r7.hasNext()     // Catch:{ all -> 0x00d4 }
            if (r8 == 0) goto L_0x00cb
            java.lang.Object r8 = r7.next()     // Catch:{ all -> 0x00d4 }
            nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilterEntry r8 = (nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilterEntry) r8     // Catch:{ all -> 0x00d4 }
            java.util.List<java.lang.String> r9 = r12.mWordsList     // Catch:{ all -> 0x00d4 }
            java.lang.String r10 = r8.getNotificationFilterContent()     // Catch:{ all -> 0x00d4 }
            r9.add(r10)     // Catch:{ all -> 0x00d4 }
            java.util.List<java.lang.Long> r9 = r12.mFilterEntryIds     // Catch:{ all -> 0x00d4 }
            java.lang.Long r10 = r8.getId()     // Catch:{ all -> 0x00d4 }
            r9.add(r10)     // Catch:{ all -> 0x00d4 }
            org.slf4j.Logger r9 = LOG     // Catch:{ all -> 0x00d4 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d4 }
            r10.<init>()     // Catch:{ all -> 0x00d4 }
            java.lang.String r11 = "Loaded filter word: "
            r10.append(r11)     // Catch:{ all -> 0x00d4 }
            java.lang.String r11 = r8.getNotificationFilterContent()     // Catch:{ all -> 0x00d4 }
            r10.append(r11)     // Catch:{ all -> 0x00d4 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x00d4 }
            r9.debug(r10)     // Catch:{ all -> 0x00d4 }
            goto L_0x0092
        L_0x00cb:
            r12.setupView(r1)     // Catch:{ all -> 0x00d4 }
            if (r1 == 0) goto L_0x00d3
            r1.close()     // Catch:{ Exception -> 0x00e2 }
        L_0x00d3:
            goto L_0x0100
        L_0x00d4:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x00d6 }
        L_0x00d6:
            r3 = move-exception
            if (r1 == 0) goto L_0x00e1
            r1.close()     // Catch:{ all -> 0x00dd }
            goto L_0x00e1
        L_0x00dd:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ Exception -> 0x00e2 }
        L_0x00e1:
            throw r3     // Catch:{ Exception -> 0x00e2 }
        L_0x00e2:
            r1 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Error accessing the database: "
            r2.append(r3)
            java.lang.String r3 = r1.getLocalizedMessage()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r3 = 1
            r4 = 3
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r12, (java.lang.String) r2, (int) r3, (int) r4)
            r12.finish()
        L_0x0100:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.NotificationFilterActivity.onCreate(android.os.Bundle):void");
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupView(DBHandler db) {
        this.mSpinnerFilterMode = (Spinner) findViewById(C0889R.C0891id.spinnerFilterMode);
        this.mSpinnerFilterMode.setSelection(this.mNotificationFilter.getNotificationFilterMode());
        this.mSpinnerFilterSubMode = (Spinner) findViewById(C0889R.C0891id.spinnerSubMode);
        this.mSpinnerFilterMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                if (pos == 0) {
                    NotificationFilterActivity.this.mEditTextWords.setEnabled(false);
                    NotificationFilterActivity.this.mSpinnerFilterSubMode.setEnabled(false);
                } else if (pos == 1 || pos == 2) {
                    NotificationFilterActivity.this.mEditTextWords.setEnabled(true);
                    NotificationFilterActivity.this.mSpinnerFilterSubMode.setEnabled(true);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.mSpinnerFilterSubMode.setSelection(this.mNotificationFilter.getNotificationFilterSubMode());
        this.mEditTextWords = (EditText) findViewById(C0889R.C0891id.editTextWords);
        if (!this.mWordsList.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (String temp : this.mWordsList) {
                builder.append(temp);
                builder.append(StringUtils.f210LF);
            }
            this.mEditTextWords.setText(builder.toString());
        }
        this.mEditTextWords.setEnabled(this.mSpinnerFilterMode.getSelectedItemPosition() == 0);
        this.mButtonSave = (Button) findViewById(C0889R.C0891id.buttonSaveFilter);
        this.mButtonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NotificationFilterActivity.this.saveFilter();
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00a2, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00a3, code lost:
        if (r1 != null) goto L_0x00a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00ad, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void saveFilter() {
        /*
            r12 = this;
            android.widget.EditText r0 = r12.mEditTextWords
            android.text.Editable r0 = r0.getText()
            java.lang.String r0 = r0.toString()
            boolean r1 = org.apache.commons.lang3.StringUtils.isBlank(r0)
            r2 = 0
            if (r1 == 0) goto L_0x0024
            android.widget.Spinner r1 = r12.mSpinnerFilterMode
            int r1 = r1.getSelectedItemPosition()
            if (r1 == 0) goto L_0x0024
            r1 = 2131755804(0x7f10031c, float:1.9142498E38)
            android.widget.Toast r1 = android.widget.Toast.makeText(r12, r1, r2)
            r1.show()
            return
        L_0x0024:
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r1 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x00ae }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r3 = r1.getDaoSession()     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilterDao r3 = r3.getNotificationFilterDao()     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r4 = r1.getDaoSession()     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilterEntryDao r4 = r4.getNotificationFilterEntryDao()     // Catch:{ all -> 0x00a0 }
            r12.debugOutput(r3)     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilter r5 = r12.mNotificationFilter     // Catch:{ all -> 0x00a0 }
            android.widget.Spinner r6 = r12.mSpinnerFilterMode     // Catch:{ all -> 0x00a0 }
            int r6 = r6.getSelectedItemPosition()     // Catch:{ all -> 0x00a0 }
            r5.setNotificationFilterMode(r6)     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilter r5 = r12.mNotificationFilter     // Catch:{ all -> 0x00a0 }
            android.widget.Spinner r6 = r12.mSpinnerFilterSubMode     // Catch:{ all -> 0x00a0 }
            int r6 = r6.getSelectedItemPosition()     // Catch:{ all -> 0x00a0 }
            r5.setNotificationFilterSubMode(r6)     // Catch:{ all -> 0x00a0 }
            java.util.List<java.lang.Long> r5 = r12.mFilterEntryIds     // Catch:{ all -> 0x00a0 }
            r4.deleteByKeyInTx(r5)     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilter r5 = r12.mNotificationFilter     // Catch:{ all -> 0x00a0 }
            long r5 = r3.insertOrReplace(r5)     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilter r7 = r12.mNotificationFilter     // Catch:{ all -> 0x00a0 }
            int r7 = r7.getNotificationFilterMode()     // Catch:{ all -> 0x00a0 }
            if (r7 == 0) goto L_0x008d
            java.lang.String r7 = "\n"
            java.lang.String[] r7 = r0.split(r7)     // Catch:{ all -> 0x00a0 }
            int r8 = r7.length     // Catch:{ all -> 0x00a0 }
            r9 = 0
        L_0x006c:
            if (r9 >= r8) goto L_0x008d
            r10 = r7[r9]     // Catch:{ all -> 0x00a0 }
            boolean r11 = org.apache.commons.lang3.StringUtils.isBlank(r10)     // Catch:{ all -> 0x00a0 }
            if (r11 == 0) goto L_0x0077
            goto L_0x008a
        L_0x0077:
            java.lang.String r11 = r10.trim()     // Catch:{ all -> 0x00a0 }
            r10 = r11
            nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilterEntry r11 = new nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilterEntry     // Catch:{ all -> 0x00a0 }
            r11.<init>()     // Catch:{ all -> 0x00a0 }
            r11.setNotificationFilterContent(r10)     // Catch:{ all -> 0x00a0 }
            r11.setNotificationFilterId(r5)     // Catch:{ all -> 0x00a0 }
            r4.insert(r11)     // Catch:{ all -> 0x00a0 }
        L_0x008a:
            int r9 = r9 + 1
            goto L_0x006c
        L_0x008d:
            r7 = 2131755803(0x7f10031b, float:1.9142496E38)
            android.widget.Toast r2 = android.widget.Toast.makeText(r12, r7, r2)     // Catch:{ all -> 0x00a0 }
            r2.show()     // Catch:{ all -> 0x00a0 }
            r12.finish()     // Catch:{ all -> 0x00a0 }
            if (r1 == 0) goto L_0x009f
            r1.close()     // Catch:{ Exception -> 0x00ae }
        L_0x009f:
            goto L_0x00c9
        L_0x00a0:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x00a2 }
        L_0x00a2:
            r3 = move-exception
            if (r1 == 0) goto L_0x00ad
            r1.close()     // Catch:{ all -> 0x00a9 }
            goto L_0x00ad
        L_0x00a9:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ Exception -> 0x00ae }
        L_0x00ad:
            throw r3     // Catch:{ Exception -> 0x00ae }
        L_0x00ae:
            r1 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Error accessing the database: "
            r2.append(r3)
            java.lang.String r3 = r1.getLocalizedMessage()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r3 = 3
            r4 = 1
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r12, (java.lang.String) r2, (int) r4, (int) r3)
        L_0x00c9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.NotificationFilterActivity.saveFilter():void");
    }

    private void debugOutput(NotificationFilterDao notificationFilterDao) {
        if (BuildConfig.DEBUG) {
            List<NotificationFilter> filters = notificationFilterDao.loadAll();
            LOG.info("Saved filters");
            for (NotificationFilter temp : filters) {
                Logger logger = LOG;
                logger.info("Filter: " + temp.getId() + StringUtils.SPACE + temp.getAppIdentifier());
            }
        }
    }
}
