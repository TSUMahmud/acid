package nodomain.freeyourgadget.gadgetbridge.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.core.app.NavUtils;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;
import nodomain.freeyourgadget.gadgetbridge.model.RecordedDataTypes;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugActivity extends AbstractGBActivity {
    private static final String ACTION_REPLY = "nodomain.freeyourgadget.gadgetbridge.DebugActivity.action.reply";
    private static final String EXTRA_REPLY = "reply";
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) DebugActivity.class);
    /* access modifiers changed from: private */
    public EditText editContent;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* JADX WARNING: Removed duplicated region for block: B:12:0x0032  */
        /* JADX WARNING: Removed duplicated region for block: B:15:0x005d  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r8, android.content.Intent r9) {
            /*
                r7 = this;
                java.lang.String r0 = r9.getAction()
                java.lang.Object r0 = java.util.Objects.requireNonNull(r0)
                java.lang.String r0 = (java.lang.String) r0
                int r1 = r0.hashCode()
                r2 = -649703171(0xffffffffd94650fd, float:-3.48881831E15)
                r3 = 0
                r4 = 1
                if (r1 == r2) goto L_0x0025
                r2 = 179490685(0xab2cf7d, float:1.7218825E-32)
                if (r1 == r2) goto L_0x001b
            L_0x001a:
                goto L_0x002f
            L_0x001b:
                java.lang.String r1 = "nodomain.freeyourgadget.gadgetbridge.devices.action.realtime_samples"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x001a
                r0 = 1
                goto L_0x0030
            L_0x0025:
                java.lang.String r1 = "nodomain.freeyourgadget.gadgetbridge.DebugActivity.action.reply"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x001a
                r0 = 0
                goto L_0x0030
            L_0x002f:
                r0 = -1
            L_0x0030:
                if (r0 == 0) goto L_0x005d
                if (r0 == r4) goto L_0x0051
                org.slf4j.Logger r0 = nodomain.freeyourgadget.gadgetbridge.activities.DebugActivity.LOG
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "ignoring intent action "
                r1.append(r2)
                java.lang.String r2 = r9.getAction()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.info(r1)
                goto L_0x0092
            L_0x0051:
                nodomain.freeyourgadget.gadgetbridge.activities.DebugActivity r0 = nodomain.freeyourgadget.gadgetbridge.activities.DebugActivity.this
                java.lang.String r1 = "realtime_sample"
                java.io.Serializable r1 = r9.getSerializableExtra(r1)
                r0.handleRealtimeSample(r1)
                goto L_0x0092
            L_0x005d:
                android.os.Bundle r0 = androidx.core.app.RemoteInput.getResultsFromIntent(r9)
                java.lang.String r1 = "reply"
                java.lang.CharSequence r1 = r0.getCharSequence(r1)
                org.slf4j.Logger r2 = nodomain.freeyourgadget.gadgetbridge.activities.DebugActivity.LOG
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "got wearable reply: "
                r5.append(r6)
                r5.append(r1)
                java.lang.String r5 = r5.toString()
                r2.info(r5)
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                r2.append(r6)
                r2.append(r1)
                java.lang.String r2 = r2.toString()
                nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r8, (java.lang.String) r2, (int) r3, (int) r4)
            L_0x0092:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.DebugActivity.C09291.onReceive(android.content.Context, android.content.Intent):void");
        }
    };
    /* access modifiers changed from: private */
    public Spinner sendTypeSpinner;

    /* access modifiers changed from: private */
    public void handleRealtimeSample(Serializable extra) {
        if (extra instanceof ActivitySample) {
            C1238GB.toast((Context) this, "Heart Rate measured: " + ((ActivitySample) extra).getHeartRate(), 1, 1);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_debug);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REPLY);
        filter.addAction(DeviceService.ACTION_REALTIME_SAMPLES);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, filter);
        registerReceiver(this.mReceiver, filter);
        this.editContent = (EditText) findViewById(C0889R.C0891id.editContent);
        ArrayList<String> spinnerArray = new ArrayList<>();
        for (NotificationType notificationType : NotificationType.values()) {
            spinnerArray.add(notificationType.name());
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, 17367049, spinnerArray);
        this.sendTypeSpinner = (Spinner) findViewById(C0889R.C0891id.sendTypeSpinner);
        this.sendTypeSpinner.setAdapter(spinnerArrayAdapter);
        ((Button) findViewById(C0889R.C0891id.sendButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NotificationSpec notificationSpec = new NotificationSpec();
                String testString = DebugActivity.this.editContent.getText().toString();
                notificationSpec.phoneNumber = testString;
                notificationSpec.body = testString;
                notificationSpec.sender = testString;
                notificationSpec.subject = testString;
                notificationSpec.type = NotificationType.values()[DebugActivity.this.sendTypeSpinner.getSelectedItemPosition()];
                notificationSpec.pebbleColor = notificationSpec.type.color;
                GBApplication.deviceService().onNotification(notificationSpec);
            }
        });
        ((Button) findViewById(C0889R.C0891id.incomingCallButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CallSpec callSpec = new CallSpec();
                callSpec.command = 2;
                callSpec.number = DebugActivity.this.editContent.getText().toString();
                GBApplication.deviceService().onSetCallState(callSpec);
            }
        });
        ((Button) findViewById(C0889R.C0891id.outgoingCallButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CallSpec callSpec = new CallSpec();
                callSpec.command = 3;
                callSpec.number = DebugActivity.this.editContent.getText().toString();
                GBApplication.deviceService().onSetCallState(callSpec);
            }
        });
        ((Button) findViewById(C0889R.C0891id.startCallButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CallSpec callSpec = new CallSpec();
                callSpec.command = 5;
                GBApplication.deviceService().onSetCallState(callSpec);
            }
        });
        ((Button) findViewById(C0889R.C0891id.endCallButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CallSpec callSpec = new CallSpec();
                callSpec.command = 6;
                GBApplication.deviceService().onSetCallState(callSpec);
            }
        });
        ((Button) findViewById(C0889R.C0891id.rebootButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GBApplication.deviceService().onReset(1);
            }
        });
        ((Button) findViewById(C0889R.C0891id.factoryResetButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(DebugActivity.this).setCancelable(true).setTitle(C0889R.string.debugactivity_really_factoryreset_title).setMessage(C0889R.string.debugactivity_really_factoryreset).setPositiveButton(C0889R.string.f117ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GBApplication.deviceService().onReset(2);
                    }
                }).setNegativeButton(C0889R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
            }
        });
        ((Button) findViewById(C0889R.C0891id.HeartRateButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                C1238GB.toast("Measuring heart rate, please wait...", 1, 1);
                GBApplication.deviceService().onHeartRateTest();
            }
        });
        ((Button) findViewById(C0889R.C0891id.SetFetchTimeButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar currentDate = Calendar.getInstance();
                Context context = DebugActivity.this.getApplicationContext();
                if (context instanceof GBApplication) {
                    final GBDevice device = ((GBApplication) context).getDeviceManager().getSelectedDevice();
                    if (device != null) {
                        new DatePickerDialog(DebugActivity.this, new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar date = Calendar.getInstance();
                                date.set(year, monthOfYear, dayOfMonth);
                                long timestamp = date.getTimeInMillis() - 1000;
                                C1238GB.toast("Setting lastSyncTimeMillis: " + timestamp, 1, 1);
                                SharedPreferences.Editor editor = GBApplication.getDeviceSpecificSharedPrefs(device.getAddress()).edit();
                                editor.remove("lastSyncTimeMillis");
                                editor.putLong("lastSyncTimeMillis", timestamp);
                                editor.apply();
                            }
                        }, currentDate.get(1), currentDate.get(2), currentDate.get(5)).show();
                    } else {
                        C1238GB.toast("Device not selected/connected", 1, 1);
                    }
                }
            }
        });
        ((Button) findViewById(C0889R.C0891id.setMusicInfoButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MusicSpec musicSpec = new MusicSpec();
                String testString = DebugActivity.this.editContent.getText().toString();
                musicSpec.artist = testString + "(artist)";
                musicSpec.album = testString + "(album)";
                musicSpec.track = testString + "(track)";
                musicSpec.duration = 10;
                musicSpec.trackCount = 5;
                musicSpec.trackNr = 2;
                GBApplication.deviceService().onSetMusicInfo(musicSpec);
                MusicStateSpec stateSpec = new MusicStateSpec();
                stateSpec.position = 0;
                stateSpec.state = 1;
                stateSpec.playRate = 100;
                stateSpec.repeat = 1;
                stateSpec.shuffle = 1;
                GBApplication.deviceService().onSetMusicState(stateSpec);
            }
        });
        ((Button) findViewById(C0889R.C0891id.setTimeButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GBApplication.deviceService().onSetTime();
            }
        });
        IntentFilter intentFilter = filter;
        ((Button) findViewById(C0889R.C0891id.testNotificationButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DebugActivity.this.testNotification();
            }
        });
        Button testPebbleKitNotificationButton = (Button) findViewById(C0889R.C0891id.testPebbleKitNotificationButton);
        ArrayList<String> arrayList = spinnerArray;
        testPebbleKitNotificationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DebugActivity.this.testPebbleKitNotification();
            }
        });
        Button fetchDebugLogsButton = (Button) findViewById(C0889R.C0891id.fetchDebugLogsButton);
        Button button = testPebbleKitNotificationButton;
        fetchDebugLogsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GBApplication.deviceService().onFetchRecordedData(RecordedDataTypes.TYPE_DEBUGLOGS);
            }
        });
        Button testNewFunctionalityButton = (Button) findViewById(C0889R.C0891id.testNewFunctionality);
        Button button2 = fetchDebugLogsButton;
        testNewFunctionalityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DebugActivity.this.testNewFunctionality();
            }
        });
        Button button3 = testNewFunctionalityButton;
        ((Button) findViewById(C0889R.C0891id.shareLog)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DebugActivity.this.showWarning();
            }
        });
    }

    /* access modifiers changed from: private */
    public void showWarning() {
        new AlertDialog.Builder(this).setCancelable(true).setTitle(C0889R.string.warning).setMessage(C0889R.string.share_log_warning).setPositiveButton(C0889R.string.f117ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DebugActivity.this.shareLog();
            }
        }).setNegativeButton(C0889R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    /* access modifiers changed from: private */
    public void testNewFunctionality() {
        GBApplication.deviceService().onTestNewFunction();
    }

    /* access modifiers changed from: private */
    public void shareLog() {
        String fileName = GBApplication.getLogPath();
        if (fileName != null && fileName.length() > 0) {
            File logFile = new File(fileName);
            if (!logFile.exists()) {
                C1238GB.toast("File does not exist", 1, 1);
                return;
            }
            Intent emailIntent = new Intent("android.intent.action.SEND");
            emailIntent.setType("*/*");
            emailIntent.putExtra("android.intent.extra.SUBJECT", "Gadgetbridge log file");
            emailIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(logFile));
            startActivity(Intent.createChooser(emailIntent, "Share File"));
        }
    }

    /* access modifiers changed from: private */
    public void testNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), DebugActivity.class);
        notificationIntent.setFlags(268468224);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        NotificationManager nManager = (NotificationManager) getSystemService("notification");
        NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this, C1238GB.NOTIFICATION_CHANNEL_ID).setContentTitle(getString(C0889R.string.test_notification)).setContentText(getString(C0889R.string.this_is_a_test_notification_from_gadgetbridge)).setTicker(getString(C0889R.string.this_is_a_test_notification_from_gadgetbridge)).setSmallIcon(C0889R.C0890drawable.ic_notification).setAutoCancel(true).setContentIntent(pendingIntent).extend(new NotificationCompat.WearableExtender().addAction(new NotificationCompat.Action.Builder(17301547, "Reply", PendingIntent.getBroadcast(this, 0, new Intent(ACTION_REPLY), 0)).addRemoteInput(new RemoteInput.Builder(EXTRA_REPLY).build()).build()));
        if (nManager != null) {
            nManager.notify((int) System.currentTimeMillis(), ncomp.build());
        }
    }

    /* access modifiers changed from: private */
    public void testPebbleKitNotification() {
        Intent pebbleKitIntent = new Intent("com.getpebble.action.SEND_NOTIFICATION");
        pebbleKitIntent.putExtra("messageType", "PEBBLE_ALERT");
        pebbleKitIntent.putExtra("notificationData", "[{\"title\":\"PebbleKitTest\",\"body\":\"sent from Gadgetbridge\"}]");
        getApplicationContext().sendBroadcast(pebbleKitIntent);
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
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mReceiver);
        unregisterReceiver(this.mReceiver);
    }
}
