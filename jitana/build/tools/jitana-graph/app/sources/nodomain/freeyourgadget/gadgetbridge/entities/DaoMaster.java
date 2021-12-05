package nodomain.freeyourgadget.gadgetbridge.entities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import p008de.greenrobot.dao.AbstractDaoMaster;
import p008de.greenrobot.dao.identityscope.IdentityScopeType;

public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 23;

    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        UserAttributesDao.createTable(db, ifNotExists);
        UserDao.createTable(db, ifNotExists);
        DeviceAttributesDao.createTable(db, ifNotExists);
        DeviceDao.createTable(db, ifNotExists);
        TagDao.createTable(db, ifNotExists);
        ActivityDescriptionDao.createTable(db, ifNotExists);
        ActivityDescTagLinkDao.createTable(db, ifNotExists);
        MakibesHR3ActivitySampleDao.createTable(db, ifNotExists);
        MiBandActivitySampleDao.createTable(db, ifNotExists);
        PebbleHealthActivitySampleDao.createTable(db, ifNotExists);
        PebbleHealthActivityOverlayDao.createTable(db, ifNotExists);
        PebbleMisfitSampleDao.createTable(db, ifNotExists);
        PebbleMorpheuzSampleDao.createTable(db, ifNotExists);
        HPlusHealthActivityOverlayDao.createTable(db, ifNotExists);
        HPlusHealthActivitySampleDao.createTable(db, ifNotExists);
        No1F1ActivitySampleDao.createTable(db, ifNotExists);
        XWatchActivitySampleDao.createTable(db, ifNotExists);
        ZeTimeActivitySampleDao.createTable(db, ifNotExists);
        ID115ActivitySampleDao.createTable(db, ifNotExists);
        JYouActivitySampleDao.createTable(db, ifNotExists);
        CalendarSyncStateDao.createTable(db, ifNotExists);
        AlarmDao.createTable(db, ifNotExists);
        NotificationFilterDao.createTable(db, ifNotExists);
        NotificationFilterEntryDao.createTable(db, ifNotExists);
        BaseActivitySummaryDao.createTable(db, ifNotExists);
    }

    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        UserAttributesDao.dropTable(db, ifExists);
        UserDao.dropTable(db, ifExists);
        DeviceAttributesDao.dropTable(db, ifExists);
        DeviceDao.dropTable(db, ifExists);
        TagDao.dropTable(db, ifExists);
        ActivityDescriptionDao.dropTable(db, ifExists);
        ActivityDescTagLinkDao.dropTable(db, ifExists);
        MakibesHR3ActivitySampleDao.dropTable(db, ifExists);
        MiBandActivitySampleDao.dropTable(db, ifExists);
        PebbleHealthActivitySampleDao.dropTable(db, ifExists);
        PebbleHealthActivityOverlayDao.dropTable(db, ifExists);
        PebbleMisfitSampleDao.dropTable(db, ifExists);
        PebbleMorpheuzSampleDao.dropTable(db, ifExists);
        HPlusHealthActivityOverlayDao.dropTable(db, ifExists);
        HPlusHealthActivitySampleDao.dropTable(db, ifExists);
        No1F1ActivitySampleDao.dropTable(db, ifExists);
        XWatchActivitySampleDao.dropTable(db, ifExists);
        ZeTimeActivitySampleDao.dropTable(db, ifExists);
        ID115ActivitySampleDao.dropTable(db, ifExists);
        JYouActivitySampleDao.dropTable(db, ifExists);
        CalendarSyncStateDao.dropTable(db, ifExists);
        AlarmDao.dropTable(db, ifExists);
        NotificationFilterDao.dropTable(db, ifExists);
        NotificationFilterEntryDao.dropTable(db, ifExists);
        BaseActivitySummaryDao.dropTable(db, ifExists);
    }

    public static abstract class OpenHelper extends SQLiteOpenHelper {
        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory, 23);
        }

        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version 23");
            DaoMaster.createAllTables(db, false);
        }
    }

    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            DaoMaster.dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, 23);
        registerDaoClass(UserAttributesDao.class);
        registerDaoClass(UserDao.class);
        registerDaoClass(DeviceAttributesDao.class);
        registerDaoClass(DeviceDao.class);
        registerDaoClass(TagDao.class);
        registerDaoClass(ActivityDescriptionDao.class);
        registerDaoClass(ActivityDescTagLinkDao.class);
        registerDaoClass(MakibesHR3ActivitySampleDao.class);
        registerDaoClass(MiBandActivitySampleDao.class);
        registerDaoClass(PebbleHealthActivitySampleDao.class);
        registerDaoClass(PebbleHealthActivityOverlayDao.class);
        registerDaoClass(PebbleMisfitSampleDao.class);
        registerDaoClass(PebbleMorpheuzSampleDao.class);
        registerDaoClass(HPlusHealthActivityOverlayDao.class);
        registerDaoClass(HPlusHealthActivitySampleDao.class);
        registerDaoClass(No1F1ActivitySampleDao.class);
        registerDaoClass(XWatchActivitySampleDao.class);
        registerDaoClass(ZeTimeActivitySampleDao.class);
        registerDaoClass(ID115ActivitySampleDao.class);
        registerDaoClass(JYouActivitySampleDao.class);
        registerDaoClass(CalendarSyncStateDao.class);
        registerDaoClass(AlarmDao.class);
        registerDaoClass(NotificationFilterDao.class);
        registerDaoClass(NotificationFilterEntryDao.class);
        registerDaoClass(BaseActivitySummaryDao.class);
    }

    public DaoSession newSession() {
        return new DaoSession(this.f107db, IdentityScopeType.Session, this.daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(this.f107db, type, this.daoConfigMap);
    }
}
