package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.ArrayList;
import java.util.List;

public class LegendRenderer extends Renderer {
    protected List<LegendEntry> computedEntries = new ArrayList(16);
    protected Paint.FontMetrics legendFontMetrics = new Paint.FontMetrics();
    protected Legend mLegend;
    protected Paint mLegendFormPaint;
    protected Paint mLegendLabelPaint;
    private Path mLineFormPath = new Path();

    public LegendRenderer(ViewPortHandler viewPortHandler, Legend legend) {
        super(viewPortHandler);
        this.mLegend = legend;
        this.mLegendLabelPaint = new Paint(1);
        this.mLegendLabelPaint.setTextSize(Utils.convertDpToPixel(9.0f));
        this.mLegendLabelPaint.setTextAlign(Paint.Align.LEFT);
        this.mLegendFormPaint = new Paint(1);
        this.mLegendFormPaint.setStyle(Paint.Style.FILL);
    }

    public Paint getLabelPaint() {
        return this.mLegendLabelPaint;
    }

    public Paint getFormPaint() {
        return this.mLegendFormPaint;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: com.github.mikephil.charting.data.ChartData<?>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: com.github.mikephil.charting.data.ChartData<?>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: com.github.mikephil.charting.data.ChartData<?>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: com.github.mikephil.charting.data.ChartData<?>} */
    /* JADX WARNING: type inference failed for: r3v6, types: [com.github.mikephil.charting.interfaces.datasets.IDataSet] */
    /* JADX WARNING: type inference failed for: r7v1, types: [com.github.mikephil.charting.interfaces.datasets.IDataSet] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void computeLegend(com.github.mikephil.charting.data.ChartData<?> r20) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            com.github.mikephil.charting.components.Legend r2 = r0.mLegend
            boolean r2 = r2.isLegendCustom()
            if (r2 != 0) goto L_0x01e0
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r2 = r0.computedEntries
            r2.clear()
            r2 = 0
        L_0x0012:
            int r3 = r20.getDataSetCount()
            if (r2 >= r3) goto L_0x01c4
            com.github.mikephil.charting.interfaces.datasets.IDataSet r3 = r1.getDataSetByIndex(r2)
            java.util.List r4 = r3.getColors()
            int r5 = r3.getEntryCount()
            boolean r6 = r3 instanceof com.github.mikephil.charting.interfaces.datasets.IBarDataSet
            if (r6 == 0) goto L_0x009f
            r6 = r3
            com.github.mikephil.charting.interfaces.datasets.IBarDataSet r6 = (com.github.mikephil.charting.interfaces.datasets.IBarDataSet) r6
            boolean r6 = r6.isStacked()
            if (r6 == 0) goto L_0x009f
            r6 = r3
            com.github.mikephil.charting.interfaces.datasets.IBarDataSet r6 = (com.github.mikephil.charting.interfaces.datasets.IBarDataSet) r6
            java.lang.String[] r7 = r6.getStackLabels()
            r8 = 0
        L_0x0039:
            int r9 = r4.size()
            if (r8 >= r9) goto L_0x007b
            int r9 = r6.getStackSize()
            if (r8 >= r9) goto L_0x007b
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r9 = r0.computedEntries
            com.github.mikephil.charting.components.LegendEntry r15 = new com.github.mikephil.charting.components.LegendEntry
            int r10 = r7.length
            int r10 = r8 % r10
            r11 = r7[r10]
            com.github.mikephil.charting.components.Legend$LegendForm r12 = r3.getForm()
            float r13 = r3.getFormSize()
            float r14 = r3.getFormLineWidth()
            android.graphics.DashPathEffect r16 = r3.getFormLineDashEffect()
            java.lang.Object r10 = r4.get(r8)
            java.lang.Integer r10 = (java.lang.Integer) r10
            int r17 = r10.intValue()
            r10 = r15
            r18 = r7
            r7 = r15
            r15 = r16
            r16 = r17
            r10.<init>(r11, r12, r13, r14, r15, r16)
            r9.add(r7)
            int r8 = r8 + 1
            r7 = r18
            goto L_0x0039
        L_0x007b:
            r18 = r7
            java.lang.String r7 = r6.getLabel()
            if (r7 == 0) goto L_0x009c
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r7 = r0.computedEntries
            com.github.mikephil.charting.components.LegendEntry r15 = new com.github.mikephil.charting.components.LegendEntry
            java.lang.String r9 = r3.getLabel()
            com.github.mikephil.charting.components.Legend$LegendForm r10 = com.github.mikephil.charting.components.Legend.LegendForm.NONE
            r11 = 2143289344(0x7fc00000, float:NaN)
            r12 = 2143289344(0x7fc00000, float:NaN)
            r13 = 0
            r14 = 1122867(0x112233, float:1.573472E-39)
            r8 = r15
            r8.<init>(r9, r10, r11, r12, r13, r14)
            r7.add(r15)
        L_0x009c:
            r6 = r1
            goto L_0x01bf
        L_0x009f:
            boolean r6 = r3 instanceof com.github.mikephil.charting.interfaces.datasets.IPieDataSet
            if (r6 == 0) goto L_0x0109
            r6 = r3
            com.github.mikephil.charting.interfaces.datasets.IPieDataSet r6 = (com.github.mikephil.charting.interfaces.datasets.IPieDataSet) r6
            r7 = 0
        L_0x00a7:
            int r8 = r4.size()
            if (r7 >= r8) goto L_0x00e6
            if (r7 >= r5) goto L_0x00e6
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r8 = r0.computedEntries
            com.github.mikephil.charting.components.LegendEntry r15 = new com.github.mikephil.charting.components.LegendEntry
            com.github.mikephil.charting.data.Entry r9 = r6.getEntryForIndex(r7)
            com.github.mikephil.charting.data.PieEntry r9 = (com.github.mikephil.charting.data.PieEntry) r9
            java.lang.String r10 = r9.getLabel()
            com.github.mikephil.charting.components.Legend$LegendForm r11 = r3.getForm()
            float r12 = r3.getFormSize()
            float r13 = r3.getFormLineWidth()
            android.graphics.DashPathEffect r14 = r3.getFormLineDashEffect()
            java.lang.Object r9 = r4.get(r7)
            java.lang.Integer r9 = (java.lang.Integer) r9
            int r16 = r9.intValue()
            r9 = r15
            r1 = r15
            r15 = r16
            r9.<init>(r10, r11, r12, r13, r14, r15)
            r8.add(r1)
            int r7 = r7 + 1
            r1 = r20
            goto L_0x00a7
        L_0x00e6:
            java.lang.String r1 = r6.getLabel()
            if (r1 == 0) goto L_0x0105
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r1 = r0.computedEntries
            com.github.mikephil.charting.components.LegendEntry r14 = new com.github.mikephil.charting.components.LegendEntry
            java.lang.String r8 = r3.getLabel()
            com.github.mikephil.charting.components.Legend$LegendForm r9 = com.github.mikephil.charting.components.Legend.LegendForm.NONE
            r10 = 2143289344(0x7fc00000, float:NaN)
            r11 = 2143289344(0x7fc00000, float:NaN)
            r12 = 0
            r13 = 1122867(0x112233, float:1.573472E-39)
            r7 = r14
            r7.<init>(r8, r9, r10, r11, r12, r13)
            r1.add(r14)
        L_0x0105:
            r6 = r20
            goto L_0x01bf
        L_0x0109:
            boolean r1 = r3 instanceof com.github.mikephil.charting.interfaces.datasets.ICandleDataSet
            if (r1 == 0) goto L_0x0167
            r1 = r3
            com.github.mikephil.charting.interfaces.datasets.ICandleDataSet r1 = (com.github.mikephil.charting.interfaces.datasets.ICandleDataSet) r1
            int r1 = r1.getDecreasingColor()
            r6 = 1122867(0x112233, float:1.573472E-39)
            if (r1 == r6) goto L_0x0167
            r1 = r3
            com.github.mikephil.charting.interfaces.datasets.ICandleDataSet r1 = (com.github.mikephil.charting.interfaces.datasets.ICandleDataSet) r1
            int r1 = r1.getDecreasingColor()
            r6 = r3
            com.github.mikephil.charting.interfaces.datasets.ICandleDataSet r6 = (com.github.mikephil.charting.interfaces.datasets.ICandleDataSet) r6
            int r14 = r6.getIncreasingColor()
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r13 = r0.computedEntries
            com.github.mikephil.charting.components.LegendEntry r15 = new com.github.mikephil.charting.components.LegendEntry
            r7 = 0
            com.github.mikephil.charting.components.Legend$LegendForm r8 = r3.getForm()
            float r9 = r3.getFormSize()
            float r10 = r3.getFormLineWidth()
            android.graphics.DashPathEffect r11 = r3.getFormLineDashEffect()
            r6 = r15
            r12 = r1
            r6.<init>(r7, r8, r9, r10, r11, r12)
            r13.add(r15)
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r6 = r0.computedEntries
            com.github.mikephil.charting.components.LegendEntry r15 = new com.github.mikephil.charting.components.LegendEntry
            java.lang.String r8 = r3.getLabel()
            com.github.mikephil.charting.components.Legend$LegendForm r9 = r3.getForm()
            float r10 = r3.getFormSize()
            float r11 = r3.getFormLineWidth()
            android.graphics.DashPathEffect r12 = r3.getFormLineDashEffect()
            r7 = r15
            r13 = r14
            r7.<init>(r8, r9, r10, r11, r12, r13)
            r6.add(r15)
            r6 = r20
            goto L_0x01bf
        L_0x0167:
            r1 = 0
        L_0x0168:
            int r6 = r4.size()
            if (r1 >= r6) goto L_0x01bb
            if (r1 >= r5) goto L_0x01bb
            int r6 = r4.size()
            int r6 = r6 + -1
            if (r1 >= r6) goto L_0x0181
            int r6 = r5 + -1
            if (r1 >= r6) goto L_0x0181
            r6 = 0
            r7 = r6
            r6 = r20
            goto L_0x018b
        L_0x0181:
            r6 = r20
            com.github.mikephil.charting.interfaces.datasets.IDataSet r7 = r6.getDataSetByIndex(r2)
            java.lang.String r7 = r7.getLabel()
        L_0x018b:
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r15 = r0.computedEntries
            com.github.mikephil.charting.components.LegendEntry r14 = new com.github.mikephil.charting.components.LegendEntry
            com.github.mikephil.charting.components.Legend$LegendForm r10 = r3.getForm()
            float r11 = r3.getFormSize()
            float r12 = r3.getFormLineWidth()
            android.graphics.DashPathEffect r13 = r3.getFormLineDashEffect()
            java.lang.Object r8 = r4.get(r1)
            java.lang.Integer r8 = (java.lang.Integer) r8
            int r16 = r8.intValue()
            r8 = r14
            r9 = r7
            r17 = r3
            r3 = r14
            r14 = r16
            r8.<init>(r9, r10, r11, r12, r13, r14)
            r15.add(r3)
            int r1 = r1 + 1
            r3 = r17
            goto L_0x0168
        L_0x01bb:
            r6 = r20
            r17 = r3
        L_0x01bf:
            int r2 = r2 + 1
            r1 = r6
            goto L_0x0012
        L_0x01c4:
            r6 = r1
            com.github.mikephil.charting.components.Legend r1 = r0.mLegend
            com.github.mikephil.charting.components.LegendEntry[] r1 = r1.getExtraEntries()
            if (r1 == 0) goto L_0x01d8
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r1 = r0.computedEntries
            com.github.mikephil.charting.components.Legend r2 = r0.mLegend
            com.github.mikephil.charting.components.LegendEntry[] r2 = r2.getExtraEntries()
            java.util.Collections.addAll(r1, r2)
        L_0x01d8:
            com.github.mikephil.charting.components.Legend r1 = r0.mLegend
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r2 = r0.computedEntries
            r1.setEntries(r2)
            goto L_0x01e1
        L_0x01e0:
            r6 = r1
        L_0x01e1:
            com.github.mikephil.charting.components.Legend r1 = r0.mLegend
            android.graphics.Typeface r1 = r1.getTypeface()
            if (r1 == 0) goto L_0x01ee
            android.graphics.Paint r2 = r0.mLegendLabelPaint
            r2.setTypeface(r1)
        L_0x01ee:
            android.graphics.Paint r2 = r0.mLegendLabelPaint
            com.github.mikephil.charting.components.Legend r3 = r0.mLegend
            float r3 = r3.getTextSize()
            r2.setTextSize(r3)
            android.graphics.Paint r2 = r0.mLegendLabelPaint
            com.github.mikephil.charting.components.Legend r3 = r0.mLegend
            int r3 = r3.getTextColor()
            r2.setColor(r3)
            com.github.mikephil.charting.components.Legend r2 = r0.mLegend
            android.graphics.Paint r3 = r0.mLegendLabelPaint
            com.github.mikephil.charting.utils.ViewPortHandler r4 = r0.mViewPortHandler
            r2.calculateDimensions(r3, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.mikephil.charting.renderer.LegendRenderer.computeLegend(com.github.mikephil.charting.data.ChartData):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:152:0x03c9  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x03cb  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x03d0  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x0405  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x0413  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x044b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void renderLegend(android.graphics.Canvas r40) {
        /*
            r39 = this;
            r6 = r39
            r7 = r40
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            boolean r0 = r0.isEnabled()
            if (r0 != 0) goto L_0x000d
            return
        L_0x000d:
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            android.graphics.Typeface r8 = r0.getTypeface()
            if (r8 == 0) goto L_0x001a
            android.graphics.Paint r0 = r6.mLegendLabelPaint
            r0.setTypeface(r8)
        L_0x001a:
            android.graphics.Paint r0 = r6.mLegendLabelPaint
            com.github.mikephil.charting.components.Legend r1 = r6.mLegend
            float r1 = r1.getTextSize()
            r0.setTextSize(r1)
            android.graphics.Paint r0 = r6.mLegendLabelPaint
            com.github.mikephil.charting.components.Legend r1 = r6.mLegend
            int r1 = r1.getTextColor()
            r0.setColor(r1)
            android.graphics.Paint r0 = r6.mLegendLabelPaint
            android.graphics.Paint$FontMetrics r1 = r6.legendFontMetrics
            float r9 = com.github.mikephil.charting.utils.Utils.getLineHeight(r0, r1)
            android.graphics.Paint r0 = r6.mLegendLabelPaint
            android.graphics.Paint$FontMetrics r1 = r6.legendFontMetrics
            float r0 = com.github.mikephil.charting.utils.Utils.getLineSpacing(r0, r1)
            com.github.mikephil.charting.components.Legend r1 = r6.mLegend
            float r1 = r1.getYEntrySpace()
            float r1 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r1)
            float r10 = r0 + r1
            android.graphics.Paint r0 = r6.mLegendLabelPaint
            java.lang.String r1 = "ABC"
            int r0 = com.github.mikephil.charting.utils.Utils.calcTextHeight(r0, r1)
            float r0 = (float) r0
            r11 = 1073741824(0x40000000, float:2.0)
            float r0 = r0 / r11
            float r12 = r9 - r0
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            com.github.mikephil.charting.components.LegendEntry[] r13 = r0.getEntries()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r0 = r0.getFormToTextSpace()
            float r14 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r0)
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r0 = r0.getXEntrySpace()
            float r15 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r0)
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            com.github.mikephil.charting.components.Legend$LegendOrientation r5 = r0.getOrientation()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            com.github.mikephil.charting.components.Legend$LegendHorizontalAlignment r4 = r0.getHorizontalAlignment()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            com.github.mikephil.charting.components.Legend$LegendVerticalAlignment r16 = r0.getVerticalAlignment()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            com.github.mikephil.charting.components.Legend$LegendDirection r3 = r0.getDirection()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r0 = r0.getFormSize()
            float r17 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r0)
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r0 = r0.getStackSpace()
            float r2 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r0)
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r18 = r0.getYOffset()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r1 = r0.getXOffset()
            r0 = 0
            int[] r19 = com.github.mikephil.charting.renderer.LegendRenderer.C05771.f85x2787f53e
            int r20 = r4.ordinal()
            r11 = r19[r20]
            r19 = r0
            r0 = 2
            r22 = r8
            r8 = 1
            if (r11 == r8) goto L_0x0163
            if (r11 == r0) goto L_0x013b
            r0 = 3
            if (r11 == r0) goto L_0x00ca
            r26 = r10
            r27 = r14
            r28 = r15
            goto L_0x0184
        L_0x00ca:
            com.github.mikephil.charting.components.Legend$LegendOrientation r0 = com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
            if (r5 != r0) goto L_0x00d8
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r6.mViewPortHandler
            float r0 = r0.getChartWidth()
            r11 = 1073741824(0x40000000, float:2.0)
            float r0 = r0 / r11
            goto L_0x00e8
        L_0x00d8:
            r11 = 1073741824(0x40000000, float:2.0)
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r6.mViewPortHandler
            float r0 = r0.contentLeft()
            com.github.mikephil.charting.utils.ViewPortHandler r8 = r6.mViewPortHandler
            float r8 = r8.contentWidth()
            float r8 = r8 / r11
            float r0 = r0 + r8
        L_0x00e8:
            com.github.mikephil.charting.components.Legend$LegendDirection r8 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            if (r3 != r8) goto L_0x00ee
            r8 = r1
            goto L_0x00ef
        L_0x00ee:
            float r8 = -r1
        L_0x00ef:
            float r0 = r0 + r8
            com.github.mikephil.charting.components.Legend$LegendOrientation r8 = com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
            if (r5 != r8) goto L_0x0132
            r8 = r10
            double r10 = (double) r0
            r19 = r0
            com.github.mikephil.charting.components.Legend$LegendDirection r0 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            r24 = 4611686018427387904(0x4000000000000000, double:2.0)
            if (r3 != r0) goto L_0x0115
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r0 = r0.mNeededWidth
            float r0 = -r0
            r26 = r8
            double r7 = (double) r0
            java.lang.Double.isNaN(r7)
            double r7 = r7 / r24
            r27 = r14
            r28 = r15
            double r14 = (double) r1
            java.lang.Double.isNaN(r14)
            double r7 = r7 + r14
            goto L_0x012a
        L_0x0115:
            r26 = r8
            r27 = r14
            r28 = r15
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r0 = r0.mNeededWidth
            double r7 = (double) r0
            java.lang.Double.isNaN(r7)
            double r7 = r7 / r24
            double r14 = (double) r1
            java.lang.Double.isNaN(r14)
            double r7 = r7 - r14
        L_0x012a:
            java.lang.Double.isNaN(r10)
            double r10 = r10 + r7
            float r0 = (float) r10
            r19 = r0
            goto L_0x0184
        L_0x0132:
            r19 = r0
            r26 = r10
            r27 = r14
            r28 = r15
            goto L_0x0184
        L_0x013b:
            r26 = r10
            r27 = r14
            r28 = r15
            com.github.mikephil.charting.components.Legend$LegendOrientation r0 = com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
            if (r5 != r0) goto L_0x014d
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r6.mViewPortHandler
            float r0 = r0.getChartWidth()
            float r0 = r0 - r1
            goto L_0x0154
        L_0x014d:
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r6.mViewPortHandler
            float r0 = r0.contentRight()
            float r0 = r0 - r1
        L_0x0154:
            com.github.mikephil.charting.components.Legend$LegendDirection r7 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            if (r3 != r7) goto L_0x0160
            com.github.mikephil.charting.components.Legend r7 = r6.mLegend
            float r7 = r7.mNeededWidth
            float r0 = r0 - r7
            r19 = r0
            goto L_0x0184
        L_0x0160:
            r19 = r0
            goto L_0x0184
        L_0x0163:
            r26 = r10
            r27 = r14
            r28 = r15
            com.github.mikephil.charting.components.Legend$LegendOrientation r0 = com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
            if (r5 != r0) goto L_0x016f
            r0 = r1
            goto L_0x0176
        L_0x016f:
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r6.mViewPortHandler
            float r0 = r0.contentLeft()
            float r0 = r0 + r1
        L_0x0176:
            com.github.mikephil.charting.components.Legend$LegendDirection r7 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r3 != r7) goto L_0x0182
            com.github.mikephil.charting.components.Legend r7 = r6.mLegend
            float r7 = r7.mNeededWidth
            float r0 = r0 + r7
            r19 = r0
            goto L_0x0184
        L_0x0182:
            r19 = r0
        L_0x0184:
            int[] r0 = com.github.mikephil.charting.renderer.LegendRenderer.C05771.f86x9c9dbef
            int r7 = r5.ordinal()
            r0 = r0[r7]
            r7 = 1
            if (r0 == r7) goto L_0x02e8
            r7 = 2
            if (r0 == r7) goto L_0x01a6
            r10 = r40
            r29 = r1
            r37 = r4
            r25 = r5
            r30 = r12
            r38 = r13
            r14 = r27
            r12 = r2
            r13 = r3
            r3 = r28
            goto L_0x047b
        L_0x01a6:
            r0 = 0
            r7 = 0
            r10 = 0
            int[] r11 = com.github.mikephil.charting.renderer.LegendRenderer.C05771.f87xc926f1ec
            int r14 = r16.ordinal()
            r11 = r11[r14]
            r14 = 1
            if (r11 == r14) goto L_0x01ed
            r14 = 2
            if (r11 == r14) goto L_0x01d3
            r14 = 3
            if (r11 == r14) goto L_0x01bb
            goto L_0x01fd
        L_0x01bb:
            com.github.mikephil.charting.utils.ViewPortHandler r11 = r6.mViewPortHandler
            float r11 = r11.getChartHeight()
            r14 = 1073741824(0x40000000, float:2.0)
            float r11 = r11 / r14
            com.github.mikephil.charting.components.Legend r15 = r6.mLegend
            float r15 = r15.mNeededHeight
            float r15 = r15 / r14
            float r11 = r11 - r15
            com.github.mikephil.charting.components.Legend r14 = r6.mLegend
            float r14 = r14.getYOffset()
            float r10 = r11 + r14
            goto L_0x01fd
        L_0x01d3:
            com.github.mikephil.charting.components.Legend$LegendHorizontalAlignment r11 = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
            if (r4 != r11) goto L_0x01de
            com.github.mikephil.charting.utils.ViewPortHandler r11 = r6.mViewPortHandler
            float r11 = r11.getChartHeight()
            goto L_0x01e4
        L_0x01de:
            com.github.mikephil.charting.utils.ViewPortHandler r11 = r6.mViewPortHandler
            float r11 = r11.contentBottom()
        L_0x01e4:
            r10 = r11
            com.github.mikephil.charting.components.Legend r11 = r6.mLegend
            float r11 = r11.mNeededHeight
            float r11 = r11 + r18
            float r10 = r10 - r11
            goto L_0x01fd
        L_0x01ed:
            com.github.mikephil.charting.components.Legend$LegendHorizontalAlignment r11 = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
            if (r4 != r11) goto L_0x01f3
            r11 = 0
            goto L_0x01f9
        L_0x01f3:
            com.github.mikephil.charting.utils.ViewPortHandler r11 = r6.mViewPortHandler
            float r11 = r11.contentTop()
        L_0x01f9:
            r10 = r11
            float r10 = r10 + r18
        L_0x01fd:
            r11 = 0
            r14 = r10
            r10 = r7
            r7 = r0
        L_0x0201:
            int r0 = r13.length
            if (r11 >= r0) goto L_0x02ce
            r15 = r13[r11]
            com.github.mikephil.charting.components.Legend$LegendForm r0 = r15.form
            com.github.mikephil.charting.components.Legend$LegendForm r8 = com.github.mikephil.charting.components.Legend.LegendForm.NONE
            if (r0 == r8) goto L_0x020e
            r0 = 1
            goto L_0x020f
        L_0x020e:
            r0 = 0
        L_0x020f:
            r8 = r0
            float r0 = r15.formSize
            boolean r0 = java.lang.Float.isNaN(r0)
            if (r0 == 0) goto L_0x021b
            r0 = r17
            goto L_0x0221
        L_0x021b:
            float r0 = r15.formSize
            float r0 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r0)
        L_0x0221:
            r20 = r0
            r0 = r19
            if (r8 == 0) goto L_0x0260
            r21 = r1
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            if (r3 != r1) goto L_0x0231
            float r0 = r0 + r7
            r23 = r0
            goto L_0x0236
        L_0x0231:
            float r1 = r20 - r7
            float r0 = r0 - r1
            r23 = r0
        L_0x0236:
            float r25 = r14 + r12
            com.github.mikephil.charting.components.Legend r1 = r6.mLegend
            r0 = r39
            r29 = r21
            r21 = r1
            r1 = r40
            r30 = r12
            r12 = r2
            r2 = r23
            r31 = r13
            r13 = r3
            r3 = r25
            r32 = r4
            r4 = r15
            r25 = r5
            r5 = r21
            r0.drawForm(r1, r2, r3, r4, r5)
            com.github.mikephil.charting.components.Legend$LegendDirection r0 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            if (r13 != r0) goto L_0x025d
            float r0 = r23 + r20
            goto L_0x026c
        L_0x025d:
            r0 = r23
            goto L_0x026c
        L_0x0260:
            r29 = r1
            r32 = r4
            r25 = r5
            r30 = r12
            r31 = r13
            r12 = r2
            r13 = r3
        L_0x026c:
            java.lang.String r1 = r15.label
            if (r1 == 0) goto L_0x02b3
            if (r8 == 0) goto L_0x0281
            if (r10 != 0) goto L_0x0281
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            if (r13 != r1) goto L_0x027c
            r1 = r27
            r5 = r1
            goto L_0x027f
        L_0x027c:
            r5 = r27
            float r1 = -r5
        L_0x027f:
            float r0 = r0 + r1
            goto L_0x0287
        L_0x0281:
            r5 = r27
            if (r10 == 0) goto L_0x0287
            r0 = r19
        L_0x0287:
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r13 != r1) goto L_0x0295
            android.graphics.Paint r1 = r6.mLegendLabelPaint
            java.lang.String r2 = r15.label
            int r1 = com.github.mikephil.charting.utils.Utils.calcTextWidth(r1, r2)
            float r1 = (float) r1
            float r0 = r0 - r1
        L_0x0295:
            if (r10 != 0) goto L_0x02a1
            float r1 = r14 + r9
            java.lang.String r2 = r15.label
            r4 = r40
            r6.drawLabel(r4, r0, r1, r2)
            goto L_0x02ad
        L_0x02a1:
            r4 = r40
            float r1 = r9 + r26
            float r14 = r14 + r1
            float r1 = r14 + r9
            java.lang.String r2 = r15.label
            r6.drawLabel(r4, r0, r1, r2)
        L_0x02ad:
            float r1 = r9 + r26
            float r14 = r14 + r1
            r1 = 0
            r7 = r1
            goto L_0x02bc
        L_0x02b3:
            r4 = r40
            r5 = r27
            float r2 = r20 + r12
            float r7 = r7 + r2
            r1 = 1
            r10 = r1
        L_0x02bc:
            int r11 = r11 + 1
            r27 = r5
            r2 = r12
            r3 = r13
            r5 = r25
            r1 = r29
            r12 = r30
            r13 = r31
            r4 = r32
            goto L_0x0201
        L_0x02ce:
            r29 = r1
            r32 = r4
            r25 = r5
            r30 = r12
            r31 = r13
            r5 = r27
            r4 = r40
            r12 = r2
            r13 = r3
            r10 = r4
            r14 = r5
            r3 = r28
            r38 = r31
            r37 = r32
            goto L_0x047b
        L_0x02e8:
            r29 = r1
            r32 = r4
            r25 = r5
            r30 = r12
            r31 = r13
            r5 = r27
            r4 = r40
            r12 = r2
            r13 = r3
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            java.util.List r7 = r0.getCalculatedLineSizes()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            java.util.List r8 = r0.getCalculatedLabelSizes()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            java.util.List r10 = r0.getCalculatedLabelBreakPoints()
            r0 = r19
            r1 = 0
            int[] r2 = com.github.mikephil.charting.renderer.LegendRenderer.C05771.f87xc926f1ec
            int r3 = r16.ordinal()
            r2 = r2[r3]
            r11 = 1
            if (r2 == r11) goto L_0x033f
            r3 = 2
            if (r2 == r3) goto L_0x0330
            r3 = 3
            if (r2 == r3) goto L_0x031f
            goto L_0x0342
        L_0x031f:
            com.github.mikephil.charting.utils.ViewPortHandler r2 = r6.mViewPortHandler
            float r2 = r2.getChartHeight()
            com.github.mikephil.charting.components.Legend r3 = r6.mLegend
            float r3 = r3.mNeededHeight
            float r2 = r2 - r3
            r3 = 1073741824(0x40000000, float:2.0)
            float r2 = r2 / r3
            float r1 = r2 + r18
            goto L_0x0342
        L_0x0330:
            com.github.mikephil.charting.utils.ViewPortHandler r2 = r6.mViewPortHandler
            float r2 = r2.getChartHeight()
            float r2 = r2 - r18
            com.github.mikephil.charting.components.Legend r3 = r6.mLegend
            float r3 = r3.mNeededHeight
            float r1 = r2 - r3
            goto L_0x0342
        L_0x033f:
            r1 = r18
        L_0x0342:
            r2 = 0
            r3 = 0
            r14 = r31
            int r15 = r14.length
        L_0x0347:
            if (r3 >= r15) goto L_0x046b
            r11 = r14[r3]
            r20 = r0
            com.github.mikephil.charting.components.Legend$LegendForm r0 = r11.form
            com.github.mikephil.charting.components.Legend$LegendForm r4 = com.github.mikephil.charting.components.Legend.LegendForm.NONE
            if (r0 == r4) goto L_0x0355
            r0 = 1
            goto L_0x0356
        L_0x0355:
            r0 = 0
        L_0x0356:
            r23 = r0
            float r0 = r11.formSize
            boolean r0 = java.lang.Float.isNaN(r0)
            if (r0 == 0) goto L_0x0363
            r0 = r17
            goto L_0x0369
        L_0x0363:
            float r0 = r11.formSize
            float r0 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r0)
        L_0x0369:
            r27 = r0
            int r0 = r10.size()
            if (r3 >= r0) goto L_0x0385
            java.lang.Object r0 = r10.get(r3)
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L_0x0385
            r0 = r19
            float r4 = r9 + r26
            float r1 = r1 + r4
            r20 = r1
            goto L_0x0389
        L_0x0385:
            r0 = r20
            r20 = r1
        L_0x0389:
            int r1 = (r0 > r19 ? 1 : (r0 == r19 ? 0 : -1))
            if (r1 != 0) goto L_0x03bf
            com.github.mikephil.charting.components.Legend$LegendHorizontalAlignment r1 = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
            r4 = r32
            if (r4 != r1) goto L_0x03bc
            int r1 = r7.size()
            if (r2 >= r1) goto L_0x03b9
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r13 != r1) goto L_0x03a6
            java.lang.Object r1 = r7.get(r2)
            com.github.mikephil.charting.utils.FSize r1 = (com.github.mikephil.charting.utils.FSize) r1
            float r1 = r1.width
            goto L_0x03af
        L_0x03a6:
            java.lang.Object r1 = r7.get(r2)
            com.github.mikephil.charting.utils.FSize r1 = (com.github.mikephil.charting.utils.FSize) r1
            float r1 = r1.width
            float r1 = -r1
        L_0x03af:
            r21 = 1073741824(0x40000000, float:2.0)
            float r1 = r1 / r21
            float r0 = r0 + r1
            int r2 = r2 + 1
            r31 = r2
            goto L_0x03c5
        L_0x03b9:
            r21 = 1073741824(0x40000000, float:2.0)
            goto L_0x03c3
        L_0x03bc:
            r21 = 1073741824(0x40000000, float:2.0)
            goto L_0x03c3
        L_0x03bf:
            r4 = r32
            r21 = 1073741824(0x40000000, float:2.0)
        L_0x03c3:
            r31 = r2
        L_0x03c5:
            java.lang.String r1 = r11.label
            if (r1 != 0) goto L_0x03cb
            r1 = 1
            goto L_0x03cc
        L_0x03cb:
            r1 = 0
        L_0x03cc:
            r32 = r1
            if (r23 == 0) goto L_0x0405
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r13 != r1) goto L_0x03d9
            float r0 = r0 - r27
            r33 = r0
            goto L_0x03db
        L_0x03d9:
            r33 = r0
        L_0x03db:
            float r34 = r20 + r30
            com.github.mikephil.charting.components.Legend r2 = r6.mLegend
            r0 = r39
            r1 = r40
            r35 = r2
            r2 = r33
            r36 = r7
            r7 = r3
            r3 = r34
            r37 = r4
            r34 = r10
            r10 = r40
            r4 = r11
            r38 = r14
            r14 = r5
            r5 = r35
            r0.drawForm(r1, r2, r3, r4, r5)
            com.github.mikephil.charting.components.Legend$LegendDirection r0 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            if (r13 != r0) goto L_0x0402
            float r0 = r33 + r27
            goto L_0x0411
        L_0x0402:
            r0 = r33
            goto L_0x0411
        L_0x0405:
            r37 = r4
            r36 = r7
            r34 = r10
            r38 = r14
            r10 = r40
            r7 = r3
            r14 = r5
        L_0x0411:
            if (r32 != 0) goto L_0x044b
            if (r23 == 0) goto L_0x041d
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r13 != r1) goto L_0x041b
            float r1 = -r14
            goto L_0x041c
        L_0x041b:
            r1 = r14
        L_0x041c:
            float r0 = r0 + r1
        L_0x041d:
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r13 != r1) goto L_0x042a
            java.lang.Object r1 = r8.get(r7)
            com.github.mikephil.charting.utils.FSize r1 = (com.github.mikephil.charting.utils.FSize) r1
            float r1 = r1.width
            float r0 = r0 - r1
        L_0x042a:
            float r1 = r20 + r9
            java.lang.String r2 = r11.label
            r6.drawLabel(r10, r0, r1, r2)
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            if (r13 != r1) goto L_0x043e
            java.lang.Object r1 = r8.get(r7)
            com.github.mikephil.charting.utils.FSize r1 = (com.github.mikephil.charting.utils.FSize) r1
            float r1 = r1.width
            float r0 = r0 + r1
        L_0x043e:
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r13 != r1) goto L_0x0446
            r3 = r28
            float r1 = -r3
            goto L_0x0449
        L_0x0446:
            r3 = r28
            r1 = r3
        L_0x0449:
            float r0 = r0 + r1
            goto L_0x0455
        L_0x044b:
            r3 = r28
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r13 != r1) goto L_0x0453
            float r2 = -r12
            goto L_0x0454
        L_0x0453:
            r2 = r12
        L_0x0454:
            float r0 = r0 + r2
        L_0x0455:
            int r1 = r7 + 1
            r28 = r3
            r4 = r10
            r5 = r14
            r2 = r31
            r10 = r34
            r7 = r36
            r32 = r37
            r14 = r38
            r11 = 1
            r3 = r1
            r1 = r20
            goto L_0x0347
        L_0x046b:
            r20 = r0
            r36 = r7
            r34 = r10
            r38 = r14
            r37 = r32
            r7 = r3
            r10 = r4
            r14 = r5
            r3 = r28
        L_0x047b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.mikephil.charting.renderer.LegendRenderer.renderLegend(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: protected */
    public void drawForm(Canvas c, float x, float y, LegendEntry entry, Legend legend) {
        Legend.LegendForm form;
        Canvas canvas = c;
        float f = x;
        float f2 = y;
        LegendEntry legendEntry = entry;
        if (legendEntry.formColor != 1122868 && legendEntry.formColor != 1122867 && legendEntry.formColor != 0) {
            int restoreCount = c.save();
            Legend.LegendForm form2 = legendEntry.form;
            if (form2 == Legend.LegendForm.DEFAULT) {
                form = legend.getForm();
            } else {
                form = form2;
            }
            this.mLegendFormPaint.setColor(legendEntry.formColor);
            float formSize = Utils.convertDpToPixel(Float.isNaN(legendEntry.formSize) ? legend.getFormSize() : legendEntry.formSize);
            float half = formSize / 2.0f;
            switch (form) {
                case DEFAULT:
                case CIRCLE:
                    this.mLegendFormPaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(f + half, f2, half, this.mLegendFormPaint);
                    break;
                case SQUARE:
                    this.mLegendFormPaint.setStyle(Paint.Style.FILL);
                    c.drawRect(x, f2 - half, f + formSize, f2 + half, this.mLegendFormPaint);
                    break;
                case LINE:
                    float formLineWidth = Utils.convertDpToPixel(Float.isNaN(legendEntry.formLineWidth) ? legend.getFormLineWidth() : legendEntry.formLineWidth);
                    DashPathEffect formLineDashEffect = legendEntry.formLineDashEffect == null ? legend.getFormLineDashEffect() : legendEntry.formLineDashEffect;
                    this.mLegendFormPaint.setStyle(Paint.Style.STROKE);
                    this.mLegendFormPaint.setStrokeWidth(formLineWidth);
                    this.mLegendFormPaint.setPathEffect(formLineDashEffect);
                    this.mLineFormPath.reset();
                    this.mLineFormPath.moveTo(f, f2);
                    this.mLineFormPath.lineTo(f + formSize, f2);
                    canvas.drawPath(this.mLineFormPath, this.mLegendFormPaint);
                    break;
            }
            canvas.restoreToCount(restoreCount);
        }
    }

