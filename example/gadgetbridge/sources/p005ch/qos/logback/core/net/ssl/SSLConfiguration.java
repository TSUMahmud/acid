package p005ch.qos.logback.core.net.ssl;

/* renamed from: ch.qos.logback.core.net.ssl.SSLConfiguration */
public class SSLConfiguration extends SSLContextFactoryBean {
    private SSLParametersConfiguration parameters;

    public SSLParametersConfiguration getParameters() {
        if (this.parameters == null) {
            this.parameters = new SSLParametersConfiguration();
        }
        return this.parameters;
    }

    public void setParameters(SSLParametersConfiguration sSLParametersConfiguration) {
        this.parameters = sSLParametersConfiguration;
    }
}
