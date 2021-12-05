package p005ch.qos.logback.core.util;

import java.util.Properties;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.spi.ContextAwareBase;

/* renamed from: ch.qos.logback.core.util.ContextUtil */
public class ContextUtil extends ContextAwareBase {
    public ContextUtil(Context context) {
        setContext(context);
    }

    public void addHostNameAsProperty() {
        this.context.putProperty(CoreConstants.HOSTNAME_KEY, "localhost");
    }

    public void addProperties(Properties properties) {
        if (properties != null) {
            for (String str : properties.keySet()) {
                this.context.putProperty(str, properties.getProperty(str));
            }
        }
    }
}
