package nodomain.freeyourgadget.gadgetbridge.devices.jyou;

import android.app.Activity;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelUuid;
import java.util.Collection;
import java.util.Collections;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BFH16DeviceCoordinator extends AbstractDeviceCoordinator {
    protected static final Logger LOG = LoggerFactory.getLogger((Class<?>) BFH16DeviceCoordinator.class);

    public DeviceType getDeviceType() {
        return DeviceType.BFH16;
    }

    public String getManufacturer() {
        return "Denver";
    }

    public Collection<? extends ScanFilter> createBLEScanFilters() {
        ParcelUuid bfhService1 = new ParcelUuid(BFH16Constants.BFH16_IDENTIFICATION_SERVICE1);
        return Collections.singletonList(new ScanFilter.Builder().setServiceUuid(bfhService1).setServiceUuid(new ParcelUuid(BFH16Constants.BFH16_IDENTIFICATION_SERVICE2)).build());
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        if (name == null || !name.startsWith("BFH-16")) {
            return DeviceType.UNKNOWN;
        }
        return DeviceType.BFH16;
    }

    public int getBondingStyle() {
        return 0;
    }

    public Class<? extends Activity> getPairingActivity() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) throws GBException {
    }

    public SampleProvider<? extends ActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        return null;
    }

    public InstallHandler findInstallHandler(Uri uri, Context context) {
        return null;
    }

    public Class<? extends Activity> getAppsManagementActivity() {
        return null;
    }

    public int getAlarmSlotCount() {
        return 3;
    }

    public boolean supportsFindDevice() {
        return true;
    }

    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return true;
    }

    public boolean supportsRealtimeData() {
        return true;
    }

    public boolean supportsActivityDataFetching() {
        return false;
    }

    public boolean supportsActivityTracking() {
        return false;
    }

    public boolean supportsAppsManagement() {
        return false;
    }

    public boolean supportsCalendarEvents() {
        return false;
    }

    public boolean supportsLedColor() {
        return false;
    }

    public boolean supportsMusicInfo() {
        return false;
    }

    public boolean supportsRgbLedColor() {
        return false;
    }

    public boolean supportsScreenshots() {
        return false;
    }

    public boolean supportsSmartWakeup(GBDevice device) {
        return false;
    }

    public boolean supportsWeather() {
        return false;
    }
}
