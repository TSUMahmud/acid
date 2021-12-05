package nodomain.freeyourgadget.gadgetbridge.devices.pebble;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.activities.appmanager.AppManagerActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.AbstractActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleHealthActivityOverlayDao;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleHealthActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleMisfitSampleDao;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleMorpheuzSampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.util.PebbleUtils;
import p008de.greenrobot.dao.query.WhereCondition;

public class PebbleCoordinator extends AbstractDeviceCoordinator {
    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        if (name == null || !name.startsWith("Pebble")) {
            return DeviceType.UNKNOWN;
        }
        return DeviceType.PEBBLE;
    }

    public DeviceType getDeviceType() {
        return DeviceType.PEBBLE;
    }

    public Class<? extends Activity> getPairingActivity() {
        return PebblePairingActivity.class;
    }

    /* access modifiers changed from: protected */
    public void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) throws GBException {
        Long deviceId = device.getId();
        session.getPebbleHealthActivitySampleDao().queryBuilder().where(PebbleHealthActivitySampleDao.Properties.DeviceId.mo14989eq(deviceId), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
        session.getPebbleHealthActivityOverlayDao().queryBuilder().where(PebbleHealthActivityOverlayDao.Properties.DeviceId.mo14989eq(deviceId), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
        session.getPebbleMisfitSampleDao().queryBuilder().where(PebbleMisfitSampleDao.Properties.DeviceId.mo14989eq(deviceId), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
        session.getPebbleMorpheuzSampleDao().queryBuilder().where(PebbleMorpheuzSampleDao.Properties.DeviceId.mo14989eq(deviceId), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public SampleProvider<? extends AbstractActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        int activityTracker = GBApplication.getPrefs().getInt("pebble_activitytracker", 4);
        if (activityTracker == 1) {
            return new PebbleMorpheuzSampleProvider(device, session);
        }
        if (activityTracker != 3) {
            return activityTracker != 4 ? new PebbleHealthSampleProvider(device, session) : new PebbleHealthSampleProvider(device, session);
        }
        return new PebbleMisfitSampleProvider(device, session);
    }

    public InstallHandler findInstallHandler(Uri uri, Context context) {
        PBWInstallHandler installHandler = new PBWInstallHandler(uri, context);
        if (installHandler.isValid()) {
            return installHandler;
        }
        return null;
    }

    public boolean supportsActivityDataFetching() {
        return false;
    }

    public boolean supportsActivityTracking() {
        return true;
    }

    public boolean supportsScreenshots() {
        return true;
    }

    public int getAlarmSlotCount() {
        return 0;
    }

    public boolean supportsSmartWakeup(GBDevice device) {
        return false;
    }

    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return PebbleUtils.hasHRM(device.getModel());
    }

    public String getManufacturer() {
        return "Pebble";
    }

    public boolean supportsAppsManagement() {
        return true;
    }

    public Class<? extends Activity> getAppsManagementActivity() {
        return AppManagerActivity.class;
    }

    public boolean supportsCalendarEvents() {
        return true;
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

    public boolean supportsMusicInfo() {
        return true;
    }

    public boolean supportsUnicodeEmojis() {
        return true;
    }
}
