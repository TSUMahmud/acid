package nodomain.freeyourgadget.gadgetbridge.devices.zetime;

import android.app.Activity;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.net.Uri;
import java.util.Collection;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;

public class ZeTimeCoordinator extends AbstractDeviceCoordinator {
    public DeviceType getDeviceType() {
        return DeviceType.ZETIME;
    }

    public Collection<? extends ScanFilter> createBLEScanFilters() {
        return super.createBLEScanFilters();
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        if (name == null || !name.startsWith("ZeTime")) {
            return DeviceType.UNKNOWN;
        }
        return DeviceType.ZETIME;
    }

    public Class<? extends Activity> getPairingActivity() {
        return null;
    }

    public boolean supportsScreenshots() {
        return false;
    }

    public int getAlarmSlotCount() {
        return 3;
    }

    public boolean supportsWeather() {
        return true;
    }

    public boolean supportsFindDevice() {
        return true;
    }

    public InstallHandler findInstallHandler(Uri uri, Context context) {
        return null;
    }

    public String getManufacturer() {
        return "MyKronoz";
    }

    public boolean supportsActivityTracking() {
        return true;
    }

    public Class<? extends Activity> getAppsManagementActivity() {
        return null;
    }

    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return true;
    }

    public boolean supportsActivityDataFetching() {
        return true;
    }

    public boolean supportsAppsManagement() {
        return false;
    }

    public boolean supportsCalendarEvents() {
        return true;
    }

    public SampleProvider<? extends ActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        return new ZeTimeSampleProvider(device, session);
    }

    /* access modifiers changed from: protected */
    public void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) {
    }

    public boolean supportsSmartWakeup(GBDevice device) {
        return false;
    }

    public boolean supportsRealtimeData() {
        return true;
    }

    public boolean supportsMusicInfo() {
        return true;
    }

    public int getBondingStyle() {
        return 0;
    }

    public boolean supportsUnicodeEmojis() {
        return true;
    }

    public int[] getSupportedDeviceSpecificSettings(GBDevice device) {
        return new int[]{C0889R.C0894xml.devicesettings_timeformat, C0889R.C0894xml.devicesettings_wearlocation};
    }
}
