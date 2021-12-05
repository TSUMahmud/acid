package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import nodomain.freeyourgadget.gadgetbridge.devices.miband.VibrationProfile;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.common.SimpleNotification;

public interface NotificationStrategy {
    void sendCustomNotification(VibrationProfile vibrationProfile, SimpleNotification simpleNotification, int i, int i2, int i3, long j, BtLEAction btLEAction, TransactionBuilder transactionBuilder);

    void sendDefaultNotification(TransactionBuilder transactionBuilder, SimpleNotification simpleNotification, BtLEAction btLEAction);

    void stopCurrentNotification(TransactionBuilder transactionBuilder);
}
