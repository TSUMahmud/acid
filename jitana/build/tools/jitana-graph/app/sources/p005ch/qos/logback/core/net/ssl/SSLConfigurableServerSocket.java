package p005ch.qos.logback.core.net.ssl;

import javax.net.ssl.SSLServerSocket;

/* renamed from: ch.qos.logback.core.net.ssl.SSLConfigurableServerSocket */
public class SSLConfigurableServerSocket implements SSLConfigurable {
    private final SSLServerSocket delegate;

    public SSLConfigurableServerSocket(SSLServerSocket sSLServerSocket) {
        this.delegate = sSLServerSocket;
    }

    public String[] getDefaultCipherSuites() {
        return this.delegate.getEnabledCipherSuites();
    }

    public String[] getDefaultProtocols() {
        return this.delegate.getEnabledProtocols();
    }

    public String[] getSupportedCipherSuites() {
        return this.delegate.getSupportedCipherSuites();
    }

    public String[] getSupportedProtocols() {
        return this.delegate.getSupportedProtocols();
    }

    public void setEnabledCipherSuites(String[] strArr) {
        this.delegate.setEnabledCipherSuites(strArr);
    }

    public void setEnabledProtocols(String[] strArr) {
        this.delegate.setEnabledProtocols(strArr);
    }

    public void setNeedClientAuth(boolean z) {
        this.delegate.setNeedClientAuth(z);
    }

    public void setWantClientAuth(boolean z) {
        this.delegate.setWantClientAuth(z);
    }
}
