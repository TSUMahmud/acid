package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import cyanogenmod.alarmclock.ClockContract;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.no1f1.No1F1Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.NotificationConfiguration;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.GenericItem;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.buttonconfig.ConfigFileBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.buttonconfig.ConfigPayload;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.RequestMtuRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.SetDeviceStateRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.alarm.AlarmsSetRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.configuration.ConfigurationPutRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file.FilePutRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.notification.NotificationFilterPutRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.notification.PlayNotificationRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.AnimationRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.MoveHandsRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.PlayNotificationRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.ReleaseHandsControlRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.RequestHandControlRequest;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p005ch.qos.logback.core.CoreConstants;

public class FossilWatchAdapter extends WatchAdapter {
    public static final String ITEM_BUTTONS = "BUTTONS";
    public final String CONFIG_ITEM_BUTTONS = "buttons";
    private final String CONFIG_ITEM_STEP_GOAL = "step_goal";
    private final String CONFIG_ITEM_TIMEZONE_OFFSET = ClockContract.CitiesColumns.TIMEZONE_OFFSET;
    private final String CONFIG_ITEM_VIBRATION_STRENGTH = "vibration_strength";
    private final String ITEM_MTU = "MTU";
    private int MTU = 23;
    private FossilRequest fossilRequest;
    private int lastButtonIndex = -1;
    Logger logger = LoggerFactory.getLogger(getClass());
    private ArrayList<Request> requestQueue = new ArrayList<>();

    public FossilWatchAdapter(QHybridSupport deviceSupport) {
        super(deviceSupport);
    }

    public void initialize() {
        playPairingAnimation();
        if (Build.VERSION.SDK_INT >= 21) {
            queueWrite(new RequestMtuRequest(512), false);
        }
        syncConfiguration();
        syncNotificationSettings();
        syncButtonSettings();
        queueWrite(new SetDeviceStateRequest(GBDevice.State.INITIALIZED), false);
    }

    private void syncButtonSettings() {
        String buttonConfig = getDeviceSpecificPreferences().getString("buttons", (String) null);
        getDeviceSupport().getDevice().addDeviceInfo(new GenericItem(ITEM_BUTTONS, buttonConfig));
        overwriteButtons(buttonConfig);
    }

    private SharedPreferences getDeviceSpecificPreferences() {
        return GBApplication.getDeviceSpecificSharedPrefs(getDeviceSupport().getDevice().getAddress());
    }

    private void syncConfiguration() {
        SharedPreferences preferences = getDeviceSpecificPreferences();
        int stepGoal = preferences.getInt("step_goal", 1000000);
        byte vibrationStrength = (byte) preferences.getInt("vibration_strength", 100);
        int timezoneOffset = preferences.getInt(ClockContract.CitiesColumns.TIMEZONE_OFFSET, 0);
        GBDevice device = getDeviceSupport().getDevice();
        device.addDeviceInfo(new GenericItem(QHybridSupport.ITEM_STEP_GOAL, String.valueOf(stepGoal)));
        device.addDeviceInfo(new GenericItem(QHybridSupport.ITEM_VIBRATION_STRENGTH, String.valueOf(vibrationStrength)));
        device.addDeviceInfo(new GenericItem(QHybridSupport.ITEM_TIMEZONE_OFFSET, String.valueOf(timezoneOffset)));
        queueWrite(new ConfigurationPutRequest(new ConfigurationPutRequest.ConfigItem[]{new ConfigurationPutRequest.DailyStepGoalConfigItem(stepGoal), new ConfigurationPutRequest.VibrationStrengthConfigItem(Byte.valueOf(vibrationStrength)), new ConfigurationPutRequest.TimezoneOffsetConfigItem(Short.valueOf((short) timezoneOffset))}, this));
    }

    public int getMTU() {
        int i = this.MTU;
        if (i >= 0) {
            return i;
        }
        throw new RuntimeException("MTU not configured");
    }

