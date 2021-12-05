package p005ch.qos.logback.core.net.ssl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.net.ssl.TrustManagerFactory;

/* renamed from: ch.qos.logback.core.net.ssl.TrustManagerFactoryFactoryBean */
public class TrustManagerFactoryFactoryBean {
    private String algorithm;
    private String provider;

    public TrustManagerFactory createTrustManagerFactory() throws NoSuchProviderException, NoSuchAlgorithmException {
        return getProvider() != null ? TrustManagerFactory.getInstance(getAlgorithm(), getProvider()) : TrustManagerFactory.getInstance(getAlgorithm());
    }

    public String getAlgorithm() {
        String str = this.algorithm;
        return str == null ? TrustManagerFactory.getDefaultAlgorithm() : str;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setAlgorithm(String str) {
        this.algorithm = str;
    }

    public void setProvider(String str) {
        this.provider = str;
    }
}
