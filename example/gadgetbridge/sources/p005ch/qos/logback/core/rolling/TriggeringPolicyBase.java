package p005ch.qos.logback.core.rolling;

import p005ch.qos.logback.core.spi.ContextAwareBase;

/* renamed from: ch.qos.logback.core.rolling.TriggeringPolicyBase */
public abstract class TriggeringPolicyBase<E> extends ContextAwareBase implements TriggeringPolicy<E> {
    private boolean start;

    public boolean isStarted() {
        return this.start;
    }

    public void start() {
        this.start = true;
    }

    public void stop() {
        this.start = false;
    }
}
