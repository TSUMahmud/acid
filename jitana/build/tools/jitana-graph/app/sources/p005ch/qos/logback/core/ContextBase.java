package p005ch.qos.logback.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import p005ch.qos.logback.core.spi.LifeCycle;
import p005ch.qos.logback.core.spi.LogbackLock;
import p005ch.qos.logback.core.status.StatusManager;
import p005ch.qos.logback.core.util.ExecutorServiceUtil;

/* renamed from: ch.qos.logback.core.ContextBase */
public class ContextBase implements Context, LifeCycle {
    private long birthTime = System.currentTimeMillis();
    LogbackLock configurationLock = new LogbackLock();
    private volatile ExecutorService executorService;
    private LifeCycleManager lifeCycleManager;
    private String name;
    Map<String, Object> objectMap = new HashMap();
    Map<String, String> propertyMap = new HashMap();

    /* renamed from: sm */
    private StatusManager f50sm = new BasicStatusManager();
    private boolean started;

    private synchronized void stopExecutorService() {
        if (this.executorService != null) {
            ExecutorServiceUtil.shutdown(this.executorService);
            this.executorService = null;
        }
    }

    public long getBirthTime() {
        return this.birthTime;
    }

    public Object getConfigurationLock() {
        return this.configurationLock;
    }

    public Map<String, String> getCopyOfPropertyMap() {
        return new HashMap(this.propertyMap);
    }

    public ExecutorService getExecutorService() {
        if (this.executorService == null) {
            synchronized (this) {
                if (this.executorService == null) {
                    this.executorService = ExecutorServiceUtil.newExecutorService();
                }
            }
        }
        return this.executorService;
    }

    /* access modifiers changed from: package-private */
    public synchronized LifeCycleManager getLifeCycleManager() {
        if (this.lifeCycleManager == null) {
            this.lifeCycleManager = new LifeCycleManager();
        }
        return this.lifeCycleManager;
    }

    public String getName() {
        return this.name;
    }

    public Object getObject(String str) {
        return this.objectMap.get(str);
    }

    public String getProperty(String str) {
        return CoreConstants.CONTEXT_NAME_KEY.equals(str) ? getName() : this.propertyMap.get(str);
    }

    public StatusManager getStatusManager() {
        return this.f50sm;
    }

    public boolean isStarted() {
        return this.started;
    }

    public void putObject(String str, Object obj) {
        this.objectMap.put(str, obj);
    }

    public void putProperty(String str, String str2) {
        this.propertyMap.put(str, str2);
    }

    public void register(LifeCycle lifeCycle) {
        getLifeCycleManager().register(lifeCycle);
    }

    public void reset() {
        getLifeCycleManager().reset();
        this.propertyMap.clear();
        this.objectMap.clear();
    }

    public void setName(String str) throws IllegalStateException {
        if (str == null || !str.equals(this.name)) {
            String str2 = this.name;
            if (str2 == null || "default".equals(str2)) {
                this.name = str;
                return;
            }
            throw new IllegalStateException("Context has been already given a name");
        }
    }

    public void setStatusManager(StatusManager statusManager) {
        if (statusManager != null) {
            this.f50sm = statusManager;
            return;
        }
        throw new IllegalArgumentException("null StatusManager not allowed");
    }

    public void start() {
        this.started = true;
    }

    public void stop() {
        stopExecutorService();
        this.started = false;
    }

    public String toString() {
        return this.name;
    }
}
