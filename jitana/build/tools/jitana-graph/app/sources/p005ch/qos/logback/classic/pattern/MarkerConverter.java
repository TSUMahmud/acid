package p005ch.qos.logback.classic.pattern;

import org.slf4j.Marker;
import p005ch.qos.logback.classic.spi.ILoggingEvent;

/* renamed from: ch.qos.logback.classic.pattern.MarkerConverter */
public class MarkerConverter extends ClassicConverter {
    private static String EMPTY = "";

    public String convert(ILoggingEvent iLoggingEvent) {
        Marker marker = iLoggingEvent.getMarker();
        return marker == null ? EMPTY : marker.toString();
    }
}
