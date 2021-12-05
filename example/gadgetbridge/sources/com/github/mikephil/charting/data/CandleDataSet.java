package com.github.mikephil.charting.data;

import android.graphics.Paint;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class CandleDataSet extends LineScatterCandleRadarDataSet<CandleEntry> implements ICandleDataSet {
    private float mBarSpace = 0.1f;
    protected int mDecreasingColor = ColorTemplate.COLOR_SKIP;
    protected Paint.Style mDecreasingPaintStyle = Paint.Style.FILL;
    protected int mIncreasingColor = ColorTemplate.COLOR_SKIP;
    protected Paint.Style mIncreasingPaintStyle = Paint.Style.STROKE;
    protected int mNeutralColor = ColorTemplate.COLOR_SKIP;
    protected int mShadowColor = ColorTemplate.COLOR_SKIP;
    private boolean mShadowColorSameAsCandle = false;
    private float mShadowWidth = 3.0f;
    private boolean mShowCandleBar = true;

    public CandleDataSet(List<CandleEntry> yVals, String label) {
        super(yVals, label);
    }

    public DataSet<CandleEntry> copy() {
        List<CandleEntry> entries = new ArrayList<>();
        for (int i = 0; i < this.mValues.size(); i++) {
            entries.add(((CandleEntry) this.mValues.get(i)).copy());
        }
        CandleDataSet copied = new CandleDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    /* access modifiers changed from: protected */
    public void copy(CandleDataSet candleDataSet) {
        super.copy(candleDataSet);
        candleDataSet.mShadowWidth = this.mShadowWidth;
        candleDataSet.mShowCandleBar = this.mShowCandleBar;
        candleDataSet.mBarSpace = this.mBarSpace;
        candleDataSet.mShadowColorSameAsCandle = this.mShadowColorSameAsCandle;
        candleDataSet.mHighLightColor = this.mHighLightColor;
        candleDataSet.mIncreasingPaintStyle = this.mIncreasingPaintStyle;
        candleDataSet.mDecreasingPaintStyle = this.mDecreasingPaintStyle;
        candleDataSet.mNeutralColor = this.mNeutralColor;
        candleDataSet.mIncreasingColor = this.mIncreasingColor;
        candleDataSet.mDecreasingColor = this.mDecreasingColor;
        candleDataSet.mShadowColor = this.mShadowColor;
    }

    /* access modifiers changed from: protected */
    public void calcMinMax(CandleEntry e) {
        if (e.getLow() < this.mYMin) {
            this.mYMin = e.getLow();
        }
        if (e.getHigh() > this.mYMax) {
            this.mYMax = e.getHigh();
        }
        calcMinMaxX(e);
    }

    /* access modifiers changed from: protected */
    public void calcMinMaxY(CandleEntry e) {
        if (e.getHigh() < this.mYMin) {
            this.mYMin = e.getHigh();
        }
        if (e.getHigh() > this.mYMax) {
            this.mYMax = e.getHigh();
        }
        if (e.getLow() < this.mYMin) {
            this.mYMin = e.getLow();
        }
        if (e.getLow() > this.mYMax) {
            this.mYMax = e.getLow();
        }
    }

    public void setBarSpace(float space) {
        if (space < 0.0f) {
            space = 0.0f;
        }
        if (space > 0.45f) {
            space = 0.45f;
        }
        this.mBarSpace = space;
    }

    public float getBarSpace() {
        return this.mBarSpace;
    }

    public void setShadowWidth(float width) {
        this.mShadowWidth = Utils.convertDpToPixel(width);
    }

    public float getShadowWidth() {
        return this.mShadowWidth;
    }

    public void setShowCandleBar(boolean showCandleBar) {
        this.mShowCandleBar = showCandleBar;
    }

    public boolean getShowCandleBar() {
        return this.mShowCandleBar;
    }

    public void setNeutralColor(int color) {
        this.mNeutralColor = color;
    }

    public int getNeutralColor() {
        return this.mNeutralColor;
    }

    public void setIncreasingColor(int color) {
        this.mIncreasingColor = color;
    }

    public int getIncreasingColor() {
        return this.mIncreasingColor;
    }

    public void setDecreasingColor(int color) {
        this.mDecreasingColor = color;
    }

    public int getDecreasingColor() {
        return this.mDecreasingColor;
    }

    public Paint.Style getIncreasingPaintStyle() {
        return this.mIncreasingPaintStyle;
    }

    public void setIncreasingPaintStyle(Paint.Style paintStyle) {
        this.mIncreasingPaintStyle = paintStyle;
    }

    public Paint.Style getDecreasingPaintStyle() {
        return this.mDecreasingPaintStyle;
    }

    public void setDecreasingPaintStyle(Paint.Style decreasingPaintStyle) {
        this.mDecreasingPaintStyle = decreasingPaintStyle;
    }

    public int getShadowColor() {
        return this.mShadowColor;
    }

    public void setShadowColor(int shadowColor) {
        this.mShadowColor = shadowColor;
    }

    public boolean getShadowColorSameAsCandle() {
        return this.mShadowColorSameAsCandle;
    }

    public void setShadowColorSameAsCandle(boolean shadowColorSameAsCandle) {
        this.mShadowColorSameAsCandle = shadowColorSameAsCandle;
    }
}
