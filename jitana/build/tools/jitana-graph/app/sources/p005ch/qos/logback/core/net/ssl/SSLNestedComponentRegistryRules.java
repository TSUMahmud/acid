package p005ch.qos.logback.core.net.ssl;

import p005ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;

/* renamed from: ch.qos.logback.core.net.ssl.SSLNestedComponentRegistryRules */
public class SSLNestedComponentRegistryRules {
    public static void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry defaultNestedComponentRegistry) {
        defaultNestedComponentRegistry.add(SSLComponent.class, "ssl", SSLConfiguration.class);
        defaultNestedComponentRegistry.add(SSLConfiguration.class, "parameters", SSLParametersConfiguration.class);
        defaultNestedComponentRegistry.add(SSLConfiguration.class, "keyStore", KeyStoreFactoryBean.class);
        defaultNestedComponentRegistry.add(SSLConfiguration.class, "trustStore", KeyStoreFactoryBean.class);
        defaultNestedComponentRegistry.add(SSLConfiguration.class, "keyManagerFactory", KeyManagerFactoryFactoryBean.class);
        defaultNestedComponentRegistry.add(SSLConfiguration.class, "trustManagerFactory", TrustManagerFactoryFactoryBean.class);
        defaultNestedComponentRegistry.add(SSLConfiguration.class, "secureRandom", SecureRandomFactoryBean.class);
    }
}
