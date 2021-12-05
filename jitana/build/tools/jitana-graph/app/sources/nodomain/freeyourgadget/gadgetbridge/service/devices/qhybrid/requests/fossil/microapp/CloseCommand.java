package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.microapp;

/* compiled from: MicroAppCommand */
class CloseCommand implements MicroAppCommand {
    CloseCommand() {
    }

    public byte[] getData() {
        return new byte[]{1, 0};
    }
}
