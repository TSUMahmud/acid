package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import cyanogenmod.alarmclock.ClockContract;
import java.util.ArrayList;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OmniJawsObserver extends ContentObserver {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) OmniJawsObserver.class);
    private static final String SERVICE_PACKAGE = "org.omnirom.omnijaws";
    private static final Uri SETTINGS_URI = Uri.parse("content://org.omnirom.omnijaws.provider/settings");
    public static final Uri WEATHER_URI = Uri.parse("content://org.omnirom.omnijaws.provider/weather");
    private final String[] SETTINGS_PROJECTION = {ClockContract.AlarmsColumns.ENABLED, "units"};
    private final String[] WEATHER_PROJECTION = {"city", "condition", "condition_code", "temperature", "humidity", "forecast_low", "forecast_high", "forecast_condition", "forecast_condition_code", "time_stamp", "forecast_date", "wind_speed", "wind_direction"};
    private Context mContext = GBApplication.getContext();
    private boolean mEnabled = false;
    private boolean mInstalled = isOmniJawsServiceAvailable();
    private boolean mMetric = true;

    public OmniJawsObserver(Handler handler) throws PackageManager.NameNotFoundException {
        super(handler);
        Logger logger = LOG;
        logger.info("OmniJaws installation status: " + this.mInstalled);
        checkSettings();
        Logger logger2 = LOG;
        logger2.info("OmniJaws is enabled: " + this.mEnabled);
        if (this.mEnabled) {
            updateWeather();
            return;
        }
        throw new PackageManager.NameNotFoundException();
    }

    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        LOG.info("Weather update received");
        checkSettings();
        if (!this.mEnabled) {
            LOG.info("Provider was disabled, ignoring.");
        } else {
            queryWeather();
        }
    }

    private void queryWeather() {
        Cursor c = this.mContext.getContentResolver().query(WEATHER_URI, this.WEATHER_PROJECTION, (String) null, (String[]) null, (String) null);
        if (c != null) {
            try {
                WeatherSpec weatherSpec = new WeatherSpec();
                weatherSpec.forecasts = new ArrayList<>();
                int count = c.getCount();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        c.moveToPosition(i);
                        if (i == 0) {
                            weatherSpec.location = c.getString(0);
                            weatherSpec.currentConditionCode = Weather.mapToOpenWeatherMapCondition(c.getInt(2));
                            weatherSpec.currentCondition = Weather.getConditionString(weatherSpec.currentConditionCode);
                            weatherSpec.currentTemp = toKelvin((double) c.getFloat(3));
                            weatherSpec.currentHumidity = (int) c.getFloat(4);
                            weatherSpec.windSpeed = toKmh(c.getFloat(11));
                            weatherSpec.windDirection = c.getInt(12);
                            weatherSpec.timestamp = (int) (Long.parseLong(c.getString(9)) / 1000);
                        } else if (i == 1) {
                            weatherSpec.todayMinTemp = toKelvin((double) c.getFloat(5));
                            weatherSpec.todayMaxTemp = toKelvin((double) c.getFloat(6));
                        } else {
                            WeatherSpec.Forecast gbForecast = new WeatherSpec.Forecast();
                            gbForecast.minTemp = toKelvin((double) c.getFloat(5));
                            gbForecast.maxTemp = toKelvin((double) c.getFloat(6));
                            gbForecast.conditionCode = Weather.mapToOpenWeatherMapCondition(c.getInt(8));
                            weatherSpec.forecasts.add(gbForecast);
                        }
                    }
                }
                Weather.getInstance().setWeatherSpec(weatherSpec);
                GBApplication.deviceService().onSendWeather(weatherSpec);
            } finally {
                c.close();
            }
        }
    }

    private void updateWeather() {
        Intent updateIntent = new Intent("android.intent.action.MAIN").setClassName(SERVICE_PACKAGE, "org.omnirom.omnijaws.WeatherService");
        updateIntent.setAction("org.omnirom.omnijaws.ACTION_UPDATE");
        this.mContext.startService(updateIntent);
    }

    private boolean isOmniJawsServiceAvailable() throws PackageManager.NameNotFoundException {
        this.mContext.getPackageManager().getPackageInfo(SERVICE_PACKAGE, 0);
        return true;
    }

    private void checkSettings() {
        Cursor c;
        if (this.mInstalled && (c = this.mContext.getContentResolver().query(SETTINGS_URI, this.SETTINGS_PROJECTION, (String) null, (String[]) null, (String) null)) != null) {
            boolean z = true;
            if (c.getCount() == 1) {
                c.moveToPosition(0);
                this.mEnabled = c.getInt(0) == 1;
                if (c.getInt(1) != 0) {
                    z = false;
                }
                this.mMetric = z;
            }
        }
    }

    private int toKelvin(double temperature) {
        if (this.mMetric) {
            return (int) (273.15d + temperature);
        }
        return (int) (((temperature - 32.0d) * 0.5555555555555556d) + 273.15d);
    }

    private float toKmh(float speed) {
        if (this.mMetric) {
            return speed;
        }
        return 1.61f * speed;
    }
}
