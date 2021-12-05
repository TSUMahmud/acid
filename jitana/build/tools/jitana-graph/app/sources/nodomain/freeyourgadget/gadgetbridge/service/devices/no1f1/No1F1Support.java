package nodomain.freeyourgadget.gadgetbridge.service.devices.no1f1;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.net.Uri;
import android.text.format.DateFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.SettingsActivity;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.no1f1.No1F1Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.no1f1.No1F1SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.No1F1ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceBusyAction;
import nodomain.freeyourgadget.gadgetbridge.util.AlarmUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class No1F1Support extends AbstractBTLEDeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) No1F1Support.class);
    private final GBDeviceEventBatteryInfo batteryCmd = new GBDeviceEventBatteryInfo();
    private byte crc = 0;
    public BluetoothGattCharacteristic ctrlCharacteristic = null;
    private int firstTimestamp = 0;
    public BluetoothGattCharacteristic measureCharacteristic = null;
    private List<No1F1ActivitySample> samples = new ArrayList();
    private final GBDeviceEventVersionInfo versionCmd = new GBDeviceEventVersionInfo();

    public No1F1Support() {
        super(LOG);
        addSupportedService(No1F1Constants.UUID_SERVICE_NO1);
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        LOG.info("Initializing");
        this.gbDevice.setState(GBDevice.State.INITIALIZING);
        this.gbDevice.sendDeviceUpdateIntent(getContext());
        this.measureCharacteristic = getCharacteristic(No1F1Constants.UUID_CHARACTERISTIC_MEASURE);
        this.ctrlCharacteristic = getCharacteristic(No1F1Constants.UUID_CHARACTERISTIC_CONTROL);
        builder.setGattCallback(this);
        builder.notify(this.measureCharacteristic, true);
        setTime(builder);
        sendSettings(builder);
        builder.write(this.ctrlCharacteristic, new byte[]{No1F1Constants.CMD_FIRMWARE_VERSION});
        builder.write(this.ctrlCharacteristic, new byte[]{No1F1Constants.CMD_BATTERY});
        this.gbDevice.setState(GBDevice.State.INITIALIZED);
        this.gbDevice.sendDeviceUpdateIntent(getContext());
        LOG.info("Initialization Done");
        return builder;
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (super.onCharacteristicChanged(gatt, characteristic)) {
            return true;
        }
        UUID characteristicUUID = characteristic.getUuid();
        byte[] data = characteristic.getValue();
        if (data.length == 0) {
            return true;
        }
        byte b = data[0];
        if (b != -87) {
            if (!(b == -63 || b == -61 || b == -45)) {
                if (!(b == -78 || b == -77)) {
                    if (b == -27) {
                        handleRealtimeHeartRateData(data);
                        return true;
                    } else if (b != -26) {
                        switch (b) {
                            case -96:
                                break;
                            case -95:
                                this.versionCmd.fwVersion = new String(Arrays.copyOfRange(data, 1, data.length));
                                handleGBDeviceEvent(this.versionCmd);
                                Logger logger = LOG;
                                logger.info("Firmware version is: " + this.versionCmd.fwVersion);
                                return true;
                            case -94:
                                GBDeviceEventBatteryInfo gBDeviceEventBatteryInfo = this.batteryCmd;
                                gBDeviceEventBatteryInfo.level = (short) data[1];
                                handleGBDeviceEvent(gBDeviceEventBatteryInfo);
                                Logger logger2 = LOG;
                                logger2.info("Battery level is: " + data[1]);
                                return true;
                            case -93:
                                Logger logger3 = LOG;
                                logger3.info("Time is set to: " + ((data[1] * 256) + (data[2] & 255)) + "-" + data[3] + "-" + data[4] + StringUtils.SPACE + data[5] + ":" + data[6] + ":" + data[7]);
                                return true;
                            default:
                                Logger logger4 = LOG;
                                logger4.warn("Unhandled characteristic change: " + characteristicUUID + " code: " + Arrays.toString(data));
                                return true;
                        }
                    }
                }
                handleActivityData(data);
                return true;
            }
            return true;
        }
        LOG.info("User data updated");
        return true;
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.service.devices.no1f1.No1F1Support$1 */
    static /* synthetic */ class C12071 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$model$NotificationType */
        static final /* synthetic */ int[] f191xadd9a595 = new int[NotificationType.values().length];

        static {
            try {
                f191xadd9a595[NotificationType.GENERIC_SMS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    public void onNotification(NotificationSpec notificationSpec) {
        if (C12071.f191xadd9a595[notificationSpec.type.ordinal()] != 1) {
            showIcon(2);
            setVibration(1, 2);
            return;
        }
        showNotification(3, notificationSpec.phoneNumber, notificationSpec.body);
        setVibration(1, 3);
    }

    public void onDeleteNotification(int id) {
    }

    public void onSetTime() {
        try {
            TransactionBuilder builder = performInitialized("setTime");
            setTime(builder);
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error setting time: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        try {
            TransactionBuilder builder = performInitialized("Set alarm");
            boolean anyAlarmEnabled = false;
            Iterator<? extends Alarm> it = alarms.iterator();
            while (it.hasNext()) {
                Alarm alarm = (Alarm) it.next();
                anyAlarmEnabled |= alarm.getEnabled();
                Calendar calendar = AlarmUtils.toCalendar(alarm);
                int i = 2;
                if (alarm.getPosition() < 3) {
                    int daysMask = 0;
                    if (alarm.getEnabled()) {
                        int daysMask2 = alarm.getRepetition();
                        daysMask = (daysMask2 / 64) + (daysMask2 >> 1);
                    }
                    byte[] alarmMessage = new byte[9];
                    alarmMessage[0] = No1F1Constants.CMD_ALARM;
                    alarmMessage[1] = (byte) daysMask;
                    alarmMessage[2] = (byte) calendar.get(11);
                    alarmMessage[3] = (byte) calendar.get(12);
                    alarmMessage[4] = (byte) (alarm.getEnabled() ? 2 : 0);
                    alarmMessage[5] = (byte) (alarm.getEnabled() ? 10 : 0);
                    if (!alarm.getEnabled()) {
                        i = 0;
                    }
                    alarmMessage[6] = (byte) i;
                    alarmMessage[7] = 0;
                    alarmMessage[8] = (byte) (alarm.getPosition() + 1);
                    builder.write(this.ctrlCharacteristic, alarmMessage);
                } else if (alarm.getEnabled()) {
                    C1238GB.toast(getContext(), "Only 3 alarms are supported.", 1, 2);
                    return;
                } else {
                    return;
                }
            }
            builder.queue(getQueue());
            if (anyAlarmEnabled) {
                C1238GB.toast(getContext(), getContext().getString(C0889R.string.user_feedback_miband_set_alarms_ok), 0, 1);
            } else {
                C1238GB.toast(getContext(), getContext().getString(C0889R.string.user_feedback_all_alarms_disabled), 0, 1);
            }
        } catch (IOException ex) {
            C1238GB.toast(getContext(), getContext().getString(C0889R.string.user_feedback_miband_set_alarms_failed), 1, 3, ex);
        }
    }

    public void onSetCallState(CallSpec callSpec) {
        if (callSpec.command == 2) {
            showNotification(2, callSpec.name, callSpec.number);
            setVibration(3, 5);
            return;
        }
        stopNotification();
        setVibration(0, 0);
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
        sendFetchCommand(No1F1Constants.CMD_FETCH_STEPS);
    }

    public void onReset(int flags) {
    }

    public void onHeartRateTest() {
        try {
            TransactionBuilder builder = performInitialized("heartRateTest");
            builder.write(this.ctrlCharacteristic, new byte[]{-27, 17});
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error starting heart rate measurement: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
    }

    public void onFindDevice(boolean start) {
        if (start) {
            setVibration(3, 10);
        } else {
            setVibration(0, 0);
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
        try {
            TransactionBuilder builder = performInitialized("Sending configuration for option: " + config);
            char c = 65535;
            if (config.hashCode() == -820369390 && config.equals(SettingsActivity.PREF_MEASUREMENT_SYSTEM)) {
                c = 0;
            }
            if (c == 0) {
                setDisplaySettings(builder);
            }
            builder.queue(getQueue());
        } catch (IOException e) {
            C1238GB.toast("Error setting configuration", 1, 3, (Throwable) e);
        }
    }

    public void onReadConfiguration(String config) {
    }

    public void onTestNewFunction() {
    }

    public void onSendWeather(WeatherSpec weatherSpec) {
    }

    public boolean useAutoConnect() {
        return true;
    }

    private void setTime(TransactionBuilder transaction) {
        Calendar c = GregorianCalendar.getInstance();
        transaction.write(this.ctrlCharacteristic, new byte[]{No1F1Constants.CMD_DATETIME, (byte) ((c.get(1) / 256) & 255), (byte) (c.get(1) % 256), (byte) (c.get(2) + 1), (byte) c.get(5), (byte) c.get(11), (byte) c.get(12), (byte) c.get(13)});
    }

    private No1F1Support setDisplaySettings(TransactionBuilder transaction) {
        byte[] displayBytes = {No1F1Constants.CMD_DISPLAY_SETTINGS, 0, 0};
        if (GBApplication.getPrefs().getString(SettingsActivity.PREF_MEASUREMENT_SYSTEM, getContext().getString(C0889R.string.p_unit_metric)).equals(getContext().getString(C0889R.string.p_unit_metric))) {
            displayBytes[1] = 1;
        } else {
            displayBytes[1] = 2;
        }
        if (DateFormat.is24HourFormat(getContext())) {
            displayBytes[2] = 1;
        } else {
            displayBytes[2] = 2;
        }
        transaction.write(this.ctrlCharacteristic, displayBytes);
        return this;
    }

    private void sendSettings(TransactionBuilder builder) {
        ActivityUser activityUser = new ActivityUser();
        double heightCm = (double) activityUser.getHeightCm();
        Double.isNaN(heightCm);
        byte[] userBytes = {No1F1Constants.CMD_USER_DATA, 0, (byte) ((int) Math.round(heightCm * 0.43d)), 0, (byte) activityUser.getWeightKg(), 5, 0, 0, (byte) (activityUser.getStepsGoal() / 256), (byte) (activityUser.getStepsGoal() % 256), 1, -1, 0, (byte) activityUser.getAge(), 0};
        if (activityUser.getGender() == 0) {
            userBytes[14] = 2;
        } else {
            userBytes[14] = 1;
        }
        builder.write(this.ctrlCharacteristic, userBytes);
        builder.write(this.ctrlCharacteristic, new byte[]{-45, 0, 60, 2, 3, 1, 0});
        setDisplaySettings(builder);
        builder.write(this.ctrlCharacteristic, new byte[]{-42, 2});
        builder.write(this.ctrlCharacteristic, new byte[]{-42, 1, 2});
    }

    private void setVibration(int duration, int count) {
        try {
            TransactionBuilder builder = performInitialized("vibrate");
            builder.write(this.ctrlCharacteristic, new byte[]{No1F1Constants.CMD_ALARM, 0, 0, 0, (byte) duration, (byte) count, 2, 1});
            builder.queue(getQueue());
        } catch (IOException e) {
            LOG.warn("Unable to set vibration", (Throwable) e);
        }
    }

    private void showIcon(int iconId) {
        try {
            TransactionBuilder builder = performInitialized("showIcon");
            builder.write(this.ctrlCharacteristic, new byte[]{-61, (byte) iconId});
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error showing icon: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    private void showNotification(int type, String header, String body) {
        try {
            TransactionBuilder builder = performInitialized("showNotification");
            byte[] bytes = header.getBytes("EUC-JP");
            int length = NumberUtils.min(bytes.length, 18);
            byte[] msg = new byte[(length + 2)];
            msg[0] = -63;
            msg[1] = 1;
            System.arraycopy(bytes, 0, msg, 2, length);
            builder.write(this.ctrlCharacteristic, msg);
            byte[] bytes2 = header.getBytes("EUC-JP");
            int length2 = NumberUtils.min(bytes2.length, 18);
            byte[] msg2 = new byte[(length2 + 2)];
            msg2[0] = -63;
            msg2[1] = (byte) type;
            System.arraycopy(bytes2, 0, msg2, 2, length2);
            builder.write(this.ctrlCharacteristic, msg2);
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error showing notificaton: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    private void stopNotification() {
        try {
            TransactionBuilder builder = performInitialized("clearNotification");
            builder.write(this.ctrlCharacteristic, new byte[]{-63, 4});
            builder.queue(getQueue());
        } catch (IOException e) {
            LOG.warn("Unable to stop notification", (Throwable) e);
        }
    }

    private void sendFetchCommand(byte type) {
        this.samples.clear();
        this.crc = 0;
        this.firstTimestamp = 0;
        try {
            TransactionBuilder builder = performInitialized("fetchActivityData");
            builder.add(new SetDeviceBusyAction(getDevice(), getContext().getString(C0889R.string.busy_task_fetch_activity_data), getContext()));
            builder.write(this.ctrlCharacteristic, new byte[]{type, -6});
            builder.queue(getQueue());
        } catch (IOException e) {
            Context context = getContext();
            C1238GB.toast(context, "Error fetching activity data: " + e.getLocalizedMessage(), 1, 3);
        }
    }

    private void handleActivityData(byte[] data) {
        Throwable th;
        if (data[1] == -3) {
            LOG.info("CRC received: " + (data[2] & 255) + ", calculated: " + (this.crc & 255));
            if (data[2] != this.crc) {
                C1238GB.toast(getContext(), "Incorrect CRC. Try fetching data again.", 1, 3);
                C1238GB.updateTransferNotification((String) null, "Data transfer failed", false, 0, getContext());
                if (getDevice().isBusy()) {
                    getDevice().unsetBusyTask();
                    getDevice().sendDeviceUpdateIntent(getContext());
                }
            } else if (this.samples.size() > 0) {
                try {
                    DBHandler dbHandler = GBApplication.acquireDB();
                    try {
                        Long userId = DBHelper.getUser(dbHandler.getDaoSession()).getId();
                        Long deviceId = DBHelper.getDevice(getDevice(), dbHandler.getDaoSession()).getId();
                        No1F1SampleProvider provider = new No1F1SampleProvider(getDevice(), dbHandler.getDaoSession());
                        for (int i = 0; i < this.samples.size(); i++) {
                            this.samples.get(i).setDeviceId(deviceId.longValue());
                            this.samples.get(i).setUserId(userId.longValue());
                            if (data[0] == -78) {
                                this.samples.get(i).setRawKind(1);
                                this.samples.get(i).setRawIntensity(this.samples.get(i).getSteps());
                            } else if (data[0] == -77) {
                                if (this.samples.get(i).getRawIntensity() < 7) {
                                    this.samples.get(i).setRawKind(4);
                                } else {
                                    this.samples.get(i).setRawKind(2);
                                }
                            }
                            provider.addGBActivitySample(this.samples.get(i));
                        }
                        LOG.info("Activity data saved");
                        if (data[0] == -78) {
                            sendFetchCommand(No1F1Constants.CMD_FETCH_SLEEP);
                        } else if (data[0] == -77) {
                            sendFetchCommand((byte) -26);
                        } else {
                            C1238GB.updateTransferNotification((String) null, "", false, 100, getContext());
                            if (getDevice().isBusy()) {
                                getDevice().unsetBusyTask();
                                C1238GB.signalActivityDataFinish();
                            }
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
                    C1238GB.toast(getContext(), "Error saving activity data: " + ex.getLocalizedMessage(), 1, 3);
                    C1238GB.updateTransferNotification((String) null, "Data transfer failed", false, 0, getContext());
                } catch (Throwable th4) {
                    th.addSuppressed(th4);
                }
            }
        } else {
            No1F1ActivitySample sample = new No1F1ActivitySample();
            Calendar timestamp = GregorianCalendar.getInstance();
            timestamp.set(1, (data[1] * 256) + (data[2] & 255));
            timestamp.set(2, (data[3] - 1) & 255);
            timestamp.set(5, data[4] & 255);
            timestamp.set(11, data[5] & 255);
            timestamp.set(13, 0);
            int startProgress = 0;
            if (data[0] == -78) {
                timestamp.set(12, 0);
                sample.setSteps((data[6] * 256) + (data[7] & 255));
                this.crc = (byte) (this.crc ^ (data[7] ^ data[6]));
            } else if (data[0] == -77) {
                timestamp.set(12, data[6] & 255);
                sample.setRawIntensity((data[7] * 256) + (data[8] & 255));
                this.crc = (byte) ((data[7] ^ data[8]) ^ this.crc);
                startProgress = 33;
            } else if (data[0] == -26) {
                timestamp.set(12, data[6] & 255);
                sample.setHeartRate(data[7] & 255);
                this.crc = (byte) ((data[7] ^ data[6]) ^ this.crc);
                startProgress = 66;
            }
            sample.setTimestamp((int) (timestamp.getTimeInMillis() / 1000));
            this.samples.add(sample);
            if (this.firstTimestamp == 0) {
                this.firstTimestamp = sample.getTimestamp();
            }
            C1238GB.updateTransferNotification((String) null, getContext().getString(C0889R.string.busy_task_fetch_activity_data), true, (((sample.getTimestamp() - this.firstTimestamp) * 33) / (((int) (Calendar.getInstance().getTimeInMillis() / 1000)) - this.firstTimestamp)) + startProgress, getContext());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x00a0, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x00a1, code lost:
        if (r1 != null) goto L_0x00a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00ab, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void handleRealtimeHeartRateData(byte[] r8) {
        /*
            r7 = this;
            int r0 = r8.length
            r1 = 2
            if (r0 != r1) goto L_0x001b
            r0 = 1
            byte r0 = r8[r0]
            r1 = 17
            if (r0 != r1) goto L_0x0013
            org.slf4j.Logger r0 = LOG
            java.lang.String r1 = "Heart rate measurement started."
            r0.info(r1)
            goto L_0x001a
        L_0x0013:
            org.slf4j.Logger r0 = LOG
            java.lang.String r1 = "Heart rate measurement stopped."
            r0.info(r1)
        L_0x001a:
            return
        L_0x001b:
            byte r0 = r8[r1]
            if (r0 != 0) goto L_0x00c7
            nodomain.freeyourgadget.gadgetbridge.entities.No1F1ActivitySample r0 = new nodomain.freeyourgadget.gadgetbridge.entities.No1F1ActivitySample
            r0.<init>()
            java.util.Calendar r1 = java.util.GregorianCalendar.getInstance()
            long r1 = r1.getTimeInMillis()
            r3 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 / r3
            int r2 = (int) r1
            r0.setTimestamp(r2)
            r1 = 3
            byte r1 = r8[r1]
            r1 = r1 & 255(0xff, float:3.57E-43)
            r0.setHeartRate(r1)
            org.slf4j.Logger r1 = LOG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Current heart rate is: "
            r2.append(r3)
            int r3 = r0.getHeartRate()
            r2.append(r3)
            java.lang.String r3 = " BPM"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.info(r2)
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r1 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x00ac }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r2 = r1.getDaoSession()     // Catch:{ all -> 0x009e }
            nodomain.freeyourgadget.gadgetbridge.entities.User r2 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getUser(r2)     // Catch:{ all -> 0x009e }
            java.lang.Long r2 = r2.getId()     // Catch:{ all -> 0x009e }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r3 = r7.getDevice()     // Catch:{ all -> 0x009e }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r4 = r1.getDaoSession()     // Catch:{ all -> 0x009e }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r3 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getDevice(r3, r4)     // Catch:{ all -> 0x009e }
            java.lang.Long r3 = r3.getId()     // Catch:{ all -> 0x009e }
            nodomain.freeyourgadget.gadgetbridge.devices.no1f1.No1F1SampleProvider r4 = new nodomain.freeyourgadget.gadgetbridge.devices.no1f1.No1F1SampleProvider     // Catch:{ all -> 0x009e }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r5 = r7.getDevice()     // Catch:{ all -> 0x009e }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r6 = r1.getDaoSession()     // Catch:{ all -> 0x009e }
            r4.<init>(r5, r6)     // Catch:{ all -> 0x009e }
            long r5 = r3.longValue()     // Catch:{ all -> 0x009e }
            r0.setDeviceId(r5)     // Catch:{ all -> 0x009e }
            long r5 = r2.longValue()     // Catch:{ all -> 0x009e }
            r0.setUserId(r5)     // Catch:{ all -> 0x009e }
            r4.addGBActivitySample(r0)     // Catch:{ all -> 0x009e }
            if (r1 == 0) goto L_0x009d
            r1.close()     // Catch:{ Exception -> 0x00ac }
        L_0x009d:
            goto L_0x00c7
        L_0x009e:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x00a0 }
        L_0x00a0:
            r3 = move-exception
            if (r1 == 0) goto L_0x00ab
            r1.close()     // Catch:{ all -> 0x00a7 }
            goto L_0x00ab
        L_0x00a7:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ Exception -> 0x00ac }
        L_0x00ab:
            throw r3     // Catch:{ Exception -> 0x00ac }
        L_0x00ac:
            r1 = move-exception
            org.slf4j.Logger r2 = LOG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Error saving current heart rate: "
            r3.append(r4)
            java.lang.String r4 = r1.getLocalizedMessage()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.warn(r3)
        L_0x00c7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.no1f1.No1F1Support.handleRealtimeHeartRateData(byte[]):void");
    }
}
