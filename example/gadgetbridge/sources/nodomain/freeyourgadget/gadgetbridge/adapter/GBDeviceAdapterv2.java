package nodomain.freeyourgadget.gadgetbridge.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import java.util.List;
import java.util.Locale;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.ActivitySummariesActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.ConfigureAlarms;
import nodomain.freeyourgadget.gadgetbridge.activities.VibrationActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.charts.ChartsActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.devicesettings.DeviceSettingsActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager;
import nodomain.freeyourgadget.gadgetbridge.devices.watch9.Watch9CalibrationActivity;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.BatteryState;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.model.RecordedDataTypes;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;
import org.apache.commons.lang3.StringUtils;

public class GBDeviceAdapterv2 extends RecyclerView.Adapter<ViewHolder> {
    /* access modifiers changed from: private */
    public final Context context;
    private List<GBDevice> deviceList;
    /* access modifiers changed from: private */
    public int expandedDevicePosition = -1;
    /* access modifiers changed from: private */
    public ViewGroup parent;

    public GBDeviceAdapterv2(Context context2, List<GBDevice> deviceList2) {
        this.context = context2;
        this.deviceList = deviceList2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent2, int viewType) {
        this.parent = parent2;
        return new ViewHolder(LayoutInflater.from(parent2.getContext()).inflate(C0889R.layout.device_itemv2, parent2, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewHolder viewHolder = holder;
        final int i = position;
        final GBDevice device = this.deviceList.get(i);
        final DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(device);
        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (device.isInitialized() || device.isConnected()) {
                    GBDeviceAdapterv2.this.showTransientSnackbar(C0889R.string.controlcenter_snackbar_need_longpress);
                    return;
                }
                GBDeviceAdapterv2.this.showTransientSnackbar(C0889R.string.controlcenter_snackbar_connecting);
                GBApplication.deviceService().connect(device);
            }
        });
        viewHolder.container.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                if (device.getState() == GBDevice.State.NOT_CONNECTED) {
                    return true;
                }
                GBDeviceAdapterv2.this.showTransientSnackbar(C0889R.string.controlcenter_snackbar_disconnecting);
                GBApplication.deviceService().disconnect();
                return true;
            }
        });
        viewHolder.deviceImageView.setImageResource(device.isInitialized() ? device.getType().getIcon() : device.getType().getDisabledIcon());
        viewHolder.deviceNameLabel.setText(getUniqueDeviceName(device));
        if (device.isBusy()) {
            viewHolder.deviceStatusLabel.setText(device.getBusyTask());
            viewHolder.busyIndicator.setVisibility(0);
        } else {
            viewHolder.deviceStatusLabel.setText(device.getStateString());
            viewHolder.busyIndicator.setVisibility(4);
        }
        viewHolder.batteryStatusBox.setVisibility(8);
        short batteryLevel = device.getBatteryLevel();
        float batteryVoltage = device.getBatteryVoltage();
        BatteryState batteryState = device.getBatteryState();
        if (batteryLevel != -1) {
            viewHolder.batteryStatusBox.setVisibility(0);
            TextView textView = viewHolder.batteryStatusLabel;
            textView.setText(device.getBatteryLevel() + "%");
            if (BatteryState.BATTERY_CHARGING.equals(batteryState) || BatteryState.BATTERY_CHARGING_FULL.equals(batteryState)) {
                viewHolder.batteryIcon.setImageLevel(device.getBatteryLevel() + 100);
            } else {
                viewHolder.batteryIcon.setImageLevel(device.getBatteryLevel());
            }
        } else if (BatteryState.NO_BATTERY.equals(batteryState) && batteryVoltage != -1.0f) {
            viewHolder.batteryStatusBox.setVisibility(0);
            viewHolder.batteryStatusLabel.setText(String.format(Locale.getDefault(), "%.2f", new Object[]{Float.valueOf(batteryVoltage)}));
            viewHolder.batteryIcon.setImageLevel(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        }
        viewHolder.deviceSpecificSettingsView.setVisibility(coordinator.getSupportedDeviceSpecificSettings(device) != null ? 0 : 8);
        viewHolder.deviceSpecificSettingsView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startIntent = new Intent(GBDeviceAdapterv2.this.context, DeviceSettingsActivity.class);
                startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
                GBDeviceAdapterv2.this.context.startActivity(startIntent);
            }
        });
        viewHolder.fetchActivityDataBox.setVisibility((!device.isInitialized() || !coordinator.supportsActivityDataFetching()) ? 8 : 0);
        viewHolder.fetchActivityData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GBDeviceAdapterv2.this.showTransientSnackbar(C0889R.string.busy_task_fetch_activity_data);
                GBApplication.deviceService().onFetchRecordedData(RecordedDataTypes.TYPE_ACTIVITY);
            }
        });
        viewHolder.takeScreenshotView.setVisibility((!device.isInitialized() || !coordinator.supportsScreenshots()) ? 8 : 0);
        viewHolder.takeScreenshotView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GBDeviceAdapterv2.this.showTransientSnackbar(C0889R.string.controlcenter_snackbar_requested_screenshot);
                GBApplication.deviceService().onScreenshotReq();
            }
        });
        viewHolder.manageAppsView.setVisibility((!device.isInitialized() || !coordinator.supportsAppsManagement()) ? 8 : 0);
        viewHolder.manageAppsView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Class<? extends Activity> appsManagementActivity = DeviceHelper.getInstance().getCoordinator(device).getAppsManagementActivity();
                if (appsManagementActivity != null) {
                    Intent startIntent = new Intent(GBDeviceAdapterv2.this.context, appsManagementActivity);
                    startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
                    GBDeviceAdapterv2.this.context.startActivity(startIntent);
                }
            }
        });
        viewHolder.setAlarmsView.setVisibility(coordinator.getAlarmSlotCount() > 0 ? 0 : 8);
        viewHolder.setAlarmsView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startIntent = new Intent(GBDeviceAdapterv2.this.context, ConfigureAlarms.class);
                startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
                GBDeviceAdapterv2.this.context.startActivity(startIntent);
            }
        });
        viewHolder.showActivityGraphs.setVisibility(coordinator.supportsActivityTracking() ? 0 : 8);
        viewHolder.showActivityGraphs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startIntent = new Intent(GBDeviceAdapterv2.this.context, ChartsActivity.class);
                startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
                GBDeviceAdapterv2.this.context.startActivity(startIntent);
            }
        });
        viewHolder.showActivityTracks.setVisibility(coordinator.supportsActivityTracks() ? 0 : 8);
        viewHolder.showActivityTracks.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startIntent = new Intent(GBDeviceAdapterv2.this.context, ActivitySummariesActivity.class);
                startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
                GBDeviceAdapterv2.this.context.startActivity(startIntent);
            }
        });
        ItemWithDetailsAdapter infoAdapter = new ItemWithDetailsAdapter(this.context, device.getDeviceInfos());
        infoAdapter.setHorizontalAlignment(true);
        viewHolder.deviceInfoList.setAdapter(infoAdapter);
        justifyListViewHeightBasedOnChildren(viewHolder.deviceInfoList);
        viewHolder.deviceInfoList.setFocusable(false);
        final boolean detailsShown = i == this.expandedDevicePosition;
        viewHolder.deviceInfoView.setVisibility(device.hasDeviceInfos() && !device.isBusy() ? 0 : 8);
        viewHolder.deviceInfoBox.setActivated(detailsShown);
        viewHolder.deviceInfoBox.setVisibility(detailsShown ? 0 : 8);
        viewHolder.deviceInfoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int unused = GBDeviceAdapterv2.this.expandedDevicePosition = detailsShown ? -1 : i;
                TransitionManager.beginDelayedTransition(GBDeviceAdapterv2.this.parent);
                GBDeviceAdapterv2.this.notifyDataSetChanged();
            }
        });
        viewHolder.findDevice.setVisibility((!device.isInitialized() || !coordinator.supportsFindDevice()) ? 8 : 0);
        viewHolder.findDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (device.getType() == DeviceType.VIBRATISSIMO) {
                    Intent startIntent = new Intent(GBDeviceAdapterv2.this.context, VibrationActivity.class);
                    startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
                    GBDeviceAdapterv2.this.context.startActivity(startIntent);
                    return;
                }
                GBApplication.deviceService().onFindDevice(true);
                Snackbar.make((View) GBDeviceAdapterv2.this.parent, (int) C0889R.string.control_center_find_lost_device, -2).setAction((CharSequence) "Found it!", (View.OnClickListener) new View.OnClickListener() {
                    public void onClick(View v) {
                        GBApplication.deviceService().onFindDevice(false);
                    }
                }).setCallback(new Snackbar.Callback() {
                    public void onDismissed(Snackbar snackbar, int event) {
                        GBApplication.deviceService().onFindDevice(false);
                        super.onDismissed(snackbar, event);
                    }
                }).show();
            }
        });
        viewHolder.calibrateDevice.setVisibility((!device.isInitialized() || device.getType() != DeviceType.WATCH9) ? 8 : 0);
        viewHolder.calibrateDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startIntent = new Intent(GBDeviceAdapterv2.this.context, Watch9CalibrationActivity.class);
                startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
                GBDeviceAdapterv2.this.context.startActivity(startIntent);
            }
        });
        viewHolder.fmFrequencyBox.setVisibility(8);
        if (device.isInitialized() && device.getExtraInfo(DeviceService.EXTRA_FM_FREQUENCY) != null) {
            viewHolder.fmFrequencyBox.setVisibility(0);
            viewHolder.fmFrequencyLabel.setText(String.format(Locale.getDefault(), "%.1f", new Object[]{Float.valueOf(((Float) device.getExtraInfo(DeviceService.EXTRA_FM_FREQUENCY)).floatValue())}));
        }
        final TextView fmFrequencyLabel = viewHolder.fmFrequencyLabel;
        viewHolder.fmFrequencyBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GBDeviceAdapterv2.this.context);
                builder.setTitle(C0889R.string.preferences_fm_frequency);
                final EditText input = new EditText(GBDeviceAdapterv2.this.context);
                input.setSelection(input.getText().length());
                input.setRawInputType(8194);
                input.setText(String.format(Locale.getDefault(), "%.1f", new Object[]{Float.valueOf(((Float) device.getExtraInfo(DeviceService.EXTRA_FM_FREQUENCY)).floatValue())}));
                builder.setView(input);
                builder.setPositiveButton(GBDeviceAdapterv2.this.context.getResources().getString(17039370), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        float frequency = Float.valueOf(input.getText().toString()).floatValue();
                        float frequency2 = Float.valueOf(String.format(Locale.getDefault(), "%.1f", new Object[]{Float.valueOf(frequency)})).floatValue();
                        if (((double) frequency2) < 87.5d || ((double) frequency2) > 108.0d) {
                            new AlertDialog.Builder(GBDeviceAdapterv2.this.context).setTitle(C0889R.string.pref_invalid_frequency_title).setMessage(C0889R.string.pref_invalid_frequency_message).setNeutralButton(17039370, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                            return;
                        }
                        device.setExtraInfo(DeviceService.EXTRA_FM_FREQUENCY, Float.valueOf(frequency2));
                        fmFrequencyLabel.setText(String.format(Locale.getDefault(), "%.1f", new Object[]{Float.valueOf(((Float) device.getExtraInfo(DeviceService.EXTRA_FM_FREQUENCY)).floatValue())}));
                        GBApplication.deviceService().onSetFmFrequency(frequency2);
                    }
                });
                builder.setNegativeButton(GBDeviceAdapterv2.this.context.getResources().getString(C0889R.string.Cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        viewHolder.ledColor.setVisibility(8);
        if (device.isInitialized() && device.getExtraInfo(DeviceService.EXTRA_LED_COLOR) != null && coordinator.supportsLedColor()) {
            viewHolder.ledColor.setVisibility(0);
            final GradientDrawable ledColor = (GradientDrawable) viewHolder.ledColor.getDrawable().mutate();
            ledColor.setColor(((Integer) device.getExtraInfo(DeviceService.EXTRA_LED_COLOR)).intValue());
            viewHolder.ledColor.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ColorPickerDialog.Builder builder = ColorPickerDialog.newBuilder();
                    builder.setDialogTitle(C0889R.string.preferences_led_color);
                    int[] presets = coordinator.getColorPresets();
                    builder.setColor(((Integer) device.getExtraInfo(DeviceService.EXTRA_LED_COLOR)).intValue());
                    builder.setShowAlphaSlider(false);
                    builder.setShowColorShades(false);
                    if (coordinator.supportsRgbLedColor()) {
                        builder.setAllowCustom(true);
                        if (presets.length == 0) {
                            builder.setDialogType(0);
                        }
                    } else {
                        builder.setAllowCustom(false);
                    }
                    if (presets.length > 0) {
                        builder.setAllowPresets(true);
                        builder.setPresets(presets);
                    }
                    ColorPickerDialog dialog = builder.create();
                    dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                        public void onColorSelected(int dialogId, int color) {
                            ledColor.setColor(color);
                            device.setExtraInfo(DeviceService.EXTRA_LED_COLOR, Integer.valueOf(color));
                            GBApplication.deviceService().onSetLedColor(color);
                        }

                        public void onDialogDismissed(int dialogId) {
                        }
                    });
                    dialog.show(((Activity) GBDeviceAdapterv2.this.context).getFragmentManager(), "color-picker-dialog");
                }
            });
        }
        viewHolder.removeDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(GBDeviceAdapterv2.this.context).setCancelable(true).setTitle(GBDeviceAdapterv2.this.context.getString(C0889R.string.controlcenter_delete_device_name, new Object[]{device.getName()})).setMessage(C0889R.string.controlcenter_delete_device_dialogmessage).setPositiveButton(C0889R.string.Delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent refreshIntent;
                        try {
                            DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(device);
                            if (coordinator != null) {
                                coordinator.deleteDevice(device);
                            }
                            DeviceHelper.getInstance().removeBond(device);
                            refreshIntent = new Intent(DeviceManager.ACTION_REFRESH_DEVICELIST);
                        } catch (Exception ex) {
                            Context access$100 = GBDeviceAdapterv2.this.context;
                            C1238GB.toast(access$100, "Error deleting device: " + ex.getMessage(), 1, 3, ex);
                            refreshIntent = new Intent(DeviceManager.ACTION_REFRESH_DEVICELIST);
                        } catch (Throwable th) {
                            LocalBroadcastManager.getInstance(GBDeviceAdapterv2.this.context).sendBroadcast(new Intent(DeviceManager.ACTION_REFRESH_DEVICELIST));
                            throw th;
                        }
                        LocalBroadcastManager.getInstance(GBDeviceAdapterv2.this.context).sendBroadcast(refreshIntent);
                    }
                }).setNegativeButton(C0889R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
            }
        });
    }

    public int getItemCount() {
        return this.deviceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView batteryIcon;
        LinearLayout batteryStatusBox;
        TextView batteryStatusLabel;
        ProgressBar busyIndicator;
        ImageView calibrateDevice;
        CardView container;
        ImageView deviceImageView;
        final RelativeLayout deviceInfoBox;
        ListView deviceInfoList;
        ImageView deviceInfoView;
        TextView deviceNameLabel;
        ImageView deviceSpecificSettingsView;
        TextView deviceStatusLabel;
        ImageView fetchActivityData;
        LinearLayout fetchActivityDataBox;
        ImageView findDevice;
        LinearLayout fmFrequencyBox;
        TextView fmFrequencyLabel;
        ImageView ledColor;
        ImageView manageAppsView;
        ImageView removeDevice;
        ImageView setAlarmsView;
        ImageView showActivityGraphs;
        ImageView showActivityTracks;
        ImageView takeScreenshotView;

        ViewHolder(View view) {
            super(view);
            this.container = (CardView) view.findViewById(C0889R.C0891id.card_view);
            this.deviceImageView = (ImageView) view.findViewById(C0889R.C0891id.device_image);
            this.deviceNameLabel = (TextView) view.findViewById(C0889R.C0891id.device_name);
            this.deviceStatusLabel = (TextView) view.findViewById(C0889R.C0891id.device_status);
            this.batteryStatusBox = (LinearLayout) view.findViewById(C0889R.C0891id.device_battery_status_box);
            this.batteryStatusLabel = (TextView) view.findViewById(C0889R.C0891id.battery_status);
            this.batteryIcon = (ImageView) view.findViewById(C0889R.C0891id.device_battery_status);
            this.deviceSpecificSettingsView = (ImageView) view.findViewById(C0889R.C0891id.device_specific_settings);
            this.fetchActivityDataBox = (LinearLayout) view.findViewById(C0889R.C0891id.device_action_fetch_activity_box);
            this.fetchActivityData = (ImageView) view.findViewById(C0889R.C0891id.device_action_fetch_activity);
            this.busyIndicator = (ProgressBar) view.findViewById(C0889R.C0891id.device_busy_indicator);
            this.takeScreenshotView = (ImageView) view.findViewById(C0889R.C0891id.device_action_take_screenshot);
            this.manageAppsView = (ImageView) view.findViewById(C0889R.C0891id.device_action_manage_apps);
            this.setAlarmsView = (ImageView) view.findViewById(C0889R.C0891id.device_action_set_alarms);
            this.showActivityGraphs = (ImageView) view.findViewById(C0889R.C0891id.device_action_show_activity_graphs);
            this.showActivityTracks = (ImageView) view.findViewById(C0889R.C0891id.device_action_show_activity_tracks);
            this.deviceInfoView = (ImageView) view.findViewById(C0889R.C0891id.device_info_image);
            this.calibrateDevice = (ImageView) view.findViewById(C0889R.C0891id.device_action_calibrate);
            this.deviceInfoBox = (RelativeLayout) view.findViewById(C0889R.C0891id.device_item_infos_box);
            this.deviceInfoList = (ListView) view.findViewById(C0889R.C0891id.device_item_infos);
            this.findDevice = (ImageView) view.findViewById(C0889R.C0891id.device_action_find);
            this.removeDevice = (ImageView) view.findViewById(C0889R.C0891id.device_action_remove);
            this.fmFrequencyBox = (LinearLayout) view.findViewById(C0889R.C0891id.device_fm_frequency_box);
            this.fmFrequencyLabel = (TextView) view.findViewById(C0889R.C0891id.fm_frequency);
            this.ledColor = (ImageView) view.findViewById(C0889R.C0891id.device_led_color);
        }
    }

    private void justifyListViewHeightBasedOnChildren(ListView listView) {
        ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
        if (adapter != null) {
            int totalHeight = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                View listItem = adapter.getView(i, (View) null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams par = listView.getLayoutParams();
            par.height = (listView.getDividerHeight() * (adapter.getCount() - 1)) + totalHeight;
            listView.setLayoutParams(par);
            listView.requestLayout();
        }
    }

    private String getUniqueDeviceName(GBDevice device) {
        String deviceName = device.getName();
        if (isUniqueDeviceName(device, deviceName)) {
            return deviceName;
        }
        if (device.getModel() != null) {
            String deviceName2 = deviceName + StringUtils.SPACE + device.getModel();
            if (isUniqueDeviceName(device, deviceName2)) {
                return deviceName2;
            }
            return deviceName2 + StringUtils.SPACE + device.getShortAddress();
        }
        return deviceName + StringUtils.SPACE + device.getShortAddress();
    }

    private boolean isUniqueDeviceName(GBDevice device, String deviceName) {
        for (int i = 0; i < this.deviceList.size(); i++) {
            GBDevice item = this.deviceList.get(i);
            if (item != device && deviceName.equals(item.getName())) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void showTransientSnackbar(int resource) {
        Snackbar.make((View) this.parent, resource, -1).show();
    }
}
