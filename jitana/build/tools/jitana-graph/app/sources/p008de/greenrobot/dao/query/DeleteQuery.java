package p008de.greenrobot.dao.query;

import android.database.sqlite.SQLiteDatabase;
import p008de.greenrobot.dao.AbstractDao;

/* renamed from: de.greenrobot.dao.query.DeleteQuery */
public class DeleteQuery<T> extends AbstractQuery<T> {
    private final QueryData<T> queryData;

    public /* bridge */ /* synthetic */ AbstractQuery setParameter(int i, Object obj) {
        return super.setParameter(i, obj);
    }

    /* renamed from: de.greenrobot.dao.query.DeleteQuery$QueryData */
    private static final class QueryData<T2> extends AbstractQueryData<T2, DeleteQuery<T2>> {
        private QueryData(AbstractDao<T2, ?> dao, String sql, String[] initialValues) {
            super(dao, sql, initialValues);
        }

        /* access modifiers changed from: protected */
        public DeleteQuery<T2> createQuery() {
            return new DeleteQuery(this, this.dao, this.sql, (String[]) this.initialValues.clone());
        }
    }

    static <T2> DeleteQuery<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        return (DeleteQuery) new QueryData<>(dao, sql, toStringArray(initialValues)).forCurrentThread();
    }

    private DeleteQuery(QueryData<T> queryData2, AbstractDao<T, ?> dao, String sql, String[] initialValues) {
        super(dao, sql, initialValues);
        this.queryData = queryData2;
    }

    public DeleteQuery<T> forCurrentThread() {
        return (DeleteQuery) this.queryData.forCurrentThread(this);
    }

    public void executeDeleteWithoutDetachingEntities() {
        checkThread();
        SQLiteDatabase db = this.dao.getDatabase();
        if (db.isDbLockedByCurrentThread()) {
            this.dao.getDatabase().execSQL(this.sql, this.parameters);
            return;
        }
        db.beginTransaction();
        try {
            this.dao.getDatabase().execSQL(this.sql, this.parameters);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
