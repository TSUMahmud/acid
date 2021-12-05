package p005ch.qos.logback.classic.util;

import java.lang.reflect.InvocationTargetException;
import p005ch.qos.logback.classic.ClassicConstants;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.classic.selector.ContextSelector;
import p005ch.qos.logback.classic.selector.DefaultContextSelector;
import p005ch.qos.logback.core.util.Loader;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.classic.util.ContextSelectorStaticBinder */
public class ContextSelectorStaticBinder {
    static ContextSelectorStaticBinder singleton = new ContextSelectorStaticBinder();
    ContextSelector contextSelector;
    Object key;

    static ContextSelector dynamicalContextSelector(LoggerContext loggerContext, String str) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return (ContextSelector) Loader.loadClass(str).getConstructor(new Class[]{LoggerContext.class}).newInstance(new Object[]{loggerContext});
    }

    public static ContextSelectorStaticBinder getSingleton() {
        return singleton;
    }

    public ContextSelector getContextSelector() {
        return this.contextSelector;
    }

    public void init(LoggerContext loggerContext, Object obj) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Object obj2 = this.key;
        if (obj2 == null) {
            this.key = obj;
        } else if (obj2 != obj) {
            throw new IllegalAccessException("Only certain classes can access this method.");
        }
        String systemProperty = OptionHelper.getSystemProperty(ClassicConstants.LOGBACK_CONTEXT_SELECTOR);
        if (systemProperty == null) {
            this.contextSelector = new DefaultContextSelector(loggerContext);
        } else if (!systemProperty.equals("JNDI")) {
            this.contextSelector = dynamicalContextSelector(loggerContext, systemProperty);
        } else {
            throw new RuntimeException("JNDI not supported");
        }
    }
}
