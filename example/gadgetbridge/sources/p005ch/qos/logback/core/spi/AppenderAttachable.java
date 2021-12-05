package p005ch.qos.logback.core.spi;

import java.util.Iterator;
import p005ch.qos.logback.core.Appender;

/* renamed from: ch.qos.logback.core.spi.AppenderAttachable */
public interface AppenderAttachable<E> {
    void addAppender(Appender<E> appender);

    void detachAndStopAllAppenders();

    boolean detachAppender(Appender<E> appender);

    boolean detachAppender(String str);

    Appender<E> getAppender(String str);

    boolean isAttached(Appender<E> appender);

    Iterator<Appender<E>> iteratorForAppenders();
}
