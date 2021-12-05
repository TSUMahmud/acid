package p005ch.qos.logback.core.joran.spi;

import org.xml.sax.Locator;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.spi.ContextAwareImpl;

/* renamed from: ch.qos.logback.core.joran.spi.CAI_WithLocatorSupport */
class CAI_WithLocatorSupport extends ContextAwareImpl {
    CAI_WithLocatorSupport(Context context, Interpreter interpreter) {
        super(context, interpreter);
    }

    /* access modifiers changed from: protected */
    public Object getOrigin() {
        Locator locator = ((Interpreter) super.getOrigin()).locator;
        if (locator != null) {
            return Interpreter.class.getName() + "@" + locator.getLineNumber() + ":" + locator.getColumnNumber();
        }
        return Interpreter.class.getName() + "@NA:NA";
    }
}
