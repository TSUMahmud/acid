package p005ch.qos.logback.core.joran.action;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.xml.sax.Attributes;
import p005ch.qos.logback.core.joran.action.ActionUtil;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import p005ch.qos.logback.core.util.Loader;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.core.joran.action.PropertyAction */
public class PropertyAction extends Action {
    static String INVALID_ATTRIBUTES = "In <property> element, either the \"file\" attribute alone, or the \"resource\" element alone, or both the \"name\" and \"value\" attributes must be set.";
    static final String RESOURCE_ATTRIBUTE = "resource";

    public void begin(InterpretationContext interpretationContext, String str, Attributes attributes) {
        String str2;
        String subst;
        StringBuilder sb;
        String str3;
        StringBuilder sb2;
        String str4;
        if ("substitutionProperty".equals(str)) {
            addWarn("[substitutionProperty] element has been deprecated. Please use the [property] element instead.");
        }
        String value = attributes.getValue("name");
        String value2 = attributes.getValue("value");
        ActionUtil.Scope stringToScope = ActionUtil.stringToScope(attributes.getValue(Action.SCOPE_ATTRIBUTE));
        if (checkFileAttributeSanity(attributes)) {
            subst = interpretationContext.subst(attributes.getValue(Action.FILE_ATTRIBUTE));
            try {
                loadAndSetProperties(interpretationContext, new FileInputStream(subst), stringToScope);
            } catch (FileNotFoundException e) {
                sb2 = new StringBuilder();
                str4 = "Could not find properties file [";
            } catch (IOException e2) {
                e = e2;
                sb = new StringBuilder();
                str3 = "Could not read properties file [";
                sb.append(str3);
                sb.append(subst);
                sb.append("].");
                addError(sb.toString(), e);
            }
        } else if (checkResourceAttributeSanity(attributes)) {
            subst = interpretationContext.subst(attributes.getValue(RESOURCE_ATTRIBUTE));
            URL resourceBySelfClassLoader = Loader.getResourceBySelfClassLoader(subst);
            if (resourceBySelfClassLoader == null) {
                sb2 = new StringBuilder();
                str4 = "Could not find resource [";
                sb2.append(str4);
                sb2.append(subst);
                sb2.append("].");
                str2 = sb2.toString();
                addError(str2);
            }
            try {
                loadAndSetProperties(interpretationContext, resourceBySelfClassLoader.openStream(), stringToScope);
            } catch (IOException e3) {
                e = e3;
                sb = new StringBuilder();
                str3 = "Could not read resource file [";
            }
        } else if (checkValueNameAttributesSanity(attributes)) {
            ActionUtil.setProperty(interpretationContext, value, interpretationContext.subst(RegularEscapeUtil.basicEscape(value2).trim()), stringToScope);
        } else {
            str2 = INVALID_ATTRIBUTES;
            addError(str2);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean checkFileAttributeSanity(Attributes attributes) {
        return !OptionHelper.isEmpty(attributes.getValue(Action.FILE_ATTRIBUTE)) && OptionHelper.isEmpty(attributes.getValue("name")) && OptionHelper.isEmpty(attributes.getValue("value")) && OptionHelper.isEmpty(attributes.getValue(RESOURCE_ATTRIBUTE));
    }

    /* access modifiers changed from: package-private */
    public boolean checkResourceAttributeSanity(Attributes attributes) {
        return !OptionHelper.isEmpty(attributes.getValue(RESOURCE_ATTRIBUTE)) && OptionHelper.isEmpty(attributes.getValue("name")) && OptionHelper.isEmpty(attributes.getValue("value")) && OptionHelper.isEmpty(attributes.getValue(Action.FILE_ATTRIBUTE));
    }

    /* access modifiers changed from: package-private */
    public boolean checkValueNameAttributesSanity(Attributes attributes) {
        return !OptionHelper.isEmpty(attributes.getValue("name")) && !OptionHelper.isEmpty(attributes.getValue("value")) && OptionHelper.isEmpty(attributes.getValue(Action.FILE_ATTRIBUTE)) && OptionHelper.isEmpty(attributes.getValue(RESOURCE_ATTRIBUTE));
    }

    public void end(InterpretationContext interpretationContext, String str) {
    }

    public void finish(InterpretationContext interpretationContext) {
    }

    /* access modifiers changed from: package-private */
    public void loadAndSetProperties(InterpretationContext interpretationContext, InputStream inputStream, ActionUtil.Scope scope) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();
        ActionUtil.setProperties(interpretationContext, properties, scope);
    }
}
