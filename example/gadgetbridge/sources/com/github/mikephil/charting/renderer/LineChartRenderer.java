package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.drawable.Drawable;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.BarLineScatterCandleBubbleRenderer;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class LineChartRenderer extends LineRadarRenderer {
    protected Path cubicFillPath = new Path();
    protected Path cubicPath = new Path();
    protected Canvas mBitmapCanvas;
    protected Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;
    protected LineDataProvider mChart;
    protected Paint mCirclePaintInner;
    private float[] mCirclesBuffer = new float[2];
    protected WeakReference<Bitmap> mDrawBitmap;
    protected Path mGenerateFilledPathBuffer = new Path();
    private HashMap<IDataSet, DataSetImageCache> mImageCaches = new HashMap<>();
    private float[] mLineBuffer = new float[4];

    public LineChartRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
        this.mCirclePaintInner = new Paint(1);
        this.mCirclePaintInner.setStyle(Paint.Style.FILL);
        this.mCirclePaintInner.setColor(-1);
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
                drawBitmap = Bitmap.createBitmap(width, height, this.mBitmapConfig);
                this.mDrawBitmap = new WeakReference<>(drawBitmap);
                this.mBitmapCanvas = new Canvas(drawBitmap);
            } else {
                return;
            }
        }
        drawBitmap.eraseColor(0);
        for (ILineDataSet set : this.mChart.getLineData().getDataSets()) {
            if (set.isVisible()) {
                drawDataSet(c, set);
            }
        }
        c.drawBitmap(drawBitmap, 0.0f, 0.0f, this.mRenderPaint);
    }

    /* access modifiers changed from: protected */
    public void drawDataSet(Canvas c, ILineDataSet dataSet) {
        if (dataSet.getEntryCount() >= 1) {
            this.mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
            this.mRenderPaint.setPathEffect(dataSet.getDashPathEffect());
            int i = C05781.$SwitchMap$com$github$mikephil$charting$data$LineDataSet$Mode[dataSet.getMode().ordinal()];
            if (i == 3) {
                drawCubicBezier(dataSet);
            } else if (i != 4) {
                drawLinear(c, dataSet);
            } else {
                drawHorizontalBezier(dataSet);
            }
            this.mRenderPaint.setPathEffect((PathEffect) null);
        }
    }

    /* renamed from: com.github.mikephil.charting.renderer.LineChartRenderer$1 */
    static /* synthetic */ class C05781 {
        static final /* synthetic */ int[] $SwitchMap$com$github$mikephil$charting$data$LineDataSet$Mode = new int[LineDataSet.Mode.values().length];

        static {
            try {
                $SwitchMap$com$github$mikephil$charting$data$LineDataSet$Mode[LineDataSet.Mode.LINEAR.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$github$mikephil$charting$data$LineDataSet$Mode[LineDataSet.Mode.STEPPED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$github$mikephil$charting$data$LineDataSet$Mode[LineDataSet.Mode.CUBIC_BEZIER.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$github$mikephil$charting$data$LineDataSet$Mode[LineDataSet.Mode.HORIZONTAL_BEZIER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawHorizontalBezier(ILineDataSet dataSet) {
        float phaseY = this.mAnimator.getPhaseY();
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        this.mXBounds.set(this.mChart, dataSet);
        this.cubicPath.reset();
        if (this.mXBounds.range >= 1) {
            Entry cur = dataSet.getEntryForIndex(this.mXBounds.min);
            this.cubicPath.moveTo(cur.getX(), cur.getY() * phaseY);
            for (int j = this.mXBounds.min + 1; j <= this.mXBounds.range + this.mXBounds.min; j++) {
                Entry prev = cur;
                cur = dataSet.getEntryForIndex(j);
                float cpx = prev.getX() + ((cur.getX() - prev.getX()) / 2.0f);
                this.cubicPath.cubicTo(cpx, prev.getY() * phaseY, cpx, cur.getY() * phaseY, cur.getX(), cur.getY() * phaseY);
            }
        }
        if (dataSet.isDrawFilledEnabled()) {
            this.cubicFillPath.reset();
            this.cubicFillPath.addPath(this.cubicPath);
            drawCubicFill(this.mBitmapCanvas, dataSet, this.cubicFillPath, trans, this.mXBounds);
        }
        this.mRenderPaint.setColor(dataSet.getColor());
        this.mRenderPaint.setStyle(Paint.Style.STROKE);
        trans.pathValueToPixel(this.cubicPath);
        this.mBitmapCanvas.drawPath(this.cubicPath, this.mRenderPaint);
        this.mRenderPaint.setPathEffect((PathEffect) null);
    }

    /* access modifiers changed from: protected */
    public void drawCubicBezier(ILineDataSet dataSet) {
        ILineDataSet iLineDataSet = dataSet;
        float phaseY = this.mAnimator.getPhaseY();
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        this.mXBounds.set(this.mChart, iLineDataSet);
        float intensity = dataSet.getCubicIntensity();
        this.cubicPath.reset();
        if (this.mXBounds.range >= 1) {
            float curDx = 0.0f;
            int firstIndex = this.mXBounds.min + 1;
            int i = this.mXBounds.min + this.mXBounds.range;
            Entry prev = iLineDataSet.getEntryForIndex(Math.max(firstIndex - 2, 0));
            Entry cur = iLineDataSet.getEntryForIndex(Math.max(firstIndex - 1, 0));
            Entry next = cur;
            int nextIndex = -1;
            if (cur != null) {
                this.cubicPath.moveTo(cur.getX(), cur.getY() * phaseY);
                int j = this.mXBounds.min + 1;
                while (j <= this.mXBounds.range + this.mXBounds.min) {
                    Entry prevPrev = prev;
                    prev = cur;
                    cur = nextIndex == j ? next : iLineDataSet.getEntryForIndex(j);
                    float f = curDx;
                    nextIndex = j + 1 < dataSet.getEntryCount() ? j + 1 : j;
                    next = iLineDataSet.getEntryForIndex(nextIndex);
                    float prevDx = (cur.getX() - prevPrev.getX()) * intensity;
                    float prevDy = (cur.getY() - prevPrev.getY()) * intensity;
                    curDx = (next.getX() - prev.getX()) * intensity;
                    this.cubicPath.cubicTo(prev.getX() + prevDx, (prev.getY() + prevDy) * phaseY, cur.getX() - curDx, (cur.getY() - ((next.getY() - prev.getY()) * intensity)) * phaseY, cur.getX(), cur.getY() * phaseY);
                    j++;
                }
                float f2 = curDx;
            } else {
                return;
            }
        }
        if (dataSet.isDrawFilledEnabled() != 0) {
            this.cubicFillPath.reset();
            this.cubicFillPath.addPath(this.cubicPath);
            drawCubicFill(this.mBitmapCanvas, dataSet, this.cubicFillPath, trans, this.mXBounds);
        }
        this.mRenderPaint.setColor(dataSet.getColor());
        this.mRenderPaint.setStyle(Paint.Style.STROKE);
        trans.pathValueToPixel(this.cubicPath);
        this.mBitmapCanvas.drawPath(this.cubicPath, this.mRenderPaint);
        this.mRenderPaint.setPathEffect((PathEffect) null);
    }

    /* access modifiers changed from: protected */
    public void drawCubicFill(Canvas c, ILineDataSet dataSet, Path spline, Transformer trans, BarLineScatterCandleBubbleRenderer.XBounds bounds) {
        float fillMin = dataSet.getFillFormatter().getFillLinePosition(dataSet, this.mChart);
        spline.lineTo(dataSet.getEntryForIndex(bounds.min + bounds.range).getX(), fillMin);
        spline.lineTo(dataSet.getEntryForIndex(bounds.min).getX(), fillMin);
        spline.close();
        trans.pathValueToPixel(spline);
        Drawable drawable = dataSet.getFillDrawable();
        if (drawable != null) {
            drawFilledPath(c, spline, drawable);
        } else {
            drawFilledPath(c, spline, dataSet.getFillColor(), dataSet.getFillAlpha());
        }
    }

    /* access modifiers changed from: protected */
    public void drawLinear(Canvas c, ILineDataSet dataSet) {
        Canvas canvas;
        ILineDataSet iLineDataSet = dataSet;
        int entryCount = dataSet.getEntryCount();
        char c2 = 1;
        boolean isDrawSteppedEnabled = dataSet.getMode() == LineDataSet.Mode.STEPPED;
        char c3 = 4;
        int pointsPerEntryPair = isDrawSteppedEnabled ? 4 : 2;
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        float phaseY = this.mAnimator.getPhaseY();
        this.mRenderPaint.setStyle(Paint.Style.STROKE);
        if (dataSet.isDashedLineEnabled()) {
            canvas = this.mBitmapCanvas;
        } else {
            canvas = c;
        }
        this.mXBounds.set(this.mChart, iLineDataSet);
        if (!dataSet.isDrawFilledEnabled() || entryCount <= 0) {
            Canvas canvas2 = c;
        } else {
            drawLinearFill(c, iLineDataSet, trans, this.mXBounds);
        }
        if (dataSet.getColors().size() > 1) {
            if (this.mLineBuffer.length <= pointsPerEntryPair * 2) {
                this.mLineBuffer = new float[(pointsPerEntryPair * 4)];
            }
            int j = this.mXBounds.min;
            while (j <= this.mXBounds.range + this.mXBounds.min) {
                Entry e = iLineDataSet.getEntryForIndex(j);
                if (e != null) {
                    this.mLineBuffer[0] = e.getX();
                    this.mLineBuffer[c2] = e.getY() * phaseY;
                    if (j < this.mXBounds.max) {
                        Entry e2 = iLineDataSet.getEntryForIndex(j + 1);
                        if (e2 == null) {
                            break;
                        } else if (isDrawSteppedEnabled) {
                            this.mLineBuffer[2] = e2.getX();
                            float[] fArr = this.mLineBuffer;
                            fArr[3] = fArr[c2];
                            fArr[c3] = fArr[2];
                            fArr[5] = fArr[3];
                            fArr[6] = e2.getX();
                            this.mLineBuffer[7] = e2.getY() * phaseY;
                        } else {
                            this.mLineBuffer[2] = e2.getX();
                            this.mLineBuffer[3] = e2.getY() * phaseY;
                        }
                    } else {
                        float[] fArr2 = this.mLineBuffer;
                        fArr2[2] = fArr2[0];
                        fArr2[3] = fArr2[c2];
                    }
                    trans.pointValuesToPixel(this.mLineBuffer);
                    if (!this.mViewPortHandler.isInBoundsRight(this.mLineBuffer[0])) {
                        break;
                    } else if (this.mViewPortHandler.isInBoundsLeft(this.mLineBuffer[2]) && (this.mViewPortHandler.isInBoundsTop(this.mLineBuffer[c2]) || this.mViewPortHandler.isInBoundsBottom(this.mLineBuffer[3]))) {
                        this.mRenderPaint.setColor(iLineDataSet.getColor(j));
                        canvas.drawLines(this.mLineBuffer, 0, pointsPerEntryPair * 2, this.mRenderPaint);
                    }
                }
                j++;
                c3 = 4;
                c2 = 1;
            }
        } else {
            if (this.mLineBuffer.length < Math.max(entryCount * pointsPerEntryPair, pointsPerEntryPair) * 2) {
                this.mLineBuffer = new float[(Math.max(entryCount * pointsPerEntryPair, pointsPerEntryPair) * 4)];
            }
            if (iLineDataSet.getEntryForIndex(this.mXBounds.min) != null) {
                int j2 = 0;
                int x = this.mXBounds.min;
                while (x <= this.mXBounds.range + this.mXBounds.min) {
                    Entry e1 = iLineDataSet.getEntryForIndex(x == 0 ? 0 : x - 1);
                    Entry e22 = iLineDataSet.getEntryForIndex(x);
                    if (!(e1 == null || e22 == null)) {
                        int j3 = j2 + 1;
                        this.mLineBuffer[j2] = e1.getX();
                        int j4 = j3 + 1;
                        this.mLineBuffer[j3] = e1.getY() * phaseY;
                        if (isDrawSteppedEnabled) {
                            int j5 = j4 + 1;
                            this.mLineBuffer[j4] = e22.getX();
                            int j6 = j5 + 1;
                            this.mLineBuffer[j5] = e1.getY() * phaseY;
                            int j7 = j6 + 1;
                            this.mLineBuffer[j6] = e22.getX();
                            j4 = j7 + 1;
                            this.mLineBuffer[j7] = e1.getY() * phaseY;
                        }
                        int j8 = j4 + 1;
                        this.mLineBuffer[j4] = e22.getX();
                        this.mLineBuffer[j8] = e22.getY() * phaseY;
                        j2 = j8 + 1;
                    }
                    x++;
                }
                if (j2 > 0) {
                    trans.pointValuesToPixel(this.mLineBuffer);
                    this.mRenderPaint.setColor(dataSet.getColor());
                    canvas.drawLines(this.mLineBuffer, 0, Math.max((this.mXBounds.range + 1) * pointsPerEntryPair, pointsPerEntryPair) * 2, this.mRenderPaint);
                }
            }
        }
        this.mRenderPaint.setPathEffect((PathEffect) null);
    }

    /* access modifiers changed from: protected */
    public void drawLinearFill(Canvas c, ILineDataSet dataSet, Transformer trans, BarLineScatterCandleBubbleRenderer.XBounds bounds) {
        int currentStartIndex;
        int currentEndIndex;
        Path filled = this.mGenerateFilledPathBuffer;
        int startingIndex = bounds.min;
        int endingIndex = bounds.range + bounds.min;
        int iterations = 0;
        do {
            currentStartIndex = startingIndex + (iterations * 128);
            int currentEndIndex2 = currentStartIndex + 128;
            currentEndIndex = currentEndIndex2 > endingIndex ? endingIndex : currentEndIndex2;
            if (currentStartIndex <= currentEndIndex) {
                generateFilledPath(dataSet, currentStartIndex, currentEndIndex, filled);
                trans.pathValueToPixel(filled);
                Drawable drawable = dataSet.getFillDrawable();
                if (drawable != null) {
                    drawFilledPath(c, filled, drawable);
                } else {
                    drawFilledPath(c, filled, dataSet.getFillColor(), dataSet.getFillAlpha());
                }
            }
            iterations++;
        } while (currentStartIndex <= currentEndIndex);
    }

    private void generateFilledPath(ILineDataSet dataSet, int startIndex, int endIndex, Path outputPath) {
        float fillMin = dataSet.getFillFormatter().getFillLinePosition(dataSet, this.mChart);
        float phaseY = this.mAnimator.getPhaseY();
        boolean isDrawSteppedEnabled = dataSet.getMode() == LineDataSet.Mode.STEPPED;
        Path filled = outputPath;
        filled.reset();
        Entry entry = dataSet.getEntryForIndex(startIndex);
        filled.moveTo(entry.getX(), fillMin);
        filled.lineTo(entry.getX(), entry.getY() * phaseY);
        Entry currentEntry = null;
        Entry previousEntry = entry;
        for (int x = startIndex + 1; x <= endIndex; x++) {
            currentEntry = dataSet.getEntryForIndex(x);
            if (isDrawSteppedEnabled) {
                filled.lineTo(currentEntry.getX(), previousEntry.getY() * phaseY);
            }
            filled.lineTo(currentEntry.getX(), currentEntry.getY() * phaseY);
            previousEntry = currentEntry;
        }
        if (currentEntry != null) {
            filled.lineTo(currentEntry.getX(), fillMin);
        }
        filled.close();
    }

    public void drawValues(Canvas c) {
        int valOffset;
        int valOffset2;
        Entry entry;
        if (isDrawingValuesAllowed(this.mChart)) {
            List<ILineDataSet> dataSets = this.mChart.getLineData().getDataSets();
            for (int i = 0; i < dataSets.size(); i++) {
                ILineDataSet dataSet = dataSets.get(i);
                if (shouldDrawValues(dataSet) && dataSet.getEntryCount() >= 1) {
                    applyValueTextStyle(dataSet);
                    Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
                    int valOffset3 = (int) (dataSet.getCircleRadius() * 1.75f);
                    if (!dataSet.isDrawCirclesEnabled()) {
                        valOffset = valOffset3 / 2;
                    } else {
                        valOffset = valOffset3;
                    }
                    this.mXBounds.set(this.mChart, dataSet);
                    float[] positions = trans.generateTransformedValuesLine(dataSet, this.mAnimator.getPhaseX(), this.mAnimator.getPhaseY(), this.mXBounds.min, this.mXBounds.max);
                    ValueFormatter formatter = dataSet.getValueFormatter();
                    MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
                    iconsOffset.f90x = Utils.convertDpToPixel(iconsOffset.f90x);
                    iconsOffset.f91y = Utils.convertDpToPixel(iconsOffset.f91y);
                    int j = 0;
                    while (true) {
                        if (j >= positions.length) {
                            break;
                        }
                        float x = positions[j];
                        float y = positions[j + 1];
                        if (!this.mViewPortHandler.isInBoundsRight(x)) {
                            int i2 = valOffset;
                            break;
                        }
                        if (!this.mViewPortHandler.isInBoundsLeft(x)) {
                            valOffset2 = valOffset;
                        } else if (!this.mViewPortHandler.isInBoundsY(y)) {
                            valOffset2 = valOffset;
                        } else {
                            Entry entry2 = dataSet.getEntryForIndex((j / 2) + this.mXBounds.min);
                            if (dataSet.isDrawValuesEnabled()) {
                                entry = entry2;
                                valOffset2 = valOffset;
                                drawValue(c, formatter.getPointLabel(entry2), x, y - ((float) valOffset), dataSet.getValueTextColor(j / 2));
                            } else {
                                entry = entry2;
                                valOffset2 = valOffset;
                            }
                            if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                                Drawable icon = entry.getIcon();
                                Utils.drawImage(c, icon, (int) (iconsOffset.f90x + x), (int) (iconsOffset.f91y + y), icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                            }
                        }
                        j += 2;
                        valOffset = valOffset2;
                    }
                    MPPointF.recycleInstance(iconsOffset);
                }
            }
        }
    }

    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        this.mValuePaint.setColor(color);
        c.drawText(valueText, x, y, this.mValuePaint);
    }

    public void drawExtras(Canvas c) {
        drawCircles(c);
    }

    /* access modifiers changed from: protected */
    public void drawCircles(Canvas c) {
        float phaseY;
        DataSetImageCache imageCache;
        List<ILineDataSet> dataSets;
        LineChartRenderer lineChartRenderer = this;
        lineChartRenderer.mRenderPaint.setStyle(Paint.Style.FILL);
        float phaseY2 = lineChartRenderer.mAnimator.getPhaseY();
        float[] fArr = lineChartRenderer.mCirclesBuffer;
        float f = 0.0f;
        char c2 = 0;
        fArr[0] = 0.0f;
        fArr[1] = 0.0f;
        List<ILineDataSet> dataSets2 = lineChartRenderer.mChart.getLineData().getDataSets();
        int i = 0;
        while (i < dataSets2.size()) {
            ILineDataSet dataSet = dataSets2.get(i);
            if (!dataSet.isVisible() || !dataSet.isDrawCirclesEnabled()) {
                Canvas canvas = c;
                phaseY = phaseY2;
            } else if (dataSet.getEntryCount() == 0) {
                Canvas canvas2 = c;
                phaseY = phaseY2;
            } else {
                lineChartRenderer.mCirclePaintInner.setColor(dataSet.getCircleHoleColor());
                Transformer trans = lineChartRenderer.mChart.getTransformer(dataSet.getAxisDependency());
                lineChartRenderer.mXBounds.set(lineChartRenderer.mChart, dataSet);
                float circleRadius = dataSet.getCircleRadius();
                float circleHoleRadius = dataSet.getCircleHoleRadius();
                boolean drawCircleHole = dataSet.isDrawCircleHoleEnabled() && circleHoleRadius < circleRadius && circleHoleRadius > f;
                boolean drawTransparentCircleHole = drawCircleHole && dataSet.getCircleHoleColor() == 1122867;
                if (lineChartRenderer.mImageCaches.containsKey(dataSet)) {
                    imageCache = lineChartRenderer.mImageCaches.get(dataSet);
                } else {
                    imageCache = new DataSetImageCache(lineChartRenderer, (C05781) null);
                    lineChartRenderer.mImageCaches.put(dataSet, imageCache);
                }
                if (imageCache.init(dataSet)) {
                    imageCache.fill(dataSet, drawCircleHole, drawTransparentCircleHole);
                }
                int boundsRangeCount = lineChartRenderer.mXBounds.range + lineChartRenderer.mXBounds.min;
                int j = lineChartRenderer.mXBounds.min;
                while (true) {
                    if (j > boundsRangeCount) {
                        Canvas canvas3 = c;
                        phaseY = phaseY2;
                        break;
                    }
                    Entry e = dataSet.getEntryForIndex(j);
                    if (e == null) {
                        Canvas canvas4 = c;
                        phaseY = phaseY2;
                        break;
                    }
                    lineChartRenderer.mCirclesBuffer[c2] = e.getX();
                    lineChartRenderer.mCirclesBuffer[1] = e.getY() * phaseY2;
                    trans.pointValuesToPixel(lineChartRenderer.mCirclesBuffer);
                    phaseY = phaseY2;
                    if (!lineChartRenderer.mViewPortHandler.isInBoundsRight(lineChartRenderer.mCirclesBuffer[c2])) {
                        Canvas canvas5 = c;
                        break;
                    }
                    if (!lineChartRenderer.mViewPortHandler.isInBoundsLeft(lineChartRenderer.mCirclesBuffer[c2])) {
                        Canvas canvas6 = c;
                        dataSets = dataSets2;
                    } else if (!lineChartRenderer.mViewPortHandler.isInBoundsY(lineChartRenderer.mCirclesBuffer[1])) {
                        Canvas canvas7 = c;
                        dataSets = dataSets2;
                    } else {
                        Bitmap circleBitmap = imageCache.getBitmap(j);
                        if (circleBitmap != null) {
                            float[] fArr2 = lineChartRenderer.mCirclesBuffer;
                            dataSets = dataSets2;
                            c.drawBitmap(circleBitmap, fArr2[c2] - circleRadius, fArr2[1] - circleRadius, (Paint) null);
                        } else {
                            Canvas canvas8 = c;
                            dataSets = dataSets2;
                        }
                    }
                    j++;
                    dataSets2 = dataSets;
                    phaseY2 = phaseY;
                    c2 = 0;
                    lineChartRenderer = this;
                }
            }
            i++;
            dataSets2 = dataSets2;
            phaseY2 = phaseY;
            f = 0.0f;
            c2 = 0;
            lineChartRenderer = this;
        }
    }

    public void drawHighlighted(Canvas c, Highlight[] indices) {
        LineData lineData = this.mChart.getLineData();
        for (Highlight high : indices) {
            ILineDataSet set = (ILineDataSet) lineData.getDataSetByIndex(high.getDataSetIndex());
            if (set != null && set.isHighlightEnabled()) {
                Entry e = set.getEntryForXValue(high.getX(), high.getY());
                if (isInBoundsX(e, set)) {
                    MPPointD pix = this.mChart.getTransformer(set.getAxisDependency()).getPixelForValues(e.getX(), e.getY() * this.mAnimator.getPhaseY());
                    high.setDraw((float) pix.f88x, (float) pix.f89y);
                    drawHighlightLines(c, (float) pix.f88x, (float) pix.f89y, set);
                }
            }
        }
    }

    public void setBitmapConfig(Bitmap.Config config) {
        this.mBitmapConfig = config;
        releaseBitmap();
    }

    public Bitmap.Config getBitmapConfig() {
        return this.mBitmapConfig;
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

    private class DataSetImageCache {
        private Bitmap[] circleBitmaps;
        private Path mCirclePathBuffer;

        private DataSetImageCache() {
            this.mCirclePathBuffer = new Path();
        }

        /* synthetic */ DataSetImageCache(LineChartRenderer x0, C05781 x1) {
            this();
        }

        /* access modifiers changed from: protected */
        public boolean init(ILineDataSet set) {
            int size = set.getCircleColorCount();
            Bitmap[] bitmapArr = this.circleBitmaps;
            if (bitmapArr == null) {
                this.circleBitmaps = new Bitmap[size];
                return true;
            } else if (bitmapArr.length == size) {
                return false;
            } else {
                this.circleBitmaps = new Bitmap[size];
                return true;
            }
        }

        /* access modifiers changed from: protected */
        public void fill(ILineDataSet set, boolean drawCircleHole, boolean drawTransparentCircleHole) {
            int colorCount = set.getCircleColorCount();
            float circleRadius = set.getCircleRadius();
            float circleHoleRadius = set.getCircleHoleRadius();
            for (int i = 0; i < colorCount; i++) {
                Bitmap.Config conf = Bitmap.Config.ARGB_4444;
                double d = (double) circleRadius;
                Double.isNaN(d);
                double d2 = (double) circleRadius;
                Double.isNaN(d2);
                Bitmap circleBitmap = Bitmap.createBitmap((int) (d * 2.1d), (int) (d2 * 2.1d), conf);
                Canvas canvas = new Canvas(circleBitmap);
                this.circleBitmaps[i] = circleBitmap;
                LineChartRenderer.this.mRenderPaint.setColor(set.getCircleColor(i));
                if (drawTransparentCircleHole) {
                    this.mCirclePathBuffer.reset();
                    this.mCirclePathBuffer.addCircle(circleRadius, circleRadius, circleRadius, Path.Direction.CW);
                    this.mCirclePathBuffer.addCircle(circleRadius, circleRadius, circleHoleRadius, Path.Direction.CCW);
                    canvas.drawPath(this.mCirclePathBuffer, LineChartRenderer.this.mRenderPaint);
                } else {
                    canvas.drawCircle(circleRadius, circleRadius, circleRadius, LineChartRenderer.this.mRenderPaint);
                    if (drawCircleHole) {
                        canvas.drawCircle(circleRadius, circleRadius, circleHoleRadius, LineChartRenderer.this.mCirclePaintInner);
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public Bitmap getBitmap(int index) {
            Bitmap[] bitmapArr = this.circleBitmaps;
            return bitmapArr[index % bitmapArr.length];
        }
    }
}
