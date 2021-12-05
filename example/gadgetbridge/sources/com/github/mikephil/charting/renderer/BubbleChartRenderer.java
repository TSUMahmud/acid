package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;

public class BubbleChartRenderer extends BarLineScatterCandleBubbleRenderer {
    private float[] _hsvBuffer = new float[3];
    protected BubbleDataProvider mChart;
    private float[] pointBuffer = new float[2];
    private float[] sizeBuffer = new float[4];

    public BubbleChartRenderer(BubbleDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
        this.mRenderPaint.setStyle(Paint.Style.FILL);
        this.mHighlightPaint.setStyle(Paint.Style.STROKE);
        this.mHighlightPaint.setStrokeWidth(Utils.convertDpToPixel(1.5f));
    }

    public void initBuffers() {
    }

    public void drawData(Canvas c) {
        for (IBubbleDataSet set : this.mChart.getBubbleData().getDataSets()) {
            if (set.isVisible()) {
                drawDataSet(c, set);
            }
        }
    }

    /* access modifiers changed from: protected */
    public float getShapeSize(float entrySize, float maxSize, float reference, boolean normalizeSize) {
        return reference * (normalizeSize ? maxSize == 0.0f ? 1.0f : (float) Math.sqrt((double) (entrySize / maxSize)) : entrySize);
    }

    /* access modifiers changed from: protected */
    public void drawDataSet(Canvas c, IBubbleDataSet dataSet) {
        IBubbleDataSet iBubbleDataSet = dataSet;
        char c2 = 1;
        if (dataSet.getEntryCount() >= 1) {
            Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
            float phaseY = this.mAnimator.getPhaseY();
            this.mXBounds.set(this.mChart, iBubbleDataSet);
            float[] fArr = this.sizeBuffer;
            char c3 = 0;
            fArr[0] = 0.0f;
            fArr[2] = 1.0f;
            trans.pointValuesToPixel(fArr);
            boolean normalizeSize = dataSet.isNormalizeSizeEnabled();
            float[] fArr2 = this.sizeBuffer;
            float referenceSize = Math.min(Math.abs(this.mViewPortHandler.contentBottom() - this.mViewPortHandler.contentTop()), Math.abs(fArr2[2] - fArr2[0]));
            int j = this.mXBounds.min;
            while (j <= this.mXBounds.range + this.mXBounds.min) {
                BubbleEntry entry = (BubbleEntry) iBubbleDataSet.getEntryForIndex(j);
                this.pointBuffer[c3] = entry.getX();
                this.pointBuffer[c2] = entry.getY() * phaseY;
                trans.pointValuesToPixel(this.pointBuffer);
                float shapeHalf = getShapeSize(entry.getSize(), dataSet.getMaxSize(), referenceSize, normalizeSize) / 2.0f;
                if (!this.mViewPortHandler.isInBoundsTop(this.pointBuffer[c2] + shapeHalf)) {
                    Canvas canvas = c;
                } else if (!this.mViewPortHandler.isInBoundsBottom(this.pointBuffer[c2] - shapeHalf)) {
                    Canvas canvas2 = c;
                } else if (!this.mViewPortHandler.isInBoundsLeft(this.pointBuffer[c3] + shapeHalf)) {
                    Canvas canvas3 = c;
                } else if (!this.mViewPortHandler.isInBoundsRight(this.pointBuffer[c3] - shapeHalf)) {
                    Canvas canvas4 = c;
                    return;
                } else {
                    this.mRenderPaint.setColor(iBubbleDataSet.getColor((int) entry.getX()));
                    float[] fArr3 = this.pointBuffer;
                    c.drawCircle(fArr3[c3], fArr3[c2], shapeHalf, this.mRenderPaint);
                }
                j++;
                c2 = 1;
                c3 = 0;
            }
            Canvas canvas5 = c;
        }
    }

