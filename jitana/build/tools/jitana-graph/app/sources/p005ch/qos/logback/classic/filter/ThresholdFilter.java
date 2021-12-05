package p005ch.qos.logback.classic.filter;

import p005ch.qos.logback.classic.Level;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.filter.Filter;
import p005ch.qos.logback.core.spi.FilterReply;

/* renamed from: ch.qos.logback.classic.filter.ThresholdFilter */
public class ThresholdFilter extends Filter<ILoggingEvent> {
    Level level;

    public FilterReply decide(ILoggingEvent iLoggingEvent) {
        return !isStarted() ? FilterReply.NEUTRAL : iLoggingEvent.getLevel().isGreaterOrEqual(this.level) ? FilterReply.NEUTRAL : FilterReply.DENY;
    }

    public void setLevel(String str) {
        this.level = Level.toLevel(str);
    }

    public void start() {
        if (this.level != null) {
            super.start();
        }
    }
}
