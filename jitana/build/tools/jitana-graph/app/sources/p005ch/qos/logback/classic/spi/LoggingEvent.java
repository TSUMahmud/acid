package p005ch.qos.logback.classic.spi;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.MDCAdapter;
import p005ch.qos.logback.classic.Level;
import p005ch.qos.logback.classic.Logger;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.classic.util.LogbackMDCAdapter;

/* renamed from: ch.qos.logback.classic.spi.LoggingEvent */
public class LoggingEvent implements ILoggingEvent {
    private static final Map<String, String> CACHED_NULL_MAP = new HashMap();
    private transient Object[] argumentArray;
    private StackTraceElement[] callerDataArray;
    transient String formattedMessage;
    transient String fqnOfLoggerClass;
    private transient Level level;
    private LoggerContext loggerContext;
    private LoggerContextVO loggerContextVO;
    private String loggerName;
    private Marker marker;
    private Map<String, String> mdcPropertyMap;
    private String message;
    private String threadName;
    private ThrowableProxy throwableProxy;
    private long timeStamp;

    public LoggingEvent() {
    }

    public LoggingEvent(String str, Logger logger, Level level2, String str2, Throwable th, Object[] objArr) {
        this.fqnOfLoggerClass = str;
        this.loggerName = logger.getName();
        this.loggerContext = logger.getLoggerContext();
        this.loggerContextVO = this.loggerContext.getLoggerContextRemoteView();
        this.level = level2;
        this.message = str2;
        this.argumentArray = objArr;
        th = th == null ? extractThrowableAnRearrangeArguments(objArr) : th;
        if (th != null) {
            this.throwableProxy = new ThrowableProxy(th);
            if (logger.getLoggerContext().isPackagingDataEnabled()) {
                this.throwableProxy.calculatePackagingData();
            }
        }
        this.timeStamp = System.currentTimeMillis();
    }

    private Throwable extractThrowableAnRearrangeArguments(Object[] objArr) {
        Throwable extractThrowable = EventArgUtil.extractThrowable(objArr);
        if (EventArgUtil.successfulExtraction(extractThrowable)) {
            this.argumentArray = EventArgUtil.trimmedCopy(objArr);
        }
        return extractThrowable;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        throw new UnsupportedOperationException(getClass() + " does not support serialization. " + "Use LoggerEventVO instance instead. See also LoggerEventVO.build method.");
    }

    public Object[] getArgumentArray() {
        return this.argumentArray;
    }

    public StackTraceElement[] getCallerData() {
        if (this.callerDataArray == null) {
            this.callerDataArray = CallerData.extract(new Throwable(), this.fqnOfLoggerClass, this.loggerContext.getMaxCallerDataDepth(), this.loggerContext.getFrameworkPackages());
        }
        return this.callerDataArray;
    }

    public long getContextBirthTime() {
        return this.loggerContextVO.getBirthTime();
    }

    public String getFormattedMessage() {
        String str = this.formattedMessage;
        if (str != null) {
            return str;
        }
        Object[] objArr = this.argumentArray;
        this.formattedMessage = objArr != null ? MessageFormatter.arrayFormat(this.message, objArr).getMessage() : this.message;
        return this.formattedMessage;
    }

    public Level getLevel() {
        return this.level;
    }

    public LoggerContextVO getLoggerContextVO() {
        return this.loggerContextVO;
    }

    public String getLoggerName() {
        return this.loggerName;
    }

    public Map<String, String> getMDCPropertyMap() {
        if (this.mdcPropertyMap == null) {
            MDCAdapter mDCAdapter = MDC.getMDCAdapter();
            this.mdcPropertyMap = mDCAdapter instanceof LogbackMDCAdapter ? ((LogbackMDCAdapter) mDCAdapter).getPropertyMap() : mDCAdapter.getCopyOfContextMap();
        }
        if (this.mdcPropertyMap == null) {
            this.mdcPropertyMap = CACHED_NULL_MAP;
        }
        return this.mdcPropertyMap;
    }

    public Marker getMarker() {
        return this.marker;
    }

    public Map<String, String> getMdc() {
        return getMDCPropertyMap();
    }

    public String getMessage() {
        return this.message;
    }

    public String getThreadName() {
        if (this.threadName == null) {
            this.threadName = Thread.currentThread().getName();
        }
        return this.threadName;
    }

    public IThrowableProxy getThrowableProxy() {
        return this.throwableProxy;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public boolean hasCallerData() {
        return this.callerDataArray != null;
    }

    public void prepareForDeferredProcessing() {
        getFormattedMessage();
        getThreadName();
        getMDCPropertyMap();
    }

    public void setArgumentArray(Object[] objArr) {
        if (this.argumentArray == null) {
            this.argumentArray = objArr;
            return;
        }
        throw new IllegalStateException("argArray has been already set");
    }

    public void setCallerData(StackTraceElement[] stackTraceElementArr) {
        this.callerDataArray = stackTraceElementArr;
    }

    public void setLevel(Level level2) {
        if (this.level == null) {
            this.level = level2;
            return;
        }
        throw new IllegalStateException("The level has been already set for this event.");
    }

    public void setLoggerContextRemoteView(LoggerContextVO loggerContextVO2) {
        this.loggerContextVO = loggerContextVO2;
    }

    public void setLoggerName(String str) {
        this.loggerName = str;
    }

    public void setMDCPropertyMap(Map<String, String> map) {
        if (this.mdcPropertyMap == null) {
            this.mdcPropertyMap = map;
            return;
        }
        throw new IllegalStateException("The MDCPropertyMap has been already set for this event.");
    }

    public void setMarker(Marker marker2) {
        if (this.marker == null) {
            this.marker = marker2;
            return;
        }
        throw new IllegalStateException("The marker has been already set for this event.");
    }

    public void setMessage(String str) {
        if (this.message == null) {
            this.message = str;
            return;
        }
        throw new IllegalStateException("The message for this event has been set already.");
    }

    public void setThreadName(String str) throws IllegalStateException {
        if (this.threadName == null) {
            this.threadName = str;
            return;
        }
        throw new IllegalStateException("threadName has been already set");
    }

    public void setThrowableProxy(ThrowableProxy throwableProxy2) {
        if (this.throwableProxy == null) {
            this.throwableProxy = throwableProxy2;
            return;
        }
        throw new IllegalStateException("ThrowableProxy has been already set.");
    }

    public void setTimeStamp(long j) {
        this.timeStamp = j;
    }

    public String toString() {
        return '[' + this.level + "] " + getFormattedMessage();
    }
}
