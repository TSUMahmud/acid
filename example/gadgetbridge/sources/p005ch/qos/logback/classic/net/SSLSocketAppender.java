package p005ch.qos.logback.classic.net;

import java.net.InetAddress;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.net.AbstractSSLSocketAppender;
import p005ch.qos.logback.core.spi.PreSerializationTransformer;

/* renamed from: ch.qos.logback.classic.net.SSLSocketAppender */
public class SSLSocketAppender extends AbstractSSLSocketAppender<ILoggingEvent> {
    private boolean includeCallerData;
    private final PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformer();

    public SSLSocketAppender() {
    }

    @Deprecated
    public SSLSocketAppender(String str, int i) {
        super(str, i);
    }

    @Deprecated
    public SSLSocketAppender(InetAddress inetAddress, int i) {
        super(inetAddress.getHostAddress(), i);
    }

    public PreSerializationTransformer<ILoggingEvent> getPST() {
        return this.pst;
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
