package nodomain.freeyourgadget.gadgetbridge;

import android.util.Log;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.Appender;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.FileAppender;
import p005ch.qos.logback.core.encoder.Encoder;
import p005ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import p005ch.qos.logback.core.util.StatusPrinter;

public abstract class Logging {
    public static final String PROP_LOGFILES_DIR = "GB_LOGFILES_DIR";
    private FileAppender<ILoggingEvent> fileLogger;

    /* access modifiers changed from: protected */
    public abstract String createLogDirectory() throws IOException;

    public void setupLogging(boolean enable) {
        try {
            if (this.fileLogger == null) {
                init();
            }
            if (enable) {
                startFileLogger();
            } else {
                stopFileLogger();
            }
            getLogger().info("Gadgetbridge version: 0.41.0");
        } catch (IOException ex) {
            Log.e("GBApplication", "External files dir not available, cannot log to file", ex);
            stopFileLogger();
        }
    }

    public String getLogPath() {
        FileAppender<ILoggingEvent> fileAppender = this.fileLogger;
        if (fileAppender != null) {
            return fileAppender.getFile();
        }
        return null;
    }

    public void debugLoggingConfiguration() {
        StatusPrinter.print((Context) (LoggerContext) LoggerFactory.getILoggerFactory());
    }

    /* access modifiers changed from: protected */
    public void init() throws IOException {
        String dir = createLogDirectory();
        if (dir != null) {
            System.setProperty(PROP_LOGFILES_DIR, dir);
            rememberFileLogger();
            return;
        }
        throw new IllegalArgumentException("log directory must not be null");
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger((Class<?>) Logging.class);
    }

    private void startFileLogger() {
        FileAppender<ILoggingEvent> fileAppender = this.fileLogger;
        if (fileAppender != null && !fileAppender.isStarted()) {
            addFileLogger(this.fileLogger);
            this.fileLogger.setLazy(false);
            this.fileLogger.start();
        }
    }

    private void stopFileLogger() {
        FileAppender<ILoggingEvent> fileAppender = this.fileLogger;
        if (fileAppender != null && fileAppender.isStarted()) {
            this.fileLogger.stop();
            removeFileLogger(this.fileLogger);
        }
    }

    private void rememberFileLogger() {
        this.fileLogger = (FileAppender) ((p005ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).getAppender("FILE");
    }

    private void addFileLogger(Appender<ILoggingEvent> fileLogger2) {
        try {
            p005ch.qos.logback.classic.Logger root = (p005ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            if (!root.isAttached(fileLogger2)) {
                root.addAppender(fileLogger2);
            }
        } catch (Throwable ex) {
            Log.e("GBApplication", "Error adding logger FILE appender", ex);
        }
    }

    private void removeFileLogger(Appender<ILoggingEvent> fileLogger2) {
        try {
            p005ch.qos.logback.classic.Logger root = (p005ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            if (root.isAttached(fileLogger2)) {
                root.detachAppender(fileLogger2);
            }
        } catch (Throwable ex) {
            Log.e("GBApplication", "Error removing logger FILE appender", ex);
        }
    }

    public FileAppender<ILoggingEvent> getFileLogger() {
        return this.fileLogger;
    }

    public boolean setImmediateFlush(boolean enable) {
        Encoder<ILoggingEvent> encoder = getFileLogger().getEncoder();
        if (!(encoder instanceof LayoutWrappingEncoder)) {
            return false;
        }
        ((LayoutWrappingEncoder) encoder).setImmediateFlush(enable);
        return true;
    }

    public boolean isImmediateFlush() {
        Encoder<ILoggingEvent> encoder = getFileLogger().getEncoder();
        if (encoder instanceof LayoutWrappingEncoder) {
            return ((LayoutWrappingEncoder) encoder).isImmediateFlush();
        }
        return false;
    }

    public static String formatBytes(byte[] bytes) {
        if (bytes == null) {
            return "(null)";
        }
        StringBuilder builder = new StringBuilder(bytes.length * 5);
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            builder.append(String.format("0x%02x", new Object[]{Byte.valueOf(bytes[i])}));
            builder.append(StringUtils.SPACE);
        }
        return builder.toString().trim();
    }

    public static void logBytes(Logger logger, byte[] value) {
        if (value != null) {
            logger.warn("DATA: " + C1238GB.hexdump(value, 0, value.length));
        }
    }
}
