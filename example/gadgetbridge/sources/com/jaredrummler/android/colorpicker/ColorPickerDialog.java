package com.jaredrummler.android.colorpicker;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import com.github.mikephil.charting.utils.Utils;
import com.jaredrummler.android.colorpicker.ColorPaletteAdapter;
import com.jaredrummler.android.colorpicker.ColorPickerView;
import java.util.Arrays;
import java.util.Locale;

public class ColorPickerDialog extends DialogFragment implements View.OnTouchListener, ColorPickerView.OnColorChangedListener, TextWatcher {
    static final int ALPHA_THRESHOLD = 165;
    private static final String ARG_ALLOW_CUSTOM = "allowCustom";
    private static final String ARG_ALLOW_PRESETS = "allowPresets";
    private static final String ARG_ALPHA = "alpha";
    private static final String ARG_COLOR = "color";
    private static final String ARG_COLOR_SHAPE = "colorShape";
    private static final String ARG_CUSTOM_BUTTON_TEXT = "customButtonText";
    private static final String ARG_DIALOG_TITLE = "dialogTitle";
    private static final String ARG_ID = "id";
    private static final String ARG_PRESETS = "presets";
    private static final String ARG_PRESETS_BUTTON_TEXT = "presetsButtonText";
    private static final String ARG_SELECTED_BUTTON_TEXT = "selectedButtonText";
    private static final String ARG_SHOW_COLOR_SHADES = "showColorShades";
    private static final String ARG_TYPE = "dialogType";
    public static final int[] MATERIAL_COLORS = {-769226, -1499549, -54125, -6543440, -10011977, -12627531, -14575885, -16537100, -16728876, -16738680, -11751600, -7617718, -3285959, -5317, -16121, -26624, -8825528, -10453621, -6381922};
    public static final int TYPE_CUSTOM = 0;
    public static final int TYPE_PRESETS = 1;
    ColorPaletteAdapter adapter;
    int color;
    ColorPickerView colorPicker;
    ColorPickerDialogListener colorPickerDialogListener;
    int colorShape;
    /* access modifiers changed from: private */
    public int customButtonStringRes;
    int dialogId;
    int dialogType;
    private boolean fromEditText;
    EditText hexEditText;
    ColorPanelView newColorPanel;
    int[] presets;
    /* access modifiers changed from: private */
    public int presetsButtonStringRes;
    FrameLayout rootView;
    LinearLayout shadesLayout;
    boolean showAlphaSlider;
    boolean showColorShades;
    TextView transparencyPercText;
    SeekBar transparencySeekBar;

