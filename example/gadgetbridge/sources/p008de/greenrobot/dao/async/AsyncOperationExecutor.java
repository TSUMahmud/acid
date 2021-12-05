package p008de.greenrobot.dao.async;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import p008de.greenrobot.dao.DaoException;
import p008de.greenrobot.dao.DaoLog;
import p008de.greenrobot.dao.query.Query;

/* renamed from: de.greenrobot.dao.async.AsyncOperationExecutor */
class AsyncOperationExecutor implements Runnable, Handler.Callback {
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    private int countOperationsCompleted;
    private int countOperationsEnqueued;
    private volatile boolean executorRunning;
    private Handler handlerMainThread;
    private int lastSequenceNumber;
    private volatile AsyncOperationListener listener;
    private volatile AsyncOperationListener listenerMainThread;
    private volatile int maxOperationCountToMerge = 50;
    private final BlockingQueue<AsyncOperation> queue = new LinkedBlockingQueue();
    private volatile int waitForMergeMillis = 50;

    AsyncOperationExecutor() {
    }

    public void enqueue(AsyncOperation operation) {
        synchronized (this) {
            int i = this.lastSequenceNumber + 1;
            this.lastSequenceNumber = i;
            operation.sequenceNumber = i;
            this.queue.add(operation);
            this.countOperationsEnqueued++;
            if (!this.executorRunning) {
                this.executorRunning = true;
                executorService.execute(this);
            }
        }
    }

    public int getMaxOperationCountToMerge() {
        return this.maxOperationCountToMerge;
    }

    public void setMaxOperationCountToMerge(int maxOperationCountToMerge2) {
        this.maxOperationCountToMerge = maxOperationCountToMerge2;
    }

    public int getWaitForMergeMillis() {
        return this.waitForMergeMillis;
    }

    public void setWaitForMergeMillis(int waitForMergeMillis2) {
        this.waitForMergeMillis = waitForMergeMillis2;
    }

    public AsyncOperationListener getListener() {
        return this.listener;
    }

    public void setListener(AsyncOperationListener listener2) {
        this.listener = listener2;
    }

    public AsyncOperationListener getListenerMainThread() {
        return this.listenerMainThread;
    }

    public void setListenerMainThread(AsyncOperationListener listenerMainThread2) {
        this.listenerMainThread = listenerMainThread2;
    }

    public synchronized boolean isCompleted() {
        return this.countOperationsEnqueued == this.countOperationsCompleted;
    }

