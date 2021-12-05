package p005ch.qos.logback.classic.pattern;

import p005ch.qos.logback.classic.spi.ILoggingEvent;

/* renamed from: ch.qos.logback.classic.pattern.LoggerConverter */
public class LoggerConverter extends NamedConverter {
    /* access modifiers changed from: protected */
    public String getFullyQualifiedName(ILoggingEvent iLoggingEvent) {
        return iLoggingEvent.getLoggerName();
    }
}
