package p005ch.qos.logback.classic.net;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import p005ch.qos.logback.core.net.ssl.ConfigurableSSLSocketFactory;
import p005ch.qos.logback.core.net.ssl.SSLComponent;
import p005ch.qos.logback.core.net.ssl.SSLConfiguration;
import p005ch.qos.logback.core.net.ssl.SSLParametersConfiguration;

/* renamed from: ch.qos.logback.classic.net.SSLSocketReceiver */
public class SSLSocketReceiver extends SocketReceiver implements SSLComponent {
    private SocketFactory socketFactory;
    private SSLConfiguration ssl;

    /* access modifiers changed from: protected */
    public SocketFactory getSocketFactory() {
        return this.socketFactory;
    }

    public SSLConfiguration getSsl() {
        if (this.ssl == null) {
            this.ssl = new SSLConfiguration();
        }
        return this.ssl;
    }

    public void setSsl(SSLConfiguration sSLConfiguration) {
        this.ssl = sSLConfiguration;
    }

    /* access modifiers changed from: protected */
    public boolean shouldStart() {
        try {
            SSLContext createContext = getSsl().createContext(this);
            SSLParametersConfiguration parameters = getSsl().getParameters();
            parameters.setContext(getContext());
            this.socketFactory = new ConfigurableSSLSocketFactory(parameters, createContext.getSocketFactory());
            return super.shouldStart();
        } catch (Exception e) {
            addError(e.getMessage(), e);
            return false;
        }
    }
}
