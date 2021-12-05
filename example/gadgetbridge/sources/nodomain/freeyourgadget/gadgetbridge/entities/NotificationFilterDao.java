package nodomain.freeyourgadget.gadgetbridge.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.internal.DaoConfig;

public class NotificationFilterDao extends AbstractDao<NotificationFilter, Long> {
    public static final String TABLENAME = "NOTIFICATION_FILTER";

    public static class Properties {
        public static final Property AppIdentifier = new Property(0, String.class, "appIdentifier", false, "APP_IDENTIFIER");

        /* renamed from: Id */
        public static final Property f146Id = new Property(1, Long.class, PackageConfigHelper.DB_ID, true, "_id");
        public static final Property NotificationFilterMode = new Property(2, Integer.TYPE, "notificationFilterMode", false, "NOTIFICATION_FILTER_MODE");
        public static final Property NotificationFilterSubMode = new Property(3, Integer.TYPE, "notificationFilterSubMode", false, "NOTIFICATION_FILTER_SUB_MODE");
    }

    public NotificationFilterDao(DaoConfig config) {
        super(config);
    }

    public NotificationFilterDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"NOTIFICATION_FILTER\" (\"APP_IDENTIFIER\" TEXT NOT NULL ,\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ,\"NOTIFICATION_FILTER_MODE\" INTEGER NOT NULL ,\"NOTIFICATION_FILTER_SUB_MODE\" INTEGER NOT NULL );");
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_NOTIFICATION_FILTER_APP_IDENTIFIER ON NOTIFICATION_FILTER (\"APP_IDENTIFIER\");");
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        sb.append(ifExists ? "IF EXISTS " : "");
        sb.append("\"NOTIFICATION_FILTER\"");
        db.execSQL(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement stmt, NotificationFilter entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getAppIdentifier());
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(2, id.longValue());
        }
        stmt.bindLong(3, (long) entity.getNotificationFilterMode());
        stmt.bindLong(4, (long) entity.getNotificationFilterSubMode());
    }

    public Long readKey(Cursor cursor, int offset) {
        if (cursor.isNull(offset + 1)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(offset + 1));
    }

    public NotificationFilter readEntity(Cursor cursor, int offset) {
        return new NotificationFilter(cursor.getString(offset + 0), cursor.isNull(offset + 1) ? null : Long.valueOf(cursor.getLong(offset + 1)), cursor.getInt(offset + 2), cursor.getInt(offset + 3));
    }

    public void readEntity(Cursor cursor, NotificationFilter entity, int offset) {
        entity.setAppIdentifier(cursor.getString(offset + 0));
        entity.setId(cursor.isNull(offset + 1) ? null : Long.valueOf(cursor.getLong(offset + 1)));
        entity.setNotificationFilterMode(cursor.getInt(offset + 2));
        entity.setNotificationFilterSubMode(cursor.getInt(offset + 3));
    }

    /* access modifiers changed from: protected */
    public Long updateKeyAfterInsert(NotificationFilter entity, long rowId) {
        entity.setId(Long.valueOf(rowId));
        return Long.valueOf(rowId);
    }

    public Long getKey(NotificationFilter entity) {
        if (entity != null) {
            return entity.getId();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isEntityUpdateable() {
        return true;
    }
}
