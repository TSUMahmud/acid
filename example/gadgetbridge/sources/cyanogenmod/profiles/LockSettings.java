package cyanogenmod.profiles;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.policy.IKeyguardService;
import cyanogenmod.p007os.Concierge;

public final class LockSettings implements Parcelable {
    public static final Parcelable.Creator<LockSettings> CREATOR = new Parcelable.Creator<LockSettings>() {
        public LockSettings createFromParcel(Parcel in) {
            return new LockSettings(in);
        }

        public LockSettings[] newArray(int size) {
            return new LockSettings[size];
        }
    };
    private static final String TAG = LockSettings.class.getSimpleName();
    private boolean mDirty;
    private int mValue;

    public LockSettings(Parcel parcel) {
        readFromParcel(parcel);
    }

    public LockSettings() {
        this(0);
    }

    public LockSettings(int value) {
        this.mValue = value;
        this.mDirty = false;
    }

    public int getValue() {
        return this.mValue;
    }

    public void setValue(int value) {
        this.mValue = value;
        this.mDirty = true;
    }

    public boolean isDirty() {
        return this.mDirty;
    }

    public void processOverride(Context context, IKeyguardService keyguard) {
        boolean enable;
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        if (devicePolicyManager != null && devicePolicyManager.requireSecureKeyguard()) {
            enable = true;
        } else if (this.mValue != 2) {
            enable = true;
        } else {
            enable = false;
        }
        try {
            keyguard.setKeyguardEnabled(enable);
        } catch (RemoteException e) {
            String str = TAG;
            Log.w(str, "unable to set keyguard enabled state to: " + enable, e);
        }
    }

    public void writeXmlString(StringBuilder builder, Context context) {
        builder.append(this.mValue);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(dest);
        dest.writeInt(this.mValue);
        dest.writeInt(this.mDirty ? 1 : 0);
        parcelInfo.complete();
    }

    public void readFromParcel(Parcel in) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(in);
        if (parcelInfo.getParcelVersion() >= 2) {
            this.mValue = in.readInt();
            this.mDirty = in.readInt() != 0;
        }
        parcelInfo.complete();
    }
}
