package p005ch.qos.logback.classic.pattern;

import java.util.Map;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.classic.pattern.MDCConverter */
public class MDCConverter extends ClassicConverter {
    private String defaultValue = "";
    private String key;

    private String outputMDCForAllKeys(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (Map.Entry next : map.entrySet()) {
            if (z) {
                z = false;
            } else {
                sb.append(", ");
            }
            sb.append((String) next.getKey());
            sb.append('=');
            sb.append((String) next.getValue());
        }
        return sb.toString();
    }

    public String convert(ILoggingEvent iLoggingEvent) {
        Map<String, String> mDCPropertyMap = iLoggingEvent.getMDCPropertyMap();
        if (mDCPropertyMap == null) {
            return this.defaultValue;
        }
        if (this.key == null) {
            return outputMDCForAllKeys(mDCPropertyMap);
        }
        String str = iLoggingEvent.getMDCPropertyMap().get(this.key);
        return str != null ? str : this.defaultValue;
    }

    public void start() {
        String[] extractDefaultReplacement = OptionHelper.extractDefaultReplacement(getFirstOption());
        this.key = extractDefaultReplacement[0];
        if (extractDefaultReplacement[1] != null) {
            this.defaultValue = extractDefaultReplacement[1];
        }
        super.start();
    }

    public void stop() {
        this.key = null;
        super.stop();
    }
}
