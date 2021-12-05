package p005ch.qos.logback.classic.boolex;

import p005ch.qos.logback.classic.spi.ILoggingEvent;

/* renamed from: ch.qos.logback.classic.boolex.IEvaluator */
public interface IEvaluator {
    boolean doEvaluate(ILoggingEvent iLoggingEvent);
}
