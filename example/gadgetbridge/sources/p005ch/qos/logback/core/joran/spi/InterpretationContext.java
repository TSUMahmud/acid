package p005ch.qos.logback.core.joran.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import org.xml.sax.Locator;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.joran.event.InPlayListener;
import p005ch.qos.logback.core.joran.event.SaxEvent;
import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.spi.PropertyContainer;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.core.joran.spi.InterpretationContext */
public class InterpretationContext extends ContextAwareBase implements PropertyContainer {
    DefaultNestedComponentRegistry defaultNestedComponentRegistry = new DefaultNestedComponentRegistry();
    Interpreter joranInterpreter;
    final List<InPlayListener> listenerList = new ArrayList();
    Map<String, Object> objectMap;
    Stack<Object> objectStack;
    Map<String, String> propertiesMap;

    public InterpretationContext(Context context, Interpreter interpreter) {
        this.context = context;
        this.joranInterpreter = interpreter;
        this.objectStack = new Stack<>();
        this.objectMap = new HashMap(5);
        this.propertiesMap = new HashMap(5);
    }

    public void addInPlayListener(InPlayListener inPlayListener) {
        if (this.listenerList.contains(inPlayListener)) {
            addWarn("InPlayListener " + inPlayListener + " has been already registered");
            return;
        }
        this.listenerList.add(inPlayListener);
    }

    public void addSubstitutionProperties(Properties properties) {
        if (properties != null) {
            for (String str : properties.keySet()) {
                addSubstitutionProperty(str, properties.getProperty(str));
            }
        }
    }

    public void addSubstitutionProperty(String str, String str2) {
        if (str != null && str2 != null) {
            this.propertiesMap.put(str, str2.trim());
        }
    }

    /* access modifiers changed from: package-private */
    public void fireInPlay(SaxEvent saxEvent) {
        for (InPlayListener inPlay : this.listenerList) {
            inPlay.inPlay(saxEvent);
        }
    }

    public Map<String, String> getCopyOfPropertyMap() {
        return new HashMap(this.propertiesMap);
    }

    public DefaultNestedComponentRegistry getDefaultNestedComponentRegistry() {
        return this.defaultNestedComponentRegistry;
    }

    public Interpreter getJoranInterpreter() {
        return this.joranInterpreter;
    }

    public Locator getLocator() {
        return this.joranInterpreter.getLocator();
    }

    public Object getObject(int i) {
        return this.objectStack.get(i);
    }

    public Map<String, Object> getObjectMap() {
        return this.objectMap;
    }

    public Stack<Object> getObjectStack() {
        return this.objectStack;
    }

    public String getProperty(String str) {
        String str2 = this.propertiesMap.get(str);
        return str2 != null ? str2 : this.context.getProperty(str);
    }

    public boolean isEmpty() {
        return this.objectStack.isEmpty();
    }

    public boolean isListenerListEmpty() {
        return this.listenerList.isEmpty();
    }

    public Object peekObject() {
        return this.objectStack.peek();
    }

    public Object popObject() {
        return this.objectStack.pop();
    }

    public void pushObject(Object obj) {
        this.objectStack.push(obj);
    }

    public boolean removeInPlayListener(InPlayListener inPlayListener) {
        return this.listenerList.remove(inPlayListener);
    }

    /* access modifiers changed from: package-private */
    public void setPropertiesMap(Map<String, String> map) {
        this.propertiesMap = map;
    }

    public String subst(String str) {
        if (str == null) {
            return null;
        }
        return OptionHelper.substVars(str, this, this.context);
    }

    /* access modifiers changed from: package-private */
    public String updateLocationInfo(String str) {
        Locator locator = this.joranInterpreter.getLocator();
        if (locator == null) {
            return str;
        }
        return str + locator.getLineNumber() + ":" + locator.getColumnNumber();
    }
}
