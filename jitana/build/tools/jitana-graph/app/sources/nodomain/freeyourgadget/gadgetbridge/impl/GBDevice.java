package nodomain.freeyourgadget.gadgetbridge.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.BatteryState;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.model.GenericItem;
import nodomain.freeyourgadget.gadgetbridge.model.ItemWithDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GBDevice implements Parcelable {
    public static final String ACTION_DEVICE_CHANGED = "nodomain.freeyourgadget.gadgetbridge.gbdevice.action.device_changed";
    private static final short BATTERY_THRESHOLD_PERCENT = 10;
    public static final short BATTERY_UNKNOWN = -1;
    public static final Parcelable.Creator<GBDevice> CREATOR = new Parcelable.Creator<GBDevice>() {
        public GBDevice createFromParcel(Parcel source) {
            return new GBDevice(source);
        }

        public GBDevice[] newArray(int size) {
            return new GBDevice[size];
        }
    };
    private static final String DEVINFO_ADDR = "ADDR: ";
    private static final String DEVINFO_ADDR2 = "ADDR2: ";
    private static final String DEVINFO_FW_VER = "FW: ";
    private static final String DEVINFO_GPS_VER = "GPS: ";
    private static final String DEVINFO_HR_VER = "HR: ";
    private static final String DEVINFO_HW_VER = "HW: ";
    public static final String EXTRA_DEVICE = "device";
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) GBDevice.class);
    public static final short RSSI_UNKNOWN = 0;
    private final String mAddress;
    private short mBatteryLevel;
    private BatteryState mBatteryState;
    private short mBatteryThresholdPercent;
    private float mBatteryVoltage;
    private String mBusyTask;
    private List<ItemWithDetails> mDeviceInfos;
    private final DeviceType mDeviceType;
    private HashMap<String, Object> mExtraInfos;
    private String mFirmwareVersion;
    private String mFirmwareVersion2;
    private String mModel;
    private String mName;
    private int mNotificationIconConnected;
    private int mNotificationIconDisconnected;
    private int mNotificationIconLowBattery;
    private short mRssi;
    private State mState;
    private String mVolatileAddress;

    public enum State {
        NOT_CONNECTED,
        WAITING_FOR_RECONNECT,
        CONNECTING,
        CONNECTED,
        INITIALIZING,
        AUTHENTICATION_REQUIRED,
        AUTHENTICATING,
        INITIALIZED
    }

    public GBDevice(String address, String name, DeviceType deviceType) {
        this(address, (String) null, name, deviceType);
    }

    public GBDevice(String address, String address2, String name, DeviceType deviceType) {
        this.mState = State.NOT_CONNECTED;
        this.mBatteryLevel = -1;
        this.mBatteryVoltage = -1.0f;
        this.mBatteryThresholdPercent = BATTERY_THRESHOLD_PERCENT;
        this.mRssi = 0;
        this.mNotificationIconConnected = C0889R.C0890drawable.ic_notification;
        this.mNotificationIconDisconnected = C0889R.C0890drawable.ic_notification_disconnected;
        this.mNotificationIconLowBattery = C0889R.C0890drawable.ic_notification_low_battery;
        this.mAddress = address;
        this.mVolatileAddress = address2;
        this.mName = name != null ? name : this.mAddress;
        this.mDeviceType = deviceType;
        validate();
    }

    private GBDevice(Parcel in) {
        this.mState = State.NOT_CONNECTED;
        this.mBatteryLevel = -1;
        this.mBatteryVoltage = -1.0f;
        this.mBatteryThresholdPercent = BATTERY_THRESHOLD_PERCENT;
        this.mRssi = 0;
        this.mNotificationIconConnected = C0889R.C0890drawable.ic_notification;
        this.mNotificationIconDisconnected = C0889R.C0890drawable.ic_notification_disconnected;
        this.mNotificationIconLowBattery = C0889R.C0890drawable.ic_notification_low_battery;
        this.mName = in.readString();
        this.mAddress = in.readString();
        this.mVolatileAddress = in.readString();
        this.mDeviceType = DeviceType.values()[in.readInt()];
        this.mFirmwareVersion = in.readString();
        this.mFirmwareVersion2 = in.readString();
        this.mModel = in.readString();
        this.mState = State.values()[in.readInt()];
        this.mBatteryLevel = (short) in.readInt();
        this.mBatteryThresholdPercent = (short) in.readInt();
        this.mBatteryState = (BatteryState) in.readSerializable();
        this.mRssi = (short) in.readInt();
        this.mBusyTask = in.readString();
        this.mDeviceInfos = in.readArrayList(getClass().getClassLoader());
        this.mExtraInfos = (HashMap) in.readSerializable();
        this.mNotificationIconConnected = in.readInt();
        this.mNotificationIconDisconnected = in.readInt();
        this.mNotificationIconLowBattery = in.readInt();
        validate();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mAddress);
        dest.writeString(this.mVolatileAddress);
        dest.writeInt(this.mDeviceType.ordinal());
        dest.writeString(this.mFirmwareVersion);
        dest.writeString(this.mFirmwareVersion2);
        dest.writeString(this.mModel);
        dest.writeInt(this.mState.ordinal());
        dest.writeInt(this.mBatteryLevel);
        dest.writeInt(this.mBatteryThresholdPercent);
        dest.writeSerializable(this.mBatteryState);
        dest.writeInt(this.mRssi);
        dest.writeString(this.mBusyTask);
        dest.writeList(this.mDeviceInfos);
        dest.writeSerializable(this.mExtraInfos);
        dest.writeInt(this.mNotificationIconConnected);
        dest.writeInt(this.mNotificationIconDisconnected);
        dest.writeInt(this.mNotificationIconLowBattery);
    }

    private void validate() {
        if (getAddress() == null) {
            throw new IllegalArgumentException("address must not be null");
        }
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        if (name == null) {
            Logger logger = LOG;
            logger.warn("Ignoring setting of GBDevice name to null for " + this);
            return;
        }
        this.mName = name;
    }

    public String getAddress() {
        return this.mAddress;
    }

    public String getVolatileAddress() {
        return this.mVolatileAddress;
    }

    public String getFirmwareVersion() {
        return this.mFirmwareVersion;
    }

    public String getFirmwareVersion2() {
        return this.mFirmwareVersion2;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.mFirmwareVersion = firmwareVersion;
    }

    public void setFirmwareVersion2(String firmwareVersion2) {
        this.mFirmwareVersion2 = firmwareVersion2;
    }

    public void setVolatileAddress(String volatileAddress) {
        this.mVolatileAddress = volatileAddress;
    }

    public String getModel() {
        return this.mModel;
    }

    public void setModel(String model) {
        this.mModel = model;
    }

    public boolean isConnected() {
        return this.mState.ordinal() >= State.CONNECTED.ordinal();
    }

    public boolean isInitializing() {
        return this.mState == State.INITIALIZING;
    }

    public boolean isInitialized() {
        return this.mState.ordinal() >= State.INITIALIZED.ordinal();
    }

    public boolean isConnecting() {
        return this.mState == State.CONNECTING;
    }

    public boolean isBusy() {
        return this.mBusyTask != null;
    }

    public String getBusyTask() {
        return this.mBusyTask;
    }

    public int getNotificationIconConnected() {
        return this.mNotificationIconConnected;
    }

    public void setNotificationIconConnected(int mNotificationIconConnected2) {
        this.mNotificationIconConnected = mNotificationIconConnected2;
    }

    public int getNotificationIconDisconnected() {
        return this.mNotificationIconDisconnected;
    }

    public void setNotificationIconDisconnected(int notificationIconDisconnected) {
        this.mNotificationIconDisconnected = notificationIconDisconnected;
    }

    public int getNotificationIconLowBattery() {
        return this.mNotificationIconLowBattery;
    }

    public void setNotificationIconLowBattery(int mNotificationIconLowBattery2) {
        this.mNotificationIconLowBattery = mNotificationIconLowBattery2;
    }

    public void setBusyTask(String task) {
        if (task != null) {
            if (this.mBusyTask != null) {
                Logger logger = LOG;
                logger.warn("Attempt to mark device as busy with: " + task + ", but is already busy with: " + this.mBusyTask);
            }
            Logger logger2 = LOG;
            logger2.info("Mark device as busy: " + task);
            this.mBusyTask = task;
            return;
        }
        throw new IllegalArgumentException("busy task must not be null");
    }

    public void unsetBusyTask() {
        if (this.mBusyTask == null) {
            LOG.error("Attempt to mark device as not busy anymore, but was not busy before.");
            return;
        }
        Logger logger = LOG;
        logger.info("Mark device as NOT busy anymore: " + this.mBusyTask);
        this.mBusyTask = null;
    }

    public State getState() {
        return this.mState;
    }

    public int getStateOrdinal() {
        return this.mState.ordinal();
    }

    public void setState(State state) {
        this.mState = state;
        if (state.ordinal() <= State.CONNECTED.ordinal()) {
            unsetDynamicState();
        }
    }

    private void unsetDynamicState() {
        setBatteryLevel(-1);
        setBatteryState(BatteryState.UNKNOWN);
        setFirmwareVersion((String) null);
        setFirmwareVersion2((String) null);
        setRssi(0);
        resetExtraInfos();
        if (this.mBusyTask != null) {
            unsetBusyTask();
        }
    }

    public String getStateString() {
        return getStateString(true);
    }

    private String getStateString(boolean simple) {
        switch (this.mState) {
            case NOT_CONNECTED:
                return GBApplication.getContext().getString(C0889R.string.not_connected);
            case WAITING_FOR_RECONNECT:
                return GBApplication.getContext().getString(C0889R.string.waiting_for_reconnect);
            case CONNECTING:
                return GBApplication.getContext().getString(C0889R.string.connecting);
            case CONNECTED:
                if (simple) {
                    return GBApplication.getContext().getString(C0889R.string.connecting);
                }
                return GBApplication.getContext().getString(C0889R.string.connected);
            case INITIALIZING:
                if (simple) {
                    return GBApplication.getContext().getString(C0889R.string.connecting);
                }
                return GBApplication.getContext().getString(C0889R.string.initializing);
            case AUTHENTICATION_REQUIRED:
                return GBApplication.getContext().getString(C0889R.string.authentication_required);
            case AUTHENTICATING:
                return GBApplication.getContext().getString(C0889R.string.authenticating);
            case INITIALIZED:
                if (simple) {
                    return GBApplication.getContext().getString(C0889R.string.connected);
                }
                return GBApplication.getContext().getString(C0889R.string.initialized);
            default:
                return GBApplication.getContext().getString(C0889R.string.unknown_state);
        }
    }

    public DeviceType getType() {
        return this.mDeviceType;
    }

    public void setRssi(short rssi) {
        if (rssi < 0) {
            Logger logger = LOG;
            logger.warn("Illegal RSSI value " + rssi + ", setting to RSSI_UNKNOWN");
            this.mRssi = 0;
            return;
        }
        this.mRssi = rssi;
    }

    public short getRssi() {
        return this.mRssi;
    }

    public void sendDeviceUpdateIntent(Context context) {
        Intent deviceUpdateIntent = new Intent(ACTION_DEVICE_CHANGED);
        deviceUpdateIntent.putExtra(EXTRA_DEVICE, this);
        LocalBroadcastManager.getInstance(context).sendBroadcast(deviceUpdateIntent);
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof GBDevice) && ((GBDevice) obj).getAddress().equals(this.mAddress)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.mAddress.hashCode() ^ 37;
    }

    public Object getExtraInfo(String key) {
        HashMap<String, Object> hashMap = this.mExtraInfos;
        if (hashMap == null) {
            return null;
        }
        return hashMap.get(key);
    }

    public void setExtraInfo(String key, Object info) {
        if (this.mExtraInfos == null) {
            this.mExtraInfos = new HashMap<>();
        }
        this.mExtraInfos.put(key, info);
    }

    public void resetExtraInfos() {
        this.mExtraInfos = null;
    }

    public short getBatteryLevel() {
        return this.mBatteryLevel;
    }

    public void setBatteryLevel(short batteryLevel) {
        if ((batteryLevel < 0 || batteryLevel > 100) && batteryLevel != -1) {
            Logger logger = LOG;
            logger.error("Battery level musts be within range 0-100: " + batteryLevel);
            return;
        }
        this.mBatteryLevel = batteryLevel;
    }

    public void setBatteryVoltage(float batteryVoltage) {
        if (batteryVoltage >= 0.0f || batteryVoltage == -1.0f) {
            this.mBatteryVoltage = batteryVoltage;
            return;
        }
        Logger logger = LOG;
        logger.error("Battery voltage must be > 0: " + batteryVoltage);
    }

    public float getBatteryVoltage() {
        return this.mBatteryVoltage;
    }

    public BatteryState getBatteryState() {
        return this.mBatteryState;
    }

    public void setBatteryState(BatteryState mBatteryState2) {
        this.mBatteryState = mBatteryState2;
    }

    public short getBatteryThresholdPercent() {
        return this.mBatteryThresholdPercent;
    }

    public void setBatteryThresholdPercent(short batteryThresholdPercent) {
        this.mBatteryThresholdPercent = batteryThresholdPercent;
    }

    public String toString() {
        return "Device " + getName() + ", " + getAddress() + ", " + getStateString(false);
    }

    public String getShortAddress() {
        String address = getAddress();
        if (address == null) {
            return "";
        }
        if (address.length() > 5) {
            return address.substring(address.length() - 5);
        }
        return address;
    }

    public boolean hasDeviceInfos() {
        return getDeviceInfos().size() > 0;
    }

    public ItemWithDetails getDeviceInfo(String name) {
        for (ItemWithDetails item : getDeviceInfos()) {
            if (name.equals(item.getName())) {
                return item;
            }
        }
        return null;
    }

    public List<ItemWithDetails> getDeviceInfos() {
        List<ItemWithDetails> result = new ArrayList<>();
        List<ItemWithDetails> list = this.mDeviceInfos;
        if (list != null) {
            result.addAll(list);
        }
        String str = this.mModel;
        if (str != null) {
            result.add(new GenericItem(DEVINFO_HW_VER, str));
        }
        String str2 = this.mFirmwareVersion;
        if (str2 != null) {
            result.add(new GenericItem(DEVINFO_FW_VER, str2));
        }
        if (this.mFirmwareVersion2 != null) {
            if (this.mDeviceType == DeviceType.AMAZFITBIP) {
                result.add(new GenericItem(DEVINFO_GPS_VER, this.mFirmwareVersion2));
            } else {
                result.add(new GenericItem(DEVINFO_HR_VER, this.mFirmwareVersion2));
            }
        }
        String str3 = this.mAddress;
        if (str3 != null) {
            result.add(new GenericItem(DEVINFO_ADDR, str3));
        }
        String str4 = this.mVolatileAddress;
        if (str4 != null) {
            result.add(new GenericItem(DEVINFO_ADDR2, str4));
        }
        Collections.sort(result);
        return result;
    }

    public void setDeviceInfos(List<ItemWithDetails> deviceInfos) {
        this.mDeviceInfos = deviceInfos;
    }

    public void addDeviceInfo(ItemWithDetails info) {
        List<ItemWithDetails> list = this.mDeviceInfos;
        if (list == null) {
            this.mDeviceInfos = new ArrayList();
        } else {
            int index = list.indexOf(info);
            if (index >= 0) {
                this.mDeviceInfos.set(index, info);
                return;
            }
        }
        this.mDeviceInfos.add(info);
    }

    public boolean removeDeviceInfo(ItemWithDetails info) {
        List<ItemWithDetails> list = this.mDeviceInfos;
        if (list == null) {
            return false;
        }
        return list.remove(info);
    }
}
