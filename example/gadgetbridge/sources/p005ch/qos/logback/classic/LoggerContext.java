package p005ch.qos.logback.classic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.Marker;
import p005ch.qos.logback.classic.android.AndroidManifestPropertiesUtil;
import p005ch.qos.logback.classic.spi.LoggerComparator;
import p005ch.qos.logback.classic.spi.LoggerContextListener;
import p005ch.qos.logback.classic.spi.LoggerContextVO;
import p005ch.qos.logback.classic.spi.TurboFilterList;
import p005ch.qos.logback.classic.turbo.TurboFilter;
import p005ch.qos.logback.classic.util.LoggerNameUtil;
import p005ch.qos.logback.core.ContextBase;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.joran.spi.JoranException;
import p005ch.qos.logback.core.spi.FilterReply;
import p005ch.qos.logback.core.spi.LifeCycle;
import p005ch.qos.logback.core.status.Status;
import p005ch.qos.logback.core.status.StatusListener;
import p005ch.qos.logback.core.status.StatusManager;
import p005ch.qos.logback.core.status.WarnStatus;

/* renamed from: ch.qos.logback.classic.LoggerContext */
public class LoggerContext extends ContextBase implements ILoggerFactory, LifeCycle {
    private boolean androidPropsInitialized = false;
    private List<String> frameworkPackages;
    private Map<String, Logger> loggerCache = new ConcurrentHashMap();
    private final List<LoggerContextListener> loggerContextListenerList = new ArrayList();
    private LoggerContextVO loggerContextRemoteView = new LoggerContextVO(this);
    private int maxCallerDataDepth = 8;
    private int noAppenderWarning = 0;
    private boolean packagingDataEnabled = true;
    int resetCount = 0;
    final Logger root = new Logger(Logger.ROOT_LOGGER_NAME, (Logger) null, this);
    private int size;
    private final TurboFilterList turboFilterList = new TurboFilterList();

    public LoggerContext() {
        this.root.setLevel(Level.DEBUG);
        this.loggerCache.put(Logger.ROOT_LOGGER_NAME, this.root);
        initEvaluatorMap();
        this.size = 1;
        this.frameworkPackages = new ArrayList();
    }

    private void fireOnReset() {
        for (LoggerContextListener onReset : this.loggerContextListenerList) {
            onReset.onReset(this);
        }
    }

    private void fireOnStart() {
        for (LoggerContextListener onStart : this.loggerContextListenerList) {
            onStart.onStart(this);
        }
    }

    private void fireOnStop() {
        for (LoggerContextListener onStop : this.loggerContextListenerList) {
            onStop.onStop(this);
        }
    }

    private void incSize() {
        this.size++;
    }

    private boolean isSpecialKey(String str) {
        return str.equals(CoreConstants.PACKAGE_NAME_KEY) || str.equals(CoreConstants.VERSION_NAME_KEY) || str.equals(CoreConstants.VERSION_CODE_KEY) || str.equals(CoreConstants.EXT_DIR_KEY) || str.equals(CoreConstants.DATA_DIR_KEY);
    }

    private void resetAllListeners() {
        this.loggerContextListenerList.clear();
    }

    private void resetListenersExceptResetResistant() {
        ArrayList arrayList = new ArrayList();
        for (LoggerContextListener next : this.loggerContextListenerList) {
            if (next.isResetResistant()) {
                arrayList.add(next);
            }
        }
        this.loggerContextListenerList.retainAll(arrayList);
    }

    private void resetStatusListeners() {
        StatusManager statusManager = getStatusManager();
        for (StatusListener remove : statusManager.getCopyOfStatusListenerList()) {
            statusManager.remove(remove);
        }
    }

    private void updateLoggerContextVO() {
        this.loggerContextRemoteView = new LoggerContextVO(this);
    }

    public void addListener(LoggerContextListener loggerContextListener) {
        this.loggerContextListenerList.add(loggerContextListener);
    }

    public void addTurboFilter(TurboFilter turboFilter) {
        this.turboFilterList.add(turboFilter);
    }

    public Logger exists(String str) {
        return this.loggerCache.get(str);
    }

    /* access modifiers changed from: package-private */
    public void fireOnLevelChange(Logger logger, Level level) {
        for (LoggerContextListener onLevelChange : this.loggerContextListenerList) {
            onLevelChange.onLevelChange(logger, level);
        }
    }

    public List<LoggerContextListener> getCopyOfListenerList() {
        return new ArrayList(this.loggerContextListenerList);
    }

    public List<String> getFrameworkPackages() {
        return this.frameworkPackages;
    }

    public final Logger getLogger(Class cls) {
        return getLogger(cls.getName());
    }

