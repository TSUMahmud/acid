package p005ch.qos.logback.classic.pattern;

import p005ch.qos.logback.classic.spi.ILoggingEvent;

/* renamed from: ch.qos.logback.classic.pattern.NopThrowableInformationConverter */
public class NopThrowableInformationConverter extends ThrowableHandlingConverter {
    public String convert(ILoggingEvent iLoggingEvent) {
        return "";
    }
}
