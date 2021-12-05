package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import java.util.Arrays;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.DeviceInfo;
import nodomain.freeyourgadget.gadgetbridge.util.CheckSums;

public class UserInfo {
    private final int age;
    private final String alias;
    private final String btAddress;
    private byte[] data = new byte[20];
    private final int gender;
    private final int height;
    private final int type;
    private final int weight;

    public static UserInfo getDefault(String btAddress2) {
        return new UserInfo(btAddress2, "1550050550", 0, 0, ActivityUser.defaultUserHeightCm, 70, 0);
    }

    public static UserInfo create(String address, String alias2, int gender2, int age2, int height2, int weight2, int type2) throws IllegalArgumentException {
        if (address == null || address.length() == 0 || alias2 == null || alias2.length() == 0 || gender2 < 0 || age2 <= 0 || weight2 <= 0 || type2 < 0) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        try {
            return new UserInfo(address, alias2, gender2, age2, height2, weight2, type2);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Illegal user info data", ex);
        }
    }

    private UserInfo(String address, String alias2, int gender2, int age2, int height2, int weight2, int type2) {
        this.btAddress = address;
        this.alias = alias2;
        this.gender = gender2;
        this.age = age2;
        this.height = height2;
        this.weight = weight2;
        this.type = type2;
    }

    private int calculateUidFrom(String alias2) {
        try {
            return Integer.parseInt(alias2);
        } catch (NumberFormatException e) {
            return alias2.hashCode();
        }
    }

    public byte[] getData(DeviceInfo mDeviceInfo) {
        byte[] sequence = new byte[20];
        int uid = calculateUidFrom(this.alias);
        sequence[0] = (byte) uid;
        sequence[1] = (byte) (uid >>> 8);
        sequence[2] = (byte) (uid >>> 16);
        sequence[3] = (byte) (uid >>> 24);
        sequence[4] = (byte) (this.gender & 255);
        sequence[5] = (byte) (this.age & 255);
        sequence[6] = (byte) (this.height & 255);
        sequence[7] = (byte) (this.weight & 255);
        sequence[8] = (byte) (this.type & 255);
        int aliasFrom = 9;
        if (!mDeviceInfo.isMili1()) {
            sequence[9] = (byte) (mDeviceInfo.feature & 255);
            sequence[10] = (byte) (mDeviceInfo.appearance & 255);
            aliasFrom = 11;
        }
        String str = this.alias;
        byte[] aliasBytes = str.substring(0, Math.min(str.length(), 19 - aliasFrom)).getBytes();
        System.arraycopy(aliasBytes, 0, sequence, aliasFrom, aliasBytes.length);
        int crc8 = CheckSums.getCRC8(Arrays.copyOf(sequence, 19));
        String str2 = this.btAddress;
        sequence[19] = (byte) ((Integer.parseInt(str2.substring(str2.length() - 2), 16) ^ crc8) & 255);
        return sequence;
    }
}
