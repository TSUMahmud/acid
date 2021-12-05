package nodomain.freeyourgadget.gadgetbridge.service.devices.miband;

import nodomain.freeyourgadget.gadgetbridge.devices.miband.VibrationProfile;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.common.SimpleNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoNotificationStrategy implements NotificationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) NoNotificationStrategy.class);

    public void sendDefaultNotification(TransactionBuilder builder, SimpleNotification simpleNotification, BtLEAction extraAction) {
        LOG.info("dummy notification stragegy: default notification");
    }

    public void sendCustomNotification(VibrationProfile vibrationProfile, SimpleNotification simpleNotification, int flashTimes, int flashColour, int originalColour, long flashDuration, BtLEAction extraAction, TransactionBuilder builder) {
        Logger logger = LOG;
        logger.info("dummy notification stragegy: custom notification: " + simpleNotification);
    }

    public void stopCurrentNotification(TransactionBuilder builder) {
        LOG.info("dummy notification stragegy: stop notification");
    }
}
