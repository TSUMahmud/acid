package p005ch.qos.logback.classic.net;

import java.io.Serializable;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.classic.spi.LoggingEvent;
import p005ch.qos.logback.classic.spi.LoggingEventVO;
import p005ch.qos.logback.core.spi.PreSerializationTransformer;

/* renamed from: ch.qos.logback.classic.net.LoggingEventPreSerializationTransformer */
public class LoggingEventPreSerializationTransformer implements PreSerializationTransformer<ILoggingEvent> {
    public Serializable transform(ILoggingEvent iLoggingEvent) {
        if (iLoggingEvent == null) {
            return null;
        }
        if (iLoggingEvent instanceof LoggingEvent) {
            return LoggingEventVO.build(iLoggingEvent);
        }
        if (iLoggingEvent instanceof LoggingEventVO) {
            return (LoggingEventVO) iLoggingEvent;
        }
        throw new IllegalArgumentException("Unsupported type " + iLoggingEvent.getClass().getName());
    }
}
