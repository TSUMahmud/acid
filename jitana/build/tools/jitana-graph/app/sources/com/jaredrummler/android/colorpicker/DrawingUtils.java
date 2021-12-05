package com.jaredrummler.android.colorpicker;

import android.content.Context;
import android.util.TypedValue;

final class DrawingUtils {
    DrawingUtils() {
    }

    static int dpToPx(Context c, float dipValue) {
        float val = TypedValue.applyDimension(1, dipValue, c.getResources().getDisplayMetrics());
        double d = (double) val;
        Double.isNaN(d);
        int res = (int) (d + 0.5d);
        if (res != 0 || val <= 0.0f) {
            return res;
        }
        return 1;
    }
}
