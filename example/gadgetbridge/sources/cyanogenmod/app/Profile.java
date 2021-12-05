package cyanogenmod.app;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.policy.IKeyguardService;
import cyanogenmod.p007os.Build;
import cyanogenmod.p007os.Concierge;
import cyanogenmod.profiles.AirplaneModeSettings;
import cyanogenmod.profiles.BrightnessSettings;
import cyanogenmod.profiles.ConnectionSettings;
import cyanogenmod.profiles.LockSettings;
import cyanogenmod.profiles.RingModeSettings;
import cyanogenmod.profiles.StreamSettings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class Profile implements Parcelable, Comparable {
    public static final Parcelable.Creator<Profile> CREATOR = new Parcelable.Creator<Profile>() {
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
    private static final String TAG = "Profile";
    private Map<Integer, ConnectionSettings> connections;
    private AirplaneModeSettings mAirplaneMode;
    private BrightnessSettings mBrightness;
    private ProfileGroup mDefaultGroup;
    private boolean mDirty;
    private int mDozeMode;
    private int mExpandedDesktopMode;
    private String mName;
    private int mNameResId;
    private int mNotificationLightMode;
    private int mProfileType;
    private RingModeSettings mRingMode;
    private LockSettings mScreenLockMode;
    private ArrayList<UUID> mSecondaryUuids;
    private boolean mStatusBarIndicator;
    private Map<String, ProfileTrigger> mTriggers;
    private UUID mUuid;
    private Map<Integer, ConnectionSettings> networkConnectionSubIds;
    private Map<UUID, ProfileGroup> profileGroups;
    private Map<Integer, StreamSettings> streams;

    public static class DozeMode {
        public static final int DEFAULT = 0;
        public static final int DISABLE = 2;
        public static final int ENABLE = 1;
    }

    public static class ExpandedDesktopMode {
        public static final int DEFAULT = 0;
        public static final int DISABLE = 2;
        public static final int ENABLE = 1;
    }

    public static class LockMode {
        public static final int DEFAULT = 0;
        public static final int DISABLE = 2;
        public static final int INSECURE = 1;
    }

    public static class NotificationLightMode {
        public static final int DEFAULT = 0;
        public static final int DISABLE = 2;
        public static final int ENABLE = 1;
    }

    public static class TriggerState {
        public static final int DISABLED = 2;
        public static final int ON_A2DP_CONNECT = 3;
        public static final int ON_A2DP_DISCONNECT = 4;
        public static final int ON_CONNECT = 0;
        public static final int ON_DISCONNECT = 1;
    }

    public static class TriggerType {
        public static final int BLUETOOTH = 1;
        public static final int WIFI = 0;
    }

    public static class Type {
        public static final int CONDITIONAL = 1;
        public static final int TOGGLE = 0;
    }

    public static class ProfileTrigger implements Parcelable {
        public static final Parcelable.Creator<ProfileTrigger> CREATOR = new Parcelable.Creator<ProfileTrigger>() {
            public ProfileTrigger createFromParcel(Parcel in) {
                return new ProfileTrigger(in);
            }

            public ProfileTrigger[] newArray(int size) {
                return new ProfileTrigger[size];
            }
        };
        /* access modifiers changed from: private */
        public String mId;
        private String mName;
        /* access modifiers changed from: private */
        public int mState;
        private int mType;

        public ProfileTrigger(int type, String id, int state, String name) {
            this.mType = type;
            this.mId = id;
            this.mState = state;
            this.mName = name;
        }

        private ProfileTrigger(Parcel in) {
            Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(in);
            if (parcelInfo.getParcelVersion() >= 2) {
                this.mType = in.readInt();
                this.mId = in.readString();
                this.mState = in.readInt();
                this.mName = in.readString();
            }
            parcelInfo.complete();
        }

        public void writeToParcel(Parcel dest, int flags) {
            Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(dest);
            dest.writeInt(this.mType);
            dest.writeString(this.mId);
            dest.writeInt(this.mState);
            dest.writeString(this.mName);
            parcelInfo.complete();
        }

        public int describeContents() {
            return 0;
        }

        public int getType() {
            return this.mType;
        }

        public String getName() {
            return this.mName;
        }

        public String getId() {
            return this.mId;
        }

        public int getState() {
            return this.mState;
        }

        public void getXmlString(StringBuilder builder, Context context) {
            String itemType = this.mType == 0 ? "wifiAP" : "btDevice";
            builder.append("<");
            builder.append(itemType);
            builder.append(StringUtils.SPACE);
            builder.append(getIdType(this.mType));
            builder.append("=\"");
            builder.append(this.mId);
            builder.append("\" state=\"");
            builder.append(this.mState);
            builder.append("\" name=\"");
            builder.append(this.mName);
            builder.append("\"></");
            builder.append(itemType);
            builder.append(">\n");
        }

        public static ProfileTrigger fromXml(XmlPullParser xpp, Context context) {
            int type;
            String name = xpp.getName();
            if (name.equals("wifiAP")) {
                type = 0;
            } else if (!name.equals("btDevice")) {
                return null;
            } else {
                type = 1;
            }
            String id = xpp.getAttributeValue((String) null, getIdType(type));
            int state = Integer.valueOf(xpp.getAttributeValue((String) null, "state")).intValue();
            String triggerName = xpp.getAttributeValue((String) null, "name");
            if (triggerName == null) {
                triggerName = id;
            }
            return new ProfileTrigger(type, id, state, triggerName);
        }

        private static String getIdType(int type) {
            return type == 0 ? "ssid" : "address";
        }
    }

    public Profile(String name) {
        this(name, -1, UUID.randomUUID());
    }

    public Profile(String name, int nameResId, UUID uuid) {
        this.mSecondaryUuids = new ArrayList<>();
        this.profileGroups = new HashMap();
        this.mStatusBarIndicator = false;
        this.streams = new HashMap();
        this.mTriggers = new HashMap();
        this.connections = new HashMap();
        this.networkConnectionSubIds = new HashMap();
        this.mRingMode = new RingModeSettings();
        this.mAirplaneMode = new AirplaneModeSettings();
        this.mBrightness = new BrightnessSettings();
        this.mScreenLockMode = new LockSettings();
        this.mExpandedDesktopMode = 0;
        this.mDozeMode = 0;
        this.mNotificationLightMode = 0;
        this.mName = name;
        this.mNameResId = nameResId;
        this.mUuid = uuid;
        this.mProfileType = 0;
        this.mDirty = false;
    }

    private Profile(Parcel in) {
        this.mSecondaryUuids = new ArrayList<>();
        this.profileGroups = new HashMap();
        this.mStatusBarIndicator = false;
        this.streams = new HashMap();
        this.mTriggers = new HashMap();
        this.connections = new HashMap();
        this.networkConnectionSubIds = new HashMap();
        this.mRingMode = new RingModeSettings();
        this.mAirplaneMode = new AirplaneModeSettings();
        this.mBrightness = new BrightnessSettings();
        this.mScreenLockMode = new LockSettings();
        this.mExpandedDesktopMode = 0;
        this.mDozeMode = 0;
        this.mNotificationLightMode = 0;
        readFromParcel(in);
    }

    public int getTriggerState(int type, String id) {
        ProfileTrigger trigger = id != null ? this.mTriggers.get(id) : null;
        if (trigger != null) {
            return trigger.mState;
        }
        return 2;
    }

    public ArrayList<ProfileTrigger> getTriggersFromType(int type) {
        ArrayList<ProfileTrigger> result = new ArrayList<>();
        for (Map.Entry<String, ProfileTrigger> profileTrigger : this.mTriggers.entrySet()) {
            ProfileTrigger trigger = profileTrigger.getValue();
            if (trigger.getType() == type) {
                result.add(trigger);
            }
        }
        return result;
    }

    public void setTrigger(int type, String id, int state, String name) {
        if (id != null && type >= 0 && type <= 1 && state >= 0 && state <= 4) {
            ProfileTrigger trigger = this.mTriggers.get(id);
            if (state == 2) {
                if (trigger != null) {
                    this.mTriggers.remove(id);
                }
            } else if (trigger != null) {
                int unused = trigger.mState = state;
            } else {
                this.mTriggers.put(id, new ProfileTrigger(type, id, state, name));
            }
            this.mDirty = true;
        }
    }

    public void setTrigger(ProfileTrigger trigger) {
        setTrigger(trigger.getType(), trigger.getId(), trigger.getState(), trigger.getName());
    }

    public int compareTo(Object obj) {
        Profile tmp = (Profile) obj;
        if (this.mName.compareTo(tmp.mName) < 0) {
            return -1;
        }
        if (this.mName.compareTo(tmp.mName) > 0) {
            return 1;
        }
        return 0;
    }

    public void addProfileGroup(ProfileGroup profileGroup) {
        if (profileGroup != null) {
            if (profileGroup.isDefaultGroup()) {
                if (this.mDefaultGroup == null) {
                    this.mDefaultGroup = profileGroup;
                } else {
                    return;
                }
            }
            this.profileGroups.put(profileGroup.getUuid(), profileGroup);
            this.mDirty = true;
        }
    }

    public void removeProfileGroup(UUID uuid) {
        if (!this.profileGroups.get(uuid).isDefaultGroup()) {
            this.profileGroups.remove(uuid);
            return;
        }
        Log.e(TAG, "Cannot remove default group: " + uuid);
    }

    public ProfileGroup[] getProfileGroups() {
        return (ProfileGroup[]) this.profileGroups.values().toArray(new ProfileGroup[this.profileGroups.size()]);
    }

    public ProfileGroup getProfileGroup(UUID uuid) {
        return this.profileGroups.get(uuid);
    }

    public ProfileGroup getDefaultGroup() {
        return this.mDefaultGroup;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(dest);
        if (!TextUtils.isEmpty(this.mName)) {
            dest.writeInt(1);
            dest.writeString(this.mName);
        } else {
            dest.writeInt(0);
        }
        if (this.mNameResId != 0) {
            dest.writeInt(1);
            dest.writeInt(this.mNameResId);
        } else {
            dest.writeInt(0);
        }
        if (this.mUuid != null) {
            dest.writeInt(1);
            new ParcelUuid(this.mUuid).writeToParcel(dest, 0);
        } else {
            dest.writeInt(0);
        }
        ArrayList<UUID> arrayList = this.mSecondaryUuids;
        if (arrayList == null || arrayList.isEmpty()) {
            dest.writeInt(0);
        } else {
            ArrayList<ParcelUuid> uuids = new ArrayList<>(this.mSecondaryUuids.size());
            Iterator i$ = this.mSecondaryUuids.iterator();
            while (i$.hasNext()) {
                uuids.add(new ParcelUuid(i$.next()));
            }
            dest.writeInt(1);
            dest.writeParcelableArray((Parcelable[]) uuids.toArray(new Parcelable[uuids.size()]), flags);
        }
        dest.writeInt(this.mStatusBarIndicator ? 1 : 0);
        dest.writeInt(this.mProfileType);
        dest.writeInt(this.mDirty ? 1 : 0);
        Map<UUID, ProfileGroup> map = this.profileGroups;
        if (map == null || map.isEmpty()) {
            dest.writeInt(0);
        } else {
            dest.writeInt(1);
            dest.writeTypedArray((Parcelable[]) this.profileGroups.values().toArray(new ProfileGroup[0]), flags);
        }
        Map<Integer, StreamSettings> map2 = this.streams;
        if (map2 == null || map2.isEmpty()) {
            dest.writeInt(0);
        } else {
            dest.writeInt(1);
            dest.writeTypedArray((Parcelable[]) this.streams.values().toArray(new StreamSettings[0]), flags);
        }
        Map<Integer, ConnectionSettings> map3 = this.connections;
        if (map3 == null || map3.isEmpty()) {
            dest.writeInt(0);
        } else {
            dest.writeInt(1);
            dest.writeTypedArray((Parcelable[]) this.connections.values().toArray(new ConnectionSettings[0]), flags);
        }
        if (this.mRingMode != null) {
            dest.writeInt(1);
            this.mRingMode.writeToParcel(dest, 0);
        } else {
            dest.writeInt(0);
        }
        if (this.mAirplaneMode != null) {
            dest.writeInt(1);
            this.mAirplaneMode.writeToParcel(dest, 0);
        } else {
            dest.writeInt(0);
        }
        if (this.mBrightness != null) {
            dest.writeInt(1);
            this.mBrightness.writeToParcel(dest, 0);
        } else {
            dest.writeInt(0);
        }
        if (this.mScreenLockMode != null) {
            dest.writeInt(1);
            this.mScreenLockMode.writeToParcel(dest, 0);
        } else {
            dest.writeInt(0);
        }
        dest.writeTypedArray((Parcelable[]) this.mTriggers.values().toArray(new ProfileTrigger[0]), flags);
        dest.writeInt(this.mExpandedDesktopMode);
        dest.writeInt(this.mDozeMode);
        dest.writeInt(this.mNotificationLightMode);
        Map<Integer, ConnectionSettings> map4 = this.networkConnectionSubIds;
        if (map4 == null || map4.isEmpty()) {
            dest.writeInt(0);
        } else {
            dest.writeInt(1);
            dest.writeTypedArray((Parcelable[]) this.networkConnectionSubIds.values().toArray(new ConnectionSettings[0]), flags);
        }
        parcelInfo.complete();
    }

    public void readFromParcel(Parcel in) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(in);
        int parcelableVersion = parcelInfo.getParcelVersion();
        if (parcelableVersion >= 2) {
            if (in.readInt() != 0) {
                this.mName = in.readString();
            }
            if (in.readInt() != 0) {
                this.mNameResId = in.readInt();
            }
            if (in.readInt() != 0) {
                this.mUuid = ((ParcelUuid) ParcelUuid.CREATOR.createFromParcel(in)).getUuid();
            }
            if (in.readInt() != 0) {
                for (Parcelable parcel : in.readParcelableArray((ClassLoader) null)) {
                    this.mSecondaryUuids.add(((ParcelUuid) parcel).getUuid());
                }
            }
            boolean z = false;
            this.mStatusBarIndicator = in.readInt() == 1;
            this.mProfileType = in.readInt();
            if (in.readInt() == 1) {
                z = true;
            }
            this.mDirty = z;
            if (in.readInt() != 0) {
                for (ProfileGroup group : (ProfileGroup[]) in.createTypedArray(ProfileGroup.CREATOR)) {
                    this.profileGroups.put(group.getUuid(), group);
                    if (group.isDefaultGroup()) {
                        this.mDefaultGroup = group;
                    }
                }
            }
            if (in.readInt() != 0) {
                for (StreamSettings stream : (StreamSettings[]) in.createTypedArray(StreamSettings.CREATOR)) {
                    this.streams.put(Integer.valueOf(stream.getStreamId()), stream);
                }
            }
            if (in.readInt() != 0) {
                for (ConnectionSettings connection : (ConnectionSettings[]) in.createTypedArray(ConnectionSettings.CREATOR)) {
                    this.connections.put(Integer.valueOf(connection.getConnectionId()), connection);
                }
            }
            if (in.readInt() != 0) {
                this.mRingMode = RingModeSettings.CREATOR.createFromParcel(in);
            }
            if (in.readInt() != 0) {
                this.mAirplaneMode = AirplaneModeSettings.CREATOR.createFromParcel(in);
            }
            if (in.readInt() != 0) {
                this.mBrightness = BrightnessSettings.CREATOR.createFromParcel(in);
            }
            if (in.readInt() != 0) {
                this.mScreenLockMode = LockSettings.CREATOR.createFromParcel(in);
            }
            for (ProfileTrigger trigger : (ProfileTrigger[]) in.createTypedArray(ProfileTrigger.CREATOR)) {
                this.mTriggers.put(trigger.mId, trigger);
            }
            this.mExpandedDesktopMode = in.readInt();
            this.mDozeMode = in.readInt();
        }
        if (parcelableVersion >= 5) {
            this.mNotificationLightMode = in.readInt();
            if (in.readInt() != 0) {
                for (ConnectionSettings connection2 : (ConnectionSettings[]) in.createTypedArray(ConnectionSettings.CREATOR)) {
                    this.networkConnectionSubIds.put(Integer.valueOf(connection2.getSubId()), connection2);
                }
            }
        }
        parcelInfo.complete();
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
        this.mNameResId = -1;
        this.mDirty = true;
    }

    public int getProfileType() {
        return this.mProfileType;
    }

    public void setProfileType(int type) {
        this.mProfileType = type;
        this.mDirty = true;
    }

    public UUID getUuid() {
        if (this.mUuid == null) {
            this.mUuid = UUID.randomUUID();
        }
        return this.mUuid;
    }

    public UUID[] getSecondaryUuids() {
        ArrayList<UUID> arrayList = this.mSecondaryUuids;
        return (UUID[]) arrayList.toArray(new UUID[arrayList.size()]);
    }

    public void setSecondaryUuids(List<UUID> uuids) {
        this.mSecondaryUuids.clear();
        if (uuids != null) {
            this.mSecondaryUuids.addAll(uuids);
            this.mDirty = true;
        }
    }

    public void addSecondaryUuid(UUID uuid) {
        if (uuid != null) {
            this.mSecondaryUuids.add(uuid);
            this.mDirty = true;
        }
    }

    public boolean getStatusBarIndicator() {
        return this.mStatusBarIndicator;
    }

    public void setStatusBarIndicator(boolean newStatusBarIndicator) {
        this.mStatusBarIndicator = newStatusBarIndicator;
        this.mDirty = true;
    }

    public boolean isConditionalType() {
        return this.mProfileType == 1;
    }

    public void setConditionalType() {
        this.mProfileType = 1;
        this.mDirty = true;
    }

    public RingModeSettings getRingMode() {
        return this.mRingMode;
    }

    public void setRingMode(RingModeSettings descriptor) {
        this.mRingMode = descriptor;
        this.mDirty = true;
    }

    public LockSettings getScreenLockMode() {
        return this.mScreenLockMode;
    }

    public void setScreenLockMode(LockSettings screenLockMode) {
        this.mScreenLockMode = screenLockMode;
        this.mDirty = true;
    }

    public int getExpandedDesktopMode() {
        return this.mExpandedDesktopMode;
    }

    public void setExpandedDesktopMode(int expandedDesktopMode) {
        if (expandedDesktopMode < 0 || expandedDesktopMode > 2) {
            this.mExpandedDesktopMode = 0;
        } else {
            this.mExpandedDesktopMode = expandedDesktopMode;
        }
        this.mDirty = true;
    }

    public int getDozeMode() {
        return this.mDozeMode;
    }

    public void setDozeMode(int dozeMode) {
        if (dozeMode < 0 || dozeMode > 2) {
            this.mDozeMode = 0;
        } else {
            this.mDozeMode = dozeMode;
        }
        this.mDirty = true;
    }

    public int getNotificationLightMode() {
        return this.mNotificationLightMode;
    }

    public void setNotificationLightMode(int notificationLightMode) {
        if (notificationLightMode < 0 || notificationLightMode > 2) {
            this.mNotificationLightMode = 0;
        } else {
            this.mNotificationLightMode = notificationLightMode;
        }
        this.mDirty = true;
    }

    public AirplaneModeSettings getAirplaneMode() {
        return this.mAirplaneMode;
    }

    public void setAirplaneMode(AirplaneModeSettings descriptor) {
        this.mAirplaneMode = descriptor;
        this.mDirty = true;
    }

    public BrightnessSettings getBrightness() {
        return this.mBrightness;
    }

    public void setBrightness(BrightnessSettings descriptor) {
        this.mBrightness = descriptor;
        this.mDirty = true;
    }

    public boolean isDirty() {
        if (this.mDirty) {
            return true;
        }
        for (ProfileGroup group : this.profileGroups.values()) {
            if (group.isDirty()) {
                return true;
            }
        }
        for (StreamSettings stream : this.streams.values()) {
            if (stream.isDirty()) {
                return true;
            }
        }
        for (ConnectionSettings conn : this.connections.values()) {
            if (conn.isDirty()) {
                return true;
            }
        }
        for (ConnectionSettings conn2 : this.networkConnectionSubIds.values()) {
            if (conn2.isDirty()) {
                return true;
            }
        }
        if (!this.mRingMode.isDirty() && !this.mAirplaneMode.isDirty() && !this.mBrightness.isDirty()) {
            return false;
        }
        return true;
    }

    public void getXmlString(StringBuilder builder, Context context) {
        builder.append("<profile ");
        if (this.mNameResId > 0) {
            builder.append("nameres=\"");
            builder.append(context.getResources().getResourceEntryName(this.mNameResId));
        } else {
            builder.append("name=\"");
            builder.append(TextUtils.htmlEncode(getName()));
        }
        builder.append("\" uuid=\"");
        builder.append(TextUtils.htmlEncode(getUuid().toString()));
        builder.append("\">\n");
        builder.append("<uuids>");
        Iterator i$ = this.mSecondaryUuids.iterator();
        while (i$.hasNext()) {
            builder.append("<uuid>");
            builder.append(TextUtils.htmlEncode(i$.next().toString()));
            builder.append("</uuid>");
        }
        builder.append("</uuids>\n");
        builder.append("<profiletype>");
        builder.append(getProfileType() == 0 ? "toggle" : "conditional");
        builder.append("</profiletype>\n");
        builder.append("<statusbar>");
        builder.append(getStatusBarIndicator() ? "yes" : "no");
        builder.append("</statusbar>\n");
        if (this.mScreenLockMode != null) {
            builder.append("<screen-lock-mode>");
            this.mScreenLockMode.writeXmlString(builder, context);
            builder.append("</screen-lock-mode>\n");
        }
        builder.append("<expanded-desktop-mode>");
        builder.append(this.mExpandedDesktopMode);
        builder.append("</expanded-desktop-mode>\n");
        builder.append("<doze-mode>");
        builder.append(this.mDozeMode);
        builder.append("</doze-mode>\n");
        builder.append("<notification-light-mode>");
        builder.append(this.mNotificationLightMode);
        builder.append("</notification-light-mode>\n");
        this.mAirplaneMode.getXmlString(builder, context);
        this.mBrightness.getXmlString(builder, context);
        this.mRingMode.getXmlString(builder, context);
        for (ProfileGroup pGroup : this.profileGroups.values()) {
            pGroup.getXmlString(builder, context);
        }
        for (StreamSettings sd : this.streams.values()) {
            sd.getXmlString(builder, context);
        }
        for (ConnectionSettings cs : this.connections.values()) {
            cs.getXmlString(builder, context);
        }
        for (ConnectionSettings cs2 : this.networkConnectionSubIds.values()) {
            cs2.getXmlString(builder, context);
        }
        if (!this.mTriggers.isEmpty()) {
            builder.append("<triggers>\n");
            for (ProfileTrigger trigger : this.mTriggers.values()) {
                trigger.getXmlString(builder, context);
            }
            builder.append("</triggers>\n");
        }
        builder.append("</profile>\n");
        this.mDirty = false;
    }

    private static List<UUID> readSecondaryUuidsFromXml(XmlPullParser xpp, Context context) throws XmlPullParserException, IOException {
        ArrayList<UUID> uuids = new ArrayList<>();
        int event = xpp.next();
        while (true) {
            if (event == 3 && xpp.getName().equals("uuids")) {
                return uuids;
            }
            if (event == 2 && xpp.getName().equals(ProfileManager.EXTRA_PROFILE_UUID)) {
                try {
                    uuids.add(UUID.fromString(xpp.nextText()));
                } catch (NullPointerException e) {
                    Log.w(TAG, "Null Pointer - invalid UUID");
                } catch (IllegalArgumentException e2) {
                    Log.w(TAG, "UUID not recognized");
                }
            }
            event = xpp.next();
        }
    }

    private static void readTriggersFromXml(XmlPullParser xpp, Context context, Profile profile) throws XmlPullParserException, IOException {
        int event = xpp.next();
        while (true) {
            if (event != 3 || !xpp.getName().equals("triggers")) {
                if (event == 2) {
                    ProfileTrigger trigger = ProfileTrigger.fromXml(xpp, context);
                    if (trigger != null) {
                        profile.mTriggers.put(trigger.mId, trigger);
                    }
                } else if (event == 1) {
                    throw new IOException("Premature end of file while parsing triggers");
                }
                event = xpp.next();
            } else {
                return;
            }
        }
    }

    public void validateRingtones(Context context) {
        for (ProfileGroup pg : this.profileGroups.values()) {
            pg.validateOverrideUris(context);
        }
    }

    public static Profile fromXml(XmlPullParser xpp, Context context) throws XmlPullParserException, IOException {
        String value = xpp.getAttributeValue((String) null, "nameres");
        int profileNameResId = -1;
        String profileName = null;
        if (value != null && (profileNameResId = context.getResources().getIdentifier(value, "string", "cyanogenmod.platform")) > 0) {
            profileName = context.getResources().getString(profileNameResId);
        }
        if (profileName == null) {
            profileName = xpp.getAttributeValue((String) null, "name");
        }
        UUID profileUuid = UUID.randomUUID();
        try {
            profileUuid = UUID.fromString(xpp.getAttributeValue((String) null, ProfileManager.EXTRA_PROFILE_UUID));
        } catch (NullPointerException e) {
            Log.w(TAG, "Null Pointer - UUID not found for " + profileName + ".  New UUID generated: " + profileUuid.toString());
        } catch (IllegalArgumentException e2) {
            Log.w(TAG, "UUID not recognized for " + profileName + ".  New UUID generated: " + profileUuid.toString());
        }
        Profile profile = new Profile(profileName, profileNameResId, profileUuid);
        int event = xpp.next();
        while (event != 3) {
            if (event == 2) {
                String name = xpp.getName();
                if (name.equals("uuids")) {
                    profile.setSecondaryUuids(readSecondaryUuidsFromXml(xpp, context));
                }
                if (name.equals("statusbar")) {
                    profile.setStatusBarIndicator(xpp.nextText().equals("yes"));
                }
                if (name.equals("profiletype")) {
                    profile.setProfileType(true ^ xpp.nextText().equals("toggle") ? 1 : 0);
                }
                if (name.equals("ringModeDescriptor")) {
                    profile.setRingMode(RingModeSettings.fromXml(xpp, context));
                }
                if (name.equals("airplaneModeDescriptor")) {
                    profile.setAirplaneMode(AirplaneModeSettings.fromXml(xpp, context));
                }
                if (name.equals("brightnessDescriptor")) {
                    profile.setBrightness(BrightnessSettings.fromXml(xpp, context));
                }
                if (name.equals("screen-lock-mode")) {
                    profile.setScreenLockMode(new LockSettings(Integer.valueOf(xpp.nextText()).intValue()));
                }
                if (name.equals("expanded-desktop-mode")) {
                    profile.setExpandedDesktopMode(Integer.valueOf(xpp.nextText()).intValue());
                }
                if (name.equals("doze-mode")) {
                    profile.setDozeMode(Integer.valueOf(xpp.nextText()).intValue());
                }
                if (name.equals("notification-light-mode")) {
                    profile.setNotificationLightMode(Integer.valueOf(xpp.nextText()).intValue());
                }
                if (name.equals("profileGroup")) {
                    profile.addProfileGroup(ProfileGroup.fromXml(xpp, context));
                }
                if (name.equals("streamDescriptor")) {
                    profile.setStreamSettings(StreamSettings.fromXml(xpp, context));
                }
                if (name.equals("connectionDescriptor")) {
                    ConnectionSettings cs = ConnectionSettings.fromXml(xpp, context);
                    if (Build.CM_VERSION.SDK_INT < 5 || cs.getConnectionId() != 9) {
                        profile.connections.put(Integer.valueOf(cs.getConnectionId()), cs);
                    } else {
                        profile.networkConnectionSubIds.put(Integer.valueOf(cs.getSubId()), cs);
                    }
                }
                if (name.equals("triggers")) {
                    readTriggersFromXml(xpp, context, profile);
                }
            } else if (event == 1) {
                throw new IOException("Premature end of file while parsing profle:" + profileName);
            }
            event = xpp.next();
        }
        profile.mDirty = false;
        return profile;
    }

    public void doSelect(Context context, IKeyguardService keyguardService) {
        int i;
        AudioManager am = (AudioManager) context.getSystemService("audio");
        Iterator i$ = this.streams.values().iterator();
        while (true) {
            i = 0;
            if (!i$.hasNext()) {
                break;
            }
            StreamSettings sd = i$.next();
            if (sd.isOverride()) {
                am.setStreamVolume(sd.getStreamId(), sd.getValue(), 0);
            }
        }
        for (ConnectionSettings cs : this.connections.values()) {
            if (cs.isOverride()) {
                cs.processOverride(context);
            }
        }
        for (ConnectionSettings cs2 : this.networkConnectionSubIds.values()) {
            if (cs2.isOverride()) {
                cs2.processOverride(context);
            }
        }
        this.mRingMode.processOverride(context);
        this.mAirplaneMode.processOverride(context);
        this.mBrightness.processOverride(context);
        if (keyguardService != null) {
            this.mScreenLockMode.processOverride(context, keyguardService);
        } else {
            Log.e(TAG, "cannot process screen lock override without a keyguard service.");
        }
        if (this.mDozeMode != 0) {
            Settings.Secure.putIntForUser(context.getContentResolver(), "doze_enabled", this.mDozeMode == 1 ? 1 : 0, -2);
        }
        if (this.mNotificationLightMode != 0) {
            ContentResolver contentResolver = context.getContentResolver();
            if (this.mNotificationLightMode == 1) {
                i = 1;
            }
            Settings.System.putIntForUser(contentResolver, "notification_light_pulse", i, -2);
        }
    }

    public StreamSettings getSettingsForStream(int streamId) {
        return this.streams.get(Integer.valueOf(streamId));
    }

    public void setStreamSettings(StreamSettings descriptor) {
        this.streams.put(Integer.valueOf(descriptor.getStreamId()), descriptor);
        this.mDirty = true;
    }

    public Collection<StreamSettings> getStreamSettings() {
        return this.streams.values();
    }

    public ConnectionSettings getSettingsForConnection(int connectionId) {
        if (connectionId != 9) {
            return this.connections.get(Integer.valueOf(connectionId));
        }
        if (this.networkConnectionSubIds.size() <= 1) {
            return this.networkConnectionSubIds.values().iterator().next();
        }
        throw new UnsupportedOperationException("Use getConnectionSettingsWithSubId for MSIM devices!");
    }

    public ConnectionSettings getConnectionSettingWithSubId(int subId) {
        return this.networkConnectionSubIds.get(Integer.valueOf(subId));
    }

    public void setConnectionSettings(ConnectionSettings descriptor) {
        if (descriptor.getConnectionId() == 9) {
            this.networkConnectionSubIds.put(Integer.valueOf(descriptor.getSubId()), descriptor);
        } else {
            this.connections.put(Integer.valueOf(descriptor.getConnectionId()), descriptor);
        }
        this.mDirty = true;
    }

    public Collection<ConnectionSettings> getConnectionSettings() {
        List<ConnectionSettings> combinedList = new ArrayList<>();
        combinedList.addAll(this.connections.values());
        combinedList.addAll(this.networkConnectionSubIds.values());
        return combinedList;
    }
}
