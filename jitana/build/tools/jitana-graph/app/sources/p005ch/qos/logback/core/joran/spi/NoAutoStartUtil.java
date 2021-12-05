package p005ch.qos.logback.core.joran.spi;

/* renamed from: ch.qos.logback.core.joran.spi.NoAutoStartUtil */
public class NoAutoStartUtil {
    public static boolean notMarkedWithNoAutoStart(Object obj) {
        return obj != null && ((NoAutoStart) obj.getClass().getAnnotation(NoAutoStart.class)) == null;
    }
}
