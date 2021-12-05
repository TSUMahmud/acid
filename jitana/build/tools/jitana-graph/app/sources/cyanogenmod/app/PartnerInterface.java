package cyanogenmod.app;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import cyanogenmod.app.CMContextConstants;
import cyanogenmod.app.IPartnerInterface;

public class PartnerInterface {
    public static final String MODIFY_NETWORK_SETTINGS_PERMISSION = "cyanogenmod.permission.MODIFY_NETWORK_SETTINGS";
    public static final String MODIFY_SOUND_SETTINGS_PERMISSION = "cyanogenmod.permission.MODIFY_SOUND_SETTINGS";
    private static final String TAG = "PartnerInterface";
    public static final int ZEN_MODE_IMPORTANT_INTERRUPTIONS = 1;
    public static final int ZEN_MODE_NO_INTERRUPTIONS = 2;
    public static final int ZEN_MODE_OFF = 0;
    private static PartnerInterface sPartnerInterfaceInstance;
    private static IPartnerInterface sService;
    private Context mContext;

    private PartnerInterface(Context context) {
        Context appContext = context.getApplicationContext();
        if (appContext != null) {
            this.mContext = appContext;
        } else {
            this.mContext = context;
        }
        sService = getService();
        if (context.getPackageManager().hasSystemFeature(CMContextConstants.Features.PARTNER) && sService == null) {
            throw new RuntimeException("Unable to get PartnerInterfaceService. The service either crashed, was not started, or the interface has been called to early in SystemServer init");
        }
    }

    public static PartnerInterface getInstance(Context context) {
        if (sPartnerInterfaceInstance == null) {
            sPartnerInterfaceInstance = new PartnerInterface(context);
        }
        return sPartnerInterfaceInstance;
    }

    public static IPartnerInterface getService() {
        IPartnerInterface iPartnerInterface = sService;
        if (iPartnerInterface != null) {
            return iPartnerInterface;
        }
        IBinder b = ServiceManager.getService(CMContextConstants.CM_PARTNER_INTERFACE);
        if (b == null) {
            return null;
        }
        sService = IPartnerInterface.Stub.asInterface(b);
        return sService;
    }

    public void setAirplaneModeEnabled(boolean enabled) {
        IPartnerInterface iPartnerInterface = sService;
        if (iPartnerInterface != null) {
            try {
                iPartnerInterface.setAirplaneModeEnabled(enabled);
            } catch (RemoteException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
        }
    }

    public void setMobileDataEnabled(boolean enabled) {
        IPartnerInterface iPartnerInterface = sService;
        if (iPartnerInterface != null) {
            try {
                iPartnerInterface.setMobileDataEnabled(enabled);
            } catch (RemoteException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
        }
    }

    public boolean setZenMode(int mode) {
        IPartnerInterface iPartnerInterface = sService;
        if (iPartnerInterface == null) {
            return false;
        }
        try {
            return iPartnerInterface.setZenMode(mode);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return false;
        }
    }

    public boolean setZenModeWithDuration(int mode, long durationMillis) {
        IPartnerInterface iPartnerInterface = sService;
        if (iPartnerInterface == null) {
            return false;
        }
        try {
            return iPartnerInterface.setZenModeWithDuration(mode, durationMillis);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return false;
        }
    }

    public void shutdownDevice() {
        IPartnerInterface iPartnerInterface = sService;
        if (iPartnerInterface != null) {
            try {
                iPartnerInterface.shutdown();
            } catch (RemoteException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
        }
    }

    public void rebootDevice() {
        IPartnerInterface iPartnerInterface = sService;
        if (iPartnerInterface != null) {
            try {
                iPartnerInterface.reboot();
            } catch (RemoteException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
        }
    }

    public String getCurrentHotwordPackageName() {
        IPartnerInterface iPartnerInterface = sService;
        if (iPartnerInterface == null) {
            return null;
        }
        try {
            return iPartnerInterface.getCurrentHotwordPackageName();
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }
}
