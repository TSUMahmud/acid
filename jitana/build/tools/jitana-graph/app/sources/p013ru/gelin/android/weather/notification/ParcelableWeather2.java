package p013ru.gelin.android.weather.notification;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p005ch.qos.logback.core.CoreConstants;

/* renamed from: ru.gelin.android.weather.notification.ParcelableWeather2 */
public class ParcelableWeather2 implements Parcelable {
    public static final Parcelable.Creator<ParcelableWeather2> CREATOR = new Parcelable.Creator<ParcelableWeather2>() {
        public ParcelableWeather2 createFromParcel(Parcel in) {
            return new ParcelableWeather2(in);
        }

        public ParcelableWeather2[] newArray(int size) {
            return new ParcelableWeather2[size];
        }
    };
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) ParcelableWeather2.class);
    public JSONObject reconstructedOWMForecast;
    public WeatherSpec weatherSpec;

    /* renamed from: ru.gelin.android.weather.notification.ParcelableWeather2$WindDirection */
    private enum WindDirection {
        N,
        NNE,
        NE,
        ENE,
        E,
        ESE,
        SE,
        SSE,
        S,
        SSW,
        SW,
        WSW,
        W,
        WNW,
        NW,
        NNW
    }

    private ParcelableWeather2(Parcel in) {
        int forecastConditionCode;
        long time;
        JSONObject main;
        ParcelableWeather2 parcelableWeather2 = this;
        Parcel parcel = in;
        parcelableWeather2.weatherSpec = new WeatherSpec();
        parcelableWeather2.reconstructedOWMForecast = null;
        int version = in.readInt();
        if (version == 2) {
            Bundle bundle = parcel.readBundle(getClass().getClassLoader());
            parcelableWeather2.weatherSpec.location = bundle.getString("weather_location");
            long time2 = bundle.getLong("weather_time");
            long queryTime = bundle.getLong("weather_query_time");
            parcelableWeather2.weatherSpec.timestamp = (int) (queryTime / 1000);
            bundle.getString("weather_forecast_url");
            int conditions = bundle.getInt("weather_conditions");
            if (conditions > 0) {
                Bundle conditionBundle = parcel.readBundle(getClass().getClassLoader());
                parcelableWeather2.weatherSpec.currentCondition = conditionBundle.getString("weather_condition_text");
                String str = "weather_condition_types";
                conditionBundle.getStringArray(str);
                parcelableWeather2.weatherSpec.currentTemp = conditionBundle.getInt("weather_current_temp");
                parcelableWeather2.weatherSpec.windDirection = parcelableWeather2.mapDirToDeg(conditionBundle.getString("weather_wind_direction"));
                int conditions2 = conditions;
                parcelableWeather2.weatherSpec.windSpeed = (float) parcelableWeather2.getSpeedInKMH(conditionBundle.getInt("weather_wind_speed"), conditionBundle.getString("weather_wind_speed_unit"));
                String[] currentConditionType = conditionBundle.getStringArray(str);
                if (currentConditionType != null) {
                    int i = version;
                    parcelableWeather2.weatherSpec.currentConditionCode = parcelableWeather2.weatherConditionTypesToOpenWeatherMapIds(currentConditionType[0]);
                }
                String str2 = "weather_low_temp";
                parcelableWeather2.weatherSpec.todayMinTemp = conditionBundle.getInt(str2);
                String str3 = "weather_high_temp";
                Bundle bundle2 = bundle;
                parcelableWeather2.weatherSpec.todayMaxTemp = conditionBundle.getInt(str3);
                String str4 = "weather_humidity_value";
                long j = queryTime;
                parcelableWeather2.weatherSpec.currentHumidity = conditionBundle.getInt(str4);
                JSONArray list = new JSONArray();
                JSONObject city = new JSONObject();
                int timeOffset = 0;
                while (true) {
                    conditions2--;
                    String[] currentConditionType2 = currentConditionType;
                    if (conditions2 <= 0) {
                        break;
                    }
                    Bundle conditionBundle2 = conditionBundle;
                    int timeOffset2 = timeOffset + CoreConstants.MILLIS_IN_ONE_DAY;
                    JSONObject item = new JSONObject();
                    JSONObject condition = new JSONObject();
                    JSONObject main2 = new JSONObject();
                    JSONArray weather = new JSONArray();
                    JSONObject city2 = city;
                    Bundle forecastBundle = parcel.readBundle(getClass().getClassLoader());
                    String[] forecastConditionType = forecastBundle.getStringArray(str);
                    if (forecastConditionType != null) {
                        forecastConditionCode = parcelableWeather2.weatherConditionTypesToOpenWeatherMapIds(forecastConditionType[0]);
                    } else {
                        forecastConditionCode = 0;
                    }
                    String str5 = str;
                    int forecastLowTemp = forecastBundle.getInt(str2);
                    String str6 = str2;
                    int forecastHighTemp = forecastBundle.getInt(str3);
                    String str7 = str3;
                    int forecastHumidity = forecastBundle.getInt(str4);
                    String str8 = str4;
                    parcelableWeather2.weatherSpec.forecasts.add(new WeatherSpec.Forecast(forecastLowTemp, forecastHighTemp, forecastConditionCode, forecastHumidity));
                    JSONObject condition2 = condition;
                    try {
                        condition2.put(PackageConfigHelper.DB_ID, forecastConditionCode);
                        condition2.put("main", forecastBundle.getString("weather_condition_text"));
                        condition2.put("description", forecastBundle.getString("weather_condition_text"));
                        condition2.put("icon", Weather.mapToOpenWeatherMapIcon(forecastConditionCode));
                        JSONArray weather2 = weather;
                        try {
                            weather2.put(condition2);
                            int i2 = forecastConditionCode;
                            try {
                                JSONObject jSONObject = condition2;
                                main = main2;
                            } catch (JSONException e) {
                                e = e;
                                JSONObject jSONObject2 = condition2;
                                time = time2;
                                JSONObject jSONObject3 = item;
                                JSONObject condition3 = main2;
                                LOG.error("error while construction JSON", (Throwable) e);
                                parcelableWeather2 = this;
                                parcel = in;
                                timeOffset = timeOffset2;
                                currentConditionType = currentConditionType2;
                                conditionBundle = conditionBundle2;
                                city = city2;
                                str = str5;
                                str2 = str6;
                                str3 = str7;
                                str4 = str8;
                                time2 = time;
                            }
                        } catch (JSONException e2) {
                            e = e2;
                            int i3 = forecastConditionCode;
                            JSONObject jSONObject4 = condition2;
                            time = time2;
                            JSONObject jSONObject5 = item;
                            JSONObject condition4 = main2;
                            LOG.error("error while construction JSON", (Throwable) e);
                            parcelableWeather2 = this;
                            parcel = in;
                            timeOffset = timeOffset2;
                            currentConditionType = currentConditionType2;
                            conditionBundle = conditionBundle2;
                            city = city2;
                            str = str5;
                            str2 = str6;
                            str3 = str7;
                            str4 = str8;
                            time2 = time;
                        }
                        try {
                            main.put("temp", forecastBundle.getInt("weather_current_temp"));
                            main.put("humidity", forecastHumidity);
                            main.put("temp_min", forecastLowTemp);
                            main.put("temp_max", forecastHighTemp);
                            try {
                                time = time2;
                                JSONObject item2 = item;
                                try {
                                    item2.put("dt", (time2 / 1000) + ((long) timeOffset2));
                                    item2.put("main", main);
                                    item2.put(DeviceService.EXTRA_WEATHER, weather2);
                                    list.put(item2);
                                } catch (JSONException e3) {
                                    e = e3;
                                }
                            } catch (JSONException e4) {
                                e = e4;
                                time = time2;
                                JSONObject jSONObject6 = item;
                                LOG.error("error while construction JSON", (Throwable) e);
                                parcelableWeather2 = this;
                                parcel = in;
                                timeOffset = timeOffset2;
                                currentConditionType = currentConditionType2;
                                conditionBundle = conditionBundle2;
                                city = city2;
                                str = str5;
                                str2 = str6;
                                str3 = str7;
                                str4 = str8;
                                time2 = time;
                            }
                        } catch (JSONException e5) {
                            e = e5;
                            time = time2;
                            JSONObject jSONObject7 = item;
                            LOG.error("error while construction JSON", (Throwable) e);
                            parcelableWeather2 = this;
                            parcel = in;
                            timeOffset = timeOffset2;
                            currentConditionType = currentConditionType2;
                            conditionBundle = conditionBundle2;
                            city = city2;
                            str = str5;
                            str2 = str6;
                            str3 = str7;
                            str4 = str8;
                            time2 = time;
                        }
                    } catch (JSONException e6) {
                        e = e6;
                        int i4 = forecastConditionCode;
                        time = time2;
                        JSONObject jSONObject8 = item;
                        JSONArray jSONArray = weather;
                        JSONObject jSONObject9 = condition2;
                        JSONObject condition5 = main2;
                        LOG.error("error while construction JSON", (Throwable) e);
                        parcelableWeather2 = this;
                        parcel = in;
                        timeOffset = timeOffset2;
                        currentConditionType = currentConditionType2;
                        conditionBundle = conditionBundle2;
                        city = city2;
                        str = str5;
                        str2 = str6;
                        str3 = str7;
                        str4 = str8;
                        time2 = time;
                    }
                    parcelableWeather2 = this;
                    parcel = in;
                    timeOffset = timeOffset2;
                    currentConditionType = currentConditionType2;
                    conditionBundle = conditionBundle2;
                    city = city2;
                    str = str5;
                    str2 = str6;
                    str3 = str7;
                    str4 = str8;
                    time2 = time;
                }
                JSONObject city3 = city;
                Bundle bundle3 = conditionBundle;
                try {
                    JSONObject city4 = city3;
                    try {
                        city4.put("name", this.weatherSpec.location);
                        city4.put("country", "World");
                        this.reconstructedOWMForecast = new JSONObject();
                        this.reconstructedOWMForecast.put("city", city4);
                        this.reconstructedOWMForecast.put("cnt", list.length());
                        this.reconstructedOWMForecast.put("list", list);
                    } catch (JSONException e7) {
                        e = e7;
                    }
                } catch (JSONException e8) {
                    e = e8;
                    JSONObject jSONObject10 = city3;
                    LOG.error("error while construction JSON", (Throwable) e);
                    LOG.debug("Forecast JSON for Webview: " + this.reconstructedOWMForecast);
                    return;
                }
                LOG.debug("Forecast JSON for Webview: " + this.reconstructedOWMForecast);
                return;
            }
            int i5 = version;
            Bundle bundle4 = bundle;
            long j2 = time2;
            long j3 = queryTime;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int weatherConditionTypesToOpenWeatherMapIds(java.lang.String r2) {
        /*
            r1 = this;
            int r0 = r2.hashCode()
            switch(r0) {
                case -2075029791: goto L_0x022d;
                case -1768893150: goto L_0x0222;
                case -1765074575: goto L_0x0218;
                case -1673670418: goto L_0x020d;
                case -1656481612: goto L_0x0202;
                case -1294753167: goto L_0x01f7;
                case -906419033: goto L_0x01ec;
                case -902600458: goto L_0x01e1;
                case -860845219: goto L_0x01d6;
                case -857026644: goto L_0x01ca;
                case -824570796: goto L_0x01be;
                case -662370916: goto L_0x01b2;
                case -650880139: goto L_0x01a6;
                case -419282123: goto L_0x019a;
                case -354093482: goto L_0x018e;
                case -194089418: goto L_0x0183;
                case -190270843: goto L_0x0178;
                case -161940826: goto L_0x016c;
                case -59990790: goto L_0x0160;
                case 69790: goto L_0x0154;
                case 71725: goto L_0x0148;
                case 2074340: goto L_0x013c;
                case 2209756: goto L_0x0130;
                case 2210276: goto L_0x0124;
                case 2366717: goto L_0x0118;
                case 2507668: goto L_0x010c;
                case 2550147: goto L_0x0100;
                case 78984891: goto L_0x00f4;
                case 79024463: goto L_0x00e8;
                case 82598225: goto L_0x00dc;
                case 132586844: goto L_0x00d0;
                case 133333589: goto L_0x00c4;
                case 136405419: goto L_0x00b8;
                case 188710039: goto L_0x00ac;
                case 261609195: goto L_0x00a0;
                case 265427770: goto L_0x0094;
                case 308778254: goto L_0x0089;
                case 359665129: goto L_0x007d;
                case 370520543: goto L_0x0071;
                case 383094568: goto L_0x0066;
                case 442315397: goto L_0x005b;
                case 959143820: goto L_0x004f;
                case 973067885: goto L_0x0044;
                case 974402623: goto L_0x0038;
                case 976886460: goto L_0x002d;
                case 1215668284: goto L_0x0021;
                case 1219486859: goto L_0x0015;
                case 1265897180: goto L_0x0009;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x0238
        L_0x0009:
            java.lang.String r0 = "SAND_WHIRLS"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 34
            goto L_0x0239
        L_0x0015:
            java.lang.String r0 = "RAIN_LIGHT"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 17
            goto L_0x0239
        L_0x0021:
            java.lang.String r0 = "RAIN_HEAVY"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 19
            goto L_0x0239
        L_0x002d:
            java.lang.String r0 = "THUNDERSTORM_LIGHT"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 3
            goto L_0x0239
        L_0x0038:
            java.lang.String r0 = "DRIZZLE_RAIN"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 14
            goto L_0x0239
        L_0x0044:
            java.lang.String r0 = "THUNDERSTORM_HEAVY"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 5
            goto L_0x0239
        L_0x004f:
            java.lang.String r0 = "CLOUDS_CLEAR"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 36
            goto L_0x0239
        L_0x005b:
            java.lang.String r0 = "THUNDERSTORM"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 4
            goto L_0x0239
        L_0x0066:
            java.lang.String r0 = "THUNDERSTORM_RAGGED"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 6
            goto L_0x0239
        L_0x0071:
            java.lang.String r0 = "RAIN_FREEZING"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 22
            goto L_0x0239
        L_0x007d:
            java.lang.String r0 = "RAIN_VERY_HEAVY"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 20
            goto L_0x0239
        L_0x0089:
            java.lang.String r0 = "THUNDERSTORM_RAIN"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 1
            goto L_0x0239
        L_0x0094:
            java.lang.String r0 = "SNOW_LIGHT"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 26
            goto L_0x0239
        L_0x00a0:
            java.lang.String r0 = "SNOW_HEAVY"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 28
            goto L_0x0239
        L_0x00ac:
            java.lang.String r0 = "CLOUDS_FEW"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 37
            goto L_0x0239
        L_0x00b8:
            java.lang.String r0 = "DRIZZLE_LIGHT"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 10
            goto L_0x0239
        L_0x00c4:
            java.lang.String r0 = "DRIZZLE_SHOWER"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 16
            goto L_0x0239
        L_0x00d0:
            java.lang.String r0 = "DRIZZLE_HEAVY"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 12
            goto L_0x0239
        L_0x00dc:
            java.lang.String r0 = "WINDY"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 46
            goto L_0x0239
        L_0x00e8:
            java.lang.String r0 = "SMOKE"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 32
            goto L_0x0239
        L_0x00f4:
            java.lang.String r0 = "SLEET"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 29
            goto L_0x0239
        L_0x0100:
            java.lang.String r0 = "SNOW"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 27
            goto L_0x0239
        L_0x010c:
            java.lang.String r0 = "RAIN"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 18
            goto L_0x0239
        L_0x0118:
            java.lang.String r0 = "MIST"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 31
            goto L_0x0239
        L_0x0124:
            java.lang.String r0 = "HAZE"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 33
            goto L_0x0239
        L_0x0130:
            java.lang.String r0 = "HAIL"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 47
            goto L_0x0239
        L_0x013c:
            java.lang.String r0 = "COLD"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 44
            goto L_0x0239
        L_0x0148:
            java.lang.String r0 = "HOT"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 45
            goto L_0x0239
        L_0x0154:
            java.lang.String r0 = "FOG"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 35
            goto L_0x0239
        L_0x0160:
            java.lang.String r0 = "THUNDERSTORM_DRIZZLE"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 8
            goto L_0x0239
        L_0x016c:
            java.lang.String r0 = "SNOW_SHOWER"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 30
            goto L_0x0239
        L_0x0178:
            java.lang.String r0 = "THUNDERSTORM_RAIN_LIGHT"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 0
            goto L_0x0239
        L_0x0183:
            java.lang.String r0 = "THUNDERSTORM_RAIN_HEAVY"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 2
            goto L_0x0239
        L_0x018e:
            java.lang.String r0 = "CLOUDS_BROKEN"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 39
            goto L_0x0239
        L_0x019a:
            java.lang.String r0 = "TORNADO"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 41
            goto L_0x0239
        L_0x01a6:
            java.lang.String r0 = "RAIN_SHOWER"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 24
            goto L_0x0239
        L_0x01b2:
            java.lang.String r0 = "CLOUDS_SCATTERED"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 38
            goto L_0x0239
        L_0x01be:
            java.lang.String r0 = "CLOUDS_OVERCAST"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 40
            goto L_0x0239
        L_0x01ca:
            java.lang.String r0 = "RAIN_SHOWER_LIGHT"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 23
            goto L_0x0239
        L_0x01d6:
            java.lang.String r0 = "RAIN_SHOWER_HEAVY"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 25
            goto L_0x0239
        L_0x01e1:
            java.lang.String r0 = "DRIZZLE_RAIN_LIGHT"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 13
            goto L_0x0239
        L_0x01ec:
            java.lang.String r0 = "DRIZZLE_RAIN_HEAVY"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 15
            goto L_0x0239
        L_0x01f7:
            java.lang.String r0 = "HURRICANE"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 43
            goto L_0x0239
        L_0x0202:
            java.lang.String r0 = "DRIZZLE"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 11
            goto L_0x0239
        L_0x020d:
            java.lang.String r0 = "TROPICAL_STORM"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 42
            goto L_0x0239
        L_0x0218:
            java.lang.String r0 = "THUNDERSTORM_DRIZZLE_LIGHT"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 7
            goto L_0x0239
        L_0x0222:
            java.lang.String r0 = "THUNDERSTORM_DRIZZLE_HEAVY"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 9
            goto L_0x0239
        L_0x022d:
            java.lang.String r0 = "RAIN_EXTREME"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 21
            goto L_0x0239
        L_0x0238:
            r0 = -1
        L_0x0239:
            switch(r0) {
                case 0: goto L_0x02cc;
                case 1: goto L_0x02c9;
                case 2: goto L_0x02c6;
                case 3: goto L_0x02c3;
                case 4: goto L_0x02c0;
                case 5: goto L_0x02bd;
                case 6: goto L_0x02ba;
                case 7: goto L_0x02b7;
                case 8: goto L_0x02b4;
                case 9: goto L_0x02b1;
                case 10: goto L_0x02ae;
                case 11: goto L_0x02ab;
                case 12: goto L_0x02a8;
                case 13: goto L_0x02a5;
                case 14: goto L_0x02a2;
                case 15: goto L_0x029f;
                case 16: goto L_0x029c;
                case 17: goto L_0x0299;
                case 18: goto L_0x0296;
                case 19: goto L_0x0293;
                case 20: goto L_0x0290;
                case 21: goto L_0x028d;
                case 22: goto L_0x028a;
                case 23: goto L_0x0287;
                case 24: goto L_0x0284;
                case 25: goto L_0x0281;
                case 26: goto L_0x027e;
                case 27: goto L_0x027b;
                case 28: goto L_0x0278;
                case 29: goto L_0x0275;
                case 30: goto L_0x0272;
                case 31: goto L_0x026f;
                case 32: goto L_0x026c;
                case 33: goto L_0x0269;
                case 34: goto L_0x0266;
                case 35: goto L_0x0263;
                case 36: goto L_0x0260;
                case 37: goto L_0x025d;
                case 38: goto L_0x025a;
                case 39: goto L_0x0257;
                case 40: goto L_0x0254;
                case 41: goto L_0x0251;
                case 42: goto L_0x024e;
                case 43: goto L_0x024b;
                case 44: goto L_0x0248;
                case 45: goto L_0x0245;
                case 46: goto L_0x0242;
                case 47: goto L_0x023f;
                default: goto L_0x023c;
            }
        L_0x023c:
            r0 = 3200(0xc80, float:4.484E-42)
            return r0
        L_0x023f:
            r0 = 906(0x38a, float:1.27E-42)
            return r0
        L_0x0242:
            r0 = 905(0x389, float:1.268E-42)
            return r0
        L_0x0245:
            r0 = 904(0x388, float:1.267E-42)
            return r0
        L_0x0248:
            r0 = 903(0x387, float:1.265E-42)
            return r0
        L_0x024b:
            r0 = 902(0x386, float:1.264E-42)
            return r0
        L_0x024e:
            r0 = 901(0x385, float:1.263E-42)
            return r0
        L_0x0251:
            r0 = 900(0x384, float:1.261E-42)
            return r0
        L_0x0254:
            r0 = 804(0x324, float:1.127E-42)
            return r0
        L_0x0257:
            r0 = 803(0x323, float:1.125E-42)
            return r0
        L_0x025a:
            r0 = 802(0x322, float:1.124E-42)
            return r0
        L_0x025d:
            r0 = 801(0x321, float:1.122E-42)
            return r0
        L_0x0260:
            r0 = 800(0x320, float:1.121E-42)
            return r0
        L_0x0263:
            r0 = 741(0x2e5, float:1.038E-42)
            return r0
        L_0x0266:
            r0 = 731(0x2db, float:1.024E-42)
            return r0
        L_0x0269:
            r0 = 721(0x2d1, float:1.01E-42)
            return r0
        L_0x026c:
            r0 = 711(0x2c7, float:9.96E-43)
            return r0
        L_0x026f:
            r0 = 701(0x2bd, float:9.82E-43)
            return r0
        L_0x0272:
            r0 = 621(0x26d, float:8.7E-43)
            return r0
        L_0x0275:
            r0 = 611(0x263, float:8.56E-43)
            return r0
        L_0x0278:
            r0 = 602(0x25a, float:8.44E-43)
            return r0
        L_0x027b:
            r0 = 601(0x259, float:8.42E-43)
            return r0
        L_0x027e:
            r0 = 600(0x258, float:8.41E-43)
            return r0
        L_0x0281:
            r0 = 522(0x20a, float:7.31E-43)
            return r0
        L_0x0284:
            r0 = 521(0x209, float:7.3E-43)
            return r0
        L_0x0287:
            r0 = 520(0x208, float:7.29E-43)
            return r0
        L_0x028a:
            r0 = 511(0x1ff, float:7.16E-43)
            return r0
        L_0x028d:
            r0 = 504(0x1f8, float:7.06E-43)
            return r0
        L_0x0290:
            r0 = 503(0x1f7, float:7.05E-43)
            return r0
        L_0x0293:
            r0 = 502(0x1f6, float:7.03E-43)
            return r0
        L_0x0296:
            r0 = 501(0x1f5, float:7.02E-43)
            return r0
        L_0x0299:
            r0 = 500(0x1f4, float:7.0E-43)
            return r0
        L_0x029c:
            r0 = 321(0x141, float:4.5E-43)
            return r0
        L_0x029f:
            r0 = 312(0x138, float:4.37E-43)
            return r0
        L_0x02a2:
            r0 = 311(0x137, float:4.36E-43)
            return r0
        L_0x02a5:
            r0 = 310(0x136, float:4.34E-43)
            return r0
        L_0x02a8:
            r0 = 302(0x12e, float:4.23E-43)
            return r0
        L_0x02ab:
            r0 = 301(0x12d, float:4.22E-43)
            return r0
        L_0x02ae:
            r0 = 300(0x12c, float:4.2E-43)
            return r0
        L_0x02b1:
            r0 = 232(0xe8, float:3.25E-43)
            return r0
        L_0x02b4:
            r0 = 231(0xe7, float:3.24E-43)
            return r0
        L_0x02b7:
            r0 = 230(0xe6, float:3.22E-43)
            return r0
        L_0x02ba:
            r0 = 221(0xdd, float:3.1E-43)
            return r0
        L_0x02bd:
            r0 = 212(0xd4, float:2.97E-43)
            return r0
        L_0x02c0:
            r0 = 211(0xd3, float:2.96E-43)
            return r0
        L_0x02c3:
            r0 = 210(0xd2, float:2.94E-43)
            return r0
        L_0x02c6:
            r0 = 202(0xca, float:2.83E-43)
            return r0
        L_0x02c9:
            r0 = 201(0xc9, float:2.82E-43)
            return r0
        L_0x02cc:
            r0 = 200(0xc8, float:2.8E-43)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: p013ru.gelin.android.weather.notification.ParcelableWeather2.weatherConditionTypesToOpenWeatherMapIds(java.lang.String):int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0046  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getSpeedInKMH(int r6, java.lang.String r7) {
        /*
            r5 = this;
            r0 = 0
            int r1 = r7.hashCode()
            r2 = 74627(0x12383, float:1.04575E-40)
            r3 = 2
            r4 = 1
            if (r1 == r2) goto L_0x002b
            r2 = 76549(0x12b05, float:1.07268E-40)
            if (r1 == r2) goto L_0x0021
            r2 = 76560(0x12b10, float:1.07283E-40)
            if (r1 == r2) goto L_0x0017
        L_0x0016:
            goto L_0x0035
        L_0x0017:
            java.lang.String r1 = "MPS"
            boolean r1 = r7.equals(r1)
            if (r1 == 0) goto L_0x0016
            r1 = 0
            goto L_0x0036
        L_0x0021:
            java.lang.String r1 = "MPH"
            boolean r1 = r7.equals(r1)
            if (r1 == 0) goto L_0x0016
            r1 = 1
            goto L_0x0036
        L_0x002b:
            java.lang.String r1 = "KPH"
            boolean r1 = r7.equals(r1)
            if (r1 == 0) goto L_0x0016
            r1 = 2
            goto L_0x0036
        L_0x0035:
            r1 = -1
        L_0x0036:
            if (r1 == 0) goto L_0x0046
            if (r1 == r4) goto L_0x003f
            if (r1 == r3) goto L_0x003d
            goto L_0x004d
        L_0x003d:
            float r0 = (float) r6
            goto L_0x004d
        L_0x003f:
            float r1 = (float) r6
            r2 = 1070464395(0x3fcdfd8b, float:1.6093)
            float r0 = r1 * r2
            goto L_0x004d
        L_0x0046:
            float r1 = (float) r6
            r2 = 1080452710(0x40666666, float:3.6)
            float r0 = r1 * r2
        L_0x004d:
            int r1 = java.lang.Math.round(r0)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: p013ru.gelin.android.weather.notification.ParcelableWeather2.getSpeedInKMH(int, java.lang.String):int");
    }

    private int mapDirToDeg(String dir) {
        return Math.round(((float) WindDirection.valueOf(dir).ordinal()) * 22.5f);
    }
}
