package p008de.greenrobot.dao.query;

import java.util.Date;
import java.util.List;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.DaoException;

/* renamed from: de.greenrobot.dao.query.Query */
public class Query<T> extends AbstractQueryWithLimit<T> {
    private final QueryData<T> queryData;

    public /* bridge */ /* synthetic */ void setLimit(int i) {
        super.setLimit(i);
    }

    public /* bridge */ /* synthetic */ void setOffset(int i) {
        super.setOffset(i);
    }

    /* renamed from: de.greenrobot.dao.query.Query$QueryData */
    private static final class QueryData<T2> extends AbstractQueryData<T2, Query<T2>> {
        private final int limitPosition;
        private final int offsetPosition;

        QueryData(AbstractDao<T2, ?> dao, String sql, String[] initialValues, int limitPosition2, int offsetPosition2) {
            super(dao, sql, initialValues);
            this.limitPosition = limitPosition2;
            this.offsetPosition = offsetPosition2;
        }

        /* access modifiers changed from: protected */
        public Query<T2> createQuery() {
            return new Query(this, this.dao, this.sql, (String[]) this.initialValues.clone(), this.limitPosition, this.offsetPosition);
        }
    }

    public static <T2> Query<T2> internalCreate(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        return create(dao, sql, initialValues, -1, -1);
    }

    static <T2> Query<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues, int limitPosition, int offsetPosition) {
        return (Query) new QueryData<>(dao, sql, toStringArray(initialValues), limitPosition, offsetPosition).forCurrentThread();
    }

    private Query(QueryData<T> queryData2, AbstractDao<T, ?> dao, String sql, String[] initialValues, int limitPosition, int offsetPosition) {
        super(dao, sql, initialValues, limitPosition, offsetPosition);
        this.queryData = queryData2;
    }

    public Query<T> forCurrentThread() {
        return (Query) this.queryData.forCurrentThread(this);
    }

    public List<T> list() {
        checkThread();
        return this.daoAccess.loadAllAndCloseCursor(this.dao.getDatabase().rawQuery(this.sql, this.parameters));
    }

    public LazyList<T> listLazy() {
        checkThread();
        return new LazyList<>(this.daoAccess, this.dao.getDatabase().rawQuery(this.sql, this.parameters), true);
    }

    public LazyList<T> listLazyUncached() {
        checkThread();
        return new LazyList<>(this.daoAccess, this.dao.getDatabase().rawQuery(this.sql, this.parameters), false);
    }

    public CloseableListIterator<T> listIterator() {
        return listLazyUncached().listIteratorAutoClose();
    }

    public T unique() {
        checkThread();
        return this.daoAccess.loadUniqueAndCloseCursor(this.dao.getDatabase().rawQuery(this.sql, this.parameters));
    }

    public T uniqueOrThrow() {
        T entity = unique();
        if (entity != null) {
            return entity;
        }
        throw new DaoException("No entity found for query");
    }

    public Query<T> setParameter(int index, Object parameter) {
        return (Query) super.setParameter(index, parameter);
    }

    public Query<T> setParameter(int index, Date parameter) {
        return (Query) super.setParameter(index, parameter);
    }

    public Query<T> setParameter(int index, Boolean parameter) {
        return (Query) super.setParameter(index, parameter);
    }
}
