package nodomain.freeyourgadget.gadgetbridge.devices.watch9;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractGBActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2;
import nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.util.AndroidUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Watch9PairingActivity extends AbstractGBActivity {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) Watch9PairingActivity.class);
    private static final String STATE_DEVICE_CANDIDATE = "stateDeviceCandidate";
    /* access modifiers changed from: private */
    public GBDeviceCandidate deviceCandidate;
    private final BroadcastReceiver mPairingReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (GBDevice.ACTION_DEVICE_CHANGED.equals(intent.getAction())) {
                GBDevice device = (GBDevice) intent.getParcelableExtra(GBDevice.EXTRA_DEVICE);
                Logger access$000 = Watch9PairingActivity.LOG;
                access$000.debug("pairing activity: device changed: " + device);
                if (!Watch9PairingActivity.this.deviceCandidate.getMacAddress().equals(device.getAddress())) {
                    return;
                }
                if (device.isInitialized()) {
                    Watch9PairingActivity.this.pairingFinished();
                } else if (device.isConnecting() || device.isInitializing()) {
                    Watch9PairingActivity.LOG.info("still connecting/initializing device...");
                }
            }
        }
    };
    private TextView message;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_watch9_pairing);
        this.message = (TextView) findViewById(C0889R.C0891id.watch9_pair_message);
        this.deviceCandidate = (GBDeviceCandidate) getIntent().getParcelableExtra(DeviceCoordinator.EXTRA_DEVICE_CANDIDATE);
        if (this.deviceCandidate == null && savedInstanceState != null) {
            this.deviceCandidate = (GBDeviceCandidate) savedInstanceState.getParcelable(STATE_DEVICE_CANDIDATE);
        }
        if (this.deviceCandidate == null) {
            Toast.makeText(this, getString(C0889R.string.message_cannot_pair_no_mac), 0).show();
            startActivity(new Intent(this, DiscoveryActivity.class).setFlags(67108864));
            finish();
            return;
        }
        startPairing();
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_DEVICE_CANDIDATE, this.deviceCandidate);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.deviceCandidate = (GBDeviceCandidate) savedInstanceState.getParcelable(STATE_DEVICE_CANDIDATE);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        AndroidUtils.safeUnregisterBroadcastReceiver(LocalBroadcastManager.getInstance(this), this.mPairingReceiver);
        super.onDestroy();
    }

    private void startPairing() {
        this.message.setText(getString(C0889R.string.pairing, new Object[]{this.deviceCandidate}));
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mPairingReceiver, new IntentFilter(GBDevice.ACTION_DEVICE_CHANGED));
        GBApplication.deviceService().disconnect();
        GBDevice device = DeviceHelper.getInstance().toSupportedDevice(this.deviceCandidate);
        if (device != null) {
            GBApplication.deviceService().connect(device, true);
            return;
        }
        C1238GB.toast((Context) this, "Unable to connect, can't recognize the device type: " + this.deviceCandidate, 1, 3);
    }

    /* access modifiers changed from: private */
    public void pairingFinished() {
        AndroidUtils.safeUnregisterBroadcastReceiver(LocalBroadcastManager.getInstance(this), this.mPairingReceiver);
        startActivity(new Intent(this, ControlCenterv2.class).setFlags(67108864));
        finish();
    }
}
