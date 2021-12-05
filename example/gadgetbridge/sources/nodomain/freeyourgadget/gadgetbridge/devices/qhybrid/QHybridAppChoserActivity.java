package nodomain.freeyourgadget.gadgetbridge.devices.qhybrid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractGBActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.TimePicker;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;

public class QHybridAppChoserActivity extends AbstractGBActivity {
    boolean hasControl = false;
    PackageConfigHelper helper;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_qhybrid_app_choser);
        try {
            this.helper = new PackageConfigHelper(getApplicationContext());
            final ListView appList = (ListView) findViewById(C0889R.C0891id.qhybrid_appChooserList);
            final PackageManager manager = getPackageManager();
            final List<PackageInfo> packages = manager.getInstalledPackages(0);
            new Thread(new Runnable() {
                public void run() {
                    final IdentityHashMap<PackageInfo, String> nameMap = new IdentityHashMap<>(packages.size());
                    for (PackageInfo info : packages) {
                        CharSequence label = manager.getApplicationLabel(info.applicationInfo);
                        if (label == null) {
                            label = info.packageName;
                        }
                        nameMap.put(info, label.toString());
                    }
                    Collections.sort(packages, new Comparator<PackageInfo>() {
                        public int compare(PackageInfo packageInfo, PackageInfo t1) {
                            return ((String) nameMap.get(packageInfo)).compareToIgnoreCase((String) nameMap.get(t1));
                        }
                    });
                    QHybridAppChoserActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            appList.setAdapter(new ConfigArrayAdapter(QHybridAppChoserActivity.this, C0889R.layout.qhybrid_app_view, packages, manager));
                            QHybridAppChoserActivity.this.findViewById(C0889R.C0891id.qhybrid_packageChooserLoading).setVisibility(8);
                        }
                    });
                }
            }).start();
            appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    QHybridAppChoserActivity.this.showPackageDialog((PackageInfo) packages.get(i));
                }
            });
        } catch (GBException e) {
            C1238GB.log("database error", 3, e);
            C1238GB.toast("error getting database helper", 0, 3, (Throwable) e);
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }

    /* access modifiers changed from: private */
    public void setControl(boolean control) {
        if (this.hasControl != control) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(control ? QHybridSupport.QHYBRID_COMMAND_CONTROL : QHybridSupport.QHYBRID_COMMAND_UNCONTROL));
            this.hasControl = control;
        }
    }

    /* access modifiers changed from: private */
    public void setHands(NotificationConfiguration config) {
        sendControl(config, QHybridSupport.QHYBRID_COMMAND_SET);
    }

    /* access modifiers changed from: private */
    public void vibrate(NotificationConfiguration config) {
        sendControl(config, QHybridSupport.QHYBRID_COMMAND_VIBRATE);
    }

    private void sendControl(NotificationConfiguration config, String request) {
        Intent intent = new Intent(request);
        intent.putExtra("CONFIG", config);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /* access modifiers changed from: private */
    public void showPackageDialog(PackageInfo info) {
        TimePicker picker = new TimePicker((Context) this, info);
        picker.finishListener = new TimePicker.OnFinishListener() {
            public void onFinish(boolean success, NotificationConfiguration config) {
                QHybridAppChoserActivity.this.setControl(false);
                if (success) {
                    try {
                        QHybridAppChoserActivity.this.helper.saveNotificationConfiguration(config);
                        LocalBroadcastManager.getInstance(QHybridAppChoserActivity.this).sendBroadcast(new Intent(QHybridSupport.QHYBRID_COMMAND_NOTIFICATION_CONFIG_CHANGED));
                    } catch (GBException e) {
                        C1238GB.log("error saving config", 3, e);
                        C1238GB.toast("error saving configuration", 0, 3, (Throwable) e);
                    }
                    QHybridAppChoserActivity.this.finish();
                }
            }
        };
        picker.handsListener = new TimePicker.OnHandsSetListener() {
            public void onHandsSet(NotificationConfiguration config) {
                QHybridAppChoserActivity.this.setHands(config);
            }
        };
        picker.vibrationListener = new TimePicker.OnVibrationSetListener() {
            public void onVibrationSet(NotificationConfiguration config) {
                QHybridAppChoserActivity.this.vibrate(config);
            }
        };
        setControl(true);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        setControl(false);
        finish();
    }

    class ConfigArrayAdapter extends ArrayAdapter<PackageInfo> {
        PackageManager manager;

        public ConfigArrayAdapter(Context context, int resource, List<PackageInfo> objects, PackageManager manager2) {
            super(context, resource, objects);
            this.manager = manager2;
        }

        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = ((LayoutInflater) QHybridAppChoserActivity.this.getSystemService("layout_inflater")).inflate(C0889R.layout.qhybrid_app_view, (ViewGroup) null);
            }
            ApplicationInfo info = ((PackageInfo) getItem(position)).applicationInfo;
            ((ImageView) view.findViewById(C0889R.C0891id.qhybrid_appChooserItemIcon)).setImageDrawable(this.manager.getApplicationIcon(info));
            ((TextView) view.findViewById(C0889R.C0891id.qhybrid_appChooserItemText)).setText(this.manager.getApplicationLabel(info));
            return view;
        }
    }
}
