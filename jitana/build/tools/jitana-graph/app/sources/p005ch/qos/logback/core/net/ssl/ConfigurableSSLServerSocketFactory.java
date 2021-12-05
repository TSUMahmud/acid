package p005ch.qos.logback.core.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

/* renamed from: ch.qos.logback.core.net.ssl.ConfigurableSSLServerSocketFactory */
public class ConfigurableSSLServerSocketFactory extends ServerSocketFactory {
    private final SSLServerSocketFactory delegate;
    private final SSLParametersConfiguration parameters;

    public ConfigurableSSLServerSocketFactory(SSLParametersConfiguration sSLParametersConfiguration, SSLServerSocketFactory sSLServerSocketFactory) {
        this.parameters = sSLParametersConfiguration;
        this.delegate = sSLServerSocketFactory;
    }

    public ServerSocket createServerSocket(int i) throws IOException {
        SSLServerSocket sSLServerSocket = (SSLServerSocket) this.delegate.createServerSocket(i);
        this.parameters.configure(new SSLConfigurableServerSocket(sSLServerSocket));
        return sSLServerSocket;
    }

    public ServerSocket createServerSocket(int i, int i2) throws IOException {
        SSLServerSocket sSLServerSocket = (SSLServerSocket) this.delegate.createServerSocket(i, i2);
        this.parameters.configure(new SSLConfigurableServerSocket(sSLServerSocket));
        return sSLServerSocket;
    }

    public ServerSocket createServerSocket(int i, int i2, InetAddress inetAddress) throws IOException {
        SSLServerSocket sSLServerSocket = (SSLServerSocket) this.delegate.createServerSocket(i, i2, inetAddress);
        this.parameters.configure(new SSLConfigurableServerSocket(sSLServerSocket));
        return sSLServerSocket;
    }
}
