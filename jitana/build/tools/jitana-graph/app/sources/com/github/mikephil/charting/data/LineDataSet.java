package com.github.mikephil.charting.data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.util.Log;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class LineDataSet extends LineRadarDataSet<Entry> implements ILineDataSet {
    private List<Integer> mCircleColors = null;
    private int mCircleHoleColor = -1;
    private float mCircleHoleRadius = 4.0f;
    private float mCircleRadius = 8.0f;
    private float mCubicIntensity = 0.2f;
    private DashPathEffect mDashPathEffect = null;
    private boolean mDrawCircleHole = true;
    private boolean mDrawCircles = true;
    private IFillFormatter mFillFormatter = new DefaultFillFormatter();
    private Mode mMode = Mode.LINEAR;

    public enum Mode {
        LINEAR,
        STEPPED,
        CUBIC_BEZIER,
        HORIZONTAL_BEZIER
    }

    public LineDataSet(List<Entry> yVals, String label) {
        super(yVals, label);
        if (this.mCircleColors == null) {
            this.mCircleColors = new ArrayList();
        }
        this.mCircleColors.clear();
        this.mCircleColors.add(Integer.valueOf(Color.rgb(140, 234, 255)));
    }

    public DataSet<Entry> copy() {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < this.mValues.size(); i++) {
            entries.add(((Entry) this.mValues.get(i)).copy());
        }
        LineDataSet copied = new LineDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    /* access modifiers changed from: protected */
    public void copy(LineDataSet lineDataSet) {
        super.copy(lineDataSet);
        lineDataSet.mCircleColors = this.mCircleColors;
        lineDataSet.mCircleHoleColor = this.mCircleHoleColor;
        lineDataSet.mCircleHoleRadius = this.mCircleHoleRadius;
        lineDataSet.mCircleRadius = this.mCircleRadius;
        lineDataSet.mCubicIntensity = this.mCubicIntensity;
        lineDataSet.mDashPathEffect = this.mDashPathEffect;
        lineDataSet.mDrawCircleHole = this.mDrawCircleHole;
        lineDataSet.mDrawCircles = this.mDrawCircleHole;
        lineDataSet.mFillFormatter = this.mFillFormatter;
        lineDataSet.mMode = this.mMode;
    }

    public Mode getMode() {
        return this.mMode;
    }

    public void setMode(Mode mode) {
        this.mMode = mode;
    }

    public void setCubicIntensity(float intensity) {
        if (intensity > 1.0f) {
            intensity = 1.0f;
        }
        if (intensity < 0.05f) {
            intensity = 0.05f;
        }
        this.mCubicIntensity = intensity;
    }

    public float getCubicIntensity() {
        return this.mCubicIntensity;
    }

    public void setCircleRadius(float radius) {
        if (radius >= 1.0f) {
            this.mCircleRadius = Utils.convertDpToPixel(radius);
        } else {
            Log.e("LineDataSet", "Circle radius cannot be < 1");
        }
    }

    public float getCircleRadius() {
        return this.mCircleRadius;
    }

    public void setCircleHoleRadius(float holeRadius) {
        if (holeRadius >= 0.5f) {
            this.mCircleHoleRadius = Utils.convertDpToPixel(holeRadius);
        } else {
            Log.e("LineDataSet", "Circle radius cannot be < 0.5");
        }
    }

    public float getCircleHoleRadius() {
        return this.mCircleHoleRadius;
    }

    @Deprecated
    public void setCircleSize(float size) {
        setCircleRadius(size);
    }

    @Deprecated
    public float getCircleSize() {
        return getCircleRadius();
    }

    public void enableDashedLine(float lineLength, float spaceLength, float phase) {
        this.mDashPathEffect = new DashPathEffect(new float[]{lineLength, spaceLength}, phase);
    }

    public void disableDashedLine() {
        this.mDashPathEffect = null;
    }

    public boolean isDashedLineEnabled() {
        return this.mDashPathEffect != null;
    }

    public DashPathEffect getDashPathEffect() {
        return this.mDashPathEffect;
    }

    public void setDrawCircles(boolean enabled) {
        this.mDrawCircles = enabled;
    }

    public boolean isDrawCirclesEnabled() {
        return this.mDrawCircles;
    }

    @Deprecated
    public boolean isDrawCubicEnabled() {
        return this.mMode == Mode.CUBIC_BEZIER;
    }

    @Deprecated
    public boolean isDrawSteppedEnabled() {
        return this.mMode == Mode.STEPPED;
    }

    public List<Integer> getCircleColors() {
        return this.mCircleColors;
    }

    public int getCircleColor(int index) {
        return this.mCircleColors.get(index).intValue();
    }

    public int getCircleColorCount() {
        return this.mCircleColors.size();
    }

    public void setCircleColors(List<Integer> colors) {
        this.mCircleColors = colors;
    }

    public void setCircleColors(int... colors) {
        this.mCircleColors = ColorTemplate.createColors(colors);
    }

    public void setCircleColors(int[] colors, Context c) {
        List<Integer> clrs = this.mCircleColors;
        if (clrs == null) {
            clrs = new ArrayList<>();
        }
        clrs.clear();
        for (int color : colors) {
            clrs.add(Integer.valueOf(c.getResources().getColor(color)));
        }
        this.mCircleColors = clrs;
    }

    public void setCircleColor(int color) {
        resetCircleColors();
        this.mCircleColors.add(Integer.valueOf(color));
    }

    public void resetCircleColors() {
        if (this.mCircleColors == null) {
            this.mCircleColors = new ArrayList();
        }
        this.mCircleColors.clear();
    }

    public void setCircleHoleColor(int color) {
        this.mCircleHoleColor = color;
    }

    public int getCircleHoleColor() {
        return this.mCircleHoleColor;
    }

    public void setDrawCircleHole(boolean enabled) {
        this.mDrawCircleHole = enabled;
    }

    public boolean isDrawCircleHoleEnabled() {
        return this.mDrawCircleHole;
    }

    public void setFillFormatter(IFillFormatter formatter) {
        if (formatter == null) {
            this.mFillFormatter = new DefaultFillFormatter();
        } else {
            this.mFillFormatter = formatter;
        }
    }

    public IFillFormatter getFillFormatter() {
        return this.mFillFormatter;
    }
}
