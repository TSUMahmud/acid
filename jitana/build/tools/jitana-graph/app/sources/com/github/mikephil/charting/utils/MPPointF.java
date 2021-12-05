package com.github.mikephil.charting.utils;

import android.os.Parcel;
import android.os.Parcelable;
import com.github.mikephil.charting.utils.ObjectPool;
import java.util.List;

public class MPPointF extends ObjectPool.Poolable {
    public static final Parcelable.Creator<MPPointF> CREATOR = new Parcelable.Creator<MPPointF>() {
        public MPPointF createFromParcel(Parcel in) {
            MPPointF r = new MPPointF(0.0f, 0.0f);
            r.my_readFromParcel(in);
            return r;
        }

        public MPPointF[] newArray(int size) {
            return new MPPointF[size];
        }
    };
    private static ObjectPool<MPPointF> pool = ObjectPool.create(32, new MPPointF(0.0f, 0.0f));

    /* renamed from: x */
    public float f90x;

    /* renamed from: y */
    public float f91y;

    static {
        pool.setReplenishPercentage(0.5f);
    }

    public MPPointF() {
    }

    public MPPointF(float x, float y) {
        this.f90x = x;
        this.f91y = y;
    }

    public static MPPointF getInstance(float x, float y) {
        MPPointF result = pool.get();
        result.f90x = x;
        result.f91y = y;
        return result;
    }

    public static MPPointF getInstance() {
        return pool.get();
    }

    public static MPPointF getInstance(MPPointF copy) {
        MPPointF result = pool.get();
        result.f90x = copy.f90x;
        result.f91y = copy.f91y;
        return result;
    }

    public static void recycleInstance(MPPointF instance) {
        pool.recycle(instance);
    }

    public static void recycleInstances(List<MPPointF> instances) {
        pool.recycle(instances);
    }

    public void my_readFromParcel(Parcel in) {
        this.f90x = in.readFloat();
        this.f91y = in.readFloat();
    }

    public float getX() {
        return this.f90x;
    }

    public float getY() {
        return this.f91y;
    }

    /* access modifiers changed from: protected */
    public ObjectPool.Poolable instantiate() {
        return new MPPointF(0.0f, 0.0f);
    }
}
