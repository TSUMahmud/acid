package p005ch.qos.logback.core.net;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import p005ch.qos.logback.core.net.ssl.ConfigurableSSLSocketFactory;
import p005ch.qos.logback.core.net.ssl.SSLComponent;
import p005ch.qos.logback.core.net.ssl.SSLConfiguration;
import p005ch.qos.logback.core.net.ssl.SSLParametersConfiguration;

/* renamed from: ch.qos.logback.core.net.AbstractSSLSocketAppender */
public abstract class AbstractSSLSocketAppender<E> extends AbstractSocketAppender<E> implements SSLComponent {
    private SocketFactory socketFactory;
    private SSLConfiguration ssl;

    protected AbstractSSLSocketAppender() {
    }

    @Deprecated
    protected AbstractSSLSocketAppender(String str, int i) {
        super(str, i);
    }

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

    public void start() {
        try {
            SSLContext createContext = getSsl().createContext(this);
            SSLParametersConfiguration parameters = getSsl().getParameters();
            parameters.setContext(getContext());
            this.socketFactory = new ConfigurableSSLSocketFactory(parameters, createContext.getSocketFactory());
            super.start();
        } catch (Exception e) {
            addError(e.getMessage(), e);
        }
    }
}
