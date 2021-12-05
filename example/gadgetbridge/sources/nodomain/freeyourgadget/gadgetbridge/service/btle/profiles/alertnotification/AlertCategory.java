package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification;

import java.util.ArrayList;

public enum AlertCategory {
    Simple(0),
    Email(1),
    News(2),
    IncomingCall(3),
    MissedCall(4),
    SMS(5),
    VoiceMail(6),
    Schedule(7),
    HighPriorityAlert(8),
    InstantMessage(9),
    Any(255),
    Custom(-1),
    CustomHuami(-6);
    

    /* renamed from: id */
    private final int f169id;

    private AlertCategory(int id) {
        this.f169id = id;
    }

    public int getId() {
        return this.f169id;
    }

    private int realBitNumber() {
        return this.f169id;
    }

    private int bitNumberPerByte() {
        return realBitNumber() % 8;
    }

    private int asBit() {
        return 1 << bitNumberPerByte();
    }

    private int byteNumber() {
        return this.f169id <= 7 ? 0 : 1;
    }

    public static byte[] toBitmask(AlertCategory... categories) {
        byte[] result = new byte[2];
        for (AlertCategory category : categories) {
            int byteNumber = category.byteNumber();
            result[byteNumber] = (byte) (result[byteNumber] | category.asBit());
        }
        return result;
    }

    public static AlertCategory[] fromBitMask(byte[] bytes) {
        new ArrayList();
        byte b = bytes[0];
        return null;
    }
}
