package p005ch.qos.logback.classic.sift;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.classic.util.DefaultNestedComponentRules;
import p005ch.qos.logback.core.Appender;
import p005ch.qos.logback.core.joran.action.Action;
import p005ch.qos.logback.core.joran.action.ActionConst;
import p005ch.qos.logback.core.joran.action.AppenderAction;
import p005ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import p005ch.qos.logback.core.joran.spi.ElementPath;
import p005ch.qos.logback.core.joran.spi.ElementSelector;
import p005ch.qos.logback.core.joran.spi.RuleStore;
import p005ch.qos.logback.core.sift.SiftingJoranConfiguratorBase;

/* renamed from: ch.qos.logback.classic.sift.SiftingJoranConfigurator */
public class SiftingJoranConfigurator extends SiftingJoranConfiguratorBase<ILoggingEvent> {
    SiftingJoranConfigurator(String str, String str2, Map<String, String> map) {
        super(str, str2, map);
    }

    /* access modifiers changed from: protected */
    public void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry defaultNestedComponentRegistry) {
        DefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(defaultNestedComponentRegistry);
    }

    /* access modifiers changed from: protected */
    public void addInstanceRules(RuleStore ruleStore) {
        super.addInstanceRules(ruleStore);
        ruleStore.addRule(new ElementSelector("configuration/appender"), (Action) new AppenderAction());
    }

    /* access modifiers changed from: protected */
    public void buildInterpreter() {
        super.buildInterpreter();
        Map<String, Object> objectMap = this.interpreter.getInterpretationContext().getObjectMap();
        objectMap.put(ActionConst.APPENDER_BAG, new HashMap());
        objectMap.put(ActionConst.FILTER_CHAIN_BAG, new HashMap());
        HashMap hashMap = new HashMap();
        hashMap.putAll(this.parentPropertyMap);
        hashMap.put(this.key, this.value);
        this.interpreter.setInterpretationContextPropertiesMap(hashMap);
    }

    public Appender<ILoggingEvent> getAppender() {
        HashMap hashMap = (HashMap) this.interpreter.getInterpretationContext().getObjectMap().get(ActionConst.APPENDER_BAG);
        oneAndOnlyOneCheck(hashMap);
        Collection values = hashMap.values();
        if (values.size() == 0) {
            return null;
        }
        return (Appender) values.iterator().next();
    }

    /* access modifiers changed from: protected */
    public ElementPath initialElementPath() {
        return new ElementPath("configuration");
    }
}
