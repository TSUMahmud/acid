package p005ch.qos.logback.classic.turbo;

import java.io.File;
import java.net.URL;
import java.util.List;
import org.slf4j.Marker;
import p005ch.qos.logback.classic.Level;
import p005ch.qos.logback.classic.Logger;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.classic.joran.JoranConfigurator;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.joran.event.SaxEvent;
import p005ch.qos.logback.core.joran.spi.ConfigurationWatchList;
import p005ch.qos.logback.core.joran.spi.JoranException;
import p005ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import p005ch.qos.logback.core.spi.FilterReply;
import p005ch.qos.logback.core.status.StatusUtil;

/* renamed from: ch.qos.logback.classic.turbo.ReconfigureOnChangeFilter */
public class ReconfigureOnChangeFilter extends TurboFilter {
    public static final long DEFAULT_REFRESH_PERIOD = 60000;
    private static final long MASK_DECREASE_THRESHOLD = 800;
    private static final long MASK_INCREASE_THRESHOLD = 100;
    private static final int MAX_MASK = 65535;
    ConfigurationWatchList configurationWatchList;
    private long invocationCounter = 0;
    private volatile long lastMaskCheck = System.currentTimeMillis();
    URL mainConfigurationURL;
    private volatile long mask = 15;
    protected volatile long nextCheck;
    long refreshPeriod = 60000;

    /* renamed from: ch.qos.logback.classic.turbo.ReconfigureOnChangeFilter$ReconfiguringThread */
    class ReconfiguringThread implements Runnable {
        ReconfiguringThread() {
        }

        private void fallbackConfiguration(LoggerContext loggerContext, List<SaxEvent> list, URL url) {
            JoranConfigurator joranConfigurator = new JoranConfigurator();
            joranConfigurator.setContext(ReconfigureOnChangeFilter.this.context);
            if (list != null) {
                ReconfigureOnChangeFilter.this.addWarn("Falling back to previously registered safe configuration.");
                try {
                    loggerContext.reset();
                    JoranConfigurator.informContextOfURLUsedForConfiguration(ReconfigureOnChangeFilter.this.context, url);
                    joranConfigurator.doConfigure(list);
                    ReconfigureOnChangeFilter.this.addInfo("Re-registering previous fallback configuration once more as a fallback configuration point");
                    joranConfigurator.registerSafeConfiguration();
                } catch (JoranException e) {
                    ReconfigureOnChangeFilter.this.addError("Unexpected exception thrown by a configuration considered safe.", e);
                }
            } else {
                ReconfigureOnChangeFilter.this.addWarn("No previous configuration to fall back on.");
            }
        }

        private void performXMLConfiguration(LoggerContext loggerContext) {
            JoranConfigurator joranConfigurator = new JoranConfigurator();
            joranConfigurator.setContext(ReconfigureOnChangeFilter.this.context);
            StatusUtil statusUtil = new StatusUtil(ReconfigureOnChangeFilter.this.context);
            List<SaxEvent> recallSafeConfiguration = joranConfigurator.recallSafeConfiguration();
            URL mainWatchURL = ConfigurationWatchListUtil.getMainWatchURL(ReconfigureOnChangeFilter.this.context);
            loggerContext.reset();
            long currentTimeMillis = System.currentTimeMillis();
            try {
                joranConfigurator.doConfigure(ReconfigureOnChangeFilter.this.mainConfigurationURL);
                if (statusUtil.hasXMLParsingErrors(currentTimeMillis)) {
                    fallbackConfiguration(loggerContext, recallSafeConfiguration, mainWatchURL);
                }
            } catch (JoranException e) {
                fallbackConfiguration(loggerContext, recallSafeConfiguration, mainWatchURL);
            }
        }

        public void run() {
            if (ReconfigureOnChangeFilter.this.mainConfigurationURL == null) {
                ReconfigureOnChangeFilter.this.addInfo("Due to missing top level configuration file, skipping reconfiguration");
                return;
            }
            LoggerContext loggerContext = (LoggerContext) ReconfigureOnChangeFilter.this.context;
            ReconfigureOnChangeFilter reconfigureOnChangeFilter = ReconfigureOnChangeFilter.this;
            reconfigureOnChangeFilter.addInfo("Will reset and reconfigure context named [" + ReconfigureOnChangeFilter.this.context.getName() + "]");
            if (ReconfigureOnChangeFilter.this.mainConfigurationURL.toString().endsWith("xml")) {
                performXMLConfiguration(loggerContext);
            }
        }
    }

    private void updateMaskIfNecessary(long j) {
        long j2;
        long j3 = j - this.lastMaskCheck;
        this.lastMaskCheck = j;
        if (j3 < MASK_INCREASE_THRESHOLD && this.mask < 65535) {
            j2 = (this.mask << 1) | 1;
        } else if (j3 > MASK_DECREASE_THRESHOLD) {
            j2 = this.mask >>> 2;
        } else {
            return;
        }
        this.mask = j2;
    }

    /* access modifiers changed from: protected */
    public boolean changeDetected(long j) {
        if (j < this.nextCheck) {
            return false;
        }
        updateNextCheck(j);
        return this.configurationWatchList.changeDetected();
    }

    public FilterReply decide(Marker marker, Logger logger, Level level, String str, Object[] objArr, Throwable th) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }
        long j = this.invocationCounter;
        this.invocationCounter = 1 + j;
        if ((j & this.mask) != this.mask) {
            return FilterReply.NEUTRAL;
        }
        long currentTimeMillis = System.currentTimeMillis();
        synchronized (this.configurationWatchList) {
            updateMaskIfNecessary(currentTimeMillis);
            if (changeDetected(currentTimeMillis)) {
                disableSubsequentReconfiguration();
                detachReconfigurationToNewThread();
            }
        }
        return FilterReply.NEUTRAL;
    }

    /* access modifiers changed from: package-private */
    public void detachReconfigurationToNewThread() {
        addInfo("Detected change in [" + this.configurationWatchList.getCopyOfFileWatchList() + "]");
        this.context.getExecutorService().submit(new ReconfiguringThread());
    }

    /* access modifiers changed from: package-private */
    public void disableSubsequentReconfiguration() {
        this.nextCheck = Long.MAX_VALUE;
    }

    public long getRefreshPeriod() {
        return this.refreshPeriod;
    }

    public void setRefreshPeriod(long j) {
        this.refreshPeriod = j;
    }

    public void start() {
        this.configurationWatchList = ConfigurationWatchListUtil.getConfigurationWatchList(this.context);
        ConfigurationWatchList configurationWatchList2 = this.configurationWatchList;
        if (configurationWatchList2 != null) {
            this.mainConfigurationURL = configurationWatchList2.getMainURL();
            if (this.mainConfigurationURL == null) {
                addWarn("Due to missing top level configuration file, automatic reconfiguration is impossible.");
                return;
            }
            List<File> copyOfFileWatchList = this.configurationWatchList.getCopyOfFileWatchList();
            addInfo("Will scan for changes in [" + copyOfFileWatchList + "] every " + (this.refreshPeriod / 1000) + " seconds. ");
            synchronized (this.configurationWatchList) {
                updateNextCheck(System.currentTimeMillis());
            }
            super.start();
            return;
        }
        addWarn("Empty ConfigurationWatchList in context");
    }

    public String toString() {
        return "ReconfigureOnChangeFilter{invocationCounter=" + this.invocationCounter + CoreConstants.CURLY_RIGHT;
    }

    /* access modifiers changed from: package-private */
    public void updateNextCheck(long j) {
        this.nextCheck = j + this.refreshPeriod;
    }
}
