package p005ch.qos.logback.core.rolling;

import java.io.File;
import java.util.Date;
import p005ch.qos.logback.core.rolling.helper.ArchiveRemover;
import p005ch.qos.logback.core.rolling.helper.DateTokenConverter;
import p005ch.qos.logback.core.rolling.helper.RollingCalendar;
import p005ch.qos.logback.core.spi.ContextAwareBase;

/* renamed from: ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicyBase */
public abstract class TimeBasedFileNamingAndTriggeringPolicyBase<E> extends ContextAwareBase implements TimeBasedFileNamingAndTriggeringPolicy<E> {
    protected ArchiveRemover archiveRemover = null;
    protected long artificialCurrentTime = -1;
    protected Date dateInCurrentPeriod = null;
    protected String elapsedPeriodsFileName;
    protected long nextCheck;

    /* renamed from: rc */
    protected RollingCalendar f57rc;
    protected boolean started = false;
    protected TimeBasedRollingPolicy<E> tbrp;

    /* access modifiers changed from: protected */
    public void computeNextCheck() {
        this.nextCheck = this.f57rc.getNextTriggeringMillis(this.dateInCurrentPeriod);
    }

    public ArchiveRemover getArchiveRemover() {
        return this.archiveRemover;
    }

    public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
        return this.tbrp.fileNamePatternWCS.convert(this.dateInCurrentPeriod);
    }

    public long getCurrentTime() {
        long j = this.artificialCurrentTime;
        return j >= 0 ? j : System.currentTimeMillis();
    }

    public String getElapsedPeriodsFileName() {
        return this.elapsedPeriodsFileName;
    }

    public boolean isStarted() {
        return this.started;
    }

    public void setCurrentTime(long j) {
        this.artificialCurrentTime = j;
    }

    /* access modifiers changed from: protected */
    public void setDateInCurrentPeriod(long j) {
        this.dateInCurrentPeriod.setTime(j);
    }

    public void setDateInCurrentPeriod(Date date) {
        this.dateInCurrentPeriod = date;
    }

    public void setTimeBasedRollingPolicy(TimeBasedRollingPolicy<E> timeBasedRollingPolicy) {
        this.tbrp = timeBasedRollingPolicy;
    }

    public void start() {
        DateTokenConverter primaryDateTokenConverter = this.tbrp.fileNamePattern.getPrimaryDateTokenConverter();
        if (primaryDateTokenConverter != null) {
            this.f57rc = new RollingCalendar();
            this.f57rc.init(primaryDateTokenConverter.getDatePattern());
            addInfo("The date pattern is '" + primaryDateTokenConverter.getDatePattern() + "' from file name pattern '" + this.tbrp.fileNamePattern.getPattern() + "'.");
            this.f57rc.printPeriodicity(this);
            setDateInCurrentPeriod(new Date(getCurrentTime()));
            if (this.tbrp.getParentsRawFileProperty() != null) {
                File file = new File(this.tbrp.getParentsRawFileProperty());
                if (file.exists() && file.canRead()) {
                    setDateInCurrentPeriod(new Date(file.lastModified()));
                }
            }
            addInfo("Setting initial period to " + this.dateInCurrentPeriod);
            computeNextCheck();
            return;
        }
        throw new IllegalStateException("FileNamePattern [" + this.tbrp.fileNamePattern.getPattern() + "] does not contain a valid DateToken");
    }

    public void stop() {
        this.started = false;
    }
}
