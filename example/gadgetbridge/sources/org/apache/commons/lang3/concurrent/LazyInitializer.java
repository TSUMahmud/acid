package org.apache.commons.lang3.concurrent;

public abstract class LazyInitializer<T> implements ConcurrentInitializer<T> {
    private static final Object NO_INIT = new Object();
    private volatile T object = NO_INIT;

    /* access modifiers changed from: protected */
    public abstract T initialize() throws ConcurrentException;

    public T get() throws ConcurrentException {
        T result = this.object;
        if (result == NO_INIT) {
            synchronized (this) {
                result = this.object;
                if (result == NO_INIT) {
                    T initialize = initialize();
                    result = initialize;
                    this.object = initialize;
                }
            }
        }
        return result;
    }
}
