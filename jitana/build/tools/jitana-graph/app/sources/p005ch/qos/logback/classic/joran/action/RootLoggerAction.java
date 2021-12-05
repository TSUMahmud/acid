package p005ch.qos.logback.classic.joran.action;

import org.xml.sax.Attributes;
import p005ch.qos.logback.classic.Level;
import p005ch.qos.logback.classic.Logger;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.core.joran.action.Action;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.classic.joran.action.RootLoggerAction */
public class RootLoggerAction extends Action {
    boolean inError = false;
    Logger root;

    public void begin(InterpretationContext interpretationContext, String str, Attributes attributes) {
        this.inError = false;
        this.root = ((LoggerContext) this.context).getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        String subst = interpretationContext.subst(attributes.getValue("level"));
        if (!OptionHelper.isEmpty(subst)) {
            Level level = Level.toLevel(subst);
            addInfo("Setting level of ROOT logger to " + level);
            this.root.setLevel(level);
        }
        interpretationContext.pushObject(this.root);
    }

    public void end(InterpretationContext interpretationContext, String str) {
        if (!this.inError) {
            Object peekObject = interpretationContext.peekObject();
            if (peekObject != this.root) {
                addWarn("The object on the top the of the stack is not the root logger");
                addWarn("It is: " + peekObject);
                return;
            }
            interpretationContext.popObject();
        }
    }

    public void finish(InterpretationContext interpretationContext) {
    }
}
