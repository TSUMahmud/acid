package cyanogenmod.hardware;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import cyanogenmod.app.CMContextConstants;
import cyanogenmod.hardware.ILiveDisplayService;

public class LiveDisplayManager {
    public static final int ADJUSTMENT_CONTRAST = 3;
    public static final int ADJUSTMENT_HUE = 0;
    public static final int ADJUSTMENT_INTENSITY = 2;
    public static final int ADJUSTMENT_SATURATION = 1;
    public static final int FEATURE_AUTO_CONTRAST = 11;
    public static final int FEATURE_CABC = 10;
    public static final int FEATURE_COLOR_ADJUSTMENT = 13;
    public static final int FEATURE_COLOR_BALANCE = 16;
    public static final int FEATURE_COLOR_ENHANCEMENT = 12;
    public static final int FEATURE_DISPLAY_MODES = 15;
    public static final int FEATURE_FIRST = 10;
    public static final int FEATURE_LAST = 17;
    public static final int FEATURE_MANAGED_OUTDOOR_MODE = 14;
    public static final int FEATURE_PICTURE_ADJUSTMENT = 17;
    public static final int MODE_AUTO = 2;
    public static final int MODE_DAY = 4;
    public static final int MODE_FIRST = 0;
    public static final int MODE_LAST = 4;
    public static final int MODE_NIGHT = 1;
    public static final int MODE_OFF = 0;
    public static final int MODE_OUTDOOR = 3;
    private static final String TAG = "LiveDisplay";
    private static LiveDisplayManager sInstance;
    private static ILiveDisplayService sService;
    private final LiveDisplayConfig mConfig;
    private final Context mContext;

    private LiveDisplayManager(Context context) {
        Context appContext = context.getApplicationContext();
        if (appContext != null) {
            this.mContext = appContext;
        } else {
            this.mContext = context;
        }
        sService = getService();
        if (!context.getPackageManager().hasSystemFeature(CMContextConstants.Features.LIVEDISPLAY) || !checkService()) {
            Log.wtf(TAG, "Unable to get LiveDisplayService. The service either crashed, was not started, or the interface has been called to early in SystemServer init");
        }
        try {
            this.mConfig = sService.getConfig();
            if (this.mConfig == null) {
                throw new RuntimeException("Unable to get LiveDisplay configuration!");
            }
        } catch (RemoteException e) {
            throw new RuntimeException("Unable to fetch LiveDisplay configuration!", e);
        }
    }

    public static synchronized LiveDisplayManager getInstance(Context context) {
        LiveDisplayManager liveDisplayManager;
        synchronized (LiveDisplayManager.class) {
            if (sInstance == null) {
                sInstance = new LiveDisplayManager(context);
            }
            liveDisplayManager = sInstance;
        }
        return liveDisplayManager;
    }

    public static ILiveDisplayService getService() {
        ILiveDisplayService iLiveDisplayService = sService;
        if (iLiveDisplayService != null) {
            return iLiveDisplayService;
        }
        IBinder b = ServiceManager.getService(CMContextConstants.CM_LIVEDISPLAY_SERVICE);
        if (b == null) {
            return null;
        }
        sService = ILiveDisplayService.Stub.asInterface(b);
        return sService;
    }

    private boolean checkService() {
        if (sService != null) {
            return true;
        }
        Log.w(TAG, "not connected to CMHardwareManagerService");
        return false;
    }

    public LiveDisplayConfig getConfig() {
        return this.mConfig;
    }

    public int getMode() {
        try {
            if (checkService()) {
                return sService.getMode();
            }
            return 0;
        } catch (RemoteException e) {
            return 0;
        }
    }

    public boolean setMode(int mode) {
        try {
            return checkService() && sService.setMode(mode);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean isAutoContrastEnabled() {
        try {
            return checkService() && sService.isAutoContrastEnabled();
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean setAutoContrastEnabled(boolean enabled) {
        try {
            return checkService() && sService.setAutoContrastEnabled(enabled);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean isCABCEnabled() {
        try {
            return checkService() && sService.isCABCEnabled();
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean setCABCEnabled(boolean enabled) {
        try {
            return checkService() && sService.setCABCEnabled(enabled);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean isColorEnhancementEnabled() {
        try {
            return checkService() && sService.isColorEnhancementEnabled();
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean setColorEnhancementEnabled(boolean enabled) {
        try {
            return checkService() && sService.setColorEnhancementEnabled(enabled);
        } catch (RemoteException e) {
            return false;
        }
    }

    public int getDayColorTemperature() {
        try {
            if (checkService()) {
                return sService.getDayColorTemperature();
            }
            return -1;
        } catch (RemoteException e) {
            return -1;
        }
    }

    public boolean setDayColorTemperature(int temperature) {
        try {
            return checkService() && sService.setDayColorTemperature(temperature);
        } catch (RemoteException e) {
            return false;
        }
    }

    public int getNightColorTemperature() {
        try {
            if (checkService()) {
                return sService.getNightColorTemperature();
            }
            return -1;
        } catch (RemoteException e) {
            return -1;
        }
    }

    public boolean setNightColorTemperature(int temperature) {
        try {
            return checkService() && sService.setNightColorTemperature(temperature);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean isAutomaticOutdoorModeEnabled() {
        try {
            return checkService() && sService.isAutomaticOutdoorModeEnabled();
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean setAutomaticOutdoorModeEnabled(boolean enabled) {
        try {
            return checkService() && sService.setAutomaticOutdoorModeEnabled(enabled);
        } catch (RemoteException e) {
            return false;
        }
    }

    public float[] getColorAdjustment() {
        try {
            if (checkService()) {
                return sService.getColorAdjustment();
            }
        } catch (RemoteException e) {
        }
        return new float[]{1.0f, 1.0f, 1.0f};
    }

    public boolean setColorAdjustment(float[] adj) {
        try {
            return checkService() && sService.setColorAdjustment(adj);
        } catch (RemoteException e) {
            return false;
        }
    }

    public HSIC getPictureAdjustment() {
        try {
            if (checkService()) {
                return sService.getPictureAdjustment();
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean setPictureAdjustment(HSIC hsic) {
        try {
            return checkService() && sService.setPictureAdjustment(hsic);
        } catch (RemoteException e) {
            return false;
        }
    }

    public HSIC getDefaultPictureAdjustment() {
        try {
            if (checkService()) {
                return sService.getDefaultPictureAdjustment();
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }
}
