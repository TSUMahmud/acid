package nodomain.freeyourgadget.gadgetbridge.service.btle;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import java.io.IOException;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.operations.OperationStatus;

public abstract class AbstractBTLEOperation<T extends AbstractBTLEDeviceSupport> implements GattCallback, BTLEOperation {
    private final T mSupport;
    private String name;
    protected OperationStatus operationStatus = OperationStatus.INITIAL;

    /* access modifiers changed from: protected */
    public abstract void doPerform() throws IOException;

    protected AbstractBTLEOperation(T support) {
        this.mSupport = support;
    }

    public final void perform() throws IOException {
        this.operationStatus = OperationStatus.STARTED;
        prePerform();
        this.operationStatus = OperationStatus.RUNNING;
        doPerform();
    }

    /* access modifiers changed from: protected */
    public void prePerform() throws IOException {
    }

    /* access modifiers changed from: protected */
    public void operationFinished() throws IOException {
    }

    public TransactionBuilder performInitialized(String taskName) throws IOException {
        TransactionBuilder builder = this.mSupport.performInitialized(taskName);
        builder.setGattCallback(this);
        return builder;
    }

    public TransactionBuilder createTransactionBuilder(String taskName) {
        TransactionBuilder builder = getSupport().createTransactionBuilder(taskName);
        builder.setGattCallback(this);
        return builder;
    }

    public void performImmediately(TransactionBuilder builder) throws IOException {
        this.mSupport.performImmediately(builder);
    }

    /* access modifiers changed from: protected */
    public Context getContext() {
        return this.mSupport.getContext();
    }

    /* access modifiers changed from: protected */
    public GBDevice getDevice() {
        return this.mSupport.getDevice();
    }

    /* access modifiers changed from: protected */
    public void setName(String name2) {
        this.name = name2;
    }

    public String getName() {
        String str = this.name;
        if (str != null) {
            return str;
        }
        String busyTask = getDevice().getBusyTask();
        if (busyTask != null) {
            return busyTask;
        }
        return getClass().getSimpleName();
    }

    /* access modifiers changed from: protected */
    public BluetoothGattCharacteristic getCharacteristic(UUID uuid) {
        return this.mSupport.getCharacteristic(uuid);
    }

    /* access modifiers changed from: protected */
    public BtLEQueue getQueue() {
        return this.mSupport.getQueue();
    }

    /* access modifiers changed from: protected */
    public void unsetBusy() {
        if (getDevice().isBusy()) {
            getDevice().unsetBusyTask();
            getDevice().sendDeviceUpdateIntent(getContext());
        }
    }

    public boolean isOperationRunning() {
        return this.operationStatus == OperationStatus.RUNNING;
    }

    public boolean isOperationFinished() {
        return this.operationStatus == OperationStatus.FINISHED;
    }

    public T getSupport() {
        return this.mSupport;
    }

    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        this.mSupport.onConnectionStateChange(gatt, status, newState);
    }

    public void onServicesDiscovered(BluetoothGatt gatt) {
        this.mSupport.onServicesDiscovered(gatt);
    }

    public boolean onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        return this.mSupport.onCharacteristicRead(gatt, characteristic, status);
    }

    public boolean onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        return this.mSupport.onCharacteristicWrite(gatt, characteristic, status);
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        return this.mSupport.onCharacteristicChanged(gatt, characteristic);
    }

    public boolean onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        return this.mSupport.onDescriptorRead(gatt, descriptor, status);
    }

    public boolean onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        return this.mSupport.onDescriptorWrite(gatt, descriptor, status);
    }

    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        this.mSupport.onReadRemoteRssi(gatt, rssi, status);
    }

    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        this.mSupport.onMtuChanged(gatt, mtu, status);
    }
}
