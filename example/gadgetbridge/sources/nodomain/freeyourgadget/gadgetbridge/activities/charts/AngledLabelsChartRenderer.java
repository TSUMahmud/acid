package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import android.graphics.Canvas;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class AngledLabelsChartRenderer extends BarChartRenderer {
    AngledLabelsChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    public void drawValue(Canvas canvas, String valueText, float x, float y, int color) {
        this.mValuePaint.setColor(color);
        float x2 = x + 8.0f;
        float y2 = y - 25.0f;
        canvas.save();
        canvas.rotate(-90.0f, x2, y2);
        canvas.drawText(valueText, x2, y2, this.mValuePaint);
        canvas.restore();
    }
}
