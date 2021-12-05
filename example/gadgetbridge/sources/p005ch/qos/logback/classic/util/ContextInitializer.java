package p005ch.qos.logback.classic.util;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import p005ch.qos.logback.classic.LoggerContext;
import p005ch.qos.logback.classic.joran.JoranConfigurator;
import p005ch.qos.logback.core.android.CommonPathUtil;
import p005ch.qos.logback.core.joran.spi.JoranException;
import p005ch.qos.logback.core.status.InfoStatus;
import p005ch.qos.logback.core.status.Status;
import p005ch.qos.logback.core.status.StatusManager;
import p005ch.qos.logback.core.util.Loader;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.classic.util.ContextInitializer */
public class ContextInitializer {
    private static final String ASSETS_DIR = CommonPathUtil.getAssetsDirectoryPath();
    public static final String AUTOCONFIG_FILE = "logback.xml";
    public static final String CONFIG_FILE_PROPERTY = "logback.configurationFile";
    public static final String STATUS_LISTENER_CLASS = "logback.statusListenerClass";
    final ClassLoader classLoader = Loader.getClassLoaderOfObject(this);
    final LoggerContext loggerContext;

    public ContextInitializer(LoggerContext loggerContext2) {
        this.loggerContext = loggerContext2;
    }

    private URL findConfigFileFromSystemProperties(boolean z) {
        URL url;
        String systemProperty = OptionHelper.getSystemProperty(CONFIG_FILE_PROPERTY);
        String str = null;
        if (systemProperty != null) {
            try {
                File file = new File(systemProperty);
                if (!file.exists() || !file.isFile()) {
                    url = new URL(systemProperty);
                } else {
                    if (z) {
                        statusOnResourceSearch(systemProperty, this.classLoader, systemProperty);
                    }
                    url = file.toURI().toURL();
                }
                if (z) {
                    ClassLoader classLoader2 = this.classLoader;
                    if (url != null) {
                        str = url.toString();
                    }
                    statusOnResourceSearch(systemProperty, classLoader2, str);
                }
                return url;
            } catch (MalformedURLException e) {
                URL resource = Loader.getResource(systemProperty, this.classLoader);
                if (resource != null) {
                    if (z) {
                        ClassLoader classLoader3 = this.classLoader;
                        if (resource != null) {
                            str = resource.toString();
                        }
                        statusOnResourceSearch(systemProperty, classLoader3, str);
                    }
                    return resource;
                } else if (z) {
                    statusOnResourceSearch(systemProperty, this.classLoader, resource != null ? resource.toString() : null);
                }
            } catch (Throwable th) {
                if (z) {
                    statusOnResourceSearch(systemProperty, this.classLoader, (String) null);
                }
                throw th;
            }
        }
        return null;
    }

    private InputStream findConfigFileURLFromAssets(boolean z) {
        return getResource(ASSETS_DIR + "/" + AUTOCONFIG_FILE, this.classLoader, z);
    }

    private InputStream getResource(String str, ClassLoader classLoader2, boolean z) {
        InputStream resourceAsStream = classLoader2.getResourceAsStream(str);
        if (z) {
            String str2 = null;
            if (resourceAsStream != null) {
                str2 = str;
            }
            statusOnResourceSearch(str, classLoader2, str2);
        }
        return resourceAsStream;
    }

    private void statusOnResourceSearch(String str, ClassLoader classLoader2, String str2) {
        StatusManager statusManager = this.loggerContext.getStatusManager();
        if (str2 == null) {
            statusManager.add((Status) new InfoStatus("Could NOT find resource [" + str + "]", this.loggerContext));
            return;
        }
        statusManager.add((Status) new InfoStatus("Found resource [" + str + "] at [" + str2 + "]", this.loggerContext));
    }

    public void autoConfig() throws JoranException {
        boolean z;
        InputStream findConfigFileURLFromAssets;
        StatusListenerConfigHelper.installIfAsked(this.loggerContext);
        JoranConfigurator joranConfigurator = new JoranConfigurator();
        joranConfigurator.setContext(this.loggerContext);
        URL findConfigFileFromSystemProperties = findConfigFileFromSystemProperties(true);
        if (findConfigFileFromSystemProperties != null) {
            joranConfigurator.doConfigure(findConfigFileFromSystemProperties);
            z = true;
        } else {
            z = false;
        }
        if (!z && (findConfigFileURLFromAssets = findConfigFileURLFromAssets(true)) != null) {
            joranConfigurator.doConfigure(findConfigFileURLFromAssets);
        }
    }
}
