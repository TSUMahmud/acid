package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.webview;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrentPosition {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) CurrentPosition.class);
    float accuracy;
    double altitude;
    private Location lastKnownLocation = new Location("preferences");
    private float latitude;
    private float longitude;
    float speed;
    long timestamp;

    /* access modifiers changed from: package-private */
    public float getLatitude() {
        return this.latitude;
    }

    /* access modifiers changed from: package-private */
    public float getLongitude() {
        return this.longitude;
    }

    public Location getLastKnownLocation() {
        return this.lastKnownLocation;
    }

    public CurrentPosition() {
        Location lastKnownLocation2;
        Prefs prefs = GBApplication.getPrefs();
        this.latitude = prefs.getFloat("location_latitude", 0.0f);
        this.longitude = prefs.getFloat("location_longitude", 0.0f);
        this.lastKnownLocation.setLatitude((double) this.latitude);
        this.lastKnownLocation.setLongitude((double) this.longitude);
        Logger logger = LOG;
        logger.info("got longitude/latitude from preferences: " + this.latitude + "/" + this.longitude);
        this.timestamp = System.currentTimeMillis() - DateUtils.MILLIS_PER_DAY;
        if (ActivityCompat.checkSelfPermission(GBApplication.getContext(), "android.permission.ACCESS_COARSE_LOCATION") == 0 && prefs.getBoolean("use_updated_location_if_available", false)) {
            LocationManager locationManager = (LocationManager) GBApplication.getContext().getSystemService("location");
            String provider = locationManager != null ? locationManager.getBestProvider(new Criteria(), false) : null;
            if (provider != null && (lastKnownLocation2 = locationManager.getLastKnownLocation(provider)) != null) {
                this.lastKnownLocation = lastKnownLocation2;
                this.timestamp = lastKnownLocation2.getTime();
                this.timestamp = System.currentTimeMillis() - 1000;
                this.latitude = (float) lastKnownLocation2.getLatitude();
                this.longitude = (float) lastKnownLocation2.getLongitude();
                this.accuracy = lastKnownLocation2.getAccuracy();
                this.altitude = (double) ((float) lastKnownLocation2.getAltitude());
                this.speed = lastKnownLocation2.getSpeed();
            }
        }
    }
}
