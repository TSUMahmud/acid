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

public class UserAttributesDao extends AbstractDao<UserAttributes, Long> {
    public static final String TABLENAME = "USER_ATTRIBUTES";
    private Query<UserAttributes> user_UserAttributesListQuery;

    public static class Properties {
        public static final Property HeightCM = new Property(1, Integer.TYPE, "heightCM", false, "HEIGHT_CM");

        /* renamed from: Id */
        public static final Property f153Id = new Property(0, Long.class, PackageConfigHelper.DB_ID, true, "_id");
        public static final Property SleepGoalHPD = new Property(3, Integer.class, "sleepGoalHPD", false, "SLEEP_GOAL_HPD");
        public static final Property StepsGoalSPD = new Property(4, Integer.class, "stepsGoalSPD", false, "STEPS_GOAL_SPD");
        public static final Property UserId = new Property(7, Long.TYPE, "userId", false, "USER_ID");
        public static final Property ValidFromUTC = new Property(5, Date.class, "validFromUTC", false, "VALID_FROM_UTC");
        public static final Property ValidToUTC = new Property(6, Date.class, "validToUTC", false, "VALID_TO_UTC");
        public static final Property WeightKG = new Property(2, Integer.TYPE, "weightKG", false, "WEIGHT_KG");
    }

    public UserAttributesDao(DaoConfig config) {
        super(config);
    }

    public UserAttributesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER_ATTRIBUTES\" (\"_id\" INTEGER PRIMARY KEY ,\"HEIGHT_CM\" INTEGER NOT NULL ,\"WEIGHT_KG\" INTEGER NOT NULL ,\"SLEEP_GOAL_HPD\" INTEGER,\"STEPS_GOAL_SPD\" INTEGER,\"VALID_FROM_UTC\" INTEGER,\"VALID_TO_UTC\" INTEGER,\"USER_ID\" INTEGER NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        sb.append(ifExists ? "IF EXISTS " : "");
        sb.append("\"USER_ATTRIBUTES\"");
        db.execSQL(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement stmt, UserAttributes entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        stmt.bindLong(2, (long) entity.getHeightCM());
        stmt.bindLong(3, (long) entity.getWeightKG());
        Integer sleepGoalHPD = entity.getSleepGoalHPD();
        if (sleepGoalHPD != null) {
            stmt.bindLong(4, (long) sleepGoalHPD.intValue());
        }
        Integer stepsGoalSPD = entity.getStepsGoalSPD();
        if (stepsGoalSPD != null) {
            stmt.bindLong(5, (long) stepsGoalSPD.intValue());
        }
        Date validFromUTC = entity.getValidFromUTC();
        if (validFromUTC != null) {
            stmt.bindLong(6, validFromUTC.getTime());
        }
        Date validToUTC = entity.getValidToUTC();
        if (validToUTC != null) {
            stmt.bindLong(7, validToUTC.getTime());
        }
        stmt.bindLong(8, entity.getUserId());
    }

    public Long readKey(Cursor cursor, int offset) {
        if (cursor.isNull(offset + 0)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(offset + 0));
    }

    public UserAttributes readEntity(Cursor cursor, int offset) {
        return new UserAttributes(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)), cursor.getInt(offset + 1), cursor.getInt(offset + 2), cursor.isNull(offset + 3) ? null : Integer.valueOf(cursor.getInt(offset + 3)), cursor.isNull(offset + 4) ? null : Integer.valueOf(cursor.getInt(offset + 4)), cursor.isNull(offset + 5) ? null : new Date(cursor.getLong(offset + 5)), cursor.isNull(offset + 6) ? null : new Date(cursor.getLong(offset + 6)), cursor.getLong(offset + 7));
    }

    public void readEntity(Cursor cursor, UserAttributes entity, int offset) {
        Date date = null;
        entity.setId(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)));
        entity.setHeightCM(cursor.getInt(offset + 1));
        entity.setWeightKG(cursor.getInt(offset + 2));
        entity.setSleepGoalHPD(cursor.isNull(offset + 3) ? null : Integer.valueOf(cursor.getInt(offset + 3)));
        entity.setStepsGoalSPD(cursor.isNull(offset + 4) ? null : Integer.valueOf(cursor.getInt(offset + 4)));
        entity.setValidFromUTC(cursor.isNull(offset + 5) ? null : new Date(cursor.getLong(offset + 5)));
        if (!cursor.isNull(offset + 6)) {
            date = new Date(cursor.getLong(offset + 6));
        }
        entity.setValidToUTC(date);
        entity.setUserId(cursor.getLong(offset + 7));
    }

    /* access modifiers changed from: protected */
    public Long updateKeyAfterInsert(UserAttributes entity, long rowId) {
        entity.setId(Long.valueOf(rowId));
        return Long.valueOf(rowId);
    }

    public Long getKey(UserAttributes entity) {
        if (entity != null) {
            return entity.getId();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isEntityUpdateable() {
        return true;
    }

    public List<UserAttributes> _queryUser_UserAttributesList(long userId) {
        synchronized (this) {
            if (this.user_UserAttributesListQuery == null) {
                QueryBuilder<UserAttributes> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.UserId.mo14989eq((Object) null), new WhereCondition[0]);
                queryBuilder.orderRaw("T.'VALID_FROM_UTC' DESC");
                this.user_UserAttributesListQuery = queryBuilder.build();
            }
        }
        Query<UserAttributes> query = this.user_UserAttributesListQuery.forCurrentThread();
        query.setParameter(0, (Object) Long.valueOf(userId));
        return query.list();
    }
}
