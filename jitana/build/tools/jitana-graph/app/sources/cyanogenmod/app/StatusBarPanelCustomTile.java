package cyanogenmod.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;
import cyanogenmod.p007os.Concierge;

public class StatusBarPanelCustomTile implements Parcelable {
    public static final Parcelable.Creator<StatusBarPanelCustomTile> CREATOR = new Parcelable.Creator<StatusBarPanelCustomTile>() {
        public StatusBarPanelCustomTile createFromParcel(Parcel parcel) {
            return new StatusBarPanelCustomTile(parcel);
        }

        public StatusBarPanelCustomTile[] newArray(int size) {
            return new StatusBarPanelCustomTile[size];
        }
    };
    private final CustomTile customTile;

    /* renamed from: id */
    private final int f102id;
    private final int initialPid;
    private final String key;
    private final String opPkg;
    private final String pkg;
    private final long postTime;
    private final String resPkg;
    private final String tag;
    private final int uid;
    private final UserHandle user;

    public StatusBarPanelCustomTile(String pkg2, String resPkg2, String opPkg2, int id, String tag2, int uid2, int initialPid2, CustomTile customTile2, UserHandle user2) {
        this(pkg2, resPkg2, opPkg2, id, tag2, uid2, initialPid2, customTile2, user2, System.currentTimeMillis());
    }

    public StatusBarPanelCustomTile(String pkg2, String resPkg2, String opPkg2, int id, String tag2, int uid2, int initialPid2, CustomTile customTile2, UserHandle user2, long postTime2) {
        if (pkg2 == null) {
            throw new NullPointerException();
        } else if (customTile2 != null) {
            this.pkg = pkg2;
            this.resPkg = resPkg2;
            this.opPkg = opPkg2;
            this.f102id = id;
            this.tag = tag2;
            this.uid = uid2;
            this.initialPid = initialPid2;
            this.customTile = customTile2;
            this.user = user2;
            this.postTime = postTime2;
            this.key = key();
        } else {
            throw new NullPointerException();
        }
    }

    public StatusBarPanelCustomTile(Parcel in) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(in);
        int parcelableVersion = parcelInfo.getParcelVersion();
        String tmpResPkg = null;
        String tmpPkg = null;
        String tmpOpPkg = null;
        int tmpId = -1;
        String tmpTag = null;
        int tmpUid = -1;
        int tmpPid = -1;
        CustomTile tmpCustomTile = null;
        UserHandle tmpUser = null;
        long tmpPostTime = -1;
        if (parcelableVersion >= 1) {
            tmpPkg = in.readString();
            tmpOpPkg = in.readString();
            tmpId = in.readInt();
            if (in.readInt() != 0) {
                tmpTag = in.readString();
            } else {
                tmpTag = null;
            }
            tmpUid = in.readInt();
            tmpPid = in.readInt();
            tmpCustomTile = new CustomTile(in);
            tmpUser = UserHandle.readFromParcel(in);
            tmpPostTime = in.readLong();
        }
        this.resPkg = parcelableVersion >= 2 ? in.readString() : tmpResPkg;
        this.pkg = tmpPkg;
        this.opPkg = tmpOpPkg;
        this.f102id = tmpId;
        this.tag = tmpTag;
        this.uid = tmpUid;
        this.initialPid = tmpPid;
        this.customTile = tmpCustomTile;
        this.user = tmpUser;
        this.postTime = tmpPostTime;
        this.key = key();
        parcelInfo.complete();
    }

    private String key() {
        return this.user.getIdentifier() + "|" + this.pkg + "|" + this.f102id + "|" + this.tag + "|" + this.uid;
    }

    public String persistableKey() {
        return this.user.getIdentifier() + "|" + this.pkg + "|" + this.tag;
    }

    public CustomTile getCustomTile() {
        return this.customTile;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(out);
        out.writeString(this.pkg);
        out.writeString(this.opPkg);
        out.writeInt(this.f102id);
        if (this.tag != null) {
            out.writeInt(1);
            out.writeString(this.tag);
        } else {
            out.writeInt(0);
        }
        out.writeInt(this.uid);
        out.writeInt(this.initialPid);
        this.customTile.writeToParcel(out, flags);
        this.user.writeToParcel(out, flags);
        out.writeLong(this.postTime);
        out.writeString(this.resPkg);
        parcelInfo.complete();
    }

    public StatusBarPanelCustomTile clone() {
        return new StatusBarPanelCustomTile(this.pkg, this.resPkg, this.opPkg, this.f102id, this.tag, this.uid, this.initialPid, this.customTile.clone(), this.user, this.postTime);
    }

    public int getUserId() {
        return this.user.getIdentifier();
    }

    public String getPackage() {
        return this.pkg;
    }

    public int getId() {
        return this.f102id;
    }

    public String getTag() {
        return this.tag;
    }

    public String getKey() {
        return this.key;
    }

    public int getUid() {
        return this.uid;
    }

    public String getResPkg() {
        return this.resPkg;
    }

    public String getOpPkg() {
        return this.opPkg;
    }

    public int getInitialPid() {
        return this.initialPid;
    }

    public UserHandle getUser() {
        return this.user;
    }

    public long getPostTime() {
        return this.postTime;
    }
}
