package nodomain.freeyourgadget.gadgetbridge.devices.qhybrid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import androidx.core.view.ViewCompat;
import com.github.mikephil.charting.utils.Utils;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.misfit.PlayNotificationRequest;

public class TimePicker extends AlertDialog.Builder {
    int controlledHand = 0;
    AlertDialog dialog;
    OnFinishListener finishListener;
    int handRadius;
    OnHandsSetListener handsListener;
    int height;
    Bitmap pickerBitmap;
    Canvas pickerCanvas;
    ImageView pickerView;
    int radius;
    int radius1;
    int radius2;
    int radius3;
    NotificationConfiguration settings;
    OnVibrationSetListener vibrationListener;
    int width;

    interface OnFinishListener {
        void onFinish(boolean z, NotificationConfiguration notificationConfiguration);
    }

    interface OnHandsSetListener {
        void onHandsSet(NotificationConfiguration notificationConfiguration);
    }

    interface OnVibrationSetListener {
        void onVibrationSet(NotificationConfiguration notificationConfiguration);
    }

    protected TimePicker(Context context, PackageInfo info) {
        super(context);
        this.settings = new NotificationConfiguration(info.packageName, context.getApplicationContext().getPackageManager().getApplicationLabel(info.applicationInfo).toString());
        initGraphics(context);
    }

    protected TimePicker(Context context, NotificationConfiguration config) {
        super(context);
        this.settings = config;
        initGraphics(context);
    }

