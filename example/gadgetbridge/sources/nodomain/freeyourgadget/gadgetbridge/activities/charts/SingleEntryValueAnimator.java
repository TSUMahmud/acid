package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleEntryValueAnimator extends ChartAnimator {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) SingleEntryValueAnimator.class);
    private final Entry entry;
    private final ValueAnimator.AnimatorUpdateListener listener;
    private float previousValue;

    public SingleEntryValueAnimator(Entry singleEntry, ValueAnimator.AnimatorUpdateListener listener2) {
        super(listener2);
        this.listener = listener2;
        this.entry = singleEntry;
    }

    public void setEntryYValue(float value) {
        this.previousValue = this.entry.getY();
        this.entry.setY(value);
    }

    public void animateY(int durationMillis) {
        float startAnim;
        if (this.entry.getY() == 0.0f) {
            startAnim = 0.0f;
        } else {
            startAnim = this.previousValue / this.entry.getY();
        }
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "phaseY", new float[]{startAnim, 1.0f});
        animatorY.setDuration((long) durationMillis);
        animatorY.addUpdateListener(this.listener);
        animatorY.start();
    }
}
