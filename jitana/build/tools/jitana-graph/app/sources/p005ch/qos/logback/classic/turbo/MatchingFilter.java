package p005ch.qos.logback.classic.turbo;

import p005ch.qos.logback.core.spi.FilterReply;

/* renamed from: ch.qos.logback.classic.turbo.MatchingFilter */
public abstract class MatchingFilter extends TurboFilter {
    protected FilterReply onMatch = FilterReply.NEUTRAL;
    protected FilterReply onMismatch = FilterReply.NEUTRAL;

    public final void setOnMatch(String str) {
        FilterReply filterReply;
        if ("NEUTRAL".equals(str)) {
            filterReply = FilterReply.NEUTRAL;
        } else if ("ACCEPT".equals(str)) {
            filterReply = FilterReply.ACCEPT;
        } else if ("DENY".equals(str)) {
            filterReply = FilterReply.DENY;
        } else {
            return;
        }
        this.onMatch = filterReply;
    }

    public final void setOnMismatch(String str) {
        FilterReply filterReply;
        if ("NEUTRAL".equals(str)) {
            filterReply = FilterReply.NEUTRAL;
        } else if ("ACCEPT".equals(str)) {
            filterReply = FilterReply.ACCEPT;
        } else if ("DENY".equals(str)) {
            filterReply = FilterReply.DENY;
        } else {
            return;
        }
        this.onMismatch = filterReply;
    }
}
