package p005ch.qos.logback.core.pattern.util;

/* renamed from: ch.qos.logback.core.pattern.util.RestrictedEscapeUtil */
public class RestrictedEscapeUtil implements IEscapeUtil {
    public void escape(String str, StringBuffer stringBuffer, char c, int i) {
        if (str.indexOf(c) < 0) {
            stringBuffer.append("\\");
        }
        stringBuffer.append(c);
    }
}
