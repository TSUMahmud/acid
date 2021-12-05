package org.apache.commons.lang3.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import org.apache.commons.lang3.Validate;

public class CallableBackgroundInitializer<T> extends BackgroundInitializer<T> {
    private final Callable<T> callable;

    public CallableBackgroundInitializer(Callable<T> call) {
        checkCallable(call);
        this.callable = call;
    }

    public CallableBackgroundInitializer(Callable<T> call, ExecutorService exec) {
        super(exec);
        checkCallable(call);
        this.callable = call;
    }

    /* access modifiers changed from: protected */
    public T initialize() throws Exception {
        return this.callable.call();
    }

    private void checkCallable(Callable<T> call) {
        Validate.isTrue(call != null, "Callable must not be null!", new Object[0]);
    }
}
