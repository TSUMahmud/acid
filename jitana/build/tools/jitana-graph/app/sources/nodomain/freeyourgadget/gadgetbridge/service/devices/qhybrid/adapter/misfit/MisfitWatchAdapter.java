package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.misfit;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.util.SparseArray;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.TimeZone;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusConstants;
import nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3Constants;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.NotificationConfiguration;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeConstants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.GenericItem;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.ActivityPointGetRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.AnimationRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.BatteryLevelRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.DownloadFileRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.EraseFileRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.FileRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.GetCountdownSettingsRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.GetCurrentStepCountRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.GetStepGoalRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.GetVibrationStrengthRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.GoalTrackingGetRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.ListFilesRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.MoveHandsRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.OTAEnterRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.OTAEraseRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.PlayNotificationRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.ReleaseHandsControlRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.RequestHandControlRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.SetCurrentStepCountRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.SetStepGoalRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.SetTimeRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.SetVibrationStrengthRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.UploadFileRequest;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.VibrateRequest;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p005ch.qos.logback.core.CoreConstants;

public class MisfitWatchAdapter extends WatchAdapter {
    private Request fileRequest = null;
    private int lastButtonIndex = -1;
    Logger logger = LoggerFactory.getLogger(getClass());
    private Queue<Request> requestQueue = new ArrayDeque();
    private final SparseArray<Request> responseFilters = new SparseArray<>();
    private UploadFileRequest uploadFileRequest;

    public MisfitWatchAdapter(QHybridSupport deviceSupport) {
        super(deviceSupport);
        fillResponseList();
    }

    public void initialize() {
        this.requestQueue.add(new GetStepGoalRequest());
        this.requestQueue.add(new GetVibrationStrengthRequest());
        this.requestQueue.add(new ActivityPointGetRequest());
        this.requestQueue.add(prepareSetTimeRequest());
        this.requestQueue.add(new AnimationRequest());
        this.requestQueue.add(new SetCurrentStepCountRequest((int) (getDeviceSupport().calculateNotificationProgress() * 999999.0d)));
        queueWrite(new GetCurrentStepCountRequest());
        getDeviceSupport().getDevice().setState(GBDevice.State.INITIALIZED);
        getDeviceSupport().getDevice().sendDeviceUpdateIntent(getContext());
    }

    private SetTimeRequest prepareSetTimeRequest() {
        long millis = System.currentTimeMillis();
        TimeZone zone = new GregorianCalendar().getTimeZone();
        return new SetTimeRequest((int) ((millis / 1000) + (getDeviceSupport().getTimeOffset() * 60)), (short) ((int) (millis % 1000)), (short) ((zone.getRawOffset() + zone.getDSTSavings()) / CoreConstants.MILLIS_IN_ONE_MINUTE));
    }

    public void playPairingAnimation() {
        queueWrite(new AnimationRequest());
    }

    public void playNotification(NotificationConfiguration config) {
        queueWrite(new PlayNotificationRequest(config.getVibration(), config.getHour(), config.getMin(), config.getSubEye()));
    }

