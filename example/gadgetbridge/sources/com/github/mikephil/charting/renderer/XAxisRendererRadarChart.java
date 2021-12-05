package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class XAxisRendererRadarChart extends XAxisRenderer {
    private RadarChart mChart;

    public XAxisRendererRadarChart(ViewPortHandler viewPortHandler, XAxis xAxis, RadarChart chart) {
        super(viewPortHandler, xAxis, (Transformer) null);
        this.mChart = chart;
    }

    public void renderAxisLabels(Canvas c) {
        if (this.mXAxis.isEnabled() && this.mXAxis.isDrawLabelsEnabled()) {
            float labelRotationAngleDegrees = this.mXAxis.getLabelRotationAngle();
            MPPointF drawLabelAnchor = MPPointF.getInstance(0.5f, 0.25f);
            this.mAxisLabelPaint.setTypeface(this.mXAxis.getTypeface());
            this.mAxisLabelPaint.setTextSize(this.mXAxis.getTextSize());
            this.mAxisLabelPaint.setColor(this.mXAxis.getTextColor());
            float sliceangle = this.mChart.getSliceAngle();
            float factor = this.mChart.getFactor();
            MPPointF center = this.mChart.getCenterOffsets();
            MPPointF pOut = MPPointF.getInstance(0.0f, 0.0f);
            for (int i = 0; i < ((IRadarDataSet) ((RadarData) this.mChart.getData()).getMaxEntryCountSet()).getEntryCount(); i++) {
                String label = this.mXAxis.getValueFormatter().getAxisLabel((float) i, this.mXAxis);
                float angle = ((((float) i) * sliceangle) + this.mChart.getRotationAngle()) % 360.0f;
                Utils.getPosition(center, (this.mChart.getYRange() * factor) + (((float) this.mXAxis.mLabelRotatedWidth) / 2.0f), angle, pOut);
                float f = angle;
                drawLabel(c, label, pOut.f90x, pOut.f91y - (((float) this.mXAxis.mLabelRotatedHeight) / 2.0f), drawLabelAnchor, labelRotationAngleDegrees);
            }
            MPPointF.recycleInstance(center);
            MPPointF.recycleInstance(pOut);
            MPPointF.recycleInstance(drawLabelAnchor);
        }
    }

    public void renderLimitLines(Canvas c) {
    }
}
