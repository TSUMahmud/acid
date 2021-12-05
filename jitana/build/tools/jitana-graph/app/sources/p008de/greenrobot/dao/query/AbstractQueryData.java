package p008de.greenrobot.dao.query;

import android.os.Process;
import android.util.SparseArray;
import java.lang.ref.WeakReference;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.query.AbstractQuery;

/* renamed from: de.greenrobot.dao.query.AbstractQueryData */
abstract class AbstractQueryData<T, Q extends AbstractQuery<T>> {
    final AbstractDao<T, ?> dao;
    final String[] initialValues;
    final SparseArray<WeakReference<Q>> queriesForThreads = new SparseArray<>();
    final String sql;

    /* access modifiers changed from: protected */
    public abstract Q createQuery();

    AbstractQueryData(AbstractDao<T, ?> dao2, String sql2, String[] initialValues2) {
        this.dao = dao2;
        this.sql = sql2;
        this.initialValues = initialValues2;
    }

    /* access modifiers changed from: package-private */
    public Q forCurrentThread(Q query) {
        if (Thread.currentThread() != query.ownerThread) {
            return forCurrentThread();
        }
        System.arraycopy(this.initialValues, 0, query.parameters, 0, this.initialValues.length);
        return query;
    }

    /* access modifiers changed from: package-private */
    public Q forCurrentThread() {
        Q query;
        int threadId = Process.myTid();
        if (threadId == 0) {
            long id = Thread.currentThread().getId();
            if (id < 0 || id > 2147483647L) {
                throw new RuntimeException("Cannot handle thread ID: " + id);
            }
            threadId = (int) id;
        }
        synchronized (this.queriesForThreads) {
            WeakReference<Q> queryRef = this.queriesForThreads.get(threadId);
            query = queryRef != null ? (AbstractQuery) queryRef.get() : null;
            if (query == null) {
                mo15185gc();
                query = createQuery();
                this.queriesForThreads.put(threadId, new WeakReference(query));
            } else {
                System.arraycopy(this.initialValues, 0, query.parameters, 0, this.initialValues.length);
            }
        }
        return query;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: gc */
    public void mo15185gc() {
        synchronized (this.queriesForThreads) {
            for (int i = this.queriesForThreads.size() - 1; i >= 0; i--) {
                if (this.queriesForThreads.valueAt(i).get() == null) {
                    this.queriesForThreads.remove(this.queriesForThreads.keyAt(i));
                }
            }
        }
    }
}
