package p005ch.qos.logback.core.joran.event;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.AttributesImpl;
import p005ch.qos.logback.core.joran.spi.ElementPath;

/* renamed from: ch.qos.logback.core.joran.event.StartEvent */
public class StartEvent extends SaxEvent {
    public final Attributes attributes;
    public final ElementPath elementPath;

    StartEvent(ElementPath elementPath2, String str, String str2, String str3, Attributes attributes2, Locator locator) {
        super(str, str2, str3, locator);
        this.attributes = new AttributesImpl(attributes2);
        this.elementPath = elementPath2;
    }

    public Attributes getAttributes() {
        return this.attributes;
    }

    public String toString() {
        return "StartEvent(" + getQName() + ")  [" + this.locator.getLineNumber() + "," + this.locator.getColumnNumber() + "]";
    }
}
