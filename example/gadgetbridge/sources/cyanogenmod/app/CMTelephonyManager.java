package cyanogenmod.app;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.SubscriptionInfo;
import android.util.Log;
import android.util.Slog;
import cyanogenmod.app.CMContextConstants;
import cyanogenmod.app.ICMTelephonyManager;
import java.util.List;

public class CMTelephonyManager {
    public static final int ASK_FOR_SUBSCRIPTION_ID = 0;
    private static final String TAG = "CMTelephonyManager";
    private static boolean localLOGD = Log.isLoggable(TAG, 3);
    private static CMTelephonyManager sCMTelephonyManagerInstance;
    private static ICMTelephonyManager sService;
    private Context mContext;

    private CMTelephonyManager(Context context) {
        Context appContext = context.getApplicationContext();
        if (appContext != null) {
            this.mContext = appContext;
        } else {
            this.mContext = context;
        }
        sService = getService();
        if (context.getPackageManager().hasSystemFeature(CMContextConstants.Features.TELEPHONY) && sService == null) {
            Log.wtf(TAG, "Unable to get CMTelephonyManagerService. The service either crashed, was not started, or the interface has been called to early in SystemServer init");
        }
    }

    public static CMTelephonyManager getInstance(Context context) {
        if (sCMTelephonyManagerInstance == null) {
            sCMTelephonyManagerInstance = new CMTelephonyManager(context);
        }
        return sCMTelephonyManagerInstance;
    }

    public ICMTelephonyManager getService() {
        ICMTelephonyManager iCMTelephonyManager = sService;
        if (iCMTelephonyManager != null) {
            return iCMTelephonyManager;
        }
        IBinder b = ServiceManager.getService(CMContextConstants.CM_TELEPHONY_MANAGER_SERVICE);
        if (b == null) {
            return null;
        }
        sService = ICMTelephonyManager.Stub.asInterface(b);
        return sService;
    }

    public List<SubscriptionInfo> getSubInformation() {
        if (sService == null) {
            Log.w(TAG, "not connected to CMTelephonyManager");
            return null;
        }
        if (localLOGD) {
            String pkg = this.mContext.getPackageName();
            Log.v(TAG, pkg + " getting the SIMs information");
        }
        List<SubscriptionInfo> subInfoList = null;
        try {
            subInfoList = sService.getSubInformation();
            if (subInfoList == null) {
                Log.w(TAG, "no subscription list was returned from the service");
            } else if (subInfoList.isEmpty()) {
                Log.w(TAG, "the subscription list is empty");
            }
        } catch (RemoteException e) {
            Slog.w(TAG, "warning: no cm telephony manager service");
        }
        return subInfoList;
    }

    public boolean isSubActive(int subId) {
        if (sService == null) {
            Log.w(TAG, "not connected to CMTelephonyManager");
            return false;
        }
        if (localLOGD) {
            String pkg = this.mContext.getPackageName();
            Log.v(TAG, pkg + " getting the state of the SIM with subscription: " + subId);
        }
        boolean simActive = false;
        try {
            simActive = sService.isSubActive(subId);
            if (localLOGD) {
                String pkg2 = this.mContext.getPackageName();
                Log.v(TAG, pkg2 + " getting the SIM state with subscription " + subId + " as active: " + simActive);
            }
        } catch (RemoteException e) {
            Slog.w(TAG, "warning: no cm telephony manager service");
        }
        return simActive;
    }

    public void setSubState(int subId, boolean state) {
        if (sService == null) {
            Log.w(TAG, "not connected to CMTelephonyManager");
            return;
        }
        if (localLOGD) {
            String pkg = this.mContext.getPackageName();
            Log.v(TAG, pkg + " setting the state of the SIM with subscription " + subId + " as active: " + state);
        }
        try {
            sService.setSubState(subId, state);
        } catch (RemoteException e) {
            Slog.w(TAG, "warning: no cm telephony manager service");
        }
    }

    public boolean isDataConnectionSelectedOnSub(int subId) {
        if (sService == null) {
            Log.w(TAG, "not connected to CMTelephonyManager");
            return false;
        }
        if (localLOGD) {
            String pkg = this.mContext.getPackageName();
            Log.v(TAG, pkg + " getting if the data connection is enabled for SIM for subscription: " + subId);
        }
        boolean dataConnectionActiveOnSim = false;
        try {
            dataConnectionActiveOnSim = sService.isDataConnectionSelectedOnSub(subId);
            if (localLOGD) {
                String pkg2 = this.mContext.getPackageName();
                Log.v(TAG, pkg2 + " getting if the data connection is enabled for SIM with subscription " + subId + " as active: " + dataConnectionActiveOnSim);
            }
        } catch (RemoteException e) {
            Slog.w(TAG, "warning: no cm telephony manager service");
        }
        return dataConnectionActiveOnSim;
    }

    public boolean isDataConnectionEnabled() {
        if (sService == null) {
            Log.w(TAG, "not connected to CMTelephonyManager");
            return false;
        }
        if (localLOGD) {
            String pkg = this.mContext.getPackageName();
            Log.v(TAG, pkg + " getting if the network data connection is enabled");
        }
        boolean dataConnectionEnabled = false;
        try {
            dataConnectionEnabled = sService.isDataConnectionEnabled();
            if (localLOGD) {
                String pkg2 = this.mContext.getPackageName();
                Log.v(TAG, pkg2 + " getting if the network data connection is enabled: " + dataConnectionEnabled);
            }
        } catch (RemoteException e) {
            Slog.w(TAG, "warning: no cm telephony manager service");
        }
        return dataConnectionEnabled;
    }

    public void setDataConnectionState(boolean state) {
        if (sService == null) {
            Log.w(TAG, "not connected to CMTelephonyManager");
            return;
        }
        if (localLOGD) {
            String pkg = this.mContext.getPackageName();
            Log.v(TAG, pkg + " setting the network data connection enabled: " + state);
        }
        try {
            sService.setDataConnectionState(state);
        } catch (RemoteException e) {
            Slog.w(TAG, "warning: no cm telephony manager service");
        }
    }

    public void setDataConnectionSelectedOnSub(int subId) {
        if (sService == null) {
            Log.w(TAG, "not connected to CMTelephonyManager");
            return;
        }
        if (localLOGD) {
            String pkg = this.mContext.getPackageName();
            Log.v(TAG, pkg + " setting the network data connection for SIM with subscription: " + subId);
        }
        try {
            sService.setDataConnectionSelectedOnSub(subId);
        } catch (RemoteException e) {
            Slog.w(TAG, "warning: no cm telephony manager service");
        }
    }

    public void setDefaultPhoneSub(int subId) {
        if (sService == null) {
            Log.w(TAG, "not connected to CMTelephonyManager");
            return;
        }
        if (localLOGD) {
            String pkg = this.mContext.getPackageName();
            Log.v(TAG, pkg + " setting the subscription used for phone calls as: " + subId);
        }
        try {
            sService.setDefaultPhoneSub(subId);
        } catch (RemoteException e) {
            Slog.w(TAG, "warning: no cm telephony manager service");
        }
    }

    public void setDefaultSmsSub(int subId) {
        if (sService == null) {
            Log.w(TAG, "not connected to CMTelephonyManager");
            return;
        }
        if (localLOGD) {
            String pkg = this.mContext.getPackageName();
            Log.v(TAG, pkg + " setting the subscription used for SMS as: " + subId);
        }
        try {
            sService.setDefaultSmsSub(subId);
        } catch (RemoteException e) {
            Slog.w(TAG, "warning: no cm telephony manager service");
        }
    }
}
