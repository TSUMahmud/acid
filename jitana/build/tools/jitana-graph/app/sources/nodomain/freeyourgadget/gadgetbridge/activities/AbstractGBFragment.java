package nodomain.freeyourgadget.gadgetbridge.activities;

import androidx.fragment.app.Fragment;

public abstract class AbstractGBFragment extends Fragment {
    private boolean mVisibleInActivity;

    /* access modifiers changed from: protected */
    public abstract CharSequence getTitle();

    /* access modifiers changed from: protected */
    public void onMadeVisibleInActivity() {
    }

    /* access modifiers changed from: protected */
    public void onMadeInvisibleInActivity() {
        this.mVisibleInActivity = false;
    }

    public boolean isVisibleInActivity() {
        return this.mVisibleInActivity;
    }

    public void onMadeVisibleInActivityInternal() {
        this.mVisibleInActivity = true;
        if (isVisible()) {
            onMadeVisibleInActivity();
        }
    }
}
