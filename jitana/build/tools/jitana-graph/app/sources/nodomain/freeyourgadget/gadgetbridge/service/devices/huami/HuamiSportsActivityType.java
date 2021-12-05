package nodomain.freeyourgadget.gadgetbridge.service.devices.huami;

public enum HuamiSportsActivityType {
    Outdoor(1),
    Treadmill(2),
    Walking(3),
    Cycling(4),
    Exercise(5),
    Swimming(6);
    
    private final int code;

    private HuamiSportsActivityType(int code2) {
        this.code = code2;
    }

    public int toActivityKind() {
        switch (this) {
            case Outdoor:
                return 16;
            case Treadmill:
                return 256;
            case Cycling:
                return 128;
            case Walking:
                return 32;
            case Exercise:
                return 512;
            case Swimming:
                return 64;
            default:
                throw new RuntimeException("Not mapped activity kind for: " + this);
        }
    }

    public static HuamiSportsActivityType fromCode(int huamiCode) {
        for (HuamiSportsActivityType type : values()) {
            if (type.code == huamiCode) {
                return type;
            }
        }
        throw new RuntimeException("No matching HuamiSportsActivityType for code: " + huamiCode);
    }

    public static HuamiSportsActivityType fromActivityKind(int activityKind) {
        if (activityKind == 16) {
            return Outdoor;
        }
        if (activityKind == 32) {
            return Walking;
        }
        if (activityKind == 64) {
            return Swimming;
        }
        if (activityKind == 128) {
            return Cycling;
        }
        if (activityKind == 256) {
            return Treadmill;
        }
        if (activityKind == 512) {
            return Exercise;
        }
        throw new RuntimeException("No matching activity activityKind: " + activityKind);
    }
}
