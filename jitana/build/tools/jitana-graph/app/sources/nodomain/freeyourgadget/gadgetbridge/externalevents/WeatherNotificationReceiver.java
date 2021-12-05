package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p013ru.gelin.android.weather.notification.ParcelableWeather2;

public class WeatherNotificationReceiver extends BroadcastReceiver {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) WeatherNotificationReceiver.class);

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null || !intent.getAction().contains("WEATHER_UPDATE_2")) {
            LOG.info("Wrong action");
            return;
        }
        ParcelableWeather2 parcelableWeather2 = null;
        try {
            parcelableWeather2 = (ParcelableWeather2) intent.getParcelableExtra("ru.gelin.android.weather.notification.EXTRA_WEATHER");
        } catch (RuntimeException e) {
            LOG.error("cannot get ParcelableWeather2", (Throwable) e);
        }
        if (parcelableWeather2 != null) {
            Weather.getInstance().setReconstructedOWMForecast(parcelableWeather2.reconstructedOWMForecast);
            WeatherSpec weatherSpec = parcelableWeather2.weatherSpec;
            Logger logger = LOG;
            StringBuilder sb = new StringBuilder();
            sb.append("weather in ");
            sb.append(weatherSpec.location);
            sb.append(" is ");
            sb.append(weatherSpec.currentCondition);
            sb.append(" (");
            sb.append(weatherSpec.currentTemp - 273);
            sb.append("°C)");
            logger.info(sb.toString());
            Weather.getInstance().setWeatherSpec(weatherSpec);
            GBApplication.deviceService().onSendWeather(weatherSpec);
        }
    }
}
