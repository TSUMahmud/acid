package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.core.view.ViewCompat;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public abstract class AxisRenderer extends Renderer {
    protected AxisBase mAxis;
    protected Paint mAxisLabelPaint;
    protected Paint mAxisLinePaint;
    protected Paint mGridPaint;
    protected Paint mLimitLinePaint;
    protected Transformer mTrans;

    public abstract void renderAxisLabels(Canvas canvas);

    public abstract void renderAxisLine(Canvas canvas);

    public abstract void renderGridLines(Canvas canvas);

    public abstract void renderLimitLines(Canvas canvas);

    public AxisRenderer(ViewPortHandler viewPortHandler, Transformer trans, AxisBase axis) {
        super(viewPortHandler);
        this.mTrans = trans;
        this.mAxis = axis;
        if (this.mViewPortHandler != null) {
            this.mAxisLabelPaint = new Paint(1);
            this.mGridPaint = new Paint();
            this.mGridPaint.setColor(-7829368);
            this.mGridPaint.setStrokeWidth(1.0f);
            this.mGridPaint.setStyle(Paint.Style.STROKE);
            this.mGridPaint.setAlpha(90);
            this.mAxisLinePaint = new Paint();
            this.mAxisLinePaint.setColor(ViewCompat.MEASURED_STATE_MASK);
            this.mAxisLinePaint.setStrokeWidth(1.0f);
            this.mAxisLinePaint.setStyle(Paint.Style.STROKE);
            this.mLimitLinePaint = new Paint(1);
            this.mLimitLinePaint.setStyle(Paint.Style.STROKE);
        }
    }

    public Paint getPaintAxisLabels() {
        return this.mAxisLabelPaint;
    }

    public Paint getPaintGrid() {
        return this.mGridPaint;
    }

    public Paint getPaintAxisLine() {
        return this.mAxisLinePaint;
    }

    public Transformer getTransformer() {
        return this.mTrans;
    }

    public void computeAxis(float min, float max, boolean inverted) {
        if (this.mViewPortHandler != null && this.mViewPortHandler.contentWidth() > 10.0f && !this.mViewPortHandler.isFullyZoomedOutY()) {
            MPPointD p1 = this.mTrans.getValuesByTouchPoint(this.mViewPortHandler.contentLeft(), this.mViewPortHandler.contentTop());
            MPPointD p2 = this.mTrans.getValuesByTouchPoint(this.mViewPortHandler.contentLeft(), this.mViewPortHandler.contentBottom());
            if (!inverted) {
                min = (float) p2.f89y;
                max = (float) p1.f89y;
            } else {
                min = (float) p1.f89y;
                max = (float) p2.f89y;
            }
            MPPointD.recycleInstance(p1);
            MPPointD.recycleInstance(p2);
        }
        computeAxisValues(min, max);
    }

    /* access modifiers changed from: protected */
    public void computeAxisValues(float min, float max) {
        int n;
        double first;
        double last;
        float yMin = min;
        float yMax = max;
        int labelCount = this.mAxis.getLabelCount();
        double range = (double) Math.abs(yMax - yMin);
        if (labelCount == 0 || range <= Utils.DOUBLE_EPSILON) {
            float f = yMax;
            int i = labelCount;
            double d = range;
        } else if (Double.isInfinite(range)) {
            float f2 = yMin;
            float f3 = yMax;
            int i2 = labelCount;
            double d2 = range;
        } else {
            double d3 = (double) labelCount;
            Double.isNaN(range);
            Double.isNaN(d3);
            double interval = (double) Utils.roundToNextSignificant(range / d3);
            if (this.mAxis.isGranularityEnabled()) {
                interval = interval < ((double) this.mAxis.getGranularity()) ? (double) this.mAxis.getGranularity() : interval;
            }
            double intervalMagnitude = (double) Utils.roundToNextSignificant(Math.pow(10.0d, (double) ((int) Math.log10(interval))));
            Double.isNaN(intervalMagnitude);
            int intervalSigDigit = (int) (interval / intervalMagnitude);
            if (intervalSigDigit > 5) {
                Double.isNaN(intervalMagnitude);
                interval = Math.floor(10.0d * intervalMagnitude);
            }
            int n2 = this.mAxis.isCenterAxisLabelsEnabled();
            if (this.mAxis.isForceLabelsEnabled()) {
                interval = (double) (((float) range) / ((float) (labelCount - 1)));
                AxisBase axisBase = this.mAxis;
                axisBase.mEntryCount = labelCount;
                if (axisBase.mEntries.length < labelCount) {
                    this.mAxis.mEntries = new float[labelCount];
                }
                float v = min;
                int i3 = 0;
                while (i3 < labelCount) {
                    double range2 = range;
                    this.mAxis.mEntries[i3] = v;
                    double d4 = (double) v;
                    Double.isNaN(d4);
                    Double.isNaN(interval);
                    v = (float) (d4 + interval);
                    i3++;
                    range = range2;
                }
                float f4 = yMin;
                float f5 = yMax;
                int i4 = labelCount;
                n = labelCount;
                int i5 = intervalSigDigit;
            } else {
                if (interval == Utils.DOUBLE_EPSILON) {
                    first = Utils.DOUBLE_EPSILON;
                } else {
                    double d5 = (double) yMin;
                    Double.isNaN(d5);
                    first = Math.ceil(d5 / interval) * interval;
                }
                if (this.mAxis.isCenterAxisLabelsEnabled()) {
                    first -= interval;
                }
                if (interval == Utils.DOUBLE_EPSILON) {
                    int i6 = intervalSigDigit;
                    n = n2;
                    last = Utils.DOUBLE_EPSILON;
                } else {
                    int i7 = intervalSigDigit;
                    n = n2;
                    double d6 = (double) yMax;
                    Double.isNaN(d6);
                    last = Utils.nextUp(Math.floor(d6 / interval) * interval);
                }
                if (interval != Utils.DOUBLE_EPSILON) {
                    for (double f6 = first; f6 <= last; f6 += interval) {
                        n++;
                    }
                }
                float f7 = yMin;
                AxisBase axisBase2 = this.mAxis;
                axisBase2.mEntryCount = n;
                if (axisBase2.mEntries.length < n) {
                    float f8 = yMax;
                    this.mAxis.mEntries = new float[n];
                }
                int i8 = 0;
                double f9 = first;
                while (i8 < n) {
                    if (f9 == Utils.DOUBLE_EPSILON) {
                        f9 = Utils.DOUBLE_EPSILON;
                    }
                    double f10 = f9;
                    this.mAxis.mEntries[i8] = (float) f10;
                    f9 = f10 + interval;
                    i8++;
                    labelCount = labelCount;
                    first = first;
                }
                double d7 = first;
            }
            if (interval < 1.0d) {
                this.mAxis.mDecimals = (int) Math.ceil(-Math.log10(interval));
            } else {
                this.mAxis.mDecimals = 0;
            }
            if (this.mAxis.isCenterAxisLabelsEnabled()) {
                if (this.mAxis.mCenteredEntries.length < n) {
                    this.mAxis.mCenteredEntries = new float[n];
                }
                float offset = ((float) interval) / 2.0f;
                for (int i9 = 0; i9 < n; i9++) {
                    this.mAxis.mCenteredEntries[i9] = this.mAxis.mEntries[i9] + offset;
                }
                return;
            }
            return;
        }
        AxisBase axisBase3 = this.mAxis;
        axisBase3.mEntries = new float[0];
        axisBase3.mCenteredEntries = new float[0];
        axisBase3.mEntryCount = 0;
    }
}
