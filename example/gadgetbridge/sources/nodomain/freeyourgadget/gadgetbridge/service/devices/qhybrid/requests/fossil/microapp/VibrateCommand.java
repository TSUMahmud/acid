package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.microapp;

import java.nio.ByteBuffer;
import nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3Constants;

/* compiled from: MicroAppCommand */
class VibrateCommand implements MicroAppCommand {
    private VibrationType vibrationType;

    public VibrateCommand(VibrationType vibrationType2) {
        this.vibrationType = vibrationType2;
    }

    public byte[] getData() {
        return ByteBuffer.wrap(new byte[]{MakibesHR3Constants.CMD_SET_DATE_TIME, 0, 0}).put(2, this.vibrationType.getValue()).array();
    }
}
