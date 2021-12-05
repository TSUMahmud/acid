package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.microapp;

/* compiled from: MicroAppCommand */
enum VibrationType {
    NORMAL((byte) 4);
    
    private byte value;

    private VibrationType(byte value2) {
        this.value = value2;
    }

    public byte getValue() {
        return this.value;
    }
}
