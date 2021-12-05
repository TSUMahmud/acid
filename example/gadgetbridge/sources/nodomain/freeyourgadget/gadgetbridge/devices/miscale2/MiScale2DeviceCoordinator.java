package nodomain.freeyourgadget.gadgetbridge.devices.miscale2;

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
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiScale2DeviceCoordinator extends AbstractDeviceCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) MiScale2DeviceCoordinator.class);

    /* access modifiers changed from: protected */
    public void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) throws GBException {
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        try {
            String name = candidate.getDevice().getName();
            if (name != null && name.equalsIgnoreCase("MIBCS")) {
                return DeviceType.MISCALE2;
            }
        } catch (Exception ex) {
            LOG.error("unable to check device support", (Throwable) ex);
        }
        return DeviceType.UNKNOWN;
    }

    public Collection<? extends ScanFilter> createBLEScanFilters() {
        ParcelUuid bodyCompositionService = new ParcelUuid(GattService.UUID_SERVICE_BODY_COMPOSITION);
        ScanFilter.Builder builder = new ScanFilter.Builder();
        builder.setServiceUuid(bodyCompositionService);
        builder.setManufacturerData(343, new byte[6], new byte[6]);
        return Collections.singletonList(builder.build());
    }

    public int getBondingStyle() {
        return 0;
    }

    public DeviceType getDeviceType() {
        return DeviceType.MISCALE2;
    }

    public Class<? extends Activity> getPairingActivity() {
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

    public InstallHandler findInstallHandler(Uri uri, Context context) {
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
        return "Huami";
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
        return false;
    }
}
