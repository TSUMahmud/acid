package cyanogenmod.power;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import cyanogenmod.app.CMContextConstants;
import cyanogenmod.power.IPerformanceManager;

public class PerformanceManager {
    public static final int[] POSSIBLE_POWER_PROFILES = {0, 1, 2, 3, 4};
    public static final String POWER_PROFILE_CHANGED = "cyanogenmod.power.PROFILE_CHANGED";
    public static final int PROFILE_BALANCED = 1;
    public static final int PROFILE_BIAS_PERFORMANCE = 4;
    public static final int PROFILE_BIAS_POWER_SAVE = 3;
    public static final int PROFILE_HIGH_PERFORMANCE = 2;
    public static final int PROFILE_POWER_SAVE = 0;
    public static final String TAG = "PerformanceManager";
    private static PerformanceManager sInstance;
    private static IPerformanceManager sService;
    private int mNumberOfProfiles = 0;

    private PerformanceManager(Context context) {
        sService = getService();
        if (context.getPackageManager().hasSystemFeature(CMContextConstants.Features.PERFORMANCE) && sService == null) {
            Log.wtf(TAG, "Unable to get PerformanceManagerService. The service either crashed, was not started, or the interface has been called to early in SystemServer init");
        }
        try {
            if (sService != null) {
                this.mNumberOfProfiles = sService.getNumberOfProfiles();
            }
        } catch (RemoteException e) {
        }
    }

    public static PerformanceManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PerformanceManager(context);
        }
        return sInstance;
    }

    public static IPerformanceManager getService() {
        IPerformanceManager iPerformanceManager = sService;
        if (iPerformanceManager != null) {
            return iPerformanceManager;
        }
        IBinder b = ServiceManager.getService(CMContextConstants.CM_PERFORMANCE_SERVICE);
        if (b == null) {
            return null;
        }
        sService = IPerformanceManager.Stub.asInterface(b);
        return sService;
    }

    private boolean checkService() {
        if (sService != null) {
            return true;
        }
        Log.w(TAG, "not connected to PerformanceManagerService");
        return false;
    }

    public void cpuBoost(int duration) {
        try {
            if (checkService()) {
                sService.cpuBoost(duration);
            }
        } catch (RemoteException e) {
        }
    }

    public int getNumberOfProfiles() {
        return this.mNumberOfProfiles;
    }

    public boolean setPowerProfile(int profile) {
        if (this.mNumberOfProfiles >= 1) {
            try {
                if (checkService()) {
                    return sService.setPowerProfile(profile);
                }
                return false;
            } catch (RemoteException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            throw new IllegalArgumentException("Power profiles not enabled on this system!");
        }
    }

    public int getPowerProfile() {
        if (this.mNumberOfProfiles <= 0) {
            return -1;
        }
        try {
            if (checkService()) {
                return sService.getPowerProfile();
            }
            return -1;
        } catch (RemoteException e) {
            return -1;
        }
    }

    public boolean getProfileHasAppProfiles(int profile) {
        if (this.mNumberOfProfiles <= 0) {
            return false;
        }
        try {
            if (checkService()) {
                return sService.getProfileHasAppProfiles(profile);
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }
}
