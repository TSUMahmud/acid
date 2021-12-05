package p005ch.qos.logback.core.rolling.helper;

import java.io.File;
import java.util.Date;
import p005ch.qos.logback.core.pattern.Converter;
import p005ch.qos.logback.core.pattern.LiteralConverter;
import p005ch.qos.logback.core.spi.ContextAwareBase;

/* renamed from: ch.qos.logback.core.rolling.helper.DefaultArchiveRemover */
public abstract class DefaultArchiveRemover extends ContextAwareBase implements ArchiveRemover {
    protected static final long INACTIVITY_TOLERANCE_IN_MILLIS = 5529600000L;
    static final int MAX_VALUE_FOR_INACTIVITY_PERIODS = 336;
    protected static final long UNINITIALIZED = -1;
    final FileNamePattern fileNamePattern;
    long lastHeartBeat = -1;
    final boolean parentClean;
    int periodOffsetForDeletionTarget;

    /* renamed from: rc */
    final RollingCalendar f59rc;

    public DefaultArchiveRemover(FileNamePattern fileNamePattern2, RollingCalendar rollingCalendar) {
        this.fileNamePattern = fileNamePattern2;
        this.f59rc = rollingCalendar;
        this.parentClean = computeParentCleaningFlag(fileNamePattern2);
    }

    private void removeFolderIfEmpty(File file, int i) {
        if (i < 3 && file.isDirectory() && FileFilterUtil.isEmptyDirectory(file)) {
            addInfo("deleting folder [" + file + "]");
            file.delete();
            removeFolderIfEmpty(file.getParentFile(), i + 1);
        }
    }

    public void clean(Date date) {
        long time = date.getTime();
        int computeElapsedPeriodsSinceLastClean = computeElapsedPeriodsSinceLastClean(time);
        this.lastHeartBeat = time;
        if (computeElapsedPeriodsSinceLastClean > 1) {
            addInfo("periodsElapsed = " + computeElapsedPeriodsSinceLastClean);
        }
        for (int i = 0; i < computeElapsedPeriodsSinceLastClean; i++) {
            cleanByPeriodOffset(date, this.periodOffsetForDeletionTarget - i);
        }
    }

    /* access modifiers changed from: package-private */
    public abstract void cleanByPeriodOffset(Date date, int i);

    /* access modifiers changed from: package-private */
    public int computeElapsedPeriodsSinceLastClean(long j) {
        long j2 = this.lastHeartBeat;
        long j3 = 336;
        if (j2 == -1) {
            addInfo("first clean up after appender initialization");
            long periodsElapsed = this.f59rc.periodsElapsed(j, INACTIVITY_TOLERANCE_IN_MILLIS + j);
            if (periodsElapsed <= 336) {
                j3 = periodsElapsed;
            }
        } else {
            j3 = this.f59rc.periodsElapsed(j2, j);
            if (j3 < 1) {
                addWarn("Unexpected periodsElapsed value " + j3);
                j3 = 1;
            }
        }
        return (int) j3;
    }

    /* access modifiers changed from: package-private */
    public boolean computeParentCleaningFlag(FileNamePattern fileNamePattern2) {
        if (fileNamePattern2.getPrimaryDateTokenConverter().getDatePattern().indexOf(47) != -1) {
            return true;
        }
        Converter<Object> converter = fileNamePattern2.headTokenConverter;
        while (converter != null && !(converter instanceof DateTokenConverter)) {
            converter = converter.getNext();
        }
        while (converter != null) {
            if ((converter instanceof LiteralConverter) && converter.convert(null).indexOf(47) != -1) {
                return true;
            }
            converter = converter.getNext();
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void removeFolderIfEmpty(File file) {
        removeFolderIfEmpty(file, 0);
    }

    public void setMaxHistory(int i) {
        this.periodOffsetForDeletionTarget = (-i) - 1;
    }
}