    public void drawValues(Canvas c) {
        BubbleData bubbleData;
        MPPointF iconsOffset;
        ValueFormatter formatter;
        int j;
        MPPointF iconsOffset2;
        float y;
        BubbleEntry entry;
        float x;
        BubbleChartRenderer bubbleChartRenderer = this;
        BubbleData bubbleData2 = bubbleChartRenderer.mChart.getBubbleData();
        if (bubbleData2 != null) {
            if (bubbleChartRenderer.isDrawingValuesAllowed(bubbleChartRenderer.mChart)) {
                List<IBubbleDataSet> dataSets = bubbleData2.getDataSets();
                float lineHeight = (float) Utils.calcTextHeight(bubbleChartRenderer.mValuePaint, MiBandConst.MI_1);
                int i = 0;
                while (i < dataSets.size()) {
                    IBubbleDataSet dataSet = dataSets.get(i);
                    if (!bubbleChartRenderer.shouldDrawValues(dataSet)) {
                        bubbleData = bubbleData2;
                    } else if (dataSet.getEntryCount() < 1) {
                        bubbleData = bubbleData2;
                    } else {
                        bubbleChartRenderer.applyValueTextStyle(dataSet);
                        float phaseX = Math.max(0.0f, Math.min(1.0f, bubbleChartRenderer.mAnimator.getPhaseX()));
                        float phaseY = bubbleChartRenderer.mAnimator.getPhaseY();
                        bubbleChartRenderer.mXBounds.set(bubbleChartRenderer.mChart, dataSet);
                        float[] positions = bubbleChartRenderer.mChart.getTransformer(dataSet.getAxisDependency()).generateTransformedValuesBubble(dataSet, phaseY, bubbleChartRenderer.mXBounds.min, bubbleChartRenderer.mXBounds.max);
                        float alpha = phaseX == 1.0f ? phaseY : phaseX;
                        ValueFormatter formatter2 = dataSet.getValueFormatter();
                        MPPointF iconsOffset3 = MPPointF.getInstance(dataSet.getIconsOffset());
                        iconsOffset3.f90x = Utils.convertDpToPixel(iconsOffset3.f90x);
                        iconsOffset3.f91y = Utils.convertDpToPixel(iconsOffset3.f91y);
                        int j2 = 0;
                        while (true) {
                            if (j2 >= positions.length) {
                                int i2 = j2;
                                iconsOffset = iconsOffset3;
                                ValueFormatter valueFormatter = formatter2;
                                bubbleData = bubbleData2;
                                break;
                            }
                            int valueTextColor = dataSet.getValueTextColor((j2 / 2) + bubbleChartRenderer.mXBounds.min);
                            MPPointF iconsOffset4 = iconsOffset3;
                            bubbleData = bubbleData2;
                            int valueTextColor2 = Color.argb(Math.round(255.0f * alpha), Color.red(valueTextColor), Color.green(valueTextColor), Color.blue(valueTextColor));
                            float x2 = positions[j2];
                            float y2 = positions[j2 + 1];
                            if (!bubbleChartRenderer.mViewPortHandler.isInBoundsRight(x2)) {
                                ValueFormatter valueFormatter2 = formatter2;
                                iconsOffset = iconsOffset4;
                                break;
                            }
                            if (!bubbleChartRenderer.mViewPortHandler.isInBoundsLeft(x2)) {
                                j = j2;
                                formatter = formatter2;
                                iconsOffset2 = iconsOffset4;
                                float f = x2;
                            } else if (!bubbleChartRenderer.mViewPortHandler.isInBoundsY(y2)) {
                                j = j2;
                                formatter = formatter2;
                                iconsOffset2 = iconsOffset4;
                            } else {
                                BubbleEntry entry2 = (BubbleEntry) dataSet.getEntryForIndex((j2 / 2) + bubbleChartRenderer.mXBounds.min);
                                if (dataSet.isDrawValuesEnabled()) {
                                    entry = entry2;
                                    y = y2;
                                    j = j2;
                                    float f2 = x2;
                                    iconsOffset2 = iconsOffset4;
                                    x = x2;
                                    float x3 = y2 + (0.5f * lineHeight);
                                    formatter = formatter2;
                                    drawValue(c, formatter2.getBubbleLabel(entry2), f2, x3, valueTextColor2);
                                } else {
                                    entry = entry2;
                                    y = y2;
                                    j = j2;
                                    formatter = formatter2;
                                    iconsOffset2 = iconsOffset4;
                                    x = x2;
                                }
                                if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                                    Drawable icon = entry.getIcon();
                                    Utils.drawImage(c, icon, (int) (x + iconsOffset2.f90x), (int) (y + iconsOffset2.f91y), icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                                }
                            }
                            j2 = j + 2;
                            iconsOffset3 = iconsOffset2;
                            bubbleData2 = bubbleData;
                            formatter2 = formatter;
                            bubbleChartRenderer = this;
                        }
                        MPPointF.recycleInstance(iconsOffset);
                    }
                    i++;
                    bubbleChartRenderer = this;
                    bubbleData2 = bubbleData;
                }
                return;
            }
        }
    }

    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        this.mValuePaint.setColor(color);
        c.drawText(valueText, x, y, this.mValuePaint);
    }

    public void drawExtras(Canvas c) {
    }

    public void drawHighlighted(Canvas c, Highlight[] indices) {
        int i;
        float phaseY;
        BubbleData bubbleData;
        BubbleChartRenderer bubbleChartRenderer = this;
        Highlight[] highlightArr = indices;
        BubbleData bubbleData2 = bubbleChartRenderer.mChart.getBubbleData();
        float phaseY2 = bubbleChartRenderer.mAnimator.getPhaseY();
        int length = highlightArr.length;
        char c2 = 0;
        int i2 = 0;
        while (i2 < length) {
            Highlight high = highlightArr[i2];
            IBubbleDataSet set = (IBubbleDataSet) bubbleData2.getDataSetByIndex(high.getDataSetIndex());
            if (set == null) {
                Canvas canvas = c;
                bubbleData = bubbleData2;
                phaseY = phaseY2;
                i = length;
            } else if (!set.isHighlightEnabled()) {
                Canvas canvas2 = c;
                bubbleData = bubbleData2;
                phaseY = phaseY2;
                i = length;
            } else {
                BubbleEntry entry = (BubbleEntry) set.getEntryForXValue(high.getX(), high.getY());
                if (entry.getY() != high.getY()) {
                    Canvas canvas3 = c;
                    bubbleData = bubbleData2;
                    phaseY = phaseY2;
                    i = length;
                } else if (!bubbleChartRenderer.isInBoundsX(entry, set)) {
                    Canvas canvas4 = c;
                    bubbleData = bubbleData2;
                    phaseY = phaseY2;
                    i = length;
                } else {
                    Transformer trans = bubbleChartRenderer.mChart.getTransformer(set.getAxisDependency());
                    float[] fArr = bubbleChartRenderer.sizeBuffer;
                    fArr[c2] = 0.0f;
                    fArr[2] = 1.0f;
                    trans.pointValuesToPixel(fArr);
                    boolean normalizeSize = set.isNormalizeSizeEnabled();
                    float[] fArr2 = bubbleChartRenderer.sizeBuffer;
                    float referenceSize = Math.min(Math.abs(bubbleChartRenderer.mViewPortHandler.contentBottom() - bubbleChartRenderer.mViewPortHandler.contentTop()), Math.abs(fArr2[2] - fArr2[c2]));
                    bubbleChartRenderer.pointBuffer[c2] = entry.getX();
                    bubbleChartRenderer.pointBuffer[1] = entry.getY() * phaseY2;
                    trans.pointValuesToPixel(bubbleChartRenderer.pointBuffer);
                    float[] fArr3 = bubbleChartRenderer.pointBuffer;
                    high.setDraw(fArr3[c2], fArr3[1]);
                    float shapeHalf = bubbleChartRenderer.getShapeSize(entry.getSize(), set.getMaxSize(), referenceSize, normalizeSize) / 2.0f;
                    if (!bubbleChartRenderer.mViewPortHandler.isInBoundsTop(bubbleChartRenderer.pointBuffer[1] + shapeHalf)) {
                        Canvas canvas5 = c;
                        bubbleData = bubbleData2;
                        phaseY = phaseY2;
                        i = length;
                    } else if (!bubbleChartRenderer.mViewPortHandler.isInBoundsBottom(bubbleChartRenderer.pointBuffer[1] - shapeHalf)) {
                        Canvas canvas6 = c;
                        bubbleData = bubbleData2;
                        phaseY = phaseY2;
                        i = length;
                    } else if (!bubbleChartRenderer.mViewPortHandler.isInBoundsLeft(bubbleChartRenderer.pointBuffer[0] + shapeHalf)) {
                        Canvas canvas7 = c;
                        bubbleData = bubbleData2;
                        phaseY = phaseY2;
                        i = length;
                    } else if (!bubbleChartRenderer.mViewPortHandler.isInBoundsRight(bubbleChartRenderer.pointBuffer[0] - shapeHalf)) {
                        Canvas canvas8 = c;
                        BubbleData bubbleData3 = bubbleData2;
                        float f = phaseY2;
                        return;
                    } else {
                        int originalColor = set.getColor((int) entry.getX());
                        bubbleData = bubbleData2;
                        phaseY = phaseY2;
                        i = length;
                        Color.RGBToHSV(Color.red(originalColor), Color.green(originalColor), Color.blue(originalColor), bubbleChartRenderer._hsvBuffer);
                        float[] fArr4 = bubbleChartRenderer._hsvBuffer;
                        fArr4[2] = fArr4[2] * 0.5f;
                        bubbleChartRenderer.mHighlightPaint.setColor(Color.HSVToColor(Color.alpha(originalColor), bubbleChartRenderer._hsvBuffer));
                        bubbleChartRenderer.mHighlightPaint.setStrokeWidth(set.getHighlightCircleWidth());
                        float[] fArr5 = bubbleChartRenderer.pointBuffer;
                        c.drawCircle(fArr5[0], fArr5[1], shapeHalf, bubbleChartRenderer.mHighlightPaint);
                    }
                }
            }
            i2++;
            bubbleChartRenderer = this;
            highlightArr = indices;
            bubbleData2 = bubbleData;
            phaseY2 = phaseY;
            length = i;
            c2 = 0;
        }
        Canvas canvas9 = c;
        BubbleData bubbleData4 = bubbleData2;
        float f2 = phaseY2;
    }
}
