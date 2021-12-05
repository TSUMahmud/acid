package p005ch.qos.logback.core.rolling.helper;

import java.io.File;
import java.util.Date;

/* renamed from: ch.qos.logback.core.rolling.helper.TimeBasedArchiveRemover */
public class TimeBasedArchiveRemover extends DefaultArchiveRemover {
    public TimeBasedArchiveRemover(FileNamePattern fileNamePattern, RollingCalendar rollingCalendar) {
        super(fileNamePattern, rollingCalendar);
    }

    /* access modifiers changed from: protected */
    public void cleanByPeriodOffset(Date date, int i) {
        File file = new File(this.fileNamePattern.convert(this.f59rc.getRelativeDate(date, i)));
        if (file.exists() && file.isFile()) {
            file.delete();
            addInfo("deleting " + file);
            if (this.parentClean) {
                removeFolderIfEmpty(file.getParentFile());
            }
        }
    }

    public String toString() {
        return "c.q.l.core.rolling.helper.TimeBasedArchiveRemover";
    }
}
