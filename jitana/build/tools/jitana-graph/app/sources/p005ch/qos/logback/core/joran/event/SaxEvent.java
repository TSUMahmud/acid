package p005ch.qos.logback.core.joran.event;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

/* renamed from: ch.qos.logback.core.joran.event.SaxEvent */
public class SaxEvent {
    public final String localName;
    public final Locator locator;
    public final String namespaceURI;
    public final String qName;

    SaxEvent(String str, String str2, String str3, Locator locator2) {
        this.namespaceURI = str;
        this.localName = str2;
        this.qName = str3;
        this.locator = new LocatorImpl(locator2);
    }

    public String getLocalName() {
        return this.localName;
    }

    public Locator getLocator() {
        return this.locator;
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public String getQName() {
        return this.qName;
    }
}
