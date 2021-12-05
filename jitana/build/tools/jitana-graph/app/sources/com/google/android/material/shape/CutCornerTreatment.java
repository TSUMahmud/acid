package com.google.android.material.shape;

public class CutCornerTreatment extends CornerTreatment {
    private final float size;

    public CutCornerTreatment(float size2) {
        this.size = size2;
    }

    public void getCornerPath(float angle, float interpolation, ShapePath shapePath) {
        shapePath.reset(0.0f, this.size * interpolation);
        double sin = Math.sin((double) angle);
        double d = (double) this.size;
        Double.isNaN(d);
        double d2 = sin * d;
        double d3 = (double) interpolation;
        Double.isNaN(d3);
        double cos = Math.cos((double) angle);
        double d4 = (double) this.size;
        Double.isNaN(d4);
        double d5 = cos * d4;
        double d6 = (double) interpolation;
        Double.isNaN(d6);
        shapePath.lineTo((float) (d2 * d3), (float) (d5 * d6));
    }
}
