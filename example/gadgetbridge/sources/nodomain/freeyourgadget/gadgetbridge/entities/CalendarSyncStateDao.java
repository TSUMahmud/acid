package nodomain.freeyourgadget.gadgetbridge.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import java.util.ArrayList;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;
import p005ch.qos.logback.core.CoreConstants;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.internal.DaoConfig;
import p008de.greenrobot.dao.internal.SqlUtils;

public class CalendarSyncStateDao extends AbstractDao<CalendarSyncState, Long> {
    public static final String TABLENAME = "CALENDAR_SYNC_STATE";
    private DaoSession daoSession;
    private String selectDeep;

    public static class Properties {
        public static final Property CalendarEntryId = new Property(2, Long.TYPE, "calendarEntryId", false, "CALENDAR_ENTRY_ID");
        public static final Property DeviceId = new Property(1, Long.TYPE, "deviceId", false, "DEVICE_ID");
        public static final Property Hash = new Property(3, Integer.TYPE, "hash", false, "HASH");

        /* renamed from: Id */
        public static final Property f140Id = new Property(0, Long.class, PackageConfigHelper.DB_ID, true, "_id");
    }

    public CalendarSyncStateDao(DaoConfig config) {
        super(config);
    }

    public CalendarSyncStateDao(DaoConfig config, DaoSession daoSession2) {
        super(config, daoSession2);
        this.daoSession = daoSession2;
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"CALENDAR_SYNC_STATE\" (\"_id\" INTEGER PRIMARY KEY ,\"DEVICE_ID\" INTEGER NOT NULL ,\"CALENDAR_ENTRY_ID\" INTEGER NOT NULL ,\"HASH\" INTEGER NOT NULL );");
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_CALENDAR_SYNC_STATE_DEVICE_ID_CALENDAR_ENTRY_ID ON CALENDAR_SYNC_STATE (\"DEVICE_ID\",\"CALENDAR_ENTRY_ID\");");
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        sb.append(ifExists ? "IF EXISTS " : "");
        sb.append("\"CALENDAR_SYNC_STATE\"");
        db.execSQL(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement stmt, CalendarSyncState entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        stmt.bindLong(2, entity.getDeviceId());
        stmt.bindLong(3, entity.getCalendarEntryId());
        stmt.bindLong(4, (long) entity.getHash());
    }

    /* access modifiers changed from: protected */
    public void attachEntity(CalendarSyncState entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(this.daoSession);
    }

    public Long readKey(Cursor cursor, int offset) {
        if (cursor.isNull(offset + 0)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(offset + 0));
    }

    public CalendarSyncState readEntity(Cursor cursor, int offset) {
        return new CalendarSyncState(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)), cursor.getLong(offset + 1), cursor.getLong(offset + 2), cursor.getInt(offset + 3));
    }

    public void readEntity(Cursor cursor, CalendarSyncState entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)));
        entity.setDeviceId(cursor.getLong(offset + 1));
        entity.setCalendarEntryId(cursor.getLong(offset + 2));
        entity.setHash(cursor.getInt(offset + 3));
    }

    /* access modifiers changed from: protected */
    public Long updateKeyAfterInsert(CalendarSyncState entity, long rowId) {
        entity.setId(Long.valueOf(rowId));
        return Long.valueOf(rowId);
    }

    public Long getKey(CalendarSyncState entity) {
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
            builder.append(" FROM CALENDAR_SYNC_STATE T");
            builder.append(" LEFT JOIN DEVICE T0 ON T.\"DEVICE_ID\"=T0.\"_id\"");
            builder.append(' ');
            this.selectDeep = builder.toString();
        }
        return this.selectDeep;
    }

    /* access modifiers changed from: protected */
    public CalendarSyncState loadCurrentDeep(Cursor cursor, boolean lock) {
        CalendarSyncState entity = (CalendarSyncState) loadCurrent(cursor, 0, lock);
        Device device = (Device) loadCurrentOther(this.daoSession.getDeviceDao(), cursor, getAllColumns().length);
        if (device != null) {
            entity.setDevice(device);
        }
        return entity;
    }

    public CalendarSyncState loadDeep(Long key) {
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
                CalendarSyncState loadCurrentDeep = loadCurrentDeep(cursor, true);
                cursor.close();
                return loadCurrentDeep;
            }
            throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    public List<CalendarSyncState> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<CalendarSyncState> list = new ArrayList<>(count);
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
    public List<CalendarSyncState> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    public List<CalendarSyncState> queryDeep(String where, String... selectionArg) {
        SQLiteDatabase sQLiteDatabase = this.f106db;
        return loadDeepAllAndCloseCursor(sQLiteDatabase.rawQuery(getSelectDeep() + where, selectionArg));
    }
}
