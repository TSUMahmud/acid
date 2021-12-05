package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.renderer.BarChartRenderer;

public class CustomBarChart extends BarChart {
    private Entry entry = null;
    private SingleEntryValueAnimator singleEntryAnimator;

    public CustomBarChart(Context context) {
        super(context);
    }

    public CustomBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSinglAnimationEntry(Entry entry2) {
        this.entry = entry2;
        if (entry2 != null) {
            this.singleEntryAnimator = new SingleEntryValueAnimator(entry2, new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    CustomBarChart.this.postInvalidate();
                }
            });
            SingleEntryValueAnimator singleEntryValueAnimator = this.singleEntryAnimator;
            this.mAnimator = singleEntryValueAnimator;
            this.mRenderer = new BarChartRenderer(this, singleEntryValueAnimator, getViewPortHandler());
        }
    }

    public void setSingleEntryYValue(float nextValue) {
        SingleEntryValueAnimator singleEntryValueAnimator = this.singleEntryAnimator;
        if (singleEntryValueAnimator != null) {
            singleEntryValueAnimator.setEntryYValue(nextValue);
        }
    }
}