    public synchronized void waitForCompletion() {
        while (!isCompleted()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new DaoException("Interrupted while waiting for all operations to complete", e);
            }
        }
    }

    public synchronized boolean waitForCompletion(int maxMillis) {
        if (!isCompleted()) {
            try {
                wait((long) maxMillis);
            } catch (InterruptedException e) {
                throw new DaoException("Interrupted while waiting for all operations to complete", e);
            }
        }
        return isCompleted();
    }

    public void run() {
        AsyncOperation operation2;
        while (true) {
            try {
                AsyncOperation operation = this.queue.poll(1, TimeUnit.SECONDS);
                if (operation == null) {
                    synchronized (this) {
                        operation = (AsyncOperation) this.queue.poll();
                        if (operation == null) {
                            this.executorRunning = false;
                            this.executorRunning = false;
                            return;
                        }
                    }
                }
                if (!operation.isMergeTx() || (operation2 = this.queue.poll((long) this.waitForMergeMillis, TimeUnit.MILLISECONDS)) == null) {
                    executeOperationAndPostCompleted(operation);
                } else if (operation.isMergeableWith(operation2)) {
                    mergeTxAndExecute(operation, operation2);
                } else {
                    executeOperationAndPostCompleted(operation);
                    executeOperationAndPostCompleted(operation2);
                }
            } catch (InterruptedException e) {
                try {
                    DaoLog.m27w(Thread.currentThread().getName() + " was interruppted", e);
                    return;
                } finally {
                    this.executorRunning = false;
                }
            }
        }
    }

    private void mergeTxAndExecute(AsyncOperation operation1, AsyncOperation operation2) {
        ArrayList<AsyncOperation> mergedOps = new ArrayList<>();
        mergedOps.add(operation1);
        mergedOps.add(operation2);
        SQLiteDatabase db = operation1.getDatabase();
        db.beginTransaction();
        boolean success = false;
        int i = 0;
        while (true) {
            try {
                if (i < mergedOps.size()) {
                    AsyncOperation operation = mergedOps.get(i);
                    executeOperation(operation);
                    if (operation.isFailed()) {
                        break;
                    }
                    if (i == mergedOps.size() - 1) {
                        AsyncOperation peekedOp = (AsyncOperation) this.queue.peek();
                        if (i >= this.maxOperationCountToMerge || !operation.isMergeableWith(peekedOp)) {
                            db.setTransactionSuccessful();
                        } else {
                            AsyncOperation removedOp = (AsyncOperation) this.queue.remove();
                            if (removedOp == peekedOp) {
                                mergedOps.add(removedOp);
                            } else {
                                throw new DaoException("Internal error: peeked op did not match removed op");
                            }
                        }
                    }
                    i++;
                }
            } finally {
                try {
                    db.endTransaction();
                } catch (RuntimeException e) {
                    DaoLog.m23i("Async transaction could not be ended, success so far was: " + success, e);
                }
            }
        }
        db.setTransactionSuccessful();
        success = true;
        if (success) {
            int mergedCount = mergedOps.size();
            Iterator<AsyncOperation> it = mergedOps.iterator();
            while (it.hasNext()) {
                AsyncOperation asyncOperation = it.next();
                asyncOperation.mergedOperationsCount = mergedCount;
                handleOperationCompleted(asyncOperation);
            }
            return;
        }
        DaoLog.m22i("Reverted merged transaction because one of the operations failed. Executing operations one by one instead...");
        Iterator<AsyncOperation> it2 = mergedOps.iterator();
        while (it2.hasNext()) {
            AsyncOperation asyncOperation2 = it2.next();
            asyncOperation2.reset();
            executeOperationAndPostCompleted(asyncOperation2);
        }
    }

    private void handleOperationCompleted(AsyncOperation operation) {
        operation.setCompleted();
        AsyncOperationListener listenerToCall = this.listener;
        if (listenerToCall != null) {
            listenerToCall.onAsyncOperationCompleted(operation);
        }
        if (this.listenerMainThread != null) {
            if (this.handlerMainThread == null) {
                this.handlerMainThread = new Handler(Looper.getMainLooper(), this);
            }
            this.handlerMainThread.sendMessage(this.handlerMainThread.obtainMessage(1, operation));
        }
        synchronized (this) {
            this.countOperationsCompleted++;
            if (this.countOperationsCompleted == this.countOperationsEnqueued) {
                notifyAll();
            }
        }
    }

    private void executeOperationAndPostCompleted(AsyncOperation operation) {
        executeOperation(operation);
        handleOperationCompleted(operation);
    }

    private void executeOperation(AsyncOperation operation) {
        operation.timeStarted = System.currentTimeMillis();
        try {
            switch (operation.type) {
                case Delete:
                    operation.dao.delete(operation.parameter);
                    break;
                case DeleteInTxIterable:
                    operation.dao.deleteInTx((Iterable) operation.parameter);
                    break;
                case DeleteInTxArray:
                    operation.dao.deleteInTx((T[]) (Object[]) operation.parameter);
                    break;
                case Insert:
                    operation.dao.insert(operation.parameter);
                    break;
                case InsertInTxIterable:
                    operation.dao.insertInTx((Iterable) operation.parameter);
                    break;
                case InsertInTxArray:
                    operation.dao.insertInTx((T[]) (Object[]) operation.parameter);
                    break;
                case InsertOrReplace:
                    operation.dao.insertOrReplace(operation.parameter);
                    break;
                case InsertOrReplaceInTxIterable:
                    operation.dao.insertOrReplaceInTx((Iterable) operation.parameter);
                    break;
                case InsertOrReplaceInTxArray:
                    operation.dao.insertOrReplaceInTx((T[]) (Object[]) operation.parameter);
                    break;
                case Update:
                    operation.dao.update(operation.parameter);
                    break;
                case UpdateInTxIterable:
                    operation.dao.updateInTx((Iterable) operation.parameter);
                    break;
                case UpdateInTxArray:
                    operation.dao.updateInTx((T[]) (Object[]) operation.parameter);
                    break;
                case TransactionRunnable:
                    executeTransactionRunnable(operation);
                    break;
                case TransactionCallable:
                    executeTransactionCallable(operation);
                    break;
                case QueryList:
                    operation.result = ((Query) operation.parameter).forCurrentThread().list();
                    break;
                case QueryUnique:
                    operation.result = ((Query) operation.parameter).forCurrentThread().unique();
                    break;
                case DeleteByKey:
                    operation.dao.deleteByKey(operation.parameter);
                    break;
                case DeleteAll:
                    operation.dao.deleteAll();
                    break;
                case Load:
                    operation.result = operation.dao.load(operation.parameter);
                    break;
                case LoadAll:
                    operation.result = operation.dao.loadAll();
                    break;
                case Count:
                    operation.result = Long.valueOf(operation.dao.count());
                    break;
                case Refresh:
                    operation.dao.refresh(operation.parameter);
                    break;
                default:
                    throw new DaoException("Unsupported operation: " + operation.type);
            }
        } catch (Throwable th) {
            operation.throwable = th;
        }
        operation.timeCompleted = System.currentTimeMillis();
    }

    private void executeTransactionRunnable(AsyncOperation operation) {
        SQLiteDatabase db = operation.getDatabase();
        db.beginTransaction();
        try {
            ((Runnable) operation.parameter).run();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void executeTransactionCallable(AsyncOperation operation) throws Exception {
        SQLiteDatabase db = operation.getDatabase();
        db.beginTransaction();
        try {
            operation.result = ((Callable) operation.parameter).call();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public boolean handleMessage(Message msg) {
        AsyncOperationListener listenerToCall = this.listenerMainThread;
        if (listenerToCall == null) {
            return false;
        }
        listenerToCall.onAsyncOperationCompleted((AsyncOperation) msg.obj);
        return false;
    }
}
