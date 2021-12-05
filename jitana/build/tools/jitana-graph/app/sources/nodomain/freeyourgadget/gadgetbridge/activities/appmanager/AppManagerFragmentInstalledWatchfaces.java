package nodomain.freeyourgadget.gadgetbridge.activities.appmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp;

public class AppManagerFragmentInstalledWatchfaces extends AbstractAppManagerFragment {
    /* access modifiers changed from: protected */
    public List<GBDeviceApp> getSystemAppsInCategory() {
        List<GBDeviceApp> systemWatchfaces = new ArrayList<>();
        systemWatchfaces.add(new GBDeviceApp(UUID.fromString("8f3c8686-31a1-4f5f-91f5-01600c9bdc59"), "Tic Toc (System)", "Pebble Inc.", "", GBDeviceApp.Type.WATCHFACE_SYSTEM));
        systemWatchfaces.add(new GBDeviceApp(UUID.fromString("3af858c3-16cb-4561-91e7-f1ad2df8725f"), "Kickstart (System)", "Pebble Inc.", "", GBDeviceApp.Type.WATCHFACE_SYSTEM));
        return systemWatchfaces;
    }

    /* access modifiers changed from: protected */
    public boolean isCacheManager() {
        return false;
    }

    /* access modifiers changed from: protected */
    public String getSortFilename() {
        return this.mGBDevice.getAddress() + ".watchfaces";
    }

    /* access modifiers changed from: protected */
    public void onChangedAppOrder() {
        super.onChangedAppOrder();
        sendOrderToDevice(this.mGBDevice.getAddress() + ".watchapps");
    }

    /* access modifiers changed from: protected */
    public boolean filterApp(GBDeviceApp gbDeviceApp) {
        if (gbDeviceApp.getType() == GBDeviceApp.Type.WATCHFACE) {
            return true;
        }
        return false;
    }
}
