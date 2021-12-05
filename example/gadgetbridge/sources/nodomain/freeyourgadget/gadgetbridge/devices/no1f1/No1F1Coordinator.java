package nodomain.freeyourgadget.gadgetbridge.devices.no1f1;

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
import nodomain.freeyourgadget.gadgetbridge.entities.No1F1ActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import p008de.greenrobot.dao.query.WhereCondition;

public class No1F1Coordinator extends AbstractDeviceCoordinator {
    public Collection<? extends ScanFilter> createBLEScanFilters() {
        return Collections.singletonList(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(No1F1Constants.UUID_SERVICE_NO1)).build());
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        if (name == null || (!name.startsWith("X-RUN") && !name.startsWith("MH30"))) {
            return DeviceType.UNKNOWN;
        }
        return DeviceType.NO1F1;
    }

    public DeviceType getDeviceType() {
        return DeviceType.NO1F1;
    }

    public int getBondingStyle() {
        return 0;
    }

    public Class<? extends Activity> getPairingActivity() {
        return null;
    }

    public boolean supportsActivityDataFetching() {
        return true;
    }

    public boolean supportsActivityTracking() {
        return true;
    }

    public SampleProvider<? extends ActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        return new No1F1SampleProvider(device, session);
    }

    public InstallHandler findInstallHandler(Uri uri, Context context) {
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
        return "NO1";
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
    public void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) throws GBException {
        session.getNo1F1ActivitySampleDao().queryBuilder().where(No1F1ActivitySampleDao.Properties.DeviceId.mo14989eq(device.getId()), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
    }
}
