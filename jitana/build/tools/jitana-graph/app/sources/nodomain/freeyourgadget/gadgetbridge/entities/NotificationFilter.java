package nodomain.freeyourgadget.gadgetbridge.entities;

public class NotificationFilter {
    private String appIdentifier;

    /* renamed from: id */
    private Long f145id;
    private int notificationFilterMode;
    private int notificationFilterSubMode;

    public NotificationFilter() {
    }

    public NotificationFilter(Long id) {
        this.f145id = id;
    }

    public NotificationFilter(String appIdentifier2, Long id, int notificationFilterMode2, int notificationFilterSubMode2) {
        this.appIdentifier = appIdentifier2;
        this.f145id = id;
        this.notificationFilterMode = notificationFilterMode2;
        this.notificationFilterSubMode = notificationFilterSubMode2;
    }

    public String getAppIdentifier() {
        return this.appIdentifier;
    }

    public void setAppIdentifier(String appIdentifier2) {
        this.appIdentifier = appIdentifier2;
    }

    public Long getId() {
        return this.f145id;
    }

    public void setId(Long id) {
        this.f145id = id;
    }

    public int getNotificationFilterMode() {
        return this.notificationFilterMode;
    }

    public void setNotificationFilterMode(int notificationFilterMode2) {
        this.notificationFilterMode = notificationFilterMode2;
    }

    public int getNotificationFilterSubMode() {
        return this.notificationFilterSubMode;
    }

    public void setNotificationFilterSubMode(int notificationFilterSubMode2) {
        this.notificationFilterSubMode = notificationFilterSubMode2;
    }
}
