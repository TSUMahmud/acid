package p005ch.qos.logback.classic.spi;

import java.io.Serializable;
import p005ch.qos.logback.classic.LoggerContext;

/* renamed from: ch.qos.logback.classic.spi.LoggerRemoteView */
public class LoggerRemoteView implements Serializable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final long serialVersionUID = 5028223666108713696L;
    final LoggerContextVO loggerContextView;
    final String name;

    public LoggerRemoteView(String str, LoggerContext loggerContext) {
        this.name = str;
        this.loggerContextView = loggerContext.getLoggerContextRemoteView();
    }

    public LoggerContextVO getLoggerContextView() {
        return this.loggerContextView;
    }

    public String getName() {
        return this.name;
    }
}