    public void playPairingAnimation() {
        queueWrite((Request) new AnimationRequest(), false);
    }

    public void playNotification(NotificationConfiguration config) {
        if (config.getPackageName() == null) {
            log("package name in notification not set");
        } else {
            queueWrite((FossilRequest) new PlayNotificationRequest(config.getPackageName(), this), false);
        }
    }

    public void setTime() {
        long millis = System.currentTimeMillis();
        TimeZone zone = new GregorianCalendar().getTimeZone();
        queueWrite((FossilRequest) new ConfigurationPutRequest((ConfigurationPutRequest.ConfigItem) new ConfigurationPutRequest.TimeConfigItem((int) ((millis / 1000) + (getDeviceSupport().getTimeOffset() * 60)), (short) ((int) (millis % 1000)), (short) ((zone.getRawOffset() + (zone.inDaylightTime(new Date()) ? 1 : 0)) / CoreConstants.MILLIS_IN_ONE_MINUTE)), this), false);
    }

    public void overwriteButtons(String jsonConfigString) {
        if (jsonConfigString != null) {
            try {
                getDeviceSpecificPreferences().edit().putString("buttons", jsonConfigString).apply();
                JSONArray buttonConfigJson = new JSONArray(jsonConfigString);
                ConfigPayload[] payloads = new ConfigPayload[buttonConfigJson.length()];
                for (int i = 0; i < buttonConfigJson.length(); i++) {
                    try {
                        payloads[i] = ConfigPayload.valueOf(buttonConfigJson.getString(i));
                    } catch (IllegalArgumentException e) {
                        payloads[i] = ConfigPayload.FORWARD_TO_PHONE;
                    }
                }
                queueWrite(new FilePutRequest(1536, new ConfigFileBuilder(payloads).build(true), this) {
                    public void onFilePut(boolean success) {
                        if (success) {
                            C1238GB.toast("successfully overwritten button settings", 0, 1);
                        } else {
                            C1238GB.toast("error overwriting button settings", 0, 1);
                        }
                    }
                });
            } catch (JSONException e2) {
                C1238GB.log("error", 3, e2);
            }
        }
    }

    public void setActivityHand(double progress) {
        queueWrite((FossilRequest) new ConfigurationPutRequest((ConfigurationPutRequest.ConfigItem) new ConfigurationPutRequest.CurrentStepCountConfigItem(Integer.valueOf(Math.min(999999, (int) (1000000.0d * progress)))), this), false);
    }

    public void setHands(short hour, short minute) {
        queueWrite((Request) new MoveHandsRequest(false, minute, hour, -1), false);
    }

    public void vibrate(PlayNotificationRequest.VibrationType vibration) {
    }

    public void vibrateFindMyDevicePattern() {
    }

    public void requestHandsControl() {
        queueWrite((Request) new RequestHandControlRequest(), false);
    }

    public void releaseHandsControl() {
        queueWrite((Request) new ReleaseHandsControlRequest(), false);
    }

    public void setStepGoal(int stepGoal) {
        getDeviceSpecificPreferences().edit().putInt("step_goal", stepGoal).apply();
        queueWrite((FossilRequest) new ConfigurationPutRequest(new ConfigurationPutRequest.DailyStepGoalConfigItem(stepGoal), this) {
            public void onFilePut(boolean success) {
                if (success) {
                    C1238GB.toast("successfully updated step goal", 0, 1);
                } else {
                    C1238GB.toast("error updating step goal", 0, 1);
                }
            }
        }, false);
    }

    public void setVibrationStrength(short strength) {
        getDeviceSpecificPreferences().edit().putInt("vibration_strength", (byte) strength).apply();
        queueWrite((FossilRequest) new ConfigurationPutRequest(new ConfigurationPutRequest.ConfigItem[]{new ConfigurationPutRequest.VibrationStrengthConfigItem(Byte.valueOf((byte) strength))}, this) {
            public void onFilePut(boolean success) {
                if (success) {
                    C1238GB.toast("successfully updated vibration strength", 0, 1);
                } else {
                    C1238GB.toast("error updating vibration strength", 0, 1);
                }
            }
        }, false);
    }

