package nodomain.freeyourgadget.gadgetbridge.devices.vibratissimo;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;

public class VibratissimoCoordinator extends AbstractDeviceCoordinator {
    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        if (name == null || !name.startsWith("Vibratissimo")) {
            return DeviceType.UNKNOWN;
        }
        return DeviceType.VIBRATISSIMO;
    }

    public DeviceType getDeviceType() {
        return DeviceType.VIBRATISSIMO;
    }

    public Class<? extends Activity> getPairingActivity() {
        return null;
    }

    public InstallHandler findInstallHandler(Uri uri, Context context) {
        return null;
    }

    public boolean supportsActivityDataFetching() {
        return false;
    }

    public boolean supportsActivityTracking() {
        return false;
    }

    public SampleProvider<? extends ActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        return null;
    }

    public boolean supportsScreenshots() {
        return false;
    }

    public int getAlarmSlotCount() {
        return 0;
    }

    public boolean supportsSmartWakeup(GBDevice device) {
        return false;
    }

    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return false;
    }

    public String getManufacturer() {
        return "Amor AG";
    }

    public boolean supportsAppsManagement() {
        return false;
    }

    public Class<? extends Activity> getAppsManagementActivity() {
        return null;
    }

    public boolean supportsCalendarEvents() {
        return false;
    }

    public boolean supportsRealtimeData() {
        return false;
    }

    public boolean supportsWeather() {
        return false;
    }

    public boolean supportsFindDevice() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) {
    }
}
