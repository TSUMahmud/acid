package p005ch.qos.logback.core.filter;

import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.spi.FilterReply;
import p005ch.qos.logback.core.spi.LifeCycle;

/* renamed from: ch.qos.logback.core.filter.Filter */
public abstract class Filter<E> extends ContextAwareBase implements LifeCycle {
    private String name;
    boolean start = false;

    public abstract FilterReply decide(E e);

    public String getName() {
        return this.name;
    }

    public boolean isStarted() {
        return this.start;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void start() {
        this.start = true;
    }

    public void stop() {
        this.start = false;
    }
}
