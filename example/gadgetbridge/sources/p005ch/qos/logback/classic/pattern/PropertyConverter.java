package p005ch.qos.logback.classic.pattern;

import p005ch.qos.logback.classic.spi.ILoggingEvent;

/* renamed from: ch.qos.logback.classic.pattern.PropertyConverter */
public final class PropertyConverter extends ClassicConverter {
    String key;

    public String convert(ILoggingEvent iLoggingEvent) {
        if (this.key == null) {
            return "Property_HAS_NO_KEY";
        }
        String str = iLoggingEvent.getLoggerContextVO().getPropertyMap().get(this.key);
        return str != null ? str : System.getProperty(this.key);
    }

    public void start() {
        String firstOption = getFirstOption();
        if (firstOption != null) {
            this.key = firstOption;
            super.start();
        }
    }
}
