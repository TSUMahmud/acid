package p005ch.qos.logback.core.net.ssl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.net.ssl.KeyManagerFactory;

/* renamed from: ch.qos.logback.core.net.ssl.KeyManagerFactoryFactoryBean */
public class KeyManagerFactoryFactoryBean {
    private String algorithm;
    private String provider;

    public KeyManagerFactory createKeyManagerFactory() throws NoSuchProviderException, NoSuchAlgorithmException {
        return getProvider() != null ? KeyManagerFactory.getInstance(getAlgorithm(), getProvider()) : KeyManagerFactory.getInstance(getAlgorithm());
    }

    public String getAlgorithm() {
        String str = this.algorithm;
        return str == null ? KeyManagerFactory.getDefaultAlgorithm() : str;
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
