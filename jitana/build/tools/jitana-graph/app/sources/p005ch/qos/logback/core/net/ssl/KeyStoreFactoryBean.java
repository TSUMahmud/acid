package p005ch.qos.logback.core.net.ssl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import p005ch.qos.logback.core.util.LocationUtil;

/* renamed from: ch.qos.logback.core.net.ssl.KeyStoreFactoryBean */
public class KeyStoreFactoryBean {
    private String location;
    private String password;
    private String provider;
    private String type;

    private KeyStore newKeyStore() throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException {
        return getProvider() != null ? KeyStore.getInstance(getType(), getProvider()) : KeyStore.getInstance(getType());
    }

    public KeyStore createKeyStore() throws NoSuchProviderException, NoSuchAlgorithmException, KeyStoreException {
        if (getLocation() != null) {
            InputStream inputStream = null;
            try {
                inputStream = LocationUtil.urlForResource(getLocation()).openStream();
                KeyStore newKeyStore = newKeyStore();
                newKeyStore.load(inputStream, getPassword().toCharArray());
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                }
                return newKeyStore;
            } catch (NoSuchProviderException e2) {
                throw new NoSuchProviderException("no such keystore provider: " + getProvider());
            } catch (NoSuchAlgorithmException e3) {
                throw new NoSuchAlgorithmException("no such keystore type: " + getType());
            } catch (FileNotFoundException e4) {
                throw new KeyStoreException(getLocation() + ": file not found");
            } catch (Exception e5) {
                throw new KeyStoreException(getLocation() + ": " + e5.getMessage(), e5);
            } catch (Throwable th) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e6) {
                        e6.printStackTrace(System.err);
                    }
                }
                throw th;
            }
        } else {
            throw new IllegalArgumentException("location is required");
        }
    }

    public String getLocation() {
        return this.location;
    }

    public String getPassword() {
        String str = this.password;
        return str == null ? SSL.DEFAULT_KEYSTORE_PASSWORD : str;
    }

    public String getProvider() {
        return this.provider;
    }

    public String getType() {
        String str = this.type;
        return str == null ? SSL.DEFAULT_KEYSTORE_TYPE : str;
    }

    public void setLocation(String str) {
        this.location = str;
    }

    public void setPassword(String str) {
        this.password = str;
    }

    public void setProvider(String str) {
        this.provider = str;
    }

    public void setType(String str) {
        this.type = str;
    }
}
