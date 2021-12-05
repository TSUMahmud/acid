package p005ch.qos.logback.classic;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.classic.spi.LoggingEvent;
import p005ch.qos.logback.classic.util.LoggerNameUtil;
import p005ch.qos.logback.core.Appender;
import p005ch.qos.logback.core.spi.AppenderAttachable;
import p005ch.qos.logback.core.spi.AppenderAttachableImpl;
import p005ch.qos.logback.core.spi.FilterReply;

/* renamed from: ch.qos.logback.classic.Logger */
public final class Logger implements org.slf4j.Logger, LocationAwareLogger, AppenderAttachable<ILoggingEvent>, Serializable {
    private static final int DEFAULT_CHILD_ARRAY_SIZE = 5;
    public static final String FQCN = Logger.class.getName();
    private static final long serialVersionUID = 5454405123156820674L;
    private transient AppenderAttachableImpl<ILoggingEvent> aai;
    private transient boolean additive = true;
    private transient List<Logger> childrenList;
    private transient int effectiveLevelInt;
    private transient Level level;
    final transient LoggerContext loggerContext;
    private String name;
    private transient Logger parent;

    Logger(String str, Logger logger, LoggerContext loggerContext2) {
        this.name = str;
        this.parent = logger;
        this.loggerContext = loggerContext2;
    }

    private int appendLoopOnAppenders(ILoggingEvent iLoggingEvent) {
        AppenderAttachableImpl<ILoggingEvent> appenderAttachableImpl = this.aai;
        if (appenderAttachableImpl != null) {
            return appenderAttachableImpl.appendLoopOnAppenders(iLoggingEvent);
        }
        return 0;
    }

    private void buildLoggingEventAndAppend(String str, Marker marker, Level level2, String str2, Object[] objArr, Throwable th) {
        LoggingEvent loggingEvent = new LoggingEvent(str, this, level2, str2, th, objArr);
        loggingEvent.setMarker(marker);
        callAppenders(loggingEvent);
    }

    private FilterReply callTurboFilters(Marker marker, Level level2) {
        return this.loggerContext.getTurboFilterChainDecision_0_3OrMore(marker, this, level2, (String) null, (Object[]) null, (Throwable) null);
    }

    private void filterAndLog_0_Or3Plus(String str, Marker marker, Level level2, String str2, Object[] objArr, Throwable th) {
        FilterReply turboFilterChainDecision_0_3OrMore = this.loggerContext.getTurboFilterChainDecision_0_3OrMore(marker, this, level2, str2, objArr, th);
        if (turboFilterChainDecision_0_3OrMore == FilterReply.NEUTRAL) {
            if (this.effectiveLevelInt > level2.levelInt) {
                return;
            }
        } else if (turboFilterChainDecision_0_3OrMore == FilterReply.DENY) {
            return;
        }
        buildLoggingEventAndAppend(str, marker, level2, str2, objArr, th);
    }

    private void filterAndLog_1(String str, Marker marker, Level level2, String str2, Object obj, Throwable th) {
        FilterReply turboFilterChainDecision_1 = this.loggerContext.getTurboFilterChainDecision_1(marker, this, level2, str2, obj, th);
        if (turboFilterChainDecision_1 == FilterReply.NEUTRAL) {
            if (this.effectiveLevelInt > level2.levelInt) {
                return;
            }
        } else if (turboFilterChainDecision_1 == FilterReply.DENY) {
            return;
        }
        buildLoggingEventAndAppend(str, marker, level2, str2, new Object[]{obj}, th);
    }

    private void filterAndLog_2(String str, Marker marker, Level level2, String str2, Object obj, Object obj2, Throwable th) {
        FilterReply turboFilterChainDecision_2 = this.loggerContext.getTurboFilterChainDecision_2(marker, this, level2, str2, obj, obj2, th);
        if (turboFilterChainDecision_2 == FilterReply.NEUTRAL) {
            if (this.effectiveLevelInt > level2.levelInt) {
                return;
            }
        } else if (turboFilterChainDecision_2 == FilterReply.DENY) {
            return;
        }
        buildLoggingEventAndAppend(str, marker, level2, str2, new Object[]{obj, obj2}, th);
    }

