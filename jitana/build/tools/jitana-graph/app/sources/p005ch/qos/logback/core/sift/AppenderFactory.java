package p005ch.qos.logback.core.sift;

import p005ch.qos.logback.core.Appender;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.joran.spi.JoranException;

/* renamed from: ch.qos.logback.core.sift.AppenderFactory */
public interface AppenderFactory<E> {
    Appender<E> buildAppender(Context context, String str) throws JoranException;
}
