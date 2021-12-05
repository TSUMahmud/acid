package nodomain.freeyourgadget.gadgetbridge.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;
import p005ch.qos.logback.core.CoreConstants;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.internal.DaoConfig;
import p008de.greenrobot.dao.internal.SqlUtils;

public class BaseActivitySummaryDao extends AbstractDao<BaseActivitySummary, Long> {
    public static final String TABLENAME = "BASE_ACTIVITY_SUMMARY";
    private DaoSession daoSession;
    private String selectDeep;

    public static class Properties {
        public static final Property ActivityKind = new Property(4, Integer.TYPE, "activityKind", false, "ACTIVITY_KIND");
        public static final Property BaseAltitude = new Property(7, Integer.class, "baseAltitude", false, "BASE_ALTITUDE");
        public static final Property BaseLatitude = new Property(6, Integer.class, "baseLatitude", false, "BASE_LATITUDE");
        public static final Property BaseLongitude = new Property(5, Integer.class, "baseLongitude", false, "BASE_LONGITUDE");
        public static final Property DeviceId = new Property(9, Long.TYPE, "deviceId", false, "DEVICE_ID");
        public static final Property EndTime = new Property(3, Date.class, "endTime", false, "END_TIME");
        public static final Property GpxTrack = new Property(8, String.class, "gpxTrack", false, "GPX_TRACK");

        /* renamed from: Id */
        public static final Property f138Id = new Property(0, Long.class, PackageConfigHelper.DB_ID, true, "_id");
        public static final Property Name = new Property(1, String.class, "name", false, "NAME");
        public static final Property StartTime = new Property(2, Date.class, "startTime", false, "START_TIME");
        public static final Property UserId = new Property(10, Long.TYPE, "userId", false, "USER_ID");
    }

    public BaseActivitySummaryDao(DaoConfig config) {
        super(config);
    }

