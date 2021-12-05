package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil;

import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

public class SetDeviceStateRequest extends FossilRequest {
    private GBDevice.State deviceState;

    public SetDeviceStateRequest(GBDevice.State deviceState2) {
        this.deviceState = deviceState2;
    }

    public GBDevice.State getDeviceState() {
        return this.deviceState;
    }

    public boolean isFinished() {
        return true;
    }

    public byte[] getStartSequence() {
        return new byte[0];
    }
}
