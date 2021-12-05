package androidx.fragment.app;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import androidx.lifecycle.Lifecycle;

final class FragmentState implements Parcelable {
    public static final Parcelable.Creator<FragmentState> CREATOR = new Parcelable.Creator<FragmentState>() {
        public FragmentState createFromParcel(Parcel in) {
            return new FragmentState(in);
        }

        public FragmentState[] newArray(int size) {
            return new FragmentState[size];
        }
    };
    final Bundle mArguments;
    final String mClassName;
    final int mContainerId;
    final boolean mDetached;
    final int mFragmentId;
    final boolean mFromLayout;
    final boolean mHidden;
    Fragment mInstance;
    final int mMaxLifecycleState;
    final boolean mRemoving;
    final boolean mRetainInstance;
    Bundle mSavedFragmentState;
    final String mTag;
    final String mWho;

    FragmentState(Fragment frag) {
        this.mClassName = frag.getClass().getName();
        this.mWho = frag.mWho;
        this.mFromLayout = frag.mFromLayout;
        this.mFragmentId = frag.mFragmentId;
        this.mContainerId = frag.mContainerId;
        this.mTag = frag.mTag;
        this.mRetainInstance = frag.mRetainInstance;
        this.mRemoving = frag.mRemoving;
        this.mDetached = frag.mDetached;
        this.mArguments = frag.mArguments;
        this.mHidden = frag.mHidden;
        this.mMaxLifecycleState = frag.mMaxState.ordinal();
    }

    FragmentState(Parcel in) {
        this.mClassName = in.readString();
        this.mWho = in.readString();
        boolean z = true;
        this.mFromLayout = in.readInt() != 0;
        this.mFragmentId = in.readInt();
        this.mContainerId = in.readInt();
        this.mTag = in.readString();
        this.mRetainInstance = in.readInt() != 0;
        this.mRemoving = in.readInt() != 0;
        this.mDetached = in.readInt() != 0;
        this.mArguments = in.readBundle();
        this.mHidden = in.readInt() == 0 ? false : z;
        this.mSavedFragmentState = in.readBundle();
        this.mMaxLifecycleState = in.readInt();
    }

    public Fragment instantiate(ClassLoader classLoader, FragmentFactory factory) {
        if (this.mInstance == null) {
            Bundle bundle = this.mArguments;
            if (bundle != null) {
                bundle.setClassLoader(classLoader);
            }
            this.mInstance = factory.instantiate(classLoader, this.mClassName);
            this.mInstance.setArguments(this.mArguments);
            Bundle bundle2 = this.mSavedFragmentState;
            if (bundle2 != null) {
                bundle2.setClassLoader(classLoader);
                this.mInstance.mSavedFragmentState = this.mSavedFragmentState;
            } else {
                this.mInstance.mSavedFragmentState = new Bundle();
            }
            Fragment fragment = this.mInstance;
            fragment.mWho = this.mWho;
            fragment.mFromLayout = this.mFromLayout;
            fragment.mRestored = true;
            fragment.mFragmentId = this.mFragmentId;
            fragment.mContainerId = this.mContainerId;
            fragment.mTag = this.mTag;
            fragment.mRetainInstance = this.mRetainInstance;
            fragment.mRemoving = this.mRemoving;
            fragment.mDetached = this.mDetached;
            fragment.mHidden = this.mHidden;
            fragment.mMaxState = Lifecycle.State.values()[this.mMaxLifecycleState];
            if (FragmentManagerImpl.DEBUG) {
                Log.v("FragmentManager", "Instantiated fragment " + this.mInstance);
            }
        }
        return this.mInstance;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("FragmentState{");
        sb.append(this.mClassName);
        sb.append(" (");
        sb.append(this.mWho);
        sb.append(")}:");
        if (this.mFromLayout) {
            sb.append(" fromLayout");
        }
        if (this.mContainerId != 0) {
            sb.append(" id=0x");
            sb.append(Integer.toHexString(this.mContainerId));
        }
        String str = this.mTag;
        if (str != null && !str.isEmpty()) {
            sb.append(" tag=");
            sb.append(this.mTag);
        }
        if (this.mRetainInstance) {
            sb.append(" retainInstance");
        }
        if (this.mRemoving) {
            sb.append(" removing");
        }
        if (this.mDetached) {
            sb.append(" detached");
        }
        if (this.mHidden) {
            sb.append(" hidden");
        }
        return sb.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mClassName);
        dest.writeString(this.mWho);
        dest.writeInt(this.mFromLayout ? 1 : 0);
        dest.writeInt(this.mFragmentId);
        dest.writeInt(this.mContainerId);
        dest.writeString(this.mTag);
        dest.writeInt(this.mRetainInstance ? 1 : 0);
        dest.writeInt(this.mRemoving ? 1 : 0);
        dest.writeInt(this.mDetached ? 1 : 0);
        dest.writeBundle(this.mArguments);
        dest.writeInt(this.mHidden ? 1 : 0);
        dest.writeBundle(this.mSavedFragmentState);
        dest.writeInt(this.mMaxLifecycleState);
    }
}
