package nodomain.freeyourgadget.gadgetbridge.service.devices.makibeshr3;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.devicesettings.DeviceSettingsPreferenceConst;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventFindPhone;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.devices.liveview.LiveviewConstants;
import nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.MakibesHR3ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.User;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.BatteryState;
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
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MakibesHR3DeviceSupport extends AbstractBTLEDeviceSupport implements SharedPreferences.OnSharedPreferenceChangeListener {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) MakibesHR3DeviceSupport.class);
    private BluetoothGattCharacteristic mControlCharacteristic = null;
    private CountDownTimer mFetchCountDown = new CountDownTimer(2000, 2000) {
        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            MakibesHR3DeviceSupport.LOG.debug("download finished");
            C1238GB.updateTransferNotification((String) null, "", false, 100, MakibesHR3DeviceSupport.this.getContext());
        }
    };
    private Handler mFindPhoneHandler = new Handler();
    private BluetoothGattCharacteristic mReportCharacteristic = null;

    public MakibesHR3DeviceSupport() {
        super(LOG);
        addSupportedService(MakibesHR3Constants.UUID_SERVICE);
    }

    private void fetch(boolean start) {
        if (start) {
            C1238GB.updateTransferNotification((String) null, getContext().getString(C0889R.string.busy_task_fetch_activity_data), true, 0, getContext());
        }
        this.mFetchCountDown.cancel();
        this.mFetchCountDown.start();
    }

    public boolean useAutoConnect() {
        return false;
    }

    private void getDayStartEnd(int timeStamp, Calendar start, Calendar end) {
        int timeStampStart = (timeStamp / 86400) * 86400;
        start.setTimeInMillis(((long) timeStampStart) * 1000);
        end.setTimeInMillis(((long) (86400 + timeStampStart)) * 1000);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0051, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0052, code lost:
        if (r0 != null) goto L_0x0054;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x005c, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getStepsOnDay(int r11) {
        /*
            r10 = this;
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x005d }
            java.util.GregorianCalendar r1 = new java.util.GregorianCalendar     // Catch:{ all -> 0x004f }
            r1.<init>()     // Catch:{ all -> 0x004f }
            java.util.GregorianCalendar r2 = new java.util.GregorianCalendar     // Catch:{ all -> 0x004f }
            r2.<init>()     // Catch:{ all -> 0x004f }
            r10.getDayStartEnd(r11, r1, r2)     // Catch:{ all -> 0x004f }
            nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3SampleProvider r3 = new nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3SampleProvider     // Catch:{ all -> 0x004f }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r10.getDevice()     // Catch:{ all -> 0x004f }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r5 = r0.getDaoSession()     // Catch:{ all -> 0x004f }
            r3.<init>(r4, r5)     // Catch:{ all -> 0x004f }
            long r4 = r1.getTimeInMillis()     // Catch:{ all -> 0x004f }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 / r6
            int r5 = (int) r4     // Catch:{ all -> 0x004f }
            long r8 = r2.getTimeInMillis()     // Catch:{ all -> 0x004f }
            long r8 = r8 / r6
            int r4 = (int) r8     // Catch:{ all -> 0x004f }
            java.util.List r4 = r3.getAllActivitySamples(r5, r4)     // Catch:{ all -> 0x004f }
            r5 = 0
            java.util.Iterator r6 = r4.iterator()     // Catch:{ all -> 0x004f }
        L_0x0036:
            boolean r7 = r6.hasNext()     // Catch:{ all -> 0x004f }
            if (r7 == 0) goto L_0x0048
            java.lang.Object r7 = r6.next()     // Catch:{ all -> 0x004f }
            nodomain.freeyourgadget.gadgetbridge.entities.MakibesHR3ActivitySample r7 = (nodomain.freeyourgadget.gadgetbridge.entities.MakibesHR3ActivitySample) r7     // Catch:{ all -> 0x004f }
            int r8 = r7.getSteps()     // Catch:{ all -> 0x004f }
            int r5 = r5 + r8
            goto L_0x0036
        L_0x0048:
            if (r0 == 0) goto L_0x004e
            r0.close()     // Catch:{ Exception -> 0x005d }
        L_0x004e:
            return r5
        L_0x004f:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0051 }
        L_0x0051:
            r2 = move-exception
            if (r0 == 0) goto L_0x005c
            r0.close()     // Catch:{ all -> 0x0058 }
            goto L_0x005c
        L_0x0058:
            r3 = move-exception
            r1.addSuppressed(r3)     // Catch:{ Exception -> 0x005d }
        L_0x005c:
            throw r2     // Catch:{ Exception -> 0x005d }
        L_0x005d:
            r0 = move-exception
            org.slf4j.Logger r1 = LOG
            java.lang.String r2 = r0.getMessage()
            r1.error(r2)
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.makibeshr3.MakibesHR3DeviceSupport.getStepsOnDay(int):int");
    }

    public MakibesHR3ActivitySample createActivitySample(Device device, User user, int timestampInSeconds, SampleProvider provider) {
        MakibesHR3ActivitySample sample = new MakibesHR3ActivitySample();
        sample.setDevice(device);
        sample.setUser(user);
        sample.setTimestamp(timestampInSeconds);
        sample.setProvider(provider);
        return sample;
    }

    public void onNotification(NotificationSpec notificationSpec) {
        byte sender;
        TransactionBuilder transactionBuilder = createTransactionBuilder("onnotificaiton");
        switch (notificationSpec.type) {
            case FACEBOOK:
            case FACEBOOK_MESSENGER:
                sender = 16;
                break;
            case LINE:
                sender = 14;
                break;
            case TELEGRAM:
                sender = 3;
                break;
            case TWITTER:
                sender = 15;
                break;
            case WECHAT:
                sender = 9;
                break;
            case WHATSAPP:
                sender = 10;
                break;
            case KAKAO_TALK:
                sender = 20;
                break;
            default:
                sender = 3;
                break;
        }
        String message = "";
        if (notificationSpec.title != null) {
            message = message + notificationSpec.title + ": ";
        }
        sendNotification(transactionBuilder, sender, message + notificationSpec.body);
        try {
            performConnected(transactionBuilder.getTransaction());
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("notification failed");
        }
    }

    public void onDeleteNotification(int id) {
    }

    public void onSetTime() {
        TransactionBuilder transactionBuilder = createTransactionBuilder("settime");
        setDateTime(transactionBuilder);
        try {
            performConnected(transactionBuilder.getTransaction());
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("factory reset failed");
        }
    }

    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        TransactionBuilder transactionBuilder = createTransactionBuilder("setalarms");
        for (int i = 0; i < alarms.size(); i++) {
            Alarm alarm = (Alarm) alarms.get(i);
            byte repetition = 0;
            int repetition2 = alarm.getRepetition();
            if (repetition2 != 0) {
                if (repetition2 == 1) {
                    repetition = (byte) (0 | 1);
                } else if (repetition2 != 2) {
                    if (repetition2 != 4) {
                        if (repetition2 != 8) {
                            if (repetition2 != 16) {
                                if (repetition2 != 32) {
                                    if (repetition2 != 64) {
                                        LOG.warn("invalid alarm repetition " + alarm.getRepetition());
                                    }
                                    repetition = (byte) (repetition | 64);
                                }
                                repetition = (byte) (repetition | 32);
                                repetition = (byte) (repetition | 64);
                            }
                            repetition = (byte) (repetition | 16);
                            repetition = (byte) (repetition | 32);
                            repetition = (byte) (repetition | 64);
                        }
                        repetition = (byte) (repetition | 8);
                        repetition = (byte) (repetition | 16);
                        repetition = (byte) (repetition | 32);
                        repetition = (byte) (repetition | 64);
                    }
                    repetition = (byte) (repetition | 4);
                    repetition = (byte) (repetition | 8);
                    repetition = (byte) (repetition | 16);
                    repetition = (byte) (repetition | 32);
                    repetition = (byte) (repetition | 64);
                }
                repetition = (byte) (repetition | 2);
                repetition = (byte) (repetition | 4);
                repetition = (byte) (repetition | 8);
                repetition = (byte) (repetition | 16);
                repetition = (byte) (repetition | 32);
                repetition = (byte) (repetition | 64);
            } else {
                repetition = Byte.MIN_VALUE;
            }
            setAlarmReminder(transactionBuilder, i, alarm.getEnabled(), alarm.getHour(), alarm.getMinute(), repetition);
        }
        try {
            performConnected(transactionBuilder.getTransaction());
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("setalarms failed");
        }
    }

    public void onSetCallState(CallSpec callSpec) {
        TransactionBuilder transactionBuilder = createTransactionBuilder("callstate");
        Logger logger = LOG;
        logger.debug("callSpec " + callSpec.command);
        if (callSpec.command == 2) {
            sendNotification(transactionBuilder, (byte) 1, callSpec.name);
        } else {
            sendNotification(transactionBuilder, (byte) 2, "");
        }
        try {
            performConnected(transactionBuilder.getTransaction());
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("call state failed");
        }
    }

    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
    }

    public void onSetMusicState(MusicStateSpec stateSpec) {
    }

    public void onSetMusicInfo(MusicSpec musicSpec) {
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
        if ((flags & 2) != 0) {
            TransactionBuilder transactionBuilder = createTransactionBuilder("reset");
            factoryReset(transactionBuilder);
            try {
                performConnected(transactionBuilder.getTransaction());
            } catch (Exception e) {
                LoggerFactory.getLogger(getClass()).error("factory reset failed");
            }
        } else if ((flags & 1) != 0) {
            TransactionBuilder transactionBuilder2 = createTransactionBuilder("reboot");
            reboot(transactionBuilder2);
            try {
                performConnected(transactionBuilder2.getTransaction());
            } catch (Exception e2) {
                LoggerFactory.getLogger(getClass()).error("factory reset failed");
            }
        }
    }

    public void onHeartRateTest() {
    }

    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
        TransactionBuilder transactionBuilder = createTransactionBuilder("finddevice");
        setEnableRealTimeHeartRate(transactionBuilder, enable);
        try {
            performConnected(transactionBuilder.getTransaction());
        } catch (Exception e) {
            LOG.debug("ERROR");
        }
    }

    /* access modifiers changed from: private */
    public void onReverseFindDevice(boolean start) {
        if (start) {
            int findPhone = MakibesHR3Coordinator.getFindPhone(GBApplication.getDeviceSpecificSharedPrefs(getDevice().getAddress()));
            if (findPhone != 0) {
                GBDeviceEventFindPhone findPhoneEvent = new GBDeviceEventFindPhone();
                findPhoneEvent.event = GBDeviceEventFindPhone.Event.START;
                evaluateGBDeviceEvent(findPhoneEvent);
                if (findPhone > 0) {
                    this.mFindPhoneHandler.postDelayed(new Runnable() {
                        public void run() {
                            MakibesHR3DeviceSupport.this.onReverseFindDevice(false);
                        }
                    }, (long) (findPhone * 1000));
                    return;
                }
                return;
            }
            return;
        }
        GBDeviceEventFindPhone findPhoneEvent2 = new GBDeviceEventFindPhone();
        findPhoneEvent2.event = GBDeviceEventFindPhone.Event.STOP;
        evaluateGBDeviceEvent(findPhoneEvent2);
    }

    public void onFindDevice(boolean start) {
        if (start) {
            TransactionBuilder transactionBuilder = createTransactionBuilder("finddevice");
            findDevice(transactionBuilder);
            try {
                performConnected(transactionBuilder.getTransaction());
            } catch (Exception e) {
                LOG.debug("ERROR");
            }
        }
    }

    public void onSetConstantVibration(int integer) {
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
    }

    private void syncPreferences(TransactionBuilder transaction) {
        SharedPreferences sharedPreferences = GBApplication.getDeviceSpecificSharedPrefs(getDevice().getAddress());
        setTimeMode(transaction, sharedPreferences);
        setDateTime(transaction);
        setQuiteHours(transaction, sharedPreferences);
        setHeadsUpScreen(transaction, sharedPreferences);
        setLostReminder(transaction, sharedPreferences);
        ActivityUser activityUser = new ActivityUser();
        double heightCm = (double) activityUser.getHeightCm();
        Double.isNaN(heightCm);
        setPersonalInformation(transaction, (byte) ((int) Math.round(heightCm * 0.43d)), activityUser.getAge(), activityUser.getHeightCm(), activityUser.getWeightKg(), activityUser.getStepsGoal() / 1000);
        fetch(true);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Logger logger = LOG;
        logger.debug(key + " changed");
        if (!isConnected()) {
            LOG.debug("ignoring change, we're disconnected");
            return;
        }
        TransactionBuilder transactionBuilder = createTransactionBuilder("onSharedPreferenceChanged");
        if (key.equals(DeviceSettingsPreferenceConst.PREF_TIMEFORMAT)) {
            setTimeMode(transactionBuilder, sharedPreferences);
        } else if (key.equals("activate_display_on_lift_wrist")) {
            setHeadsUpScreen(transactionBuilder, sharedPreferences);
        } else if (key.equals("disconnect_notification")) {
            setLostReminder(transactionBuilder, sharedPreferences);
        } else if (key.equals(MakibesHR3Constants.PREF_DO_NOT_DISTURB) || key.equals(MakibesHR3Constants.PREF_DO_NOT_DISTURB_START) || key.equals(MakibesHR3Constants.PREF_DO_NOT_DISTURB_END)) {
            setQuiteHours(transactionBuilder, sharedPreferences);
        } else if (!key.equals(MakibesHR3Constants.PREF_FIND_PHONE) && !key.equals(MakibesHR3Constants.PREF_FIND_PHONE_DURATION)) {
            return;
        }
        try {
            performConnected(transactionBuilder.getTransaction());
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
        }
    }

    private void fakeBattery() {
        GBDeviceEventBatteryInfo batteryInfo = new GBDeviceEventBatteryInfo();
        batteryInfo.level = 100;
        batteryInfo.state = BatteryState.UNKNOWN;
        handleGBDeviceEvent(batteryInfo);
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        fakeBattery();
        C1238GB.updateTransferNotification((String) null, getContext().getString(C0889R.string.busy_task_fetch_activity_data), true, 0, getContext());
        this.gbDevice.setState(GBDevice.State.INITIALIZING);
        this.gbDevice.sendDeviceUpdateIntent(getContext());
        this.mControlCharacteristic = getCharacteristic(MakibesHR3Constants.UUID_CHARACTERISTIC_CONTROL);
        this.mReportCharacteristic = getCharacteristic(MakibesHR3Constants.UUID_CHARACTERISTIC_REPORT);
        builder.notify(this.mReportCharacteristic, true);
        builder.setGattCallback(this);
        builder.write(this.mControlCharacteristic, new byte[]{1, 0});
        syncPreferences(builder);
        requestFitness(builder);
        this.gbDevice.setState(GBDevice.State.INITIALIZED);
        this.gbDevice.sendDeviceUpdateIntent(getContext());
        getDevice().setFirmwareVersion("N/A");
        getDevice().setFirmwareVersion2("N/A");
        GBApplication.getDeviceSpecificSharedPrefs(getDevice().getAddress()).registerOnSharedPreferenceChangeListener(this);
        return builder;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0047, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0048, code lost:
        if (r1 != null) goto L_0x004a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0052, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void addGBActivitySamples(nodomain.freeyourgadget.gadgetbridge.entities.MakibesHR3ActivitySample[] r10) {
        /*
            r9 = this;
            r0 = 0
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r1 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x0053 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r2 = r1.getDaoSession()     // Catch:{ all -> 0x0045 }
            nodomain.freeyourgadget.gadgetbridge.entities.User r2 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getUser(r2)     // Catch:{ all -> 0x0045 }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r3 = r9.getDevice()     // Catch:{ all -> 0x0045 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r4 = r1.getDaoSession()     // Catch:{ all -> 0x0045 }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r3 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getDevice(r3, r4)     // Catch:{ all -> 0x0045 }
            nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3SampleProvider r4 = new nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3SampleProvider     // Catch:{ all -> 0x0045 }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r5 = r9.getDevice()     // Catch:{ all -> 0x0045 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r6 = r1.getDaoSession()     // Catch:{ all -> 0x0045 }
            r4.<init>(r5, r6)     // Catch:{ all -> 0x0045 }
            int r5 = r10.length     // Catch:{ all -> 0x0045 }
            r6 = 0
        L_0x0028:
            if (r6 >= r5) goto L_0x003f
            r7 = r10[r6]     // Catch:{ all -> 0x0045 }
            r7.setDevice(r3)     // Catch:{ all -> 0x0045 }
            r7.setUser(r2)     // Catch:{ all -> 0x0045 }
            r7.setProvider(r4)     // Catch:{ all -> 0x0045 }
            r8 = -1
            r7.setRawIntensity(r8)     // Catch:{ all -> 0x0045 }
            r4.addGBActivitySample(r7)     // Catch:{ all -> 0x0045 }
            int r6 = r6 + 1
            goto L_0x0028
        L_0x003f:
            if (r1 == 0) goto L_0x0044
            r1.close()     // Catch:{ Exception -> 0x0053 }
        L_0x0044:
            goto L_0x0085
        L_0x0045:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0047 }
        L_0x0047:
            r3 = move-exception
            if (r1 == 0) goto L_0x0052
            r1.close()     // Catch:{ all -> 0x004e }
            goto L_0x0052
        L_0x004e:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ Exception -> 0x0053 }
        L_0x0052:
            throw r3     // Catch:{ Exception -> 0x0053 }
        L_0x0053:
            r1 = move-exception
            android.content.Context r2 = r9.getContext()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Error saving samples: "
            r3.append(r4)
            java.lang.String r4 = r1.getLocalizedMessage()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r4 = 3
            r5 = 1
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r2, (java.lang.String) r3, (int) r5, (int) r4)
            r2 = 0
            android.content.Context r3 = r9.getContext()
            java.lang.String r4 = "Data transfer failed"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.updateTransferNotification(r2, r4, r0, r0, r3)
            org.slf4j.Logger r0 = LOG
            java.lang.String r2 = r1.getMessage()
            r0.error(r2)
        L_0x0085:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.makibeshr3.MakibesHR3DeviceSupport.addGBActivitySamples(nodomain.freeyourgadget.gadgetbridge.entities.MakibesHR3ActivitySample[]):void");
    }

    private void addGBActivitySample(MakibesHR3ActivitySample sample) {
        addGBActivitySamples(new MakibesHR3ActivitySample[]{sample});
    }

    private void broadcastSample(MakibesHR3ActivitySample sample) {
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(DeviceService.ACTION_REALTIME_SAMPLES).putExtra(DeviceService.EXTRA_REALTIME_SAMPLE, sample).putExtra("timestamp", sample.getTimestamp()));
    }

    private void onReceiveFitness(int steps) {
        Logger logger = LOG;
        logger.info("steps: " + steps);
        onReceiveStepsSample(steps);
    }

    private void onReceiveHeartRate(int heartRate) {
        Logger logger = LOG;
        logger.info("heart rate: " + heartRate);
        MakibesHR3ActivitySample sample = new MakibesHR3ActivitySample();
        if (heartRate > 0) {
            sample.setHeartRate(heartRate);
            sample.setTimestamp((int) (System.currentTimeMillis() / 1000));
            sample.setRawKind(1);
        } else if (heartRate == -1) {
            sample.setRawKind(8);
        } else if (heartRate == 0) {
            sample.setRawKind(-1);
        } else {
            Logger logger2 = LOG;
            logger2.warn("invalid heart rate reading: " + heartRate);
            return;
        }
        addGBActivitySample(sample);
        broadcastSample(sample);
    }

    private void onReceiveHeartRateSample(int year, int month, int day, int hour, int minute, int heartRate) {
        Logger logger = LOG;
        logger.debug("received heart rate sample " + year + "-" + month + "-" + day + StringUtils.SPACE + hour + ":" + minute + StringUtils.SPACE + heartRate);
        MakibesHR3ActivitySample sample = new MakibesHR3ActivitySample();
        sample.setHeartRate(heartRate);
        sample.setTimestamp((int) (new GregorianCalendar(year, month + -1, day, hour, minute).getTimeInMillis() / 1000));
        sample.setRawKind(1);
        addGBActivitySample(sample);
    }

    private void onReceiveStepsSample(int timeStamp, int steps) {
        MakibesHR3ActivitySample sample = new MakibesHR3ActivitySample();
        int dayStepCount = getStepsOnDay(timeStamp);
        int newSteps = steps - dayStepCount;
        if (newSteps > 0) {
            Logger logger = LOG;
            logger.debug("adding " + newSteps + " steps");
            sample.setSteps(steps - dayStepCount);
            sample.setTimestamp(timeStamp);
            sample.setRawKind(1);
            addGBActivitySample(sample);
        }
    }

    private void onReceiveStepsSample(int year, int month, int day, int hour, int minute, int steps) {
        Logger logger = LOG;
        logger.debug("received steps sample " + year + "-" + month + "-" + day + StringUtils.SPACE + hour + ":" + minute + StringUtils.SPACE + steps);
        onReceiveStepsSample((int) (new GregorianCalendar(year, month + -1, day, hour + 1, minute).getTimeInMillis() / 1000), steps);
    }

    private void onReceiveStepsSample(int steps) {
        onReceiveStepsSample((int) (Calendar.getInstance().getTimeInMillis() / 1000), steps);
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        boolean z = true;
        if (super.onCharacteristicChanged(gatt, characteristic) || characteristic.getValue().length < 6) {
            return true;
        }
        fetch(false);
        if (characteristic.getUuid().equals(this.mReportCharacteristic.getUuid())) {
            byte[] value = characteristic.getValue();
            byte[] arguments = new byte[(value.length - 6)];
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = value[i + 6];
            }
            byte[] report = {value[4], value[5]};
            byte b = report[0];
            if (b != -124) {
                if (b == 125) {
                    if (arguments[0] != 1) {
                        z = false;
                    }
                    onReverseFindDevice(z);
                } else if (b != -111) {
                    if (b != -110) {
                        if (Arrays.equals(report, MakibesHR3Constants.RPRT_FITNESS)) {
                            onReceiveFitness(((arguments[1] & 255) * 256) + (arguments[2] & 255));
                        } else if (Arrays.equals(report, MakibesHR3Constants.RPRT_HEART_RATE_SAMPLE)) {
                            onReceiveHeartRateSample((arguments[0] & 255) + 2000, arguments[1] & 255, arguments[2] & 255, arguments[3] & 255, arguments[4] & 255, arguments[5] & 255);
                        } else if (Arrays.equals(report, MakibesHR3Constants.RPRT_STEPS_SAMPLE)) {
                            onReceiveStepsSample((arguments[0] & 255) + 2000, arguments[1] & 255, arguments[2] & 255, arguments[3] & 255, 0, ((arguments[5] & 255) * 256) + (arguments[6] & 255));
                        }
                    } else if (arguments.length == 11) {
                        GBDevice device = getDevice();
                        device.setFirmwareVersion((arguments[0] & 255) + "." + (arguments[1] & 255));
                    }
                } else if (arguments.length == 2) {
                    GBDeviceEventBatteryInfo batteryInfo = new GBDeviceEventBatteryInfo();
                    batteryInfo.level = (short) (arguments[1] & 255);
                    batteryInfo.state = arguments[0] == 1 ? BatteryState.BATTERY_CHARGING : BatteryState.BATTERY_NORMAL;
                    handleGBDeviceEvent(batteryInfo);
                }
            } else if (value.length == 7) {
                onReceiveHeartRate(arguments[0]);
            }
        }
        return false;
    }

    private byte[] craftData(byte[] command, byte[] data) {
        byte[] result = new byte[(MakibesHR3Constants.DATA_TEMPLATE.length + data.length)];
        System.arraycopy(MakibesHR3Constants.DATA_TEMPLATE, 0, result, 0, MakibesHR3Constants.DATA_TEMPLATE.length);
        result[2] = (byte) (data.length + 3);
        for (int i = 0; i < command.length; i++) {
            result[i + 4] = command[i];
        }
        System.arraycopy(data, 0, result, 6, data.length);
        return result;
    }

    private byte[] craftData(byte command, byte[] data) {
        return craftData(new byte[]{command}, data);
    }

    private byte[] craftData(byte command) {
        return craftData(command, new byte[0]);
    }

    private void writeSafe(BluetoothGattCharacteristic characteristic, TransactionBuilder builder, byte[] data) {
        int segmentLength;
        int extraBytes = 0;
        if (data.length > 20) {
            extraBytes = ((data.length - 20) / 20) + 1;
        }
        int totalDataLength = data.length + extraBytes;
        int segmentCount = ((totalDataLength - 1) / 20) + 1;
        byte[] indexedData = new byte[totalDataLength];
        int it = 0;
        int segmentIndex = 0;
        int i = 0;
        while (i < data.length) {
            if (i != 0 && it % 20 == 0) {
                indexedData[it] = (byte) segmentIndex;
                it++;
                segmentIndex++;
            }
            indexedData[it] = data[i];
            i++;
            it++;
        }
        for (int i2 = 0; i2 < segmentCount; i2++) {
            int segmentStart = i2 * 20;
            if (i2 == segmentCount - 1) {
                segmentLength = indexedData.length - segmentStart;
            } else {
                segmentLength = 20;
            }
            byte[] segment = new byte[segmentLength];
            System.arraycopy(indexedData, segmentStart, segment, 0, segmentLength);
            builder.write(characteristic, segment);
        }
    }

    private MakibesHR3DeviceSupport factoryReset(TransactionBuilder transaction) {
        transaction.write(this.mControlCharacteristic, craftData((byte) 35));
        return reboot(transaction);
    }

    private MakibesHR3DeviceSupport requestFitness(TransactionBuilder transaction, int yearStepsAfter, int monthStepsAfter, int dayStepsAfter, int hourStepsAfter, int minuteStepsAfter, int yearHeartRateAfter, int monthHeartRateAfter, int dayHeartRateAfter, int hourHeartRateAfter, int minuteHeartRateAfter) {
        transaction.write(this.mControlCharacteristic, craftData((byte) 81, new byte[]{0, (byte) (yearStepsAfter - 2000), (byte) monthStepsAfter, (byte) dayStepsAfter, (byte) hourStepsAfter, (byte) minuteStepsAfter, (byte) (yearHeartRateAfter - 2000), (byte) monthHeartRateAfter, (byte) dayHeartRateAfter, (byte) hourHeartRateAfter, (byte) minuteHeartRateAfter}));
        fetch(true);
        return this;
    }

    private MakibesHR3DeviceSupport requestFitness(TransactionBuilder transaction) {
        Throwable th;
        try {
            DBHandler dbHandler = GBApplication.acquireDB();
            try {
                MakibesHR3ActivitySample latestSample = (MakibesHR3ActivitySample) new MakibesHR3SampleProvider(getDevice(), dbHandler.getDaoSession()).getLatestActivitySample();
                if (latestSample == null) {
                    requestFitness(transaction, ActivityUser.defaultUserCaloriesBurnt, 0, 0, 0, 0, ActivityUser.defaultUserCaloriesBurnt, 0, 0, 0, 0);
                } else {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(new Date(((long) latestSample.getTimestamp()) * 1000));
                    int year = calendar.get(1);
                    int month = 1 + calendar.get(2);
                    int day = calendar.get(5);
                    int hour = calendar.get(11);
                    int minute = calendar.get(12);
                    requestFitness(transaction, year, month, day, hour, minute, year, month, day, hour, minute);
                }
                if (dbHandler != null) {
                    dbHandler.close();
                }
            } catch (Throwable th2) {
                Throwable th3 = th2;
                if (dbHandler != null) {
                    dbHandler.close();
                }
                throw th3;
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        } catch (Throwable th4) {
            th.addSuppressed(th4);
        }
        return this;
    }

    private MakibesHR3DeviceSupport findDevice(TransactionBuilder transaction) {
        transaction.write(this.mControlCharacteristic, craftData((byte) 113));
        return this;
    }

    private MakibesHR3DeviceSupport sendNotification(TransactionBuilder transaction, byte source, String message) {
        byte[] data = new byte[(message.length() + 2)];
        data[0] = source;
        data[1] = 2;
        for (int i = 0; i < message.length(); i++) {
            data[i + 2] = (byte) message.charAt(i);
        }
        writeSafe(this.mControlCharacteristic, transaction, craftData((byte) MakibesHR3Constants.CMD_SEND_NOTIFICATION, data));
        return this;
    }

    private MakibesHR3DeviceSupport setAlarmReminder(TransactionBuilder transaction, int id, boolean enable, int hour, int minute, byte repeat) {
        transaction.write(this.mControlCharacteristic, craftData((byte) MakibesHR3Constants.CMD_SET_ALARM_REMINDER, new byte[]{(byte) id, enable ? (byte) 1 : 0, (byte) hour, (byte) minute, repeat}));
        return this;
    }

    private MakibesHR3DeviceSupport setPersonalInformation(TransactionBuilder transactionBuilder, int stepLength, int age, int height, int weight, int stepGoal) {
        transactionBuilder.write(this.mControlCharacteristic, craftData((byte) MakibesHR3Constants.CMD_SET_PERSONAL_INFORMATION, new byte[]{(byte) stepLength, (byte) age, (byte) height, (byte) weight, 1, (byte) stepGoal, 90, -126, 60, 90, LiveviewConstants.MSG_SETLED, -76, ZeTimeConstants.CMD_HEARTRATE_ALARM_LIMITS, 100}));
        return this;
    }

    private MakibesHR3DeviceSupport setHeadsUpScreen(TransactionBuilder transactionBuilder, boolean enable) {
        transactionBuilder.write(this.mControlCharacteristic, craftData((byte) 119, new byte[]{enable ? (byte) 1 : 0}));
        return this;
    }

    private MakibesHR3DeviceSupport setQuiteHours(TransactionBuilder transactionBuilder, boolean enable, int hourStart, int minuteStart, int hourEnd, int minuteEnd) {
        transactionBuilder.write(this.mControlCharacteristic, craftData((byte) 118, new byte[]{enable ? (byte) 1 : 0, (byte) hourStart, (byte) minuteStart, (byte) hourEnd, (byte) minuteEnd}));
        return this;
    }

    private MakibesHR3DeviceSupport setQuiteHours(TransactionBuilder transactionBuilder, SharedPreferences sharedPreferences) {
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        return setQuiteHours(transactionBuilder, MakibesHR3Coordinator.getQuiteHours(sharedPreferences, start, end), start.get(11), start.get(12), end.get(11), end.get(12));
    }

    private MakibesHR3DeviceSupport setHeadsUpScreen(TransactionBuilder transactionBuilder, SharedPreferences sharedPreferences) {
        return setHeadsUpScreen(transactionBuilder, MakibesHR3Coordinator.shouldEnableHeadsUpScreen(sharedPreferences));
    }

    private MakibesHR3DeviceSupport setLostReminder(TransactionBuilder transactionBuilder, boolean enable) {
        transactionBuilder.write(this.mControlCharacteristic, craftData((byte) MakibesHR3Constants.CMD_SET_LOST_REMINDER, new byte[]{enable ? (byte) 1 : 0}));
        return this;
    }

    private MakibesHR3DeviceSupport setLostReminder(TransactionBuilder transactionBuilder, SharedPreferences sharedPreferences) {
        return setLostReminder(transactionBuilder, MakibesHR3Coordinator.shouldEnableLostReminder(sharedPreferences));
    }

    private MakibesHR3DeviceSupport setTimeMode(TransactionBuilder transactionBuilder, byte timeMode) {
        transactionBuilder.write(this.mControlCharacteristic, craftData((byte) MakibesHR3Constants.CMD_SET_TIMEMODE, new byte[]{timeMode}));
        return this;
    }

    private MakibesHR3DeviceSupport setTimeMode(TransactionBuilder transactionBuilder, SharedPreferences sharedPreferences) {
        return setTimeMode(transactionBuilder, MakibesHR3Coordinator.getTimeMode(sharedPreferences));
    }

    private MakibesHR3DeviceSupport setEnableRealTimeHeartRate(TransactionBuilder transaction, boolean enable) {
        transaction.write(this.mControlCharacteristic, craftData((byte) -124, new byte[]{enable ? (byte) 1 : 0}));
        return this;
    }

    private MakibesHR3DeviceSupport setDateTime(TransactionBuilder transaction, int year, int month, int day, int hour, int minute, int second) {
        transaction.write(this.mControlCharacteristic, craftData((byte) MakibesHR3Constants.CMD_SET_DATE_TIME, new byte[]{0, (byte) ((65280 & year) >> 8), (byte) (year & 255), (byte) month, (byte) day, (byte) hour, (byte) minute, (byte) second}));
        return this;
    }

    private MakibesHR3DeviceSupport setDateTime(TransactionBuilder transaction) {
        Calendar calendar = Calendar.getInstance();
        return setDateTime(transaction, calendar.get(1), calendar.get(2) + 1, calendar.get(5), calendar.get(11), calendar.get(12), calendar.get(13));
    }

    private MakibesHR3DeviceSupport reboot(TransactionBuilder transaction) {
        transaction.write(this.mControlCharacteristic, craftData((byte) -1));
        return this;
    }
}
