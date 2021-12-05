package p005ch.qos.logback.classic.joran.action;

import org.xml.sax.Attributes;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.classic.spi.LoggerContextListener;
import p005ch.qos.logback.core.joran.action.Action;
import p005ch.qos.logback.core.joran.spi.ActionException;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.spi.ContextAware;
import p005ch.qos.logback.core.spi.LifeCycle;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.classic.joran.action.LoggerContextListenerAction */
public class LoggerContextListenerAction extends Action {
    boolean inError = false;
    LoggerContextListener lcl;

    public void begin(InterpretationContext interpretationContext, String str, Attributes attributes) throws ActionException {
        this.inError = false;
        String value = attributes.getValue(Action.CLASS_ATTRIBUTE);
        if (OptionHelper.isEmpty(value)) {
            addError("Mandatory \"class\" attribute not set for <loggerContextListener> element");
            this.inError = true;
            return;
        }
        try {
            this.lcl = (LoggerContextListener) OptionHelper.instantiateByClassName(value, (Class<?>) LoggerContextListener.class, this.context);
            if (this.lcl instanceof ContextAware) {
                ((ContextAware) this.lcl).setContext(this.context);
            }
            interpretationContext.pushObject(this.lcl);
            addInfo("Adding LoggerContextListener of type [" + value + "] to the object stack");
        } catch (Exception e) {
            this.inError = true;
            addError("Could not create LoggerContextListener of type " + value + "].", e);
        }
    }

    public void end(InterpretationContext interpretationContext, String str) throws ActionException {
        if (!this.inError) {
            Object peekObject = interpretationContext.peekObject();
            LoggerContextListener loggerContextListener = this.lcl;
            if (peekObject != loggerContextListener) {
                addWarn("The object on the top the of the stack is not the LoggerContextListener pushed earlier.");
                return;
            }
            if (loggerContextListener instanceof LifeCycle) {
                ((LifeCycle) loggerContextListener).start();
                addInfo("Starting LoggerContextListener");
            }
            ((LoggerContext) this.context).addListener(this.lcl);
            interpretationContext.popObject();
        }
    }
}
