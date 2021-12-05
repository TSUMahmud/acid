package nodomain.freeyourgadget.gadgetbridge.service.devices.banglejs;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import cyanogenmod.providers.ThemesContract;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.devices.banglejs.BangleJSConstants;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.util.AlarmUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.rolling.helper.DateTokenConverter;

public class BangleJSDeviceSupport extends AbstractBTLEDeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) BangleJSDeviceSupport.class);
    private String receivedLine = "";
    private BluetoothGattCharacteristic rxCharacteristic = null;
    private BluetoothGattCharacteristic txCharacteristic = null;

    public BangleJSDeviceSupport() {
        super(LOG);
        addSupportedService(BangleJSConstants.UUID_SERVICE_NORDIC_UART);
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        LOG.info("Initializing");
        this.gbDevice.setState(GBDevice.State.INITIALIZING);
        this.gbDevice.sendDeviceUpdateIntent(getContext());
        this.rxCharacteristic = getCharacteristic(BangleJSConstants.UUID_CHARACTERISTIC_NORDIC_UART_RX);
        this.txCharacteristic = getCharacteristic(BangleJSConstants.UUID_CHARACTERISTIC_NORDIC_UART_TX);
        builder.setGattCallback(this);
        builder.notify(this.rxCharacteristic, true);
        uartTx(builder, " \u0003");
        setTime(builder);
        this.gbDevice.setState(GBDevice.State.INITIALIZED);
        this.gbDevice.sendDeviceUpdateIntent(getContext());
        LOG.info("Initialization Done");
        return builder;
    }

    private void uartTx(TransactionBuilder builder, String str) {
        Logger logger = LOG;
        logger.info("UART TX: " + str);
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < bytes.length; i += 20) {
            int l = bytes.length - i;
            if (l > 20) {
                l = 20;
            }
            byte[] packet = new byte[l];
            System.arraycopy(bytes, i, packet, 0, l);
            builder.write(this.txCharacteristic, packet);
        }
    }

    private void uartTxJSON(String taskName, JSONObject json) {
        try {
            TransactionBuilder builder = performInitialized(taskName);
            uartTx(builder, "\u0010GB(" + json.toString() + ")\n");
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error in " + taskName + ": " + e.getLocalizedMessage(), 1, 3);
        }
    }

    private void handleUartRxLine(String line) {
        Logger logger = LOG;
        logger.info("UART RX LINE: " + line);
        if (">Uncaught ReferenceError: \"gb\" is not defined".equals(line)) {
            C1238GB.toast(getContext(), "Gadgetbridge plugin not installed on Bangle.js", 1, 3);
        } else if (line.charAt(0) == '{') {
            try {
                handleUartRxJSON(new JSONObject(line));
            } catch (JSONException e) {
                Context context = getContext();
                C1238GB.toast(context, "Malformed JSON from Bangle.js: " + e.getLocalizedMessage(), 1, 3);
            }
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void handleUartRxJSON(org.json.JSONObject r9) throws org.json.JSONException {
        /*
            r8 = this;
            java.lang.String r0 = "t"
            java.lang.String r0 = r9.getString(r0)
            int r1 = r0.hashCode()
            r2 = 3
            r3 = 0
            r4 = 2
            r5 = 1
            switch(r1) {
                case -1039689911: goto L_0x0058;
                case -892481550: goto L_0x004e;
                case 3045982: goto L_0x0044;
                case 3237038: goto L_0x003a;
                case 3641990: goto L_0x0030;
                case 96784904: goto L_0x0026;
                case 104263205: goto L_0x001c;
                case 415892277: goto L_0x0012;
                default: goto L_0x0011;
            }
        L_0x0011:
            goto L_0x0062
        L_0x0012:
            java.lang.String r1 = "findPhone"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0011
            r0 = 4
            goto L_0x0063
        L_0x001c:
            java.lang.String r1 = "music"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0011
            r0 = 5
            goto L_0x0063
        L_0x0026:
            java.lang.String r1 = "error"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0011
            r0 = 2
            goto L_0x0063
        L_0x0030:
            java.lang.String r1 = "warn"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0011
            r0 = 1
            goto L_0x0063
        L_0x003a:
            java.lang.String r1 = "info"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0011
            r0 = 0
            goto L_0x0063
        L_0x0044:
            java.lang.String r1 = "call"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0011
            r0 = 6
            goto L_0x0063
        L_0x004e:
            java.lang.String r1 = "status"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0011
            r0 = 3
            goto L_0x0063
        L_0x0058:
            java.lang.String r1 = "notify"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0011
            r0 = 7
            goto L_0x0063
        L_0x0062:
            r0 = -1
        L_0x0063:
            java.lang.String r1 = "Bangle.js: "
            java.lang.String r6 = "n"
            java.lang.String r7 = "msg"
            switch(r0) {
                case 0: goto L_0x019f;
                case 1: goto L_0x0184;
                case 2: goto L_0x0169;
                case 3: goto L_0x0100;
                case 4: goto L_0x00df;
                case 5: goto L_0x00c7;
                case 6: goto L_0x00af;
                case 7: goto L_0x006e;
                default: goto L_0x006c;
            }
        L_0x006c:
            goto L_0x01ba
        L_0x006e:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventNotificationControl r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventNotificationControl
            r0.<init>()
            java.lang.String r1 = r9.getString(r6)
            java.lang.String r1 = r1.toUpperCase()
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventNotificationControl$Event r1 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventNotificationControl.Event.valueOf(r1)
            r0.event = r1
            java.lang.String r1 = "id"
            boolean r2 = r9.has(r1)
            if (r2 == 0) goto L_0x0090
            int r1 = r9.getInt(r1)
            long r1 = (long) r1
            r0.handle = r1
        L_0x0090:
            java.lang.String r1 = "tel"
            boolean r2 = r9.has(r1)
            if (r2 == 0) goto L_0x009e
            java.lang.String r1 = r9.getString(r1)
            r0.phoneNumber = r1
        L_0x009e:
            boolean r1 = r9.has(r7)
            if (r1 == 0) goto L_0x00aa
            java.lang.String r1 = r9.getString(r7)
            r0.reply = r1
        L_0x00aa:
            r8.evaluateGBDeviceEvent(r0)
            goto L_0x01ba
        L_0x00af:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventCallControl r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventCallControl
            r0.<init>()
            java.lang.String r1 = r9.getString(r6)
            java.lang.String r1 = r1.toUpperCase()
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventCallControl$Event r1 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventCallControl.Event.valueOf(r1)
            r0.event = r1
            r8.evaluateGBDeviceEvent(r0)
            goto L_0x01ba
        L_0x00c7:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl r0 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl
            r0.<init>()
            java.lang.String r1 = r9.getString(r6)
            java.lang.String r1 = r1.toUpperCase()
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl$Event r1 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl.Event.valueOf(r1)
            r0.event = r1
            r8.evaluateGBDeviceEvent(r0)
            goto L_0x01ba
        L_0x00df:
            boolean r0 = r9.has(r6)
            if (r0 == 0) goto L_0x00ec
            boolean r0 = r9.getBoolean(r6)
            if (r0 == 0) goto L_0x00ec
            r3 = 1
        L_0x00ec:
            r0 = r3
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventFindPhone r1 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventFindPhone
            r1.<init>()
            if (r0 == 0) goto L_0x00f7
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventFindPhone$Event r2 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventFindPhone.Event.START
            goto L_0x00f9
        L_0x00f7:
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventFindPhone$Event r2 = nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventFindPhone.Event.STOP
        L_0x00f9:
            r1.event = r2
            r8.evaluateGBDeviceEvent(r1)
            goto L_0x01ba
        L_0x0100:
            android.content.Context r0 = r8.getContext()
            java.lang.String r1 = "bat"
            boolean r2 = r9.has(r1)
            if (r2 == 0) goto L_0x0151
            int r1 = r9.getInt(r1)
            if (r1 >= 0) goto L_0x0113
            r1 = 0
        L_0x0113:
            r2 = 100
            if (r1 <= r2) goto L_0x0119
            r1 = 100
        L_0x0119:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r2 = r8.gbDevice
            short r6 = (short) r1
            r2.setBatteryLevel(r6)
            r2 = 30
            if (r1 >= r2) goto L_0x0147
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r2 = r8.gbDevice
            nodomain.freeyourgadget.gadgetbridge.model.BatteryState r6 = nodomain.freeyourgadget.gadgetbridge.model.BatteryState.BATTERY_LOW
            r2.setBatteryState(r6)
            r2 = 2131755449(0x7f1001b9, float:1.9141778E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r6 = r8.gbDevice
            java.lang.String r6 = r6.getName()
            r4[r3] = r6
            java.lang.String r3 = java.lang.String.valueOf(r1)
            r4[r5] = r3
            java.lang.String r2 = r0.getString(r2, r4)
            java.lang.String r3 = ""
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.updateBatteryNotification(r2, r3, r0)
            goto L_0x0151
        L_0x0147:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r2 = r8.gbDevice
            nodomain.freeyourgadget.gadgetbridge.model.BatteryState r3 = nodomain.freeyourgadget.gadgetbridge.model.BatteryState.BATTERY_NORMAL
            r2.setBatteryState(r3)
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.removeBatteryNotification(r0)
        L_0x0151:
            java.lang.String r1 = "volt"
            boolean r2 = r9.has(r1)
            if (r2 == 0) goto L_0x0163
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r2 = r8.gbDevice
            double r3 = r9.getDouble(r1)
            float r1 = (float) r3
            r2.setBatteryVoltage(r1)
        L_0x0163:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r1 = r8.gbDevice
            r1.sendDeviceUpdateIntent(r0)
            goto L_0x01ba
        L_0x0169:
            android.content.Context r0 = r8.getContext()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r1)
            java.lang.String r1 = r9.getString(r7)
            r3.append(r1)
            java.lang.String r1 = r3.toString()
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r0, (java.lang.String) r1, (int) r5, (int) r2)
            goto L_0x01ba
        L_0x0184:
            android.content.Context r0 = r8.getContext()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r1)
            java.lang.String r1 = r9.getString(r7)
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r0, (java.lang.String) r1, (int) r5, (int) r4)
            goto L_0x01ba
        L_0x019f:
            android.content.Context r0 = r8.getContext()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r1)
            java.lang.String r1 = r9.getString(r7)
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r0, (java.lang.String) r1, (int) r5, (int) r5)
        L_0x01ba:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.banglejs.BangleJSDeviceSupport.handleUartRxJSON(org.json.JSONObject):void");
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (super.onCharacteristicChanged(gatt, characteristic)) {
            return true;
        }
        if (BangleJSConstants.UUID_CHARACTERISTIC_NORDIC_UART_RX.equals(characteristic.getUuid())) {
            String packetStr = new String(characteristic.getValue());
            LOG.info("RX: " + packetStr);
            this.receivedLine += packetStr;
            while (this.receivedLine.contains(StringUtils.f210LF)) {
                int p = this.receivedLine.indexOf(StringUtils.f210LF);
                String line = this.receivedLine.substring(0, p - 1);
                this.receivedLine = this.receivedLine.substring(p + 1);
                handleUartRxLine(line);
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setTime(TransactionBuilder builder) {
        uartTx(builder, "\u0010setTime(" + (System.currentTimeMillis() / 1000) + ");E.setTimeZone(" + (TimeZone.getDefault().getRawOffset() / CoreConstants.MILLIS_IN_ONE_HOUR) + ");\n");
    }

    public boolean useAutoConnect() {
        return true;
    }

    public void onNotification(NotificationSpec notificationSpec) {
        try {
            JSONObject o = new JSONObject();
            o.put("t", "notify");
            o.put(PackageConfigHelper.DB_ID, notificationSpec.getId());
            o.put("src", notificationSpec.sourceName);
            o.put(ThemesContract.ThemesColumns.TITLE, notificationSpec.title);
            o.put("subject", notificationSpec.subject);
            o.put("body", notificationSpec.body);
            o.put("sender", notificationSpec.sender);
            o.put("tel", notificationSpec.phoneNumber);
            uartTxJSON("onNotification", o);
        } catch (JSONException e) {
            Logger logger = LOG;
            logger.info("JSONException: " + e.getLocalizedMessage());
        }
    }

    public void onDeleteNotification(int id) {
        try {
            JSONObject o = new JSONObject();
            o.put("t", "notify-");
            o.put(PackageConfigHelper.DB_ID, id);
            uartTxJSON("onDeleteNotification", o);
        } catch (JSONException e) {
            Logger logger = LOG;
            logger.info("JSONException: " + e.getLocalizedMessage());
        }
    }

    public void onSetTime() {
        try {
            TransactionBuilder builder = performInitialized("setTime");
            setTime(builder);
            builder.queue(getQueue());
        } catch (Exception e) {
            Context context = getContext();
            C1238GB.toast(context, "Error setting time: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        try {
            JSONObject o = new JSONObject();
            o.put("t", "alarm");
            JSONArray jsonalarms = new JSONArray();
            o.put(DateTokenConverter.CONVERTER_KEY, jsonalarms);
            Iterator<? extends Alarm> it = alarms.iterator();
            while (it.hasNext()) {
                Alarm alarm = (Alarm) it.next();
                if (alarm.getEnabled()) {
                    JSONObject jsonalarm = new JSONObject();
                    jsonalarms.put(jsonalarm);
                    Calendar calendar = AlarmUtils.toCalendar(alarm);
                    jsonalarm.put("h", alarm.getHour());
                    jsonalarm.put("m", alarm.getMinute());
                }
            }
            uartTxJSON("onSetAlarms", o);
        } catch (JSONException e) {
            Logger logger = LOG;
            logger.info("JSONException: " + e.getLocalizedMessage());
        }
    }

    public void onSetCallState(CallSpec callSpec) {
        try {
            JSONObject o = new JSONObject();
            o.put("t", NotificationCompat.CATEGORY_CALL);
            o.put("cmd", new String[]{"", "undefined", "accept", "incoming", "outgoing", "reject", "start", "end"}[callSpec.command]);
            o.put("name", callSpec.name);
            o.put("number", callSpec.number);
            uartTxJSON("onSetCallState", o);
        } catch (JSONException e) {
            Logger logger = LOG;
            logger.info("JSONException: " + e.getLocalizedMessage());
        }
    }

    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
    }

    public void onSetMusicState(MusicStateSpec stateSpec) {
        try {
            JSONObject o = new JSONObject();
            o.put("t", "musicstate");
            o.put("state", new String[]{"play", "pause", "stop", ""}[stateSpec.state]);
            o.put("position", stateSpec.position);
            o.put("shuffle", stateSpec.shuffle);
            o.put("repeat", stateSpec.repeat);
            uartTxJSON("onSetMusicState", o);
        } catch (JSONException e) {
            Logger logger = LOG;
            logger.info("JSONException: " + e.getLocalizedMessage());
        }
    }

    public void onSetMusicInfo(MusicSpec musicSpec) {
        try {
            JSONObject o = new JSONObject();
            o.put("t", "musicinfo");
            o.put("artist", musicSpec.artist);
            o.put("album", musicSpec.album);
            o.put("track", musicSpec.track);
            o.put("dur", musicSpec.duration);
            o.put("c", musicSpec.trackCount);
            o.put("n", musicSpec.trackNr);
            uartTxJSON("onSetMusicInfo", o);
        } catch (JSONException e) {
            Logger logger = LOG;
            logger.info("JSONException: " + e.getLocalizedMessage());
        }
    }

    public void onEnableRealtimeSteps(boolean enable) {
    }

    public void onInstallApp(Uri uri) {
    }

    public void onAppInfoReq() {
    }

    public void onAppStart(UUID uuid, boolean start) {
    }

    public void onAppDelete(UUID uuid) {
    }

    public void onAppConfiguration(UUID appUuid, String config, Integer id) {
    }

    public void onAppReorder(UUID[] uuids) {
    }

    public void onFetchRecordedData(int dataTypes) {
    }

    public void onReset(int flags) {
    }

    public void onHeartRateTest() {
    }

    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
    }

    public void onFindDevice(boolean start) {
        try {
            JSONObject o = new JSONObject();
            o.put("t", "find");
            o.put("n", start);
            uartTxJSON("onFindDevice", o);
        } catch (JSONException e) {
            Logger logger = LOG;
            logger.info("JSONException: " + e.getLocalizedMessage());
        }
    }

    public void onSetConstantVibration(int integer) {
        try {
            JSONObject o = new JSONObject();
            o.put("t", "vibrate");
            o.put("n", integer);
            uartTxJSON("onSetConstantVibration", o);
        } catch (JSONException e) {
            Logger logger = LOG;
            logger.info("JSONException: " + e.getLocalizedMessage());
        }
    }

    public void onScreenshotReq() {
    }

    public void onEnableHeartRateSleepSupport(boolean enable) {
    }

    public void onSetHeartRateMeasurementInterval(int seconds) {
    }

    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
    }

    public void onDeleteCalendarEvent(byte type, long id) {
    }

    public void onSendConfiguration(String config) {
    }

    public void onReadConfiguration(String config) {
    }

    public void onTestNewFunction() {
    }

    public void onSendWeather(WeatherSpec weatherSpec) {
        try {
            JSONObject o = new JSONObject();
            o.put("t", DeviceService.EXTRA_WEATHER);
            o.put("temp", weatherSpec.currentTemp);
            o.put("hum", weatherSpec.currentHumidity);
            o.put("txt", weatherSpec.currentCondition);
            o.put("wind", (double) weatherSpec.windSpeed);
            o.put("loc", weatherSpec.location);
            uartTxJSON("onSendWeather", o);
        } catch (JSONException e) {
            Logger logger = LOG;
            logger.info("JSONException: " + e.getLocalizedMessage());
        }
    }
}
