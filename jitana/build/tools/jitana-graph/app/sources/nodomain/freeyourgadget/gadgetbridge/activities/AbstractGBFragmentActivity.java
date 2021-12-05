package nodomain.freeyourgadget.gadgetbridge.activities;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;

public abstract class AbstractGBFragmentActivity extends AbstractGBActivity {
    private AbstractFragmentPagerAdapter mSectionsPagerAdapter;

    /* access modifiers changed from: protected */
    public abstract AbstractFragmentPagerAdapter createFragmentPagerAdapter(FragmentManager fragmentManager);

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSectionsPagerAdapter = createFragmentPagerAdapter(getSupportFragmentManager());
    }

    public AbstractFragmentPagerAdapter getPagerAdapter() {
        return this.mSectionsPagerAdapter;
    }
}
