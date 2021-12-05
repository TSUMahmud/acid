package p005ch.qos.logback.classic.joran.action;

import org.xml.sax.Attributes;
import p005ch.qos.logback.classic.Level;
import p005ch.qos.logback.classic.Logger;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.core.joran.action.Action;
import p005ch.qos.logback.core.joran.action.ActionConst;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.classic.joran.action.LoggerAction */
public class LoggerAction extends Action {
    public static final String LEVEL_ATTRIBUTE = "level";
    boolean inError = false;
    Logger logger;

    public void begin(InterpretationContext interpretationContext, String str, Attributes attributes) {
        this.inError = false;
        this.logger = null;
        LoggerContext loggerContext = (LoggerContext) this.context;
        String subst = interpretationContext.subst(attributes.getValue("name"));
        if (OptionHelper.isEmpty(subst)) {
            this.inError = true;
            String lineColStr = getLineColStr(interpretationContext);
            addError("No 'name' attribute in element " + str + ", around " + lineColStr);
            return;
        }
        this.logger = loggerContext.getLogger(subst);
        String subst2 = interpretationContext.subst(attributes.getValue("level"));
        if (!OptionHelper.isEmpty(subst2)) {
            if (ActionConst.INHERITED.equalsIgnoreCase(subst2) || ActionConst.NULL.equalsIgnoreCase(subst2)) {
                addInfo("Setting level of logger [" + subst + "] to null, i.e. INHERITED");
                this.logger.setLevel((Level) null);
            } else {
                Level level = Level.toLevel(subst2);
                addInfo("Setting level of logger [" + subst + "] to " + level);
                this.logger.setLevel(level);
            }
        }
        String subst3 = interpretationContext.subst(attributes.getValue(ActionConst.ADDITIVITY_ATTRIBUTE));
        if (!OptionHelper.isEmpty(subst3)) {
            boolean booleanValue = Boolean.valueOf(subst3).booleanValue();
            addInfo("Setting additivity of logger [" + subst + "] to " + booleanValue);
            this.logger.setAdditive(booleanValue);
        }
        interpretationContext.pushObject(this.logger);
    }

    public void end(InterpretationContext interpretationContext, String str) {
        if (!this.inError) {
            Object peekObject = interpretationContext.peekObject();
            if (peekObject != this.logger) {
                addWarn("The object on the top the of the stack is not " + this.logger + " pushed earlier");
                StringBuilder sb = new StringBuilder();
                sb.append("It is: ");
                sb.append(peekObject);
                addWarn(sb.toString());
                return;
            }
            interpretationContext.popObject();
        }
    }

    public void finish(InterpretationContext interpretationContext) {
    }
}
