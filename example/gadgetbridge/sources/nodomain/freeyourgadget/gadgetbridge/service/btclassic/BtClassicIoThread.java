package nodomain.freeyourgadget.gadgetbridge.service.btclassic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.ParcelUuid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.serial.AbstractSerialDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceIoThread;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceProtocol;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BtClassicIoThread extends GBDeviceIoThread {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) BtClassicIoThread.class);
    private BluetoothAdapter mBtAdapter = null;
    private BluetoothSocket mBtSocket = null;
    private final AbstractSerialDeviceSupport mDeviceSupport;
    private InputStream mInStream = null;
    private boolean mIsConnected = false;
    private OutputStream mOutStream = null;
    private final GBDeviceProtocol mProtocol;
    private boolean mQuit = false;

    /* access modifiers changed from: protected */
    public abstract byte[] parseIncoming(InputStream inputStream) throws IOException;

    public void quit() {
        this.mQuit = true;
        BluetoothSocket bluetoothSocket = this.mBtSocket;
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    public BtClassicIoThread(GBDevice gbDevice, Context context, GBDeviceProtocol deviceProtocol, AbstractSerialDeviceSupport deviceSupport, BluetoothAdapter btAdapter) {
        super(gbDevice, context);
        this.mProtocol = deviceProtocol;
        this.mDeviceSupport = deviceSupport;
        this.mBtAdapter = btAdapter;
    }

    public synchronized void write(byte[] bytes) {
        if (bytes != null) {
            if (this.mOutStream == null) {
                LOG.error("mOutStream is null");
                return;
            }
            Logger logger = LOG;
            logger.debug("writing:" + C1238GB.hexdump(bytes, 0, bytes.length));
            try {
                this.mOutStream.write(bytes);
                this.mOutStream.flush();
            } catch (IOException e) {
                LOG.error("Error writing.", (Throwable) e);
            }
        } else {
            return;
        }
        return;
    }

    public void run() {
        this.mIsConnected = connect();
        if (!this.mIsConnected) {
            setUpdateState(GBDevice.State.NOT_CONNECTED);
            return;
        }
        this.mQuit = false;
        while (!this.mQuit) {
            LOG.info("Ready for a new message exchange.");
            try {
                GBDeviceEvent[] deviceEvents = this.mProtocol.decodeResponse(parseIncoming(this.mInStream));
                if (deviceEvents == null) {
                    LOG.info("unhandled message");
                } else {
                    for (GBDeviceEvent deviceEvent : deviceEvents) {
                        if (deviceEvent != null) {
                            this.mDeviceSupport.evaluateGBDeviceEvent(deviceEvent);
                        }
                    }
                }
            } catch (SocketTimeoutException e) {
                LOG.debug("socket timeout, we can't help but ignore this");
            } catch (IOException e2) {
                LOG.info(e2.getMessage());
                this.mIsConnected = false;
                this.mBtSocket = null;
                this.mInStream = null;
                this.mOutStream = null;
                LOG.info("Bluetooth socket closed, will quit IO Thread");
            }
        }
        this.mIsConnected = false;
        BluetoothSocket bluetoothSocket = this.mBtSocket;
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e3) {
                LOG.error(e3.getMessage());
            }
            this.mBtSocket = null;
        }
        setUpdateState(GBDevice.State.NOT_CONNECTED);
    }

    /* access modifiers changed from: protected */
    public boolean connect() {
        GBDevice.State originalState = this.gbDevice.getState();
        setUpdateState(GBDevice.State.CONNECTING);
        try {
            BluetoothDevice btDevice = this.mBtAdapter.getRemoteDevice(this.gbDevice.getAddress());
            ParcelUuid[] uuids = btDevice.getUuids();
            if (uuids == null) {
                LOG.warn("Device provided no UUIDs to connect to, giving up: " + this.gbDevice);
                return false;
            }
            for (ParcelUuid uuid : uuids) {
                Logger logger = LOG;
                logger.info("found service UUID " + uuid);
            }
            this.mBtSocket = btDevice.createRfcommSocketToServiceRecord(getUuidToConnect(uuids));
            this.mBtSocket.connect();
            this.mInStream = this.mBtSocket.getInputStream();
            this.mOutStream = this.mBtSocket.getOutputStream();
            setUpdateState(GBDevice.State.CONNECTED);
            write(this.mProtocol.encodeSetTime());
            setUpdateState(GBDevice.State.INITIALIZED);
            return true;
        } catch (IOException e) {
            LOG.error("Server socket cannot be started.");
            setUpdateState(originalState);
            this.mInStream = null;
            this.mOutStream = null;
            this.mBtSocket = null;
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public UUID getUuidToConnect(ParcelUuid[] uuids) {
        return uuids[0].getUuid();
    }

    /* access modifiers changed from: protected */
    public void setUpdateState(GBDevice.State state) {
        this.gbDevice.setState(state);
        this.gbDevice.sendDeviceUpdateIntent(getContext());
    }
}
