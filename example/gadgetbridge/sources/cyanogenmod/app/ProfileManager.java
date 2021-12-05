package cyanogenmod.app;

import android.app.NotificationGroup;
import android.content.Context;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import cyanogenmod.app.CMContextConstants;
import cyanogenmod.app.IProfileManager;
import java.util.UUID;

public class ProfileManager {
    public static final String ACTION_PROFILE_PICKER = "cyanogenmod.platform.intent.action.PROFILE_PICKER";
    public static final String EXTRA_LAST_PROFILE_NAME = "lastName";
    public static final String EXTRA_LAST_PROFILE_UUID = "lastUuid";
    public static final String EXTRA_PROFILES_STATE = "profile_state";
    public static final String EXTRA_PROFILE_DIALOG_THEME = "cyanogenmod.platform.intent.extra.profile.DIALOG_THEME";
    public static final String EXTRA_PROFILE_EXISTING_UUID = "cyanogenmod.platform.extra.profile.EXISTING_UUID";
    public static final String EXTRA_PROFILE_NAME = "name";
    public static final String EXTRA_PROFILE_PICKED_UUID = "cyanogenmod.platform.intent.extra.profile.PICKED_UUID";
    public static final String EXTRA_PROFILE_SHOW_NONE = "cyanogenmod.platform.intent.extra.profile.SHOW_NONE";
    public static final String EXTRA_PROFILE_TITLE = "cyanogenmod.platform.intent.extra.profile.TITLE";
    public static final String EXTRA_PROFILE_UUID = "uuid";
    public static final String EXTRA_TRIGGER_ID = "trigger_id";
    public static final String EXTRA_TRIGGER_STATE = "trigger_state";
    public static final String EXTRA_TRIGGER_TYPE = "trigger_type";
    public static final String INTENT_ACTION_PROFILE_SELECTED = "cyanogenmod.platform.intent.action.PROFILE_SELECTED";
    public static final String INTENT_ACTION_PROFILE_TRIGGER_STATE_CHANGED = "cyanogenmod.platform.intent.action.INTENT_ACTION_PROFILE_TRIGGER_STATE_CHANGED";
    public static final String INTENT_ACTION_PROFILE_UPDATED = "cyanogenmod.platform.intent.action.PROFILE_UPDATED";
    public static final UUID NO_PROFILE = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final String PROFILES_STATE_CHANGED_ACTION = "cyanogenmod.platform.app.profiles.PROFILES_STATE_CHANGED";
    public static final int PROFILES_STATE_DISABLED = 0;
    public static final int PROFILES_STATE_ENABLED = 1;
    private static final String TAG = "ProfileManager";
    private static ProfileManager sProfileManagerInstance;
    private static IProfileManager sService;
    private Context mContext;

    private ProfileManager(Context context) {
        Context appContext = context.getApplicationContext();
        if (appContext != null) {
            this.mContext = appContext;
        } else {
            this.mContext = context;
        }
        sService = getService();
        if (context.getPackageManager().hasSystemFeature(CMContextConstants.Features.PROFILES) && sService == null) {
            Log.wtf(TAG, "Unable to get ProfileManagerService. The service either crashed, was not started, or the interface has been called to early in SystemServer init");
        }
    }

    public static ProfileManager getInstance(Context context) {
        if (sProfileManagerInstance == null) {
            sProfileManagerInstance = new ProfileManager(context);
        }
        return sProfileManagerInstance;
    }

    public static IProfileManager getService() {
        IProfileManager iProfileManager = sService;
        if (iProfileManager != null) {
            return iProfileManager;
        }
        sService = IProfileManager.Stub.asInterface(ServiceManager.getService("profile"));
        return sService;
    }

    @Deprecated
    public void setActiveProfile(String profileName) {
        try {
            getService().setActiveProfileByName(profileName);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    public void setActiveProfile(UUID profileUuid) {
        try {
            getService().setActiveProfile(new ParcelUuid(profileUuid));
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    public Profile getActiveProfile() {
        try {
            return getService().getActiveProfile();
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public void addProfile(Profile profile) {
        try {
            getService().addProfile(profile);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    public void removeProfile(Profile profile) {
        try {
            getService().removeProfile(profile);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    public void updateProfile(Profile profile) {
        try {
            getService().updateProfile(profile);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    @Deprecated
    public Profile getProfile(String profileName) {
        try {
            return getService().getProfileByName(profileName);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public Profile getProfile(UUID profileUuid) {
        try {
            return getService().getProfile(new ParcelUuid(profileUuid));
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public String[] getProfileNames() {
        try {
            Profile[] profiles = getService().getProfiles();
            String[] names = new String[profiles.length];
            for (int i = 0; i < profiles.length; i++) {
                names[i] = profiles[i].getName();
            }
            return names;
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public Profile[] getProfiles() {
        try {
            return getService().getProfiles();
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public boolean profileExists(String profileName) {
        try {
            return getService().profileExistsByName(profileName);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return true;
        }
    }

    public boolean profileExists(UUID profileUuid) {
        try {
            return getService().profileExists(new ParcelUuid(profileUuid));
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return true;
        }
    }

    public boolean notificationGroupExists(String notificationGroupName) {
        try {
            return getService().notificationGroupExistsByName(notificationGroupName);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return true;
        }
    }

    public NotificationGroup[] getNotificationGroups() {
        try {
            return getService().getNotificationGroups();
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public void addNotificationGroup(NotificationGroup group) {
        try {
            getService().addNotificationGroup(group);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    public void removeNotificationGroup(NotificationGroup group) {
        try {
            getService().removeNotificationGroup(group);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    public void updateNotificationGroup(NotificationGroup group) {
        try {
            getService().updateNotificationGroup(group);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    public NotificationGroup getNotificationGroupForPackage(String pkg) {
        try {
            return getService().getNotificationGroupForPackage(pkg);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public NotificationGroup getNotificationGroup(UUID uuid) {
        try {
            return getService().getNotificationGroup(new ParcelUuid(uuid));
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public ProfileGroup getActiveProfileGroup(String packageName) {
        NotificationGroup notificationGroup = getNotificationGroupForPackage(packageName);
        if (notificationGroup == null) {
            return getActiveProfile().getDefaultGroup();
        }
        return getActiveProfile().getProfileGroup(notificationGroup.getUuid());
    }

    public void resetAll() {
        try {
            getService().resetAll();
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        } catch (SecurityException e2) {
            Log.e(TAG, e2.getLocalizedMessage(), e2);
        }
    }

    public boolean isProfilesEnabled() {
        try {
            return getService().isEnabled();
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return false;
        }
    }
}
