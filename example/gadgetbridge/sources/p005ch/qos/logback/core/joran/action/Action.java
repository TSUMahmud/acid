package p005ch.qos.logback.core.joran.action;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import p005ch.qos.logback.core.joran.spi.ActionException;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.spi.ContextAwareBase;

/* renamed from: ch.qos.logback.core.joran.action.Action */
public abstract class Action extends ContextAwareBase {
    public static final String ACTION_CLASS_ATTRIBUTE = "actionClass";
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String FILE_ATTRIBUTE = "file";
    public static final String KEY_ATTRIBUTE = "key";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String PATTERN_ATTRIBUTE = "pattern";
    public static final String SCOPE_ATTRIBUTE = "scope";
    public static final String VALUE_ATTRIBUTE = "value";

    public abstract void begin(InterpretationContext interpretationContext, String str, Attributes attributes) throws ActionException;

    public void body(InterpretationContext interpretationContext, String str) throws ActionException {
    }

    public abstract void end(InterpretationContext interpretationContext, String str) throws ActionException;

    /* access modifiers changed from: protected */
    public int getColumnNumber(InterpretationContext interpretationContext) {
        Locator locator = interpretationContext.getJoranInterpreter().getLocator();
        if (locator != null) {
            return locator.getColumnNumber();
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public String getLineColStr(InterpretationContext interpretationContext) {
        return "line: " + getLineNumber(interpretationContext) + ", column: " + getColumnNumber(interpretationContext);
    }

    /* access modifiers changed from: protected */
    public int getLineNumber(InterpretationContext interpretationContext) {
        Locator locator = interpretationContext.getJoranInterpreter().getLocator();
        if (locator != null) {
            return locator.getLineNumber();
        }
        return -1;
    }

    public String toString() {
        return getClass().getName();
    }
}