    public void syncNotificationSettings() {
        log("syncing notification settings...");
        try {
            ArrayList<NotificationConfiguration> configurations = new PackageConfigHelper(getContext()).getNotificationConfigurations();
            if (configurations.size() == 1) {
                configurations.add(configurations.get(0));
            }
            queueWrite((FossilRequest) new NotificationFilterPutRequest(configurations, this) {
                public void onFilePut(boolean success) {
                    super.onFilePut(success);
                    if (!success) {
                        C1238GB.toast("error writing notification settings", 0, 3);
                        FossilWatchAdapter.this.getDeviceSupport().getDevice().setState(GBDevice.State.NOT_CONNECTED);
                        FossilWatchAdapter.this.getDeviceSupport().getDevice().sendDeviceUpdateIntent(FossilWatchAdapter.this.getContext());
                    }
                    FossilWatchAdapter.this.getDeviceSupport().getDevice().setState(GBDevice.State.INITIALIZED);
                    FossilWatchAdapter.this.getDeviceSupport().getDevice().sendDeviceUpdateIntent(FossilWatchAdapter.this.getContext());
                }
            }, false);
        } catch (GBException e) {
            C1238GB.log("error", 3, e);
        }
    }

    public void onTestNewFunction() {
        queueWrite(new FilePutRequest(1536, new byte[]{1, 0, 8, 1, 1, 36, 0, MakibesHR3Constants.CMD_85, 1, ZeTimeConstants.CMD_USER_INFO, 82, -1, 38, 0, 3, 0, 9, 4, 1, 3, No1F1Constants.CMD_DISPLAY_SETTINGS, 0, 0, No1F1Constants.CMD_DISPLAY_SETTINGS, 0, 0, 8, 1, 5, 0, MakibesHR3Constants.CMD_SET_DATE_TIME, 0, 2, 9, 4, 1, 3, 0, 36, 0, 0, 36, 0, 8, 1, 80, 0, 1, 0, 31, -66, -76, 27}, this));
    }

    public void setTimezoneOffsetMinutes(short offset) {
        getDeviceSpecificPreferences().edit().putInt(ClockContract.CitiesColumns.TIMEZONE_OFFSET, offset).apply();
        queueWrite(new ConfigurationPutRequest(new ConfigurationPutRequest.TimezoneOffsetConfigItem(Short.valueOf(offset)), this) {
            public void onFilePut(boolean success) {
                super.onFilePut(success);
                if (success) {
                    C1238GB.toast("successfully updated timezone", 0, 1);
                } else {
                    C1238GB.toast("error updating timezone", 0, 3);
                }
            }
        });
    }

