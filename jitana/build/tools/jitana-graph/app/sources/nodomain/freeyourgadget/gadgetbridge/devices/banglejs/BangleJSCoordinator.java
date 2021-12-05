package nodomain.freeyourgadget.gadgetbridge.devices.banglejs;

import android.app.Activity;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelUuid;
import java.util.Collection;
import java.util.Collections;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;

public class BangleJSCoordinator extends AbstractDeviceCoordinator {
    public DeviceType getDeviceType() {
        return DeviceType.BANGLEJS;
    }

    public String getManufacturer() {
        return "Espruino";
    }

    public Collection<? extends ScanFilter> createBLEScanFilters() {
        return Collections.singletonList(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(BangleJSConstants.UUID_SERVICE_NORDIC_UART)).build());
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        if (name == null || (!name.startsWith("Bangle.js") && !name.startsWith("Pixl.js") && !name.startsWith("Puck.js") && !name.startsWith("MDBT42Q") && !name.startsWith("Espruino"))) {
            return DeviceType.UNKNOWN;
        }
        return DeviceType.BANGLEJS;
    }

    public int getBondingStyle() {
        return 2;
    }

    public boolean supportsCalendarEvents() {
        return false;
    }

    public boolean supportsRealtimeData() {
        return false;
    }

    public boolean supportsWeather() {
        return true;
    }

    public boolean supportsFindDevice() {
        return true;
    }

    public boolean supportsActivityDataFetching() {
        return false;
    }

    public boolean supportsActivityTracking() {
        return false;
    }

    public boolean supportsScreenshots() {
        return false;
    }

    public boolean supportsSmartWakeup(GBDevice device) {
        return false;
    }

    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return false;
    }

    public boolean supportsAppsManagement() {
        return false;
    }

    public int getAlarmSlotCount() {
        return 10;
    }

    public Class<? extends Activity> getAppsManagementActivity() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) {
    }

    public Class<? extends Activity> getPairingActivity() {
        return null;
    }

    public SampleProvider<? extends ActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        return null;
    }

    public InstallHandler findInstallHandler(Uri uri, Context context) {
        return null;
    }
}
