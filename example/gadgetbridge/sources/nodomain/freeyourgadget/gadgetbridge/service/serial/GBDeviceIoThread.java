package nodomain.freeyourgadget.gadgetbridge.service.serial;

import android.content.Context;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

public abstract class GBDeviceIoThread extends Thread {
    private final Context context;
    protected final GBDevice gbDevice;

    public GBDeviceIoThread(GBDevice gbDevice2, Context context2) {
        this.gbDevice = gbDevice2;
        this.context = context2;
    }

    public Context getContext() {
        return this.context;
    }

    public GBDevice getDevice() {
        return this.gbDevice;
    }

    /* access modifiers changed from: protected */
    public boolean connect() {
        return false;
    }

    public void run() {
    }

    public synchronized void write(byte[] bytes) {
    }

    public void quit() {
    }
}
