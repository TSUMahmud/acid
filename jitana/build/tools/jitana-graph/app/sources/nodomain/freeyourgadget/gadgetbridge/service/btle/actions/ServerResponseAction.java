package nodomain.freeyourgadget.gadgetbridge.service.btle.actions;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServer;
import nodomain.freeyourgadget.gadgetbridge.Logging;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEServerAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerResponseAction extends BtLEServerAction {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) ServerResponseAction.class);
    private final int offset;
    private final int requestId;
    private final int status;
    private final byte[] value;

    public ServerResponseAction(BluetoothDevice device, int requestId2, int status2, int offset2, byte[] data) {
        super(device);
        this.value = data;
        this.requestId = requestId2;
        this.status = status2;
        this.offset = offset2;
    }

    public boolean run(BluetoothGattServer server) {
        return writeValue(server, getDevice(), this.requestId, this.status, this.offset, this.value);
    }

    /* access modifiers changed from: protected */
    public boolean writeValue(BluetoothGattServer gattServer, BluetoothDevice device, int requestId2, int status2, int offset2, byte[] value2) {
        if (LOG.isDebugEnabled()) {
            Logger logger = LOG;
            logger.debug("writing to server: " + device.getAddress() + ": " + Logging.formatBytes(value2));
        }
        return gattServer.sendResponse(device, requestId2, 0, offset2, value2);
    }

    /* access modifiers changed from: protected */
    public final byte[] getValue() {
        return this.value;
    }

    public boolean expectsResult() {
        return false;
    }
}
