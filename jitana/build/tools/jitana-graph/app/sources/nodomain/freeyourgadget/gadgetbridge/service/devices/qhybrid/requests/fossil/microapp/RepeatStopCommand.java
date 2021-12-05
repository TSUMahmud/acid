package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.microapp;

/* compiled from: MicroAppCommand */
class RepeatStopCommand implements MicroAppCommand {
    RepeatStopCommand() {
    }

    public byte[] getData() {
        return new byte[]{7, 0};
    }
}
