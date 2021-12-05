package p005ch.qos.logback.core.rolling;

import java.io.File;
import java.util.Date;
import p005ch.qos.logback.core.joran.spi.NoAutoStart;
import p005ch.qos.logback.core.rolling.helper.ArchiveRemover;
import p005ch.qos.logback.core.rolling.helper.CompressionMode;
import p005ch.qos.logback.core.rolling.helper.FileFilterUtil;
import p005ch.qos.logback.core.rolling.helper.SizeAndTimeBasedArchiveRemover;
import p005ch.qos.logback.core.util.FileSize;

@NoAutoStart
/* renamed from: ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP */
public class SizeAndTimeBasedFNATP<E> extends TimeBasedFileNamingAndTriggeringPolicyBase<E> {
    int currentPeriodsCounter = 0;
    private int invocationCounter;
    private int invocationMask = 1;
    FileSize maxFileSize;
    String maxFileSizeAsString;

    private String getFileNameIncludingCompressionSuffix(Date date, int i) {
        return this.tbrp.fileNamePattern.convertMultipleArguments(this.dateInCurrentPeriod, Integer.valueOf(i));
    }

    /* access modifiers changed from: package-private */
    public void computeCurrentPeriodsHighestCounterValue(String str) {
        File[] filesInFolderMatchingStemRegex = FileFilterUtil.filesInFolderMatchingStemRegex(new File(getCurrentPeriodsFileNameWithoutCompressionSuffix()).getParentFile(), str);
        if (filesInFolderMatchingStemRegex == null || filesInFolderMatchingStemRegex.length == 0) {
            this.currentPeriodsCounter = 0;
            return;
        }
        this.currentPeriodsCounter = FileFilterUtil.findHighestCounter(filesInFolderMatchingStemRegex, str);
        if (this.tbrp.getParentsRawFileProperty() != null || this.tbrp.compressionMode != CompressionMode.NONE) {
            this.currentPeriodsCounter++;
        }
    }

    /* access modifiers changed from: protected */
    public ArchiveRemover createArchiveRemover() {
        return new SizeAndTimeBasedArchiveRemover(this.tbrp.fileNamePattern, this.f57rc);
    }

    public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
        return this.tbrp.fileNamePatternWCS.convertMultipleArguments(this.dateInCurrentPeriod, Integer.valueOf(this.currentPeriodsCounter));
    }

    public String getMaxFileSize() {
        return this.maxFileSizeAsString;
    }

    public boolean isTriggeringEvent(File file, E e) {
        long currentTime = getCurrentTime();
        if (currentTime >= this.nextCheck) {
            this.elapsedPeriodsFileName = this.tbrp.fileNamePatternWCS.convertMultipleArguments(this.dateInCurrentPeriod, Integer.valueOf(this.currentPeriodsCounter));
            this.currentPeriodsCounter = 0;
            setDateInCurrentPeriod(currentTime);
            computeNextCheck();
            return true;
        }
        int i = this.invocationCounter + 1;
        this.invocationCounter = i;
        int i2 = this.invocationMask;
        if ((i & i2) != i2) {
            return false;
        }
        if (i2 < 15) {
            this.invocationMask = (i2 << 1) + 1;
        }
        if (file.length() < this.maxFileSize.getSize()) {
            return false;
        }
        this.elapsedPeriodsFileName = this.tbrp.fileNamePatternWCS.convertMultipleArguments(this.dateInCurrentPeriod, Integer.valueOf(this.currentPeriodsCounter));
        this.currentPeriodsCounter++;
        return true;
    }

    public void setMaxFileSize(String str) {
        this.maxFileSizeAsString = str;
        this.maxFileSize = FileSize.valueOf(str);
    }

    public void start() {
        super.start();
        this.archiveRemover = createArchiveRemover();
        this.archiveRemover.setContext(this.context);
        computeCurrentPeriodsHighestCounterValue(FileFilterUtil.afterLastSlash(this.tbrp.fileNamePattern.toRegexForFixedDate(this.dateInCurrentPeriod)));
        this.started = true;
    }
}