    public BaseActivitySummaryDao(DaoConfig config, DaoSession daoSession2) {
        super(config, daoSession2);
        this.daoSession = daoSession2;
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"BASE_ACTIVITY_SUMMARY\" (\"_id\" INTEGER PRIMARY KEY ,\"NAME\" TEXT,\"START_TIME\" INTEGER NOT NULL ,\"END_TIME\" INTEGER NOT NULL ,\"ACTIVITY_KIND\" INTEGER NOT NULL ,\"BASE_LONGITUDE\" INTEGER,\"BASE_LATITUDE\" INTEGER,\"BASE_ALTITUDE\" INTEGER,\"GPX_TRACK\" TEXT,\"DEVICE_ID\" INTEGER NOT NULL ,\"USER_ID\" INTEGER NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        sb.append(ifExists ? "IF EXISTS " : "");
        sb.append("\"BASE_ACTIVITY_SUMMARY\"");
        db.execSQL(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement stmt, BaseActivitySummary entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
        stmt.bindLong(3, entity.getStartTime().getTime());
        stmt.bindLong(4, entity.getEndTime().getTime());
        stmt.bindLong(5, (long) entity.getActivityKind());
        Integer baseLongitude = entity.getBaseLongitude();
        if (baseLongitude != null) {
            stmt.bindLong(6, (long) baseLongitude.intValue());
        }
        Integer baseLatitude = entity.getBaseLatitude();
        if (baseLatitude != null) {
            stmt.bindLong(7, (long) baseLatitude.intValue());
        }
        Integer baseAltitude = entity.getBaseAltitude();
        if (baseAltitude != null) {
            stmt.bindLong(8, (long) baseAltitude.intValue());
        }
        String gpxTrack = entity.getGpxTrack();
        if (gpxTrack != null) {
            stmt.bindString(9, gpxTrack);
        }
        stmt.bindLong(10, entity.getDeviceId());
        stmt.bindLong(11, entity.getUserId());
    }

    /* access modifiers changed from: protected */
    public void attachEntity(BaseActivitySummary entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(this.daoSession);
    }

    public Long readKey(Cursor cursor, int offset) {
        if (cursor.isNull(offset + 0)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(offset + 0));
    }

    public BaseActivitySummary readEntity(Cursor cursor, int offset) {
        Cursor cursor2 = cursor;
        return new BaseActivitySummary(cursor2.isNull(offset + 0) ? null : Long.valueOf(cursor2.getLong(offset + 0)), cursor2.isNull(offset + 1) ? null : cursor2.getString(offset + 1), new Date(cursor2.getLong(offset + 2)), new Date(cursor2.getLong(offset + 3)), cursor2.getInt(offset + 4), cursor2.isNull(offset + 5) ? null : Integer.valueOf(cursor2.getInt(offset + 5)), cursor2.isNull(offset + 6) ? null : Integer.valueOf(cursor2.getInt(offset + 6)), cursor2.isNull(offset + 7) ? null : Integer.valueOf(cursor2.getInt(offset + 7)), cursor2.isNull(offset + 8) ? null : cursor2.getString(offset + 8), cursor2.getLong(offset + 9), cursor2.getLong(offset + 10));
    }

    public void readEntity(Cursor cursor, BaseActivitySummary entity, int offset) {
        String str = null;
        entity.setId(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setStartTime(new Date(cursor.getLong(offset + 2)));
        entity.setEndTime(new Date(cursor.getLong(offset + 3)));
        entity.setActivityKind(cursor.getInt(offset + 4));
        entity.setBaseLongitude(cursor.isNull(offset + 5) ? null : Integer.valueOf(cursor.getInt(offset + 5)));
        entity.setBaseLatitude(cursor.isNull(offset + 6) ? null : Integer.valueOf(cursor.getInt(offset + 6)));
        entity.setBaseAltitude(cursor.isNull(offset + 7) ? null : Integer.valueOf(cursor.getInt(offset + 7)));
        if (!cursor.isNull(offset + 8)) {
            str = cursor.getString(offset + 8);
        }
        entity.setGpxTrack(str);
        entity.setDeviceId(cursor.getLong(offset + 9));
        entity.setUserId(cursor.getLong(offset + 10));
    }

    /* access modifiers changed from: protected */
    public Long updateKeyAfterInsert(BaseActivitySummary entity, long rowId) {
        entity.setId(Long.valueOf(rowId));
        return Long.valueOf(rowId);
    }

    public Long getKey(BaseActivitySummary entity) {
        if (entity != null) {
            return entity.getId();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isEntityUpdateable() {
        return true;
    }

    /* access modifiers changed from: protected */
    public String getSelectDeep() {
        if (this.selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(CoreConstants.COMMA_CHAR);
            SqlUtils.appendColumns(builder, "T0", this.daoSession.getDeviceDao().getAllColumns());
            builder.append(CoreConstants.COMMA_CHAR);
            SqlUtils.appendColumns(builder, "T1", this.daoSession.getUserDao().getAllColumns());
            builder.append(" FROM BASE_ACTIVITY_SUMMARY T");
            builder.append(" LEFT JOIN DEVICE T0 ON T.\"DEVICE_ID\"=T0.\"_id\"");
            builder.append(" LEFT JOIN USER T1 ON T.\"USER_ID\"=T1.\"_id\"");
            builder.append(' ');
            this.selectDeep = builder.toString();
        }
        return this.selectDeep;
    }

    /* access modifiers changed from: protected */
    public BaseActivitySummary loadCurrentDeep(Cursor cursor, boolean lock) {
        BaseActivitySummary entity = (BaseActivitySummary) loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;
        Device device = (Device) loadCurrentOther(this.daoSession.getDeviceDao(), cursor, offset);
        if (device != null) {
            entity.setDevice(device);
        }
        User user = (User) loadCurrentOther(this.daoSession.getUserDao(), cursor, offset + this.daoSession.getDeviceDao().getAllColumns().length);
        if (user != null) {
            entity.setUser(user);
        }
        return entity;
    }

    public BaseActivitySummary loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        Cursor cursor = this.f106db.rawQuery(builder.toString(), new String[]{key.toString()});
        try {
            if (!cursor.moveToFirst()) {
                return null;
            }
            if (cursor.isLast()) {
                BaseActivitySummary loadCurrentDeep = loadCurrentDeep(cursor, true);
                cursor.close();
                return loadCurrentDeep;
            }
            throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    public List<BaseActivitySummary> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<BaseActivitySummary> list = new ArrayList<>(count);
        if (cursor.moveToFirst()) {
            if (this.identityScope != null) {
                this.identityScope.lock();
                this.identityScope.reserveRoom(count);
            }
            do {
                try {
                    list.add(loadCurrentDeep(cursor, false));
                } finally {
                    if (this.identityScope != null) {
                        this.identityScope.unlock();
                    }
                }
            } while (cursor.moveToNext());
        }
        return list;
    }

    /* access modifiers changed from: protected */
    public List<BaseActivitySummary> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    public List<BaseActivitySummary> queryDeep(String where, String... selectionArg) {
        SQLiteDatabase sQLiteDatabase = this.f106db;
        return loadDeepAllAndCloseCursor(sQLiteDatabase.rawQuery(getSelectDeep() + where, selectionArg));
    }
}
