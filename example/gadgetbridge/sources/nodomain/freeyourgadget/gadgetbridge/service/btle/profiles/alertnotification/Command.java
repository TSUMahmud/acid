package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification;

public enum Command {
    EnableNewIncomingAlertNotification(0),
    EnableUnreadCategoryStatusNotification(1),
    DisableNewIncomingAlertNotification(2),
    DisbleUnreadCategoryStatusNotification(3),
    NotifyNewIncomingAlertImmediately(4),
    NotifyUnreadCategoryStatusImmediately(5);
    

    /* renamed from: id */
    private final int f171id;

    private Command(int id) {
        this.f171id = id;
    }

    public int getId() {
        return this.f171id;
    }
}
