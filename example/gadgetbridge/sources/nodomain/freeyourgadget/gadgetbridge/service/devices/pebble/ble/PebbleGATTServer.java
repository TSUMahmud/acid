package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PebbleGATTServer extends BluetoothGattServerCallback {
    private static final UUID CHARACTERISTICS_CONFIGURATION_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) PebbleGATTServer.class);
    private static final UUID READ_CHARACTERISTICS = UUID.fromString("10000002-328E-0FBB-C642-1AA6699BDADA");
    private static final UUID SERVER_SERVICE = UUID.fromString("10000000-328E-0FBB-C642-1AA6699BDADA");
    private static final UUID SERVER_SERVICE_BADBAD = UUID.fromString("BADBADBA-DBAD-BADB-ADBA-BADBADBADBAD");
    private static final UUID WRITE_CHARACTERISTICS = UUID.fromString("10000001-328E-0FBB-C642-1AA6699BDADA");
    private BluetoothGattServer mBluetoothGattServer;
    private final BluetoothDevice mBtDevice;
    private Context mContext;
    private final PebbleLESupport mPebbleLESupport;
    private CountDownLatch mWaitWriteCompleteLatch;
    private BluetoothGattCharacteristic writeCharacteristics;

    PebbleGATTServer(PebbleLESupport pebbleLESupport, Context context, BluetoothDevice btDevice) {
        this.mContext = context;
        this.mBtDevice = btDevice;
        this.mPebbleLESupport = pebbleLESupport;
    }

    /* access modifiers changed from: package-private */
    public boolean initialize() {
        BluetoothManager bluetoothManager = (BluetoothManager) this.mContext.getSystemService("bluetooth");
        if (bluetoothManager == null) {
            return false;
        }
        this.mBluetoothGattServer = bluetoothManager.openGattServer(this.mContext, this);
        if (this.mBluetoothGattServer == null) {
            return false;
        }
        BluetoothGattService pebbleGATTService = new BluetoothGattService(SERVER_SERVICE, 0);
        pebbleGATTService.addCharacteristic(new BluetoothGattCharacteristic(READ_CHARACTERISTICS, 2, 1));
        this.writeCharacteristics = new BluetoothGattCharacteristic(WRITE_CHARACTERISTICS, 20, 16);
        this.writeCharacteristics.addDescriptor(new BluetoothGattDescriptor(CHARACTERISTICS_CONFIGURATION_DESCRIPTOR, 16));
        pebbleGATTService.addCharacteristic(this.writeCharacteristics);
        this.mBluetoothGattServer.addService(pebbleGATTService);
        return true;
    }

    /* access modifiers changed from: package-private */
    public synchronized void sendDataToPebble(byte[] data) {
        this.mWaitWriteCompleteLatch = new CountDownLatch(1);
        this.writeCharacteristics.setValue((byte[]) data.clone());
        if (!this.mBluetoothGattServer.notifyCharacteristicChanged(this.mBtDevice, this.writeCharacteristics, false)) {
            LOG.error("could not send data to pebble (error writing characteristic)");
        } else {
            try {
                this.mWaitWriteCompleteLatch.await();
            } catch (InterruptedException e) {
                LOG.warn("interrupted while waiting for write complete latch");
            }
        }
        this.mWaitWriteCompleteLatch = null;
    }

    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        if (this.mPebbleLESupport.isExpectedDevice(device)) {
            if (!characteristic.getUuid().equals(READ_CHARACTERISTICS)) {
                LOG.warn("unexpected read request");
                return;
            }
            Logger logger = LOG;
            logger.info("will send response to read request from device: " + device.getAddress());
            if (!this.mBluetoothGattServer.sendResponse(device, requestId, 0, offset, new byte[]{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1})) {
                LOG.warn("error sending response");
            }
        }
    }

    public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        if (this.mPebbleLESupport.isExpectedDevice(device)) {
            if (!characteristic.getUuid().equals(WRITE_CHARACTERISTICS)) {
                LOG.warn("unexpected write request");
            } else {
                this.mPebbleLESupport.handlePPoGATTPacket(value);
            }
        }
    }

    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
        if (this.mPebbleLESupport.isExpectedDevice(device)) {
            Logger logger = LOG;
            logger.info("Connection state change for device: " + device.getAddress() + "  status = " + status + " newState = " + newState);
            if (newState == 0) {
                this.mPebbleLESupport.close();
            }
        }
    }

    public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        if (this.mPebbleLESupport.isExpectedDevice(device)) {
            if (!descriptor.getCharacteristic().getUuid().equals(WRITE_CHARACTERISTICS)) {
                LOG.warn("unexpected write request");
                return;
            }
            Logger logger = LOG;
            StringBuilder sb = new StringBuilder();
            sb.append("onDescriptorWriteRequest() notifications enabled = ");
            boolean z = false;
            if (value[0] == 1) {
                z = true;
            }
            sb.append(z);
            logger.info(sb.toString());
            if (!this.mBluetoothGattServer.sendResponse(device, requestId, 0, offset, value)) {
                LOG.warn("onDescriptorWriteRequest() error sending response!");
            }
        }
    }

    public void onServiceAdded(int status, BluetoothGattService service) {
        Logger logger = LOG;
        logger.info("onServiceAdded() status = " + status + " service = " + service.getUuid());
        if (status == 0 && service.getUuid().equals(SERVER_SERVICE)) {
            BluetoothGattService badbadService = new BluetoothGattService(SERVER_SERVICE_BADBAD, 0);
            badbadService.addCharacteristic(new BluetoothGattCharacteristic(SERVER_SERVICE_BADBAD, 2, 1));
            this.mBluetoothGattServer.addService(badbadService);
        }
    }

    public void onMtuChanged(BluetoothDevice device, int mtu) {
        if (this.mPebbleLESupport.isExpectedDevice(device)) {
            Logger logger = LOG;
            logger.info("Pebble requested mtu for server: " + mtu);
            this.mPebbleLESupport.setMTU(mtu);
        }
    }

    public void onNotificationSent(BluetoothDevice bluetoothDevice, int status) {
        if (this.mPebbleLESupport.isExpectedDevice(bluetoothDevice)) {
            if (status != 0) {
                LOG.error("something went wrong when writing to PPoGATT characteristics");
            }
            CountDownLatch countDownLatch = this.mWaitWriteCompleteLatch;
            if (countDownLatch != null) {
                countDownLatch.countDown();
            } else {
                LOG.warn("mWaitWriteCompleteLatch is null!");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void close() {
        BluetoothGattServer bluetoothGattServer = this.mBluetoothGattServer;
        if (bluetoothGattServer != null) {
            bluetoothGattServer.cancelConnection(this.mBtDevice);
            this.mBluetoothGattServer.clearServices();
            this.mBluetoothGattServer.close();
        }
    }
}
