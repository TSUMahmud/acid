package nodomain.freeyourgadget.gadgetbridge.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;
import nodomain.freeyourgadget.gadgetbridge.entities.ActivityDescTagLinkDao;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.internal.DaoConfig;
import p008de.greenrobot.dao.query.Query;
import p008de.greenrobot.dao.query.QueryBuilder;
import p008de.greenrobot.dao.query.WhereCondition;

public class TagDao extends AbstractDao<Tag, Long> {
    public static final String TABLENAME = "TAG";
    private Query<Tag> activityDescription_TagListQuery;

    public static class Properties {
        public static final Property Description = new Property(2, String.class, "description", false, "DESCRIPTION");

        /* renamed from: Id */
        public static final Property f150Id = new Property(0, Long.class, PackageConfigHelper.DB_ID, true, "_id");
        public static final Property Name = new Property(1, String.class, "name", false, "NAME");
        public static final Property UserId = new Property(3, Long.TYPE, "userId", false, "USER_ID");
    }

    public TagDao(DaoConfig config) {
        super(config);
    }

    public TagDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"TAG\" (\"_id\" INTEGER PRIMARY KEY ,\"NAME\" TEXT NOT NULL ,\"DESCRIPTION\" TEXT,\"USER_ID\" INTEGER NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        sb.append(ifExists ? "IF EXISTS " : "");
        sb.append("\"TAG\"");
        db.execSQL(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement stmt, Tag entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        stmt.bindString(2, entity.getName());
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(3, description);
        }
        stmt.bindLong(4, entity.getUserId());
    }

    public Long readKey(Cursor cursor, int offset) {
        if (cursor.isNull(offset + 0)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(offset + 0));
    }

    public Tag readEntity(Cursor cursor, int offset) {
        return new Tag(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)), cursor.getString(offset + 1), cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), cursor.getLong(offset + 3));
    }

    public void readEntity(Cursor cursor, Tag entity, int offset) {
        String str = null;
        entity.setId(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)));
        entity.setName(cursor.getString(offset + 1));
        if (!cursor.isNull(offset + 2)) {
            str = cursor.getString(offset + 2);
        }
        entity.setDescription(str);
        entity.setUserId(cursor.getLong(offset + 3));
    }

    /* access modifiers changed from: protected */
    public Long updateKeyAfterInsert(Tag entity, long rowId) {
        entity.setId(Long.valueOf(rowId));
        return Long.valueOf(rowId);
    }

    public Long getKey(Tag entity) {
        if (entity != null) {
            return entity.getId();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isEntityUpdateable() {
        return true;
    }

    public List<Tag> _queryActivityDescription_TagList(long activityDescriptionId) {
        synchronized (this) {
            if (this.activityDescription_TagListQuery == null) {
                QueryBuilder<Tag> queryBuilder = queryBuilder();
                queryBuilder.join((Class<J>) ActivityDescTagLink.class, ActivityDescTagLinkDao.Properties.TagId).where(ActivityDescTagLinkDao.Properties.ActivityDescriptionId.mo14989eq(Long.valueOf(activityDescriptionId)), new WhereCondition[0]);
                this.activityDescription_TagListQuery = queryBuilder.build();
            }
        }
        Query<Tag> query = this.activityDescription_TagListQuery.forCurrentThread();
        query.setParameter(0, (Object) Long.valueOf(activityDescriptionId));
        return query.list();
    }
}
