package nodomain.freeyourgadget.gadgetbridge.service.devices.hplus;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

class HPlusDataRecordDaySummary extends HPlusDataRecord {
    public int activeTime;
    public int calories;
    public int day;
    public int distance;
    public int maxHeartRate;
    public int minHeartRate;
    public int month;
    public int steps;
    public int year;

    HPlusDataRecordDaySummary(byte[] data) {
        super(data, 101);
        this.year = ((data[10] & 255) * 256) + (data[9] & 255);
        this.month = data[11] & 255;
        this.day = data[12] & 255;
        int i = this.year;
        if (i < 1900) {
            this.year = i + 1900;
        }
        if (this.year < 2000 || this.month > 12 || this.day > 31) {
            throw new IllegalArgumentException("Invalid record date " + this.year + "-" + this.month + "-" + this.day);
        }
        this.steps = ((data[2] & 255) * 256) + (data[1] & 255);
        this.distance = (((data[4] & 255) * 256) + (data[3] & 255)) * 10;
        this.activeTime = ((data[14] & 255) * 256) + (data[13] & 255);
        this.calories = ((data[6] & 255) * 256) + (data[5] & 255);
        this.calories += ((data[8] & 255) * 256) + (data[7] & 255);
        this.maxHeartRate = data[15] & 255;
        this.minHeartRate = data[16] & 255;
        Calendar date = GregorianCalendar.getInstance();
        date.set(1, this.year);
        date.set(2, this.month - 1);
        date.set(5, this.day);
        date.set(11, 23);
        date.set(12, 59);
        date.set(13, 59);
        date.set(14, 999);
        this.timestamp = (int) (date.getTimeInMillis() / 1000);
    }

    public String toString() {
        return String.format(Locale.US, "%s-%s-%s steps:%d distance:%d minHR:%d maxHR:%d calories:%d activeTime:%d", new Object[]{Integer.valueOf(this.year), Integer.valueOf(this.month), Integer.valueOf(this.day), Integer.valueOf(this.steps), Integer.valueOf(this.distance), Integer.valueOf(this.minHeartRate), Integer.valueOf(this.maxHeartRate), Integer.valueOf(this.calories), Integer.valueOf(this.activeTime)});
    }
}
