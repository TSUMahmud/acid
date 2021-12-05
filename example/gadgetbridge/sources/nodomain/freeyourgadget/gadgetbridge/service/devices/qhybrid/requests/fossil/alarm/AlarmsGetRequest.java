package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.alarm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file.FileLookupAndGetRequest;

public class AlarmsGetRequest extends FileLookupAndGetRequest {
    public AlarmsGetRequest(FossilWatchAdapter adapter) {
        super((byte) 10, adapter);
    }

    public void handleFileData(byte[] fileData) {
        ByteBuffer buffer = ByteBuffer.wrap(fileData);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        if (buffer.getShort(0) == 2560) {
            int length = buffer.getInt(8) / 3;
            Alarm[] alarms = new Alarm[length];
            for (int i = 0; i < length; i++) {
                buffer.position((i * 3) + 12);
                alarms[i] = Alarm.fromBytes(new byte[]{buffer.get(), buffer.get(), buffer.get()});
            }
            handleAlarms(alarms);
            return;
        }
        throw new RuntimeException("wrong alarm handle");
    }

    public void handleAlarms(Alarm[] alarms) {
        Alarm[] alarms2 = new Alarm[alarms.length];
        for (int i = 0; i < alarms.length; i++) {
            alarms2[i] = Alarm.fromBytes(alarms[i].getData());
        }
    }
}
