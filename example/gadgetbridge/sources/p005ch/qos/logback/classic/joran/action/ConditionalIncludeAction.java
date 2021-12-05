package p005ch.qos.logback.classic.joran.action;

import java.io.FileNotFoundException;
import java.net.URL;
import java.net.UnknownHostException;
import org.xml.sax.Attributes;
import p005ch.qos.logback.core.joran.action.AbstractIncludeAction;
import p005ch.qos.logback.core.joran.spi.ActionException;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.joran.spi.JoranException;

/* renamed from: ch.qos.logback.classic.joran.action.ConditionalIncludeAction */
public class ConditionalIncludeAction extends AbstractIncludeAction {

    /* renamed from: ch.qos.logback.classic.joran.action.ConditionalIncludeAction$State */
    class State {
        private URL url;

        State() {
        }

        /* access modifiers changed from: package-private */
        public URL getUrl() {
            return this.url;
        }

        /* access modifiers changed from: package-private */
        public void setUrl(URL url2) {
            this.url = url2;
        }
    }

    private URL peekPath(InterpretationContext interpretationContext) {
        URL url;
        if (interpretationContext.isEmpty()) {
            return null;
        }
        Object peekObject = interpretationContext.peekObject();
        if (!(peekObject instanceof State) || (url = ((State) peekObject).getUrl()) == null) {
            return null;
        }
        return url;
    }

    private URL pushPath(InterpretationContext interpretationContext, URL url) {
        State state = new State();
        state.setUrl(url);
        interpretationContext.pushObject(state);
        return url;
    }

    public void begin(InterpretationContext interpretationContext, String str, Attributes attributes) throws ActionException {
        if (peekPath(interpretationContext) == null) {
            super.begin(interpretationContext, str, attributes);
        }
    }

    /* access modifiers changed from: protected */
    public void handleError(String str, Exception exc) {
        if (exc == null || (exc instanceof FileNotFoundException) || (exc instanceof UnknownHostException)) {
            addInfo(str);
        } else {
            addWarn(str, exc);
        }
    }

    /* access modifiers changed from: protected */
    public void processInclude(InterpretationContext interpretationContext, URL url) throws JoranException {
        pushPath(interpretationContext, url);
    }
}
