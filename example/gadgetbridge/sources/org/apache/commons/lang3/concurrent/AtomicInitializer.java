package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AtomicInitializer<T> implements ConcurrentInitializer<T> {
    private final AtomicReference<T> reference = new AtomicReference<>();

    /* access modifiers changed from: protected */
    public abstract T initialize() throws ConcurrentException;

    public T get() throws ConcurrentException {
        T result = this.reference.get();
        if (result != null) {
            return result;
        }
        T result2 = initialize();
        if (!this.reference.compareAndSet((Object) null, result2)) {
            return this.reference.get();
        }
        return result2;
    }
}
