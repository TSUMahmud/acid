package p005ch.qos.logback.core.sift;

import java.util.List;
import java.util.Map;
import p005ch.qos.logback.core.Appender;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.joran.GenericConfigurator;
import p005ch.qos.logback.core.joran.action.Action;
import p005ch.qos.logback.core.joran.action.DefinePropertyAction;
import p005ch.qos.logback.core.joran.action.NestedBasicPropertyIA;
import p005ch.qos.logback.core.joran.action.NestedComplexPropertyIA;
import p005ch.qos.logback.core.joran.action.PropertyAction;
import p005ch.qos.logback.core.joran.action.TimestampAction;
import p005ch.qos.logback.core.joran.event.SaxEvent;
import p005ch.qos.logback.core.joran.spi.ElementSelector;
import p005ch.qos.logback.core.joran.spi.Interpreter;
import p005ch.qos.logback.core.joran.spi.JoranException;
import p005ch.qos.logback.core.joran.spi.RuleStore;

/* renamed from: ch.qos.logback.core.sift.SiftingJoranConfiguratorBase */
public abstract class SiftingJoranConfiguratorBase<E> extends GenericConfigurator {
    static final String ONE_AND_ONLY_ONE_URL = "http://logback.qos.ch/codes.html#1andOnly1";
    int errorEmmissionCount = 0;
    protected final String key;
    protected final Map<String, String> parentPropertyMap;
    protected final String value;

    protected SiftingJoranConfiguratorBase(String str, String str2, Map<String, String> map) {
        this.key = str;
        this.value = str2;
        this.parentPropertyMap = map;
    }

    /* access modifiers changed from: protected */
    public void addImplicitRules(Interpreter interpreter) {
        NestedComplexPropertyIA nestedComplexPropertyIA = new NestedComplexPropertyIA();
        nestedComplexPropertyIA.setContext(this.context);
        interpreter.addImplicitAction(nestedComplexPropertyIA);
        NestedBasicPropertyIA nestedBasicPropertyIA = new NestedBasicPropertyIA();
        nestedBasicPropertyIA.setContext(this.context);
        interpreter.addImplicitAction(nestedBasicPropertyIA);
    }

    /* access modifiers changed from: protected */
    public void addInstanceRules(RuleStore ruleStore) {
        ruleStore.addRule(new ElementSelector("configuration/property"), (Action) new PropertyAction());
        ruleStore.addRule(new ElementSelector("configuration/timestamp"), (Action) new TimestampAction());
        ruleStore.addRule(new ElementSelector("configuration/define"), (Action) new DefinePropertyAction());
    }

    public void doConfigure(List<SaxEvent> list) throws JoranException {
        super.doConfigure(list);
    }

    public abstract Appender<E> getAppender();

    /* access modifiers changed from: protected */
    public void oneAndOnlyOneCheck(Map<?, ?> map) {
        String str;
        if (map.size() == 0) {
            this.errorEmmissionCount++;
            str = "No nested appenders found within the <sift> element in SiftingAppender.";
        } else if (map.size() > 1) {
            this.errorEmmissionCount++;
            str = "Only and only one appender can be nested the <sift> element in SiftingAppender. See also http://logback.qos.ch/codes.html#1andOnly1";
        } else {
            str = null;
        }
        if (str != null && this.errorEmmissionCount < 4) {
            addError(str);
        }
    }

    public String toString() {
        return getClass().getName() + "{" + this.key + "=" + this.value + CoreConstants.CURLY_RIGHT;
    }
}
