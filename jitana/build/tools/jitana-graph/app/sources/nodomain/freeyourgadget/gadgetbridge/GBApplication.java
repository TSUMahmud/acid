package nodomain.freeyourgadget.gadgetbridge;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.TypedValue;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.database.DBOpenHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiConst;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoMaster;
import nodomain.freeyourgadget.gadgetbridge.externalevents.BluetoothStateChangeReceiver;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceService;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.service.NotificationCollectorMonitorService;
import nodomain.freeyourgadget.gadgetbridge.util.AndroidUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.FileUtils;
import nodomain.freeyourgadget.gadgetbridge.util.GBPrefs;
import nodomain.freeyourgadget.gadgetbridge.util.LimitedQueue;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;

public class GBApplication extends Application {
    public static final String ACTION_LANGUAGE_CHANGE = "nodomain.freeyourgadget.gadgetbridge.gbapplication.action.language_change";
    public static final String ACTION_NEW_DATA = "nodomain.freeyourgadget.gadgetbridge.action.new_data";
    public static final String ACTION_QUIT = "nodomain.freeyourgadget.gadgetbridge.gbapplication.action.quit";
    private static final int CURRENT_PREFS_VERSION = 7;
    public static final String DATABASE_NAME = "Gadgetbridge";
    private static final String PREFS_VERSION = "shared_preferences_version";
    private static final String TAG = "GBApplication";
    private static GBApplication app;
    private static HashSet<String> apps_notification_blacklist = null;
    private static HashSet<String> apps_pebblemsg_blacklist = null;
    private static HashSet<String> calendars_blacklist = null;
    private static GBApplication context;
    private static final Lock dbLock = new ReentrantLock();
    private static DeviceService deviceService;
    private static GBPrefs gbPrefs;
    private static Locale language;
    private static LockHandler lockHandler;
    private static Logging logging = new Logging() {
        /* access modifiers changed from: protected */
        public String createLogDirectory() throws IOException {
            if (GBEnvironment.env().isLocalTest()) {
                return System.getProperty(Logging.PROP_LOGFILES_DIR);
            }
            return FileUtils.getExternalFilesDir().getAbsolutePath();
        }
    };
    private static LimitedQueue mIDSenderLookup = new LimitedQueue(16);
    private static NotificationManager notificationManager;
    private static Prefs prefs;
    private static SharedPreferences sharedPrefs;
    private BluetoothStateChangeReceiver bluetoothStateChangeReceiver;
    private DeviceManager deviceManager;

