package p005ch.qos.logback.classic.net.server;

import p005ch.qos.logback.classic.net.LoggingEventPreSerializationTransformer;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.net.server.AbstractServerSocketAppender;
import p005ch.qos.logback.core.spi.PreSerializationTransformer;

/* renamed from: ch.qos.logback.classic.net.server.ServerSocketAppender */
public class ServerSocketAppender extends AbstractServerSocketAppender<ILoggingEvent> {
    private static final PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformer();
    private boolean includeCallerData;

    /* access modifiers changed from: protected */
    public PreSerializationTransformer<ILoggingEvent> getPST() {
        return pst;
    }

    public boolean isIncludeCallerData() {
        return this.includeCallerData;
    }

    /* access modifiers changed from: protected */
    public void postProcessEvent(ILoggingEvent iLoggingEvent) {
        if (isIncludeCallerData()) {
            iLoggingEvent.getCallerData();
        }
    }

    public void setIncludeCallerData(boolean z) {
        this.includeCallerData = z;
    }
}