    private synchronized void handleParentLevelChange(int i) {
        if (this.level == null) {
            this.effectiveLevelInt = i;
            if (this.childrenList != null) {
                int size = this.childrenList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    this.childrenList.get(i2).handleParentLevelChange(i);
                }
            }
        }
    }

    private boolean isRootLogger() {
        return this.parent == null;
    }

    private void localLevelReset() {
        this.effectiveLevelInt = 10000;
        this.level = isRootLogger() ? Level.DEBUG : null;
    }

    public synchronized void addAppender(Appender<ILoggingEvent> appender) {
        if (this.aai == null) {
            this.aai = new AppenderAttachableImpl<>();
        }
        this.aai.addAppender(appender);
    }

    public void callAppenders(ILoggingEvent iLoggingEvent) {
        int i = 0;
        for (Logger logger = this; logger != null; logger = logger.parent) {
            i += logger.appendLoopOnAppenders(iLoggingEvent);
            if (!logger.additive) {
                break;
            }
        }
        if (i == 0) {
            this.loggerContext.noAppenderDefinedWarning(this);
        }
    }

    /* access modifiers changed from: package-private */
    public Logger createChildByLastNamePart(String str) {
        Logger logger;
        if (LoggerNameUtil.getFirstSeparatorIndexOf(str) == -1) {
            if (this.childrenList == null) {
                this.childrenList = new ArrayList();
            }
            if (isRootLogger()) {
                logger = new Logger(str, this, this.loggerContext);
            } else {
                logger = new Logger(this.name + '.' + str, this, this.loggerContext);
            }
            this.childrenList.add(logger);
            logger.effectiveLevelInt = this.effectiveLevelInt;
            return logger;
        }
        throw new IllegalArgumentException("Child name [" + str + " passed as parameter, may not include [" + '.' + "]");
    }

    /* access modifiers changed from: package-private */
    public Logger createChildByName(String str) {
        if (LoggerNameUtil.getSeparatorIndexOf(str, this.name.length() + 1) == -1) {
            if (this.childrenList == null) {
                this.childrenList = new ArrayList(5);
            }
            Logger logger = new Logger(str, this, this.loggerContext);
            this.childrenList.add(logger);
            logger.effectiveLevelInt = this.effectiveLevelInt;
            return logger;
        }
        throw new IllegalArgumentException("For logger [" + this.name + "] child name [" + str + " passed as parameter, may not include '.' after index" + (this.name.length() + 1));
    }

    public void debug(String str) {
        filterAndLog_0_Or3Plus(FQCN, (Marker) null, Level.DEBUG, str, (Object[]) null, (Throwable) null);
    }

    public void debug(String str, Object obj) {
        filterAndLog_1(FQCN, (Marker) null, Level.DEBUG, str, obj, (Throwable) null);
    }

    public void debug(String str, Object obj, Object obj2) {
        filterAndLog_2(FQCN, (Marker) null, Level.DEBUG, str, obj, obj2, (Throwable) null);
    }

    public void debug(String str, Throwable th) {
        filterAndLog_0_Or3Plus(FQCN, (Marker) null, Level.DEBUG, str, (Object[]) null, th);
    }

    public void debug(String str, Object[] objArr) {
        filterAndLog_0_Or3Plus(FQCN, (Marker) null, Level.DEBUG, str, objArr, (Throwable) null);
    }

    public void debug(Marker marker, String str) {
        filterAndLog_0_Or3Plus(FQCN, marker, Level.DEBUG, str, (Object[]) null, (Throwable) null);
    }

    public void debug(Marker marker, String str, Object obj) {
        filterAndLog_1(FQCN, marker, Level.DEBUG, str, obj, (Throwable) null);
    }

    public void debug(Marker marker, String str, Object obj, Object obj2) {
        filterAndLog_2(FQCN, marker, Level.DEBUG, str, obj, obj2, (Throwable) null);
    }

    public void debug(Marker marker, String str, Throwable th) {
        filterAndLog_0_Or3Plus(FQCN, marker, Level.DEBUG, str, (Object[]) null, th);
    }

    public void debug(Marker marker, String str, Object[] objArr) {
        filterAndLog_0_Or3Plus(FQCN, marker, Level.DEBUG, str, objArr, (Throwable) null);
    }

    public void detachAndStopAllAppenders() {
        AppenderAttachableImpl<ILoggingEvent> appenderAttachableImpl = this.aai;
        if (appenderAttachableImpl != null) {
            appenderAttachableImpl.detachAndStopAllAppenders();
        }
    }

    public boolean detachAppender(Appender<ILoggingEvent> appender) {
        AppenderAttachableImpl<ILoggingEvent> appenderAttachableImpl = this.aai;
        if (appenderAttachableImpl == null) {
            return false;
        }
        return appenderAttachableImpl.detachAppender(appender);
    }

    public boolean detachAppender(String str) {
        AppenderAttachableImpl<ILoggingEvent> appenderAttachableImpl = this.aai;
        if (appenderAttachableImpl == null) {
            return false;
        }
        return appenderAttachableImpl.detachAppender(str);
    }

    public void error(String str) {
        filterAndLog_0_Or3Plus(FQCN, (Marker) null, Level.ERROR, str, (Object[]) null, (Throwable) null);
    }

    public void error(String str, Object obj) {
        filterAndLog_1(FQCN, (Marker) null, Level.ERROR, str, obj, (Throwable) null);
    }

    public void error(String str, Object obj, Object obj2) {
        filterAndLog_2(FQCN, (Marker) null, Level.ERROR, str, obj, obj2, (Throwable) null);
    }

    public void error(String str, Throwable th) {
        filterAndLog_0_Or3Plus(FQCN, (Marker) null, Level.ERROR, str, (Object[]) null, th);
    }

    public void error(String str, Object[] objArr) {
        filterAndLog_0_Or3Plus(FQCN, (Marker) null, Level.ERROR, str, objArr, (Throwable) null);
    }

    public void error(Marker marker, String str) {
        filterAndLog_0_Or3Plus(FQCN, marker, Level.ERROR, str, (Object[]) null, (Throwable) null);
    }

    public void error(Marker marker, String str, Object obj) {
        filterAndLog_1(FQCN, marker, Level.ERROR, str, obj, (Throwable) null);
    }

    public void error(Marker marker, String str, Object obj, Object obj2) {
        filterAndLog_2(FQCN, marker, Level.ERROR, str, obj, obj2, (Throwable) null);
    }

    public void error(Marker marker, String str, Throwable th) {
        filterAndLog_0_Or3Plus(FQCN, marker, Level.ERROR, str, (Object[]) null, th);
    }

    public void error(Marker marker, String str, Object[] objArr) {
        filterAndLog_0_Or3Plus(FQCN, marker, Level.ERROR, str, objArr, (Throwable) null);
    }

    public Appender<ILoggingEvent> getAppender(String str) {
        AppenderAttachableImpl<ILoggingEvent> appenderAttachableImpl = this.aai;
        if (appenderAttachableImpl == null) {
            return null;
        }
        return appenderAttachableImpl.getAppender(str);
    }

    /* access modifiers changed from: package-private */
    public Logger getChildByName(String str) {
        List<Logger> list = this.childrenList;
        if (list == null) {
            return null;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Logger logger = this.childrenList.get(i);
            if (str.equals(logger.getName())) {
                return logger;
            }
        }
        return null;
    }

    public Level getEffectiveLevel() {
        return Level.toLevel(this.effectiveLevelInt);
    }

    /* access modifiers changed from: package-private */
    public int getEffectiveLevelInt() {
        return this.effectiveLevelInt;
    }

    public Level getLevel() {
        return this.level;
    }

    public LoggerContext getLoggerContext() {
        return this.loggerContext;
    }

    public String getName() {
        return this.name;
    }

    public void info(String str) {
        filterAndLog_0_Or3Plus(FQCN, (Marker) null, Level.INFO, str, (Object[]) null, (Throwable) null);
    }

    public void info(String str, Object obj) {
        filterAndLog_1(FQCN, (Marker) null, Level.INFO, str, obj, (Throwable) null);
    }

    public void info(String str, Object obj, Object obj2) {
        filterAndLog_2(FQCN, (Marker) null, Level.INFO, str, obj, obj2, (Throwable) null);
    }

    public void info(String str, Throwable th) {
        filterAndLog_0_Or3Plus(FQCN, (Marker) null, Level.INFO, str, (Object[]) null, th);
    }

    public void info(String str, Object[] objArr) {
        filterAndLog_0_Or3Plus(FQCN, (Marker) null, Level.INFO, str, objArr, (Throwable) null);
    }

    public void info(Marker marker, String str) {
        filterAndLog_0_Or3Plus(FQCN, marker, Level.INFO, str, (Object[]) null, (Throwable) null);
    }

    public void info(Marker marker, String str, Object obj) {
        filterAndLog_1(FQCN, marker, Level.INFO, str, obj, (Throwable) null);
    }

    public void info(Marker marker, String str, Object obj, Object obj2) {
        filterAndLog_2(FQCN, marker, Level.INFO, str, obj, obj2, (Throwable) null);
    }

    public void info(Marker marker, String str, Throwable th) {
        filterAndLog_0_Or3Plus(FQCN, marker, Level.INFO, str, (Object[]) null, th);
    }

    public void info(Marker marker, String str, Object[] objArr) {
        filterAndLog_0_Or3Plus(FQCN, marker, Level.INFO, str, objArr, (Throwable) null);
    }

    public boolean isAdditive() {
        return this.additive;
    }

    public boolean isAttached(Appender<ILoggingEvent> appender) {
        AppenderAttachableImpl<ILoggingEvent> appenderAttachableImpl = this.aai;
        if (appenderAttachableImpl == null) {
            return false;
        }
        return appenderAttachableImpl.isAttached(appender);
    }

    public boolean isDebugEnabled() {
        return isDebugEnabled((Marker) null);
    }

    public boolean isDebugEnabled(Marker marker) {
        FilterReply callTurboFilters = callTurboFilters(marker, Level.DEBUG);
        if (callTurboFilters == FilterReply.NEUTRAL) {
            return this.effectiveLevelInt <= 10000;
        }
        if (callTurboFilters == FilterReply.DENY) {
            return false;
        }
        if (callTurboFilters == FilterReply.ACCEPT) {
            return true;
        }
        throw new IllegalStateException("Unknown FilterReply value: " + callTurboFilters);
    }

    public boolean isEnabledFor(Level level2) {
        return isEnabledFor((Marker) null, level2);
    }

    public boolean isEnabledFor(Marker marker, Level level2) {
        FilterReply callTurboFilters = callTurboFilters(marker, level2);
        if (callTurboFilters == FilterReply.NEUTRAL) {
            return this.effectiveLevelInt <= level2.levelInt;
        }
        if (callTurboFilters == FilterReply.DENY) {
            return false;
        }
        if (callTurboFilters == FilterReply.ACCEPT) {
            return true;
        }
        throw new IllegalStateException("Unknown FilterReply value: " + callTurboFilters);
    }

    public boolean isErrorEnabled() {
        return isErrorEnabled((Marker) null);
    }

    public boolean isErrorEnabled(Marker marker) {
        FilterReply callTurboFilters = callTurboFilters(marker, Level.ERROR);
        if (callTurboFilters == FilterReply.NEUTRAL) {
            return this.effectiveLevelInt <= 40000;
        }
        if (callTurboFilters == FilterReply.DENY) {
            return false;
        }
        if (callTurboFilters == FilterReply.ACCEPT) {
            return true;
        }
        throw new IllegalStateException("Unknown FilterReply value: " + callTurboFilters);
    }

    public boolean isInfoEnabled() {
        return isInfoEnabled((Marker) null);
    }

    public boolean isInfoEnabled(Marker marker) {
        FilterReply callTurboFilters = callTurboFilters(marker, Level.INFO);
        if (callTurboFilters == FilterReply.NEUTRAL) {
            return this.effectiveLevelInt <= 20000;
        }
        if (callTurboFilters == FilterReply.DENY) {
            return false;
        }
        if (callTurboFilters == FilterReply.ACCEPT) {
            return true;
        }
        throw new IllegalStateException("Unknown FilterReply value: " + callTurboFilters);
    }

    public boolean isTraceEnabled() {
        return isTraceEnabled((Marker) null);
    }

    public boolean isTraceEnabled(Marker marker) {
        FilterReply callTurboFilters = callTurboFilters(marker, Level.TRACE);
        if (callTurboFilters == FilterReply.NEUTRAL) {
            return this.effectiveLevelInt <= 5000;
        }
        if (callTurboFilters == FilterReply.DENY) {
            return false;
        }
        if (callTurboFilters == FilterReply.ACCEPT) {
            return true;
        }
        throw new IllegalStateException("Unknown FilterReply value: " + callTurboFilters);
    }

    public boolean isWarnEnabled() {
        return isWarnEnabled((Marker) null);
    }

    public boolean isWarnEnabled(Marker marker) {
        FilterReply callTurboFilters = callTurboFilters(marker, Level.WARN);
        if (callTurboFilters == FilterReply.NEUTRAL) {
            return this.effectiveLevelInt <= 30000;
        }
        if (callTurboFilters == FilterReply.DENY) {
            return false;
        }
        if (callTurboFilters == FilterReply.ACCEPT) {
            return true;
        }
        throw new IllegalStateException("Unknown FilterReply value: " + callTurboFilters);
    }

    public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() {
        AppenderAttachableImpl<ILoggingEvent> appenderAttachableImpl = this.aai;
        return appenderAttachableImpl == null ? Collections.EMPTY_LIST.iterator() : appenderAttachableImpl.iteratorForAppenders();
    }

    public void log(Marker marker, String str, int i, String str2, Object[] objArr, Throwable th) {
        filterAndLog_0_Or3Plus(str, marker, Level.fromLocationAwareLoggerInteger(i), str2, objArr, th);
    }

    /* access modifiers changed from: protected */
    public Object readResolve() throws ObjectStreamException {
        return LoggerFactory.getLogger(getName());
    }

    /* access modifiers changed from: package-private */
    public void recursiveReset() {
        detachAndStopAllAppenders();
        localLevelReset();
        this.additive = true;
        List<Logger> list = this.childrenList;
        if (list != null) {
            Iterator it = new CopyOnWriteArrayList(list).iterator();
            while (it.hasNext()) {
                ((Logger) it.next()).recursiveReset();
            }
        }
    }

    public void setAdditive(boolean z) {
        this.additive = z;
    }

    public synchronized void setLevel(Level level2) {
        if (this.level != level2) {
            if (level2 == null) {
                if (isRootLogger()) {
                    throw new IllegalArgumentException("The level of the root logger cannot be set to null");
                }
            }
            this.level = level2;
            if (level2 == null) {
                this.effectiveLevelInt = this.parent.effectiveLevelInt;
                level2 = this.parent.getEffectiveLevel();
            } else {
                this.effectiveLevelInt = level2.levelInt;
            }
            if (this.childrenList != null) {
                int size = this.childrenList.size();
                for (int i = 0; i < size; i++) {
                    this.childrenList.get(i).handleParentLevelChange(this.effectiveLevelInt);
                }
            }
            this.loggerContext.fireOnLevelChange(this, level2);
        }
    }

    public String toString() {
        return "Logger[" + this.name + "]";
    }

    public void trace(String str) {
        filterAndLog_0_Or3Plus(FQCN, (Marker) null, Level.TRACE, str, (Object[]) null, (Throwable) null);
    }

    public void trace(String str, Object obj) {
        filterAndLog_1(FQCN, (Marker) null, Level.TRACE, str, obj, (Throwable) null);
    }

    public void trace(String str, Object obj, Object obj2) {
        filterAndLog_2(FQCN, (Marker) null, Level.TRACE, str, obj, obj2, (Throwable) null);
    }

    public void trace(String str, Throwable th) {
        filterAndLog_0_Or3Plus(FQCN, (Marker) null, Level.TRACE, str, (Object[]) null, th);
    }

    public void trace(String str, Object[] objArr) {
        filterAndLog_0_Or3Plus(FQCN, (Marker) null, Level.TRACE, str, objArr, (Throwable) null);
    }

    public void trace(Marker marker, String str) {
        filterAndLog_0_Or3Plus(FQCN, marker, Level.TRACE, str, (Object[]) null, (Throwable) null);
    }

    public void trace(Marker marker, String str, Object obj) {
        filterAndLog_1(FQCN, marker, Level.TRACE, str, obj, (Throwable) null);
    }

    public void trace(Marker marker, String str, Object obj, Object obj2) {
        filterAndLog_2(FQCN, marker, Level.TRACE, str, obj, obj2, (Throwable) null);
    }

    public void trace(Marker marker, String str, Throwable th) {
        filterAndLog_0_Or3Plus(FQCN, marker, Level.TRACE, str, (Object[]) null, th);
    }

    public void trace(Marker marker, String str, Object[] objArr) {
        filterAndLog_0_Or3Plus(FQCN, marker, Level.TRACE, str, objArr, (Throwable) null);
    }

    public void warn(String str) {
        filterAndLog_0_Or3Plus(FQCN, (Marker) null, Level.WARN, str, (Object[]) null, (Throwable) null);
    }

    public void warn(String str, Object obj) {
        filterAndLog_1(FQCN, (Marker) null, Level.WARN, str, obj, (Throwable) null);
    }

    public void warn(String str, Object obj, Object obj2) {
        filterAndLog_2(FQCN, (Marker) null, Level.WARN, str, obj, obj2, (Throwable) null);
    }

    public void warn(String str, Throwable th) {
        filterAndLog_0_Or3Plus(FQCN, (Marker) null, Level.WARN, str, (Object[]) null, th);
    }

    public void warn(String str, Object[] objArr) {
        filterAndLog_0_Or3Plus(FQCN, (Marker) null, Level.WARN, str, objArr, (Throwable) null);
    }

    public void warn(Marker marker, String str) {
        filterAndLog_0_Or3Plus(FQCN, marker, Level.WARN, str, (Object[]) null, (Throwable) null);
    }

    public void warn(Marker marker, String str, Object obj) {
        filterAndLog_1(FQCN, marker, Level.WARN, str, obj, (Throwable) null);
    }

    public void warn(Marker marker, String str, Object obj, Object obj2) {
        filterAndLog_2(FQCN, marker, Level.WARN, str, obj, obj2, (Throwable) null);
    }

    public void warn(Marker marker, String str, Throwable th) {
        filterAndLog_0_Or3Plus(FQCN, marker, Level.WARN, str, (Object[]) null, th);
    }

    public void warn(Marker marker, String str, Object[] objArr) {
        filterAndLog_0_Or3Plus(FQCN, marker, Level.WARN, str, objArr, (Throwable) null);
    }
}
