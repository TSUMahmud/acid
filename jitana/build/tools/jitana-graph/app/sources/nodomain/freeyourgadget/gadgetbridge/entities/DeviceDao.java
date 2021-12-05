package nodomain.freeyourgadget.gadgetbridge.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.internal.DaoConfig;

public class DeviceDao extends AbstractDao<Device, Long> {
    public static final String TABLENAME = "DEVICE";
    private DaoSession daoSession;

    public static class Properties {

        /* renamed from: Id */
        public static final Property f144Id = new Property(0, Long.class, PackageConfigHelper.DB_ID, true, "_id");
        public static final Property Identifier = new Property(3, String.class, "identifier", false, "IDENTIFIER");
        public static final Property Manufacturer = new Property(2, String.class, "manufacturer", false, "MANUFACTURER");
        public static final Property Model = new Property(5, String.class, "model", false, "MODEL");
        public static final Property Name = new Property(1, String.class, "name", false, "NAME");
        public static final Property Type = new Property(4, Integer.TYPE, "type", false, "TYPE");
    }

    public DeviceDao(DaoConfig config) {
        super(config);
    }

    public DeviceDao(DaoConfig config, DaoSession daoSession2) {
        super(config, daoSession2);
        this.daoSession = daoSession2;
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"DEVICE\" (\"_id\" INTEGER PRIMARY KEY ,\"NAME\" TEXT NOT NULL ,\"MANUFACTURER\" TEXT NOT NULL ,\"IDENTIFIER\" TEXT NOT NULL UNIQUE ,\"TYPE\" INTEGER NOT NULL ,\"MODEL\" TEXT);");
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        sb.append(ifExists ? "IF EXISTS " : "");
        sb.append("\"DEVICE\"");
        db.execSQL(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement stmt, Device entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        stmt.bindString(2, entity.getName());
        stmt.bindString(3, entity.getManufacturer());
        stmt.bindString(4, entity.getIdentifier());
        stmt.bindLong(5, (long) entity.getType());
        String model = entity.getModel();
        if (model != null) {
            stmt.bindString(6, model);
        }
    }

    /* access modifiers changed from: protected */
    public void attachEntity(Device entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(this.daoSession);
    }

    public Long readKey(Cursor cursor, int offset) {
        if (cursor.isNull(offset + 0)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(offset + 0));
    }

    public Device readEntity(Cursor cursor, int offset) {
        return new Device(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)), cursor.getString(offset + 1), cursor.getString(offset + 2), cursor.getString(offset + 3), cursor.getInt(offset + 4), cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
    }

    public void readEntity(Cursor cursor, Device entity, int offset) {
        String str = null;
        entity.setId(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)));
        entity.setName(cursor.getString(offset + 1));
        entity.setManufacturer(cursor.getString(offset + 2));
        entity.setIdentifier(cursor.getString(offset + 3));
        entity.setType(cursor.getInt(offset + 4));
        if (!cursor.isNull(offset + 5)) {
            str = cursor.getString(offset + 5);
        }
        entity.setModel(str);
    }

    /* access modifiers changed from: protected */
    public Long updateKeyAfterInsert(Device entity, long rowId) {
        entity.setId(Long.valueOf(rowId));
        return Long.valueOf(rowId);
    }

    public Long getKey(Device entity) {
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
