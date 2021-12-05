package nodomain.freeyourgadget.gadgetbridge.service.devices.liveview;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.devices.liveview.LiveviewConstants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.btclassic.BtClassicIoThread;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceProtocol;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveviewIoThread extends BtClassicIoThread {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) LiveviewIoThread.class);
    private static final UUID SERIAL = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private enum ReaderState {
        ID,
        HEADER_LEN,
        HEADER,
        PAYLOAD
    }

    public LiveviewIoThread(GBDevice gbDevice, Context context, GBDeviceProtocol lvProtocol, LiveviewSupport lvSupport, BluetoothAdapter lvBtAdapter) {
        super(gbDevice, context, lvProtocol, lvSupport, lvBtAdapter);
    }

    /* access modifiers changed from: protected */
    public byte[] parseIncoming(InputStream inputStream) throws IOException {
        ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
        boolean finished = false;
        ReaderState state = ReaderState.ID;
        byte[] incoming = new byte[1];
        while (!finished) {
            inputStream.read(incoming);
            msgStream.write(incoming);
            int i = C11911.f186xf06a6fb3[state.ordinal()];
            if (i == 1) {
                state = ReaderState.HEADER_LEN;
                incoming = new byte[1];
            } else if (i == 2) {
                state = ReaderState.HEADER;
                incoming = new byte[(incoming[0] & 255)];
            } else if (i == 3) {
                int payloadSize = getLastInt(msgStream);
                if (payloadSize < 0 || payloadSize > 8000) {
                    throw new IOException();
                }
                state = ReaderState.PAYLOAD;
                incoming = new byte[payloadSize];
            } else if (i == 4) {
                finished = true;
            }
        }
        byte[] msgArray = msgStream.toByteArray();
        Logger logger = LOG;
        logger.debug("received: " + C1238GB.hexdump(msgArray, 0, msgArray.length));
        return msgArray;
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.service.devices.liveview.LiveviewIoThread$1 */
    static /* synthetic */ class C11911 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$service$devices$liveview$LiveviewIoThread$ReaderState */
        static final /* synthetic */ int[] f186xf06a6fb3 = new int[ReaderState.values().length];

        static {
            try {
                f186xf06a6fb3[ReaderState.ID.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f186xf06a6fb3[ReaderState.HEADER_LEN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f186xf06a6fb3[ReaderState.HEADER.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f186xf06a6fb3[ReaderState.PAYLOAD.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private int getLastInt(ByteArrayOutputStream stream) {
        byte[] array = stream.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(array, array.length - 4, 4);
        buffer.order(LiveviewConstants.BYTE_ORDER);
        return buffer.getInt();
    }
}
