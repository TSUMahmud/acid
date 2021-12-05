package p005ch.qos.logback.core.rolling.helper;

import java.io.File;
import java.util.Date;

/* renamed from: ch.qos.logback.core.rolling.helper.SizeAndTimeBasedArchiveRemover */
public class SizeAndTimeBasedArchiveRemover extends DefaultArchiveRemover {
    public SizeAndTimeBasedArchiveRemover(FileNamePattern fileNamePattern, RollingCalendar rollingCalendar) {
        super(fileNamePattern, rollingCalendar);
    }

    public void cleanByPeriodOffset(Date date, int i) {
        Date relativeDate = this.f59rc.getRelativeDate(date, i);
        String afterLastSlash = FileFilterUtil.afterLastSlash(this.fileNamePattern.toRegexForFixedDate(relativeDate));
        File parentFile = new File(this.fileNamePattern.convertMultipleArguments(relativeDate, 0)).getAbsoluteFile().getAbsoluteFile().getParentFile();
        for (File delete : FileFilterUtil.filesInFolderMatchingStemRegex(parentFile, afterLastSlash)) {
            delete.delete();
        }
        if (this.parentClean) {
            removeFolderIfEmpty(parentFile);
        }
    }
}
