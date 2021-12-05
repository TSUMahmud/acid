package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import androidx.core.app.NavUtils;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.Locale;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.util.AndroidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSettingsActivity extends AppCompatPreferenceActivity implements GBActivity {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractSettingsActivity.class);
    private static final SimpleSetSummaryOnChangeListener sBindPreferenceSummaryToValueListener = new SimpleSetSummaryOnChangeListener();
    private boolean isLanguageInvalid = false;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* JADX WARNING: Removed duplicated region for block: B:12:0x002b  */
        /* JADX WARNING: Removed duplicated region for block: B:14:0x0034  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r5, android.content.Intent r6) {
            /*
                r4 = this;
                java.lang.String r0 = r6.getAction()
                int r1 = r0.hashCode()
                r2 = -812299209(0xffffffffcf954c37, float:-5.0096E9)
                r3 = 1
                if (r1 == r2) goto L_0x001e
                r2 = 208140431(0xc67f88f, float:1.787039E-31)
                if (r1 == r2) goto L_0x0014
            L_0x0013:
                goto L_0x0028
            L_0x0014:
                java.lang.String r1 = "nodomain.freeyourgadget.gadgetbridge.gbapplication.action.quit"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0013
                r1 = 1
                goto L_0x0029
            L_0x001e:
                java.lang.String r1 = "nodomain.freeyourgadget.gadgetbridge.gbapplication.action.language_change"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0013
                r1 = 0
                goto L_0x0029
            L_0x0028:
                r1 = -1
            L_0x0029:
                if (r1 == 0) goto L_0x0034
                if (r1 == r3) goto L_0x002e
                goto L_0x003e
            L_0x002e:
                nodomain.freeyourgadget.gadgetbridge.activities.AbstractSettingsActivity r1 = nodomain.freeyourgadget.gadgetbridge.activities.AbstractSettingsActivity.this
                r1.finish()
                goto L_0x003e
            L_0x0034:
                nodomain.freeyourgadget.gadgetbridge.activities.AbstractSettingsActivity r1 = nodomain.freeyourgadget.gadgetbridge.activities.AbstractSettingsActivity.this
                java.util.Locale r2 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getLanguage()
                r1.setLanguage(r2, r3)
            L_0x003e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.AbstractSettingsActivity.C08971.onReceive(android.content.Context, android.content.Intent):void");
        }
    };

    private static class SimpleSetSummaryOnChangeListener implements Preference.OnPreferenceChangeListener {
        private SimpleSetSummaryOnChangeListener() {
        }

        public boolean onPreferenceChange(Preference preference, Object value) {
            if ((preference instanceof EditTextPreference) && (((EditTextPreference) preference).getEditText().getKeyListener().getInputType() & 2) != 0 && "".equals(String.valueOf(value))) {
                return false;
            }
            updateSummary(preference, value);
            return true;
        }

        public void updateSummary(Preference preference, Object value) {
            String stringValue = String.valueOf(value);
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
                return;
            }
            preference.setSummary(stringValue);
        }
    }

    private static class ExtraSetSummaryOnChangeListener extends SimpleSetSummaryOnChangeListener {
        private final Preference.OnPreferenceChangeListener prefChangeListener;

        public ExtraSetSummaryOnChangeListener(Preference.OnPreferenceChangeListener prefChangeListener2) {
            super();
            this.prefChangeListener = prefChangeListener2;
        }

        public boolean onPreferenceChange(Preference preference, Object value) {
            if (this.prefChangeListener.onPreferenceChange(preference, value)) {
                return super.onPreferenceChange(preference, value);
            }
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        AbstractGBActivity.init(this);
        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(GBApplication.ACTION_QUIT);
        filterLocal.addAction(GBApplication.ACTION_LANGUAGE_CHANGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, filterLocal);
        super.onCreate(savedInstanceState);
    }

    /* access modifiers changed from: protected */
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        for (String prefKey : getPreferenceKeysWithSummary()) {
            Preference pref = findPreference(prefKey);
            if (pref != null) {
                bindPreferenceSummaryToValue(pref);
            } else {
                LOG.error("Unknown preference key: " + prefKey + ", unable to display value.");
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.isLanguageInvalid) {
            this.isLanguageInvalid = false;
            recreate();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mReceiver);
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public String[] getPreferenceKeysWithSummary() {
        return new String[0];
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        SimpleSetSummaryOnChangeListener listener;
        Preference.OnPreferenceChangeListener existingListener = preference.getOnPreferenceChangeListener();
        if (existingListener != null) {
            listener = new ExtraSetSummaryOnChangeListener(existingListener);
        } else {
            listener = sBindPreferenceSummaryToValueListener;
        }
        preference.setOnPreferenceChangeListener(listener);
        try {
            listener.updateSummary(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
        } catch (ClassCastException e) {
            listener.updateSummary(preference, preference.getSummary());
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }

    public void setLanguage(Locale language, boolean invalidateLanguage) {
        if (invalidateLanguage) {
            this.isLanguageInvalid = true;
        }
        AndroidUtils.setLanguage(this, language);
    }
}
