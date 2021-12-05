package p005ch.qos.logback.core;

import p005ch.qos.logback.core.spi.ContextAware;
import p005ch.qos.logback.core.spi.LifeCycle;

/* renamed from: ch.qos.logback.core.Layout */
public interface Layout<E> extends ContextAware, LifeCycle {
    String doLayout(E e);

    String getContentType();

    String getFileFooter();

    String getFileHeader();

    String getPresentationFooter();

    String getPresentationHeader();
}
