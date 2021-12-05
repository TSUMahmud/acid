package p005ch.qos.logback.core.joran;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import org.xml.sax.InputSource;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.joran.event.SaxEvent;
import p005ch.qos.logback.core.joran.event.SaxEventRecorder;
import p005ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import p005ch.qos.logback.core.joran.spi.ElementPath;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.joran.spi.Interpreter;
import p005ch.qos.logback.core.joran.spi.JoranException;
import p005ch.qos.logback.core.joran.spi.RuleStore;
import p005ch.qos.logback.core.joran.spi.SimpleRuleStore;
import p005ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.status.StatusUtil;

/* renamed from: ch.qos.logback.core.joran.GenericConfigurator */
public abstract class GenericConfigurator extends ContextAwareBase {
    protected Interpreter interpreter;

    private final void doConfigure(InputSource inputSource) throws JoranException {
        long currentTimeMillis = System.currentTimeMillis();
        if (!ConfigurationWatchListUtil.wasConfigurationWatchListReset(this.context)) {
            informContextOfURLUsedForConfiguration(getContext(), (URL) null);
        }
        SaxEventRecorder saxEventRecorder = new SaxEventRecorder(this.context);
        saxEventRecorder.recordEvents(inputSource);
        doConfigure(saxEventRecorder.getSaxEventList());
        if (new StatusUtil(this.context).noXMLParsingErrorsOccurred(currentTimeMillis)) {
            addInfo("Registering current configuration as safe fallback point");
            registerSafeConfiguration();
        }
    }

    public static void informContextOfURLUsedForConfiguration(Context context, URL url) {
        ConfigurationWatchListUtil.setMainWatchURL(context, url);
    }

    /* access modifiers changed from: protected */
    public void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry defaultNestedComponentRegistry) {
    }

    /* access modifiers changed from: protected */
    public abstract void addImplicitRules(Interpreter interpreter2);

    /* access modifiers changed from: protected */
    public abstract void addInstanceRules(RuleStore ruleStore);

    /* access modifiers changed from: protected */
    public void buildInterpreter() {
        SimpleRuleStore simpleRuleStore = new SimpleRuleStore(this.context);
        addInstanceRules(simpleRuleStore);
        this.interpreter = new Interpreter(this.context, simpleRuleStore, initialElementPath());
        InterpretationContext interpretationContext = this.interpreter.getInterpretationContext();
        interpretationContext.setContext(this.context);
        addImplicitRules(this.interpreter);
        addDefaultNestedComponentRegistryRules(interpretationContext.getDefaultNestedComponentRegistry());
    }

    public final void doConfigure(File file) throws JoranException {
        try {
            informContextOfURLUsedForConfiguration(getContext(), file.toURI().toURL());
            doConfigure((InputStream) new FileInputStream(file));
        } catch (IOException e) {
            String str = "Could not open [" + file.getPath() + "].";
            addError(str, e);
            throw new JoranException(str, e);
        }
    }

    public final void doConfigure(InputStream inputStream) throws JoranException {
        try {
            doConfigure(new InputSource(inputStream));
            try {
            } catch (IOException e) {
                addError("Could not close the stream", e);
                throw new JoranException("Could not close the stream", e);
            }
        } finally {
            try {
                inputStream.close();
            } catch (IOException e2) {
                addError("Could not close the stream", e2);
                throw new JoranException("Could not close the stream", e2);
            }
        }
    }

    public final void doConfigure(String str) throws JoranException {
        doConfigure(new File(str));
    }

    public final void doConfigure(URL url) throws JoranException {
        try {
            informContextOfURLUsedForConfiguration(getContext(), url);
            URLConnection openConnection = url.openConnection();
            openConnection.setUseCaches(false);
            doConfigure(openConnection.getInputStream());
        } catch (IOException e) {
            String str = "Could not open URL [" + url + "].";
            addError(str, e);
            throw new JoranException(str, e);
        }
    }

    public void doConfigure(List<SaxEvent> list) throws JoranException {
        buildInterpreter();
        synchronized (this.context.getConfigurationLock()) {
            this.interpreter.getEventPlayer().play(list);
        }
    }

    /* access modifiers changed from: protected */
    public ElementPath initialElementPath() {
        return new ElementPath();
    }

    public List<SaxEvent> recallSafeConfiguration() {
        return (List) this.context.getObject(CoreConstants.SAFE_JORAN_CONFIGURATION);
    }

    public void registerSafeConfiguration() {
        this.context.putObject(CoreConstants.SAFE_JORAN_CONFIGURATION, this.interpreter.getEventPlayer().getCopyOfPlayerEventList());
    }
}