    public static void quit() {
        C1238GB.log("Quitting Gadgetbridge...", 1, (Throwable) null);
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_QUIT));
        deviceService().quit();
    }

    public GBApplication() {
        context = this;
    }

    public static Logging getLogging() {
        return logging;
    }

    /* access modifiers changed from: protected */
    public DeviceService createDeviceService() {
        return new GBDeviceService(this);
    }

    public void onCreate() {
        app = this;
        super.onCreate();
        if (lockHandler == null) {
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefs = new Prefs(sharedPrefs);
            gbPrefs = new GBPrefs(prefs);
            if (!GBEnvironment.isEnvironmentSetup()) {
                GBEnvironment.setupEnvironment(GBEnvironment.createDeviceEnvironment());
                setupDatabase();
            }
            setupLogging(isFileLoggingEnabled());
            if (getPrefsFileVersion() != 7) {
                migratePrefs(getPrefsFileVersion());
            }
            setupExceptionHandler();
            this.deviceManager = new DeviceManager(this);
            setLanguage(prefs.getString(HuamiConst.PREF_LANGUAGE, "default"));
            deviceService = createDeviceService();
            loadAppsNotifBlackList();
            loadAppsPebbleBlackList();
            loadCalendarsBlackList();
            if (isRunningMarshmallowOrLater()) {
                notificationManager = (NotificationManager) context.getSystemService("notification");
                if (isRunningOreoOrLater()) {
                    if (notificationManager.getNotificationChannel(C1238GB.NOTIFICATION_CHANNEL_ID) == null) {
                        notificationManager.createNotificationChannel(new NotificationChannel(C1238GB.NOTIFICATION_CHANNEL_ID, getString(C0889R.string.notification_channel_name), 2));
                    }
                    this.bluetoothStateChangeReceiver = new BluetoothStateChangeReceiver();
                    registerReceiver(this.bluetoothStateChangeReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
                }
                startService(new Intent(this, NotificationCollectorMonitorService.class));
            }
        }
    }

    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= 40 && !hasBusyDevice()) {
            DBHelper.clearSession();
        }
    }

    private boolean hasBusyDevice() {
        for (GBDevice device : getDeviceManager().getDevices()) {
            if (device.isBusy()) {
                return true;
            }
        }
        return false;
    }

    public static void setupLogging(boolean enabled) {
        logging.setupLogging(enabled);
    }

    public static String getLogPath() {
        return logging.getLogPath();
    }

    private void setupExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new LoggingExceptionHandler(Thread.getDefaultUncaughtExceptionHandler()));
    }

    public static boolean isFileLoggingEnabled() {
        return prefs.getBoolean("log_to_file", false);
    }

    public static boolean minimizeNotification() {
        return prefs.getBoolean("minimize_priority", false);
    }

    public void setupDatabase() {
        DaoMaster.OpenHelper helper;
        if (GBEnvironment.env().isTest()) {
            helper = new DaoMaster.DevOpenHelper(this, (String) null, (SQLiteDatabase.CursorFactory) null);
        } else {
            helper = new DBOpenHelper(this, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null);
        }
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        if (lockHandler == null) {
            lockHandler = new LockHandler();
        }
        lockHandler.init(daoMaster, helper);
    }

    public static Context getContext() {
        return context;
    }

    public static DeviceService deviceService() {
        return deviceService;
    }

    public static DBHandler acquireDB() throws GBException {
        try {
            if (dbLock.tryLock(30, TimeUnit.SECONDS)) {
                return lockHandler;
            }
        } catch (InterruptedException e) {
            Log.i(TAG, "Interrupted while waiting for DB lock");
        }
        throw new GBException("Unable to access the database.");
    }

    public static void releaseDB() {
        dbLock.unlock();
    }

    public static boolean isRunningLollipopOrLater() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public static boolean isRunningMarshmallowOrLater() {
        return Build.VERSION.SDK_INT >= 23;
    }

    public static boolean isRunningNougatOrLater() {
        return Build.VERSION.SDK_INT >= 24;
    }

    public static boolean isRunningOreoOrLater() {
        return Build.VERSION.SDK_INT >= 26;
    }

    private static boolean isPrioritySender(int prioritySenders, String number) {
        if (prioritySenders == 0) {
            return true;
        }
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"_id", "starred"}, (String) null, (String[]) null, (String) null);
        boolean exists = false;
        int starred = 0;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    exists = true;
                    starred = cursor.getInt(cursor.getColumnIndexOrThrow("starred"));
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        if (prioritySenders == 1 && exists) {
            return true;
        }
        if (prioritySenders == 2 && starred == 1) {
            return true;
        }
        return false;
    }

    public static boolean isPriorityNumber(int priorityType, String number) {
        NotificationManager.Policy notificationPolicy = notificationManager.getNotificationPolicy();
        if (priorityType == 4) {
            if ((notificationPolicy.priorityCategories & 4) == 4) {
                return isPrioritySender(notificationPolicy.priorityMessageSenders, number);
            }
            return false;
        } else if (priorityType == 8 && (notificationPolicy.priorityCategories & 8) == 8) {
            return isPrioritySender(notificationPolicy.priorityCallSenders, number);
        } else {
            return false;
        }
    }

    public static int getGrantedInterruptionFilter() {
        if (!prefs.getBoolean("notification_filter", false) || !isRunningMarshmallowOrLater() || !notificationManager.isNotificationPolicyAccessGranted()) {
            return 1;
        }
        return notificationManager.getCurrentInterruptionFilter();
    }

    public static boolean appIsNotifBlacklisted(String packageName) {
        if (apps_notification_blacklist == null) {
            C1238GB.log("appIsNotifBlacklisted: apps_notification_blacklist is null!", 1, (Throwable) null);
        }
        HashSet<String> hashSet = apps_notification_blacklist;
        if (hashSet == null || !hashSet.contains(packageName)) {
            return false;
        }
        return true;
    }

    public static void setAppsNotifBlackList(Set<String> packageNames) {
        if (packageNames == null) {
            C1238GB.log("Set null apps_notification_blacklist", 1, (Throwable) null);
            apps_notification_blacklist = new HashSet<>();
        } else {
            apps_notification_blacklist = new HashSet<>(packageNames);
        }
        C1238GB.log("New apps_notification_blacklist has " + apps_notification_blacklist.size() + " entries", 1, (Throwable) null);
        saveAppsNotifBlackList();
    }

    private static void loadAppsNotifBlackList() {
        C1238GB.log("Loading apps_notification_blacklist", 1, (Throwable) null);
        apps_notification_blacklist = (HashSet) sharedPrefs.getStringSet(GBPrefs.PACKAGE_BLACKLIST, (Set) null);
        if (apps_notification_blacklist == null) {
            apps_notification_blacklist = new HashSet<>();
        }
        C1238GB.log("Loaded apps_notification_blacklist has " + apps_notification_blacklist.size() + " entries", 1, (Throwable) null);
    }

    private static void saveAppsNotifBlackList() {
        C1238GB.log("Saving apps_notification_blacklist with " + apps_notification_blacklist.size() + " entries", 1, (Throwable) null);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        if (apps_notification_blacklist.isEmpty()) {
            editor.putStringSet(GBPrefs.PACKAGE_BLACKLIST, (Set) null);
        } else {
            Prefs.putStringSet(editor, GBPrefs.PACKAGE_BLACKLIST, apps_notification_blacklist);
        }
        editor.apply();
    }

    public static void addAppToNotifBlacklist(String packageName) {
        if (apps_notification_blacklist.add(packageName)) {
            saveAppsNotifBlackList();
        }
    }

    public static synchronized void removeFromAppsNotifBlacklist(String packageName) {
        synchronized (GBApplication.class) {
            C1238GB.log("Removing from apps_notification_blacklist: " + packageName, 1, (Throwable) null);
            apps_notification_blacklist.remove(packageName);
            saveAppsNotifBlackList();
        }
    }

    public static boolean appIsPebbleBlacklisted(String sender) {
        if (apps_pebblemsg_blacklist == null) {
            C1238GB.log("appIsPebbleBlacklisted: apps_pebblemsg_blacklist is null!", 1, (Throwable) null);
        }
        HashSet<String> hashSet = apps_pebblemsg_blacklist;
        if (hashSet == null || !hashSet.contains(sender)) {
            return false;
        }
        return true;
    }

    public static void setAppsPebbleBlackList(Set<String> packageNames) {
        if (packageNames == null) {
            C1238GB.log("Set null apps_pebblemsg_blacklist", 1, (Throwable) null);
            apps_pebblemsg_blacklist = new HashSet<>();
        } else {
            apps_pebblemsg_blacklist = new HashSet<>(packageNames);
        }
        C1238GB.log("New apps_pebblemsg_blacklist has " + apps_pebblemsg_blacklist.size() + " entries", 1, (Throwable) null);
        saveAppsPebbleBlackList();
    }

    private static void loadAppsPebbleBlackList() {
        C1238GB.log("Loading apps_pebblemsg_blacklist", 1, (Throwable) null);
        apps_pebblemsg_blacklist = (HashSet) sharedPrefs.getStringSet(GBPrefs.PACKAGE_PEBBLEMSG_BLACKLIST, (Set) null);
        if (apps_pebblemsg_blacklist == null) {
            apps_pebblemsg_blacklist = new HashSet<>();
        }
        C1238GB.log("Loaded apps_pebblemsg_blacklist has " + apps_pebblemsg_blacklist.size() + " entries", 1, (Throwable) null);
    }

    private static void saveAppsPebbleBlackList() {
        C1238GB.log("Saving apps_pebblemsg_blacklist with " + apps_pebblemsg_blacklist.size() + " entries", 1, (Throwable) null);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        if (apps_pebblemsg_blacklist.isEmpty()) {
            editor.putStringSet(GBPrefs.PACKAGE_PEBBLEMSG_BLACKLIST, (Set) null);
        } else {
            Prefs.putStringSet(editor, GBPrefs.PACKAGE_PEBBLEMSG_BLACKLIST, apps_pebblemsg_blacklist);
        }
        editor.apply();
    }

    public static void addAppToPebbleBlacklist(String packageName) {
        if (apps_pebblemsg_blacklist.add(packageNameToPebbleMsgSender(packageName))) {
            saveAppsPebbleBlackList();
        }
    }

    public static synchronized void removeFromAppsPebbleBlacklist(String packageName) {
        synchronized (GBApplication.class) {
            C1238GB.log("Removing from apps_pebblemsg_blacklist: " + packageName, 1, (Throwable) null);
            apps_pebblemsg_blacklist.remove(packageNameToPebbleMsgSender(packageName));
            saveAppsPebbleBlackList();
        }
    }

    public static String packageNameToPebbleMsgSender(String packageName) {
        if ("eu.siacs.conversations".equals(packageName)) {
            return "Conversations";
        }
        if ("net.osmand.plus".equals(packageName)) {
            return "OsmAnd";
        }
        return packageName;
    }

    public static boolean calendarIsBlacklisted(String calendarDisplayName) {
        if (calendars_blacklist == null) {
            C1238GB.log("calendarIsBlacklisted: calendars_blacklist is null!", 1, (Throwable) null);
        }
        HashSet<String> hashSet = calendars_blacklist;
        if (hashSet == null || !hashSet.contains(calendarDisplayName)) {
            return false;
        }
        return true;
    }

    public static void setCalendarsBlackList(Set<String> calendarNames) {
        if (calendarNames == null) {
            C1238GB.log("Set null apps_notification_blacklist", 1, (Throwable) null);
            calendars_blacklist = new HashSet<>();
        } else {
            calendars_blacklist = new HashSet<>(calendarNames);
        }
        C1238GB.log("New calendars_blacklist has " + calendars_blacklist.size() + " entries", 1, (Throwable) null);
        saveCalendarsBlackList();
    }

    public static void addCalendarToBlacklist(String calendarDisplayName) {
        if (calendars_blacklist.add(calendarDisplayName)) {
            saveCalendarsBlackList();
        }
    }

    public static void removeFromCalendarBlacklist(String calendarDisplayName) {
        calendars_blacklist.remove(calendarDisplayName);
        saveCalendarsBlackList();
    }

    private static void loadCalendarsBlackList() {
        C1238GB.log("Loading calendars_blacklist", 1, (Throwable) null);
        calendars_blacklist = (HashSet) sharedPrefs.getStringSet(GBPrefs.CALENDAR_BLACKLIST, (Set) null);
        if (calendars_blacklist == null) {
            calendars_blacklist = new HashSet<>();
        }
        C1238GB.log("Loaded calendars_blacklist has " + calendars_blacklist.size() + " entries", 1, (Throwable) null);
    }

    private static void saveCalendarsBlackList() {
        C1238GB.log("Saving calendars_blacklist with " + calendars_blacklist.size() + " entries", 1, (Throwable) null);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        if (calendars_blacklist.isEmpty()) {
            editor.putStringSet(GBPrefs.CALENDAR_BLACKLIST, (Set) null);
        } else {
            Prefs.putStringSet(editor, GBPrefs.CALENDAR_BLACKLIST, calendars_blacklist);
        }
        editor.apply();
    }

    public static synchronized boolean deleteActivityDatabase(Context context2) {
        boolean result;
        synchronized (GBApplication.class) {
            if (lockHandler != null) {
                lockHandler.closeDb();
            }
            result = deleteOldActivityDatabase(context2) & getContext().deleteDatabase(DATABASE_NAME);
        }
        return result;
    }

    public static synchronized boolean deleteOldActivityDatabase(Context context2) {
        boolean result;
        synchronized (GBApplication.class) {
            result = true;
            if (new DBHelper(context2).existsDB("ActivityDatabase")) {
                result = getContext().deleteDatabase("ActivityDatabase");
            }
        }
        return result;
    }

    private int getPrefsFileVersion() {
        try {
            return Integer.parseInt(sharedPrefs.getString(PREFS_VERSION, "0"));
        } catch (Exception e) {
            return 1;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00a7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00a8, code lost:
        r8 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00a9, code lost:
        if (r6 != null) goto L_0x00ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:?, code lost:
        r6.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00af, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
        r7.addSuppressed(r0);
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:27:0x009a, B:33:0x00a6, B:38:0x00ab] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void migrateStringPrefToPerDevicePref(java.lang.String r17, java.lang.String r18, java.lang.String r19, java.util.ArrayList<nodomain.freeyourgadget.gadgetbridge.model.DeviceType> r20) {
        /*
            r16 = this;
            r1 = r17
            java.lang.String r2 = "GBApplication"
            android.content.SharedPreferences r0 = sharedPrefs
            android.content.SharedPreferences$Editor r3 = r0.edit()
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs
            r4 = r18
            java.lang.String r5 = r0.getString(r1, r4)
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r0 = acquireDB()     // Catch:{ Exception -> 0x00b7 }
            r6 = r0
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r0 = r6.getDaoSession()     // Catch:{ all -> 0x00a0 }
            java.util.List r7 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getActiveDevices(r0)     // Catch:{ all -> 0x00a0 }
            java.util.Iterator r8 = r7.iterator()     // Catch:{ all -> 0x00a0 }
        L_0x0023:
            boolean r9 = r8.hasNext()     // Catch:{ all -> 0x00a0 }
            if (r9 == 0) goto L_0x008e
            java.lang.Object r9 = r8.next()     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r9 = (nodomain.freeyourgadget.gadgetbridge.entities.Device) r9     // Catch:{ all -> 0x00a0 }
            java.lang.String r10 = r9.getIdentifier()     // Catch:{ all -> 0x00a0 }
            android.content.SharedPreferences r10 = getDeviceSpecificSharedPrefs(r10)     // Catch:{ all -> 0x00a0 }
            if (r10 == 0) goto L_0x0089
            android.content.SharedPreferences$Editor r11 = r10.edit()     // Catch:{ all -> 0x00a0 }
            int r12 = r9.getType()     // Catch:{ all -> 0x00a0 }
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r12 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.fromKey(r12)     // Catch:{ all -> 0x00a0 }
            r13 = r20
            boolean r14 = r13.contains(r12)     // Catch:{ all -> 0x0085 }
            if (r14 == 0) goto L_0x007f
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x0085 }
            r14.<init>()     // Catch:{ all -> 0x0085 }
            java.lang.String r15 = "migrating global string preference "
            r14.append(r15)     // Catch:{ all -> 0x0085 }
            r14.append(r1)     // Catch:{ all -> 0x0085 }
            java.lang.String r15 = " for "
            r14.append(r15)     // Catch:{ all -> 0x0085 }
            java.lang.String r15 = r12.name()     // Catch:{ all -> 0x0085 }
            r14.append(r15)     // Catch:{ all -> 0x0085 }
            java.lang.String r15 = " "
            r14.append(r15)     // Catch:{ all -> 0x0085 }
            java.lang.String r15 = r9.getIdentifier()     // Catch:{ all -> 0x0085 }
            r14.append(r15)     // Catch:{ all -> 0x0085 }
            java.lang.String r14 = r14.toString()     // Catch:{ all -> 0x0085 }
            android.util.Log.i(r2, r14)     // Catch:{ all -> 0x0085 }
            r14 = r19
            r11.putString(r14, r5)     // Catch:{ all -> 0x009e }
            goto L_0x0081
        L_0x007f:
            r14 = r19
        L_0x0081:
            r11.apply()     // Catch:{ all -> 0x009e }
            goto L_0x008d
        L_0x0085:
            r0 = move-exception
            r14 = r19
            goto L_0x00a5
        L_0x0089:
            r14 = r19
            r13 = r20
        L_0x008d:
            goto L_0x0023
        L_0x008e:
            r14 = r19
            r13 = r20
            r3.remove(r1)     // Catch:{ all -> 0x009e }
            r3.apply()     // Catch:{ all -> 0x009e }
            if (r6 == 0) goto L_0x009d
            r6.close()     // Catch:{ Exception -> 0x00b5 }
        L_0x009d:
            goto L_0x00c1
        L_0x009e:
            r0 = move-exception
            goto L_0x00a5
        L_0x00a0:
            r0 = move-exception
            r14 = r19
            r13 = r20
        L_0x00a5:
            r7 = r0
            throw r7     // Catch:{ all -> 0x00a7 }
        L_0x00a7:
            r0 = move-exception
            r8 = r0
            if (r6 == 0) goto L_0x00b4
            r6.close()     // Catch:{ all -> 0x00af }
            goto L_0x00b4
        L_0x00af:
            r0 = move-exception
            r9 = r0
            r7.addSuppressed(r9)     // Catch:{ Exception -> 0x00b5 }
        L_0x00b4:
            throw r8     // Catch:{ Exception -> 0x00b5 }
        L_0x00b5:
            r0 = move-exception
            goto L_0x00bc
        L_0x00b7:
            r0 = move-exception
            r14 = r19
            r13 = r20
        L_0x00bc:
            java.lang.String r6 = "error acquiring DB lock"
            android.util.Log.w(r2, r6)
        L_0x00c1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.GBApplication.migrateStringPrefToPerDevicePref(java.lang.String, java.lang.String, java.lang.String, java.util.ArrayList):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00a9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00aa, code lost:
        r7 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00ab, code lost:
        if (r5 != null) goto L_0x00ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00b1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
        r6.addSuppressed(r0);
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:27:0x009c, B:33:0x00a8, B:38:0x00ad] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void migrateBooleanPrefToPerDevicePref(java.lang.String r16, java.lang.Boolean r17, java.lang.String r18, java.util.ArrayList<nodomain.freeyourgadget.gadgetbridge.model.DeviceType> r19) {
        /*
            r15 = this;
            r1 = r16
            java.lang.String r2 = "GBApplication"
            android.content.SharedPreferences r0 = sharedPrefs
            android.content.SharedPreferences$Editor r3 = r0.edit()
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs
            boolean r4 = r17.booleanValue()
            boolean r4 = r0.getBoolean(r1, r4)
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r0 = acquireDB()     // Catch:{ Exception -> 0x00b9 }
            r5 = r0
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r0 = r5.getDaoSession()     // Catch:{ all -> 0x00a2 }
            java.util.List r6 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getActiveDevices(r0)     // Catch:{ all -> 0x00a2 }
            java.util.Iterator r7 = r6.iterator()     // Catch:{ all -> 0x00a2 }
        L_0x0025:
            boolean r8 = r7.hasNext()     // Catch:{ all -> 0x00a2 }
            if (r8 == 0) goto L_0x0090
            java.lang.Object r8 = r7.next()     // Catch:{ all -> 0x00a2 }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r8 = (nodomain.freeyourgadget.gadgetbridge.entities.Device) r8     // Catch:{ all -> 0x00a2 }
            java.lang.String r9 = r8.getIdentifier()     // Catch:{ all -> 0x00a2 }
            android.content.SharedPreferences r9 = getDeviceSpecificSharedPrefs(r9)     // Catch:{ all -> 0x00a2 }
            if (r9 == 0) goto L_0x008b
            android.content.SharedPreferences$Editor r10 = r9.edit()     // Catch:{ all -> 0x00a2 }
            int r11 = r8.getType()     // Catch:{ all -> 0x00a2 }
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r11 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.fromKey(r11)     // Catch:{ all -> 0x00a2 }
            r12 = r19
            boolean r13 = r12.contains(r11)     // Catch:{ all -> 0x0087 }
            if (r13 == 0) goto L_0x0081
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x0087 }
            r13.<init>()     // Catch:{ all -> 0x0087 }
            java.lang.String r14 = "migrating global boolean preference "
            r13.append(r14)     // Catch:{ all -> 0x0087 }
            r13.append(r1)     // Catch:{ all -> 0x0087 }
            java.lang.String r14 = " for "
            r13.append(r14)     // Catch:{ all -> 0x0087 }
            java.lang.String r14 = r11.name()     // Catch:{ all -> 0x0087 }
            r13.append(r14)     // Catch:{ all -> 0x0087 }
            java.lang.String r14 = " "
            r13.append(r14)     // Catch:{ all -> 0x0087 }
            java.lang.String r14 = r8.getIdentifier()     // Catch:{ all -> 0x0087 }
            r13.append(r14)     // Catch:{ all -> 0x0087 }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x0087 }
            android.util.Log.i(r2, r13)     // Catch:{ all -> 0x0087 }
            r13 = r18
            r10.putBoolean(r13, r4)     // Catch:{ all -> 0x00a0 }
            goto L_0x0083
        L_0x0081:
            r13 = r18
        L_0x0083:
            r10.apply()     // Catch:{ all -> 0x00a0 }
            goto L_0x008f
        L_0x0087:
            r0 = move-exception
            r13 = r18
            goto L_0x00a7
        L_0x008b:
            r13 = r18
            r12 = r19
        L_0x008f:
            goto L_0x0025
        L_0x0090:
            r13 = r18
            r12 = r19
            r3.remove(r1)     // Catch:{ all -> 0x00a0 }
            r3.apply()     // Catch:{ all -> 0x00a0 }
            if (r5 == 0) goto L_0x009f
            r5.close()     // Catch:{ Exception -> 0x00b7 }
        L_0x009f:
            goto L_0x00c3
        L_0x00a0:
            r0 = move-exception
            goto L_0x00a7
        L_0x00a2:
            r0 = move-exception
            r13 = r18
            r12 = r19
        L_0x00a7:
            r6 = r0
            throw r6     // Catch:{ all -> 0x00a9 }
        L_0x00a9:
            r0 = move-exception
            r7 = r0
            if (r5 == 0) goto L_0x00b6
            r5.close()     // Catch:{ all -> 0x00b1 }
            goto L_0x00b6
        L_0x00b1:
            r0 = move-exception
            r8 = r0
            r6.addSuppressed(r8)     // Catch:{ Exception -> 0x00b7 }
        L_0x00b6:
            throw r7     // Catch:{ Exception -> 0x00b7 }
        L_0x00b7:
            r0 = move-exception
            goto L_0x00be
        L_0x00b9:
            r0 = move-exception
            r13 = r18
            r12 = r19
        L_0x00be:
            java.lang.String r5 = "error acquiring DB lock"
            android.util.Log.w(r2, r5)
        L_0x00c3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.GBApplication.migrateBooleanPrefToPerDevicePref(java.lang.String, java.lang.Boolean, java.lang.String, java.util.ArrayList):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:118:0x03ad, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x03ae, code lost:
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x03af, code lost:
        if (r12 != null) goto L_0x03b1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:?, code lost:
        r12.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x03b5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:?, code lost:
        r2.addSuppressed(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x019d, code lost:
        if (r10 == nodomain.freeyourgadget.gadgetbridge.model.DeviceType.AMAZFITCOR) goto L_0x019f;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:109:0x039f, B:116:0x03ac, B:121:0x03b1] */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x03ce A[SYNTHETIC, Splitter:B:135:0x03ce] */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0432 A[SYNTHETIC, Splitter:B:167:0x0432] */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x054a  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x05c1  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x05c6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void migratePrefs(int r38) {
        /*
            r37 = this;
            r1 = r37
            r2 = r38
            android.content.SharedPreferences r0 = sharedPrefs
            android.content.SharedPreferences$Editor r3 = r0.edit()
            java.lang.String r4 = "activity_user_gender"
            r6 = 2
            r7 = 0
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r7)
            r9 = 0
            if (r2 != 0) goto L_0x007c
            android.content.SharedPreferences r0 = sharedPrefs
            java.lang.String r10 = "mi_user_gender"
            java.lang.String r0 = r0.getString(r10, r9)
            android.content.SharedPreferences r10 = sharedPrefs
            java.lang.String r11 = "mi_user_height_cm"
            java.lang.String r10 = r10.getString(r11, r9)
            android.content.SharedPreferences r11 = sharedPrefs
            java.lang.String r12 = "mi_user_weight_kg"
            java.lang.String r11 = r11.getString(r12, r9)
            android.content.SharedPreferences r12 = sharedPrefs
            java.lang.String r13 = "mi_user_year_of_birth"
            java.lang.String r12 = r12.getString(r13, r9)
            if (r0 == 0) goto L_0x0058
            java.lang.String r13 = "male"
            boolean r13 = r13.equals(r0)
            if (r13 == 0) goto L_0x0041
            r13 = 1
            goto L_0x004c
        L_0x0041:
            java.lang.String r13 = "female"
            boolean r13 = r13.equals(r0)
            if (r13 == 0) goto L_0x004b
            r13 = 0
            goto L_0x004c
        L_0x004b:
            r13 = 2
        L_0x004c:
            java.lang.String r14 = java.lang.Integer.toString(r13)
            r3.putString(r4, r14)
            java.lang.String r14 = "mi_user_gender"
            r3.remove(r14)
        L_0x0058:
            if (r10 == 0) goto L_0x0064
            java.lang.String r13 = "activity_user_height_cm"
            r3.putString(r13, r10)
            java.lang.String r13 = "mi_user_height_cm"
            r3.remove(r13)
        L_0x0064:
            if (r11 == 0) goto L_0x0070
            java.lang.String r13 = "activity_user_weight_kg"
            r3.putString(r13, r11)
            java.lang.String r13 = "mi_user_weight_kg"
            r3.remove(r13)
        L_0x0070:
            if (r12 == 0) goto L_0x007c
            java.lang.String r13 = "activity_user_year_of_birth"
            r3.putString(r13, r12)
            java.lang.String r13 = "mi_user_year_of_birth"
            r3.remove(r13)
        L_0x007c:
            java.lang.String r10 = "GBApplication"
            if (r2 >= r6) goto L_0x0096
            r11 = 2
            android.content.SharedPreferences r0 = sharedPrefs     // Catch:{ Exception -> 0x0089 }
            int r0 = r0.getInt(r4, r6)     // Catch:{ Exception -> 0x0089 }
            r11 = r0
            goto L_0x008f
        L_0x0089:
            r0 = move-exception
            java.lang.String r12 = "Could not access legacy activity gender"
            android.util.Log.e(r10, r12, r0)
        L_0x008f:
            java.lang.String r0 = java.lang.Integer.toString(r11)
            r3.putString(r4, r0)
        L_0x0096:
            r0 = 3
            java.lang.String r4 = "device_time_offset_hours"
            java.lang.String r11 = "error acquiring DB lock"
            if (r2 >= r0) goto L_0x03c7
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r0 = acquireDB()     // Catch:{ Exception -> 0x03bd }
            r12 = r0
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r0 = r12.getDaoSession()     // Catch:{ all -> 0x03a7 }
            java.util.List r13 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getActiveDevices(r0)     // Catch:{ all -> 0x03a7 }
            java.util.Iterator r14 = r13.iterator()     // Catch:{ all -> 0x03a7 }
        L_0x00ae:
            boolean r15 = r14.hasNext()     // Catch:{ all -> 0x03a7 }
            java.lang.String r7 = "activate_display_on_lift_wrist"
            java.lang.String r9 = "disconnect_notification_end"
            java.lang.String r6 = "disconnect_notification_start"
            java.lang.String r5 = "disconnect_notification"
            if (r15 == 0) goto L_0x032a
            java.lang.Object r15 = r14.next()     // Catch:{ all -> 0x03a7 }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r15 = (nodomain.freeyourgadget.gadgetbridge.entities.Device) r15     // Catch:{ all -> 0x03a7 }
            java.lang.String r17 = r15.getIdentifier()     // Catch:{ all -> 0x03a7 }
            android.content.SharedPreferences r17 = getDeviceSpecificSharedPrefs(r17)     // Catch:{ all -> 0x03a7 }
            if (r17 == 0) goto L_0x030e
            android.content.SharedPreferences$Editor r18 = r17.edit()     // Catch:{ all -> 0x03a7 }
            r19 = r18
            r18 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x03a7 }
            r0.<init>()     // Catch:{ all -> 0x03a7 }
            r20 = r13
            java.lang.String r13 = r15.getIdentifier()     // Catch:{ all -> 0x03a7 }
            r0.append(r13)     // Catch:{ all -> 0x03a7 }
            java.lang.String r13 = "_lastSportsActivityTimeMillis"
            r0.append(r13)     // Catch:{ all -> 0x03a7 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x03a7 }
            android.content.SharedPreferences r13 = sharedPrefs     // Catch:{ all -> 0x03a7 }
            r1 = 0
            long r21 = r13.getLong(r0, r1)     // Catch:{ all -> 0x03a7 }
            r23 = r21
            r21 = r14
            r13 = r23
            int r22 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r22 == 0) goto L_0x010f
            java.lang.String r1 = "lastSportsActivityTimeMillis"
            r2 = r19
            r2.putLong(r1, r13)     // Catch:{ all -> 0x0108 }
            r3.remove(r0)     // Catch:{ all -> 0x0108 }
            goto L_0x0111
        L_0x0108:
            r0 = move-exception
            r2 = r0
            r1 = r10
            r19 = r11
            goto L_0x03ac
        L_0x010f:
            r2 = r19
        L_0x0111:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x03a7 }
            r1.<init>()     // Catch:{ all -> 0x03a7 }
            r19 = r0
            java.lang.String r0 = r15.getIdentifier()     // Catch:{ all -> 0x03a7 }
            r1.append(r0)     // Catch:{ all -> 0x03a7 }
            java.lang.String r0 = "_lastSyncTimeMillis"
            r1.append(r0)     // Catch:{ all -> 0x03a7 }
            java.lang.String r0 = r1.toString()     // Catch:{ all -> 0x03a7 }
            android.content.SharedPreferences r1 = sharedPrefs     // Catch:{ all -> 0x03a7 }
            r24 = r13
            r13 = 0
            long r22 = r1.getLong(r0, r13)     // Catch:{ all -> 0x03a7 }
            r26 = r22
            r1 = r10
            r19 = r11
            r10 = r26
            int r22 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r22 == 0) goto L_0x0145
            java.lang.String r13 = "lastSyncTimeMillis"
            r2.putLong(r13, r10)     // Catch:{ all -> 0x03a5 }
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
        L_0x0145:
            r13 = 0
            r14 = 0
            int r22 = r15.getType()     // Catch:{ all -> 0x03a5 }
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r22 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.fromKey(r22)     // Catch:{ all -> 0x03a5 }
            r23 = r22
            r22 = r0
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r0 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.AMAZFITBIP     // Catch:{ all -> 0x03a5 }
            r26 = r10
            r10 = r23
            if (r10 == r0) goto L_0x0163
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r0 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.AMAZFITCOR     // Catch:{ all -> 0x03a5 }
            if (r10 == r0) goto L_0x0163
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r0 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.AMAZFITCOR2     // Catch:{ all -> 0x03a5 }
            if (r10 != r0) goto L_0x0195
        L_0x0163:
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r11 = "amazfitbip_language"
            r23 = r13
            r13 = -1
            int r0 = r0.getInt(r11, r13)     // Catch:{ all -> 0x03a5 }
            java.lang.String r11 = "auto"
            r13 = r11
            java.lang.String r28 = "zh_CN"
            java.lang.String r29 = "zh_TW"
            java.lang.String r30 = "en_US"
            java.lang.String r31 = "es_ES"
            java.lang.String r32 = "ru_RU"
            java.lang.String r33 = "de_DE"
            java.lang.String r34 = "it_IT"
            java.lang.String r35 = "fr_FR"
            java.lang.String r36 = "tr_TR"
            java.lang.String[] r11 = new java.lang.String[]{r28, r29, r30, r31, r32, r33, r34, r35, r36}     // Catch:{ all -> 0x03a5 }
            if (r0 < 0) goto L_0x0191
            r23 = r13
            int r13 = r11.length     // Catch:{ all -> 0x03a5 }
            if (r0 >= r13) goto L_0x0193
            r13 = r11[r0]     // Catch:{ all -> 0x03a5 }
            goto L_0x0195
        L_0x0191:
            r23 = r13
        L_0x0193:
            r13 = r23
        L_0x0195:
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r0 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.AMAZFITBIP     // Catch:{ all -> 0x03a5 }
            java.lang.String r11 = "off"
            if (r10 == r0) goto L_0x019f
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r0 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.AMAZFITCOR     // Catch:{ all -> 0x03a5 }
            if (r10 != r0) goto L_0x01be
        L_0x019f:
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = r0.getString(r5, r11)     // Catch:{ all -> 0x03a5 }
            r2.putString(r5, r0)     // Catch:{ all -> 0x03a5 }
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r5 = "8:00"
            java.lang.String r0 = r0.getString(r6, r5)     // Catch:{ all -> 0x03a5 }
            r2.putString(r6, r0)     // Catch:{ all -> 0x03a5 }
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r5 = "22:00"
            java.lang.String r0 = r0.getString(r9, r5)     // Catch:{ all -> 0x03a5 }
            r2.putString(r9, r0)     // Catch:{ all -> 0x03a5 }
        L_0x01be:
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r0 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.MIBAND2     // Catch:{ all -> 0x03a5 }
            if (r10 == r0) goto L_0x01c6
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r0 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.MIBAND3     // Catch:{ all -> 0x03a5 }
            if (r10 != r0) goto L_0x01f1
        L_0x01c6:
            java.lang.String r0 = "do_not_disturb"
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r5 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r6 = "mi2_do_not_disturb"
            java.lang.String r5 = r5.getString(r6, r11)     // Catch:{ all -> 0x03a5 }
            r2.putString(r0, r5)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "do_not_disturb_start"
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r5 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r6 = "mi2_do_not_disturb_start"
            java.lang.String r9 = "1:00"
            java.lang.String r5 = r5.getString(r6, r9)     // Catch:{ all -> 0x03a5 }
            r2.putString(r0, r5)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "do_not_disturb_end"
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r5 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r6 = "mi2_do_not_disturb_end"
            java.lang.String r9 = "6:00"
            java.lang.String r5 = r5.getString(r6, r9)     // Catch:{ all -> 0x03a5 }
            r2.putString(r0, r5)     // Catch:{ all -> 0x03a5 }
        L_0x01f1:
            java.lang.String r0 = r15.getManufacturer()     // Catch:{ all -> 0x03a5 }
            java.lang.String r5 = "Huami"
            boolean r0 = r0.equals(r5)     // Catch:{ all -> 0x03a5 }
            if (r0 == 0) goto L_0x0224
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = r0.getString(r7, r11)     // Catch:{ all -> 0x03a5 }
            r2.putString(r7, r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "display_on_lift_start"
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r5 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r6 = "display_on_lift_start"
            java.lang.String r7 = "0:00"
            java.lang.String r5 = r5.getString(r6, r7)     // Catch:{ all -> 0x03a5 }
            r2.putString(r0, r5)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "display_on_lift_end"
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r5 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r6 = "display_on_lift_end"
            java.lang.String r7 = "0:00"
            java.lang.String r5 = r5.getString(r6, r7)     // Catch:{ all -> 0x03a5 }
            r2.putString(r0, r5)     // Catch:{ all -> 0x03a5 }
        L_0x0224:
            int[] r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.C08882.$SwitchMap$nodomain$freeyourgadget$gadgetbridge$model$DeviceType     // Catch:{ all -> 0x03a5 }
            int r5 = r10.ordinal()     // Catch:{ all -> 0x03a5 }
            r0 = r0[r5]     // Catch:{ all -> 0x03a5 }
            r5 = 1
            if (r0 == r5) goto L_0x02dc
            r5 = 2
            if (r0 == r5) goto L_0x02d1
            r5 = 3
            if (r0 == r5) goto L_0x02c5
            r5 = 4
            if (r0 == r5) goto L_0x028e
            r5 = 5
            if (r0 == r5) goto L_0x023e
            r6 = 0
            goto L_0x02fc
        L_0x023e:
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r5 = "miband3_language"
            java.lang.String r6 = "auto"
            java.lang.String r0 = r0.getString(r5, r6)     // Catch:{ all -> 0x03a5 }
            r13 = r0
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r5 = "miband3_display_items"
            r6 = 0
            java.util.Set r0 = r0.getStringSet(r5, r6)     // Catch:{ all -> 0x03a5 }
            r14 = r0
            java.lang.String r0 = "swipe_unlock"
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r5 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r6 = "mi3_band_screen_unlock"
            r7 = 0
            boolean r5 = r5.getBoolean(r6, r7)     // Catch:{ all -> 0x03a5 }
            r2.putBoolean(r0, r5)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "night_mode"
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r5 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r6 = "mi3_night_mode"
            java.lang.String r5 = r5.getString(r6, r11)     // Catch:{ all -> 0x03a5 }
            r2.putString(r0, r5)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "night_mode_start"
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r5 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r6 = "mi3_night_mode_start"
            java.lang.String r7 = "16:00"
            java.lang.String r5 = r5.getString(r6, r7)     // Catch:{ all -> 0x03a5 }
            r2.putString(r0, r5)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "night_mode_end"
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r5 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r6 = "mi3_night_mode_end"
            java.lang.String r7 = "7:00"
            java.lang.String r5 = r5.getString(r6, r7)     // Catch:{ all -> 0x03a5 }
            r2.putString(r0, r5)     // Catch:{ all -> 0x03a5 }
            r6 = 0
            goto L_0x02fc
        L_0x028e:
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r5 = "mi2_display_items"
            r6 = 0
            java.util.Set r0 = r0.getStringSet(r5, r6)     // Catch:{ all -> 0x03a5 }
            r14 = r0
            java.lang.String r0 = "mi2_enable_text_notifications"
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r5 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r6 = "mi2_enable_text_notifications"
            r7 = 1
            boolean r5 = r5.getBoolean(r6, r7)     // Catch:{ all -> 0x03a5 }
            r2.putBoolean(r0, r5)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "mi2_dateformat"
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r5 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r6 = "mi2_dateformat"
            java.lang.String r7 = "dateformat_time"
            java.lang.String r5 = r5.getString(r6, r7)     // Catch:{ all -> 0x03a5 }
            r2.putString(r0, r5)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "rotate_wrist_to_cycle_info"
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r5 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r6 = "mi2_rotate_wrist_to_switch_info"
            r7 = 0
            boolean r5 = r5.getBoolean(r6, r7)     // Catch:{ all -> 0x03a5 }
            r2.putBoolean(r0, r5)     // Catch:{ all -> 0x03a5 }
            r6 = 0
            goto L_0x02fc
        L_0x02c5:
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r5 = "bip_display_items"
            r6 = 0
            java.util.Set r0 = r0.getStringSet(r5, r6)     // Catch:{ all -> 0x03a5 }
            r14 = r0
            r6 = 0
            goto L_0x02fc
        L_0x02d1:
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r5 = "cor_display_items"
            r6 = 0
            java.util.Set r0 = r0.getStringSet(r5, r6)     // Catch:{ all -> 0x03a5 }
            r14 = r0
            goto L_0x02fc
        L_0x02dc:
            r6 = 0
            java.lang.String r0 = "low_latency_fw_update"
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r5 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r7 = "mi_low_latency_fw_update"
            r9 = 1
            boolean r5 = r5.getBoolean(r7, r9)     // Catch:{ all -> 0x03a5 }
            r2.putBoolean(r0, r5)     // Catch:{ all -> 0x03a5 }
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs     // Catch:{ all -> 0x03a5 }
            java.lang.String r5 = "mi_device_time_offset_hours"
            r7 = 0
            int r0 = r0.getInt(r5, r7)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = java.lang.String.valueOf(r0)     // Catch:{ all -> 0x03a5 }
            r2.putString(r4, r0)     // Catch:{ all -> 0x03a5 }
        L_0x02fc:
            if (r14 == 0) goto L_0x0303
            java.lang.String r0 = "display_items"
            r2.putStringSet(r0, r14)     // Catch:{ all -> 0x03a5 }
        L_0x0303:
            if (r13 == 0) goto L_0x030a
            java.lang.String r0 = "language"
            r2.putString(r0, r13)     // Catch:{ all -> 0x03a5 }
        L_0x030a:
            r2.apply()     // Catch:{ all -> 0x03a5 }
            goto L_0x0318
        L_0x030e:
            r18 = r0
            r1 = r10
            r19 = r11
            r20 = r13
            r21 = r14
            r6 = 0
        L_0x0318:
            r2 = r38
            r10 = r1
            r9 = r6
            r0 = r18
            r11 = r19
            r13 = r20
            r14 = r21
            r6 = 2
            r7 = 0
            r1 = r37
            goto L_0x00ae
        L_0x032a:
            r18 = r0
            r1 = r10
            r19 = r11
            r20 = r13
            java.lang.String r0 = "amazfitbip_language"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "bip_display_items"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "cor_display_items"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            r3.remove(r5)     // Catch:{ all -> 0x03a5 }
            r3.remove(r6)     // Catch:{ all -> 0x03a5 }
            r3.remove(r9)     // Catch:{ all -> 0x03a5 }
            r3.remove(r7)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "display_on_lift_start"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "display_on_lift_end"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "mi_low_latency_fw_update"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "mi_device_time_offset_hours"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "mi2_do_not_disturb"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "mi2_do_not_disturb_start"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "mi2_do_not_disturb_end"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "mi2_dateformat"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "mi2_display_items"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "mi2_rotate_wrist_to_switch_info"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "mi2_enable_text_notifications"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "mi3_band_screen_unlock"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "mi3_night_mode"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "mi3_night_mode_start"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "mi3_night_mode_end"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            java.lang.String r0 = "miband3_language"
            r3.remove(r0)     // Catch:{ all -> 0x03a5 }
            if (r12 == 0) goto L_0x03a2
            r12.close()     // Catch:{ Exception -> 0x03bb }
        L_0x03a2:
            r2 = r19
            goto L_0x03c9
        L_0x03a5:
            r0 = move-exception
            goto L_0x03ab
        L_0x03a7:
            r0 = move-exception
            r1 = r10
            r19 = r11
        L_0x03ab:
            r2 = r0
        L_0x03ac:
            throw r2     // Catch:{ all -> 0x03ad }
        L_0x03ad:
            r0 = move-exception
            r5 = r0
            if (r12 == 0) goto L_0x03ba
            r12.close()     // Catch:{ all -> 0x03b5 }
            goto L_0x03ba
        L_0x03b5:
            r0 = move-exception
            r6 = r0
            r2.addSuppressed(r6)     // Catch:{ Exception -> 0x03bb }
        L_0x03ba:
            throw r5     // Catch:{ Exception -> 0x03bb }
        L_0x03bb:
            r0 = move-exception
            goto L_0x03c1
        L_0x03bd:
            r0 = move-exception
            r1 = r10
            r19 = r11
        L_0x03c1:
            r2 = r19
            android.util.Log.w(r1, r2)
            goto L_0x03c9
        L_0x03c7:
            r1 = r10
            r2 = r11
        L_0x03c9:
            r0 = 4
            r5 = r38
            if (r5 >= r0) goto L_0x042f
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r0 = acquireDB()     // Catch:{ Exception -> 0x042b }
            r6 = r0
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r0 = r6.getDaoSession()     // Catch:{ all -> 0x041a }
            java.util.List r7 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getActiveDevices(r0)     // Catch:{ all -> 0x041a }
            java.util.Iterator r9 = r7.iterator()     // Catch:{ all -> 0x041a }
        L_0x03df:
            boolean r10 = r9.hasNext()     // Catch:{ all -> 0x041a }
            if (r10 == 0) goto L_0x0414
            java.lang.Object r10 = r9.next()     // Catch:{ all -> 0x041a }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r10 = (nodomain.freeyourgadget.gadgetbridge.entities.Device) r10     // Catch:{ all -> 0x041a }
            java.lang.String r11 = r10.getIdentifier()     // Catch:{ all -> 0x041a }
            android.content.SharedPreferences r11 = getDeviceSpecificSharedPrefs(r11)     // Catch:{ all -> 0x041a }
            android.content.SharedPreferences$Editor r12 = r11.edit()     // Catch:{ all -> 0x041a }
            int r13 = r10.getType()     // Catch:{ all -> 0x041a }
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r13 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.fromKey(r13)     // Catch:{ all -> 0x041a }
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r14 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.MIBAND     // Catch:{ all -> 0x041a }
            if (r13 != r14) goto L_0x0410
            r14 = 0
            int r15 = r11.getInt(r4, r14)     // Catch:{ all -> 0x041a }
            r14 = r15
            java.lang.String r15 = java.lang.Integer.toString(r14)     // Catch:{ all -> 0x041a }
            r12.putString(r4, r15)     // Catch:{ all -> 0x041a }
        L_0x0410:
            r12.apply()     // Catch:{ all -> 0x041a }
            goto L_0x03df
        L_0x0414:
            if (r6 == 0) goto L_0x0419
            r6.close()     // Catch:{ Exception -> 0x042b }
        L_0x0419:
            goto L_0x042f
        L_0x041a:
            r0 = move-exception
            r4 = r0
            throw r4     // Catch:{ all -> 0x041d }
        L_0x041d:
            r0 = move-exception
            r7 = r0
            if (r6 == 0) goto L_0x042a
            r6.close()     // Catch:{ all -> 0x0425 }
            goto L_0x042a
        L_0x0425:
            r0 = move-exception
            r9 = r0
            r4.addSuppressed(r9)     // Catch:{ Exception -> 0x042b }
        L_0x042a:
            throw r7     // Catch:{ Exception -> 0x042b }
        L_0x042b:
            r0 = move-exception
            android.util.Log.w(r1, r2)
        L_0x042f:
            r0 = 5
            if (r5 >= r0) goto L_0x0547
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r0 = acquireDB()     // Catch:{ Exception -> 0x0543 }
            r4 = r0
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r0 = r4.getDaoSession()     // Catch:{ all -> 0x0532 }
            java.util.List r6 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getActiveDevices(r0)     // Catch:{ all -> 0x0532 }
            java.util.Iterator r7 = r6.iterator()     // Catch:{ all -> 0x0532 }
        L_0x0443:
            boolean r9 = r7.hasNext()     // Catch:{ all -> 0x0532 }
            if (r9 == 0) goto L_0x0504
            java.lang.Object r9 = r7.next()     // Catch:{ all -> 0x0532 }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r9 = (nodomain.freeyourgadget.gadgetbridge.entities.Device) r9     // Catch:{ all -> 0x0532 }
            java.lang.String r10 = r9.getIdentifier()     // Catch:{ all -> 0x0532 }
            android.content.SharedPreferences r10 = getDeviceSpecificSharedPrefs(r10)     // Catch:{ all -> 0x0532 }
            if (r10 == 0) goto L_0x04f6
            android.content.SharedPreferences$Editor r11 = r10.edit()     // Catch:{ all -> 0x0532 }
            int r12 = r9.getType()     // Catch:{ all -> 0x0532 }
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r12 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.fromKey(r12)     // Catch:{ all -> 0x0532 }
            r13 = 0
            r14 = 0
            r15 = 0
            int[] r16 = nodomain.freeyourgadget.gadgetbridge.GBApplication.C08882.$SwitchMap$nodomain$freeyourgadget$gadgetbridge$model$DeviceType     // Catch:{ all -> 0x0532 }
            int r17 = r12.ordinal()     // Catch:{ all -> 0x0532 }
            r16 = r16[r17]     // Catch:{ all -> 0x0532 }
            r17 = r0
            java.lang.String r0 = "left"
            switch(r16) {
                case 1: goto L_0x04cf;
                case 2: goto L_0x04cf;
                case 3: goto L_0x04cf;
                case 4: goto L_0x04cf;
                case 5: goto L_0x04cf;
                case 6: goto L_0x04cf;
                case 7: goto L_0x04cf;
                case 8: goto L_0x04b6;
                case 9: goto L_0x049d;
                case 10: goto L_0x047d;
                default: goto L_0x0477;
            }
        L_0x0477:
            r16 = r6
            r18 = r7
            goto L_0x04dd
        L_0x047d:
            r16 = r6
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r6 = prefs     // Catch:{ all -> 0x0532 }
            r18 = r7
            java.lang.String r7 = "zetime_wrist"
            java.lang.String r0 = r6.getString(r7, r0)     // Catch:{ all -> 0x0532 }
            r13 = r0
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs     // Catch:{ all -> 0x0532 }
            java.lang.String r6 = "zetime_timeformat"
            r7 = 1
            int r0 = r0.getInt(r6, r7)     // Catch:{ all -> 0x0532 }
            r6 = 2
            if (r0 != r6) goto L_0x0499
            java.lang.String r0 = "am/pm"
            goto L_0x049b
        L_0x0499:
            java.lang.String r0 = "24h"
        L_0x049b:
            r15 = r0
            goto L_0x04dd
        L_0x049d:
            r16 = r6
            r18 = r7
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r6 = prefs     // Catch:{ all -> 0x0532 }
            java.lang.String r7 = "id115_wrist"
            java.lang.String r0 = r6.getString(r7, r0)     // Catch:{ all -> 0x0532 }
            r13 = r0
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs     // Catch:{ all -> 0x0532 }
            java.lang.String r6 = "id115_screen_orientation"
            java.lang.String r7 = "horizontal"
            java.lang.String r0 = r0.getString(r6, r7)     // Catch:{ all -> 0x0532 }
            r14 = r0
            goto L_0x04dd
        L_0x04b6:
            r16 = r6
            r18 = r7
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r6 = prefs     // Catch:{ all -> 0x0532 }
            java.lang.String r7 = "hplus_wrist"
            java.lang.String r0 = r6.getString(r7, r0)     // Catch:{ all -> 0x0532 }
            r13 = r0
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = prefs     // Catch:{ all -> 0x0532 }
            java.lang.String r6 = "hplus_timeformat"
            java.lang.String r7 = "24h"
            java.lang.String r0 = r0.getString(r6, r7)     // Catch:{ all -> 0x0532 }
            r15 = r0
            goto L_0x04dd
        L_0x04cf:
            r16 = r6
            r18 = r7
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r6 = prefs     // Catch:{ all -> 0x0532 }
            java.lang.String r7 = "mi_wearside"
            java.lang.String r0 = r6.getString(r7, r0)     // Catch:{ all -> 0x0532 }
            r13 = r0
        L_0x04dd:
            if (r13 == 0) goto L_0x04e4
            java.lang.String r0 = "wearlocation"
            r11.putString(r0, r13)     // Catch:{ all -> 0x0532 }
        L_0x04e4:
            if (r14 == 0) goto L_0x04eb
            java.lang.String r0 = "screen_orientation"
            r11.putString(r0, r14)     // Catch:{ all -> 0x0532 }
        L_0x04eb:
            if (r15 == 0) goto L_0x04f2
            java.lang.String r0 = "timeformat"
            r11.putString(r0, r15)     // Catch:{ all -> 0x0532 }
        L_0x04f2:
            r11.apply()     // Catch:{ all -> 0x0532 }
            goto L_0x04fc
        L_0x04f6:
            r17 = r0
            r16 = r6
            r18 = r7
        L_0x04fc:
            r6 = r16
            r0 = r17
            r7 = r18
            goto L_0x0443
        L_0x0504:
            r17 = r0
            r16 = r6
            java.lang.String r0 = "hplus_timeformat"
            r3.remove(r0)     // Catch:{ all -> 0x0532 }
            java.lang.String r0 = "hplus_wrist"
            r3.remove(r0)     // Catch:{ all -> 0x0532 }
            java.lang.String r0 = "id115_wrist"
            r3.remove(r0)     // Catch:{ all -> 0x0532 }
            java.lang.String r0 = "id115_screen_orientation"
            r3.remove(r0)     // Catch:{ all -> 0x0532 }
            java.lang.String r0 = "mi_wearside"
            r3.remove(r0)     // Catch:{ all -> 0x0532 }
            java.lang.String r0 = "zetime_timeformat"
            r3.remove(r0)     // Catch:{ all -> 0x0532 }
            java.lang.String r0 = "zetime_wrist"
            r3.remove(r0)     // Catch:{ all -> 0x0532 }
            if (r4 == 0) goto L_0x0531
            r4.close()     // Catch:{ Exception -> 0x0543 }
        L_0x0531:
            goto L_0x0547
        L_0x0532:
            r0 = move-exception
            r6 = r0
            throw r6     // Catch:{ all -> 0x0535 }
        L_0x0535:
            r0 = move-exception
            r7 = r0
            if (r4 == 0) goto L_0x0542
            r4.close()     // Catch:{ all -> 0x053d }
            goto L_0x0542
        L_0x053d:
            r0 = move-exception
            r9 = r0
            r6.addSuppressed(r9)     // Catch:{ Exception -> 0x0543 }
        L_0x0542:
            throw r7     // Catch:{ Exception -> 0x0543 }
        L_0x0543:
            r0 = move-exception
            android.util.Log.w(r1, r2)
        L_0x0547:
            r0 = 6
            if (r5 >= r0) goto L_0x05c1
            java.util.ArrayList r0 = new java.util.ArrayList
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r1 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.MIBAND2
            java.util.List r1 = java.util.Collections.singletonList(r1)
            r0.<init>(r1)
            java.lang.String r1 = "mi2_enable_button_action"
            java.lang.String r2 = "button_action_enable"
            r4 = r37
            r4.migrateBooleanPrefToPerDevicePref(r1, r8, r2, r0)
            java.util.ArrayList r0 = new java.util.ArrayList
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r1 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.MIBAND2
            java.util.List r1 = java.util.Collections.singletonList(r1)
            r0.<init>(r1)
            java.lang.String r1 = "mi2_button_action_vibrate"
            java.lang.String r2 = "button_action_vibrate"
            r4.migrateBooleanPrefToPerDevicePref(r1, r8, r2, r0)
            java.util.ArrayList r0 = new java.util.ArrayList
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r1 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.MIBAND2
            java.util.List r1 = java.util.Collections.singletonList(r1)
            r0.<init>(r1)
            java.lang.String r1 = "mi_button_press_count"
            java.lang.String r2 = "6"
            java.lang.String r6 = "button_action_press_count"
            r4.migrateStringPrefToPerDevicePref(r1, r2, r6, r0)
            java.util.ArrayList r0 = new java.util.ArrayList
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r1 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.MIBAND2
            java.util.List r1 = java.util.Collections.singletonList(r1)
            r0.<init>(r1)
            java.lang.String r1 = "mi_button_press_count_max_delay"
            java.lang.String r2 = "2000"
            java.lang.String r6 = "button_action_press_max_interval"
            r4.migrateStringPrefToPerDevicePref(r1, r2, r6, r0)
            java.util.ArrayList r0 = new java.util.ArrayList
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r1 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.MIBAND2
            java.util.List r1 = java.util.Collections.singletonList(r1)
            r0.<init>(r1)
            java.lang.String r1 = "mi_button_press_count_match_delay"
            java.lang.String r2 = "0"
            java.lang.String r6 = "button_action_broadcast_delay"
            r4.migrateStringPrefToPerDevicePref(r1, r2, r6, r0)
            java.util.ArrayList r0 = new java.util.ArrayList
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r1 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.MIBAND2
            java.util.List r1 = java.util.Collections.singletonList(r1)
            r0.<init>(r1)
            java.lang.String r1 = "mi_button_press_broadcast"
            java.lang.String r2 = "nodomain.freeyourgadget.gadgetbridge.ButtonPressed"
            java.lang.String r6 = "button_action_broadcast"
            r4.migrateStringPrefToPerDevicePref(r1, r2, r6, r0)
            goto L_0x05c3
        L_0x05c1:
            r4 = r37
        L_0x05c3:
            r0 = 7
            if (r5 >= r0) goto L_0x05e5
            java.util.ArrayList r0 = new java.util.ArrayList
            r1 = 2
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType[] r1 = new nodomain.freeyourgadget.gadgetbridge.model.DeviceType[r1]
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r2 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.MIBAND
            r6 = 0
            r1[r6] = r2
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r2 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.MIBAND2
            r6 = 1
            r1[r6] = r2
            java.util.List r1 = java.util.Arrays.asList(r1)
            r0.<init>(r1)
            java.lang.String r1 = "mi_reserve_alarm_calendar"
            java.lang.String r2 = "0"
            java.lang.String r6 = "reserve_alarms_calendar"
            r4.migrateStringPrefToPerDevicePref(r1, r2, r6, r0)
        L_0x05e5:
            r0 = 7
            java.lang.String r0 = java.lang.Integer.toString(r0)
            java.lang.String r1 = "shared_preferences_version"
            r3.putString(r1, r0)
            r3.apply()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.GBApplication.migratePrefs(int):void");
    }

    public static SharedPreferences getDeviceSpecificSharedPrefs(String deviceIdentifier) {
        if (deviceIdentifier == null || deviceIdentifier.isEmpty()) {
            return null;
        }
        GBApplication gBApplication = context;
        return gBApplication.getSharedPreferences("devicesettings_" + deviceIdentifier, 0);
    }

    public static void setLanguage(String lang) {
        if (lang.equals("default")) {
            language = Resources.getSystem().getConfiguration().locale;
        } else {
            language = new Locale(lang);
        }
        updateLanguage(language);
    }

    public static void updateLanguage(Locale locale) {
        AndroidUtils.setLanguage(context, locale);
        Intent intent = new Intent();
        intent.setAction(ACTION_LANGUAGE_CHANGE);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static LimitedQueue getIDSenderLookup() {
        return mIDSenderLookup;
    }

    public static boolean isDarkThemeEnabled() {
        return prefs.getString("pref_key_theme", context.getString(C0889R.string.pref_theme_value_light)).equals(context.getString(C0889R.string.pref_theme_value_dark));
    }

    public static int getTextColor(Context context2) {
        TypedValue typedValue = new TypedValue();
        context2.getTheme().resolveAttribute(C0889R.attr.textColorPrimary, typedValue, true);
        return typedValue.data;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateLanguage(getLanguage());
    }

    public static int getBackgroundColor(Context context2) {
        TypedValue typedValue = new TypedValue();
        context2.getTheme().resolveAttribute(16842964, typedValue, true);
        return typedValue.data;
    }

    public static Prefs getPrefs() {
        return prefs;
    }

    public static GBPrefs getGBPrefs() {
        return gbPrefs;
    }

    public DeviceManager getDeviceManager() {
        return this.deviceManager;
    }

    public static GBApplication app() {
        return app;
    }

    public static Locale getLanguage() {
        return language;
    }

    public String getVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 128).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            C1238GB.log("Unable to determine Gadgetbridge's version", 2, e);
            return "0.0.0";
        }
    }

    public String getNameAndVersion() {
        try {
            return String.format("%s %s", new Object[]{getPackageManager().getApplicationInfo(getContext().getPackageName(), 128).name, getPackageManager().getPackageInfo(getPackageName(), 128).versionName});
        } catch (PackageManager.NameNotFoundException e) {
            C1238GB.log("Unable to determine Gadgetbridge's name/version", 2, e);
            return DATABASE_NAME;
        }
    }
}
