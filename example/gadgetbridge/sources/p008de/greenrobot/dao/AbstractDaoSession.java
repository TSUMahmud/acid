package p008de.greenrobot.dao;

import android.database.sqlite.SQLiteDatabase;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import p008de.greenrobot.dao.async.AsyncSession;
import p008de.greenrobot.dao.query.QueryBuilder;

/* renamed from: de.greenrobot.dao.AbstractDaoSession */
public class AbstractDaoSession {

    /* renamed from: db */
    private final SQLiteDatabase f108db;
    private final Map<Class<?>, AbstractDao<?, ?>> entityToDao = new HashMap();

    public AbstractDaoSession(SQLiteDatabase db) {
        this.f108db = db;
    }

    /* access modifiers changed from: protected */
    public <T> void registerDao(Class<T> entityClass, AbstractDao<T, ?> dao) {
        this.entityToDao.put(entityClass, dao);
    }

    public <T> long insert(T entity) {
        return getDao(entity.getClass()).insert(entity);
    }

    public <T> long insertOrReplace(T entity) {
        return getDao(entity.getClass()).insertOrReplace(entity);
    }

    public <T> void refresh(T entity) {
        getDao(entity.getClass()).refresh(entity);
    }

    public <T> void update(T entity) {
        getDao(entity.getClass()).update(entity);
    }

    public <T> void delete(T entity) {
        getDao(entity.getClass()).delete(entity);
    }

    public <T> void deleteAll(Class<T> entityClass) {
        getDao(entityClass).deleteAll();
    }

    public <T, K> T load(Class<T> entityClass, K key) {
        return getDao(entityClass).load(key);
    }

    public <T, K> List<T> loadAll(Class<T> entityClass) {
        return getDao(entityClass).loadAll();
    }

    public <T, K> List<T> queryRaw(Class<T> entityClass, String where, String... selectionArgs) {
        return getDao(entityClass).queryRaw(where, selectionArgs);
    }

    public <T> QueryBuilder<T> queryBuilder(Class<T> entityClass) {
        return getDao(entityClass).queryBuilder();
    }

    public AbstractDao<?, ?> getDao(Class<? extends Object> entityClass) {
        AbstractDao<?, ?> dao = this.entityToDao.get(entityClass);
        if (dao != null) {
            return dao;
        }
        throw new DaoException("No DAO registered for " + entityClass);
    }

    public void runInTx(Runnable runnable) {
        this.f108db.beginTransaction();
        try {
            runnable.run();
            this.f108db.setTransactionSuccessful();
        } finally {
            this.f108db.endTransaction();
        }
    }

    public <V> V callInTx(Callable<V> callable) throws Exception {
        this.f108db.beginTransaction();
        try {
            V result = callable.call();
            this.f108db.setTransactionSuccessful();
            return result;
        } finally {
            this.f108db.endTransaction();
        }
    }

    public <V> V callInTxNoException(Callable<V> callable) {
        this.f108db.beginTransaction();
        try {
            V result = callable.call();
            this.f108db.setTransactionSuccessful();
            this.f108db.endTransaction();
            return result;
        } catch (Exception e) {
            throw new DaoException("Callable failed", e);
        } catch (Throwable result2) {
            this.f108db.endTransaction();
            throw result2;
        }
    }

    public SQLiteDatabase getDatabase() {
        return this.f108db;
    }

    public Collection<AbstractDao<?, ?>> getAllDaos() {
        return Collections.unmodifiableCollection(this.entityToDao.values());
    }

    public AsyncSession startAsyncSession() {
        return new AsyncSession(this);
    }
}
