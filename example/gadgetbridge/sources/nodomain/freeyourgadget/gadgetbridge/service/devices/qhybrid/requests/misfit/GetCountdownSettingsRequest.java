package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit;

import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.Request;

public class GetCountdownSettingsRequest extends Request {
    public byte[] getStartSequence() {
        return new byte[]{1, 19, 1};
    }

    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        if (value.length == 14) {
            ByteBuffer buffer = ByteBuffer.wrap(value);
            long j = m39j(buffer.getInt(3));
            long j2 = m39j(buffer.getInt(7));
            byte progress = buffer.get(13);
            short s = buffer.getShort(11);
            log("progress: " + progress);
        }
    }

    /* renamed from: j */
    public static long m39j(int n) {
        if (n < 0) {
            return ((long) n) + 4294967296L;
        }
        return (long) n;
    }
}
