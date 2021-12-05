package p005ch.qos.logback.classic.pattern;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.classic.util.LevelToSyslogSeverity;
import p005ch.qos.logback.core.net.SyslogAppenderBase;

/* renamed from: ch.qos.logback.classic.pattern.SyslogStartConverter */
public class SyslogStartConverter extends ClassicConverter {
    int facility;
    long lastTimestamp = -1;
    final String localHostName = "localhost";
    SimpleDateFormat simpleFormat;
    String timesmapStr = null;

    /* access modifiers changed from: package-private */
    public String computeTimeStampString(long j) {
        String str;
        synchronized (this) {
            if (j != this.lastTimestamp) {
                this.lastTimestamp = j;
                this.timesmapStr = this.simpleFormat.format(new Date(j));
            }
            str = this.timesmapStr;
        }
        return str;
    }

    public String convert(ILoggingEvent iLoggingEvent) {
        return "<" + (this.facility + LevelToSyslogSeverity.convert(iLoggingEvent)) + ">" + computeTimeStampString(iLoggingEvent.getTimeStamp()) + ' ' + "localhost" + ' ';
    }

    public void start() {
        boolean z;
        String firstOption = getFirstOption();
        if (firstOption == null) {
            addError("was expecting a facility string as an option");
            return;
        }
        this.facility = SyslogAppenderBase.facilityStringToint(firstOption);
        try {
            this.simpleFormat = new SimpleDateFormat("MMM dd HH:mm:ss", new DateFormatSymbols(Locale.US));
            z = false;
        } catch (IllegalArgumentException e) {
            addError("Could not instantiate SimpleDateFormat", e);
            z = true;
        }
        if (!z) {
            super.start();
        }
    }
}
