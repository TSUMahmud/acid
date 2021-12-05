package p005ch.qos.logback.core.net.ssl;

/* renamed from: ch.qos.logback.core.net.ssl.SSLConfigurable */
public interface SSLConfigurable {
    String[] getDefaultCipherSuites();

    String[] getDefaultProtocols();

    String[] getSupportedCipherSuites();

    String[] getSupportedProtocols();

    void setEnabledCipherSuites(String[] strArr);

    void setEnabledProtocols(String[] strArr);

    void setNeedClientAuth(boolean z);

    void setWantClientAuth(boolean z);
}
