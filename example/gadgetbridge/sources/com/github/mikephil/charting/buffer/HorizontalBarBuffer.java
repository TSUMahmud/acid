package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

public class HorizontalBarBuffer extends BarBuffer {
    public HorizontalBarBuffer(int size, int dataSetCount, boolean containsStacks) {
        super(size, dataSetCount, containsStacks);
    }

    public void feed(IBarDataSet data) {
        float size;
        float right;
        float left;
        float f;
        float yStart;
        float y;
        float right2;
        float left2;
        float size2 = ((float) data.getEntryCount()) * this.phaseX;
        float barWidthHalf = this.mBarWidth / 2.0f;
        int i = 0;
        while (((float) i) < size2) {
            BarEntry e = (BarEntry) data.getEntryForIndex(i);
            if (e == null) {
                size = size2;
            } else {
                float x = e.getX();
                float y2 = e.getY();
                float[] vals = e.getYVals();
                float f2 = 0.0f;
                if (!this.mContainsStacks) {
                    size = size2;
                    BarEntry barEntry = e;
                } else if (vals == null) {
                    size = size2;
                    BarEntry barEntry2 = e;
                } else {
                    float posY = 0.0f;
                    float negY = -e.getNegativeSum();
                    int k = 0;
                    while (k < vals.length) {
                        float value = vals[k];
                        if (value >= f2) {
                            y = posY;
                            yStart = posY + value;
                            posY = yStart;
                        } else {
                            y = negY;
                            float yStart2 = Math.abs(value) + negY;
                            negY += Math.abs(value);
                            yStart = yStart2;
                        }
                        float bottom = x - barWidthHalf;
                        float top = x + barWidthHalf;
                        float size3 = size2;
                        if (this.mInverted) {
                            left2 = y >= yStart ? y : yStart;
                            right2 = y <= yStart ? y : yStart;
                        } else {
                            right2 = y >= yStart ? y : yStart;
                            left2 = y <= yStart ? y : yStart;
                        }
                        addBar(left2 * this.phaseY, top, this.phaseY * right2, bottom);
                        k++;
                        IBarDataSet iBarDataSet = data;
                        size2 = size3;
                        e = e;
                        f2 = 0.0f;
                    }
                    size = size2;
                    BarEntry barEntry3 = e;
                }
                float bottom2 = x - barWidthHalf;
                float top2 = x + barWidthHalf;
                if (this.mInverted) {
                    f = 0.0f;
                    left = y2 >= 0.0f ? y2 : 0.0f;
                    right = y2 <= 0.0f ? y2 : 0.0f;
                } else {
                    f = 0.0f;
                    right = y2 >= 0.0f ? y2 : 0.0f;
                    left = y2 <= 0.0f ? y2 : 0.0f;
                }
                if (right > f) {
                    right *= this.phaseY;
                } else {
                    left *= this.phaseY;
                }
                addBar(left, top2, right, bottom2);
            }
            i++;
            size2 = size;
        }
        reset();
    }
}
