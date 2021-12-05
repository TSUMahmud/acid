package nodomain.freeyourgadget.gadgetbridge.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class GPSCoordinate {
    public static final int GPS_DECIMAL_DEGREES_SCALE = 6;
    private final double altitude;
    private final double latitude;
    private final double longitude;

    public GPSCoordinate(double longitude2, double latitude2, double altitude2) {
        this.longitude = longitude2;
        this.latitude = latitude2;
        this.altitude = altitude2;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getAltitude() {
        return this.altitude;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GPSCoordinate that = (GPSCoordinate) o;
        if (Double.compare(that.getLatitude(), getLatitude()) == 0 && Double.compare(that.getLongitude(), getLongitude()) == 0 && Double.compare(that.getAltitude(), getAltitude()) == 0) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(getLatitude());
        long temp2 = Double.doubleToLongBits(getLongitude());
        long temp3 = Double.doubleToLongBits(getAltitude());
        return (((((int) ((temp >>> 32) ^ temp)) * 31) + ((int) ((temp2 >>> 32) ^ temp2))) * 31) + ((int) ((temp3 >>> 32) ^ temp3));
    }

    private String formatLocation(double value) {
        return new BigDecimal(value).setScale(8, RoundingMode.HALF_UP).toPlainString();
    }

    public String toString() {
        return "lon: " + formatLocation(this.longitude) + ", lat: " + formatLocation(this.latitude) + ", alt: " + formatLocation(this.altitude) + "m";
    }
}
