package nodomain.freeyourgadget.gadgetbridge.service.devices.hplus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import nodomain.freeyourgadget.gadgetbridge.service.devices.hplus.HPlusDataRecord;

public class HPlusDataRecordSleep extends HPlusDataRecord {
    public int bedTimeEnd;
    public int bedTimeStart;
    public int deepSleepMinutes;
    public int enterSleepMinutes;
    public int lightSleepMinutes;
    public int remSleepMinutes;
    public int spindleMinutes;
    public int wakeupCount;
    public int wakeupMinutes;

    public HPlusDataRecordSleep(byte[] data) {
        super(data, 100);
        int year = ((data[2] & 255) * 256) + (data[1] & 255);
        int month = data[3] & 255;
        int day = data[4] & 255;
        year = year < 2000 ? year + 1900 : year;
        if (year < 2000 || month > 12 || day <= 0 || day > 31) {
            throw new IllegalArgumentException("Invalid record date: " + year + "-" + month + "-" + day);
        }
        this.enterSleepMinutes = ((data[6] & 255) * 256) + (data[5] & 255);
        this.spindleMinutes = ((data[8] & 255) * 256) + (data[7] & 255);
        this.deepSleepMinutes = ((data[10] & 255) * 256) + (data[9] & 255);
        this.remSleepMinutes = ((data[12] & 255) * 256) + (data[11] & 255);
        this.wakeupMinutes = ((data[14] & 255) * 256) + (data[13] & 255);
        this.wakeupCount = ((data[16] & 255) * 256) + (data[15] & 255);
        Calendar sleepStart = GregorianCalendar.getInstance();
        sleepStart.clear();
        sleepStart.set(1, year);
        sleepStart.set(2, month - 1);
        sleepStart.set(5, day);
        sleepStart.set(11, data[17] & 255);
        sleepStart.set(12, data[18] & 255);
        sleepStart.set(13, 0);
        sleepStart.set(14, 0);
        this.bedTimeStart = (int) (sleepStart.getTimeInMillis() / 1000);
        int i = this.enterSleepMinutes;
        int i2 = this.spindleMinutes;
        int i3 = i + i2 + this.deepSleepMinutes;
        int i4 = this.remSleepMinutes;
        int i5 = this.bedTimeStart;
        this.bedTimeEnd = ((i3 + i4 + this.wakeupMinutes) * 60) + i5;
        this.lightSleepMinutes = i + i2 + i4;
        this.timestamp = i5;
    }

    public List<HPlusDataRecord.RecordInterval> getIntervals() {
        List<HPlusDataRecord.RecordInterval> intervals = new ArrayList<>();
        int i = this.bedTimeStart;
        int ts = (this.lightSleepMinutes * 60) + i;
        intervals.add(new HPlusDataRecord.RecordInterval(i, ts, 2));
        intervals.add(new HPlusDataRecord.RecordInterval(ts, this.bedTimeEnd, 4));
        return intervals;
    }

    public String toString() {
        Calendar s = GregorianCalendar.getInstance();
        s.setTimeInMillis(((long) this.bedTimeStart) * 1000);
        Calendar end = GregorianCalendar.getInstance();
        end.setTimeInMillis(((long) this.bedTimeEnd) * 1000);
        return String.format(Locale.US, "Sleep start: %s end: %s enter: %d spindles: %d rem: %d deep: %d wake: %d-%d", new Object[]{s.getTime(), end.getTime(), Integer.valueOf(this.enterSleepMinutes), Integer.valueOf(this.spindleMinutes), Integer.valueOf(this.remSleepMinutes), Integer.valueOf(this.deepSleepMinutes), Integer.valueOf(this.wakeupMinutes), Integer.valueOf(this.wakeupCount)});
    }
}
