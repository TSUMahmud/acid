package com.github.mikephil.charting.highlight;

public final class Range {
    public float from;

    /* renamed from: to */
    public float f79to;

    public Range(float from2, float to) {
        this.from = from2;
        this.f79to = to;
    }

    public boolean contains(float value) {
        if (value <= this.from || value > this.f79to) {
            return false;
        }
        return true;
    }

    public boolean isLarger(float value) {
        return value > this.f79to;
    }

    public boolean isSmaller(float value) {
        return value < this.from;
    }
}
