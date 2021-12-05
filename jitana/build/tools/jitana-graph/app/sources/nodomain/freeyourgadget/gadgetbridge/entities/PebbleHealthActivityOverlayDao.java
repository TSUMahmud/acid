package nodomain.freeyourgadget.gadgetbridge.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import java.util.ArrayList;
import java.util.List;
import p005ch.qos.logback.core.CoreConstants;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.internal.DaoConfig;
import p008de.greenrobot.dao.internal.SqlUtils;

public class PebbleHealthActivityOverlayDao extends AbstractDao<PebbleHealthActivityOverlay, Void> {
    public static final String TABLENAME = "PEBBLE_HEALTH_ACTIVITY_OVERLAY";
    private DaoSession daoSession;
    private String selectDeep;

    public static class Properties {
        public static final Property DeviceId = new Property(3, Long.TYPE, "deviceId", true, "DEVICE_ID");
        public static final Property RawKind = new Property(2, Integer.TYPE, "rawKind", true, "RAW_KIND");
        public static final Property RawPebbleHealthData = new Property(5, byte[].class, "rawPebbleHealthData", false, "RAW_PEBBLE_HEALTH_DATA");
        public static final Property TimestampFrom = new Property(0, Integer.TYPE, "timestampFrom", true, "TIMESTAMP_FROM");
        public static final Property TimestampTo = new Property(1, Integer.TYPE, "timestampTo", true, "TIMESTAMP_TO");
        public static final Property UserId = new Property(4, Long.TYPE, "userId", false, "USER_ID");
    }

    public PebbleHealthActivityOverlayDao(DaoConfig config) {
        super(config);
    }

    public PebbleHealthActivityOverlayDao(DaoConfig config, DaoSession daoSession2) {
        super(config, daoSession2);
        this.daoSession = daoSession2;
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(constraint);
        sb.append("\"PEBBLE_HEALTH_ACTIVITY_OVERLAY\" (\"TIMESTAMP_FROM\" INTEGER  NOT NULL ,\"TIMESTAMP_TO\" INTEGER  NOT NULL ,\"RAW_KIND\" INTEGER  NOT NULL ,\"DEVICE_ID\" INTEGER  NOT NULL ,\"USER_ID\" INTEGER NOT NULL ,\"RAW_PEBBLE_HEALTH_DATA\" BLOB,PRIMARY KEY (\"TIMESTAMP_FROM\" ,\"TIMESTAMP_TO\" ,\"RAW_KIND\" ,\"DEVICE_ID\" ) ON CONFLICT REPLACE)");
        sb.append(Build.VERSION.SDK_INT >= 21 ? " WITHOUT ROWID;" : ";");
        db.execSQL(sb.toString());
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        sb.append(ifExists ? "IF EXISTS " : "");
        sb.append("\"PEBBLE_HEALTH_ACTIVITY_OVERLAY\"");
        db.execSQL(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement stmt, PebbleHealthActivityOverlay entity) {
        stmt.clearBindings();
        stmt.bindLong(1, (long) entity.getTimestampFrom());
        stmt.bindLong(2, (long) entity.getTimestampTo());
        stmt.bindLong(3, (long) entity.getRawKind());
        stmt.bindLong(4, entity.getDeviceId());
        stmt.bindLong(5, entity.getUserId());
        byte[] rawPebbleHealthData = entity.getRawPebbleHealthData();
        if (rawPebbleHealthData != null) {
            stmt.bindBlob(6, rawPebbleHealthData);
        }
    }

    /* access modifiers changed from: protected */
    public void attachEntity(PebbleHealthActivityOverlay entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(this.daoSession);
    }

    public Void readKey(Cursor cursor, int offset) {
        return null;
    }

    public PebbleHealthActivityOverlay readEntity(Cursor cursor, int offset) {
        return new PebbleHealthActivityOverlay(cursor.getInt(offset + 0), cursor.getInt(offset + 1), cursor.getInt(offset + 2), cursor.getLong(offset + 3), cursor.getLong(offset + 4), cursor.isNull(offset + 5) ? null : cursor.getBlob(offset + 5));
    }

    public void readEntity(Cursor cursor, PebbleHealthActivityOverlay entity, int offset) {
        entity.setTimestampFrom(cursor.getInt(offset + 0));
        entity.setTimestampTo(cursor.getInt(offset + 1));
        entity.setRawKind(cursor.getInt(offset + 2));
        entity.setDeviceId(cursor.getLong(offset + 3));
        entity.setUserId(cursor.getLong(offset + 4));
        entity.setRawPebbleHealthData(cursor.isNull(offset + 5) ? null : cursor.getBlob(offset + 5));
    }

    /* access modifiers changed from: protected */
    public Void updateKeyAfterInsert(PebbleHealthActivityOverlay entity, long rowId) {
        return null;
    }

    public Void getKey(PebbleHealthActivityOverlay entity) {
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
            builder.append(" FROM PEBBLE_HEALTH_ACTIVITY_OVERLAY T");
            builder.append(" LEFT JOIN DEVICE T0 ON T.\"DEVICE_ID\"=T0.\"_id\"");
            builder.append(" LEFT JOIN USER T1 ON T.\"USER_ID\"=T1.\"_id\"");
            builder.append(' ');
            this.selectDeep = builder.toString();
        }
        return this.selectDeep;
    }

    /* access modifiers changed from: protected */
    public PebbleHealthActivityOverlay loadCurrentDeep(Cursor cursor, boolean lock) {
        PebbleHealthActivityOverlay entity = (PebbleHealthActivityOverlay) loadCurrent(cursor, 0, lock);
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

    public PebbleHealthActivityOverlay loadDeep(Long key) {
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
                PebbleHealthActivityOverlay loadCurrentDeep = loadCurrentDeep(cursor, true);
                cursor.close();
                return loadCurrentDeep;
            }
            throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    public List<PebbleHealthActivityOverlay> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<PebbleHealthActivityOverlay> list = new ArrayList<>(count);
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
    public List<PebbleHealthActivityOverlay> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    public List<PebbleHealthActivityOverlay> queryDeep(String where, String... selectionArg) {
        SQLiteDatabase sQLiteDatabase = this.f106db;
        return loadDeepAllAndCloseCursor(sQLiteDatabase.rawQuery(getSelectDeep() + where, selectionArg));
    }
}
