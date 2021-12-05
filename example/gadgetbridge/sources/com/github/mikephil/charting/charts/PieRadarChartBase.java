package com.github.mikephil.charting.charts;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.PieRadarChartTouchListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

public abstract class PieRadarChartBase<T extends ChartData<? extends IDataSet<? extends Entry>>> extends Chart<T> {
    protected float mMinOffset = 0.0f;
    private float mRawRotationAngle = 270.0f;
    protected boolean mRotateEnabled = true;
    private float mRotationAngle = 270.0f;

    public abstract int getIndexForAngle(float f);

    public abstract float getRadius();

    /* access modifiers changed from: protected */
    public abstract float getRequiredBaseOffset();

    /* access modifiers changed from: protected */
    public abstract float getRequiredLegendOffset();

    public PieRadarChartBase(Context context) {
        super(context);
    }

    public PieRadarChartBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PieRadarChartBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* access modifiers changed from: protected */
    public void init() {
        super.init();
        this.mChartTouchListener = new PieRadarChartTouchListener(this);
    }

    /* access modifiers changed from: protected */
    public void calcMinMax() {
    }

    public int getMaxVisibleCount() {
        return this.mData.getEntryCount();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mTouchEnabled || this.mChartTouchListener == null) {
            return super.onTouchEvent(event);
        }
        return this.mChartTouchListener.onTouch(this, event);
    }

    public void computeScroll() {
        if (this.mChartTouchListener instanceof PieRadarChartTouchListener) {
            ((PieRadarChartTouchListener) this.mChartTouchListener).computeScroll();
        }
    }

    public void notifyDataSetChanged() {
        if (this.mData != null) {
            calcMinMax();
            if (this.mLegend != null) {
                this.mLegendRenderer.computeLegend(this.mData);
            }
            calculateOffsets();
        }
    }

    public void calculateOffsets() {
        float legendBottom;
        float legendRight;
        float legendLeft;
        float legendBottom2;
        float legendRight2;
        float legendLeft2;
        float legendLeft3 = 0.0f;
        float legendRight3 = 0.0f;
        float legendBottom3 = 0.0f;
        float legendTop = 0.0f;
        if (this.mLegend == null || !this.mLegend.isEnabled() || this.mLegend.isDrawInsideEnabled()) {
            legendLeft = 0.0f;
            legendRight = 0.0f;
            legendBottom = 0.0f;
        } else {
            float fullLegendWidth = Math.min(this.mLegend.mNeededWidth, this.mViewPortHandler.getChartWidth() * this.mLegend.getMaxSizePercent());
            int i = C05722.f67x9c9dbef[this.mLegend.getOrientation().ordinal()];
            if (i == 1) {
                float xLegendOffset = 0.0f;
                if (this.mLegend.getHorizontalAlignment() != Legend.LegendHorizontalAlignment.LEFT && this.mLegend.getHorizontalAlignment() != Legend.LegendHorizontalAlignment.RIGHT) {
                    legendLeft2 = 0.0f;
                    legendRight2 = 0.0f;
                    legendBottom2 = 0.0f;
                } else if (this.mLegend.getVerticalAlignment() == Legend.LegendVerticalAlignment.CENTER) {
                    xLegendOffset = fullLegendWidth + Utils.convertDpToPixel(13.0f);
                    legendLeft2 = 0.0f;
                    legendRight2 = 0.0f;
                    legendBottom2 = 0.0f;
                } else {
                    float legendWidth = fullLegendWidth + Utils.convertDpToPixel(8.0f);
                    float legendHeight = this.mLegend.mNeededHeight + this.mLegend.mTextHeightMax;
                    MPPointF center = getCenter();
                    float bottomX = this.mLegend.getHorizontalAlignment() == Legend.LegendHorizontalAlignment.RIGHT ? (((float) getWidth()) - legendWidth) + 15.0f : legendWidth - 15.0f;
                    float bottomY = 15.0f + legendHeight;
                    float distLegend = distanceToCenter(bottomX, bottomY);
                    MPPointF reference = getPosition(center, getRadius(), getAngleForPoint(bottomX, bottomY));
                    legendLeft2 = 0.0f;
                    float distReference = distanceToCenter(reference.f90x, reference.f91y);
                    float minOffset = Utils.convertDpToPixel(5.0f);
                    legendRight2 = 0.0f;
                    if (bottomY >= center.f91y) {
                        legendBottom2 = 0.0f;
                        if (((float) getHeight()) - legendWidth > ((float) getWidth())) {
                            xLegendOffset = legendWidth;
                            MPPointF.recycleInstance(center);
                            MPPointF.recycleInstance(reference);
                        }
                    } else {
                        legendBottom2 = 0.0f;
                    }
                    if (distLegend < distReference) {
                        xLegendOffset = minOffset + (distReference - distLegend);
                    }
                    MPPointF.recycleInstance(center);
                    MPPointF.recycleInstance(reference);
                }
                int i2 = C05722.f66x2787f53e[this.mLegend.getHorizontalAlignment().ordinal()];
                if (i2 == 1) {
                    legendLeft3 = xLegendOffset;
                    legendRight3 = legendRight2;
                    legendBottom3 = legendBottom2;
                } else if (i2 != 2) {
                    if (i2 == 3) {
                        int i3 = C05722.f68xc926f1ec[this.mLegend.getVerticalAlignment().ordinal()];
                        if (i3 == 1) {
                            legendTop = Math.min(this.mLegend.mNeededHeight, this.mViewPortHandler.getChartHeight() * this.mLegend.getMaxSizePercent());
                            legendLeft3 = legendLeft2;
                            legendRight3 = legendRight2;
                            legendBottom3 = legendBottom2;
                        } else if (i3 == 2) {
                            legendBottom3 = Math.min(this.mLegend.mNeededHeight, this.mViewPortHandler.getChartHeight() * this.mLegend.getMaxSizePercent());
                            legendLeft3 = legendLeft2;
                            legendRight3 = legendRight2;
                        }
                    }
                    legendLeft3 = legendLeft2;
                    legendRight3 = legendRight2;
                    legendBottom3 = legendBottom2;
                } else {
                    legendRight3 = xLegendOffset;
                    legendLeft3 = legendLeft2;
                    legendBottom3 = legendBottom2;
                }
            } else if (i == 2 && (this.mLegend.getVerticalAlignment() == Legend.LegendVerticalAlignment.TOP || this.mLegend.getVerticalAlignment() == Legend.LegendVerticalAlignment.BOTTOM)) {
                float yLegendOffset = Math.min(this.mLegend.mNeededHeight + getRequiredLegendOffset(), this.mViewPortHandler.getChartHeight() * this.mLegend.getMaxSizePercent());
                int i4 = C05722.f68xc926f1ec[this.mLegend.getVerticalAlignment().ordinal()];
                if (i4 == 1) {
                    legendTop = yLegendOffset;
                } else if (i4 == 2) {
                    legendBottom3 = yLegendOffset;
                }
            }
            legendLeft = legendLeft3 + getRequiredBaseOffset();
            legendRight = legendRight3 + getRequiredBaseOffset();
            legendTop += getRequiredBaseOffset();
            legendBottom = legendBottom3 + getRequiredBaseOffset();
        }
        float minOffset2 = Utils.convertDpToPixel(this.mMinOffset);
        if (this instanceof RadarChart) {
            XAxis x = getXAxis();
            if (x.isEnabled() && x.isDrawLabelsEnabled()) {
                minOffset2 = Math.max(minOffset2, (float) x.mLabelRotatedWidth);
            }
        }
        float legendTop2 = legendTop + getExtraTopOffset();
        float legendRight4 = legendRight + getExtraRightOffset();
        float legendBottom4 = legendBottom + getExtraBottomOffset();
        float offsetLeft = Math.max(minOffset2, legendLeft + getExtraLeftOffset());
        float offsetTop = Math.max(minOffset2, legendTop2);
        float offsetRight = Math.max(minOffset2, legendRight4);
        float offsetBottom = Math.max(minOffset2, Math.max(getRequiredBaseOffset(), legendBottom4));
        this.mViewPortHandler.restrainViewPort(offsetLeft, offsetTop, offsetRight, offsetBottom);
        if (this.mLogEnabled) {
            Log.i(Chart.LOG_TAG, "offsetLeft: " + offsetLeft + ", offsetTop: " + offsetTop + ", offsetRight: " + offsetRight + ", offsetBottom: " + offsetBottom);
        }
    }

    /* renamed from: com.github.mikephil.charting.charts.PieRadarChartBase$2 */
    static /* synthetic */ class C05722 {

        /* renamed from: $SwitchMap$com$github$mikephil$charting$components$Legend$LegendHorizontalAlignment */
        static final /* synthetic */ int[] f66x2787f53e = new int[Legend.LegendHorizontalAlignment.values().length];

        /* renamed from: $SwitchMap$com$github$mikephil$charting$components$Legend$LegendOrientation */
        static final /* synthetic */ int[] f67x9c9dbef = new int[Legend.LegendOrientation.values().length];

        /* renamed from: $SwitchMap$com$github$mikephil$charting$components$Legend$LegendVerticalAlignment */
        static final /* synthetic */ int[] f68xc926f1ec = new int[Legend.LegendVerticalAlignment.values().length];

        static {
            try {
                f67x9c9dbef[Legend.LegendOrientation.VERTICAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f67x9c9dbef[Legend.LegendOrientation.HORIZONTAL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f66x2787f53e[Legend.LegendHorizontalAlignment.LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f66x2787f53e[Legend.LegendHorizontalAlignment.RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f66x2787f53e[Legend.LegendHorizontalAlignment.CENTER.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f68xc926f1ec[Legend.LegendVerticalAlignment.TOP.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f68xc926f1ec[Legend.LegendVerticalAlignment.BOTTOM.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public float getAngleForPoint(float x, float y) {
        MPPointF c = getCenterOffsets();
        double tx = (double) (x - c.f90x);
        double ty = (double) (y - c.f91y);
        Double.isNaN(tx);
        Double.isNaN(tx);
        Double.isNaN(ty);
        Double.isNaN(ty);
        double length = Math.sqrt((tx * tx) + (ty * ty));
        Double.isNaN(ty);
        float angle = (float) Math.toDegrees(Math.acos(ty / length));
        if (x > c.f90x) {
            angle = 360.0f - angle;
        }
        float angle2 = angle + 90.0f;
        if (angle2 > 360.0f) {
            angle2 -= 360.0f;
        }
        MPPointF.recycleInstance(c);
        return angle2;
    }

    public MPPointF getPosition(MPPointF center, float dist, float angle) {
        MPPointF p = MPPointF.getInstance(0.0f, 0.0f);
        getPosition(center, dist, angle, p);
        return p;
    }

    public void getPosition(MPPointF center, float dist, float angle, MPPointF outputPoint) {
        double d = (double) center.f90x;
        double d2 = (double) dist;
        double cos = Math.cos(Math.toRadians((double) angle));
        Double.isNaN(d2);
        Double.isNaN(d);
        outputPoint.f90x = (float) (d + (d2 * cos));
        double d3 = (double) center.f91y;
        double d4 = (double) dist;
        double sin = Math.sin(Math.toRadians((double) angle));
        Double.isNaN(d4);
        Double.isNaN(d3);
        outputPoint.f91y = (float) (d3 + (d4 * sin));
    }

    public float distanceToCenter(float x, float y) {
        float xDist;
        float yDist;
        MPPointF c = getCenterOffsets();
        if (x > c.f90x) {
            xDist = x - c.f90x;
        } else {
            xDist = c.f90x - x;
        }
        if (y > c.f91y) {
            yDist = y - c.f91y;
        } else {
            yDist = c.f91y - y;
        }
        float dist = (float) Math.sqrt(Math.pow((double) xDist, 2.0d) + Math.pow((double) yDist, 2.0d));
        MPPointF.recycleInstance(c);
        return dist;
    }

    public void setRotationAngle(float angle) {
        this.mRawRotationAngle = angle;
        this.mRotationAngle = Utils.getNormalizedAngle(this.mRawRotationAngle);
    }

    public float getRawRotationAngle() {
        return this.mRawRotationAngle;
    }

    public float getRotationAngle() {
        return this.mRotationAngle;
    }

    public void setRotationEnabled(boolean enabled) {
        this.mRotateEnabled = enabled;
    }

    public boolean isRotationEnabled() {
        return this.mRotateEnabled;
    }

    public float getMinOffset() {
        return this.mMinOffset;
    }

    public void setMinOffset(float minOffset) {
        this.mMinOffset = minOffset;
    }

    public float getDiameter() {
        RectF content = this.mViewPortHandler.getContentRect();
        content.left += getExtraLeftOffset();
        content.top += getExtraTopOffset();
        content.right -= getExtraRightOffset();
        content.bottom -= getExtraBottomOffset();
        return Math.min(content.width(), content.height());
    }

    public float getYChartMax() {
        return 0.0f;
    }

    public float getYChartMin() {
        return 0.0f;
    }

    public void spin(int durationmillis, float fromangle, float toangle, Easing.EasingFunction easing) {
        setRotationAngle(fromangle);
        ObjectAnimator spinAnimator = ObjectAnimator.ofFloat(this, "rotationAngle", new float[]{fromangle, toangle});
        spinAnimator.setDuration((long) durationmillis);
        spinAnimator.setInterpolator(easing);
        spinAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                PieRadarChartBase.this.postInvalidate();
            }
        });
        spinAnimator.start();
    }
}
