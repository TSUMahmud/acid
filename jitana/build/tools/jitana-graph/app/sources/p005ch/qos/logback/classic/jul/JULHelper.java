package p005ch.qos.logback.classic.jul;

import java.util.logging.Level;
import java.util.logging.Logger;

@Deprecated
/* renamed from: ch.qos.logback.classic.jul.JULHelper */
public class JULHelper {
    public static Level asJULLevel(p005ch.qos.logback.classic.Level level) {
        if (level != null) {
            int i = level.levelInt;
            if (i == Integer.MIN_VALUE) {
                return Level.ALL;
            }
            if (i == 5000) {
                return Level.FINEST;
            }
            if (i == 10000) {
                return Level.FINE;
            }
            if (i == 20000) {
                return Level.INFO;
            }
            if (i == 30000) {
                return Level.WARNING;
            }
            if (i == 40000) {
                return Level.SEVERE;
            }
            if (i == Integer.MAX_VALUE) {
                return Level.OFF;
            }
            throw new IllegalArgumentException("Unexpected level [" + level + "]");
        }
        throw new IllegalArgumentException("Unexpected level [null]");
    }

    public static Logger asJULLogger(p005ch.qos.logback.classic.Logger logger) {
        return asJULLogger(logger.getName());
    }

    public static Logger asJULLogger(String str) {
        return Logger.getLogger(asJULLoggerName(str));
    }

    public static String asJULLoggerName(String str) {
        return org.slf4j.Logger.ROOT_LOGGER_NAME.equals(str) ? "" : str;
    }

    public static final boolean isRegularNonRootLogger(Logger logger) {
        if (logger == null) {
            return false;
        }
        return !logger.getName().equals("");
    }

    public static final boolean isRoot(Logger logger) {
        if (logger == null) {
            return false;
        }
        return logger.getName().equals("");
    }
}
