package androidx.constraintlayout.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.solver.widgets.Helper;
import androidx.constraintlayout.widget.C0150R;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Arrays;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;

public abstract class ConstraintHelper extends View {
    protected int mCount;
    protected Helper mHelperWidget;
    protected int[] mIds = new int[32];
    private String mReferenceIds;
    protected boolean mUseViewMeasure = false;
    protected Context myContext;

    public ConstraintHelper(Context context) {
        super(context);
        this.myContext = context;
        init((AttributeSet) null);
    }

    public ConstraintHelper(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.myContext = context;
        init(attrs);
    }

    public ConstraintHelper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.myContext = context;
        init(attrs);
    }

    /* access modifiers changed from: protected */
    public void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, C0150R.styleable.ConstraintLayout_Layout);
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == C0150R.styleable.ConstraintLayout_Layout_constraint_referenced_ids) {
                    this.mReferenceIds = a.getString(attr);
                    setIds(this.mReferenceIds);
                }
            }
        }
    }

    public int[] getReferencedIds() {
        return Arrays.copyOf(this.mIds, this.mCount);
    }

    public void setReferencedIds(int[] ids) {
        this.mCount = 0;
        for (int tag : ids) {
            setTag(tag, (Object) null);
        }
    }

    public void setTag(int tag, Object value) {
        int i = this.mCount + 1;
        int[] iArr = this.mIds;
        if (i > iArr.length) {
            this.mIds = Arrays.copyOf(iArr, iArr.length * 2);
        }
        int[] iArr2 = this.mIds;
        int i2 = this.mCount;
        iArr2[i2] = tag;
        this.mCount = i2 + 1;
    }

    public void onDraw(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mUseViewMeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            setMeasuredDimension(0, 0);
        }
    }

    public void validateParams() {
        if (this.mHelperWidget != null) {
            ViewGroup.LayoutParams params = getLayoutParams();
            if (params instanceof ConstraintLayout.LayoutParams) {
                ((ConstraintLayout.LayoutParams) params).widget = this.mHelperWidget;
            }
        }
    }

    private void addID(String idString) {
        Object value;
        if (idString != null && this.myContext != null) {
            String idString2 = idString.trim();
            int tag = 0;
            try {
                tag = C0150R.C0151id.class.getField(idString2).getInt((Object) null);
            } catch (Exception e) {
            }
            if (tag == 0) {
                tag = this.myContext.getResources().getIdentifier(idString2, PackageConfigHelper.DB_ID, this.myContext.getPackageName());
            }
            if (tag == 0 && isInEditMode() && (getParent() instanceof ConstraintLayout) && (value = ((ConstraintLayout) getParent()).getDesignInformation(0, idString2)) != null && (value instanceof Integer)) {
                tag = ((Integer) value).intValue();
            }
            if (tag != 0) {
                setTag(tag, (Object) null);
                return;
            }
            Log.w("ConstraintHelper", "Could not find id of \"" + idString2 + "\"");
        }
    }

    private void setIds(String idList) {
        if (idList != null) {
            int begin = 0;
            while (true) {
                int end = idList.indexOf(44, begin);
                if (end == -1) {
                    addID(idList.substring(begin));
                    return;
                } else {
                    addID(idList.substring(begin, end));
                    begin = end + 1;
                }
            }
        }
    }

    public void updatePreLayout(ConstraintLayout container) {
        if (isInEditMode()) {
            setIds(this.mReferenceIds);
        }
        Helper helper = this.mHelperWidget;
        if (helper != null) {
            helper.removeAllIds();
            for (int i = 0; i < this.mCount; i++) {
                View view = container.getViewById(this.mIds[i]);
                if (view != null) {
                    this.mHelperWidget.add(container.getViewWidget(view));
                }
            }
        }
    }

    public void updatePostLayout(ConstraintLayout container) {
    }

    public void updatePostMeasure(ConstraintLayout container) {
    }
}
