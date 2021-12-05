package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification;

public enum AlertLevel {
    NoAlert(0),
    MildAlert(1),
    HighAlert(2);
    

    /* renamed from: id */
    private final int f170id;

    private AlertLevel(int id) {
        this.f170id = id;
    }

    public int getId() {
        return this.f170id;
    }
}
