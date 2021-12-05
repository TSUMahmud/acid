package nodomain.freeyourgadget.gadgetbridge.devices.casiogb6900;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
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

public class CasioGB6900DeviceCoordinator extends AbstractDeviceCoordinator {
    protected static final Logger LOG = LoggerFactory.getLogger((Class<?>) CasioGB6900DeviceCoordinator.class);

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        if (candidate.supportsService(CasioGB6900Constants.CASIO_VIRTUAL_SERVER_SERVICE)) {
            return DeviceType.CASIOGB6900;
        }
        String name = candidate.getDevice().getName();
        if (name == null || !name.startsWith("CASIO")) {
            return DeviceType.UNKNOWN;
        }
        return DeviceType.CASIOGB6900;
    }

    public int getBondingStyle() {
        return 1;
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

    public DeviceType getDeviceType() {
        return DeviceType.CASIOGB6900;
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
        return 5;
    }

    public boolean supportsSmartWakeup(GBDevice device) {
        return false;
    }

    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return false;
    }

    public String getManufacturer() {
        return "Casio";
    }

    public boolean supportsAppsManagement() {
        return false;
    }

    public Class<? extends Activity> getAppsManagementActivity() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) throws GBException {
    }
}
