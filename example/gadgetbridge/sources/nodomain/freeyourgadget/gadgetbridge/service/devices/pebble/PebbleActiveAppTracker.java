package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import java.util.UUID;

public class PebbleActiveAppTracker {
    private UUID mCurrentRunningApp = null;
    private UUID mPreviousRunningApp = null;

    public UUID getPreviousRunningApp() {
        return this.mPreviousRunningApp;
    }

    public UUID getCurrentRunningApp() {
        return this.mCurrentRunningApp;
    }

    public void markAppClosed(UUID app) {
        if (this.mCurrentRunningApp == app) {
            UUID uuid = this.mPreviousRunningApp;
            if (uuid != null) {
                markAppOpened(uuid);
            } else {
                this.mCurrentRunningApp = null;
            }
        }
    }

    public void markAppOpened(UUID openedApp) {
        if (!openedApp.equals(this.mCurrentRunningApp)) {
            this.mPreviousRunningApp = this.mCurrentRunningApp;
            this.mCurrentRunningApp = openedApp;
        }
    }
}
