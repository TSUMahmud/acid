package nodomain.freeyourgadget.gadgetbridge.devices;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceManager {
    public static final String ACTION_DEVICES_CHANGED = "nodomain.freeyourgadget.gadgetbridge.devices.devicemanager.action.devices_changed";
    public static final String ACTION_REFRESH_DEVICELIST = "nodomain.freeyourgadget.gadgetbridge.devices.devicemanager.action.set_version";
    public static final String BLUETOOTH_DEVICE_ACTION_ALIAS_CHANGED = "android.bluetooth.device.action.ALIAS_CHANGED";
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DeviceManager.class);
    private final Context context;
    /* access modifiers changed from: private */
    public final List<GBDevice> deviceList = new ArrayList();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x0095, code lost:
            r5 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x0096, code lost:
            if (r3 != null) goto L_0x0098;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
            r3.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:48:0x00a0, code lost:
            throw r5;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r8, android.content.Intent r9) {
            /*
                r7 = this;
                java.lang.String r0 = r9.getAction()
                int r1 = r0.hashCode()
                r2 = 4
                r3 = 3
                r4 = 2
                r5 = 1
                switch(r1) {
                    case 889001322: goto L_0x0038;
                    case 1174571750: goto L_0x002e;
                    case 2047137119: goto L_0x0024;
                    case 2101976453: goto L_0x001a;
                    case 2116862345: goto L_0x0010;
                    default: goto L_0x000f;
                }
            L_0x000f:
                goto L_0x0042
            L_0x0010:
                java.lang.String r1 = "android.bluetooth.device.action.BOND_STATE_CHANGED"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x000f
                r1 = 1
                goto L_0x0043
            L_0x001a:
                java.lang.String r1 = "nodomain.freeyourgadget.gadgetbridge.gbdevice.action.device_changed"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x000f
                r1 = 4
                goto L_0x0043
            L_0x0024:
                java.lang.String r1 = "android.bluetooth.device.action.NAME_CHANGED"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x000f
                r1 = 2
                goto L_0x0043
            L_0x002e:
                java.lang.String r1 = "android.bluetooth.device.action.ALIAS_CHANGED"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x000f
                r1 = 3
                goto L_0x0043
            L_0x0038:
                java.lang.String r1 = "nodomain.freeyourgadget.gadgetbridge.devices.devicemanager.action.set_version"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x000f
                r1 = 0
                goto L_0x0043
            L_0x0042:
                r1 = -1
            L_0x0043:
                if (r1 == 0) goto L_0x00c1
                if (r1 == r5) goto L_0x00c1
                if (r1 == r4) goto L_0x00ad
                if (r1 == r3) goto L_0x00ad
                if (r1 == r2) goto L_0x004f
                goto L_0x00c7
            L_0x004f:
                java.lang.String r1 = "device"
                android.os.Parcelable r1 = r9.getParcelableExtra(r1)
                nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r1 = (nodomain.freeyourgadget.gadgetbridge.impl.GBDevice) r1
                java.lang.String r2 = r1.getAddress()
                if (r2 == 0) goto L_0x00a2
                nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager r2 = nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager.this
                java.util.List r2 = r2.deviceList
                int r2 = r2.indexOf(r1)
                if (r2 < 0) goto L_0x0073
                nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager r3 = nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager.this
                java.util.List r3 = r3.deviceList
                r3.set(r2, r1)
                goto L_0x007c
            L_0x0073:
                nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager r3 = nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager.this
                java.util.List r3 = r3.deviceList
                r3.add(r1)
            L_0x007c:
                boolean r3 = r1.isInitialized()
                if (r3 == 0) goto L_0x00a2
                nodomain.freeyourgadget.gadgetbridge.database.DBHandler r3 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x00a1 }
                nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r4 = r3.getDaoSession()     // Catch:{ all -> 0x0093 }
                nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getDevice(r1, r4)     // Catch:{ all -> 0x0093 }
                if (r3 == 0) goto L_0x0092
                r3.close()     // Catch:{ Exception -> 0x00a1 }
            L_0x0092:
                goto L_0x00a2
            L_0x0093:
                r4 = move-exception
                throw r4     // Catch:{ all -> 0x0095 }
            L_0x0095:
                r5 = move-exception
                if (r3 == 0) goto L_0x00a0
                r3.close()     // Catch:{ all -> 0x009c }
                goto L_0x00a0
            L_0x009c:
                r6 = move-exception
                r4.addSuppressed(r6)     // Catch:{ Exception -> 0x00a1 }
            L_0x00a0:
                throw r5     // Catch:{ Exception -> 0x00a1 }
            L_0x00a1:
                r3 = move-exception
            L_0x00a2:
                nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager r2 = nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager.this
                r2.updateSelectedDevice(r1)
                nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager r2 = nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager.this
                r2.refreshPairedDevices()
                goto L_0x00c7
            L_0x00ad:
                java.lang.String r1 = "android.bluetooth.device.extra.DEVICE"
                android.os.Parcelable r1 = r9.getParcelableExtra(r1)
                android.bluetooth.BluetoothDevice r1 = (android.bluetooth.BluetoothDevice) r1
                java.lang.String r2 = "android.bluetooth.device.extra.NAME"
                java.lang.String r2 = r9.getStringExtra(r2)
                nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager r3 = nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager.this
                r3.updateDeviceName(r1, r2)
                goto L_0x00c7
            L_0x00c1:
                nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager r1 = nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager.this
                r1.refreshPairedDevices()
            L_0x00c7:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager.C10781.onReceive(android.content.Context, android.content.Intent):void");
        }
    };
    private GBDevice selectedDevice = null;

    public DeviceManager(Context context2) {
        this.context = context2;
        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(ACTION_REFRESH_DEVICELIST);
        filterLocal.addAction(GBDevice.ACTION_DEVICE_CHANGED);
        filterLocal.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
        LocalBroadcastManager.getInstance(context2).registerReceiver(this.mReceiver, filterLocal);
        IntentFilter filterGlobal = new IntentFilter();
        filterGlobal.addAction("android.bluetooth.device.action.NAME_CHANGED");
        filterGlobal.addAction(BLUETOOTH_DEVICE_ACTION_ALIAS_CHANGED);
        filterGlobal.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
        context2.registerReceiver(this.mReceiver, filterGlobal);
        refreshPairedDevices();
    }

    /* access modifiers changed from: private */
    public void updateDeviceName(BluetoothDevice device, String newName) {
        for (GBDevice dev : this.deviceList) {
            if (device.getAddress().equals(dev.getAddress()) && !dev.getName().equals(newName)) {
                dev.setName(newName);
                notifyDevicesChanged();
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateSelectedDevice(GBDevice dev) {
        GBDevice gBDevice = this.selectedDevice;
        if (gBDevice == null) {
            this.selectedDevice = dev;
        } else if (gBDevice.equals(dev)) {
            this.selectedDevice = dev;
        } else if (this.selectedDevice.isConnected() && dev.isConnected()) {
            LOG.warn("multiple connected devices -- this is currently not really supported");
            this.selectedDevice = dev;
        } else if (!this.selectedDevice.isConnected()) {
            this.selectedDevice = dev;
        }
        C1238GB.updateNotification(this.selectedDevice, this.context);
    }

    /* access modifiers changed from: private */
    public void refreshPairedDevices() {
        Set<GBDevice> availableDevices = DeviceHelper.getInstance().getAvailableDevices(this.context);
        this.deviceList.retainAll(availableDevices);
        for (GBDevice availableDevice : availableDevices) {
            if (!this.deviceList.contains(availableDevice)) {
                this.deviceList.add(availableDevice);
            }
        }
        Collections.sort(this.deviceList, new Comparator<GBDevice>() {
            public int compare(GBDevice lhs, GBDevice rhs) {
                if (rhs.getStateOrdinal() - lhs.getStateOrdinal() == 0) {
                    return Collator.getInstance().compare(lhs.getName(), rhs.getName());
                }
                return rhs.getStateOrdinal() - lhs.getStateOrdinal();
            }
        });
        notifyDevicesChanged();
    }

    public List<GBDevice> getDevices() {
        return Collections.unmodifiableList(this.deviceList);
    }

    public GBDevice getSelectedDevice() {
        return this.selectedDevice;
    }

    private void notifyDevicesChanged() {
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(new Intent(ACTION_DEVICES_CHANGED));
    }
}
