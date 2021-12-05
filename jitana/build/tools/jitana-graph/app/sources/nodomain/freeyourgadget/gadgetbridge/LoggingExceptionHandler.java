package nodomain.freeyourgadget.gadgetbridge;

import java.lang.Thread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p005ch.qos.logback.classic.LoggerContext;

public class LoggingExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) LoggingExceptionHandler.class);
    private final Thread.UncaughtExceptionHandler mDelegate;

    public LoggingExceptionHandler(Thread.UncaughtExceptionHandler delegate) {
        this.mDelegate = delegate;
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        Logger logger = LOG;
        logger.error("Uncaught exception: " + ex.getMessage(), ex);
        ((LoggerContext) LoggerFactory.getILoggerFactory()).stop();
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.mDelegate;
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, ex);
        } else {
            System.exit(1);
        }
    }
}
