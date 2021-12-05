package nodomain.freeyourgadget.gadgetbridge.devices.jyou.p011y5;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.devices.jyou.JYouSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.JYouActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import p008de.greenrobot.dao.query.WhereCondition;

/* renamed from: nodomain.freeyourgadget.gadgetbridge.devices.jyou.y5.Y5Coordinator */
public class Y5Coordinator extends AbstractDeviceCoordinator {
    /* access modifiers changed from: protected */
    public void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) throws GBException {
        session.getJYouActivitySampleDao().queryBuilder().where(JYouActivitySampleDao.Properties.DeviceId.mo14989eq(device.getId()), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        try {
            String name = candidate.getDevice().getName();
            if (name != null && name.contains("Y5")) {
                return DeviceType.Y5;
            }
        } catch (Exception ex) {
            ex.getLocalizedMessage();
        }
        return DeviceType.UNKNOWN;
    }

    public DeviceType getDeviceType() {
        return DeviceType.Y5;
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
        return new JYouSampleProvider(device, session);
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
        return true;
    }

    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return true;
    }

    public String getManufacturer() {
        return "Y5";
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
        return true;
    }

    public boolean supportsWeather() {
        return false;
    }

    public boolean supportsFindDevice() {
        return true;
    }
}
