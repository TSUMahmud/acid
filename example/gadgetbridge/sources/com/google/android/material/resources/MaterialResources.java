package com.google.android.material.resources;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.appcompat.content.res.AppCompatResources;

public class MaterialResources {
    private MaterialResources() {
    }

    public static ColorStateList getColorStateList(Context context, TypedArray attributes, int index) {
        int resourceId;
        ColorStateList value;
        if (!attributes.hasValue(index) || (resourceId = attributes.getResourceId(index, 0)) == 0 || (value = AppCompatResources.getColorStateList(context, resourceId)) == null) {
            return attributes.getColorStateList(index);
        }
        return value;
    }

    public static Drawable getDrawable(Context context, TypedArray attributes, int index) {
        int resourceId;
        Drawable value;
        if (!attributes.hasValue(index) || (resourceId = attributes.getResourceId(index, 0)) == 0 || (value = AppCompatResources.getDrawable(context, resourceId)) == null) {
            return attributes.getDrawable(index);
        }
        return value;
    }

    public static TextAppearance getTextAppearance(Context context, TypedArray attributes, int index) {
        int resourceId;
        if (!attributes.hasValue(index) || (resourceId = attributes.getResourceId(index, 0)) == 0) {
            return null;
        }
        return new TextAppearance(context, resourceId);
    }

    static int getIndexWithValue(TypedArray attributes, int a, int b) {
        if (attributes.hasValue(a)) {
            return a;
        }
        return b;
    }
}
