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

public class NotificationFilterEntryDao extends AbstractDao<NotificationFilterEntry, Long> {
    public static final String TABLENAME = "NOTIFICATION_FILTER_ENTRY";
    private DaoSession daoSession;
    private String selectDeep;

    public static class Properties {

        /* renamed from: Id */
        public static final Property f148Id = new Property(0, Long.class, PackageConfigHelper.DB_ID, true, "_id");
        public static final Property NotificationFilterContent = new Property(2, String.class, "notificationFilterContent", false, "NOTIFICATION_FILTER_CONTENT");
        public static final Property NotificationFilterId = new Property(1, Long.TYPE, "notificationFilterId", false, "NOTIFICATION_FILTER_ID");
    }

    public NotificationFilterEntryDao(DaoConfig config) {
        super(config);
    }

    public NotificationFilterEntryDao(DaoConfig config, DaoSession daoSession2) {
        super(config, daoSession2);
        this.daoSession = daoSession2;
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"NOTIFICATION_FILTER_ENTRY\" (\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ,\"NOTIFICATION_FILTER_ID\" INTEGER NOT NULL ,\"NOTIFICATION_FILTER_CONTENT\" TEXT NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        sb.append(ifExists ? "IF EXISTS " : "");
        sb.append("\"NOTIFICATION_FILTER_ENTRY\"");
        db.execSQL(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement stmt, NotificationFilterEntry entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        stmt.bindLong(2, entity.getNotificationFilterId());
        stmt.bindString(3, entity.getNotificationFilterContent());
    }

    /* access modifiers changed from: protected */
    public void attachEntity(NotificationFilterEntry entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(this.daoSession);
    }

    public Long readKey(Cursor cursor, int offset) {
        if (cursor.isNull(offset + 0)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(offset + 0));
    }

    public NotificationFilterEntry readEntity(Cursor cursor, int offset) {
        return new NotificationFilterEntry(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)), cursor.getLong(offset + 1), cursor.getString(offset + 2));
    }

    public void readEntity(Cursor cursor, NotificationFilterEntry entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)));
        entity.setNotificationFilterId(cursor.getLong(offset + 1));
        entity.setNotificationFilterContent(cursor.getString(offset + 2));
    }

    /* access modifiers changed from: protected */
    public Long updateKeyAfterInsert(NotificationFilterEntry entity, long rowId) {
        entity.setId(Long.valueOf(rowId));
        return Long.valueOf(rowId);
    }

    public Long getKey(NotificationFilterEntry entity) {
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
            SqlUtils.appendColumns(builder, "T0", this.daoSession.getNotificationFilterDao().getAllColumns());
            builder.append(" FROM NOTIFICATION_FILTER_ENTRY T");
            builder.append(" LEFT JOIN NOTIFICATION_FILTER T0 ON T.\"NOTIFICATION_FILTER_ID\"=T0.\"_id\"");
            builder.append(' ');
            this.selectDeep = builder.toString();
        }
        return this.selectDeep;
    }

    /* access modifiers changed from: protected */
    public NotificationFilterEntry loadCurrentDeep(Cursor cursor, boolean lock) {
        NotificationFilterEntry entity = (NotificationFilterEntry) loadCurrent(cursor, 0, lock);
        NotificationFilter notificationFilter = (NotificationFilter) loadCurrentOther(this.daoSession.getNotificationFilterDao(), cursor, getAllColumns().length);
        if (notificationFilter != null) {
            entity.setNotificationFilter(notificationFilter);
        }
        return entity;
    }

    public NotificationFilterEntry loadDeep(Long key) {
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
                NotificationFilterEntry loadCurrentDeep = loadCurrentDeep(cursor, true);
                cursor.close();
                return loadCurrentDeep;
            }
            throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    public List<NotificationFilterEntry> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<NotificationFilterEntry> list = new ArrayList<>(count);
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
    public List<NotificationFilterEntry> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    public List<NotificationFilterEntry> queryDeep(String where, String... selectionArg) {
        SQLiteDatabase sQLiteDatabase = this.f106db;
        return loadDeepAllAndCloseCursor(sQLiteDatabase.rawQuery(getSelectDeep() + where, selectionArg));
    }
}
