package p005ch.qos.logback.core;

import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.spi.PropertyDefiner;

/* renamed from: ch.qos.logback.core.PropertyDefinerBase */
public abstract class PropertyDefinerBase extends ContextAwareBase implements PropertyDefiner {
    protected static String booleanAsStr(boolean z) {
        return (z ? Boolean.TRUE : Boolean.FALSE).toString();
    }
}
