package com.google.android.material.animation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import androidx.collection.SimpleArrayMap;
import java.util.ArrayList;
import java.util.List;
import p005ch.qos.logback.core.CoreConstants;

public class MotionSpec {
    private static final String TAG = "MotionSpec";
    private final SimpleArrayMap<String, MotionTiming> timings = new SimpleArrayMap<>();

    public boolean hasTiming(String name) {
        return this.timings.get(name) != null;
    }

    public MotionTiming getTiming(String name) {
        if (hasTiming(name)) {
            return this.timings.get(name);
        }
        throw new IllegalArgumentException();
    }

    public void setTiming(String name, MotionTiming timing) {
        this.timings.put(name, timing);
    }

    public long getTotalDuration() {
        long duration = 0;
        int count = this.timings.size();
        for (int i = 0; i < count; i++) {
            MotionTiming timing = this.timings.valueAt(i);
            duration = Math.max(duration, timing.getDelay() + timing.getDuration());
        }
        return duration;
    }

    public static MotionSpec createFromAttribute(Context context, TypedArray attributes, int index) {
        int resourceId;
        if (!attributes.hasValue(index) || (resourceId = attributes.getResourceId(index, 0)) == 0) {
            return null;
        }
        return createFromResource(context, resourceId);
    }

    public static MotionSpec createFromResource(Context context, int id) {
        try {
            Animator animator = AnimatorInflater.loadAnimator(context, id);
            if (animator instanceof AnimatorSet) {
                return createSpecFromAnimators(((AnimatorSet) animator).getChildAnimations());
            }
            if (animator == null) {
                return null;
            }
            List<Animator> animators = new ArrayList<>();
            animators.add(animator);
            return createSpecFromAnimators(animators);
        } catch (Exception e) {
            Log.w(TAG, "Can't load animation resource ID #0x" + Integer.toHexString(id), e);
            return null;
        }
    }

    private static MotionSpec createSpecFromAnimators(List<Animator> animators) {
        MotionSpec spec = new MotionSpec();
        int count = animators.size();
        for (int i = 0; i < count; i++) {
            addTimingFromAnimator(spec, animators.get(i));
        }
        return spec;
    }

    private static void addTimingFromAnimator(MotionSpec spec, Animator animator) {
        if (animator instanceof ObjectAnimator) {
            ObjectAnimator anim = (ObjectAnimator) animator;
            spec.setTiming(anim.getPropertyName(), MotionTiming.createFromAnimator(anim));
            return;
        }
        throw new IllegalArgumentException("Animator must be an ObjectAnimator: " + animator);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.timings.equals(((MotionSpec) o).timings);
    }

    public int hashCode() {
        return this.timings.hashCode();
    }

    public String toString() {
        return 10 + getClass().getName() + CoreConstants.CURLY_LEFT + Integer.toHexString(System.identityHashCode(this)) + " timings: " + this.timings + "}\n";
    }
}
