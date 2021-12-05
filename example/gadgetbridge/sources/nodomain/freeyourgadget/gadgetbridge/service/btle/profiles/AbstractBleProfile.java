package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractGattCallback;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEQueue;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;

public abstract class AbstractBleProfile<T extends AbstractBTLEDeviceSupport> extends AbstractGattCallback {
    private List<IntentListener> listeners = new ArrayList(1);
    private final T mSupport;

    public AbstractBleProfile(T support) {
        this.mSupport = support;
    }

    public void addListener(IntentListener listener) {
        if (listener != null && !this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public boolean removeListener(IntentListener listener) {
        return this.listeners.remove(listener);
    }

    /* access modifiers changed from: protected */
    public List<IntentListener> getListeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    /* access modifiers changed from: protected */
    public void notify(Intent intent) {
        for (IntentListener listener : this.listeners) {
            listener.notify(intent);
        }
    }

    public TransactionBuilder performInitialized(String taskName) throws IOException {
        TransactionBuilder builder = this.mSupport.performInitialized(taskName);
        builder.setGattCallback(this);
        return builder;
    }

    public Context getContext() {
        return this.mSupport.getContext();
    }

    /* access modifiers changed from: protected */
    public GBDevice getDevice() {
        return this.mSupport.getDevice();
    }

    /* access modifiers changed from: protected */
    public BluetoothGattCharacteristic getCharacteristic(UUID uuid) {
        return this.mSupport.getCharacteristic(uuid);
    }

    /* access modifiers changed from: protected */
    public BtLEQueue getQueue() {
        return this.mSupport.getQueue();
    }
}
