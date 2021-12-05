package nodomain.freeyourgadget.gadgetbridge.service.devices.hplus;

import java.util.GregorianCalendar;
import java.util.Locale;

class HPlusDataRecordRealtime extends HPlusDataRecord {
    public int activeTime;
    public byte battery;
    public int calories;
    public int distance;
    public int heartRate;
    public int intensity;
    public int steps;

    public HPlusDataRecordRealtime(byte[] data, int age) {
        super(data, 103);
        if (data.length >= 15) {
            this.timestamp = (int) (GregorianCalendar.getInstance().getTimeInMillis() / 1000);
            this.distance = (((data[4] & 255) * 256) + (data[3] & 255)) * 10;
            this.steps = ((data[2] & 255) * 256) + (data[1] & 255);
            int y = ((data[8] & 255) * 256) + (data[7] & 255);
            this.battery = data[9];
            this.calories = ((data[6] & 255) * 256) + (data[5] & 255) + y;
            this.activeTime = (data[14] & 65280) + (data[13] & 255);
            if (this.battery == 255) {
                this.battery = -1;
                this.heartRate = -1;
                this.intensity = 0;
                this.activityKind = 8;
                return;
            }
            this.heartRate = data[11] & 255;
            int i = this.heartRate;
            if (i == 255) {
                this.intensity = 0;
                this.activityKind = -1;
                this.heartRate = -1;
                return;
            }
            double d = (double) (i * 100);
            double d2 = (double) age;
            Double.isNaN(d2);
            Double.isNaN(d);
            this.intensity = (int) (d / (208.0d - (d2 * 0.7d)));
            this.activityKind = 103;
            return;
        }
        throw new IllegalArgumentException("Invalid data packet");
    }

    public void computeActivity(HPlusDataRecordRealtime prev) {
        int deltaDistance;
        int deltaTime;
        if (prev != null && (deltaDistance = this.distance - prev.distance) > 0 && (deltaTime = this.timestamp - prev.timestamp) > 0 && ((double) (deltaDistance / deltaTime)) >= 1.6d) {
            this.activityKind = 1;
        }
    }

    public boolean same(HPlusDataRecordRealtime other) {
        if (other != null && this.steps == other.steps && this.distance == other.distance && this.calories == other.calories && this.heartRate == other.heartRate && this.battery == other.battery) {
            return true;
        }
        return false;
    }

    public String toString() {
        return String.format(Locale.US, "Distance: %d Steps: %d Calories: %d HeartRate: %d Battery: %d ActiveTime: %d Intensity: %d", new Object[]{Integer.valueOf(this.distance), Integer.valueOf(this.steps), Integer.valueOf(this.calories), Integer.valueOf(this.heartRate), Byte.valueOf(this.battery), Integer.valueOf(this.activeTime), Integer.valueOf(this.intensity)});
    }
}
