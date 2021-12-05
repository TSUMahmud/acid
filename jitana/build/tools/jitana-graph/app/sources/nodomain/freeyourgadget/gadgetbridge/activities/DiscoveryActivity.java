package nodomain.freeyourgadget.gadgetbridge.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.devicesettings.DeviceSettingsActivity;
import nodomain.freeyourgadget.gadgetbridge.adapter.DeviceCandidateAdapter;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscoveryActivity extends AbstractGBActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) DiscoveryActivity.class);
    private static final long SCAN_DURATION = 60000;
    private BluetoothAdapter adapter;
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r9, android.content.Intent r10) {
            /*
                r8 = this;
                java.lang.String r0 = r10.getAction()
                java.lang.Object r0 = java.util.Objects.requireNonNull(r0)
                java.lang.String r0 = (java.lang.String) r0
                int r1 = r0.hashCode()
                r2 = 5
                r3 = 4
                r4 = 3
                r5 = 2
                r6 = 1
                r7 = 0
                switch(r1) {
                    case -1780914469: goto L_0x004a;
                    case -1530327060: goto L_0x0040;
                    case -377527494: goto L_0x0036;
                    case 6759640: goto L_0x002c;
                    case 1167529923: goto L_0x0022;
                    case 2116862345: goto L_0x0018;
                    default: goto L_0x0017;
                }
            L_0x0017:
                goto L_0x0054
            L_0x0018:
                java.lang.String r1 = "android.bluetooth.device.action.BOND_STATE_CHANGED"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0017
                r0 = 5
                goto L_0x0055
            L_0x0022:
                java.lang.String r1 = "android.bluetooth.device.action.FOUND"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0017
                r0 = 3
                goto L_0x0055
            L_0x002c:
                java.lang.String r1 = "android.bluetooth.adapter.action.DISCOVERY_STARTED"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0017
                r0 = 0
                goto L_0x0055
            L_0x0036:
                java.lang.String r1 = "android.bluetooth.device.action.UUID"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0017
                r0 = 4
                goto L_0x0055
            L_0x0040:
                java.lang.String r1 = "android.bluetooth.adapter.action.STATE_CHANGED"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0017
                r0 = 2
                goto L_0x0055
            L_0x004a:
                java.lang.String r1 = "android.bluetooth.adapter.action.DISCOVERY_FINISHED"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0017
                r0 = 1
                goto L_0x0055
            L_0x0054:
                r0 = -1
            L_0x0055:
                if (r0 == 0) goto L_0x00e2
                if (r0 == r6) goto L_0x00d3
                r1 = 10
                if (r0 == r5) goto L_0x00c7
                java.lang.String r5 = "android.bluetooth.device.extra.RSSI"
                java.lang.String r6 = "android.bluetooth.device.extra.DEVICE"
                if (r0 == r4) goto L_0x00b7
                if (r0 == r3) goto L_0x009d
                if (r0 == r2) goto L_0x0069
                goto L_0x00fd
            L_0x0069:
                android.os.Parcelable r0 = r10.getParcelableExtra(r6)
                android.bluetooth.BluetoothDevice r0 = (android.bluetooth.BluetoothDevice) r0
                if (r0 == 0) goto L_0x00fd
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity r2 = nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity.this
                nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate r2 = r2.bondingDevice
                if (r2 == 0) goto L_0x00fd
                java.lang.String r2 = r0.getAddress()
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity r3 = nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity.this
                nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate r3 = r3.bondingDevice
                java.lang.String r3 = r3.getMacAddress()
                boolean r2 = r2.equals(r3)
                if (r2 == 0) goto L_0x00fd
                java.lang.String r2 = "android.bluetooth.device.extra.BOND_STATE"
                int r1 = r10.getIntExtra(r2, r1)
                r2 = 12
                if (r1 != r2) goto L_0x00fd
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity r2 = nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity.this
                r2.handleDeviceBonded()
                goto L_0x00fd
            L_0x009d:
                android.os.Parcelable r0 = r10.getParcelableExtra(r6)
                android.bluetooth.BluetoothDevice r0 = (android.bluetooth.BluetoothDevice) r0
                short r1 = r10.getShortExtra(r5, r7)
                java.lang.String r2 = "android.bluetooth.device.extra.UUID"
                android.os.Parcelable[] r2 = r10.getParcelableArrayExtra(r2)
                android.os.ParcelUuid[] r3 = nodomain.freeyourgadget.gadgetbridge.util.AndroidUtils.toParcelUuids(r2)
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity r4 = nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity.this
                boolean unused = r4.handleDeviceFound(r0, r1, r3)
                goto L_0x00fd
            L_0x00b7:
                android.os.Parcelable r0 = r10.getParcelableExtra(r6)
                android.bluetooth.BluetoothDevice r0 = (android.bluetooth.BluetoothDevice) r0
                short r1 = r10.getShortExtra(r5, r7)
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity r2 = nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity.this
                r2.handleDeviceFound(r0, r1)
                goto L_0x00fd
            L_0x00c7:
                java.lang.String r0 = "android.bluetooth.adapter.extra.STATE"
                int r0 = r10.getIntExtra(r0, r1)
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity r1 = nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity.this
                r1.bluetoothStateChanged(r0)
                goto L_0x00fd
            L_0x00d3:
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity r0 = nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity.this
                android.os.Handler r0 = r0.handler
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity$1$1 r1 = new nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity$1$1
                r1.<init>()
                r0.post(r1)
                goto L_0x00fd
            L_0x00e2:
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity r0 = nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity.this
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity$Scanning r0 = r0.isScanning
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity$Scanning r1 = nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity.Scanning.SCANNING_BTLE
                if (r0 == r1) goto L_0x00fd
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity r0 = nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity.this
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity$Scanning r0 = r0.isScanning
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity$Scanning r1 = nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity.Scanning.SCANNING_NEW_BTLE
                if (r0 == r1) goto L_0x00fd
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity r0 = nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity.this
                nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity$Scanning r1 = nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity.Scanning.SCANNING_BT
                r0.discoveryStarted(r1)
            L_0x00fd:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.DiscoveryActivity.C09511.onReceive(android.content.Context, android.content.Intent):void");
        }
    };
    /* access modifiers changed from: private */
    public GBDeviceCandidate bondingDevice;
    private DeviceCandidateAdapter cadidateListAdapter;
    private final ArrayList<GBDeviceCandidate> deviceCandidates = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean disableNewBLEScanning = false;
    /* access modifiers changed from: private */
    public final Handler handler = new Handler();
    /* access modifiers changed from: private */
    public Scanning isScanning = Scanning.SCANNING_OFF;
    private final BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            DiscoveryActivity.this.handleDeviceFound(device, (short) rssi);
        }
    };
    private ScanCallback newLeScanCallback = null;
    private ProgressBar progressView;
    /* access modifiers changed from: private */
    public Button startButton;
    private final Runnable stopRunnable = new Runnable() {
        public void run() {
            DiscoveryActivity.this.stopDiscovery();
        }
    };

    private enum Scanning {
        SCANNING_BT,
        SCANNING_BTLE,
        SCANNING_NEW_BTLE,
        SCANNING_OFF
    }

    /* access modifiers changed from: private */
    public void connectAndFinish(GBDevice device) {
        C1238GB.toast((Context) this, getString(C0889R.string.discovery_trying_to_connect_to, new Object[]{device.getName()}), 0, 1);
        GBApplication.deviceService().connect(device, true);
        finish();
    }

    private void createBond(final GBDeviceCandidate deviceCandidate, int bondingStyle) {
        if (bondingStyle != 0) {
            if (bondingStyle == 2) {
                new AlertDialog.Builder(this).setCancelable(true).setTitle(getString(C0889R.string.discovery_pair_title, new Object[]{deviceCandidate.getName()})).setMessage(getString(C0889R.string.discovery_pair_question)).setPositiveButton(getString(C0889R.string.discovery_yes_pair), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DiscoveryActivity.this.doCreatePair(deviceCandidate);
                    }
                }).setNegativeButton(C0889R.string.discovery_dont_pair, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DiscoveryActivity.this.connectAndFinish(DeviceHelper.getInstance().toSupportedDevice(deviceCandidate));
                    }
                }).show();
                return;
            }
            doCreatePair(deviceCandidate);
        }
    }

    /* access modifiers changed from: private */
    public void doCreatePair(GBDeviceCandidate deviceCandidate) {
        C1238GB.toast((Context) this, getString(C0889R.string.discovery_attempting_to_pair, new Object[]{deviceCandidate.getName()}), 0, 1);
        if (deviceCandidate.getDevice().createBond()) {
            LOG.info("Bonding in progress...");
            this.bondingDevice = deviceCandidate;
            return;
        }
        C1238GB.toast((Context) this, getString(C0889R.string.discovery_bonding_failed_immediately, new Object[]{deviceCandidate.getName()}), 0, 3);
    }

    /* access modifiers changed from: private */
    public void handleDeviceBonded() {
        C1238GB.toast((Context) this, getString(C0889R.string.discovery_successfully_bonded, new Object[]{this.bondingDevice.getName()}), 0, 1);
        connectAndFinish(DeviceHelper.getInstance().toSupportedDevice(this.bondingDevice));
    }

    private ScanCallback getScanCallback() {
        if (Build.VERSION.SDK_INT >= 21) {
            this.newLeScanCallback = new ScanCallback() {
                public void onScanResult(int callbackType, ScanResult result) {
                    List<ParcelUuid> serviceUuids;
                    super.onScanResult(callbackType, result);
                    try {
                        ScanRecord scanRecord = result.getScanRecord();
                        ParcelUuid[] uuids = null;
                        if (!(scanRecord == null || (serviceUuids = scanRecord.getServiceUuids()) == null)) {
                            uuids = (ParcelUuid[]) serviceUuids.toArray(new ParcelUuid[0]);
                        }
                        Logger access$1400 = DiscoveryActivity.LOG;
                        StringBuilder sb = new StringBuilder();
                        sb.append(result.getDevice().getName());
                        sb.append(": ");
                        sb.append(scanRecord != null ? scanRecord.getBytes().length : -1);
                        access$1400.warn(sb.toString());
                        boolean unused = DiscoveryActivity.this.handleDeviceFound(result.getDevice(), (short) result.getRssi(), uuids);
                    } catch (NullPointerException e) {
                        DiscoveryActivity.LOG.warn("Error handling scan result", (Throwable) e);
                    }
                }
            };
        }
        return this.newLeScanCallback;
    }

    public void logMessageContent(byte[] value) {
        if (value != null) {
            Logger logger = LOG;
            logger.warn("DATA: " + C1238GB.hexdump(value, 0, value.length));
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.disableNewBLEScanning = GBApplication.getPrefs().getBoolean("disable_new_ble_scanning", false);
        if (this.disableNewBLEScanning) {
            LOG.info("new BLE scanning disabled via settings, using old method");
        }
        setContentView((int) C0889R.layout.activity_discovery);
        this.startButton = (Button) findViewById(C0889R.C0891id.discovery_start);
        this.startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DiscoveryActivity discoveryActivity = DiscoveryActivity.this;
                discoveryActivity.onStartButtonClick(discoveryActivity.startButton);
            }
        });
        this.progressView = (ProgressBar) findViewById(C0889R.C0891id.discovery_progressbar);
        this.progressView.setProgress(0);
        this.progressView.setIndeterminate(true);
        this.progressView.setVisibility(8);
        ListView deviceCandidatesView = (ListView) findViewById(C0889R.C0891id.discovery_deviceCandidatesView);
        this.cadidateListAdapter = new DeviceCandidateAdapter(this, this.deviceCandidates);
        deviceCandidatesView.setAdapter(this.cadidateListAdapter);
        deviceCandidatesView.setOnItemClickListener(this);
        deviceCandidatesView.setOnItemLongClickListener(this);
        IntentFilter bluetoothIntents = new IntentFilter();
        bluetoothIntents.addAction("android.bluetooth.device.action.FOUND");
        bluetoothIntents.addAction("android.bluetooth.device.action.UUID");
        bluetoothIntents.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
        bluetoothIntents.addAction("android.bluetooth.adapter.action.DISCOVERY_STARTED");
        bluetoothIntents.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        bluetoothIntents.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        registerReceiver(this.bluetoothReceiver, bluetoothIntents);
        startDiscovery();
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("deviceCandidates", this.deviceCandidates);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Parcelable> restoredCandidates = savedInstanceState.getParcelableArrayList("deviceCandidates");
        if (restoredCandidates != null) {
            this.deviceCandidates.clear();
            Iterator<Parcelable> it = restoredCandidates.iterator();
            while (it.hasNext()) {
                this.deviceCandidates.add((GBDeviceCandidate) it.next());
            }
        }
    }

    public void onStartButtonClick(View button) {
        LOG.debug("Start Button clicked");
        if (isScanning()) {
            stopDiscovery();
        } else {
            startDiscovery();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        try {
            unregisterReceiver(this.bluetoothReceiver);
        } catch (IllegalArgumentException e) {
            LOG.warn("Tried to unregister Bluetooth Receiver that wasn't registered.");
        }
        super.onDestroy();
    }

    /* access modifiers changed from: private */
    public void handleDeviceFound(BluetoothDevice device, short rssi) {
        if (device.getName() == null || !handleDeviceFound(device, rssi, (ParcelUuid[]) null)) {
            ParcelUuid[] uuids = device.getUuids();
            if (uuids != null || !device.fetchUuidsWithSdp()) {
                handleDeviceFound(device, rssi, uuids);
                return;
            }
            return;
        }
        Logger logger = LOG;
        logger.info("found supported device " + device.getName() + " without scanning services, skipping service scan.");
    }

    /* access modifiers changed from: private */
    public boolean handleDeviceFound(BluetoothDevice device, short rssi, ParcelUuid[] uuids) {
        LOG.debug("found device: " + device.getName() + ", " + device.getAddress());
        if (LOG.isDebugEnabled() && uuids != null && uuids.length > 0) {
            for (ParcelUuid uuid : uuids) {
                LOG.debug("  supports uuid: " + uuid.toString());
            }
        }
        if (device.getBondState() == 12) {
            return true;
        }
        GBDeviceCandidate candidate = new GBDeviceCandidate(device, rssi, uuids);
        DeviceType deviceType = DeviceHelper.getInstance().getSupportedType(candidate);
        if (!deviceType.isSupported()) {
            return false;
        }
        candidate.setDeviceType(deviceType);
        LOG.info("Recognized supported device: " + candidate);
        int index = this.deviceCandidates.indexOf(candidate);
        if (index >= 0) {
            this.deviceCandidates.set(index, candidate);
        } else {
            this.deviceCandidates.add(candidate);
        }
        this.cadidateListAdapter.notifyDataSetChanged();
        return true;
    }

    private void startDiscovery() {
        if (isScanning()) {
            LOG.warn("Not starting discovery, because already scanning.");
        } else {
            startDiscovery(Scanning.SCANNING_BT);
        }
    }

    /* access modifiers changed from: private */
    public void startDiscovery(Scanning what) {
        Logger logger = LOG;
        logger.info("Starting discovery: " + what);
        discoveryStarted(what);
        if (!ensureBluetoothReady()) {
            discoveryFinished();
            C1238GB.toast((Context) this, getString(C0889R.string.discovery_enable_bluetooth), 0, 3);
        } else if (what == Scanning.SCANNING_BT) {
            startBTDiscovery();
        } else if (what == Scanning.SCANNING_BTLE) {
            if (C1238GB.supportsBluetoothLE()) {
                startBTLEDiscovery();
            } else {
                discoveryFinished();
            }
        } else if (what != Scanning.SCANNING_NEW_BTLE) {
        } else {
            if (C1238GB.supportsBluetoothLE()) {
                startNEWBTLEDiscovery();
            } else {
                discoveryFinished();
            }
        }
    }

    private boolean isScanning() {
        return this.isScanning != Scanning.SCANNING_OFF;
    }

    /* access modifiers changed from: private */
    public void stopDiscovery() {
        LOG.info("Stopping discovery");
        if (isScanning()) {
            Scanning wasScanning = this.isScanning;
            discoveryFinished();
            if (wasScanning == Scanning.SCANNING_BT) {
                stopBTDiscovery();
            } else if (wasScanning == Scanning.SCANNING_BTLE) {
                stopBTLEDiscovery();
            } else if (wasScanning == Scanning.SCANNING_NEW_BTLE) {
                stopNewBTLEDiscovery();
            }
            this.handler.removeMessages(0, this.stopRunnable);
        }
    }

    private void stopBTLEDiscovery() {
        BluetoothAdapter bluetoothAdapter = this.adapter;
        if (bluetoothAdapter != null) {
            bluetoothAdapter.stopLeScan(this.leScanCallback);
        }
    }

    private void stopBTDiscovery() {
        BluetoothAdapter bluetoothAdapter = this.adapter;
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    private void stopNewBTLEDiscovery() {
        BluetoothAdapter bluetoothAdapter = this.adapter;
        if (bluetoothAdapter != null) {
            BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothLeScanner == null) {
                LOG.warn("could not get BluetoothLeScanner()!");
                return;
            }
            ScanCallback scanCallback = this.newLeScanCallback;
            if (scanCallback == null) {
                LOG.warn("newLeScanCallback == null!");
            } else {
                bluetoothLeScanner.stopScan(scanCallback);
            }
        }
    }

    /* access modifiers changed from: private */
    public void bluetoothStateChanged(int newState) {
        discoveryFinished();
        if (newState == 12) {
            this.adapter = BluetoothAdapter.getDefaultAdapter();
            this.startButton.setEnabled(true);
            return;
        }
        this.adapter = null;
        this.startButton.setEnabled(false);
    }

    /* access modifiers changed from: private */
    public void discoveryFinished() {
        this.isScanning = Scanning.SCANNING_OFF;
        this.progressView.setVisibility(8);
        this.startButton.setText(getString(C0889R.string.discovery_start_scanning));
    }

    /* access modifiers changed from: private */
    public void discoveryStarted(Scanning what) {
        this.isScanning = what;
        this.progressView.setVisibility(0);
        this.startButton.setText(getString(C0889R.string.discovery_stop_scanning));
    }

    private boolean ensureBluetoothReady() {
        boolean available = checkBluetoothAvailable();
        this.startButton.setEnabled(available);
        if (!available) {
            return false;
        }
        this.adapter.cancelDiscovery();
        return true;
    }

    private boolean checkBluetoothAvailable() {
        BluetoothManager bluetoothService = (BluetoothManager) getSystemService("bluetooth");
        if (bluetoothService == null) {
            LOG.warn("No bluetooth available");
            this.adapter = null;
            return false;
        }
        BluetoothAdapter adapter2 = bluetoothService.getAdapter();
        if (adapter2 == null) {
            LOG.warn("No bluetooth available");
            this.adapter = null;
            return false;
        } else if (!adapter2.isEnabled()) {
            LOG.warn("Bluetooth not enabled");
            startActivity(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"));
            this.adapter = null;
            return false;
        } else {
            this.adapter = adapter2;
            return true;
        }
    }

    private void startNEWBTLEDiscovery() {
        LOG.info("Start New BTLE Discovery");
        this.handler.removeMessages(0, this.stopRunnable);
        this.handler.sendMessageDelayed(getPostMessage(this.stopRunnable), 60000);
        this.adapter.getBluetoothLeScanner().startScan(getScanFilters(), getScanSettings(), getScanCallback());
    }

    private List<ScanFilter> getScanFilters() {
        List<ScanFilter> allFilters = new ArrayList<>();
        for (DeviceCoordinator coordinator : DeviceHelper.getInstance().getAllCoordinators()) {
            allFilters.addAll(coordinator.createBLEScanFilters());
        }
        return allFilters;
    }

    private ScanSettings getScanSettings() {
        if (Build.VERSION.SDK_INT >= 23) {
            return new ScanSettings.Builder().setScanMode(2).setMatchMode(2).build();
        }
        return new ScanSettings.Builder().setScanMode(2).build();
    }

    private void startBTLEDiscovery() {
        LOG.info("Starting BTLE Discovery");
        this.handler.removeMessages(0, this.stopRunnable);
        this.handler.sendMessageDelayed(getPostMessage(this.stopRunnable), 60000);
        this.adapter.startLeScan(this.leScanCallback);
    }

    private void startBTDiscovery() {
        LOG.info("Starting BT Discovery");
        this.handler.removeMessages(0, this.stopRunnable);
        this.handler.sendMessageDelayed(getPostMessage(this.stopRunnable), 60000);
        this.adapter.startDiscovery();
    }

    /* access modifiers changed from: private */
    public void checkAndRequestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_COARSE_LOCATION"}, 0);
        }
    }

    private Message getPostMessage(Runnable runnable) {
        Message m = Message.obtain(this.handler, runnable);
        m.obj = runnable;
        return m;
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        GBDeviceCandidate deviceCandidate = this.deviceCandidates.get(position);
        if (deviceCandidate == null) {
            LOG.error("Device candidate clicked, but item not found");
            return true;
        }
        DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(deviceCandidate);
        GBDevice device = DeviceHelper.getInstance().toSupportedDevice(deviceCandidate);
        if (coordinator.getSupportedDeviceSpecificSettings(device) == null) {
            return true;
        }
        Intent startIntent = new Intent(this, DeviceSettingsActivity.class);
        startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
        startActivity(startIntent);
        return true;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String authKey;
        GBDeviceCandidate deviceCandidate = this.deviceCandidates.get(position);
        if (deviceCandidate == null) {
            LOG.error("Device candidate clicked, but item not found");
            return;
        }
        stopDiscovery();
        DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(deviceCandidate);
        Logger logger = LOG;
        logger.info("Using device candidate " + deviceCandidate + " with coordinator: " + coordinator.getClass());
        if (coordinator.getBondingStyle() != 3 || ((authKey = GBApplication.getDeviceSpecificSharedPrefs(deviceCandidate.getMacAddress()).getString("authkey", (String) null)) != null && !authKey.isEmpty() && authKey.getBytes().length >= 34 && authKey.substring(0, 2).equals("0x"))) {
            Class<? extends Activity> pairingActivity = coordinator.getPairingActivity();
            if (pairingActivity != null) {
                Intent intent = new Intent(this, pairingActivity);
                intent.putExtra(DeviceCoordinator.EXTRA_DEVICE_CANDIDATE, deviceCandidate);
                startActivity(intent);
                return;
            }
            GBDevice device = DeviceHelper.getInstance().toSupportedDevice(deviceCandidate);
            int bondingStyle = coordinator.getBondingStyle();
            if (bondingStyle == 0) {
                LOG.info("No bonding needed, according to coordinator, so connecting right away");
                connectAndFinish(device);
                return;
            }
            try {
                switch (this.adapter.getRemoteDevice(deviceCandidate.getMacAddress()).getBondState()) {
                    case 10:
                        createBond(deviceCandidate, bondingStyle);
                        return;
                    case 11:
                        this.bondingDevice = deviceCandidate;
                        return;
                    case 12:
                        handleDeviceBonded();
                        return;
                    default:
                        return;
                }
            } catch (Exception e) {
                Logger logger2 = LOG;
                logger2.error("Error pairing device: " + deviceCandidate.getMacAddress());
            }
        } else {
            C1238GB.toast((Context) this, getString(C0889R.string.discovery_need_to_enter_authkey), 1, 2);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        stopBTDiscovery();
        stopBTLEDiscovery();
        if (GBApplication.isRunningLollipopOrLater() && !this.disableNewBLEScanning) {
            stopNewBTLEDiscovery();
        }
    }
}
