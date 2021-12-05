package p005ch.qos.logback.classic.turbo;

import org.slf4j.MDC;
import org.slf4j.Marker;
import p005ch.qos.logback.classic.Level;
import p005ch.qos.logback.classic.Logger;
import p005ch.qos.logback.core.spi.FilterReply;

/* renamed from: ch.qos.logback.classic.turbo.MDCFilter */
public class MDCFilter extends MatchingFilter {
    String MDCKey;
    String value;

    public FilterReply decide(Marker marker, Logger logger, Level level, String str, Object[] objArr, Throwable th) {
        String str2 = this.MDCKey;
        if (str2 == null) {
            return FilterReply.NEUTRAL;
        }
        return this.value.equals(MDC.get(str2)) ? this.onMatch : this.onMismatch;
    }

    public void setMDCKey(String str) {
        this.MDCKey = str;
    }

    public void setValue(String str) {
        this.value = str;
    }
}
