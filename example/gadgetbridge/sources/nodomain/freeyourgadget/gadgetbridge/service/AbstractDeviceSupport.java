package nodomain.freeyourgadget.gadgetbridge.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.telephony.SmsManager;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import cyanogenmod.providers.ThemesContract;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.FindPhoneActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.appmanager.AbstractAppManagerFragment;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventAppInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventCallControl;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventDisplayMessage;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventFindPhone;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventFmFrequency;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventLEDColor;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventNotificationControl;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventScreenshot;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.externalevents.NotificationListener;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.BatteryState;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.service.receivers.GBCallControlReceiver;
import nodomain.freeyourgadget.gadgetbridge.service.receivers.GBMusicControlReceiver;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDeviceSupport implements DeviceSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractDeviceSupport.class);
    private static final int NOTIFICATION_ID_SCREENSHOT = 8000;
    private boolean autoReconnect;
    private BluetoothAdapter btAdapter;
    private Context context;
    /* access modifiers changed from: protected */
    public GBDevice gbDevice;

    public void setContext(GBDevice gbDevice2, BluetoothAdapter btAdapter2, Context context2) {
        this.gbDevice = gbDevice2;
        this.btAdapter = btAdapter2;
        this.context = context2;
    }

    public boolean connectFirstTime() {
        return connect();
    }

    public boolean isConnected() {
        return this.gbDevice.isConnected();
    }

    /* access modifiers changed from: protected */
    public boolean isInitialized() {
        return this.gbDevice.isInitialized();
    }

    public void setAutoReconnect(boolean enable) {
        this.autoReconnect = enable;
    }

    public boolean getAutoReconnect() {
        return this.autoReconnect;
    }

    public GBDevice getDevice() {
        return this.gbDevice;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return this.btAdapter;
    }

    public Context getContext() {
        return this.context;
    }

    public void evaluateGBDeviceEvent(GBDeviceEvent deviceEvent) {
        if (deviceEvent instanceof GBDeviceEventMusicControl) {
            handleGBDeviceEvent((GBDeviceEventMusicControl) deviceEvent);
        } else if (deviceEvent instanceof GBDeviceEventCallControl) {
            handleGBDeviceEvent((GBDeviceEventCallControl) deviceEvent);
        } else if (deviceEvent instanceof GBDeviceEventVersionInfo) {
            handleGBDeviceEvent((GBDeviceEventVersionInfo) deviceEvent);
        } else if (deviceEvent instanceof GBDeviceEventAppInfo) {
            handleGBDeviceEvent((GBDeviceEventAppInfo) deviceEvent);
        } else if (deviceEvent instanceof GBDeviceEventScreenshot) {
            handleGBDeviceEvent((GBDeviceEventScreenshot) deviceEvent);
        } else if (deviceEvent instanceof GBDeviceEventNotificationControl) {
            handleGBDeviceEvent((GBDeviceEventNotificationControl) deviceEvent);
        } else if (deviceEvent instanceof GBDeviceEventBatteryInfo) {
            handleGBDeviceEvent((GBDeviceEventBatteryInfo) deviceEvent);
        } else if (deviceEvent instanceof GBDeviceEventFindPhone) {
            handleGBDeviceEvent((GBDeviceEventFindPhone) deviceEvent);
        } else if (deviceEvent instanceof GBDeviceEventLEDColor) {
            handleGBDeviceEvent((GBDeviceEventLEDColor) deviceEvent);
        } else if (deviceEvent instanceof GBDeviceEventFmFrequency) {
            handleGBDeviceEvent((GBDeviceEventFmFrequency) deviceEvent);
        }
    }

    private void handleGBDeviceEvent(GBDeviceEventFindPhone deviceEvent) {
        Context context2 = getContext();
        LOG.info("Got GBDeviceEventFindPhone");
        int i = C11631.f163xf05d8fe2[deviceEvent.event.ordinal()];
        if (i == 1) {
            Intent startIntent = new Intent(getContext(), FindPhoneActivity.class);
            startIntent.setFlags(268435456);
            context2.startActivity(startIntent);
        } else if (i != 2) {
            LOG.warn("unknown GBDeviceEventFindPhone");
        } else {
            LocalBroadcastManager.getInstance(context2).sendBroadcast(new Intent(FindPhoneActivity.ACTION_FOUND));
        }
    }

    private void handleGBDeviceEvent(GBDeviceEventMusicControl musicEvent) {
        Context context2 = getContext();
        LOG.info("Got event for MUSIC_CONTROL");
        Intent musicIntent = new Intent(GBMusicControlReceiver.ACTION_MUSICCONTROL);
        musicIntent.putExtra(NotificationCompat.CATEGORY_EVENT, musicEvent.event.ordinal());
        musicIntent.setPackage(context2.getPackageName());
        context2.sendBroadcast(musicIntent);
    }

    private void handleGBDeviceEvent(GBDeviceEventCallControl callEvent) {
        Context context2 = getContext();
        LOG.info("Got event for CALL_CONTROL");
        if (callEvent.event == GBDeviceEventCallControl.Event.IGNORE) {
            LOG.info("Sending intent for mute");
            Intent broadcastIntent = new Intent("nodomain.freeyourgadget.gadgetbridge.MUTE_CALL");
            broadcastIntent.setPackage(context2.getPackageName());
            context2.sendBroadcast(broadcastIntent);
            return;
        }
        Intent callIntent = new Intent(GBCallControlReceiver.ACTION_CALLCONTROL);
        callIntent.putExtra(NotificationCompat.CATEGORY_EVENT, callEvent.event.ordinal());
        callIntent.setPackage(context2.getPackageName());
        context2.sendBroadcast(callIntent);
    }

    /* access modifiers changed from: protected */
    public void handleGBDeviceEvent(GBDeviceEventVersionInfo infoEvent) {
        Context context2 = getContext();
        Logger logger = LOG;
        logger.info("Got event for VERSION_INFO: " + infoEvent);
        GBDevice gBDevice = this.gbDevice;
        if (gBDevice != null) {
            gBDevice.setFirmwareVersion(infoEvent.fwVersion);
            this.gbDevice.setModel(infoEvent.hwVersion);
            this.gbDevice.sendDeviceUpdateIntent(context2);
        }
    }

    /* access modifiers changed from: protected */
    public void handleGBDeviceEvent(GBDeviceEventLEDColor colorEvent) {
        Context context2 = getContext();
        Logger logger = LOG;
        logger.info("Got event for LED Color: #" + Integer.toHexString(colorEvent.color).toUpperCase());
        GBDevice gBDevice = this.gbDevice;
        if (gBDevice != null) {
            gBDevice.setExtraInfo(DeviceService.EXTRA_LED_COLOR, Integer.valueOf(colorEvent.color));
            this.gbDevice.sendDeviceUpdateIntent(context2);
        }
    }

    /* access modifiers changed from: protected */
    public void handleGBDeviceEvent(GBDeviceEventFmFrequency frequencyEvent) {
        Context context2 = getContext();
        LOG.info("Got event for FM Frequency");
        GBDevice gBDevice = this.gbDevice;
        if (gBDevice != null) {
            gBDevice.setExtraInfo(DeviceService.EXTRA_FM_FREQUENCY, Float.valueOf(frequencyEvent.frequency));
            this.gbDevice.sendDeviceUpdateIntent(context2);
        }
    }

    private void handleGBDeviceEvent(GBDeviceEventAppInfo appInfoEvent) {
        Context context2 = getContext();
        LOG.info("Got event for APP_INFO");
        Intent appInfoIntent = new Intent(AbstractAppManagerFragment.ACTION_REFRESH_APPLIST);
        int appCount = appInfoEvent.apps.length;
        appInfoIntent.putExtra("app_count", appCount);
        for (int i = 0; i < appCount; i++) {
            appInfoIntent.putExtra("app_name" + i, appInfoEvent.apps[i].getName());
            appInfoIntent.putExtra("app_creator" + i, appInfoEvent.apps[i].getCreator());
            appInfoIntent.putExtra(DeviceService.EXTRA_APP_UUID + i, appInfoEvent.apps[i].getUUID().toString());
            appInfoIntent.putExtra("app_type" + i, appInfoEvent.apps[i].getType().ordinal());
        }
        LocalBroadcastManager.getInstance(context2).sendBroadcast(appInfoIntent);
    }

    private void handleGBDeviceEvent(GBDeviceEventScreenshot screenshot) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-hhmmss", Locale.US);
        String filename = "screenshot_" + dateFormat.format(new Date()) + ".bmp";
        try {
            String fullpath = C1238GB.writeScreenshot(screenshot, filename);
            Bitmap bmp = BitmapFactory.decodeFile(fullpath);
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri screenshotURI = FileProvider.getUriForFile(this.context, this.context.getApplicationContext().getPackageName() + ".screenshot_provider", new File(fullpath));
            intent.setDataAndType(screenshotURI, "image/*");
            PendingIntent pIntent = PendingIntent.getActivity(this.context, 0, intent, 0);
            Intent shareIntent = new Intent("android.intent.action.SEND");
            shareIntent.setType("image/*");
            shareIntent.putExtra("android.intent.extra.STREAM", screenshotURI);
            ((NotificationManager) this.context.getSystemService("notification")).notify(8000, new NotificationCompat.Builder(this.context, C1238GB.NOTIFICATION_CHANNEL_ID).setContentTitle("Screenshot taken").setTicker("Screenshot taken").setContentText(filename).setSmallIcon(C0889R.C0890drawable.ic_notification).setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bmp)).setContentIntent(pIntent).addAction(new NotificationCompat.Action.Builder(17301586, "share", PendingIntent.getActivity(this.context, 0, Intent.createChooser(shareIntent, "share screenshot"), 134217728)).build()).setAutoCancel(true).build());
        } catch (IOException ex) {
            LOG.error("Error writing screenshot", (Throwable) ex);
        }
    }

    private void handleGBDeviceEvent(GBDeviceEventNotificationControl deviceEvent) {
        Context context2 = getContext();
        LOG.info("Got NOTIFICATION CONTROL device event");
        String action = null;
        int i = C11631.f164x3dcb201f[deviceEvent.event.ordinal()];
        if (i == 1) {
            action = NotificationListener.ACTION_DISMISS;
        } else if (i == 2) {
            action = NotificationListener.ACTION_DISMISS_ALL;
        } else if (i == 3) {
            action = NotificationListener.ACTION_OPEN;
        } else if (i == 4) {
            action = NotificationListener.ACTION_MUTE;
        } else if (i == 5) {
            if (deviceEvent.phoneNumber == null) {
                deviceEvent.phoneNumber = (String) GBApplication.getIDSenderLookup().lookup((int) (deviceEvent.handle >> 4));
            }
            if (deviceEvent.phoneNumber != null) {
                LOG.info("Got notification reply for SMS from " + deviceEvent.phoneNumber + " : " + deviceEvent.reply);
                SmsManager.getDefault().sendTextMessage(deviceEvent.phoneNumber, (String) null, deviceEvent.reply, (PendingIntent) null, (PendingIntent) null);
            } else {
                LOG.info("Got notification reply for notification id " + deviceEvent.handle + " : " + deviceEvent.reply);
                action = NotificationListener.ACTION_REPLY;
            }
        }
        if (action != null) {
            Intent notificationListenerIntent = new Intent(action);
            notificationListenerIntent.putExtra("handle", deviceEvent.handle);
            notificationListenerIntent.putExtra(ThemesContract.ThemesColumns.TITLE, deviceEvent.title);
            if (deviceEvent.reply != null) {
                String suffix = GBApplication.getPrefs().getString("canned_reply_suffix", (String) null);
                if (suffix != null && !Objects.equals(suffix, "")) {
                    deviceEvent.reply += suffix;
                }
                notificationListenerIntent.putExtra("reply", deviceEvent.reply);
            }
            LocalBroadcastManager.getInstance(context2).sendBroadcast(notificationListenerIntent);
        }
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.service.AbstractDeviceSupport$1 */
    static /* synthetic */ class C11631 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$deviceevents$GBDeviceEventFindPhone$Event */
        static final /* synthetic */ int[] f163xf05d8fe2 = new int[GBDeviceEventFindPhone.Event.values().length];

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$deviceevents$GBDeviceEventNotificationControl$Event */
        static final /* synthetic */ int[] f164x3dcb201f = new int[GBDeviceEventNotificationControl.Event.values().length];

        static {
            try {
                f164x3dcb201f[GBDeviceEventNotificationControl.Event.DISMISS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f164x3dcb201f[GBDeviceEventNotificationControl.Event.DISMISS_ALL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f164x3dcb201f[GBDeviceEventNotificationControl.Event.OPEN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f164x3dcb201f[GBDeviceEventNotificationControl.Event.MUTE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f164x3dcb201f[GBDeviceEventNotificationControl.Event.REPLY.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f163xf05d8fe2[GBDeviceEventFindPhone.Event.START.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f163xf05d8fe2[GBDeviceEventFindPhone.Event.STOP.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public void handleGBDeviceEvent(GBDeviceEventBatteryInfo deviceEvent) {
        Context context2 = getContext();
        LOG.info("Got BATTERY_INFO device event");
        this.gbDevice.setBatteryLevel(deviceEvent.level);
        this.gbDevice.setBatteryState(deviceEvent.state);
        this.gbDevice.setBatteryVoltage(deviceEvent.voltage);
        String str = "";
        if (deviceEvent.level == -1) {
            if (BatteryState.BATTERY_LOW.equals(deviceEvent.state)) {
                String string = context2.getString(C0889R.string.notif_battery_low, new Object[]{this.gbDevice.getName()});
                if (deviceEvent.extendedInfoAvailable()) {
                    str = context2.getString(C0889R.string.notif_battery_low_extended, new Object[]{this.gbDevice.getName(), context2.getString(C0889R.string.notif_battery_low_bigtext_last_charge_time, new Object[]{DateFormat.getDateTimeInstance().format(deviceEvent.lastChargeTime.getTime())}) + context2.getString(C0889R.string.notif_battery_low_bigtext_number_of_charges, new Object[]{String.valueOf(deviceEvent.numCharges)})});
                }
                C1238GB.updateBatteryNotification(string, str, context2);
            } else {
                C1238GB.removeBatteryNotification(context2);
            }
        } else if (deviceEvent.level > this.gbDevice.getBatteryThresholdPercent() || (!BatteryState.BATTERY_LOW.equals(deviceEvent.state) && !BatteryState.BATTERY_NORMAL.equals(deviceEvent.state))) {
            C1238GB.removeBatteryNotification(context2);
        } else {
            String string2 = context2.getString(C0889R.string.notif_battery_low_percent, new Object[]{this.gbDevice.getName(), String.valueOf(deviceEvent.level)});
            if (deviceEvent.extendedInfoAvailable()) {
                str = context2.getString(C0889R.string.notif_battery_low_percent, new Object[]{this.gbDevice.getName(), String.valueOf(deviceEvent.level)}) + StringUtils.f210LF + context2.getString(C0889R.string.notif_battery_low_bigtext_last_charge_time, new Object[]{DateFormat.getDateTimeInstance().format(deviceEvent.lastChargeTime.getTime())}) + context2.getString(C0889R.string.notif_battery_low_bigtext_number_of_charges, new Object[]{String.valueOf(deviceEvent.numCharges)});
            }
            C1238GB.updateBatteryNotification(string2, str, context2);
        }
        this.gbDevice.sendDeviceUpdateIntent(context2);
    }

    public void handleGBDeviceEvent(GBDeviceEventDisplayMessage message) {
        C1238GB.log(message.message, message.severity, (Throwable) null);
        Intent messageIntent = new Intent(C1238GB.ACTION_DISPLAY_MESSAGE);
        messageIntent.putExtra(C1238GB.DISPLAY_MESSAGE_MESSAGE, message.message);
        messageIntent.putExtra(C1238GB.DISPLAY_MESSAGE_DURATION, message.duration);
        messageIntent.putExtra(C1238GB.DISPLAY_MESSAGE_SEVERITY, message.severity);
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(messageIntent);
    }

    public String customStringFilter(String inputString) {
        return inputString;
    }
}
