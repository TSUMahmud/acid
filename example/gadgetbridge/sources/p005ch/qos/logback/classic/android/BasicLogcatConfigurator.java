package p005ch.qos.logback.classic.android;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import p005ch.qos.logback.core.status.InfoStatus;
import p005ch.qos.logback.core.status.Status;
import p005ch.qos.logback.core.status.StatusManager;

/* renamed from: ch.qos.logback.classic.android.BasicLogcatConfigurator */
public class BasicLogcatConfigurator {
    private BasicLogcatConfigurator() {
    }

    public static void configure(LoggerContext loggerContext) {
        StatusManager statusManager = loggerContext.getStatusManager();
        if (statusManager != null) {
            statusManager.add((Status) new InfoStatus("Setting up default configuration.", loggerContext));
        }
        LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(loggerContext);
        logcatAppender.setName("logcat");
        PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder();
        patternLayoutEncoder.setContext(loggerContext);
        patternLayoutEncoder.setPattern("%msg");
        patternLayoutEncoder.start();
        logcatAppender.setEncoder(patternLayoutEncoder);
        logcatAppender.start();
        loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(logcatAppender);
    }

    public static void configureDefaultContext() {
        configure((LoggerContext) LoggerFactory.getILoggerFactory());
    }
}
