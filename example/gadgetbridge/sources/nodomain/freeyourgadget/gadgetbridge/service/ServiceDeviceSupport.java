package nodomain.freeyourgadget.gadgetbridge.service;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.Uri;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDeviceSupport implements DeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) ServiceDeviceSupport.class);
    private static final long THROTTLING_THRESHOLD = 1000;
    private final DeviceSupport delegate;
    private final EnumSet<Flags> flags;
    private String lastNotificationKind;
    private long lastNotificationTime = 0;

    enum Flags {
        THROTTLING,
        BUSY_CHECKING
    }

    public ServiceDeviceSupport(DeviceSupport delegate2, EnumSet<Flags> flags2) {
        this.delegate = delegate2;
        this.flags = flags2;
    }

    public void setContext(GBDevice gbDevice, BluetoothAdapter btAdapter, Context context) {
        this.delegate.setContext(gbDevice, btAdapter, context);
    }

    public boolean isConnected() {
        return this.delegate.isConnected();
    }

    public boolean connectFirstTime() {
        return this.delegate.connectFirstTime();
    }

    public boolean connect() {
        return this.delegate.connect();
    }

    public void setAutoReconnect(boolean enable) {
        this.delegate.setAutoReconnect(enable);
    }

    public boolean getAutoReconnect() {
        return this.delegate.getAutoReconnect();
    }

    public void dispose() {
        this.delegate.dispose();
    }

    public GBDevice getDevice() {
        return this.delegate.getDevice();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return this.delegate.getBluetoothAdapter();
    }

    public Context getContext() {
        return this.delegate.getContext();
    }

    public String customStringFilter(String inputString) {
        return this.delegate.customStringFilter(inputString);
    }

    public boolean useAutoConnect() {
        return this.delegate.useAutoConnect();
    }

    private boolean checkBusy(String notificationKind) {
        if (!this.flags.contains(Flags.BUSY_CHECKING) || !getDevice().isBusy()) {
            return false;
        }
        Logger logger = LOG;
        logger.info("Ignoring " + notificationKind + " because we're busy with " + getDevice().getBusyTask());
        return true;
    }

    private boolean checkThrottle(String notificationKind) {
        if (!this.flags.contains(Flags.THROTTLING)) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastNotificationTime >= 1000 || notificationKind == null || !notificationKind.equals(this.lastNotificationKind)) {
            this.lastNotificationTime = currentTime;
            this.lastNotificationKind = notificationKind;
            return false;
        }
        Logger logger = LOG;
        logger.info("Ignoring " + notificationKind + " because of throttling threshold reached");
        return true;
    }

    public void onNotification(NotificationSpec notificationSpec) {
        if (!checkBusy("generic notification") && !checkThrottle("generic notification")) {
            this.delegate.onNotification(notificationSpec);
        }
    }

    public void onDeleteNotification(int id) {
        this.delegate.onDeleteNotification(id);
    }

    public void onSetTime() {
        if (!checkBusy("set time") && !checkThrottle("set time")) {
            this.delegate.onSetTime();
        }
    }

    public void onSetCallState(CallSpec callSpec) {
        if (!checkBusy("set call state")) {
            this.delegate.onSetCallState(callSpec);
        }
    }

    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
        if (!checkBusy("set canned messages")) {
            this.delegate.onSetCannedMessages(cannedMessagesSpec);
        }
    }

    public void onSetMusicState(MusicStateSpec stateSpec) {
        if (!checkBusy("set music state")) {
            this.delegate.onSetMusicState(stateSpec);
        }
    }

    public void onSetMusicInfo(MusicSpec musicSpec) {
        if (!checkBusy("set music info")) {
            this.delegate.onSetMusicInfo(musicSpec);
        }
    }

    public void onInstallApp(Uri uri) {
        if (!checkBusy("install app")) {
            this.delegate.onInstallApp(uri);
        }
    }

    public void onAppInfoReq() {
        if (!checkBusy("app info request")) {
            this.delegate.onAppInfoReq();
        }
    }

    public void onAppStart(UUID uuid, boolean start) {
        if (!checkBusy("app start")) {
            this.delegate.onAppStart(uuid, start);
        }
    }

    public void onAppDelete(UUID uuid) {
        if (!checkBusy("app delete")) {
            this.delegate.onAppDelete(uuid);
        }
    }

    public void onAppConfiguration(UUID uuid, String config, Integer id) {
        if (!checkBusy("app configuration")) {
            this.delegate.onAppConfiguration(uuid, config, id);
        }
    }

    public void onAppReorder(UUID[] uuids) {
        if (!checkBusy("app reorder")) {
            this.delegate.onAppReorder(uuids);
        }
    }

    public void onFetchRecordedData(int dataTypes) {
        if (!checkBusy("fetch activity data")) {
            this.delegate.onFetchRecordedData(dataTypes);
        }
    }

    public void onReset(int flags2) {
        if (!checkBusy("reset")) {
            this.delegate.onReset(flags2);
        }
    }

    public void onHeartRateTest() {
        if (!checkBusy("heartrate")) {
            this.delegate.onHeartRateTest();
        }
    }

    public void onFindDevice(boolean start) {
        if (!checkBusy("find device")) {
            this.delegate.onFindDevice(start);
        }
    }

    public void onSetConstantVibration(int intensity) {
        if (!checkBusy("set constant vibration")) {
            this.delegate.onSetConstantVibration(intensity);
        }
    }

    public void onScreenshotReq() {
        if (!checkBusy("request screenshot")) {
            this.delegate.onScreenshotReq();
        }
    }

    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        if (!checkBusy("set alarms")) {
            this.delegate.onSetAlarms(alarms);
        }
    }

    public void onEnableRealtimeSteps(boolean enable) {
        if (!checkBusy("enable realtime steps: " + enable)) {
            this.delegate.onEnableRealtimeSteps(enable);
        }
    }

    public void onEnableHeartRateSleepSupport(boolean enable) {
        if (!checkBusy("enable heart rate sleep support: " + enable)) {
            this.delegate.onEnableHeartRateSleepSupport(enable);
        }
    }

    public void onSetHeartRateMeasurementInterval(int seconds) {
        if (!checkBusy("set heart rate measurement interval: " + seconds + "s")) {
            this.delegate.onSetHeartRateMeasurementInterval(seconds);
        }
    }

    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
        if (!checkBusy("enable realtime heart rate measurement: " + enable)) {
            this.delegate.onEnableRealtimeHeartRateMeasurement(enable);
        }
    }

    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
        if (!checkBusy("add calendar event")) {
            this.delegate.onAddCalendarEvent(calendarEventSpec);
        }
    }

    public void onDeleteCalendarEvent(byte type, long id) {
        if (!checkBusy("delete calendar event")) {
            this.delegate.onDeleteCalendarEvent(type, id);
        }
    }

    public void onSendConfiguration(String config) {
        if (!checkBusy("send configuration: " + config)) {
            this.delegate.onSendConfiguration(config);
        }
    }

    public void onReadConfiguration(String config) {
        if (!checkBusy("read configuration: " + config)) {
            this.delegate.onReadConfiguration(config);
        }
    }

    public void onTestNewFunction() {
        if (!checkBusy("test new function event")) {
            this.delegate.onTestNewFunction();
        }
    }

    public void onSendWeather(WeatherSpec weatherSpec) {
        if (!checkBusy("send weather event")) {
            this.delegate.onSendWeather(weatherSpec);
        }
    }

    public void onSetFmFrequency(float frequency) {
        if (!checkBusy("set frequency event")) {
            this.delegate.onSetFmFrequency(frequency);
        }
    }

    public void onSetLedColor(int color) {
        if (!checkBusy("set led color event")) {
            this.delegate.onSetLedColor(color);
        }
    }
}
