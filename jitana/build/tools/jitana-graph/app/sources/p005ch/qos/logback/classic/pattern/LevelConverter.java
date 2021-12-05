package p005ch.qos.logback.classic.pattern;

import p005ch.qos.logback.classic.spi.ILoggingEvent;

/* renamed from: ch.qos.logback.classic.pattern.LevelConverter */
public class LevelConverter extends ClassicConverter {
    public String convert(ILoggingEvent iLoggingEvent) {
        return iLoggingEvent.getLevel().toString();
    }
}
