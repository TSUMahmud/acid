package com.github.mikephil.charting.components;

import android.graphics.DashPathEffect;
import android.graphics.Paint;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class Legend extends ComponentBase {
    private List<Boolean> mCalculatedLabelBreakPoints;
    private List<FSize> mCalculatedLabelSizes;
    private List<FSize> mCalculatedLineSizes;
    private LegendDirection mDirection;
    private boolean mDrawInside;
    private LegendEntry[] mEntries;
    private LegendEntry[] mExtraEntries;
    private DashPathEffect mFormLineDashEffect;
    private float mFormLineWidth;
    private float mFormSize;
    private float mFormToTextSpace;
    private LegendHorizontalAlignment mHorizontalAlignment;
    private boolean mIsLegendCustom;
    private float mMaxSizePercent;
    public float mNeededHeight;
    public float mNeededWidth;
    private LegendOrientation mOrientation;
    private LegendForm mShape;
    private float mStackSpace;
    public float mTextHeightMax;
    public float mTextWidthMax;
    private LegendVerticalAlignment mVerticalAlignment;
    private boolean mWordWrapEnabled;
    private float mXEntrySpace;
    private float mYEntrySpace;

    public enum LegendDirection {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }

    public enum LegendForm {
        NONE,
        EMPTY,
        DEFAULT,
        SQUARE,
        CIRCLE,
        LINE
    }

    public enum LegendHorizontalAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum LegendOrientation {
        HORIZONTAL,
        VERTICAL
    }

    public enum LegendVerticalAlignment {
        TOP,
        CENTER,
        BOTTOM
    }

    public Legend() {
        this.mEntries = new LegendEntry[0];
        this.mIsLegendCustom = false;
        this.mHorizontalAlignment = LegendHorizontalAlignment.LEFT;
        this.mVerticalAlignment = LegendVerticalAlignment.BOTTOM;
        this.mOrientation = LegendOrientation.HORIZONTAL;
        this.mDrawInside = false;
        this.mDirection = LegendDirection.LEFT_TO_RIGHT;
        this.mShape = LegendForm.SQUARE;
        this.mFormSize = 8.0f;
        this.mFormLineWidth = 3.0f;
        this.mFormLineDashEffect = null;
        this.mXEntrySpace = 6.0f;
        this.mYEntrySpace = 0.0f;
        this.mFormToTextSpace = 5.0f;
        this.mStackSpace = 3.0f;
        this.mMaxSizePercent = 0.95f;
        this.mNeededWidth = 0.0f;
        this.mNeededHeight = 0.0f;
        this.mTextHeightMax = 0.0f;
        this.mTextWidthMax = 0.0f;
        this.mWordWrapEnabled = false;
        this.mCalculatedLabelSizes = new ArrayList(16);
        this.mCalculatedLabelBreakPoints = new ArrayList(16);
        this.mCalculatedLineSizes = new ArrayList(16);
        this.mTextSize = Utils.convertDpToPixel(10.0f);
        this.mXOffset = Utils.convertDpToPixel(5.0f);
        this.mYOffset = Utils.convertDpToPixel(3.0f);
    }

    public Legend(LegendEntry[] entries) {
        this();
        if (entries != null) {
            this.mEntries = entries;
            return;
        }
        throw new IllegalArgumentException("entries array is NULL");
    }

    public void setEntries(List<LegendEntry> entries) {
        this.mEntries = (LegendEntry[]) entries.toArray(new LegendEntry[entries.size()]);
    }

    public LegendEntry[] getEntries() {
        return this.mEntries;
    }

    public float getMaximumEntryWidth(Paint p) {
        float max = 0.0f;
        float maxFormSize = 0.0f;
        float formToTextSpace = Utils.convertDpToPixel(this.mFormToTextSpace);
        for (LegendEntry entry : this.mEntries) {
            float formSize = Utils.convertDpToPixel(Float.isNaN(entry.formSize) ? this.mFormSize : entry.formSize);
            if (formSize > maxFormSize) {
                maxFormSize = formSize;
            }
            String label = entry.label;
            if (label != null) {
                float length = (float) Utils.calcTextWidth(p, label);
                if (length > max) {
                    max = length;
                }
            }
        }
        return max + maxFormSize + formToTextSpace;
    }

    public float getMaximumEntryHeight(Paint p) {
        float max = 0.0f;
        for (LegendEntry entry : this.mEntries) {
            String label = entry.label;
            if (label != null) {
                float length = (float) Utils.calcTextHeight(p, label);
                if (length > max) {
                    max = length;
                }
            }
        }
        return max;
    }

    public LegendEntry[] getExtraEntries() {
        return this.mExtraEntries;
    }

    public void setExtra(List<LegendEntry> entries) {
        this.mExtraEntries = (LegendEntry[]) entries.toArray(new LegendEntry[entries.size()]);
    }

    public void setExtra(LegendEntry[] entries) {
        if (entries == null) {
            entries = new LegendEntry[0];
        }
        this.mExtraEntries = entries;
    }

    public void setExtra(int[] colors, String[] labels) {
        List<LegendEntry> entries = new ArrayList<>();
        for (int i = 0; i < Math.min(colors.length, labels.length); i++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = colors[i];
            entry.label = labels[i];
            if (entry.formColor == 1122868 || entry.formColor == 0) {
                entry.form = LegendForm.NONE;
            } else if (entry.formColor == 1122867) {
                entry.form = LegendForm.EMPTY;
            }
            entries.add(entry);
        }
        this.mExtraEntries = (LegendEntry[]) entries.toArray(new LegendEntry[entries.size()]);
    }

    public void setCustom(LegendEntry[] entries) {
        this.mEntries = entries;
        this.mIsLegendCustom = true;
    }

    public void setCustom(List<LegendEntry> entries) {
        this.mEntries = (LegendEntry[]) entries.toArray(new LegendEntry[entries.size()]);
        this.mIsLegendCustom = true;
    }

    public void resetCustom() {
        this.mIsLegendCustom = false;
    }

    public boolean isLegendCustom() {
        return this.mIsLegendCustom;
    }

    public LegendHorizontalAlignment getHorizontalAlignment() {
        return this.mHorizontalAlignment;
    }

    public void setHorizontalAlignment(LegendHorizontalAlignment value) {
        this.mHorizontalAlignment = value;
    }

    public LegendVerticalAlignment getVerticalAlignment() {
        return this.mVerticalAlignment;
    }

    public void setVerticalAlignment(LegendVerticalAlignment value) {
        this.mVerticalAlignment = value;
    }

    public LegendOrientation getOrientation() {
        return this.mOrientation;
    }

    public void setOrientation(LegendOrientation value) {
        this.mOrientation = value;
    }

    public boolean isDrawInsideEnabled() {
        return this.mDrawInside;
    }

    public void setDrawInside(boolean value) {
        this.mDrawInside = value;
    }

    public LegendDirection getDirection() {
        return this.mDirection;
    }

    public void setDirection(LegendDirection pos) {
        this.mDirection = pos;
    }

    public LegendForm getForm() {
        return this.mShape;
    }

    public void setForm(LegendForm shape) {
        this.mShape = shape;
    }

    public void setFormSize(float size) {
        this.mFormSize = size;
    }

    public float getFormSize() {
        return this.mFormSize;
    }

    public void setFormLineWidth(float size) {
        this.mFormLineWidth = size;
    }

    public float getFormLineWidth() {
        return this.mFormLineWidth;
    }

    public void setFormLineDashEffect(DashPathEffect dashPathEffect) {
        this.mFormLineDashEffect = dashPathEffect;
    }

    public DashPathEffect getFormLineDashEffect() {
        return this.mFormLineDashEffect;
    }

    public float getXEntrySpace() {
        return this.mXEntrySpace;
    }

    public void setXEntrySpace(float space) {
        this.mXEntrySpace = space;
    }

    public float getYEntrySpace() {
        return this.mYEntrySpace;
    }

    public void setYEntrySpace(float space) {
        this.mYEntrySpace = space;
    }

    public float getFormToTextSpace() {
        return this.mFormToTextSpace;
    }

    public void setFormToTextSpace(float space) {
        this.mFormToTextSpace = space;
    }

    public float getStackSpace() {
        return this.mStackSpace;
    }

    public void setStackSpace(float space) {
        this.mStackSpace = space;
    }

    public void setWordWrapEnabled(boolean enabled) {
        this.mWordWrapEnabled = enabled;
    }

    public boolean isWordWrapEnabled() {
        return this.mWordWrapEnabled;
    }

    public float getMaxSizePercent() {
        return this.mMaxSizePercent;
    }

    public void setMaxSizePercent(float maxSize) {
        this.mMaxSizePercent = maxSize;
    }

    public List<FSize> getCalculatedLabelSizes() {
        return this.mCalculatedLabelSizes;
    }

    public List<Boolean> getCalculatedLabelBreakPoints() {
        return this.mCalculatedLabelBreakPoints;
    }

    public List<FSize> getCalculatedLineSizes() {
        return this.mCalculatedLineSizes;
    }

    /* JADX WARNING: Removed duplicated region for block: B:55:0x015a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void calculateDimensions(android.graphics.Paint r31, com.github.mikephil.charting.utils.ViewPortHandler r32) {
        /*
            r30 = this;
            r0 = r30
            r1 = r31
            float r2 = r0.mFormSize
            float r2 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r2)
            float r3 = r0.mStackSpace
            float r3 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r3)
            float r4 = r0.mFormToTextSpace
            float r4 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r4)
            float r5 = r0.mXEntrySpace
            float r5 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r5)
            float r6 = r0.mYEntrySpace
            float r6 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r6)
            boolean r7 = r0.mWordWrapEnabled
            com.github.mikephil.charting.components.LegendEntry[] r8 = r0.mEntries
            int r9 = r8.length
            float r10 = r30.getMaximumEntryWidth(r31)
            r0.mTextWidthMax = r10
            float r10 = r30.getMaximumEntryHeight(r31)
            r0.mTextHeightMax = r10
            int[] r10 = com.github.mikephil.charting.components.Legend.C05731.f70x9c9dbef
            com.github.mikephil.charting.components.Legend$LegendOrientation r11 = r0.mOrientation
            int r11 = r11.ordinal()
            r10 = r10[r11]
            r12 = 1
            if (r10 == r12) goto L_0x01b8
            r13 = 2
            if (r10 == r13) goto L_0x0051
            r18 = r2
            r27 = r3
            r22 = r5
            r23 = r6
            r26 = r7
            r25 = r8
            goto L_0x0230
        L_0x0051:
            float r10 = com.github.mikephil.charting.utils.Utils.getLineHeight(r31)
            float r13 = com.github.mikephil.charting.utils.Utils.getLineSpacing(r31)
            float r13 = r13 + r6
            float r14 = r32.contentWidth()
            float r15 = r0.mMaxSizePercent
            float r14 = r14 * r15
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = -1
            java.util.List<java.lang.Boolean> r12 = r0.mCalculatedLabelBreakPoints
            r12.clear()
            java.util.List<com.github.mikephil.charting.utils.FSize> r12 = r0.mCalculatedLabelSizes
            r12.clear()
            java.util.List<com.github.mikephil.charting.utils.FSize> r12 = r0.mCalculatedLineSizes
            r12.clear()
            r12 = 0
            r20 = r16
            r21 = r18
        L_0x007d:
            if (r12 >= r9) goto L_0x0181
            r11 = r8[r12]
            r18 = r2
            com.github.mikephil.charting.components.Legend$LegendForm r2 = r11.form
            r22 = r5
            com.github.mikephil.charting.components.Legend$LegendForm r5 = com.github.mikephil.charting.components.Legend.LegendForm.NONE
            if (r2 == r5) goto L_0x008d
            r2 = 1
            goto L_0x008e
        L_0x008d:
            r2 = 0
        L_0x008e:
            float r5 = r11.formSize
            boolean r5 = java.lang.Float.isNaN(r5)
            if (r5 == 0) goto L_0x0099
            r5 = r18
            goto L_0x009f
        L_0x0099:
            float r5 = r11.formSize
            float r5 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r5)
        L_0x009f:
            r23 = r6
            java.lang.String r6 = r11.label
            r24 = r11
            java.util.List<java.lang.Boolean> r11 = r0.mCalculatedLabelBreakPoints
            r25 = r8
            r16 = 0
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r16)
            r11.add(r8)
            r8 = -1
            r11 = r21
            if (r11 != r8) goto L_0x00bb
            r17 = 0
            goto L_0x00bd
        L_0x00bb:
            float r17 = r17 + r3
        L_0x00bd:
            if (r6 == 0) goto L_0x00e1
            java.util.List<com.github.mikephil.charting.utils.FSize> r8 = r0.mCalculatedLabelSizes
            r27 = r3
            com.github.mikephil.charting.utils.FSize r3 = com.github.mikephil.charting.utils.Utils.calcTextSize(r1, r6)
            r8.add(r3)
            if (r2 == 0) goto L_0x00cf
            float r8 = r4 + r5
            goto L_0x00d0
        L_0x00cf:
            r8 = 0
        L_0x00d0:
            float r17 = r17 + r8
            java.util.List<com.github.mikephil.charting.utils.FSize> r3 = r0.mCalculatedLabelSizes
            java.lang.Object r3 = r3.get(r12)
            com.github.mikephil.charting.utils.FSize r3 = (com.github.mikephil.charting.utils.FSize) r3
            float r3 = r3.width
            float r17 = r17 + r3
            r28 = r5
            goto L_0x00fc
        L_0x00e1:
            r27 = r3
            java.util.List<com.github.mikephil.charting.utils.FSize> r3 = r0.mCalculatedLabelSizes
            r28 = r5
            r8 = 0
            com.github.mikephil.charting.utils.FSize r5 = com.github.mikephil.charting.utils.FSize.getInstance(r8, r8)
            r3.add(r5)
            if (r2 == 0) goto L_0x00f4
            r3 = r28
            goto L_0x00f5
        L_0x00f4:
            r3 = 0
        L_0x00f5:
            float r17 = r17 + r3
            r3 = -1
            if (r11 != r3) goto L_0x00fc
            r3 = r12
            r11 = r3
        L_0x00fc:
            if (r6 != 0) goto L_0x010c
            int r3 = r9 + -1
            if (r12 != r3) goto L_0x0103
            goto L_0x010c
        L_0x0103:
            r26 = r7
            r29 = r20
            r20 = r2
            r2 = r29
            goto L_0x0168
        L_0x010c:
            r3 = r20
            r8 = 0
            int r5 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r5 != 0) goto L_0x0115
            r5 = 0
            goto L_0x0117
        L_0x0115:
            r5 = r22
        L_0x0117:
            if (r7 == 0) goto L_0x014f
            int r8 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r8 == 0) goto L_0x014f
            float r8 = r14 - r3
            float r20 = r5 + r17
            int r8 = (r8 > r20 ? 1 : (r8 == r20 ? 0 : -1))
            if (r8 < 0) goto L_0x012a
            r20 = r2
            r26 = r7
            goto L_0x0153
        L_0x012a:
            java.util.List<com.github.mikephil.charting.utils.FSize> r8 = r0.mCalculatedLineSizes
            r20 = r2
            com.github.mikephil.charting.utils.FSize r2 = com.github.mikephil.charting.utils.FSize.getInstance(r3, r10)
            r8.add(r2)
            float r15 = java.lang.Math.max(r15, r3)
            java.util.List<java.lang.Boolean> r2 = r0.mCalculatedLabelBreakPoints
            r8 = -1
            if (r11 <= r8) goto L_0x0140
            r8 = r11
            goto L_0x0141
        L_0x0140:
            r8 = r12
        L_0x0141:
            r26 = r7
            r19 = 1
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r19)
            r2.set(r8, r7)
            r2 = r17
            goto L_0x0156
        L_0x014f:
            r20 = r2
            r26 = r7
        L_0x0153:
            float r2 = r5 + r17
            float r2 = r2 + r3
        L_0x0156:
            int r3 = r9 + -1
            if (r12 != r3) goto L_0x0168
            java.util.List<com.github.mikephil.charting.utils.FSize> r3 = r0.mCalculatedLineSizes
            com.github.mikephil.charting.utils.FSize r7 = com.github.mikephil.charting.utils.FSize.getInstance(r2, r10)
            r3.add(r7)
            float r3 = java.lang.Math.max(r15, r2)
            r15 = r3
        L_0x0168:
            if (r6 == 0) goto L_0x016d
            r21 = -1
            goto L_0x016f
        L_0x016d:
            r21 = r11
        L_0x016f:
            int r12 = r12 + 1
            r20 = r2
            r2 = r18
            r5 = r22
            r6 = r23
            r8 = r25
            r7 = r26
            r3 = r27
            goto L_0x007d
        L_0x0181:
            r18 = r2
            r27 = r3
            r22 = r5
            r23 = r6
            r26 = r7
            r25 = r8
            r3 = r20
            r11 = r21
            r16 = 0
            r0.mNeededWidth = r15
            java.util.List<com.github.mikephil.charting.utils.FSize> r2 = r0.mCalculatedLineSizes
            int r2 = r2.size()
            float r2 = (float) r2
            float r2 = r2 * r10
            java.util.List<com.github.mikephil.charting.utils.FSize> r5 = r0.mCalculatedLineSizes
            int r5 = r5.size()
            if (r5 != 0) goto L_0x01a8
            r5 = 0
            goto L_0x01b0
        L_0x01a8:
            java.util.List<com.github.mikephil.charting.utils.FSize> r5 = r0.mCalculatedLineSizes
            int r5 = r5.size()
            r6 = 1
            int r5 = r5 - r6
        L_0x01b0:
            float r5 = (float) r5
            float r5 = r5 * r13
            float r2 = r2 + r5
            r0.mNeededHeight = r2
            goto L_0x0230
        L_0x01b8:
            r18 = r2
            r27 = r3
            r22 = r5
            r23 = r6
            r26 = r7
            r25 = r8
            r6 = 1
            r16 = 0
            r2 = 0
            r3 = 0
            r5 = 0
            float r7 = com.github.mikephil.charting.utils.Utils.getLineHeight(r31)
            r8 = 0
            r10 = 0
        L_0x01d0:
            if (r10 >= r9) goto L_0x022b
            r11 = r25[r10]
            com.github.mikephil.charting.components.Legend$LegendForm r12 = r11.form
            com.github.mikephil.charting.components.Legend$LegendForm r13 = com.github.mikephil.charting.components.Legend.LegendForm.NONE
            if (r12 == r13) goto L_0x01dc
            r12 = 1
            goto L_0x01dd
        L_0x01dc:
            r12 = 0
        L_0x01dd:
            float r13 = r11.formSize
            boolean r13 = java.lang.Float.isNaN(r13)
            if (r13 == 0) goto L_0x01e8
            r13 = r18
            goto L_0x01ee
        L_0x01e8:
            float r13 = r11.formSize
            float r13 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r13)
        L_0x01ee:
            java.lang.String r14 = r11.label
            if (r8 != 0) goto L_0x01f4
            r5 = 0
        L_0x01f4:
            if (r12 == 0) goto L_0x01fb
            if (r8 == 0) goto L_0x01fa
            float r5 = r5 + r27
        L_0x01fa:
            float r5 = r5 + r13
        L_0x01fb:
            if (r14 == 0) goto L_0x021c
            if (r12 == 0) goto L_0x0203
            if (r8 != 0) goto L_0x0203
            float r5 = r5 + r4
            goto L_0x020e
        L_0x0203:
            if (r8 == 0) goto L_0x020e
            float r2 = java.lang.Math.max(r2, r5)
            float r15 = r7 + r23
            float r3 = r3 + r15
            r5 = 0
            r8 = 0
        L_0x020e:
            int r15 = com.github.mikephil.charting.utils.Utils.calcTextWidth(r1, r14)
            float r15 = (float) r15
            float r5 = r5 + r15
            int r15 = r9 + -1
            if (r10 >= r15) goto L_0x0224
            float r15 = r7 + r23
            float r3 = r3 + r15
            goto L_0x0224
        L_0x021c:
            r8 = 1
            float r5 = r5 + r13
            int r15 = r9 + -1
            if (r10 >= r15) goto L_0x0224
            float r5 = r5 + r27
        L_0x0224:
            float r2 = java.lang.Math.max(r2, r5)
            int r10 = r10 + 1
            goto L_0x01d0
        L_0x022b:
            r0.mNeededWidth = r2
            r0.mNeededHeight = r3
        L_0x0230:
            float r2 = r0.mNeededHeight
            float r3 = r0.mYOffset
            float r2 = r2 + r3
            r0.mNeededHeight = r2
            float r2 = r0.mNeededWidth
            float r3 = r0.mXOffset
            float r2 = r2 + r3
            r0.mNeededWidth = r2
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.mikephil.charting.components.Legend.calculateDimensions(android.graphics.Paint, com.github.mikephil.charting.utils.ViewPortHandler):void");
    }

    /* renamed from: com.github.mikephil.charting.components.Legend$1 */
    static /* synthetic */ class C05731 {

        /* renamed from: $SwitchMap$com$github$mikephil$charting$components$Legend$LegendOrientation */
        static final /* synthetic */ int[] f70x9c9dbef = new int[LegendOrientation.values().length];

        static {
            try {
                f70x9c9dbef[LegendOrientation.VERTICAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f70x9c9dbef[LegendOrientation.HORIZONTAL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }
}
