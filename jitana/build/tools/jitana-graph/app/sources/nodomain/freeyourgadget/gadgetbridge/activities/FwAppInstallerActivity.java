package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.app.NavUtils;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.ArrayList;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.adapter.ItemWithDetailsAdapter;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.GenericItem;
import nodomain.freeyourgadget.gadgetbridge.model.ItemWithDetails;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FwAppInstallerActivity extends AbstractGBActivity implements InstallActivity {
    private static final String ITEM_DETAILS = "details";
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) FwAppInstallerActivity.class);
    private ListView detailsListView;
    /* access modifiers changed from: private */
    public GBDevice device;
    private TextView fwAppInstallTextView;
    private Button installButton;
    /* access modifiers changed from: private */
    public InstallHandler installHandler;
    private ListView itemListView;
    private ArrayList<ItemWithDetails> mDetails = new ArrayList<>();
    private ItemWithDetailsAdapter mDetailsItemAdapter;
    private ItemWithDetailsAdapter mItemAdapter;
    private final List<ItemWithDetails> mItems = new ArrayList();
    private ProgressBar mProgressBar;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (GBDevice.ACTION_DEVICE_CHANGED.equals(action)) {
                GBDevice unused = FwAppInstallerActivity.this.device = (GBDevice) intent.getParcelableExtra(GBDevice.EXTRA_DEVICE);
                if (FwAppInstallerActivity.this.device != null) {
                    FwAppInstallerActivity fwAppInstallerActivity = FwAppInstallerActivity.this;
                    fwAppInstallerActivity.refreshBusyState(fwAppInstallerActivity.device);
                    if (!FwAppInstallerActivity.this.device.isInitialized()) {
                        FwAppInstallerActivity.this.setInstallEnabled(false);
                        if (FwAppInstallerActivity.this.mayConnect) {
                            FwAppInstallerActivity fwAppInstallerActivity2 = FwAppInstallerActivity.this;
                            C1238GB.toast((Context) fwAppInstallerActivity2, fwAppInstallerActivity2.getString(C0889R.string.connecting), 0, 1);
                            FwAppInstallerActivity.this.connect();
                            return;
                        }
                        FwAppInstallerActivity fwAppInstallerActivity3 = FwAppInstallerActivity.this;
                        fwAppInstallerActivity3.setInfoText(fwAppInstallerActivity3.getString(C0889R.string.fwappinstaller_connection_state, new Object[]{fwAppInstallerActivity3.device.getStateString()}));
                        return;
                    }
                    FwAppInstallerActivity.this.validateInstallation();
                }
            } else if (C1238GB.ACTION_DISPLAY_MESSAGE.equals(action)) {
                FwAppInstallerActivity.this.addMessage(intent.getStringExtra(C1238GB.DISPLAY_MESSAGE_MESSAGE), intent.getIntExtra(C1238GB.DISPLAY_MESSAGE_SEVERITY, 1));
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mayConnect;
    /* access modifiers changed from: private */
    public Uri uri;

    /* access modifiers changed from: private */
    public void refreshBusyState(GBDevice dev) {
        boolean wasBusy = false;
        if (dev.isConnecting() || dev.isBusy()) {
            this.mProgressBar.setVisibility(0);
            return;
        }
        if (this.mProgressBar.getVisibility() != 8) {
            wasBusy = true;
        }
        if (wasBusy) {
            this.mProgressBar.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public void connect() {
        this.mayConnect = false;
        GBApplication.deviceService().connect(this.device);
    }

    /* access modifiers changed from: private */
    public void validateInstallation() {
        InstallHandler installHandler2 = this.installHandler;
        if (installHandler2 != null) {
            installHandler2.validateInstallation(this, this.device);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_appinstaller);
        GBDevice dev = (GBDevice) getIntent().getParcelableExtra(GBDevice.EXTRA_DEVICE);
        if (dev != null) {
            this.device = dev;
        }
        if (savedInstanceState != null) {
            this.mDetails = savedInstanceState.getParcelableArrayList(ITEM_DETAILS);
            if (this.mDetails == null) {
                this.mDetails = new ArrayList<>();
            }
        }
        this.mayConnect = true;
        this.itemListView = (ListView) findViewById(C0889R.C0891id.itemListView);
        this.mItemAdapter = new ItemWithDetailsAdapter(this, this.mItems);
        this.itemListView.setAdapter(this.mItemAdapter);
        this.fwAppInstallTextView = (TextView) findViewById(C0889R.C0891id.infoTextView);
        this.installButton = (Button) findViewById(C0889R.C0891id.installButton);
        this.mProgressBar = (ProgressBar) findViewById(C0889R.C0891id.installProgressBar);
        this.detailsListView = (ListView) findViewById(C0889R.C0891id.detailsListView);
        this.mDetailsItemAdapter = new ItemWithDetailsAdapter(this, this.mDetails);
        this.mDetailsItemAdapter.setSize(1);
        this.detailsListView.setAdapter(this.mDetailsItemAdapter);
        setInstallEnabled(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(GBDevice.ACTION_DEVICE_CHANGED);
        filter.addAction(C1238GB.ACTION_DISPLAY_MESSAGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, filter);
        this.installButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FwAppInstallerActivity.this.setInstallEnabled(false);
                FwAppInstallerActivity.this.installHandler.onStartInstall(FwAppInstallerActivity.this.device);
                GBApplication.deviceService().onInstallApp(FwAppInstallerActivity.this.uri);
            }
        });
        this.uri = getIntent().getData();
        if (this.uri == null) {
            this.uri = (Uri) getIntent().getParcelableExtra("android.intent.extra.STREAM");
        }
        this.installHandler = findInstallHandlerFor(this.uri);
        if (this.installHandler == null) {
            setInfoText(getString(C0889R.string.installer_activity_unable_to_find_handler));
            return;
        }
        setInfoText(getString(C0889R.string.installer_activity_wait_while_determining_status));
        GBDevice gBDevice = this.device;
        if (gBDevice == null || !gBDevice.isConnected()) {
            connect();
        } else {
            GBApplication.deviceService().requestDeviceInfo();
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ITEM_DETAILS, this.mDetails);
    }

    private InstallHandler findInstallHandlerFor(Uri uri2) {
        for (DeviceCoordinator coordinator : getAllCoordinatorsConnectedFirst()) {
            InstallHandler handler = coordinator.findInstallHandler(uri2, this);
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }

    private List<DeviceCoordinator> getAllCoordinatorsConnectedFirst() {
        DeviceCoordinator coordinator;
        DeviceManager deviceManager = ((GBApplication) getApplicationContext()).getDeviceManager();
        List<DeviceCoordinator> connectedCoordinators = new ArrayList<>();
        List<DeviceCoordinator> allCoordinators = DeviceHelper.getInstance().getAllCoordinators();
        List<DeviceCoordinator> sortedCoordinators = new ArrayList<>(allCoordinators.size());
        GBDevice connectedDevice = deviceManager.getSelectedDevice();
        if (!(connectedDevice == null || !connectedDevice.isConnected() || (coordinator = DeviceHelper.getInstance().getCoordinator(connectedDevice)) == null)) {
            connectedCoordinators.add(coordinator);
        }
        sortedCoordinators.addAll(connectedCoordinators);
        for (DeviceCoordinator coordinator2 : allCoordinators) {
            if (!connectedCoordinators.contains(coordinator2)) {
                sortedCoordinators.add(coordinator2);
            }
        }
        return sortedCoordinators;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mReceiver);
        super.onDestroy();
    }

    public void setInfoText(String text) {
        this.fwAppInstallTextView.setText(text);
    }

    public CharSequence getInfoText() {
        return this.fwAppInstallTextView.getText();
    }

    public void setInstallEnabled(boolean enable) {
        GBDevice gBDevice = this.device;
        int i = 0;
        boolean enabled = gBDevice != null && gBDevice.isConnected() && enable;
        this.installButton.setEnabled(enabled);
        Button button = this.installButton;
        if (!enabled) {
            i = 8;
        }
        button.setVisibility(i);
    }

    public void clearInstallItems() {
        this.mItems.clear();
        this.mItemAdapter.notifyDataSetChanged();
    }

    public void setInstallItem(ItemWithDetails item) {
        this.mItems.clear();
        this.mItems.add(item);
        this.mItemAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void addMessage(String message, int severity) {
        this.mDetails.add(new GenericItem(message));
        this.mDetailsItemAdapter.notifyDataSetChanged();
    }
}
