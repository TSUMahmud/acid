package nodomain.freeyourgadget.gadgetbridge.activities.appmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp;
import nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol;
import nodomain.freeyourgadget.gadgetbridge.util.PebbleUtils;

public class AppManagerFragmentInstalledApps extends AbstractAppManagerFragment {
    /* access modifiers changed from: protected */
    public List<GBDeviceApp> getSystemAppsInCategory() {
        List<GBDeviceApp> systemApps = new ArrayList<>();
        systemApps.add(new GBDeviceApp(UUID.fromString("1f03293d-47af-4f28-b960-f2b02a6dd757"), "Music (System)", "Pebble Inc.", "", GBDeviceApp.Type.APP_SYSTEM));
        systemApps.add(new GBDeviceApp(PebbleProtocol.UUID_NOTIFICATIONS, "Notifications (System)", "Pebble Inc.", "", GBDeviceApp.Type.APP_SYSTEM));
        systemApps.add(new GBDeviceApp(UUID.fromString("67a32d95-ef69-46d4-a0b9-854cc62f97f9"), "Alarms (System)", "Pebble Inc.", "", GBDeviceApp.Type.APP_SYSTEM));
        systemApps.add(new GBDeviceApp(UUID.fromString("18e443ce-38fd-47c8-84d5-6d0c775fbe55"), "Watchfaces (System)", "Pebble Inc.", "", GBDeviceApp.Type.APP_SYSTEM));
        if (this.mGBDevice != null) {
            if (PebbleUtils.hasHealth(this.mGBDevice.getModel())) {
                systemApps.add(new GBDeviceApp(UUID.fromString("0863fc6a-66c5-4f62-ab8a-82ed00a98b5d"), "Send Text (System)", "Pebble Inc.", "", GBDeviceApp.Type.APP_SYSTEM));
                systemApps.add(new GBDeviceApp(PebbleProtocol.UUID_PEBBLE_HEALTH, "Health (System)", "Pebble Inc.", "", GBDeviceApp.Type.APP_SYSTEM));
            }
            if (PebbleUtils.hasHRM(this.mGBDevice.getModel())) {
                systemApps.add(new GBDeviceApp(PebbleProtocol.UUID_WORKOUT, "Workout (System)", "Pebble Inc.", "", GBDeviceApp.Type.APP_SYSTEM));
            }
            if (PebbleUtils.getFwMajor(this.mGBDevice.getFirmwareVersion()) >= 4) {
                systemApps.add(new GBDeviceApp(PebbleProtocol.UUID_WEATHER, "Weather (System)", "Pebble Inc.", "", GBDeviceApp.Type.APP_SYSTEM));
            }
        }
        return systemApps;
    }

    /* access modifiers changed from: protected */
    public boolean isCacheManager() {
        return false;
    }

    /* access modifiers changed from: protected */
    public String getSortFilename() {
        return this.mGBDevice.getAddress() + ".watchapps";
    }

    /* access modifiers changed from: protected */
    public void onChangedAppOrder() {
        super.onChangedAppOrder();
        sendOrderToDevice(this.mGBDevice.getAddress() + ".watchfaces");
    }

    /* access modifiers changed from: protected */
    public boolean filterApp(GBDeviceApp gbDeviceApp) {
        return gbDeviceApp.getType() == GBDeviceApp.Type.APP_ACTIVITYTRACKER || gbDeviceApp.getType() == GBDeviceApp.Type.APP_GENERIC;
    }
}