    public boolean supportsFindDevice() {
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0044  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0066 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean supportsExtendedVibration() {
        /*
            r6 = this;
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r0 = r6.getDeviceSupport()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r0.getDevice()
            java.lang.String r0 = r0.getModel()
            int r1 = r0.hashCode()
            r2 = 2020235855(0x786a5a4f, float:1.9012955E34)
            r3 = 0
            r4 = 2
            r5 = 1
            if (r1 == r2) goto L_0x0037
            r2 = 2132904456(0x7f218a08, float:2.147224E38)
            if (r1 == r2) goto L_0x002d
            r2 = 2143063187(0x7fbc8c93, float:NaN)
            if (r1 == r2) goto L_0x0023
        L_0x0022:
            goto L_0x0041
        L_0x0023:
            java.lang.String r1 = "HW.0.0"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0022
            r1 = 0
            goto L_0x0042
        L_0x002d:
            java.lang.String r1 = "HL.0.0"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0022
            r1 = 1
            goto L_0x0042
        L_0x0037:
            java.lang.String r1 = "DN.1.0"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0022
            r1 = 2
            goto L_0x0042
        L_0x0041:
            r1 = -1
        L_0x0042:
            if (r1 == 0) goto L_0x0066
            if (r1 == r5) goto L_0x0065
            if (r1 != r4) goto L_0x0049
            return r5
        L_0x0049:
            java.lang.UnsupportedOperationException r1 = new java.lang.UnsupportedOperationException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "model "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r3 = " not supported"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L_0x0065:
            return r3
        L_0x0066:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter.supportsExtendedVibration():boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0044  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0066 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean supportsActivityHand() {
        /*
            r6 = this;
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r0 = r6.getDeviceSupport()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r0.getDevice()
            java.lang.String r0 = r0.getModel()
            int r1 = r0.hashCode()
            r2 = 2020235855(0x786a5a4f, float:1.9012955E34)
            r3 = 2
            r4 = 0
            r5 = 1
            if (r1 == r2) goto L_0x0037
            r2 = 2132904456(0x7f218a08, float:2.147224E38)
            if (r1 == r2) goto L_0x002d
            r2 = 2143063187(0x7fbc8c93, float:NaN)
            if (r1 == r2) goto L_0x0023
        L_0x0022:
            goto L_0x0041
        L_0x0023:
            java.lang.String r1 = "HW.0.0"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0022
            r1 = 0
            goto L_0x0042
        L_0x002d:
            java.lang.String r1 = "HL.0.0"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0022
            r1 = 1
            goto L_0x0042
        L_0x0037:
            java.lang.String r1 = "DN.1.0"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0022
            r1 = 2
            goto L_0x0042
        L_0x0041:
            r1 = -1
        L_0x0042:
            if (r1 == 0) goto L_0x0066
            if (r1 == r5) goto L_0x0065
            if (r1 != r3) goto L_0x0049
            return r4
        L_0x0049:
            java.lang.UnsupportedOperationException r1 = new java.lang.UnsupportedOperationException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Model "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r3 = " not supported"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L_0x0065:
            return r4
        L_0x0066:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter.supportsActivityHand():boolean");
    }

    public void onFetchActivityData() {
        setVibrationStrength(50);
    }

    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        ArrayList<nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.alarm.Alarm> activeAlarms = new ArrayList<>();
        Iterator<? extends Alarm> it = alarms.iterator();
        while (it.hasNext()) {
            Alarm alarm = (Alarm) it.next();
            if (alarm.getEnabled()) {
                if (alarm.getRepetition() == 0) {
                    activeAlarms.add(new nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.alarm.Alarm((byte) alarm.getMinute(), (byte) alarm.getHour(), false));
                } else {
                    int repitition = alarm.getRepetition();
                    activeAlarms.add(new nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.alarm.Alarm((byte) alarm.getMinute(), (byte) alarm.getHour(), (byte) ((repitition << 1) | ((repitition >> 6) & 1))));
                }
            }
        }
        queueWrite(new AlarmsSetRequest((nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.alarm.Alarm[]) activeAlarms.toArray(new nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.alarm.Alarm[0]), this) {
            public void onFilePut(boolean success) {
                super.onFilePut(success);
                if (success) {
                    C1238GB.toast("successfully set alarms", 0, 1);
                } else {
                    C1238GB.toast("error setting alarms", 0, 1);
                }
            }
        });
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onCharacteristicChanged(android.bluetooth.BluetoothGatt r8, android.bluetooth.BluetoothGattCharacteristic r9) {
        /*
            r7 = this;
            java.util.UUID r0 = r9.getUuid()
            java.lang.String r0 = r0.toString()
            int r1 = r0.hashCode()
            java.lang.String r2 = "3dda0003-957f-7d4a-34a6-74696673696d"
            r3 = 2
            r4 = 0
            r5 = 3
            r6 = 1
            switch(r1) {
                case -1648524010: goto L_0x0032;
                case -955422313: goto L_0x002a;
                case -262320616: goto L_0x0020;
                case 1123882778: goto L_0x0016;
                default: goto L_0x0015;
            }
        L_0x0015:
            goto L_0x003c
        L_0x0016:
            java.lang.String r1 = "3dda0006-957f-7d4a-34a6-74696673696d"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0015
            r0 = 0
            goto L_0x003d
        L_0x0020:
            java.lang.String r1 = "3dda0004-957f-7d4a-34a6-74696673696d"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0015
            r0 = 2
            goto L_0x003d
        L_0x002a:
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0015
            r0 = 3
            goto L_0x003d
        L_0x0032:
            java.lang.String r1 = "3dda0002-957f-7d4a-34a6-74696673696d"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0015
            r0 = 1
            goto L_0x003d
        L_0x003c:
            r0 = -1
        L_0x003d:
            if (r0 == 0) goto L_0x00c4
            if (r0 == r6) goto L_0x0047
            if (r0 == r3) goto L_0x0047
            if (r0 == r5) goto L_0x0047
            goto L_0x00c8
        L_0x0047:
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest r0 = r7.fossilRequest
            if (r0 == 0) goto L_0x00c0
            java.util.UUID r0 = r9.getUuid()     // Catch:{ RuntimeException -> 0x0077 }
            java.lang.String r0 = r0.toString()     // Catch:{ RuntimeException -> 0x0077 }
            boolean r0 = r0.equals(r2)     // Catch:{ RuntimeException -> 0x0077 }
            if (r0 == 0) goto L_0x006b
            byte[] r0 = r9.getValue()     // Catch:{ RuntimeException -> 0x0077 }
            byte r0 = r0[r4]     // Catch:{ RuntimeException -> 0x0077 }
            r0 = r0 & 15
            byte r0 = (byte) r0     // Catch:{ RuntimeException -> 0x0077 }
            r1 = 10
            if (r0 == r1) goto L_0x006b
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest r1 = r7.fossilRequest     // Catch:{ RuntimeException -> 0x0077 }
            r1.getType()     // Catch:{ RuntimeException -> 0x0077 }
        L_0x006b:
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest r0 = r7.fossilRequest     // Catch:{ RuntimeException -> 0x0077 }
            r0.handleResponse(r9)     // Catch:{ RuntimeException -> 0x0077 }
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest r0 = r7.fossilRequest     // Catch:{ RuntimeException -> 0x0077 }
            boolean r0 = r0.isFinished()     // Catch:{ RuntimeException -> 0x0077 }
            goto L_0x009f
        L_0x0077:
            r0 = move-exception
            java.lang.String r1 = "error"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.log(r1, r5, r0)
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r1 = r7.getDeviceSupport()
            r1.notifiyException(r0)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest r2 = r7.fossilRequest
            java.lang.String r2 = r2.getName()
            r1.append(r2)
            java.lang.String r2 = " failed"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r1, r4, r5)
            r0 = 1
        L_0x009f:
            if (r0 == 0) goto L_0x00bf
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest r2 = r7.fossilRequest
            java.lang.String r2 = r2.getName()
            r1.append(r2)
            java.lang.String r2 = " finished"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r7.log(r1)
            r1 = 0
            r7.fossilRequest = r1
            goto L_0x00c0
        L_0x00bf:
            return r6
        L_0x00c0:
            r7.queueNextRequest()
            goto L_0x00c8
        L_0x00c4:
            r7.handleBackgroundCharacteristic(r9)
        L_0x00c8:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter.onCharacteristicChanged(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic):boolean");
    }

    private void handleBackgroundCharacteristic(BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        byte b = value[1];
        if (b == 2) {
            byte b2 = value[2];
            getDeviceSupport().getDevice().addDeviceInfo(new GenericItem(QHybridSupport.ITEM_LAST_HEARTBEAT, DateFormat.getTimeInstance().format(new Date())));
        } else if (b != 5) {
            if (b == 8) {
                if (value.length == 12) {
                    int index = value[2] & 255;
                    int button = (value[9] >> 4) & 255;
                    if (index != this.lastButtonIndex) {
                        this.lastButtonIndex = index;
                        log("Button press on button " + button);
                        Intent i = new Intent(QHybridSupport.QHYBRID_EVENT_BUTTON_PRESS);
                        i.putExtra("BUTTON", button);
                        getContext().sendBroadcast(i);
                        return;
                    }
                    return;
                }
                throw new RuntimeException("wrong button message");
            }
        } else if (value.length == 4) {
            byte action = value[3];
            String actionString = "SINGLE";
            if (action == 3) {
                actionString = "DOUBLE";
            } else if (action == 4) {
                actionString = "LONG";
            }
            log(actionString + " button press");
            Intent i2 = new Intent(QHybridSupport.QHYBRID_EVENT_MULTI_BUTTON_PRESS);
            i2.putExtra("ACTION", actionString);
            getContext().sendBroadcast(i2);
        } else {
            throw new RuntimeException("wrong button message");
        }
    }

    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
        log("MTU changed: " + mtu);
        this.MTU = mtu;
        getDeviceSupport().getDevice().addDeviceInfo(new GenericItem("MTU", String.valueOf(mtu)));
        getDeviceSupport().getDevice().sendDeviceUpdateIntent(getContext());
        ((RequestMtuRequest) this.fossilRequest).setFinished(true);
        queueNextRequest();
    }

    public void queueWrite(RequestMtuRequest request, boolean priorise) {
        if (Build.VERSION.SDK_INT >= 21) {
            new TransactionBuilder("requestMtu").requestMtu(512).queue(getDeviceSupport().getQueue());
            this.fossilRequest = request;
        }
    }

    private void log(String message) {
        this.logger.debug(message);
    }

    public void queueWrite(SetDeviceStateRequest request, boolean priorise) {
        FossilRequest fossilRequest2 = this.fossilRequest;
        if (fossilRequest2 == null || fossilRequest2.isFinished()) {
            log("setting device state: " + request.getDeviceState());
            getDeviceSupport().getDevice().setState(request.getDeviceState());
            getDeviceSupport().getDevice().sendDeviceUpdateIntent(getContext());
            queueNextRequest();
            return;
        }
        log("queing request: " + request.getName());
        if (priorise) {
            this.requestQueue.add(0, request);
        } else {
            this.requestQueue.add(request);
        }
    }

    public void queueWrite(FossilRequest request, boolean priorise) {
        FossilRequest fossilRequest2 = this.fossilRequest;
        if (fossilRequest2 == null || fossilRequest2.isFinished()) {
            log("executing request: " + request.getName());
            this.fossilRequest = request;
            new TransactionBuilder(request.getClass().getSimpleName()).write(getDeviceSupport().getCharacteristic(request.getRequestUUID()), request.getRequestData()).queue(getDeviceSupport().getQueue());
            return;
        }
        log("queing request: " + request.getName());
        if (priorise) {
            this.requestQueue.add(0, request);
        } else {
            this.requestQueue.add(request);
        }
    }

    public void queueWrite(Request request, boolean priorise) {
        new TransactionBuilder(request.getClass().getSimpleName()).write(getDeviceSupport().getCharacteristic(request.getRequestUUID()), request.getRequestData()).queue(getDeviceSupport().getQueue());
        queueNextRequest();
    }

    /* access modifiers changed from: package-private */
    public void queueWrite(Request request) {
        if (request instanceof SetDeviceStateRequest) {
            queueWrite((SetDeviceStateRequest) request, false);
        } else if (request instanceof RequestMtuRequest) {
            queueWrite((RequestMtuRequest) request, false);
        } else if (request instanceof FossilRequest) {
            queueWrite((FossilRequest) request, false);
        } else {
            queueWrite(request, false);
        }
    }

    private void queueNextRequest() {
        try {
            queueWrite(this.requestQueue.remove(0));
        } catch (IndexOutOfBoundsException e) {
            log("requestsQueue empty");
        }
    }
}
