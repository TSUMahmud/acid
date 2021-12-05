package p008de.greenrobot.dao.query;

import android.database.Cursor;
import java.util.Date;
import p008de.greenrobot.dao.AbstractDao;

/* renamed from: de.greenrobot.dao.query.CursorQuery */
public class CursorQuery<T> extends AbstractQueryWithLimit<T> {
    private final QueryData<T> queryData;

    public /* bridge */ /* synthetic */ void setLimit(int i) {
        super.setLimit(i);
    }

    public /* bridge */ /* synthetic */ void setOffset(int i) {
        super.setOffset(i);
    }

    public /* bridge */ /* synthetic */ AbstractQueryWithLimit setParameter(int i, Boolean bool) {
        return super.setParameter(i, bool);
    }

    public /* bridge */ /* synthetic */ AbstractQueryWithLimit setParameter(int i, Object obj) {
        return super.setParameter(i, obj);
    }

    public /* bridge */ /* synthetic */ AbstractQueryWithLimit setParameter(int i, Date date) {
        return super.setParameter(i, date);
    }

    /* renamed from: de.greenrobot.dao.query.CursorQuery$QueryData */
    private static final class QueryData<T2> extends AbstractQueryData<T2, CursorQuery<T2>> {
        private final int limitPosition;
        private final int offsetPosition;

        QueryData(AbstractDao dao, String sql, String[] initialValues, int limitPosition2, int offsetPosition2) {
            super(dao, sql, initialValues);
            this.limitPosition = limitPosition2;
            this.offsetPosition = offsetPosition2;
        }

        /* access modifiers changed from: protected */
        public CursorQuery<T2> createQuery() {
            return new CursorQuery(this, this.dao, this.sql, (String[]) this.initialValues.clone(), this.limitPosition, this.offsetPosition);
        }
    }

    public static <T2> CursorQuery<T2> internalCreate(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        return create(dao, sql, initialValues, -1, -1);
    }

    static <T2> CursorQuery<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues, int limitPosition, int offsetPosition) {
        return (CursorQuery) new QueryData<>(dao, sql, toStringArray(initialValues), limitPosition, offsetPosition).forCurrentThread();
    }

    private CursorQuery(QueryData<T> queryData2, AbstractDao<T, ?> dao, String sql, String[] initialValues, int limitPosition, int offsetPosition) {
        super(dao, sql, initialValues, limitPosition, offsetPosition);
        this.queryData = queryData2;
    }

    public CursorQuery forCurrentThread() {
        return (CursorQuery) this.queryData.forCurrentThread(this);
    }

    public Cursor query() {
        checkThread();
        return this.dao.getDatabase().rawQuery(this.sql, this.parameters);
    }
}
