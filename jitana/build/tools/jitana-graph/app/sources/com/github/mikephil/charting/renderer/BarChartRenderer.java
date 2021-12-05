package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.List;

public class BarChartRenderer extends BarLineScatterCandleBubbleRenderer {
    protected Paint mBarBorderPaint;
    protected BarBuffer[] mBarBuffers;
    protected RectF mBarRect = new RectF();
    private RectF mBarShadowRectBuffer = new RectF();
    protected BarDataProvider mChart;
    protected Paint mShadowPaint;

    public BarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
        this.mHighlightPaint = new Paint(1);
        this.mHighlightPaint.setStyle(Paint.Style.FILL);
        this.mHighlightPaint.setColor(Color.rgb(0, 0, 0));
        this.mHighlightPaint.setAlpha(120);
        this.mShadowPaint = new Paint(1);
        this.mShadowPaint.setStyle(Paint.Style.FILL);
        this.mBarBorderPaint = new Paint(1);
        this.mBarBorderPaint.setStyle(Paint.Style.STROKE);
    }

    public void initBuffers() {
        BarData barData = this.mChart.getBarData();
        this.mBarBuffers = new BarBuffer[barData.getDataSetCount()];
        for (int i = 0; i < this.mBarBuffers.length; i++) {
            IBarDataSet set = (IBarDataSet) barData.getDataSetByIndex(i);
            this.mBarBuffers[i] = new BarBuffer(set.getEntryCount() * 4 * (set.isStacked() ? set.getStackSize() : 1), barData.getDataSetCount(), set.isStacked());
        }
    }

    public void drawData(Canvas c) {
        BarData barData = this.mChart.getBarData();
        for (int i = 0; i < barData.getDataSetCount(); i++) {
            IBarDataSet set = (IBarDataSet) barData.getDataSetByIndex(i);
            if (set.isVisible()) {
                drawDataSet(c, set, i);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        BarData barData;
        IBarDataSet iBarDataSet = dataSet;
        int i = index;
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        this.mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        this.mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));
        boolean drawBorder = dataSet.getBarBorderWidth() > 0.0f;
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        if (this.mChart.isDrawBarShadowEnabled()) {
            this.mShadowPaint.setColor(dataSet.getBarShadowColor());
            BarData barData2 = this.mChart.getBarData();
            float barWidthHalf = barData2.getBarWidth() / 2.0f;
            int i2 = 0;
            int count = Math.min((int) Math.ceil((double) (((float) dataSet.getEntryCount()) * phaseX)), dataSet.getEntryCount());
            while (true) {
                if (i2 >= count) {
                    Canvas canvas = c;
                    break;
                }
                float x = ((BarEntry) iBarDataSet.getEntryForIndex(i2)).getX();
                RectF rectF = this.mBarShadowRectBuffer;
                rectF.left = x - barWidthHalf;
                rectF.right = x + barWidthHalf;
                trans.rectValueToPixel(rectF);
                if (!this.mViewPortHandler.isInBoundsLeft(this.mBarShadowRectBuffer.right)) {
                    barData = barData2;
                    Canvas canvas2 = c;
                } else if (!this.mViewPortHandler.isInBoundsRight(this.mBarShadowRectBuffer.left)) {
                    Canvas canvas3 = c;
                    break;
                } else {
                    this.mBarShadowRectBuffer.top = this.mViewPortHandler.contentTop();
                    this.mBarShadowRectBuffer.bottom = this.mViewPortHandler.contentBottom();
                    barData = barData2;
                    c.drawRect(this.mBarShadowRectBuffer, this.mShadowPaint);
                }
                i2++;
                barData2 = barData;
            }
        } else {
            Canvas canvas4 = c;
        }
        BarBuffer buffer = this.mBarBuffers[i];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(i);
        buffer.setInverted(this.mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(this.mChart.getBarData().getBarWidth());
        buffer.feed(iBarDataSet);
        trans.pointValuesToPixel(buffer.buffer);
        boolean isSingleColor = dataSet.getColors().size() == 1;
        if (isSingleColor) {
            this.mRenderPaint.setColor(dataSet.getColor());
        }
        int j = 0;
        while (j < buffer.size()) {
            if (this.mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                if (this.mViewPortHandler.isInBoundsRight(buffer.buffer[j])) {
                    if (!isSingleColor) {
                        this.mRenderPaint.setColor(iBarDataSet.getColor(j / 4));
                    }
                    if (dataSet.getGradientColor() != null) {
                        GradientColor gradientColor = dataSet.getGradientColor();
                        Paint paint = this.mRenderPaint;
                        LinearGradient linearGradient = r13;
                        LinearGradient linearGradient2 = new LinearGradient(buffer.buffer[j], buffer.buffer[j + 3], buffer.buffer[j], buffer.buffer[j + 1], gradientColor.getStartColor(), gradientColor.getEndColor(), Shader.TileMode.MIRROR);
                        paint.setShader(linearGradient);
                    }
                    if (dataSet.getGradientColors() != null) {
                        Paint paint2 = this.mRenderPaint;
                        float f = buffer.buffer[j];
                        float f2 = buffer.buffer[j + 3];
                        float f3 = buffer.buffer[j];
                        float f4 = buffer.buffer[j + 1];
                        int startColor = iBarDataSet.getGradientColor(j / 4).getStartColor();
                        int endColor = iBarDataSet.getGradientColor(j / 4).getEndColor();
                        LinearGradient linearGradient3 = r11;
                        LinearGradient linearGradient4 = new LinearGradient(f, f2, f3, f4, startColor, endColor, Shader.TileMode.MIRROR);
                        paint2.setShader(linearGradient3);
                    }
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3], this.mRenderPaint);
                    if (drawBorder) {
                        c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3], this.mBarBorderPaint);
                    }
                } else {
                    return;
                }
            }
            j += 4;
            iBarDataSet = dataSet;
            int i3 = index;
        }
    }

    /* access modifiers changed from: protected */
    public void prepareBarHighlight(float x, float y1, float y2, float barWidthHalf, Transformer trans) {
        RectF rectF = this.mBarRect;
        rectF.set(x - barWidthHalf, y1, x + barWidthHalf, y2);
        trans.rectToPixelPhase(this.mBarRect, this.mAnimator.getPhaseY());
    }

    public void drawValues(Canvas c) {
        float valueOffsetPlus;
        boolean drawValueAboveBar;
        List<IBarDataSet> dataSets;
        float negOffset;
        float posOffset;
        MPPointF iconsOffset;
        int index;
        boolean isInverted;
        float valueTextHeight;
        Transformer trans;
        float[] vals;
        float[] transformed;
        int k;
        float x;
        BarEntry entry;
        float y;
        float y2;
        int index2;
        boolean isInverted2;
        float valueTextHeight2;
        float x2;
        BarEntry entry2;
        float valueOffsetPlus2;
        int j;
        boolean drawValueAboveBar2;
        List<IBarDataSet> dataSets2;
        BarBuffer buffer;
        ValueFormatter formatter;
        MPPointF iconsOffset2;
        BarEntry entry3;
        float x3;
        if (isDrawingValuesAllowed(this.mChart)) {
            List<IBarDataSet> dataSets3 = this.mChart.getBarData().getDataSets();
            float valueOffsetPlus3 = Utils.convertDpToPixel(4.5f);
            boolean drawValueAboveBar3 = this.mChart.isDrawValueAboveBarEnabled();
            int i = 0;
            while (i < this.mChart.getBarData().getDataSetCount()) {
                IBarDataSet dataSet = dataSets3.get(i);
                if (!shouldDrawValues(dataSet)) {
                    dataSets = dataSets3;
                    valueOffsetPlus = valueOffsetPlus3;
                    drawValueAboveBar = drawValueAboveBar3;
                } else {
                    applyValueTextStyle(dataSet);
                    boolean isInverted3 = this.mChart.isInverted(dataSet.getAxisDependency());
                    float valueTextHeight3 = (float) Utils.calcTextHeight(this.mValuePaint, "8");
                    float posOffset2 = drawValueAboveBar3 ? -valueOffsetPlus3 : valueTextHeight3 + valueOffsetPlus3;
                    float negOffset2 = drawValueAboveBar3 ? valueTextHeight3 + valueOffsetPlus3 : -valueOffsetPlus3;
                    if (isInverted3) {
                        posOffset = (-posOffset2) - valueTextHeight3;
                        negOffset = (-negOffset2) - valueTextHeight3;
                    } else {
                        posOffset = posOffset2;
                        negOffset = negOffset2;
                    }
                    BarBuffer buffer2 = this.mBarBuffers[i];
                    float phaseY = this.mAnimator.getPhaseY();
                    ValueFormatter formatter2 = dataSet.getValueFormatter();
                    MPPointF iconsOffset3 = MPPointF.getInstance(dataSet.getIconsOffset());
                    iconsOffset3.f90x = Utils.convertDpToPixel(iconsOffset3.f90x);
                    iconsOffset3.f91y = Utils.convertDpToPixel(iconsOffset3.f91y);
                    if (dataSet.isStacked()) {
                        dataSets = dataSets3;
                        valueOffsetPlus = valueOffsetPlus3;
                        drawValueAboveBar = drawValueAboveBar3;
                        iconsOffset = iconsOffset3;
                        ValueFormatter formatter3 = formatter2;
                        BarBuffer buffer3 = buffer2;
                        Transformer trans2 = this.mChart.getTransformer(dataSet.getAxisDependency());
                        int bufferIndex = 0;
                        int index3 = 0;
                        while (true) {
                            if (((float) index3) >= ((float) dataSet.getEntryCount()) * this.mAnimator.getPhaseX()) {
                                int i2 = index3;
                                boolean z = isInverted3;
                                float f = valueTextHeight3;
                                Transformer transformer = trans2;
                                break;
                            }
                            BarEntry entry4 = (BarEntry) dataSet.getEntryForIndex(index3);
                            float[] vals2 = entry4.getYVals();
                            float x4 = (buffer3.buffer[bufferIndex] + buffer3.buffer[bufferIndex + 2]) / 2.0f;
                            int color = dataSet.getValueTextColor(index3);
                            if (vals2 != null) {
                                float x5 = x4;
                                BarEntry entry5 = entry4;
                                index = index3;
                                isInverted = isInverted3;
                                valueTextHeight = valueTextHeight3;
                                vals = vals2;
                                trans = trans2;
                                float[] transformed2 = new float[(vals.length * 2)];
                                int k2 = 0;
                                int idx = 0;
                                float posY = 0.0f;
                                float negY = -entry5.getNegativeSum();
                                while (k2 < transformed2.length) {
                                    float value = vals[idx];
                                    if (value == 0.0f && (posY == 0.0f || negY == 0.0f)) {
                                        y2 = value;
                                    } else if (value >= 0.0f) {
                                        posY += value;
                                        y2 = posY;
                                    } else {
                                        y2 = negY;
                                        negY -= value;
                                    }
                                    transformed2[k2 + 1] = y2 * phaseY;
                                    k2 += 2;
                                    idx++;
                                }
                                trans.pointValuesToPixel(transformed2);
                                int k3 = 0;
                                while (true) {
                                    if (k3 >= transformed2.length) {
                                        int i3 = k3;
                                        float[] fArr = transformed2;
                                        float f2 = x5;
                                        BarEntry barEntry = entry5;
                                        break;
                                    }
                                    float val = vals[k3 / 2];
                                    float y3 = transformed2[k3 + 1] + (((val > 0.0f ? 1 : (val == 0.0f ? 0 : -1)) == 0 && (negY > 0.0f ? 1 : (negY == 0.0f ? 0 : -1)) == 0 && (posY > 0.0f ? 1 : (posY == 0.0f ? 0 : -1)) > 0) || (val > 0.0f ? 1 : (val == 0.0f ? 0 : -1)) < 0 ? negOffset : posOffset);
                                    float x6 = x5;
                                    if (!this.mViewPortHandler.isInBoundsRight(x6)) {
                                        float f3 = x6;
                                        BarEntry barEntry2 = entry5;
                                        break;
                                    }
                                    if (!this.mViewPortHandler.isInBoundsY(y3)) {
                                        x = x6;
                                        float f4 = y3;
                                        k = k3;
                                        transformed = transformed2;
                                        entry = entry5;
                                        float f5 = val;
                                    } else if (!this.mViewPortHandler.isInBoundsLeft(x6)) {
                                        x = x6;
                                        k = k3;
                                        transformed = transformed2;
                                        entry = entry5;
                                    } else {
                                        if (dataSet.isDrawValuesEnabled()) {
                                            BarEntry entry6 = entry5;
                                            entry = entry6;
                                            x = x6;
                                            y = y3;
                                            float f6 = val;
                                            k = k3;
                                            transformed = transformed2;
                                            drawValue(c, formatter3.getBarStackedLabel(val, entry6), x, y, color);
                                        } else {
                                            x = x6;
                                            y = y3;
                                            k = k3;
                                            transformed = transformed2;
                                            entry = entry5;
                                            float f7 = val;
                                        }
                                        if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                                            Drawable icon = entry.getIcon();
                                            Utils.drawImage(c, icon, (int) (x + iconsOffset.f90x), (int) (y + iconsOffset.f91y), icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                                        }
                                    }
                                    k3 = k + 2;
                                    entry5 = entry;
                                    x5 = x;
                                    transformed2 = transformed;
                                }
                            } else if (!this.mViewPortHandler.isInBoundsRight(x4)) {
                                boolean z2 = isInverted3;
                                float f8 = valueTextHeight3;
                                break;
                            } else {
                                float[] vals3 = vals2;
                                if (!this.mViewPortHandler.isInBoundsY(buffer3.buffer[bufferIndex + 1])) {
                                    index2 = index3;
                                    isInverted2 = isInverted3;
                                    valueTextHeight2 = valueTextHeight3;
                                    float[] fArr2 = vals3;
                                    BarEntry barEntry3 = entry4;
                                } else if (!this.mViewPortHandler.isInBoundsLeft(x4)) {
                                    index2 = index3;
                                    isInverted2 = isInverted3;
                                    valueTextHeight2 = valueTextHeight3;
                                } else {
                                    if (dataSet.isDrawValuesEnabled()) {
                                        x2 = x4;
                                        isInverted = isInverted3;
                                        vals = vals3;
                                        entry2 = entry4;
                                        index = index3;
                                        valueTextHeight = valueTextHeight3;
                                        trans = trans2;
                                        drawValue(c, formatter3.getBarLabel(entry4), x2, buffer3.buffer[bufferIndex + 1] + (entry4.getY() >= 0.0f ? posOffset : negOffset), color);
                                    } else {
                                        x2 = x4;
                                        index = index3;
                                        isInverted = isInverted3;
                                        valueTextHeight = valueTextHeight3;
                                        vals = vals3;
                                        entry2 = entry4;
                                        trans = trans2;
                                    }
                                    if (entry2.getIcon() == null || !dataSet.isDrawIconsEnabled()) {
                                        float f9 = x2;
                                        BarEntry barEntry4 = entry2;
                                    } else {
                                        Drawable icon2 = entry2.getIcon();
                                        Utils.drawImage(c, icon2, (int) (x2 + iconsOffset.f90x), (int) (buffer3.buffer[bufferIndex + 1] + (entry2.getY() >= 0.0f ? posOffset : negOffset) + iconsOffset.f91y), icon2.getIntrinsicWidth(), icon2.getIntrinsicHeight());
                                        float f10 = x2;
                                        BarEntry barEntry5 = entry2;
                                    }
                                }
                                trans2 = trans2;
                                valueTextHeight3 = valueTextHeight2;
                                isInverted3 = isInverted2;
                                index3 = index2;
                            }
                            bufferIndex = vals == null ? bufferIndex + 4 : bufferIndex + (vals.length * 4);
                            index3 = index + 1;
                            trans2 = trans;
                            valueTextHeight3 = valueTextHeight;
                            isInverted3 = isInverted;
                        }
                    } else {
                        int j2 = 0;
                        while (true) {
                            MPPointF iconsOffset4 = iconsOffset3;
                            if (((float) j2) >= ((float) buffer2.buffer.length) * this.mAnimator.getPhaseX()) {
                                int i4 = j2;
                                dataSets = dataSets3;
                                valueOffsetPlus = valueOffsetPlus3;
                                drawValueAboveBar = drawValueAboveBar3;
                                iconsOffset = iconsOffset4;
                                ValueFormatter valueFormatter = formatter2;
                                BarBuffer barBuffer = buffer2;
                                break;
                            }
                            float x7 = (buffer2.buffer[j2] + buffer2.buffer[j2 + 2]) / 2.0f;
                            if (!this.mViewPortHandler.isInBoundsRight(x7)) {
                                dataSets = dataSets3;
                                valueOffsetPlus = valueOffsetPlus3;
                                drawValueAboveBar = drawValueAboveBar3;
                                iconsOffset = iconsOffset4;
                                ValueFormatter valueFormatter2 = formatter2;
                                BarBuffer barBuffer2 = buffer2;
                                break;
                            }
                            if (!this.mViewPortHandler.isInBoundsY(buffer2.buffer[j2 + 1])) {
                                j = j2;
                                dataSets2 = dataSets3;
                                valueOffsetPlus2 = valueOffsetPlus3;
                                drawValueAboveBar2 = drawValueAboveBar3;
                                iconsOffset2 = iconsOffset4;
                                float f11 = x7;
                                formatter = formatter2;
                                buffer = buffer2;
                            } else if (!this.mViewPortHandler.isInBoundsLeft(x7)) {
                                j = j2;
                                dataSets2 = dataSets3;
                                valueOffsetPlus2 = valueOffsetPlus3;
                                drawValueAboveBar2 = drawValueAboveBar3;
                                iconsOffset2 = iconsOffset4;
                                formatter = formatter2;
                                buffer = buffer2;
                            } else {
                                BarEntry entry7 = (BarEntry) dataSet.getEntryForIndex(j2 / 4);
                                float val2 = entry7.getY();
                                if (dataSet.isDrawValuesEnabled()) {
                                    String barLabel = formatter2.getBarLabel(entry7);
                                    float f12 = val2 >= 0.0f ? buffer2.buffer[j2 + 1] + posOffset : buffer2.buffer[j2 + 3] + negOffset;
                                    int valueTextColor = dataSet.getValueTextColor(j2 / 4);
                                    entry3 = entry7;
                                    j = j2;
                                    String str = barLabel;
                                    dataSets2 = dataSets3;
                                    iconsOffset2 = iconsOffset4;
                                    x3 = x7;
                                    valueOffsetPlus2 = valueOffsetPlus3;
                                    formatter = formatter2;
                                    drawValueAboveBar2 = drawValueAboveBar3;
                                    buffer = buffer2;
                                    drawValue(c, str, x7, f12, valueTextColor);
                                } else {
                                    entry3 = entry7;
                                    j = j2;
                                    dataSets2 = dataSets3;
                                    valueOffsetPlus2 = valueOffsetPlus3;
                                    drawValueAboveBar2 = drawValueAboveBar3;
                                    iconsOffset2 = iconsOffset4;
                                    x3 = x7;
                                    formatter = formatter2;
                                    buffer = buffer2;
                                }
                                if (entry3.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                                    Drawable icon3 = entry3.getIcon();
                                    Utils.drawImage(c, icon3, (int) (x3 + iconsOffset2.f90x), (int) ((val2 >= 0.0f ? buffer.buffer[j + 1] + posOffset : buffer.buffer[j + 3] + negOffset) + iconsOffset2.f91y), icon3.getIntrinsicWidth(), icon3.getIntrinsicHeight());
                                }
                            }
                            j2 = j + 4;
                            iconsOffset3 = iconsOffset2;
                            formatter2 = formatter;
                            buffer2 = buffer;
                            dataSets3 = dataSets2;
                            drawValueAboveBar3 = drawValueAboveBar2;
                            valueOffsetPlus3 = valueOffsetPlus2;
                        }
                        boolean z3 = isInverted3;
                        float f13 = valueTextHeight3;
                    }
                    MPPointF.recycleInstance(iconsOffset);
                    float f14 = posOffset;
                    float f15 = negOffset;
                }
                i++;
                dataSets3 = dataSets;
                drawValueAboveBar3 = drawValueAboveBar;
                valueOffsetPlus3 = valueOffsetPlus;
            }
            float f16 = valueOffsetPlus3;
            boolean z4 = drawValueAboveBar3;
        }
    }

    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        this.mValuePaint.setColor(color);
        c.drawText(valueText, x, y, this.mValuePaint);
    }

    public void drawHighlighted(Canvas c, Highlight[] indices) {
        float y2;
        float y1;
        BarData barData = this.mChart.getBarData();
        for (Highlight high : indices) {
            IBarDataSet set = (IBarDataSet) barData.getDataSetByIndex(high.getDataSetIndex());
            if (set == null) {
                Canvas canvas = c;
            } else if (!set.isHighlightEnabled()) {
                Canvas canvas2 = c;
            } else {
                BarEntry e = (BarEntry) set.getEntryForXValue(high.getX(), high.getY());
                if (!isInBoundsX(e, set)) {
                    Canvas canvas3 = c;
                } else {
                    Transformer trans = this.mChart.getTransformer(set.getAxisDependency());
                    this.mHighlightPaint.setColor(set.getHighLightColor());
                    this.mHighlightPaint.setAlpha(set.getHighLightAlpha());
                    if (!(high.getStackIndex() >= 0 && e.isStacked())) {
                        y1 = e.getY();
                        y2 = 0.0f;
                    } else if (this.mChart.isHighlightFullBarEnabled()) {
                        y1 = e.getPositiveSum();
                        y2 = -e.getNegativeSum();
                    } else {
                        Range range = e.getRanges()[high.getStackIndex()];
                        float y12 = range.from;
                        y2 = range.f79to;
                        y1 = y12;
                    }
                    prepareBarHighlight(e.getX(), y1, y2, barData.getBarWidth() / 2.0f, trans);
                    setHighlightDrawPos(high, this.mBarRect);
                    c.drawRect(this.mBarRect, this.mHighlightPaint);
                }
            }
        }
        Canvas canvas4 = c;
    }

    /* access modifiers changed from: protected */
    public void setHighlightDrawPos(Highlight high, RectF bar) {
        high.setDraw(bar.centerX(), bar.top);
    }

    public void drawExtras(Canvas c) {
    }
}
