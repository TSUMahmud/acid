package nodomain.freeyourgadget.gadgetbridge.activities.appmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.ExternalPebbleJSActivity;
import nodomain.freeyourgadget.gadgetbridge.adapter.GBDeviceAppAdapter;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol;
import nodomain.freeyourgadget.gadgetbridge.util.PebbleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAppManagerFragment extends Fragment {
    public static final String ACTION_REFRESH_APPLIST = "nodomain.freeyourgadget.gadgetbridge.appmanager.action.refresh_applist";
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractAppManagerFragment.class);
    protected final List<GBDeviceApp> appList = new ArrayList();
    private ItemTouchHelper appManagementTouchHelper;
    protected GBDevice mGBDevice = null;
    /* access modifiers changed from: private */
    public GBDeviceAppAdapter mGBDeviceAppAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AbstractAppManagerFragment.ACTION_REFRESH_APPLIST)) {
                if (intent.hasExtra("app_count")) {
                    AbstractAppManagerFragment.LOG.info("got app info from pebble");
                    if (!AbstractAppManagerFragment.this.isCacheManager()) {
                        AbstractAppManagerFragment.LOG.info("will refresh list based on data from pebble");
                        AbstractAppManagerFragment.this.refreshListFromPebble(intent);
                    }
                } else if (PebbleUtils.getFwMajor(AbstractAppManagerFragment.this.mGBDevice.getFirmwareVersion()) >= 3 || AbstractAppManagerFragment.this.isCacheManager()) {
                    AbstractAppManagerFragment.this.refreshList();
                }
                AbstractAppManagerFragment.this.mGBDeviceAppAdapter.notifyDataSetChanged();
            }
        }
    };

    /* access modifiers changed from: protected */
    public abstract boolean filterApp(GBDeviceApp gBDeviceApp);

    /* access modifiers changed from: protected */
    public abstract String getSortFilename();

    /* access modifiers changed from: protected */
    public abstract List<GBDeviceApp> getSystemAppsInCategory();

    /* access modifiers changed from: protected */
    public abstract boolean isCacheManager();

    public void startDragging(RecyclerView.ViewHolder viewHolder) {
        this.appManagementTouchHelper.startDrag(viewHolder);
    }

    /* access modifiers changed from: protected */
    public void onChangedAppOrder() {
        List<UUID> uuidList = new ArrayList<>();
        for (GBDeviceApp gbDeviceApp : this.mGBDeviceAppAdapter.getAppList()) {
            uuidList.add(gbDeviceApp.getUUID());
        }
        AppManagerActivity.rewriteAppOrderFile(getSortFilename(), uuidList);
    }

    /* access modifiers changed from: protected */
    public void refreshList() {
        this.appList.clear();
        ArrayList<UUID> uuids = AppManagerActivity.getUuidsFromFile(getSortFilename());
        boolean needsRewrite = false;
        for (GBDeviceApp systemApp : getSystemAppsInCategory()) {
            if (!uuids.contains(systemApp.getUUID())) {
                uuids.add(systemApp.getUUID());
                needsRewrite = true;
            }
        }
        if (needsRewrite) {
            AppManagerActivity.rewriteAppOrderFile(getSortFilename(), uuids);
        }
        this.appList.addAll(getCachedApps(uuids));
    }

    /* access modifiers changed from: private */
    public void refreshListFromPebble(Intent intent) {
        this.appList.clear();
        int appCount = intent.getIntExtra("app_count", 0);
        for (int i = 0; i < appCount; i++) {
            String appName = intent.getStringExtra("app_name" + i);
            String appCreator = intent.getStringExtra("app_creator" + i);
            UUID uuid = UUID.fromString(intent.getStringExtra(DeviceService.EXTRA_APP_UUID + i));
            GBDeviceApp.Type[] values = GBDeviceApp.Type.values();
            GBDeviceApp app = new GBDeviceApp(uuid, appName, appCreator, "", values[intent.getIntExtra("app_type" + i, 0)]);
            app.setOnDevice(true);
            if (filterApp(app)) {
                this.appList.add(app);
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp> getCachedApps(java.util.List<java.util.UUID> r27) {
        /*
            r26 = this;
            r1 = r26
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r2 = r0
            java.io.File r0 = nodomain.freeyourgadget.gadgetbridge.util.PebbleUtils.getPbwCacheDir()     // Catch:{ IOException -> 0x0270 }
            r3 = r0
            java.lang.String r4 = ".pbw"
            if (r27 != 0) goto L_0x0018
            java.io.File[] r0 = r3.listFiles()
            r5 = r0
            goto L_0x004e
        L_0x0018:
            int r0 = r27.size()
            java.io.File[] r0 = new java.io.File[r0]
            r5 = 0
            java.util.Iterator r6 = r27.iterator()
        L_0x0023:
            boolean r7 = r6.hasNext()
            if (r7 == 0) goto L_0x004d
            java.lang.Object r7 = r6.next()
            java.util.UUID r7 = (java.util.UUID) r7
            int r8 = r5 + 1
            java.io.File r9 = new java.io.File
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = r7.toString()
            r10.append(r11)
            r10.append(r4)
            java.lang.String r10 = r10.toString()
            r9.<init>(r10)
            r0[r5] = r9
            r5 = r8
            goto L_0x0023
        L_0x004d:
            r5 = r0
        L_0x004e:
            if (r5 == 0) goto L_0x026f
            int r6 = r5.length
            r7 = 0
            r8 = 0
        L_0x0053:
            if (r8 >= r6) goto L_0x026f
            r9 = r5[r8]
            java.lang.String r0 = r9.getName()
            boolean r0 = r0.endsWith(r4)
            if (r0 == 0) goto L_0x026a
            java.lang.String r0 = r9.getName()
            java.lang.String r10 = r9.getName()
            int r10 = r10.length()
            r11 = 4
            int r10 = r10 - r11
            java.lang.String r10 = r0.substring(r7, r10)
            java.io.File r0 = new java.io.File
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r10)
            java.lang.String r13 = ".json"
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            r0.<init>(r3, r12)
            r18 = r0
            java.io.File r0 = new java.io.File
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r10)
            java.lang.String r13 = "_config.js"
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            r0.<init>(r3, r12)
            r19 = r0
            java.lang.String r0 = nodomain.freeyourgadget.gadgetbridge.util.FileUtils.getStringFromFile(r18)     // Catch:{ Exception -> 0x00bb }
            org.json.JSONObject r12 = new org.json.JSONObject     // Catch:{ Exception -> 0x00bb }
            r12.<init>(r0)     // Catch:{ Exception -> 0x00bb }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp r13 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp     // Catch:{ Exception -> 0x00bb }
            boolean r14 = r19.exists()     // Catch:{ Exception -> 0x00bb }
            r13.<init>(r12, r14)     // Catch:{ Exception -> 0x00bb }
            r2.add(r13)     // Catch:{ Exception -> 0x00bb }
            goto L_0x026a
        L_0x00bb:
            r0 = move-exception
            org.slf4j.Logger r12 = LOG
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "could not read json file for "
            r13.append(r14)
            r13.append(r10)
            java.lang.String r13 = r13.toString()
            r12.info(r13)
            int r13 = r10.hashCode()
            r14 = 5
            r15 = 3
            r7 = 2
            r12 = 1
            switch(r13) {
                case -2123423692: goto L_0x0110;
                case -1424088983: goto L_0x0106;
                case -808663038: goto L_0x00fc;
                case -716747397: goto L_0x00f2;
                case 826009225: goto L_0x00e8;
                case 1948249965: goto L_0x00de;
                default: goto L_0x00dd;
            }
        L_0x00dd:
            goto L_0x011a
        L_0x00de:
            java.lang.String r13 = "67a32d95-ef69-46d4-a0b9-854cc62f97f9"
            boolean r13 = r10.equals(r13)
            if (r13 == 0) goto L_0x00dd
            r13 = 3
            goto L_0x011b
        L_0x00e8:
            java.lang.String r13 = "b2cae818-10f8-46df-ad2b-98ad2254a3c1"
            boolean r13 = r10.equals(r13)
            if (r13 == 0) goto L_0x00dd
            r13 = 2
            goto L_0x011b
        L_0x00f2:
            java.lang.String r13 = "1f03293d-47af-4f28-b960-f2b02a6dd757"
            boolean r13 = r10.equals(r13)
            if (r13 == 0) goto L_0x00dd
            r13 = 1
            goto L_0x011b
        L_0x00fc:
            java.lang.String r13 = "8f3c8686-31a1-4f5f-91f5-01600c9bdc59"
            boolean r13 = r10.equals(r13)
            if (r13 == 0) goto L_0x00dd
            r13 = 0
            goto L_0x011b
        L_0x0106:
            java.lang.String r13 = "0863fc6a-66c5-4f62-ab8a-82ed00a98b5d"
            boolean r13 = r10.equals(r13)
            if (r13 == 0) goto L_0x00dd
            r13 = 5
            goto L_0x011b
        L_0x0110:
            java.lang.String r13 = "18e443ce-38fd-47c8-84d5-6d0c775fbe55"
            boolean r13 = r10.equals(r13)
            if (r13 == 0) goto L_0x00dd
            r13 = 4
            goto L_0x011b
        L_0x011a:
            r13 = -1
        L_0x011b:
            if (r13 == 0) goto L_0x019b
            if (r13 == r12) goto L_0x0184
            if (r13 == r7) goto L_0x016e
            if (r13 == r15) goto L_0x0157
            if (r13 == r11) goto L_0x0141
            if (r13 == r14) goto L_0x0129
            goto L_0x01b1
        L_0x0129:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp r7 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp
            java.util.UUID r21 = java.util.UUID.fromString(r10)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r25 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.APP_SYSTEM
            java.lang.String r22 = "Send Text (System)"
            java.lang.String r23 = "Pebble Inc."
            java.lang.String r24 = ""
            r20 = r7
            r20.<init>(r21, r22, r23, r24, r25)
            r2.add(r7)
            goto L_0x01b1
        L_0x0141:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp r7 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp
            java.util.UUID r13 = java.util.UUID.fromString(r10)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r17 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.APP_SYSTEM
            java.lang.String r14 = "Watchfaces (System)"
            java.lang.String r15 = "Pebble Inc."
            java.lang.String r16 = ""
            r12 = r7
            r12.<init>(r13, r14, r15, r16, r17)
            r2.add(r7)
            goto L_0x01b1
        L_0x0157:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp r7 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp
            java.util.UUID r21 = java.util.UUID.fromString(r10)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r25 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.APP_SYSTEM
            java.lang.String r22 = "Alarms (System)"
            java.lang.String r23 = "Pebble Inc."
            java.lang.String r24 = ""
            r20 = r7
            r20.<init>(r21, r22, r23, r24, r25)
            r2.add(r7)
            goto L_0x01b1
        L_0x016e:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp r7 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp
            java.util.UUID r13 = java.util.UUID.fromString(r10)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r17 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.APP_SYSTEM
            java.lang.String r14 = "Notifications (System)"
            java.lang.String r15 = "Pebble Inc."
            java.lang.String r16 = ""
            r12 = r7
            r12.<init>(r13, r14, r15, r16, r17)
            r2.add(r7)
            goto L_0x01b1
        L_0x0184:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp r7 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp
            java.util.UUID r21 = java.util.UUID.fromString(r10)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r25 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.APP_SYSTEM
            java.lang.String r22 = "Music (System)"
            java.lang.String r23 = "Pebble Inc."
            java.lang.String r24 = ""
            r20 = r7
            r20.<init>(r21, r22, r23, r24, r25)
            r2.add(r7)
            goto L_0x01b1
        L_0x019b:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp r7 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp
            java.util.UUID r13 = java.util.UUID.fromString(r10)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r17 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.WATCHFACE_SYSTEM
            java.lang.String r14 = "Tic Toc (System)"
            java.lang.String r15 = "Pebble Inc."
            java.lang.String r16 = ""
            r12 = r7
            r12.<init>(r13, r14, r15, r16, r17)
            r2.add(r7)
        L_0x01b1:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r7 = r1.mGBDevice
            if (r7 == 0) goto L_0x0254
            java.lang.String r7 = r7.getModel()
            boolean r7 = nodomain.freeyourgadget.gadgetbridge.util.PebbleUtils.hasHealth(r7)
            if (r7 == 0) goto L_0x01e0
            java.util.UUID r7 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol.UUID_PEBBLE_HEALTH
            java.lang.String r7 = r7.toString()
            boolean r7 = r10.equals(r7)
            if (r7 == 0) goto L_0x01e0
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp r7 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp
            java.util.UUID r13 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol.UUID_PEBBLE_HEALTH
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r17 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.APP_SYSTEM
            java.lang.String r14 = "Health (System)"
            java.lang.String r15 = "Pebble Inc."
            java.lang.String r16 = ""
            r12 = r7
            r12.<init>(r13, r14, r15, r16, r17)
            r2.add(r7)
            goto L_0x026a
        L_0x01e0:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r7 = r1.mGBDevice
            java.lang.String r7 = r7.getModel()
            boolean r7 = nodomain.freeyourgadget.gadgetbridge.util.PebbleUtils.hasHRM(r7)
            if (r7 == 0) goto L_0x020c
            java.util.UUID r7 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol.UUID_WORKOUT
            java.lang.String r7 = r7.toString()
            boolean r7 = r10.equals(r7)
            if (r7 == 0) goto L_0x020c
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp r7 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp
            java.util.UUID r13 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol.UUID_WORKOUT
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r17 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.APP_SYSTEM
            java.lang.String r14 = "Workout (System)"
            java.lang.String r15 = "Pebble Inc."
            java.lang.String r16 = ""
            r12 = r7
            r12.<init>(r13, r14, r15, r16, r17)
            r2.add(r7)
            goto L_0x026a
        L_0x020c:
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r7 = r1.mGBDevice
            java.lang.String r7 = r7.getFirmwareVersion()
            int r7 = nodomain.freeyourgadget.gadgetbridge.util.PebbleUtils.getFwMajor(r7)
            if (r7 < r11) goto L_0x0254
            java.lang.String r7 = "3af858c3-16cb-4561-91e7-f1ad2df8725f"
            boolean r7 = r10.equals(r7)
            if (r7 == 0) goto L_0x0235
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp r7 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp
            java.util.UUID r12 = java.util.UUID.fromString(r10)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r16 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.WATCHFACE_SYSTEM
            java.lang.String r13 = "Kickstart (System)"
            java.lang.String r14 = "Pebble Inc."
            java.lang.String r15 = ""
            r11 = r7
            r11.<init>(r12, r13, r14, r15, r16)
            r2.add(r7)
        L_0x0235:
            java.util.UUID r7 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol.UUID_WEATHER
            java.lang.String r7 = r7.toString()
            boolean r7 = r10.equals(r7)
            if (r7 == 0) goto L_0x0254
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp r7 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp
            java.util.UUID r12 = nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleProtocol.UUID_WEATHER
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r16 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.APP_SYSTEM
            java.lang.String r13 = "Weather (System)"
            java.lang.String r14 = "Pebble Inc."
            java.lang.String r15 = ""
            r11 = r7
            r11.<init>(r12, r13, r14, r15, r16)
            r2.add(r7)
        L_0x0254:
            if (r27 != 0) goto L_0x026a
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp r7 = new nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp
            java.util.UUID r13 = java.util.UUID.fromString(r10)
            nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp$Type r17 = nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp.Type.UNKNOWN
            java.lang.String r15 = "N/A"
            java.lang.String r16 = ""
            r12 = r7
            r14 = r10
            r12.<init>(r13, r14, r15, r16, r17)
            r2.add(r7)
        L_0x026a:
            int r8 = r8 + 1
            r7 = 0
            goto L_0x0053
        L_0x026f:
            return r2
        L_0x0270:
            r0 = move-exception
            r3 = r0
            r0 = r3
            org.slf4j.Logger r3 = LOG
            java.lang.String r4 = "could not get external dir while reading pbw cache."
            r3.warn(r4)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.appmanager.AbstractAppManagerFragment.getCachedApps(java.util.List):java.util.List");
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mGBDevice = ((AppManagerActivity) getActivity()).getGBDevice();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH_APPLIST);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(this.mReceiver, filter);
        if (PebbleUtils.getFwMajor(this.mGBDevice.getFirmwareVersion()) < 3) {
            GBApplication.deviceService().onAppInfoReq();
            if (isCacheManager()) {
                refreshList();
                return;
            }
            return;
        }
        refreshList();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FloatingActionButton appListFab = (FloatingActionButton) getActivity().findViewById(C0889R.C0891id.fab);
        View rootView = inflater.inflate(C0889R.layout.activity_appmanager, container, false);
        RecyclerView appListView = (RecyclerView) rootView.findViewById(C0889R.C0891id.appListView);
        appListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    appListFab.hide();
                } else if (dy < 0) {
                    appListFab.show();
                }
            }
        });
        appListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.mGBDeviceAppAdapter = new GBDeviceAppAdapter(this.appList, C0889R.layout.item_pebble_watchapp, this);
        appListView.setAdapter(this.mGBDeviceAppAdapter);
        this.appManagementTouchHelper = new ItemTouchHelper(new AppItemTouchHelperCallback(this.mGBDeviceAppAdapter));
        this.appManagementTouchHelper.attachToRecyclerView(appListView);
        return rootView;
    }

    /* access modifiers changed from: protected */
    public void sendOrderToDevice(String concatFilename) {
        ArrayList<UUID> uuids = new ArrayList<>();
        for (GBDeviceApp gbDeviceApp : this.mGBDeviceAppAdapter.getAppList()) {
            uuids.add(gbDeviceApp.getUUID());
        }
        if (concatFilename != null) {
            uuids.addAll(AppManagerActivity.getUuidsFromFile(concatFilename));
        }
        GBApplication.deviceService().onAppReorder((UUID[]) uuids.toArray(new UUID[uuids.size()]));
    }

    public boolean openPopupMenu(View view, GBDeviceApp deviceApp) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(C0889R.C0893menu.appmanager_context, popupMenu.getMenu());
        Menu menu = popupMenu.getMenu();
        final GBDeviceApp selectedApp = deviceApp;
        if (!selectedApp.isInCache()) {
            menu.removeItem(C0889R.C0891id.appmanager_app_reinstall);
            menu.removeItem(C0889R.C0891id.appmanager_app_delete_cache);
        }
        if (!PebbleProtocol.UUID_PEBBLE_HEALTH.equals(selectedApp.getUUID())) {
            menu.removeItem(C0889R.C0891id.appmanager_health_activate);
            menu.removeItem(C0889R.C0891id.appmanager_health_deactivate);
        }
        if (!PebbleProtocol.UUID_WORKOUT.equals(selectedApp.getUUID())) {
            menu.removeItem(C0889R.C0891id.appmanager_hrm_activate);
            menu.removeItem(C0889R.C0891id.appmanager_hrm_deactivate);
        }
        if (!PebbleProtocol.UUID_WEATHER.equals(selectedApp.getUUID())) {
            menu.removeItem(C0889R.C0891id.appmanager_weather_activate);
            menu.removeItem(C0889R.C0891id.appmanager_weather_deactivate);
            menu.removeItem(C0889R.C0891id.appmanager_weather_install_provider);
        }
        if (selectedApp.getType() == GBDeviceApp.Type.APP_SYSTEM || selectedApp.getType() == GBDeviceApp.Type.WATCHFACE_SYSTEM) {
            menu.removeItem(C0889R.C0891id.appmanager_app_delete);
        }
        if (!selectedApp.isConfigurable()) {
            menu.removeItem(C0889R.C0891id.appmanager_app_configure);
        }
        if (PebbleProtocol.UUID_WEATHER.equals(selectedApp.getUUID())) {
            try {
                getActivity().getPackageManager().getPackageInfo("ru.gelin.android.weather.notification", 1);
                menu.removeItem(C0889R.C0891id.appmanager_weather_install_provider);
            } catch (PackageManager.NameNotFoundException e) {
                menu.removeItem(C0889R.C0891id.appmanager_weather_activate);
                menu.removeItem(C0889R.C0891id.appmanager_weather_deactivate);
            }
        }
        int i = C09924.f122x7d95cefd[selectedApp.getType().ordinal()];
        if (!(i == 1 || i == 2 || i == 3)) {
            menu.removeItem(C0889R.C0891id.appmanager_app_openinstore);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                return AbstractAppManagerFragment.this.onContextItemSelected(item, selectedApp);
            }
        });
        popupMenu.show();
        return true;
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.activities.appmanager.AbstractAppManagerFragment$4 */
    static /* synthetic */ class C09924 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$impl$GBDeviceApp$Type */
        static final /* synthetic */ int[] f122x7d95cefd = new int[GBDeviceApp.Type.values().length];

        static {
            try {
                f122x7d95cefd[GBDeviceApp.Type.WATCHFACE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f122x7d95cefd[GBDeviceApp.Type.APP_GENERIC.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f122x7d95cefd[GBDeviceApp.Type.APP_ACTIVITYTRACKER.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean onContextItemSelected(MenuItem item, GBDeviceApp selectedApp) {
        switch (item.getItemId()) {
            case C0889R.C0891id.appmanager_app_configure:
                GBApplication.deviceService().onAppStart(selectedApp.getUUID(), true);
                Intent startIntent = new Intent(getContext().getApplicationContext(), ExternalPebbleJSActivity.class);
                startIntent.putExtra(DeviceService.EXTRA_APP_UUID, selectedApp.getUUID());
                startIntent.putExtra(GBDevice.EXTRA_DEVICE, this.mGBDevice);
                startIntent.putExtra(ExternalPebbleJSActivity.SHOW_CONFIG, true);
                startActivity(startIntent);
                return true;
            case C0889R.C0891id.appmanager_app_delete:
                break;
            case C0889R.C0891id.appmanager_app_delete_cache:
                try {
                    File pbwCacheDir = PebbleUtils.getPbwCacheDir();
                    String baseName = selectedApp.getUUID().toString();
                    for (String suffix : new String[]{".pbw", ".json", "_config.js", "_preset.json"}) {
                        File fileToDelete = new File(pbwCacheDir, baseName + suffix);
                        if (!fileToDelete.delete()) {
                            LOG.warn("could not delete file from pbw cache: " + fileToDelete.toString());
                        } else {
                            LOG.info("deleted file: " + fileToDelete.toString());
                        }
                    }
                    AppManagerActivity.deleteFromAppOrderFile("pbwcacheorder.txt", selectedApp.getUUID());
                    break;
                } catch (IOException e) {
                    LOG.warn("could not get external dir while trying to access pbw cache.");
                    return true;
                }
            case C0889R.C0891id.appmanager_app_openinstore:
                StringBuilder sb = new StringBuilder();
                sb.append("https://apps.rebble.io/en_US/search/");
                sb.append(selectedApp.getType() == GBDeviceApp.Type.WATCHFACE ? "watchfaces" : "watchapps");
                sb.append("/1/?native=true&?query=");
                sb.append(Uri.encode(selectedApp.getName()));
                String url = sb.toString();
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            case C0889R.C0891id.appmanager_app_reinstall:
                try {
                    GBApplication.deviceService().onInstallApp(Uri.fromFile(new File(PebbleUtils.getPbwCacheDir(), selectedApp.getUUID() + ".pbw")));
                    return true;
                } catch (IOException e2) {
                    LOG.warn("could not get external dir while trying to access pbw cache.");
                    return true;
                }
            case C0889R.C0891id.appmanager_health_activate:
                GBApplication.deviceService().onInstallApp(Uri.parse("fake://health"));
                return true;
            case C0889R.C0891id.appmanager_health_deactivate:
            case C0889R.C0891id.appmanager_hrm_deactivate:
            case C0889R.C0891id.appmanager_weather_deactivate:
                GBApplication.deviceService().onAppDelete(selectedApp.getUUID());
                return true;
            case C0889R.C0891id.appmanager_hrm_activate:
                GBApplication.deviceService().onInstallApp(Uri.parse("fake://hrm"));
                return true;
            case C0889R.C0891id.appmanager_weather_activate:
                GBApplication.deviceService().onInstallApp(Uri.parse("fake://weather"));
                return true;
            case C0889R.C0891id.appmanager_weather_install_provider:
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://f-droid.org/app/ru.gelin.android.weather.notification")));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
        if (PebbleUtils.getFwMajor(this.mGBDevice.getFirmwareVersion()) >= 3) {
            AppManagerActivity.deleteFromAppOrderFile(this.mGBDevice.getAddress() + ".watchapps", selectedApp.getUUID());
            AppManagerActivity.deleteFromAppOrderFile(this.mGBDevice.getAddress() + ".watchfaces", selectedApp.getUUID());
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(ACTION_REFRESH_APPLIST));
        }
        GBApplication.deviceService().onAppDelete(selectedApp.getUUID());
        return true;
    }

    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this.mReceiver);
        super.onDestroy();
    }

    public class AppItemTouchHelperCallback extends ItemTouchHelper.Callback {
        private final GBDeviceAppAdapter gbDeviceAppAdapter;

        public AppItemTouchHelperCallback(GBDeviceAppAdapter gbDeviceAppAdapter2) {
            this.gbDeviceAppAdapter = gbDeviceAppAdapter2;
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (PebbleUtils.getFwMajor(AbstractAppManagerFragment.this.mGBDevice.getFirmwareVersion()) >= 3 || AbstractAppManagerFragment.this.isCacheManager()) {
                return makeMovementFlags(3, 0);
            }
            return 0;
        }

        public boolean isLongPressDragEnabled() {
            return false;
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            this.gbDeviceAppAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            AbstractAppManagerFragment.this.onChangedAppOrder();
        }
    }
}
