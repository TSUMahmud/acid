package p008de.greenrobot.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import p008de.greenrobot.dao.identityscope.IdentityScope;
import p008de.greenrobot.dao.internal.DaoConfig;

/* renamed from: de.greenrobot.dao.InternalUnitTestDaoAccess */
public class InternalUnitTestDaoAccess<T, K> {
    private final AbstractDao<T, K> dao;

    public InternalUnitTestDaoAccess(SQLiteDatabase db, Class<AbstractDao<T, K>> daoClass, IdentityScope<?, ?> identityScope) throws Exception {
        DaoConfig daoConfig = new DaoConfig(db, daoClass);
        daoConfig.setIdentityScope(identityScope);
        this.dao = daoClass.getConstructor(new Class[]{DaoConfig.class}).newInstance(new Object[]{daoConfig});
    }

    public K getKey(T entity) {
        return this.dao.getKey(entity);
    }

    public Property[] getProperties() {
        return this.dao.getProperties();
    }

    public boolean isEntityUpdateable() {
        return this.dao.isEntityUpdateable();
    }

    public T readEntity(Cursor cursor, int offset) {
        return this.dao.readEntity(cursor, offset);
    }

    public K readKey(Cursor cursor, int offset) {
        return this.dao.readKey(cursor, offset);
    }

    public AbstractDao<T, K> getDao() {
        return this.dao;
    }
}
