package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.adapter.GBAlarmListAdapter;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.entities.Alarm;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.User;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.util.AlarmUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigureAlarms extends AbstractGBActivity {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) ConfigureAlarms.class);
    private static final int REQ_CONFIGURE_ALARM = 1;
    private boolean avoidSendAlarmsToDevice;
    private GBDevice gbDevice;
    private GBAlarmListAdapter mGBAlarmListAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (((action.hashCode() == 256389628 && action.equals(DeviceService.ACTION_SAVE_ALARMS)) ? (char) 0 : 65535) == 0) {
                ConfigureAlarms.this.updateAlarmsFromDB();
            }
        }
    };

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_configure_alarms);
        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(DeviceService.ACTION_SAVE_ALARMS);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, filterLocal);
        this.gbDevice = (GBDevice) getIntent().getParcelableExtra(GBDevice.EXTRA_DEVICE);
        this.mGBAlarmListAdapter = new GBAlarmListAdapter(this);
        RecyclerView alarmsRecyclerView = (RecyclerView) findViewById(C0889R.C0891id.alarm_list);
        alarmsRecyclerView.setHasFixedSize(true);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        alarmsRecyclerView.setAdapter(this.mGBAlarmListAdapter);
        updateAlarmsFromDB();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        if (!this.avoidSendAlarmsToDevice && this.gbDevice.isInitialized()) {
            sendAlarmsToDevice();
        }
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            this.avoidSendAlarmsToDevice = false;
            updateAlarmsFromDB();
        }
    }

    /* access modifiers changed from: private */
    public void updateAlarmsFromDB() {
        List<Alarm> alarms = DBHelper.getAlarms(getGbDevice());
        if (alarms.isEmpty()) {
            alarms = AlarmUtils.readAlarmsFromPrefs(getGbDevice());
            storeMigratedAlarms(alarms);
        }
        addMissingAlarms(alarms);
        this.mGBAlarmListAdapter.setAlarmList(alarms);
        this.mGBAlarmListAdapter.notifyDataSetChanged();
    }

    private void storeMigratedAlarms(List<Alarm> alarms) {
        for (Alarm alarm : alarms) {
            DBHelper.store(alarm);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0071, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0072, code lost:
        if (r2 != null) goto L_0x0074;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x007c, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void addMissingAlarms(java.util.List<nodomain.freeyourgadget.gadgetbridge.entities.Alarm> r12) {
        /*
            r11 = this;
            nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper r0 = nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper.getInstance()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r1 = r11.getGbDevice()
            nodomain.freeyourgadget.gadgetbridge.devices.DeviceCoordinator r0 = r0.getCoordinator((nodomain.freeyourgadget.gadgetbridge.impl.GBDevice) r1)
            int r1 = r0.getAlarmSlotCount()
            int r2 = r12.size()
            if (r1 <= r2) goto L_0x0085
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r2 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x007d }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r3 = r2.getDaoSession()     // Catch:{ all -> 0x006f }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r11.getGbDevice()     // Catch:{ all -> 0x006f }
            nodomain.freeyourgadget.gadgetbridge.entities.Device r4 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getDevice(r4, r3)     // Catch:{ all -> 0x006f }
            nodomain.freeyourgadget.gadgetbridge.entities.User r5 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getUser(r3)     // Catch:{ all -> 0x006f }
            r6 = 0
        L_0x002b:
            if (r6 >= r1) goto L_0x0069
            r7 = 0
            java.util.Iterator r8 = r12.iterator()     // Catch:{ all -> 0x006f }
        L_0x0032:
            boolean r9 = r8.hasNext()     // Catch:{ all -> 0x006f }
            if (r9 == 0) goto L_0x0047
            java.lang.Object r9 = r8.next()     // Catch:{ all -> 0x006f }
            nodomain.freeyourgadget.gadgetbridge.entities.Alarm r9 = (nodomain.freeyourgadget.gadgetbridge.entities.Alarm) r9     // Catch:{ all -> 0x006f }
            int r10 = r9.getPosition()     // Catch:{ all -> 0x006f }
            if (r10 != r6) goto L_0x0046
            r7 = 1
            goto L_0x0047
        L_0x0046:
            goto L_0x0032
        L_0x0047:
            if (r7 != 0) goto L_0x0066
            org.slf4j.Logger r8 = LOG     // Catch:{ all -> 0x006f }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x006f }
            r9.<init>()     // Catch:{ all -> 0x006f }
            java.lang.String r10 = "adding missing alarm at position "
            r9.append(r10)     // Catch:{ all -> 0x006f }
            r9.append(r6)     // Catch:{ all -> 0x006f }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x006f }
            r8.info(r9)     // Catch:{ all -> 0x006f }
            nodomain.freeyourgadget.gadgetbridge.entities.Alarm r8 = r11.createDefaultAlarm(r4, r5, r6)     // Catch:{ all -> 0x006f }
            r12.add(r6, r8)     // Catch:{ all -> 0x006f }
        L_0x0066:
            int r6 = r6 + 1
            goto L_0x002b
        L_0x0069:
            if (r2 == 0) goto L_0x006e
            r2.close()     // Catch:{ Exception -> 0x007d }
        L_0x006e:
            goto L_0x0085
        L_0x006f:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0071 }
        L_0x0071:
            r4 = move-exception
            if (r2 == 0) goto L_0x007c
            r2.close()     // Catch:{ all -> 0x0078 }
            goto L_0x007c
        L_0x0078:
            r5 = move-exception
            r3.addSuppressed(r5)     // Catch:{ Exception -> 0x007d }
        L_0x007c:
            throw r4     // Catch:{ Exception -> 0x007d }
        L_0x007d:
            r2 = move-exception
            org.slf4j.Logger r3 = LOG
            java.lang.String r4 = "Error accessing database"
            r3.error((java.lang.String) r4, (java.lang.Throwable) r2)
        L_0x0085:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.ConfigureAlarms.addMissingAlarms(java.util.List):void");
    }

    private Alarm createDefaultAlarm(Device device, User user, int position) {
        return new Alarm(device.getId().longValue(), user.getId().longValue(), position, false, false, false, 0, 6, 30, false);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        finish();
        return true;
    }

    public void configureAlarm(Alarm alarm) {
        this.avoidSendAlarmsToDevice = true;
        Intent startIntent = new Intent(getApplicationContext(), AlarmDetails.class);
        startIntent.putExtra("alarm", alarm);
        startIntent.putExtra(GBDevice.EXTRA_DEVICE, getGbDevice());
        startActivityForResult(startIntent, 1);
    }

    private GBDevice getGbDevice() {
        return this.gbDevice;
    }

    private void sendAlarmsToDevice() {
        GBApplication.deviceService().onSetAlarms(this.mGBAlarmListAdapter.getAlarmList());
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mReceiver);
        super.onDestroy();
    }
}
