package p005ch.qos.logback.classic.filter;

import p005ch.qos.logback.classic.Level;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.filter.AbstractMatcherFilter;
import p005ch.qos.logback.core.spi.FilterReply;

/* renamed from: ch.qos.logback.classic.filter.LevelFilter */
public class LevelFilter extends AbstractMatcherFilter<ILoggingEvent> {
    Level level;

    public FilterReply decide(ILoggingEvent iLoggingEvent) {
        return !isStarted() ? FilterReply.NEUTRAL : iLoggingEvent.getLevel().equals(this.level) ? this.onMatch : this.onMismatch;
    }

    public void setLevel(Level level2) {
        this.level = level2;
    }

    public void start() {
        if (this.level != null) {
            super.start();
        }
    }
}
