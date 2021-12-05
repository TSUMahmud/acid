package nodomain.freeyourgadget.gadgetbridge.service.devices.hplus;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class HPlusDataRecordDaySlot extends HPlusDataRecord {
    private int age = 0;
    public int heartRate = -1;
    public int intensity = -1;
    public int secondsInactive = -1;
    public int slot;
    public int steps = -1;

    public HPlusDataRecordDaySlot(byte[] data, int age2) {
        super(data, 102);
        int a = ((data[4] & 255) * 256) + (data[5] & 255);
        if (a < 144) {
            this.slot = a;
            this.heartRate = data[1] & 255;
            int i = this.heartRate;
            if (i == 255 || i == 0) {
                this.heartRate = -1;
            }
            this.steps = ((data[2] & 255) * 256) + (data[3] & 255);
            this.secondsInactive = data[7] & 255;
            Calendar slotTime = GregorianCalendar.getInstance();
            slotTime.set(12, (this.slot % 6) * 10);
            slotTime.set(11, this.slot / 6);
            slotTime.set(13, 0);
            this.timestamp = (int) (slotTime.getTimeInMillis() / 1000);
            this.age = age2;
            double d = (double) (this.heartRate * 100);
            double d2 = (double) age2;
            Double.isNaN(d2);
            Double.isNaN(d);
            this.intensity = (int) (d / (208.0d - (d2 * 0.7d)));
            return;
        }
        throw new IllegalArgumentException("Invalid Slot Number");
    }

    public String toString() {
        Calendar slotTime = GregorianCalendar.getInstance();
        slotTime.setTimeInMillis(((long) this.timestamp) * 1000);
        return String.format(Locale.US, "Slot: %d, Time: %s, Steps: %d, InactiveSeconds: %d, HeartRate: %d", new Object[]{Integer.valueOf(this.slot), slotTime.getTime(), Integer.valueOf(this.steps), Integer.valueOf(this.secondsInactive), Integer.valueOf(this.heartRate)});
    }

    public void accumulate(HPlusDataRecordDaySlot other) {
        if (other != null) {
            int i = this.steps;
            if (i == -1) {
                this.steps = other.steps;
            } else {
                int i2 = other.steps;
                if (i2 != -1) {
                    this.steps = i + i2;
                }
            }
            int i3 = this.heartRate;
            if (i3 == -1) {
                this.heartRate = other.heartRate;
            } else {
                int i4 = other.heartRate;
                if (i4 != -1) {
                    this.heartRate = (i3 + i4) / 2;
                }
            }
            this.secondsInactive += other.secondsInactive;
            double d = (double) (this.heartRate * 100);
            double d2 = (double) this.age;
            Double.isNaN(d2);
            Double.isNaN(d);
            this.intensity = (int) (d / (208.0d - (d2 * 0.7d)));
        }
    }

    public boolean isValid() {
        return (this.steps == 0 && this.secondsInactive == 0 && this.heartRate == -1) ? false : true;
    }
}
