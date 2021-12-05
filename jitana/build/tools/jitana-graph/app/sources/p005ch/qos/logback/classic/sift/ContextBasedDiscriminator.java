package p005ch.qos.logback.classic.sift;

import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.sift.AbstractDiscriminator;

/* renamed from: ch.qos.logback.classic.sift.ContextBasedDiscriminator */
public class ContextBasedDiscriminator extends AbstractDiscriminator<ILoggingEvent> {
    private static final String KEY = "contextName";
    private String defaultValue;

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
        String name = iLoggingEvent.getLoggerContextVO().getName();
        return name == null ? this.defaultValue : name;
    }

    public String getKey() {
        return KEY;
    }

    public void setDefaultValue(String str) {
        this.defaultValue = str;
    }

    public void setKey(String str) {
        throw new UnsupportedOperationException("Key cannot be set. Using fixed key contextName");
    }
}
