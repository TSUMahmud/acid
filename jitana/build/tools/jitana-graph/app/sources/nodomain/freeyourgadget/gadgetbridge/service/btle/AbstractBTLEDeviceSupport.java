package nodomain.freeyourgadget.gadgetbridge.service.btle;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.Logging;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.AbstractDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.CheckInitializedAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.AbstractBleProfile;
import org.slf4j.Logger;

public abstract class AbstractBTLEDeviceSupport extends AbstractDeviceSupport implements GattCallback, GattServerCallback {
    public static final String BASE_UUID = "0000%s-0000-1000-8000-00805f9b34fb";
    private final Object characteristicsMonitor = new Object();
    private Logger logger;
    private Map<UUID, BluetoothGattCharacteristic> mAvailableCharacteristics;
    private BtLEQueue mQueue;
    private final List<AbstractBleProfile<?>> mSupportedProfiles = new ArrayList();
    private final Set<BluetoothGattService> mSupportedServerServices = new HashSet(4);
    private final Set<UUID> mSupportedServices = new HashSet(4);

    public AbstractBTLEDeviceSupport(Logger logger2) {
        this.logger = logger2;
        if (logger2 == null) {
            throw new IllegalArgumentException("logger must not be null");
        }
    }

    public boolean connect() {
        if (this.mQueue == null) {
            this.mQueue = new BtLEQueue(getBluetoothAdapter(), getDevice(), this, this, getContext(), this.mSupportedServerServices);
            this.mQueue.setAutoReconnect(getAutoReconnect());
        }
        return this.mQueue.connect();
    }

    public void setAutoReconnect(boolean enable) {
        super.setAutoReconnect(enable);
        BtLEQueue btLEQueue = this.mQueue;
        if (btLEQueue != null) {
            btLEQueue.setAutoReconnect(enable);
        }
    }

    /* access modifiers changed from: protected */
    public TransactionBuilder initializeDevice(TransactionBuilder builder) {
        return builder;
    }

    public void dispose() {
        BtLEQueue btLEQueue = this.mQueue;
        if (btLEQueue != null) {
            btLEQueue.dispose();
            this.mQueue = null;
        }
    }

    public TransactionBuilder createTransactionBuilder(String taskName) {
        return new TransactionBuilder(taskName);
    }

    public TransactionBuilder performInitialized(String taskName) throws IOException {
        if (isConnected() || connect()) {
            if (!isInitialized()) {
                TransactionBuilder builder = createTransactionBuilder("Initialize device");
                builder.add(new CheckInitializedAction(this.gbDevice));
                initializeDevice(builder).queue(getQueue());
            }
            return createTransactionBuilder(taskName);
        }
        throw new IOException("1: Unable to connect to device: " + getDevice());
    }

    public ServerTransactionBuilder createServerTransactionBuilder(String taskName) {
        return new ServerTransactionBuilder(taskName);
    }

    public ServerTransactionBuilder performServer(String taskName) throws IOException {
        if (isConnected() || connect()) {
            return createServerTransactionBuilder(taskName);
        }
        throw new IOException("1: Unable to connect to device: " + getDevice());
    }

    public void performConnected(Transaction transaction) throws IOException {
        if (isConnected() || connect()) {
            getQueue().add(transaction);
            return;
        }
        throw new IOException("2: Unable to connect to device: " + getDevice());
    }

    public void performImmediately(TransactionBuilder builder) throws IOException {
        if (isConnected()) {
            getQueue().insert(builder.getTransaction());
            return;
        }
        throw new IOException("Not connected to device: " + getDevice());
    }

    public BtLEQueue getQueue() {
        return this.mQueue;
    }

    /* access modifiers changed from: protected */
    public void addSupportedService(UUID aSupportedService) {
        this.mSupportedServices.add(aSupportedService);
    }

    /* access modifiers changed from: protected */
    public void addSupportedProfile(AbstractBleProfile<?> profile) {
        this.mSupportedProfiles.add(profile);
    }

    /* access modifiers changed from: protected */
    public void addSupportedServerService(BluetoothGattService service) {
        this.mSupportedServerServices.add(service);
    }

    public BluetoothGattCharacteristic getCharacteristic(UUID uuid) {
        synchronized (this.characteristicsMonitor) {
            if (this.mAvailableCharacteristics == null) {
                return null;
            }
            BluetoothGattCharacteristic bluetoothGattCharacteristic = this.mAvailableCharacteristics.get(uuid);
            return bluetoothGattCharacteristic;
        }
    }

