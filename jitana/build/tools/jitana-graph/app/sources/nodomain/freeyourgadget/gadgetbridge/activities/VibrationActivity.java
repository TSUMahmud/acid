package nodomain.freeyourgadget.gadgetbridge.activities;

import android.os.Bundle;
import android.widget.SeekBar;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VibrationActivity extends AbstractGBActivity {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) VibrationActivity.class);
    private SeekBar seekBar;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_vibration);
        this.seekBar = (SeekBar) findViewById(C0889R.C0891id.vibration_seekbar);
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 0) {
                    progress = (progress * 16) - 1;
                }
                GBApplication.deviceService().onSetConstantVibration(progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}
