package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification;

public class NewAlert {
    private final AlertCategory category;
    private byte customIcon = -1;
    private final String message;
    private final int numAlerts;

    public NewAlert(AlertCategory category2, int numAlerts2, String message2) {
        this.category = category2;
        this.numAlerts = numAlerts2;
        this.message = message2;
    }

    public NewAlert(AlertCategory category2, int numAlerts2, String message2, byte customIcon2) {
        this.category = category2;
        this.numAlerts = numAlerts2;
        this.message = message2;
        this.customIcon = customIcon2;
    }

    public AlertCategory getCategory() {
        return this.category;
    }

    public int getNumAlerts() {
        return this.numAlerts;
    }

    public String getMessage() {
        return this.message;
    }

    public byte getCustomIcon() {
        return this.customIcon;
    }
}
