package nodomain.freeyourgadget.gadgetbridge.devices.pebble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PebblePairingActivity extends AbstractGBActivity {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) PebblePairingActivity.class);
    /* access modifiers changed from: private */
    public boolean isLEPebble;
    private boolean isPairing;
    private final BroadcastReceiver mBondingReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(intent.getAction())) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                Logger access$000 = PebblePairingActivity.LOG;
                access$000.info("Bond state changed: " + device + ", state: " + device.getBondState() + ", expected address: " + PebblePairingActivity.this.macAddress);
                if (PebblePairingActivity.this.macAddress != null && PebblePairingActivity.this.macAddress.equals(device.getAddress())) {
                    int bondState = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", 10);
                    if (bondState == 12) {
                        Logger access$0002 = PebblePairingActivity.LOG;
                        access$0002.info("Bonded with " + device.getAddress());
                        if (!PebblePairingActivity.this.isLEPebble) {
                            PebblePairingActivity.this.performConnect((GBDevice) null);
                        }
                    } else if (bondState == 11) {
                        Logger access$0003 = PebblePairingActivity.LOG;
                        access$0003.info("Bonding in progress with " + device.getAddress());
                    } else if (bondState == 10) {
                        Logger access$0004 = PebblePairingActivity.LOG;
                        access$0004.info("Not bonded with " + device.getAddress() + ", attempting to connect anyway.");
                    } else {
                        Logger access$0005 = PebblePairingActivity.LOG;
                        access$0005.warn("Unknown bond state for device " + device.getAddress() + ": " + bondState);
                        PebblePairingActivity.this.pairingFinished(false);
                    }
                }
            }
        }
    };
    private BluetoothDevice mBtDevice;
    private final BroadcastReceiver mPairingReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (GBDevice.ACTION_DEVICE_CHANGED.equals(intent.getAction())) {
                GBDevice device = (GBDevice) intent.getParcelableExtra(GBDevice.EXTRA_DEVICE);
                Logger access$000 = PebblePairingActivity.LOG;
                access$000.debug("pairing activity: device changed: " + device);
                if (!PebblePairingActivity.this.macAddress.equals(device.getAddress()) && !PebblePairingActivity.this.macAddress.equals(device.getVolatileAddress())) {
                    return;
                }
                if (device.isInitialized()) {
                    PebblePairingActivity.this.pairingFinished(true);
                } else if (device.isConnecting() || device.isInitializing()) {
                    PebblePairingActivity.LOG.info("still connecting/initializing device...");
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public String macAddress;
    private TextView message;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_pebble_pairing);
        this.message = (TextView) findViewById(C0889R.C0891id.pebble_pair_message);
        GBDeviceCandidate candidate = (GBDeviceCandidate) getIntent().getParcelableExtra(DeviceCoordinator.EXTRA_DEVICE_CANDIDATE);
        if (candidate != null) {
            this.macAddress = candidate.getMacAddress();
        }
        if (this.macAddress == null) {
            Toast.makeText(this, getString(C0889R.string.message_cannot_pair_no_mac), 0).show();
            returnToPairingActivity();
            return;
        }
        this.mBtDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(this.macAddress);
        BluetoothDevice bluetoothDevice = this.mBtDevice;
        if (bluetoothDevice == null) {
            C1238GB.toast((Context) this, "No such Bluetooth Device: " + this.macAddress, 1, 3);
            returnToPairingActivity();
            return;
        }
        this.isLEPebble = bluetoothDevice.getType() == 2;
        GBDevice gbDevice = null;
        if (this.isLEPebble && (this.mBtDevice.getName().startsWith("Pebble-LE ") || this.mBtDevice.getName().startsWith("Pebble Time LE "))) {
            if (!GBApplication.getPrefs().getBoolean("pebble_force_le", false)) {
                C1238GB.toast((Context) this, "Please switch on \"Always prefer BLE\" option in Pebble settings before pairing you Pebble LE", 1, 3);
                returnToPairingActivity();
                return;
            }
            gbDevice = getMatchingParentDeviceFromDB(this.mBtDevice);
            if (gbDevice == null) {
                return;
            }
        }
        startPairing(gbDevice);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mPairingReceiver);
            unregisterReceiver(this.mBondingReceiver);
        } catch (IllegalArgumentException e) {
        }
        if (this.isPairing) {
            stopPairing();
        }
        super.onDestroy();
    }

    private void startPairing(GBDevice gbDevice) {
        this.isPairing = true;
        this.message.setText(getString(C0889R.string.pairing, new Object[]{this.macAddress}));
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mPairingReceiver, new IntentFilter(GBDevice.ACTION_DEVICE_CHANGED));
        registerReceiver(this.mBondingReceiver, new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED"));
        performPair(gbDevice);
    }

    /* access modifiers changed from: private */
    public void pairingFinished(boolean pairedSuccessfully) {
        Logger logger = LOG;
        logger.debug("pairingFinished: " + pairedSuccessfully);
        if (this.isPairing) {
            this.isPairing = false;
            LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mPairingReceiver);
            unregisterReceiver(this.mBondingReceiver);
            if (pairedSuccessfully) {
                startActivity(new Intent(this, ControlCenterv2.class).setFlags(67108864));
            }
            finish();
        }
    }

    private void stopPairing() {
        this.isPairing = false;
    }

    /* access modifiers changed from: protected */
    public void performPair(GBDevice gbDevice) {
        int bondState = this.mBtDevice.getBondState();
        if (bondState == 12) {
            C1238GB.toast(getString(C0889R.string.pairing_already_bonded, new Object[]{this.mBtDevice.getName(), this.mBtDevice.getAddress()}), 0, 1);
        } else if (bondState == 11) {
            C1238GB.toast((Context) this, getString(C0889R.string.pairing_in_progress, new Object[]{this.mBtDevice.getName(), this.macAddress}), 1, 1);
        } else {
            C1238GB.toast((Context) this, getString(C0889R.string.pairing_creating_bond_with, new Object[]{this.mBtDevice.getName(), this.macAddress}), 1, 1);
            GBApplication.deviceService().disconnect();
            if (this.isLEPebble) {
                performConnect(gbDevice);
            } else {
                this.mBtDevice.createBond();
            }
        }
    }

    /* access modifiers changed from: private */
    public void performConnect(GBDevice gbDevice) {
        if (gbDevice == null) {
            gbDevice = new GBDevice(this.mBtDevice.getAddress(), this.mBtDevice.getName(), DeviceType.PEBBLE);
        }
        GBApplication.deviceService().connect(gbDevice);
    }

    private void returnToPairingActivity() {
        startActivity(new Intent(this, DiscoveryActivity.class).setFlags(67108864));
        finish();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00d3, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00d4, code lost:
        if (r4 != null) goto L_0x00d6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00de, code lost:
        throw r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private nodomain.freeyourgadget.gadgetbridge.impl.GBDevice getMatchingParentDeviceFromDB(android.bluetooth.BluetoothDevice r15) {
        /*
            r14 = this;
            java.lang.String r0 = r15.getName()
            java.lang.String r1 = ""
            java.lang.String r2 = "Pebble-LE "
            java.lang.String r0 = r0.replace(r2, r1)
            java.lang.String r2 = "Pebble Time LE "
            java.lang.String r0 = r0.replace(r2, r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 2
            r3 = 0
            java.lang.String r4 = r0.substring(r3, r2)
            r1.append(r4)
            java.lang.String r4 = ":"
            r1.append(r4)
            java.lang.String r2 = r0.substring(r2)
            r1.append(r2)
            java.lang.String r0 = r1.toString()
            org.slf4j.Logger r1 = LOG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "will try to find a Pebble with BT address suffix "
            r2.append(r4)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            r1.info(r2)
            r1 = 0
            r2 = 0
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r4 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x00df }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r5 = r4.getDaoSession()     // Catch:{ all -> 0x00d1 }
            nodomain.freeyourgadget.gadgetbridge.entities.DeviceDao r6 = r5.getDeviceDao()     // Catch:{ all -> 0x00d1 }
            de.greenrobot.dao.query.QueryBuilder r7 = r6.queryBuilder()     // Catch:{ all -> 0x00d1 }
            de.greenrobot.dao.Property r8 = nodomain.freeyourgadget.gadgetbridge.entities.DeviceDao.Properties.Type     // Catch:{ all -> 0x00d1 }
            r9 = 1
            java.lang.Integer r10 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x00d1 }
            de.greenrobot.dao.query.WhereCondition r8 = r8.mo14989eq(r10)     // Catch:{ all -> 0x00d1 }
            de.greenrobot.dao.query.WhereCondition[] r10 = new p008de.greenrobot.dao.query.WhereCondition[r9]     // Catch:{ all -> 0x00d1 }
            de.greenrobot.dao.Property r11 = nodomain.freeyourgadget.gadgetbridge.entities.DeviceDao.Properties.Identifier     // Catch:{ all -> 0x00d1 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d1 }
            r12.<init>()     // Catch:{ all -> 0x00d1 }
            java.lang.String r13 = "%"
            r12.append(r13)     // Catch:{ all -> 0x00d1 }
            r12.append(r0)     // Catch:{ all -> 0x00d1 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x00d1 }
            de.greenrobot.dao.query.WhereCondition r11 = r11.like(r12)     // Catch:{ all -> 0x00d1 }
            r10[r3] = r11     // Catch:{ all -> 0x00d1 }
            de.greenrobot.dao.query.QueryBuilder r7 = r7.where(r8, r10)     // Catch:{ all -> 0x00d1 }
            de.greenrobot.dao.query.Query r7 = r7.build()     // Catch:{ all -> 0x00d1 }
            java.util.List r8 = r7.list()     // Catch:{ all -> 0x00d1 }
            int r10 = r8.size()     // Catch:{ all -> 0x00d1 }
            if (r10 != 0) goto L_0x009f
            java.lang.String r10 = "Please pair your non-LE Pebble before pairing the LE one"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r10, r3, r9)     // Catch:{ all -> 0x00d1 }
            r14.returnToPairingActivity()     // Catch:{ all -> 0x00d1 }
            if (r4 == 0) goto L_0x009e
            r4.close()     // Catch:{ Exception -> 0x00df }
        L_0x009e:
            return r2
        L_0x009f:
            int r10 = r8.size()     // Catch:{ all -> 0x00d1 }
            if (r10 <= r9) goto L_0x00b4
            java.lang.String r10 = "Can not match this Pebble LE to a unique device"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r10, r3, r9)     // Catch:{ all -> 0x00d1 }
            r14.returnToPairingActivity()     // Catch:{ all -> 0x00d1 }
            if (r4 == 0) goto L_0x00b3
            r4.close()     // Catch:{ Exception -> 0x00df }
        L_0x00b3:
            return r2
        L_0x00b4:
            nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper r9 = nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper.getInstance()     // Catch:{ all -> 0x00d1 }
            java.lang.Object r10 = r8.get(r3)     // Catch:{ all -> 0x00d1 }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r10 = (nodomain.freeyourgadget.gadgetbridge.entities.Device) r10     // Catch:{ all -> 0x00d1 }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r10 = r9.toGBDevice(r10)     // Catch:{ all -> 0x00d1 }
            r1 = r10
            java.lang.String r10 = r15.getAddress()     // Catch:{ all -> 0x00d1 }
            r1.setVolatileAddress(r10)     // Catch:{ all -> 0x00d1 }
            if (r4 == 0) goto L_0x00cf
            r4.close()     // Catch:{ Exception -> 0x00df }
        L_0x00cf:
            return r1
        L_0x00d1:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x00d3 }
        L_0x00d3:
            r6 = move-exception
            if (r4 == 0) goto L_0x00de
            r4.close()     // Catch:{ all -> 0x00da }
            goto L_0x00de
        L_0x00da:
            r7 = move-exception
            r5.addSuppressed(r7)     // Catch:{ Exception -> 0x00df }
        L_0x00de:
            throw r6     // Catch:{ Exception -> 0x00df }
        L_0x00df:
            r4 = move-exception
            r5 = 3
            java.lang.String r6 = "Error retrieving devices from database"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r6, r3, r5)
            r14.returnToPairingActivity()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebblePairingActivity.getMatchingParentDeviceFromDB(android.bluetooth.BluetoothDevice):nodomain.freeyourgadget.gadgetbridge.impl.GBDevice");
    }
}
