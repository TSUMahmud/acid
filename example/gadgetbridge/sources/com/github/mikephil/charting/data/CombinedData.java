package com.github.mikephil.charting.data;

import android.util.Log;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import java.util.ArrayList;
import java.util.List;

public class CombinedData extends BarLineScatterCandleBubbleData<IBarLineScatterCandleBubbleDataSet<? extends Entry>> {
    private BarData mBarData;
    private BubbleData mBubbleData;
    private CandleData mCandleData;
    private LineData mLineData;
    private ScatterData mScatterData;

    public void setData(LineData data) {
        this.mLineData = data;
        notifyDataChanged();
    }

    public void setData(BarData data) {
        this.mBarData = data;
        notifyDataChanged();
    }

    public void setData(ScatterData data) {
        this.mScatterData = data;
        notifyDataChanged();
    }

    public void setData(CandleData data) {
        this.mCandleData = data;
        notifyDataChanged();
    }

    public void setData(BubbleData data) {
        this.mBubbleData = data;
        notifyDataChanged();
    }

    public void calcMinMax() {
        if (this.mDataSets == null) {
            this.mDataSets = new ArrayList();
        }
        this.mDataSets.clear();
        this.mYMax = -3.4028235E38f;
        this.mYMin = Float.MAX_VALUE;
        this.mXMax = -3.4028235E38f;
        this.mXMin = Float.MAX_VALUE;
        this.mLeftAxisMax = -3.4028235E38f;
        this.mLeftAxisMin = Float.MAX_VALUE;
        this.mRightAxisMax = -3.4028235E38f;
        this.mRightAxisMin = Float.MAX_VALUE;
        for (ChartData data : getAllData()) {
            data.calcMinMax();
            this.mDataSets.addAll(data.getDataSets());
            if (data.getYMax() > this.mYMax) {
                this.mYMax = data.getYMax();
            }
            if (data.getYMin() < this.mYMin) {
                this.mYMin = data.getYMin();
            }
            if (data.getXMax() > this.mXMax) {
                this.mXMax = data.getXMax();
            }
            if (data.getXMin() < this.mXMin) {
                this.mXMin = data.getXMin();
            }
            if (data.mLeftAxisMax > this.mLeftAxisMax) {
                this.mLeftAxisMax = data.mLeftAxisMax;
            }
            if (data.mLeftAxisMin < this.mLeftAxisMin) {
                this.mLeftAxisMin = data.mLeftAxisMin;
            }
            if (data.mRightAxisMax > this.mRightAxisMax) {
                this.mRightAxisMax = data.mRightAxisMax;
            }
            if (data.mRightAxisMin < this.mRightAxisMin) {
                this.mRightAxisMin = data.mRightAxisMin;
            }
        }
    }

    public BubbleData getBubbleData() {
        return this.mBubbleData;
    }

    public LineData getLineData() {
        return this.mLineData;
    }

    public BarData getBarData() {
        return this.mBarData;
    }

    public ScatterData getScatterData() {
        return this.mScatterData;
    }

    public CandleData getCandleData() {
        return this.mCandleData;
    }

    public List<BarLineScatterCandleBubbleData> getAllData() {
        List<BarLineScatterCandleBubbleData> data = new ArrayList<>();
        LineData lineData = this.mLineData;
        if (lineData != null) {
            data.add(lineData);
        }
        BarData barData = this.mBarData;
        if (barData != null) {
            data.add(barData);
        }
        ScatterData scatterData = this.mScatterData;
        if (scatterData != null) {
            data.add(scatterData);
        }
        CandleData candleData = this.mCandleData;
        if (candleData != null) {
            data.add(candleData);
        }
        BubbleData bubbleData = this.mBubbleData;
        if (bubbleData != null) {
            data.add(bubbleData);
        }
        return data;
    }

    public BarLineScatterCandleBubbleData getDataByIndex(int index) {
        return getAllData().get(index);
    }

    public void notifyDataChanged() {
        LineData lineData = this.mLineData;
        if (lineData != null) {
            lineData.notifyDataChanged();
        }
        BarData barData = this.mBarData;
        if (barData != null) {
            barData.notifyDataChanged();
        }
        CandleData candleData = this.mCandleData;
        if (candleData != null) {
            candleData.notifyDataChanged();
        }
        ScatterData scatterData = this.mScatterData;
        if (scatterData != null) {
            scatterData.notifyDataChanged();
        }
        BubbleData bubbleData = this.mBubbleData;
        if (bubbleData != null) {
            bubbleData.notifyDataChanged();
        }
        calcMinMax();
    }

