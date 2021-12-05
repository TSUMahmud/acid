package nodomain.freeyourgadget.gadgetbridge.activities;

import android.view.ViewGroup;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private final Set<AbstractGBFragment> fragments = new HashSet();
    private Object primaryFragment;

    public AbstractFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        if (fragment instanceof AbstractGBFragment) {
            this.fragments.add((AbstractGBFragment) fragment);
        }
        return fragment;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        this.fragments.remove(object);
    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (object != this.primaryFragment) {
            this.primaryFragment = object;
            setCurrentFragment(this.primaryFragment);
        }
    }

    private void setCurrentFragment(Object newCurrentFragment) {
        for (AbstractGBFragment frag : this.fragments) {
            if (frag != newCurrentFragment) {
                frag.onMadeInvisibleInActivity();
            } else {
                frag.onMadeVisibleInActivityInternal();
            }
        }
    }
}
