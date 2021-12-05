package com.github.mikephil.charting.matrix;

public final class Vector3 {
    public static final Vector3 UNIT_X = new Vector3(1.0f, 0.0f, 0.0f);
    public static final Vector3 UNIT_Y = new Vector3(0.0f, 1.0f, 0.0f);
    public static final Vector3 UNIT_Z = new Vector3(0.0f, 0.0f, 1.0f);
    public static final Vector3 ZERO = new Vector3(0.0f, 0.0f, 0.0f);

    /* renamed from: x */
    public float f80x;

    /* renamed from: y */
    public float f81y;

    /* renamed from: z */
    public float f82z;

    public Vector3() {
    }

    public Vector3(float[] array) {
        set(array[0], array[1], array[2]);
    }

    public Vector3(float xValue, float yValue, float zValue) {
        set(xValue, yValue, zValue);
    }

    public Vector3(Vector3 other) {
        set(other);
    }

    public final void add(Vector3 other) {
        this.f80x += other.f80x;
        this.f81y += other.f81y;
        this.f82z += other.f82z;
    }

    public final void add(float otherX, float otherY, float otherZ) {
        this.f80x += otherX;
        this.f81y += otherY;
        this.f82z += otherZ;
    }

    public final void subtract(Vector3 other) {
        this.f80x -= other.f80x;
        this.f81y -= other.f81y;
        this.f82z -= other.f82z;
    }

    public final void subtractMultiple(Vector3 other, float multiplicator) {
        this.f80x -= other.f80x * multiplicator;
        this.f81y -= other.f81y * multiplicator;
        this.f82z -= other.f82z * multiplicator;
    }

    public final void multiply(float magnitude) {
        this.f80x *= magnitude;
        this.f81y *= magnitude;
        this.f82z *= magnitude;
    }

    public final void multiply(Vector3 other) {
        this.f80x *= other.f80x;
        this.f81y *= other.f81y;
        this.f82z *= other.f82z;
    }

    public final void divide(float magnitude) {
        if (magnitude != 0.0f) {
            this.f80x /= magnitude;
            this.f81y /= magnitude;
            this.f82z /= magnitude;
        }
    }

    public final void set(Vector3 other) {
        this.f80x = other.f80x;
        this.f81y = other.f81y;
        this.f82z = other.f82z;
    }

    public final void set(float xValue, float yValue, float zValue) {
        this.f80x = xValue;
        this.f81y = yValue;
        this.f82z = zValue;
    }

    public final float dot(Vector3 other) {
        return (this.f80x * other.f80x) + (this.f81y * other.f81y) + (this.f82z * other.f82z);
    }

    public final Vector3 cross(Vector3 other) {
        float f = this.f81y;
        float f2 = other.f82z;
        float f3 = this.f82z;
        float f4 = other.f81y;
        float f5 = (f * f2) - (f3 * f4);
        float f6 = other.f80x;
        float f7 = this.f80x;
        return new Vector3(f5, (f3 * f6) - (f2 * f7), (f7 * f4) - (f * f6));
    }

    public final float length() {
        return (float) Math.sqrt((double) length2());
    }

    public final float length2() {
        float f = this.f80x;
        float f2 = this.f81y;
        float f3 = (f * f) + (f2 * f2);
        float f4 = this.f82z;
        return f3 + (f4 * f4);
    }

    public final float distance2(Vector3 other) {
        float dx = this.f80x - other.f80x;
        float dy = this.f81y - other.f81y;
        float dz = this.f82z - other.f82z;
        return (dx * dx) + (dy * dy) + (dz * dz);
    }

    public final float normalize() {
        float magnitude = length();
        if (magnitude != 0.0f) {
            this.f80x /= magnitude;
            this.f81y /= magnitude;
            this.f82z /= magnitude;
        }
        return magnitude;
    }

    public final void zero() {
        set(0.0f, 0.0f, 0.0f);
    }

    public final boolean pointsInSameDirection(Vector3 other) {
        return dot(other) > 0.0f;
    }
}