    /* JADX WARNING: Removed duplicated region for block: B:9:0x003d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.github.mikephil.charting.data.Entry getEntryForHighlight(com.github.mikephil.charting.highlight.Highlight r8) {
        /*
            r7 = this;
            int r0 = r8.getDataIndex()
            java.util.List r1 = r7.getAllData()
            int r1 = r1.size()
            r2 = 0
            if (r0 < r1) goto L_0x0010
            return r2
        L_0x0010:
            int r0 = r8.getDataIndex()
            com.github.mikephil.charting.data.BarLineScatterCandleBubbleData r0 = r7.getDataByIndex(r0)
            int r1 = r8.getDataSetIndex()
            int r3 = r0.getDataSetCount()
            if (r1 < r3) goto L_0x0023
            return r2
        L_0x0023:
            int r1 = r8.getDataSetIndex()
            com.github.mikephil.charting.interfaces.datasets.IDataSet r1 = r0.getDataSetByIndex(r1)
            float r3 = r8.getX()
            java.util.List r1 = r1.getEntriesForXValue(r3)
            java.util.Iterator r3 = r1.iterator()
        L_0x0037:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x005c
            java.lang.Object r4 = r3.next()
            com.github.mikephil.charting.data.Entry r4 = (com.github.mikephil.charting.data.Entry) r4
            float r5 = r4.getY()
            float r6 = r8.getY()
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 == 0) goto L_0x005b
            float r5 = r8.getY()
            boolean r5 = java.lang.Float.isNaN(r5)
            if (r5 == 0) goto L_0x005a
            goto L_0x005b
        L_0x005a:
            goto L_0x0037
        L_0x005b:
            return r4
        L_0x005c:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.mikephil.charting.data.CombinedData.getEntryForHighlight(com.github.mikephil.charting.highlight.Highlight):com.github.mikephil.charting.data.Entry");
    }

    public IBarLineScatterCandleBubbleDataSet<? extends Entry> getDataSetByHighlight(Highlight highlight) {
        if (highlight.getDataIndex() >= getAllData().size()) {
            return null;
        }
        BarLineScatterCandleBubbleData data = getDataByIndex(highlight.getDataIndex());
        if (highlight.getDataSetIndex() >= data.getDataSetCount()) {
            return null;
        }
        return (IBarLineScatterCandleBubbleDataSet) data.getDataSets().get(highlight.getDataSetIndex());
    }

    public int getDataIndex(ChartData data) {
        return getAllData().indexOf(data);
    }

    /* JADX WARNING: Removed duplicated region for block: B:1:0x0009 A[LOOP:0: B:1:0x0009->B:4:0x0019, LOOP_START, PHI: r1 
      PHI: (r1v1 'success' boolean) = (r1v0 'success' boolean), (r1v3 'success' boolean) binds: [B:0:0x0000, B:4:0x0019] A[DONT_GENERATE, DONT_INLINE]] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean removeDataSet(com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet<? extends com.github.mikephil.charting.data.Entry> r5) {
        /*
            r4 = this;
            java.util.List r0 = r4.getAllData()
            r1 = 0
            java.util.Iterator r2 = r0.iterator()
        L_0x0009:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x001d
            java.lang.Object r3 = r2.next()
            com.github.mikephil.charting.data.ChartData r3 = (com.github.mikephil.charting.data.ChartData) r3
            boolean r1 = r3.removeDataSet(r5)
            if (r1 == 0) goto L_0x001c
            goto L_0x001d
        L_0x001c:
            goto L_0x0009
        L_0x001d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.mikephil.charting.data.CombinedData.removeDataSet(com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet):boolean");
    }

    @Deprecated
    public boolean removeDataSet(int index) {
        Log.e(Chart.LOG_TAG, "removeDataSet(int index) not supported for CombinedData");
        return false;
    }

    @Deprecated
    public boolean removeEntry(Entry e, int dataSetIndex) {
        Log.e(Chart.LOG_TAG, "removeEntry(...) not supported for CombinedData");
        return false;
    }

    @Deprecated
    public boolean removeEntry(float xValue, int dataSetIndex) {
        Log.e(Chart.LOG_TAG, "removeEntry(...) not supported for CombinedData");
        return false;
    }
}
