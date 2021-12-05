package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.buffer.HorizontalBarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.List;

public class HorizontalBarChartRenderer extends BarChartRenderer {
    private RectF mBarShadowRectBuffer = new RectF();

    public HorizontalBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
        this.mValuePaint.setTextAlign(Paint.Align.LEFT);
    }

    public void initBuffers() {
        BarData barData = this.mChart.getBarData();
        this.mBarBuffers = new HorizontalBarBuffer[barData.getDataSetCount()];
        for (int i = 0; i < this.mBarBuffers.length; i++) {
            IBarDataSet set = (IBarDataSet) barData.getDataSetByIndex(i);
            this.mBarBuffers[i] = new HorizontalBarBuffer(set.getEntryCount() * 4 * (set.isStacked() ? set.getStackSize() : 1), barData.getDataSetCount(), set.isStacked());
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
                rectF.top = x - barWidthHalf;
                rectF.bottom = x + barWidthHalf;
                trans.rectValueToPixel(rectF);
                if (!this.mViewPortHandler.isInBoundsTop(this.mBarShadowRectBuffer.bottom)) {
                    barData = barData2;
                    Canvas canvas2 = c;
                } else if (!this.mViewPortHandler.isInBoundsBottom(this.mBarShadowRectBuffer.top)) {
                    Canvas canvas3 = c;
                    break;
                } else {
                    this.mBarShadowRectBuffer.left = this.mViewPortHandler.contentLeft();
                    this.mBarShadowRectBuffer.right = this.mViewPortHandler.contentRight();
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
        while (j < buffer.size() && this.mViewPortHandler.isInBoundsTop(buffer.buffer[j + 3])) {
            if (this.mViewPortHandler.isInBoundsBottom(buffer.buffer[j + 1])) {
                if (!isSingleColor) {
                    this.mRenderPaint.setColor(iBarDataSet.getColor(j / 4));
                }
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3], this.mRenderPaint);
                if (drawBorder) {
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3], this.mBarBorderPaint);
                }
            }
            j += 4;
        }
    }

    public void drawValues(Canvas c) {
        float valueOffsetPlus;
        List<IBarDataSet> dataSets;
        int i;
        float negOffset;
        float posOffset;
        MPPointF iconsOffset;
        float posOffset2;
        float negOffset2;
        float valueOffsetPlus2;
        float halfTextHeight;
        int index;
        float[] vals;
        float posOffset3;
        BarEntry entry;
        float negOffset3;
        int k;
        float[] transformed;
        float x;
        float y;
        float y2;
        String formattedValue;
        float negOffset4;
        float[] vals2;
        float posOffset4;
        float negOffset5;
        BarEntry entry2;
        List<IBarDataSet> dataSets2;
        int j;
        int i2;
        BarBuffer buffer;
        MPPointF iconsOffset2;
        BarEntry entry3;
        float negOffset6;
        MPPointF iconsOffset3;
        float posOffset5;
        float negOffset7;
        if (isDrawingValuesAllowed(this.mChart)) {
            List<IBarDataSet> dataSets3 = this.mChart.getBarData().getDataSets();
            float valueOffsetPlus3 = Utils.convertDpToPixel(5.0f);
            float posOffset6 = 0.0f;
            float px = 0.0f;
            boolean drawValueAboveBar = this.mChart.isDrawValueAboveBarEnabled();
            int i3 = 0;
            while (i3 < this.mChart.getBarData().getDataSetCount()) {
                IBarDataSet dataSet = dataSets3.get(i3);
                if (!shouldDrawValues(dataSet)) {
                    dataSets = dataSets3;
                    valueOffsetPlus = valueOffsetPlus3;
                    i = i3;
                } else {
                    boolean isInverted = this.mChart.isInverted(dataSet.getAxisDependency());
                    applyValueTextStyle(dataSet);
                    float halfTextHeight2 = ((float) Utils.calcTextHeight(this.mValuePaint, "10")) / 2.0f;
                    ValueFormatter formatter = dataSet.getValueFormatter();
                    BarBuffer buffer2 = this.mBarBuffers[i3];
                    float phaseY = this.mAnimator.getPhaseY();
                    MPPointF iconsOffset4 = MPPointF.getInstance(dataSet.getIconsOffset());
                    iconsOffset4.f90x = Utils.convertDpToPixel(iconsOffset4.f90x);
                    iconsOffset4.f91y = Utils.convertDpToPixel(iconsOffset4.f91y);
                    if (!dataSet.isStacked()) {
                        int j2 = 0;
                        while (true) {
                            posOffset = posOffset6;
                            if (((float) j2) >= ((float) buffer2.buffer.length) * this.mAnimator.getPhaseX()) {
                                negOffset = px;
                                int i4 = j2;
                                dataSets = dataSets3;
                                i = i3;
                                iconsOffset = iconsOffset4;
                                BarBuffer barBuffer = buffer2;
                                break;
                            }
                            float y3 = (buffer2.buffer[j2 + 1] + buffer2.buffer[j2 + 3]) / 2.0f;
                            if (!this.mViewPortHandler.isInBoundsTop(buffer2.buffer[j2 + 1])) {
                                negOffset = px;
                                dataSets = dataSets3;
                                i = i3;
                                iconsOffset = iconsOffset4;
                                BarBuffer barBuffer2 = buffer2;
                                break;
                            }
                            if (this.mViewPortHandler.isInBoundsX(buffer2.buffer[j2]) && this.mViewPortHandler.isInBoundsBottom(buffer2.buffer[j2 + 1])) {
                                BarEntry entry4 = (BarEntry) dataSet.getEntryForIndex(j2 / 4);
                                float val = entry4.getY();
                                String formattedValue2 = formatter.getBarLabel(entry4);
                                float f = px;
                                float valueTextWidth = (float) Utils.calcTextWidth(this.mValuePaint, formattedValue2);
                                String formattedValue3 = formattedValue2;
                                float posOffset7 = drawValueAboveBar ? valueOffsetPlus3 : -(valueTextWidth + valueOffsetPlus3);
                                if (drawValueAboveBar) {
                                    entry3 = entry4;
                                    negOffset6 = -(valueTextWidth + valueOffsetPlus3);
                                } else {
                                    entry3 = entry4;
                                    negOffset6 = valueOffsetPlus3;
                                }
                                if (isInverted) {
                                    iconsOffset3 = iconsOffset4;
                                    posOffset5 = (-posOffset7) - valueTextWidth;
                                    negOffset7 = (-negOffset6) - valueTextWidth;
                                } else {
                                    iconsOffset3 = iconsOffset4;
                                    posOffset5 = posOffset7;
                                    negOffset7 = negOffset6;
                                }
                                if (dataSet.isDrawValuesEnabled()) {
                                    float f2 = valueTextWidth;
                                    j = j2;
                                    dataSets2 = dataSets3;
                                    iconsOffset2 = iconsOffset3;
                                    i2 = i3;
                                    buffer = buffer2;
                                    drawValue(c, formattedValue3, buffer2.buffer[j2 + 2] + (val >= 0.0f ? posOffset5 : negOffset7), y3 + halfTextHeight2, dataSet.getValueTextColor(j2 / 2));
                                } else {
                                    j = j2;
                                    dataSets2 = dataSets3;
                                    iconsOffset2 = iconsOffset3;
                                    i2 = i3;
                                    buffer = buffer2;
                                }
                                if (entry3.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                                    Drawable icon = entry3.getIcon();
                                    Utils.drawImage(c, icon, (int) (buffer.buffer[j + 2] + (val >= 0.0f ? posOffset5 : negOffset7) + iconsOffset2.f90x), (int) (y3 + iconsOffset2.f91y), icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                                }
                                px = negOffset7;
                                posOffset6 = posOffset5;
                            } else {
                                j = j2;
                                dataSets2 = dataSets3;
                                i2 = i3;
                                posOffset6 = posOffset;
                                iconsOffset2 = iconsOffset4;
                                buffer = buffer2;
                            }
                            j2 = j + 4;
                            iconsOffset4 = iconsOffset2;
                            buffer2 = buffer;
                            i3 = i2;
                            dataSets3 = dataSets2;
                        }
                        valueOffsetPlus = valueOffsetPlus3;
                        float f3 = halfTextHeight2;
                    } else {
                        dataSets = dataSets3;
                        i = i3;
                        iconsOffset = iconsOffset4;
                        BarBuffer buffer3 = buffer2;
                        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
                        int bufferIndex = 0;
                        int index2 = 0;
                        while (true) {
                            if (((float) index2) >= ((float) dataSet.getEntryCount()) * this.mAnimator.getPhaseX()) {
                                posOffset2 = posOffset6;
                                negOffset2 = px;
                                int i5 = index2;
                                valueOffsetPlus = valueOffsetPlus3;
                                float f4 = halfTextHeight2;
                                break;
                            }
                            BarEntry entry5 = (BarEntry) dataSet.getEntryForIndex(index2);
                            int color = dataSet.getValueTextColor(index2);
                            float[] vals3 = entry5.getYVals();
                            if (vals3 != null) {
                                float posOffset8 = posOffset6;
                                float negOffset8 = px;
                                BarEntry entry6 = entry5;
                                index = index2;
                                halfTextHeight = halfTextHeight2;
                                vals = vals3;
                                float[] transformed2 = new float[(vals.length * 2)];
                                int k2 = 0;
                                int idx = 0;
                                float posY = 0.0f;
                                float negY = -entry6.getNegativeSum();
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
                                    transformed2[k2] = y2 * phaseY;
                                    k2 += 2;
                                    idx++;
                                }
                                trans.pointValuesToPixel(transformed2);
                                int k3 = 0;
                                while (true) {
                                    if (k3 >= transformed2.length) {
                                        int i6 = k3;
                                        float[] fArr = transformed2;
                                        valueOffsetPlus2 = valueOffsetPlus3;
                                        BarEntry barEntry = entry6;
                                        posOffset3 = posOffset8;
                                        px = negOffset8;
                                        break;
                                    }
                                    float val2 = vals[k3 / 2];
                                    BarEntry entry7 = entry6;
                                    String formattedValue4 = formatter.getBarStackedLabel(val2, entry7);
                                    float valueTextWidth2 = (float) Utils.calcTextWidth(this.mValuePaint, formattedValue4);
                                    String formattedValue5 = formattedValue4;
                                    float posOffset9 = drawValueAboveBar ? valueOffsetPlus3 : -(valueTextWidth2 + valueOffsetPlus3);
                                    if (drawValueAboveBar) {
                                        entry = entry7;
                                        negOffset3 = -(valueTextWidth2 + valueOffsetPlus3);
                                    } else {
                                        entry = entry7;
                                        negOffset3 = valueOffsetPlus3;
                                    }
                                    if (isInverted) {
                                        valueOffsetPlus2 = valueOffsetPlus3;
                                        negOffset8 = (-negOffset3) - valueTextWidth2;
                                        posOffset8 = (-posOffset9) - valueTextWidth2;
                                    } else {
                                        valueOffsetPlus2 = valueOffsetPlus3;
                                        posOffset8 = posOffset9;
                                        negOffset8 = negOffset3;
                                    }
                                    float x2 = (((val2 > 0.0f ? 1 : (val2 == 0.0f ? 0 : -1)) == 0 && (negY > 0.0f ? 1 : (negY == 0.0f ? 0 : -1)) == 0 && (posY > 0.0f ? 1 : (posY == 0.0f ? 0 : -1)) > 0) || (val2 > 0.0f ? 1 : (val2 == 0.0f ? 0 : -1)) < 0 ? negOffset8 : posOffset8) + transformed2[k3];
                                    float f5 = valueTextWidth2;
                                    float y4 = (buffer3.buffer[bufferIndex + 1] + buffer3.buffer[bufferIndex + 3]) / 2.0f;
                                    if (!this.mViewPortHandler.isInBoundsTop(y4)) {
                                        posOffset3 = posOffset8;
                                        px = negOffset8;
                                        break;
                                    }
                                    if (!this.mViewPortHandler.isInBoundsX(x2)) {
                                        k = k3;
                                        transformed = transformed2;
                                    } else if (!this.mViewPortHandler.isInBoundsBottom(y4)) {
                                        k = k3;
                                        transformed = transformed2;
                                    } else {
                                        if (dataSet.isDrawValuesEnabled()) {
                                            y = y4;
                                            x = x2;
                                            float f6 = val2;
                                            k = k3;
                                            transformed = transformed2;
                                            drawValue(c, formattedValue5, x, y4 + halfTextHeight, color);
                                        } else {
                                            y = y4;
                                            x = x2;
                                            float f7 = val2;
                                            k = k3;
                                            transformed = transformed2;
                                        }
                                        if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                                            Drawable icon2 = entry.getIcon();
                                            Utils.drawImage(c, icon2, (int) (x + iconsOffset.f90x), (int) (y + iconsOffset.f91y), icon2.getIntrinsicWidth(), icon2.getIntrinsicHeight());
                                        }
                                    }
                                    k3 = k + 2;
                                    entry6 = entry;
                                    valueOffsetPlus3 = valueOffsetPlus2;
                                    transformed2 = transformed;
                                }
                            } else {
                                posOffset2 = posOffset6;
                                if (!this.mViewPortHandler.isInBoundsTop(buffer3.buffer[bufferIndex + 1])) {
                                    negOffset2 = px;
                                    valueOffsetPlus = valueOffsetPlus3;
                                    float f8 = halfTextHeight2;
                                    break;
                                } else if (this.mViewPortHandler.isInBoundsX(buffer3.buffer[bufferIndex]) && this.mViewPortHandler.isInBoundsBottom(buffer3.buffer[bufferIndex + 1])) {
                                    String formattedValue6 = formatter.getBarLabel(entry5);
                                    float valueTextWidth3 = (float) Utils.calcTextWidth(this.mValuePaint, formattedValue6);
                                    float f9 = px;
                                    float negOffset9 = drawValueAboveBar ? valueOffsetPlus3 : -(valueTextWidth3 + valueOffsetPlus3);
                                    if (drawValueAboveBar) {
                                        formattedValue = formattedValue6;
                                        negOffset4 = -(valueTextWidth3 + valueOffsetPlus3);
                                    } else {
                                        formattedValue = formattedValue6;
                                        negOffset4 = valueOffsetPlus3;
                                    }
                                    if (isInverted) {
                                        vals2 = vals3;
                                        posOffset4 = (-negOffset9) - valueTextWidth3;
                                        negOffset5 = (-negOffset4) - valueTextWidth3;
                                    } else {
                                        vals2 = vals3;
                                        posOffset4 = negOffset9;
                                        negOffset5 = negOffset4;
                                    }
                                    if (dataSet.isDrawValuesEnabled()) {
                                        float f10 = buffer3.buffer[bufferIndex + 2];
                                        float f11 = entry5.getY() >= 0.0f ? posOffset4 : negOffset5;
                                        float f12 = buffer3.buffer[bufferIndex + 1] + halfTextHeight2;
                                        float f13 = valueTextWidth3;
                                        halfTextHeight = halfTextHeight2;
                                        vals = vals2;
                                        entry2 = entry5;
                                        float f14 = f12;
                                        index = index2;
                                        drawValue(c, formattedValue, f10 + f11, f14, color);
                                    } else {
                                        index = index2;
                                        halfTextHeight = halfTextHeight2;
                                        vals = vals2;
                                        entry2 = entry5;
                                    }
                                    if (entry2.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                                        Drawable icon3 = entry2.getIcon();
                                        Utils.drawImage(c, icon3, (int) (buffer3.buffer[bufferIndex + 2] + (entry2.getY() >= 0.0f ? posOffset4 : negOffset5) + iconsOffset.f90x), (int) (buffer3.buffer[bufferIndex + 1] + iconsOffset.f91y), icon3.getIntrinsicWidth(), icon3.getIntrinsicHeight());
                                    }
                                    valueOffsetPlus2 = valueOffsetPlus3;
                                    px = negOffset5;
                                    posOffset3 = posOffset4;
                                    BarEntry barEntry2 = entry2;
                                } else {
                                    posOffset3 = posOffset2;
                                }
                            }
                            bufferIndex = vals == null ? bufferIndex + 4 : bufferIndex + (vals.length * 4);
                            index2 = index + 1;
                            halfTextHeight2 = halfTextHeight;
                            valueOffsetPlus3 = valueOffsetPlus2;
                        }
                        posOffset = posOffset2;
                        negOffset = negOffset2;
                    }
                    MPPointF.recycleInstance(iconsOffset);
                    posOffset6 = posOffset;
                    px = negOffset;
                }
                i3 = i + 1;
                dataSets3 = dataSets;
                valueOffsetPlus3 = valueOffsetPlus;
            }
            float f15 = valueOffsetPlus3;
            int i7 = i3;
        }
    }

    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        this.mValuePaint.setColor(color);
        c.drawText(valueText, x, y, this.mValuePaint);
    }

    /* access modifiers changed from: protected */
    public void prepareBarHighlight(float x, float y1, float y2, float barWidthHalf, Transformer trans) {
        RectF rectF = this.mBarRect;
        rectF.set(y1, x - barWidthHalf, y2, x + barWidthHalf);
        trans.rectToPixelPhaseHorizontal(this.mBarRect, this.mAnimator.getPhaseY());
    }

    /* access modifiers changed from: protected */
    public void setHighlightDrawPos(Highlight high, RectF bar) {
        high.setDraw(bar.centerY(), bar.right);
    }

    /* access modifiers changed from: protected */
    public boolean isDrawingValuesAllowed(ChartInterface chart) {
        return ((float) chart.getData().getEntryCount()) < ((float) chart.getMaxVisibleCount()) * this.mViewPortHandler.getScaleY();
    }
}
