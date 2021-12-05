package nodomain.freeyourgadget.gadgetbridge.impl;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.util.ArrayList;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.DeviceCommunicationService;
import nodomain.freeyourgadget.gadgetbridge.util.JavaExtensions;
import nodomain.freeyourgadget.gadgetbridge.util.LanguageUtils;
import nodomain.freeyourgadget.gadgetbridge.util.RtlUtils;

public class GBDeviceService implements DeviceService {
    protected final Context mContext;
    private final Class<? extends Service> mServiceClass;
    private final String[] transliterationExtras = {DeviceService.EXTRA_NOTIFICATION_PHONENUMBER, DeviceService.EXTRA_NOTIFICATION_SENDER, DeviceService.EXTRA_NOTIFICATION_SUBJECT, DeviceService.EXTRA_NOTIFICATION_TITLE, DeviceService.EXTRA_NOTIFICATION_BODY, DeviceService.EXTRA_NOTIFICATION_SOURCENAME, DeviceService.EXTRA_CALL_PHONENUMBER, DeviceService.EXTRA_CALL_DISPLAYNAME, DeviceService.EXTRA_MUSIC_ARTIST, DeviceService.EXTRA_MUSIC_ALBUM, DeviceService.EXTRA_MUSIC_TRACK, DeviceService.EXTRA_CALENDAREVENT_TITLE, DeviceService.EXTRA_CALENDAREVENT_DESCRIPTION};

    public GBDeviceService(Context context) {
        this.mContext = context;
        this.mServiceClass = DeviceCommunicationService.class;
    }

    /* access modifiers changed from: protected */
    public Intent createIntent() {
        return new Intent(this.mContext, this.mServiceClass);
    }

    /* access modifiers changed from: protected */
    public void invokeService(Intent intent) {
        if (LanguageUtils.transliterate()) {
            for (String extra : this.transliterationExtras) {
                if (intent.hasExtra(extra)) {
                    intent.putExtra(extra, LanguageUtils.transliterate(intent.getStringExtra(extra)));
                }
            }
        }
        if (RtlUtils.rtlSupport()) {
            for (String extra2 : this.transliterationExtras) {
                if (intent.hasExtra(extra2)) {
                    intent.putExtra(extra2, RtlUtils.fixRtl(intent.getStringExtra(extra2)));
                }
            }
        }
        this.mContext.startService(intent);
    }

    /* access modifiers changed from: protected */
    public void stopService(Intent intent) {
        this.mContext.stopService(intent);
    }

    public void start() {
        invokeService(createIntent().setAction(DeviceService.ACTION_START));
    }

    public void connect() {
        connect((GBDevice) null, false);
    }

    public void connect(GBDevice device) {
        connect(device, false);
    }

    public void connect(GBDevice device, boolean firstTime) {
        invokeService(createIntent().setAction(DeviceService.ACTION_CONNECT).putExtra(GBDevice.EXTRA_DEVICE, device).putExtra(DeviceService.EXTRA_CONNECT_FIRST_TIME, firstTime));
    }

    public void disconnect() {
        invokeService(createIntent().setAction(DeviceService.ACTION_DISCONNECT));
    }

    public void quit() {
        stopService(createIntent());
    }

    public void requestDeviceInfo() {
        invokeService(createIntent().setAction(DeviceService.ACTION_REQUEST_DEVICEINFO));
    }

    public void onNotification(NotificationSpec notificationSpec) {
        invokeService(createIntent().setAction(DeviceService.ACTION_NOTIFICATION).putExtra(DeviceService.EXTRA_NOTIFICATION_FLAGS, notificationSpec.flags).putExtra(DeviceService.EXTRA_NOTIFICATION_PHONENUMBER, notificationSpec.phoneNumber).putExtra(DeviceService.EXTRA_NOTIFICATION_SENDER, (String) JavaExtensions.coalesce(notificationSpec.sender, getContactDisplayNameByNumber(notificationSpec.phoneNumber))).putExtra(DeviceService.EXTRA_NOTIFICATION_SUBJECT, notificationSpec.subject).putExtra(DeviceService.EXTRA_NOTIFICATION_TITLE, notificationSpec.title).putExtra(DeviceService.EXTRA_NOTIFICATION_BODY, notificationSpec.body).putExtra(DeviceService.EXTRA_NOTIFICATION_ID, notificationSpec.getId()).putExtra(DeviceService.EXTRA_NOTIFICATION_TYPE, notificationSpec.type).putExtra(DeviceService.EXTRA_NOTIFICATION_ACTIONS, notificationSpec.attachedActions).putExtra(DeviceService.EXTRA_NOTIFICATION_SOURCENAME, notificationSpec.sourceName).putExtra(DeviceService.EXTRA_NOTIFICATION_PEBBLE_COLOR, notificationSpec.pebbleColor).putExtra(DeviceService.EXTRA_NOTIFICATION_SOURCEAPPID, notificationSpec.sourceAppId));
    }

