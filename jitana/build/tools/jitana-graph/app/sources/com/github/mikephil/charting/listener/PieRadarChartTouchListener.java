package com.github.mikephil.charting.listener;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import com.github.mikephil.charting.charts.PieRadarChartBase;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import java.util.ArrayList;

public class PieRadarChartTouchListener extends ChartTouchListener<PieRadarChartBase<?>> {
    private ArrayList<AngularVelocitySample> _velocitySamples = new ArrayList<>();
    private float mDecelerationAngularVelocity = 0.0f;
    private long mDecelerationLastTime = 0;
    private float mStartAngle = 0.0f;
    private MPPointF mTouchStartPoint = MPPointF.getInstance(0.0f, 0.0f);

    public PieRadarChartTouchListener(PieRadarChartBase<?> chart) {
        super(chart);
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (!this.mGestureDetector.onTouchEvent(event) && ((PieRadarChartBase) this.mChart).isRotationEnabled()) {
            float x = event.getX();
            float y = event.getY();
            int action = event.getAction();
            if (action == 0) {
                startAction(event);
                stopDeceleration();
                resetVelocity();
                if (((PieRadarChartBase) this.mChart).isDragDecelerationEnabled()) {
                    sampleVelocity(x, y);
                }
                setGestureStartAngle(x, y);
                MPPointF mPPointF = this.mTouchStartPoint;
                mPPointF.f90x = x;
                mPPointF.f91y = y;
            } else if (action == 1) {
                if (((PieRadarChartBase) this.mChart).isDragDecelerationEnabled()) {
                    stopDeceleration();
                    sampleVelocity(x, y);
                    this.mDecelerationAngularVelocity = calculateVelocity();
                    if (this.mDecelerationAngularVelocity != 0.0f) {
                        this.mDecelerationLastTime = AnimationUtils.currentAnimationTimeMillis();
                        Utils.postInvalidateOnAnimation(this.mChart);
                    }
                }
                ((PieRadarChartBase) this.mChart).enableScroll();
                this.mTouchMode = 0;
                endAction(event);
            } else if (action == 2) {
                if (((PieRadarChartBase) this.mChart).isDragDecelerationEnabled()) {
                    sampleVelocity(x, y);
                }
                if (this.mTouchMode == 0 && distance(x, this.mTouchStartPoint.f90x, y, this.mTouchStartPoint.f91y) > Utils.convertDpToPixel(8.0f)) {
                    this.mLastGesture = ChartTouchListener.ChartGesture.ROTATE;
                    this.mTouchMode = 6;
                    ((PieRadarChartBase) this.mChart).disableScroll();
                } else if (this.mTouchMode == 6) {
                    updateGestureRotation(x, y);
                    ((PieRadarChartBase) this.mChart).invalidate();
                }
                endAction(event);
            }
        }
        return true;
    }

    public void onLongPress(MotionEvent me) {
        this.mLastGesture = ChartTouchListener.ChartGesture.LONG_PRESS;
        OnChartGestureListener l = ((PieRadarChartBase) this.mChart).getOnChartGestureListener();
        if (l != null) {
            l.onChartLongPressed(me);
        }
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        return true;
    }

    public boolean onSingleTapUp(MotionEvent e) {
        this.mLastGesture = ChartTouchListener.ChartGesture.SINGLE_TAP;
        OnChartGestureListener l = ((PieRadarChartBase) this.mChart).getOnChartGestureListener();
        if (l != null) {
            l.onChartSingleTapped(e);
        }
        if (!((PieRadarChartBase) this.mChart).isHighlightPerTapEnabled()) {
            return false;
        }
        performHighlight(((PieRadarChartBase) this.mChart).getHighlightByTouchPoint(e.getX(), e.getY()), e);
        return true;
    }

    private void resetVelocity() {
        this._velocitySamples.clear();
    }

    private void sampleVelocity(float touchLocationX, float touchLocationY) {
        long currentTime = AnimationUtils.currentAnimationTimeMillis();
        this._velocitySamples.add(new AngularVelocitySample(currentTime, ((PieRadarChartBase) this.mChart).getAngleForPoint(touchLocationX, touchLocationY)));
        int i = 0;
        int count = this._velocitySamples.size();
        while (i < count - 2 && currentTime - this._velocitySamples.get(i).time > 1000) {
            this._velocitySamples.remove(0);
            count--;
            i = (i - 1) + 1;
        }
    }

    private float calculateVelocity() {
        if (this._velocitySamples.isEmpty()) {
            return 0.0f;
        }
        boolean z = false;
        AngularVelocitySample firstSample = this._velocitySamples.get(0);
        ArrayList<AngularVelocitySample> arrayList = this._velocitySamples;
        AngularVelocitySample lastSample = arrayList.get(arrayList.size() - 1);
        AngularVelocitySample beforeLastSample = firstSample;
        for (int i = this._velocitySamples.size() - 1; i >= 0; i--) {
            beforeLastSample = this._velocitySamples.get(i);
            if (beforeLastSample.angle != lastSample.angle) {
                break;
            }
        }
        float timeDelta = ((float) (lastSample.time - firstSample.time)) / 1000.0f;
        if (timeDelta == 0.0f) {
            timeDelta = 0.1f;
        }
        boolean clockwise = lastSample.angle >= beforeLastSample.angle;
        if (((double) Math.abs(lastSample.angle - beforeLastSample.angle)) > 270.0d) {
            if (!clockwise) {
                z = true;
            }
            clockwise = z;
        }
        if (((double) (lastSample.angle - firstSample.angle)) > 180.0d) {
            double d = (double) firstSample.angle;
            Double.isNaN(d);
            firstSample.angle = (float) (d + 360.0d);
        } else if (((double) (firstSample.angle - lastSample.angle)) > 180.0d) {
            double d2 = (double) lastSample.angle;
            Double.isNaN(d2);
            lastSample.angle = (float) (d2 + 360.0d);
        }
        float velocity = Math.abs((lastSample.angle - firstSample.angle) / timeDelta);
        if (!clockwise) {
            return -velocity;
        }
        return velocity;
    }

    public void setGestureStartAngle(float x, float y) {
        this.mStartAngle = ((PieRadarChartBase) this.mChart).getAngleForPoint(x, y) - ((PieRadarChartBase) this.mChart).getRawRotationAngle();
    }

    public void updateGestureRotation(float x, float y) {
        ((PieRadarChartBase) this.mChart).setRotationAngle(((PieRadarChartBase) this.mChart).getAngleForPoint(x, y) - this.mStartAngle);
    }

    public void stopDeceleration() {
        this.mDecelerationAngularVelocity = 0.0f;
    }

    public void computeScroll() {
        if (this.mDecelerationAngularVelocity != 0.0f) {
            long currentTime = AnimationUtils.currentAnimationTimeMillis();
            this.mDecelerationAngularVelocity *= ((PieRadarChartBase) this.mChart).getDragDecelerationFrictionCoef();
            ((PieRadarChartBase) this.mChart).setRotationAngle(((PieRadarChartBase) this.mChart).getRotationAngle() + (this.mDecelerationAngularVelocity * (((float) (currentTime - this.mDecelerationLastTime)) / 1000.0f)));
            this.mDecelerationLastTime = currentTime;
            if (((double) Math.abs(this.mDecelerationAngularVelocity)) >= 0.001d) {
                Utils.postInvalidateOnAnimation(this.mChart);
            } else {
                stopDeceleration();
            }
        }
    }

    private class AngularVelocitySample {
        public float angle;
        public long time;

        public AngularVelocitySample(long time2, float angle2) {
            this.time = time2;
            this.angle = angle2;
        }
    }
}
