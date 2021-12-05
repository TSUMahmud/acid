package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.adapter.GBDeviceAdapterv2;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.util.AndroidUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import p008de.cketti.library.changelog.ChangeLog;

public class ControlCenterv2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GBActivity {
    public static final int MENU_REFRESH_CODE = 1;
    private static PhoneStateListener fakeStateListener;
    private RecyclerView deviceListView;
    private DeviceManager deviceManager;
    private FloatingActionButton fab;
    private boolean isLanguageInvalid = false;
    private GBDeviceAdapterv2 mGBDeviceAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* JADX WARNING: Removed duplicated region for block: B:17:0x0041  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x0052  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r7, android.content.Intent r8) {
            /*
                r6 = this;
                java.lang.String r0 = r8.getAction()
                java.lang.Object r1 = java.util.Objects.requireNonNull(r0)
                java.lang.String r1 = (java.lang.String) r1
                int r2 = r1.hashCode()
                r3 = -1573629631(0xffffffffa2345141, float:-2.4437564E-18)
                r4 = 2
                r5 = 1
                if (r2 == r3) goto L_0x0034
                r3 = -812299209(0xffffffffcf954c37, float:-5.0096E9)
                if (r2 == r3) goto L_0x002a
                r3 = 208140431(0xc67f88f, float:1.787039E-31)
                if (r2 == r3) goto L_0x0020
            L_0x001f:
                goto L_0x003e
            L_0x0020:
                java.lang.String r2 = "nodomain.freeyourgadget.gadgetbridge.gbapplication.action.quit"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x001f
                r1 = 1
                goto L_0x003f
            L_0x002a:
                java.lang.String r2 = "nodomain.freeyourgadget.gadgetbridge.gbapplication.action.language_change"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x001f
                r1 = 0
                goto L_0x003f
            L_0x0034:
                java.lang.String r2 = "nodomain.freeyourgadget.gadgetbridge.devices.devicemanager.action.devices_changed"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x001f
                r1 = 2
                goto L_0x003f
            L_0x003e:
                r1 = -1
            L_0x003f:
                if (r1 == 0) goto L_0x0052
                if (r1 == r5) goto L_0x004c
                if (r1 == r4) goto L_0x0046
                goto L_0x005c
            L_0x0046:
                nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2 r1 = nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2.this
                r1.refreshPairedDevices()
                goto L_0x005c
            L_0x004c:
                nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2 r1 = nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2.this
                r1.finish()
                goto L_0x005c
            L_0x0052:
                nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2 r1 = nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2.this
                java.util.Locale r2 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getLanguage()
                r1.setLanguage(r2, r5)
            L_0x005c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2.C09161.onReceive(android.content.Context, android.content.Intent):void");
        }
    };

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        AbstractGBActivity.init(this, 1);
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_controlcenterv2);
        Toolbar toolbar = (Toolbar) findViewById(C0889R.C0891id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(C0889R.C0891id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, C0889R.string.controlcenter_navigation_drawer_open, C0889R.string.controlcenter_navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        ((NavigationView) findViewById(C0889R.C0891id.nav_view)).setNavigationItemSelectedListener(this);
        this.deviceManager = ((GBApplication) getApplication()).getDeviceManager();
        this.deviceListView = (RecyclerView) findViewById(C0889R.C0891id.deviceListView);
        this.deviceListView.setHasFixedSize(true);
        this.deviceListView.setLayoutManager(new LinearLayoutManager(this));
        List<GBDevice> deviceList = this.deviceManager.getDevices();
        this.mGBDeviceAdapter = new GBDeviceAdapterv2(this, deviceList);
        this.deviceListView.setAdapter(this.mGBDeviceAdapter);
        this.fab = (FloatingActionButton) findViewById(C0889R.C0891id.fab);
        this.fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ControlCenterv2.this.launchDiscoveryActivity();
            }
        });
        showFabIfNeccessary();
        registerForContextMenu(this.deviceListView);
        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(GBApplication.ACTION_LANGUAGE_CHANGE);
        filterLocal.addAction(GBApplication.ACTION_QUIT);
        filterLocal.addAction(DeviceManager.ACTION_DEVICES_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, filterLocal);
        refreshPairedDevices();
        Prefs prefs = GBApplication.getPrefs();
        if (prefs.getBoolean("firstrun", true)) {
            prefs.getPreferences().edit().putBoolean("firstrun", false).apply();
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermissions();
        }
        ChangeLog cl = createChangeLog();
        if (cl.isFirstRun()) {
            try {
                cl.getLogDialog().show();
            } catch (Exception e) {
                C1238GB.toast(getBaseContext(), "Error showing Changelog", 1, 3);
            }
        }
        GBApplication.deviceService().start();
        if (!C1238GB.isBluetoothEnabled() || !deviceList.isEmpty() || Build.VERSION.SDK_INT >= 23) {
            GBApplication.deviceService().requestDeviceInfo();
        } else {
            startActivity(new Intent(this, DiscoveryActivity.class));
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
        unregisterForContextMenu(this.deviceListView);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mReceiver);
        super.onDestroy();
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(C0889R.C0891id.drawer_layout);
        if (drawer.isDrawerOpen((int) GravityCompat.START)) {
            drawer.closeDrawer((int) GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            showFabIfNeccessary();
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        ((DrawerLayout) findViewById(C0889R.C0891id.drawer_layout)).closeDrawer((int) GravityCompat.START);
        switch (item.getItemId()) {
            case C0889R.C0891id.action_blacklist:
                startActivity(new Intent(this, AppBlacklistActivity.class));
                return true;
            case C0889R.C0891id.action_db_management:
                startActivity(new Intent(this, DbManagementActivity.class));
                return true;
            case C0889R.C0891id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case C0889R.C0891id.action_quit:
                GBApplication.quit();
                return true;
            case C0889R.C0891id.action_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), 1);
                return true;
            case C0889R.C0891id.device_action_discover:
                launchDiscoveryActivity();
                return true;
            case C0889R.C0891id.donation_link:
                Intent i = new Intent("android.intent.action.VIEW", Uri.parse("https://liberapay.com/Gadgetbridge"));
                i.setFlags(268468224);
                startActivity(i);
                return true;
            case C0889R.C0891id.external_changelog:
                try {
                    createChangeLog().getLogDialog().show();
                } catch (Exception e) {
                    C1238GB.toast(getBaseContext(), "Error showing Changelog", 1, 3);
                }
                return true;
            default:
                return true;
        }
    }

    private ChangeLog createChangeLog() {
        return new ChangeLog(this, ChangeLog.DEFAULT_CSS + "body { color: " + AndroidUtils.getTextColorHex(getBaseContext()) + "; background-color: " + AndroidUtils.getBackgroundColorHex(getBaseContext()) + ";}");
    }

    /* access modifiers changed from: private */
    public void launchDiscoveryActivity() {
        startActivity(new Intent(this, DiscoveryActivity.class));
    }

    /* access modifiers changed from: private */
    public void refreshPairedDevices() {
        this.mGBDeviceAdapter.notifyDataSetChanged();
    }

    private void showFabIfNeccessary() {
        if (GBApplication.getPrefs().getBoolean("display_add_device_fab", true)) {
            this.fab.show();
        } else if (this.deviceListView.getChildCount() < 1) {
            this.fab.show();
        } else {
            this.fab.hide();
        }
    }

    private void checkAndRequestPermissions() {
        List<String> wantedPermissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH") == -1) {
            wantedPermissions.add("android.permission.BLUETOOTH");
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_ADMIN") == -1) {
            wantedPermissions.add("android.permission.BLUETOOTH_ADMIN");
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS") == -1) {
            wantedPermissions.add("android.permission.READ_CONTACTS");
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.CALL_PHONE") == -1) {
            wantedPermissions.add("android.permission.CALL_PHONE");
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_CALL_LOG") == -1) {
            wantedPermissions.add("android.permission.READ_CALL_LOG");
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") == -1) {
            wantedPermissions.add("android.permission.READ_PHONE_STATE");
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.PROCESS_OUTGOING_CALLS") == -1) {
            wantedPermissions.add("android.permission.PROCESS_OUTGOING_CALLS");
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.RECEIVE_SMS") == -1) {
            wantedPermissions.add("android.permission.RECEIVE_SMS");
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_SMS") == -1) {
            wantedPermissions.add("android.permission.READ_SMS");
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.SEND_SMS") == -1) {
            wantedPermissions.add("android.permission.SEND_SMS");
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == -1) {
            wantedPermissions.add("android.permission.READ_EXTERNAL_STORAGE");
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_CALENDAR") == -1) {
            wantedPermissions.add("android.permission.READ_CALENDAR");
        }
        try {
            if (ContextCompat.checkSelfPermission(this, "android.permission.MEDIA_CONTENT_CONTROL") == -1) {
                wantedPermissions.add("android.permission.MEDIA_CONTENT_CONTROL");
            }
        } catch (Exception e) {
        }
        if (!wantedPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, (String[]) wantedPermissions.toArray(new String[0]), 0);
        }
        if (fakeStateListener == null) {
            fakeStateListener = new PhoneStateListener();
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService("phone");
            telephonyManager.listen(fakeStateListener, 32);
            telephonyManager.listen(fakeStateListener, 0);
        }
    }

    public void setLanguage(Locale language, boolean invalidateLanguage) {
        if (invalidateLanguage) {
            this.isLanguageInvalid = true;
        }
        AndroidUtils.setLanguage(this, language);
    }
}
