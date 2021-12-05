package nodomain.freeyourgadget.gadgetbridge.service.devices.roidmi;

import android.net.Uri;
import android.os.Handler;
import java.util.ArrayList;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.devices.roidmi.RoidmiConst;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.service.serial.AbstractSerialDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceIoThread;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoidmiSupport extends AbstractSerialDeviceSupport {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) RoidmiSupport.class);
    private final Handler handler = new Handler();
    /* access modifiers changed from: private */
    public int infoRequestTries = 0;
    private final Runnable infosRunnable = new Runnable() {
        public void run() {
            RoidmiSupport roidmiSupport = RoidmiSupport.this;
            int unused = roidmiSupport.infoRequestTries = roidmiSupport.infoRequestTries + 1;
            boolean infoMissing = false;
            try {
                if (RoidmiSupport.this.getDevice().getExtraInfo(DeviceService.EXTRA_LED_COLOR) == null) {
                    infoMissing = true;
                    RoidmiSupport.this.onSendConfiguration(RoidmiConst.ACTION_GET_LED_COLOR);
                }
                if (RoidmiSupport.this.getDevice().getExtraInfo(DeviceService.EXTRA_FM_FREQUENCY) == null) {
                    infoMissing = true;
                    RoidmiSupport.this.onSendConfiguration(RoidmiConst.ACTION_GET_FM_FREQUENCY);
                }
                if (RoidmiSupport.this.getDevice().getType() == DeviceType.ROIDMI3 && RoidmiSupport.this.getDevice().getBatteryVoltage() == -1.0f) {
                    infoMissing = true;
                    RoidmiSupport.this.onSendConfiguration(RoidmiConst.ACTION_GET_VOLTAGE);
                }
                if (!infoMissing) {
                    return;
                }
                if (RoidmiSupport.this.infoRequestTries < 6) {
                    RoidmiSupport.this.requestDeviceInfos((RoidmiSupport.this.infoRequestTries * 120) + 500);
                } else {
                    RoidmiSupport.LOG.error("Failed to get Roidmi infos after 6 tries");
                }
            } catch (Exception e) {
                RoidmiSupport.LOG.error("Failed to get Roidmi infos", (Throwable) e);
            }
        }
    };

    /* access modifiers changed from: private */
    public void requestDeviceInfos(int delayMillis) {
        this.handler.postDelayed(this.infosRunnable, (long) delayMillis);
    }

    public boolean connect() {
        getDeviceIOThread().start();
        requestDeviceInfos(1500);
        return true;
    }

    /* access modifiers changed from: protected */
    public GBDeviceProtocol createDeviceProtocol() {
        if (getDevice().getType() == DeviceType.ROIDMI) {
            return new Roidmi1Protocol(getDevice());
        }
        if (getDevice().getType() == DeviceType.ROIDMI3) {
            return new Roidmi3Protocol(getDevice());
        }
        Logger logger = LOG;
        logger.error("Unsupported device type with key = " + getDevice().getType().getKey());
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0057  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0082  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSendConfiguration(java.lang.String r7) {
        /*
            r6 = this;
            org.slf4j.Logger r0 = LOG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "onSendConfiguration "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            r0.debug(r1)
            nodomain.freeyourgadget.gadgetbridge.service.devices.roidmi.RoidmiIoThread r0 = r6.getDeviceIOThread()
            nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceProtocol r1 = r6.getDeviceProtocol()
            nodomain.freeyourgadget.gadgetbridge.service.devices.roidmi.RoidmiProtocol r1 = (nodomain.freeyourgadget.gadgetbridge.service.devices.roidmi.RoidmiProtocol) r1
            int r2 = r7.hashCode()
            r3 = 629224298(0x2581336a, float:2.2412772E-16)
            r4 = 2
            r5 = 1
            if (r2 == r3) goto L_0x004a
            r3 = 631391547(0x25a2453b, float:2.8149433E-16)
            if (r2 == r3) goto L_0x0040
            r3 = 1192069736(0x470d8a68, float:36234.406)
            if (r2 == r3) goto L_0x0036
        L_0x0035:
            goto L_0x0054
        L_0x0036:
            java.lang.String r2 = "roidmi_get_frequency"
            boolean r2 = r7.equals(r2)
            if (r2 == 0) goto L_0x0035
            r2 = 1
            goto L_0x0055
        L_0x0040:
            java.lang.String r2 = "roidmi_get_led_color"
            boolean r2 = r7.equals(r2)
            if (r2 == 0) goto L_0x0035
            r2 = 0
            goto L_0x0055
        L_0x004a:
            java.lang.String r2 = "roidmi_get_voltage"
            boolean r2 = r7.equals(r2)
            if (r2 == 0) goto L_0x0035
            r2 = 2
            goto L_0x0055
        L_0x0054:
            r2 = -1
        L_0x0055:
            if (r2 == 0) goto L_0x0082
            if (r2 == r5) goto L_0x007a
            if (r2 == r4) goto L_0x0072
            org.slf4j.Logger r2 = LOG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Invalid Roidmi configuration "
            r3.append(r4)
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            r2.error(r3)
            goto L_0x008a
        L_0x0072:
            byte[] r2 = r1.encodeGetVoltage()
            r0.write(r2)
            goto L_0x008a
        L_0x007a:
            byte[] r2 = r1.encodeGetFmFrequency()
            r0.write(r2)
            goto L_0x008a
        L_0x0082:
            byte[] r2 = r1.encodeGetLedColor()
            r0.write(r2)
        L_0x008a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.roidmi.RoidmiSupport.onSendConfiguration(java.lang.String):void");
    }

    public void onReadConfiguration(String config) {
    }

    /* access modifiers changed from: protected */
    public GBDeviceIoThread createDeviceIOThread() {
        return new RoidmiIoThread(getDevice(), getContext(), (RoidmiProtocol) getDeviceProtocol(), this, getBluetoothAdapter());
    }

    public synchronized RoidmiIoThread getDeviceIOThread() {
        return (RoidmiIoThread) super.getDeviceIOThread();
    }

    public boolean useAutoConnect() {
        return false;
    }

    public void onInstallApp(Uri uri) {
    }

    public void onAppConfiguration(UUID uuid, String config, Integer id) {
    }

    public void onHeartRateTest() {
    }

    public void onSetConstantVibration(int intensity) {
    }

    public void onSetHeartRateMeasurementInterval(int seconds) {
    }

    public void onSetAlarms(ArrayList<? extends Alarm> arrayList) {
    }
}
