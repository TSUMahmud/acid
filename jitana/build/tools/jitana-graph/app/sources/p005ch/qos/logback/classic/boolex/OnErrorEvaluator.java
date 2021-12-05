package p005ch.qos.logback.classic.boolex;

import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.boolex.EvaluationException;
import p005ch.qos.logback.core.boolex.EventEvaluatorBase;

/* renamed from: ch.qos.logback.classic.boolex.OnErrorEvaluator */
public class OnErrorEvaluator extends EventEvaluatorBase<ILoggingEvent> {
    public boolean evaluate(ILoggingEvent iLoggingEvent) throws NullPointerException, EvaluationException {
        return iLoggingEvent.getLevel().levelInt >= 40000;
    }
}
