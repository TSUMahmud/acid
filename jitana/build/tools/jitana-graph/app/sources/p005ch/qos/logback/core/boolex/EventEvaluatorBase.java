package p005ch.qos.logback.core.boolex;

import p005ch.qos.logback.core.spi.ContextAwareBase;

/* renamed from: ch.qos.logback.core.boolex.EventEvaluatorBase */
public abstract class EventEvaluatorBase<E> extends ContextAwareBase implements EventEvaluator<E> {
    String name;
    boolean started;

    public String getName() {
        return this.name;
    }

    public boolean isStarted() {
        return this.started;
    }

    public void setName(String str) {
        if (this.name == null) {
            this.name = str;
            return;
        }
        throw new IllegalStateException("name has been already set");
    }

    public void start() {
        this.started = true;
    }

    public void stop() {
        this.started = false;
    }
}