    /* renamed from: com.github.mikephil.charting.renderer.LegendRenderer$1 */
    static /* synthetic */ class C05771 {

        /* renamed from: $SwitchMap$com$github$mikephil$charting$components$Legend$LegendHorizontalAlignment */
        static final /* synthetic */ int[] f85x2787f53e = new int[Legend.LegendHorizontalAlignment.values().length];

        /* renamed from: $SwitchMap$com$github$mikephil$charting$components$Legend$LegendOrientation */
        static final /* synthetic */ int[] f86x9c9dbef = new int[Legend.LegendOrientation.values().length];

        /* renamed from: $SwitchMap$com$github$mikephil$charting$components$Legend$LegendVerticalAlignment */
        static final /* synthetic */ int[] f87xc926f1ec = new int[Legend.LegendVerticalAlignment.values().length];

        static {
            f84xfbec3b85 = new int[Legend.LegendForm.values().length];
            try {
                f84xfbec3b85[Legend.LegendForm.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f84xfbec3b85[Legend.LegendForm.EMPTY.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f84xfbec3b85[Legend.LegendForm.DEFAULT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f84xfbec3b85[Legend.LegendForm.CIRCLE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f84xfbec3b85[Legend.LegendForm.SQUARE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f84xfbec3b85[Legend.LegendForm.LINE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f86x9c9dbef[Legend.LegendOrientation.HORIZONTAL.ordinal()] = 1;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f86x9c9dbef[Legend.LegendOrientation.VERTICAL.ordinal()] = 2;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f87xc926f1ec[Legend.LegendVerticalAlignment.TOP.ordinal()] = 1;
            } catch (NoSuchFieldError e9) {
            }
            try {
                f87xc926f1ec[Legend.LegendVerticalAlignment.BOTTOM.ordinal()] = 2;
            } catch (NoSuchFieldError e10) {
            }
            try {
                f87xc926f1ec[Legend.LegendVerticalAlignment.CENTER.ordinal()] = 3;
            } catch (NoSuchFieldError e11) {
            }
            try {
                f85x2787f53e[Legend.LegendHorizontalAlignment.LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError e12) {
            }
            try {
                f85x2787f53e[Legend.LegendHorizontalAlignment.RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError e13) {
            }
            try {
                f85x2787f53e[Legend.LegendHorizontalAlignment.CENTER.ordinal()] = 3;
            } catch (NoSuchFieldError e14) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawLabel(Canvas c, float x, float y, String label) {
        c.drawText(label, x, y, this.mLegendLabelPaint);
    }
}
