package p005ch.qos.logback.core.joran.action;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import p005ch.qos.logback.core.joran.event.SaxEvent;
import p005ch.qos.logback.core.joran.event.SaxEventRecorder;
import p005ch.qos.logback.core.joran.spi.InterpretationContext;
import p005ch.qos.logback.core.joran.spi.JoranException;
import p005ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;

/* renamed from: ch.qos.logback.core.joran.action.IncludeAction */
public class IncludeAction extends AbstractIncludeAction {
    private static final String CONFIG_TAG = "configuration";
    private static final String INCLUDED_TAG = "included";
    private int eventOffset = 2;

    private String getEventName(SaxEvent saxEvent) {
        return saxEvent.qName.length() > 0 ? saxEvent.qName : saxEvent.localName;
    }

    private InputStream openURL(URL url) {
        try {
            return url.openStream();
        } catch (IOException e) {
            if (isOptional()) {
                return null;
            }
            addError("Failed to open [" + url.toString() + "]", e);
            return null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0035, code lost:
        r0 = r0 - 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void trimHeadAndTail(p005ch.qos.logback.core.joran.event.SaxEventRecorder r7) {
        /*
            r6 = this;
            java.util.List r7 = r7.getSaxEventList()
            int r0 = r7.size()
            if (r0 != 0) goto L_0x000b
            return
        L_0x000b:
            r0 = 0
            java.lang.Object r1 = r7.get(r0)
            ch.qos.logback.core.joran.event.SaxEvent r1 = (p005ch.qos.logback.core.joran.event.SaxEvent) r1
            java.lang.String r2 = "configuration"
            java.lang.String r3 = "included"
            if (r1 == 0) goto L_0x0025
            java.lang.String r1 = r6.getEventName(r1)
            boolean r4 = r3.equalsIgnoreCase(r1)
            boolean r1 = r2.equalsIgnoreCase(r1)
            goto L_0x0027
        L_0x0025:
            r1 = 0
            r4 = 0
        L_0x0027:
            if (r4 != 0) goto L_0x002b
            if (r1 == 0) goto L_0x0056
        L_0x002b:
            r7.remove(r0)
            int r0 = r7.size()
            if (r0 != 0) goto L_0x0035
            return
        L_0x0035:
            int r0 = r0 + -1
            java.lang.Object r5 = r7.get(r0)
            ch.qos.logback.core.joran.event.SaxEvent r5 = (p005ch.qos.logback.core.joran.event.SaxEvent) r5
            if (r5 == 0) goto L_0x0056
            java.lang.String r5 = r6.getEventName(r5)
            if (r4 == 0) goto L_0x004b
            boolean r3 = r3.equalsIgnoreCase(r5)
            if (r3 != 0) goto L_0x0053
        L_0x004b:
            if (r1 == 0) goto L_0x0056
            boolean r1 = r2.equalsIgnoreCase(r5)
            if (r1 == 0) goto L_0x0056
        L_0x0053:
            r7.remove(r0)
        L_0x0056:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: p005ch.qos.logback.core.joran.action.IncludeAction.trimHeadAndTail(ch.qos.logback.core.joran.event.SaxEventRecorder):void");
    }

    /* access modifiers changed from: protected */
    public SaxEventRecorder createRecorder(InputStream inputStream, URL url) {
        return new SaxEventRecorder(getContext());
    }

    /* access modifiers changed from: protected */
    public void processInclude(InterpretationContext interpretationContext, URL url) throws JoranException {
        InputStream openURL = openURL(url);
        if (openURL != null) {
            try {
                ConfigurationWatchListUtil.addToWatchList(getContext(), url);
                SaxEventRecorder createRecorder = createRecorder(openURL, url);
                createRecorder.setContext(getContext());
                createRecorder.recordEvents(openURL);
                trimHeadAndTail(createRecorder);
                interpretationContext.getJoranInterpreter().getEventPlayer().addEventsDynamically(createRecorder.getSaxEventList(), this.eventOffset);
            } catch (JoranException e) {
                addError("Failed processing [" + url.toString() + "]", e);
            } catch (Throwable th) {
                close(openURL);
                throw th;
            }
        }
        close(openURL);
    }

    /* access modifiers changed from: protected */
    public void setEventOffset(int i) {
        this.eventOffset = i;
    }
}
