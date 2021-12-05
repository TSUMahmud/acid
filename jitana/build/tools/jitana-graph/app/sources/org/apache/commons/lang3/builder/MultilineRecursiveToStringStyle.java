package org.apache.commons.lang3.builder;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

public class MultilineRecursiveToStringStyle extends RecursiveToStringStyle {
    private static final int INDENT = 2;
    private static final long serialVersionUID = 1;
    private int spaces = 2;

    public MultilineRecursiveToStringStyle() {
        resetIndent();
    }

    private void resetIndent() {
        setArrayStart("{" + System.lineSeparator() + spacer(this.spaces));
        setArraySeparator("," + System.lineSeparator() + spacer(this.spaces));
        setArrayEnd(System.lineSeparator() + spacer(this.spaces + -2) + "}");
        setContentStart("[" + System.lineSeparator() + spacer(this.spaces));
        setFieldSeparator("," + System.lineSeparator() + spacer(this.spaces));
        setContentEnd(System.lineSeparator() + spacer(this.spaces + -2) + "]");
    }

    private StringBuilder spacer(int spaces2) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spaces2; i++) {
            sb.append(StringUtils.SPACE);
        }
        return sb;
    }

    public void appendDetail(StringBuffer buffer, String fieldName, Object value) {
        if (ClassUtils.isPrimitiveWrapper(value.getClass()) || String.class.equals(value.getClass()) || !accept(value.getClass())) {
            super.appendDetail(buffer, fieldName, value);
            return;
        }
        this.spaces += 2;
        resetIndent();
        buffer.append(ReflectionToStringBuilder.toString(value, this));
        this.spaces -= 2;
        resetIndent();
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, Object[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    /* access modifiers changed from: protected */
    public void reflectionAppendArrayDetail(StringBuffer buffer, String fieldName, Object array) {
        this.spaces += 2;
        resetIndent();
        super.reflectionAppendArrayDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, long[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, int[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, short[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, byte[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, char[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, double[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, float[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    /* access modifiers changed from: protected */
    public void appendDetail(StringBuffer buffer, String fieldName, boolean[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }
}