    public @interface DialogType {
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (this.colorPickerDialogListener == null && (activity instanceof ColorPickerDialogListener)) {
            this.colorPickerDialogListener = (ColorPickerDialogListener) activity;
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int neutralButtonStringRes;
        this.dialogId = getArguments().getInt("id");
        this.showAlphaSlider = getArguments().getBoolean(ARG_ALPHA);
        this.showColorShades = getArguments().getBoolean(ARG_SHOW_COLOR_SHADES);
        this.colorShape = getArguments().getInt(ARG_COLOR_SHAPE);
        if (savedInstanceState == null) {
            this.color = getArguments().getInt(ARG_COLOR);
            this.dialogType = getArguments().getInt(ARG_TYPE);
        } else {
            this.color = savedInstanceState.getInt(ARG_COLOR);
            this.dialogType = savedInstanceState.getInt(ARG_TYPE);
        }
        this.rootView = new FrameLayout(getActivity());
        int i = this.dialogType;
        if (i == 0) {
            this.rootView.addView(createPickerView());
        } else if (i == 1) {
            this.rootView.addView(createPresetsView());
        }
        int selectedButtonStringRes = getArguments().getInt(ARG_SELECTED_BUTTON_TEXT);
        if (selectedButtonStringRes == 0) {
            selectedButtonStringRes = C0763R.string.cpv_select;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView((View) this.rootView).setPositiveButton(selectedButtonStringRes, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ColorPickerDialog.this.colorPickerDialogListener.onColorSelected(ColorPickerDialog.this.dialogId, ColorPickerDialog.this.color);
            }
        });
        int dialogTitleStringRes = getArguments().getInt(ARG_DIALOG_TITLE);
        if (dialogTitleStringRes != 0) {
            builder.setTitle(dialogTitleStringRes);
        }
        this.presetsButtonStringRes = getArguments().getInt(ARG_PRESETS_BUTTON_TEXT);
        this.customButtonStringRes = getArguments().getInt(ARG_CUSTOM_BUTTON_TEXT);
        if (this.dialogType == 0 && getArguments().getBoolean(ARG_ALLOW_PRESETS)) {
            neutralButtonStringRes = this.presetsButtonStringRes;
            if (neutralButtonStringRes == 0) {
                neutralButtonStringRes = C0763R.string.cpv_presets;
            }
        } else if (this.dialogType != 1 || !getArguments().getBoolean(ARG_ALLOW_CUSTOM)) {
            neutralButtonStringRes = 0;
        } else {
            neutralButtonStringRes = this.customButtonStringRes;
            if (neutralButtonStringRes == 0) {
                neutralButtonStringRes = C0763R.string.cpv_custom;
            }
        }
        if (neutralButtonStringRes != 0) {
            builder.setNeutralButton(neutralButtonStringRes, (DialogInterface.OnClickListener) null);
        }
        return builder.create();
    }

    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getWindow().clearFlags(131080);
        dialog.getWindow().setSoftInputMode(4);
        Button neutralButton = dialog.getButton(-3);
        if (neutralButton != null) {
            neutralButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ColorPickerDialog.this.rootView.removeAllViews();
                    int i = ColorPickerDialog.this.dialogType;
                    if (i == 0) {
                        ColorPickerDialog colorPickerDialog = ColorPickerDialog.this;
                        colorPickerDialog.dialogType = 1;
                        ((Button) v).setText(colorPickerDialog.customButtonStringRes != 0 ? ColorPickerDialog.this.customButtonStringRes : C0763R.string.cpv_custom);
                        ColorPickerDialog.this.rootView.addView(ColorPickerDialog.this.createPresetsView());
                    } else if (i == 1) {
                        ColorPickerDialog colorPickerDialog2 = ColorPickerDialog.this;
                        colorPickerDialog2.dialogType = 0;
                        ((Button) v).setText(colorPickerDialog2.presetsButtonStringRes != 0 ? ColorPickerDialog.this.presetsButtonStringRes : C0763R.string.cpv_presets);
                        ColorPickerDialog.this.rootView.addView(ColorPickerDialog.this.createPickerView());
                    }
                }
            });
        }
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        this.colorPickerDialogListener.onDialogDismissed(this.dialogId);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_COLOR, this.color);
        outState.putInt(ARG_TYPE, this.dialogType);
        super.onSaveInstanceState(outState);
    }

    public void setColorPickerDialogListener(ColorPickerDialogListener colorPickerDialogListener2) {
        this.colorPickerDialogListener = colorPickerDialogListener2;
    }

    /* access modifiers changed from: package-private */
    public View createPickerView() {
        View contentView = View.inflate(getActivity(), C0763R.layout.cpv_dialog_color_picker, (ViewGroup) null);
        this.colorPicker = (ColorPickerView) contentView.findViewById(C0763R.C0765id.cpv_color_picker_view);
        ColorPanelView oldColorPanel = (ColorPanelView) contentView.findViewById(C0763R.C0765id.cpv_color_panel_old);
        this.newColorPanel = (ColorPanelView) contentView.findViewById(C0763R.C0765id.cpv_color_panel_new);
        ImageView arrowRight = (ImageView) contentView.findViewById(C0763R.C0765id.cpv_arrow_right);
        this.hexEditText = (EditText) contentView.findViewById(C0763R.C0765id.cpv_hex);
        try {
            TypedValue value = new TypedValue();
            TypedArray typedArray = getActivity().obtainStyledAttributes(value.data, new int[]{16842806});
            int arrowColor = typedArray.getColor(0, ViewCompat.MEASURED_STATE_MASK);
            typedArray.recycle();
            arrowRight.setColorFilter(arrowColor);
        } catch (Exception e) {
        }
        this.colorPicker.setAlphaSliderVisible(this.showAlphaSlider);
        oldColorPanel.setColor(getArguments().getInt(ARG_COLOR));
        this.colorPicker.setColor(this.color, true);
        this.newColorPanel.setColor(this.color);
        setHex(this.color);
        if (!this.showAlphaSlider) {
            this.hexEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        }
        this.newColorPanel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ColorPickerDialog.this.newColorPanel.getColor() == ColorPickerDialog.this.color) {
                    ColorPickerDialog.this.colorPickerDialogListener.onColorSelected(ColorPickerDialog.this.dialogId, ColorPickerDialog.this.color);
                    ColorPickerDialog.this.dismiss();
                }
            }
        });
        contentView.setOnTouchListener(this);
        this.colorPicker.setOnColorChangedListener(this);
        this.hexEditText.addTextChangedListener(this);
        this.hexEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((InputMethodManager) ColorPickerDialog.this.getActivity().getSystemService("input_method")).showSoftInput(ColorPickerDialog.this.hexEditText, 1);
                }
            }
        });
        return contentView;
    }

    public boolean onTouch(View v, MotionEvent event) {
        EditText editText = this.hexEditText;
        if (v == editText || !editText.hasFocus()) {
            return false;
        }
        this.hexEditText.clearFocus();
        ((InputMethodManager) getActivity().getSystemService("input_method")).hideSoftInputFromWindow(this.hexEditText.getWindowToken(), 0);
        this.hexEditText.clearFocus();
        return true;
    }

    public void onColorChanged(int newColor) {
        this.color = newColor;
        this.newColorPanel.setColor(newColor);
        if (!this.fromEditText) {
            setHex(newColor);
            if (this.hexEditText.hasFocus()) {
                ((InputMethodManager) getActivity().getSystemService("input_method")).hideSoftInputFromWindow(this.hexEditText.getWindowToken(), 0);
                this.hexEditText.clearFocus();
            }
        }
        this.fromEditText = false;
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void afterTextChanged(Editable s) {
        int color2;
        if (this.hexEditText.isFocused() && (color2 = parseColorString(s.toString())) != this.colorPicker.getColor()) {
            this.fromEditText = true;
            this.colorPicker.setColor(color2, true);
        }
    }

    private void setHex(int color2) {
        if (this.showAlphaSlider) {
            this.hexEditText.setText(String.format("%08X", new Object[]{Integer.valueOf(color2)}));
            return;
        }
        this.hexEditText.setText(String.format("%06X", new Object[]{Integer.valueOf(16777215 & color2)}));
    }

    private int parseColorString(String colorString) throws NumberFormatException {
        int g;
        int a;
        int r;
        int b = 0;
        if (colorString.startsWith("#")) {
            colorString = colorString.substring(1);
        }
        if (colorString.length() == 0) {
            r = 0;
            a = 255;
            g = 0;
        } else if (colorString.length() <= 2) {
            a = 255;
            r = 0;
            b = Integer.parseInt(colorString, 16);
            g = 0;
        } else if (colorString.length() == 3) {
            int r2 = Integer.parseInt(colorString.substring(0, 1), 16);
            int g2 = Integer.parseInt(colorString.substring(1, 2), 16);
            b = Integer.parseInt(colorString.substring(2, 3), 16);
            g = g2;
            a = 255;
            r = r2;
        } else if (colorString.length() == 4) {
            a = 255;
            int g3 = Integer.parseInt(colorString.substring(0, 2), 16);
            r = 0;
            b = Integer.parseInt(colorString.substring(2, 4), 16);
            g = g3;
        } else if (colorString.length() == 5) {
            int r3 = Integer.parseInt(colorString.substring(0, 1), 16);
            int g4 = Integer.parseInt(colorString.substring(1, 3), 16);
            b = Integer.parseInt(colorString.substring(3, 5), 16);
            int i = g4;
            a = 255;
            r = r3;
            g = i;
        } else if (colorString.length() == 6) {
            a = 255;
            r = Integer.parseInt(colorString.substring(0, 2), 16);
            g = Integer.parseInt(colorString.substring(2, 4), 16);
            b = Integer.parseInt(colorString.substring(4, 6), 16);
        } else if (colorString.length() == 7) {
            int a2 = Integer.parseInt(colorString.substring(0, 1), 16);
            int r4 = Integer.parseInt(colorString.substring(1, 3), 16);
            g = Integer.parseInt(colorString.substring(3, 5), 16);
            b = Integer.parseInt(colorString.substring(5, 7), 16);
            int i2 = r4;
            a = a2;
            r = i2;
        } else if (colorString.length() == 8) {
            int a3 = Integer.parseInt(colorString.substring(0, 2), 16);
            int r5 = Integer.parseInt(colorString.substring(2, 4), 16);
            int g5 = Integer.parseInt(colorString.substring(4, 6), 16);
            b = Integer.parseInt(colorString.substring(6, 8), 16);
            a = a3;
            r = r5;
            g = g5;
        } else {
            b = -1;
            g = -1;
            r = -1;
            a = -1;
        }
        return Color.argb(a, r, g, b);
    }

    /* access modifiers changed from: package-private */
    public View createPresetsView() {
        View contentView = View.inflate(getActivity(), C0763R.layout.cpv_dialog_presets, (ViewGroup) null);
        this.shadesLayout = (LinearLayout) contentView.findViewById(C0763R.C0765id.shades_layout);
        this.transparencySeekBar = (SeekBar) contentView.findViewById(C0763R.C0765id.transparency_seekbar);
        this.transparencyPercText = (TextView) contentView.findViewById(C0763R.C0765id.transparency_text);
        GridView gridView = (GridView) contentView.findViewById(C0763R.C0765id.gridView);
        loadPresets();
        if (this.showColorShades) {
            createColorShades(this.color);
        } else {
            this.shadesLayout.setVisibility(8);
            contentView.findViewById(C0763R.C0765id.shades_divider).setVisibility(8);
        }
        this.adapter = new ColorPaletteAdapter(new ColorPaletteAdapter.OnColorSelectedListener() {
            public void onColorSelected(int newColor) {
                if (ColorPickerDialog.this.color == newColor) {
                    ColorPickerDialog.this.colorPickerDialogListener.onColorSelected(ColorPickerDialog.this.dialogId, ColorPickerDialog.this.color);
                    ColorPickerDialog.this.dismiss();
                    return;
                }
                ColorPickerDialog colorPickerDialog = ColorPickerDialog.this;
                colorPickerDialog.color = newColor;
                if (colorPickerDialog.showColorShades) {
                    ColorPickerDialog colorPickerDialog2 = ColorPickerDialog.this;
                    colorPickerDialog2.createColorShades(colorPickerDialog2.color);
                }
            }
        }, this.presets, getSelectedItemPosition(), this.colorShape);
        gridView.setAdapter(this.adapter);
        if (this.showAlphaSlider) {
            setupTransparency();
        } else {
            contentView.findViewById(C0763R.C0765id.transparency_layout).setVisibility(8);
            contentView.findViewById(C0763R.C0765id.transparency_title).setVisibility(8);
        }
        return contentView;
    }

    private void loadPresets() {
        int alpha = Color.alpha(this.color);
        this.presets = getArguments().getIntArray(ARG_PRESETS);
        if (this.presets == null) {
            this.presets = MATERIAL_COLORS;
        }
        boolean isMaterialColors = this.presets == MATERIAL_COLORS;
        int[] iArr = this.presets;
        this.presets = Arrays.copyOf(iArr, iArr.length);
        if (alpha != 255) {
            int i = 0;
            while (true) {
                int[] iArr2 = this.presets;
                if (i >= iArr2.length) {
                    break;
                }
                int color2 = iArr2[i];
                this.presets[i] = Color.argb(alpha, Color.red(color2), Color.green(color2), Color.blue(color2));
                i++;
            }
        }
        this.presets = unshiftIfNotExists(this.presets, this.color);
        if (isMaterialColors) {
            int[] iArr3 = this.presets;
            if (iArr3.length == 19) {
                this.presets = pushIfNotExists(iArr3, Color.argb(alpha, 0, 0, 0));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void createColorShades(int color2) {
        int layoutResId;
        int[] colorShades = getColorShades(color2);
        if (this.shadesLayout.getChildCount() != 0) {
            for (int i = 0; i < this.shadesLayout.getChildCount(); i++) {
                FrameLayout layout = (FrameLayout) this.shadesLayout.getChildAt(i);
                ColorPanelView cpv = (ColorPanelView) layout.findViewById(C0763R.C0765id.cpv_color_panel_view);
                cpv.setColor(colorShades[i]);
                cpv.setTag(false);
                ((ImageView) layout.findViewById(C0763R.C0765id.cpv_color_image_view)).setImageDrawable((Drawable) null);
            }
            return;
        }
        int horizontalPadding = getResources().getDimensionPixelSize(C0763R.dimen.cpv_item_horizontal_padding);
        for (final int colorShade : colorShades) {
            if (this.colorShape == 0) {
                layoutResId = C0763R.layout.cpv_color_item_square;
            } else {
                layoutResId = C0763R.layout.cpv_color_item_circle;
            }
            View view = View.inflate(getActivity(), layoutResId, (ViewGroup) null);
            final ColorPanelView colorPanelView = (ColorPanelView) view.findViewById(C0763R.C0765id.cpv_color_panel_view);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) colorPanelView.getLayoutParams();
            params.rightMargin = horizontalPadding;
            params.leftMargin = horizontalPadding;
            colorPanelView.setLayoutParams(params);
            colorPanelView.setColor(colorShade);
            this.shadesLayout.addView(view);
            colorPanelView.post(new Runnable() {
                public void run() {
                    colorPanelView.setColor(colorShade);
                }
            });
            colorPanelView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!(v.getTag() instanceof Boolean) || !((Boolean) v.getTag()).booleanValue()) {
                        ColorPickerDialog.this.color = colorPanelView.getColor();
                        ColorPickerDialog.this.adapter.selectNone();
                        for (int i = 0; i < ColorPickerDialog.this.shadesLayout.getChildCount(); i++) {
                            FrameLayout layout = (FrameLayout) ColorPickerDialog.this.shadesLayout.getChildAt(i);
                            ColorPanelView cpv = (ColorPanelView) layout.findViewById(C0763R.C0765id.cpv_color_panel_view);
                            ImageView iv = (ImageView) layout.findViewById(C0763R.C0765id.cpv_color_image_view);
                            boolean z = false;
                            iv.setImageResource(cpv == v ? C0763R.C0764drawable.cpv_preset_checked : 0);
                            if ((cpv != v || ColorUtils.calculateLuminance(cpv.getColor()) < 0.65d) && Color.alpha(cpv.getColor()) > 165) {
                                iv.setColorFilter((ColorFilter) null);
                            } else {
                                iv.setColorFilter(ViewCompat.MEASURED_STATE_MASK, PorterDuff.Mode.SRC_IN);
                            }
                            if (cpv == v) {
                                z = true;
                            }
                            cpv.setTag(Boolean.valueOf(z));
                        }
                        return;
                    }
                    ColorPickerDialog.this.colorPickerDialogListener.onColorSelected(ColorPickerDialog.this.dialogId, ColorPickerDialog.this.color);
                    ColorPickerDialog.this.dismiss();
                }
            });
            colorPanelView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    colorPanelView.showHint();
                    return true;
                }
            });
        }
    }

    private int shadeColor(int color2, double percent) {
        String hex = String.format("#%06X", new Object[]{Integer.valueOf(color2 & ViewCompat.MEASURED_SIZE_MASK)});
        long f = Long.parseLong(hex.substring(1), 16);
        double t = percent < Utils.DOUBLE_EPSILON ? 0.0d : 255.0d;
        double p = percent < Utils.DOUBLE_EPSILON ? -1.0d * percent : percent;
        long R = f >> 16;
        long G = (f >> 8) & 255;
        long B = 255 & f;
        int alpha = Color.alpha(color2);
        String str = hex;
        double d = (double) R;
        Double.isNaN(d);
        int red = (int) (Math.round((t - d) * p) + R);
        long j = f;
        double d2 = (double) G;
        Double.isNaN(d2);
        int green = (int) (Math.round((t - d2) * p) + G);
        double d3 = (double) B;
        Double.isNaN(d3);
        return Color.argb(alpha, red, green, (int) (Math.round((t - d3) * p) + B));
    }

    private int[] getColorShades(int color2) {
        return new int[]{shadeColor(color2, 0.9d), shadeColor(color2, 0.7d), shadeColor(color2, 0.5d), shadeColor(color2, 0.333d), shadeColor(color2, 0.166d), shadeColor(color2, -0.125d), shadeColor(color2, -0.25d), shadeColor(color2, -0.375d), shadeColor(color2, -0.5d), shadeColor(color2, -0.675d), shadeColor(color2, -0.7d), shadeColor(color2, -0.775d)};
    }

    private void setupTransparency() {
        int progress = 255 - Color.alpha(this.color);
        this.transparencySeekBar.setMax(255);
        this.transparencySeekBar.setProgress(progress);
        double d = (double) progress;
        Double.isNaN(d);
        this.transparencyPercText.setText(String.format(Locale.ENGLISH, "%d%%", new Object[]{Integer.valueOf((int) ((d * 100.0d) / 255.0d))}));
        this.transparencySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int i = progress;
                double d = (double) i;
                Double.isNaN(d);
                ColorPickerDialog.this.transparencyPercText.setText(String.format(Locale.ENGLISH, "%d%%", new Object[]{Integer.valueOf((int) ((d * 100.0d) / 255.0d))}));
                int alpha = 255 - i;
                for (int i2 = 0; i2 < ColorPickerDialog.this.adapter.colors.length; i2++) {
                    int color = ColorPickerDialog.this.adapter.colors[i2];
                    ColorPickerDialog.this.adapter.colors[i2] = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
                }
                ColorPickerDialog.this.adapter.notifyDataSetChanged();
                for (int i3 = 0; i3 < ColorPickerDialog.this.shadesLayout.getChildCount(); i3++) {
                    FrameLayout layout = (FrameLayout) ColorPickerDialog.this.shadesLayout.getChildAt(i3);
                    ColorPanelView cpv = (ColorPanelView) layout.findViewById(C0763R.C0765id.cpv_color_panel_view);
                    ImageView iv = (ImageView) layout.findViewById(C0763R.C0765id.cpv_color_image_view);
                    if (layout.getTag() == null) {
                        layout.setTag(Integer.valueOf(cpv.getBorderColor()));
                    }
                    int color2 = cpv.getColor();
                    int color3 = Color.argb(alpha, Color.red(color2), Color.green(color2), Color.blue(color2));
                    if (alpha <= 165) {
                        cpv.setBorderColor(color3 | ViewCompat.MEASURED_STATE_MASK);
                    } else {
                        cpv.setBorderColor(((Integer) layout.getTag()).intValue());
                    }
                    if (cpv.getTag() != null && ((Boolean) cpv.getTag()).booleanValue()) {
                        if (alpha <= 165) {
                            iv.setColorFilter(ViewCompat.MEASURED_STATE_MASK, PorterDuff.Mode.SRC_IN);
                        } else if (ColorUtils.calculateLuminance(color3) >= 0.65d) {
                            iv.setColorFilter(ViewCompat.MEASURED_STATE_MASK, PorterDuff.Mode.SRC_IN);
                        } else {
                            iv.setColorFilter(-1, PorterDuff.Mode.SRC_IN);
                        }
                    }
                    cpv.setColor(color3);
                }
                int red = Color.red(ColorPickerDialog.this.color);
                int green = Color.green(ColorPickerDialog.this.color);
                int blue = Color.blue(ColorPickerDialog.this.color);
                ColorPickerDialog.this.color = Color.argb(alpha, red, green, blue);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private int[] unshiftIfNotExists(int[] array, int value) {
        boolean present = false;
        int length = array.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            } else if (array[i] == value) {
                present = true;
                break;
            } else {
                i++;
            }
        }
        if (present) {
            return array;
        }
        int[] newArray = new int[(array.length + 1)];
        newArray[0] = value;
        System.arraycopy(array, 0, newArray, 1, newArray.length - 1);
        return newArray;
    }

    private int[] pushIfNotExists(int[] array, int value) {
        boolean present = false;
        int length = array.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            } else if (array[i] == value) {
                present = true;
                break;
            } else {
                i++;
            }
        }
        if (present) {
            return array;
        }
        int[] newArray = new int[(array.length + 1)];
        newArray[newArray.length - 1] = value;
        System.arraycopy(array, 0, newArray, 0, newArray.length - 1);
        return newArray;
    }

    private int getSelectedItemPosition() {
        int i = 0;
        while (true) {
            int[] iArr = this.presets;
            if (i >= iArr.length) {
                return -1;
            }
            if (iArr[i] == this.color) {
                return i;
            }
            i++;
        }
    }

    public static final class Builder {
        boolean allowCustom = true;
        boolean allowPresets = true;
        int color = ViewCompat.MEASURED_STATE_MASK;
        int colorShape = 1;
        int customButtonText = C0763R.string.cpv_custom;
        int dialogId = 0;
        int dialogTitle = C0763R.string.cpv_default_title;
        int dialogType = 1;
        int[] presets = ColorPickerDialog.MATERIAL_COLORS;
        int presetsButtonText = C0763R.string.cpv_presets;
        int selectedButtonText = C0763R.string.cpv_select;
        boolean showAlphaSlider = false;
        boolean showColorShades = true;

        Builder() {
        }

        public Builder setDialogTitle(int dialogTitle2) {
            this.dialogTitle = dialogTitle2;
            return this;
        }

        public Builder setSelectedButtonText(int selectedButtonText2) {
            this.selectedButtonText = selectedButtonText2;
            return this;
        }

        public Builder setPresetsButtonText(int presetsButtonText2) {
            this.presetsButtonText = presetsButtonText2;
            return this;
        }

        public Builder setCustomButtonText(int customButtonText2) {
            this.customButtonText = customButtonText2;
            return this;
        }

        public Builder setDialogType(int dialogType2) {
            this.dialogType = dialogType2;
            return this;
        }

        public Builder setPresets(int[] presets2) {
            this.presets = presets2;
            return this;
        }

        public Builder setColor(int color2) {
            this.color = color2;
            return this;
        }

        public Builder setDialogId(int dialogId2) {
            this.dialogId = dialogId2;
            return this;
        }

        public Builder setShowAlphaSlider(boolean showAlphaSlider2) {
            this.showAlphaSlider = showAlphaSlider2;
            return this;
        }

        public Builder setAllowPresets(boolean allowPresets2) {
            this.allowPresets = allowPresets2;
            return this;
        }

        public Builder setAllowCustom(boolean allowCustom2) {
            this.allowCustom = allowCustom2;
            return this;
        }

        public Builder setShowColorShades(boolean showColorShades2) {
            this.showColorShades = showColorShades2;
            return this;
        }

        public Builder setColorShape(int colorShape2) {
            this.colorShape = colorShape2;
            return this;
        }

        public ColorPickerDialog create() {
            ColorPickerDialog dialog = new ColorPickerDialog();
            Bundle args = new Bundle();
            args.putInt("id", this.dialogId);
            args.putInt(ColorPickerDialog.ARG_TYPE, this.dialogType);
            args.putInt(ColorPickerDialog.ARG_COLOR, this.color);
            args.putIntArray(ColorPickerDialog.ARG_PRESETS, this.presets);
            args.putBoolean(ColorPickerDialog.ARG_ALPHA, this.showAlphaSlider);
            args.putBoolean(ColorPickerDialog.ARG_ALLOW_CUSTOM, this.allowCustom);
            args.putBoolean(ColorPickerDialog.ARG_ALLOW_PRESETS, this.allowPresets);
            args.putInt(ColorPickerDialog.ARG_DIALOG_TITLE, this.dialogTitle);
            args.putBoolean(ColorPickerDialog.ARG_SHOW_COLOR_SHADES, this.showColorShades);
            args.putInt(ColorPickerDialog.ARG_COLOR_SHAPE, this.colorShape);
            args.putInt(ColorPickerDialog.ARG_PRESETS_BUTTON_TEXT, this.presetsButtonText);
            args.putInt(ColorPickerDialog.ARG_CUSTOM_BUTTON_TEXT, this.customButtonText);
            args.putInt(ColorPickerDialog.ARG_SELECTED_BUTTON_TEXT, this.selectedButtonText);
            dialog.setArguments(args);
            return dialog;
        }

        public void show(Activity activity) {
            create().show(activity.getFragmentManager(), "color-picker-dialog");
        }
    }
}
