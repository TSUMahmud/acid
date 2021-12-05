package com.github.mikephil.charting.listener;

import android.graphics.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AnimationUtils;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class BarLineChartTouchListener extends ChartTouchListener<BarLineChartBase<? extends BarLineScatterCandleBubbleData<? extends IBarLineScatterCandleBubbleDataSet<? extends Entry>>>> {
    private IDataSet mClosestDataSetToTouch;
    private MPPointF mDecelerationCurrentPoint = MPPointF.getInstance(0.0f, 0.0f);
    private long mDecelerationLastTime = 0;
    private MPPointF mDecelerationVelocity = MPPointF.getInstance(0.0f, 0.0f);
    private float mDragTriggerDist;
    private Matrix mMatrix = new Matrix();
    private float mMinScalePointerDistance;
    private float mSavedDist = 1.0f;
    private Matrix mSavedMatrix = new Matrix();
    private float mSavedXDist = 1.0f;
    private float mSavedYDist = 1.0f;
    private MPPointF mTouchPointCenter = MPPointF.getInstance(0.0f, 0.0f);
    private MPPointF mTouchStartPoint = MPPointF.getInstance(0.0f, 0.0f);
    private VelocityTracker mVelocityTracker;

    public BarLineChartTouchListener(BarLineChartBase<? extends BarLineScatterCandleBubbleData<? extends IBarLineScatterCandleBubbleDataSet<? extends Entry>>> chart, Matrix touchMatrix, float dragTriggerDistance) {
        super(chart);
        this.mMatrix = touchMatrix;
        this.mDragTriggerDist = Utils.convertDpToPixel(dragTriggerDistance);
        this.mMinScalePointerDistance = Utils.convertDpToPixel(3.5f);
    }

    public boolean onTouch(View v, MotionEvent event) {
        VelocityTracker velocityTracker;
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
        int i = 3;
        if (event.getActionMasked() == 3 && (velocityTracker = this.mVelocityTracker) != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
        if (this.mTouchMode == 0) {
            this.mGestureDetector.onTouchEvent(event);
        }
        if (!((BarLineChartBase) this.mChart).isDragEnabled() && !((BarLineChartBase) this.mChart).isScaleXEnabled() && !((BarLineChartBase) this.mChart).isScaleYEnabled()) {
            return true;
        }
        int action = event.getAction() & 255;
        if (action != 0) {
            boolean shouldPan = false;
            if (action == 1) {
                VelocityTracker velocityTracker2 = this.mVelocityTracker;
                int pointerId = event.getPointerId(0);
                velocityTracker2.computeCurrentVelocity(1000, (float) Utils.getMaximumFlingVelocity());
                float velocityY = velocityTracker2.getYVelocity(pointerId);
                float velocityX = velocityTracker2.getXVelocity(pointerId);
                if ((Math.abs(velocityX) > ((float) Utils.getMinimumFlingVelocity()) || Math.abs(velocityY) > ((float) Utils.getMinimumFlingVelocity())) && this.mTouchMode == 1 && ((BarLineChartBase) this.mChart).isDragDecelerationEnabled()) {
                    stopDeceleration();
                    this.mDecelerationLastTime = AnimationUtils.currentAnimationTimeMillis();
                    this.mDecelerationCurrentPoint.f90x = event.getX();
                    this.mDecelerationCurrentPoint.f91y = event.getY();
                    MPPointF mPPointF = this.mDecelerationVelocity;
                    mPPointF.f90x = velocityX;
                    mPPointF.f91y = velocityY;
                    Utils.postInvalidateOnAnimation(this.mChart);
                }
                if (this.mTouchMode == 2 || this.mTouchMode == 3 || this.mTouchMode == 4 || this.mTouchMode == 5) {
                    ((BarLineChartBase) this.mChart).calculateOffsets();
                    ((BarLineChartBase) this.mChart).postInvalidate();
                }
                this.mTouchMode = 0;
                ((BarLineChartBase) this.mChart).enableScroll();
                VelocityTracker velocityTracker3 = this.mVelocityTracker;
                if (velocityTracker3 != null) {
                    velocityTracker3.recycle();
                    this.mVelocityTracker = null;
                }
                endAction(event);
            } else if (action != 2) {
                if (action == 3) {
                    this.mTouchMode = 0;
                    endAction(event);
                } else if (action != 5) {
                    if (action == 6) {
                        Utils.velocityTrackerPointerUpCleanUpIfNecessary(event, this.mVelocityTracker);
                        this.mTouchMode = 5;
                    }
                } else if (event.getPointerCount() >= 2) {
                    ((BarLineChartBase) this.mChart).disableScroll();
                    saveTouchStart(event);
                    this.mSavedXDist = getXDist(event);
                    this.mSavedYDist = getYDist(event);
                    this.mSavedDist = spacing(event);
                    if (this.mSavedDist > 10.0f) {
                        if (((BarLineChartBase) this.mChart).isPinchZoomEnabled()) {
                            this.mTouchMode = 4;
                        } else if (((BarLineChartBase) this.mChart).isScaleXEnabled() != ((BarLineChartBase) this.mChart).isScaleYEnabled()) {
                            if (((BarLineChartBase) this.mChart).isScaleXEnabled()) {
                                i = 2;
                            }
                            this.mTouchMode = i;
                        } else {
                            if (this.mSavedXDist > this.mSavedYDist) {
                                i = 2;
                            }
                            this.mTouchMode = i;
                        }
                    }
                    midPoint(this.mTouchPointCenter, event);
                }
            } else if (this.mTouchMode == 1) {
                ((BarLineChartBase) this.mChart).disableScroll();
                float y = 0.0f;
                float x = ((BarLineChartBase) this.mChart).isDragXEnabled() ? event.getX() - this.mTouchStartPoint.f90x : 0.0f;
                if (((BarLineChartBase) this.mChart).isDragYEnabled()) {
                    y = event.getY() - this.mTouchStartPoint.f91y;
                }
                performDrag(event, x, y);
            } else if (this.mTouchMode == 2 || this.mTouchMode == 3 || this.mTouchMode == 4) {
                ((BarLineChartBase) this.mChart).disableScroll();
                if (((BarLineChartBase) this.mChart).isScaleXEnabled() || ((BarLineChartBase) this.mChart).isScaleYEnabled()) {
                    performZoom(event);
                }
            } else if (this.mTouchMode == 0 && Math.abs(distance(event.getX(), this.mTouchStartPoint.f90x, event.getY(), this.mTouchStartPoint.f91y)) > this.mDragTriggerDist && ((BarLineChartBase) this.mChart).isDragEnabled()) {
                if (!((BarLineChartBase) this.mChart).isFullyZoomedOut() || !((BarLineChartBase) this.mChart).hasNoDragOffset()) {
                    shouldPan = true;
                }
                if (shouldPan) {
                    float distanceX = Math.abs(event.getX() - this.mTouchStartPoint.f90x);
                    float distanceY = Math.abs(event.getY() - this.mTouchStartPoint.f91y);
                    if ((((BarLineChartBase) this.mChart).isDragXEnabled() || distanceY >= distanceX) && (((BarLineChartBase) this.mChart).isDragYEnabled() || distanceY <= distanceX)) {
                        this.mLastGesture = ChartTouchListener.ChartGesture.DRAG;
                        this.mTouchMode = 1;
                    }
                } else if (((BarLineChartBase) this.mChart).isHighlightPerDragEnabled()) {
                    this.mLastGesture = ChartTouchListener.ChartGesture.DRAG;
                    if (((BarLineChartBase) this.mChart).isHighlightPerDragEnabled()) {
                        performHighlightDrag(event);
                    }
                }
            }
        } else {
            startAction(event);
            stopDeceleration();
            saveTouchStart(event);
        }
        this.mMatrix = ((BarLineChartBase) this.mChart).getViewPortHandler().refresh(this.mMatrix, this.mChart, true);
        return true;
    }

    private void saveTouchStart(MotionEvent event) {
        this.mSavedMatrix.set(this.mMatrix);
        this.mTouchStartPoint.f90x = event.getX();
        this.mTouchStartPoint.f91y = event.getY();
        this.mClosestDataSetToTouch = ((BarLineChartBase) this.mChart).getDataSetByTouchPoint(event.getX(), event.getY());
    }

    private void performDrag(MotionEvent event, float distanceX, float distanceY) {
        this.mLastGesture = ChartTouchListener.ChartGesture.DRAG;
        this.mMatrix.set(this.mSavedMatrix);
        OnChartGestureListener l = ((BarLineChartBase) this.mChart).getOnChartGestureListener();
        if (inverted()) {
            if (this.mChart instanceof HorizontalBarChart) {
                distanceX = -distanceX;
            } else {
                distanceY = -distanceY;
            }
        }
        this.mMatrix.postTranslate(distanceX, distanceY);
        if (l != null) {
            l.onChartTranslate(event, distanceX, distanceY);
        }
    }

    private void performZoom(MotionEvent event) {
        boolean canZoomMoreY;
        boolean canZoomMoreX;
        boolean canZoomMoreX2;
        boolean canZoomMoreY2;
        if (event.getPointerCount() >= 2) {
            OnChartGestureListener l = ((BarLineChartBase) this.mChart).getOnChartGestureListener();
            float totalDist = spacing(event);
            if (totalDist > this.mMinScalePointerDistance) {
                MPPointF t = getTrans(this.mTouchPointCenter.f90x, this.mTouchPointCenter.f91y);
                ViewPortHandler h = ((BarLineChartBase) this.mChart).getViewPortHandler();
                boolean isZoomingOut = true;
                float scaleY = 1.0f;
                if (this.mTouchMode == 4) {
                    this.mLastGesture = ChartTouchListener.ChartGesture.PINCH_ZOOM;
                    float scale = totalDist / this.mSavedDist;
                    if (scale >= 1.0f) {
                        isZoomingOut = false;
                    }
                    boolean isZoomingOut2 = isZoomingOut;
                    if (isZoomingOut2) {
                        canZoomMoreX2 = h.canZoomOutMoreX();
                    } else {
                        canZoomMoreX2 = h.canZoomInMoreX();
                    }
                    if (isZoomingOut2) {
                        canZoomMoreY2 = h.canZoomOutMoreY();
                    } else {
                        canZoomMoreY2 = h.canZoomInMoreY();
                    }
                    float scaleX = ((BarLineChartBase) this.mChart).isScaleXEnabled() ? scale : 1.0f;
                    if (((BarLineChartBase) this.mChart).isScaleYEnabled()) {
                        scaleY = scale;
                    }
                    if (canZoomMoreY2 || canZoomMoreX2) {
                        this.mMatrix.set(this.mSavedMatrix);
                        this.mMatrix.postScale(scaleX, scaleY, t.f90x, t.f91y);
                        if (l != null) {
                            l.onChartScale(event, scaleX, scaleY);
                        }
                    }
                } else if (this.mTouchMode == 2 && ((BarLineChartBase) this.mChart).isScaleXEnabled()) {
                    this.mLastGesture = ChartTouchListener.ChartGesture.X_ZOOM;
                    float scaleX2 = getXDist(event) / this.mSavedXDist;
                    if (scaleX2 >= 1.0f) {
                        isZoomingOut = false;
                    }
                    if (isZoomingOut) {
                        canZoomMoreX = h.canZoomOutMoreX();
                    } else {
                        canZoomMoreX = h.canZoomInMoreX();
                    }
                    if (canZoomMoreX) {
                        this.mMatrix.set(this.mSavedMatrix);
                        this.mMatrix.postScale(scaleX2, 1.0f, t.f90x, t.f91y);
                        if (l != null) {
                            l.onChartScale(event, scaleX2, 1.0f);
                        }
                    }
                } else if (this.mTouchMode == 3 && ((BarLineChartBase) this.mChart).isScaleYEnabled()) {
                    this.mLastGesture = ChartTouchListener.ChartGesture.Y_ZOOM;
                    float scaleY2 = getYDist(event) / this.mSavedYDist;
                    if (scaleY2 >= 1.0f) {
                        isZoomingOut = false;
                    }
                    if (isZoomingOut) {
                        canZoomMoreY = h.canZoomOutMoreY();
                    } else {
                        canZoomMoreY = h.canZoomInMoreY();
                    }
                    if (canZoomMoreY) {
                        this.mMatrix.set(this.mSavedMatrix);
                        this.mMatrix.postScale(1.0f, scaleY2, t.f90x, t.f91y);
                        if (l != null) {
                            l.onChartScale(event, 1.0f, scaleY2);
                        }
                    }
                }
                MPPointF.recycleInstance(t);
            }
        }
    }

    private void performHighlightDrag(MotionEvent e) {
        Highlight h = ((BarLineChartBase) this.mChart).getHighlightByTouchPoint(e.getX(), e.getY());
        if (h != null && !h.equalTo(this.mLastHighlighted)) {
            this.mLastHighlighted = h;
            ((BarLineChartBase) this.mChart).highlightValue(h, true);
        }
    }

    private static void midPoint(MPPointF point, MotionEvent event) {
        point.f90x = (event.getX(0) + event.getX(1)) / 2.0f;
        point.f91y = (event.getY(0) + event.getY(1)) / 2.0f;
    }

    private static float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
    }

    private static float getXDist(MotionEvent e) {
        return Math.abs(e.getX(0) - e.getX(1));
    }

    private static float getYDist(MotionEvent e) {
        return Math.abs(e.getY(0) - e.getY(1));
    }

    public MPPointF getTrans(float x, float y) {
        float yTrans;
        ViewPortHandler vph = ((BarLineChartBase) this.mChart).getViewPortHandler();
        float xTrans = x - vph.offsetLeft();
        if (inverted()) {
            yTrans = -(y - vph.offsetTop());
        } else {
            yTrans = -((((float) ((BarLineChartBase) this.mChart).getMeasuredHeight()) - y) - vph.offsetBottom());
        }
        return MPPointF.getInstance(xTrans, yTrans);
    }

    private boolean inverted() {
        return (this.mClosestDataSetToTouch == null && ((BarLineChartBase) this.mChart).isAnyAxisInverted()) || (this.mClosestDataSetToTouch != null && ((BarLineChartBase) this.mChart).isInverted(this.mClosestDataSetToTouch.getAxisDependency()));
    }

    public Matrix getMatrix() {
        return this.mMatrix;
    }

    public void setDragTriggerDist(float dragTriggerDistance) {
        this.mDragTriggerDist = Utils.convertDpToPixel(dragTriggerDistance);
    }

    public boolean onDoubleTap(MotionEvent e) {
        this.mLastGesture = ChartTouchListener.ChartGesture.DOUBLE_TAP;
        OnChartGestureListener l = ((BarLineChartBase) this.mChart).getOnChartGestureListener();
        if (l != null) {
            l.onChartDoubleTapped(e);
        }
        if (((BarLineChartBase) this.mChart).isDoubleTapToZoomEnabled() && ((BarLineScatterCandleBubbleData) ((BarLineChartBase) this.mChart).getData()).getEntryCount() > 0) {
            MPPointF trans = getTrans(e.getX(), e.getY());
            BarLineChartBase barLineChartBase = (BarLineChartBase) this.mChart;
            float f = 1.4f;
            float f2 = ((BarLineChartBase) this.mChart).isScaleXEnabled() ? 1.4f : 1.0f;
            if (!((BarLineChartBase) this.mChart).isScaleYEnabled()) {
                f = 1.0f;
            }
            barLineChartBase.zoom(f2, f, trans.f90x, trans.f91y);
            if (((BarLineChartBase) this.mChart).isLogEnabled()) {
                Log.i("BarlineChartTouch", "Double-Tap, Zooming In, x: " + trans.f90x + ", y: " + trans.f91y);
            }
            MPPointF.recycleInstance(trans);
        }
        return super.onDoubleTap(e);
    }

    public void onLongPress(MotionEvent e) {
        this.mLastGesture = ChartTouchListener.ChartGesture.LONG_PRESS;
        OnChartGestureListener l = ((BarLineChartBase) this.mChart).getOnChartGestureListener();
        if (l != null) {
            l.onChartLongPressed(e);
        }
    }

    public boolean onSingleTapUp(MotionEvent e) {
        this.mLastGesture = ChartTouchListener.ChartGesture.SINGLE_TAP;
        OnChartGestureListener l = ((BarLineChartBase) this.mChart).getOnChartGestureListener();
        if (l != null) {
            l.onChartSingleTapped(e);
        }
        if (!((BarLineChartBase) this.mChart).isHighlightPerTapEnabled()) {
            return false;
        }
        performHighlight(((BarLineChartBase) this.mChart).getHighlightByTouchPoint(e.getX(), e.getY()), e);
        return super.onSingleTapUp(e);
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        this.mLastGesture = ChartTouchListener.ChartGesture.FLING;
        OnChartGestureListener l = ((BarLineChartBase) this.mChart).getOnChartGestureListener();
        if (l != null) {
            l.onChartFling(e1, e2, velocityX, velocityY);
        }
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    public void stopDeceleration() {
        MPPointF mPPointF = this.mDecelerationVelocity;
        mPPointF.f90x = 0.0f;
        mPPointF.f91y = 0.0f;
    }

    public void computeScroll() {
        float dragDistanceY = 0.0f;
        if (this.mDecelerationVelocity.f90x != 0.0f || this.mDecelerationVelocity.f91y != 0.0f) {
            long currentTime = AnimationUtils.currentAnimationTimeMillis();
            this.mDecelerationVelocity.f90x *= ((BarLineChartBase) this.mChart).getDragDecelerationFrictionCoef();
            this.mDecelerationVelocity.f91y *= ((BarLineChartBase) this.mChart).getDragDecelerationFrictionCoef();
            float timeInterval = ((float) (currentTime - this.mDecelerationLastTime)) / 1000.0f;
            float distanceX = this.mDecelerationVelocity.f90x * timeInterval;
            float distanceY = this.mDecelerationVelocity.f91y * timeInterval;
            this.mDecelerationCurrentPoint.f90x += distanceX;
            this.mDecelerationCurrentPoint.f91y += distanceY;
            MotionEvent event = MotionEvent.obtain(currentTime, currentTime, 2, this.mDecelerationCurrentPoint.f90x, this.mDecelerationCurrentPoint.f91y, 0);
            float dragDistanceX = ((BarLineChartBase) this.mChart).isDragXEnabled() ? this.mDecelerationCurrentPoint.f90x - this.mTouchStartPoint.f90x : 0.0f;
            if (((BarLineChartBase) this.mChart).isDragYEnabled()) {
                dragDistanceY = this.mDecelerationCurrentPoint.f91y - this.mTouchStartPoint.f91y;
            }
            performDrag(event, dragDistanceX, dragDistanceY);
            event.recycle();
            this.mMatrix = ((BarLineChartBase) this.mChart).getViewPortHandler().refresh(this.mMatrix, this.mChart, false);
            this.mDecelerationLastTime = currentTime;
            if (((double) Math.abs(this.mDecelerationVelocity.f90x)) >= 0.01d || ((double) Math.abs(this.mDecelerationVelocity.f91y)) >= 0.01d) {
                Utils.postInvalidateOnAnimation(this.mChart);
                return;
            }
            ((BarLineChartBase) this.mChart).calculateOffsets();
            ((BarLineChartBase) this.mChart).postInvalidate();
            stopDeceleration();
        }
    }
}
