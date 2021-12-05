package nodomain.freeyourgadget.gadgetbridge.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.devicesettings.DeviceSettingsPreferenceConst;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.entities.ActivityDescription;
import nodomain.freeyourgadget.gadgetbridge.entities.ActivityDescriptionDao;
import nodomain.freeyourgadget.gadgetbridge.entities.Alarm;
import nodomain.freeyourgadget.gadgetbridge.entities.AlarmDao;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.DeviceAttributes;
import nodomain.freeyourgadget.gadgetbridge.entities.DeviceDao;
import nodomain.freeyourgadget.gadgetbridge.entities.Tag;
import nodomain.freeyourgadget.gadgetbridge.entities.TagDao;
import nodomain.freeyourgadget.gadgetbridge.entities.User;
import nodomain.freeyourgadget.gadgetbridge.entities.UserAttributes;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.model.ValidByDate;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;
import nodomain.freeyourgadget.gadgetbridge.util.FileUtils;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.query.QueryBuilder;
import p008de.greenrobot.dao.query.WhereCondition;

public class DBHelper {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DBHelper.class);
    private final Context context;

    public DBHelper(Context context2) {
        this.context = context2;
    }

    private String getClosedDBPath(DBHandler dbHandler) throws IllegalStateException {
        SQLiteDatabase db = dbHandler.getDatabase();
        String path = db.getPath();
        dbHandler.closeDb();
        if (!db.isOpen()) {
            return path;
        }
        throw new IllegalStateException("Database must be closed");
    }

    public File exportDB(DBHandler dbHandler, File toDir) throws IllegalStateException, IOException {
        try {
            File sourceFile = new File(getClosedDBPath(dbHandler));
            File destFile = new File(toDir, sourceFile.getName());
            if (destFile.exists()) {
                destFile.renameTo(new File(toDir, destFile.getName() + "_" + getDate()));
            } else if (!toDir.exists()) {
                if (!toDir.mkdirs()) {
                    throw new IOException("Unable to create directory: " + toDir.getAbsolutePath());
                }
            }
            FileUtils.copyFile(sourceFile, destFile);
            return destFile;
        } finally {
            dbHandler.openDb();
        }
    }

    public void exportDB(DBHandler dbHandler, OutputStream dest) throws IOException {
        try {
            FileUtils.copyFileToStream(new File(getClosedDBPath(dbHandler)), dest);
        } finally {
            dbHandler.openDb();
        }
    }

    private String getDate() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(new Date());
    }

    public void importDB(DBHandler dbHandler, File fromFile) throws IllegalStateException, IOException {
        try {
            FileUtils.copyFile(fromFile, new File(getClosedDBPath(dbHandler)));
        } finally {
            dbHandler.openDb();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001a, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001b, code lost:
        if (r0 != null) goto L_0x001d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0021, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0022, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0025, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void validateDB(android.database.sqlite.SQLiteOpenHelper r5) throws java.io.IOException {
        /*
            r4 = this;
            android.database.sqlite.SQLiteDatabase r0 = r5.getReadableDatabase()
            boolean r1 = r0.isDatabaseIntegrityOk()     // Catch:{ all -> 0x0018 }
            if (r1 == 0) goto L_0x0010
            if (r0 == 0) goto L_0x000f
            r0.close()
        L_0x000f:
            return
        L_0x0010:
            java.io.IOException r1 = new java.io.IOException     // Catch:{ all -> 0x0018 }
            java.lang.String r2 = "Database integrity is not OK"
            r1.<init>(r2)     // Catch:{ all -> 0x0018 }
            throw r1     // Catch:{ all -> 0x0018 }
        L_0x0018:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x001a }
        L_0x001a:
            r2 = move-exception
            if (r0 == 0) goto L_0x0025
            r0.close()     // Catch:{ all -> 0x0021 }
            goto L_0x0025
        L_0x0021:
            r3 = move-exception
            r1.addSuppressed(r3)
        L_0x0025:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.database.DBHelper.validateDB(android.database.sqlite.SQLiteOpenHelper):void");
    }

    public static void dropTable(String tableName, SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS '" + tableName + "'");
    }

    public boolean existsDB(String dbName) {
        File path = this.context.getDatabasePath(dbName);
        return path != null && path.exists();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x004c, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x004d, code lost:
        if (r0 != null) goto L_0x004f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0053, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0054, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0058, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean existsColumn(java.lang.String r6, java.lang.String r7, android.database.sqlite.SQLiteDatabase r8) {
        /*
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "PRAGMA table_info('"
            r0.append(r1)
            r0.append(r6)
            java.lang.String r1 = "')"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 0
            android.database.Cursor r0 = r8.rawQuery(r0, r1)
            java.lang.String r1 = "name"
            int r1 = r0.getColumnIndex(r1)     // Catch:{ all -> 0x004a }
            r2 = 0
            r3 = 1
            if (r1 >= r3) goto L_0x002c
            if (r0 == 0) goto L_0x002b
            r0.close()
        L_0x002b:
            return r2
        L_0x002c:
            boolean r4 = r0.moveToNext()     // Catch:{ all -> 0x004a }
            if (r4 == 0) goto L_0x0044
            java.lang.String r4 = r0.getString(r1)     // Catch:{ all -> 0x004a }
            boolean r5 = r7.equals(r4)     // Catch:{ all -> 0x004a }
            if (r5 == 0) goto L_0x0043
            if (r0 == 0) goto L_0x0042
            r0.close()
        L_0x0042:
            return r3
        L_0x0043:
            goto L_0x002c
        L_0x0044:
            if (r0 == 0) goto L_0x0049
            r0.close()
        L_0x0049:
            return r2
        L_0x004a:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x004c }
        L_0x004c:
            r2 = move-exception
            if (r0 == 0) goto L_0x0057
            r0.close()     // Catch:{ all -> 0x0053 }
            goto L_0x0057
        L_0x0053:
            r3 = move-exception
            r1.addSuppressed(r3)
        L_0x0057:
            goto L_0x0059
        L_0x0058:
            throw r2
        L_0x0059:
            goto L_0x0058
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.database.DBHelper.existsColumn(java.lang.String, java.lang.String, android.database.sqlite.SQLiteDatabase):boolean");
    }

    public static String getWithoutRowId() {
        if (GBApplication.isRunningLollipopOrLater()) {
            return " WITHOUT ROWID;";
        }
        return "";
    }

    public static User getUser(DaoSession session) {
        User user;
        ActivityUser prefsUser = new ActivityUser();
        List<User> users = session.getUserDao().loadAll();
        if (users.isEmpty()) {
            user = createUser(prefsUser, session);
        } else {
            user = users.get(0);
            ensureUserUpToDate(user, prefsUser, session);
        }
        ensureUserAttributes(user, prefsUser, session);
        return user;
    }

    public static UserAttributes getUserAttributes(User user) {
        List<UserAttributes> list = user.getUserAttributesList();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        throw new IllegalStateException("user has no attributes");
    }

    private static User createUser(ActivityUser prefsUser, DaoSession session) {
        User user = new User();
        ensureUserUpToDate(user, prefsUser, session);
        return user;
    }

    private static void ensureUserUpToDate(User user, ActivityUser prefsUser, DaoSession session) {
        if (!isUserUpToDate(user, prefsUser)) {
            user.setName(prefsUser.getName());
            user.setBirthday(prefsUser.getUserBirthday());
            user.setGender(prefsUser.getGender());
            if (user.getId() == null) {
                session.getUserDao().insert(user);
            } else {
                session.getUserDao().update(user);
            }
        }
    }

    public static boolean isUserUpToDate(User user, ActivityUser prefsUser) {
        if (Objects.equals(user.getName(), prefsUser.getName()) && Objects.equals(user.getBirthday(), prefsUser.getUserBirthday()) && user.getGender() == prefsUser.getGender()) {
            return true;
        }
        return false;
    }

    private static void ensureUserAttributes(User user, ActivityUser prefsUser, DaoSession session) {
        UserAttributes[] previousUserAttributes = new UserAttributes[1];
        if (!hasUpToDateUserAttributes(user.getUserAttributesList(), prefsUser, previousUserAttributes)) {
            Calendar now = DateTimeUtils.getCalendarUTC();
            invalidateUserAttributes(previousUserAttributes[0], now, session);
            UserAttributes attributes = new UserAttributes();
            attributes.setValidFromUTC(now.getTime());
            attributes.setHeightCM(prefsUser.getHeightCm());
            attributes.setWeightKG(prefsUser.getWeightKg());
            attributes.setSleepGoalHPD(Integer.valueOf(prefsUser.getSleepDuration()));
            attributes.setStepsGoalSPD(Integer.valueOf(prefsUser.getStepsGoal()));
            attributes.setUserId(user.getId().longValue());
            session.getUserAttributesDao().insert(attributes);
            user.resetUserAttributesList();
        }
    }

    private static void invalidateUserAttributes(UserAttributes userAttributes, Calendar now, DaoSession session) {
        if (userAttributes != null) {
            Calendar invalid = (Calendar) now.clone();
            invalid.add(12, -1);
            userAttributes.setValidToUTC(invalid.getTime());
            session.getUserAttributesDao().update(userAttributes);
        }
    }

    private static boolean hasUpToDateUserAttributes(List<UserAttributes> userAttributes, ActivityUser prefsUser, UserAttributes[] outPreviousUserAttributes) {
        for (UserAttributes attr : userAttributes) {
            if (isValidNow(attr)) {
                if (isEqual(attr, prefsUser)) {
                    return true;
                }
                outPreviousUserAttributes[0] = attr;
            }
        }
        return false;
    }

    private static boolean isValidNow(ValidByDate element) {
        return isValid(element, DateTimeUtils.getCalendarUTC().getTime());
    }

    private static boolean isValid(ValidByDate element, Date nowUTC) {
        Date validFromUTC = element.getValidFromUTC();
        Date validToUTC = element.getValidToUTC();
        if (nowUTC.before(validFromUTC)) {
            return false;
        }
        if (validToUTC == null || !nowUTC.after(validToUTC)) {
            return true;
        }
        return false;
    }

    private static boolean isEqual(UserAttributes attr, ActivityUser prefsUser) {
        if (prefsUser.getHeightCm() != attr.getHeightCM()) {
            Logger logger = LOG;
            logger.info("user height changed to " + prefsUser.getHeightCm() + " from " + attr.getHeightCM());
            return false;
        } else if (prefsUser.getWeightKg() != attr.getWeightKG()) {
            Logger logger2 = LOG;
            logger2.info("user changed to " + prefsUser.getWeightKg() + " from " + attr.getWeightKG());
            return false;
        } else if (!Integer.valueOf(prefsUser.getSleepDuration()).equals(attr.getSleepGoalHPD())) {
            Logger logger3 = LOG;
            logger3.info("user sleep goal changed to " + prefsUser.getSleepDuration() + " from " + attr.getSleepGoalHPD());
            return false;
        } else if (Integer.valueOf(prefsUser.getStepsGoal()).equals(attr.getStepsGoalSPD())) {
            return true;
        } else {
            Logger logger4 = LOG;
            logger4.info("user steps goal changed to " + prefsUser.getStepsGoal() + " from " + attr.getStepsGoalSPD());
            return false;
        }
    }

    private static boolean isEqual(DeviceAttributes attr, GBDevice gbDevice) {
        if (Objects.equals(attr.getFirmwareVersion1(), gbDevice.getFirmwareVersion()) && Objects.equals(attr.getFirmwareVersion2(), gbDevice.getFirmwareVersion2()) && Objects.equals(attr.getVolatileIdentifier(), gbDevice.getVolatileAddress())) {
            return true;
        }
        return false;
    }

    public static Device findDevice(GBDevice gbDevice, DaoSession session) {
        List<Device> devices = session.getDeviceDao().queryBuilder().where(DeviceDao.Properties.Identifier.mo14989eq(gbDevice.getAddress()), new WhereCondition[0]).build().list();
        if (devices.size() > 0) {
            return devices.get(0);
        }
        return null;
    }

    public static List<Device> getActiveDevices(DaoSession daoSession) {
        return daoSession.getDeviceDao().loadAll();
    }

    public static Device getDevice(GBDevice gbDevice, DaoSession session) {
        Device device = findDevice(gbDevice, session);
        if (device == null) {
            device = createDevice(gbDevice, session);
        } else {
            ensureDeviceUpToDate(device, gbDevice, session);
        }
        if (gbDevice.isInitialized()) {
            ensureDeviceAttributes(device, gbDevice, session);
        }
        return device;
    }

    public static DeviceAttributes getDeviceAttributes(Device device) {
        List<DeviceAttributes> list = device.getDeviceAttributesList();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        throw new IllegalStateException("device has no attributes");
    }

    private static void ensureDeviceUpToDate(Device device, GBDevice gbDevice, DaoSession session) {
        if (!isDeviceUpToDate(device, gbDevice)) {
            device.setIdentifier(gbDevice.getAddress());
            device.setName(gbDevice.getName());
            device.setManufacturer(DeviceHelper.getInstance().getCoordinator(gbDevice).getManufacturer());
            device.setType(gbDevice.getType().getKey());
            device.setModel(gbDevice.getModel());
            if (device.getId() == null) {
                session.getDeviceDao().insert(device);
            } else {
                session.getDeviceDao().update(device);
            }
        }
    }

    private static boolean isDeviceUpToDate(Device device, GBDevice gbDevice) {
        if (!Objects.equals(device.getIdentifier(), gbDevice.getAddress()) || !Objects.equals(device.getName(), gbDevice.getName())) {
            return false;
        }
        if (Objects.equals(device.getManufacturer(), DeviceHelper.getInstance().getCoordinator(gbDevice).getManufacturer()) && device.getType() == gbDevice.getType().getKey() && Objects.equals(device.getModel(), gbDevice.getModel())) {
            return true;
        }
        return false;
    }

    private static Device createDevice(GBDevice gbDevice, DaoSession session) {
        Device device = new Device();
        ensureDeviceUpToDate(device, gbDevice, session);
        return device;
    }

    private static void ensureDeviceAttributes(Device device, GBDevice gbDevice, DaoSession session) {
        DeviceAttributes[] previousDeviceAttributes = new DeviceAttributes[1];
        if (!hasUpToDateDeviceAttributes(device.getDeviceAttributesList(), gbDevice, previousDeviceAttributes)) {
            Calendar now = DateTimeUtils.getCalendarUTC();
            invalidateDeviceAttributes(previousDeviceAttributes[0], now, session);
            DeviceAttributes attributes = new DeviceAttributes();
            attributes.setDeviceId(device.getId().longValue());
            attributes.setValidFromUTC(now.getTime());
            attributes.setFirmwareVersion1(gbDevice.getFirmwareVersion());
            attributes.setFirmwareVersion2(gbDevice.getFirmwareVersion2());
            attributes.setVolatileIdentifier(gbDevice.getVolatileAddress());
            session.getDeviceAttributesDao().insert(attributes);
            device.resetDeviceAttributesList();
        }
    }

    private static void invalidateDeviceAttributes(DeviceAttributes deviceAttributes, Calendar now, DaoSession session) {
        if (deviceAttributes != null) {
            Calendar invalid = (Calendar) now.clone();
            invalid.add(12, -1);
            deviceAttributes.setValidToUTC(invalid.getTime());
            session.getDeviceAttributesDao().update(deviceAttributes);
        }
    }

    private static boolean hasUpToDateDeviceAttributes(List<DeviceAttributes> deviceAttributes, GBDevice gbDevice, DeviceAttributes[] outPreviousAttributes) {
        for (DeviceAttributes attr : deviceAttributes) {
            if (isValidNow(attr)) {
                if (isEqual(attr, gbDevice)) {
                    return true;
                }
                outPreviousAttributes[0] = attr;
            }
        }
        return false;
    }

    public static List<ActivityDescription> findActivityDecriptions(User user, int tsFrom, int tsTo, DaoSession session) {
        Property tsFromProperty = ActivityDescriptionDao.Properties.TimestampFrom;
        Property tsToProperty = ActivityDescriptionDao.Properties.TimestampTo;
        Property userIdProperty = ActivityDescriptionDao.Properties.UserId;
        QueryBuilder<ActivityDescription> qb = session.getActivityDescriptionDao().queryBuilder();
        qb.where(userIdProperty.mo14989eq(user.getId()), isAtLeastPartiallyInRange(qb, tsFromProperty, tsToProperty, tsFrom, tsTo));
        return qb.build().list();
    }

    private static <T> WhereCondition isAtLeastPartiallyInRange(QueryBuilder<T> qb, Property tsFromProperty, Property tsToProperty, int tsFrom, int tsTo) {
        return qb.and(tsFromProperty.mo14998lt(Integer.valueOf(tsTo)), tsToProperty.mo14991gt(Integer.valueOf(tsFrom)), new WhereCondition[0]);
    }

    public static ActivityDescription createActivityDescription(User user, int tsFrom, int tsTo, DaoSession session) {
        ActivityDescription desc = new ActivityDescription();
        desc.setUser(user);
        desc.setTimestampFrom(tsFrom);
        desc.setTimestampTo(tsTo);
        session.getActivityDescriptionDao().insertOrReplace(desc);
        return desc;
    }

    public static Tag getTag(User user, String name, DaoSession session) {
        List<Tag> tags = session.getTagDao().queryBuilder().where(TagDao.Properties.UserId.mo14989eq(user.getId()), TagDao.Properties.Name.mo14989eq(name)).build().list();
        if (tags.size() > 0) {
            return tags.get(0);
        }
        return createTag(user, name, (String) null, session);
    }

    static Tag createTag(User user, String name, String description, DaoSession session) {
        Tag tag = new Tag();
        tag.setUserId(user.getId().longValue());
        tag.setName(name);
        tag.setDescription(description);
        session.getTagDao().insertOrReplace(tag);
        return tag;
    }

    public static List<Alarm> getAlarms(GBDevice gbDevice) {
        Throwable th;
        GBDevice gBDevice = gbDevice;
        DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(gBDevice);
        int reservedSlots = new Prefs(GBApplication.getDeviceSpecificSharedPrefs(gbDevice.getAddress())).getInt(DeviceSettingsPreferenceConst.PREF_RESERVER_ALARMS_CALENDAR, 0);
        int alarmSlots = coordinator.getAlarmSlotCount();
        try {
            DBHandler db = GBApplication.acquireDB();
            try {
                DaoSession daoSession = db.getDaoSession();
                User user = getUser(daoSession);
                Device dbDevice = findDevice(gBDevice, daoSession);
                if (dbDevice != null) {
                    AlarmDao alarmDao = daoSession.getAlarmDao();
                    Long deviceId = dbDevice.getId();
                    QueryBuilder<Alarm> qb = alarmDao.queryBuilder();
                    qb.where(AlarmDao.Properties.UserId.mo14989eq(user.getId()), AlarmDao.Properties.DeviceId.mo14989eq(deviceId)).orderAsc(AlarmDao.Properties.Position).limit(alarmSlots - reservedSlots);
                    List<Alarm> list = qb.build().list();
                    if (db != null) {
                        db.close();
                    }
                    return list;
                }
                if (db != null) {
                    db.close();
                }
                return Collections.emptyList();
            } catch (Throwable th2) {
                Throwable th3 = th2;
                if (db != null) {
                    db.close();
                }
                throw th3;
            }
        } catch (Exception e) {
            LOG.warn("Error reading alarms from db", (Throwable) e);
        } catch (Throwable th4) {
            th.addSuppressed(th4);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0014, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0015, code lost:
        if (r0 != null) goto L_0x0017;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x001f, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void store(nodomain.freeyourgadget.gadgetbridge.entities.Alarm r4) {
        /*
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x0020 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r1 = r0.getDaoSession()     // Catch:{ all -> 0x0012 }
            r1.insertOrReplace(r4)     // Catch:{ all -> 0x0012 }
            if (r0 == 0) goto L_0x0011
            r0.close()     // Catch:{ Exception -> 0x0020 }
        L_0x0011:
            goto L_0x0028
        L_0x0012:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0014 }
        L_0x0014:
            r2 = move-exception
            if (r0 == 0) goto L_0x001f
            r0.close()     // Catch:{ all -> 0x001b }
            goto L_0x001f
        L_0x001b:
            r3 = move-exception
            r1.addSuppressed(r3)     // Catch:{ Exception -> 0x0020 }
        L_0x001f:
            throw r2     // Catch:{ Exception -> 0x0020 }
        L_0x0020:
            r0 = move-exception
            org.slf4j.Logger r1 = LOG
            java.lang.String r2 = "Error acquiring database"
            r1.error((java.lang.String) r2, (java.lang.Throwable) r0)
        L_0x0028:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.database.DBHelper.store(nodomain.freeyourgadget.gadgetbridge.entities.Alarm):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0013, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0014, code lost:
        if (r0 != null) goto L_0x0016;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x001e, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void clearSession() {
        /*
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x001f }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r1 = r0.getDaoSession()     // Catch:{ all -> 0x0011 }
            r1.clear()     // Catch:{ all -> 0x0011 }
            if (r0 == 0) goto L_0x0010
            r0.close()     // Catch:{ Exception -> 0x001f }
        L_0x0010:
            goto L_0x0027
        L_0x0011:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0013 }
        L_0x0013:
            r2 = move-exception
            if (r0 == 0) goto L_0x001e
            r0.close()     // Catch:{ all -> 0x001a }
            goto L_0x001e
        L_0x001a:
            r3 = move-exception
            r1.addSuppressed(r3)     // Catch:{ Exception -> 0x001f }
        L_0x001e:
            throw r2     // Catch:{ Exception -> 0x001f }
        L_0x001f:
            r0 = move-exception
            org.slf4j.Logger r1 = LOG
            java.lang.String r2 = "Unable to acquire database to clear the session"
            r1.warn((java.lang.String) r2, (java.lang.Throwable) r0)
        L_0x0027:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.database.DBHelper.clearSession():void");
    }
}
