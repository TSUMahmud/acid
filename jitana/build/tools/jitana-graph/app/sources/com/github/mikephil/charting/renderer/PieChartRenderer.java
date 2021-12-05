package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import androidx.core.view.ViewCompat;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.lang.ref.WeakReference;

public class PieChartRenderer extends DataRenderer {
    protected Canvas mBitmapCanvas;
    private RectF mCenterTextLastBounds = new RectF();
    private CharSequence mCenterTextLastValue;
    private StaticLayout mCenterTextLayout;
    private TextPaint mCenterTextPaint;
    protected PieChart mChart;
    protected WeakReference<Bitmap> mDrawBitmap;
    protected Path mDrawCenterTextPathBuffer = new Path();
    protected RectF mDrawHighlightedRectF = new RectF();
    private Paint mEntryLabelsPaint;
    private Path mHoleCirclePath = new Path();
    protected Paint mHolePaint;
    private RectF mInnerRectBuffer = new RectF();
    private Path mPathBuffer = new Path();
    private RectF[] mRectBuffer = {new RectF(), new RectF(), new RectF()};
    protected Paint mTransparentCirclePaint;
    protected Paint mValueLinePaint;

    public PieChartRenderer(PieChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
        this.mHolePaint = new Paint(1);
        this.mHolePaint.setColor(-1);
        this.mHolePaint.setStyle(Paint.Style.FILL);
        this.mTransparentCirclePaint = new Paint(1);
        this.mTransparentCirclePaint.setColor(-1);
        this.mTransparentCirclePaint.setStyle(Paint.Style.FILL);
        this.mTransparentCirclePaint.setAlpha(105);
        this.mCenterTextPaint = new TextPaint(1);
        this.mCenterTextPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mCenterTextPaint.setTextSize(Utils.convertDpToPixel(12.0f));
        this.mValuePaint.setTextSize(Utils.convertDpToPixel(13.0f));
        this.mValuePaint.setColor(-1);
        this.mValuePaint.setTextAlign(Paint.Align.CENTER);
        this.mEntryLabelsPaint = new Paint(1);
        this.mEntryLabelsPaint.setColor(-1);
        this.mEntryLabelsPaint.setTextAlign(Paint.Align.CENTER);
        this.mEntryLabelsPaint.setTextSize(Utils.convertDpToPixel(13.0f));
        this.mValueLinePaint = new Paint(1);
        this.mValueLinePaint.setStyle(Paint.Style.STROKE);
    }

    public Paint getPaintHole() {
        return this.mHolePaint;
    }

    public Paint getPaintTransparentCircle() {
        return this.mTransparentCirclePaint;
    }

    public TextPaint getPaintCenterText() {
        return this.mCenterTextPaint;
    }

    public Paint getPaintEntryLabels() {
        return this.mEntryLabelsPaint;
    }

    public void initBuffers() {
    }

    public void drawData(Canvas c) {
        int width = (int) this.mViewPortHandler.getChartWidth();
        int height = (int) this.mViewPortHandler.getChartHeight();
        WeakReference<Bitmap> weakReference = this.mDrawBitmap;
        Bitmap drawBitmap = weakReference == null ? null : (Bitmap) weakReference.get();
        if (!(drawBitmap != null && drawBitmap.getWidth() == width && drawBitmap.getHeight() == height)) {
            if (width > 0 && height > 0) {
                drawBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                this.mDrawBitmap = new WeakReference<>(drawBitmap);
                this.mBitmapCanvas = new Canvas(drawBitmap);
            } else {
                return;
            }
        }
        drawBitmap.eraseColor(0);
        for (IPieDataSet set : ((PieData) this.mChart.getData()).getDataSets()) {
            if (set.isVisible() && set.getEntryCount() > 0) {
                drawDataSet(c, set);
            }
        }
    }

    /* access modifiers changed from: protected */
    public float calculateMinimumRadiusForSpacedSlice(MPPointF center, float radius, float angle, float arcStartPointX, float arcStartPointY, float startAngle, float sweepAngle) {
        MPPointF mPPointF = center;
        float angleMiddle = startAngle + (sweepAngle / 2.0f);
        float arcEndPointX = mPPointF.f90x + (((float) Math.cos((double) ((startAngle + sweepAngle) * 0.017453292f))) * radius);
        float arcEndPointY = mPPointF.f91y + (((float) Math.sin((double) ((startAngle + sweepAngle) * 0.017453292f))) * radius);
        float arcMidPointX = mPPointF.f90x + (((float) Math.cos((double) (angleMiddle * 0.017453292f))) * radius);
        float arcMidPointY = mPPointF.f91y + (((float) Math.sin((double) (0.017453292f * angleMiddle))) * radius);
        double d = (double) angle;
        Double.isNaN(d);
        double sqrt = (double) (radius - ((float) ((Math.sqrt(Math.pow((double) (arcEndPointX - arcStartPointX), 2.0d) + Math.pow((double) (arcEndPointY - arcStartPointY), 2.0d)) / 2.0d) * Math.tan(((180.0d - d) / 2.0d) * 0.017453292519943295d))));
        float f = angleMiddle;
        float f2 = arcEndPointX;
        double sqrt2 = Math.sqrt(Math.pow((double) (arcMidPointX - ((arcEndPointX + arcStartPointX) / 2.0f)), 2.0d) + Math.pow((double) (arcMidPointY - ((arcEndPointY + arcStartPointY) / 2.0f)), 2.0d));
        Double.isNaN(sqrt);
        return (float) (sqrt - sqrt2);
    }

    /* access modifiers changed from: protected */
    public float getSliceSpace(IPieDataSet dataSet) {
        if (!dataSet.isAutomaticallyDisableSliceSpacingEnabled()) {
            return dataSet.getSliceSpace();
        }
        if (dataSet.getSliceSpace() / this.mViewPortHandler.getSmallestContentExtension() > (dataSet.getYMin() / ((PieData) this.mChart.getData()).getYValueSum()) * 2.0f) {
            return 0.0f;
        }
        return dataSet.getSliceSpace();
    }

