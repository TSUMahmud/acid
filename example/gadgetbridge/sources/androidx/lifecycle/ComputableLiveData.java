package androidx.lifecycle;

import androidx.arch.core.executor.ArchTaskExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ComputableLiveData<T> {
    final AtomicBoolean mComputing;
    final Executor mExecutor;
    final AtomicBoolean mInvalid;
    final Runnable mInvalidationRunnable;
    final LiveData<T> mLiveData;
    final Runnable mRefreshRunnable;

    /* access modifiers changed from: protected */
    public abstract T compute();

    public ComputableLiveData() {
        this(ArchTaskExecutor.getIOThreadExecutor());
    }

    public ComputableLiveData(Executor executor) {
        this.mInvalid = new AtomicBoolean(true);
        this.mComputing = new AtomicBoolean(false);
        this.mRefreshRunnable = new Runnable() {
            /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP_START, MTH_ENTER_BLOCK] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r5 = this;
                L_0x0000:
                    r0 = 0
                    androidx.lifecycle.ComputableLiveData r1 = androidx.lifecycle.ComputableLiveData.this
                    java.util.concurrent.atomic.AtomicBoolean r1 = r1.mComputing
                    r2 = 1
                    r3 = 0
                    boolean r1 = r1.compareAndSet(r3, r2)
                    if (r1 == 0) goto L_0x003b
                    r1 = 0
                L_0x000e:
                    androidx.lifecycle.ComputableLiveData r4 = androidx.lifecycle.ComputableLiveData.this     // Catch:{ all -> 0x0032 }
                    java.util.concurrent.atomic.AtomicBoolean r4 = r4.mInvalid     // Catch:{ all -> 0x0032 }
                    boolean r4 = r4.compareAndSet(r2, r3)     // Catch:{ all -> 0x0032 }
                    if (r4 == 0) goto L_0x0021
                    r0 = 1
                    androidx.lifecycle.ComputableLiveData r4 = androidx.lifecycle.ComputableLiveData.this     // Catch:{ all -> 0x0032 }
                    java.lang.Object r4 = r4.compute()     // Catch:{ all -> 0x0032 }
                    r1 = r4
                    goto L_0x000e
                L_0x0021:
                    if (r0 == 0) goto L_0x002a
                    androidx.lifecycle.ComputableLiveData r2 = androidx.lifecycle.ComputableLiveData.this     // Catch:{ all -> 0x0032 }
                    androidx.lifecycle.LiveData<T> r2 = r2.mLiveData     // Catch:{ all -> 0x0032 }
                    r2.postValue(r1)     // Catch:{ all -> 0x0032 }
                L_0x002a:
                    androidx.lifecycle.ComputableLiveData r1 = androidx.lifecycle.ComputableLiveData.this
                    java.util.concurrent.atomic.AtomicBoolean r1 = r1.mComputing
                    r1.set(r3)
                    goto L_0x003b
                L_0x0032:
                    r1 = move-exception
                    androidx.lifecycle.ComputableLiveData r2 = androidx.lifecycle.ComputableLiveData.this
                    java.util.concurrent.atomic.AtomicBoolean r2 = r2.mComputing
                    r2.set(r3)
                    throw r1
                L_0x003b:
                    if (r0 == 0) goto L_0x0047
                    androidx.lifecycle.ComputableLiveData r1 = androidx.lifecycle.ComputableLiveData.this
                    java.util.concurrent.atomic.AtomicBoolean r1 = r1.mInvalid
                    boolean r1 = r1.get()
                    if (r1 != 0) goto L_0x0000
                L_0x0047:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: androidx.lifecycle.ComputableLiveData.C02822.run():void");
            }
        };
        this.mInvalidationRunnable = new Runnable() {
            public void run() {
                boolean isActive = ComputableLiveData.this.mLiveData.hasActiveObservers();
                if (ComputableLiveData.this.mInvalid.compareAndSet(false, true) && isActive) {
                    ComputableLiveData.this.mExecutor.execute(ComputableLiveData.this.mRefreshRunnable);
                }
            }
        };
        this.mExecutor = executor;
        this.mLiveData = new LiveData<T>() {
            /* access modifiers changed from: protected */
            public void onActive() {
                ComputableLiveData.this.mExecutor.execute(ComputableLiveData.this.mRefreshRunnable);
            }
        };
    }

    public LiveData<T> getLiveData() {
        return this.mLiveData;
    }

    public void invalidate() {
        ArchTaskExecutor.getInstance().executeOnMainThread(this.mInvalidationRunnable);
    }
}
