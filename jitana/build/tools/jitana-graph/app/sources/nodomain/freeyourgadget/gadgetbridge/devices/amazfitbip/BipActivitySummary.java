package nodomain.freeyourgadget.gadgetbridge.devices.amazfitbip;

import nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummary;

public class BipActivitySummary extends BaseActivitySummary {
    private long activeTimeSeconds;
    private float ascentMeters;
    private int averageHR;
    private int averagePace;
    private int averageStride;
    private float caloriesBurnt;
    private float descentMeters;
    private float distanceMeters;
    private float maxAltitude;
    private int maxLatitude;
    private int maxLongitude;
    private float maxPace;
    private float maxSpeed;
    private float minAltitude;
    private int minLatitude;
    private int minLongitude;
    private float minPace;
    private long steps;
    private long timeAscent;
    private long timeDescent;
    private long timeFlat;
    private float totalStride;
    private int version;

    public void setVersion(int version2) {
        this.version = version2;
    }

    public int getVersion() {
        return this.version;
    }

    public void setDistanceMeters(float distanceMeters2) {
        this.distanceMeters = distanceMeters2;
    }

    public void setAscentMeters(float ascentMeters2) {
        this.ascentMeters = ascentMeters2;
    }

    public void setDescentMeters(float descentMeters2) {
        this.descentMeters = descentMeters2;
    }

    public void setMinAltitude(float minAltitude2) {
        this.minAltitude = minAltitude2;
    }

    public void setMaxAltitude(float maxAltitude2) {
        this.maxAltitude = maxAltitude2;
    }

    public void setMinLatitude(int minLatitude2) {
        this.minLatitude = minLatitude2;
    }

    public void setMaxLatitude(int maxLatitude2) {
        this.maxLatitude = maxLatitude2;
    }

    public void setMinLongitude(int minLongitude2) {
        this.minLongitude = minLongitude2;
    }

    public void setMaxLongitude(int maxLongitude2) {
        this.maxLongitude = maxLongitude2;
    }

    public void setSteps(long steps2) {
        this.steps = steps2;
    }

    public void setActiveTimeSeconds(long activeTimeSeconds2) {
        this.activeTimeSeconds = activeTimeSeconds2;
    }

    public void setCaloriesBurnt(float caloriesBurnt2) {
        this.caloriesBurnt = caloriesBurnt2;
    }

    public void setMaxSpeed(float maxSpeed2) {
        this.maxSpeed = maxSpeed2;
    }

    public void setMinPace(float minPace2) {
        this.minPace = minPace2;
    }

    public void setMaxPace(float maxPace2) {
        this.maxPace = maxPace2;
    }

    public void setTotalStride(float totalStride2) {
        this.totalStride = totalStride2;
    }

    public float getTotalStride() {
        return this.totalStride;
    }

    public void setTimeAscent(long timeAscent2) {
        this.timeAscent = timeAscent2;
    }

    public long getTimeAscent() {
        return this.timeAscent;
    }

    public void setTimeDescent(long timeDescent2) {
        this.timeDescent = timeDescent2;
    }

    public long getTimeDescent() {
        return this.timeDescent;
    }

    public void setTimeFlat(long timeFlat2) {
        this.timeFlat = timeFlat2;
    }

    public long getTimeFlat() {
        return this.timeFlat;
    }

    public void setAverageHR(int averageHR2) {
        this.averageHR = averageHR2;
    }

    public int getAverageHR() {
        return this.averageHR;
    }

    public void setAveragePace(int averagePace2) {
        this.averagePace = averagePace2;
    }

    public int getAveragePace() {
        return this.averagePace;
    }

    public void setAverageStride(int averageStride2) {
        this.averageStride = averageStride2;
    }

    public int getAverageStride() {
        return this.averageStride;
    }
}
