package p005ch.qos.logback.classic.sift;

import java.util.Map;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.sift.AbstractDiscriminator;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.classic.sift.MDCBasedDiscriminator */
public class MDCBasedDiscriminator extends AbstractDiscriminator<ILoggingEvent> {
    private String defaultValue;
    private String key;

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
        Map<String, String> mDCPropertyMap = iLoggingEvent.getMDCPropertyMap();
        if (mDCPropertyMap == null) {
            return this.defaultValue;
        }
        String str = mDCPropertyMap.get(this.key);
        return str == null ? this.defaultValue : str;
    }

    public String getKey() {
        return this.key;
    }

    public void setDefaultValue(String str) {
        this.defaultValue = str;
    }

    public void setKey(String str) {
        this.key = str;
    }

    public void start() {
        int i;
        if (OptionHelper.isEmpty(this.key)) {
            addError("The \"Key\" property must be set");
            i = 1;
        } else {
            i = 0;
        }
        if (OptionHelper.isEmpty(this.defaultValue)) {
            i++;
            addError("The \"DefaultValue\" property must be set");
        }
        if (i == 0) {
            this.started = true;
        }
    }
}
