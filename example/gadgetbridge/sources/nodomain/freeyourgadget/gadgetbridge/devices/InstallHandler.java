package nodomain.freeyourgadget.gadgetbridge.devices;

import nodomain.freeyourgadget.gadgetbridge.activities.InstallActivity;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

public interface InstallHandler {
    boolean isValid();

    void onStartInstall(GBDevice gBDevice);

    void validateInstallation(InstallActivity installActivity, GBDevice gBDevice);
}
