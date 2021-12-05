package p005ch.qos.logback.core.net.ssl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

/* renamed from: ch.qos.logback.core.net.ssl.SecureRandomFactoryBean */
public class SecureRandomFactoryBean {
    private String algorithm;
    private String provider;

    public SecureRandom createSecureRandom() throws NoSuchProviderException, NoSuchAlgorithmException {
        try {
            return getProvider() != null ? SecureRandom.getInstance(getAlgorithm(), getProvider()) : SecureRandom.getInstance(getAlgorithm());
        } catch (NoSuchProviderException e) {
            throw new NoSuchProviderException("no such secure random provider: " + getProvider());
        } catch (NoSuchAlgorithmException e2) {
            throw new NoSuchAlgorithmException("no such secure random algorithm: " + getAlgorithm());
        }
    }

    public String getAlgorithm() {
        String str = this.algorithm;
        return str == null ? SSL.DEFAULT_SECURE_RANDOM_ALGORITHM : str;
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
