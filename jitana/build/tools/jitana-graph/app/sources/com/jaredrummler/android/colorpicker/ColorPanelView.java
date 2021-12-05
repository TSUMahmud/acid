package com.jaredrummler.android.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import java.util.Locale;

public class ColorPanelView extends View {
    private static final int DEFAULT_BORDER_COLOR = -9539986;
    private Paint alphaPaint;
    private Drawable alphaPattern;
    private int borderColor;
    private Paint borderPaint;
    private int borderWidthPx;
    private RectF centerRect;
    private int color;
    private Paint colorPaint;
    private Rect colorRect;
    private Rect drawingRect;
    private Paint originalPaint;
    private int shape;
    private boolean showOldColor;

    public ColorPanelView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ColorPanelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPanelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.centerRect = new RectF();
        this.borderColor = DEFAULT_BORDER_COLOR;
        this.color = ViewCompat.MEASURED_STATE_MASK;
        init(context, attrs);
    }

    public Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putParcelable("instanceState", super.onSaveInstanceState());
        state.putInt("color", this.color);
        return state;
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.color = bundle.getInt("color");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, C0763R.styleable.ColorPanelView);
        this.shape = a.getInt(C0763R.styleable.ColorPanelView_cpv_colorShape, 1);
        this.showOldColor = a.getBoolean(C0763R.styleable.ColorPanelView_cpv_showOldColor, false);
        if (!this.showOldColor || this.shape == 1) {
            this.borderColor = a.getColor(C0763R.styleable.ColorPanelView_cpv_borderColor, DEFAULT_BORDER_COLOR);
            a.recycle();
            if (this.borderColor == DEFAULT_BORDER_COLOR) {
                TypedArray typedArray = context.obtainStyledAttributes(new TypedValue().data, new int[]{16842808});
                this.borderColor = typedArray.getColor(0, this.borderColor);
                typedArray.recycle();
            }
            this.borderWidthPx = DrawingUtils.dpToPx(context, 1.0f);
            this.borderPaint = new Paint();
            this.borderPaint.setAntiAlias(true);
            this.colorPaint = new Paint();
            this.colorPaint.setAntiAlias(true);
            if (this.showOldColor) {
                this.originalPaint = new Paint();
            }
            if (this.shape == 1) {
                Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(C0763R.C0764drawable.cpv_alpha)).getBitmap();
                this.alphaPaint = new Paint();
                this.alphaPaint.setAntiAlias(true);
                this.alphaPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
                return;
            }
            return;
        }
        throw new IllegalStateException("Color preview is only available in circle mode");
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.borderPaint.setColor(this.borderColor);
        this.colorPaint.setColor(this.color);
        int i = this.shape;
        if (i == 0) {
            if (this.borderWidthPx > 0) {
                canvas.drawRect(this.drawingRect, this.borderPaint);
            }
            Drawable drawable = this.alphaPattern;
            if (drawable != null) {
                drawable.draw(canvas);
            }
            canvas.drawRect(this.colorRect, this.colorPaint);
        } else if (i == 1) {
            int outerRadius = getMeasuredWidth() / 2;
            if (this.borderWidthPx > 0) {
                canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) outerRadius, this.borderPaint);
            }
            if (Color.alpha(this.color) < 255) {
                canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) (outerRadius - this.borderWidthPx), this.alphaPaint);
            }
            if (this.showOldColor) {
                canvas.drawArc(this.centerRect, 90.0f, 180.0f, true, this.originalPaint);
                canvas.drawArc(this.centerRect, 270.0f, 180.0f, true, this.colorPaint);
                return;
            }
            canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) (outerRadius - this.borderWidthPx), this.colorPaint);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i = this.shape;
        if (i == 0) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
        } else if (i == 1) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.shape == 0 || this.showOldColor) {
            this.drawingRect = new Rect();
            this.drawingRect.left = getPaddingLeft();
            this.drawingRect.right = w - getPaddingRight();
            this.drawingRect.top = getPaddingTop();
            this.drawingRect.bottom = h - getPaddingBottom();
            if (this.showOldColor) {
                setUpCenterRect();
            } else {
                setUpColorRect();
            }
        }
    }

    private void setUpCenterRect() {
        Rect dRect = this.drawingRect;
        this.centerRect = new RectF((float) (dRect.left + this.borderWidthPx), (float) (dRect.top + this.borderWidthPx), (float) (dRect.right - this.borderWidthPx), (float) (dRect.bottom - this.borderWidthPx));
    }

    private void setUpColorRect() {
        Rect dRect = this.drawingRect;
        this.colorRect = new Rect(dRect.left + this.borderWidthPx, dRect.top + this.borderWidthPx, dRect.right - this.borderWidthPx, dRect.bottom - this.borderWidthPx);
        this.alphaPattern = new AlphaPatternDrawable(DrawingUtils.dpToPx(getContext(), 4.0f));
        this.alphaPattern.setBounds(Math.round((float) this.colorRect.left), Math.round((float) this.colorRect.top), Math.round((float) this.colorRect.right), Math.round((float) this.colorRect.bottom));
    }

    public void setColor(int color2) {
        this.color = color2;
        invalidate();
    }

    public int getColor() {
        return this.color;
    }

    public void setOriginalColor(int color2) {
        Paint paint = this.originalPaint;
        if (paint != null) {
            paint.setColor(color2);
        }
    }

    public void setBorderColor(int color2) {
        this.borderColor = color2;
        invalidate();
    }

    public int getBorderColor() {
        return this.borderColor;
    }

    public void setShape(int shape2) {
        this.shape = shape2;
        invalidate();
    }

    public int getShape() {
        return this.shape;
    }

    public void showHint() {
        int[] screenPos = new int[2];
        Rect displayFrame = new Rect();
        getLocationOnScreen(screenPos);
        getWindowVisibleDisplayFrame(displayFrame);
        Context context = getContext();
        int width = getWidth();
        int height = getHeight();
        int midy = screenPos[1] + (height / 2);
        int referenceX = screenPos[0] + (width / 2);
        if (ViewCompat.getLayoutDirection(this) == 0) {
            referenceX = context.getResources().getDisplayMetrics().widthPixels - referenceX;
        }
        StringBuilder hint = new StringBuilder("#");
        if (Color.alpha(this.color) != 255) {
            hint.append(Integer.toHexString(this.color).toUpperCase(Locale.ENGLISH));
        } else {
            hint.append(String.format("%06X", new Object[]{Integer.valueOf(16777215 & this.color)}).toUpperCase(Locale.ENGLISH));
        }
        Toast cheatSheet = Toast.makeText(context, hint.toString(), 0);
        if (midy < displayFrame.height()) {
            cheatSheet.setGravity(8388661, referenceX, (screenPos[1] + height) - displayFrame.top);
        } else {
            cheatSheet.setGravity(81, 0, height);
        }
        cheatSheet.show();
    }
}
