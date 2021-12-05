package p005ch.qos.logback.core.joran.event;

import org.xml.sax.Locator;

/* renamed from: ch.qos.logback.core.joran.event.EndEvent */
public class EndEvent extends SaxEvent {
    EndEvent(String str, String str2, String str3, Locator locator) {
        super(str, str2, str3, locator);
    }

    public String toString() {
        return "  EndEvent(" + getQName() + ")  [" + this.locator.getLineNumber() + "," + this.locator.getColumnNumber() + "]";
    }
}
