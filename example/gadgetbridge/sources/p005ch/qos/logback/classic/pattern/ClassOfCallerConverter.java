package p005ch.qos.logback.classic.pattern;

import p005ch.qos.logback.classic.spi.CallerData;
import p005ch.qos.logback.classic.spi.ILoggingEvent;

/* renamed from: ch.qos.logback.classic.pattern.ClassOfCallerConverter */
public class ClassOfCallerConverter extends NamedConverter {
    /* access modifiers changed from: protected */
    public String getFullyQualifiedName(ILoggingEvent iLoggingEvent) {
        StackTraceElement[] callerData = iLoggingEvent.getCallerData();
        return (callerData == null || callerData.length <= 0) ? CallerData.f49NA : callerData[0].getClassName();
    }
}
