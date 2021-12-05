package nodomain.freeyourgadget.gadgetbridge.util;

import android.content.SharedPreferences;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

public class Prefs {
    private static final String TAG = "Prefs";
    private final SharedPreferences preferences;

    public Prefs(SharedPreferences preferences2) {
        this.preferences = preferences2;
    }

    public String getString(String key, String defaultValue) {
        String value = this.preferences.getString(key, defaultValue);
        if (value == null || "".equals(value)) {
            return defaultValue;
        }
        return value;
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        Set<String> value = this.preferences.getStringSet(key, defaultValue);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return value;
    }

    public int getInt(String key, int defaultValue) {
        try {
            return this.preferences.getInt(key, defaultValue);
        } catch (Exception ex) {
            try {
                String value = this.preferences.getString(key, String.valueOf(defaultValue));
                if ("".equals(value)) {
                    return defaultValue;
                }
                return Integer.parseInt(value);
            } catch (Exception e) {
                logReadError(key, ex);
                return defaultValue;
            }
        }
    }

    public long getLong(String key, long defaultValue) {
        try {
            return this.preferences.getLong(key, defaultValue);
        } catch (Exception ex) {
            try {
                String value = this.preferences.getString(key, String.valueOf(defaultValue));
                if ("".equals(value)) {
                    return defaultValue;
                }
                return Long.parseLong(value);
            } catch (Exception e) {
                logReadError(key, ex);
                return defaultValue;
            }
        }
    }

    public float getFloat(String key, float defaultValue) {
        try {
            return this.preferences.getFloat(key, defaultValue);
        } catch (Exception ex) {
            try {
                String value = this.preferences.getString(key, String.valueOf(defaultValue));
                if ("".equals(value)) {
                    return defaultValue;
                }
                return Float.parseFloat(value);
            } catch (Exception e) {
                logReadError(key, ex);
                return defaultValue;
            }
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return this.preferences.getBoolean(key, defaultValue);
        } catch (Exception ex) {
            try {
                String value = this.preferences.getString(key, String.valueOf(defaultValue));
                if ("".equals(value)) {
                    return defaultValue;
                }
                return Boolean.parseBoolean(value);
            } catch (Exception e) {
                logReadError(key, ex);
                return defaultValue;
            }
        }
    }

    private void logReadError(String key, Exception ex) {
        Log.e(TAG, "Error reading preference value: " + key + "; returning default value", ex);
    }

    public SharedPreferences getPreferences() {
        return this.preferences;
    }

    public static void putStringSet(SharedPreferences.Editor editor, String preference, HashSet<String> value) {
        editor.putStringSet(preference, (Set) null);
        editor.commit();
        editor.putStringSet(preference, new HashSet(value));
    }
}
