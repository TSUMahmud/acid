package p005ch.qos.logback.classic.spi;

import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.core.spi.ContextAware;

/* renamed from: ch.qos.logback.classic.spi.LoggerContextAware */
public interface LoggerContextAware extends ContextAware {
    void setLoggerContext(LoggerContext loggerContext);
}
