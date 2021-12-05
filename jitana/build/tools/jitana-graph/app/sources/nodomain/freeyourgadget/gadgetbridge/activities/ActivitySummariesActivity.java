package nodomain.freeyourgadget.gadgetbridge.activities;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.adapter.ActivitySummariesAdapter;
import nodomain.freeyourgadget.gadgetbridge.entities.BaseActivitySummary;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySummary;
import nodomain.freeyourgadget.gadgetbridge.model.RecordedDataTypes;
import nodomain.freeyourgadget.gadgetbridge.util.AndroidUtils;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;

public class ActivitySummariesActivity extends AbstractListActivity<BaseActivitySummary> {
    /* access modifiers changed from: private */
    public GBDevice mGBDevice;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String str = (String) Objects.requireNonNull(intent.getAction());
            if (((str.hashCode() == 2101976453 && str.equals(GBDevice.ACTION_DEVICE_CHANGED)) ? (char) 0 : 65535) == 0) {
                GBDevice device = (GBDevice) intent.getParcelableExtra(GBDevice.EXTRA_DEVICE);
                GBDevice unused = ActivitySummariesActivity.this.mGBDevice = device;
                if (device.isBusy()) {
                    ActivitySummariesActivity.this.swipeLayout.setRefreshing(true);
                    return;
                }
                boolean wasBusy = ActivitySummariesActivity.this.swipeLayout.isRefreshing();
                ActivitySummariesActivity.this.swipeLayout.setRefreshing(false);
                if (wasBusy) {
                    ActivitySummariesActivity.this.refresh();
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public SwipeRefreshLayout swipeLayout;

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(C0889R.C0893menu.activity_list_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != C0889R.C0891id.activity_action_manage_timestamp) {
            return false;
        }
        resetFetchTimestampToChosenDate();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mGBDevice = (GBDevice) extras.getParcelable(GBDevice.EXTRA_DEVICE);
            IntentFilter filterLocal = new IntentFilter();
            filterLocal.addAction(GBDevice.ACTION_DEVICE_CHANGED);
            LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, filterLocal);
            super.onCreate(savedInstanceState);
            setItemAdapter(new ActivitySummariesAdapter(this, this.mGBDevice));
            getItemListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object item = parent.getItemAtPosition(position);
                    if (item != null) {
                        String gpxTrack = ((ActivitySummary) item).getGpxTrack();
                        if (gpxTrack != null) {
                            ActivitySummariesActivity.this.showTrack(gpxTrack);
                        } else {
                            C1238GB.toast("This activity does not contain GPX tracks.", 1, 1);
                        }
                    }
                }
            });
            getItemListView().setChoiceMode(3);
            getItemListView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
                    int selectedItems = ActivitySummariesActivity.this.getItemListView().getCheckedItemCount();
                    actionMode.setTitle(selectedItems + " selected");
                }

                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    ActivitySummariesActivity.this.getMenuInflater().inflate(C0889R.C0893menu.activity_list_context_menu, menu);
                    ActivitySummariesActivity.this.findViewById(C0889R.C0891id.fab).setVisibility(4);
                    return true;
                }

                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    ActivitySummary item;
                    String gpxTrack;
                    boolean processed = false;
                    SparseBooleanArray checked = ActivitySummariesActivity.this.getItemListView().getCheckedItemPositions();
                    switch (menuItem.getItemId()) {
                        case C0889R.C0891id.activity_action_delete:
                            List<BaseActivitySummary> toDelete = new ArrayList<>();
                            for (int i = 0; i < checked.size(); i++) {
                                if (checked.valueAt(i)) {
                                    toDelete.add(ActivitySummariesActivity.this.getItemAdapter().getItem(checked.keyAt(i)));
                                }
                            }
                            ActivitySummariesActivity.this.deleteItems(toDelete);
                            processed = true;
                            break;
                        case C0889R.C0891id.activity_action_export:
                            List<String> paths = new ArrayList<>();
                            for (int i2 = 0; i2 < checked.size(); i2++) {
                                if (!(!checked.valueAt(i2) || (item = (BaseActivitySummary) ActivitySummariesActivity.this.getItemAdapter().getItem(checked.keyAt(i2))) == null || (gpxTrack = item.getGpxTrack()) == null)) {
                                    paths.add(gpxTrack);
                                }
                            }
                            ActivitySummariesActivity.this.shareMultiple(paths);
                            processed = true;
                            break;
                        case C0889R.C0891id.activity_action_select_all:
                            for (int i3 = 0; i3 < ActivitySummariesActivity.this.getItemListView().getCount(); i3++) {
                                ActivitySummariesActivity.this.getItemListView().setItemChecked(i3, true);
                            }
                            return true;
                    }
                    actionMode.finish();
                    return processed;
                }

                public void onDestroyActionMode(ActionMode actionMode) {
                    ActivitySummariesActivity.this.findViewById(C0889R.C0891id.fab).setVisibility(0);
                }
            });
            this.swipeLayout = (SwipeRefreshLayout) findViewById(C0889R.C0891id.list_activity_swipe_layout);
            this.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                public void onRefresh() {
                    ActivitySummariesActivity.this.fetchTrackData();
                }
            });
            ((FloatingActionButton) findViewById(C0889R.C0891id.fab)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ActivitySummariesActivity.this.fetchTrackData();
                }
            });
            return;
        }
        throw new IllegalArgumentException("Must provide a device when invoking this activity");
    }

    public void resetFetchTimestampToChosenDate() {
        Calendar currentDate = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar date = Calendar.getInstance();
                date.set(year, monthOfYear, dayOfMonth);
                SharedPreferences.Editor editor = GBApplication.getDeviceSpecificSharedPrefs(ActivitySummariesActivity.this.mGBDevice.getAddress()).edit();
                editor.remove("lastSportsActivityTimeMillis");
                editor.putLong("lastSportsActivityTimeMillis", date.getTimeInMillis() - 1000);
                editor.apply();
            }
        }, currentDate.get(1), currentDate.get(2), currentDate.get(5)).show();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mReceiver);
        super.onDestroy();
    }

    /* access modifiers changed from: private */
    public void deleteItems(List<BaseActivitySummary> items) {
        for (BaseActivitySummary item : items) {
            item.delete();
            getItemAdapter().remove(item);
        }
        refresh();
    }

    /* access modifiers changed from: private */
    public void showTrack(String gpxTrack) {
        try {
            AndroidUtils.viewFile(gpxTrack, "android.intent.action.VIEW", this);
        } catch (IOException e) {
            C1238GB.toast(this, "Unable to display GPX track: " + e.getMessage(), 1, 3, e);
        }
    }

    /* access modifiers changed from: private */
    public void fetchTrackData() {
        if (!this.mGBDevice.isInitialized() || this.mGBDevice.isBusy()) {
            this.swipeLayout.setRefreshing(false);
            if (!this.mGBDevice.isInitialized()) {
                C1238GB.toast((Context) this, getString(C0889R.string.device_not_connected), 0, 3);
                return;
            }
            return;
        }
        GBApplication.deviceService().onFetchRecordedData(RecordedDataTypes.TYPE_GPS_TRACKS);
    }

    /* access modifiers changed from: private */
    public void shareMultiple(List<String> paths) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (String path : paths) {
            File file = new File(path);
            uris.add(FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".screenshot_provider", file));
        }
        if (uris.size() > 0) {
            Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
            intent.setType("application/gpx+xml");
            intent.putParcelableArrayListExtra("android.intent.extra.STREAM", uris);
            startActivity(Intent.createChooser(intent, "SHARE"));
            return;
        }
        C1238GB.toast((Context) this, "No selected activity contains a GPX track to share", 0, 3);
    }
}
