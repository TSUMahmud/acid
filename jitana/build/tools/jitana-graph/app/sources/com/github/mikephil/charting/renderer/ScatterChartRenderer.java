package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.ScatterDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.renderer.scatter.IShapeRenderer;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.List;

public class ScatterChartRenderer extends LineScatterCandleRadarRenderer {
    protected ScatterDataProvider mChart;
    float[] mPixelBuffer = new float[2];

    public ScatterChartRenderer(ScatterDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
    }

    public void initBuffers() {
    }

    public void drawData(Canvas c) {
        for (IScatterDataSet set : this.mChart.getScatterData().getDataSets()) {
            if (set.isVisible()) {
                drawDataSet(c, set);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawDataSet(Canvas c, IScatterDataSet dataSet) {
        IScatterDataSet iScatterDataSet = dataSet;
        if (dataSet.getEntryCount() >= 1) {
            ViewPortHandler viewPortHandler = this.mViewPortHandler;
            Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
            float phaseY = this.mAnimator.getPhaseY();
            IShapeRenderer renderer = dataSet.getShapeRenderer();
            if (renderer == null) {
                Log.i("MISSING", "There's no IShapeRenderer specified for ScatterDataSet");
                return;
            }
            int max = (int) Math.min(Math.ceil((double) (((float) dataSet.getEntryCount()) * this.mAnimator.getPhaseX())), (double) ((float) dataSet.getEntryCount()));
            int i = 0;
            while (i < max) {
                Entry e = iScatterDataSet.getEntryForIndex(i);
                this.mPixelBuffer[0] = e.getX();
                this.mPixelBuffer[1] = e.getY() * phaseY;
                trans.pointValuesToPixel(this.mPixelBuffer);
                if (viewPortHandler.isInBoundsRight(this.mPixelBuffer[0])) {
                    if (viewPortHandler.isInBoundsLeft(this.mPixelBuffer[0]) && viewPortHandler.isInBoundsY(this.mPixelBuffer[1])) {
                        this.mRenderPaint.setColor(iScatterDataSet.getColor(i / 2));
                        ViewPortHandler viewPortHandler2 = this.mViewPortHandler;
                        float[] fArr = this.mPixelBuffer;
                        renderer.renderShape(c, dataSet, viewPortHandler2, fArr[0], fArr[1], this.mRenderPaint);
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public void drawValues(Canvas c) {
        if (isDrawingValuesAllowed(this.mChart)) {
            List<IScatterDataSet> dataSets = this.mChart.getScatterData().getDataSets();
            for (int i = 0; i < this.mChart.getScatterData().getDataSetCount(); i++) {
                IScatterDataSet dataSet = dataSets.get(i);
                if (shouldDrawValues(dataSet) && dataSet.getEntryCount() >= 1) {
                    applyValueTextStyle(dataSet);
                    this.mXBounds.set(this.mChart, dataSet);
                    float[] positions = this.mChart.getTransformer(dataSet.getAxisDependency()).generateTransformedValuesScatter(dataSet, this.mAnimator.getPhaseX(), this.mAnimator.getPhaseY(), this.mXBounds.min, this.mXBounds.max);
                    float shapeSize = Utils.convertDpToPixel(dataSet.getScatterShapeSize());
                    ValueFormatter formatter = dataSet.getValueFormatter();
                    MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
                    iconsOffset.f90x = Utils.convertDpToPixel(iconsOffset.f90x);
                    iconsOffset.f91y = Utils.convertDpToPixel(iconsOffset.f91y);
                    int j = 0;
                    while (j < positions.length && this.mViewPortHandler.isInBoundsRight(positions[j])) {
                        if (this.mViewPortHandler.isInBoundsLeft(positions[j]) && this.mViewPortHandler.isInBoundsY(positions[j + 1])) {
                            Entry entry = dataSet.getEntryForIndex((j / 2) + this.mXBounds.min);
                            if (dataSet.isDrawValuesEnabled()) {
                                drawValue(c, formatter.getPointLabel(entry), positions[j], positions[j + 1] - shapeSize, dataSet.getValueTextColor((j / 2) + this.mXBounds.min));
                            }
                            if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                                Drawable icon = entry.getIcon();
                                Utils.drawImage(c, icon, (int) (positions[j] + iconsOffset.f90x), (int) (positions[j + 1] + iconsOffset.f91y), icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                            }
                        }
                        j += 2;
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
        ScatterData scatterData = this.mChart.getScatterData();
        for (Highlight high : indices) {
            IScatterDataSet set = (IScatterDataSet) scatterData.getDataSetByIndex(high.getDataSetIndex());
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
}
