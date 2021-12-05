package de.baumann.browser.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;

import de.baumann.browser.activity.Settings_Delete;
import de.baumann.browser.activity.Settings_Backup;
import de.baumann.browser.activity.Settings_Filter;
import de.baumann.browser.activity.Settings_Gesture;
import de.baumann.browser.activity.Settings_PrivacyActivity;
import de.baumann.browser.activity.Settings_UI;
import de.baumann.browser.R;

public class Fragment_settings extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.preference_setting, rootKey);
        Context context = getContext();
        assert context != null;
        initSummary(getPreferenceScreen());

        Preference settings_filter = findPreference("settings_filter");
        assert settings_filter != null;
        settings_filter.setOnPreferenceClickListener(preference -> {
           Intent intent = new Intent(getActivity(), Settings_Filter.class);
           requireActivity().startActivity(intent);
           return false;
        });

        Preference settings_data = findPreference("settings_data");
        assert settings_data != null;
        settings_data.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), Settings_Backup.class);
            requireActivity().startActivity(intent);
            return false;
        });

        Preference settings_ui = findPreference("settings_ui");
        assert settings_ui != null;
        settings_ui.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), Settings_UI.class);
            requireActivity().startActivity(intent);
            return false;
        });

        Preference settings_gesture = findPreference("settings_gesture");
        assert settings_gesture != null;
        settings_gesture.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), Settings_Gesture.class);
            requireActivity().startActivity(intent);
            return false;
        });

        Preference settings_start = findPreference("settings_start");
        assert settings_start != null;
        settings_start.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), Settings_PrivacyActivity.class);
            requireActivity().startActivity(intent);
            return false;
        });

        Preference settings_clear = findPreference("settings_clear");
        assert settings_clear != null;
        settings_clear.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), Settings_Delete.class);
            requireActivity().startActivity(intent);
            return false;
        });
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getTitle().toString().toLowerCase().contains("password")) {
                p.setSummary("******");
            } else {
                if (p.getSummaryProvider()==null)   p.setSummary(editTextPref.getText());
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sp, String key) {
        if (key.equals("sp_userAgent") ||
                key.equals("sp_search_engine_custom") ||
                key.equals("searchEngineSwitch") ||
                key.equals("userAgentSwitch") ||
                key.equals("sp_search_engine")) {
            sp.edit().putInt("restart_changed", 1).apply();
            updatePrefSummary(findPreference(key));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}