package p008de.greenrobot.dao.query;

import android.database.Cursor;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.DaoException;

/* renamed from: de.greenrobot.dao.query.CountQuery */
public class CountQuery<T> extends AbstractQuery<T> {
    private final QueryData<T> queryData;

    public /* bridge */ /* synthetic */ AbstractQuery setParameter(int i, Object obj) {
        return super.setParameter(i, obj);
    }

    /* renamed from: de.greenrobot.dao.query.CountQuery$QueryData */
    private static final class QueryData<T2> extends AbstractQueryData<T2, CountQuery<T2>> {
        private QueryData(AbstractDao<T2, ?> dao, String sql, String[] initialValues) {
            super(dao, sql, initialValues);
        }

        /* access modifiers changed from: protected */
        public CountQuery<T2> createQuery() {
            return new CountQuery(this, this.dao, this.sql, (String[]) this.initialValues.clone());
        }
    }

    static <T2> CountQuery<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        return (CountQuery) new QueryData<>(dao, sql, toStringArray(initialValues)).forCurrentThread();
    }

    private CountQuery(QueryData<T> queryData2, AbstractDao<T, ?> dao, String sql, String[] initialValues) {
        super(dao, sql, initialValues);
        this.queryData = queryData2;
    }

    public CountQuery<T> forCurrentThread() {
        return (CountQuery) this.queryData.forCurrentThread(this);
    }

    public long count() {
        checkThread();
        Cursor cursor = this.dao.getDatabase().rawQuery(this.sql, this.parameters);
        try {
            if (!cursor.moveToNext()) {
                throw new DaoException("No result for count");
            } else if (!cursor.isLast()) {
                throw new DaoException("Unexpected row count: " + cursor.getCount());
            } else if (cursor.getColumnCount() == 1) {
                return cursor.getLong(0);
            } else {
                throw new DaoException("Unexpected column count: " + cursor.getColumnCount());
            }
        } finally {
            cursor.close();
        }
    }
}
