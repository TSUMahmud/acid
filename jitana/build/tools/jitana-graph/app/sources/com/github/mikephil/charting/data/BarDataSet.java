package com.github.mikephil.charting.data;

import android.graphics.Color;
import androidx.core.view.ViewCompat;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import java.util.ArrayList;
import java.util.List;

public class BarDataSet extends BarLineScatterCandleBubbleDataSet<BarEntry> implements IBarDataSet {
    private int mBarBorderColor = ViewCompat.MEASURED_STATE_MASK;
    private float mBarBorderWidth = 0.0f;
    private int mBarShadowColor = Color.rgb(215, 215, 215);
    private int mEntryCountStacks = 0;
    private int mHighLightAlpha = 120;
    private String[] mStackLabels = {"Stack"};
    private int mStackSize = 1;

    public BarDataSet(List<BarEntry> yVals, String label) {
        super(yVals, label);
        this.mHighLightColor = Color.rgb(0, 0, 0);
        calcStackSize(yVals);
        calcEntryCountIncludingStacks(yVals);
    }

    public DataSet<BarEntry> copy() {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < this.mValues.size(); i++) {
            entries.add(((BarEntry) this.mValues.get(i)).copy());
        }
        BarDataSet copied = new BarDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    /* access modifiers changed from: protected */
    public void copy(BarDataSet barDataSet) {
        super.copy(barDataSet);
        barDataSet.mStackSize = this.mStackSize;
        barDataSet.mBarShadowColor = this.mBarShadowColor;
        barDataSet.mBarBorderWidth = this.mBarBorderWidth;
        barDataSet.mStackLabels = this.mStackLabels;
        barDataSet.mHighLightAlpha = this.mHighLightAlpha;
    }

    private void calcEntryCountIncludingStacks(List<BarEntry> yVals) {
        this.mEntryCountStacks = 0;
        for (int i = 0; i < yVals.size(); i++) {
            float[] vals = yVals.get(i).getYVals();
            if (vals == null) {
                this.mEntryCountStacks++;
            } else {
                this.mEntryCountStacks += vals.length;
            }
        }
    }

    private void calcStackSize(List<BarEntry> yVals) {
        for (int i = 0; i < yVals.size(); i++) {
            float[] vals = yVals.get(i).getYVals();
            if (vals != null && vals.length > this.mStackSize) {
                this.mStackSize = vals.length;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void calcMinMax(BarEntry e) {
        if (e != null && !Float.isNaN(e.getY())) {
            if (e.getYVals() == null) {
                if (e.getY() < this.mYMin) {
                    this.mYMin = e.getY();
                }
                if (e.getY() > this.mYMax) {
                    this.mYMax = e.getY();
                }
            } else {
                if ((-e.getNegativeSum()) < this.mYMin) {
                    this.mYMin = -e.getNegativeSum();
                }
                if (e.getPositiveSum() > this.mYMax) {
                    this.mYMax = e.getPositiveSum();
                }
            }
            calcMinMaxX(e);
        }
    }

    public int getStackSize() {
        return this.mStackSize;
    }

    public boolean isStacked() {
        return this.mStackSize > 1;
    }

    public int getEntryCountStacks() {
        return this.mEntryCountStacks;
    }

    public void setBarShadowColor(int color) {
        this.mBarShadowColor = color;
    }

    public int getBarShadowColor() {
        return this.mBarShadowColor;
    }

    public void setBarBorderWidth(float width) {
        this.mBarBorderWidth = width;
    }

    public float getBarBorderWidth() {
        return this.mBarBorderWidth;
    }

    public void setBarBorderColor(int color) {
        this.mBarBorderColor = color;
    }

    public int getBarBorderColor() {
        return this.mBarBorderColor;
    }

    public void setHighLightAlpha(int alpha) {
        this.mHighLightAlpha = alpha;
    }

    public int getHighLightAlpha() {
        return this.mHighLightAlpha;
    }

    public void setStackLabels(String[] labels) {
        this.mStackLabels = labels;
    }

    public String[] getStackLabels() {
        return this.mStackLabels;
    }
}
