package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.List;

public class CandleStickChartRenderer extends LineScatterCandleRadarRenderer {
    private float[] mBodyBuffers = new float[4];
    protected CandleDataProvider mChart;
    private float[] mCloseBuffers = new float[4];
    private float[] mOpenBuffers = new float[4];
    private float[] mRangeBuffers = new float[4];
    private float[] mShadowBuffers = new float[8];

    public CandleStickChartRenderer(CandleDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
    }

    public void initBuffers() {
    }

    public void drawData(Canvas c) {
        for (ICandleDataSet set : this.mChart.getCandleData().getDataSets()) {
            if (set.isVisible()) {
                drawDataSet(c, set);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawDataSet(Canvas c, ICandleDataSet dataSet) {
        int barColor;
        int i;
        int i2;
        int i3;
        int i4;
        ICandleDataSet iCandleDataSet = dataSet;
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        float phaseY = this.mAnimator.getPhaseY();
        float barSpace = dataSet.getBarSpace();
        boolean showCandleBar = dataSet.getShowCandleBar();
        this.mXBounds.set(this.mChart, iCandleDataSet);
        this.mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());
        for (int j = this.mXBounds.min; j <= this.mXBounds.range + this.mXBounds.min; j++) {
            CandleEntry e = (CandleEntry) iCandleDataSet.getEntryForIndex(j);
            if (e == null) {
                Canvas canvas = c;
            } else {
                float xPos = e.getX();
                float open = e.getOpen();
                float close = e.getClose();
                float high = e.getHigh();
                float low = e.getLow();
                if (showCandleBar) {
                    float[] fArr = this.mShadowBuffers;
                    fArr[0] = xPos;
                    fArr[2] = xPos;
                    fArr[4] = xPos;
                    fArr[6] = xPos;
                    if (open > close) {
                        fArr[1] = high * phaseY;
                        fArr[3] = open * phaseY;
                        fArr[5] = low * phaseY;
                        fArr[7] = close * phaseY;
                    } else if (open < close) {
                        fArr[1] = high * phaseY;
                        fArr[3] = close * phaseY;
                        fArr[5] = low * phaseY;
                        fArr[7] = open * phaseY;
                    } else {
                        fArr[1] = high * phaseY;
                        fArr[3] = open * phaseY;
                        fArr[5] = low * phaseY;
                        fArr[7] = fArr[3];
                    }
                    trans.pointValuesToPixel(this.mShadowBuffers);
                    if (!dataSet.getShadowColorSameAsCandle()) {
                        Paint paint = this.mRenderPaint;
                        if (dataSet.getShadowColor() == 1122867) {
                            i = iCandleDataSet.getColor(j);
                        } else {
                            i = dataSet.getShadowColor();
                        }
                        paint.setColor(i);
                    } else if (open > close) {
                        Paint paint2 = this.mRenderPaint;
                        if (dataSet.getDecreasingColor() == 1122867) {
                            i4 = iCandleDataSet.getColor(j);
                        } else {
                            i4 = dataSet.getDecreasingColor();
                        }
                        paint2.setColor(i4);
                    } else if (open < close) {
                        Paint paint3 = this.mRenderPaint;
                        if (dataSet.getIncreasingColor() == 1122867) {
                            i3 = iCandleDataSet.getColor(j);
                        } else {
                            i3 = dataSet.getIncreasingColor();
                        }
                        paint3.setColor(i3);
                    } else {
                        Paint paint4 = this.mRenderPaint;
                        if (dataSet.getNeutralColor() == 1122867) {
                            i2 = iCandleDataSet.getColor(j);
                        } else {
                            i2 = dataSet.getNeutralColor();
                        }
                        paint4.setColor(i2);
                    }
                    this.mRenderPaint.setStyle(Paint.Style.STROKE);
                    c.drawLines(this.mShadowBuffers, this.mRenderPaint);
                    float[] fArr2 = this.mBodyBuffers;
                    fArr2[0] = (xPos - 0.5f) + barSpace;
                    fArr2[1] = close * phaseY;
                    fArr2[2] = (0.5f + xPos) - barSpace;
                    fArr2[3] = open * phaseY;
                    trans.pointValuesToPixel(fArr2);
                    if (open > close) {
                        if (dataSet.getDecreasingColor() == 1122867) {
                            this.mRenderPaint.setColor(iCandleDataSet.getColor(j));
                        } else {
                            this.mRenderPaint.setColor(dataSet.getDecreasingColor());
                        }
                        this.mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());
                        float[] fArr3 = this.mBodyBuffers;
                        c.drawRect(fArr3[0], fArr3[3], fArr3[2], fArr3[1], this.mRenderPaint);
                    } else if (open < close) {
                        if (dataSet.getIncreasingColor() == 1122867) {
                            this.mRenderPaint.setColor(iCandleDataSet.getColor(j));
                        } else {
                            this.mRenderPaint.setColor(dataSet.getIncreasingColor());
                        }
                        this.mRenderPaint.setStyle(dataSet.getIncreasingPaintStyle());
                        float[] fArr4 = this.mBodyBuffers;
                        c.drawRect(fArr4[0], fArr4[1], fArr4[2], fArr4[3], this.mRenderPaint);
                    } else {
                        if (dataSet.getNeutralColor() == 1122867) {
                            this.mRenderPaint.setColor(iCandleDataSet.getColor(j));
                        } else {
                            this.mRenderPaint.setColor(dataSet.getNeutralColor());
                        }
                        float[] fArr5 = this.mBodyBuffers;
                        c.drawLine(fArr5[0], fArr5[1], fArr5[2], fArr5[3], this.mRenderPaint);
                    }
                } else {
                    Canvas canvas2 = c;
                    float[] fArr6 = this.mRangeBuffers;
                    fArr6[0] = xPos;
                    fArr6[1] = high * phaseY;
                    fArr6[2] = xPos;
                    fArr6[3] = low * phaseY;
                    float[] fArr7 = this.mOpenBuffers;
                    fArr7[0] = (xPos - 0.5f) + barSpace;
                    fArr7[1] = open * phaseY;
                    fArr7[2] = xPos;
                    fArr7[3] = open * phaseY;
                    float[] fArr8 = this.mCloseBuffers;
                    fArr8[0] = (xPos + 0.5f) - barSpace;
                    fArr8[1] = close * phaseY;
                    fArr8[2] = xPos;
                    fArr8[3] = close * phaseY;
                    trans.pointValuesToPixel(fArr6);
                    trans.pointValuesToPixel(this.mOpenBuffers);
                    trans.pointValuesToPixel(this.mCloseBuffers);
                    if (open > close) {
                        if (dataSet.getDecreasingColor() == 1122867) {
                            barColor = iCandleDataSet.getColor(j);
                        } else {
                            barColor = dataSet.getDecreasingColor();
                        }
                    } else if (open < close) {
                        if (dataSet.getIncreasingColor() == 1122867) {
                            barColor = iCandleDataSet.getColor(j);
                        } else {
                            barColor = dataSet.getIncreasingColor();
                        }
                    } else if (dataSet.getNeutralColor() == 1122867) {
                        barColor = iCandleDataSet.getColor(j);
                    } else {
                        barColor = dataSet.getNeutralColor();
                    }
                    this.mRenderPaint.setColor(barColor);
                    float[] fArr9 = this.mRangeBuffers;
                    Canvas canvas3 = c;
                    canvas3.drawLine(fArr9[0], fArr9[1], fArr9[2], fArr9[3], this.mRenderPaint);
                    float[] fArr10 = this.mOpenBuffers;
                    canvas3.drawLine(fArr10[0], fArr10[1], fArr10[2], fArr10[3], this.mRenderPaint);
                    float[] fArr11 = this.mCloseBuffers;
                    canvas3.drawLine(fArr11[0], fArr11[1], fArr11[2], fArr11[3], this.mRenderPaint);
                }
            }
        }
        Canvas canvas4 = c;
    }

    public void drawValues(Canvas c) {
        CandleEntry entry;
        float y;
        if (isDrawingValuesAllowed(this.mChart)) {
            List<ICandleDataSet> dataSets = this.mChart.getCandleData().getDataSets();
            for (int i = 0; i < dataSets.size(); i++) {
                ICandleDataSet dataSet = dataSets.get(i);
                if (shouldDrawValues(dataSet) && dataSet.getEntryCount() >= 1) {
                    applyValueTextStyle(dataSet);
                    Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
                    this.mXBounds.set(this.mChart, dataSet);
                    float[] positions = trans.generateTransformedValuesCandle(dataSet, this.mAnimator.getPhaseX(), this.mAnimator.getPhaseY(), this.mXBounds.min, this.mXBounds.max);
                    float yOffset = Utils.convertDpToPixel(5.0f);
                    ValueFormatter formatter = dataSet.getValueFormatter();
                    MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
                    iconsOffset.f90x = Utils.convertDpToPixel(iconsOffset.f90x);
                    iconsOffset.f91y = Utils.convertDpToPixel(iconsOffset.f91y);
                    for (int j = 0; j < positions.length; j += 2) {
                        float x = positions[j];
                        float y2 = positions[j + 1];
                        if (!this.mViewPortHandler.isInBoundsRight(x)) {
                            break;
                        }
                        if (!this.mViewPortHandler.isInBoundsLeft(x)) {
                        } else if (this.mViewPortHandler.isInBoundsY(y2)) {
                            CandleEntry entry2 = (CandleEntry) dataSet.getEntryForIndex((j / 2) + this.mXBounds.min);
                            if (dataSet.isDrawValuesEnabled()) {
                                entry = entry2;
                                y = y2;
                                drawValue(c, formatter.getCandleLabel(entry2), x, y2 - yOffset, dataSet.getValueTextColor(j / 2));
                            } else {
                                entry = entry2;
                                y = y2;
                            }
                            if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                                Drawable icon = entry.getIcon();
                                Utils.drawImage(c, icon, (int) (iconsOffset.f90x + x), (int) (y + iconsOffset.f91y), icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                            }
                        }
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
    }

    public void drawHighlighted(Canvas c, Highlight[] indices) {
        CandleData candleData = this.mChart.getCandleData();
        for (Highlight high : indices) {
            ICandleDataSet set = (ICandleDataSet) candleData.getDataSetByIndex(high.getDataSetIndex());
            if (set != null && set.isHighlightEnabled()) {
                CandleEntry e = (CandleEntry) set.getEntryForXValue(high.getX(), high.getY());
                if (isInBoundsX(e, set)) {
                    MPPointD pix = this.mChart.getTransformer(set.getAxisDependency()).getPixelForValues(e.getX(), ((e.getLow() * this.mAnimator.getPhaseY()) + (e.getHigh() * this.mAnimator.getPhaseY())) / 2.0f);
                    high.setDraw((float) pix.f88x, (float) pix.f89y);
                    drawHighlightLines(c, (float) pix.f88x, (float) pix.f89y, set);
                }
            }
        }
    }
}
