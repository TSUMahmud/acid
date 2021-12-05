package p005ch.qos.logback.classic.util;

import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.spi.ContextAware;
import p005ch.qos.logback.core.spi.LifeCycle;
import p005ch.qos.logback.core.status.StatusListener;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.classic.util.StatusListenerConfigHelper */
public class StatusListenerConfigHelper {
    private static void addStatusListener(LoggerContext loggerContext, String str) {
        initListener(loggerContext, createListenerPerClassName(loggerContext, str));
    }

    private static StatusListener createListenerPerClassName(LoggerContext loggerContext, String str) {
        try {
            return (StatusListener) OptionHelper.instantiateByClassName(str, (Class<?>) StatusListener.class, (Context) loggerContext);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void initListener(LoggerContext loggerContext, StatusListener statusListener) {
        if (statusListener != null) {
            if (statusListener instanceof ContextAware) {
                ((ContextAware) statusListener).setContext(loggerContext);
            }
            if (statusListener instanceof LifeCycle) {
                ((LifeCycle) statusListener).start();
            }
            loggerContext.getStatusManager().add(statusListener);
        }
    }

    static void installIfAsked(LoggerContext loggerContext) {
        String systemProperty = OptionHelper.getSystemProperty(ContextInitializer.STATUS_LISTENER_CLASS);
        if (!OptionHelper.isEmpty(systemProperty)) {
            addStatusListener(loggerContext, systemProperty);
        }
    }
}
