package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.DeviceCommunicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluetoothConnectReceiver extends BroadcastReceiver {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) BluetoothConnectReceiver.class);
    final DeviceCommunicationService service;

    public BluetoothConnectReceiver(DeviceCommunicationService service2) {
        this.service = service2;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.bluetooth.device.action.ACL_CONNECTED") && intent.hasExtra("android.bluetooth.device.extra.DEVICE")) {
            BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            Logger logger = LOG;
            logger.info("connection attempt detected from or to " + device.getAddress() + "(" + device.getName() + ")");
            GBDevice gbDevice = this.service.getGBDevice();
            if (gbDevice != null && device.getAddress().equals(gbDevice.getAddress()) && gbDevice.getState() == GBDevice.State.WAITING_FOR_RECONNECT) {
                Logger logger2 = LOG;
                logger2.info("Will re-connect to " + gbDevice.getAddress() + "(" + gbDevice.getName() + ")");
                GBApplication.deviceService().connect();
            }
        }
    }
}
