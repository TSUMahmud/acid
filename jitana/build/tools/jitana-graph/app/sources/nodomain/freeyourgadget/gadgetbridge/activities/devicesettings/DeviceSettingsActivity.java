package nodomain.freeyourgadget.gadgetbridge.activities.devicesettings;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractGBActivity;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceSettingsActivity extends AbstractGBActivity implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DeviceSettingsActivity.class);
    GBDevice device;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        this.device = (GBDevice) getIntent().getParcelableExtra(GBDevice.EXTRA_DEVICE);
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_device_settings);
        if (savedInstanceState == null) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("DEVICE_SPECIFIC_SETTINGS_FRAGMENT");
            if (fragment == null) {
                fragment = DeviceSpecificSettingsFragment.newInstance(this.device.getAddress(), DeviceHelper.getInstance().getCoordinator(this.device).getSupportedDeviceSpecificSettings(this.device));
            }
            getSupportFragmentManager().beginTransaction().replace(C0889R.C0891id.settings_container, fragment, "DEVICE_SPECIFIC_SETTINGS_FRAGMENT").commit();
        }
    }

    public boolean onPreferenceStartScreen(PreferenceFragmentCompat caller, PreferenceScreen preferenceScreen) {
        PreferenceFragmentCompat fragment = DeviceSpecificSettingsFragment.newInstance(this.device.getAddress(), DeviceHelper.getInstance().getCoordinator(this.device).getSupportedDeviceSpecificSettings(this.device));
        Bundle args = fragment.getArguments();
        args.putString("androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT", preferenceScreen.getKey());
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(C0889R.C0891id.settings_container, fragment, preferenceScreen.getKey()).addToBackStack(preferenceScreen.getKey()).commit();
        return true;
    }
}
