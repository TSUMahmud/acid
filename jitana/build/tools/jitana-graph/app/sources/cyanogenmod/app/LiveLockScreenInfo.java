package cyanogenmod.app;

import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import cyanogenmod.p007os.Concierge;

public class LiveLockScreenInfo implements Parcelable {
    public static final Parcelable.Creator<LiveLockScreenInfo> CREATOR = new Parcelable.Creator<LiveLockScreenInfo>() {
        public LiveLockScreenInfo createFromParcel(Parcel source) {
            return new LiveLockScreenInfo(source);
        }

        public LiveLockScreenInfo[] newArray(int size) {
            return new LiveLockScreenInfo[0];
        }
    };
    public static final int PRIORITY_DEFAULT = 0;
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_LOW = -1;
    public static final int PRIORITY_MAX = 2;
    public static final int PRIORITY_MIN = -2;
    public ComponentName component;
    public int priority;

    public LiveLockScreenInfo(ComponentName component2, int priority2) {
        this.component = component2;
        this.priority = priority2;
    }

    public LiveLockScreenInfo() {
        this.component = null;
        this.priority = 0;
    }

    private LiveLockScreenInfo(Parcel source) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(source);
        int parcelVersion = parcelInfo.getParcelVersion();
        this.priority = source.readInt();
        String component2 = source.readString();
        this.component = !TextUtils.isEmpty(component2) ? ComponentName.unflattenFromString(component2) : null;
        parcelInfo.complete();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(dest);
        dest.writeInt(this.priority);
        ComponentName componentName = this.component;
        dest.writeString(componentName != null ? componentName.flattenToString() : "");
        parcelInfo.complete();
    }

    public String toString() {
        return "LiveLockScreenInfo: priority=" + this.priority + ", component=" + this.component;
    }

    public LiveLockScreenInfo clone() {
        LiveLockScreenInfo that = new LiveLockScreenInfo();
        cloneInto(that);
        return that;
    }

    public void cloneInto(LiveLockScreenInfo that) {
        that.component = this.component.clone();
        that.priority = this.priority;
    }

    public static class Builder {
        private ComponentName mComponent;
        private int mPriority;

        public Builder setPriority(int priority) {
            if (priority < -2 || priority > 2) {
                throw new IllegalArgumentException("Invalid priorty given (" + priority + "): " + -2 + " <= priority <= " + -2);
            }
            this.mPriority = priority;
            return this;
        }

        public Builder setComponent(ComponentName component) {
            if (component != null) {
                this.mComponent = component;
                return this;
            }
            throw new IllegalArgumentException("Cannot call setComponent with a null component");
        }

        public LiveLockScreenInfo build() {
            return new LiveLockScreenInfo(this.mComponent, this.mPriority);
        }
    }
}
