package p005ch.qos.logback.core.joran.action;

import java.util.Map;
import org.xml.sax.Attributes;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.boolex.EventEvaluator;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.spi.LifeCycle;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.core.joran.action.AbstractEventEvaluatorAction */
public abstract class AbstractEventEvaluatorAction extends Action {
    EventEvaluator<?> evaluator;
    boolean inError = false;

    public void begin(InterpretationContext interpretationContext, String str, Attributes attributes) {
        String str2;
        this.inError = false;
        this.evaluator = null;
        String value = attributes.getValue(Action.CLASS_ATTRIBUTE);
        if (OptionHelper.isEmpty(value)) {
            value = defaultClassName();
            addInfo("Assuming default evaluator class [" + value + "]");
        }
        if (OptionHelper.isEmpty(value)) {
            defaultClassName();
            this.inError = true;
            str2 = "Mandatory \"class\" attribute not set for <evaluator>";
        } else {
            String value2 = attributes.getValue("name");
            if (OptionHelper.isEmpty(value2)) {
                this.inError = true;
                str2 = "Mandatory \"name\" attribute not set for <evaluator>";
            } else {
                try {
                    this.evaluator = (EventEvaluator) OptionHelper.instantiateByClassName(value, (Class<?>) EventEvaluator.class, this.context);
                    this.evaluator.setContext(this.context);
                    this.evaluator.setName(value2);
                    interpretationContext.pushObject(this.evaluator);
                    addInfo("Adding evaluator named [" + value2 + "] to the object stack");
                    return;
                } catch (Exception e) {
                    this.inError = true;
                    addError("Could not create evaluator of type " + value + "].", e);
                    return;
                }
            }
        }
        addError(str2);
    }

    /* access modifiers changed from: protected */
    public abstract String defaultClassName();

    public void end(InterpretationContext interpretationContext, String str) {
        if (!this.inError) {
            EventEvaluator<?> eventEvaluator = this.evaluator;
            if (eventEvaluator instanceof LifeCycle) {
                eventEvaluator.start();
                addInfo("Starting evaluator named [" + this.evaluator.getName() + "]");
            }
            if (interpretationContext.peekObject() != this.evaluator) {
                addWarn("The object on the top the of the stack is not the evaluator pushed earlier.");
                return;
            }
            interpretationContext.popObject();
            try {
                Map map = (Map) this.context.getObject(CoreConstants.EVALUATOR_MAP);
                if (map == null) {
                    addError("Could not find EvaluatorMap");
                } else {
                    map.put(this.evaluator.getName(), this.evaluator);
                }
            } catch (Exception e) {
                addError("Could not set evaluator named [" + this.evaluator + "].", e);
            }
        }
    }

    public void finish(InterpretationContext interpretationContext) {
    }
}
