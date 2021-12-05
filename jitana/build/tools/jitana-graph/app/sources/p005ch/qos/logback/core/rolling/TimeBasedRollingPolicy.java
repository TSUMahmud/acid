package p005ch.qos.logback.core.rolling;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.rolling.helper.ArchiveRemover;
import p005ch.qos.logback.core.rolling.helper.AsynchronousCompressor;
import p005ch.qos.logback.core.rolling.helper.CompressionMode;
import p005ch.qos.logback.core.rolling.helper.Compressor;
import p005ch.qos.logback.core.rolling.helper.FileFilterUtil;
import p005ch.qos.logback.core.rolling.helper.FileNamePattern;
import p005ch.qos.logback.core.rolling.helper.RenameUtil;

/* renamed from: ch.qos.logback.core.rolling.TimeBasedRollingPolicy */
public class TimeBasedRollingPolicy<E> extends RollingPolicyBase implements TriggeringPolicy<E> {
    static final String FNP_NOT_SET = "The FileNamePattern option must be set before using TimeBasedRollingPolicy. ";
    static final int INFINITE_HISTORY = 0;
    private ArchiveRemover archiveRemover;
    boolean cleanHistoryOnStart = false;
    private Compressor compressor;
    FileNamePattern fileNamePatternWCS;
    Future<?> future;
    private int maxHistory = 0;
    private RenameUtil renameUtil = new RenameUtil();
    TimeBasedFileNamingAndTriggeringPolicy<E> timeBasedFileNamingAndTriggeringPolicy;

    private String transformFileNamePattern2ZipEntry(String str) {
        return FileFilterUtil.afterLastSlash(FileFilterUtil.slashify(str));
    }

    private void waitForAsynchronousJobToStop() {
        String str;
        Future<?> future2 = this.future;
        if (future2 != null) {
            try {
                future2.get(30, TimeUnit.SECONDS);
                return;
            } catch (TimeoutException e) {
                e = e;
                str = "Timeout while waiting for compression job to finish";
            } catch (Exception e2) {
                e = e2;
                str = "Unexpected exception while waiting for compression job to finish";
            }
        } else {
            return;
        }
        addError(str, e);
    }

    /* access modifiers changed from: package-private */
    public Future asyncCompress(String str, String str2, String str3) throws RolloverFailure {
        return new AsynchronousCompressor(this.compressor).compressAsynchronously(str, str2, str3);
    }

    public String getActiveFileName() {
        String parentsRawFileProperty = getParentsRawFileProperty();
        return parentsRawFileProperty != null ? parentsRawFileProperty : this.timeBasedFileNamingAndTriggeringPolicy.getCurrentPeriodsFileNameWithoutCompressionSuffix();
    }

    public int getMaxHistory() {
        return this.maxHistory;
    }

    public TimeBasedFileNamingAndTriggeringPolicy<E> getTimeBasedFileNamingAndTriggeringPolicy() {
        return this.timeBasedFileNamingAndTriggeringPolicy;
    }

    public boolean isCleanHistoryOnStart() {
        return this.cleanHistoryOnStart;
    }

    public boolean isTriggeringEvent(File file, E e) {
        return this.timeBasedFileNamingAndTriggeringPolicy.isTriggeringEvent(file, e);
    }

    /* access modifiers changed from: package-private */
    public Future renamedRawAndAsyncCompress(String str, String str2) throws RolloverFailure {
        String parentsRawFileProperty = getParentsRawFileProperty();
        String str3 = parentsRawFileProperty + System.nanoTime() + ".tmp";
        this.renameUtil.rename(parentsRawFileProperty, str3);
        return asyncCompress(str3, str, str2);
    }

    public void rollover() throws RolloverFailure {
        String elapsedPeriodsFileName = this.timeBasedFileNamingAndTriggeringPolicy.getElapsedPeriodsFileName();
        String afterLastSlash = FileFilterUtil.afterLastSlash(elapsedPeriodsFileName);
        if (this.compressionMode != CompressionMode.NONE) {
            this.future = getParentsRawFileProperty() == null ? asyncCompress(elapsedPeriodsFileName, elapsedPeriodsFileName, afterLastSlash) : renamedRawAndAsyncCompress(elapsedPeriodsFileName, afterLastSlash);
        } else if (getParentsRawFileProperty() != null) {
            this.renameUtil.rename(getParentsRawFileProperty(), elapsedPeriodsFileName);
        }
        ArchiveRemover archiveRemover2 = this.archiveRemover;
        if (archiveRemover2 != null) {
            archiveRemover2.clean(new Date(this.timeBasedFileNamingAndTriggeringPolicy.getCurrentTime()));
        }
    }

    public void setCleanHistoryOnStart(boolean z) {
        this.cleanHistoryOnStart = z;
    }

    public void setMaxHistory(int i) {
        this.maxHistory = i;
    }

    public void setTimeBasedFileNamingAndTriggeringPolicy(TimeBasedFileNamingAndTriggeringPolicy<E> timeBasedFileNamingAndTriggeringPolicy2) {
        this.timeBasedFileNamingAndTriggeringPolicy = timeBasedFileNamingAndTriggeringPolicy2;
    }

    public void start() {
        this.renameUtil.setContext(this.context);
        if (this.fileNamePatternStr != null) {
            this.fileNamePattern = new FileNamePattern(this.fileNamePatternStr, this.context);
            determineCompressionMode();
            this.compressor = new Compressor(this.compressionMode);
            this.compressor.setContext(this.context);
            this.fileNamePatternWCS = new FileNamePattern(Compressor.computeFileNameStr_WCS(this.fileNamePatternStr, this.compressionMode), this.context);
            addInfo("Will use the pattern " + this.fileNamePatternWCS + " for the active file");
            if (this.compressionMode == CompressionMode.ZIP) {
                this.zipEntryFileNamePattern = new FileNamePattern(transformFileNamePattern2ZipEntry(this.fileNamePatternStr), this.context);
            }
            if (this.timeBasedFileNamingAndTriggeringPolicy == null) {
                this.timeBasedFileNamingAndTriggeringPolicy = new DefaultTimeBasedFileNamingAndTriggeringPolicy();
            }
            this.timeBasedFileNamingAndTriggeringPolicy.setContext(this.context);
            this.timeBasedFileNamingAndTriggeringPolicy.setTimeBasedRollingPolicy(this);
            this.timeBasedFileNamingAndTriggeringPolicy.start();
            if (this.maxHistory != 0) {
                this.archiveRemover = this.timeBasedFileNamingAndTriggeringPolicy.getArchiveRemover();
                this.archiveRemover.setMaxHistory(this.maxHistory);
                if (this.cleanHistoryOnStart) {
                    addInfo("Cleaning on start up");
                    this.archiveRemover.clean(new Date(this.timeBasedFileNamingAndTriggeringPolicy.getCurrentTime()));
                }
            }
            super.start();
            return;
        }
        addWarn(FNP_NOT_SET);
        addWarn(CoreConstants.SEE_FNP_NOT_SET);
        throw new IllegalStateException("The FileNamePattern option must be set before using TimeBasedRollingPolicy. See also http://logback.qos.ch/codes.html#tbr_fnp_not_set");
    }

    public void stop() {
        if (isStarted()) {
            waitForAsynchronousJobToStop();
            super.stop();
        }
    }

    public String toString() {
        return "c.q.l.core.rolling.TimeBasedRollingPolicy";
    }
}
