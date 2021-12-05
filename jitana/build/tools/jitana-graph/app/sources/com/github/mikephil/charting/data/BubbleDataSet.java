package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class BubbleDataSet extends BarLineScatterCandleBubbleDataSet<BubbleEntry> implements IBubbleDataSet {
    private float mHighlightCircleWidth = 2.5f;
    protected float mMaxSize;
    protected boolean mNormalizeSize = true;

    public BubbleDataSet(List<BubbleEntry> yVals, String label) {
        super(yVals, label);
    }

    public void setHighlightCircleWidth(float width) {
        this.mHighlightCircleWidth = Utils.convertDpToPixel(width);
    }

    public float getHighlightCircleWidth() {
        return this.mHighlightCircleWidth;
    }

    /* access modifiers changed from: protected */
    public void calcMinMax(BubbleEntry e) {
        super.calcMinMax(e);
        float size = e.getSize();
        if (size > this.mMaxSize) {
            this.mMaxSize = size;
        }
    }

    public DataSet<BubbleEntry> copy() {
        List<BubbleEntry> entries = new ArrayList<>();
        for (int i = 0; i < this.mValues.size(); i++) {
            entries.add(((BubbleEntry) this.mValues.get(i)).copy());
        }
        BubbleDataSet copied = new BubbleDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    /* access modifiers changed from: protected */
    public void copy(BubbleDataSet bubbleDataSet) {
        bubbleDataSet.mHighlightCircleWidth = this.mHighlightCircleWidth;
        bubbleDataSet.mNormalizeSize = this.mNormalizeSize;
    }

    public float getMaxSize() {
        return this.mMaxSize;
    }

    public boolean isNormalizeSizeEnabled() {
        return this.mNormalizeSize;
    }

    public void setNormalizeSizeEnabled(boolean normalizeSize) {
        this.mNormalizeSize = normalizeSize;
    }
}
