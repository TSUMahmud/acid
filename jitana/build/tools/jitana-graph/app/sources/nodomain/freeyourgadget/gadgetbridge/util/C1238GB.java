package nodomain.freeyourgadget.gadgetbridge.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBEnvironment;
import nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2;
import nodomain.freeyourgadget.gadgetbridge.activities.SettingsActivity;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.service.DeviceCommunicationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* renamed from: nodomain.freeyourgadget.gadgetbridge.util.GB */
public class C1238GB {
    public static final String ACTION_DISPLAY_MESSAGE = "GB_Display_Message";
    public static final String DISPLAY_MESSAGE_DURATION = "duration";
    public static final String DISPLAY_MESSAGE_MESSAGE = "message";
    public static final String DISPLAY_MESSAGE_SEVERITY = "severity";
    public static final int ERROR = 3;
    public static final int INFO = 1;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) C1238GB.class);
    public static final String NOTIFICATION_CHANNEL_ID = "gadgetbridge";
    public static final String NOTIFICATION_CHANNEL_ID_TRANSFER = "gadgetbridge transfer";
    public static final int NOTIFICATION_ID = 1;
    public static final int NOTIFICATION_ID_EXPORT_FAILED = 5;
    public static final int NOTIFICATION_ID_INSTALL = 2;
    public static final int NOTIFICATION_ID_LOW_BATTERY = 3;
    public static final int NOTIFICATION_ID_TRANSFER = 4;
    public static final int WARN = 2;

    private static PendingIntent getContentIntent(Context context) {
        Intent notificationIntent = new Intent(context, ControlCenterv2.class);
        notificationIntent.setFlags(268468224);
        return PendingIntent.getActivity(context, 0, notificationIntent, 0);
    }

    public static Notification createNotification(GBDevice device, Context context) {
        String deviceName = device.getName();
        String text = device.getStateString();
        if (device.getBatteryLevel() != -1) {
            text = text + ": " + context.getString(C0889R.string.battery) + StringUtils.SPACE + device.getBatteryLevel() + "%";
        }
        boolean connected = device.isInitialized();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(deviceName).setTicker(deviceName + " - " + text).setContentText(text).setSmallIcon(connected ? device.getNotificationIconConnected() : device.getNotificationIconDisconnected()).setContentIntent(getContentIntent(context)).setColor(context.getResources().getColor(C0889R.color.accent)).setOngoing(true);
        Intent deviceCommunicationServiceIntent = new Intent(context, DeviceCommunicationService.class);
        if (connected) {
            deviceCommunicationServiceIntent.setAction(DeviceService.ACTION_DISCONNECT);
            builder.addAction(C0889R.C0890drawable.ic_notification_disconnected, context.getString(C0889R.string.controlcenter_disconnect), PendingIntent.getService(context, 0, deviceCommunicationServiceIntent, 1073741824));
            if (GBApplication.isRunningLollipopOrLater() && DeviceHelper.getInstance().getCoordinator(device).supportsActivityDataFetching()) {
                deviceCommunicationServiceIntent.setAction(DeviceService.ACTION_FETCH_RECORDED_DATA);
                builder.addAction(C0889R.C0890drawable.ic_action_fetch_activity_data, context.getString(C0889R.string.controlcenter_fetch_activity_data), PendingIntent.getService(context, 1, deviceCommunicationServiceIntent, 1073741824));
            }
        } else if (device.getState().equals(GBDevice.State.WAITING_FOR_RECONNECT) || device.getState().equals(GBDevice.State.NOT_CONNECTED)) {
            deviceCommunicationServiceIntent.setAction(DeviceService.ACTION_CONNECT);
            deviceCommunicationServiceIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
            builder.addAction(C0889R.C0890drawable.ic_notification, context.getString(C0889R.string.controlcenter_connect), PendingIntent.getService(context, 2, deviceCommunicationServiceIntent, 134217728));
        }
        if (GBApplication.isRunningLollipopOrLater()) {
            builder.setVisibility(1);
        }
        if (GBApplication.minimizeNotification()) {
            builder.setPriority(-2);
        }
        return builder.build();
    }

    public static Notification createNotification(String text, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setTicker(text).setContentText(text).setSmallIcon(C0889R.C0890drawable.ic_notification_disconnected).setContentIntent(getContentIntent(context)).setColor(context.getResources().getColor(C0889R.color.accent)).setOngoing(true);
        if (GBApplication.getPrefs().getString("last_device_address", (String) null) != null) {
            Intent deviceCommunicationServiceIntent = new Intent(context, DeviceCommunicationService.class);
            deviceCommunicationServiceIntent.setAction(DeviceService.ACTION_CONNECT);
            builder.addAction(C0889R.C0890drawable.ic_notification, context.getString(C0889R.string.controlcenter_connect), PendingIntent.getService(context, 2, deviceCommunicationServiceIntent, 1073741824));
        }
        if (GBApplication.isRunningLollipopOrLater()) {
            builder.setVisibility(1);
        }
        if (GBApplication.minimizeNotification()) {
            builder.setPriority(-2);
        }
        return builder.build();
    }

    public static void updateNotification(GBDevice device, Context context) {
        updateNotification(createNotification(device, context), 1, context);
    }

    private static void updateNotification(Notification notification, int id, Context context) {
        if (notification != null) {
            NotificationManagerCompat.from(context).notify(id, notification);
        }
    }

    private static void removeNotification(int id, Context context) {
        NotificationManagerCompat.from(context).cancel(id);
    }

    public static boolean isBluetoothEnabled() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter != null && adapter.isEnabled();
    }

    public static boolean supportsBluetoothLE() {
        return GBApplication.getContext().getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
    }

    public static String hexdump(byte[] buffer, int offset, int length) {
        if (length == -1) {
            length = buffer.length - offset;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[(length * 2)];
        for (int i = 0; i < length; i++) {
            int v = buffer[i + offset] & 255;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[(i * 2) + 1] = hexArray[v & 15];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[(len / 2)];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String formatRssi(short rssi) {
        return String.valueOf(rssi);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x00cd, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x00d2, code lost:
        r7 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x00d3, code lost:
        r5.addSuppressed(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x00d7, code lost:
        throw r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String writeScreenshot(nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventScreenshot r11, java.lang.String r12) throws java.io.IOException {
        /*
            org.slf4j.Logger r0 = LOG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Will write screenshot: "
            r1.append(r2)
            int r2 = r11.width
            r1.append(r2)
            java.lang.String r2 = "x"
            r1.append(r2)
            int r3 = r11.height
            r1.append(r3)
            r1.append(r2)
            byte r2 = r11.bpp
            r1.append(r2)
            java.lang.String r2 = "bpp"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.info(r1)
            r0 = 14
            r1 = 40
            java.io.File r2 = nodomain.freeyourgadget.gadgetbridge.util.FileUtils.getExternalFilesDir()
            java.io.File r3 = new java.io.File
            r3.<init>(r2, r12)
            java.io.FileOutputStream r4 = new java.io.FileOutputStream
            r4.<init>(r3)
            byte[] r5 = r11.clut     // Catch:{ all -> 0x00cb }
            int r5 = r5.length     // Catch:{ all -> 0x00cb }
            int r5 = r5 + 54
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocate(r5)     // Catch:{ all -> 0x00cb }
            java.nio.ByteOrder r6 = java.nio.ByteOrder.LITTLE_ENDIAN     // Catch:{ all -> 0x00cb }
            r5.order(r6)     // Catch:{ all -> 0x00cb }
            r6 = 66
            r5.put(r6)     // Catch:{ all -> 0x00cb }
            r6 = 77
            r5.put(r6)     // Catch:{ all -> 0x00cb }
            r6 = 0
            r5.putInt(r6)     // Catch:{ all -> 0x00cb }
            r5.putInt(r6)     // Catch:{ all -> 0x00cb }
            byte[] r7 = r11.clut     // Catch:{ all -> 0x00cb }
            int r7 = r7.length     // Catch:{ all -> 0x00cb }
            int r7 = r7 + 54
            r5.putInt(r7)     // Catch:{ all -> 0x00cb }
            r7 = 40
            r5.putInt(r7)     // Catch:{ all -> 0x00cb }
            int r7 = r11.width     // Catch:{ all -> 0x00cb }
            r5.putInt(r7)     // Catch:{ all -> 0x00cb }
            int r7 = r11.height     // Catch:{ all -> 0x00cb }
            int r7 = -r7
            r5.putInt(r7)     // Catch:{ all -> 0x00cb }
            r7 = 1
            r5.putShort(r7)     // Catch:{ all -> 0x00cb }
            byte r7 = r11.bpp     // Catch:{ all -> 0x00cb }
            short r7 = (short) r7     // Catch:{ all -> 0x00cb }
            r5.putShort(r7)     // Catch:{ all -> 0x00cb }
            r5.putInt(r6)     // Catch:{ all -> 0x00cb }
            r5.putInt(r6)     // Catch:{ all -> 0x00cb }
            r5.putInt(r6)     // Catch:{ all -> 0x00cb }
            r5.putInt(r6)     // Catch:{ all -> 0x00cb }
            byte[] r7 = r11.clut     // Catch:{ all -> 0x00cb }
            int r7 = r7.length     // Catch:{ all -> 0x00cb }
            int r7 = r7 / 4
            r5.putInt(r7)     // Catch:{ all -> 0x00cb }
            r5.putInt(r6)     // Catch:{ all -> 0x00cb }
            byte[] r7 = r11.clut     // Catch:{ all -> 0x00cb }
            r5.put(r7)     // Catch:{ all -> 0x00cb }
            byte[] r7 = r5.array()     // Catch:{ all -> 0x00cb }
            r4.write(r7)     // Catch:{ all -> 0x00cb }
            int r7 = r11.width     // Catch:{ all -> 0x00cb }
            byte r8 = r11.bpp     // Catch:{ all -> 0x00cb }
            int r7 = r7 * r8
            int r7 = r7 / 8
            int r8 = r7 % 4
            byte[] r8 = new byte[r8]     // Catch:{ all -> 0x00cb }
        L_0x00b2:
            int r9 = r11.height     // Catch:{ all -> 0x00cb }
            if (r6 >= r9) goto L_0x00c3
            byte[] r9 = r11.data     // Catch:{ all -> 0x00cb }
            int r10 = r7 * r6
            r4.write(r9, r10, r7)     // Catch:{ all -> 0x00cb }
            r4.write(r8)     // Catch:{ all -> 0x00cb }
            int r6 = r6 + 1
            goto L_0x00b2
        L_0x00c3:
            r4.close()
            java.lang.String r4 = r3.getAbsolutePath()
            return r4
        L_0x00cb:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x00cd }
        L_0x00cd:
            r6 = move-exception
            r4.close()     // Catch:{ all -> 0x00d2 }
            goto L_0x00d6
        L_0x00d2:
            r7 = move-exception
            r5.addSuppressed(r7)
        L_0x00d6:
            goto L_0x00d8
        L_0x00d7:
            throw r6
        L_0x00d8:
            goto L_0x00d7
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.util.C1238GB.writeScreenshot(nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventScreenshot, java.lang.String):java.lang.String");
    }

    public static void toast(String message, int displayTime, int severity) {
        toast(GBApplication.getContext(), message, displayTime, severity, (Throwable) null);
    }

    public static void toast(String message, int displayTime, int severity, Throwable ex) {
        toast(GBApplication.getContext(), message, displayTime, severity, ex);
    }

    public static void toast(Context context, String message, int displayTime, int severity) {
        toast(context, message, displayTime, severity, (Throwable) null);
    }

    public static void toast(final Context context, final String message, final int displayTime, int severity, Throwable ex) {
        log(message, severity, ex);
        if (!GBEnvironment.env().isLocalTest()) {
            Looper mainLooper = Looper.getMainLooper();
            if (Thread.currentThread() == mainLooper.getThread()) {
                Toast.makeText(context, message, displayTime).show();
                return;
            }
            Runnable runnable = new Runnable() {
                public void run() {
                    Toast.makeText(context, message, displayTime).show();
                }
            };
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(runnable);
            } else {
                new Handler(mainLooper).post(runnable);
            }
        }
    }

    public static void log(String message, int severity, Throwable ex) {
        if (severity == 1) {
            LOG.info(message, ex);
        } else if (severity == 2) {
            LOG.warn(message, ex);
        } else if (severity == 3) {
            LOG.error(message, ex);
        }
    }

    private static Notification createTransferNotification(String title, String text, boolean ongoing, int percentage, Context context) {
        Intent notificationIntent = new Intent(context, ControlCenterv2.class);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (GBApplication.isRunningOreoOrLater() && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID_TRANSFER) == null) {
            notificationManager.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_TRANSFER, context.getString(C0889R.string.notification_channel_name), 2));
        }
        notificationIntent.setFlags(268468224);
        boolean z = false;
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_TRANSFER).setTicker(title == null ? context.getString(C0889R.string.app_name) : title).setVisibility(1).setContentTitle(title == null ? context.getString(C0889R.string.app_name) : title).setStyle(new NotificationCompat.BigTextStyle().bigText(text)).setContentText(text).setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, 0)).setOngoing(ongoing);
        if (ongoing) {
            if (percentage == 0) {
                z = true;
            }
            nb.setProgress(100, percentage, z);
            nb.setSmallIcon(17301633);
        } else {
            nb.setProgress(0, 0, false);
            nb.setSmallIcon(17301634);
        }
        return nb.build();
    }

    public static void removeAllNotifications(Context context) {
        removeNotification(4, context);
        removeNotification(2, context);
        removeNotification(3, context);
    }

    public static void updateTransferNotification(String title, String text, boolean ongoing, int percentage, Context context) {
        if (percentage == 100) {
            removeNotification(4, context);
        } else {
            updateNotification(createTransferNotification(title, text, ongoing, percentage, context), 4, context);
        }
    }

    private static Notification createInstallNotification(String text, boolean ongoing, int percentage, Context context) {
        Intent notificationIntent = new Intent(context, ControlCenterv2.class);
        notificationIntent.setFlags(268468224);
        boolean z = false;
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).setContentTitle(context.getString(C0889R.string.app_name)).setContentText(text).setTicker(text).setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, 0)).setOngoing(ongoing);
        if (ongoing) {
            if (percentage == 0) {
                z = true;
            }
            nb.setProgress(100, percentage, z);
            nb.setSmallIcon(17301640);
        } else {
            nb.setSmallIcon(17301641);
        }
        return nb.build();
    }

    public static void updateInstallNotification(String text, boolean ongoing, int percentage, Context context) {
        updateNotification(createInstallNotification(text, ongoing, percentage, context), 2, context);
    }

    private static Notification createBatteryNotification(String text, String bigText, Context context) {
        Intent notificationIntent = new Intent(context, ControlCenterv2.class);
        notificationIntent.setFlags(268468224);
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).setContentTitle(context.getString(C0889R.string.notif_battery_low_title)).setContentText(text).setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, 0)).setSmallIcon(C0889R.C0890drawable.ic_notification_low_battery).setPriority(1).setOngoing(false);
        if (bigText != null) {
            nb.setStyle(new NotificationCompat.BigTextStyle().bigText(bigText));
        }
        return nb.build();
    }

    public static void updateBatteryNotification(String text, String bigText, Context context) {
        if (!GBEnvironment.env().isLocalTest()) {
            updateNotification(createBatteryNotification(text, bigText, context), 3, context);
        }
    }

    public static void removeBatteryNotification(Context context) {
        removeNotification(3, context);
    }

    public static Notification createExportFailedNotification(String text, Context context) {
        Intent notificationIntent = new Intent(context, SettingsActivity.class);
        notificationIntent.setFlags(268468224);
        return new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).setContentTitle(context.getString(C0889R.string.notif_export_failed_title)).setContentText(text).setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, 0)).setSmallIcon(C0889R.C0890drawable.ic_notification).setPriority(1).setOngoing(false).build();
    }

    public static void updateExportFailedNotification(String text, Context context) {
        if (!GBEnvironment.env().isLocalTest()) {
            updateNotification(createExportFailedNotification(text, context), 5, context);
        }
    }

    public static void removeExportFailedNotification(Context context) {
        removeNotification(5, context);
    }

    public static void assertThat(boolean condition, String errorMessage) {
        if (!condition) {
            throw new AssertionError(errorMessage);
        }
    }

    public static void signalActivityDataFinish() {
        LocalBroadcastManager.getInstance(GBApplication.getContext()).sendBroadcast(new Intent(GBApplication.ACTION_NEW_DATA));
    }
}
