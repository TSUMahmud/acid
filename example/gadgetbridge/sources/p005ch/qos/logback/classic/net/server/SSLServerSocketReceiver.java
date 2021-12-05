package p005ch.qos.logback.classic.net.server;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import p005ch.qos.logback.core.net.ssl.ConfigurableSSLServerSocketFactory;
import p005ch.qos.logback.core.net.ssl.SSLComponent;
import p005ch.qos.logback.core.net.ssl.SSLConfiguration;
import p005ch.qos.logback.core.net.ssl.SSLParametersConfiguration;

/* renamed from: ch.qos.logback.classic.net.server.SSLServerSocketReceiver */
public class SSLServerSocketReceiver extends ServerSocketReceiver implements SSLComponent {
    private ServerSocketFactory socketFactory;
    private SSLConfiguration ssl;

    /* access modifiers changed from: protected */
    public ServerSocketFactory getServerSocketFactory() throws Exception {
        if (this.socketFactory == null) {
            SSLContext createContext = getSsl().createContext(this);
            SSLParametersConfiguration parameters = getSsl().getParameters();
            parameters.setContext(getContext());
            this.socketFactory = new ConfigurableSSLServerSocketFactory(parameters, createContext.getServerSocketFactory());
        }
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
}
