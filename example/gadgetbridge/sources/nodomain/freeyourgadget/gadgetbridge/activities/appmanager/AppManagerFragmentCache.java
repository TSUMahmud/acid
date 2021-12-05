package nodomain.freeyourgadget.gadgetbridge.activities.appmanager;

import java.util.List;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp;

public class AppManagerFragmentCache extends AbstractAppManagerFragment {
    public void refreshList() {
        this.appList.clear();
        this.appList.addAll(getCachedApps((List<UUID>) null));
    }

    /* access modifiers changed from: protected */
    public boolean isCacheManager() {
        return true;
    }

    /* access modifiers changed from: protected */
    public List<GBDeviceApp> getSystemAppsInCategory() {
        return null;
    }

    public String getSortFilename() {
        return "pbwcacheorder.txt";
    }

    /* access modifiers changed from: protected */
    public boolean filterApp(GBDeviceApp gbDeviceApp) {
        return true;
    }
}
