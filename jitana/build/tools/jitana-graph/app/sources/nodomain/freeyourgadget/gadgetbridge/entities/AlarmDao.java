package nodomain.freeyourgadget.gadgetbridge.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import cyanogenmod.alarmclock.ClockContract;
import java.util.ArrayList;
import java.util.List;
import p005ch.qos.logback.core.CoreConstants;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.internal.DaoConfig;
import p008de.greenrobot.dao.internal.SqlUtils;

public class AlarmDao extends AbstractDao<Alarm, Void> {
    public static final String TABLENAME = "ALARM";
    private DaoSession daoSession;
    private String selectDeep;

    public static class Properties {
        public static final Property DeviceId = new Property(0, Long.TYPE, "deviceId", false, "DEVICE_ID");
        public static final Property Enabled = new Property(3, Boolean.TYPE, ClockContract.AlarmsColumns.ENABLED, false, "ENABLED");
        public static final Property Hour = new Property(7, Integer.TYPE, "hour", false, "HOUR");
        public static final Property Minute = new Property(8, Integer.TYPE, "minute", false, "MINUTE");
        public static final Property Position = new Property(2, Integer.TYPE, "position", false, "POSITION");
        public static final Property Repetition = new Property(6, Integer.TYPE, "repetition", false, "REPETITION");
        public static final Property SmartWakeup = new Property(4, Boolean.TYPE, "smartWakeup", false, "SMART_WAKEUP");
        public static final Property Snooze = new Property(5, Boolean.TYPE, "snooze", false, "SNOOZE");
        public static final Property Unused = new Property(9, Boolean.TYPE, "unused", false, "UNUSED");
        public static final Property UserId = new Property(1, Long.TYPE, "userId", false, "USER_ID");
    }

    public AlarmDao(DaoConfig config) {
        super(config);
    }

    public AlarmDao(DaoConfig config, DaoSession daoSession2) {
        super(config, daoSession2);
        this.daoSession = daoSession2;
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"ALARM\" (\"DEVICE_ID\" INTEGER NOT NULL ,\"USER_ID\" INTEGER NOT NULL ,\"POSITION\" INTEGER NOT NULL ,\"ENABLED\" INTEGER NOT NULL ,\"SMART_WAKEUP\" INTEGER NOT NULL ,\"SNOOZE\" INTEGER NOT NULL ,\"REPETITION\" INTEGER NOT NULL ,\"HOUR\" INTEGER NOT NULL ,\"MINUTE\" INTEGER NOT NULL ,\"UNUSED\" INTEGER NOT NULL );");
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_ALARM_DEVICE_ID_USER_ID_POSITION ON ALARM (\"DEVICE_ID\",\"USER_ID\",\"POSITION\");");
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        sb.append(ifExists ? "IF EXISTS " : "");
        sb.append("\"ALARM\"");
        db.execSQL(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement stmt, Alarm entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getDeviceId());
        stmt.bindLong(2, entity.getUserId());
        stmt.bindLong(3, (long) entity.getPosition());
        long j = 1;
        stmt.bindLong(4, entity.getEnabled() ? 1 : 0);
        stmt.bindLong(5, entity.getSmartWakeup() ? 1 : 0);
        stmt.bindLong(6, entity.getSnooze() ? 1 : 0);
        stmt.bindLong(7, (long) entity.getRepetition());
        stmt.bindLong(8, (long) entity.getHour());
        stmt.bindLong(9, (long) entity.getMinute());
        if (!entity.getUnused()) {
            j = 0;
        }
        stmt.bindLong(10, j);
    }

    /* access modifiers changed from: protected */
    public void attachEntity(Alarm entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(this.daoSession);
    }

    public Void readKey(Cursor cursor, int offset) {
        return null;
    }

    public Alarm readEntity(Cursor cursor, int offset) {
        Cursor cursor2 = cursor;
        return new Alarm(cursor2.getLong(offset + 0), cursor2.getLong(offset + 1), cursor2.getInt(offset + 2), cursor2.getShort(offset + 3) != 0, cursor2.getShort(offset + 4) != 0, cursor2.getShort(offset + 5) != 0, cursor2.getInt(offset + 6), cursor2.getInt(offset + 7), cursor2.getInt(offset + 8), cursor2.getShort(offset + 9) != 0);
    }

    public void readEntity(Cursor cursor, Alarm entity, int offset) {
        entity.setDeviceId(cursor.getLong(offset + 0));
        entity.setUserId(cursor.getLong(offset + 1));
        entity.setPosition(cursor.getInt(offset + 2));
        boolean z = true;
        entity.setEnabled(cursor.getShort(offset + 3) != 0);
        entity.setSmartWakeup(cursor.getShort(offset + 4) != 0);
        entity.setSnooze(cursor.getShort(offset + 5) != 0);
        entity.setRepetition(cursor.getInt(offset + 6));
        entity.setHour(cursor.getInt(offset + 7));
        entity.setMinute(cursor.getInt(offset + 8));
        if (cursor.getShort(offset + 9) == 0) {
            z = false;
        }
        entity.setUnused(z);
    }

    /* access modifiers changed from: protected */
    public Void updateKeyAfterInsert(Alarm entity, long rowId) {
        return null;
    }

    public Void getKey(Alarm entity) {
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
            SqlUtils.appendColumns(builder, "T0", this.daoSession.getUserDao().getAllColumns());
            builder.append(CoreConstants.COMMA_CHAR);
            SqlUtils.appendColumns(builder, "T1", this.daoSession.getDeviceDao().getAllColumns());
            builder.append(" FROM ALARM T");
            builder.append(" LEFT JOIN USER T0 ON T.\"USER_ID\"=T0.\"_id\"");
            builder.append(" LEFT JOIN DEVICE T1 ON T.\"DEVICE_ID\"=T1.\"_id\"");
            builder.append(' ');
            this.selectDeep = builder.toString();
        }
        return this.selectDeep;
    }

    /* access modifiers changed from: protected */
    public Alarm loadCurrentDeep(Cursor cursor, boolean lock) {
        Alarm entity = (Alarm) loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;
        User user = (User) loadCurrentOther(this.daoSession.getUserDao(), cursor, offset);
        if (user != null) {
            entity.setUser(user);
        }
        Device device = (Device) loadCurrentOther(this.daoSession.getDeviceDao(), cursor, offset + this.daoSession.getUserDao().getAllColumns().length);
        if (device != null) {
            entity.setDevice(device);
        }
        return entity;
    }

    public Alarm loadDeep(Long key) {
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
                Alarm loadCurrentDeep = loadCurrentDeep(cursor, true);
                cursor.close();
                return loadCurrentDeep;
            }
            throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    public List<Alarm> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Alarm> list = new ArrayList<>(count);
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
    public List<Alarm> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    public List<Alarm> queryDeep(String where, String... selectionArg) {
        SQLiteDatabase sQLiteDatabase = this.f106db;
        return loadDeepAllAndCloseCursor(sQLiteDatabase.rawQuery(getSelectDeep() + where, selectionArg));
    }
}
