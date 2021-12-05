package p008de.greenrobot.dao.async;

import android.database.sqlite.SQLiteDatabase;
import p008de.greenrobot.dao.AbstractDao;
import p008de.greenrobot.dao.DaoException;

/* renamed from: de.greenrobot.dao.async.AsyncOperation */
public class AsyncOperation {
    public static final int FLAG_MERGE_TX = 1;
    public static final int FLAG_STOP_QUEUE_ON_EXCEPTION = 2;
    public static final int FLAG_TRACK_CREATOR_STACKTRACE = 4;
    private volatile boolean completed;
    final Exception creatorStacktrace;
    final AbstractDao<Object, Object> dao;
    private final SQLiteDatabase database;
    final int flags;
    volatile int mergedOperationsCount;
    final Object parameter;
    volatile Object result;
    int sequenceNumber;
    volatile Throwable throwable;
    volatile long timeCompleted;
    volatile long timeStarted;
    final OperationType type;

    /* renamed from: de.greenrobot.dao.async.AsyncOperation$OperationType */
    public enum OperationType {
        Insert,
        InsertInTxIterable,
        InsertInTxArray,
        InsertOrReplace,
        InsertOrReplaceInTxIterable,
        InsertOrReplaceInTxArray,
        Update,
        UpdateInTxIterable,
        UpdateInTxArray,
        Delete,
        DeleteInTxIterable,
        DeleteInTxArray,
        DeleteByKey,
        DeleteAll,
        TransactionRunnable,
        TransactionCallable,
        QueryList,
        QueryUnique,
        Load,
        LoadAll,
        Count,
        Refresh
    }

    AsyncOperation(OperationType type2, AbstractDao<?, ?> dao2, SQLiteDatabase database2, Object parameter2, int flags2) {
        this.type = type2;
        this.flags = flags2;
        this.dao = dao2;
        this.database = database2;
        this.parameter = parameter2;
        this.creatorStacktrace = (flags2 & 4) != 0 ? new Exception("AsyncOperation was created here") : null;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public void setThrowable(Throwable throwable2) {
        this.throwable = throwable2;
    }

    public OperationType getType() {
        return this.type;
    }

    public Object getParameter() {
        return this.parameter;
    }

    public synchronized Object getResult() {
        if (!this.completed) {
            waitForCompletion();
        }
        if (this.throwable == null) {
        } else {
            throw new AsyncDaoException(this, this.throwable);
        }
        return this.result;
    }

    public boolean isMergeTx() {
        return (this.flags & 1) != 0;
    }

    /* access modifiers changed from: package-private */
    public SQLiteDatabase getDatabase() {
        SQLiteDatabase sQLiteDatabase = this.database;
        return sQLiteDatabase != null ? sQLiteDatabase : this.dao.getDatabase();
    }

    /* access modifiers changed from: package-private */
    public boolean isMergeableWith(AsyncOperation other) {
        return other != null && isMergeTx() && other.isMergeTx() && getDatabase() == other.getDatabase();
    }

    public long getTimeStarted() {
        return this.timeStarted;
    }

    public long getTimeCompleted() {
        return this.timeCompleted;
    }

    public long getDuration() {
        if (this.timeCompleted != 0) {
            return this.timeCompleted - this.timeStarted;
        }
        throw new DaoException("This operation did not yet complete");
    }

    public boolean isFailed() {
        return this.throwable != null;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public synchronized Object waitForCompletion() {
        while (!this.completed) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new DaoException("Interrupted while waiting for operation to complete", e);
            }
        }
        return this.result;
    }

    public synchronized boolean waitForCompletion(int maxMillis) {
        if (!this.completed) {
            try {
                wait((long) maxMillis);
            } catch (InterruptedException e) {
                throw new DaoException("Interrupted while waiting for operation to complete", e);
            }
        }
        return this.completed;
    }

    /* access modifiers changed from: package-private */
    public synchronized void setCompleted() {
        this.completed = true;
        notifyAll();
    }

    public boolean isCompletedSucessfully() {
        return this.completed && this.throwable == null;
    }

    public int getMergedOperationsCount() {
        return this.mergedOperationsCount;
    }

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    /* access modifiers changed from: package-private */
    public void reset() {
        this.timeStarted = 0;
        this.timeCompleted = 0;
        this.completed = false;
        this.throwable = null;
        this.result = null;
        this.mergedOperationsCount = 0;
    }

    public Exception getCreatorStacktrace() {
        return this.creatorStacktrace;
    }
}
