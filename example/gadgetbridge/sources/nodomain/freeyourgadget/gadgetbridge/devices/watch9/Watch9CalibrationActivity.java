package nodomain.freeyourgadget.gadgetbridge.devices.watch9;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractGBActivity;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import p005ch.qos.logback.core.spi.AbstractComponentTracker;

public class Watch9CalibrationActivity extends AbstractGBActivity {
    private static final String STATE_DEVICE = "stateDevice";
    GBDevice device;
    Handler handler;
    Runnable holdCalibration;
    NumberPicker pickerHour;
    NumberPicker pickerMinute;
    NumberPicker pickerSecond;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_watch9_calibration);
        this.pickerHour = (NumberPicker) findViewById(C0889R.C0891id.np_hour);
        this.pickerMinute = (NumberPicker) findViewById(C0889R.C0891id.np_minute);
        this.pickerSecond = (NumberPicker) findViewById(C0889R.C0891id.np_second);
        this.pickerHour.setMinValue(1);
        this.pickerHour.setMaxValue(12);
        this.pickerHour.setValue(12);
        this.pickerMinute.setMinValue(0);
        this.pickerMinute.setMaxValue(59);
        this.pickerMinute.setValue(0);
        this.pickerSecond.setMinValue(0);
        this.pickerSecond.setMaxValue(59);
        this.pickerSecond.setValue(0);
        this.handler = new Handler();
        this.holdCalibration = new Runnable() {
            public void run() {
                LocalBroadcastManager.getInstance(Watch9CalibrationActivity.this.getApplicationContext()).sendBroadcast(new Intent(Watch9Constants.ACTION_CALIBRATION_HOLD));
                Watch9CalibrationActivity.this.handler.postDelayed(this, AbstractComponentTracker.LINGERING_TIMEOUT);
            }
        };
        this.device = (GBDevice) getIntent().getParcelableExtra(GBDevice.EXTRA_DEVICE);
        if (this.device == null && savedInstanceState != null) {
            this.device = (GBDevice) savedInstanceState.getParcelable(STATE_DEVICE);
        }
        if (this.device == null) {
            finish();
        }
        final Button btCalibrate = (Button) findViewById(C0889R.C0891id.watch9_bt_calibrate);
        btCalibrate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btCalibrate.setEnabled(false);
                Watch9CalibrationActivity.this.handler.removeCallbacks(Watch9CalibrationActivity.this.holdCalibration);
                Intent calibrationData = new Intent(Watch9Constants.ACTION_CALIBRATION_SEND);
                calibrationData.putExtra(Watch9Constants.VALUE_CALIBRATION_HOUR, Watch9CalibrationActivity.this.pickerHour.getValue());
                calibrationData.putExtra(Watch9Constants.VALUE_CALIBRATION_MINUTE, Watch9CalibrationActivity.this.pickerMinute.getValue());
                calibrationData.putExtra(Watch9Constants.VALUE_CALIBRATION_SECOND, Watch9CalibrationActivity.this.pickerSecond.getValue());
                LocalBroadcastManager.getInstance(Watch9CalibrationActivity.this.getApplicationContext()).sendBroadcast(calibrationData);
                Watch9CalibrationActivity.this.finish();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_DEVICE, this.device);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.device = (GBDevice) savedInstanceState.getParcelable(STATE_DEVICE);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        Intent calibration = new Intent(Watch9Constants.ACTION_CALIBRATION);
        calibration.putExtra(Watch9Constants.ACTION_ENABLE, true);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(calibration);
        this.handler.postDelayed(this.holdCalibration, 1000);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        Intent calibration = new Intent(Watch9Constants.ACTION_CALIBRATION);
        calibration.putExtra(Watch9Constants.ACTION_ENABLE, false);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(calibration);
        this.handler.removeCallbacks(this.holdCalibration);
    }
}
