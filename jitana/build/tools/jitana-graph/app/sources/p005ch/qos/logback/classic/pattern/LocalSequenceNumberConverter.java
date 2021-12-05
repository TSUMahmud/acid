package p005ch.qos.logback.classic.pattern;

import java.util.concurrent.atomic.AtomicLong;
import p005ch.qos.logback.classic.spi.ILoggingEvent;

/* renamed from: ch.qos.logback.classic.pattern.LocalSequenceNumberConverter */
public class LocalSequenceNumberConverter extends ClassicConverter {
    AtomicLong sequenceNumber = new AtomicLong(System.currentTimeMillis());

    public String convert(ILoggingEvent iLoggingEvent) {
        return Long.toString(this.sequenceNumber.getAndIncrement());
    }
}
