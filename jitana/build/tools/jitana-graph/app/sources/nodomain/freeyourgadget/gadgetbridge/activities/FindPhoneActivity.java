package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.io.IOException;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindPhoneActivity extends AbstractGBActivity {
    public static final String ACTION_FOUND = "nodomain.freeyourgadget.gadgetbridge.findphone.action.reply";
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) FindPhoneActivity.class);
    AudioManager mAudioManager;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                char c = 65535;
                if (action.hashCode() == 1603572330 && action.equals(FindPhoneActivity.ACTION_FOUND)) {
                    c = 0;
                }
                if (c == 0) {
                    FindPhoneActivity.this.finish();
                }
            }
        }
    };
    Vibrator mVibrator;

    /* renamed from: mp */
    MediaPlayer f121mp;
    int userVolume;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_find_phone);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FOUND);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, filter);
        registerReceiver(this.mReceiver, filter);
        ((Button) findViewById(C0889R.C0891id.foundbutton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FindPhoneActivity.this.finish();
            }
        });
        vibrate();
        playRingtone();
    }

    private void vibrate() {
        this.mVibrator = (Vibrator) getSystemService("vibrator");
        long[] vibrationPattern = {1000, 1000};
        if (Build.VERSION.SDK_INT >= 26) {
            this.mVibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, 0));
            return;
        }
        this.mVibrator.vibrate(vibrationPattern, 0);
    }

    private void playRingtone() {
        this.mAudioManager = (AudioManager) getSystemService("audio");
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            this.userVolume = audioManager.getStreamVolume(4);
        }
        this.f121mp = new MediaPlayer();
        try {
            this.f121mp.setDataSource(this, RingtoneManager.getDefaultUri(1));
            this.f121mp.setAudioStreamType(4);
            this.f121mp.setLooping(true);
            this.f121mp.prepare();
            this.f121mp.start();
        } catch (IOException e) {
            LOG.warn("problem playing ringtone");
        }
        AudioManager audioManager2 = this.mAudioManager;
        if (audioManager2 != null) {
            this.userVolume = audioManager2.getStreamVolume(4);
            AudioManager audioManager3 = this.mAudioManager;
            audioManager3.setStreamVolume(4, audioManager3.getStreamMaxVolume(4), 4);
        }
    }

    private void stopVibration() {
        this.mVibrator.cancel();
    }

    private void stopSound() {
        this.mAudioManager.setStreamVolume(4, this.userVolume, 4);
        this.f121mp.stop();
        this.f121mp.reset();
        this.f121mp.release();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        stopVibration();
        stopSound();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mReceiver);
        unregisterReceiver(this.mReceiver);
    }
}
