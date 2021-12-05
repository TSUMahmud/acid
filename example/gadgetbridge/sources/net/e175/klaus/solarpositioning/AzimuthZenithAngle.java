package net.e175.klaus.solarpositioning;

public class AzimuthZenithAngle {
    private final double azimuth;
    private final double zenithAngle;

    public AzimuthZenithAngle(double azimuth2, double zenithAngle2) {
        this.zenithAngle = zenithAngle2;
        this.azimuth = azimuth2;
    }

    public final double getZenithAngle() {
        return this.zenithAngle;
    }

    public final double getAzimuth() {
        return this.azimuth;
    }

    public String toString() {
        return String.format("azimuth %.6f°, zenith angle %.6f°", new Object[]{Double.valueOf(this.azimuth), Double.valueOf(this.zenithAngle)});
    }
}
