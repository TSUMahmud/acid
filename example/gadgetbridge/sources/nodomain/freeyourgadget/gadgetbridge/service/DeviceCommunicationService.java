package nodomain.freeyourgadget.gadgetbridge.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.HeartRateUtils;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.externalevents.AlarmClockReceiver;
import nodomain.freeyourgadget.gadgetbridge.externalevents.AlarmReceiver;
import nodomain.freeyourgadget.gadgetbridge.externalevents.BluetoothConnectReceiver;
import nodomain.freeyourgadget.gadgetbridge.externalevents.BluetoothPairingRequestReceiver;
import nodomain.freeyourgadget.gadgetbridge.externalevents.CMWeatherReceiver;
import nodomain.freeyourgadget.gadgetbridge.externalevents.CalendarReceiver;
import nodomain.freeyourgadget.gadgetbridge.externalevents.LineageOsWeatherReceiver;
import nodomain.freeyourgadget.gadgetbridge.externalevents.MusicPlaybackReceiver;
import nodomain.freeyourgadget.gadgetbridge.externalevents.OmniJawsObserver;
import nodomain.freeyourgadget.gadgetbridge.externalevents.PebbleReceiver;
import nodomain.freeyourgadget.gadgetbridge.externalevents.PhoneCallReceiver;
import nodomain.freeyourgadget.gadgetbridge.externalevents.SMSReceiver;
import nodomain.freeyourgadget.gadgetbridge.externalevents.TimeChangeReceiver;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.receivers.AutoConnectIntervalReceiver;
import nodomain.freeyourgadget.gadgetbridge.service.receivers.GBAutoFetchReceiver;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;
import nodomain.freeyourgadget.gadgetbridge.util.EmojiConverter;
import nodomain.freeyourgadget.gadgetbridge.util.GBPrefs;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceCommunicationService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static DeviceSupportFactory DEVICE_SUPPORT_FACTORY = null;
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) DeviceCommunicationService.class);
    private AlarmClockReceiver mAlarmClockReceiver = null;
    private AlarmReceiver mAlarmReceiver = null;
    private AutoConnectIntervalReceiver mAutoConnectInvervalReceiver = null;
    private BluetoothConnectReceiver mBlueToothConnectReceiver = null;
    private BluetoothPairingRequestReceiver mBlueToothPairingRequestReceiver = null;
    private CMWeatherReceiver mCMWeatherReceiver = null;
    private CalendarReceiver mCalendarReceiver = null;
    /* access modifiers changed from: private */
    public DeviceCoordinator mCoordinator = null;
    /* access modifiers changed from: private */
    public DeviceSupport mDeviceSupport;
    private DeviceSupportFactory mFactory;
    private GBAutoFetchReceiver mGBAutoFetchReceiver = null;
    /* access modifiers changed from: private */
    public GBDevice mGBDevice = null;
    private LineageOsWeatherReceiver mLineageOsWeatherReceiver = null;
    private final String[] mMusicActions = {"com.android.music.metachanged", "com.android.music.playstatechanged", "com.android.music.queuechanged", "com.android.music.playbackcomplete", "net.sourceforge.subsonic.androidapp.EVENT_META_CHANGED", "com.maxmpz.audioplayer.TPOS_SYNC", "com.maxmpz.audioplayer.STATUS_CHANGED", "com.maxmpz.audioplayer.PLAYING_MODE_CHANGED", "com.spotify.music.metadatachanged", "com.spotify.music.playbackstatechanged"};
    private MusicPlaybackReceiver mMusicPlaybackReceiver = null;
    private OmniJawsObserver mOmniJawsObserver = null;
    private PebbleReceiver mPebbleReceiver = null;
    private PhoneCallReceiver mPhoneCallReceiver = null;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (GBDevice.ACTION_DEVICE_CHANGED.equals(intent.getAction())) {
                GBDevice device = (GBDevice) intent.getParcelableExtra(GBDevice.EXTRA_DEVICE);
                if (DeviceCommunicationService.this.mGBDevice == null || !DeviceCommunicationService.this.mGBDevice.equals(device)) {
                    Logger access$400 = DeviceCommunicationService.LOG;
                    access$400.error("Got ACTION_DEVICE_CHANGED from unexpected device: " + device);
                    return;
                }
                GBDevice unused = DeviceCommunicationService.this.mGBDevice = device;
                DeviceCoordinator unused2 = DeviceCommunicationService.this.mCoordinator = DeviceHelper.getInstance().getCoordinator(device);
                boolean enableReceivers = DeviceCommunicationService.this.mDeviceSupport != null && (DeviceCommunicationService.this.mDeviceSupport.useAutoConnect() || DeviceCommunicationService.this.mGBDevice.isInitialized());
                DeviceCommunicationService deviceCommunicationService = DeviceCommunicationService.this;
                deviceCommunicationService.setReceiversEnableState(enableReceivers, deviceCommunicationService.mGBDevice.isInitialized(), DeviceCommunicationService.this.mCoordinator);
            }
        }
    };
    private SMSReceiver mSMSReceiver = null;
    private boolean mStarted = false;
    private TimeChangeReceiver mTimeChangeReceiver = null;

    public static void setDeviceSupportFactory(DeviceSupportFactory factory) {
        DEVICE_SUPPORT_FACTORY = factory;
    }

    public void onCreate() {
        LOG.debug("DeviceCommunicationService is being created");
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, new IntentFilter(GBDevice.ACTION_DEVICE_CHANGED));
        this.mFactory = getDeviceSupportFactory();
        if (hasPrefs()) {
            getPrefs().getPreferences().registerOnSharedPreferenceChangeListener(this);
        }
    }

    private DeviceSupportFactory getDeviceSupportFactory() {
        DeviceSupportFactory deviceSupportFactory = DEVICE_SUPPORT_FACTORY;
        if (deviceSupportFactory != null) {
            return deviceSupportFactory;
        }
        return new DeviceSupportFactory(this);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0094, code lost:
        return 1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00bf A[Catch:{ Exception -> 0x0187 }] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01a5 A[Catch:{ Exception -> 0x0187 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int onStartCommand(android.content.Intent r17, int r18, int r19) {
        /*
            r16 = this;
            r1 = r16
            r2 = r17
            monitor-enter(r16)
            r0 = 2
            if (r2 != 0) goto L_0x0011
            org.slf4j.Logger r3 = LOG     // Catch:{ all -> 0x01ab }
            java.lang.String r4 = "no intent"
            r3.info(r4)     // Catch:{ all -> 0x01ab }
            monitor-exit(r16)
            return r0
        L_0x0011:
            java.lang.String r3 = r17.getAction()     // Catch:{ all -> 0x01ab }
            java.lang.String r4 = "connect_first_time"
            r5 = 0
            boolean r4 = r2.getBooleanExtra(r4, r5)     // Catch:{ all -> 0x01ab }
            if (r3 != 0) goto L_0x0027
            org.slf4j.Logger r5 = LOG     // Catch:{ all -> 0x01ab }
            java.lang.String r6 = "no action"
            r5.info(r6)     // Catch:{ all -> 0x01ab }
            monitor-exit(r16)
            return r0
        L_0x0027:
            org.slf4j.Logger r6 = LOG     // Catch:{ all -> 0x01ab }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x01ab }
            r7.<init>()     // Catch:{ all -> 0x01ab }
            java.lang.String r8 = "Service startcommand: "
            r7.append(r8)     // Catch:{ all -> 0x01ab }
            r7.append(r3)     // Catch:{ all -> 0x01ab }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x01ab }
            r6.debug(r7)     // Catch:{ all -> 0x01ab }
            java.lang.String r6 = "nodomain.freeyourgadget.gadgetbridge.devices.action.start"
            boolean r6 = r3.equals(r6)     // Catch:{ all -> 0x01ab }
            r7 = 1
            if (r6 != 0) goto L_0x0095
            java.lang.String r6 = "nodomain.freeyourgadget.gadgetbridge.devices.action.connect"
            boolean r6 = r3.equals(r6)     // Catch:{ all -> 0x01ab }
            if (r6 != 0) goto L_0x0095
            boolean r6 = r1.mStarted     // Catch:{ all -> 0x01ab }
            if (r6 != 0) goto L_0x006a
            org.slf4j.Logger r5 = LOG     // Catch:{ all -> 0x01ab }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x01ab }
            r6.<init>()     // Catch:{ all -> 0x01ab }
            java.lang.String r7 = "Must start service with nodomain.freeyourgadget.gadgetbridge.devices.action.start or nodomain.freeyourgadget.gadgetbridge.devices.action.connect before using it: "
            r6.append(r7)     // Catch:{ all -> 0x01ab }
            r6.append(r3)     // Catch:{ all -> 0x01ab }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01ab }
            r5.info(r6)     // Catch:{ all -> 0x01ab }
            monitor-exit(r16)
            return r0
        L_0x006a:
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r0 = r1.mDeviceSupport     // Catch:{ all -> 0x01ab }
            if (r0 == 0) goto L_0x008a
            boolean r0 = r16.isInitialized()     // Catch:{ all -> 0x01ab }
            if (r0 != 0) goto L_0x0095
            java.lang.String r0 = "nodomain.freeyourgadget.gadgetbridge.devices.action.disconnect"
            boolean r0 = r3.equals(r0)     // Catch:{ all -> 0x01ab }
            if (r0 != 0) goto L_0x0095
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r0 = r1.mDeviceSupport     // Catch:{ all -> 0x01ab }
            boolean r0 = r0.useAutoConnect()     // Catch:{ all -> 0x01ab }
            if (r0 == 0) goto L_0x008a
            boolean r0 = r16.isConnected()     // Catch:{ all -> 0x01ab }
            if (r0 == 0) goto L_0x0095
        L_0x008a:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r1.mGBDevice     // Catch:{ all -> 0x01ab }
            if (r0 == 0) goto L_0x0093
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r1.mGBDevice     // Catch:{ all -> 0x01ab }
            r0.sendDeviceUpdateIntent(r1)     // Catch:{ all -> 0x01ab }
        L_0x0093:
            monitor-exit(r16)
            return r7
        L_0x0095:
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = r16.getPrefs()     // Catch:{ all -> 0x01ab }
            r6 = r0
            r0 = -1
            int r8 = r3.hashCode()     // Catch:{ all -> 0x01ab }
            r9 = -560133350(0xffffffffde9d0b1a, float:-5.6580835E18)
            if (r8 == r9) goto L_0x00b4
            r9 = 1431302562(0x554ff1a2, float:1.42897945E13)
            if (r8 == r9) goto L_0x00aa
        L_0x00a9:
            goto L_0x00bd
        L_0x00aa:
            java.lang.String r8 = "nodomain.freeyourgadget.gadgetbridge.devices.action.connect"
            boolean r8 = r3.equals(r8)     // Catch:{ all -> 0x01ab }
            if (r8 == 0) goto L_0x00a9
            r0 = 1
            goto L_0x00bd
        L_0x00b4:
            java.lang.String r8 = "nodomain.freeyourgadget.gadgetbridge.devices.action.start"
            boolean r8 = r3.equals(r8)     // Catch:{ all -> 0x01ab }
            if (r8 == 0) goto L_0x00a9
            r0 = 0
        L_0x00bd:
            if (r0 == 0) goto L_0x01a5
            if (r0 == r7) goto L_0x00f8
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r0 = r1.mDeviceSupport     // Catch:{ all -> 0x01ab }
            if (r0 == 0) goto L_0x00cf
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r1.mGBDevice     // Catch:{ all -> 0x01ab }
            if (r0 != 0) goto L_0x00ca
            goto L_0x00cf
        L_0x00ca:
            r1.handleAction(r2, r3, r6)     // Catch:{ all -> 0x01ab }
            goto L_0x01a9
        L_0x00cf:
            org.slf4j.Logger r0 = LOG     // Catch:{ all -> 0x01ab }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x01ab }
            r5.<init>()     // Catch:{ all -> 0x01ab }
            java.lang.String r8 = "device support:"
            r5.append(r8)     // Catch:{ all -> 0x01ab }
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r8 = r1.mDeviceSupport     // Catch:{ all -> 0x01ab }
            r5.append(r8)     // Catch:{ all -> 0x01ab }
            java.lang.String r8 = ", device: "
            r5.append(r8)     // Catch:{ all -> 0x01ab }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r8 = r1.mGBDevice     // Catch:{ all -> 0x01ab }
            r5.append(r8)     // Catch:{ all -> 0x01ab }
            java.lang.String r8 = ", aborting"
            r5.append(r8)     // Catch:{ all -> 0x01ab }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x01ab }
            r0.warn(r5)     // Catch:{ all -> 0x01ab }
            goto L_0x01a9
        L_0x00f8:
            r16.start()     // Catch:{ all -> 0x01ab }
            java.lang.String r0 = "device"
            android.os.Parcelable r0 = r2.getParcelableExtra(r0)     // Catch:{ all -> 0x01ab }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = (nodomain.freeyourgadget.gadgetbridge.impl.GBDevice) r0     // Catch:{ all -> 0x01ab }
            r8 = 0
            r9 = 0
            if (r0 != 0) goto L_0x0120
            if (r6 == 0) goto L_0x011e
            java.lang.String r10 = "last_device_address"
            java.lang.String r10 = r6.getString(r10, r9)     // Catch:{ all -> 0x01ab }
            r8 = r10
            if (r8 == 0) goto L_0x011c
            nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper r10 = nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper.getInstance()     // Catch:{ all -> 0x01ab }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r10 = r10.findAvailableDevice(r8, r1)     // Catch:{ all -> 0x01ab }
            r0 = r10
            goto L_0x0126
        L_0x011c:
            r10 = r0
            goto L_0x0126
        L_0x011e:
            r10 = r0
            goto L_0x0126
        L_0x0120:
            java.lang.String r10 = r0.getAddress()     // Catch:{ all -> 0x01ab }
            r8 = r10
            r10 = r0
        L_0x0126:
            boolean r0 = nodomain.freeyourgadget.gadgetbridge.util.GBPrefs.AUTO_RECONNECT_DEFAULT     // Catch:{ all -> 0x01ab }
            if (r6 == 0) goto L_0x014b
            android.content.SharedPreferences r11 = r6.getPreferences()     // Catch:{ all -> 0x01ab }
            if (r11 == 0) goto L_0x014b
            android.content.SharedPreferences r11 = r6.getPreferences()     // Catch:{ all -> 0x01ab }
            android.content.SharedPreferences$Editor r11 = r11.edit()     // Catch:{ all -> 0x01ab }
            java.lang.String r12 = "last_device_address"
            android.content.SharedPreferences$Editor r11 = r11.putString(r12, r8)     // Catch:{ all -> 0x01ab }
            r11.apply()     // Catch:{ all -> 0x01ab }
            nodomain.freeyourgadget.gadgetbridge.util.GBPrefs r11 = r16.getGBPrefs()     // Catch:{ all -> 0x01ab }
            boolean r11 = r11.getAutoReconnect()     // Catch:{ all -> 0x01ab }
            r0 = r11
            goto L_0x014c
        L_0x014b:
            r11 = r0
        L_0x014c:
            if (r10 == 0) goto L_0x019b
            boolean r0 = r16.isConnecting()     // Catch:{ all -> 0x01ab }
            if (r0 != 0) goto L_0x019b
            boolean r0 = r16.isConnected()     // Catch:{ all -> 0x01ab }
            if (r0 != 0) goto L_0x019b
            r1.setDeviceSupport(r9)     // Catch:{ all -> 0x01ab }
            r12 = 3
            r13 = 2131755147(0x7f10008b, float:1.9141165E38)
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupportFactory r0 = r1.mFactory     // Catch:{ Exception -> 0x0187 }
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r0 = r0.createDeviceSupport(r10)     // Catch:{ Exception -> 0x0187 }
            if (r0 == 0) goto L_0x0179
            r1.setDeviceSupport(r0)     // Catch:{ Exception -> 0x0187 }
            if (r4 == 0) goto L_0x0172
            r0.connectFirstTime()     // Catch:{ Exception -> 0x0187 }
            goto L_0x0186
        L_0x0172:
            r0.setAutoReconnect(r11)     // Catch:{ Exception -> 0x0187 }
            r0.connect()     // Catch:{ Exception -> 0x0187 }
            goto L_0x0186
        L_0x0179:
            java.lang.Object[] r14 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0187 }
            java.lang.String r15 = "Can't create device support"
            r14[r5] = r15     // Catch:{ Exception -> 0x0187 }
            java.lang.String r14 = r1.getString(r13, r14)     // Catch:{ Exception -> 0x0187 }
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r1, (java.lang.String) r14, (int) r5, (int) r12)     // Catch:{ Exception -> 0x0187 }
        L_0x0186:
            goto L_0x01a9
        L_0x0187:
            r0 = move-exception
            java.lang.Object[] r14 = new java.lang.Object[r7]     // Catch:{ all -> 0x01ab }
            java.lang.String r15 = r0.getMessage()     // Catch:{ all -> 0x01ab }
            r14[r5] = r15     // Catch:{ all -> 0x01ab }
            java.lang.String r13 = r1.getString(r13, r14)     // Catch:{ all -> 0x01ab }
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r1, r13, r5, r12, r0)     // Catch:{ all -> 0x01ab }
            r1.setDeviceSupport(r9)     // Catch:{ all -> 0x01ab }
            goto L_0x0186
        L_0x019b:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r1.mGBDevice     // Catch:{ all -> 0x01ab }
            if (r0 == 0) goto L_0x01a9
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r1.mGBDevice     // Catch:{ all -> 0x01ab }
            r0.sendDeviceUpdateIntent(r1)     // Catch:{ all -> 0x01ab }
            goto L_0x01a9
        L_0x01a5:
            r16.start()     // Catch:{ all -> 0x01ab }
        L_0x01a9:
            monitor-exit(r16)
            return r7
        L_0x01ab:
            r0 = move-exception
            monitor-exit(r16)
            goto L_0x01af
        L_0x01ae:
            throw r0
        L_0x01af:
            goto L_0x01ae
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.DeviceCommunicationService.onStartCommand(android.content.Intent, int, int):int");
    }

    private String sanitizeNotifText(String text) {
        if (text == null || text.length() == 0) {
            return text;
        }
        String text2 = this.mDeviceSupport.customStringFilter(text);
        if (!this.mCoordinator.supportsUnicodeEmojis()) {
            return EmojiConverter.convertUnicodeEmojiToAscii(text2, getApplicationContext());
        }
        return text2;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void handleAction(android.content.Intent r17, java.lang.String r18, nodomain.freeyourgadget.gadgetbridge.util.Prefs r19) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r2 = r18
            int r3 = r18.hashCode()
            r5 = 1
            r6 = -1
            r7 = 0
            switch(r3) {
                case -2077260203: goto L_0x018e;
                case -2023422855: goto L_0x0183;
                case -1857446317: goto L_0x0178;
                case -1829075225: goto L_0x016d;
                case -1744272557: goto L_0x0163;
                case -1715408825: goto L_0x0158;
                case -1665540682: goto L_0x014e;
                case -1610108183: goto L_0x0143;
                case -1551897069: goto L_0x0138;
                case -1417614585: goto L_0x012c;
                case -1354382541: goto L_0x0121;
                case -1300519005: goto L_0x0115;
                case -1121342265: goto L_0x0109;
                case -984588089: goto L_0x00fd;
                case -796845670: goto L_0x00f1;
                case -561486841: goto L_0x00e6;
                case -359612073: goto L_0x00da;
                case -222614123: goto L_0x00ce;
                case -175941696: goto L_0x00c2;
                case 349729610: goto L_0x00b6;
                case 360763600: goto L_0x00ab;
                case 481775700: goto L_0x009f;
                case 643841963: goto L_0x0093;
                case 805471460: goto L_0x0087;
                case 910519646: goto L_0x007b;
                case 1104017101: goto L_0x0070;
                case 1168270556: goto L_0x0064;
                case 1180070144: goto L_0x0058;
                case 1236098999: goto L_0x004d;
                case 1312734030: goto L_0x0041;
                case 1369616124: goto L_0x0035;
                case 1707810342: goto L_0x0029;
                case 1989730437: goto L_0x001d;
                case 2108772348: goto L_0x0012;
                default: goto L_0x0010;
            }
        L_0x0010:
            goto L_0x0199
        L_0x0012:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.request_deviceinfo"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 0
            goto L_0x019a
        L_0x001d:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.send_weather"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 31
            goto L_0x019a
        L_0x0029:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.setmusicstate"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 15
            goto L_0x019a
        L_0x0035:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.set_constant_vibration"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 10
            goto L_0x019a
        L_0x0041:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.deleteapp"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 19
            goto L_0x019a
        L_0x004d:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.delete_notification"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 2
            goto L_0x019a
        L_0x0058:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.app_configure"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 20
            goto L_0x019a
        L_0x0064:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.test_new_function"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 30
            goto L_0x019a
        L_0x0070:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.fetch_activity_data"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 7
            goto L_0x019a
        L_0x007b:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.request_screenshot"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 17
            goto L_0x019a
        L_0x0087:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.disconnect"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 8
            goto L_0x019a
        L_0x0093:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.callstate"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 11
            goto L_0x019a
        L_0x009f:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.find_device"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 9
            goto L_0x019a
        L_0x00ab:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.delete_calendarevent"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 4
            goto L_0x019a
        L_0x00b6:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.set_led_color"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 32
            goto L_0x019a
        L_0x00c2:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.set_heartrate_measurement_intervarl"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 26
            goto L_0x019a
        L_0x00ce:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.app_reorder"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 21
            goto L_0x019a
        L_0x00da:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.request_appinfo"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 16
            goto L_0x019a
        L_0x00e6:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.reset"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 5
            goto L_0x019a
        L_0x00f1:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.enable_heartrate_sleep_support"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 25
            goto L_0x019a
        L_0x00fd:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.startapp"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 18
            goto L_0x019a
        L_0x0109:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.send_configuration"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 28
            goto L_0x019a
        L_0x0115:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.setcannedmessages"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 12
            goto L_0x019a
        L_0x0121:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.notification"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 1
            goto L_0x019a
        L_0x012c:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.set_alarms"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 23
            goto L_0x019a
        L_0x0138:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.realtime_hr_measurement"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 27
            goto L_0x019a
        L_0x0143:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.set_fm_frequency"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 33
            goto L_0x019a
        L_0x014e:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.add_calendarevent"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 3
            goto L_0x019a
        L_0x0158:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.enable_realtime_steps"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 24
            goto L_0x019a
        L_0x0163:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.heartrate_test"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 6
            goto L_0x019a
        L_0x016d:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.settime"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 13
            goto L_0x019a
        L_0x0178:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.install"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 22
            goto L_0x019a
        L_0x0183:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.setmusicinfo"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 14
            goto L_0x019a
        L_0x018e:
            java.lang.String r3 = "nodomain.freeyourgadget.gadgetbridge.devices.action.read_configuration"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0010
            r3 = 29
            goto L_0x019a
        L_0x0199:
            r3 = -1
        L_0x019a:
            java.lang.String r8 = "config"
            java.lang.String r9 = "calendarevent_type"
            r10 = -1
            java.lang.String r12 = "calendarevent_id"
            java.lang.String r13 = "notification_id"
            java.lang.String r14 = "enable_realtime_steps"
            java.lang.String r15 = "app_uuid"
            r4 = 0
            switch(r3) {
                case 0: goto L_0x057e;
                case 1: goto L_0x048c;
                case 2: goto L_0x047f;
                case 3: goto L_0x0431;
                case 4: goto L_0x0420;
                case 5: goto L_0x0411;
                case 6: goto L_0x0408;
                case 7: goto L_0x03f9;
                case 8: goto L_0x03d9;
                case 9: goto L_0x03ca;
                case 10: goto L_0x03bb;
                case 11: goto L_0x0391;
                case 12: goto L_0x0373;
                case 13: goto L_0x036a;
                case 14: goto L_0x0320;
                case 15: goto L_0x02ea;
                case 16: goto L_0x02e1;
                case 17: goto L_0x02d8;
                case 18: goto L_0x02c3;
                case 19: goto L_0x02b4;
                case 20: goto L_0x028e;
                case 21: goto L_0x027d;
                case 22: goto L_0x025f;
                case 23: goto L_0x024e;
                case 24: goto L_0x0241;
                case 25: goto L_0x0234;
                case 26: goto L_0x0225;
                case 27: goto L_0x0218;
                case 28: goto L_0x020b;
                case 29: goto L_0x01fe;
                case 30: goto L_0x01f5;
                case 31: goto L_0x01de;
                case 32: goto L_0x01c9;
                case 33: goto L_0x01b0;
                default: goto L_0x01ac;
            }
        L_0x01ac:
            r10 = r19
            goto L_0x0586
        L_0x01b0:
            r3 = -1082130432(0xffffffffbf800000, float:-1.0)
            java.lang.String r4 = "fm_frequency"
            float r4 = r1.getFloatExtra(r4, r3)
            int r3 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r3 == 0) goto L_0x01c5
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r3 = r0.mDeviceSupport
            r3.onSetFmFrequency(r4)
            r10 = r19
            goto L_0x0586
        L_0x01c5:
            r10 = r19
            goto L_0x0586
        L_0x01c9:
            java.lang.String r3 = "led_color"
            int r3 = r1.getIntExtra(r3, r7)
            if (r3 == 0) goto L_0x01da
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onSetLedColor(r3)
            r10 = r19
            goto L_0x0586
        L_0x01da:
            r10 = r19
            goto L_0x0586
        L_0x01de:
            java.lang.String r3 = "weather"
            android.os.Parcelable r3 = r1.getParcelableExtra(r3)
            nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec r3 = (nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec) r3
            if (r3 == 0) goto L_0x01f1
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onSendWeather(r3)
            r10 = r19
            goto L_0x0586
        L_0x01f1:
            r10 = r19
            goto L_0x0586
        L_0x01f5:
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r3 = r0.mDeviceSupport
            r3.onTestNewFunction()
            r10 = r19
            goto L_0x0586
        L_0x01fe:
            java.lang.String r3 = r1.getStringExtra(r8)
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onReadConfiguration(r3)
            r10 = r19
            goto L_0x0586
        L_0x020b:
            java.lang.String r3 = r1.getStringExtra(r8)
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onSendConfiguration(r3)
            r10 = r19
            goto L_0x0586
        L_0x0218:
            boolean r3 = r1.getBooleanExtra(r14, r7)
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onEnableRealtimeHeartRateMeasurement(r3)
            r10 = r19
            goto L_0x0586
        L_0x0225:
            java.lang.String r3 = "interval_seconds"
            int r3 = r1.getIntExtra(r3, r7)
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onSetHeartRateMeasurementInterval(r3)
            r10 = r19
            goto L_0x0586
        L_0x0234:
            boolean r3 = r1.getBooleanExtra(r14, r7)
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onEnableHeartRateSleepSupport(r3)
            r10 = r19
            goto L_0x0586
        L_0x0241:
            boolean r3 = r1.getBooleanExtra(r14, r7)
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onEnableRealtimeSteps(r3)
            r10 = r19
            goto L_0x0586
        L_0x024e:
            java.lang.String r3 = "alarms"
            java.io.Serializable r3 = r1.getSerializableExtra(r3)
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onSetAlarms(r3)
            r10 = r19
            goto L_0x0586
        L_0x025f:
            java.lang.String r3 = "uri"
            android.os.Parcelable r3 = r1.getParcelableExtra(r3)
            android.net.Uri r3 = (android.net.Uri) r3
            if (r3 == 0) goto L_0x0279
            org.slf4j.Logger r4 = LOG
            java.lang.String r5 = "will try to install app/fw"
            r4.info(r5)
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onInstallApp(r3)
            r10 = r19
            goto L_0x0586
        L_0x0279:
            r10 = r19
            goto L_0x0586
        L_0x027d:
            java.io.Serializable r3 = r1.getSerializableExtra(r15)
            java.util.UUID[] r3 = (java.util.UUID[]) r3
            java.util.UUID[] r3 = (java.util.UUID[]) r3
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onAppReorder(r3)
            r10 = r19
            goto L_0x0586
        L_0x028e:
            java.io.Serializable r3 = r1.getSerializableExtra(r15)
            java.util.UUID r3 = (java.util.UUID) r3
            java.lang.String r4 = "app_config"
            java.lang.String r4 = r1.getStringExtra(r4)
            r5 = 0
            java.lang.String r6 = "app_config_id"
            boolean r8 = r1.hasExtra(r6)
            if (r8 == 0) goto L_0x02ab
            int r6 = r1.getIntExtra(r6, r7)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r6)
        L_0x02ab:
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r6 = r0.mDeviceSupport
            r6.onAppConfiguration(r3, r4, r5)
            r10 = r19
            goto L_0x0586
        L_0x02b4:
            java.io.Serializable r3 = r1.getSerializableExtra(r15)
            java.util.UUID r3 = (java.util.UUID) r3
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onAppDelete(r3)
            r10 = r19
            goto L_0x0586
        L_0x02c3:
            java.io.Serializable r3 = r1.getSerializableExtra(r15)
            java.util.UUID r3 = (java.util.UUID) r3
            java.lang.String r4 = "app_start"
            boolean r4 = r1.getBooleanExtra(r4, r5)
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r5 = r0.mDeviceSupport
            r5.onAppStart(r3, r4)
            r10 = r19
            goto L_0x0586
        L_0x02d8:
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r3 = r0.mDeviceSupport
            r3.onScreenshotReq()
            r10 = r19
            goto L_0x0586
        L_0x02e1:
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r3 = r0.mDeviceSupport
            r3.onAppInfoReq()
            r10 = r19
            goto L_0x0586
        L_0x02ea:
            nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec r3 = new nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec
            r3.<init>()
            java.lang.String r4 = "music_shuffle"
            byte r4 = r1.getByteExtra(r4, r7)
            r3.shuffle = r4
            java.lang.String r4 = "music_repeat"
            byte r4 = r1.getByteExtra(r4, r7)
            r3.repeat = r4
            java.lang.String r4 = "music_position"
            int r4 = r1.getIntExtra(r4, r7)
            r3.position = r4
            java.lang.String r4 = "music_rate"
            int r4 = r1.getIntExtra(r4, r7)
            r3.playRate = r4
            java.lang.String r4 = "music_state"
            byte r4 = r1.getByteExtra(r4, r7)
            r3.state = r4
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onSetMusicState(r3)
            r10 = r19
            goto L_0x0586
        L_0x0320:
            nodomain.freeyourgadget.gadgetbridge.model.MusicSpec r3 = new nodomain.freeyourgadget.gadgetbridge.model.MusicSpec
            r3.<init>()
            java.lang.String r4 = "music_artist"
            java.lang.String r4 = r1.getStringExtra(r4)
            java.lang.String r4 = r0.sanitizeNotifText(r4)
            r3.artist = r4
            java.lang.String r4 = "music_album"
            java.lang.String r4 = r1.getStringExtra(r4)
            java.lang.String r4 = r0.sanitizeNotifText(r4)
            r3.album = r4
            java.lang.String r4 = "music_track"
            java.lang.String r4 = r1.getStringExtra(r4)
            java.lang.String r4 = r0.sanitizeNotifText(r4)
            r3.track = r4
            java.lang.String r4 = "music_duration"
            int r4 = r1.getIntExtra(r4, r7)
            r3.duration = r4
            java.lang.String r4 = "music_trackcount"
            int r4 = r1.getIntExtra(r4, r7)
            r3.trackCount = r4
            java.lang.String r4 = "music_tracknr"
            int r4 = r1.getIntExtra(r4, r7)
            r3.trackNr = r4
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onSetMusicInfo(r3)
            r10 = r19
            goto L_0x0586
        L_0x036a:
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r3 = r0.mDeviceSupport
            r3.onSetTime()
            r10 = r19
            goto L_0x0586
        L_0x0373:
            java.lang.String r3 = "cannedmessages_type"
            int r3 = r1.getIntExtra(r3, r6)
            java.lang.String r4 = "cannedmessages"
            java.lang.String[] r4 = r1.getStringArrayExtra(r4)
            nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec r5 = new nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec
            r5.<init>()
            r5.type = r3
            r5.cannedMessages = r4
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r6 = r0.mDeviceSupport
            r6.onSetCannedMessages(r5)
            r10 = r19
            goto L_0x0586
        L_0x0391:
            nodomain.freeyourgadget.gadgetbridge.model.CallSpec r3 = new nodomain.freeyourgadget.gadgetbridge.model.CallSpec
            r3.<init>()
            java.lang.String r4 = "call_command"
            int r4 = r1.getIntExtra(r4, r5)
            r3.command = r4
            java.lang.String r4 = "call_phonenumber"
            java.lang.String r4 = r1.getStringExtra(r4)
            r3.number = r4
            java.lang.String r4 = "call_displayname"
            java.lang.String r4 = r1.getStringExtra(r4)
            java.lang.String r4 = r0.sanitizeNotifText(r4)
            r3.name = r4
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onSetCallState(r3)
            r10 = r19
            goto L_0x0586
        L_0x03bb:
            java.lang.String r3 = "vibration_intensity"
            int r3 = r1.getIntExtra(r3, r7)
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onSetConstantVibration(r3)
            r10 = r19
            goto L_0x0586
        L_0x03ca:
            java.lang.String r3 = "find_start"
            boolean r3 = r1.getBooleanExtra(r3, r7)
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onFindDevice(r3)
            r10 = r19
            goto L_0x0586
        L_0x03d9:
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r3 = r0.mDeviceSupport
            r3.dispose()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r3 = r0.mGBDevice
            if (r3 == 0) goto L_0x03ec
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice$State r5 = nodomain.freeyourgadget.gadgetbridge.impl.GBDevice.State.NOT_CONNECTED
            r3.setState(r5)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r3 = r0.mGBDevice
            r3.sendDeviceUpdateIntent(r0)
        L_0x03ec:
            r0.setReceiversEnableState(r7, r7, r4)
            r0.mGBDevice = r4
            r0.mDeviceSupport = r4
            r0.mCoordinator = r4
            r10 = r19
            goto L_0x0586
        L_0x03f9:
            java.lang.String r3 = "data_types"
            int r3 = r1.getIntExtra(r3, r7)
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onFetchRecordedData(r3)
            r10 = r19
            goto L_0x0586
        L_0x0408:
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r3 = r0.mDeviceSupport
            r3.onHeartRateTest()
            r10 = r19
            goto L_0x0586
        L_0x0411:
            java.lang.String r3 = "reset_flags"
            int r3 = r1.getIntExtra(r3, r7)
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onReset(r3)
            r10 = r19
            goto L_0x0586
        L_0x0420:
            long r3 = r1.getLongExtra(r12, r10)
            byte r5 = r1.getByteExtra(r9, r6)
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r6 = r0.mDeviceSupport
            r6.onDeleteCalendarEvent(r5, r3)
            r10 = r19
            goto L_0x0586
        L_0x0431:
            nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec r3 = new nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec
            r3.<init>()
            long r4 = r1.getLongExtra(r12, r10)
            r3.f156id = r4
            byte r4 = r1.getByteExtra(r9, r6)
            r3.type = r4
            java.lang.String r4 = "calendarevent_timestamp"
            int r4 = r1.getIntExtra(r4, r6)
            r3.timestamp = r4
            java.lang.String r4 = "calendarevent_duration"
            int r4 = r1.getIntExtra(r4, r6)
            r3.durationInSeconds = r4
            java.lang.String r4 = "calendarevent_title"
            java.lang.String r4 = r1.getStringExtra(r4)
            java.lang.String r4 = r0.sanitizeNotifText(r4)
            r3.title = r4
            java.lang.String r4 = "calendarevent_description"
            java.lang.String r4 = r1.getStringExtra(r4)
            java.lang.String r4 = r0.sanitizeNotifText(r4)
            r3.description = r4
            java.lang.String r4 = "calendarevent_location"
            java.lang.String r4 = r1.getStringExtra(r4)
            java.lang.String r4 = r0.sanitizeNotifText(r4)
            r3.location = r4
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onAddCalendarEvent(r3)
            r10 = r19
            goto L_0x0586
        L_0x047f:
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r3 = r0.mDeviceSupport
            int r4 = r1.getIntExtra(r13, r6)
            r3.onDeleteNotification(r4)
            r10 = r19
            goto L_0x0586
        L_0x048c:
            int r3 = r1.getIntExtra(r13, r6)
            nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec r5 = new nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec
            r5.<init>(r3)
            java.lang.String r6 = "notification_phonenumber"
            java.lang.String r6 = r1.getStringExtra(r6)
            r5.phoneNumber = r6
            java.lang.String r6 = "notification_sender"
            java.lang.String r6 = r1.getStringExtra(r6)
            java.lang.String r6 = r0.sanitizeNotifText(r6)
            r5.sender = r6
            java.lang.String r6 = "notification_subject"
            java.lang.String r6 = r1.getStringExtra(r6)
            java.lang.String r6 = r0.sanitizeNotifText(r6)
            r5.subject = r6
            java.lang.String r6 = "notification_title"
            java.lang.String r6 = r1.getStringExtra(r6)
            java.lang.String r6 = r0.sanitizeNotifText(r6)
            r5.title = r6
            java.lang.String r6 = "notification_body"
            java.lang.String r6 = r1.getStringExtra(r6)
            java.lang.String r6 = r0.sanitizeNotifText(r6)
            r5.body = r6
            java.lang.String r6 = "notification_sourcename"
            java.lang.String r6 = r1.getStringExtra(r6)
            r5.sourceName = r6
            java.lang.String r6 = "notification_type"
            java.io.Serializable r6 = r1.getSerializableExtra(r6)
            nodomain.freeyourgadget.gadgetbridge.model.NotificationType r6 = (nodomain.freeyourgadget.gadgetbridge.model.NotificationType) r6
            r5.type = r6
            java.lang.String r6 = "notification_actions"
            java.io.Serializable r6 = r1.getSerializableExtra(r6)
            java.util.ArrayList r6 = (java.util.ArrayList) r6
            r5.attachedActions = r6
            java.lang.String r6 = "notification_pebble_color"
            java.io.Serializable r6 = r1.getSerializableExtra(r6)
            java.lang.Byte r6 = (java.lang.Byte) r6
            byte r6 = r6.byteValue()
            r5.pebbleColor = r6
            java.lang.String r6 = "notification_flags"
            int r6 = r1.getIntExtra(r6, r7)
            r5.flags = r6
            java.lang.String r6 = "notification_sourceappid"
            java.lang.String r6 = r1.getStringExtra(r6)
            r5.sourceAppId = r6
            nodomain.freeyourgadget.gadgetbridge.model.NotificationType r6 = r5.type
            nodomain.freeyourgadget.gadgetbridge.model.NotificationType r7 = nodomain.freeyourgadget.gadgetbridge.model.NotificationType.GENERIC_SMS
            if (r6 != r7) goto L_0x051e
            java.lang.String r6 = r5.phoneNumber
            if (r6 == 0) goto L_0x051e
            nodomain.freeyourgadget.gadgetbridge.util.LimitedQueue r6 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getIDSenderLookup()
            int r7 = r5.getId()
            java.lang.String r8 = r5.phoneNumber
            r6.add(r7, r8)
        L_0x051e:
            java.util.ArrayList<nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec$Action> r6 = r5.attachedActions
            if (r6 == 0) goto L_0x052a
            java.util.ArrayList<nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec$Action> r6 = r5.attachedActions
            int r6 = r6.size()
            if (r6 > 0) goto L_0x0534
        L_0x052a:
            nodomain.freeyourgadget.gadgetbridge.model.NotificationType r6 = r5.type
            nodomain.freeyourgadget.gadgetbridge.model.NotificationType r7 = nodomain.freeyourgadget.gadgetbridge.model.NotificationType.GENERIC_SMS
            if (r6 != r7) goto L_0x0576
            java.lang.String r6 = r5.phoneNumber
            if (r6 == 0) goto L_0x0576
        L_0x0534:
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r7 = 1
        L_0x053a:
            r8 = 16
            if (r7 > r8) goto L_0x0565
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "canned_reply_"
            r9.append(r10)
            r9.append(r7)
            java.lang.String r9 = r9.toString()
            r10 = r19
            java.lang.String r9 = r10.getString(r9, r4)
            if (r9 == 0) goto L_0x0562
            java.lang.String r11 = ""
            boolean r11 = r9.equals(r11)
            if (r11 != 0) goto L_0x0562
            r6.add(r9)
        L_0x0562:
            int r7 = r7 + 1
            goto L_0x053a
        L_0x0565:
            r10 = r19
            int r4 = r6.size()
            java.lang.String[] r4 = new java.lang.String[r4]
            java.lang.Object[] r4 = r6.toArray(r4)
            java.lang.String[] r4 = (java.lang.String[]) r4
            r5.cannedReplies = r4
            goto L_0x0578
        L_0x0576:
            r10 = r19
        L_0x0578:
            nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport r4 = r0.mDeviceSupport
            r4.onNotification(r5)
            goto L_0x0586
        L_0x057e:
            r10 = r19
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r3 = r0.mGBDevice
            r3.sendDeviceUpdateIntent(r0)
        L_0x0586:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.DeviceCommunicationService.handleAction(android.content.Intent, java.lang.String, nodomain.freeyourgadget.gadgetbridge.util.Prefs):void");
    }

    private void setDeviceSupport(DeviceSupport deviceSupport) {
        DeviceSupport deviceSupport2 = this.mDeviceSupport;
        DeviceCoordinator deviceCoordinator = null;
        if (!(deviceSupport == deviceSupport2 || deviceSupport2 == null)) {
            deviceSupport2.dispose();
            this.mDeviceSupport = null;
            this.mGBDevice = null;
            this.mCoordinator = null;
        }
        this.mDeviceSupport = deviceSupport;
        DeviceSupport deviceSupport3 = this.mDeviceSupport;
        this.mGBDevice = deviceSupport3 != null ? deviceSupport3.getDevice() : null;
        if (this.mGBDevice != null) {
            deviceCoordinator = DeviceHelper.getInstance().getCoordinator(this.mGBDevice);
        }
        this.mCoordinator = deviceCoordinator;
    }

    private void start() {
        if (!this.mStarted) {
            startForeground(1, C1238GB.createNotification(getString(C0889R.string.gadgetbridge_running), (Context) this));
            this.mStarted = true;
        }
    }

    public boolean isStarted() {
        return this.mStarted;
    }

    private boolean isConnected() {
        GBDevice gBDevice = this.mGBDevice;
        return gBDevice != null && gBDevice.isConnected();
    }

    private boolean isConnecting() {
        GBDevice gBDevice = this.mGBDevice;
        return gBDevice != null && gBDevice.isConnecting();
    }

    private boolean isInitialized() {
        GBDevice gBDevice = this.mGBDevice;
        return gBDevice != null && gBDevice.isInitialized();
    }

    /* access modifiers changed from: private */
    public void setReceiversEnableState(boolean enable, boolean initialized, DeviceCoordinator coordinator) {
        LOG.info("Setting broadcast receivers to: " + enable);
        if (!enable || !initialized || coordinator == null || !coordinator.supportsCalendarEvents()) {
            CalendarReceiver calendarReceiver = this.mCalendarReceiver;
            if (calendarReceiver != null) {
                unregisterReceiver(calendarReceiver);
                this.mCalendarReceiver = null;
            }
            AlarmReceiver alarmReceiver = this.mAlarmReceiver;
            if (alarmReceiver != null) {
                unregisterReceiver(alarmReceiver);
                this.mAlarmReceiver = null;
            }
        } else {
            if (this.mCalendarReceiver == null && getPrefs().getBoolean("enable_calendar_sync", true) && (!GBApplication.isRunningMarshmallowOrLater() || ContextCompat.checkSelfPermission(this, "android.permission.READ_CALENDAR") != -1)) {
                IntentFilter calendarIntentFilter = new IntentFilter();
                calendarIntentFilter.addAction("android.intent.action.PROVIDER_CHANGED");
                calendarIntentFilter.addDataScheme("content");
                calendarIntentFilter.addDataAuthority("com.android.calendar", (String) null);
                this.mCalendarReceiver = new CalendarReceiver(this.mGBDevice);
                registerReceiver(this.mCalendarReceiver, calendarIntentFilter);
            }
            if (this.mAlarmReceiver == null) {
                this.mAlarmReceiver = new AlarmReceiver();
                registerReceiver(this.mAlarmReceiver, new IntentFilter("DAILY_ALARM"));
            }
        }
        if (enable) {
            if (this.mPhoneCallReceiver == null) {
                this.mPhoneCallReceiver = new PhoneCallReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.intent.action.PHONE_STATE");
                filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
                filter.addAction("nodomain.freeyourgadget.gadgetbridge.MUTE_CALL");
                registerReceiver(this.mPhoneCallReceiver, filter);
            }
            if (this.mSMSReceiver == null) {
                this.mSMSReceiver = new SMSReceiver();
                registerReceiver(this.mSMSReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
            }
            if (this.mPebbleReceiver == null) {
                this.mPebbleReceiver = new PebbleReceiver();
                registerReceiver(this.mPebbleReceiver, new IntentFilter("com.getpebble.action.SEND_NOTIFICATION"));
            }
            if (this.mMusicPlaybackReceiver == null && coordinator != null && coordinator.supportsMusicInfo()) {
                this.mMusicPlaybackReceiver = new MusicPlaybackReceiver();
                IntentFilter filter2 = new IntentFilter();
                for (String action : this.mMusicActions) {
                    filter2.addAction(action);
                }
                registerReceiver(this.mMusicPlaybackReceiver, filter2);
            }
            if (this.mTimeChangeReceiver == null) {
                this.mTimeChangeReceiver = new TimeChangeReceiver();
                IntentFilter filter3 = new IntentFilter();
                filter3.addAction("android.intent.action.TIME_SET");
                filter3.addAction("android.intent.action.TIMEZONE_CHANGED");
                registerReceiver(this.mTimeChangeReceiver, filter3);
            }
            if (this.mBlueToothConnectReceiver == null) {
                this.mBlueToothConnectReceiver = new BluetoothConnectReceiver(this);
                registerReceiver(this.mBlueToothConnectReceiver, new IntentFilter("android.bluetooth.device.action.ACL_CONNECTED"));
            }
            if (this.mBlueToothPairingRequestReceiver == null) {
                this.mBlueToothPairingRequestReceiver = new BluetoothPairingRequestReceiver(this);
                registerReceiver(this.mBlueToothPairingRequestReceiver, new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST"));
            }
            if (this.mAlarmClockReceiver == null) {
                this.mAlarmClockReceiver = new AlarmClockReceiver();
                IntentFilter filter4 = new IntentFilter();
                filter4.addAction(AlarmClockReceiver.ALARM_ALERT_ACTION);
                filter4.addAction(AlarmClockReceiver.ALARM_DONE_ACTION);
                filter4.addAction(AlarmClockReceiver.GOOGLE_CLOCK_ALARM_ALERT_ACTION);
                filter4.addAction(AlarmClockReceiver.GOOGLE_CLOCK_ALARM_DONE_ACTION);
                registerReceiver(this.mAlarmClockReceiver, filter4);
            }
            if (this.mCMWeatherReceiver == null && coordinator != null && coordinator.supportsWeather()) {
                this.mCMWeatherReceiver = new CMWeatherReceiver();
                registerReceiver(this.mCMWeatherReceiver, new IntentFilter("GB_UPDATE_WEATHER"));
            }
            if (GBApplication.isRunningOreoOrLater() && this.mLineageOsWeatherReceiver == null && coordinator != null && coordinator.supportsWeather()) {
                this.mLineageOsWeatherReceiver = new LineageOsWeatherReceiver();
                registerReceiver(this.mLineageOsWeatherReceiver, new IntentFilter("GB_UPDATE_WEATHER"));
            }
            if (this.mOmniJawsObserver == null && coordinator != null && coordinator.supportsWeather()) {
                try {
                    this.mOmniJawsObserver = new OmniJawsObserver(new Handler());
                    getContentResolver().registerContentObserver(OmniJawsObserver.WEATHER_URI, true, this.mOmniJawsObserver);
                } catch (PackageManager.NameNotFoundException e) {
                }
            }
            if (GBApplication.getPrefs().getBoolean("auto_fetch_enabled", false) && coordinator != null && coordinator.supportsActivityDataFetching() && this.mGBAutoFetchReceiver == null) {
                this.mGBAutoFetchReceiver = new GBAutoFetchReceiver();
                registerReceiver(this.mGBAutoFetchReceiver, new IntentFilter("android.intent.action.USER_PRESENT"));
            }
            if (this.mAutoConnectInvervalReceiver == null) {
                this.mAutoConnectInvervalReceiver = new AutoConnectIntervalReceiver(this);
                registerReceiver(this.mAutoConnectInvervalReceiver, new IntentFilter("GB_RECONNECT"));
                return;
            }
            return;
        }
        PhoneCallReceiver phoneCallReceiver = this.mPhoneCallReceiver;
        if (phoneCallReceiver != null) {
            unregisterReceiver(phoneCallReceiver);
            this.mPhoneCallReceiver = null;
        }
        SMSReceiver sMSReceiver = this.mSMSReceiver;
        if (sMSReceiver != null) {
            unregisterReceiver(sMSReceiver);
            this.mSMSReceiver = null;
        }
        PebbleReceiver pebbleReceiver = this.mPebbleReceiver;
        if (pebbleReceiver != null) {
            unregisterReceiver(pebbleReceiver);
            this.mPebbleReceiver = null;
        }
        MusicPlaybackReceiver musicPlaybackReceiver = this.mMusicPlaybackReceiver;
        if (musicPlaybackReceiver != null) {
            unregisterReceiver(musicPlaybackReceiver);
            this.mMusicPlaybackReceiver = null;
        }
        TimeChangeReceiver timeChangeReceiver = this.mTimeChangeReceiver;
        if (timeChangeReceiver != null) {
            unregisterReceiver(timeChangeReceiver);
            this.mTimeChangeReceiver = null;
        }
        BluetoothConnectReceiver bluetoothConnectReceiver = this.mBlueToothConnectReceiver;
        if (bluetoothConnectReceiver != null) {
            unregisterReceiver(bluetoothConnectReceiver);
            this.mBlueToothConnectReceiver = null;
        }
        BluetoothPairingRequestReceiver bluetoothPairingRequestReceiver = this.mBlueToothPairingRequestReceiver;
        if (bluetoothPairingRequestReceiver != null) {
            unregisterReceiver(bluetoothPairingRequestReceiver);
            this.mBlueToothPairingRequestReceiver = null;
        }
        AlarmClockReceiver alarmClockReceiver = this.mAlarmClockReceiver;
        if (alarmClockReceiver != null) {
            unregisterReceiver(alarmClockReceiver);
            this.mAlarmClockReceiver = null;
        }
        CMWeatherReceiver cMWeatherReceiver = this.mCMWeatherReceiver;
        if (cMWeatherReceiver != null) {
            unregisterReceiver(cMWeatherReceiver);
            this.mCMWeatherReceiver = null;
        }
        LineageOsWeatherReceiver lineageOsWeatherReceiver = this.mLineageOsWeatherReceiver;
        if (lineageOsWeatherReceiver != null) {
            unregisterReceiver(lineageOsWeatherReceiver);
            this.mLineageOsWeatherReceiver = null;
        }
        if (this.mOmniJawsObserver != null) {
            getContentResolver().unregisterContentObserver(this.mOmniJawsObserver);
        }
        GBAutoFetchReceiver gBAutoFetchReceiver = this.mGBAutoFetchReceiver;
        if (gBAutoFetchReceiver != null) {
            unregisterReceiver(gBAutoFetchReceiver);
            this.mGBAutoFetchReceiver = null;
        }
        AutoConnectIntervalReceiver autoConnectIntervalReceiver = this.mAutoConnectInvervalReceiver;
        if (autoConnectIntervalReceiver != null) {
            unregisterReceiver(autoConnectIntervalReceiver);
            this.mAutoConnectInvervalReceiver.destroy();
            this.mAutoConnectInvervalReceiver = null;
        }
    }

    public void onDestroy() {
        if (hasPrefs()) {
            getPrefs().getPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
        LOG.debug("DeviceCommunicationService is being destroyed");
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mReceiver);
        setReceiversEnableState(false, false, (DeviceCoordinator) null);
        setDeviceSupport((DeviceSupport) null);
        NotificationManager nm = (NotificationManager) getSystemService("notification");
        if (nm != null) {
            nm.cancel(1);
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (GBPrefs.AUTO_RECONNECT.equals(key)) {
            boolean autoReconnect = getGBPrefs().getAutoReconnect();
            DeviceSupport deviceSupport = this.mDeviceSupport;
            if (deviceSupport != null) {
                deviceSupport.setAutoReconnect(autoReconnect);
            }
        }
        if (GBPrefs.CHART_MAX_HEART_RATE.equals(key) || GBPrefs.CHART_MIN_HEART_RATE.equals(key)) {
            HeartRateUtils.getInstance().updateCachedHeartRatePreferences();
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasPrefs() {
        return getPrefs().getPreferences() != null;
    }

    public Prefs getPrefs() {
        return GBApplication.getPrefs();
    }

    public GBPrefs getGBPrefs() {
        return GBApplication.getGBPrefs();
    }

    public GBDevice getGBDevice() {
        return this.mGBDevice;
    }
}
