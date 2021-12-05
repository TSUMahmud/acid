package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification;

public class SupportedNewAlertCategory {

    /* renamed from: id */
    private final int f172id;

    public SupportedNewAlertCategory(int id) {
        this.f172id = id;
    }

    public int getId() {
        return this.f172id;
    }

    private int realBitNumber() {
        return this.f172id;
    }

    private int bitNumberPerByte() {
        return realBitNumber() % 8;
    }

    private int asBit() {
        return 1 << bitNumberPerByte();
    }

    private int byteNumber() {
        return this.f172id <= 7 ? 0 : 1;
    }

    public static byte[] toBitmask(SupportedNewAlertCategory... categories) {
        byte[] result = new byte[2];
        for (SupportedNewAlertCategory category : categories) {
            int byteNumber = category.byteNumber();
            result[byteNumber] = (byte) (result[byteNumber] | category.asBit());
        }
        return result;
    }
}
