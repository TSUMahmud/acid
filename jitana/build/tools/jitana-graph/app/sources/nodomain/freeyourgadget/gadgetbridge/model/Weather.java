package nodomain.freeyourgadget.gadgetbridge.model;

import androidx.core.app.FrameMetricsAggregator;
import androidx.recyclerview.widget.ItemTouchHelper;
import cyanogenmod.externalviews.ExternalViewProviderService;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Weather {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) Weather.class);
    private static final Weather weather = new Weather();
    private JSONObject reconstructedOWMForecast = null;
    private WeatherSpec weatherSpec = null;

    public WeatherSpec getWeatherSpec() {
        return this.weatherSpec;
    }

    public void setWeatherSpec(WeatherSpec weatherSpec2) {
        this.weatherSpec = weatherSpec2;
    }

    public JSONObject createReconstructedOWMWeatherReply() {
        if (this.weatherSpec == null) {
            return null;
        }
        JSONObject reconstructedOWMWeather = new JSONObject();
        JSONArray weather2 = new JSONArray();
        JSONObject condition = new JSONObject();
        JSONObject main = new JSONObject();
        JSONObject wind = new JSONObject();
        try {
            condition.put(PackageConfigHelper.DB_ID, this.weatherSpec.currentConditionCode);
            condition.put("main", this.weatherSpec.currentCondition);
            condition.put("description", this.weatherSpec.currentCondition);
            condition.put("icon", mapToOpenWeatherMapIcon(this.weatherSpec.currentConditionCode));
            weather2.put(condition);
            main.put("temp", this.weatherSpec.currentTemp);
            main.put("humidity", this.weatherSpec.currentHumidity);
            main.put("temp_min", this.weatherSpec.todayMinTemp);
            main.put("temp_max", this.weatherSpec.todayMaxTemp);
            wind.put("speed", (double) (this.weatherSpec.windSpeed / 3.6f));
            wind.put("deg", this.weatherSpec.windDirection);
            reconstructedOWMWeather.put(DeviceService.EXTRA_WEATHER, weather2);
            reconstructedOWMWeather.put("main", main);
            reconstructedOWMWeather.put("name", this.weatherSpec.location);
            reconstructedOWMWeather.put("wind", wind);
            Logger logger = LOG;
            logger.debug("Weather JSON for WEBVIEW: " + reconstructedOWMWeather.toString());
            return reconstructedOWMWeather;
        } catch (JSONException e) {
            LOG.error("Error while reconstructing OWM weather reply");
            return null;
        }
    }

    public JSONObject getReconstructedOWMForecast() {
        return this.reconstructedOWMForecast;
    }

    public void setReconstructedOWMForecast(JSONObject reconstructedOWMForecast2) {
        this.reconstructedOWMForecast = reconstructedOWMForecast2;
    }

    public static Weather getInstance() {
        return weather;
    }

    public static byte mapToPebbleCondition(int openWeatherMapCondition) {
        switch (openWeatherMapCondition) {
            case ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION:
            case 201:
            case 202:
            case 210:
            case 211:
            case 212:
            case 221:
            case 230:
            case 231:
            case 232:
                return 4;
            case 300:
            case 301:
            case 302:
            case 310:
            case 311:
            case 312:
            case 313:
            case 314:
            case 321:
            case 500:
            case 501:
                return 3;
            case 502:
            case 503:
            case 504:
            case FrameMetricsAggregator.EVERY_DURATION:
            case 520:
            case 521:
            case 522:
            case 531:
                return 4;
            case 600:
            case 601:
            case 620:
                return 2;
            case 602:
            case 611:
            case 612:
            case 621:
            case 622:
                return 5;
            case 615:
            case 616:
                return 8;
            case 701:
            case 711:
            case 721:
            case 731:
            case 741:
            case 751:
            case 761:
            case 762:
            case 771:
            case 781:
            case 900:
                return 6;
            case ExternalViewProviderService.Provider.DEFAULT_WINDOW_FLAGS:
                return 7;
            case 801:
            case 802:
            case 803:
            case 804:
                return 0;
            default:
                return 6;
        }
    }

    public static int mapToYahooCondition(int openWeatherMapCondition) {
        switch (openWeatherMapCondition) {
            case ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION:
            case 201:
            case 202:
            case 210:
            case 211:
            case 230:
            case 231:
            case 232:
                return 4;
            case 212:
            case 221:
                return 3;
            case 300:
            case 301:
            case 302:
            case 310:
            case 311:
            case 312:
                return 9;
            case 313:
            case 314:
            case 321:
                return 11;
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
            case FrameMetricsAggregator.EVERY_DURATION:
                return 10;
            case 520:
                return 40;
            case 521:
            case 522:
            case 531:
                return 12;
            case 600:
                return 7;
            case 601:
                return 16;
            case 602:
                return 15;
            case 611:
            case 612:
                return 18;
            case 615:
            case 616:
                return 5;
            case 620:
                return 14;
            case 621:
                return 46;
            case 622:
            case 701:
            case 711:
                return 22;
            case 721:
                return 21;
            case 731:
                return 3200;
            case 741:
                return 20;
            case 751:
            case 761:
                return 19;
            case 762:
            case 771:
                return 3200;
            case 781:
            case 900:
                return 0;
            case ExternalViewProviderService.Provider.DEFAULT_WINDOW_FLAGS:
                return 32;
            case 801:
            case 802:
                return 34;
            case 803:
            case 804:
                return 44;
            case 901:
                return 1;
            case 902:
            case 962:
                return 2;
            case 903:
                return 25;
            case 904:
                return 36;
            case 905:
                return 24;
            case 906:
                return 17;
            case 951:
            case 952:
            case 953:
            case 954:
            case 955:
                return 34;
            case 956:
            case 957:
                return 24;
            case 958:
            case 959:
            case 960:
            case 961:
                return 3200;
            default:
                return 3200;
        }
    }

    public static String mapToOpenWeatherMapIcon(int openWeatherMapCondition) {
        if (openWeatherMapCondition >= 200 && openWeatherMapCondition < 300) {
            return "11d";
        }
        if (openWeatherMapCondition >= 300 && openWeatherMapCondition < 500) {
            return "09d";
        }
        if (openWeatherMapCondition >= 500 && openWeatherMapCondition < 510) {
            return "10d";
        }
        if (openWeatherMapCondition >= 511 && openWeatherMapCondition < 600) {
            return "09d";
        }
        if (openWeatherMapCondition >= 600 && openWeatherMapCondition < 700) {
            return "13d";
        }
        if (openWeatherMapCondition >= 700 && openWeatherMapCondition < 800) {
            return "50d";
        }
        if (openWeatherMapCondition == 800) {
            return "01d";
        }
        if (openWeatherMapCondition == 801) {
            return "02d";
        }
        if (openWeatherMapCondition == 802) {
            return "03d";
        }
        if (openWeatherMapCondition == 803 || openWeatherMapCondition == 804) {
            return "04d";
        }
        return "02d";
    }

    public static int mapToOpenWeatherMapCondition(int yahooCondition) {
        switch (yahooCondition) {
            case 0:
                return 900;
            case 1:
                return 901;
            case 2:
                return 962;
            case 3:
                return 212;
            case 4:
                return 211;
            case 5:
            case 6:
                return 616;
            case 7:
                return 600;
            case 8:
            case 9:
                return 301;
            case 10:
                return FrameMetricsAggregator.EVERY_DURATION;
            case 11:
            case 12:
                return 521;
            case 13:
            case 14:
                return 620;
            case 15:
            case 41:
            case 42:
            case 43:
            case 46:
                return 602;
            case 16:
                return 601;
            case 17:
            case 35:
                return 906;
            case 18:
                return 611;
            case 19:
                return 761;
            case 20:
                return 741;
            case 21:
                return 721;
            case 22:
                return 711;
            case 23:
            case 24:
                return 905;
            case 25:
                return 903;
            case 26:
            case 27:
            case 28:
                return 804;
            case 29:
            case 30:
                return 801;
            case 31:
            case 32:
                return ExternalViewProviderService.Provider.DEFAULT_WINDOW_FLAGS;
            case 33:
            case 34:
                return 801;
            case 36:
                return 904;
            case 37:
            case 38:
            case 39:
                return 210;
            case 40:
                return 520;
            case 44:
                return 801;
            case 45:
            case 47:
                return 211;
            default:
                return -1;
        }
    }

    public static String getConditionString(int openWeatherMapCondition) {
        switch (openWeatherMapCondition) {
            case ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION:
                return "thunderstorm with light rain";
            case 201:
                return "thunderstorm with rain";
            case 202:
                return "thunderstorm with heavy rain";
            case 210:
                return "light thunderstorm:";
            case 211:
                return "thunderstorm";
            case 212:
                return "heavy thunderstorm";
            case 221:
                return "ragged thunderstorm";
            case 230:
                return "thunderstorm with light drizzle";
            case 231:
                return "thunderstorm with drizzle";
            case 232:
                return "thunderstorm with heavy drizzle";
            case 300:
                return "light intensity drizzle";
            case 301:
                return "drizzle";
            case 302:
                return "heavy intensity drizzle";
            case 310:
                return "light intensity drizzle rain";
            case 311:
                return "drizzle rain";
            case 312:
                return "heavy intensity drizzle rain";
            case 313:
                return "shower rain and drizzle";
            case 314:
                return "heavy shower rain and drizzle";
            case 321:
                return "shower drizzle";
            case 500:
                return "light rain";
            case 501:
                return "moderate rain";
            case 502:
                return "heavy intensity rain";
            case 503:
                return "very heavy rain";
            case 504:
                return "extreme rain";
            case FrameMetricsAggregator.EVERY_DURATION:
                return "freezing rain";
            case 520:
                return "light intensity shower rain";
            case 521:
                return "shower rain";
            case 522:
                return "heavy intensity shower rain";
            case 531:
                return "ragged shower rain";
            case 600:
                return "light snow";
            case 601:
                return "snow";
            case 602:
                return "heavy snow";
            case 611:
                return "sleet";
            case 612:
                return "shower sleet";
            case 615:
                return "light rain and snow";
            case 616:
                return "rain and snow";
            case 620:
                return "light shower snow";
            case 621:
                return "shower snow";
            case 622:
                return "heavy shower snow";
            case 701:
                return "mist";
            case 711:
                return "smoke";
            case 721:
                return "haze";
            case 731:
                return "sandcase dust whirls";
            case 741:
                return "fog";
            case 751:
                return "sand";
            case 761:
                return "dust";
            case 762:
                return "volcanic ash";
            case 771:
                return "squalls";
            case 781:
                return "tornado";
            case ExternalViewProviderService.Provider.DEFAULT_WINDOW_FLAGS:
                return "clear sky";
            case 801:
                return "few clouds";
            case 802:
                return "scattered clouds";
            case 803:
                return "broken clouds";
            case 804:
                return "overcast clouds";
            case 900:
                return "tornado";
            case 901:
                return "tropical storm";
            case 902:
                return "hurricane";
            case 903:
                return "cold";
            case 904:
                return "hot";
            case 905:
                return "windy";
            case 906:
                return "hail";
            case 951:
                return "calm";
            case 952:
                return "light breeze";
            case 953:
                return "gentle breeze";
            case 954:
                return "moderate breeze";
            case 955:
                return "fresh breeze";
            case 956:
                return "strong breeze";
            case 957:
                return "high windcase  near gale";
            case 958:
                return "gale";
            case 959:
                return "severe gale";
            case 960:
                return "storm";
            case 961:
                return "violent storm";
            case 962:
                return "hurricane";
            default:
                return "";
        }
    }

    public static byte mapToZeTimeConditionOld(int openWeatherMapCondition) {
        switch (openWeatherMapCondition) {
            case ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION:
            case 201:
            case 202:
            case 210:
            case 211:
            case 212:
            case 221:
            case 230:
            case 231:
            case 232:
            case 771:
            case 781:
            case 900:
            case 901:
            case 902:
            case 960:
            case 961:
            case 962:
                return 6;
            case 300:
            case 301:
            case 302:
            case 310:
            case 311:
            case 312:
            case 313:
            case 314:
            case 321:
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
            case FrameMetricsAggregator.EVERY_DURATION:
            case 520:
            case 521:
            case 522:
            case 531:
            case 906:
                return 4;
            case 600:
            case 601:
            case 602:
            case 611:
            case 612:
            case 615:
            case 616:
            case 620:
            case 621:
            case 622:
            case 903:
                return 5;
            case 701:
            case 711:
            case 721:
            case 731:
            case 741:
            case 751:
            case 761:
            case 762:
                return 1;
            case ExternalViewProviderService.Provider.DEFAULT_WINDOW_FLAGS:
            case 904:
                return 2;
            case 905:
            case 951:
            case 952:
            case 953:
            case 954:
            case 955:
            case 956:
            case 957:
            case 958:
            case 959:
                return 3;
            default:
                return 0;
        }
    }

    public static byte mapToZeTimeCondition(int openWeatherMapCondition) {
        switch (openWeatherMapCondition) {
            case ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION:
            case 201:
            case 202:
            case 230:
            case 231:
            case 232:
                return 20;
            case 210:
                return 22;
            case 211:
            case 212:
            case 221:
                return 3;
            case 300:
            case 301:
            case 302:
            case 310:
            case 311:
            case 312:
            case 313:
            case 314:
            case 321:
                return 7;
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
            case 520:
            case 521:
            case 522:
            case 531:
            case 906:
                return 8;
            case FrameMetricsAggregator.EVERY_DURATION:
                return 6;
            case 600:
            case 601:
                return 11;
            case 602:
                return 24;
            case 611:
            case 612:
                return 12;
            case 615:
            case 616:
                return 4;
            case 620:
            case 621:
            case 622:
                return 23;
            case 701:
            case 711:
            case 721:
            case 731:
            case 741:
            case 751:
            case 761:
            case 762:
                return 13;
            case 771:
            case 902:
            case 960:
            case 961:
            case 962:
                return 2;
            case 781:
            case 900:
                return 0;
            case ExternalViewProviderService.Provider.DEFAULT_WINDOW_FLAGS:
                return 19;
            case 801:
            case 802:
            case 803:
                return 17;
            case 804:
                return 15;
            case 901:
                return 1;
            case 904:
                return 21;
            case 905:
            case 951:
            case 952:
            case 953:
            case 954:
            case 955:
            case 956:
            case 957:
            case 958:
            case 959:
                return 14;
            default:
                return 5;
        }
    }
}
