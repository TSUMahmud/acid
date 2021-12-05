package p005ch.qos.logback.core.joran.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.xml.sax.Attributes;
import p005ch.qos.logback.core.joran.spi.ActionException;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.joran.spi.JoranException;
import p005ch.qos.logback.core.util.Loader;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.core.joran.action.AbstractIncludeAction */
public abstract class AbstractIncludeAction extends Action {
    private static final String FILE_ATTR = "file";
    private static final String OPTIONAL_ATTR = "optional";
    private static final String RESOURCE_ATTR = "resource";
    private static final String URL_ATTR = "url";
    private String attributeInUse;
    private boolean optional;
    private URL urlInUse;

    private URL attributeToURL(String str) {
        StringBuilder sb;
        String str2;
        try {
            URL url = new URL(str);
            url.openStream().close();
            return url;
        } catch (MalformedURLException e) {
            e = e;
            if (this.optional) {
                return null;
            }
            sb = new StringBuilder();
            sb.append("URL [");
            sb.append(str);
            str2 = "] is not well formed.";
            sb.append(str2);
            handleError(sb.toString(), e);
            return null;
        } catch (IOException e2) {
            e = e2;
            if (this.optional) {
                return null;
            }
            sb = new StringBuilder();
            sb.append("URL [");
            sb.append(str);
            str2 = "] cannot be opened.";
            sb.append(str2);
            handleError(sb.toString(), e);
            return null;
        }
    }

    private boolean checkAttributes(Attributes attributes) {
        String format;
        String value = attributes.getValue("file");
        String value2 = attributes.getValue(URL_ATTR);
        String value3 = attributes.getValue(RESOURCE_ATTR);
        int i = !OptionHelper.isEmpty(value) ? 1 : 0;
        if (!OptionHelper.isEmpty(value2)) {
            i++;
        }
        if (!OptionHelper.isEmpty(value3)) {
            i++;
        }
        if (i == 0) {
            format = String.format("One of \"%1$s\", \"%2$s\" or \"%3$s\" attributes must be set.", new Object[]{"file", RESOURCE_ATTR, URL_ATTR});
        } else if (i > 1) {
            format = String.format("Only one of \"%1$s\", \"%2$s\" or \"%3$s\" attributes should be set.", new Object[]{"file", RESOURCE_ATTR, URL_ATTR});
        } else if (i == 1) {
            return true;
        } else {
            throw new IllegalStateException("Count value [" + i + "] is not expected");
        }
        handleError(format, (Exception) null);
        return false;
    }

    private URL filePathAsURL(String str) {
        File file = new File(str);
        if (!file.exists() || !file.isFile()) {
            if (!this.optional) {
                handleError("File does not exist [" + str + "]", new FileNotFoundException(str));
            }
            return null;
        }
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private URL getInputURL(InterpretationContext interpretationContext, Attributes attributes) {
        String value = attributes.getValue("file");
        String value2 = attributes.getValue(URL_ATTR);
        String value3 = attributes.getValue(RESOURCE_ATTR);
        if (!OptionHelper.isEmpty(value)) {
            this.attributeInUse = interpretationContext.subst(value);
            return filePathAsURL(this.attributeInUse);
        } else if (!OptionHelper.isEmpty(value2)) {
            this.attributeInUse = interpretationContext.subst(value2);
            return attributeToURL(this.attributeInUse);
        } else if (!OptionHelper.isEmpty(value3)) {
            this.attributeInUse = interpretationContext.subst(value3);
            return resourceAsURL(this.attributeInUse);
        } else {
            throw new IllegalStateException("A URL stream should have been returned");
        }
    }

    private URL resourceAsURL(String str) {
        URL resourceBySelfClassLoader = Loader.getResourceBySelfClassLoader(str);
        if (resourceBySelfClassLoader != null) {
            return resourceBySelfClassLoader;
        }
        if (!this.optional) {
            handleError("Could not find resource corresponding to [" + str + "]", (Exception) null);
        }
        return null;
    }

    public void begin(InterpretationContext interpretationContext, String str, Attributes attributes) throws ActionException {
        this.attributeInUse = null;
        this.optional = OptionHelper.toBoolean(attributes.getValue(OPTIONAL_ATTR), false);
        if (checkAttributes(attributes)) {
            try {
                URL inputURL = getInputURL(interpretationContext, attributes);
                if (inputURL != null) {
                    processInclude(interpretationContext, inputURL);
                }
            } catch (JoranException e) {
                handleError("Error while parsing " + this.attributeInUse, e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public void end(InterpretationContext interpretationContext, String str) throws ActionException {
    }

    /* access modifiers changed from: protected */
    public String getAttributeInUse() {
        return this.attributeInUse;
    }

    public URL getUrl() {
        return this.urlInUse;
    }

    /* access modifiers changed from: protected */
    public void handleError(String str, Exception exc) {
        addError(str, exc);
    }

    /* access modifiers changed from: protected */
    public boolean isOptional() {
        return this.optional;
    }

    /* access modifiers changed from: protected */
    public abstract void processInclude(InterpretationContext interpretationContext, URL url) throws JoranException;
}
