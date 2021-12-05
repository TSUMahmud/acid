package p005ch.qos.logback.core.joran.event;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.sax2.Driver;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.joran.spi.ElementPath;
import p005ch.qos.logback.core.joran.spi.JoranException;
import p005ch.qos.logback.core.spi.ContextAware;
import p005ch.qos.logback.core.spi.ContextAwareImpl;
import p005ch.qos.logback.core.status.Status;

/* renamed from: ch.qos.logback.core.joran.event.SaxEventRecorder */
public class SaxEventRecorder extends DefaultHandler implements ContextAware {
    private final ContextAwareImpl cai;
    ElementPath globalElementPath;
    private Locator locator;
    private List<SaxEvent> saxEventList;

    public SaxEventRecorder() {
        this.saxEventList = new ArrayList();
        this.globalElementPath = new ElementPath();
        this.cai = new ContextAwareImpl((Context) null, this);
    }

    public SaxEventRecorder(Context context) {
        this.saxEventList = new ArrayList();
        this.globalElementPath = new ElementPath();
        this.cai = new ContextAwareImpl(context, this);
    }

    private Driver buildPullParser() throws JoranException {
        try {
            Driver driver = new Driver();
            try {
                driver.setFeature("http://xml.org/sax/features/validation", false);
            } catch (SAXNotSupportedException e) {
            }
            driver.setFeature("http://xml.org/sax/features/namespaces", true);
            return driver;
        } catch (Exception e2) {
            addError("Parser configuration error occurred", e2);
            throw new JoranException("Parser configuration error occurred", e2);
        }
    }

    private void handleError(String str, Throwable th) throws JoranException {
        addError(str, th);
        throw new JoranException(str, th);
    }

    public void addError(String str) {
        this.cai.addError(str);
    }

    public void addError(String str, Throwable th) {
        this.cai.addError(str, th);
    }

    public void addInfo(String str) {
        this.cai.addInfo(str);
    }

    public void addInfo(String str, Throwable th) {
        this.cai.addInfo(str, th);
    }

    public void addStatus(Status status) {
        this.cai.addStatus(status);
    }

    public void addWarn(String str) {
        this.cai.addWarn(str);
    }

    public void addWarn(String str, Throwable th) {
        this.cai.addWarn(str, th);
    }

    public void characters(char[] cArr, int i, int i2) {
        String str = new String(cArr, i, i2);
        SaxEvent lastEvent = getLastEvent();
        if (lastEvent instanceof BodyEvent) {
            ((BodyEvent) lastEvent).append(str);
        } else if (!isSpaceOnly(str)) {
            this.saxEventList.add(new BodyEvent(str, getLocator()));
        }
    }

    public void endElement(String str, String str2, String str3) {
        if (str3 != null) {
            int length = str3.length();
        }
        this.saxEventList.add(new EndEvent(str, str2, str3, getLocator()));
        this.globalElementPath.pop();
    }

    public void error(SAXParseException sAXParseException) throws SAXException {
        addError("XML_PARSING - Parsing error on line " + sAXParseException.getLineNumber() + " and column " + sAXParseException.getColumnNumber(), sAXParseException);
    }

    public void fatalError(SAXParseException sAXParseException) throws SAXException {
        addError("XML_PARSING - Parsing fatal error on line " + sAXParseException.getLineNumber() + " and column " + sAXParseException.getColumnNumber(), sAXParseException);
    }

    public Context getContext() {
        return this.cai.getContext();
    }

    /* access modifiers changed from: package-private */
    public SaxEvent getLastEvent() {
        if (this.saxEventList.isEmpty()) {
            return null;
        }
        return this.saxEventList.get(this.saxEventList.size() - 1);
    }

    public Locator getLocator() {
        return this.locator;
    }

    public List<SaxEvent> getSaxEventList() {
        return this.saxEventList;
    }

    /* access modifiers changed from: package-private */
    public String getTagName(String str, String str2) {
        return (str == null || str.length() < 1) ? str2 : str;
    }

    /* access modifiers changed from: package-private */
    public boolean isSpaceOnly(String str) {
        return str.trim().length() == 0;
    }

    public List<SaxEvent> recordEvents(InputSource inputSource) throws JoranException {
        String str;
        Driver buildPullParser = buildPullParser();
        try {
            buildPullParser.setContentHandler(this);
            buildPullParser.setErrorHandler(this);
            buildPullParser.parse(inputSource);
            return this.saxEventList;
        } catch (EOFException e) {
            handleError(e.getLocalizedMessage(), new SAXParseException(e.getLocalizedMessage(), this.locator, e));
            throw new IllegalStateException("This point can never be reached");
        } catch (IOException e2) {
            e = e2;
            str = "I/O error occurred while parsing xml file";
            handleError(str, e);
            throw new IllegalStateException("This point can never be reached");
        } catch (SAXException e3) {
            throw new JoranException("Problem parsing XML document. See previously reported errors.", e3);
        } catch (Exception e4) {
            e = e4;
            str = "Unexpected exception while parsing XML document.";
            handleError(str, e);
            throw new IllegalStateException("This point can never be reached");
        }
    }

    public final void recordEvents(InputStream inputStream) throws JoranException {
        recordEvents(new InputSource(inputStream));
    }

    public void setContext(Context context) {
        this.cai.setContext(context);
    }

    public void setDocumentLocator(Locator locator2) {
        this.locator = locator2;
    }

    public void startDocument() {
    }

    public void startElement(String str, String str2, String str3, Attributes attributes) {
        if (str3 != null) {
            int length = str3.length();
        }
        this.globalElementPath.push(getTagName(str2, str3));
        this.saxEventList.add(new StartEvent(this.globalElementPath.duplicate(), str, str2, str3, attributes, getLocator()));
    }

    public void warning(SAXParseException sAXParseException) throws SAXException {
        addWarn("XML_PARSING - Parsing warning on line " + sAXParseException.getLineNumber() + " and column " + sAXParseException.getColumnNumber(), sAXParseException);
    }
}
