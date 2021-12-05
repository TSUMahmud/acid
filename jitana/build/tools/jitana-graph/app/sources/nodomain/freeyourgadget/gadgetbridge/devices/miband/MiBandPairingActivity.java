package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractGBActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.util.AndroidUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiBandPairingActivity extends AbstractGBActivity {
    private static final long DELAY_AFTER_BONDING = 1000;
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) MiBandPairingActivity.class);
    private static final int REQ_CODE_USER_SETTINGS = 52;
    private static final String STATE_DEVICE_CANDIDATE = "stateDeviceCandidate";
    /* access modifiers changed from: private */
    public String bondingMacAddress;
    /* access modifiers changed from: private */
    public GBDeviceCandidate deviceCandidate;
    private boolean isPairing;
    private final BroadcastReceiver mBondingReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(intent.getAction())) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                Logger access$000 = MiBandPairingActivity.LOG;
                access$000.info("Bond state changed: " + device + ", state: " + device.getBondState() + ", expected address: " + MiBandPairingActivity.this.bondingMacAddress);
                if (MiBandPairingActivity.this.bondingMacAddress != null && MiBandPairingActivity.this.bondingMacAddress.equals(device.getAddress())) {
                    int bondState = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", 10);
                    if (bondState == 12) {
                        Logger access$0002 = MiBandPairingActivity.LOG;
                        access$0002.info("Bonded with " + device.getAddress());
                        String unused = MiBandPairingActivity.this.bondingMacAddress = null;
                        MiBandPairingActivity.this.attemptToConnect();
                    } else if (bondState == 11) {
                        Logger access$0003 = MiBandPairingActivity.LOG;
                        access$0003.info("Bonding in progress with " + device.getAddress());
                    } else if (bondState == 10) {
                        Logger access$0004 = MiBandPairingActivity.LOG;
                        access$0004.info("Not bonded with " + device.getAddress() + ", attempting to connect anyway.");
                        String unused2 = MiBandPairingActivity.this.bondingMacAddress = null;
                        MiBandPairingActivity.this.attemptToConnect();
                    } else {
                        Logger access$0005 = MiBandPairingActivity.LOG;
                        access$0005.warn("Unknown bond state for device " + device.getAddress() + ": " + bondState);
                        MiBandPairingActivity miBandPairingActivity = MiBandPairingActivity.this;
                        miBandPairingActivity.pairingFinished(false, miBandPairingActivity.deviceCandidate);
                    }
                }
            }
        }
    };
    private final BroadcastReceiver mPairingReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (GBDevice.ACTION_DEVICE_CHANGED.equals(intent.getAction())) {
                GBDevice device = (GBDevice) intent.getParcelableExtra(GBDevice.EXTRA_DEVICE);
                Logger access$000 = MiBandPairingActivity.LOG;
                access$000.debug("pairing activity: device changed: " + device);
                if (!MiBandPairingActivity.this.deviceCandidate.getMacAddress().equals(device.getAddress())) {
                    return;
                }
                if (device.isInitialized()) {
                    MiBandPairingActivity miBandPairingActivity = MiBandPairingActivity.this;
                    miBandPairingActivity.pairingFinished(true, miBandPairingActivity.deviceCandidate);
                } else if (device.isConnecting() || device.isInitializing()) {
                    MiBandPairingActivity.LOG.info("still connecting/initializing device...");
                }
            }
        }
    };
    private TextView message;

    /* access modifiers changed from: private */
    public void attemptToConnect() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                MiBandPairingActivity.this.performApplicationLevelPair();
            }
        }, 1000);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0074, code lost:
        r3 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getDeviceSpecificSharedPrefs(r2.getAddress());
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r11) {
        /*
            r10 = this;
            super.onCreate(r11)
            r0 = 2131492913(0x7f0c0031, float:1.8609291E38)
            r10.setContentView((int) r0)
            r0 = 2131296572(0x7f09013c, float:1.8211064E38)
            android.view.View r0 = r10.findViewById(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            r10.message = r0
            android.content.Intent r0 = r10.getIntent()
            java.lang.String r1 = "nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate.EXTRA_DEVICE_CANDIDATE"
            android.os.Parcelable r1 = r0.getParcelableExtra(r1)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate r1 = (nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate) r1
            r10.deviceCandidate = r1
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate r1 = r10.deviceCandidate
            if (r1 != 0) goto L_0x0032
            if (r11 == 0) goto L_0x0032
            java.lang.String r1 = "stateDeviceCandidate"
            android.os.Parcelable r1 = r11.getParcelable(r1)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate r1 = (nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate) r1
            r10.deviceCandidate = r1
        L_0x0032:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate r1 = r10.deviceCandidate
            if (r1 != 0) goto L_0x0059
            r1 = 2131755375(0x7f10016f, float:1.9141628E38)
            java.lang.String r1 = r10.getString(r1)
            r2 = 0
            android.widget.Toast r1 = android.widget.Toast.makeText(r10, r1, r2)
            r1.show()
            android.content.Intent r1 = new android.content.Intent
            java.lang.Class<nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity> r2 = nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity.class
            r1.<init>(r10, r2)
            r2 = 67108864(0x4000000, float:1.5046328E-36)
            android.content.Intent r1 = r1.setFlags(r2)
            r10.startActivity(r1)
            r10.finish()
            return
        L_0x0059:
            nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper r1 = nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper.getInstance()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate r2 = r10.deviceCandidate
            nodomain.freeyourgadget.gadgetbridge.devices.DeviceCoordinator r1 = r1.getCoordinator((nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate) r2)
            nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper r2 = nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper.getInstance()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate r3 = r10.deviceCandidate
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r2 = r2.toSupportedDevice((nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate) r3)
            int[] r3 = r1.getSupportedDeviceSpecificSettings(r2)
            r4 = 0
            if (r3 == 0) goto L_0x009b
            java.lang.String r3 = r2.getAddress()
            android.content.SharedPreferences r3 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getDeviceSpecificSharedPrefs(r3)
            java.lang.String r5 = "authkey"
            java.lang.String r6 = r3.getString(r5, r4)
            if (r6 == 0) goto L_0x008a
            boolean r7 = r6.isEmpty()
            if (r7 == 0) goto L_0x009b
        L_0x008a:
            android.content.SharedPreferences$Editor r7 = r3.edit()
            r8 = 16
            r9 = 1
            java.lang.String r8 = org.apache.commons.lang3.RandomStringUtils.random(r8, r9, r9)
            r7.putString(r5, r8)
            r7.apply()
        L_0x009b:
            boolean r3 = nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandCoordinator.hasValidUserInfo()
            if (r3 != 0) goto L_0x00ae
            android.content.Intent r3 = new android.content.Intent
            java.lang.Class<nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandPreferencesActivity> r5 = nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandPreferencesActivity.class
            r3.<init>(r10, r5)
            r5 = 52
            r10.startActivityForResult(r3, r5, r4)
            return
        L_0x00ae:
            r10.startPairing()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandPairingActivity.onCreate(android.os.Bundle):void");
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 52) {
            if (!MiBandCoordinator.hasValidUserInfo()) {
                C1238GB.toast((Context) this, getString(C0889R.string.miband_pairing_using_dummy_userdata), 1, 2);
            }
            startPairing();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        AndroidUtils.safeUnregisterBroadcastReceiver(LocalBroadcastManager.getInstance(this), this.mPairingReceiver);
        AndroidUtils.safeUnregisterBroadcastReceiver((Context) this, this.mBondingReceiver);
        if (this.isPairing) {
            stopPairing();
        }
        super.onDestroy();
    }

    private void startPairing() {
        this.isPairing = true;
        this.message.setText(getString(C0889R.string.pairing, new Object[]{this.deviceCandidate}));
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mPairingReceiver, new IntentFilter(GBDevice.ACTION_DEVICE_CHANGED));
        if (!shouldSetupBTLevelPairing()) {
            attemptToConnect();
            return;
        }
        registerReceiver(this.mBondingReceiver, new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED"));
        performBluetoothPair(this.deviceCandidate);
    }

    private boolean shouldSetupBTLevelPairing() {
        return GBApplication.getPrefs().getPreferences().getBoolean(MiBandConst.PREF_MIBAND_SETUP_BT_PAIRING, true);
    }

    /* access modifiers changed from: private */
    public void pairingFinished(boolean pairedSuccessfully, GBDeviceCandidate candidate) {
        Logger logger = LOG;
        logger.debug("pairingFinished: " + pairedSuccessfully);
        if (this.isPairing) {
            this.isPairing = false;
            AndroidUtils.safeUnregisterBroadcastReceiver(LocalBroadcastManager.getInstance(this), this.mPairingReceiver);
            AndroidUtils.safeUnregisterBroadcastReceiver((Context) this, this.mBondingReceiver);
            if (pairedSuccessfully) {
                String macAddress = this.deviceCandidate.getMacAddress();
                BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(macAddress);
                if (device != null && device.getBondState() == 10) {
                    GBApplication.getPrefs().getPreferences().edit().putString(MiBandConst.PREF_MIBAND_ADDRESS, macAddress).apply();
                }
                startActivity(new Intent(this, ControlCenterv2.class).setFlags(67108864));
            }
            finish();
        }
    }

    private void stopPairing() {
        this.isPairing = false;
    }

    /* access modifiers changed from: protected */
    public void performBluetoothPair(GBDeviceCandidate deviceCandidate2) {
        BluetoothDevice device = deviceCandidate2.getDevice();
        int bondState = device.getBondState();
        if (bondState == 12) {
            C1238GB.toast(getString(C0889R.string.pairing_already_bonded, new Object[]{device.getName(), device.getAddress()}), 0, 1);
            performApplicationLevelPair();
            return;
        }
        this.bondingMacAddress = device.getAddress();
        if (bondState == 11) {
            C1238GB.toast((Context) this, getString(C0889R.string.pairing_in_progress, new Object[]{device.getName(), this.bondingMacAddress}), 1, 1);
            return;
        }
        C1238GB.toast((Context) this, getString(C0889R.string.pairing_creating_bond_with, new Object[]{device.getName(), this.bondingMacAddress}), 1, 1);
        if (!device.createBond()) {
            C1238GB.toast((Context) this, getString(C0889R.string.pairing_unable_to_pair_with, new Object[]{device.getName(), this.bondingMacAddress}), 1, 3);
        }
    }

    /* access modifiers changed from: private */
    public void performApplicationLevelPair() {
        GBApplication.deviceService().disconnect();
        GBDevice device = DeviceHelper.getInstance().toSupportedDevice(this.deviceCandidate);
        if (device != null) {
            GBApplication.deviceService().connect(device, true);
            return;
        }
        C1238GB.toast((Context) this, "Unable to connect, can't recognize the device type: " + this.deviceCandidate, 1, 3);
    }
}
