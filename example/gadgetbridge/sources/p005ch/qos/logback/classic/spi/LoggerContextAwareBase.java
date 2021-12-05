package p005ch.qos.logback.classic.spi;

import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.spi.ContextAwareBase;

/* renamed from: ch.qos.logback.classic.spi.LoggerContextAwareBase */
public class LoggerContextAwareBase extends ContextAwareBase implements LoggerContextAware {
    public LoggerContext getLoggerContext() {
        return (LoggerContext) this.context;
    }

    public void setContext(Context context) {
        if ((context instanceof LoggerContext) || context == null) {
            super.setContext(context);
            return;
        }
        throw new IllegalArgumentException("LoggerContextAwareBase only accepts contexts of type c.l.classic.LoggerContext");
    }

    public void setLoggerContext(LoggerContext loggerContext) {
        super.setContext(loggerContext);
    }
}
