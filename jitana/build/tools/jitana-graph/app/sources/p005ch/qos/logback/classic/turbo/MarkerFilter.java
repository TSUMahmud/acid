package p005ch.qos.logback.classic.turbo;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import p005ch.qos.logback.classic.Level;
import p005ch.qos.logback.classic.Logger;
import p005ch.qos.logback.core.spi.FilterReply;

/* renamed from: ch.qos.logback.classic.turbo.MarkerFilter */
public class MarkerFilter extends MatchingFilter {
    Marker markerToMatch;

    public FilterReply decide(Marker marker, Logger logger, Level level, String str, Object[] objArr, Throwable th) {
        return !isStarted() ? FilterReply.NEUTRAL : marker == null ? this.onMismatch : marker.contains(this.markerToMatch) ? this.onMatch : this.onMismatch;
    }

    public void setMarker(String str) {
        if (str != null) {
            this.markerToMatch = MarkerFactory.getMarker(str);
        }
    }

    public void start() {
        if (this.markerToMatch != null) {
            super.start();
            return;
        }
        addError("The marker property must be set for [" + getName() + "]");
    }
}
