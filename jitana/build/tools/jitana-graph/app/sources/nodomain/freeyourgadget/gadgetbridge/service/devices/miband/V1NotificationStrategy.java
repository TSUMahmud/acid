package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import android.bluetooth.BluetoothGattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandService;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.VibrationProfile;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.common.SimpleNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V1NotificationStrategy implements NotificationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) V1NotificationStrategy.class);
    static final byte[] startVibrate = {8, 1};
    static final byte[] stopVibrate = {19};
    private final AbstractBTLEDeviceSupport support;

    public V1NotificationStrategy(AbstractBTLEDeviceSupport support2) {
        this.support = support2;
    }

    public void sendDefaultNotification(TransactionBuilder builder, SimpleNotification simpleNotification, BtLEAction extraAction) {
        builder.write(this.support.getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT), getDefaultNotification());
        builder.add(extraAction);
    }

    private byte[] getDefaultNotification() {
        return getNotification(250, 1, 1, -1, -1, 250);
    }

    private byte[] getNotification(long vibrateDuration, int vibrateTimes, int flashTimes, int flashColour, int originalColour, long flashDuration) {
        return startVibrate;
    }

    public void sendCustomNotification(VibrationProfile vibrationProfile, SimpleNotification simpleNotification, int flashTimes, int flashColour, int originalColour, long flashDuration, BtLEAction extraAction, TransactionBuilder builder) {
        BtLEAction btLEAction = extraAction;
        TransactionBuilder transactionBuilder = builder;
        BluetoothGattCharacteristic controlPoint = this.support.getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT);
        for (short i = 0; i < vibrationProfile.getRepeat(); i = (short) (i + 1)) {
            int[] onOffSequence = vibrationProfile.getOnOffSequence();
            int j = 0;
            while (j < onOffSequence.length) {
                int on = Math.min(500, onOffSequence[j]);
                transactionBuilder.write(controlPoint, startVibrate);
                transactionBuilder.wait(on);
                transactionBuilder.write(controlPoint, stopVibrate);
                int j2 = j + 1;
                if (j2 < onOffSequence.length) {
                    transactionBuilder.wait(Math.max(onOffSequence[j2], 25));
                }
                if (btLEAction != null) {
                    transactionBuilder.add(btLEAction);
                }
                j = j2 + 1;
            }
        }
    }

    public void stopCurrentNotification(TransactionBuilder builder) {
        builder.write(this.support.getCharacteristic(MiBandService.UUID_CHARACTERISTIC_CONTROL_POINT), stopVibrate);
    }
}
