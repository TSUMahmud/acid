package p005ch.qos.logback.core.pattern.util;

/* renamed from: ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil */
public class AlmostAsIsEscapeUtil extends RestrictedEscapeUtil {
    public void escape(String str, StringBuffer stringBuffer, char c, int i) {
        super.escape("%)", stringBuffer, c, i);
    }
}
