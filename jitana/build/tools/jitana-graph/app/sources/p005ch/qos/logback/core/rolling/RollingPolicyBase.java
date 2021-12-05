package p005ch.qos.logback.core.rolling;

import p005ch.qos.logback.core.FileAppender;
import p005ch.qos.logback.core.rolling.helper.CompressionMode;
import p005ch.qos.logback.core.rolling.helper.FileNamePattern;
import p005ch.qos.logback.core.spi.ContextAwareBase;

/* renamed from: ch.qos.logback.core.rolling.RollingPolicyBase */
public abstract class RollingPolicyBase extends ContextAwareBase implements RollingPolicy {
    protected CompressionMode compressionMode = CompressionMode.NONE;
    FileNamePattern fileNamePattern;
    protected String fileNamePatternStr;
    private FileAppender parent;
    private boolean started;
    FileNamePattern zipEntryFileNamePattern;

    /* access modifiers changed from: protected */
    public void determineCompressionMode() {
        CompressionMode compressionMode2;
        if (this.fileNamePatternStr.endsWith(".gz")) {
            addInfo("Will use gz compression");
            compressionMode2 = CompressionMode.GZ;
        } else if (this.fileNamePatternStr.endsWith(".zip")) {
            addInfo("Will use zip compression");
            compressionMode2 = CompressionMode.ZIP;
        } else {
            addInfo("No compression will be used");
            compressionMode2 = CompressionMode.NONE;
        }
        this.compressionMode = compressionMode2;
    }

    public CompressionMode getCompressionMode() {
        return this.compressionMode;
    }

    public String getFileNamePattern() {
        return this.fileNamePatternStr;
    }

    public String getParentsRawFileProperty() {
        return this.parent.rawFileProperty();
    }

    public boolean isParentPrudent() {
        return this.parent.isPrudent();
    }

    public boolean isStarted() {
        return this.started;
    }

    public void setFileNamePattern(String str) {
        this.fileNamePatternStr = str;
    }

    public void setParent(FileAppender fileAppender) {
        this.parent = fileAppender;
    }

    public void start() {
        this.started = true;
    }

    public void stop() {
        this.started = false;
    }
}
