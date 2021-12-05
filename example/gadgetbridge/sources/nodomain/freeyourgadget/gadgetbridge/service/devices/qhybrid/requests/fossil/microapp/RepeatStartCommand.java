package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.microapp;

/* compiled from: MicroAppCommand */
class RepeatStartCommand implements MicroAppCommand {
    private byte count;

    public RepeatStartCommand(byte count2) {
        this.count = count2;
    }

    public byte[] getData() {
        return new byte[]{-122, 0, this.count};
    }
}
