package p005ch.qos.logback.core.joran.event;

import org.xml.sax.Locator;

/* renamed from: ch.qos.logback.core.joran.event.BodyEvent */
public class BodyEvent extends SaxEvent {
    private String text;

    BodyEvent(String str, Locator locator) {
        super((String) null, (String) null, (String) null, locator);
        this.text = str;
    }

    public void append(String str) {
        this.text += str;
    }

    public String getText() {
        String str = this.text;
        return str != null ? str.trim() : str;
    }

    public String toString() {
        return "BodyEvent(" + getText() + ")" + this.locator.getLineNumber() + "," + this.locator.getColumnNumber();
    }
}
