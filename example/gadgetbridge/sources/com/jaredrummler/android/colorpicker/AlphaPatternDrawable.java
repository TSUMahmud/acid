package com.jaredrummler.android.colorpicker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

class AlphaPatternDrawable extends Drawable {
    private Bitmap bitmap;
    private int numRectanglesHorizontal;
    private int numRectanglesVertical;
    private Paint paint = new Paint();
    private Paint paintGray = new Paint();
    private Paint paintWhite = new Paint();
    private int rectangleSize = 10;

    AlphaPatternDrawable(int rectangleSize2) {
        this.rectangleSize = rectangleSize2;
        this.paintWhite.setColor(-1);
        this.paintGray.setColor(-3421237);
    }

    public void draw(Canvas canvas) {
        Bitmap bitmap2 = this.bitmap;
        if (bitmap2 != null && !bitmap2.isRecycled()) {
            canvas.drawBitmap(this.bitmap, (Rect) null, getBounds(), this.paint);
        }
    }

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int alpha) {
        throw new UnsupportedOperationException("Alpha is not supported by this drawable.");
    }

    public void setColorFilter(ColorFilter cf) {
        throw new UnsupportedOperationException("ColorFilter is not supported by this drawable.");
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        int height = bounds.height();
        this.numRectanglesHorizontal = (int) Math.ceil((double) (bounds.width() / this.rectangleSize));
        this.numRectanglesVertical = (int) Math.ceil((double) (height / this.rectangleSize));
        generatePatternBitmap();
    }

    private void generatePatternBitmap() {
        boolean z;
        if (getBounds().width() > 0 && getBounds().height() > 0) {
            this.bitmap = Bitmap.createBitmap(getBounds().width(), getBounds().height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(this.bitmap);
            Rect r = new Rect();
            boolean verticalStartWhite = true;
            for (int i = 0; i <= this.numRectanglesVertical; i++) {
                boolean isWhite = verticalStartWhite;
                int j = 0;
                while (true) {
                    z = false;
                    if (j > this.numRectanglesHorizontal) {
                        break;
                    }
                    int i2 = this.rectangleSize;
                    r.top = i * i2;
                    r.left = i2 * j;
                    r.bottom = r.top + this.rectangleSize;
                    r.right = r.left + this.rectangleSize;
                    canvas.drawRect(r, isWhite ? this.paintWhite : this.paintGray);
                    if (!isWhite) {
                        z = true;
                    }
                    isWhite = z;
                    j++;
                }
                if (!verticalStartWhite) {
                    z = true;
                }
                verticalStartWhite = z;
            }
        }
    }
}
