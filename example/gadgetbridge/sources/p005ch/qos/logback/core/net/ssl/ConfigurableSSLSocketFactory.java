package p005ch.qos.logback.core.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/* renamed from: ch.qos.logback.core.net.ssl.ConfigurableSSLSocketFactory */
public class ConfigurableSSLSocketFactory extends SocketFactory {
    private final SSLSocketFactory delegate;
    private final SSLParametersConfiguration parameters;

    public ConfigurableSSLSocketFactory(SSLParametersConfiguration sSLParametersConfiguration, SSLSocketFactory sSLSocketFactory) {
        this.parameters = sSLParametersConfiguration;
        this.delegate = sSLSocketFactory;
    }

    public Socket createSocket(String str, int i) throws IOException, UnknownHostException {
        SSLSocket sSLSocket = (SSLSocket) this.delegate.createSocket(str, i);
        this.parameters.configure(new SSLConfigurableSocket(sSLSocket));
        return sSLSocket;
    }

    public Socket createSocket(String str, int i, InetAddress inetAddress, int i2) throws IOException, UnknownHostException {
        SSLSocket sSLSocket = (SSLSocket) this.delegate.createSocket(str, i, inetAddress, i2);
        this.parameters.configure(new SSLConfigurableSocket(sSLSocket));
        return sSLSocket;
    }

    public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        SSLSocket sSLSocket = (SSLSocket) this.delegate.createSocket(inetAddress, i);
        this.parameters.configure(new SSLConfigurableSocket(sSLSocket));
        return sSLSocket;
    }

    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress2, int i2) throws IOException {
        SSLSocket sSLSocket = (SSLSocket) this.delegate.createSocket(inetAddress, i, inetAddress2, i2);
        this.parameters.configure(new SSLConfigurableSocket(sSLSocket));
        return sSLSocket;
    }
}
