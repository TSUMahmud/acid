package com.jaredrummler.android.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.view.ViewCompat;

public class ColorPickerView extends View {
    private static final int ALPHA_PANEL_HEIGH_DP = 20;
    private static final int BORDER_WIDTH_PX = 1;
    private static final int CIRCLE_TRACKER_RADIUS_DP = 5;
    private static final int DEFAULT_BORDER_COLOR = -9539986;
    private static final int DEFAULT_SLIDER_COLOR = -4342339;
    private static final int HUE_PANEL_WDITH_DP = 30;
    private static final int PANEL_SPACING_DP = 10;
    private static final int SLIDER_TRACKER_OFFSET_DP = 2;
    private static final int SLIDER_TRACKER_SIZE_DP = 4;
    private int alpha;
    private Paint alphaPaint;
    private int alphaPanelHeightPx;
    private AlphaPatternDrawable alphaPatternDrawable;
    private Rect alphaRect;
    private Shader alphaShader;
    private String alphaSliderText;
    private Paint alphaTextPaint;
    private int borderColor;
    private Paint borderPaint;
    private int circleTrackerRadiusPx;
    private Rect drawingRect;
    private float hue;
    private Paint hueAlphaTrackerPaint;
    private BitmapCache hueBackgroundCache;
    private int huePanelWidthPx;
    private Rect hueRect;
    private int mRequiredPadding;
    private OnColorChangedListener onColorChangedListener;
    private int panelSpacingPx;
    private float sat;
    private Shader satShader;
    private BitmapCache satValBackgroundCache;
    private Paint satValPaint;
    private Rect satValRect;
    private Paint satValTrackerPaint;
    private boolean showAlphaPanel;
    private int sliderTrackerColor;
    private int sliderTrackerOffsetPx;
    private int sliderTrackerSizePx;
    private Point startTouchPoint;
    private float val;
    private Shader valShader;

    public interface OnColorChangedListener {
        void onColorChanged(int i);
    }

