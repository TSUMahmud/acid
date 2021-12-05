package p005ch.qos.logback.classic.net;

import java.net.InetAddress;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.net.AbstractSocketAppender;
import p005ch.qos.logback.core.spi.PreSerializationTransformer;

/* renamed from: ch.qos.logback.classic.net.SocketAppender */
public class SocketAppender extends AbstractSocketAppender<ILoggingEvent> {
    private static final PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformer();
    private boolean includeCallerData = false;

    public SocketAppender() {
    }

    @Deprecated
    public SocketAppender(String str, int i) {
        super(str, i);
    }

    @Deprecated
    public SocketAppender(InetAddress inetAddress, int i) {
        super(inetAddress.getHostAddress(), i);
    }

    public PreSerializationTransformer<ILoggingEvent> getPST() {
        return pst;
    }

    /* access modifiers changed from: protected */
    public void postProcessEvent(ILoggingEvent iLoggingEvent) {
        if (this.includeCallerData) {
            iLoggingEvent.getCallerData();
        }
    }

    public void setIncludeCallerData(boolean z) {
        this.includeCallerData = z;
    }
}
