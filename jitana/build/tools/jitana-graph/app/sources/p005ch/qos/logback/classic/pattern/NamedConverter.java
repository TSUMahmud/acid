package p005ch.qos.logback.classic.pattern;

import p005ch.qos.logback.classic.spi.ILoggingEvent;

/* renamed from: ch.qos.logback.classic.pattern.NamedConverter */
public abstract class NamedConverter extends ClassicConverter {
    Abbreviator abbreviator = null;

    public String convert(ILoggingEvent iLoggingEvent) {
        String fullyQualifiedName = getFullyQualifiedName(iLoggingEvent);
        Abbreviator abbreviator2 = this.abbreviator;
        return abbreviator2 == null ? fullyQualifiedName : abbreviator2.abbreviate(fullyQualifiedName);
    }

    /* access modifiers changed from: protected */
    public abstract String getFullyQualifiedName(ILoggingEvent iLoggingEvent);

    public void start() {
        String firstOption = getFirstOption();
        if (firstOption != null) {
            try {
                int parseInt = Integer.parseInt(firstOption);
                if (parseInt == 0) {
                    this.abbreviator = new ClassNameOnlyAbbreviator();
                } else if (parseInt > 0) {
                    this.abbreviator = new TargetLengthBasedClassNameAbbreviator(parseInt);
                }
            } catch (NumberFormatException e) {
            }
        }
    }
}
