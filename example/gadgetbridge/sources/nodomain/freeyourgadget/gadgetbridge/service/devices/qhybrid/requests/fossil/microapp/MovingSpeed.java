package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.microapp;

/* compiled from: MicroAppCommand */
enum MovingSpeed {
    MAX((byte) 0),
    HALF((byte) 1),
    QUARTER((byte) 2),
    EIGHTH((byte) 3),
    SIXTEENTH((byte) 4);
    
    private byte value;

    private MovingSpeed(byte value2) {
        this.value = value2;
    }

    public byte getValue() {
        return this.value;
    }
}
