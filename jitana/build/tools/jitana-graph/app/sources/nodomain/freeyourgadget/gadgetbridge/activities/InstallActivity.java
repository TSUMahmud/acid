package nodomain.freeyourgadget.gadgetbridge.activities;

import nodomain.freeyourgadget.gadgetbridge.model.ItemWithDetails;

public interface InstallActivity {
    void clearInstallItems();

    CharSequence getInfoText();

    void setInfoText(String str);

    void setInstallEnabled(boolean z);

    void setInstallItem(ItemWithDetails itemWithDetails);
}
