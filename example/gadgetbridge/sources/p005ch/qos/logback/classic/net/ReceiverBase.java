package p005ch.qos.logback.classic.net;

import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.spi.LifeCycle;

/* renamed from: ch.qos.logback.classic.net.ReceiverBase */
public abstract class ReceiverBase extends ContextAwareBase implements LifeCycle {
    private boolean started;

    /* access modifiers changed from: protected */
    public abstract Runnable getRunnableTask();

    public final boolean isStarted() {
        return this.started;
    }

    /* access modifiers changed from: protected */
    public abstract void onStop();

    /* access modifiers changed from: protected */
    public abstract boolean shouldStart();

    public final void start() {
        if (!isStarted()) {
            if (getContext() == null) {
                throw new IllegalStateException("context not set");
            } else if (shouldStart()) {
                getContext().getExecutorService().execute(getRunnableTask());
                this.started = true;
            }
        }
    }

    public final void stop() {
        if (isStarted()) {
            try {
                onStop();
            } catch (RuntimeException e) {
                addError("on stop: " + e, e);
            }
            this.started = false;
        }
    }
}
