package nodomain.freeyourgadget.gadgetbridge.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.UnknownDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.banglejs.BangleJSCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.casiogb6900.CasioGB6900DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.EXRIZUK8Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.MakibesF68Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.Q8Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip.AmazfitBipCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip.AmazfitBipLiteCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitcor.AmazfitCorCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitcor2.AmazfitCor2Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitgtr.AmazfitGTRCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitgts.AmazfitGTSCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.miband2.MiBand2Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.miband2.MiBand2HRXCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.miband3.MiBand3Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.miband4.MiBand4Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.id115.ID115Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.jyou.BFH16DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.jyou.TeclastH30.TeclastH30Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.jyou.p011y5.Y5Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.liveview.LiveviewCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.makibeshr3.MakibesHR3Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.mijia_lywsd02.MijiaLywsd02Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.miscale2.MiScale2DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.no1f1.No1F1Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.QHybridCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.roidmi.Roidmi1Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.roidmi.Roidmi3Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.vibratissimo.VibratissimoCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.watch9.Watch9DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.xwatch.XWatchCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.zetime.ZeTimeCoordinator;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.DeviceAttributes;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceHelper {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DeviceHelper.class);
    private static final DeviceHelper instance = new DeviceHelper();
    private List<DeviceCoordinator> coordinators;

    public static DeviceHelper getInstance() {
        return instance;
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        for (DeviceCoordinator coordinator : getAllCoordinators()) {
            DeviceType deviceType = coordinator.getSupportedType(candidate);
            if (deviceType.isSupported()) {
                return deviceType;
            }
        }
        return DeviceType.UNKNOWN;
    }

    public boolean getSupportedType(GBDevice device) {
        for (DeviceCoordinator coordinator : getAllCoordinators()) {
            if (coordinator.supports(device)) {
                return true;
            }
        }
        return false;
    }

    public GBDevice findAvailableDevice(String deviceAddress, Context context) {
        for (GBDevice availableDevice : getAvailableDevices(context)) {
            if (deviceAddress.equals(availableDevice.getAddress())) {
                return availableDevice;
            }
        }
        return null;
    }

    public Set<GBDevice> getAvailableDevices(Context context) {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<GBDevice> availableDevices = new LinkedHashSet<>();
        if (btAdapter == null) {
            C1238GB.toast(context, context.getString(C0889R.string.bluetooth_is_not_supported_), 0, 2);
        } else if (!btAdapter.isEnabled()) {
            C1238GB.toast(context, context.getString(C0889R.string.bluetooth_is_disabled_), 0, 2);
        }
        availableDevices.addAll(getDatabaseDevices());
        Prefs prefs = GBApplication.getPrefs();
        String miAddr = prefs.getString(MiBandConst.PREF_MIBAND_ADDRESS, "");
        if (miAddr.length() > 0) {
            availableDevices.add(new GBDevice(miAddr, MiBandConst.MI_GENERAL_NAME_PREFIX, DeviceType.MIBAND));
        }
        String pebbleEmuAddr = prefs.getString("pebble_emu_addr", "");
        String pebbleEmuPort = prefs.getString("pebble_emu_port", "");
        if (pebbleEmuAddr.length() >= 7 && pebbleEmuPort.length() > 0) {
            availableDevices.add(new GBDevice(pebbleEmuAddr + ":" + pebbleEmuPort, "Pebble qemu", DeviceType.PEBBLE));
        }
        return availableDevices;
    }

    public GBDevice toSupportedDevice(BluetoothDevice device) {
        return toSupportedDevice(new GBDeviceCandidate(device, 0, device.getUuids()));
    }

    public GBDevice toSupportedDevice(GBDeviceCandidate candidate) {
        for (DeviceCoordinator coordinator : getAllCoordinators()) {
            if (coordinator.supports(candidate)) {
                return coordinator.createDevice(candidate);
            }
        }
        return null;
    }

    public DeviceCoordinator getCoordinator(GBDeviceCandidate device) {
        synchronized (this) {
            for (DeviceCoordinator coord : getAllCoordinators()) {
                if (coord.supports(device)) {
                    return coord;
                }
            }
            return new UnknownDeviceCoordinator();
        }
    }

    public DeviceCoordinator getCoordinator(GBDevice device) {
        synchronized (this) {
            for (DeviceCoordinator coord : getAllCoordinators()) {
                if (coord.supports(device)) {
                    return coord;
                }
            }
            return new UnknownDeviceCoordinator();
        }
    }

    public synchronized List<DeviceCoordinator> getAllCoordinators() {
        if (this.coordinators == null) {
            this.coordinators = createCoordinators();
        }
        return this.coordinators;
    }

    private List<DeviceCoordinator> createCoordinators() {
        List<DeviceCoordinator> result = new ArrayList<>();
        result.add(new MiScale2DeviceCoordinator());
        result.add(new AmazfitBipCoordinator());
        result.add(new AmazfitBipLiteCoordinator());
        result.add(new AmazfitCorCoordinator());
        result.add(new AmazfitCor2Coordinator());
        result.add(new AmazfitGTRCoordinator());
        result.add(new AmazfitGTSCoordinator());
        result.add(new MiBand3Coordinator());
        result.add(new MiBand4Coordinator());
        result.add(new MiBand2HRXCoordinator());
        result.add(new MiBand2Coordinator());
        result.add(new MiBandCoordinator());
        result.add(new PebbleCoordinator());
        result.add(new VibratissimoCoordinator());
        result.add(new LiveviewCoordinator());
        result.add(new HPlusCoordinator());
        result.add(new No1F1Coordinator());
        result.add(new MakibesF68Coordinator());
        result.add(new Q8Coordinator());
        result.add(new EXRIZUK8Coordinator());
        result.add(new TeclastH30Coordinator());
        result.add(new XWatchCoordinator());
        result.add(new QHybridCoordinator());
        result.add(new ZeTimeCoordinator());
        result.add(new ID115Coordinator());
        result.add(new Watch9DeviceCoordinator());
        result.add(new Roidmi1Coordinator());
        result.add(new Roidmi3Coordinator());
        result.add(new Y5Coordinator());
        result.add(new CasioGB6900DeviceCoordinator());
        result.add(new BFH16DeviceCoordinator());
        result.add(new MijiaLywsd02Coordinator());
        result.add(new MakibesHR3Coordinator());
        result.add(new BangleJSCoordinator());
        return result;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003e, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003f, code lost:
        if (r1 != null) goto L_0x0041;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0049, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.List<nodomain.freeyourgadget.gadgetbridge.impl.GBDevice> getDatabaseDevices() {
        /*
            r7 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r1 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x004a }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r2 = r1.getDaoSession()     // Catch:{ all -> 0x003c }
            java.util.List r2 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getActiveDevices(r2)     // Catch:{ all -> 0x003c }
            java.util.Iterator r3 = r2.iterator()     // Catch:{ all -> 0x003c }
        L_0x0015:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x003c }
            if (r4 == 0) goto L_0x0035
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x003c }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r4 = (nodomain.freeyourgadget.gadgetbridge.entities.Device) r4     // Catch:{ all -> 0x003c }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r5 = r7.toGBDevice(r4)     // Catch:{ all -> 0x003c }
            if (r5 == 0) goto L_0x0034
            nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper r6 = getInstance()     // Catch:{ all -> 0x003c }
            boolean r6 = r6.getSupportedType((nodomain.freeyourgadget.gadgetbridge.impl.GBDevice) r5)     // Catch:{ all -> 0x003c }
            if (r6 == 0) goto L_0x0034
            r0.add(r5)     // Catch:{ all -> 0x003c }
        L_0x0034:
            goto L_0x0015
        L_0x0035:
            if (r1 == 0) goto L_0x003b
            r1.close()     // Catch:{ Exception -> 0x004a }
        L_0x003b:
            return r0
        L_0x003c:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x003e }
        L_0x003e:
            r3 = move-exception
            if (r1 == 0) goto L_0x0049
            r1.close()     // Catch:{ all -> 0x0045 }
            goto L_0x0049
        L_0x0045:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ Exception -> 0x004a }
        L_0x0049:
            throw r3     // Catch:{ Exception -> 0x004a }
        L_0x004a:
            r1 = move-exception
            r2 = 0
            r3 = 3
            java.lang.String r4 = "Error retrieving devices from database"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast(r4, r2, r3)
            java.util.List r2 = java.util.Collections.emptyList()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper.getDatabaseDevices():java.util.List");
    }

    public GBDevice toGBDevice(Device dbDevice) {
        GBDevice gbDevice = new GBDevice(dbDevice.getIdentifier(), dbDevice.getName(), DeviceType.fromKey(dbDevice.getType()));
        List<DeviceAttributes> deviceAttributesList = dbDevice.getDeviceAttributesList();
        if (deviceAttributesList.size() > 0) {
            gbDevice.setModel(dbDevice.getModel());
            DeviceAttributes attrs = deviceAttributesList.get(0);
            gbDevice.setFirmwareVersion(attrs.getFirmwareVersion1());
            gbDevice.setFirmwareVersion2(attrs.getFirmwareVersion2());
            gbDevice.setVolatileAddress(attrs.getVolatileIdentifier());
        }
        return gbDevice;
    }

    public boolean removeBond(GBDevice device) throws GBException {
        BluetoothDevice remoteDevice;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null || (remoteDevice = defaultAdapter.getRemoteDevice(device.getAddress())) == null) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(BluetoothDevice.class.getMethod("removeBond", (Class[]) null).invoke(remoteDevice, (Object[]) null));
        } catch (Exception e) {
            throw new GBException("Error removing bond to device: " + device, e);
        }
    }
}
