package com.github.mikephil.charting.data;

import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import java.util.List;

public class PieData extends ChartData<IPieDataSet> {
    public PieData() {
    }

    public PieData(IPieDataSet dataSet) {
        super((T[]) new IPieDataSet[]{dataSet});
    }

    public void setDataSet(IPieDataSet dataSet) {
        this.mDataSets.clear();
        this.mDataSets.add(dataSet);
        notifyDataChanged();
    }

    public IPieDataSet getDataSet() {
        return (IPieDataSet) this.mDataSets.get(0);
    }

    public IPieDataSet getDataSetByIndex(int index) {
        if (index == 0) {
            return getDataSet();
        }
        return null;
    }

    public IPieDataSet getDataSetByLabel(String label, boolean ignorecase) {
        List list = this.mDataSets;
        if (ignorecase) {
            if (label.equalsIgnoreCase(((IPieDataSet) list.get(0)).getLabel())) {
                return (IPieDataSet) this.mDataSets.get(0);
            }
            return null;
        } else if (label.equals(((IPieDataSet) list.get(0)).getLabel())) {
            return (IPieDataSet) this.mDataSets.get(0);
        } else {
            return null;
        }
    }

    public Entry getEntryForHighlight(Highlight highlight) {
        return getDataSet().getEntryForIndex((int) highlight.getX());
    }

    public float getYValueSum() {
        float sum = 0.0f;
        for (int i = 0; i < getDataSet().getEntryCount(); i++) {
            sum += ((PieEntry) getDataSet().getEntryForIndex(i)).getY();
        }
        return sum;
    }
}
