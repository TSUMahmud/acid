package p005ch.qos.logback.core.rolling;

import java.io.File;
import java.util.Date;
import p005ch.qos.logback.core.rolling.helper.TimeBasedArchiveRemover;

/* renamed from: ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy */
public class DefaultTimeBasedFileNamingAndTriggeringPolicy<E> extends TimeBasedFileNamingAndTriggeringPolicyBase<E> {
    public boolean isTriggeringEvent(File file, E e) {
        long currentTime = getCurrentTime();
        if (currentTime < this.nextCheck) {
            return false;
        }
        Date date = this.dateInCurrentPeriod;
        addInfo("Elapsed period: " + date);
        this.elapsedPeriodsFileName = this.tbrp.fileNamePatternWCS.convert(date);
        setDateInCurrentPeriod(currentTime);
        computeNextCheck();
        return true;
    }

    public void start() {
        super.start();
        this.archiveRemover = new TimeBasedArchiveRemover(this.tbrp.fileNamePattern, this.f57rc);
        this.archiveRemover.setContext(this.context);
        this.started = true;
    }

    public String toString() {
        return "c.q.l.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy";
    }
}
