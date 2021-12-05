package com.github.mikephil.charting.data;

import android.graphics.Typeface;
import android.util.Log;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import java.util.ArrayList;
import java.util.List;

public abstract class ChartData<T extends IDataSet<? extends Entry>> {
    protected List<T> mDataSets;
    protected float mLeftAxisMax;
    protected float mLeftAxisMin;
    protected float mRightAxisMax;
    protected float mRightAxisMin;
    protected float mXMax;
    protected float mXMin;
    protected float mYMax;
    protected float mYMin;

    public ChartData() {
        this.mYMax = -3.4028235E38f;
        this.mYMin = Float.MAX_VALUE;
        this.mXMax = -3.4028235E38f;
        this.mXMin = Float.MAX_VALUE;
        this.mLeftAxisMax = -3.4028235E38f;
        this.mLeftAxisMin = Float.MAX_VALUE;
        this.mRightAxisMax = -3.4028235E38f;
        this.mRightAxisMin = Float.MAX_VALUE;
        this.mDataSets = new ArrayList();
    }

    public ChartData(T... dataSets) {
        this.mYMax = -3.4028235E38f;
        this.mYMin = Float.MAX_VALUE;
        this.mXMax = -3.4028235E38f;
        this.mXMin = Float.MAX_VALUE;
        this.mLeftAxisMax = -3.4028235E38f;
        this.mLeftAxisMin = Float.MAX_VALUE;
        this.mRightAxisMax = -3.4028235E38f;
        this.mRightAxisMin = Float.MAX_VALUE;
        this.mDataSets = arrayToList(dataSets);
        notifyDataChanged();
    }

    private List<T> arrayToList(T[] array) {
        List<T> list = new ArrayList<>();
        for (T set : array) {
            list.add(set);
        }
        return list;
    }

    public ChartData(List<T> sets) {
        this.mYMax = -3.4028235E38f;
        this.mYMin = Float.MAX_VALUE;
        this.mXMax = -3.4028235E38f;
        this.mXMin = Float.MAX_VALUE;
        this.mLeftAxisMax = -3.4028235E38f;
        this.mLeftAxisMin = Float.MAX_VALUE;
        this.mRightAxisMax = -3.4028235E38f;
        this.mRightAxisMin = Float.MAX_VALUE;
        this.mDataSets = sets;
        notifyDataChanged();
    }

    public void notifyDataChanged() {
        calcMinMax();
    }

    public void calcMinMaxY(float fromX, float toX) {
        for (T set : this.mDataSets) {
            set.calcMinMaxY(fromX, toX);
        }
        calcMinMax();
    }

    /* access modifiers changed from: protected */
    public void calcMinMax() {
        List<T> list = this.mDataSets;
        if (list != null) {
            this.mYMax = -3.4028235E38f;
            this.mYMin = Float.MAX_VALUE;
            this.mXMax = -3.4028235E38f;
            this.mXMin = Float.MAX_VALUE;
            for (T set : list) {
                calcMinMax(set);
            }
            this.mLeftAxisMax = -3.4028235E38f;
            this.mLeftAxisMin = Float.MAX_VALUE;
            this.mRightAxisMax = -3.4028235E38f;
            this.mRightAxisMin = Float.MAX_VALUE;
            T firstLeft = getFirstLeft(this.mDataSets);
            if (firstLeft != null) {
                this.mLeftAxisMax = firstLeft.getYMax();
                this.mLeftAxisMin = firstLeft.getYMin();
                for (T dataSet : this.mDataSets) {
                    if (dataSet.getAxisDependency() == YAxis.AxisDependency.LEFT) {
                        if (dataSet.getYMin() < this.mLeftAxisMin) {
                            this.mLeftAxisMin = dataSet.getYMin();
                        }
                        if (dataSet.getYMax() > this.mLeftAxisMax) {
                            this.mLeftAxisMax = dataSet.getYMax();
                        }
                    }
                }
            }
            T firstRight = getFirstRight(this.mDataSets);
            if (firstRight != null) {
                this.mRightAxisMax = firstRight.getYMax();
                this.mRightAxisMin = firstRight.getYMin();
                for (T dataSet2 : this.mDataSets) {
                    if (dataSet2.getAxisDependency() == YAxis.AxisDependency.RIGHT) {
                        if (dataSet2.getYMin() < this.mRightAxisMin) {
                            this.mRightAxisMin = dataSet2.getYMin();
                        }
                        if (dataSet2.getYMax() > this.mRightAxisMax) {
                            this.mRightAxisMax = dataSet2.getYMax();
                        }
                    }
                }
            }
        }
    }

