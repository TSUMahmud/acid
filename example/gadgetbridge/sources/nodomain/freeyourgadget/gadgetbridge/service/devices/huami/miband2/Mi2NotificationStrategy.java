package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband2;

import android.bluetooth.BluetoothGattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.VibrationProfile;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.common.SimpleNotification;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.V2NotificationStrategy;

public class Mi2NotificationStrategy extends V2NotificationStrategy<HuamiSupport> {
    private final BluetoothGattCharacteristic alertLevelCharacteristic;

    public Mi2NotificationStrategy(HuamiSupport support) {
        super(support);
        this.alertLevelCharacteristic = support.getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_ALERT_LEVEL);
    }

    /* access modifiers changed from: protected */
    public void sendCustomNotification(VibrationProfile vibrationProfile, SimpleNotification simpleNotification, BtLEAction extraAction, TransactionBuilder builder) {
        startNotify(builder, vibrationProfile.getAlertLevel(), simpleNotification);
        BluetoothGattCharacteristic alert = ((HuamiSupport) getSupport()).getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_ALERT_LEVEL);
        byte repeat = (byte) (vibrationProfile.getRepeat() * (vibrationProfile.getOnOffSequence().length / 2));
        int waitDuration = 0;
        if (repeat > 0) {
            short vibration = (short) vibrationProfile.getOnOffSequence()[0];
            short pause = (short) vibrationProfile.getOnOffSequence()[1];
            waitDuration = (vibration + pause) * repeat;
            builder.write(alert, new byte[]{-1, (byte) (vibration & 255), (byte) ((vibration >> 8) & 255), (byte) (pause & 255), (byte) ((pause >> 8) & 255), repeat});
        }
        builder.wait(Math.max(waitDuration, 4000));
        if (extraAction != null) {
            builder.add(extraAction);
        }
    }

    /* access modifiers changed from: protected */
    public void startNotify(TransactionBuilder builder, int alertLevel, SimpleNotification simpleNotification) {
        builder.write(this.alertLevelCharacteristic, new byte[]{(byte) alertLevel});
    }

    /* access modifiers changed from: protected */
    public void stopNotify(TransactionBuilder builder) {
        builder.write(this.alertLevelCharacteristic, new byte[]{0});
    }

    public void sendCustomNotification(VibrationProfile vibrationProfile, SimpleNotification simpleNotification, int flashTimes, int flashColour, int originalColour, long flashDuration, BtLEAction extraAction, TransactionBuilder builder) {
        sendCustomNotification(vibrationProfile, simpleNotification, extraAction, builder);
    }
}
