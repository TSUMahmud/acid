package p005ch.qos.logback.classic.selector;

import java.util.Arrays;
import java.util.List;
import p005ch.qos.logback.classic.LoggerContext;

/* renamed from: ch.qos.logback.classic.selector.DefaultContextSelector */
public class DefaultContextSelector implements ContextSelector {
    private LoggerContext defaultLoggerContext;

    public DefaultContextSelector(LoggerContext loggerContext) {
        this.defaultLoggerContext = loggerContext;
    }

    public LoggerContext detachLoggerContext(String str) {
        return this.defaultLoggerContext;
    }

    public List<String> getContextNames() {
        return Arrays.asList(new String[]{this.defaultLoggerContext.getName()});
    }

    public LoggerContext getDefaultLoggerContext() {
        return this.defaultLoggerContext;
    }

    public LoggerContext getLoggerContext() {
        return getDefaultLoggerContext();
    }

    public LoggerContext getLoggerContext(String str) {
        if (this.defaultLoggerContext.getName().equals(str)) {
            return this.defaultLoggerContext;
        }
        return null;
    }
}