    public int getDataSetCount() {
        List<T> list = this.mDataSets;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public float getYMin() {
        return this.mYMin;
    }

    public float getYMin(YAxis.AxisDependency axis) {
        if (axis == YAxis.AxisDependency.LEFT) {
            float f = this.mLeftAxisMin;
            if (f == Float.MAX_VALUE) {
                return this.mRightAxisMin;
            }
            return f;
        }
        float f2 = this.mRightAxisMin;
        if (f2 == Float.MAX_VALUE) {
            return this.mLeftAxisMin;
        }
        return f2;
    }

    public float getYMax() {
        return this.mYMax;
    }

    public float getYMax(YAxis.AxisDependency axis) {
        if (axis == YAxis.AxisDependency.LEFT) {
            float f = this.mLeftAxisMax;
            if (f == -3.4028235E38f) {
                return this.mRightAxisMax;
            }
            return f;
        }
        float f2 = this.mRightAxisMax;
        if (f2 == -3.4028235E38f) {
            return this.mLeftAxisMax;
        }
        return f2;
    }

    public float getXMin() {
        return this.mXMin;
    }

    public float getXMax() {
        return this.mXMax;
    }

    public List<T> getDataSets() {
        return this.mDataSets;
    }

    /* access modifiers changed from: protected */
    public int getDataSetIndexByLabel(List<T> dataSets, String label, boolean ignorecase) {
        if (ignorecase) {
            for (int i = 0; i < dataSets.size(); i++) {
                if (label.equalsIgnoreCase(((IDataSet) dataSets.get(i)).getLabel())) {
                    return i;
                }
            }
            return -1;
        }
        for (int i2 = 0; i2 < dataSets.size(); i2++) {
            if (label.equals(((IDataSet) dataSets.get(i2)).getLabel())) {
                return i2;
            }
        }
        return -1;
    }

    public String[] getDataSetLabels() {
        String[] types = new String[this.mDataSets.size()];
        for (int i = 0; i < this.mDataSets.size(); i++) {
            types[i] = ((IDataSet) this.mDataSets.get(i)).getLabel();
        }
        return types;
    }

    public Entry getEntryForHighlight(Highlight highlight) {
        if (highlight.getDataSetIndex() >= this.mDataSets.size()) {
            return null;
        }
        return ((IDataSet) this.mDataSets.get(highlight.getDataSetIndex())).getEntryForXValue(highlight.getX(), highlight.getY());
    }

    public T getDataSetByLabel(String label, boolean ignorecase) {
        int index = getDataSetIndexByLabel(this.mDataSets, label, ignorecase);
        if (index < 0 || index >= this.mDataSets.size()) {
            return null;
        }
        return (IDataSet) this.mDataSets.get(index);
    }

    public T getDataSetByIndex(int index) {
        List<T> list = this.mDataSets;
        if (list == null || index < 0 || index >= list.size()) {
            return null;
        }
        return (IDataSet) this.mDataSets.get(index);
    }

    public void addDataSet(T d) {
        if (d != null) {
            calcMinMax(d);
            this.mDataSets.add(d);
        }
    }

    public boolean removeDataSet(T d) {
        if (d == null) {
            return false;
        }
        boolean removed = this.mDataSets.remove(d);
        if (removed) {
            calcMinMax();
        }
        return removed;
    }

    public boolean removeDataSet(int index) {
        if (index >= this.mDataSets.size() || index < 0) {
            return false;
        }
        return removeDataSet((IDataSet) this.mDataSets.get(index));
    }

    public void addEntry(Entry e, int dataSetIndex) {
        if (this.mDataSets.size() <= dataSetIndex || dataSetIndex < 0) {
            Log.e("addEntry", "Cannot add Entry because dataSetIndex too high or too low.");
            return;
        }
        IDataSet set = (IDataSet) this.mDataSets.get(dataSetIndex);
        if (set.addEntry(e)) {
            calcMinMax(e, set.getAxisDependency());
        }
    }

    /* access modifiers changed from: protected */
    public void calcMinMax(Entry e, YAxis.AxisDependency axis) {
        if (this.mYMax < e.getY()) {
            this.mYMax = e.getY();
        }
        if (this.mYMin > e.getY()) {
            this.mYMin = e.getY();
        }
        if (this.mXMax < e.getX()) {
            this.mXMax = e.getX();
        }
        if (this.mXMin > e.getX()) {
            this.mXMin = e.getX();
        }
        if (axis == YAxis.AxisDependency.LEFT) {
            if (this.mLeftAxisMax < e.getY()) {
                this.mLeftAxisMax = e.getY();
            }
            if (this.mLeftAxisMin > e.getY()) {
                this.mLeftAxisMin = e.getY();
                return;
            }
            return;
        }
        if (this.mRightAxisMax < e.getY()) {
            this.mRightAxisMax = e.getY();
        }
        if (this.mRightAxisMin > e.getY()) {
            this.mRightAxisMin = e.getY();
        }
    }

    /* access modifiers changed from: protected */
    public void calcMinMax(T d) {
        if (this.mYMax < d.getYMax()) {
            this.mYMax = d.getYMax();
        }
        if (this.mYMin > d.getYMin()) {
            this.mYMin = d.getYMin();
        }
        if (this.mXMax < d.getXMax()) {
            this.mXMax = d.getXMax();
        }
        if (this.mXMin > d.getXMin()) {
            this.mXMin = d.getXMin();
        }
        if (d.getAxisDependency() == YAxis.AxisDependency.LEFT) {
            if (this.mLeftAxisMax < d.getYMax()) {
                this.mLeftAxisMax = d.getYMax();
            }
            if (this.mLeftAxisMin > d.getYMin()) {
                this.mLeftAxisMin = d.getYMin();
                return;
            }
            return;
        }
        if (this.mRightAxisMax < d.getYMax()) {
            this.mRightAxisMax = d.getYMax();
        }
        if (this.mRightAxisMin > d.getYMin()) {
            this.mRightAxisMin = d.getYMin();
        }
    }

    public boolean removeEntry(Entry e, int dataSetIndex) {
        IDataSet set;
        if (e == null || dataSetIndex >= this.mDataSets.size() || (set = (IDataSet) this.mDataSets.get(dataSetIndex)) == null) {
            return false;
        }
        boolean removed = set.removeEntry(e);
        if (removed) {
            calcMinMax();
        }
        return removed;
    }

    public boolean removeEntry(float xValue, int dataSetIndex) {
        Entry e;
        if (dataSetIndex < this.mDataSets.size() && (e = ((IDataSet) this.mDataSets.get(dataSetIndex)).getEntryForXValue(xValue, Float.NaN)) != null) {
            return removeEntry(e, dataSetIndex);
        }
        return false;
    }

    public T getDataSetForEntry(Entry e) {
        if (e == null) {
            return null;
        }
        for (int i = 0; i < this.mDataSets.size(); i++) {
            T set = (IDataSet) this.mDataSets.get(i);
            for (int j = 0; j < set.getEntryCount(); j++) {
                if (e.equalTo(set.getEntryForXValue(e.getX(), e.getY()))) {
                    return set;
                }
            }
        }
        return null;
    }

    public int[] getColors() {
        if (this.mDataSets == null) {
            return null;
        }
        int clrcnt = 0;
        for (int i = 0; i < this.mDataSets.size(); i++) {
            clrcnt += ((IDataSet) this.mDataSets.get(i)).getColors().size();
        }
        int[] colors = new int[clrcnt];
        int cnt = 0;
        for (int i2 = 0; i2 < this.mDataSets.size(); i2++) {
            for (Integer clr : ((IDataSet) this.mDataSets.get(i2)).getColors()) {
                colors[cnt] = clr.intValue();
                cnt++;
            }
        }
        return colors;
    }

    public int getIndexOfDataSet(T dataSet) {
        return this.mDataSets.indexOf(dataSet);
    }

    /* access modifiers changed from: protected */
    public T getFirstLeft(List<T> sets) {
        for (T dataSet : sets) {
            if (dataSet.getAxisDependency() == YAxis.AxisDependency.LEFT) {
                return dataSet;
            }
        }
        return null;
    }

    public T getFirstRight(List<T> sets) {
        for (T dataSet : sets) {
            if (dataSet.getAxisDependency() == YAxis.AxisDependency.RIGHT) {
                return dataSet;
            }
        }
        return null;
    }

    public void setValueFormatter(ValueFormatter f) {
        if (f != null) {
            for (T set : this.mDataSets) {
                set.setValueFormatter(f);
            }
        }
    }

    public void setValueTextColor(int color) {
        for (T set : this.mDataSets) {
            set.setValueTextColor(color);
        }
    }

    public void setValueTextColors(List<Integer> colors) {
        for (T set : this.mDataSets) {
            set.setValueTextColors(colors);
        }
    }

    public void setValueTypeface(Typeface tf) {
        for (T set : this.mDataSets) {
            set.setValueTypeface(tf);
        }
    }

    public void setValueTextSize(float size) {
        for (T set : this.mDataSets) {
            set.setValueTextSize(size);
        }
    }

    public void setDrawValues(boolean enabled) {
        for (T set : this.mDataSets) {
            set.setDrawValues(enabled);
        }
    }

    public void setHighlightEnabled(boolean enabled) {
        for (T set : this.mDataSets) {
            set.setHighlightEnabled(enabled);
        }
    }

    public boolean isHighlightEnabled() {
        for (T set : this.mDataSets) {
            if (!set.isHighlightEnabled()) {
                return false;
            }
        }
        return true;
    }

    public void clearValues() {
        List<T> list = this.mDataSets;
        if (list != null) {
            list.clear();
        }
        notifyDataChanged();
    }

    public boolean contains(T dataSet) {
        for (T set : this.mDataSets) {
            if (set.equals(dataSet)) {
                return true;
            }
        }
        return false;
    }

    public int getEntryCount() {
        int count = 0;
        for (T set : this.mDataSets) {
            count += set.getEntryCount();
        }
        return count;
    }

    public T getMaxEntryCountSet() {
        List<T> list = this.mDataSets;
        if (list == null || list.isEmpty()) {
            return null;
        }
        T max = (IDataSet) this.mDataSets.get(0);
        for (T set : this.mDataSets) {
            if (set.getEntryCount() > max.getEntryCount()) {
                max = set;
            }
        }
        return max;
    }
}
