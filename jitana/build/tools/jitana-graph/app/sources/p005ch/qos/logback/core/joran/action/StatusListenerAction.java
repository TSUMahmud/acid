package p005ch.qos.logback.core.joran.action;

import org.xml.sax.Attributes;
import p005ch.qos.logback.core.joran.spi.ActionException;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.spi.ContextAware;
import p005ch.qos.logback.core.spi.LifeCycle;
import p005ch.qos.logback.core.status.OnConsoleStatusListener;
import p005ch.qos.logback.core.status.StatusListener;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.core.joran.action.StatusListenerAction */
public class StatusListenerAction extends Action {
    boolean inError = false;
    StatusListener statusListener = null;

    public void begin(InterpretationContext interpretationContext, String str, Attributes attributes) throws ActionException {
        this.inError = false;
        String value = attributes.getValue(Action.CLASS_ATTRIBUTE);
        if (OptionHelper.isEmpty(value)) {
            addError("Missing class name for statusListener. Near [" + str + "] line " + getLineNumber(interpretationContext));
            this.inError = true;
            return;
        }
        try {
            if (OnConsoleStatusListener.class.getName().equals(value)) {
                OnConsoleStatusListener.addNewInstanceToContext(this.context);
            } else {
                this.statusListener = (StatusListener) OptionHelper.instantiateByClassName(value, (Class<?>) StatusListener.class, this.context);
                interpretationContext.getContext().getStatusManager().add(this.statusListener);
                if (this.statusListener instanceof ContextAware) {
                    ((ContextAware) this.statusListener).setContext(this.context);
                }
            }
            addInfo("Added status listener of type [" + value + "]");
            interpretationContext.pushObject(this.statusListener);
        } catch (Exception e) {
            this.inError = true;
            addError("Could not create an StatusListener of type [" + value + "].", e);
            throw new ActionException(e);
        }
    }

    public void end(InterpretationContext interpretationContext, String str) {
        if (!this.inError) {
            StatusListener statusListener2 = this.statusListener;
            if (statusListener2 instanceof LifeCycle) {
                ((LifeCycle) statusListener2).start();
            }
            if (interpretationContext.peekObject() != this.statusListener) {
                addWarn("The object at the of the stack is not the statusListener pushed earlier.");
            } else {
                interpretationContext.popObject();
            }
        }
    }

    public void finish(InterpretationContext interpretationContext) {
    }
}
