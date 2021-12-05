package androidx.preference;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.TypedArrayUtils;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

public class PreferenceCategory extends PreferenceGroup {
    public PreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PreferenceCategory(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, C0361R.attr.preferenceCategoryStyle, 16842892));
    }

    public PreferenceCategory(Context context) {
        this(context, (AttributeSet) null);
    }

    public boolean isEnabled() {
        return false;
    }

    public boolean shouldDisableDependents() {
        return !super.isEnabled();
    }

    public void onBindViewHolder(PreferenceViewHolder holder) {
        TextView titleView;
        super.onBindViewHolder(holder);
        if (Build.VERSION.SDK_INT >= 28) {
            holder.itemView.setAccessibilityHeading(true);
        } else if (Build.VERSION.SDK_INT < 21) {
            TypedValue value = new TypedValue();
            if (getContext().getTheme().resolveAttribute(C0361R.attr.colorAccent, value, true) && (titleView = (TextView) holder.findViewById(16908310)) != null && titleView.getCurrentTextColor() == ContextCompat.getColor(getContext(), C0361R.color.preference_fallback_accent_color)) {
                titleView.setTextColor(value.data);
            }
        }
    }

    @Deprecated
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat info) {
        AccessibilityNodeInfoCompat.CollectionItemInfoCompat existingItemInfo;
        super.onInitializeAccessibilityNodeInfo(info);
        if (Build.VERSION.SDK_INT < 28 && (existingItemInfo = info.getCollectionItemInfo()) != null) {
            info.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(existingItemInfo.getRowIndex(), existingItemInfo.getRowSpan(), existingItemInfo.getColumnIndex(), existingItemInfo.getColumnSpan(), true, existingItemInfo.isSelected()));
        }
    }
}
