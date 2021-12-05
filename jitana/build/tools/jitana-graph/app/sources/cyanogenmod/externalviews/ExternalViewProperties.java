package cyanogenmod.externalviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;

public class ExternalViewProperties {
    private final View mDecorView;
    private int mHeight;
    private Rect mHitRect = new Rect();
    private final int[] mScreenCoords = new int[2];
    private final View mView;
    private boolean mVisible;
    private int mWidth;

    ExternalViewProperties(View view, Context context) {
        this.mView = view;
        if (context instanceof Activity) {
            this.mDecorView = ((Activity) context).getWindow().getDecorView();
        } else {
            this.mDecorView = null;
        }
    }

    public Rect getHitRect() {
        return this.mHitRect;
    }

    public int getX() {
        return this.mScreenCoords[0];
    }

    public int getY() {
        return this.mScreenCoords[1];
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    public boolean hasChanged() {
        int previousWidth = this.mWidth;
        int previousHeight = this.mHeight;
        this.mWidth = this.mView.getWidth();
        this.mHeight = this.mView.getHeight();
        int[] iArr = this.mScreenCoords;
        int previousX = iArr[0];
        int previousY = iArr[1];
        this.mView.getLocationOnScreen(iArr);
        int[] iArr2 = this.mScreenCoords;
        int newX = iArr2[0];
        int newY = iArr2[1];
        this.mHitRect.setEmpty();
        View view = this.mDecorView;
        if (view != null) {
            view.getHitRect(this.mHitRect);
        }
        boolean visible = this.mView.getLocalVisibleRect(this.mHitRect);
        this.mVisible = visible;
        if (previousX == newX && previousY == newY && previousWidth == this.mWidth && previousHeight == this.mHeight && this.mVisible == visible) {
            return false;
        }
        return true;
    }
}
