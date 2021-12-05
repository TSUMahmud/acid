package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.util.Pair;
import java.util.ArrayList;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;

class AppMessageHandlerObsidian extends AppMessageHandler {
    private static final String ICON_01d = "a";
    private static final String ICON_02d = "b";
    private static final String ICON_03d = "c";
    private static final String ICON_04d = "d";
    private static final String ICON_09d = "e";
    private static final String ICON_10d = "f";
    private static final String ICON_11d = "g";
    private static final String ICON_13d = "h";
    private static final String ICON_50d = "i";

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    AppMessageHandlerObsidian(java.util.UUID r9, nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol r10) {
        /*
            r8 = this;
            r8.<init>(r9, r10)
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r8.messageKeys = r0
            r0 = 3
            r1 = 1
            org.json.JSONObject r2 = r8.getAppKeys()     // Catch:{ JSONException -> 0x006b, IOException -> 0x0069 }
            java.util.Iterator r3 = r2.keys()     // Catch:{ JSONException -> 0x006b, IOException -> 0x0069 }
        L_0x0014:
            boolean r4 = r3.hasNext()     // Catch:{ JSONException -> 0x006b, IOException -> 0x0069 }
            if (r4 == 0) goto L_0x0068
            java.lang.Object r4 = r3.next()     // Catch:{ JSONException -> 0x006b, IOException -> 0x0069 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ JSONException -> 0x006b, IOException -> 0x0069 }
            r5 = -1
            int r6 = r4.hashCode()     // Catch:{ JSONException -> 0x006b, IOException -> 0x0069 }
            r7 = 2
            switch(r6) {
                case -1697720030: goto L_0x0048;
                case -1697390467: goto L_0x003e;
                case -1479458248: goto L_0x0034;
                case -1209041325: goto L_0x002a;
                default: goto L_0x0029;
            }     // Catch:{ JSONException -> 0x006b, IOException -> 0x0069 }
        L_0x0029:
            goto L_0x0051
        L_0x002a:
            java.lang.String r6 = "CONFIG_WEATHER_REFRESH"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x006b, IOException -> 0x0069 }
            if (r6 == 0) goto L_0x0029
            r5 = 0
            goto L_0x0051
        L_0x0034:
            java.lang.String r6 = "CONFIG_WEATHER_UNIT_LOCAL"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x006b, IOException -> 0x0069 }
            if (r6 == 0) goto L_0x0029
            r5 = 1
            goto L_0x0051
        L_0x003e:
            java.lang.String r6 = "MSG_KEY_WEATHER_TEMP"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x006b, IOException -> 0x0069 }
            if (r6 == 0) goto L_0x0029
            r5 = 2
            goto L_0x0051
        L_0x0048:
            java.lang.String r6 = "MSG_KEY_WEATHER_ICON"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x006b, IOException -> 0x0069 }
            if (r6 == 0) goto L_0x0029
            r5 = 3
        L_0x0051:
            if (r5 == 0) goto L_0x005a
            if (r5 == r1) goto L_0x005a
            if (r5 == r7) goto L_0x005a
            if (r5 == r0) goto L_0x005a
            goto L_0x0067
        L_0x005a:
            java.util.Map r5 = r8.messageKeys     // Catch:{ JSONException -> 0x006b, IOException -> 0x0069 }
            int r6 = r2.getInt(r4)     // Catch:{ JSONException -> 0x006b, IOException -> 0x0069 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ JSONException -> 0x006b, IOException -> 0x0069 }
            r5.put(r4, r6)     // Catch:{ JSONException -> 0x006b, IOException -> 0x0069 }
        L_0x0067:
            goto L_0x0014
        L_0x0068:
            goto L_0x0071
        L_0x0069:
            r0 = move-exception
            goto L_0x0072
        L_0x006b:
            r2 = move-exception
            java.lang.String r3 = "There was an error accessing the timestyle watchface configuration."
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r3, r1, r0)
        L_0x0071:
        L_0x0072:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.AppMessageHandlerObsidian.<init>(java.util.UUID, nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol):void");
    }

    private String getIconForConditionCode(int conditionCode, boolean isNight) {
        String iconToLoad;
        int generalCondition = conditionCode / 100;
        if (generalCondition == 2) {
            iconToLoad = ICON_11d;
        } else if (generalCondition == 3) {
            iconToLoad = ICON_09d;
        } else if (generalCondition != 5) {
            if (generalCondition != 6) {
                if (generalCondition == 7) {
                    iconToLoad = ICON_03d;
                } else if (generalCondition != 8) {
                    iconToLoad = ICON_02d;
                } else if (conditionCode == 800) {
                    iconToLoad = ICON_01d;
                } else if (conditionCode < 803) {
                    iconToLoad = ICON_02d;
                } else {
                    iconToLoad = "d";
                }
            } else if (conditionCode == 600 || conditionCode == 620) {
                iconToLoad = ICON_13d;
            } else if (conditionCode <= 610 || conditionCode >= 620) {
                iconToLoad = ICON_13d;
            } else {
                iconToLoad = ICON_13d;
            }
        } else if (conditionCode == 500) {
            iconToLoad = ICON_09d;
        } else if (conditionCode < 505) {
            iconToLoad = ICON_10d;
        } else if (conditionCode == 511) {
            iconToLoad = ICON_10d;
        } else {
            iconToLoad = ICON_09d;
        }
        return !isNight ? iconToLoad : iconToLoad.toUpperCase();
    }

    private byte[] encodeObsidianWeather(WeatherSpec weatherSpec) {
        if (weatherSpec == null) {
            return null;
        }
        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>();
        pairs.add(new Pair(this.messageKeys.get("CONFIG_WEATHER_REFRESH"), 60));
        pairs.add(new Pair(this.messageKeys.get("CONFIG_WEATHER_UNIT_LOCAL"), 1));
        pairs.add(new Pair(this.messageKeys.get("MSG_KEY_WEATHER_ICON"), getIconForConditionCode(weatherSpec.currentConditionCode, false)));
        pairs.add(new Pair(this.messageKeys.get("MSG_KEY_WEATHER_TEMP"), Integer.valueOf(weatherSpec.currentTemp - 273)));
        return this.mPebbleProtocol.encodeApplicationMessagePush(48, this.mUUID, pairs, (Integer) null);
    }

    public GBDeviceEvent[] onAppStart() {
        WeatherSpec weatherSpec = Weather.getInstance().getWeatherSpec();
        if (weatherSpec == null) {
            return new GBDeviceEvent[]{null};
        }
        GBDeviceEventSendBytes sendBytes = new GBDeviceEventSendBytes();
        sendBytes.encodedBytes = encodeObsidianWeather(weatherSpec);
        return new GBDeviceEvent[]{sendBytes};
    }

    public byte[] encodeUpdateWeather(WeatherSpec weatherSpec) {
        return encodeObsidianWeather(weatherSpec);
    }
}
