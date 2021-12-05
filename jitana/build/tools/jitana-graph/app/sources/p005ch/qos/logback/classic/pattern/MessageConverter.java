package p005ch.qos.logback.classic.pattern;

import p005ch.qos.logback.classic.spi.ILoggingEvent;

/* renamed from: ch.qos.logback.classic.pattern.MessageConverter */
public class MessageConverter extends ClassicConverter {
    public String convert(ILoggingEvent iLoggingEvent) {
        return iLoggingEvent.getFormattedMessage();
    }
}