    private void gattServicesDiscovered(List<BluetoothGattService> discoveredGattServices) {
        if (discoveredGattServices == null) {
            this.logger.warn("No gatt services discovered: null!");
            return;
        }
        Set<UUID> supportedServices = getSupportedServices();
        Map<UUID, BluetoothGattCharacteristic> newCharacteristics = new HashMap<>();
        for (BluetoothGattService service : discoveredGattServices) {
            if (supportedServices.contains(service.getUuid())) {
                Logger logger2 = this.logger;
                logger2.debug("discovered supported service: " + BleNamesResolver.resolveServiceName(service.getUuid().toString()) + ": " + service.getUuid());
                List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                if (characteristics == null || characteristics.isEmpty()) {
                    Logger logger3 = this.logger;
                    logger3.warn("Supported LE service " + service.getUuid() + "did not return any characteristics");
                } else {
                    HashMap<UUID, BluetoothGattCharacteristic> intmAvailableCharacteristics = new HashMap<>(characteristics.size());
                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        intmAvailableCharacteristics.put(characteristic.getUuid(), characteristic);
                        Logger logger4 = this.logger;
                        logger4.info("    characteristic: " + BleNamesResolver.resolveCharacteristicName(characteristic.getUuid().toString()) + ": " + characteristic.getUuid());
                    }
                    newCharacteristics.putAll(intmAvailableCharacteristics);
                    synchronized (this.characteristicsMonitor) {
                        this.mAvailableCharacteristics = newCharacteristics;
                    }
                }
            } else {
                Logger logger5 = this.logger;
                logger5.debug("discovered unsupported service: " + BleNamesResolver.resolveServiceName(service.getUuid().toString()) + ": " + service.getUuid());
            }
        }
    }

    /* access modifiers changed from: protected */
    public Set<UUID> getSupportedServices() {
        return this.mSupportedServices;
    }

    public void logMessageContent(byte[] value) {
        Logger logger2 = this.logger;
        StringBuilder sb = new StringBuilder();
        sb.append("RECEIVED DATA WITH LENGTH: ");
        sb.append(value != null ? Integer.valueOf(value.length) : "(null)");
        logger2.info(sb.toString());
        Logging.logBytes(this.logger, value);
    }

    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        for (AbstractBleProfile<?> profile : this.mSupportedProfiles) {
            profile.onConnectionStateChange(gatt, status, newState);
        }
    }

    public void onServicesDiscovered(BluetoothGatt gatt) {
        gattServicesDiscovered(gatt.getServices());
        if (getDevice().getState().compareTo(GBDevice.State.INITIALIZING) >= 0) {
            Logger logger2 = this.logger;
            logger2.warn("Services discovered, but device state is already " + getDevice().getState() + " for device: " + getDevice() + ", so ignoring");
            return;
        }
        initializeDevice(createTransactionBuilder("Initializing device")).queue(getQueue());
    }

    public boolean onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        for (AbstractBleProfile<?> profile : this.mSupportedProfiles) {
            if (profile.onCharacteristicRead(gatt, characteristic, status)) {
                return true;
            }
        }
        return false;
    }

    public boolean onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        for (AbstractBleProfile<?> profile : this.mSupportedProfiles) {
            if (profile.onCharacteristicWrite(gatt, characteristic, status)) {
                return true;
            }
        }
        return false;
    }

    public boolean onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        for (AbstractBleProfile<?> profile : this.mSupportedProfiles) {
            if (profile.onDescriptorRead(gatt, descriptor, status)) {
                return true;
            }
        }
        return false;
    }

    public boolean onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        for (AbstractBleProfile<?> profile : this.mSupportedProfiles) {
            if (profile.onDescriptorWrite(gatt, descriptor, status)) {
                return true;
            }
        }
        return false;
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        for (AbstractBleProfile<?> profile : this.mSupportedProfiles) {
            if (profile.onCharacteristicChanged(gatt, characteristic)) {
                return true;
            }
        }
        return false;
    }

    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        for (AbstractBleProfile<?> profile : this.mSupportedProfiles) {
            profile.onReadRemoteRssi(gatt, rssi, status);
        }
    }

    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
    }

    public void onSetFmFrequency(float frequency) {
    }

    public void onSetLedColor(int color) {
    }

    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
    }

    public boolean onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        return false;
    }

    public boolean onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        return false;
    }

    public boolean onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
        return false;
    }

    public boolean onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        return false;
    }
}
