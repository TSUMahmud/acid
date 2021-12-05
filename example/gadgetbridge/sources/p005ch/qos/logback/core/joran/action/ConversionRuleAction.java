package p005ch.qos.logback.core.joran.action;

import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.core.joran.action.ConversionRuleAction */
public class ConversionRuleAction extends Action {
    boolean inError = false;

    public void begin(InterpretationContext interpretationContext, String str, Attributes attributes) {
        this.inError = false;
        String value = attributes.getValue(ActionConst.CONVERSION_WORD_ATTRIBUTE);
        String value2 = attributes.getValue(ActionConst.CONVERTER_CLASS_ATTRIBUTE);
        if (OptionHelper.isEmpty(value)) {
            this.inError = true;
            addError("No 'conversionWord' attribute in <conversionRule>");
        } else if (OptionHelper.isEmpty(value2)) {
            this.inError = true;
            interpretationContext.addError("No 'converterClass' attribute in <conversionRule>");
        } else {
            try {
                Map map = (Map) this.context.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
                if (map == null) {
                    map = new HashMap();
                    this.context.putObject(CoreConstants.PATTERN_RULE_REGISTRY, map);
                }
                addInfo("registering conversion word " + value + " with class [" + value2 + "]");
                map.put(value, value2);
            } catch (Exception e) {
                this.inError = true;
                addError("Could not add conversion rule to PatternLayout.");
            }
        }
    }

    public void end(InterpretationContext interpretationContext, String str) {
    }

    public void finish(InterpretationContext interpretationContext) {
    }
}
