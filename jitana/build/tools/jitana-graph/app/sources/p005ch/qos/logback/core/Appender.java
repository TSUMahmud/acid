package p005ch.qos.logback.core;

import p005ch.qos.logback.core.spi.ContextAware;
import p005ch.qos.logback.core.spi.FilterAttachable;
import p005ch.qos.logback.core.spi.LifeCycle;

/* renamed from: ch.qos.logback.core.Appender */
public interface Appender<E> extends LifeCycle, ContextAware, FilterAttachable<E> {
    void doAppend(E e) throws LogbackException;

    String getName();

    void setName(String str);
}
