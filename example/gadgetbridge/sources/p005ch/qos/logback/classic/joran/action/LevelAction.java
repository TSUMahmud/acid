package p005ch.qos.logback.classic.joran.action;

import org.xml.sax.Attributes;
import p005ch.qos.logback.classic.Level;
import p005ch.qos.logback.classic.Logger;
import p005ch.qos.logback.core.joran.action.Action;
import p005ch.qos.logback.core.joran.action.ActionConst;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;

@Deprecated
/* renamed from: ch.qos.logback.classic.joran.action.LevelAction */
public class LevelAction extends Action {
    boolean inError = false;

    public void begin(InterpretationContext interpretationContext, String str, Attributes attributes) {
        Object peekObject = interpretationContext.peekObject();
        if (!(peekObject instanceof Logger)) {
            this.inError = true;
            addError("For element <level>, could not find a logger at the top of execution stack.");
            return;
        }
        Logger logger = (Logger) peekObject;
        String name = logger.getName();
        String subst = interpretationContext.subst(attributes.getValue("value"));
        logger.setLevel((ActionConst.INHERITED.equalsIgnoreCase(subst) || ActionConst.NULL.equalsIgnoreCase(subst)) ? null : Level.toLevel(subst, Level.DEBUG));
        addInfo(name + " level set to " + logger.getLevel());
    }

    public void end(InterpretationContext interpretationContext, String str) {
    }

    public void finish(InterpretationContext interpretationContext) {
    }
}
