package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import android.os.Bundle;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractSettingsActivity;

public class ChartsPreferencesActivity extends AbstractSettingsActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(C0889R.C0894xml.charts_preferences);
    }
}
