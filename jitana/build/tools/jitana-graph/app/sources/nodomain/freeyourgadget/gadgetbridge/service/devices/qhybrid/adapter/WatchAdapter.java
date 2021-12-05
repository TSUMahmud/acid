package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import java.util.ArrayList;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.NotificationConfiguration;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.PlayNotificationRequest;
import org.apache.commons.lang3.StringUtils;

public abstract class WatchAdapter {
    private QHybridSupport deviceSupport;

    public abstract void initialize();

    public abstract boolean onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic);

    public abstract void onFetchActivityData();

    public abstract void onSetAlarms(ArrayList<? extends Alarm> arrayList);

    public abstract void onTestNewFunction();

    public abstract void overwriteButtons(String str);

    public abstract void playNotification(NotificationConfiguration notificationConfiguration);

    public abstract void playPairingAnimation();

    public abstract void releaseHandsControl();

    public abstract void requestHandsControl();

    public abstract void setActivityHand(double d);

    public abstract void setHands(short s, short s2);

    public abstract void setStepGoal(int i);

    public abstract void setTime();

    public abstract void setTimezoneOffsetMinutes(short s);

    public abstract void setVibrationStrength(short s);

    public abstract boolean supportsActivityHand();

    public abstract boolean supportsExtendedVibration();

    public abstract boolean supportsFindDevice();

    public abstract void syncNotificationSettings();

    public abstract void vibrate(PlayNotificationRequest.VibrationType vibrationType);

    public abstract void vibrateFindMyDevicePattern();

    public WatchAdapter(QHybridSupport deviceSupport2) {
        this.deviceSupport = deviceSupport2;
    }

    public QHybridSupport getDeviceSupport() {
        return this.deviceSupport;
    }

    public Context getContext() {
        return getDeviceSupport().getContext();
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0043  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0050 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getModelName() {
        /*
            r5 = this;
            nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport r0 = r5.getDeviceSupport()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r0.getDevice()
            java.lang.String r0 = r0.getModel()
            int r1 = r0.hashCode()
            r2 = 2020235855(0x786a5a4f, float:1.9012955E34)
            r3 = 2
            r4 = 1
            if (r1 == r2) goto L_0x0036
            r2 = 2132904456(0x7f218a08, float:2.147224E38)
            if (r1 == r2) goto L_0x002c
            r2 = 2143063187(0x7fbc8c93, float:NaN)
            if (r1 == r2) goto L_0x0022
        L_0x0021:
            goto L_0x0040
        L_0x0022:
            java.lang.String r1 = "HW.0.0"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 0
            goto L_0x0041
        L_0x002c:
            java.lang.String r1 = "HL.0.0"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 1
            goto L_0x0041
        L_0x0036:
            java.lang.String r1 = "DN.1.0"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 2
            goto L_0x0041
        L_0x0040:
            r1 = -1
        L_0x0041:
            if (r1 == 0) goto L_0x0050
            if (r1 == r4) goto L_0x004d
            if (r1 == r3) goto L_0x004a
            java.lang.String r1 = "unknwon Q"
            return r1
        L_0x004a:
            java.lang.String r1 = "Hybrid HR Collider"
            return r1
        L_0x004d:
            java.lang.String r1 = "Q Activist"
            return r1
        L_0x0050:
            java.lang.String r1 = "Q Commuter"
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.WatchAdapter.getModelName():java.lang.String");
    }

    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
    }

    public String arrayToString(byte[] bytes) {
        if (bytes.length == 0) {
            return "";
        }
        StringBuilder s = new StringBuilder();
        for (byte b : bytes) {
            s.append("0123456789ABCDEF".charAt((b >> 4) & 15));
            s.append("0123456789ABCDEF".charAt(b & 15));
            s.append(StringUtils.SPACE);
        }
        return s.substring(0, s.length() - 1) + StringUtils.f210LF;
    }
}
