package p005ch.qos.logback.classic.jul;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.LogManager;
import p005ch.qos.logback.classic.Level;
import p005ch.qos.logback.classic.Logger;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.classic.spi.LoggerContextListener;
import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.spi.LifeCycle;

@Deprecated
/* renamed from: ch.qos.logback.classic.jul.LevelChangePropagator */
public class LevelChangePropagator extends ContextAwareBase implements LoggerContextListener, LifeCycle {
    boolean isStarted = false;
    private Set julLoggerSet = new HashSet();
    boolean resetJUL = false;

    private void propagate(Logger logger, Level level) {
        addInfo("Propagating " + level + " level on " + logger + " onto the JUL framework");
        java.util.logging.Logger asJULLogger = JULHelper.asJULLogger(logger);
        this.julLoggerSet.add(asJULLogger);
        asJULLogger.setLevel(JULHelper.asJULLevel(level));
    }

    private void propagateExistingLoggerLevels() {
        for (Logger next : ((LoggerContext) this.context).getLoggerList()) {
            if (next.getLevel() != null) {
                propagate(next, next.getLevel());
            }
        }
    }

    public boolean isResetResistant() {
        return false;
    }

    public boolean isStarted() {
        return this.isStarted;
    }

    public void onLevelChange(Logger logger, Level level) {
        propagate(logger, level);
    }

    public void onReset(LoggerContext loggerContext) {
    }

    public void onStart(LoggerContext loggerContext) {
    }

    public void onStop(LoggerContext loggerContext) {
    }

    public void resetJULLevels() {
        LogManager logManager = LogManager.getLogManager();
        Enumeration<String> loggerNames = logManager.getLoggerNames();
        while (loggerNames.hasMoreElements()) {
            String nextElement = loggerNames.nextElement();
            java.util.logging.Logger logger = logManager.getLogger(nextElement);
            if (JULHelper.isRegularNonRootLogger(logger) && logger.getLevel() != null) {
                addInfo("Setting level of jul logger [" + nextElement + "] to null");
                logger.setLevel((java.util.logging.Level) null);
            }
        }
    }

    public void setResetJUL(boolean z) {
        this.resetJUL = z;
    }

    public void start() {
        if (this.resetJUL) {
            resetJULLevels();
        }
        propagateExistingLoggerLevels();
        this.isStarted = true;
    }

    public void stop() {
        this.isStarted = false;
    }
}
