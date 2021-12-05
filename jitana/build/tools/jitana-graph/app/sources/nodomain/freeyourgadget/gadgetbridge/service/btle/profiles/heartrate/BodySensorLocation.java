package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.heartrate;

public enum BodySensorLocation {
    Other(0),
    Chest(1),
    Wrist(2),
    Finger(3),
    Hand(4),
    EarLobe(5),
    Foot(6);
    
    private final int val;

    private BodySensorLocation(int val2) {
        this.val = val2;
    }
}
