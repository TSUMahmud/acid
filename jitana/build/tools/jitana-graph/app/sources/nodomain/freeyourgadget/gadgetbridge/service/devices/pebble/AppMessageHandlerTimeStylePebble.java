package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.util.Pair;
import java.util.ArrayList;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;

class AppMessageHandlerTimeStylePebble extends AppMessageHandler {
    private static final int ICON_CLEAR_DAY = 0;
    private static final int ICON_CLEAR_NIGHT = 1;
    private static final int ICON_CLOUDY_DAY = 2;
    private static final int ICON_HEAVY_RAIN = 3;
    private static final int ICON_HEAVY_SNOW = 4;
    private static final int ICON_LIGHT_RAIN = 5;
    private static final int ICON_LIGHT_SNOW = 6;
    private static final int ICON_PARTLY_CLOUDY = 8;
    private static final int ICON_PARTLY_CLOUDY_NIGHT = 7;
    private static final int ICON_RAINING_AND_SNOWING = 9;
    private static final int ICON_THUNDERSTORM = 10;
    private static final int ICON_WEATHER_GENERIC = 11;

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    AppMessageHandlerTimeStylePebble(java.util.UUID r8, nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol r9) {
        /*
            r7 = this;
            r7.<init>(r8, r9)
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r7.messageKeys = r0
            r0 = 3
            r1 = 1
            org.json.JSONObject r2 = r7.getAppKeys()     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
            java.util.Iterator r3 = r2.keys()     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
        L_0x0014:
            boolean r4 = r3.hasNext()     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
            if (r4 == 0) goto L_0x0080
            java.lang.Object r4 = r3.next()     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
            r5 = -1
            int r6 = r4.hashCode()     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
            switch(r6) {
                case -1882771200: goto L_0x0065;
                case -1485967138: goto L_0x005b;
                case -1346286681: goto L_0x0051;
                case -1169648185: goto L_0x0047;
                case -794098599: goto L_0x003d;
                case 1301275557: goto L_0x0033;
                case 1527013356: goto L_0x0029;
                default: goto L_0x0028;
            }     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
        L_0x0028:
            goto L_0x006e
        L_0x0029:
            java.lang.String r6 = "WeatherForecastCondition"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
            if (r6 == 0) goto L_0x0028
            r5 = 1
            goto L_0x006e
        L_0x0033:
            java.lang.String r6 = "WeatherForecastHighTemp"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
            if (r6 == 0) goto L_0x0028
            r5 = 2
            goto L_0x006e
        L_0x003d:
            java.lang.String r6 = "WeatherForecastLowTemp"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
            if (r6 == 0) goto L_0x0028
            r5 = 3
            goto L_0x006e
        L_0x0047:
            java.lang.String r6 = "SettingUseMetric"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
            if (r6 == 0) goto L_0x0028
            r5 = 5
            goto L_0x006e
        L_0x0051:
            java.lang.String r6 = "WeatherCondition"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
            if (r6 == 0) goto L_0x0028
            r5 = 0
            goto L_0x006e
        L_0x005b:
            java.lang.String r6 = "WeatherUseNightIcon"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
            if (r6 == 0) goto L_0x0028
            r5 = 6
            goto L_0x006e
        L_0x0065:
            java.lang.String r6 = "WeatherTemperature"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
            if (r6 == 0) goto L_0x0028
            r5 = 4
        L_0x006e:
            switch(r5) {
                case 0: goto L_0x0072;
                case 1: goto L_0x0072;
                case 2: goto L_0x0072;
                case 3: goto L_0x0072;
                case 4: goto L_0x0072;
                case 5: goto L_0x0072;
                case 6: goto L_0x0072;
                default: goto L_0x0071;
            }     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
        L_0x0071:
            goto L_0x007f
        L_0x0072:
            java.util.Map r5 = r7.messageKeys     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
            int r6 = r2.getInt(r4)     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
            r5.put(r4, r6)     // Catch:{ JSONException -> 0x0083, IOException -> 0x0081 }
        L_0x007f:
            goto L_0x0014
        L_0x0080:
            goto L_0x0089
        L_0x0081:
            r0 = move-exception
            goto L_0x008a
        L_0x0083:
            r2 = move-exception
            java.lang.String r3 = "There was an error accessing the timestyle watchface configuration."
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r3, r1, r0)
        L_0x0089:
        L_0x008a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.AppMessageHandlerTimeStylePebble.<init>(java.util.UUID, nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol):void");
    }

    private int getIconForConditionCode(int conditionCode, boolean isNight) {
        int generalCondition = conditionCode / 100;
        if (generalCondition == 2) {
            return 10;
        }
        if (generalCondition == 3) {
            return 5;
        }
        if (generalCondition != 5) {
            if (generalCondition != 6) {
                if (generalCondition == 7) {
                    return 2;
                }
                if (generalCondition != 8) {
                    return 11;
                }
                if (conditionCode == 800) {
                    return (int) isNight;
                }
                if (conditionCode >= 803) {
                    return 2;
                }
                if (!isNight) {
                    return 8;
                }
                return 7;
            } else if (conditionCode == 600 || conditionCode == 620) {
                return 6;
            } else {
                if (conditionCode <= 610 || conditionCode >= 620) {
                    return 4;
                }
                return 9;
            }
        } else if (conditionCode == 500) {
            return 5;
        } else {
            if (conditionCode < 505) {
                return 3;
            }
            if (conditionCode == 511) {
                return 9;
            }
            return 5;
        }
    }

    private byte[] encodeTimeStylePebbleWeather(WeatherSpec weatherSpec) {
        if (weatherSpec == null) {
            return null;
        }
        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>();
        pairs.add(new Pair(this.messageKeys.get("SettingUseMetric"), 1));
        pairs.add(new Pair(this.messageKeys.get("WeatherUseNightIcon"), 0));
        pairs.add(new Pair(this.messageKeys.get("WeatherTemperature"), Integer.valueOf(weatherSpec.currentTemp - 273)));
        pairs.add(new Pair(this.messageKeys.get("WeatherCondition"), Integer.valueOf(getIconForConditionCode(weatherSpec.currentConditionCode, false))));
        if (weatherSpec.forecasts.size() > 0) {
            pairs.add(new Pair(this.messageKeys.get("WeatherForecastCondition"), Integer.valueOf(getIconForConditionCode(weatherSpec.forecasts.get(0).conditionCode, false))));
        }
        pairs.add(new Pair(this.messageKeys.get("WeatherForecastHighTemp"), Integer.valueOf(weatherSpec.todayMaxTemp - 273)));
        pairs.add(new Pair(this.messageKeys.get("WeatherForecastLowTemp"), Integer.valueOf(weatherSpec.todayMinTemp - 273)));
        return this.mPebbleProtocol.encodeApplicationMessagePush(48, this.mUUID, pairs, (Integer) null);
    }

    public GBDeviceEvent[] onAppStart() {
        WeatherSpec weatherSpec = Weather.getInstance().getWeatherSpec();
        if (weatherSpec == null) {
            return new GBDeviceEvent[]{null};
        }
        GBDeviceEventSendBytes sendBytes = new GBDeviceEventSendBytes();
        sendBytes.encodedBytes = encodeTimeStylePebbleWeather(weatherSpec);
        return new GBDeviceEvent[]{sendBytes};
    }

    public byte[] encodeUpdateWeather(WeatherSpec weatherSpec) {
        return encodeTimeStylePebbleWeather(weatherSpec);
    }
}
