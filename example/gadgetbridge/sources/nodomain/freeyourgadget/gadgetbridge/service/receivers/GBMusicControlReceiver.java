package nodomain.freeyourgadget.gadgetbridge.service.receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.view.KeyEvent;
import androidx.core.app.NotificationCompat;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventMusicControl;
import nodomain.freeyourgadget.gadgetbridge.externalevents.NotificationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GBMusicControlReceiver extends BroadcastReceiver {
    public static final String ACTION_MUSICCONTROL = "nodomain.freeyourgadget.gadgetbridge.musiccontrol";
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) GBMusicControlReceiver.class);

    public void onReceive(Context context, Intent intent) {
        Context context2 = context;
        GBDeviceEventMusicControl.Event musicCmd = GBDeviceEventMusicControl.Event.values()[intent.getIntExtra(NotificationCompat.CATEGORY_EVENT, 0)];
        int keyCode = -1;
        int volumeAdjust = -1;
        switch (musicCmd) {
            case NEXT:
                keyCode = 87;
                break;
            case PREVIOUS:
                keyCode = 88;
                break;
            case PLAY:
                keyCode = 126;
                break;
            case PAUSE:
                keyCode = 127;
                break;
            case PLAYPAUSE:
                keyCode = 85;
                break;
            case REWIND:
                keyCode = 89;
                break;
            case FORWARD:
                keyCode = 90;
                break;
            case VOLUMEUP:
                volumeAdjust = 1;
                break;
            case VOLUMEDOWN:
                break;
            default:
                return;
        }
        ((AudioManager) context2.getSystemService("audio")).adjustStreamVolume(3, volumeAdjust, 0);
        if (keyCode != -1) {
            String audioPlayer = getAudioPlayer(context);
            Logger logger = LOG;
            logger.debug("keypress: " + musicCmd.toString() + " sent to: " + audioPlayer);
            long eventtime = SystemClock.uptimeMillis();
            GBDeviceEventMusicControl.Event event = musicCmd;
            String str = "android.intent.action.MEDIA_BUTTON";
            KeyEvent downEvent = new KeyEvent(eventtime, eventtime, 0, keyCode, 0);
            Intent downIntent = new Intent("android.intent.action.MEDIA_BUTTON", (Uri) null);
            downIntent.putExtra("android.intent.extra.KEY_EVENT", downEvent);
            if (!"default".equals(audioPlayer)) {
                downIntent.setPackage(audioPlayer);
            }
            context2.sendOrderedBroadcast(downIntent, (String) null);
            Intent upIntent = new Intent(str, (Uri) null);
            Intent intent2 = downIntent;
            KeyEvent keyEvent = downEvent;
            upIntent.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(eventtime, eventtime, 1, keyCode, 0));
            if (!"default".equals(audioPlayer)) {
                upIntent.setPackage(audioPlayer);
            }
            context2.sendOrderedBroadcast(upIntent, (String) null);
            return;
        }
    }

    private String getAudioPlayer(Context context) {
        String audioPlayer = GBApplication.getPrefs().getString("audio_player", "default");
        if (Build.VERSION.SDK_INT < 21) {
            return audioPlayer;
        }
        try {
            try {
                return ((MediaSessionManager) context.getSystemService("media_session")).getActiveSessions(new ComponentName(context, NotificationListener.class)).get(0).getPackageName();
            } catch (IndexOutOfBoundsException e) {
                LOG.error("No media controller available", (Throwable) e);
                return audioPlayer;
            }
        } catch (SecurityException e2) {
            LOG.warn("No permission to get media sessions - did not grant notification access?", (Throwable) e2);
            return audioPlayer;
        }
    }
}
