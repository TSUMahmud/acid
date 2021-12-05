package com.github.mikephil.charting.model;

public class GradientColor {
    private int endColor;
    private int startColor;

    public GradientColor(int startColor2, int endColor2) {
        this.startColor = startColor2;
        this.endColor = endColor2;
    }

    public int getStartColor() {
        return this.startColor;
    }

    public void setStartColor(int startColor2) {
        this.startColor = startColor2;
    }

    public int getEndColor() {
        return this.endColor;
    }

    public void setEndColor(int endColor2) {
        this.endColor = endColor2;
    }
}
