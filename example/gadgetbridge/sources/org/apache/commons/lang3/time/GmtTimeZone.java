package org.apache.commons.lang3.time;

import java.util.Date;
import java.util.TimeZone;
import p005ch.qos.logback.core.CoreConstants;

class GmtTimeZone extends TimeZone {
    private static final int HOURS_PER_DAY = 24;
    private static final int MILLISECONDS_PER_MINUTE = 60000;
    private static final int MINUTES_PER_HOUR = 60;
    static final long serialVersionUID = 1;
    private final int offset;
    private final String zoneId;

    GmtTimeZone(boolean negate, int hours, int minutes) {
        if (hours >= 24) {
            throw new IllegalArgumentException(hours + " hours out of range");
        } else if (minutes < 60) {
            int milliseconds = ((hours * 60) + minutes) * 60000;
            this.offset = negate ? -milliseconds : milliseconds;
            StringBuilder sb = new StringBuilder(9);
            sb.append(TimeZones.GMT_ID);
            sb.append(negate ? CoreConstants.DASH_CHAR : '+');
            StringBuilder twoDigits = twoDigits(sb, hours);
            twoDigits.append(CoreConstants.COLON_CHAR);
            this.zoneId = twoDigits(twoDigits, minutes).toString();
        } else {
            throw new IllegalArgumentException(minutes + " minutes out of range");
        }
    }

    private static StringBuilder twoDigits(StringBuilder sb, int n) {
        sb.append((char) ((n / 10) + 48));
        sb.append((char) ((n % 10) + 48));
        return sb;
    }

    public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds) {
        return this.offset;
    }

    public void setRawOffset(int offsetMillis) {
        throw new UnsupportedOperationException();
    }

    public int getRawOffset() {
        return this.offset;
    }

    public String getID() {
        return this.zoneId;
    }

    public boolean useDaylightTime() {
        return false;
    }

    public boolean inDaylightTime(Date date) {
        return false;
    }

    public String toString() {
        return "[GmtTimeZone id=\"" + this.zoneId + "\",offset=" + this.offset + ']';
    }

    public int hashCode() {
        return this.offset;
    }

    public boolean equals(Object other) {
        if ((other instanceof GmtTimeZone) && this.zoneId == ((GmtTimeZone) other).zoneId) {
            return true;
        }
        return false;
    }
}
