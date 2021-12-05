package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class RadarChartRenderer extends LineRadarRenderer {
    protected RadarChart mChart;
    protected Path mDrawDataSetSurfacePathBuffer = new Path();
    protected Path mDrawHighlightCirclePathBuffer = new Path();
    protected Paint mHighlightCirclePaint;
    protected Paint mWebPaint;

    public RadarChartRenderer(RadarChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
        this.mHighlightPaint = new Paint(1);
        this.mHighlightPaint.setStyle(Paint.Style.STROKE);
        this.mHighlightPaint.setStrokeWidth(2.0f);
        this.mHighlightPaint.setColor(Color.rgb(255, 187, 115));
        this.mWebPaint = new Paint(1);
        this.mWebPaint.setStyle(Paint.Style.STROKE);
        this.mHighlightCirclePaint = new Paint(1);
    }

    public Paint getWebPaint() {
        return this.mWebPaint;
    }

    public void initBuffers() {
    }

    public void drawData(Canvas c) {
        RadarData radarData = (RadarData) this.mChart.getData();
        int mostEntries = ((IRadarDataSet) radarData.getMaxEntryCountSet()).getEntryCount();
        for (IRadarDataSet set : radarData.getDataSets()) {
            if (set.isVisible()) {
                drawDataSet(c, set, mostEntries);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawDataSet(Canvas c, IRadarDataSet dataSet, int mostEntries) {
        Canvas canvas = c;
        IRadarDataSet iRadarDataSet = dataSet;
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        float sliceangle = this.mChart.getSliceAngle();
        float factor = this.mChart.getFactor();
        MPPointF center = this.mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0.0f, 0.0f);
        Path surface = this.mDrawDataSetSurfacePathBuffer;
        surface.reset();
        boolean hasMovedToPoint = false;
        for (int j = 0; j < dataSet.getEntryCount(); j++) {
            this.mRenderPaint.setColor(iRadarDataSet.getColor(j));
            Utils.getPosition(center, (((RadarEntry) iRadarDataSet.getEntryForIndex(j)).getY() - this.mChart.getYChartMin()) * factor * phaseY, (((float) j) * sliceangle * phaseX) + this.mChart.getRotationAngle(), pOut);
            if (!Float.isNaN(pOut.f90x)) {
                if (!hasMovedToPoint) {
                    surface.moveTo(pOut.f90x, pOut.f91y);
                    hasMovedToPoint = true;
                } else {
                    surface.lineTo(pOut.f90x, pOut.f91y);
                }
            }
        }
        if (dataSet.getEntryCount() > mostEntries) {
            surface.lineTo(center.f90x, center.f91y);
        }
        surface.close();
        if (dataSet.isDrawFilledEnabled()) {
            Drawable drawable = dataSet.getFillDrawable();
            if (drawable != null) {
                drawFilledPath(canvas, surface, drawable);
            } else {
                drawFilledPath(canvas, surface, dataSet.getFillColor(), dataSet.getFillAlpha());
            }
        }
        this.mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
        this.mRenderPaint.setStyle(Paint.Style.STROKE);
        if (!dataSet.isDrawFilledEnabled() || dataSet.getFillAlpha() < 255) {
            canvas.drawPath(surface, this.mRenderPaint);
        }
        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }

    public void drawValues(Canvas c) {
        MPPointF pOut;
        float yoffset;
        int i;
        MPPointF pOut2;
        float yoffset2;
        RadarEntry entry;
        IRadarDataSet dataSet;
        ValueFormatter formatter;
        int j;
        MPPointF pOut3;
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        float sliceangle = this.mChart.getSliceAngle();
        float factor = this.mChart.getFactor();
        MPPointF center = this.mChart.getCenterOffsets();
        MPPointF iconsOffset = MPPointF.getInstance(0.0f, 0.0f);
        MPPointF pIcon = MPPointF.getInstance(0.0f, 0.0f);
        float yoffset3 = Utils.convertDpToPixel(5.0f);
        int i2 = 0;
        while (i2 < ((RadarData) this.mChart.getData()).getDataSetCount()) {
            IRadarDataSet dataSet2 = (IRadarDataSet) ((RadarData) this.mChart.getData()).getDataSetByIndex(i2);
            if (!shouldDrawValues(dataSet2)) {
                pOut = iconsOffset;
                yoffset = yoffset3;
                i = i2;
            } else {
                applyValueTextStyle(dataSet2);
                ValueFormatter formatter2 = dataSet2.getValueFormatter();
                MPPointF iconsOffset2 = MPPointF.getInstance(dataSet2.getIconsOffset());
                iconsOffset2.f90x = Utils.convertDpToPixel(iconsOffset2.f90x);
                iconsOffset2.f91y = Utils.convertDpToPixel(iconsOffset2.f91y);
                int j2 = 0;
                while (j2 < dataSet2.getEntryCount()) {
                    RadarEntry entry2 = (RadarEntry) dataSet2.getEntryForIndex(j2);
                    MPPointF iconsOffset3 = iconsOffset2;
                    int i3 = i2;
                    Utils.getPosition(center, (entry2.getY() - this.mChart.getYChartMin()) * factor * phaseY, (((float) j2) * sliceangle * phaseX) + this.mChart.getRotationAngle(), iconsOffset);
                    if (dataSet2.isDrawValuesEnabled()) {
                        String radarLabel = formatter2.getRadarLabel(entry2);
                        float f = iconsOffset.f90x;
                        float f2 = iconsOffset.f91y - yoffset3;
                        int valueTextColor = dataSet2.getValueTextColor(j2);
                        entry = entry2;
                        yoffset2 = yoffset3;
                        j = j2;
                        String str = radarLabel;
                        pOut2 = iconsOffset;
                        pOut3 = iconsOffset3;
                        float f3 = f;
                        formatter = formatter2;
                        dataSet = dataSet2;
                        drawValue(c, str, f3, f2, valueTextColor);
                    } else {
                        entry = entry2;
                        formatter = formatter2;
                        pOut2 = iconsOffset;
                        yoffset2 = yoffset3;
                        pOut3 = iconsOffset3;
                        j = j2;
                        dataSet = dataSet2;
                    }
                    if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                        Drawable icon = entry.getIcon();
                        Utils.getPosition(center, (entry.getY() * factor * phaseY) + pOut3.f91y, (((float) j) * sliceangle * phaseX) + this.mChart.getRotationAngle(), pIcon);
                        pIcon.f91y += pOut3.f90x;
                        Utils.drawImage(c, icon, (int) pIcon.f90x, (int) pIcon.f91y, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                    }
                    j2 = j + 1;
                    iconsOffset2 = pOut3;
                    formatter2 = formatter;
                    dataSet2 = dataSet;
                    i2 = i3;
                    yoffset3 = yoffset2;
                    iconsOffset = pOut2;
                }
                pOut = iconsOffset;
                yoffset = yoffset3;
                i = i2;
                float yoffset4 = j2;
                ValueFormatter valueFormatter = formatter2;
                MPPointF.recycleInstance(iconsOffset2);
            }
            i2 = i + 1;
            yoffset3 = yoffset;
            iconsOffset = pOut;
        }
        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(iconsOffset);
        MPPointF.recycleInstance(pIcon);
    }

    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        this.mValuePaint.setColor(color);
        c.drawText(valueText, x, y, this.mValuePaint);
    }

    public void drawExtras(Canvas c) {
        drawWeb(c);
    }

    /* access modifiers changed from: protected */
    public void drawWeb(Canvas c) {
        float sliceangle = this.mChart.getSliceAngle();
        float factor = this.mChart.getFactor();
        float rotationangle = this.mChart.getRotationAngle();
        MPPointF center = this.mChart.getCenterOffsets();
        this.mWebPaint.setStrokeWidth(this.mChart.getWebLineWidth());
        this.mWebPaint.setColor(this.mChart.getWebColor());
        this.mWebPaint.setAlpha(this.mChart.getWebAlpha());
        int xIncrements = this.mChart.getSkipWebLineCount() + 1;
        int maxEntryCount = ((IRadarDataSet) ((RadarData) this.mChart.getData()).getMaxEntryCountSet()).getEntryCount();
        MPPointF p = MPPointF.getInstance(0.0f, 0.0f);
        for (int i = 0; i < maxEntryCount; i += xIncrements) {
            Utils.getPosition(center, this.mChart.getYRange() * factor, (((float) i) * sliceangle) + rotationangle, p);
            c.drawLine(center.f90x, center.f91y, p.f90x, p.f91y, this.mWebPaint);
        }
        MPPointF.recycleInstance(p);
        this.mWebPaint.setStrokeWidth(this.mChart.getWebLineWidthInner());
        this.mWebPaint.setColor(this.mChart.getWebColorInner());
        this.mWebPaint.setAlpha(this.mChart.getWebAlpha());
        int labelCount = this.mChart.getYAxis().mEntryCount;
        MPPointF p1out = MPPointF.getInstance(0.0f, 0.0f);
        MPPointF p2out = MPPointF.getInstance(0.0f, 0.0f);
        for (int j = 0; j < labelCount; j++) {
            int i2 = 0;
            while (i2 < ((RadarData) this.mChart.getData()).getEntryCount()) {
                float r = (this.mChart.getYAxis().mEntries[j] - this.mChart.getYChartMin()) * factor;
                Utils.getPosition(center, r, (((float) i2) * sliceangle) + rotationangle, p1out);
                Utils.getPosition(center, r, (((float) (i2 + 1)) * sliceangle) + rotationangle, p2out);
                float f = p1out.f90x;
                float f2 = p1out.f91y;
                float sliceangle2 = sliceangle;
                float factor2 = factor;
                float f3 = f2;
                c.drawLine(f, f3, p2out.f90x, p2out.f91y, this.mWebPaint);
                i2++;
                sliceangle = sliceangle2;
                factor = factor2;
                rotationangle = rotationangle;
            }
            float f4 = factor;
            float f5 = rotationangle;
        }
        MPPointF.recycleInstance(p1out);
        MPPointF.recycleInstance(p2out);
    }

    public void drawHighlighted(Canvas c, Highlight[] indices) {
        int i;
        int strokeColor;
        Highlight[] highlightArr = indices;
        float sliceangle = this.mChart.getSliceAngle();
        float factor = this.mChart.getFactor();
        MPPointF center = this.mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0.0f, 0.0f);
        RadarData radarData = (RadarData) this.mChart.getData();
        int length = highlightArr.length;
        int i2 = 0;
        int i3 = 0;
        while (i3 < length) {
            Highlight high = highlightArr[i3];
            IRadarDataSet set = (IRadarDataSet) radarData.getDataSetByIndex(high.getDataSetIndex());
            if (set == null) {
                Highlight highlight = high;
                i = i3;
            } else if (!set.isHighlightEnabled()) {
                i = i3;
            } else {
                RadarEntry e = (RadarEntry) set.getEntryForIndex((int) high.getX());
                if (!isInBoundsX(e, set)) {
                    i = i3;
                } else {
                    Utils.getPosition(center, (e.getY() - this.mChart.getYChartMin()) * factor * this.mAnimator.getPhaseY(), (high.getX() * sliceangle * this.mAnimator.getPhaseX()) + this.mChart.getRotationAngle(), pOut);
                    high.setDraw(pOut.f90x, pOut.f91y);
                    drawHighlightLines(c, pOut.f90x, pOut.f91y, set);
                    if (!set.isDrawHighlightCircleEnabled()) {
                        IRadarDataSet iRadarDataSet = set;
                        Highlight highlight2 = high;
                        i = i3;
                    } else if (Float.isNaN(pOut.f90x) || Float.isNaN(pOut.f91y)) {
                        IRadarDataSet iRadarDataSet2 = set;
                        Highlight highlight3 = high;
                        i = i3;
                    } else {
                        int strokeColor2 = set.getHighlightCircleStrokeColor();
                        if (strokeColor2 == 1122867) {
                            strokeColor2 = set.getColor(i2);
                        }
                        if (set.getHighlightCircleStrokeAlpha() < 255) {
                            strokeColor = ColorTemplate.colorWithAlpha(strokeColor2, set.getHighlightCircleStrokeAlpha());
                        } else {
                            strokeColor = strokeColor2;
                        }
                        float highlightCircleInnerRadius = set.getHighlightCircleInnerRadius();
                        float highlightCircleOuterRadius = set.getHighlightCircleOuterRadius();
                        int highlightCircleFillColor = set.getHighlightCircleFillColor();
                        float highlightCircleStrokeWidth = set.getHighlightCircleStrokeWidth();
                        RadarEntry radarEntry = e;
                        IRadarDataSet iRadarDataSet3 = set;
                        float f = highlightCircleOuterRadius;
                        Highlight highlight4 = high;
                        int i4 = highlightCircleFillColor;
                        i = i3;
                        drawHighlightCircle(c, pOut, highlightCircleInnerRadius, f, i4, strokeColor, highlightCircleStrokeWidth);
                    }
                }
            }
            i3 = i + 1;
            i2 = 0;
        }
        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }

    public void drawHighlightCircle(Canvas c, MPPointF point, float innerRadius, float outerRadius, int fillColor, int strokeColor, float strokeWidth) {
        c.save();
        float outerRadius2 = Utils.convertDpToPixel(outerRadius);
        float innerRadius2 = Utils.convertDpToPixel(innerRadius);
        if (fillColor != 1122867) {
            Path p = this.mDrawHighlightCirclePathBuffer;
            p.reset();
            p.addCircle(point.f90x, point.f91y, outerRadius2, Path.Direction.CW);
            if (innerRadius2 > 0.0f) {
                p.addCircle(point.f90x, point.f91y, innerRadius2, Path.Direction.CCW);
            }
            this.mHighlightCirclePaint.setColor(fillColor);
            this.mHighlightCirclePaint.setStyle(Paint.Style.FILL);
            c.drawPath(p, this.mHighlightCirclePaint);
        }
        if (strokeColor != 1122867) {
            this.mHighlightCirclePaint.setColor(strokeColor);
            this.mHighlightCirclePaint.setStyle(Paint.Style.STROKE);
            this.mHighlightCirclePaint.setStrokeWidth(Utils.convertDpToPixel(strokeWidth));
            c.drawCircle(point.f90x, point.f91y, outerRadius2, this.mHighlightCirclePaint);
        }
        c.restore();
    }
}
