package nodomain.freeyourgadget.gadgetbridge.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import java.util.ArrayList;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import p005ch.qos.logback.core.CoreConstants;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.internal.DaoConfig;
import p008de.greenrobot.dao.internal.SqlUtils;

public class MiBandActivitySampleDao extends AbstractDao<MiBandActivitySample, Void> {
    public static final String TABLENAME = "MI_BAND_ACTIVITY_SAMPLE";
    private DaoSession daoSession;
    private String selectDeep;

    public static class Properties {
        public static final Property DeviceId = new Property(1, Long.TYPE, "deviceId", true, "DEVICE_ID");
        public static final Property HeartRate = new Property(6, Integer.TYPE, "heartRate", false, "HEART_RATE");
        public static final Property RawIntensity = new Property(3, Integer.TYPE, "rawIntensity", false, "RAW_INTENSITY");
        public static final Property RawKind = new Property(5, Integer.TYPE, "rawKind", false, "RAW_KIND");
        public static final Property Steps = new Property(4, Integer.TYPE, MiBandConst.PREF_MI2_DISPLAY_ITEM_STEPS, false, "STEPS");
        public static final Property Timestamp = new Property(0, Integer.TYPE, "timestamp", true, "TIMESTAMP");
        public static final Property UserId = new Property(2, Long.TYPE, "userId", false, "USER_ID");
    }

    public MiBandActivitySampleDao(DaoConfig config) {
        super(config);
    }

    public MiBandActivitySampleDao(DaoConfig config, DaoSession daoSession2) {
        super(config, daoSession2);
        this.daoSession = daoSession2;
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(constraint);
        sb.append("\"MI_BAND_ACTIVITY_SAMPLE\" (\"TIMESTAMP\" INTEGER  NOT NULL ,\"DEVICE_ID\" INTEGER  NOT NULL ,\"USER_ID\" INTEGER NOT NULL ,\"RAW_INTENSITY\" INTEGER NOT NULL ,\"STEPS\" INTEGER NOT NULL ,\"RAW_KIND\" INTEGER NOT NULL ,\"HEART_RATE\" INTEGER NOT NULL ,PRIMARY KEY (\"TIMESTAMP\" ,\"DEVICE_ID\" ) ON CONFLICT REPLACE)");
        sb.append(Build.VERSION.SDK_INT >= 21 ? " WITHOUT ROWID;" : ";");
        db.execSQL(sb.toString());
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        sb.append(ifExists ? "IF EXISTS " : "");
        sb.append("\"MI_BAND_ACTIVITY_SAMPLE\"");
        db.execSQL(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement stmt, MiBandActivitySample entity) {
        stmt.clearBindings();
        stmt.bindLong(1, (long) entity.getTimestamp());
        stmt.bindLong(2, entity.getDeviceId());
        stmt.bindLong(3, entity.getUserId());
        stmt.bindLong(4, (long) entity.getRawIntensity());
        stmt.bindLong(5, (long) entity.getSteps());
        stmt.bindLong(6, (long) entity.getRawKind());
        stmt.bindLong(7, (long) entity.getHeartRate());
    }

    /* access modifiers changed from: protected */
    public void attachEntity(MiBandActivitySample entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(this.daoSession);
    }

    public Void readKey(Cursor cursor, int offset) {
        return null;
    }

    public MiBandActivitySample readEntity(Cursor cursor, int offset) {
        return new MiBandActivitySample(cursor.getInt(offset + 0), cursor.getLong(offset + 1), cursor.getLong(offset + 2), cursor.getInt(offset + 3), cursor.getInt(offset + 4), cursor.getInt(offset + 5), cursor.getInt(offset + 6));
    }

    public void readEntity(Cursor cursor, MiBandActivitySample entity, int offset) {
        entity.setTimestamp(cursor.getInt(offset + 0));
        entity.setDeviceId(cursor.getLong(offset + 1));
        entity.setUserId(cursor.getLong(offset + 2));
        entity.setRawIntensity(cursor.getInt(offset + 3));
        entity.setSteps(cursor.getInt(offset + 4));
        entity.setRawKind(cursor.getInt(offset + 5));
        entity.setHeartRate(cursor.getInt(offset + 6));
    }

    /* access modifiers changed from: protected */
    public Void updateKeyAfterInsert(MiBandActivitySample entity, long rowId) {
        return null;
    }

    public Void getKey(MiBandActivitySample entity) {
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
            builder.append(" FROM MI_BAND_ACTIVITY_SAMPLE T");
            builder.append(" LEFT JOIN DEVICE T0 ON T.\"DEVICE_ID\"=T0.\"_id\"");
            builder.append(" LEFT JOIN USER T1 ON T.\"USER_ID\"=T1.\"_id\"");
            builder.append(' ');
            this.selectDeep = builder.toString();
        }
        return this.selectDeep;
    }

    /* access modifiers changed from: protected */
    public MiBandActivitySample loadCurrentDeep(Cursor cursor, boolean lock) {
        MiBandActivitySample entity = (MiBandActivitySample) loadCurrent(cursor, 0, lock);
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

    public MiBandActivitySample loadDeep(Long key) {
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
                MiBandActivitySample loadCurrentDeep = loadCurrentDeep(cursor, true);
                cursor.close();
                return loadCurrentDeep;
            }
            throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    public List<MiBandActivitySample> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<MiBandActivitySample> list = new ArrayList<>(count);
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
    public List<MiBandActivitySample> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    public List<MiBandActivitySample> queryDeep(String where, String... selectionArg) {
        SQLiteDatabase sQLiteDatabase = this.f106db;
        return loadDeepAllAndCloseCursor(sQLiteDatabase.rawQuery(getSelectDeep() + where, selectionArg));
    }
}
