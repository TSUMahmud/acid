package p005ch.qos.logback.core.net.ssl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.util.OptionHelper;
import p005ch.qos.logback.core.util.StringCollectionUtil;

/* renamed from: ch.qos.logback.core.net.ssl.SSLParametersConfiguration */
public class SSLParametersConfiguration extends ContextAwareBase {
    private String[] enabledCipherSuites;
    private String[] enabledProtocols;
    private String excludedCipherSuites;
    private String excludedProtocols;
    private String includedCipherSuites;
    private String includedProtocols;
    private Boolean needClientAuth;
    private Boolean wantClientAuth;

    private String[] enabledCipherSuites(String[] strArr, String[] strArr2) {
        if (this.enabledCipherSuites == null) {
            if (!OptionHelper.isEmpty(getIncludedCipherSuites()) || !OptionHelper.isEmpty(getExcludedCipherSuites())) {
                this.enabledCipherSuites = includedStrings(strArr, getIncludedCipherSuites(), getExcludedCipherSuites());
            } else {
                this.enabledCipherSuites = (String[]) Arrays.copyOf(strArr2, strArr2.length);
            }
            for (String str : this.enabledCipherSuites) {
                addInfo("enabled cipher suite: " + str);
            }
        }
        return this.enabledCipherSuites;
    }

    private String[] enabledProtocols(String[] strArr, String[] strArr2) {
        if (this.enabledProtocols == null) {
            if (!OptionHelper.isEmpty(getIncludedProtocols()) || !OptionHelper.isEmpty(getExcludedProtocols())) {
                this.enabledProtocols = includedStrings(strArr, getIncludedProtocols(), getExcludedProtocols());
            } else {
                this.enabledProtocols = (String[]) Arrays.copyOf(strArr2, strArr2.length);
            }
            for (String str : this.enabledProtocols) {
                addInfo("enabled protocol: " + str);
            }
        }
        return this.enabledProtocols;
    }

    private String[] includedStrings(String[] strArr, String str, String str2) {
        ArrayList arrayList = new ArrayList(strArr.length);
        arrayList.addAll(Arrays.asList(strArr));
        if (str != null) {
            StringCollectionUtil.retainMatching((Collection<String>) arrayList, stringToArray(str));
        }
        if (str2 != null) {
            StringCollectionUtil.removeMatching((Collection<String>) arrayList, stringToArray(str2));
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    private String[] stringToArray(String str) {
        return str.split("\\s*,\\s*");
    }

    public void configure(SSLConfigurable sSLConfigurable) {
        sSLConfigurable.setEnabledProtocols(enabledProtocols(sSLConfigurable.getSupportedProtocols(), sSLConfigurable.getDefaultProtocols()));
        sSLConfigurable.setEnabledCipherSuites(enabledCipherSuites(sSLConfigurable.getSupportedCipherSuites(), sSLConfigurable.getDefaultCipherSuites()));
        if (isNeedClientAuth() != null) {
            sSLConfigurable.setNeedClientAuth(isNeedClientAuth().booleanValue());
        }
        if (isWantClientAuth() != null) {
            sSLConfigurable.setWantClientAuth(isWantClientAuth().booleanValue());
        }
    }

    public String getExcludedCipherSuites() {
        return this.excludedCipherSuites;
    }

    public String getExcludedProtocols() {
        return this.excludedProtocols;
    }

    public String getIncludedCipherSuites() {
        return this.includedCipherSuites;
    }

    public String getIncludedProtocols() {
        return this.includedProtocols;
    }

    public Boolean isNeedClientAuth() {
        return this.needClientAuth;
    }

    public Boolean isWantClientAuth() {
        return this.wantClientAuth;
    }

    public void setExcludedCipherSuites(String str) {
        this.excludedCipherSuites = str;
    }

    public void setExcludedProtocols(String str) {
        this.excludedProtocols = str;
    }

    public void setIncludedCipherSuites(String str) {
        this.includedCipherSuites = str;
    }

    public void setIncludedProtocols(String str) {
        this.includedProtocols = str;
    }

    public void setNeedClientAuth(Boolean bool) {
        this.needClientAuth = bool;
    }

    public void setWantClientAuth(Boolean bool) {
        this.wantClientAuth = bool;
    }
}
