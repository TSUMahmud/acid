package p005ch.qos.logback.core.pattern.util;

/* renamed from: ch.qos.logback.core.pattern.util.AsIsEscapeUtil */
public class AsIsEscapeUtil implements IEscapeUtil {
    public void escape(String str, StringBuffer stringBuffer, char c, int i) {
        stringBuffer.append("\\");
        stringBuffer.append(c);
    }
}
