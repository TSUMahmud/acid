package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluetoothStateChangeReceiver extends BroadcastReceiver {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) BluetoothStateChangeReceiver.class);

    public void onReceive(Context context, Intent intent) {
        if (!"android.bluetooth.adapter.action.STATE_CHANGED".equals(intent.getAction())) {
            return;
        }
        if (intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1) == 12) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(DeviceManager.ACTION_REFRESH_DEVICELIST));
            if (GBApplication.getPrefs().getBoolean("general_autoconnectonbluetooth", false)) {
                LOG.info("Bluetooth turned on => connecting...");
                GBApplication.deviceService().connect();
            }
        } else if (intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1) == 10) {
            LOG.info("Bluetooth turned off => disconnecting...");
            GBApplication.deviceService().disconnect();
        }
    }
}
