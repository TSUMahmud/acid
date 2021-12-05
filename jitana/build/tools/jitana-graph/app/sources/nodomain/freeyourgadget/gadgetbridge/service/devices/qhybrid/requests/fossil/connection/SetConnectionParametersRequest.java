package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.connection;

import android.bluetooth.BluetoothGattCharacteristic;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusConstants;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest;

public class SetConnectionParametersRequest extends FossilRequest {
    private boolean finished = false;

    public boolean isFinished() {
        return this.finished;
    }

    public UUID getRequestUUID() {
        return UUID.fromString("3dda0002-957f-7d4a-34a6-74696673696d");
    }

    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        super.handleResponse(characteristic);
        this.finished = true;
    }

    public byte[] getStartSequence() {
        return new byte[]{2, 9, 12, 0, 12, 0, HPlusConstants.CMD_SET_GENDER, 0, 88, 2};
    }

    public boolean isBasicRequest() {
        return false;
    }
}
