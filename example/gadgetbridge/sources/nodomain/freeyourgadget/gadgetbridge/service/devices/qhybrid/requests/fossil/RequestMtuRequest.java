package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil;

public class RequestMtuRequest extends FossilRequest {
    private boolean finished = false;
    private int mtu;

    public RequestMtuRequest(int mtu2) {
        this.mtu = mtu2;
    }

    public int getMtu() {
        return this.mtu;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void setFinished(boolean finished2) {
        this.finished = finished2;
    }

    public byte[] getStartSequence() {
        return new byte[0];
    }
}
