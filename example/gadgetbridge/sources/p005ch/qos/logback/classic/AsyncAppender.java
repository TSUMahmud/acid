package p005ch.qos.logback.classic;

import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.AsyncAppenderBase;

/* renamed from: ch.qos.logback.classic.AsyncAppender */
public class AsyncAppender extends AsyncAppenderBase<ILoggingEvent> {
    boolean includeCallerData = false;

    /* access modifiers changed from: protected */
    public boolean isDiscardable(ILoggingEvent iLoggingEvent) {
        return iLoggingEvent.getLevel().toInt() <= 20000;
    }

    public boolean isIncludeCallerData() {
        return this.includeCallerData;
    }

    /* access modifiers changed from: protected */
    public void preprocess(ILoggingEvent iLoggingEvent) {
        iLoggingEvent.prepareForDeferredProcessing();
        if (this.includeCallerData) {
            iLoggingEvent.getCallerData();
        }
    }

    public void setIncludeCallerData(boolean z) {
        this.includeCallerData = z;
    }
}
