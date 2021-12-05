package cyanogenmod.app;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import cyanogenmod.app.CMContextConstants;
import cyanogenmod.app.ILiveLockScreenManager;

public class LiveLockScreenManager {
    public static final String SERVICE_INTERFACE = "cyanogenmod.app.LiveLockScreenManagerService";
    private static final String TAG = LiveLockScreenManager.class.getSimpleName();
    private static LiveLockScreenManager sInstance;
    private static ILiveLockScreenManager sService;
    private Context mContext;

    private LiveLockScreenManager(Context context) {
        this.mContext = context;
        sService = getService();
        if (context.getPackageManager().hasSystemFeature(CMContextConstants.Features.LIVE_LOCK_SCREEN) && sService == null) {
            Log.wtf(TAG, "Unable to get LiveLockScreenManagerService. The service either crashed, was not started, or the interface has been called to early in SystemServer init");
        }
    }

    private ILiveLockScreenManager getService() {
        IBinder b;
        if (sService == null && (b = ServiceManager.getService(CMContextConstants.CM_LIVE_LOCK_SCREEN_SERVICE)) != null) {
            sService = ILiveLockScreenManager.Stub.asInterface(b);
        }
        return sService;
    }

    private void logServiceException(Exception e) {
        Log.w(TAG, "Unable to access LiveLockScreenServiceBroker", e);
    }

    public static LiveLockScreenManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LiveLockScreenManager(context);
        }
        return sInstance;
    }

    public boolean show(int id, LiveLockScreenInfo llsInfo) {
        int[] idOut = new int[1];
        try {
            sService.enqueueLiveLockScreen(this.mContext.getPackageName(), id, llsInfo, idOut, UserHandle.myUserId());
            if (id == idOut[0]) {
                return true;
            }
            String str = TAG;
            Log.w(str, "show: id corrupted: sent " + id + ", got back " + idOut[0]);
            return false;
        } catch (RemoteException e) {
            logServiceException(e);
            return false;
        }
    }

    public void cancel(int id) {
        try {
            sService.cancelLiveLockScreen(this.mContext.getPackageName(), id, UserHandle.myUserId());
        } catch (RemoteException e) {
            logServiceException(e);
        }
    }

    public void setDefaultLiveLockScreen(LiveLockScreenInfo llsInfo) {
        try {
            sService.setDefaultLiveLockScreen(llsInfo);
        } catch (RemoteException e) {
            logServiceException(e);
        }
    }

    public LiveLockScreenInfo getDefaultLiveLockScreen() {
        try {
            return sService.getDefaultLiveLockScreen();
        } catch (RemoteException e) {
            logServiceException(e);
            return null;
        }
    }

    public LiveLockScreenInfo getCurrentLiveLockScreen() {
        try {
            return sService.getCurrentLiveLockScreen();
        } catch (RemoteException e) {
            logServiceException(e);
            return null;
        }
    }

    public boolean getLiveLockScreenEnabled() {
        try {
            return sService.getLiveLockScreenEnabled();
        } catch (RemoteException e) {
            logServiceException(e);
            return false;
        }
    }

    public void setLiveLockScreenEnabled(boolean enabled) {
        try {
            sService.setLiveLockScreenEnabled(enabled);
        } catch (RemoteException e) {
            logServiceException(e);
        }
    }
}