    /* access modifiers changed from: protected */
    public void drawDataSet(Canvas c, IPieDataSet dataSet) {
        float rotationAngle;
        int visibleAngleCount;
        float radius;
        float phaseX;
        float[] drawAngles;
        RectF circleBox;
        int entryCount;
        int j;
        RectF roundedCircleBox;
        MPPointF center;
        float sweepAngleOuter;
        int visibleAngleCount2;
        int j2;
        float arcStartPointX;
        float startAngleOuter;
        RectF roundedCircleBox2;
        float rotationAngle2;
        int j3;
        int visibleAngleCount3;
        float radius2;
        RectF circleBox2;
        MPPointF center2;
        RectF roundedCircleBox3;
        int i;
        int visibleAngleCount4;
        MPPointF center3;
        RectF roundedCircleBox4;
        PieChartRenderer pieChartRenderer = this;
        IPieDataSet iPieDataSet = dataSet;
        float rotationAngle3 = pieChartRenderer.mChart.getRotationAngle();
        float phaseX2 = pieChartRenderer.mAnimator.getPhaseX();
        float phaseY = pieChartRenderer.mAnimator.getPhaseY();
        RectF circleBox3 = pieChartRenderer.mChart.getCircleBox();
        int entryCount2 = dataSet.getEntryCount();
        float[] drawAngles2 = pieChartRenderer.mChart.getDrawAngles();
        MPPointF center4 = pieChartRenderer.mChart.getCenterCircleBox();
        float radius3 = pieChartRenderer.mChart.getRadius();
        boolean drawInnerArc = pieChartRenderer.mChart.isDrawHoleEnabled() && !pieChartRenderer.mChart.isDrawSlicesUnderHoleEnabled();
        float userInnerRadius = drawInnerArc ? (pieChartRenderer.mChart.getHoleRadius() / 100.0f) * radius3 : 0.0f;
        float roundedRadius = (radius3 - ((pieChartRenderer.mChart.getHoleRadius() * radius3) / 100.0f)) / 2.0f;
        RectF roundedCircleBox5 = new RectF();
        boolean drawRoundedSlices = drawInnerArc && pieChartRenderer.mChart.isDrawRoundedSlicesEnabled();
        int visibleAngleCount5 = 0;
        for (int j4 = 0; j4 < entryCount2; j4++) {
            if (Math.abs(((PieEntry) iPieDataSet.getEntryForIndex(j4)).getY()) > Utils.FLOAT_EPSILON) {
                visibleAngleCount5++;
            }
        }
        float sliceSpace = visibleAngleCount5 <= 1 ? 0 : pieChartRenderer.getSliceSpace(iPieDataSet);
        float angle = 0.0f;
        int j5 = 0;
        while (j5 < entryCount2) {
            float sliceAngle = drawAngles2[j5];
            float innerRadius = userInnerRadius;
            if (Math.abs(iPieDataSet.getEntryForIndex(j5).getY()) <= Utils.FLOAT_EPSILON) {
                angle += sliceAngle * phaseX2;
                j = j5;
                visibleAngleCount = visibleAngleCount5;
                radius = radius3;
                rotationAngle = rotationAngle3;
                phaseX = phaseX2;
                circleBox = circleBox3;
                entryCount = entryCount2;
                drawAngles = drawAngles2;
                roundedCircleBox = roundedCircleBox5;
                center = center4;
            } else if (!pieChartRenderer.mChart.needsHighlight(j5) || drawRoundedSlices) {
                boolean accountForSliceSpacing = sliceSpace > 0.0f && sliceAngle <= 180.0f;
                pieChartRenderer.mRenderPaint.setColor(iPieDataSet.getColor(j5));
                float sliceSpaceAngleOuter = visibleAngleCount5 == 1 ? 0.0f : sliceSpace / (radius3 * 0.017453292f);
                float startAngleOuter2 = ((angle + (sliceSpaceAngleOuter / 2.0f)) * phaseY) + rotationAngle3;
                float sweepAngleOuter2 = (sliceAngle - sliceSpaceAngleOuter) * phaseY;
                if (sweepAngleOuter2 < 0.0f) {
                    sweepAngleOuter = 0.0f;
                } else {
                    sweepAngleOuter = sweepAngleOuter2;
                }
                pieChartRenderer.mPathBuffer.reset();
                if (drawRoundedSlices) {
                    j2 = j5;
                    visibleAngleCount2 = visibleAngleCount5;
                    float x = center4.f90x + ((radius3 - roundedRadius) * ((float) Math.cos((double) (startAngleOuter2 * 0.017453292f))));
                    entryCount = entryCount2;
                    drawAngles = drawAngles2;
                    float y = center4.f91y + ((radius3 - roundedRadius) * ((float) Math.sin((double) (startAngleOuter2 * 0.017453292f))));
                    float f = x;
                    roundedCircleBox5.set(x - roundedRadius, y - roundedRadius, x + roundedRadius, y + roundedRadius);
                } else {
                    j2 = j5;
                    visibleAngleCount2 = visibleAngleCount5;
                    entryCount = entryCount2;
                    drawAngles = drawAngles2;
                }
                float arcStartPointX2 = center4.f90x + (((float) Math.cos((double) (startAngleOuter2 * 0.017453292f))) * radius3);
                float rotationAngle4 = rotationAngle3;
                phaseX = phaseX2;
                float arcStartPointY = center4.f91y + (((float) Math.sin((double) (startAngleOuter2 * 0.017453292f))) * radius3);
                if (sweepAngleOuter < 360.0f || sweepAngleOuter % 360.0f > Utils.FLOAT_EPSILON) {
                    if (drawRoundedSlices) {
                        pieChartRenderer.mPathBuffer.arcTo(roundedCircleBox5, startAngleOuter2 + 180.0f, -180.0f);
                    }
                    pieChartRenderer.mPathBuffer.arcTo(circleBox3, startAngleOuter2, sweepAngleOuter);
                } else {
                    pieChartRenderer.mPathBuffer.addCircle(center4.f90x, center4.f91y, radius3, Path.Direction.CW);
                }
                float startAngleOuter3 = startAngleOuter2;
                pieChartRenderer.mInnerRectBuffer.set(center4.f90x - innerRadius, center4.f91y - innerRadius, center4.f90x + innerRadius, center4.f91y + innerRadius);
                if (!drawInnerArc) {
                    rotationAngle2 = rotationAngle4;
                    j3 = j2;
                    visibleAngleCount3 = visibleAngleCount2;
                    startAngleOuter = startAngleOuter3;
                    radius2 = radius3;
                    circleBox2 = circleBox3;
                    arcStartPointX = arcStartPointX2;
                    float f2 = innerRadius;
                    roundedCircleBox2 = roundedCircleBox5;
                } else if (innerRadius > 0.0f || accountForSliceSpacing) {
                    if (accountForSliceSpacing) {
                        float startAngleOuter4 = startAngleOuter3;
                        j = j2;
                        circleBox = circleBox3;
                        float innerRadius2 = innerRadius;
                        visibleAngleCount4 = visibleAngleCount2;
                        roundedCircleBox3 = roundedCircleBox5;
                        float f3 = arcStartPointX2;
                        float f4 = arcStartPointX2;
                        i = 1;
                        radius = radius3;
                        center2 = center4;
                        float minSpacedRadius = calculateMinimumRadiusForSpacedSlice(center4, radius3, sliceAngle * phaseY, f3, arcStartPointY, startAngleOuter4, sweepAngleOuter);
                        if (minSpacedRadius < 0.0f) {
                            minSpacedRadius = -minSpacedRadius;
                        }
                        innerRadius = Math.max(innerRadius2, minSpacedRadius);
                    } else {
                        roundedCircleBox3 = roundedCircleBox5;
                        center2 = center4;
                        j = j2;
                        visibleAngleCount4 = visibleAngleCount2;
                        float f5 = startAngleOuter3;
                        radius = radius3;
                        circleBox = circleBox3;
                        float f6 = arcStartPointX2;
                        i = 1;
                        float f7 = innerRadius;
                    }
                    float sliceSpaceAngleInner = (visibleAngleCount4 == i || innerRadius == 0.0f) ? 0.0f : sliceSpace / (innerRadius * 0.017453292f);
                    float startAngleInner = ((angle + (sliceSpaceAngleInner / 2.0f)) * phaseY) + rotationAngle4;
                    float sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY;
                    if (sweepAngleInner < 0.0f) {
                        sweepAngleInner = 0.0f;
                    }
                    float endAngleInner = startAngleInner + sweepAngleInner;
                    if (sweepAngleOuter < 360.0f || sweepAngleOuter % 360.0f > Utils.FLOAT_EPSILON) {
                        visibleAngleCount = visibleAngleCount4;
                        center3 = center2;
                        pieChartRenderer = this;
                        if (drawRoundedSlices) {
                            float x2 = center3.f90x + ((radius - roundedRadius) * ((float) Math.cos((double) (endAngleInner * 0.017453292f))));
                            rotationAngle = rotationAngle4;
                            float y2 = center3.f91y + ((radius - roundedRadius) * ((float) Math.sin((double) (endAngleInner * 0.017453292f))));
                            float f8 = sliceSpaceAngleInner;
                            float sliceSpaceAngleInner2 = y2 + roundedRadius;
                            float f9 = y2;
                            roundedCircleBox4 = roundedCircleBox3;
                            roundedCircleBox4.set(x2 - roundedRadius, y2 - roundedRadius, x2 + roundedRadius, sliceSpaceAngleInner2);
                            pieChartRenderer.mPathBuffer.arcTo(roundedCircleBox4, endAngleInner, 180.0f);
                        } else {
                            rotationAngle = rotationAngle4;
                            roundedCircleBox4 = roundedCircleBox3;
                            pieChartRenderer.mPathBuffer.lineTo(center3.f90x + (((float) Math.cos((double) (endAngleInner * 0.017453292f))) * innerRadius), center3.f91y + (((float) Math.sin((double) (endAngleInner * 0.017453292f))) * innerRadius));
                        }
                        pieChartRenderer.mPathBuffer.arcTo(pieChartRenderer.mInnerRectBuffer, endAngleInner, -sweepAngleInner);
                    } else {
                        visibleAngleCount = visibleAngleCount4;
                        pieChartRenderer = this;
                        center3 = center2;
                        pieChartRenderer.mPathBuffer.addCircle(center3.f90x, center3.f91y, innerRadius, Path.Direction.CCW);
                        rotationAngle = rotationAngle4;
                        roundedCircleBox4 = roundedCircleBox3;
                    }
                    float f10 = innerRadius;
                    roundedCircleBox = roundedCircleBox4;
                    float f11 = arcStartPointY;
                    center = center3;
                    pieChartRenderer.mPathBuffer.close();
                    pieChartRenderer.mBitmapCanvas.drawPath(pieChartRenderer.mPathBuffer, pieChartRenderer.mRenderPaint);
                    angle += sliceAngle * phaseX;
                } else {
                    rotationAngle2 = rotationAngle4;
                    j3 = j2;
                    visibleAngleCount3 = visibleAngleCount2;
                    startAngleOuter = startAngleOuter3;
                    radius2 = radius3;
                    circleBox2 = circleBox3;
                    arcStartPointX = arcStartPointX2;
                    float f12 = innerRadius;
                    roundedCircleBox2 = roundedCircleBox5;
                }
                if (sweepAngleOuter % 360.0f <= Utils.FLOAT_EPSILON) {
                    roundedCircleBox = roundedCircleBox2;
                    float f13 = arcStartPointY;
                    center = center4;
                } else if (accountForSliceSpacing) {
                    float angleMiddle = startAngleOuter + (sweepAngleOuter / 2.0f);
                    roundedCircleBox = roundedCircleBox2;
                    float f14 = arcStartPointY;
                    center = center4;
                    float sliceSpaceOffset = calculateMinimumRadiusForSpacedSlice(center4, radius, sliceAngle * phaseY, arcStartPointX, arcStartPointY, startAngleOuter, sweepAngleOuter);
                    pieChartRenderer.mPathBuffer.lineTo(center.f90x + (((float) Math.cos((double) (angleMiddle * 0.017453292f))) * sliceSpaceOffset), center.f91y + (((float) Math.sin((double) (angleMiddle * 0.017453292f))) * sliceSpaceOffset));
                } else {
                    roundedCircleBox = roundedCircleBox2;
                    float f15 = arcStartPointY;
                    center = center4;
                    pieChartRenderer.mPathBuffer.lineTo(center.f90x, center.f91y);
                }
                pieChartRenderer.mPathBuffer.close();
                pieChartRenderer.mBitmapCanvas.drawPath(pieChartRenderer.mPathBuffer, pieChartRenderer.mRenderPaint);
                angle += sliceAngle * phaseX;
            } else {
                angle += sliceAngle * phaseX2;
                j = j5;
                visibleAngleCount = visibleAngleCount5;
                radius = radius3;
                rotationAngle = rotationAngle3;
                phaseX = phaseX2;
                circleBox = circleBox3;
                entryCount = entryCount2;
                drawAngles = drawAngles2;
                roundedCircleBox = roundedCircleBox5;
                center = center4;
            }
            j5 = j + 1;
            iPieDataSet = dataSet;
            center4 = center;
            roundedCircleBox5 = roundedCircleBox;
            entryCount2 = entryCount;
            circleBox3 = circleBox;
            drawAngles2 = drawAngles;
            phaseX2 = phaseX;
            radius3 = radius;
            visibleAngleCount5 = visibleAngleCount;
            rotationAngle3 = rotationAngle;
        }
        MPPointF.recycleInstance(center4);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v1, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v1, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: float[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v5, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v3, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: float[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v3, resolved type: float[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v1, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v0, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r63v2, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v7, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v1, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v1, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v0, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r63v3, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r63v4, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r63v5, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v4, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v2, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v13, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v3, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v14, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v4, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v17, resolved type: float} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x02cc  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0309  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x034e  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0369  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawValues(android.graphics.Canvas r69) {
        /*
            r68 = this;
            r6 = r68
            r7 = r69
            com.github.mikephil.charting.charts.PieChart r0 = r6.mChart
            com.github.mikephil.charting.utils.MPPointF r8 = r0.getCenterCircleBox()
            com.github.mikephil.charting.charts.PieChart r0 = r6.mChart
            float r9 = r0.getRadius()
            com.github.mikephil.charting.charts.PieChart r0 = r6.mChart
            float r0 = r0.getRotationAngle()
            com.github.mikephil.charting.charts.PieChart r1 = r6.mChart
            float[] r10 = r1.getDrawAngles()
            com.github.mikephil.charting.charts.PieChart r1 = r6.mChart
            float[] r11 = r1.getAbsoluteAngles()
            com.github.mikephil.charting.animation.ChartAnimator r1 = r6.mAnimator
            float r12 = r1.getPhaseX()
            com.github.mikephil.charting.animation.ChartAnimator r1 = r6.mAnimator
            float r13 = r1.getPhaseY()
            com.github.mikephil.charting.charts.PieChart r1 = r6.mChart
            float r1 = r1.getHoleRadius()
            float r1 = r1 * r9
            r14 = 1120403456(0x42c80000, float:100.0)
            float r1 = r1 / r14
            float r1 = r9 - r1
            r15 = 1073741824(0x40000000, float:2.0)
            float r16 = r1 / r15
            com.github.mikephil.charting.charts.PieChart r1 = r6.mChart
            float r1 = r1.getHoleRadius()
            float r17 = r1 / r14
            r1 = 1092616192(0x41200000, float:10.0)
            float r1 = r9 / r1
            r2 = 1080452710(0x40666666, float:3.6)
            float r1 = r1 * r2
            com.github.mikephil.charting.charts.PieChart r2 = r6.mChart
            boolean r2 = r2.isDrawHoleEnabled()
            if (r2 == 0) goto L_0x008e
            float r2 = r9 * r17
            float r2 = r9 - r2
            float r1 = r2 / r15
            com.github.mikephil.charting.charts.PieChart r2 = r6.mChart
            boolean r2 = r2.isDrawSlicesUnderHoleEnabled()
            if (r2 != 0) goto L_0x008b
            com.github.mikephil.charting.charts.PieChart r2 = r6.mChart
            boolean r2 = r2.isDrawRoundedSlicesEnabled()
            if (r2 == 0) goto L_0x008b
            double r2 = (double) r0
            r4 = 1135869952(0x43b40000, float:360.0)
            float r4 = r4 * r16
            double r4 = (double) r4
            r18 = 4618760256179416344(0x401921fb54442d18, double:6.283185307179586)
            double r14 = (double) r9
            java.lang.Double.isNaN(r14)
            double r14 = r14 * r18
            java.lang.Double.isNaN(r4)
            double r4 = r4 / r14
            java.lang.Double.isNaN(r2)
            double r2 = r2 + r4
            float r0 = (float) r2
            r15 = r0
            r14 = r1
            goto L_0x0090
        L_0x008b:
            r15 = r0
            r14 = r1
            goto L_0x0090
        L_0x008e:
            r15 = r0
            r14 = r1
        L_0x0090:
            float r18 = r9 - r14
            com.github.mikephil.charting.charts.PieChart r0 = r6.mChart
            com.github.mikephil.charting.data.ChartData r0 = r0.getData()
            r19 = r0
            com.github.mikephil.charting.data.PieData r19 = (com.github.mikephil.charting.data.PieData) r19
            java.util.List r5 = r19.getDataSets()
            float r22 = r19.getYValueSum()
            com.github.mikephil.charting.charts.PieChart r0 = r6.mChart
            boolean r23 = r0.isDrawEntryLabelsEnabled()
            r0 = 0
            r69.save()
            r1 = 1084227584(0x40a00000, float:5.0)
            float r24 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r1)
            r1 = 0
            r4 = r1
        L_0x00b6:
            int r1 = r5.size()
            if (r4 >= r1) goto L_0x0497
            java.lang.Object r1 = r5.get(r4)
            r3 = r1
            com.github.mikephil.charting.interfaces.datasets.IPieDataSet r3 = (com.github.mikephil.charting.interfaces.datasets.IPieDataSet) r3
            boolean r25 = r3.isDrawValuesEnabled()
            if (r25 != 0) goto L_0x00e2
            if (r23 != 0) goto L_0x00e2
            r27 = r4
            r29 = r5
            r63 = r9
            r38 = r10
            r40 = r11
            r44 = r12
            r45 = r13
            r30 = r14
            r20 = 1120403456(0x42c80000, float:100.0)
            r21 = 1073741824(0x40000000, float:2.0)
            r13 = r7
            goto L_0x0484
        L_0x00e2:
            com.github.mikephil.charting.data.PieDataSet$ValuePosition r2 = r3.getXValuePosition()
            com.github.mikephil.charting.data.PieDataSet$ValuePosition r1 = r3.getYValuePosition()
            r6.applyValueTextStyle(r3)
            r26 = r0
            android.graphics.Paint r0 = r6.mValuePaint
            r27 = r4
            java.lang.String r4 = "Q"
            int r0 = com.github.mikephil.charting.utils.Utils.calcTextHeight(r0, r4)
            float r0 = (float) r0
            r4 = 1082130432(0x40800000, float:4.0)
            float r4 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r4)
            float r28 = r0 + r4
            com.github.mikephil.charting.formatter.ValueFormatter r4 = r3.getValueFormatter()
            int r0 = r3.getEntryCount()
            r29 = r5
            android.graphics.Paint r5 = r6.mValueLinePaint
            r30 = r14
            int r14 = r3.getValueLineColor()
            r5.setColor(r14)
            android.graphics.Paint r5 = r6.mValueLinePaint
            float r14 = r3.getValueLineWidth()
            float r14 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r14)
            r5.setStrokeWidth(r14)
            float r14 = r6.getSliceSpace(r3)
            com.github.mikephil.charting.utils.MPPointF r5 = r3.getIconsOffset()
            com.github.mikephil.charting.utils.MPPointF r5 = com.github.mikephil.charting.utils.MPPointF.getInstance(r5)
            float r7 = r5.f90x
            float r7 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r7)
            r5.f90x = r7
            float r7 = r5.f91y
            float r7 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r7)
            r5.f91y = r7
            r7 = 0
        L_0x0141:
            if (r7 >= r0) goto L_0x0463
            com.github.mikephil.charting.data.Entry r31 = r3.getEntryForIndex(r7)
            r32 = r5
            r5 = r31
            com.github.mikephil.charting.data.PieEntry r5 = (com.github.mikephil.charting.data.PieEntry) r5
            if (r26 != 0) goto L_0x0152
            r31 = 0
            goto L_0x0158
        L_0x0152:
            int r31 = r26 + -1
            r31 = r11[r31]
            float r31 = r31 * r12
        L_0x0158:
            r33 = r10[r26]
            r34 = 1016003125(0x3c8efa35, float:0.017453292)
            float r35 = r18 * r34
            float r35 = r14 / r35
            r21 = 1073741824(0x40000000, float:2.0)
            float r36 = r35 / r21
            float r36 = r33 - r36
            float r36 = r36 / r21
            float r31 = r31 + r36
            float r37 = r31 * r13
            r38 = r10
            float r10 = r15 + r37
            r37 = r0
            com.github.mikephil.charting.charts.PieChart r0 = r6.mChart
            boolean r0 = r0.isUsePercentValuesEnabled()
            if (r0 == 0) goto L_0x0186
            float r0 = r5.getY()
            float r0 = r0 / r22
            r20 = 1120403456(0x42c80000, float:100.0)
            float r0 = r0 * r20
            goto L_0x018a
        L_0x0186:
            float r0 = r5.getY()
        L_0x018a:
            java.lang.String r39 = r4.getPieLabel(r0, r5)
            r40 = r11
            java.lang.String r11 = r5.getLabel()
            r41 = r0
            float r0 = r10 * r34
            r42 = r4
            r43 = r5
            double r4 = (double) r0
            double r4 = java.lang.Math.cos(r4)
            float r5 = (float) r4
            float r0 = r10 * r34
            r44 = r12
            r45 = r13
            double r12 = (double) r0
            double r12 = java.lang.Math.sin(r12)
            float r12 = (float) r12
            r4 = 1
            if (r23 == 0) goto L_0x01b8
            com.github.mikephil.charting.data.PieDataSet$ValuePosition r13 = com.github.mikephil.charting.data.PieDataSet.ValuePosition.OUTSIDE_SLICE
            if (r2 != r13) goto L_0x01b8
            r13 = 1
            goto L_0x01b9
        L_0x01b8:
            r13 = 0
        L_0x01b9:
            if (r25 == 0) goto L_0x01c1
            com.github.mikephil.charting.data.PieDataSet$ValuePosition r0 = com.github.mikephil.charting.data.PieDataSet.ValuePosition.OUTSIDE_SLICE
            if (r1 != r0) goto L_0x01c1
            r0 = 1
            goto L_0x01c2
        L_0x01c1:
            r0 = 0
        L_0x01c2:
            r47 = r0
            if (r23 == 0) goto L_0x01cc
            com.github.mikephil.charting.data.PieDataSet$ValuePosition r0 = com.github.mikephil.charting.data.PieDataSet.ValuePosition.INSIDE_SLICE
            if (r2 != r0) goto L_0x01cc
            r0 = 1
            goto L_0x01cd
        L_0x01cc:
            r0 = 0
        L_0x01cd:
            r48 = r0
            if (r25 == 0) goto L_0x01d8
            com.github.mikephil.charting.data.PieDataSet$ValuePosition r0 = com.github.mikephil.charting.data.PieDataSet.ValuePosition.INSIDE_SLICE
            if (r1 != r0) goto L_0x01d8
            r46 = 1
            goto L_0x01da
        L_0x01d8:
            r46 = 0
        L_0x01da:
            if (r13 != 0) goto L_0x01f6
            if (r47 == 0) goto L_0x01df
            goto L_0x01f6
        L_0x01df:
            r53 = r1
            r34 = r2
            r65 = r5
            r63 = r9
            r62 = r13
            r20 = 1120403456(0x42c80000, float:100.0)
            r13 = r69
            r9 = r3
            r67 = r32
            r32 = r10
            r10 = r67
            goto L_0x038e
        L_0x01f6:
            float r49 = r3.getValueLinePart1Length()
            float r50 = r3.getValueLinePart2Length()
            float r0 = r3.getValueLinePart1OffsetPercentage()
            r20 = 1120403456(0x42c80000, float:100.0)
            float r51 = r0 / r20
            com.github.mikephil.charting.charts.PieChart r0 = r6.mChart
            boolean r0 = r0.isDrawHoleEnabled()
            if (r0 == 0) goto L_0x021a
            float r0 = r9 * r17
            float r0 = r9 - r0
            float r0 = r0 * r51
            float r4 = r9 * r17
            float r0 = r0 + r4
            r52 = r0
            goto L_0x021e
        L_0x021a:
            float r0 = r9 * r51
            r52 = r0
        L_0x021e:
            boolean r0 = r3.isValueLineVariableLength()
            if (r0 == 0) goto L_0x0239
            float r0 = r18 * r50
            float r4 = r10 * r34
            r53 = r1
            r34 = r2
            double r1 = (double) r4
            double r1 = java.lang.Math.sin(r1)
            double r1 = java.lang.Math.abs(r1)
            float r1 = (float) r1
            float r0 = r0 * r1
            goto L_0x023f
        L_0x0239:
            r53 = r1
            r34 = r2
            float r0 = r18 * r50
        L_0x023f:
            r54 = r0
            float r0 = r52 * r5
            float r1 = r8.f90x
            float r55 = r0 + r1
            float r0 = r52 * r12
            float r1 = r8.f91y
            float r56 = r0 + r1
            r0 = 1065353216(0x3f800000, float:1.0)
            float r1 = r49 + r0
            float r1 = r1 * r18
            float r1 = r1 * r5
            float r2 = r8.f90x
            float r57 = r1 + r2
            float r0 = r49 + r0
            float r0 = r0 * r18
            float r0 = r0 * r12
            float r1 = r8.f91y
            float r58 = r0 + r1
            double r0 = (double) r10
            r59 = 4645040803167600640(0x4076800000000000, double:360.0)
            java.lang.Double.isNaN(r0)
            double r0 = r0 % r59
            r61 = 4636033603912859648(0x4056800000000000, double:90.0)
            int r2 = (r0 > r61 ? 1 : (r0 == r61 ? 0 : -1))
            if (r2 < 0) goto L_0x02a5
            double r0 = (double) r10
            java.lang.Double.isNaN(r0)
            double r0 = r0 % r59
            r59 = 4643457506423603200(0x4070e00000000000, double:270.0)
            int r2 = (r0 > r59 ? 1 : (r0 == r59 ? 0 : -1))
            if (r2 > 0) goto L_0x02a5
            float r0 = r57 - r54
            r1 = r58
            android.graphics.Paint r2 = r6.mValuePaint
            android.graphics.Paint$Align r4 = android.graphics.Paint.Align.RIGHT
            r2.setTextAlign(r4)
            if (r13 == 0) goto L_0x029a
            android.graphics.Paint r2 = r6.mEntryLabelsPaint
            android.graphics.Paint$Align r4 = android.graphics.Paint.Align.RIGHT
            r2.setTextAlign(r4)
        L_0x029a:
            float r2 = r0 - r24
            r4 = r1
            r59 = r0
            r60 = r1
            r61 = r4
            r4 = r2
            goto L_0x02c3
        L_0x02a5:
            float r0 = r57 + r54
            r1 = r58
            android.graphics.Paint r2 = r6.mValuePaint
            android.graphics.Paint$Align r4 = android.graphics.Paint.Align.LEFT
            r2.setTextAlign(r4)
            if (r13 == 0) goto L_0x02b9
            android.graphics.Paint r2 = r6.mEntryLabelsPaint
            android.graphics.Paint$Align r4 = android.graphics.Paint.Align.LEFT
            r2.setTextAlign(r4)
        L_0x02b9:
            float r2 = r0 + r24
            r4 = r1
            r59 = r0
            r60 = r1
            r61 = r4
            r4 = r2
        L_0x02c3:
            int r0 = r3.getValueLineColor()
            r1 = 1122867(0x112233, float:1.573472E-39)
            if (r0 == r1) goto L_0x0309
            boolean r0 = r3.isUsingSliceColorAsValueLineColor()
            if (r0 == 0) goto L_0x02db
            android.graphics.Paint r0 = r6.mValueLinePaint
            int r1 = r3.getColor(r7)
            r0.setColor(r1)
        L_0x02db:
            android.graphics.Paint r2 = r6.mValueLinePaint
            r0 = r69
            r1 = r55
            r62 = r2
            r2 = r56
            r63 = r9
            r9 = r3
            r3 = r57
            r64 = r4
            r4 = r58
            r65 = r5
            r67 = r32
            r32 = r10
            r10 = r67
            r5 = r62
            r0.drawLine(r1, r2, r3, r4, r5)
            android.graphics.Paint r5 = r6.mValueLinePaint
            r1 = r57
            r2 = r58
            r3 = r59
            r4 = r60
            r0.drawLine(r1, r2, r3, r4, r5)
            goto L_0x0316
        L_0x0309:
            r64 = r4
            r65 = r5
            r63 = r9
            r9 = r3
            r67 = r32
            r32 = r10
            r10 = r67
        L_0x0316:
            if (r13 == 0) goto L_0x0348
            if (r47 == 0) goto L_0x0348
            int r5 = r9.getValueTextColor(r7)
            r0 = r68
            r1 = r69
            r2 = r39
            r3 = r64
            r4 = r61
            r0.drawValue(r1, r2, r3, r4, r5)
            int r0 = r19.getEntryCount()
            if (r7 >= r0) goto L_0x0340
            if (r11 == 0) goto L_0x0340
            float r0 = r61 + r28
            r5 = r69
            r4 = r64
            r6.drawEntryLabel(r5, r11, r4, r0)
            r62 = r13
            r13 = r5
            goto L_0x038e
        L_0x0340:
            r5 = r69
            r4 = r64
            r62 = r13
            r13 = r5
            goto L_0x038e
        L_0x0348:
            r5 = r69
            r4 = r64
            if (r13 == 0) goto L_0x0369
            int r0 = r19.getEntryCount()
            if (r7 >= r0) goto L_0x0363
            if (r11 == 0) goto L_0x0363
            r0 = 1073741824(0x40000000, float:2.0)
            float r1 = r28 / r0
            float r1 = r61 + r1
            r6.drawEntryLabel(r5, r11, r4, r1)
            r62 = r13
            r13 = r5
            goto L_0x038e
        L_0x0363:
            r0 = 1073741824(0x40000000, float:2.0)
            r62 = r13
            r13 = r5
            goto L_0x038e
        L_0x0369:
            r0 = 1073741824(0x40000000, float:2.0)
            if (r47 == 0) goto L_0x0389
            float r1 = r28 / r0
            float r62 = r61 + r1
            int r64 = r9.getValueTextColor(r7)
            r0 = r68
            r1 = r69
            r2 = r39
            r3 = r4
            r66 = r4
            r4 = r62
            r62 = r13
            r13 = r5
            r5 = r64
            r0.drawValue(r1, r2, r3, r4, r5)
            goto L_0x038e
        L_0x0389:
            r66 = r4
            r62 = r13
            r13 = r5
        L_0x038e:
            if (r48 != 0) goto L_0x0399
            if (r46 == 0) goto L_0x0393
            goto L_0x0399
        L_0x0393:
            r51 = r14
            r21 = 1073741824(0x40000000, float:2.0)
            goto L_0x0407
        L_0x0399:
            float r5 = r18 * r65
            float r0 = r8.f90x
            float r5 = r5 + r0
            float r0 = r18 * r12
            float r1 = r8.f91y
            float r49 = r0 + r1
            android.graphics.Paint r0 = r6.mValuePaint
            android.graphics.Paint$Align r1 = android.graphics.Paint.Align.CENTER
            r0.setTextAlign(r1)
            if (r48 == 0) goto L_0x03d7
            if (r46 == 0) goto L_0x03d7
            int r50 = r9.getValueTextColor(r7)
            r0 = r68
            r1 = r69
            r2 = r39
            r3 = r5
            r4 = r49
            r51 = r14
            r14 = r5
            r5 = r50
            r0.drawValue(r1, r2, r3, r4, r5)
            int r0 = r19.getEntryCount()
            if (r7 >= r0) goto L_0x03d4
            if (r11 == 0) goto L_0x03d4
            float r0 = r49 + r28
            r6.drawEntryLabel(r13, r11, r14, r0)
            r21 = 1073741824(0x40000000, float:2.0)
            goto L_0x0407
        L_0x03d4:
            r21 = 1073741824(0x40000000, float:2.0)
            goto L_0x0407
        L_0x03d7:
            r51 = r14
            r14 = r5
            if (r48 == 0) goto L_0x03f1
            int r0 = r19.getEntryCount()
            if (r7 >= r0) goto L_0x03ee
            if (r11 == 0) goto L_0x03ee
            r21 = 1073741824(0x40000000, float:2.0)
            float r0 = r28 / r21
            float r0 = r49 + r0
            r6.drawEntryLabel(r13, r11, r14, r0)
            goto L_0x0407
        L_0x03ee:
            r21 = 1073741824(0x40000000, float:2.0)
            goto L_0x0407
        L_0x03f1:
            r21 = 1073741824(0x40000000, float:2.0)
            if (r46 == 0) goto L_0x0407
            float r0 = r28 / r21
            float r4 = r49 + r0
            int r5 = r9.getValueTextColor(r7)
            r0 = r68
            r1 = r69
            r2 = r39
            r3 = r14
            r0.drawValue(r1, r2, r3, r4, r5)
        L_0x0407:
            android.graphics.drawable.Drawable r0 = r43.getIcon()
            if (r0 == 0) goto L_0x0446
            boolean r0 = r9.isDrawIconsEnabled()
            if (r0 == 0) goto L_0x0446
            android.graphics.drawable.Drawable r14 = r43.getIcon()
            float r0 = r10.f91y
            float r0 = r18 + r0
            float r0 = r0 * r65
            float r1 = r8.f90x
            float r5 = r0 + r1
            float r0 = r10.f91y
            float r0 = r18 + r0
            float r0 = r0 * r12
            float r1 = r8.f91y
            float r0 = r0 + r1
            float r1 = r10.f90x
            float r4 = r0 + r1
            int r2 = (int) r5
            int r3 = (int) r4
            int r49 = r14.getIntrinsicWidth()
            int r50 = r14.getIntrinsicHeight()
            r0 = r69
            r1 = r14
            r52 = r4
            r4 = r49
            r49 = r5
            r5 = r50
            com.github.mikephil.charting.utils.Utils.drawImage(r0, r1, r2, r3, r4, r5)
        L_0x0446:
            int r26 = r26 + 1
            int r7 = r7 + 1
            r3 = r9
            r5 = r10
            r2 = r34
            r0 = r37
            r10 = r38
            r11 = r40
            r4 = r42
            r12 = r44
            r13 = r45
            r14 = r51
            r1 = r53
            r9 = r63
            goto L_0x0141
        L_0x0463:
            r37 = r0
            r53 = r1
            r34 = r2
            r42 = r4
            r63 = r9
            r38 = r10
            r40 = r11
            r44 = r12
            r45 = r13
            r51 = r14
            r20 = 1120403456(0x42c80000, float:100.0)
            r21 = 1073741824(0x40000000, float:2.0)
            r13 = r69
            r9 = r3
            r10 = r5
            com.github.mikephil.charting.utils.MPPointF.recycleInstance(r10)
            r0 = r26
        L_0x0484:
            int r4 = r27 + 1
            r7 = r13
            r5 = r29
            r14 = r30
            r10 = r38
            r11 = r40
            r12 = r44
            r13 = r45
            r9 = r63
            goto L_0x00b6
        L_0x0497:
            r45 = r13
            r13 = r7
            com.github.mikephil.charting.utils.MPPointF.recycleInstance(r8)
            r69.restore()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.mikephil.charting.renderer.PieChartRenderer.drawValues(android.graphics.Canvas):void");
    }

    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        this.mValuePaint.setColor(color);
        c.drawText(valueText, x, y, this.mValuePaint);
    }

    /* access modifiers changed from: protected */
    public void drawEntryLabel(Canvas c, String label, float x, float y) {
        c.drawText(label, x, y, this.mEntryLabelsPaint);
    }

    public void drawExtras(Canvas c) {
        drawHole(c);
        c.drawBitmap((Bitmap) this.mDrawBitmap.get(), 0.0f, 0.0f, (Paint) null);
        drawCenterText(c);
    }

    /* access modifiers changed from: protected */
    public void drawHole(Canvas c) {
        if (this.mChart.isDrawHoleEnabled() && this.mBitmapCanvas != null) {
            float radius = this.mChart.getRadius();
            float holeRadius = (this.mChart.getHoleRadius() / 100.0f) * radius;
            MPPointF center = this.mChart.getCenterCircleBox();
            if (Color.alpha(this.mHolePaint.getColor()) > 0) {
                this.mBitmapCanvas.drawCircle(center.f90x, center.f91y, holeRadius, this.mHolePaint);
            }
            if (Color.alpha(this.mTransparentCirclePaint.getColor()) > 0 && this.mChart.getTransparentCircleRadius() > this.mChart.getHoleRadius()) {
                int alpha = this.mTransparentCirclePaint.getAlpha();
                this.mTransparentCirclePaint.setAlpha((int) (((float) alpha) * this.mAnimator.getPhaseX() * this.mAnimator.getPhaseY()));
                this.mHoleCirclePath.reset();
                this.mHoleCirclePath.addCircle(center.f90x, center.f91y, (this.mChart.getTransparentCircleRadius() / 100.0f) * radius, Path.Direction.CW);
                this.mHoleCirclePath.addCircle(center.f90x, center.f91y, holeRadius, Path.Direction.CCW);
                this.mBitmapCanvas.drawPath(this.mHoleCirclePath, this.mTransparentCirclePaint);
                this.mTransparentCirclePaint.setAlpha(alpha);
            }
            MPPointF.recycleInstance(center);
        }
    }

    /* access modifiers changed from: protected */
    public void drawCenterText(Canvas c) {
        float f;
        RectF boundingRect;
        RectF holeRect;
        Canvas canvas = c;
        CharSequence centerText = this.mChart.getCenterText();
        if (!this.mChart.isDrawCenterTextEnabled() || centerText == null) {
            return;
        }
        MPPointF center = this.mChart.getCenterCircleBox();
        MPPointF offset = this.mChart.getCenterTextOffset();
        float x = center.f90x + offset.f90x;
        float y = center.f91y + offset.f91y;
        if (!this.mChart.isDrawHoleEnabled() || this.mChart.isDrawSlicesUnderHoleEnabled()) {
            f = this.mChart.getRadius();
        } else {
            f = this.mChart.getRadius() * (this.mChart.getHoleRadius() / 100.0f);
        }
        float innerRadius = f;
        RectF[] rectFArr = this.mRectBuffer;
        RectF holeRect2 = rectFArr[0];
        holeRect2.left = x - innerRadius;
        holeRect2.top = y - innerRadius;
        holeRect2.right = x + innerRadius;
        holeRect2.bottom = y + innerRadius;
        RectF boundingRect2 = rectFArr[1];
        boundingRect2.set(holeRect2);
        float radiusPercent = this.mChart.getCenterTextRadiusPercent() / 100.0f;
        if (((double) radiusPercent) > Utils.DOUBLE_EPSILON) {
            boundingRect2.inset((boundingRect2.width() - (boundingRect2.width() * radiusPercent)) / 2.0f, (boundingRect2.height() - (boundingRect2.height() * radiusPercent)) / 2.0f);
        }
        if (!centerText.equals(this.mCenterTextLastValue) || !boundingRect2.equals(this.mCenterTextLastBounds)) {
            this.mCenterTextLastBounds.set(boundingRect2);
            this.mCenterTextLastValue = centerText;
            float width = this.mCenterTextLastBounds.width();
            int length = centerText.length();
            TextPaint textPaint = this.mCenterTextPaint;
            StaticLayout staticLayout = r3;
            int max = (int) Math.max(Math.ceil((double) width), 1.0d);
            float f2 = width;
            float f3 = radiusPercent;
            boundingRect = boundingRect2;
            CharSequence charSequence = centerText;
            holeRect = holeRect2;
            StaticLayout staticLayout2 = new StaticLayout(centerText, 0, length, textPaint, max, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            this.mCenterTextLayout = staticLayout;
        } else {
            CharSequence charSequence2 = centerText;
            float f4 = radiusPercent;
            boundingRect = boundingRect2;
            holeRect = holeRect2;
        }
        float layoutHeight = (float) this.mCenterTextLayout.getHeight();
        c.save();
        if (Build.VERSION.SDK_INT >= 18) {
            Path path = this.mDrawCenterTextPathBuffer;
            path.reset();
            path.addOval(holeRect, Path.Direction.CW);
            canvas.clipPath(path);
        }
        RectF boundingRect3 = boundingRect;
        canvas.translate(boundingRect3.left, boundingRect3.top + ((boundingRect3.height() - layoutHeight) / 2.0f));
        this.mCenterTextLayout.draw(canvas);
        c.restore();
        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(offset);
    }

    public void drawHighlighted(Canvas c, Highlight[] indices) {
        RectF highlightedCircleBox;
        boolean drawInnerArc;
        float[] drawAngles;
        int i;
        MPPointF center;
        float angle;
        float sweepAngleOuter;
        float angle2;
        float[] drawAngles2;
        int visibleAngleCount;
        int visibleAngleCount2;
        Highlight[] highlightArr = indices;
        int i2 = 1;
        boolean drawInnerArc2 = this.mChart.isDrawHoleEnabled() && !this.mChart.isDrawSlicesUnderHoleEnabled();
        if (!drawInnerArc2 || !this.mChart.isDrawRoundedSlicesEnabled()) {
            float phaseX = this.mAnimator.getPhaseX();
            float phaseY = this.mAnimator.getPhaseY();
            float rotationAngle = this.mChart.getRotationAngle();
            float[] drawAngles3 = this.mChart.getDrawAngles();
            float[] absoluteAngles = this.mChart.getAbsoluteAngles();
            MPPointF center2 = this.mChart.getCenterCircleBox();
            float radius = this.mChart.getRadius();
            float userInnerRadius = drawInnerArc2 ? (this.mChart.getHoleRadius() / 100.0f) * radius : 0.0f;
            RectF highlightedCircleBox2 = this.mDrawHighlightedRectF;
            highlightedCircleBox2.set(0.0f, 0.0f, 0.0f, 0.0f);
            int i3 = 0;
            while (i3 < highlightArr.length) {
                int index = (int) highlightArr[i3].getX();
                if (index >= drawAngles3.length) {
                    i = i3;
                    highlightedCircleBox = highlightedCircleBox2;
                    center = center2;
                    drawAngles = drawAngles3;
                    drawInnerArc = drawInnerArc2;
                } else {
                    IPieDataSet set = ((PieData) this.mChart.getData()).getDataSetByIndex(highlightArr[i3].getDataSetIndex());
                    if (set == null) {
                        int i4 = index;
                        i = i3;
                        highlightedCircleBox = highlightedCircleBox2;
                        center = center2;
                        drawAngles = drawAngles3;
                        drawInnerArc = drawInnerArc2;
                    } else if (!set.isHighlightEnabled()) {
                        i = i3;
                        highlightedCircleBox = highlightedCircleBox2;
                        center = center2;
                        drawAngles = drawAngles3;
                        drawInnerArc = drawInnerArc2;
                    } else {
                        int entryCount = set.getEntryCount();
                        int visibleAngleCount3 = 0;
                        for (int j = 0; j < entryCount; j++) {
                            if (Math.abs(((PieEntry) set.getEntryForIndex(j)).getY()) > Utils.FLOAT_EPSILON) {
                                visibleAngleCount3++;
                            }
                        }
                        if (index == 0) {
                            angle = 0.0f;
                        } else {
                            angle = absoluteAngles[index - 1] * phaseX;
                        }
                        float sliceSpace = visibleAngleCount3 <= i2 ? 0.0f : set.getSliceSpace();
                        float sliceAngle = drawAngles3[index];
                        float innerRadius = userInnerRadius;
                        float shift = set.getSelectionShift();
                        float highlightedRadius = radius + shift;
                        int i5 = entryCount;
                        highlightedCircleBox2.set(this.mChart.getCircleBox());
                        i = i3;
                        highlightedCircleBox2.inset(-shift, -shift);
                        boolean accountForSliceSpacing = sliceSpace > 0.0f && sliceAngle <= 180.0f;
                        this.mRenderPaint.setColor(set.getColor(index));
                        float sliceSpaceAngleOuter = visibleAngleCount3 == 1 ? 0.0f : sliceSpace / (radius * 0.017453292f);
                        float sliceSpaceAngleShifted = visibleAngleCount3 == 1 ? 0.0f : sliceSpace / (highlightedRadius * 0.017453292f);
                        float startAngleOuter = rotationAngle + (((sliceSpaceAngleOuter / 2.0f) + angle) * phaseY);
                        float sweepAngleOuter2 = (sliceAngle - sliceSpaceAngleOuter) * phaseY;
                        if (sweepAngleOuter2 < 0.0f) {
                            sweepAngleOuter = 0.0f;
                        } else {
                            sweepAngleOuter = sweepAngleOuter2;
                        }
                        float startAngleShifted = rotationAngle + (((sliceSpaceAngleShifted / 2.0f) + angle) * phaseY);
                        float sweepAngleShifted = (sliceAngle - sliceSpaceAngleShifted) * phaseY;
                        if (sweepAngleShifted < 0.0f) {
                            sweepAngleShifted = 0.0f;
                        }
                        IPieDataSet iPieDataSet = set;
                        this.mPathBuffer.reset();
                        if (sweepAngleOuter < 360.0f || sweepAngleOuter % 360.0f > Utils.FLOAT_EPSILON) {
                            visibleAngleCount = visibleAngleCount3;
                            drawAngles2 = drawAngles3;
                            angle2 = angle;
                            float f = shift;
                            this.mPathBuffer.moveTo(center2.f90x + (((float) Math.cos((double) (startAngleShifted * 0.017453292f))) * highlightedRadius), center2.f91y + (((float) Math.sin((double) (startAngleShifted * 0.017453292f))) * highlightedRadius));
                            this.mPathBuffer.arcTo(highlightedCircleBox2, startAngleShifted, sweepAngleShifted);
                        } else {
                            int i6 = index;
                            visibleAngleCount = visibleAngleCount3;
                            drawAngles2 = drawAngles3;
                            this.mPathBuffer.addCircle(center2.f90x, center2.f91y, highlightedRadius, Path.Direction.CW);
                            angle2 = angle;
                            float f2 = shift;
                        }
                        float sliceSpaceRadius = 0.0f;
                        if (accountForSliceSpacing) {
                            float f3 = sweepAngleShifted;
                            float f4 = startAngleShifted;
                            highlightedCircleBox = highlightedCircleBox2;
                            visibleAngleCount2 = visibleAngleCount;
                            drawAngles = drawAngles2;
                            float f5 = highlightedRadius;
                            center = center2;
                            sliceSpaceRadius = calculateMinimumRadiusForSpacedSlice(center2, radius, sliceAngle * phaseY, center2.f90x + (((float) Math.cos((double) (startAngleOuter * 0.017453292f))) * radius), center2.f91y + (((float) Math.sin((double) (startAngleOuter * 0.017453292f))) * radius), startAngleOuter, sweepAngleOuter);
                        } else {
                            float f6 = startAngleShifted;
                            highlightedCircleBox = highlightedCircleBox2;
                            visibleAngleCount2 = visibleAngleCount;
                            drawAngles = drawAngles2;
                            float f7 = highlightedRadius;
                            center = center2;
                        }
                        float innerRadius2 = innerRadius;
                        this.mInnerRectBuffer.set(center.f90x - innerRadius2, center.f91y - innerRadius2, center.f90x + innerRadius2, center.f91y + innerRadius2);
                        if (!drawInnerArc2) {
                            drawInnerArc = drawInnerArc2;
                        } else if (innerRadius2 > 0.0f || accountForSliceSpacing) {
                            if (accountForSliceSpacing) {
                                float minSpacedRadius = sliceSpaceRadius;
                                if (minSpacedRadius < 0.0f) {
                                    minSpacedRadius = -minSpacedRadius;
                                }
                                innerRadius2 = Math.max(innerRadius2, minSpacedRadius);
                            }
                            float sliceSpaceAngleInner = (visibleAngleCount2 == 1 || innerRadius2 == 0.0f) ? 0.0f : sliceSpace / (innerRadius2 * 0.017453292f);
                            float startAngleInner = ((angle2 + (sliceSpaceAngleInner / 2.0f)) * phaseY) + rotationAngle;
                            float sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY;
                            if (sweepAngleInner < 0.0f) {
                                sweepAngleInner = 0.0f;
                            }
                            float endAngleInner = startAngleInner + sweepAngleInner;
                            if (sweepAngleOuter < 360.0f || sweepAngleOuter % 360.0f > Utils.FLOAT_EPSILON) {
                                int i7 = visibleAngleCount2;
                                drawInnerArc = drawInnerArc2;
                                this.mPathBuffer.lineTo(center.f90x + (((float) Math.cos((double) (endAngleInner * 0.017453292f))) * innerRadius2), center.f91y + (((float) Math.sin((double) (endAngleInner * 0.017453292f))) * innerRadius2));
                                this.mPathBuffer.arcTo(this.mInnerRectBuffer, endAngleInner, -sweepAngleInner);
                            } else {
                                float f8 = sliceSpaceAngleInner;
                                this.mPathBuffer.addCircle(center.f90x, center.f91y, innerRadius2, Path.Direction.CCW);
                                int i8 = visibleAngleCount2;
                                drawInnerArc = drawInnerArc2;
                            }
                            this.mPathBuffer.close();
                            this.mBitmapCanvas.drawPath(this.mPathBuffer, this.mRenderPaint);
                        } else {
                            int i9 = visibleAngleCount2;
                            drawInnerArc = drawInnerArc2;
                        }
                        if (sweepAngleOuter % 360.0f > Utils.FLOAT_EPSILON) {
                            if (accountForSliceSpacing) {
                                float angleMiddle = startAngleOuter + (sweepAngleOuter / 2.0f);
                                this.mPathBuffer.lineTo(center.f90x + (((float) Math.cos((double) (angleMiddle * 0.017453292f))) * sliceSpaceRadius), center.f91y + (((float) Math.sin((double) (angleMiddle * 0.017453292f))) * sliceSpaceRadius));
                            } else {
                                this.mPathBuffer.lineTo(center.f90x, center.f91y);
                            }
                        }
                        this.mPathBuffer.close();
                        this.mBitmapCanvas.drawPath(this.mPathBuffer, this.mRenderPaint);
                    }
                }
                i3 = i + 1;
                center2 = center;
                drawAngles3 = drawAngles;
                drawInnerArc2 = drawInnerArc;
                highlightedCircleBox2 = highlightedCircleBox;
                i2 = 1;
                highlightArr = indices;
            }
            MPPointF.recycleInstance(center2);
        }
    }

    /* access modifiers changed from: protected */
    public void drawRoundedSlices(Canvas c) {
        float angle;
        float[] drawAngles;
        if (this.mChart.isDrawRoundedSlicesEnabled()) {
            IPieDataSet dataSet = ((PieData) this.mChart.getData()).getDataSet();
            if (dataSet.isVisible()) {
                float phaseX = this.mAnimator.getPhaseX();
                float phaseY = this.mAnimator.getPhaseY();
                MPPointF center = this.mChart.getCenterCircleBox();
                float r = this.mChart.getRadius();
                float circleRadius = (r - ((this.mChart.getHoleRadius() * r) / 100.0f)) / 2.0f;
                float[] drawAngles2 = this.mChart.getDrawAngles();
                float angle2 = this.mChart.getRotationAngle();
                int j = 0;
                while (j < dataSet.getEntryCount()) {
                    float sliceAngle = drawAngles2[j];
                    if (Math.abs(dataSet.getEntryForIndex(j).getY()) > Utils.FLOAT_EPSILON) {
                        double d = (double) (r - circleRadius);
                        double cos = Math.cos(Math.toRadians((double) ((angle2 + sliceAngle) * phaseY)));
                        Double.isNaN(d);
                        double d2 = d * cos;
                        double d3 = (double) center.f90x;
                        Double.isNaN(d3);
                        double d4 = (double) (r - circleRadius);
                        drawAngles = drawAngles2;
                        angle = angle2;
                        double sin = Math.sin(Math.toRadians((double) ((angle2 + sliceAngle) * phaseY)));
                        Double.isNaN(d4);
                        double d5 = d4 * sin;
                        double d6 = (double) center.f91y;
                        Double.isNaN(d6);
                        this.mRenderPaint.setColor(dataSet.getColor(j));
                        Canvas canvas = this.mBitmapCanvas;
                        canvas.drawCircle((float) (d2 + d3), (float) (d5 + d6), circleRadius, this.mRenderPaint);
                    } else {
                        drawAngles = drawAngles2;
                        angle = angle2;
                    }
                    angle2 = angle + (sliceAngle * phaseX);
                    j++;
                    drawAngles2 = drawAngles;
                }
                MPPointF.recycleInstance(center);
            }
        }
    }

    public void releaseBitmap() {
        Canvas canvas = this.mBitmapCanvas;
        if (canvas != null) {
            canvas.setBitmap((Bitmap) null);
            this.mBitmapCanvas = null;
        }
        WeakReference<Bitmap> weakReference = this.mDrawBitmap;
        if (weakReference != null) {
            Bitmap drawBitmap = (Bitmap) weakReference.get();
            if (drawBitmap != null) {
                drawBitmap.recycle();
            }
            this.mDrawBitmap.clear();
            this.mDrawBitmap = null;
        }
    }
}
