package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import android.bluetooth.BluetoothGattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.VibrationProfile;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.common.SimpleNotification;

public class V2NotificationStrategy<T extends AbstractBTLEDeviceSupport> implements NotificationStrategy {
    private final T support;

    public V2NotificationStrategy(T support2) {
        this.support = support2;
    }

    /* access modifiers changed from: protected */
    public T getSupport() {
        return this.support;
    }

    public void sendDefaultNotification(TransactionBuilder builder, SimpleNotification simpleNotification, BtLEAction extraAction) {
        sendCustomNotification(VibrationProfile.getProfile(VibrationProfile.ID_MEDIUM, 3), simpleNotification, extraAction, builder);
    }

    /* access modifiers changed from: protected */
    public void sendCustomNotification(VibrationProfile vibrationProfile, SimpleNotification simpleNotification, BtLEAction extraAction, TransactionBuilder builder) {
        BluetoothGattCharacteristic alert = this.support.getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_ALERT_LEVEL);
        for (short i = 0; i < vibrationProfile.getRepeat(); i = (short) (i + 1)) {
            int[] onOffSequence = vibrationProfile.getOnOffSequence();
            int j = 0;
            while (j < onOffSequence.length) {
                int on = Math.min(500, onOffSequence[j]);
                builder.write(alert, new byte[]{1});
                builder.wait(on);
                builder.write(alert, new byte[]{0});
                int j2 = j + 1;
                if (j2 < onOffSequence.length) {
                    builder.wait(Math.max(onOffSequence[j2], 25));
                }
                if (extraAction != null) {
                    builder.add(extraAction);
                }
                j = j2 + 1;
            }
        }
    }

    public void sendCustomNotification(VibrationProfile vibrationProfile, SimpleNotification simpleNotification, int flashTimes, int flashColour, int originalColour, long flashDuration, BtLEAction extraAction, TransactionBuilder builder) {
        sendCustomNotification(vibrationProfile, simpleNotification, extraAction, builder);
    }

    public void stopCurrentNotification(TransactionBuilder builder) {
        builder.write(this.support.getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_ALERT_LEVEL), new byte[]{0});
    }
}