    public void onDeleteNotification(int id) {
        invokeService(createIntent().setAction(DeviceService.ACTION_DELETE_NOTIFICATION).putExtra(DeviceService.EXTRA_NOTIFICATION_ID, id));
    }

    public void onSetTime() {
        invokeService(createIntent().setAction(DeviceService.ACTION_SETTIME));
    }

    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        invokeService(createIntent().setAction(DeviceService.ACTION_SET_ALARMS).putExtra(DeviceService.EXTRA_ALARMS, alarms));
    }

    public void onSetCallState(CallSpec callSpec) {
        Context context = GBApplication.getContext();
        String currentPrivacyMode = GBApplication.getPrefs().getString("pref_call_privacy_mode", GBApplication.getContext().getString(C0889R.string.p_call_privacy_mode_off));
        if (currentPrivacyMode.equals(context.getString(C0889R.string.p_call_privacy_mode_name))) {
            callSpec.name = callSpec.number;
        } else if (currentPrivacyMode.equals(context.getString(C0889R.string.p_call_privacy_mode_complete))) {
            callSpec.number = null;
            callSpec.name = null;
        } else if (currentPrivacyMode.equals(context.getString(C0889R.string.p_call_privacy_mode_number))) {
            callSpec.name = (String) JavaExtensions.coalesce(callSpec.name, getContactDisplayNameByNumber(callSpec.number));
            if (callSpec.name != null && !callSpec.name.equals(callSpec.number)) {
                callSpec.number = null;
            }
        } else {
            callSpec.name = (String) JavaExtensions.coalesce(callSpec.name, getContactDisplayNameByNumber(callSpec.number));
        }
        invokeService(createIntent().setAction(DeviceService.ACTION_CALLSTATE).putExtra(DeviceService.EXTRA_CALL_PHONENUMBER, callSpec.number).putExtra(DeviceService.EXTRA_CALL_DISPLAYNAME, callSpec.name).putExtra(DeviceService.EXTRA_CALL_COMMAND, callSpec.command));
    }

    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
        invokeService(createIntent().setAction(DeviceService.ACTION_SETCANNEDMESSAGES).putExtra(DeviceService.EXTRA_CANNEDMESSAGES_TYPE, cannedMessagesSpec.type).putExtra(DeviceService.EXTRA_CANNEDMESSAGES, cannedMessagesSpec.cannedMessages));
    }

    public void onSetMusicState(MusicStateSpec stateSpec) {
        invokeService(createIntent().setAction(DeviceService.ACTION_SETMUSICSTATE).putExtra(DeviceService.EXTRA_MUSIC_REPEAT, stateSpec.repeat).putExtra(DeviceService.EXTRA_MUSIC_RATE, stateSpec.playRate).putExtra(DeviceService.EXTRA_MUSIC_STATE, stateSpec.state).putExtra(DeviceService.EXTRA_MUSIC_SHUFFLE, stateSpec.shuffle).putExtra(DeviceService.EXTRA_MUSIC_POSITION, stateSpec.position));
    }

    public void onSetMusicInfo(MusicSpec musicSpec) {
        invokeService(createIntent().setAction(DeviceService.ACTION_SETMUSICINFO).putExtra(DeviceService.EXTRA_MUSIC_ARTIST, musicSpec.artist).putExtra(DeviceService.EXTRA_MUSIC_ALBUM, musicSpec.album).putExtra(DeviceService.EXTRA_MUSIC_TRACK, musicSpec.track).putExtra(DeviceService.EXTRA_MUSIC_DURATION, musicSpec.duration).putExtra(DeviceService.EXTRA_MUSIC_TRACKCOUNT, musicSpec.trackCount).putExtra(DeviceService.EXTRA_MUSIC_TRACKNR, musicSpec.trackNr));
    }

    public void onInstallApp(Uri uri) {
        invokeService(createIntent().setAction(DeviceService.ACTION_INSTALL).putExtra(DeviceService.EXTRA_URI, uri));
    }

    public void onAppInfoReq() {
        invokeService(createIntent().setAction(DeviceService.ACTION_REQUEST_APPINFO));
    }

    public void onAppStart(UUID uuid, boolean start) {
        invokeService(createIntent().setAction(DeviceService.ACTION_STARTAPP).putExtra(DeviceService.EXTRA_APP_UUID, uuid).putExtra(DeviceService.EXTRA_APP_START, start));
    }

    public void onAppDelete(UUID uuid) {
        invokeService(createIntent().setAction(DeviceService.ACTION_DELETEAPP).putExtra(DeviceService.EXTRA_APP_UUID, uuid));
    }

    public void onAppConfiguration(UUID uuid, String config, Integer id) {
        Intent intent = createIntent().setAction(DeviceService.ACTION_APP_CONFIGURE).putExtra(DeviceService.EXTRA_APP_UUID, uuid).putExtra(DeviceService.EXTRA_APP_CONFIG, config);
        if (id != null) {
            intent.putExtra(DeviceService.EXTRA_APP_CONFIG_ID, id);
        }
        invokeService(intent);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [java.util.UUID[], java.io.Serializable] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onAppReorder(java.util.UUID[] r3) {
        /*
            r2 = this;
            android.content.Intent r0 = r2.createIntent()
            java.lang.String r1 = "nodomain.freeyourgadget.gadgetbridge.devices.action.app_reorder"
            android.content.Intent r0 = r0.setAction(r1)
            java.lang.String r1 = "app_uuid"
            android.content.Intent r0 = r0.putExtra(r1, r3)
            r2.invokeService(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceService.onAppReorder(java.util.UUID[]):void");
    }

    public void onFetchRecordedData(int dataTypes) {
        invokeService(createIntent().setAction(DeviceService.ACTION_FETCH_RECORDED_DATA).putExtra(DeviceService.EXTRA_RECORDED_DATA_TYPES, dataTypes));
    }

    public void onReset(int flags) {
        invokeService(createIntent().setAction(DeviceService.ACTION_RESET).putExtra(DeviceService.EXTRA_RESET_FLAGS, flags));
    }

    public void onHeartRateTest() {
        invokeService(createIntent().setAction(DeviceService.ACTION_HEARTRATE_TEST));
    }

    public void onFindDevice(boolean start) {
        invokeService(createIntent().setAction(DeviceService.ACTION_FIND_DEVICE).putExtra(DeviceService.EXTRA_FIND_START, start));
    }

    public void onSetConstantVibration(int intensity) {
        invokeService(createIntent().setAction(DeviceService.ACTION_SET_CONSTANT_VIBRATION).putExtra(DeviceService.EXTRA_VIBRATION_INTENSITY, intensity));
    }

    public void onScreenshotReq() {
        invokeService(createIntent().setAction(DeviceService.ACTION_REQUEST_SCREENSHOT));
    }

    public void onEnableRealtimeSteps(boolean enable) {
        invokeService(createIntent().setAction(DeviceService.ACTION_ENABLE_REALTIME_STEPS).putExtra(DeviceService.EXTRA_BOOLEAN_ENABLE, enable));
    }

    public void onEnableHeartRateSleepSupport(boolean enable) {
        invokeService(createIntent().setAction(DeviceService.ACTION_ENABLE_HEARTRATE_SLEEP_SUPPORT).putExtra(DeviceService.EXTRA_BOOLEAN_ENABLE, enable));
    }

    public void onSetHeartRateMeasurementInterval(int seconds) {
        invokeService(createIntent().setAction(DeviceService.ACTION_SET_HEARTRATE_MEASUREMENT_INTERVAL).putExtra(DeviceService.EXTRA_INTERVAL_SECONDS, seconds));
    }

    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
        invokeService(createIntent().setAction(DeviceService.ACTION_ENABLE_REALTIME_HEARTRATE_MEASUREMENT).putExtra(DeviceService.EXTRA_BOOLEAN_ENABLE, enable));
    }

    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
        invokeService(createIntent().setAction(DeviceService.ACTION_ADD_CALENDAREVENT).putExtra(DeviceService.EXTRA_CALENDAREVENT_ID, calendarEventSpec.f156id).putExtra(DeviceService.EXTRA_CALENDAREVENT_TYPE, calendarEventSpec.type).putExtra(DeviceService.EXTRA_CALENDAREVENT_TIMESTAMP, calendarEventSpec.timestamp).putExtra(DeviceService.EXTRA_CALENDAREVENT_DURATION, calendarEventSpec.durationInSeconds).putExtra(DeviceService.EXTRA_CALENDAREVENT_TITLE, calendarEventSpec.title).putExtra(DeviceService.EXTRA_CALENDAREVENT_DESCRIPTION, calendarEventSpec.description).putExtra(DeviceService.EXTRA_CALENDAREVENT_LOCATION, calendarEventSpec.location));
    }

    public void onDeleteCalendarEvent(byte type, long id) {
        invokeService(createIntent().setAction(DeviceService.ACTION_DELETE_CALENDAREVENT).putExtra(DeviceService.EXTRA_CALENDAREVENT_TYPE, type).putExtra(DeviceService.EXTRA_CALENDAREVENT_ID, id));
    }

    public void onSendConfiguration(String config) {
        invokeService(createIntent().setAction(DeviceService.ACTION_SEND_CONFIGURATION).putExtra(DeviceService.EXTRA_CONFIG, config));
    }

    public void onReadConfiguration(String config) {
        invokeService(createIntent().setAction(DeviceService.ACTION_READ_CONFIGURATION).putExtra(DeviceService.EXTRA_CONFIG, config));
    }

    public void onTestNewFunction() {
        invokeService(createIntent().setAction(DeviceService.ACTION_TEST_NEW_FUNCTION));
    }

    public void onSendWeather(WeatherSpec weatherSpec) {
        invokeService(createIntent().setAction(DeviceService.ACTION_SEND_WEATHER).putExtra(DeviceService.EXTRA_WEATHER, weatherSpec));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004f, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0050, code lost:
        if (r1 != null) goto L_0x0052;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x005a, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getContactDisplayNameByNumber(java.lang.String r9) {
        /*
            r8 = this;
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L_0x0011
            android.net.Uri r0 = android.provider.ContactsContract.PhoneLookup.ENTERPRISE_CONTENT_FILTER_URI
            java.lang.String r1 = android.net.Uri.encode(r9)
            android.net.Uri r0 = android.net.Uri.withAppendedPath(r0, r1)
            goto L_0x001b
        L_0x0011:
            android.net.Uri r0 = android.provider.ContactsContract.PhoneLookup.CONTENT_FILTER_URI
            java.lang.String r1 = android.net.Uri.encode(r9)
            android.net.Uri r0 = android.net.Uri.withAppendedPath(r0, r1)
        L_0x001b:
            r7 = r9
            if (r9 == 0) goto L_0x0063
            java.lang.String r1 = ""
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0027
            goto L_0x0063
        L_0x0027:
            android.content.Context r1 = r8.mContext     // Catch:{ SecurityException -> 0x0061 }
            android.content.ContentResolver r1 = r1.getContentResolver()     // Catch:{ SecurityException -> 0x0061 }
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r2 = r0
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ SecurityException -> 0x0061 }
            if (r1 == 0) goto L_0x005b
            int r2 = r1.getCount()     // Catch:{ all -> 0x004d }
            if (r2 <= 0) goto L_0x005b
            r1.moveToNext()     // Catch:{ all -> 0x004d }
            java.lang.String r2 = "display_name"
            int r2 = r1.getColumnIndex(r2)     // Catch:{ all -> 0x004d }
            java.lang.String r2 = r1.getString(r2)     // Catch:{ all -> 0x004d }
            r7 = r2
            goto L_0x005b
        L_0x004d:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x004f }
        L_0x004f:
            r3 = move-exception
            if (r1 == 0) goto L_0x005a
            r1.close()     // Catch:{ all -> 0x0056 }
            goto L_0x005a
        L_0x0056:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ SecurityException -> 0x0061 }
        L_0x005a:
            throw r3     // Catch:{ SecurityException -> 0x0061 }
        L_0x005b:
            if (r1 == 0) goto L_0x0060
            r1.close()     // Catch:{ SecurityException -> 0x0061 }
        L_0x0060:
            goto L_0x0062
        L_0x0061:
            r1 = move-exception
        L_0x0062:
            return r7
        L_0x0063:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceService.getContactDisplayNameByNumber(java.lang.String):java.lang.String");
    }

    public void onSetFmFrequency(float frequency) {
        invokeService(createIntent().setAction(DeviceService.ACTION_SET_FM_FREQUENCY).putExtra(DeviceService.EXTRA_FM_FREQUENCY, frequency));
    }

    public void onSetLedColor(int color) {
        invokeService(createIntent().setAction(DeviceService.ACTION_SET_LED_COLOR).putExtra(DeviceService.EXTRA_LED_COLOR, color));
    }
}
