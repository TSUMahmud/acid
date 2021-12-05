package p005ch.qos.logback.classic.pattern;

import p005ch.qos.logback.classic.spi.ILoggingEvent;

/* renamed from: ch.qos.logback.classic.pattern.RelativeTimeConverter */
public class RelativeTimeConverter extends ClassicConverter {
    long lastTimestamp = -1;
    String timesmapCache = null;

    public String convert(ILoggingEvent iLoggingEvent) {
        String str;
        long timeStamp = iLoggingEvent.getTimeStamp();
        synchronized (this) {
            if (timeStamp != this.lastTimestamp) {
                this.lastTimestamp = timeStamp;
                this.timesmapCache = Long.toString(timeStamp - iLoggingEvent.getLoggerContextVO().getBirthTime());
            }
            str = this.timesmapCache;
        }
        return str;
    }
}
