package nodomain.freeyourgadget.gadgetbridge.impl;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.util.AndroidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GBDeviceCandidate implements Parcelable {
    public static final Parcelable.Creator<GBDeviceCandidate> CREATOR = new Parcelable.Creator<GBDeviceCandidate>() {
        public GBDeviceCandidate createFromParcel(Parcel in) {
            return new GBDeviceCandidate(in);
        }

        public GBDeviceCandidate[] newArray(int size) {
            return new GBDeviceCandidate[size];
        }
    };
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) GBDeviceCandidate.class);
    private final BluetoothDevice device;
    private DeviceType deviceType;
    private final short rssi;
    private final ParcelUuid[] serviceUuids;

    public GBDeviceCandidate(BluetoothDevice device2, short rssi2, ParcelUuid[] serviceUuids2) {
        this.deviceType = DeviceType.UNKNOWN;
        this.device = device2;
        this.rssi = rssi2;
        this.serviceUuids = mergeServiceUuids(serviceUuids2, device2.getUuids());
    }

    private GBDeviceCandidate(Parcel in) {
        this.deviceType = DeviceType.UNKNOWN;
        this.device = (BluetoothDevice) in.readParcelable(getClass().getClassLoader());
        if (this.device != null) {
            this.rssi = (short) in.readInt();
            this.deviceType = DeviceType.valueOf(in.readString());
            this.serviceUuids = mergeServiceUuids(AndroidUtils.toParcelUuids(in.readParcelableArray(getClass().getClassLoader())), this.device.getUuids());
            return;
        }
        throw new IllegalStateException("Unable to read state from Parcel");
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.device, 0);
        dest.writeInt(this.rssi);
        dest.writeString(this.deviceType.name());
        dest.writeParcelableArray(this.serviceUuids, 0);
    }

    public BluetoothDevice getDevice() {
        return this.device;
    }

    public void setDeviceType(DeviceType type) {
        this.deviceType = type;
    }

    public DeviceType getDeviceType() {
        return this.deviceType;
    }

    public String getMacAddress() {
        BluetoothDevice bluetoothDevice = this.device;
        return bluetoothDevice != null ? bluetoothDevice.getAddress() : GBApplication.getContext().getString(C0889R.string._unknown_);
    }

    private ParcelUuid[] mergeServiceUuids(ParcelUuid[] serviceUuids2, ParcelUuid[] deviceUuids) {
        Set<ParcelUuid> uuids = new HashSet<>();
        if (serviceUuids2 != null) {
            uuids.addAll(Arrays.asList(serviceUuids2));
        }
        if (deviceUuids != null) {
            uuids.addAll(Arrays.asList(deviceUuids));
        }
        return (ParcelUuid[]) uuids.toArray(new ParcelUuid[0]);
    }

    public ParcelUuid[] getServiceUuids() {
        return this.serviceUuids;
    }

    public boolean supportsService(UUID aService) {
        ParcelUuid[] uuids = getServiceUuids();
        if (uuids.length == 0) {
            LOG.warn("no cached services available for " + this);
            return false;
        }
        for (ParcelUuid uuid : uuids) {
            if (uuid != null && aService.equals(uuid.getUuid())) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        String deviceName = null;
        try {
            Method method = this.device.getClass().getMethod("getAliasName", new Class[0]);
            if (method != null) {
                deviceName = (String) method.invoke(this.device, new Object[0]);
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            Logger logger = LOG;
            logger.info("Could not get device alias for " + this.device.getName());
        }
        if (deviceName == null || deviceName.length() == 0) {
            deviceName = this.device.getName();
        }
        if (deviceName == null || deviceName.length() == 0) {
            return "(unknown)";
        }
        return deviceName;
    }

    public short getRssi() {
        return this.rssi;
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.device.getAddress().equals(((GBDeviceCandidate) o).device.getAddress());
    }

    public int hashCode() {
        return this.device.getAddress().hashCode() ^ 37;
    }

    public String toString() {
        return getName() + ": " + getMacAddress() + " (" + getDeviceType() + ")";
    }
}
