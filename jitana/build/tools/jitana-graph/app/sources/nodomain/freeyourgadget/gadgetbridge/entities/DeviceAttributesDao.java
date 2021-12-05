package nodomain.freeyourgadget.gadgetbridge.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import java.util.Date;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.Property;
import p008de.greenrobot.dao.internal.DaoConfig;
import p008de.greenrobot.dao.query.Query;
import p008de.greenrobot.dao.query.QueryBuilder;
import p008de.greenrobot.dao.query.WhereCondition;

public class DeviceAttributesDao extends AbstractDao<DeviceAttributes, Long> {
    public static final String TABLENAME = "DEVICE_ATTRIBUTES";
    private Query<DeviceAttributes> device_DeviceAttributesListQuery;

    public static class Properties {
        public static final Property DeviceId = new Property(5, Long.TYPE, "deviceId", false, "DEVICE_ID");
        public static final Property FirmwareVersion1 = new Property(1, String.class, "firmwareVersion1", false, "FIRMWARE_VERSION1");
        public static final Property FirmwareVersion2 = new Property(2, String.class, "firmwareVersion2", false, "FIRMWARE_VERSION2");

        /* renamed from: Id */
        public static final Property f143Id = new Property(0, Long.class, PackageConfigHelper.DB_ID, true, "_id");
        public static final Property ValidFromUTC = new Property(3, Date.class, "validFromUTC", false, "VALID_FROM_UTC");
        public static final Property ValidToUTC = new Property(4, Date.class, "validToUTC", false, "VALID_TO_UTC");
        public static final Property VolatileIdentifier = new Property(6, String.class, "volatileIdentifier", false, "VOLATILE_IDENTIFIER");
    }

    public DeviceAttributesDao(DaoConfig config) {
        super(config);
    }

    public DeviceAttributesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"DEVICE_ATTRIBUTES\" (\"_id\" INTEGER PRIMARY KEY ,\"FIRMWARE_VERSION1\" TEXT NOT NULL ,\"FIRMWARE_VERSION2\" TEXT,\"VALID_FROM_UTC\" INTEGER,\"VALID_TO_UTC\" INTEGER,\"DEVICE_ID\" INTEGER NOT NULL ,\"VOLATILE_IDENTIFIER\" TEXT);");
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        sb.append(ifExists ? "IF EXISTS " : "");
        sb.append("\"DEVICE_ATTRIBUTES\"");
        db.execSQL(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement stmt, DeviceAttributes entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        stmt.bindString(2, entity.getFirmwareVersion1());
        String firmwareVersion2 = entity.getFirmwareVersion2();
        if (firmwareVersion2 != null) {
            stmt.bindString(3, firmwareVersion2);
        }
        Date validFromUTC = entity.getValidFromUTC();
        if (validFromUTC != null) {
            stmt.bindLong(4, validFromUTC.getTime());
        }
        Date validToUTC = entity.getValidToUTC();
        if (validToUTC != null) {
            stmt.bindLong(5, validToUTC.getTime());
        }
        stmt.bindLong(6, entity.getDeviceId());
        String volatileIdentifier = entity.getVolatileIdentifier();
        if (volatileIdentifier != null) {
            stmt.bindString(7, volatileIdentifier);
        }
    }

    public Long readKey(Cursor cursor, int offset) {
        if (cursor.isNull(offset + 0)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(offset + 0));
    }

    public DeviceAttributes readEntity(Cursor cursor, int offset) {
        return new DeviceAttributes(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)), cursor.getString(offset + 1), cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), cursor.isNull(offset + 3) ? null : new Date(cursor.getLong(offset + 3)), cursor.isNull(offset + 4) ? null : new Date(cursor.getLong(offset + 4)), cursor.getLong(offset + 5), cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
    }

    public void readEntity(Cursor cursor, DeviceAttributes entity, int offset) {
        String str = null;
        entity.setId(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)));
        entity.setFirmwareVersion1(cursor.getString(offset + 1));
        entity.setFirmwareVersion2(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setValidFromUTC(cursor.isNull(offset + 3) ? null : new Date(cursor.getLong(offset + 3)));
        entity.setValidToUTC(cursor.isNull(offset + 4) ? null : new Date(cursor.getLong(offset + 4)));
        entity.setDeviceId(cursor.getLong(offset + 5));
        if (!cursor.isNull(offset + 6)) {
            str = cursor.getString(offset + 6);
        }
        entity.setVolatileIdentifier(str);
    }

    /* access modifiers changed from: protected */
    public Long updateKeyAfterInsert(DeviceAttributes entity, long rowId) {
        entity.setId(Long.valueOf(rowId));
        return Long.valueOf(rowId);
    }

    public Long getKey(DeviceAttributes entity) {
        if (entity != null) {
            return entity.getId();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isEntityUpdateable() {
        return true;
    }

    public List<DeviceAttributes> _queryDevice_DeviceAttributesList(long deviceId) {
        synchronized (this) {
            if (this.device_DeviceAttributesListQuery == null) {
                QueryBuilder<DeviceAttributes> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.DeviceId.mo14989eq((Object) null), new WhereCondition[0]);
                queryBuilder.orderRaw("T.'VALID_FROM_UTC' DESC");
                this.device_DeviceAttributesListQuery = queryBuilder.build();
            }
        }
        Query<DeviceAttributes> query = this.device_DeviceAttributesListQuery.forCurrentThread();
        query.setParameter(0, (Object) Long.valueOf(deviceId));
        return query.list();
    }
}
