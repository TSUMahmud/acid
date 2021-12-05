package nodomain.freeyourgadget.gadgetbridge.devices.jyou.TeclastH30;

import android.app.Activity;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelUuid;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.devices.jyou.JYouConstants;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeclastH30Coordinator extends AbstractDeviceCoordinator {
    protected static final Logger LOG = LoggerFactory.getLogger((Class<?>) TeclastH30Coordinator.class);
    private Pattern deviceNamePattern = Pattern.compile("^H[13]-[ABCDEF0123456789]{4}$");

    public Collection<? extends ScanFilter> createBLEScanFilters() {
        return Collections.singletonList(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(JYouConstants.UUID_SERVICE_JYOU)).build());
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        if (candidate.supportsService(JYouConstants.UUID_SERVICE_JYOU)) {
            return DeviceType.TECLASTH30;
        }
        String name = candidate.getDevice().getName();
        if (name != null) {
            if (name.startsWith("TECLAST_H30") || name.startsWith("TECLAST_H10")) {
                return DeviceType.TECLASTH30;
            }
            if (this.deviceNamePattern.matcher(name).matches()) {
                return DeviceType.TECLASTH30;
            }
        }
        return DeviceType.UNKNOWN;
    }

    public int getBondingStyle() {
        return 0;
    }

    public boolean supportsCalendarEvents() {
        return false;
    }

    public boolean supportsRealtimeData() {
        return true;
    }

    public boolean supportsWeather() {
        return false;
    }

    public boolean supportsFindDevice() {
        return true;
    }

    public DeviceType getDeviceType() {
        return DeviceType.TECLASTH30;
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
        return 3;
    }

    public boolean supportsSmartWakeup(GBDevice device) {
        return false;
    }

    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return true;
    }

    public String getManufacturer() {
        return "Teclast";
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
