package nodomain.freeyourgadget.gadgetbridge.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.User;

public class ActivityTrack {
    private Date baseTime;
    private Device device;
    private String name;
    private List<ActivityPoint> trackPoints = new ArrayList();
    private User user;

    public void setBaseTime(Date baseTime2) {
        this.baseTime = baseTime2;
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(Device device2) {
        this.device = device2;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user2) {
        this.user = user2;
    }

    public void setTrackPoints(List<ActivityPoint> trackPoints2) {
        this.trackPoints = trackPoints2;
    }

    public void addTrackPoint(ActivityPoint point) {
        this.trackPoints.add(point);
    }

    public List<ActivityPoint> getTrackPoints() {
        return this.trackPoints;
    }

    public Date getBaseTime() {
        return this.baseTime;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }
}
