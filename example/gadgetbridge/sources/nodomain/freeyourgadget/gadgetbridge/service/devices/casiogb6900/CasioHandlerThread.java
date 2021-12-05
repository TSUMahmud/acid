package nodomain.freeyourgadget.gadgetbridge.service.devices.casiogb6900;

import android.content.Context;
import java.util.Calendar;
import java.util.GregorianCalendar;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceIoThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CasioHandlerThread extends GBDeviceIoThread {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) CasioHandlerThread.class);
    private int TX_PERIOD = 60;
    private CasioGB6900DeviceSupport mDeviceSupport;
    private boolean mQuit = false;
    private Calendar mTxTime = GregorianCalendar.getInstance();
    private final Object waitObject = new Object();

    public CasioHandlerThread(GBDevice gbDevice, Context context, CasioGB6900DeviceSupport deviceSupport) {
        super(gbDevice, context);
        LOG.info("Initializing Casio Handler Thread");
        this.mQuit = false;
        this.mDeviceSupport = deviceSupport;
    }

    public void run() {
        this.mQuit = false;
        long waitTime = (long) (this.TX_PERIOD * 1000);
        while (!this.mQuit) {
            if (waitTime > 0) {
                synchronized (this.waitObject) {
                    try {
                        this.waitObject.wait(waitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!this.mQuit) {
                GBDevice.State state = this.gbDevice.getState();
                if (state == GBDevice.State.NOT_CONNECTED || state == GBDevice.State.WAITING_FOR_RECONNECT) {
                    LOG.debug("Closing handler thread, state not connected or waiting for reconnect.");
                    quit();
                } else {
                    if (GregorianCalendar.getInstance().compareTo(this.mTxTime) > 0) {
                        requestTxPowerLevel();
                    }
                    waitTime = this.mTxTime.getTimeInMillis() - GregorianCalendar.getInstance().getTimeInMillis();
                }
            } else {
                return;
            }
        }
    }

    public void requestTxPowerLevel() {
        try {
            this.mDeviceSupport.readTxPowerLevel();
        } catch (Exception e) {
        }
        this.mTxTime = GregorianCalendar.getInstance();
        this.mTxTime.add(13, this.TX_PERIOD);
        synchronized (this.waitObject) {
            this.waitObject.notify();
        }
    }

    public void quit() {
        LOG.info("CasioHandlerThread: Quit Handler Thread");
        this.mQuit = true;
        synchronized (this.waitObject) {
            this.waitObject.notify();
        }
    }
}