    public ColorPickerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alpha = 255;
        this.hue = 360.0f;
        this.sat = 0.0f;
        this.val = 0.0f;
        this.showAlphaPanel = false;
        this.alphaSliderText = null;
        this.sliderTrackerColor = DEFAULT_SLIDER_COLOR;
        this.borderColor = DEFAULT_BORDER_COLOR;
        this.startTouchPoint = null;
        init(context, attrs);
    }

    public Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putParcelable("instanceState", super.onSaveInstanceState());
        state.putInt("alpha", this.alpha);
        state.putFloat("hue", this.hue);
        state.putFloat("sat", this.sat);
        state.putFloat("val", this.val);
        state.putBoolean("show_alpha", this.showAlphaPanel);
        state.putString("alpha_text", this.alphaSliderText);
        return state;
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.alpha = bundle.getInt("alpha");
            this.hue = bundle.getFloat("hue");
            this.sat = bundle.getFloat("sat");
            this.val = bundle.getFloat("val");
            this.showAlphaPanel = bundle.getBoolean("show_alpha");
            this.alphaSliderText = bundle.getString("alpha_text");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, C0763R.styleable.ColorPickerView);
        this.showAlphaPanel = a.getBoolean(C0763R.styleable.ColorPickerView_cpv_alphaChannelVisible, false);
        this.alphaSliderText = a.getString(C0763R.styleable.ColorPickerView_cpv_alphaChannelText);
        this.sliderTrackerColor = a.getColor(C0763R.styleable.ColorPickerView_cpv_sliderColor, DEFAULT_SLIDER_COLOR);
        this.borderColor = a.getColor(C0763R.styleable.ColorPickerView_cpv_borderColor, DEFAULT_BORDER_COLOR);
        a.recycle();
        applyThemeColors(context);
        this.huePanelWidthPx = DrawingUtils.dpToPx(getContext(), 30.0f);
        this.alphaPanelHeightPx = DrawingUtils.dpToPx(getContext(), 20.0f);
        this.panelSpacingPx = DrawingUtils.dpToPx(getContext(), 10.0f);
        this.circleTrackerRadiusPx = DrawingUtils.dpToPx(getContext(), 5.0f);
        this.sliderTrackerSizePx = DrawingUtils.dpToPx(getContext(), 4.0f);
        this.sliderTrackerOffsetPx = DrawingUtils.dpToPx(getContext(), 2.0f);
        this.mRequiredPadding = getResources().getDimensionPixelSize(C0763R.dimen.cpv_required_padding);
        initPaintTools();
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    private void applyThemeColors(Context c) {
        TypedArray a = c.obtainStyledAttributes(new TypedValue().data, new int[]{16842808});
        if (this.borderColor == DEFAULT_BORDER_COLOR) {
            this.borderColor = a.getColor(0, DEFAULT_BORDER_COLOR);
        }
        if (this.sliderTrackerColor == DEFAULT_SLIDER_COLOR) {
            this.sliderTrackerColor = a.getColor(0, DEFAULT_SLIDER_COLOR);
        }
        a.recycle();
    }

    private void initPaintTools() {
        this.satValPaint = new Paint();
        this.satValTrackerPaint = new Paint();
        this.hueAlphaTrackerPaint = new Paint();
        this.alphaPaint = new Paint();
        this.alphaTextPaint = new Paint();
        this.borderPaint = new Paint();
        this.satValTrackerPaint.setStyle(Paint.Style.STROKE);
        this.satValTrackerPaint.setStrokeWidth((float) DrawingUtils.dpToPx(getContext(), 2.0f));
        this.satValTrackerPaint.setAntiAlias(true);
        this.hueAlphaTrackerPaint.setColor(this.sliderTrackerColor);
        this.hueAlphaTrackerPaint.setStyle(Paint.Style.STROKE);
        this.hueAlphaTrackerPaint.setStrokeWidth((float) DrawingUtils.dpToPx(getContext(), 2.0f));
        this.hueAlphaTrackerPaint.setAntiAlias(true);
        this.alphaTextPaint.setColor(-14935012);
        this.alphaTextPaint.setTextSize((float) DrawingUtils.dpToPx(getContext(), 14.0f));
        this.alphaTextPaint.setAntiAlias(true);
        this.alphaTextPaint.setTextAlign(Paint.Align.CENTER);
        this.alphaTextPaint.setFakeBoldText(true);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.drawingRect.width() > 0 && this.drawingRect.height() > 0) {
            drawSatValPanel(canvas);
            drawHuePanel(canvas);
            drawAlphaPanel(canvas);
        }
    }

    private void drawSatValPanel(Canvas canvas) {
        Canvas canvas2 = canvas;
        Rect rect = this.satValRect;
        this.borderPaint.setColor(this.borderColor);
        canvas.drawRect((float) this.drawingRect.left, (float) this.drawingRect.top, (float) (rect.right + 1), (float) (rect.bottom + 1), this.borderPaint);
        if (this.valShader == null) {
            this.valShader = new LinearGradient((float) rect.left, (float) rect.top, (float) rect.left, (float) rect.bottom, -1, ViewCompat.MEASURED_STATE_MASK, Shader.TileMode.CLAMP);
        }
        BitmapCache bitmapCache = this.satValBackgroundCache;
        if (bitmapCache == null || bitmapCache.value != this.hue) {
            if (this.satValBackgroundCache == null) {
                this.satValBackgroundCache = new BitmapCache();
            }
            if (this.satValBackgroundCache.bitmap == null) {
                this.satValBackgroundCache.bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
            }
            if (this.satValBackgroundCache.canvas == null) {
                BitmapCache bitmapCache2 = this.satValBackgroundCache;
                bitmapCache2.canvas = new Canvas(bitmapCache2.bitmap);
            }
            this.satShader = new LinearGradient((float) rect.left, (float) rect.top, (float) rect.right, (float) rect.top, -1, Color.HSVToColor(new float[]{this.hue, 1.0f, 1.0f}), Shader.TileMode.CLAMP);
            this.satValPaint.setShader(new ComposeShader(this.valShader, this.satShader, PorterDuff.Mode.MULTIPLY));
            this.satValBackgroundCache.canvas.drawRect(0.0f, 0.0f, (float) this.satValBackgroundCache.bitmap.getWidth(), (float) this.satValBackgroundCache.bitmap.getHeight(), this.satValPaint);
            this.satValBackgroundCache.value = this.hue;
        }
        canvas2.drawBitmap(this.satValBackgroundCache.bitmap, (Rect) null, rect, (Paint) null);
        Point p = satValToPoint(this.sat, this.val);
        this.satValTrackerPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        canvas2.drawCircle((float) p.x, (float) p.y, (float) (this.circleTrackerRadiusPx - DrawingUtils.dpToPx(getContext(), 1.0f)), this.satValTrackerPaint);
        this.satValTrackerPaint.setColor(-2236963);
        canvas2.drawCircle((float) p.x, (float) p.y, (float) this.circleTrackerRadiusPx, this.satValTrackerPaint);
    }

    private void drawHuePanel(Canvas canvas) {
        Rect rect = this.hueRect;
        this.borderPaint.setColor(this.borderColor);
        canvas.drawRect((float) (rect.left - 1), (float) (rect.top - 1), (float) (rect.right + 1), (float) (rect.bottom + 1), this.borderPaint);
        if (this.hueBackgroundCache == null) {
            this.hueBackgroundCache = new BitmapCache();
            this.hueBackgroundCache.bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
            BitmapCache bitmapCache = this.hueBackgroundCache;
            bitmapCache.canvas = new Canvas(bitmapCache.bitmap);
            int[] hueColors = new int[((int) (((float) rect.height()) + 0.5f))];
            float h = 360.0f;
            for (int i = 0; i < hueColors.length; i++) {
                hueColors[i] = Color.HSVToColor(new float[]{h, 1.0f, 1.0f});
                h -= 360.0f / ((float) hueColors.length);
            }
            Paint linePaint = new Paint();
            linePaint.setStrokeWidth(0.0f);
            for (int i2 = 0; i2 < hueColors.length; i2++) {
                linePaint.setColor(hueColors[i2]);
                this.hueBackgroundCache.canvas.drawLine(0.0f, (float) i2, (float) this.hueBackgroundCache.bitmap.getWidth(), (float) i2, linePaint);
            }
        }
        canvas.drawBitmap(this.hueBackgroundCache.bitmap, (Rect) null, rect, (Paint) null);
        Point p = hueToPoint(this.hue);
        RectF r = new RectF();
        r.left = (float) (rect.left - this.sliderTrackerOffsetPx);
        r.right = (float) (rect.right + this.sliderTrackerOffsetPx);
        r.top = (float) (p.y - (this.sliderTrackerSizePx / 2));
        r.bottom = (float) (p.y + (this.sliderTrackerSizePx / 2));
        canvas.drawRoundRect(r, 2.0f, 2.0f, this.hueAlphaTrackerPaint);
    }

    private void drawAlphaPanel(Canvas canvas) {
        if (this.showAlphaPanel && this.alphaRect != null && this.alphaPatternDrawable != null) {
            Rect rect = this.alphaRect;
            this.borderPaint.setColor(this.borderColor);
            canvas.drawRect((float) (rect.left - 1), (float) (rect.top - 1), (float) (rect.right + 1), (float) (rect.bottom + 1), this.borderPaint);
            this.alphaPatternDrawable.draw(canvas);
            float[] hsv = {this.hue, this.sat, this.val};
            int color = Color.HSVToColor(hsv);
            this.alphaShader = new LinearGradient((float) rect.left, (float) rect.top, (float) rect.right, (float) rect.top, color, Color.HSVToColor(0, hsv), Shader.TileMode.CLAMP);
            this.alphaPaint.setShader(this.alphaShader);
            canvas.drawRect(rect, this.alphaPaint);
            String str = this.alphaSliderText;
            if (str != null && !str.equals("")) {
                canvas.drawText(this.alphaSliderText, (float) rect.centerX(), (float) (rect.centerY() + DrawingUtils.dpToPx(getContext(), 4.0f)), this.alphaTextPaint);
            }
            Point p = alphaToPoint(this.alpha);
            RectF r = new RectF();
            r.left = (float) (p.x - (this.sliderTrackerSizePx / 2));
            r.right = (float) (p.x + (this.sliderTrackerSizePx / 2));
            r.top = (float) (rect.top - this.sliderTrackerOffsetPx);
            r.bottom = (float) (rect.bottom + this.sliderTrackerOffsetPx);
            canvas.drawRoundRect(r, 2.0f, 2.0f, this.hueAlphaTrackerPaint);
        }
    }

    private Point hueToPoint(float hue2) {
        Rect rect = this.hueRect;
        float height = (float) rect.height();
        Point p = new Point();
        p.y = (int) ((height - ((hue2 * height) / 360.0f)) + ((float) rect.top));
        p.x = rect.left;
        return p;
    }

    private Point satValToPoint(float sat2, float val2) {
        Rect rect = this.satValRect;
        Point p = new Point();
        p.x = (int) ((sat2 * ((float) rect.width())) + ((float) rect.left));
        p.y = (int) (((1.0f - val2) * ((float) rect.height())) + ((float) rect.top));
        return p;
    }

    private Point alphaToPoint(int alpha2) {
        Rect rect = this.alphaRect;
        float width = (float) rect.width();
        Point p = new Point();
        p.x = (int) ((width - ((((float) alpha2) * width) / 255.0f)) + ((float) rect.left));
        p.y = rect.top;
        return p;
    }

    private float[] pointToSatVal(float x, float y) {
        float x2;
        float y2;
        Rect rect = this.satValRect;
        float[] result = new float[2];
        float width = (float) rect.width();
        float height = (float) rect.height();
        if (x < ((float) rect.left)) {
            x2 = 0.0f;
        } else if (x > ((float) rect.right)) {
            x2 = width;
        } else {
            x2 = x - ((float) rect.left);
        }
        if (y < ((float) rect.top)) {
            y2 = 0.0f;
        } else if (y > ((float) rect.bottom)) {
            y2 = height;
        } else {
            y2 = y - ((float) rect.top);
        }
        result[0] = (1.0f / width) * x2;
        result[1] = 1.0f - ((1.0f / height) * y2);
        return result;
    }

    private float pointToHue(float y) {
        float y2;
        Rect rect = this.hueRect;
        float height = (float) rect.height();
        if (y < ((float) rect.top)) {
            y2 = 0.0f;
        } else if (y > ((float) rect.bottom)) {
            y2 = height;
        } else {
            y2 = y - ((float) rect.top);
        }
        return 360.0f - ((y2 * 360.0f) / height);
    }

    private int pointToAlpha(int x) {
        int x2;
        Rect rect = this.alphaRect;
        int width = rect.width();
        if (x < rect.left) {
            x2 = 0;
        } else if (x > rect.right) {
            x2 = width;
        } else {
            x2 = x - rect.left;
        }
        return 255 - ((x2 * 255) / width);
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean update = false;
        int action = event.getAction();
        if (action == 0) {
            this.startTouchPoint = new Point((int) event.getX(), (int) event.getY());
            update = moveTrackersIfNeeded(event);
        } else if (action == 1) {
            this.startTouchPoint = null;
            update = moveTrackersIfNeeded(event);
        } else if (action == 2) {
            update = moveTrackersIfNeeded(event);
        }
        if (!update) {
            return super.onTouchEvent(event);
        }
        OnColorChangedListener onColorChangedListener2 = this.onColorChangedListener;
        if (onColorChangedListener2 != null) {
            onColorChangedListener2.onColorChanged(Color.HSVToColor(this.alpha, new float[]{this.hue, this.sat, this.val}));
        }
        invalidate();
        return true;
    }

    private boolean moveTrackersIfNeeded(MotionEvent event) {
        Point point = this.startTouchPoint;
        if (point == null) {
            return false;
        }
        int startX = point.x;
        int startY = this.startTouchPoint.y;
        if (this.hueRect.contains(startX, startY)) {
            this.hue = pointToHue(event.getY());
            return true;
        } else if (this.satValRect.contains(startX, startY)) {
            float[] result = pointToSatVal(event.getX(), event.getY());
            this.sat = result[0];
            this.val = result[1];
            return true;
        } else {
            Rect rect = this.alphaRect;
            if (rect == null || !rect.contains(startX, startY)) {
                return false;
            }
            this.alpha = pointToAlpha((int) event.getX());
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int finalHeight;
        int finalWidth;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthAllowed = (View.MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()) - getPaddingRight();
        int heightAllowed = (View.MeasureSpec.getSize(heightMeasureSpec) - getPaddingBottom()) - getPaddingTop();
        if (widthMode != 1073741824 && heightMode != 1073741824) {
            int i = this.panelSpacingPx;
            int i2 = this.huePanelWidthPx;
            int widthNeeded = heightAllowed + i + i2;
            int heightNeeded = (widthAllowed - i) - i2;
            if (this.showAlphaPanel) {
                int i3 = this.alphaPanelHeightPx;
                widthNeeded -= i + i3;
                heightNeeded += i + i3;
            }
            boolean widthOk = false;
            boolean heightOk = false;
            if (widthNeeded <= widthAllowed) {
                widthOk = true;
            }
            if (heightNeeded <= heightAllowed) {
                heightOk = true;
            }
            if (widthOk && heightOk) {
                finalWidth = widthAllowed;
                finalHeight = heightNeeded;
            } else if (!heightOk && widthOk) {
                finalHeight = heightAllowed;
                finalWidth = widthNeeded;
            } else if (widthOk || !heightOk) {
                finalHeight = heightAllowed;
                finalWidth = widthAllowed;
            } else {
                finalHeight = heightNeeded;
                finalWidth = widthAllowed;
            }
        } else if (widthMode == 1073741824 && heightMode != 1073741824) {
            int i4 = this.panelSpacingPx;
            int h = (widthAllowed - i4) - this.huePanelWidthPx;
            if (this.showAlphaPanel) {
                h += i4 + this.alphaPanelHeightPx;
            }
            if (h > heightAllowed) {
                finalHeight = heightAllowed;
            } else {
                finalHeight = h;
            }
            finalWidth = widthAllowed;
        } else if (heightMode != 1073741824 || widthMode == 1073741824) {
            finalWidth = widthAllowed;
            finalHeight = heightAllowed;
        } else {
            int i5 = this.panelSpacingPx;
            int w = heightAllowed + i5 + this.huePanelWidthPx;
            if (this.showAlphaPanel) {
                w -= i5 + this.alphaPanelHeightPx;
            }
            if (w > widthAllowed) {
                finalWidth = widthAllowed;
            } else {
                finalWidth = w;
            }
            finalHeight = heightAllowed;
        }
        setMeasuredDimension(getPaddingLeft() + finalWidth + getPaddingRight(), getPaddingTop() + finalHeight + getPaddingBottom());
    }

    private int getPreferredWidth() {
        return this.huePanelWidthPx + DrawingUtils.dpToPx(getContext(), 200.0f) + this.panelSpacingPx;
    }

    private int getPreferredHeight() {
        int height = DrawingUtils.dpToPx(getContext(), 200.0f);
        if (this.showAlphaPanel) {
            return height + this.panelSpacingPx + this.alphaPanelHeightPx;
        }
        return height;
    }

    public int getPaddingTop() {
        return Math.max(super.getPaddingTop(), this.mRequiredPadding);
    }

    public int getPaddingBottom() {
        return Math.max(super.getPaddingBottom(), this.mRequiredPadding);
    }

    public int getPaddingLeft() {
        return Math.max(super.getPaddingLeft(), this.mRequiredPadding);
    }

    public int getPaddingRight() {
        return Math.max(super.getPaddingRight(), this.mRequiredPadding);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.drawingRect = new Rect();
        this.drawingRect.left = getPaddingLeft();
        this.drawingRect.right = w - getPaddingRight();
        this.drawingRect.top = getPaddingTop();
        this.drawingRect.bottom = h - getPaddingBottom();
        this.valShader = null;
        this.satShader = null;
        this.alphaShader = null;
        this.satValBackgroundCache = null;
        this.hueBackgroundCache = null;
        setUpSatValRect();
        setUpHueRect();
        setUpAlphaRect();
    }

    private void setUpSatValRect() {
        Rect dRect = this.drawingRect;
        int left = dRect.left + 1;
        int top = dRect.top + 1;
        int bottom = dRect.bottom - 1;
        int i = this.panelSpacingPx;
        int right = ((dRect.right - 1) - i) - this.huePanelWidthPx;
        if (this.showAlphaPanel) {
            bottom -= this.alphaPanelHeightPx + i;
        }
        this.satValRect = new Rect(left, top, right, bottom);
    }

    private void setUpHueRect() {
        Rect dRect = this.drawingRect;
        this.hueRect = new Rect((dRect.right - this.huePanelWidthPx) + 1, dRect.top + 1, dRect.right - 1, (dRect.bottom - 1) - (this.showAlphaPanel ? this.panelSpacingPx + this.alphaPanelHeightPx : 0));
    }

    private void setUpAlphaRect() {
        if (this.showAlphaPanel) {
            Rect dRect = this.drawingRect;
            this.alphaRect = new Rect(dRect.left + 1, (dRect.bottom - this.alphaPanelHeightPx) + 1, dRect.right - 1, dRect.bottom - 1);
            this.alphaPatternDrawable = new AlphaPatternDrawable(DrawingUtils.dpToPx(getContext(), 4.0f));
            this.alphaPatternDrawable.setBounds(Math.round((float) this.alphaRect.left), Math.round((float) this.alphaRect.top), Math.round((float) this.alphaRect.right), Math.round((float) this.alphaRect.bottom));
        }
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.onColorChangedListener = listener;
    }

    public int getColor() {
        return Color.HSVToColor(this.alpha, new float[]{this.hue, this.sat, this.val});
    }

    public void setColor(int color) {
        setColor(color, false);
    }

    public void setColor(int color, boolean callback) {
        OnColorChangedListener onColorChangedListener2;
        int alpha2 = Color.alpha(color);
        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);
        this.alpha = alpha2;
        this.hue = hsv[0];
        this.sat = hsv[1];
        this.val = hsv[2];
        if (callback && (onColorChangedListener2 = this.onColorChangedListener) != null) {
            onColorChangedListener2.onColorChanged(Color.HSVToColor(this.alpha, new float[]{this.hue, this.sat, this.val}));
        }
        invalidate();
    }

    public void setAlphaSliderVisible(boolean visible) {
        if (this.showAlphaPanel != visible) {
            this.showAlphaPanel = visible;
            this.valShader = null;
            this.satShader = null;
            this.alphaShader = null;
            this.hueBackgroundCache = null;
            this.satValBackgroundCache = null;
            requestLayout();
        }
    }

    public void setSliderTrackerColor(int color) {
        this.sliderTrackerColor = color;
        this.hueAlphaTrackerPaint.setColor(this.sliderTrackerColor);
        invalidate();
    }

    public int getSliderTrackerColor() {
        return this.sliderTrackerColor;
    }

    public void setBorderColor(int color) {
        this.borderColor = color;
        invalidate();
    }

    public int getBorderColor() {
        return this.borderColor;
    }

    public void setAlphaSliderText(int res) {
        setAlphaSliderText(getContext().getString(res));
    }

    public void setAlphaSliderText(String text) {
        this.alphaSliderText = text;
        invalidate();
    }

    public String getAlphaSliderText() {
        return this.alphaSliderText;
    }

    private class BitmapCache {
        public Bitmap bitmap;
        public Canvas canvas;
        public float value;

        private BitmapCache() {
        }
    }
}
