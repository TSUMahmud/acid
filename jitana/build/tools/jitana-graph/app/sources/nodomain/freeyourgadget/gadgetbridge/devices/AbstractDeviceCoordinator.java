package nodomain.freeyourgadget.gadgetbridge.devices;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanFilter;
import java.util.Collection;
import java.util.Collections;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDeviceCoordinator implements DeviceCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractDeviceCoordinator.class);

    /* access modifiers changed from: protected */
    public abstract void deleteDevice(GBDevice gBDevice, Device device, DaoSession daoSession) throws GBException;

    public final boolean supports(GBDeviceCandidate candidate) {
        return getSupportedType(candidate).isSupported();
    }

    public boolean supports(GBDevice device) {
        return getDeviceType().equals(device.getType());
    }

    public Collection<? extends ScanFilter> createBLEScanFilters() {
        return Collections.emptyList();
    }

    public GBDevice createDevice(GBDeviceCandidate candidate) {
        return new GBDevice(candidate.getDevice().getAddress(), candidate.getName(), getDeviceType());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00d7, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00d8, code lost:
        if (r3 != null) goto L_0x00da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00e2, code lost:
        throw r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void deleteDevice(nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r10) throws nodomain.freeyourgadget.gadgetbridge.GBException {
        /*
            r9 = this;
            org.slf4j.Logger r0 = LOG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "will try to delete device: "
            r1.append(r2)
            java.lang.String r2 = r10.getName()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.info(r1)
            boolean r0 = r10.isConnected()
            if (r0 != 0) goto L_0x0026
            boolean r0 = r10.isConnecting()
            if (r0 == 0) goto L_0x002d
        L_0x0026:
            nodomain.freeyourgadget.gadgetbridge.model.DeviceService r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.deviceService()
            r0.disconnect()
        L_0x002d:
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getPrefs()
            android.content.SharedPreferences r1 = r0.getPreferences()
            java.lang.String r2 = ""
            java.lang.String r3 = "last_device_address"
            java.lang.String r1 = r1.getString(r3, r2)
            java.lang.String r4 = r10.getAddress()
            if (r4 != r1) goto L_0x0059
            org.slf4j.Logger r4 = LOG
            java.lang.String r5 = "#1605 removing last device"
            r4.debug(r5)
            android.content.SharedPreferences r4 = r0.getPreferences()
            android.content.SharedPreferences$Editor r4 = r4.edit()
            android.content.SharedPreferences$Editor r3 = r4.remove(r3)
            r3.apply()
        L_0x0059:
            android.content.SharedPreferences r3 = r0.getPreferences()
            java.lang.String r4 = "development_miaddr"
            java.lang.String r2 = r3.getString(r4, r2)
            java.lang.String r3 = r10.getAddress()
            if (r3 != r2) goto L_0x007f
            org.slf4j.Logger r3 = LOG
            java.lang.String r5 = "#1605 removing devel miband"
            r3.debug(r5)
            android.content.SharedPreferences r3 = r0.getPreferences()
            android.content.SharedPreferences$Editor r3 = r3.edit()
            android.content.SharedPreferences$Editor r3 = r3.remove(r4)
            r3.apply()
        L_0x007f:
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r3 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x00e3 }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r4 = r3.getDaoSession()     // Catch:{ all -> 0x00d5 }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r5 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.findDevice(r10, r4)     // Catch:{ all -> 0x00d5 }
            if (r5 == 0) goto L_0x00b8
            r9.deleteDevice(r10, r5, r4)     // Catch:{ all -> 0x00d5 }
            nodomain.freeyourgadget.gadgetbridge.entities.DeviceAttributesDao r6 = r4.getDeviceAttributesDao()     // Catch:{ all -> 0x00d5 }
            de.greenrobot.dao.query.QueryBuilder r6 = r6.queryBuilder()     // Catch:{ all -> 0x00d5 }
            de.greenrobot.dao.Property r7 = nodomain.freeyourgadget.gadgetbridge.entities.DeviceAttributesDao.Properties.DeviceId     // Catch:{ all -> 0x00d5 }
            java.lang.Long r8 = r5.getId()     // Catch:{ all -> 0x00d5 }
            de.greenrobot.dao.query.WhereCondition r7 = r7.mo14989eq(r8)     // Catch:{ all -> 0x00d5 }
            r8 = 0
            de.greenrobot.dao.query.WhereCondition[] r8 = new p008de.greenrobot.dao.query.WhereCondition[r8]     // Catch:{ all -> 0x00d5 }
            de.greenrobot.dao.query.QueryBuilder r7 = r6.where(r7, r8)     // Catch:{ all -> 0x00d5 }
            de.greenrobot.dao.query.DeleteQuery r7 = r7.buildDelete()     // Catch:{ all -> 0x00d5 }
            r7.executeDeleteWithoutDetachingEntities()     // Catch:{ all -> 0x00d5 }
            nodomain.freeyourgadget.gadgetbridge.entities.DeviceDao r7 = r4.getDeviceDao()     // Catch:{ all -> 0x00d5 }
            r7.delete(r5)     // Catch:{ all -> 0x00d5 }
            goto L_0x00ce
        L_0x00b8:
            org.slf4j.Logger r6 = LOG     // Catch:{ all -> 0x00d5 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d5 }
            r7.<init>()     // Catch:{ all -> 0x00d5 }
            java.lang.String r8 = "device to delete not found in db: "
            r7.append(r8)     // Catch:{ all -> 0x00d5 }
            r7.append(r10)     // Catch:{ all -> 0x00d5 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x00d5 }
            r6.info(r7)     // Catch:{ all -> 0x00d5 }
        L_0x00ce:
            if (r3 == 0) goto L_0x00d3
            r3.close()     // Catch:{ Exception -> 0x00e3 }
        L_0x00d3:
            return
        L_0x00d5:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x00d7 }
        L_0x00d7:
            r5 = move-exception
            if (r3 == 0) goto L_0x00e2
            r3.close()     // Catch:{ all -> 0x00de }
            goto L_0x00e2
        L_0x00de:
            r6 = move-exception
            r4.addSuppressed(r6)     // Catch:{ Exception -> 0x00e3 }
        L_0x00e2:
            throw r5     // Catch:{ Exception -> 0x00e3 }
        L_0x00e3:
            r3 = move-exception
            nodomain.freeyourgadget.gadgetbridge.GBException r4 = new nodomain.freeyourgadget.gadgetbridge.GBException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Error deleting device: "
            r5.append(r6)
            java.lang.String r6 = r3.getMessage()
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r4.<init>(r5, r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.devices.AbstractDeviceCoordinator.deleteDevice(nodomain.freeyourgadget.gadgetbridge.impl.GBDevice):void");
    }

    public boolean allowFetchActivityData(GBDevice device) {
        return device.isInitialized() && !device.isBusy() && supportsActivityDataFetching();
    }

    public boolean isHealthWearable(BluetoothDevice device) {
        BluetoothClass bluetoothClass = device.getBluetoothClass();
        if (bluetoothClass == null) {
            Logger logger = LOG;
            logger.warn("unable to determine bluetooth device class of " + device);
            return false;
        } else if ((bluetoothClass.getMajorDeviceClass() == 1792 || bluetoothClass.getMajorDeviceClass() == 7936) && (bluetoothClass.getDeviceClass() & 2332) != 0) {
            return true;
        } else {
            return false;
        }
    }

    public int getBondingStyle() {
        return 2;
    }

    public boolean supportsActivityTracks() {
        return false;
    }

    public boolean supportsAlarmSnoozing() {
        return false;
    }

    public boolean supportsMusicInfo() {
        return false;
    }

    public boolean supportsLedColor() {
        return false;
    }

    public boolean supportsRgbLedColor() {
        return false;
    }

    public int[] getColorPresets() {
        return new int[0];
    }

    public boolean supportsUnicodeEmojis() {
        return false;
    }

    public int[] getSupportedDeviceSpecificSettings(GBDevice device) {
        return null;
    }
}
