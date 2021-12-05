package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MiBandDateConverter {
    public static GregorianCalendar createCalendar() {
        return new GregorianCalendar();
    }

    public static GregorianCalendar rawBytesToCalendar(byte[] value, String deviceAddress) {
        if (value.length == 6) {
            return rawBytesToCalendar(value, 0, deviceAddress);
        }
        return createCalendar();
    }

    public static GregorianCalendar rawBytesToCalendar(byte[] value, int offset, String deviceAddress) {
        if (value.length - offset < 6) {
            return createCalendar();
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar(value[offset] + 2000, value[offset + 1], value[offset + 2], value[offset + 3], value[offset + 4], value[offset + 5]);
        int offsetInHours = MiBandCoordinator.getDeviceTimeOffsetHours(deviceAddress);
        if (offsetInHours != 0) {
            gregorianCalendar.add(11, -offsetInHours);
        }
        return gregorianCalendar;
    }

    public static byte[] calendarToRawBytes(Calendar timestamp, String deviceAddress) {
        int offsetInHours = MiBandCoordinator.getDeviceTimeOffsetHours(deviceAddress);
        if (offsetInHours != 0) {
            timestamp.add(11, offsetInHours);
        }
        return new byte[]{(byte) (timestamp.get(1) - 2000), (byte) timestamp.get(2), (byte) timestamp.get(5), (byte) timestamp.get(11), (byte) timestamp.get(12), (byte) timestamp.get(13)};
    }
}
