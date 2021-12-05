package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import cyanogenmod.weather.CMWeatherManager;
import cyanogenmod.weather.WeatherInfo;
import cyanogenmod.weather.WeatherLocation;
import cyanogenmod.weather.util.WeatherUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.BuildConfig;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p005ch.qos.logback.core.spi.AbstractComponentTracker;

public class CMWeatherReceiver extends BroadcastReceiver implements CMWeatherManager.WeatherUpdateRequestListener, CMWeatherManager.LookupCityRequestListener {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) CMWeatherReceiver.class);
    private Context mContext = GBApplication.getContext();
    private PendingIntent mPendingIntent = null;
    private WeatherLocation weatherLocation = null;

    public CMWeatherReceiver() {
        if (CMWeatherManager.getInstance(this.mContext) != null) {
            Prefs prefs = GBApplication.getPrefs();
            String city = prefs.getString("weather_city", (String) null);
            String cityId = prefs.getString("weather_cityid", (String) null);
            if ((cityId == null || cityId.equals("")) && city != null && !city.equals("")) {
                lookupCity(city);
            } else if (city != null && cityId != null) {
                this.weatherLocation = new WeatherLocation.Builder(cityId, city).build();
                enablePeriodicAlarm(true);
            }
        }
    }

    private void lookupCity(String city) {
        CMWeatherManager weatherManager = CMWeatherManager.getInstance(this.mContext);
        if (weatherManager != null && city != null && !city.equals("") && weatherManager.getActiveWeatherServiceProviderLabel() != null) {
            weatherManager.lookupCity(city, this);
        }
    }

    private void enablePeriodicAlarm(boolean enable) {
        if (this.mPendingIntent != null && enable) {
            return;
        }
        if (this.mPendingIntent != null || enable) {
            AlarmManager am = (AlarmManager) this.mContext.getSystemService("alarm");
            if (am == null) {
                LOG.warn("could not get alarm manager!");
            } else if (enable) {
                Intent intent = new Intent("GB_UPDATE_WEATHER");
                intent.setPackage(BuildConfig.APPLICATION_ID);
                this.mPendingIntent = PendingIntent.getBroadcast(this.mContext, 0, intent, 0);
                am.setInexactRepeating(0, Calendar.getInstance().getTimeInMillis() + AbstractComponentTracker.LINGERING_TIMEOUT, DateUtils.MILLIS_PER_HOUR, this.mPendingIntent);
            } else {
                am.cancel(this.mPendingIntent);
                this.mPendingIntent = null;
            }
        }
    }

    public void onReceive(Context context, Intent intent) {
        Prefs prefs = GBApplication.getPrefs();
        String city = prefs.getString("weather_city", (String) null);
        String cityId = prefs.getString("weather_cityid", (String) null);
        if (city == null || city.equals("") || cityId != null) {
            requestWeather();
        } else {
            lookupCity(city);
        }
    }

    private void requestWeather() {
        WeatherLocation weatherLocation2;
        CMWeatherManager weatherManager = CMWeatherManager.getInstance(GBApplication.getContext());
        if (weatherManager.getActiveWeatherServiceProviderLabel() != null && (weatherLocation2 = this.weatherLocation) != null) {
            weatherManager.requestWeatherUpdate(weatherLocation2, (CMWeatherManager.WeatherUpdateRequestListener) this);
        }
    }

    public void onWeatherRequestCompleted(int status, WeatherInfo weatherInfo) {
        if (weatherInfo != null) {
            Logger logger = LOG;
            logger.info("weather: " + weatherInfo.toString());
            WeatherSpec weatherSpec = new WeatherSpec();
            weatherSpec.timestamp = (int) (weatherInfo.getTimestamp() / 1000);
            weatherSpec.location = weatherInfo.getCity();
            if (weatherInfo.getTemperatureUnit() == 2) {
                weatherSpec.currentTemp = ((int) WeatherUtils.fahrenheitToCelsius(weatherInfo.getTemperature())) + 273;
                weatherSpec.todayMaxTemp = ((int) WeatherUtils.fahrenheitToCelsius(weatherInfo.getTodaysHigh())) + 273;
                weatherSpec.todayMinTemp = ((int) WeatherUtils.fahrenheitToCelsius(weatherInfo.getTodaysLow())) + 273;
            } else {
                weatherSpec.currentTemp = ((int) weatherInfo.getTemperature()) + 273;
                weatherSpec.todayMaxTemp = ((int) weatherInfo.getTodaysHigh()) + 273;
                weatherSpec.todayMinTemp = ((int) weatherInfo.getTodaysLow()) + 273;
            }
            if (weatherInfo.getWindSpeedUnit() == 2) {
                weatherSpec.windSpeed = ((float) weatherInfo.getWindSpeed()) * 1.609344f;
            } else {
                weatherSpec.windSpeed = (float) weatherInfo.getWindSpeed();
            }
            weatherSpec.windDirection = (int) weatherInfo.getWindDirection();
            weatherSpec.currentConditionCode = Weather.mapToOpenWeatherMapCondition(CMtoYahooCondintion(weatherInfo.getConditionCode()));
            weatherSpec.currentCondition = Weather.getConditionString(weatherSpec.currentConditionCode);
            weatherSpec.currentHumidity = (int) weatherInfo.getHumidity();
            weatherSpec.forecasts = new ArrayList<>();
            List<WeatherInfo.DayForecast> forecasts = weatherInfo.getForecasts();
            for (int i = 1; i < forecasts.size(); i++) {
                WeatherInfo.DayForecast cmForecast = forecasts.get(i);
                WeatherSpec.Forecast gbForecast = new WeatherSpec.Forecast();
                if (weatherInfo.getTemperatureUnit() == 2) {
                    gbForecast.maxTemp = ((int) WeatherUtils.fahrenheitToCelsius(cmForecast.getHigh())) + 273;
                    gbForecast.minTemp = ((int) WeatherUtils.fahrenheitToCelsius(cmForecast.getLow())) + 273;
                } else {
                    gbForecast.maxTemp = ((int) cmForecast.getHigh()) + 273;
                    gbForecast.minTemp = ((int) cmForecast.getLow()) + 273;
                }
                gbForecast.conditionCode = Weather.mapToOpenWeatherMapCondition(CMtoYahooCondintion(cmForecast.getConditionCode()));
                weatherSpec.forecasts.add(gbForecast);
            }
            Weather.getInstance().setWeatherSpec(weatherSpec);
            GBApplication.deviceService().onSendWeather(weatherSpec);
            return;
        }
        LOG.info("request has returned null for WeatherInfo");
    }

    private int CMtoYahooCondintion(int cmCondition) {
        if (cmCondition <= 11) {
            return cmCondition;
        }
        if (cmCondition <= 37) {
            return cmCondition + 1;
        }
        if (cmCondition <= 40) {
            return cmCondition + 2;
        }
        if (cmCondition <= 44) {
            return cmCondition + 3;
        }
        return 3200;
    }

    public void onLookupCityRequestCompleted(int result, List<WeatherLocation> list) {
        if (list != null) {
            this.weatherLocation = list.get(0);
            String cityId = this.weatherLocation.getCityId();
            String city = this.weatherLocation.getCity();
            SharedPreferences.Editor editor = GBApplication.getPrefs().getPreferences().edit();
            editor.putString("weather_city", city).apply();
            editor.putString("weather_cityid", cityId).apply();
            enablePeriodicAlarm(true);
            requestWeather();
            return;
        }
        enablePeriodicAlarm(false);
    }
}
