package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.microapp;

/* compiled from: MicroAppCommand */
class StreamCommand implements MicroAppCommand {
    private byte type;

    public StreamCommand(byte type2) {
        this.type = type2;
    }

    public byte[] getData() {
        return new byte[]{-117, 0, this.type};
    }
}
