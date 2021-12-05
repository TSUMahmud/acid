package com.github.mikephil.charting.data;

import android.graphics.drawable.Drawable;

public abstract class BaseEntry {
    private Object mData;
    private Drawable mIcon;

    /* renamed from: y */
    private float f71y;

    public BaseEntry() {
        this.f71y = 0.0f;
        this.mData = null;
        this.mIcon = null;
    }

    public BaseEntry(float y) {
        this.f71y = 0.0f;
        this.mData = null;
        this.mIcon = null;
        this.f71y = y;
    }

    public BaseEntry(float y, Object data) {
        this(y);
        this.mData = data;
    }

    public BaseEntry(float y, Drawable icon) {
        this(y);
        this.mIcon = icon;
    }

    public BaseEntry(float y, Drawable icon, Object data) {
        this(y);
        this.mIcon = icon;
        this.mData = data;
    }

    public float getY() {
        return this.f71y;
    }

    public void setIcon(Drawable icon) {
        this.mIcon = icon;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public void setY(float y) {
        this.f71y = y;
    }

    public Object getData() {
        return this.mData;
    }

    public void setData(Object data) {
        this.mData = data;
    }
}
