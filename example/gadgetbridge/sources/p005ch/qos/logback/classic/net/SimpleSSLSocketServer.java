package p005ch.qos.logback.classic.net;

import java.security.NoSuchAlgorithmException;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.core.net.ssl.ConfigurableSSLServerSocketFactory;
import p005ch.qos.logback.core.net.ssl.SSLParametersConfiguration;

/* renamed from: ch.qos.logback.classic.net.SimpleSSLSocketServer */
public class SimpleSSLSocketServer extends SimpleSocketServer {
    private final ServerSocketFactory socketFactory;

    public SimpleSSLSocketServer(LoggerContext loggerContext, int i) throws NoSuchAlgorithmException {
        this(loggerContext, i, SSLContext.getDefault());
    }

    public SimpleSSLSocketServer(LoggerContext loggerContext, int i, SSLContext sSLContext) {
        super(loggerContext, i);
        if (sSLContext != null) {
            SSLParametersConfiguration sSLParametersConfiguration = new SSLParametersConfiguration();
            sSLParametersConfiguration.setContext(loggerContext);
            this.socketFactory = new ConfigurableSSLServerSocketFactory(sSLParametersConfiguration, sSLContext.getServerSocketFactory());
            return;
        }
        throw new NullPointerException("SSL context required");
    }

    public static void main(String[] strArr) throws Exception {
        doMain(SimpleSSLSocketServer.class, strArr);
    }

    /* access modifiers changed from: protected */
    public ServerSocketFactory getServerSocketFactory() {
        return this.socketFactory;
    }
}
