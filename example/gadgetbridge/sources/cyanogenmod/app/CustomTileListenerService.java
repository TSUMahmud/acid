package cyanogenmod.app;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import cyanogenmod.app.ICMStatusBarManager;
import cyanogenmod.app.ICustomTileListener;
import org.cyanogenmod.internal.statusbar.IStatusBarCustomTileHolder;

public class CustomTileListenerService extends Service {
    public static final String SERVICE_INTERFACE = "cyanogenmod.app.CustomTileListenerService";
    /* access modifiers changed from: private */
    public final String TAG = (CustomTileListenerService.class.getSimpleName() + "[" + getClass().getSimpleName() + "]");
    private int mCurrentUser;
    private ICMStatusBarManager mStatusBarService;
    /* access modifiers changed from: private */
    public ICustomTileListenerWrapper mWrapper = null;

    public IBinder onBind(Intent intent) {
        if (this.mWrapper == null) {
            this.mWrapper = new ICustomTileListenerWrapper();
        }
        return this.mWrapper;
    }

    private final ICMStatusBarManager getStatusBarInterface() {
        if (this.mStatusBarService == null) {
            this.mStatusBarService = ICMStatusBarManager.Stub.asInterface(ServiceManager.getService(CMContextConstants.CM_STATUS_BAR_SERVICE));
        }
        return this.mStatusBarService;
    }

    public void registerAsSystemService(Context context, ComponentName componentName, int currentUser) throws RemoteException {
        if (!isBound()) {
            ICMStatusBarManager statusBarInterface = this.mStatusBarService;
            if (this.mStatusBarService != null) {
                this.mWrapper = new ICustomTileListenerWrapper();
                statusBarInterface.registerListener(this.mWrapper, componentName, currentUser);
                this.mCurrentUser = currentUser;
            }
        }
    }

    public void unregisterAsSystemService() throws RemoteException {
        if (isBound()) {
            this.mStatusBarService.unregisterListener(this.mWrapper, this.mCurrentUser);
            this.mWrapper = null;
            this.mStatusBarService = null;
        }
    }

    private class ICustomTileListenerWrapper extends ICustomTileListener.Stub {
        private ICustomTileListenerWrapper() {
        }

        public void onListenerConnected() {
            synchronized (CustomTileListenerService.this.mWrapper) {
                try {
                    CustomTileListenerService.this.onListenerConnected();
                } catch (Throwable t) {
                    Log.w(CustomTileListenerService.this.TAG, "Error running onListenerConnected", t);
                }
            }
        }

        public void onCustomTilePosted(IStatusBarCustomTileHolder sbcHolder) {
            try {
                StatusBarPanelCustomTile sbc = sbcHolder.get();
                synchronized (CustomTileListenerService.this.mWrapper) {
                    try {
                        CustomTileListenerService.this.onCustomTilePosted(sbc);
                    } catch (Throwable t) {
                        Log.w(CustomTileListenerService.this.TAG, "Error running onCustomTilePosted", t);
                    }
                }
            } catch (RemoteException e) {
                Log.w(CustomTileListenerService.this.TAG, "onCustomTilePosted: Error receiving StatusBarPanelCustomTile", e);
            }
        }

        public void onCustomTileRemoved(IStatusBarCustomTileHolder sbcHolder) {
            try {
                StatusBarPanelCustomTile sbc = sbcHolder.get();
                synchronized (CustomTileListenerService.this.mWrapper) {
                    try {
                        CustomTileListenerService.this.onCustomTileRemoved(sbc);
                    } catch (Throwable t) {
                        Log.w(CustomTileListenerService.this.TAG, "Error running onCustomTileRemoved", t);
                    }
                }
            } catch (RemoteException e) {
                Log.w(CustomTileListenerService.this.TAG, "onCustomTileRemoved: Error receiving StatusBarPanelCustomTile", e);
            }
        }
    }

    public void onCustomTilePosted(StatusBarPanelCustomTile sbc) {
    }

    public void onCustomTileRemoved(StatusBarPanelCustomTile sbc) {
    }

    public void onListenerConnected() {
    }

    public final void removeCustomTile(String pkg, String tag, int id) {
        if (isBound()) {
            try {
                this.mStatusBarService.removeCustomTileFromListener(this.mWrapper, pkg, tag, id);
            } catch (RemoteException ex) {
                Log.v(this.TAG, "Unable to contact cmstautusbar manager", ex);
            }
        }
    }

    private boolean isBound() {
        if (getStatusBarInterface() != null && this.mWrapper != null) {
            return true;
        }
        Log.w(this.TAG, "CustomTile listener service not yet bound.");
        return false;
    }
}
