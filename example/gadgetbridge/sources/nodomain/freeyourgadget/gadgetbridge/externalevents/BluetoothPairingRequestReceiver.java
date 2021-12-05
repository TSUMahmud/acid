package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.DeviceCommunicationService;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluetoothPairingRequestReceiver extends BroadcastReceiver {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) BluetoothConnectReceiver.class);
    final DeviceCommunicationService service;

    public BluetoothPairingRequestReceiver(DeviceCommunicationService service2) {
        this.service = service2;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
            GBDevice gbDevice = this.service.getGBDevice();
            BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            if (gbDevice != null && device != null) {
                try {
                    if (DeviceHelper.getInstance().getCoordinator(gbDevice).getBondingStyle() == 0) {
                        LOG.info("Aborting unwanted pairing request");
                        abortBroadcast();
                    }
                } catch (Exception e) {
                    LOG.warn("Could not abort pairing request process");
                }
            }
        }
    }
}