    private void initGraphics(Context context) {
        double width2 = (double) ((WindowManager) context.getApplicationContext().getSystemService("window")).getDefaultDisplay().getWidth();
        Double.isNaN(width2);
        int w = (int) (width2 * 0.8d);
        this.height = w;
        this.width = w;
        double d = (double) w;
        Double.isNaN(d);
        this.radius = (int) (d * 0.06d);
        this.radius1 = 0;
        double d2 = (double) this.radius;
        Double.isNaN(d2);
        this.radius2 = (int) (d2 * 2.3d);
        int i = this.radius2;
        double d3 = (double) i;
        Double.isNaN(d3);
        this.radius3 = (int) (d3 * 2.15d);
        double d4 = (double) w;
        Double.isNaN(d4);
        int offset = (int) (d4 * 0.1d);
        this.radius1 += offset;
        this.radius2 = i + offset;
        this.radius3 += offset;
        this.pickerView = new ImageView(context);
        this.pickerBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
        this.pickerCanvas = new Canvas(this.pickerBitmap);
        drawClock();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(1);
        layout.addView(this.pickerView);
        CheckBox box = new CheckBox(context);
        box.setText("Respect silent mode");
        box.setChecked(this.settings.getRespectSilentMode());
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                TimePicker.this.settings.setRespectSilentMode(b);
            }
        });
        layout.addView(box);
        RadioGroup group = new RadioGroup(context);
        for (PlayNotificationRequest.VibrationType vibe : PlayNotificationRequest.VibrationType.values()) {
            RadioButton button = new RadioButton(context);
            button.setText(vibe.toString());
            button.setId(vibe.getValue());
            group.addView(button);
        }
        group.check(this.settings.getVibration().getValue());
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                TimePicker.this.settings.setVibration(PlayNotificationRequest.VibrationType.fromValue((byte) i));
                if (TimePicker.this.vibrationListener != null) {
                    TimePicker.this.vibrationListener.onVibrationSet(TimePicker.this.settings);
                }
            }
        });
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(group);
        layout.addView(scrollView);
        setView(layout);
        setNegativeButton("cancel", (DialogInterface.OnClickListener) null);
        setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TimePicker.this.finishListener != null) {
                    TimePicker.this.finishListener.onFinish(true, TimePicker.this.settings);
                }
            }
        });
        setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialogInterface) {
                if (TimePicker.this.finishListener != null) {
                    TimePicker.this.finishListener.onFinish(false, TimePicker.this.settings);
                }
            }
        });
        setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                if (TimePicker.this.finishListener != null) {
                    TimePicker.this.finishListener.onFinish(false, TimePicker.this.settings);
                }
            }
        });
        this.dialog = show();
        this.pickerView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TimePicker timePicker = TimePicker.this;
                timePicker.handleTouch(timePicker.dialog, motionEvent);
                return true;
            }
        });
    }

    public NotificationConfiguration getSettings() {
        return this.settings;
    }

    /* access modifiers changed from: private */
    public void handleTouch(AlertDialog dialog2, MotionEvent event) {
        double degree;
        int difX = (this.width / 2) - ((int) event.getX());
        int difY = ((int) event.getY()) - (this.height / 2);
        int dist = (int) Math.sqrt((double) ((Math.abs(difX) * Math.abs(difX)) + (Math.abs(difY) * Math.abs(difY))));
        int action = event.getAction();
        if (action == 0) {
            int radiusHalf = this.radius;
            int i = this.radius1;
            if (dist >= i + radiusHalf || dist <= i - radiusHalf) {
                int i2 = this.radius2;
                if (dist >= i2 + radiusHalf || dist <= i2 - radiusHalf) {
                    int i3 = this.radius3;
                    if (dist >= i3 + radiusHalf || dist <= i3 - radiusHalf) {
                        Log.d("Settings", "hit nothing");
                    } else {
                        Log.d("Settings", "hit minute");
                        this.controlledHand = 2;
                        this.handRadius = (int) ((((float) this.height) / 2.0f) - ((float) this.radius3));
                    }
                } else {
                    Log.d("Settings", "hit hour");
                    this.controlledHand = 1;
                    this.handRadius = (int) ((((float) this.height) / 2.0f) - ((float) this.radius2));
                }
            } else {
                Log.d("Settings", "hit sub");
                this.handRadius = (int) ((((float) this.height) / 2.0f) - ((float) this.radius1));
                this.controlledHand = 3;
            }
        } else if (action == 1) {
            dialog2.getButton(-1).setClickable(true);
            dialog2.getButton(-1).setAlpha(1.0f);
            OnHandsSetListener onHandsSetListener = this.handsListener;
            if (onHandsSetListener != null) {
                onHandsSetListener.onHandsSet(this.settings);
            }
        } else if (action == 2) {
            if (this.controlledHand != 0) {
                if (difY == 0) {
                    degree = (double) (difX < 0 ? 90 : 270);
                } else {
                    degree = Math.toDegrees(Math.atan((double) (((float) difX) / ((float) difY))));
                }
                if (difY > 0) {
                    degree += 180.0d;
                }
                if (degree < Utils.DOUBLE_EPSILON) {
                    degree += 360.0d;
                }
                int i4 = this.controlledHand;
                if (i4 == 1) {
                    this.settings.setHour((short) (((((int) (15.0d + degree)) / 30) * 30) % 360));
                } else if (i4 == 2) {
                    this.settings.setMin((short) (((((int) (15.0d + degree)) / 30) * 30) % 360));
                }
            } else {
                return;
            }
        }
        drawClock();
    }

    private void drawClock() {
        int textShiftY;
        Paint white = new Paint();
        white.setColor(-1);
        white.setStyle(Paint.Style.FILL);
        Canvas canvas = this.pickerCanvas;
        int i = this.width;
        canvas.drawCircle((float) (i / 2), (float) (i / 2), (float) (i / 2), white);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(-16776961);
        Paint text = new Paint();
        text.setStyle(Paint.Style.FILL);
        text.setTextSize(((float) this.radius) * 1.5f);
        text.setColor(ViewCompat.MEASURED_STATE_MASK);
        text.setTextAlign(Paint.Align.CENTER);
        int textShiftY2 = (int) ((text.descent() + text.ascent()) / 2.0f);
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(10.0f);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        if (this.settings.getMin() != -1) {
            paint.setAlpha(255);
            double d = (double) (((float) this.width) / 2.0f);
            double sin = Math.sin(Math.toRadians((double) this.settings.getMin()));
            double d2 = (double) ((float) this.radius3);
            Double.isNaN(d2);
            Double.isNaN(d);
            float x = (float) (d + (sin * d2));
            double d3 = (double) (((float) this.height) / 2.0f);
            double cos = Math.cos(Math.toRadians((double) this.settings.getMin()));
            int textShiftY3 = textShiftY2;
            double d4 = (double) ((float) this.radius3);
            Double.isNaN(d4);
            Double.isNaN(d3);
            float y = (float) (d3 - (cos * d4));
            linePaint.setAlpha(255);
            float x2 = x;
            this.pickerCanvas.drawLine((float) (this.width / 2), (float) (this.height / 2), x, y, linePaint);
            this.pickerCanvas.drawCircle(x2, y, (float) this.radius, paint);
            textShiftY = textShiftY3;
            this.pickerCanvas.drawText(String.valueOf(this.settings.getMin() / 6), x2, y - ((float) textShiftY), text);
        } else {
            textShiftY = textShiftY2;
        }
        if (this.settings.getHour() != -1) {
            paint.setAlpha(255);
            double d5 = (double) (((float) this.width) / 2.0f);
            double sin2 = Math.sin(Math.toRadians((double) this.settings.getHour()));
            double d6 = (double) ((float) this.radius2);
            Double.isNaN(d6);
            Double.isNaN(d5);
            float x3 = (float) (d5 + (sin2 * d6));
            double d7 = (double) (((float) this.height) / 2.0f);
            double cos2 = Math.cos(Math.toRadians((double) this.settings.getHour()));
            double d8 = (double) ((float) this.radius2);
            Double.isNaN(d8);
            Double.isNaN(d7);
            float y2 = (float) (d7 - (cos2 * d8));
            linePaint.setAlpha(255);
            int textShiftY4 = textShiftY;
            this.pickerCanvas.drawLine((float) (this.width / 2), (float) (this.height / 2), x3, y2, linePaint);
            this.pickerCanvas.drawCircle(x3, y2, (float) this.radius, paint);
            this.pickerCanvas.drawText(this.settings.getHour() == 0 ? "12" : String.valueOf(this.settings.getHour() / 30), x3, y2 - ((float) textShiftY4), text);
        }
        Paint paint2 = new Paint();
        paint2.setColor(ViewCompat.MEASURED_STATE_MASK);
        paint2.setStyle(Paint.Style.FILL_AND_STROKE);
        this.pickerCanvas.drawCircle((float) (this.width / 2), (float) (this.height / 2), 5.0f, paint2);
        this.pickerView.setImageBitmap(this.pickerBitmap);
    }
}
