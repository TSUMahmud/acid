package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PebbleLESupport {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) PebbleLESupport.class);
    public boolean clientOnly = false;
    private final BluetoothDevice mBtDevice;
    private boolean mIsConnected = false;
    /* access modifiers changed from: private */
    public int mMTU = 20;
    private int mMTULimit = Integer.MAX_VALUE;
    /* access modifiers changed from: private */
    public PebbleGATTClient mPebbleGATTClient;
    /* access modifiers changed from: private */
    public PebbleGATTServer mPebbleGATTServer;
    private PipeReader mPipeReader;
    /* access modifiers changed from: private */
    public PipedInputStream mPipedInputStream;
    private PipedOutputStream mPipedOutputStream;
    private Handler mWriteHandler;
    private HandlerThread mWriteHandlerThread;

    public PebbleLESupport(Context context, BluetoothDevice btDevice, PipedInputStream pipedInputStream, PipedOutputStream pipedOutputStream) throws IOException {
        this.mBtDevice = btDevice;
        this.mPipedInputStream = new PipedInputStream();
        this.mPipedOutputStream = new PipedOutputStream();
        try {
            pipedOutputStream.connect(this.mPipedInputStream);
            pipedInputStream.connect(this.mPipedOutputStream);
        } catch (IOException e) {
            LOG.warn("could not connect input stream");
        }
        this.mWriteHandlerThread = new HandlerThread("write handler thread");
        this.mWriteHandlerThread.start();
        this.mWriteHandler = new Handler(this.mWriteHandlerThread.getLooper());
        this.mMTULimit = GBApplication.getPrefs().getInt("pebble_mtu_limit", 512);
        this.mMTULimit = Math.max(this.mMTULimit, 20);
        this.mMTULimit = Math.min(this.mMTULimit, 512);
        this.clientOnly = GBApplication.getPrefs().getBoolean("pebble_gatt_clientonly", false);
        if (!this.clientOnly) {
            this.mPebbleGATTServer = new PebbleGATTServer(this, context, this.mBtDevice);
        }
        if (this.clientOnly || this.mPebbleGATTServer.initialize()) {
            this.mPebbleGATTClient = new PebbleGATTClient(this, context, this.mBtDevice);
            try {
                synchronized (this) {
                    wait(30000);
                    if (this.mIsConnected) {
                        return;
                    }
                }
            } catch (InterruptedException e2) {
            }
        }
        close();
        throw new IOException("connection failed");
    }

    private void writeToPipedOutputStream(byte[] value, int offset, int count) {
        try {
            this.mPipedOutputStream.write(value, offset, count);
        } catch (IOException e) {
            LOG.warn("error writing to output stream", (Throwable) e);
        }
    }

    public synchronized void close() {
        destroyPipedInputReader();
        if (this.mPebbleGATTServer != null) {
            this.mPebbleGATTServer.close();
            this.mPebbleGATTServer = null;
        }
        if (this.mPebbleGATTClient != null) {
            this.mPebbleGATTClient.close();
            this.mPebbleGATTClient = null;
        }
        try {
            this.mPipedInputStream.close();
        } catch (IOException e) {
        }
        try {
            this.mPipedOutputStream.close();
        } catch (IOException e2) {
        }
        if (this.mWriteHandlerThread != null) {
            this.mWriteHandlerThread.quit();
        }
    }

    private synchronized void createPipedInputReader() {
        if (this.mPipeReader == null) {
            this.mPipeReader = new PipeReader();
        }
        if (!this.mPipeReader.isAlive()) {
            this.mPipeReader.start();
        }
    }

    private synchronized void destroyPipedInputReader() {
        if (this.mPipeReader != null) {
            this.mPipeReader.interrupt();
            try {
                this.mPipeReader.join();
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            }
            this.mPipeReader = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void setMTU(int mtu) {
        this.mMTU = Math.min(mtu, this.mMTULimit);
    }

    public void handlePPoGATTPacket(byte[] value) {
        if (!this.mIsConnected) {
            this.mIsConnected = true;
            synchronized (this) {
                notify();
            }
        }
        int header = value[0] & 255;
        int command = header & 7;
        int serial = header >> 3;
        if (command == 1) {
            Logger logger = LOG;
            logger.info("got ACK for serial = " + serial);
        }
        if (command == 2) {
            LOG.info("got command 0x02");
            if (value.length > 1) {
                sendDataToPebble(new byte[]{3, 25, 25});
                createPipedInputReader();
                return;
            }
            sendDataToPebble(new byte[]{3});
        } else if (command == 0) {
            Logger logger2 = LOG;
            logger2.info("got PPoGATT package serial = " + serial + " sending ACK");
            sendAckToPebble(serial);
            writeToPipedOutputStream(value, 1, value.length - 1);
        }
    }

    private void sendAckToPebble(int serial) {
        sendDataToPebble(new byte[]{(byte) ((1 | (serial << 3)) & 255)});
    }

    /* access modifiers changed from: private */
    public synchronized void sendDataToPebble(final byte[] bytes) {
        if (this.mPebbleGATTServer != null) {
            this.mWriteHandler.post(new Runnable() {
                public void run() {
                    PebbleLESupport.this.mPebbleGATTServer.sendDataToPebble(bytes);
                }
            });
        } else {
            this.mWriteHandler.post(new Runnable() {
                public void run() {
                    PebbleLESupport.this.mPebbleGATTClient.sendDataToPebble(bytes);
                }
            });
        }
    }

    private class PipeReader extends Thread {
        int mmSequence;

        private PipeReader() {
            this.mmSequence = 0;
        }

        /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: Regions count limit reached
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
            	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
            */
        /* JADX WARNING: Removed duplicated region for block: B:10:0x004e A[Catch:{ IOException -> 0x007d }] */
        /* JADX WARNING: Removed duplicated region for block: B:4:0x0012 A[Catch:{ IOException -> 0x007d }, LOOP:1: B:3:0x0010->B:4:0x0012, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:7:0x0039 A[Catch:{ IOException -> 0x007d }, LOOP:2: B:6:0x0037->B:7:0x0039, LOOP_END] */
        public void run() {
            /*
                r12 = this;
                r0 = 8192(0x2000, float:1.14794E-41)
                byte[] r0 = new byte[r0]
            L_0x0004:
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport r1 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport.this     // Catch:{ IOException -> 0x007d }
                java.io.PipedInputStream r1 = r1.mPipedInputStream     // Catch:{ IOException -> 0x007d }
                r2 = 0
                r3 = 4
                int r1 = r1.read(r0, r2, r3)     // Catch:{ IOException -> 0x007d }
            L_0x0010:
                if (r1 >= r3) goto L_0x0020
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport r4 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport.this     // Catch:{ IOException -> 0x007d }
                java.io.PipedInputStream r4 = r4.mPipedInputStream     // Catch:{ IOException -> 0x007d }
                int r5 = 4 - r1
                int r4 = r4.read(r0, r1, r5)     // Catch:{ IOException -> 0x007d }
                int r1 = r1 + r4
                goto L_0x0010
            L_0x0020:
                byte r4 = r0[r2]     // Catch:{ IOException -> 0x007d }
                r4 = r4 & 255(0xff, float:3.57E-43)
                int r4 = r4 << 8
                r5 = 1
                byte r6 = r0[r5]     // Catch:{ IOException -> 0x007d }
                r6 = r6 & 255(0xff, float:3.57E-43)
                r4 = r4 | r6
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport r6 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport.this     // Catch:{ IOException -> 0x007d }
                java.io.PipedInputStream r6 = r6.mPipedInputStream     // Catch:{ IOException -> 0x007d }
                int r6 = r6.read(r0, r3, r4)     // Catch:{ IOException -> 0x007d }
                r1 = r6
            L_0x0037:
                if (r1 >= r4) goto L_0x0049
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport r6 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport.this     // Catch:{ IOException -> 0x007d }
                java.io.PipedInputStream r6 = r6.mPipedInputStream     // Catch:{ IOException -> 0x007d }
                int r7 = r1 + 4
                int r8 = r4 - r1
                int r6 = r6.read(r0, r7, r8)     // Catch:{ IOException -> 0x007d }
                int r1 = r1 + r6
                goto L_0x0037
            L_0x0049:
                int r6 = r1 + 4
                r7 = 0
            L_0x004c:
                if (r6 <= 0) goto L_0x007c
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport r8 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport.this     // Catch:{ IOException -> 0x007d }
                int r8 = r8.mMTU     // Catch:{ IOException -> 0x007d }
                int r8 = r8 - r3
                if (r6 >= r8) goto L_0x0059
                r8 = r6
                goto L_0x0060
            L_0x0059:
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport r8 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport.this     // Catch:{ IOException -> 0x007d }
                int r8 = r8.mMTU     // Catch:{ IOException -> 0x007d }
                int r8 = r8 - r3
            L_0x0060:
                int r9 = r8 + 1
                byte[] r9 = new byte[r9]     // Catch:{ IOException -> 0x007d }
                int r10 = r12.mmSequence     // Catch:{ IOException -> 0x007d }
                int r11 = r10 + 1
                r12.mmSequence = r11     // Catch:{ IOException -> 0x007d }
                int r10 = r10 << 3
                r10 = r10 & 255(0xff, float:3.57E-43)
                byte r10 = (byte) r10     // Catch:{ IOException -> 0x007d }
                r9[r2] = r10     // Catch:{ IOException -> 0x007d }
                java.lang.System.arraycopy(r0, r7, r9, r5, r8)     // Catch:{ IOException -> 0x007d }
                nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport r10 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport.this     // Catch:{ IOException -> 0x007d }
                r10.sendDataToPebble(r9)     // Catch:{ IOException -> 0x007d }
                int r7 = r7 + r8
                int r6 = r6 - r8
                goto L_0x004c
            L_0x007c:
                goto L_0x0004
            L_0x007d:
                r1 = move-exception
                org.slf4j.Logger r2 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport.LOG
                java.lang.String r3 = r1.getMessage()
                r2.info(r3)
                java.lang.Thread r2 = java.lang.Thread.currentThread()
                r2.interrupt()
                org.slf4j.Logger r1 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport.LOG
                java.lang.String r2 = "Pipereader thread shut down"
                r1.info(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble.PebbleLESupport.PipeReader.run():void");
        }

        public void interrupt() {
            super.interrupt();
            try {
                PebbleLESupport.LOG.info("closing piped inputstream");
                PebbleLESupport.this.mPipedInputStream.close();
            } catch (IOException e) {
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isExpectedDevice(BluetoothDevice device) {
        if (device.getAddress().equals(this.mBtDevice.getAddress())) {
            return true;
        }
        Logger logger = LOG;
        logger.info("unhandled device: " + device.getAddress() + " , ignoring, will only talk to " + this.mBtDevice.getAddress());
        return false;
    }
}
