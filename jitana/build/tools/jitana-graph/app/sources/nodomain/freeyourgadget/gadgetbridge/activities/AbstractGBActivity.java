package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.Locale;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.util.AndroidUtils;

public abstract class AbstractGBActivity extends AppCompatActivity implements GBActivity {
    public static final int NONE = 0;
    public static final int NO_ACTIONBAR = 1;
    private boolean isLanguageInvalid = false;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                char c = 65535;
                int hashCode = action.hashCode();
                if (hashCode != -812299209) {
                    if (hashCode == 208140431 && action.equals(GBApplication.ACTION_QUIT)) {
                        c = 1;
                    }
                } else if (action.equals(GBApplication.ACTION_LANGUAGE_CHANGE)) {
                    c = 0;
                }
                if (c == 0) {
                    AbstractGBActivity.this.setLanguage(GBApplication.getLanguage(), true);
                } else if (c == 1) {
                    AbstractGBActivity.this.finish();
                }
            }
        }
    };

    public void setLanguage(Locale language, boolean invalidateLanguage) {
        if (invalidateLanguage) {
            this.isLanguageInvalid = true;
        }
        AndroidUtils.setLanguage(this, language);
    }

    public static void init(GBActivity activity) {
        init(activity, 0);
    }

    public static void init(GBActivity activity, int flags) {
        if (GBApplication.isDarkThemeEnabled()) {
            if ((flags & 1) != 0) {
                activity.setTheme(C0889R.style.GadgetbridgeThemeDark_NoActionBar);
            } else {
                activity.setTheme(C0889R.style.GadgetbridgeThemeDark);
            }
        } else if ((flags & 1) != 0) {
            activity.setTheme(C0889R.style.GadgetbridgeTheme_NoActionBar);
        } else {
            activity.setTheme(C0889R.style.GadgetbridgeTheme);
        }
        activity.setLanguage(GBApplication.getLanguage(), false);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(GBApplication.ACTION_QUIT);
        filterLocal.addAction(GBApplication.ACTION_LANGUAGE_CHANGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, filterLocal);
        init(this);
        super.onCreate(savedInstanceState);
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
}
