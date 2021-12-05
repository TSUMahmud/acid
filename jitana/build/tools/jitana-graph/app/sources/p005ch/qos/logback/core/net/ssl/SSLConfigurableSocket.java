package p005ch.qos.logback.core.net.ssl;

import javax.net.ssl.SSLSocket;

/* renamed from: ch.qos.logback.core.net.ssl.SSLConfigurableSocket */
public class SSLConfigurableSocket implements SSLConfigurable {
    private final SSLSocket delegate;

    public SSLConfigurableSocket(SSLSocket sSLSocket) {
        this.delegate = sSLSocket;
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
