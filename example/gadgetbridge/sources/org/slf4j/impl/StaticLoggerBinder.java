package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.helpers.Util;
import org.slf4j.spi.LoggerFactoryBinder;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.classic.util.ContextInitializer;
import p005ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import p005ch.qos.logback.core.joran.spi.JoranException;
import p005ch.qos.logback.core.status.StatusUtil;
import p005ch.qos.logback.core.util.StatusPrinter;

public class StaticLoggerBinder implements LoggerFactoryBinder {
    private static Object KEY = new Object();
    static final String NULL_CS_URL = "http://logback.qos.ch/codes.html#null_CS";
    public static String REQUESTED_API_VERSION = "1.6";
    private static StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
    private final ContextSelectorStaticBinder contextSelectorBinder = ContextSelectorStaticBinder.getSingleton();
    private LoggerContext defaultLoggerContext = new LoggerContext();
    private boolean initialized = false;

    static {
        SINGLETON.init();
    }

    private StaticLoggerBinder() {
        this.defaultLoggerContext.setName("default");
    }

    public static StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    static void reset() {
        SINGLETON = new StaticLoggerBinder();
        SINGLETON.init();
    }

    public ILoggerFactory getLoggerFactory() {
        if (!this.initialized) {
            return this.defaultLoggerContext;
        }
        if (this.contextSelectorBinder.getContextSelector() != null) {
            return this.contextSelectorBinder.getContextSelector().getLoggerContext();
        }
        throw new IllegalStateException("contextSelector cannot be null. See also http://logback.qos.ch/codes.html#null_CS");
    }

    public String getLoggerFactoryClassStr() {
        return this.contextSelectorBinder.getClass().getName();
    }

    /* access modifiers changed from: package-private */
    public void init() {
        try {
            new ContextInitializer(this.defaultLoggerContext).autoConfig();
        } catch (JoranException e) {
            Util.report("Failed to auto configure default logger context", e);
        } catch (Throwable th) {
            Util.report("Failed to instantiate [" + LoggerContext.class.getName() + "]", th);
            return;
        }
        if (!StatusUtil.contextHasStatusListener(this.defaultLoggerContext)) {
            StatusPrinter.printInCaseOfErrorsOrWarnings(this.defaultLoggerContext);
        }
        this.contextSelectorBinder.init(this.defaultLoggerContext, KEY);
        this.initialized = true;
    }
}
