package nodomain.freeyourgadget.gadgetbridge.devices.huami;

import androidx.core.app.FrameMetricsAggregator;
import androidx.recyclerview.widget.ItemTouchHelper;
import cyanogenmod.externalviews.ExternalViewProviderService;

public class HuamiWeatherConditions {
    public static final byte CLEAR_SKY = 0;
    public static final byte CLOUDY = 2;
    public static final byte DRIZZLE = 19;
    public static final byte EXTREME_RAIN = 10;
    public static final byte EXTREME_SNOW = 17;
    public static final byte HAIL = 5;
    public static final byte HEAVY_RAIN = 9;
    public static final byte HEAVY_SNOW = 16;
    public static final byte LIGHT_RAIN = 7;
    public static final byte LIGHT_SNOW = 14;
    public static final byte MEDIUM_RAIN = 8;
    public static final byte MEDIUM_SNOW = 15;
    public static final byte MIST = 18;
    public static final byte RAIN_AND_SNOW = 6;
    public static final byte RAIN_WITH_SUN = 3;
    public static final byte SCATTERED_CLOUDS = 1;
    public static final byte SNOW_AND_SUN = 13;
    public static final byte SUPER_EXTREME_RAIN = 11;
    public static final byte THUNDERSTORM = 4;
    public static final byte TORRENTIAL_RAIN = 12;
    public static final byte WIND_AND_RAIN = 20;

    public static byte mapToAmazfitBipWeatherCode(int openWeatherMapCondition) {
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
                return 19;
            case 500:
                return 7;
            case 501:
                return 8;
            case 502:
                return 9;
            case 503:
                return 10;
            case 504:
                return 12;
            case FrameMetricsAggregator.EVERY_DURATION:
                return 8;
            case 520:
                return 7;
            case 521:
                return 8;
            case 522:
                return 9;
            case 531:
                return 8;
            case 600:
                return 14;
            case 601:
                return 15;
            case 602:
                return 16;
            case 611:
            case 612:
            case 615:
            case 616:
            case 620:
            case 621:
            case 622:
                return 6;
            case 701:
            case 711:
            case 721:
            case 731:
            case 741:
            case 751:
            case 761:
            case 762:
            case 771:
                return 18;
            case 781:
            case 900:
                return 20;
            case ExternalViewProviderService.Provider.DEFAULT_WINDOW_FLAGS:
                return 0;
            case 801:
            case 802:
            case 803:
                return 1;
            case 804:
                return 2;
            case 901:
                return 20;
            case 903:
            case 904:
            case 905:
                return 0;
            case 906:
                return 5;
            default:
                return 0;
        }
    }
}
