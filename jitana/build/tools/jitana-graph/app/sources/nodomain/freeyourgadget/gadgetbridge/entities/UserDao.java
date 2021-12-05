package nodomain.freeyourgadget.gadgetbridge.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import java.util.Date;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.internal.DaoConfig;

public class UserDao extends AbstractDao<User, Long> {
    public static final String TABLENAME = "USER";
    private DaoSession daoSession;

    public static class Properties {
        public static final Property Birthday = new Property(2, Date.class, "birthday", false, "BIRTHDAY");
        public static final Property Gender = new Property(3, Integer.TYPE, "gender", false, "GENDER");

        /* renamed from: Id */
        public static final Property f154Id = new Property(0, Long.class, PackageConfigHelper.DB_ID, true, "_id");
        public static final Property Name = new Property(1, String.class, "name", false, "NAME");
    }

    public UserDao(DaoConfig config) {
        super(config);
    }

    public UserDao(DaoConfig config, DaoSession daoSession2) {
        super(config, daoSession2);
        this.daoSession = daoSession2;
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER\" (\"_id\" INTEGER PRIMARY KEY ,\"NAME\" TEXT NOT NULL ,\"BIRTHDAY\" INTEGER NOT NULL ,\"GENDER\" INTEGER NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        sb.append(ifExists ? "IF EXISTS " : "");
        sb.append("\"USER\"");
        db.execSQL(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        stmt.bindString(2, entity.getName());
        stmt.bindLong(3, entity.getBirthday().getTime());
        stmt.bindLong(4, (long) entity.getGender());
    }

    /* access modifiers changed from: protected */
    public void attachEntity(User entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(this.daoSession);
    }

    public Long readKey(Cursor cursor, int offset) {
        if (cursor.isNull(offset + 0)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(offset + 0));
    }

    public User readEntity(Cursor cursor, int offset) {
        return new User(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)), cursor.getString(offset + 1), new Date(cursor.getLong(offset + 2)), cursor.getInt(offset + 3));
    }

    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)));
        entity.setName(cursor.getString(offset + 1));
        entity.setBirthday(new Date(cursor.getLong(offset + 2)));
        entity.setGender(cursor.getInt(offset + 3));
    }

    /* access modifiers changed from: protected */
    public Long updateKeyAfterInsert(User entity, long rowId) {
        entity.setId(Long.valueOf(rowId));
        return Long.valueOf(rowId);
    }

    public Long getKey(User entity) {
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
