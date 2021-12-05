package nodomain.freeyourgadget.gadgetbridge.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.internal.DaoConfig;

public class ActivityDescTagLinkDao extends AbstractDao<ActivityDescTagLink, Long> {
    public static final String TABLENAME = "ACTIVITY_DESC_TAG_LINK";

    public static class Properties {
        public static final Property ActivityDescriptionId = new Property(1, Long.TYPE, "activityDescriptionId", false, "ACTIVITY_DESCRIPTION_ID");

        /* renamed from: Id */
        public static final Property f134Id = new Property(0, Long.class, PackageConfigHelper.DB_ID, true, "_id");
        public static final Property TagId = new Property(2, Long.TYPE, "tagId", false, "TAG_ID");
    }

    public ActivityDescTagLinkDao(DaoConfig config) {
        super(config);
    }

    public ActivityDescTagLinkDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"ACTIVITY_DESC_TAG_LINK\" (\"_id\" INTEGER PRIMARY KEY ,\"ACTIVITY_DESCRIPTION_ID\" INTEGER NOT NULL ,\"TAG_ID\" INTEGER NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        sb.append(ifExists ? "IF EXISTS " : "");
        sb.append("\"ACTIVITY_DESC_TAG_LINK\"");
        db.execSQL(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement stmt, ActivityDescTagLink entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        stmt.bindLong(2, entity.getActivityDescriptionId());
        stmt.bindLong(3, entity.getTagId());
    }

    public Long readKey(Cursor cursor, int offset) {
        if (cursor.isNull(offset + 0)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(offset + 0));
    }

    public ActivityDescTagLink readEntity(Cursor cursor, int offset) {
        return new ActivityDescTagLink(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)), cursor.getLong(offset + 1), cursor.getLong(offset + 2));
    }

    public void readEntity(Cursor cursor, ActivityDescTagLink entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)));
        entity.setActivityDescriptionId(cursor.getLong(offset + 1));
        entity.setTagId(cursor.getLong(offset + 2));
    }

    /* access modifiers changed from: protected */
    public Long updateKeyAfterInsert(ActivityDescTagLink entity, long rowId) {
        entity.setId(Long.valueOf(rowId));
        return Long.valueOf(rowId);
    }

    public Long getKey(ActivityDescTagLink entity) {
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
