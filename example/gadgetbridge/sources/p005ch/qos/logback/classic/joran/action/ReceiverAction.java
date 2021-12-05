package p005ch.qos.logback.classic.joran.action;

import org.xml.sax.Attributes;
import p005ch.qos.logback.classic.net.ReceiverBase;
import p005ch.qos.logback.core.joran.action.Action;
import p005ch.qos.logback.core.joran.spi.ActionException;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.classic.joran.action.ReceiverAction */
public class ReceiverAction extends Action {
    private boolean inError;
    private ReceiverBase receiver;

    public void begin(InterpretationContext interpretationContext, String str, Attributes attributes) throws ActionException {
        String value = attributes.getValue(Action.CLASS_ATTRIBUTE);
        if (OptionHelper.isEmpty(value)) {
            addError("Missing class name for receiver. Near [" + str + "] line " + getLineNumber(interpretationContext));
            this.inError = true;
            return;
        }
        try {
            addInfo("About to instantiate receiver of type [" + value + "]");
            this.receiver = (ReceiverBase) OptionHelper.instantiateByClassName(value, (Class<?>) ReceiverBase.class, this.context);
            this.receiver.setContext(this.context);
            interpretationContext.pushObject(this.receiver);
        } catch (Exception e) {
            this.inError = true;
            addError("Could not create a receiver of type [" + value + "].", e);
            throw new ActionException(e);
        }
    }

    public void end(InterpretationContext interpretationContext, String str) throws ActionException {
        if (!this.inError) {
            interpretationContext.getContext().register(this.receiver);
            this.receiver.start();
            if (interpretationContext.peekObject() != this.receiver) {
                addWarn("The object at the of the stack is not the remote pushed earlier.");
            } else {
                interpretationContext.popObject();
            }
        }
    }
}
