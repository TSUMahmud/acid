package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.microapp;

/* compiled from: MicroAppCommand */
enum MovingDirection {
    CLOCKWISE((byte) 0),
    COUNTER_CLOCKWISE((byte) 1),
    SHORTEST((byte) 2);
    
    private byte value;

    private MovingDirection(byte value2) {
        this.value = value2;
    }

    public byte getValue() {
        return this.value;
    }
}
