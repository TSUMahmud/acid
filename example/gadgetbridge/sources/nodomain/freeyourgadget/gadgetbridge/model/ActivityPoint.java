package nodomain.freeyourgadget.gadgetbridge.model;

import java.util.Date;

public class ActivityPoint {
    private String description;
    private int heartRate;
    private GPSCoordinate location;
    private long speed4;
    private long speed5;
    private long speed6;
    private Date time;

    public ActivityPoint() {
    }

    public ActivityPoint(Date time2) {
        this.time = time2;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time2) {
        this.time = time2;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description2) {
        this.description = description2;
    }

    public GPSCoordinate getLocation() {
        return this.location;
    }

    public void setLocation(GPSCoordinate location2) {
        this.location = location2;
    }

    public int getHeartRate() {
        return this.heartRate;
    }

    public void setHeartRate(int heartRate2) {
        this.heartRate = heartRate2;
    }

    public long getSpeed4() {
        return this.speed4;
    }

    public void setSpeed4(long speed42) {
        this.speed4 = speed42;
    }

    public long getSpeed5() {
        return this.speed5;
    }

    public void setSpeed5(long speed52) {
        this.speed5 = speed52;
    }

    public long getSpeed6() {
        return this.speed6;
    }

    public void setSpeed6(long speed62) {
        this.speed6 = speed62;
    }
}
