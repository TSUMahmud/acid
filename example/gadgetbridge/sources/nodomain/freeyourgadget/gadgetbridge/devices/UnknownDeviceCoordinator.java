package nodomain.freeyourgadget.gadgetbridge.devices;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2;
import nodomain.freeyourgadget.gadgetbridge.entities.AbstractActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;

public class UnknownDeviceCoordinator extends AbstractDeviceCoordinator {
    private final UnknownSampleProvider sampleProvider = new UnknownSampleProvider();

    private static final class UnknownSampleProvider implements SampleProvider {
        private UnknownSampleProvider() {
        }

        public int normalizeType(int rawType) {
            return 0;
        }

        public int toRawActivityKind(int activityKind) {
            return 0;
        }

        public float normalizeIntensity(int rawIntensity) {
            return 0.0f;
        }

        public List getAllActivitySamples(int timestamp_from, int timestamp_to) {
            return null;
        }

        public List getActivitySamples(int timestamp_from, int timestamp_to) {
            return null;
        }

        public List getSleepSamples(int timestamp_from, int timestamp_to) {
            return null;
        }

        public void addGBActivitySample(AbstractActivitySample activitySample) {
        }

        public void addGBActivitySamples(AbstractActivitySample[] activitySamples) {
        }

        public AbstractActivitySample createActivitySample() {
            return null;
        }

        public AbstractActivitySample getLatestActivitySample() {
            return null;
        }
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        return DeviceType.UNKNOWN;
    }

    /* access modifiers changed from: protected */
    public void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) throws GBException {
    }

    public DeviceType getDeviceType() {
        return DeviceType.UNKNOWN;
    }

    public Class<? extends Activity> getPairingActivity() {
        return ControlCenterv2.class;
    }

    public SampleProvider<?> getSampleProvider(GBDevice device, DaoSession session) {
        return new UnknownSampleProvider();
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
        return "unknown";
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

    public boolean supportsLedColor() {
        return false;
    }

    public boolean supportsRgbLedColor() {
        return false;
    }

    public int[] getColorPresets() {
        return new int[0];
    }
}
