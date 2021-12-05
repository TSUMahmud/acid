package p005ch.qos.logback.core.joran.action;

import org.xml.sax.Attributes;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.joran.util.PropertySetter;

/* renamed from: ch.qos.logback.core.joran.action.ParamAction */
public class ParamAction extends Action {
    static String NO_NAME = "No name attribute in <param> element";
    static String NO_VALUE = "No value attribute in <param> element";
    boolean inError = false;

    public void begin(InterpretationContext interpretationContext, String str, Attributes attributes) {
        String value = attributes.getValue("name");
        String value2 = attributes.getValue("value");
        if (value == null) {
            this.inError = true;
            addError(NO_NAME);
        } else if (value2 == null) {
            this.inError = true;
            addError(NO_VALUE);
        } else {
            String trim = value2.trim();
            PropertySetter propertySetter = new PropertySetter(interpretationContext.peekObject());
            propertySetter.setContext(this.context);
            propertySetter.setProperty(interpretationContext.subst(value), interpretationContext.subst(trim));
        }
    }

    public void end(InterpretationContext interpretationContext, String str) {
    }

    public void finish(InterpretationContext interpretationContext) {
    }
}
