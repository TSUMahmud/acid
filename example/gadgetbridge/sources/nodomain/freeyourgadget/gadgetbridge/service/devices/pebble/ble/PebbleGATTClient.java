package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PebbleGATTClient extends BluetoothGattCallback {
    private static final UUID CHARACTERISTIC_CONFIGURATION_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final UUID CONNECTION_PARAMETERS_CHARACTERISTIC = UUID.fromString("00000005-328E-0FBB-C642-1AA6699BDADA");
    private static final UUID CONNECTIVITY_CHARACTERISTIC = UUID.fromString("00000001-328E-0FBB-C642-1AA6699BDADA");
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) PebbleGATTClient.class);
    private static final UUID MTU_CHARACTERISTIC = UUID.fromString("00000003-328e-0fbb-c642-1aa6699bdada");
    private static final UUID PAIRING_TRIGGER_CHARACTERISTIC = UUID.fromString("00000002-328E-0FBB-C642-1AA6699BDADA");
    private static final UUID PPOGATT_CHARACTERISTIC_READ = UUID.fromString("30000004-328E-0FBB-C642-1AA6699BDADA");
    private static final UUID PPOGATT_CHARACTERISTIC_WRITE = UUID.fromString("30000006-328E-0FBB-C642-1AA6699BDADA");
    private static final UUID PPOGATT_SERVICE_UUID = UUID.fromString("30000003-328E-0FBB-C642-1AA6699BDADA");
    private static final UUID SERVICE_UUID = UUID.fromString("0000fed9-0000-1000-8000-00805f9b34fb");
    private boolean doPairing = true;
    private BluetoothGatt mBluetoothGatt;
    private final Context mContext;
    private final PebbleLESupport mPebbleLESupport;
    private CountDownLatch mWaitWriteCompleteLatch;
    private boolean oldPebble = false;
    private boolean removeBond = false;
    private BluetoothGattCharacteristic writeCharacteristics;

    PebbleGATTClient(PebbleLESupport pebbleLESupport, Context context, BluetoothDevice btDevice) {
        this.mContext = context;
        this.mPebbleLESupport = pebbleLESupport;
        connectToPebble(btDevice);
    }

    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (this.mPebbleLESupport.isExpectedDevice(gatt.getDevice())) {
            if (characteristic.getUuid().equals(MTU_CHARACTERISTIC)) {
                int newMTU = characteristic.getIntValue(18, 0).intValue();
                Logger logger = LOG;
                logger.info("Pebble requested MTU: " + newMTU);
                this.mPebbleLESupport.setMTU(newMTU);
            } else if (characteristic.getUuid().equals(PPOGATT_CHARACTERISTIC_READ)) {
                this.mPebbleLESupport.handlePPoGATTPacket((byte[]) characteristic.getValue().clone());
            } else {
                Logger logger2 = LOG;
                logger2.info("onCharacteristicChanged()" + characteristic.getUuid().toString() + StringUtils.SPACE + C1238GB.hexdump(characteristic.getValue(), 0, -1));
            }
        }
    }

    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (this.mPebbleLESupport.isExpectedDevice(gatt.getDevice())) {
            Logger logger = LOG;
            logger.info("onCharacteristicRead() status = " + status);
            if (status == 0) {
                Logger logger2 = LOG;
                logger2.info("onCharacteristicRead()" + characteristic.getUuid().toString() + StringUtils.SPACE + C1238GB.hexdump(characteristic.getValue(), 0, -1));
                if (this.oldPebble) {
                    subscribeToConnectivity(gatt);
                } else {
                    subscribeToConnectionParams(gatt);
                }
            }
        }
    }

    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (this.mPebbleLESupport.isExpectedDevice(gatt.getDevice())) {
            Logger logger = LOG;
            logger.info("onConnectionStateChange() status = " + status + " newState = " + newState);
            if (newState == 2) {
                LOG.info("calling discoverServices()");
                gatt.discoverServices();
            } else if (newState == 0) {
                this.mPebbleLESupport.close();
            }
        }
    }

    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (this.mPebbleLESupport.isExpectedDevice(gatt.getDevice())) {
            if (characteristic.getUuid().equals(PPOGATT_CHARACTERISTIC_WRITE)) {
                if (status != 0) {
                    LOG.error("something went wrong when writing to PPoGATT characteristics");
                }
                CountDownLatch countDownLatch = this.mWaitWriteCompleteLatch;
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                } else {
                    LOG.warn("mWaitWriteCompleteLatch is null!");
                }
            } else if (characteristic.getUuid().equals(PAIRING_TRIGGER_CHARACTERISTIC) || characteristic.getUuid().equals(CONNECTIVITY_CHARACTERISTIC)) {
                if (this.oldPebble) {
                    subscribeToConnectivity(gatt);
                } else {
                    subscribeToConnectionParams(gatt);
                }
            } else if (characteristic.getUuid().equals(MTU_CHARACTERISTIC) && GBApplication.isRunningLollipopOrLater()) {
                gatt.requestMtu(339);
            }
        }
    }

    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor bluetoothGattDescriptor, int status) {
        if (this.mPebbleLESupport.isExpectedDevice(gatt.getDevice())) {
            Logger logger = LOG;
            logger.info("onDescriptorWrite() status=" + status);
            UUID CHARACTERISTICUUID = bluetoothGattDescriptor.getCharacteristic().getUuid();
            if (CHARACTERISTICUUID.equals(CONNECTION_PARAMETERS_CHARACTERISTIC)) {
                subscribeToConnectivity(gatt);
            } else if (CHARACTERISTICUUID.equals(CONNECTIVITY_CHARACTERISTIC)) {
                subscribeToMTU(gatt);
            } else if (CHARACTERISTICUUID.equals(MTU_CHARACTERISTIC)) {
                if (this.mPebbleLESupport.clientOnly) {
                    subscribeToPPoGATT(gatt);
                } else {
                    setMTU(gatt);
                }
            } else if (CHARACTERISTICUUID.equals(PPOGATT_CHARACTERISTIC_READ)) {
                setMTU(gatt);
            }
        }
    }

    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (this.mPebbleLESupport.isExpectedDevice(gatt.getDevice())) {
            Logger logger = LOG;
            logger.info("onServicesDiscovered() status = " + status);
            if (status == 0) {
                this.oldPebble = gatt.getService(SERVICE_UUID).getCharacteristic(CONNECTION_PARAMETERS_CHARACTERISTIC) == null;
                if (this.oldPebble) {
                    LOG.info("This seems to be an older le enabled pebble");
                }
                if (this.doPairing) {
                    BluetoothGattCharacteristic characteristic = gatt.getService(SERVICE_UUID).getCharacteristic(PAIRING_TRIGGER_CHARACTERISTIC);
                    if ((characteristic.getProperties() & 8) != 0) {
                        LOG.info("This seems to be a >=4.0 FW Pebble, writing to pairing trigger");
                        if (this.mPebbleLESupport.clientOnly) {
                            characteristic.setValue(new byte[]{17});
                        } else {
                            characteristic.setValue(new byte[]{9});
                        }
                        gatt.writeCharacteristic(characteristic);
                        return;
                    }
                    LOG.info("This seems to be some <4.0 FW Pebble, reading pairing trigger");
                    gatt.readCharacteristic(characteristic);
                } else if (this.oldPebble) {
                    subscribeToConnectivity(gatt);
                } else {
                    subscribeToConnectionParams(gatt);
                }
            }
        }
    }

    private void connectToPebble(BluetoothDevice btDevice) {
        if (this.removeBond) {
            try {
                btDevice.getClass().getMethod("removeBond", (Class[]) null).invoke(btDevice, (Object[]) null);
            } catch (Exception e) {
                LOG.warn(e.getMessage());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e2) {
            }
        }
        if (this.mBluetoothGatt != null) {
            close();
        }
        this.mBluetoothGatt = btDevice.connectGatt(this.mContext, false, this);
    }

    private void subscribeToConnectivity(BluetoothGatt gatt) {
        LOG.info("subscribing to connectivity characteristic");
        BluetoothGattDescriptor descriptor = gatt.getService(SERVICE_UUID).getCharacteristic(CONNECTIVITY_CHARACTERISTIC).getDescriptor(CHARACTERISTIC_CONFIGURATION_DESCRIPTOR);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
        gatt.setCharacteristicNotification(gatt.getService(SERVICE_UUID).getCharacteristic(CONNECTIVITY_CHARACTERISTIC), true);
    }

    private void subscribeToMTU(BluetoothGatt gatt) {
        LOG.info("subscribing to mtu characteristic");
        BluetoothGattDescriptor descriptor = gatt.getService(SERVICE_UUID).getCharacteristic(MTU_CHARACTERISTIC).getDescriptor(CHARACTERISTIC_CONFIGURATION_DESCRIPTOR);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
        gatt.setCharacteristicNotification(gatt.getService(SERVICE_UUID).getCharacteristic(MTU_CHARACTERISTIC), true);
    }

    private void subscribeToConnectionParams(BluetoothGatt gatt) {
        LOG.info("subscribing to connection parameters characteristic");
        BluetoothGattDescriptor descriptor = gatt.getService(SERVICE_UUID).getCharacteristic(CONNECTION_PARAMETERS_CHARACTERISTIC).getDescriptor(CHARACTERISTIC_CONFIGURATION_DESCRIPTOR);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
        gatt.setCharacteristicNotification(gatt.getService(SERVICE_UUID).getCharacteristic(CONNECTION_PARAMETERS_CHARACTERISTIC), true);
    }

    private void setMTU(BluetoothGatt gatt) {
        LOG.info("setting MTU");
        BluetoothGattCharacteristic characteristic = gatt.getService(SERVICE_UUID).getCharacteristic(MTU_CHARACTERISTIC);
        characteristic.getDescriptor(CHARACTERISTIC_CONFIGURATION_DESCRIPTOR).setValue(new byte[]{11, 1});
        gatt.writeCharacteristic(characteristic);
    }

    private void subscribeToPPoGATT(BluetoothGatt gatt) {
        LOG.info("subscribing to PPoGATT read characteristic");
        BluetoothGattDescriptor descriptor = gatt.getService(PPOGATT_SERVICE_UUID).getCharacteristic(PPOGATT_CHARACTERISTIC_READ).getDescriptor(CHARACTERISTIC_CONFIGURATION_DESCRIPTOR);
        descriptor.setValue(new byte[]{1, 0});
        gatt.writeDescriptor(descriptor);
        gatt.setCharacteristicNotification(gatt.getService(PPOGATT_SERVICE_UUID).getCharacteristic(PPOGATT_CHARACTERISTIC_READ), true);
        this.writeCharacteristics = gatt.getService(PPOGATT_SERVICE_UUID).getCharacteristic(PPOGATT_CHARACTERISTIC_WRITE);
    }

    /* access modifiers changed from: package-private */
    public synchronized void sendDataToPebble(byte[] data) {
        this.mWaitWriteCompleteLatch = new CountDownLatch(1);
        this.writeCharacteristics.setValue((byte[]) data.clone());
        if (!this.mBluetoothGatt.writeCharacteristic(this.writeCharacteristics)) {
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

    public void close() {
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
    }
}
