package org.apache.commons.lang3.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

public class Memoizer<I, O> implements Computable<I, O> {
    private final ConcurrentMap<I, Future<O>> cache;
    /* access modifiers changed from: private */
    public final Computable<I, O> computable;
    private final boolean recalculate;

    public Memoizer(Computable<I, O> computable2) {
        this(computable2, false);
    }

    public Memoizer(Computable<I, O> computable2, boolean recalculate2) {
        this.cache = new ConcurrentHashMap();
        this.computable = computable2;
        this.recalculate = recalculate2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000a, code lost:
        r2 = new java.util.concurrent.FutureTask<>(new org.apache.commons.lang3.concurrent.Memoizer.C12841(r4));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public O compute(final I r5) throws java.lang.InterruptedException {
        /*
            r4 = this;
        L_0x0000:
            java.util.concurrent.ConcurrentMap<I, java.util.concurrent.Future<O>> r0 = r4.cache
            java.lang.Object r0 = r0.get(r5)
            java.util.concurrent.Future r0 = (java.util.concurrent.Future) r0
            if (r0 != 0) goto L_0x0023
            org.apache.commons.lang3.concurrent.Memoizer$1 r1 = new org.apache.commons.lang3.concurrent.Memoizer$1
            r1.<init>(r5)
            java.util.concurrent.FutureTask r2 = new java.util.concurrent.FutureTask
            r2.<init>(r1)
            java.util.concurrent.ConcurrentMap<I, java.util.concurrent.Future<O>> r3 = r4.cache
            java.lang.Object r3 = r3.putIfAbsent(r5, r2)
            r0 = r3
            java.util.concurrent.Future r0 = (java.util.concurrent.Future) r0
            if (r0 != 0) goto L_0x0023
            r0 = r2
            r2.run()
        L_0x0023:
            java.lang.Object r1 = r0.get()     // Catch:{ CancellationException -> 0x003b, ExecutionException -> 0x0028 }
            return r1
        L_0x0028:
            r1 = move-exception
            boolean r2 = r4.recalculate
            if (r2 == 0) goto L_0x0032
            java.util.concurrent.ConcurrentMap<I, java.util.concurrent.Future<O>> r2 = r4.cache
            r2.remove(r5, r0)
        L_0x0032:
            java.lang.Throwable r2 = r1.getCause()
            java.lang.RuntimeException r2 = r4.launderException(r2)
            throw r2
        L_0x003b:
            r1 = move-exception
            java.util.concurrent.ConcurrentMap<I, java.util.concurrent.Future<O>> r2 = r4.cache
            r2.remove(r5, r0)
            goto L_0x0000
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.concurrent.Memoizer.compute(java.lang.Object):java.lang.Object");
    }

    private RuntimeException launderException(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        }
        if (throwable instanceof Error) {
            throw ((Error) throwable);
        }
        throw new IllegalStateException("Unchecked exception", throwable);
    }
}
