package p005ch.qos.logback.classic;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/* renamed from: ch.qos.logback.classic.ClassicConstants */
public class ClassicConstants {
    public static final int DEFAULT_MAX_CALLEDER_DATA_DEPTH = 8;
    public static final String FINALIZE_SESSION = "FINALIZE_SESSION";
    public static final Marker FINALIZE_SESSION_MARKER = MarkerFactory.getMarker(FINALIZE_SESSION);
    public static final String LOGBACK_CONTEXT_SELECTOR = "logback.ContextSelector";
    public static final int MAX_DOTS = 16;
}
