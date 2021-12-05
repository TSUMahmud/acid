package p005ch.qos.logback.classic.joran.action;

import org.xml.sax.Attributes;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.classic.turbo.ReconfigureOnChangeFilter;
import p005ch.qos.logback.core.joran.action.Action;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.status.OnConsoleStatusListener;
import p005ch.qos.logback.core.util.ContextUtil;
import p005ch.qos.logback.core.util.Duration;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.classic.joran.action.ConfigurationAction */
public class ConfigurationAction extends Action {
    static final String DEBUG_SYSTEM_PROPERTY_KEY = "logback.debug";
    static final String INTERNAL_DEBUG_ATTR = "debug";
    static final String SCAN_ATTR = "scan";
    static final String SCAN_PERIOD_ATTR = "scanPeriod";

    public void begin(InterpretationContext interpretationContext, String str, Attributes attributes) {
        String systemProperty = OptionHelper.getSystemProperty(DEBUG_SYSTEM_PROPERTY_KEY);
        if (systemProperty == null) {
            systemProperty = interpretationContext.subst(attributes.getValue("debug"));
        }
        if (OptionHelper.isEmpty(systemProperty) || systemProperty.equalsIgnoreCase("false") || systemProperty.equalsIgnoreCase("null")) {
            addInfo("debug attribute not set");
        } else {
            OnConsoleStatusListener.addNewInstanceToContext(this.context);
        }
        processScanAttrib(interpretationContext, attributes);
        new ContextUtil(this.context).addHostNameAsProperty();
        interpretationContext.pushObject(getContext());
    }

    public void end(InterpretationContext interpretationContext, String str) {
        addInfo("End of configuration.");
        interpretationContext.popObject();
    }

    /* access modifiers changed from: package-private */
    public String getSystemProperty(String str) {
        try {
            return System.getProperty(str);
        } catch (SecurityException e) {
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public void processScanAttrib(InterpretationContext interpretationContext, Attributes attributes) {
        String subst = interpretationContext.subst(attributes.getValue(SCAN_ATTR));
        if (!OptionHelper.isEmpty(subst) && !"false".equalsIgnoreCase(subst)) {
            ReconfigureOnChangeFilter reconfigureOnChangeFilter = new ReconfigureOnChangeFilter();
            reconfigureOnChangeFilter.setContext(this.context);
            String subst2 = interpretationContext.subst(attributes.getValue(SCAN_PERIOD_ATTR));
            if (!OptionHelper.isEmpty(subst2)) {
                try {
                    Duration valueOf = Duration.valueOf(subst2);
                    reconfigureOnChangeFilter.setRefreshPeriod(valueOf.getMilliseconds());
                    addInfo("Setting ReconfigureOnChangeFilter scanning period to " + valueOf);
                } catch (NumberFormatException e) {
                    addError("Error while converting [" + subst + "] to long", e);
                }
            }
            reconfigureOnChangeFilter.start();
            addInfo("Adding ReconfigureOnChangeFilter as a turbo filter");
            ((LoggerContext) this.context).addTurboFilter(reconfigureOnChangeFilter);
        }
    }
}