    public void setTime() {
        queueWrite(prepareSetTimeRequest());
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onCharacteristicChanged(android.bluetooth.BluetoothGatt r11, android.bluetooth.BluetoothGattCharacteristic r12) {
        /*
            r10 = this;
            java.lang.String r0 = ": "
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r1 = r10.getDeviceSupport()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r1 = r1.getDevice()
            java.util.UUID r2 = r12.getUuid()
            java.lang.String r2 = r2.toString()
            int r3 = r2.hashCode()
            r4 = 0
            r5 = 5
            r6 = 4
            r7 = 3
            r8 = 2
            r9 = 1
            switch(r3) {
                case -1648524010: goto L_0x0052;
                case -955422313: goto L_0x0048;
                case -892660755: goto L_0x003e;
                case -262320616: goto L_0x0034;
                case 1123882778: goto L_0x002a;
                case 1816984475: goto L_0x0020;
                default: goto L_0x001f;
            }
        L_0x001f:
            goto L_0x005c
        L_0x0020:
            java.lang.String r3 = "3dda0007-957f-7d4a-34a6-74696673696d"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x001f
            r2 = 2
            goto L_0x005d
        L_0x002a:
            java.lang.String r3 = "3dda0006-957f-7d4a-34a6-74696673696d"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x001f
            r2 = 4
            goto L_0x005d
        L_0x0034:
            java.lang.String r3 = "3dda0004-957f-7d4a-34a6-74696673696d"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x001f
            r2 = 0
            goto L_0x005d
        L_0x003e:
            java.lang.String r3 = "00002a19-0000-1000-8000-00805f9b34fb"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x001f
            r2 = 5
            goto L_0x005d
        L_0x0048:
            java.lang.String r3 = "3dda0003-957f-7d4a-34a6-74696673696d"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x001f
            r2 = 1
            goto L_0x005d
        L_0x0052:
            java.lang.String r3 = "3dda0002-957f-7d4a-34a6-74696673696d"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x001f
            r2 = 3
            goto L_0x005d
        L_0x005c:
            r2 = -1
        L_0x005d:
            if (r2 == 0) goto L_0x0126
            if (r2 == r9) goto L_0x0126
            if (r2 == r8) goto L_0x0121
            if (r2 == r7) goto L_0x011c
            if (r2 == r6) goto L_0x0117
            if (r2 == r5) goto L_0x00ea
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "unknown shit on "
            r2.append(r3)
            java.util.UUID r3 = r12.getUuid()
            java.lang.String r3 = r3.toString()
            r2.append(r3)
            java.lang.String r3 = ":  "
            r2.append(r3)
            byte[] r3 = r12.getValue()
            java.lang.String r3 = r10.arrayToString(r3)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r10.log(r2)
            java.io.File r2 = new java.io.File     // Catch:{ IOException -> 0x00e3 }
            java.lang.String r3 = "/sdcard/qFiles/charLog.txt"
            r2.<init>(r3)     // Catch:{ IOException -> 0x00e3 }
            boolean r3 = r2.exists()     // Catch:{ IOException -> 0x00e3 }
            if (r3 != 0) goto L_0x00a5
            r2.createNewFile()     // Catch:{ IOException -> 0x00e3 }
        L_0x00a5:
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x00e3 }
            r3.<init>(r2, r9)     // Catch:{ IOException -> 0x00e3 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00e3 }
            r4.<init>()     // Catch:{ IOException -> 0x00e3 }
            java.util.Date r5 = new java.util.Date     // Catch:{ IOException -> 0x00e3 }
            r5.<init>()     // Catch:{ IOException -> 0x00e3 }
            java.lang.String r5 = r5.toString()     // Catch:{ IOException -> 0x00e3 }
            r4.append(r5)     // Catch:{ IOException -> 0x00e3 }
            r4.append(r0)     // Catch:{ IOException -> 0x00e3 }
            java.util.UUID r5 = r12.getUuid()     // Catch:{ IOException -> 0x00e3 }
            java.lang.String r5 = r5.toString()     // Catch:{ IOException -> 0x00e3 }
            r4.append(r5)     // Catch:{ IOException -> 0x00e3 }
            r4.append(r0)     // Catch:{ IOException -> 0x00e3 }
            byte[] r0 = r12.getValue()     // Catch:{ IOException -> 0x00e3 }
            java.lang.String r0 = r10.arrayToString(r0)     // Catch:{ IOException -> 0x00e3 }
            r4.append(r0)     // Catch:{ IOException -> 0x00e3 }
            java.lang.String r0 = r4.toString()     // Catch:{ IOException -> 0x00e3 }
            byte[] r0 = r0.getBytes()     // Catch:{ IOException -> 0x00e3 }
            r3.write(r0)     // Catch:{ IOException -> 0x00e3 }
            goto L_0x010e
        L_0x00e3:
            r0 = move-exception
            java.lang.String r2 = "error"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.log(r2, r7, r0)
            goto L_0x010e
        L_0x00ea:
            byte[] r0 = r12.getValue()
            byte r0 = r0[r4]
            short r0 = (short) r0
            r1.setBatteryLevel(r0)
            r1.setBatteryThresholdPercent(r8)
            nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo r2 = new nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo
            r2.<init>()
            short r3 = r1.getBatteryLevel()
            r2.level = r3
            nodomain.freeyourgadget.gadgetbridge.model.BatteryState r3 = nodomain.freeyourgadget.gadgetbridge.model.BatteryState.BATTERY_NORMAL
            r2.state = r3
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r3 = r10.getDeviceSupport()
            r3.handleGBDeviceEvent(r2)
        L_0x010e:
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r0 = r10.getDeviceSupport()
            boolean r0 = r0.onCharacteristicChanged(r11, r12)
            return r0
        L_0x0117:
            boolean r0 = r10.handleButtonCharacteristic(r12)
            return r0
        L_0x011c:
            boolean r0 = r10.handleBasicCharacteristic(r12)
            return r0
        L_0x0121:
            boolean r0 = r10.handleFileUploadCharacteristic(r12)
            return r0
        L_0x0126:
            boolean r0 = r10.handleFileDownloadCharacteristic(r12)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.misfit.MisfitWatchAdapter.onCharacteristicChanged(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic):boolean");
    }

    private void fillResponseList() {
        for (Class cls : new Class[]{BatteryLevelRequest.class, GetStepGoalRequest.class, GetVibrationStrengthRequest.class, GetCurrentStepCountRequest.class, OTAEnterRequest.class, GoalTrackingGetRequest.class, ActivityPointGetRequest.class, GetCountdownSettingsRequest.class}) {
            try {
                cls.getSuperclass().getDeclaredMethod("handleResponse", new Class[]{BluetoothGattCharacteristic.class});
                Request object = (Request) cls.newInstance();
                if (object.getStartSequence().length > 1) {
                    this.responseFilters.put(object.getStartSequence()[1], object);
                    log("response filter " + object.getStartSequence()[1] + ": " + cls.getSimpleName());
                }
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException e) {
                log("skipping class " + cls.getName());
            }
        }
    }

    private boolean handleBasicCharacteristic(BluetoothGattCharacteristic characteristic) {
        byte[] values = characteristic.getValue();
        Request request = resolveAnswer(characteristic);
        GBDevice gbDevice = getDeviceSupport().getDevice();
        if (request == null) {
            StringBuilder valueString = new StringBuilder(String.valueOf(values[0]));
            for (int i = 1; i < characteristic.getValue().length; i++) {
                valueString.append(", ");
                valueString.append(values[i]);
            }
            log("unable to resolve " + characteristic.getUuid().toString() + ": " + valueString);
            return true;
        }
        log("response: " + request.getClass().getSimpleName());
        request.handleResponse(characteristic);
        if (request instanceof GetStepGoalRequest) {
            gbDevice.addDeviceInfo(new GenericItem(QHybridSupport.ITEM_STEP_GOAL, String.valueOf(((GetStepGoalRequest) request).stepGoal)));
        } else if (request instanceof GetVibrationStrengthRequest) {
            gbDevice.addDeviceInfo(new GenericItem(QHybridSupport.ITEM_VIBRATION_STRENGTH, String.valueOf(((GetVibrationStrengthRequest) request).strength)));
        } else if (request instanceof GetCurrentStepCountRequest) {
            int steps = ((GetCurrentStepCountRequest) request).steps;
            Logger logger2 = this.logger;
            logger2.debug("get current steps: " + steps);
            try {
                File f = new File("/sdcard/qFiles/");
                if (!f.exists()) {
                    f.mkdir();
                }
                File file = new File("/sdcard/qFiles/steps");
                if (!file.exists()) {
                    file.createNewFile();
                }
                Logger logger3 = this.logger;
                logger3.debug("Writing file " + file.getPath());
                FileOutputStream fos = new FileOutputStream(file, true);
                fos.write((System.currentTimeMillis() + ": " + steps + StringUtils.f210LF).getBytes());
                fos.close();
                this.logger.debug("file written.");
            } catch (Exception e) {
                C1238GB.log("error", 3, e);
            }
            gbDevice.addDeviceInfo(new GenericItem(QHybridSupport.ITEM_STEP_COUNT, String.valueOf(((GetCurrentStepCountRequest) request).steps)));
        } else if (request instanceof OTAEnterRequest) {
            if (((OTAEnterRequest) request).success) {
                this.fileRequest = new OTAEraseRequest(67108864);
                queueWrite(this.fileRequest);
            }
        } else if (request instanceof ActivityPointGetRequest) {
            gbDevice.addDeviceInfo(new GenericItem(QHybridSupport.ITEM_ACTIVITY_POINT, String.valueOf(((ActivityPointGetRequest) request).activityPoint)));
        }
        try {
            queueWrite(this.requestQueue.remove());
        } catch (NoSuchElementException e2) {
        }
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(DeviceManager.ACTION_DEVICES_CHANGED));
        return true;
    }

    private Request resolveAnswer(BluetoothGattCharacteristic characteristic) {
        byte[] values = characteristic.getValue();
        if (values[0] != 3) {
            return null;
        }
        return this.responseFilters.get(values[1]);
    }

    private boolean handleFileDownloadCharacteristic(BluetoothGattCharacteristic characteristic) {
        Request request = this.fileRequest;
        request.handleResponse(characteristic);
        if (request instanceof ListFilesRequest) {
            if (((ListFilesRequest) request).completed) {
                Logger logger2 = this.logger;
                logger2.debug("File count: " + ((ListFilesRequest) request).fileCount + "  size: " + ((ListFilesRequest) request).size);
                if (((ListFilesRequest) request).fileCount == 0) {
                    return true;
                }
            }
        } else if (request instanceof DownloadFileRequest) {
            if (((FileRequest) request).completed) {
                Logger logger3 = this.logger;
                logger3.debug("file " + ((DownloadFileRequest) request).fileHandle + " completed: " + ((DownloadFileRequest) request).size);
            }
        } else if ((request instanceof EraseFileRequest) && ((EraseFileRequest) request).fileHandle > 257) {
            queueWrite(new DownloadFileRequest((short) (((EraseFileRequest) request).fileHandle - 1)));
        }
        return true;
    }

    private boolean handleFileUploadCharacteristic(BluetoothGattCharacteristic characteristic) {
        UploadFileRequest uploadFileRequest2 = this.uploadFileRequest;
        if (uploadFileRequest2 == null) {
            this.logger.debug("no uploadFileRequest to handle response");
            return true;
        }
        uploadFileRequest2.handleResponse(characteristic);
        int i = C12231.f198x4601f5d2[this.uploadFileRequest.state.ordinal()];
        if (i == 1) {
            Intent fileIntent = new Intent(QHybridSupport.QHYBRID_EVENT_FILE_UPLOADED);
            fileIntent.putExtra("EXTRA_ERROR", true);
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(fileIntent);
            this.uploadFileRequest = null;
        } else if (i == 2) {
            Iterator<byte[]> it = this.uploadFileRequest.packets.iterator();
            while (it.hasNext()) {
                new TransactionBuilder("File upload").write(characteristic, it.next()).queue(getDeviceSupport().getQueue());
            }
        } else if (i == 3) {
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(QHybridSupport.QHYBRID_EVENT_FILE_UPLOADED));
            this.uploadFileRequest = null;
        }
        return true;
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.misfit.MisfitWatchAdapter$1 */
    static /* synthetic */ class C12231 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$service$devices$qhybrid$requests$misfit$UploadFileRequest$UploadState */
        static final /* synthetic */ int[] f198x4601f5d2 = new int[UploadFileRequest.UploadState.values().length];

        static {
            try {
                f198x4601f5d2[UploadFileRequest.UploadState.ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f198x4601f5d2[UploadFileRequest.UploadState.UPLOAD.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f198x4601f5d2[UploadFileRequest.UploadState.UPLOADED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private boolean handleButtonCharacteristic(BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        if (value.length != 11) {
            this.logger.debug("wrong button message");
            return true;
        }
        int index = value[6] & 255;
        int button = (value[8] >> 4) & 255;
        if (index != this.lastButtonIndex) {
            this.lastButtonIndex = index;
            Logger logger2 = this.logger;
            logger2.debug("Button press on button " + button);
            Intent i = new Intent(QHybridSupport.QHYBRID_EVENT_BUTTON_PRESS);
            i.putExtra("BUTTON", button);
            getContext().sendBroadcast(i);
        }
        return true;
    }

    private void log(String message) {
        this.logger.debug(message);
    }

    public void setActivityHand(double progress) {
        queueWrite(new SetCurrentStepCountRequest(Math.min((int) (1000000.0d * progress), 999999)));
    }

    public void setHands(short hour, short minute) {
        queueWrite(new MoveHandsRequest(false, minute, hour, -1));
    }

    public void vibrate(PlayNotificationRequest.VibrationType vibration) {
        queueWrite(new PlayNotificationRequest(vibration, -1, -1));
    }

    public void vibrateFindMyDevicePattern() {
        queueWrite(new VibrateRequest(false, 4, 1));
    }

    public void requestHandsControl() {
        queueWrite(new RequestHandControlRequest());
    }

    public void releaseHandsControl() {
        queueWrite(new ReleaseHandsControlRequest());
    }

    public void setStepGoal(int stepGoal) {
        queueWrite(new SetStepGoalRequest(stepGoal));
    }

    public void setVibrationStrength(short strength) {
        queueWrite(new SetVibrationStrengthRequest(strength));
    }

    public void syncNotificationSettings() {
    }

    public void onTestNewFunction() {
    }

    public void setTimezoneOffsetMinutes(short offset) {
        C1238GB.toast("old firmware does't support timezones", 1, 3);
    }

    public boolean supportsFindDevice() {
        return supportsExtendedVibration();
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
            java.lang.String r3 = "Model "
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
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.misfit.MisfitWatchAdapter.supportsExtendedVibration():boolean");
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
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.misfit.MisfitWatchAdapter.supportsActivityHand():boolean");
    }

    public void onFetchActivityData() {
        this.requestQueue.add(new BatteryLevelRequest());
        this.requestQueue.add(new GetCurrentStepCountRequest());
        queueWrite(new ActivityPointGetRequest());
    }

    public void onSetAlarms(ArrayList<? extends Alarm> arrayList) {
        C1238GB.toast("alarms not supported with this firmware", 1, 3);
    }

    public void overwriteButtons(String jsonConfigString) {
        this.uploadFileRequest = new UploadFileRequest(2048, new byte[]{1, 0, 0, 3, 16, 1, 1, 1, 12, 0, 0, 32, 1, 1, 1, 12, 0, 0, ZeTimeConstants.CMD_USER_INFO, 1, 1, 1, 12, 0, 0, 1, 1, 0, 1, 1, 12, HPlusConstants.DATA_VERSION1, 0, 0, 0, 1, 0, 6, 0, 1, 1, 1, 3, 0, 2, 1, 15, 0, -117, 0, 0, MakibesHR3Constants.CMD_SET_DATE_TIME, 0, 1, 8, 1, 20, 0, 1, 0, -2, 8, 0, MakibesHR3Constants.CMD_SET_DATE_TIME, 0, 2, 1, 0, -65, PebbleColor.DarkGray, ZeTimeConstants.CMD_GET_STEP_COUNT, PebbleColor.ImperialPurple, 0});
        queueWrite(this.uploadFileRequest);
    }

    private void queueWrite(Request request) {
        new TransactionBuilder(request.getClass().getSimpleName()).write(getDeviceSupport().getCharacteristic(request.getRequestUUID()), request.getRequestData()).queue(getDeviceSupport().getQueue());
        if (!request.expectsResponse()) {
            try {
                queueWrite(this.requestQueue.remove());
            } catch (NoSuchElementException e) {
            }
        }
    }
}
