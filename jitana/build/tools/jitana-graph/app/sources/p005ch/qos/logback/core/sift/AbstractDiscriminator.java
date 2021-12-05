package p005ch.qos.logback.core.sift;

import p005ch.qos.logback.core.spi.ContextAwareBase;

/* renamed from: ch.qos.logback.core.sift.AbstractDiscriminator */
public abstract class AbstractDiscriminator<E> extends ContextAwareBase implements Discriminator<E> {
    protected boolean started;

    public boolean isStarted() {
        return this.started;
    }

    public void start() {
        this.started = true;
    }

    public void stop() {
        this.started = false;
    }
}
