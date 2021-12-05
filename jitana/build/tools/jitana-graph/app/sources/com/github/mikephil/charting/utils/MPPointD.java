package com.github.mikephil.charting.utils;

import com.github.mikephil.charting.utils.ObjectPool;
import java.util.List;

public class MPPointD extends ObjectPool.Poolable {
    private static ObjectPool<MPPointD> pool = ObjectPool.create(64, new MPPointD(Utils.DOUBLE_EPSILON, Utils.DOUBLE_EPSILON));

    /* renamed from: x */
    public double f88x;

    /* renamed from: y */
    public double f89y;

    static {
        pool.setReplenishPercentage(0.5f);
    }

    public static MPPointD getInstance(double x, double y) {
        MPPointD result = pool.get();
        result.f88x = x;
        result.f89y = y;
        return result;
    }

    public static void recycleInstance(MPPointD instance) {
        pool.recycle(instance);
    }

    public static void recycleInstances(List<MPPointD> instances) {
        pool.recycle(instances);
    }

    /* access modifiers changed from: protected */
    public ObjectPool.Poolable instantiate() {
        return new MPPointD(Utils.DOUBLE_EPSILON, Utils.DOUBLE_EPSILON);
    }

    private MPPointD(double x, double y) {
        this.f88x = x;
        this.f89y = y;
    }

    public String toString() {
        return "MPPointD, x: " + this.f88x + ", y: " + this.f89y;
    }
}
