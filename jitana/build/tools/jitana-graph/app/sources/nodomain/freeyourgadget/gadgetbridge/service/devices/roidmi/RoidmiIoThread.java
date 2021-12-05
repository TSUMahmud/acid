package nodomain.freeyourgadget.gadgetbridge.service.devices.roidmi;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.btclassic.BtClassicIoThread;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoidmiIoThread extends BtClassicIoThread {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) RoidmiIoThread.class);
    private final byte[] HEADER;
    private final byte[] TRAILER;

    public RoidmiIoThread(GBDevice gbDevice, Context context, RoidmiProtocol roidmiProtocol, RoidmiSupport roidmiSupport, BluetoothAdapter roidmiBtAdapter) {
        super(gbDevice, context, roidmiProtocol, roidmiSupport, roidmiBtAdapter);
        this.HEADER = roidmiProtocol.packetHeader();
        this.TRAILER = roidmiProtocol.packetTrailer();
    }

    /* access modifiers changed from: protected */
    public byte[] parseIncoming(InputStream inputStream) throws IOException {
        ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
        boolean finished = false;
        byte[] incoming = new byte[1];
        while (!finished) {
            inputStream.read(incoming);
            msgStream.write(incoming);
            byte[] arr = msgStream.toByteArray();
            int length = arr.length;
            byte[] bArr = this.HEADER;
            if (length > bArr.length && arr.length == bArr.length + this.TRAILER.length + arr[bArr.length] + 2) {
                finished = true;
            }
        }
        byte[] msgArray = msgStream.toByteArray();
        Logger logger = LOG;
        logger.debug("Packet: " + C1238GB.hexdump(msgArray, 0, msgArray.length));
        return msgArray;
    }
}
