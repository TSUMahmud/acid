package p008de.greenrobot.dao;

import android.database.sqlite.SQLiteDatabase;
import java.util.HashMap;
import java.util.Map;
import p008de.greenrobot.dao.identityscope.IdentityScopeType;
import p008de.greenrobot.dao.internal.DaoConfig;

/* renamed from: de.greenrobot.dao.AbstractDaoMaster */
public abstract class AbstractDaoMaster {
    protected final Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap = new HashMap();

    /* renamed from: db */
    protected final SQLiteDatabase f107db;
    protected final int schemaVersion;

    public abstract AbstractDaoSession newSession();

    public abstract AbstractDaoSession newSession(IdentityScopeType identityScopeType);

    public AbstractDaoMaster(SQLiteDatabase db, int schemaVersion2) {
        this.f107db = db;
        this.schemaVersion = schemaVersion2;
    }

    /* access modifiers changed from: protected */
    public void registerDaoClass(Class<? extends AbstractDao<?, ?>> daoClass) {
        this.daoConfigMap.put(daoClass, new DaoConfig(this.f107db, daoClass));
    }

    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    public SQLiteDatabase getDatabase() {
        return this.f107db;
    }
}