    public final Logger getLogger(String str) {
        Logger childByName;
        if (str == null) {
            throw new IllegalArgumentException("name argument cannot be null");
        } else if (Logger.ROOT_LOGGER_NAME.equalsIgnoreCase(str)) {
            return this.root;
        } else {
            Logger logger = this.root;
            Logger logger2 = this.loggerCache.get(str);
            if (logger2 != null) {
                return logger2;
            }
            Logger logger3 = logger;
            int i = 0;
            while (true) {
                int separatorIndexOf = LoggerNameUtil.getSeparatorIndexOf(str, i);
                String substring = separatorIndexOf == -1 ? str : str.substring(0, separatorIndexOf);
                int i2 = separatorIndexOf + 1;
                synchronized (logger3) {
                    childByName = logger3.getChildByName(substring);
                    if (childByName == null) {
                        childByName = logger3.createChildByName(substring);
                        this.loggerCache.put(substring, childByName);
                        incSize();
                    }
                }
                if (separatorIndexOf == -1) {
                    return childByName;
                }
                i = i2;
                logger3 = childByName;
            }
        }
    }

    public LoggerContextVO getLoggerContextRemoteView() {
        return this.loggerContextRemoteView;
    }

    public List<Logger> getLoggerList() {
        ArrayList arrayList = new ArrayList(this.loggerCache.values());
        Collections.sort(arrayList, new LoggerComparator());
        return arrayList;
    }

    public int getMaxCallerDataDepth() {
        return this.maxCallerDataDepth;
    }

    public String getProperty(String str) {
        if (isSpecialKey(str)) {
            try {
                if (!this.androidPropsInitialized) {
                    this.androidPropsInitialized = true;
                    AndroidManifestPropertiesUtil.setAndroidProperties(this);
                }
            } catch (JoranException e) {
                getStatusManager().add((Status) new WarnStatus("Can't set manifest properties", e));
                this.androidPropsInitialized = false;
            }
        }
        return super.getProperty(str);
    }

    /* access modifiers changed from: package-private */
    public final FilterReply getTurboFilterChainDecision_0_3OrMore(Marker marker, Logger logger, Level level, String str, Object[] objArr, Throwable th) {
        return this.turboFilterList.size() == 0 ? FilterReply.NEUTRAL : this.turboFilterList.getTurboFilterChainDecision(marker, logger, level, str, objArr, th);
    }

    /* access modifiers changed from: package-private */
    public final FilterReply getTurboFilterChainDecision_1(Marker marker, Logger logger, Level level, String str, Object obj, Throwable th) {
        if (this.turboFilterList.size() == 0) {
            return FilterReply.NEUTRAL;
        }
        return this.turboFilterList.getTurboFilterChainDecision(marker, logger, level, str, new Object[]{obj}, th);
    }

    /* access modifiers changed from: package-private */
    public final FilterReply getTurboFilterChainDecision_2(Marker marker, Logger logger, Level level, String str, Object obj, Object obj2, Throwable th) {
        if (this.turboFilterList.size() == 0) {
            return FilterReply.NEUTRAL;
        }
        return this.turboFilterList.getTurboFilterChainDecision(marker, logger, level, str, new Object[]{obj, obj2}, th);
    }

    public TurboFilterList getTurboFilterList() {
        return this.turboFilterList;
    }

    /* access modifiers changed from: package-private */
    public void initEvaluatorMap() {
        putObject(CoreConstants.EVALUATOR_MAP, new HashMap());
    }

    public boolean isPackagingDataEnabled() {
        return this.packagingDataEnabled;
    }

    /* access modifiers changed from: package-private */
    public final void noAppenderDefinedWarning(Logger logger) {
        int i = this.noAppenderWarning;
        this.noAppenderWarning = i + 1;
        if (i == 0) {
            StatusManager statusManager = getStatusManager();
            statusManager.add((Status) new WarnStatus("No appenders present in context [" + getName() + "] for logger [" + logger.getName() + "].", logger));
        }
    }

    public void putProperty(String str, String str2) {
        super.putProperty(str, str2);
        updateLoggerContextVO();
    }

    public void removeListener(LoggerContextListener loggerContextListener) {
        this.loggerContextListenerList.remove(loggerContextListener);
    }

    public void reset() {
        this.resetCount++;
        super.reset();
        initEvaluatorMap();
        this.root.recursiveReset();
        resetTurboFilterList();
        fireOnReset();
        resetListenersExceptResetResistant();
        resetStatusListeners();
    }

    public void resetTurboFilterList() {
        Iterator it = this.turboFilterList.iterator();
        while (it.hasNext()) {
            ((TurboFilter) it.next()).stop();
        }
        this.turboFilterList.clear();
    }

    public void setMaxCallerDataDepth(int i) {
        this.maxCallerDataDepth = i;
    }

    public void setName(String str) {
        super.setName(str);
        updateLoggerContextVO();
    }

    public void setPackagingDataEnabled(boolean z) {
        this.packagingDataEnabled = z;
    }

    /* access modifiers changed from: package-private */
    public int size() {
        return this.size;
    }

    public void start() {
        super.start();
        fireOnStart();
    }

    public void stop() {
        reset();
        fireOnStop();
        resetAllListeners();
        super.stop();
    }

    public String toString() {
        return getClass().getName() + "[" + getName() + "]";
    }
}
