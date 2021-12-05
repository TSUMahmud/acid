package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;

/* compiled from: ChartsActivity */
class NonSwipeableViewPager extends ViewPager {
    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (GBApplication.getPrefs().getBoolean("charts_allow_swipe", true)) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (GBApplication.getPrefs().getBoolean("charts_allow_swipe", true)) {
            return super.onTouchEvent(ev);
        }
        return false;
    }
}
