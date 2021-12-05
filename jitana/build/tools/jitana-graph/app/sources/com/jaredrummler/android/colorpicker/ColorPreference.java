package com.jaredrummler.android.colorpicker;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.view.ViewCompat;

public class ColorPreference extends Preference implements ColorPickerDialogListener {
    private static final int SIZE_LARGE = 1;
    private static final int SIZE_NORMAL = 0;
    private boolean allowCustom;
    private boolean allowPresets;
    private int color = ViewCompat.MEASURED_STATE_MASK;
    private int colorShape;
    private int dialogTitle;
    private int dialogType;
    private OnShowDialogListener onShowDialogListener;
    private int[] presets;
    private int previewSize;
    private boolean showAlphaSlider;
    private boolean showColorShades;
    private boolean showDialog;

    public interface OnShowDialogListener {
        void onShowColorPickerDialog(String str, int i);
    }

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ColorPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setPersistent(true);
        TypedArray a = getContext().obtainStyledAttributes(attrs, C0763R.styleable.ColorPreference);
        this.showDialog = a.getBoolean(C0763R.styleable.ColorPreference_cpv_showDialog, true);
        this.dialogType = a.getInt(C0763R.styleable.ColorPreference_cpv_dialogType, 1);
        this.colorShape = a.getInt(C0763R.styleable.ColorPreference_cpv_colorShape, 1);
        this.allowPresets = a.getBoolean(C0763R.styleable.ColorPreference_cpv_allowPresets, true);
        this.allowCustom = a.getBoolean(C0763R.styleable.ColorPreference_cpv_allowCustom, true);
        this.showAlphaSlider = a.getBoolean(C0763R.styleable.ColorPreference_cpv_showAlphaSlider, false);
        this.showColorShades = a.getBoolean(C0763R.styleable.ColorPreference_cpv_showColorShades, true);
        this.previewSize = a.getInt(C0763R.styleable.ColorPreference_cpv_previewSize, 0);
        int presetsResId = a.getResourceId(C0763R.styleable.ColorPreference_cpv_colorPresets, 0);
        this.dialogTitle = a.getResourceId(C0763R.styleable.ColorPreference_cpv_dialogTitle, C0763R.string.cpv_default_title);
        if (presetsResId != 0) {
            this.presets = getContext().getResources().getIntArray(presetsResId);
        } else {
            this.presets = ColorPickerDialog.MATERIAL_COLORS;
        }
        if (this.colorShape == 1) {
            setWidgetLayoutResource(this.previewSize == 1 ? C0763R.layout.cpv_preference_circle_large : C0763R.layout.cpv_preference_circle);
        } else {
            setWidgetLayoutResource(this.previewSize == 1 ? C0763R.layout.cpv_preference_square_large : C0763R.layout.cpv_preference_square);
        }
        a.recycle();
    }

    /* access modifiers changed from: protected */
    public void onClick() {
        super.onClick();
        OnShowDialogListener onShowDialogListener2 = this.onShowDialogListener;
        if (onShowDialogListener2 != null) {
            onShowDialogListener2.onShowColorPickerDialog((String) getTitle(), this.color);
        } else if (this.showDialog) {
            ColorPickerDialog dialog = ColorPickerDialog.newBuilder().setDialogType(this.dialogType).setDialogTitle(this.dialogTitle).setColorShape(this.colorShape).setPresets(this.presets).setAllowPresets(this.allowPresets).setAllowCustom(this.allowCustom).setShowAlphaSlider(this.showAlphaSlider).setShowColorShades(this.showColorShades).setColor(this.color).create();
            dialog.setColorPickerDialogListener(this);
            dialog.show(((Activity) getContext()).getFragmentManager(), getFragmentTag());
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToActivity() {
        ColorPickerDialog fragment;
        super.onAttachedToActivity();
        if (this.showDialog && (fragment = (ColorPickerDialog) ((Activity) getContext()).getFragmentManager().findFragmentByTag(getFragmentTag())) != null) {
            fragment.setColorPickerDialogListener(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        ColorPanelView preview = (ColorPanelView) view.findViewById(C0763R.C0765id.cpv_preference_preview_color_panel);
        if (preview != null) {
            preview.setColor(this.color);
        }
    }

    /* access modifiers changed from: protected */
    public void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            this.color = getPersistedInt(ViewCompat.MEASURED_STATE_MASK);
            return;
        }
        this.color = ((Integer) defaultValue).intValue();
        persistInt(this.color);
    }

    /* access modifiers changed from: protected */
    public Object onGetDefaultValue(TypedArray a, int index) {
        return Integer.valueOf(a.getInteger(index, ViewCompat.MEASURED_STATE_MASK));
    }

    public void onColorSelected(int dialogId, int color2) {
        saveValue(color2);
    }

    public void onDialogDismissed(int dialogId) {
    }

    public void saveValue(int color2) {
        this.color = color2;
        persistInt(this.color);
        notifyChanged();
        callChangeListener(Integer.valueOf(color2));
    }

    public void setPresets(int[] presets2) {
        this.presets = presets2;
    }

    public int[] getPresets() {
        return this.presets;
    }

    public void setOnShowDialogListener(OnShowDialogListener listener) {
        this.onShowDialogListener = listener;
    }

    public String getFragmentTag() {
        return "color_" + getKey();
    }
}
