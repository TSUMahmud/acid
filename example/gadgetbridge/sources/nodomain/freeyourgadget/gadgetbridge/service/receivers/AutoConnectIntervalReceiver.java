package nodomain.freeyourgadget.gadgetbridge.service.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.Calendar;
import nodomain.freeyourgadget.gadgetbridge.BuildConfig;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.DeviceCommunicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoConnectIntervalReceiver extends BroadcastReceiver {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AutoConnectIntervalReceiver.class);
    static int mDelay = 4;
    final DeviceCommunicationService service;

    public AutoConnectIntervalReceiver(DeviceCommunicationService service2) {
        this.service = service2;
        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(DeviceManager.ACTION_DEVICES_CHANGED);
        LocalBroadcastManager.getInstance(service2).registerReceiver(this, filterLocal);
    }

    public void onReceive(Context context, Intent intent) {
        GBDevice gbDevice;
        String action = intent.getAction();
        if (action != null && (gbDevice = this.service.getGBDevice()) != null) {
            if (action.equals(DeviceManager.ACTION_DEVICES_CHANGED)) {
                if (gbDevice.isInitialized()) {
                    LOG.info("will reset connection delay, device is initialized!");
                    mDelay = 4;
                } else if (gbDevice.getState() == GBDevice.State.WAITING_FOR_RECONNECT) {
                    scheduleReconnect();
                }
            } else if (action.equals("GB_RECONNECT") && gbDevice.getState() == GBDevice.State.WAITING_FOR_RECONNECT) {
                Logger logger = LOG;
                logger.info("Will re-connect to " + gbDevice.getAddress() + "(" + gbDevice.getName() + ")");
                GBApplication.deviceService().connect();
            }
        }
    }

    public void scheduleReconnect() {
        mDelay *= 2;
        if (mDelay > 64) {
            mDelay = 64;
        }
        scheduleReconnect(mDelay);
    }

    public void scheduleReconnect(int delay) {
        Logger logger = LOG;
        logger.info("scheduling reconnect in " + delay + " seconds");
        AlarmManager am = (AlarmManager) GBApplication.getContext().getSystemService("alarm");
        Intent intent = new Intent("GB_RECONNECT");
        intent.setPackage(BuildConfig.APPLICATION_ID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(GBApplication.getContext(), 0, intent, 0);
        if (Build.VERSION.SDK_INT >= 23) {
            am.setAndAllowWhileIdle(0, Calendar.getInstance().getTimeInMillis() + ((long) (delay * 1000)), pendingIntent);
        } else {
            am.set(0, Calendar.getInstance().getTimeInMillis() + ((long) (delay * 1000)), pendingIntent);
        }
    }

    public void destroy() {
        LocalBroadcastManager.getInstance(this.service).unregisterReceiver(this);
    }
}
