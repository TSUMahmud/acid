package cyanogenmod.app;

import android.app.Notification;
import android.app.NotificationGroup;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import cyanogenmod.p007os.Concierge;
import java.io.IOException;
import java.util.UUID;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class ProfileGroup implements Parcelable {
    public static final Parcelable.Creator<ProfileGroup> CREATOR = new Parcelable.Creator<ProfileGroup>() {
        public ProfileGroup createFromParcel(Parcel in) {
            return new ProfileGroup(in);
        }

        public ProfileGroup[] newArray(int size) {
            return new ProfileGroup[size];
        }
    };
    private static final String TAG = "ProfileGroup";
    private boolean mDefaultGroup;
    private boolean mDirty;
    private Mode mLightsMode;
    private String mName;
    private int mNameResId;
    private Mode mRingerMode;
    private Uri mRingerOverride;
    private Mode mSoundMode;
    private Uri mSoundOverride;
    private UUID mUuid;
    private Mode mVibrateMode;

    public enum Mode {
        SUPPRESS,
        DEFAULT,
        OVERRIDE
    }

    public ProfileGroup(UUID uuid, boolean defaultGroup) {
        this((String) null, uuid, defaultGroup);
    }

    private ProfileGroup(String name, UUID uuid, boolean defaultGroup) {
        this.mSoundOverride = RingtoneManager.getDefaultUri(2);
        boolean z = true;
        this.mRingerOverride = RingtoneManager.getDefaultUri(1);
        this.mSoundMode = Mode.DEFAULT;
        this.mRingerMode = Mode.DEFAULT;
        this.mVibrateMode = Mode.DEFAULT;
        this.mLightsMode = Mode.DEFAULT;
        this.mDefaultGroup = false;
        this.mName = name;
        this.mUuid = uuid != null ? uuid : UUID.randomUUID();
        this.mDefaultGroup = defaultGroup;
        this.mDirty = uuid != null ? false : z;
    }

    private ProfileGroup(Parcel in) {
        this.mSoundOverride = RingtoneManager.getDefaultUri(2);
        this.mRingerOverride = RingtoneManager.getDefaultUri(1);
        this.mSoundMode = Mode.DEFAULT;
        this.mRingerMode = Mode.DEFAULT;
        this.mVibrateMode = Mode.DEFAULT;
        this.mLightsMode = Mode.DEFAULT;
        this.mDefaultGroup = false;
        readFromParcel(in);
    }

    public boolean matches(NotificationGroup group, boolean defaultGroup) {
        if (this.mUuid.equals(group.getUuid())) {
            return true;
        }
        boolean matches = false;
        String str = this.mName;
        if (str != null && str.equals(group.getName())) {
            matches = true;
        } else if (this.mDefaultGroup && defaultGroup) {
            matches = true;
        }
        if (!matches) {
            return false;
        }
        this.mName = null;
        this.mUuid = group.getUuid();
        this.mDirty = true;
        return true;
    }

    public UUID getUuid() {
        return this.mUuid;
    }

    public boolean isDefaultGroup() {
        return this.mDefaultGroup;
    }

    public boolean isDirty() {
        return this.mDirty;
    }

    public void setSoundOverride(Uri sound) {
        this.mSoundOverride = sound;
        this.mDirty = true;
    }

    public Uri getSoundOverride() {
        return this.mSoundOverride;
    }

    public void setRingerOverride(Uri ringer) {
        this.mRingerOverride = ringer;
        this.mDirty = true;
    }

    public Uri getRingerOverride() {
        return this.mRingerOverride;
    }

    public void setSoundMode(Mode soundMode) {
        this.mSoundMode = soundMode;
        this.mDirty = true;
    }

    public Mode getSoundMode() {
        return this.mSoundMode;
    }

    public void setRingerMode(Mode ringerMode) {
        this.mRingerMode = ringerMode;
        this.mDirty = true;
    }

    public Mode getRingerMode() {
        return this.mRingerMode;
    }

    public void setVibrateMode(Mode vibrateMode) {
        this.mVibrateMode = vibrateMode;
        this.mDirty = true;
    }

    public Mode getVibrateMode() {
        return this.mVibrateMode;
    }

    public void setLightsMode(Mode lightsMode) {
        this.mLightsMode = lightsMode;
        this.mDirty = true;
    }

    public Mode getLightsMode() {
        return this.mLightsMode;
    }

    /* renamed from: cyanogenmod.app.ProfileGroup$2 */
    static /* synthetic */ class C07752 {
        static final /* synthetic */ int[] $SwitchMap$cyanogenmod$app$ProfileGroup$Mode = new int[Mode.values().length];

        static {
            try {
                $SwitchMap$cyanogenmod$app$ProfileGroup$Mode[Mode.OVERRIDE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$cyanogenmod$app$ProfileGroup$Mode[Mode.SUPPRESS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$cyanogenmod$app$ProfileGroup$Mode[Mode.DEFAULT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public void applyOverridesToNotification(Notification notification) {
        int i = C07752.$SwitchMap$cyanogenmod$app$ProfileGroup$Mode[this.mSoundMode.ordinal()];
        if (i == 1) {
            notification.sound = this.mSoundOverride;
        } else if (i == 2) {
            notification.defaults &= -2;
            notification.sound = null;
        }
        int i2 = C07752.$SwitchMap$cyanogenmod$app$ProfileGroup$Mode[this.mVibrateMode.ordinal()];
        if (i2 == 1) {
            notification.defaults |= 2;
        } else if (i2 == 2) {
            notification.defaults &= -3;
            notification.vibrate = null;
        }
        int i3 = C07752.$SwitchMap$cyanogenmod$app$ProfileGroup$Mode[this.mLightsMode.ordinal()];
        if (i3 == 1) {
            notification.defaults |= 4;
        } else if (i3 == 2) {
            notification.defaults &= -5;
            notification.flags &= -2;
        }
    }

    private boolean validateOverrideUri(Context context, Uri uri) {
        if (RingtoneManager.isDefault(uri)) {
            return true;
        }
        Cursor cursor = context.getContentResolver().query(uri, (String[]) null, (String) null, (String[]) null, (String) null);
        if (cursor == null) {
            return false;
        }
        boolean valid = cursor.moveToFirst();
        cursor.close();
        return valid;
    }

    /* access modifiers changed from: package-private */
    public void validateOverrideUris(Context context) {
        if (!validateOverrideUri(context, this.mSoundOverride)) {
            this.mSoundOverride = RingtoneManager.getDefaultUri(2);
            this.mSoundMode = Mode.DEFAULT;
            this.mDirty = true;
        }
        if (!validateOverrideUri(context, this.mRingerOverride)) {
            this.mRingerOverride = RingtoneManager.getDefaultUri(1);
            this.mRingerMode = Mode.DEFAULT;
            this.mDirty = true;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(dest);
        dest.writeString(this.mName);
        new ParcelUuid(this.mUuid).writeToParcel(dest, 0);
        dest.writeInt(this.mDefaultGroup ? 1 : 0);
        dest.writeInt(this.mDirty ? 1 : 0);
        dest.writeParcelable(this.mSoundOverride, flags);
        dest.writeParcelable(this.mRingerOverride, flags);
        dest.writeString(this.mSoundMode.name());
        dest.writeString(this.mRingerMode.name());
        dest.writeString(this.mVibrateMode.name());
        dest.writeString(this.mLightsMode.name());
        parcelInfo.complete();
    }

    public void readFromParcel(Parcel in) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(in);
        if (parcelInfo.getParcelVersion() >= 2) {
            this.mName = in.readString();
            this.mUuid = ((ParcelUuid) ParcelUuid.CREATOR.createFromParcel(in)).getUuid();
            boolean z = true;
            this.mDefaultGroup = in.readInt() != 0;
            if (in.readInt() == 0) {
                z = false;
            }
            this.mDirty = z;
            this.mSoundOverride = (Uri) in.readParcelable((ClassLoader) null);
            this.mRingerOverride = (Uri) in.readParcelable((ClassLoader) null);
            this.mSoundMode = (Mode) Mode.valueOf(Mode.class, in.readString());
            this.mRingerMode = (Mode) Mode.valueOf(Mode.class, in.readString());
            this.mVibrateMode = (Mode) Mode.valueOf(Mode.class, in.readString());
            this.mLightsMode = (Mode) Mode.valueOf(Mode.class, in.readString());
        }
        parcelInfo.complete();
    }

    public void getXmlString(StringBuilder builder, Context context) {
        builder.append("<profileGroup uuid=\"");
        builder.append(TextUtils.htmlEncode(this.mUuid.toString()));
        if (this.mName != null) {
            builder.append("\" name=\"");
            builder.append(this.mName);
        }
        builder.append("\" default=\"");
        builder.append(isDefaultGroup());
        builder.append("\">\n<sound>");
        builder.append(TextUtils.htmlEncode(this.mSoundOverride.toString()));
        builder.append("</sound>\n<ringer>");
        builder.append(TextUtils.htmlEncode(this.mRingerOverride.toString()));
        builder.append("</ringer>\n<soundMode>");
        builder.append(this.mSoundMode);
        builder.append("</soundMode>\n<ringerMode>");
        builder.append(this.mRingerMode);
        builder.append("</ringerMode>\n<vibrateMode>");
        builder.append(this.mVibrateMode);
        builder.append("</vibrateMode>\n<lightsMode>");
        builder.append(this.mLightsMode);
        builder.append("</lightsMode>\n</profileGroup>\n");
        this.mDirty = false;
    }

    public static ProfileGroup fromXml(XmlPullParser xpp, Context context) throws XmlPullParserException, IOException {
        String name = xpp.getAttributeValue((String) null, "name");
        UUID uuid = null;
        String value = xpp.getAttributeValue((String) null, ProfileManager.EXTRA_PROFILE_UUID);
        if (value != null) {
            try {
                uuid = UUID.fromString(value);
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "UUID not recognized for " + name + ", using new one.");
            }
        }
        ProfileGroup profileGroup = new ProfileGroup(name, uuid, TextUtils.equals(xpp.getAttributeValue((String) null, "default"), "true"));
        int event = xpp.next();
        while (true) {
            if (event != 3 || !xpp.getName().equals("profileGroup")) {
                if (event == 2) {
                    name = xpp.getName();
                    if (name.equals("sound")) {
                        profileGroup.setSoundOverride(Uri.parse(xpp.nextText()));
                    } else if (name.equals("ringer")) {
                        profileGroup.setRingerOverride(Uri.parse(xpp.nextText()));
                    } else if (name.equals("soundMode")) {
                        profileGroup.setSoundMode(Mode.valueOf(xpp.nextText()));
                    } else if (name.equals("ringerMode")) {
                        profileGroup.setRingerMode(Mode.valueOf(xpp.nextText()));
                    } else if (name.equals("vibrateMode")) {
                        profileGroup.setVibrateMode(Mode.valueOf(xpp.nextText()));
                    } else if (name.equals("lightsMode")) {
                        profileGroup.setLightsMode(Mode.valueOf(xpp.nextText()));
                    }
                } else if (event == 1) {
                    throw new IOException("Premature end of file while parsing profleGroup:" + name);
                }
                event = xpp.next();
            } else {
                profileGroup.mDirty = false;
                return profileGroup;
            }
        }
    }
}
