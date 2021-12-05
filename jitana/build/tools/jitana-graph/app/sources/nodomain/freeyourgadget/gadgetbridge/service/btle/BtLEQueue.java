package nodomain.freeyourgadget.gadgetbridge.service.btle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.Logging;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BtLEQueue {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) BtLEQueue.class);
    private Thread dispatchThread = new Thread("Gadgetbridge GATT Dispatcher") {
        public void run() {
            BtLEQueue.LOG.debug("Queue Dispatch Thread started.");
            while (!BtLEQueue.this.mDisposed && !BtLEQueue.this.mCrashed) {
                try {
                    AbstractTransaction qTransaction = (AbstractTransaction) BtLEQueue.this.mTransactions.take();
                    if (!BtLEQueue.this.isConnected()) {
                        BtLEQueue.LOG.debug("not connected, waiting for connection...");
                        BtLEQueue.this.internalGattCallback.reset();
                        CountDownLatch unused = BtLEQueue.this.mConnectionLatch = new CountDownLatch(1);
                        BtLEQueue.this.mConnectionLatch.await();
                        CountDownLatch unused2 = BtLEQueue.this.mConnectionLatch = null;
                    }
                    if (qTransaction instanceof ServerTransaction) {
                        ServerTransaction serverTransaction = (ServerTransaction) qTransaction;
                        BtLEQueue.this.internalGattServerCallback.setTransactionGattCallback(serverTransaction.getGattCallback());
                        boolean unused3 = BtLEQueue.this.mAbortServerTransaction = false;
                        Iterator<BtLEServerAction> it = serverTransaction.getActions().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            BtLEServerAction action = it.next();
                            if (!BtLEQueue.this.mAbortServerTransaction) {
                                if (BtLEQueue.LOG.isDebugEnabled()) {
                                    Logger access$000 = BtLEQueue.LOG;
                                    access$000.debug("About to run action: " + action);
                                }
                                if (!action.run(BtLEQueue.this.mBluetoothGattServer)) {
                                    Logger access$0002 = BtLEQueue.LOG;
                                    access$0002.error("Action returned false: " + action);
                                    break;
                                } else if (action.expectsResult()) {
                                    BtLEQueue.this.mWaitForServerActionResultLatch.await();
                                    CountDownLatch unused4 = BtLEQueue.this.mWaitForServerActionResultLatch = null;
                                    if (BtLEQueue.this.mAbortServerTransaction) {
                                        break;
                                    }
                                }
                            } else {
                                BtLEQueue.LOG.info("Aborting running transaction");
                                break;
                            }
                        }
                    }
                    if (qTransaction instanceof Transaction) {
                        Transaction transaction = (Transaction) qTransaction;
                        BtLEQueue.this.internalGattCallback.setTransactionGattCallback(transaction.getGattCallback());
                        boolean unused5 = BtLEQueue.this.mAbortTransaction = false;
                        Iterator<BtLEAction> it2 = transaction.getActions().iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                break;
                            }
                            BtLEAction action2 = it2.next();
                            if (!BtLEQueue.this.mAbortTransaction) {
                                BluetoothGattCharacteristic unused6 = BtLEQueue.this.mWaitCharacteristic = action2.getCharacteristic();
                                CountDownLatch unused7 = BtLEQueue.this.mWaitForActionResultLatch = new CountDownLatch(1);
                                if (BtLEQueue.LOG.isDebugEnabled()) {
                                    Logger access$0003 = BtLEQueue.LOG;
                                    access$0003.debug("About to run action: " + action2);
                                }
                                if (action2 instanceof GattListenerAction) {
                                    BtLEQueue.this.internalGattCallback.setTransactionGattCallback(((GattListenerAction) action2).getGattCallback());
                                }
                                if (!action2.run(BtLEQueue.this.mBluetoothGatt)) {
                                    Logger access$0004 = BtLEQueue.LOG;
                                    access$0004.error("Action returned false: " + action2);
                                    break;
                                } else if (action2.expectsResult()) {
                                    BtLEQueue.this.mWaitForActionResultLatch.await();
                                    CountDownLatch unused8 = BtLEQueue.this.mWaitForActionResultLatch = null;
                                    if (BtLEQueue.this.mAbortTransaction) {
                                        break;
                                    }
                                }
                            } else {
                                BtLEQueue.LOG.info("Aborting running transaction");
                                break;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    CountDownLatch unused9 = BtLEQueue.this.mConnectionLatch = null;
                    BtLEQueue.LOG.debug("Thread interrupted");
                } catch (Throwable th) {
                    CountDownLatch unused10 = BtLEQueue.this.mWaitForActionResultLatch = null;
                    BluetoothGattCharacteristic unused11 = BtLEQueue.this.mWaitCharacteristic = null;
                    throw th;
                }
                CountDownLatch unused12 = BtLEQueue.this.mWaitForActionResultLatch = null;
                BluetoothGattCharacteristic unused13 = BtLEQueue.this.mWaitCharacteristic = null;
            }
            BtLEQueue.LOG.info("Queue Dispatch Thread terminated.");
        }
    };
    /* access modifiers changed from: private */
    public final InternalGattCallback internalGattCallback;
    /* access modifiers changed from: private */
    public final InternalGattServerCallback internalGattServerCallback;
    /* access modifiers changed from: private */
    public volatile boolean mAbortServerTransaction;
    /* access modifiers changed from: private */
    public volatile boolean mAbortTransaction;
    private boolean mAutoReconnect;
    private final BluetoothAdapter mBluetoothAdapter;
    /* access modifiers changed from: private */
    public BluetoothGatt mBluetoothGatt;
    /* access modifiers changed from: private */
    public BluetoothGattServer mBluetoothGattServer;
    /* access modifiers changed from: private */
    public CountDownLatch mConnectionLatch;
    private final Context mContext;
    /* access modifiers changed from: private */
    public volatile boolean mCrashed;
    /* access modifiers changed from: private */
    public volatile boolean mDisposed;
    /* access modifiers changed from: private */
    public final Object mGattMonitor = new Object();
    private final GBDevice mGbDevice;
    private final Set<BluetoothGattService> mSupportedServerServices;
    /* access modifiers changed from: private */
    public final BlockingQueue<AbstractTransaction> mTransactions = new LinkedBlockingQueue();
    /* access modifiers changed from: private */
    public BluetoothGattCharacteristic mWaitCharacteristic;
    /* access modifiers changed from: private */
    public CountDownLatch mWaitForActionResultLatch;
    /* access modifiers changed from: private */
    public CountDownLatch mWaitForServerActionResultLatch;

    public BtLEQueue(BluetoothAdapter bluetoothAdapter, GBDevice gbDevice, GattCallback externalGattCallback, GattServerCallback externalGattServerCallback, Context context, Set<BluetoothGattService> supportedServerServices) {
        this.mBluetoothAdapter = bluetoothAdapter;
        this.mGbDevice = gbDevice;
        this.internalGattCallback = new InternalGattCallback(externalGattCallback);
        this.internalGattServerCallback = new InternalGattServerCallback(externalGattServerCallback);
        this.mContext = context;
        this.mSupportedServerServices = supportedServerServices;
        this.dispatchThread.start();
    }

    public void setAutoReconnect(boolean enable) {
        this.mAutoReconnect = enable;
    }

    /* access modifiers changed from: protected */
    public boolean isConnected() {
        return this.mGbDevice.isConnected();
    }

    public boolean connect() {
        boolean z = false;
        if (isConnected()) {
            LOG.warn("Ingoring connect() because already connected.");
            return false;
        }
        synchronized (this.mGattMonitor) {
            if (this.mBluetoothGatt != null) {
                Logger logger = LOG;
                logger.info("connect() requested -- disconnecting previous connection: " + this.mGbDevice.getName());
                disconnect();
            }
        }
        Logger logger2 = LOG;
        logger2.info("Attempting to connect to " + this.mGbDevice.getName());
        this.mBluetoothAdapter.cancelDiscovery();
        BluetoothDevice remoteDevice = this.mBluetoothAdapter.getRemoteDevice(this.mGbDevice.getAddress());
        if (!this.mSupportedServerServices.isEmpty()) {
            BluetoothManager bluetoothManager = (BluetoothManager) this.mContext.getSystemService("bluetooth");
            if (bluetoothManager == null) {
                LOG.error("Error getting bluetoothManager");
                return false;
            }
            this.mBluetoothGattServer = bluetoothManager.openGattServer(this.mContext, this.internalGattServerCallback);
            if (this.mBluetoothGattServer == null) {
                LOG.error("Error opening Gatt Server");
                return false;
            }
            for (BluetoothGattService service : this.mSupportedServerServices) {
                this.mBluetoothGattServer.addService(service);
            }
        }
        synchronized (this.mGattMonitor) {
            if (GBApplication.isRunningMarshmallowOrLater()) {
                this.mBluetoothGatt = remoteDevice.connectGatt(this.mContext, false, this.internalGattCallback, 2);
            } else {
                this.mBluetoothGatt = remoteDevice.connectGatt(this.mContext, false, this.internalGattCallback);
            }
        }
        if (this.mBluetoothGatt != null) {
            z = true;
        }
        boolean result = z;
        if (result) {
            setDeviceConnectionState(GBDevice.State.CONNECTING);
        }
        return result;
    }

    /* access modifiers changed from: private */
    public void setDeviceConnectionState(GBDevice.State newState) {
        Logger logger = LOG;
        logger.debug("new device connection state: " + newState);
        this.mGbDevice.setState(newState);
        this.mGbDevice.sendDeviceUpdateIntent(this.mContext);
    }

    public void disconnect() {
        synchronized (this.mGattMonitor) {
            LOG.debug("disconnect()");
            BluetoothGatt gatt = this.mBluetoothGatt;
            if (gatt != null) {
                this.mBluetoothGatt = null;
                LOG.info("Disconnecting BtLEQueue from GATT device");
                gatt.disconnect();
                gatt.close();
                setDeviceConnectionState(GBDevice.State.NOT_CONNECTED);
            }
            BluetoothGattServer gattServer = this.mBluetoothGattServer;
            if (gattServer != null) {
                this.mBluetoothGattServer = null;
                gattServer.clearServices();
                gattServer.close();
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleDisconnected(int status) {
        Logger logger = LOG;
        logger.debug("handleDisconnected: " + status);
        this.internalGattCallback.reset();
        this.mTransactions.clear();
        this.mAbortTransaction = true;
        this.mAbortServerTransaction = true;
        CountDownLatch countDownLatch = this.mWaitForActionResultLatch;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
        CountDownLatch countDownLatch2 = this.mWaitForServerActionResultLatch;
        if (countDownLatch2 != null) {
            countDownLatch2.countDown();
        }
        setDeviceConnectionState(GBDevice.State.NOT_CONNECTED);
        if (this.mBluetoothGatt != null && !maybeReconnect()) {
            disconnect();
        }
    }

    private boolean maybeReconnect() {
        if (!this.mAutoReconnect || this.mBluetoothGatt == null) {
            return false;
        }
        LOG.info("Enabling automatic ble reconnect...");
        boolean result = this.mBluetoothGatt.connect();
        if (result) {
            setDeviceConnectionState(GBDevice.State.WAITING_FOR_RECONNECT);
        }
        return result;
    }

    public void dispose() {
        if (!this.mDisposed) {
            this.mDisposed = true;
            disconnect();
            this.dispatchThread.interrupt();
            this.dispatchThread = null;
        }
    }

    public void add(Transaction transaction) {
        Logger logger = LOG;
        logger.debug("about to add: " + transaction);
        if (!transaction.isEmpty()) {
            this.mTransactions.add(transaction);
        }
    }

    public void add(ServerTransaction transaction) {
        Logger logger = LOG;
        logger.debug("about to add: " + transaction);
        if (!transaction.isEmpty()) {
            this.mTransactions.add(transaction);
        }
    }

    public void insert(Transaction transaction) {
        Logger logger = LOG;
        logger.debug("about to insert: " + transaction);
        if (!transaction.isEmpty()) {
            List<AbstractTransaction> tail = new ArrayList<>(this.mTransactions.size() + 2);
            for (AbstractTransaction t : this.mTransactions) {
                tail.add(t);
            }
            this.mTransactions.clear();
            this.mTransactions.add(transaction);
            this.mTransactions.addAll(tail);
        }
    }

    public void clear() {
        this.mTransactions.clear();
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt != null) {
            return bluetoothGatt.getServices();
        }
        LOG.warn("BluetoothGatt is null => no services available.");
        return Collections.emptyList();
    }

    /* access modifiers changed from: private */
    public boolean checkCorrectGattInstance(BluetoothGatt gatt, String where) {
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (gatt == bluetoothGatt || bluetoothGatt == null) {
            return true;
        }
        Logger logger = LOG;
        logger.info("Ignoring event from wrong BluetoothGatt instance: " + where + "; " + gatt);
        return false;
    }

    /* access modifiers changed from: private */
    public boolean checkCorrectBluetoothDevice(BluetoothDevice device) {
        if (device.getAddress().equals(this.mGbDevice.getAddress())) {
            return true;
        }
        Logger logger = LOG;
        logger.info("Ignoring request from wrong Bluetooth device: " + device.getAddress());
        return false;
    }

    private final class InternalGattCallback extends BluetoothGattCallback {
        private final GattCallback mExternalGattCallback;
        private GattCallback mTransactionGattCallback;

        public InternalGattCallback(GattCallback externalGattCallback) {
            this.mExternalGattCallback = externalGattCallback;
        }

        public void setTransactionGattCallback(GattCallback callback) {
            this.mTransactionGattCallback = callback;
        }

        private GattCallback getCallbackToUse() {
            GattCallback gattCallback = this.mTransactionGattCallback;
            if (gattCallback != null) {
                return gattCallback;
            }
            return this.mExternalGattCallback;
        }

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Logger access$000 = BtLEQueue.LOG;
            access$000.debug("connection state change, newState: " + newState + getStatusString(status));
            synchronized (BtLEQueue.this.mGattMonitor) {
                if (BtLEQueue.this.mBluetoothGatt == null) {
                    BluetoothGatt unused = BtLEQueue.this.mBluetoothGatt = gatt;
                }
            }
            if (BtLEQueue.this.checkCorrectGattInstance(gatt, "connection state event")) {
                if (status != 0) {
                    Logger access$0002 = BtLEQueue.LOG;
                    access$0002.warn("connection state event with error status " + status);
                }
                if (newState == 0) {
                    BtLEQueue.LOG.info("Disconnected from GATT server.");
                    BtLEQueue.this.handleDisconnected(status);
                } else if (newState == 1) {
                    BtLEQueue.LOG.info("Connecting to GATT server...");
                    BtLEQueue.this.setDeviceConnectionState(GBDevice.State.CONNECTING);
                } else if (newState == 2) {
                    BtLEQueue.LOG.info("Connected to GATT server.");
                    BtLEQueue.this.setDeviceConnectionState(GBDevice.State.CONNECTED);
                    List<BluetoothGattService> cachedServices = gatt.getServices();
                    if (cachedServices == null || cachedServices.size() <= 0) {
                        BtLEQueue.LOG.info("Attempting to start service discovery");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                if (BtLEQueue.this.mBluetoothGatt != null) {
                                    BtLEQueue.this.mBluetoothGatt.discoverServices();
                                }
                            }
                        });
                        return;
                    }
                    BtLEQueue.LOG.info("Using cached services, skipping discovery");
                    onServicesDiscovered(gatt, 0);
                }
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BtLEQueue btLEQueue = BtLEQueue.this;
            if (btLEQueue.checkCorrectGattInstance(gatt, "services discovered: " + getStatusString(status))) {
                if (status == 0) {
                    if (getCallbackToUse() != null) {
                        getCallbackToUse().onServicesDiscovered(gatt);
                    }
                    if (BtLEQueue.this.mConnectionLatch != null) {
                        BtLEQueue.this.mConnectionLatch.countDown();
                        return;
                    }
                    return;
                }
                Logger access$000 = BtLEQueue.LOG;
                access$000.warn("onServicesDiscovered received: " + status);
            }
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Logger access$000 = BtLEQueue.LOG;
            access$000.debug("characteristic write: " + characteristic.getUuid() + getStatusString(status));
            if (BtLEQueue.this.checkCorrectGattInstance(gatt, "characteristic write")) {
                if (getCallbackToUse() != null) {
                    getCallbackToUse().onCharacteristicWrite(gatt, characteristic, status);
                }
                checkWaitingCharacteristic(characteristic, status);
            }
        }

        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            if (getCallbackToUse() != null) {
                getCallbackToUse().onMtuChanged(gatt, mtu, status);
            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Logger access$000 = BtLEQueue.LOG;
            access$000.debug("characteristic read: " + characteristic.getUuid() + getStatusString(status));
            if (BtLEQueue.this.checkCorrectGattInstance(gatt, "characteristic read")) {
                if (getCallbackToUse() != null) {
                    try {
                        getCallbackToUse().onCharacteristicRead(gatt, characteristic, status);
                    } catch (Throwable ex) {
                        Logger access$0002 = BtLEQueue.LOG;
                        access$0002.error("onCharacteristicRead: " + ex.getMessage(), ex);
                    }
                }
                checkWaitingCharacteristic(characteristic, status);
            }
        }

        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Logger access$000 = BtLEQueue.LOG;
            access$000.debug("descriptor read: " + descriptor.getUuid() + getStatusString(status));
            if (BtLEQueue.this.checkCorrectGattInstance(gatt, "descriptor read")) {
                if (getCallbackToUse() != null) {
                    try {
                        getCallbackToUse().onDescriptorRead(gatt, descriptor, status);
                    } catch (Throwable ex) {
                        Logger access$0002 = BtLEQueue.LOG;
                        access$0002.error("onDescriptorRead: " + ex.getMessage(), ex);
                    }
                }
                checkWaitingCharacteristic(descriptor.getCharacteristic(), status);
            }
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Logger access$000 = BtLEQueue.LOG;
            access$000.debug("descriptor write: " + descriptor.getUuid() + getStatusString(status));
            if (BtLEQueue.this.checkCorrectGattInstance(gatt, "descriptor write")) {
                if (getCallbackToUse() != null) {
                    try {
                        getCallbackToUse().onDescriptorWrite(gatt, descriptor, status);
                    } catch (Throwable ex) {
                        Logger access$0002 = BtLEQueue.LOG;
                        access$0002.error("onDescriptorWrite: " + ex.getMessage(), ex);
                    }
                }
                checkWaitingCharacteristic(descriptor.getCharacteristic(), status);
            }
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (BtLEQueue.LOG.isDebugEnabled()) {
                String content = Logging.formatBytes(characteristic.getValue());
                Logger access$000 = BtLEQueue.LOG;
                access$000.debug("characteristic changed: " + characteristic.getUuid() + " value: " + content);
            }
            if (BtLEQueue.this.checkCorrectGattInstance(gatt, "characteristic changed")) {
                if (getCallbackToUse() != null) {
                    try {
                        getCallbackToUse().onCharacteristicChanged(gatt, characteristic);
                    } catch (Throwable ex) {
                        Logger access$0002 = BtLEQueue.LOG;
                        access$0002.error("onCharaceristicChanged: " + ex.getMessage(), ex);
                    }
                } else {
                    BtLEQueue.LOG.info("No gattcallback registered, ignoring characteristic change");
                }
            }
        }

        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Logger access$000 = BtLEQueue.LOG;
            access$000.debug("remote rssi: " + rssi + getStatusString(status));
            if (BtLEQueue.this.checkCorrectGattInstance(gatt, "remote rssi") && getCallbackToUse() != null) {
                try {
                    getCallbackToUse().onReadRemoteRssi(gatt, rssi, status);
                } catch (Throwable ex) {
                    Logger access$0002 = BtLEQueue.LOG;
                    access$0002.error("onReadRemoteRssi: " + ex.getMessage(), ex);
                }
            }
        }

        private void checkWaitingCharacteristic(BluetoothGattCharacteristic characteristic, int status) {
            if (status != 0) {
                if (characteristic != null) {
                    Logger access$000 = BtLEQueue.LOG;
                    access$000.debug("failed btle action, aborting transaction: " + characteristic.getUuid() + getStatusString(status));
                }
                boolean unused = BtLEQueue.this.mAbortTransaction = true;
            }
            if (characteristic == null || BtLEQueue.this.mWaitCharacteristic == null || !characteristic.getUuid().equals(BtLEQueue.this.mWaitCharacteristic.getUuid())) {
                if (BtLEQueue.this.mWaitCharacteristic != null) {
                    Logger access$0002 = BtLEQueue.LOG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("checkWaitingCharacteristic: mismatched characteristic received: ");
                    sb.append((characteristic == null || characteristic.getUuid() == null) ? "(null)" : characteristic.getUuid().toString());
                    access$0002.error(sb.toString());
                }
            } else if (BtLEQueue.this.mWaitForActionResultLatch != null) {
                BtLEQueue.this.mWaitForActionResultLatch.countDown();
            }
        }

        private String getStatusString(int status) {
            if (status == 0) {
                return " (success)";
            }
            return " (failed: " + status + ")";
        }

        public void reset() {
            if (BtLEQueue.LOG.isDebugEnabled()) {
                BtLEQueue.LOG.debug("internal gatt callback set to null");
            }
            this.mTransactionGattCallback = null;
        }
    }

    private final class InternalGattServerCallback extends BluetoothGattServerCallback {
        private final GattServerCallback mExternalGattServerCallback;
        private GattServerCallback mTransactionGattCallback;

        public InternalGattServerCallback(GattServerCallback externalGattServerCallback) {
            this.mExternalGattServerCallback = externalGattServerCallback;
        }

        public void setTransactionGattCallback(GattServerCallback callback) {
            this.mTransactionGattCallback = callback;
        }

        private GattServerCallback getCallbackToUse() {
            GattServerCallback gattServerCallback = this.mTransactionGattCallback;
            if (gattServerCallback != null) {
                return gattServerCallback;
            }
            return this.mExternalGattServerCallback;
        }

        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            Logger access$000 = BtLEQueue.LOG;
            access$000.debug("gatt server connection state change, newState: " + newState + getStatusString(status));
            if (BtLEQueue.this.checkCorrectBluetoothDevice(device) && status != 0) {
                Logger access$0002 = BtLEQueue.LOG;
                access$0002.warn("connection state event with error status " + status);
            }
        }

        private String getStatusString(int status) {
            if (status == 0) {
                return " (success)";
            }
            return " (failed: " + status + ")";
        }

        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            if (BtLEQueue.this.checkCorrectBluetoothDevice(device)) {
                Logger access$000 = BtLEQueue.LOG;
                access$000.debug("characterstic read request: " + device.getAddress() + " characteristic: " + characteristic.getUuid());
                if (getCallbackToUse() != null) {
                    getCallbackToUse().onCharacteristicReadRequest(device, requestId, offset, characteristic);
                }
            }
        }

        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            BluetoothDevice bluetoothDevice = device;
            if (BtLEQueue.this.checkCorrectBluetoothDevice(device)) {
                Logger access$000 = BtLEQueue.LOG;
                access$000.debug("characteristic write request: " + device.getAddress() + " characteristic: " + characteristic.getUuid());
                if (getCallbackToUse() != null) {
                    getCallbackToUse().onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
                }
            }
        }

        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            if (BtLEQueue.this.checkCorrectBluetoothDevice(device)) {
                Logger access$000 = BtLEQueue.LOG;
                access$000.debug("onDescriptorReadRequest: " + device.getAddress());
                if (getCallbackToUse() != null) {
                    getCallbackToUse().onDescriptorReadRequest(device, requestId, offset, descriptor);
                }
            }
        }

        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            BluetoothDevice bluetoothDevice = device;
            if (BtLEQueue.this.checkCorrectBluetoothDevice(device)) {
                Logger access$000 = BtLEQueue.LOG;
                access$000.debug("onDescriptorWriteRequest: " + device.getAddress());
                if (getCallbackToUse() != null) {
                    getCallbackToUse().onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
                }
            }
        }
    }
}
