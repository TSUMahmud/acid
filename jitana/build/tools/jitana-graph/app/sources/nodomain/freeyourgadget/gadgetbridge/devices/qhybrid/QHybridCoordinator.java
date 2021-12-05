package nodomain.freeyourgadget.gadgetbridge.devices.qhybrid;

import android.app.Activity;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelUuid;
import java.util.Collection;
import java.util.Collections;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
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
import nodomain.freeyourgadget.gadgetbridge.model.ItemWithDetails;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport;

public class QHybridCoordinator extends AbstractDeviceCoordinator {
    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        for (ParcelUuid uuid : candidate.getServiceUuids()) {
            if (uuid.getUuid().toString().equals("3dda0001-957f-7d4a-34a6-74696673696d")) {
                return DeviceType.FOSSILQHYBRID;
            }
        }
        return DeviceType.UNKNOWN;
    }

    public Collection<? extends ScanFilter> createBLEScanFilters() {
        return Collections.singletonList(new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString("3dda0001-957f-7d4a-34a6-74696673696d")).build());
    }

    public DeviceType getDeviceType() {
        return DeviceType.FOSSILQHYBRID;
    }

    public Class<? extends Activity> getPairingActivity() {
        return null;
    }

    public boolean supportsActivityDataFetching() {
        return true;
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

    public boolean supportsAlarmConfiguration() {
        GBDevice connectedDevice = GBApplication.app().getDeviceManager().getSelectedDevice();
        if (connectedDevice != null && connectedDevice.getType() == DeviceType.FOSSILQHYBRID && connectedDevice.getState() == GBDevice.State.INITIALIZED) {
            return true;
        }
        return false;
    }

    public int getAlarmSlotCount() {
        return supportsAlarmConfiguration() ? 5 : 0;
    }

    public boolean supportsSmartWakeup(GBDevice device) {
        return false;
    }

    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return false;
    }

    public String getManufacturer() {
        return "Fossil";
    }

    public boolean supportsAppsManagement() {
        return true;
    }

    public Class<? extends Activity> getAppsManagementActivity() {
        return ConfigActivity.class;
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
        ItemWithDetails vibration;
        GBDevice connectedDevice = GBApplication.app().getDeviceManager().getSelectedDevice();
        if (connectedDevice == null || connectedDevice.getType() != DeviceType.FOSSILQHYBRID || (vibration = connectedDevice.getDeviceInfo(QHybridSupport.ITEM_EXTENDED_VIBRATION_SUPPORT)) == null) {
            return true;
        }
        return vibration.getDetails().equals("true");
    }

    /* access modifiers changed from: protected */
    public void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) throws GBException {
    }
}
