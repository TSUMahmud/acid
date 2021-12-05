package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.database.PeriodicExporter;

public class AutoStartReceiver extends BroadcastReceiver {
    private static final String TAG = AutoStartReceiver.class.getName();

    public void onReceive(Context context, Intent intent) {
        if (GBApplication.getGBPrefs().getAutoStart() && "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.i(TAG, "Boot completed, starting Gadgetbridge");
            if (GBApplication.getPrefs().getBoolean("general_autoconnectonbluetooth", false)) {
                Log.i(TAG, "Autoconnect is enabled, attempting to connect");
                GBApplication.deviceService().connect();
            } else {
                GBApplication.deviceService().start();
            }
            PeriodicExporter.enablePeriodicExport(context);
        }
    }
}
