package nodomain.freeyourgadget.gadgetbridge.service.devices.common;

import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.AlertCategory;

public class SimpleNotification {
    private final AlertCategory alertCategory;
    private final String message;
    private final NotificationType notificationType;

    public SimpleNotification(String message2, AlertCategory alertCategory2, NotificationType notificationType2) {
        this.message = message2;
        this.alertCategory = alertCategory2;
        this.notificationType = notificationType2;
    }

    public AlertCategory getAlertCategory() {
        return this.alertCategory;
    }

    public NotificationType getNotificationType() {
        return this.notificationType;
    }

    public String getMessage() {
        return this.message;
    }
}
