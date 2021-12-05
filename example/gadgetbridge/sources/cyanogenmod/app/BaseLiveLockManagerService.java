package cyanogenmod.app;

import android.app.AppGlobals;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import cyanogenmod.app.ILiveLockScreenManagerProvider;
import cyanogenmod.platform.Manifest;
import org.apache.commons.lang3.StringUtils;

public abstract class BaseLiveLockManagerService extends Service implements ILiveLockScreenManagerProvider {
    private static final String TAG = BaseLiveLockManagerService.class.getSimpleName();
    private final RemoteCallbackList<ILiveLockScreenChangeListener> mChangeListeners = new RemoteCallbackList<>();
    private final IBinder mService = new ILiveLockScreenManagerProvider.Stub() {
        public void enqueueLiveLockScreen(String pkg, int id, LiveLockScreenInfo llsInfo, int[] idReceived, int userId) throws RemoteException {
            BaseLiveLockManagerService.this.enforceAccessPermission();
            BaseLiveLockManagerService.this.enforceSamePackageOrSystem(pkg, llsInfo);
            BaseLiveLockManagerService.this.enqueueLiveLockScreen(pkg, id, llsInfo, idReceived, userId);
        }

        public void cancelLiveLockScreen(String pkg, int id, int userId) throws RemoteException {
            BaseLiveLockManagerService.this.enforceAccessPermission();
            BaseLiveLockManagerService.this.cancelLiveLockScreen(pkg, id, userId);
        }

        public LiveLockScreenInfo getCurrentLiveLockScreen() throws RemoteException {
            BaseLiveLockManagerService.this.enforceAccessPermission();
            return BaseLiveLockManagerService.this.getCurrentLiveLockScreen();
        }

        public void updateDefaultLiveLockScreen(LiveLockScreenInfo llsInfo) throws RemoteException {
            BaseLiveLockManagerService.this.enforcePrivateAccessPermission();
            BaseLiveLockManagerService.this.updateDefaultLiveLockScreen(llsInfo);
        }

        public boolean getLiveLockScreenEnabled() throws RemoteException {
            BaseLiveLockManagerService.this.enforceAccessPermission();
            return BaseLiveLockManagerService.this.getLiveLockScreenEnabled();
        }

        public boolean registerChangeListener(ILiveLockScreenChangeListener listener) throws RemoteException {
            BaseLiveLockManagerService.this.enforcePrivateAccessPermission();
            return BaseLiveLockManagerService.this.registerChangeListener(listener);
        }

        public boolean unregisterChangeListener(ILiveLockScreenChangeListener listener) throws RemoteException {
            BaseLiveLockManagerService.this.enforcePrivateAccessPermission();
            return BaseLiveLockManagerService.this.unregisterChangeListener(listener);
        }
    };

    public abstract void cancelLiveLockScreen(String str, int i, int i2) throws RemoteException;

    public abstract void enqueueLiveLockScreen(String str, int i, LiveLockScreenInfo liveLockScreenInfo, int[] iArr, int i2) throws RemoteException;

    public abstract LiveLockScreenInfo getCurrentLiveLockScreen() throws RemoteException;

    public abstract void updateDefaultLiveLockScreen(LiveLockScreenInfo liveLockScreenInfo) throws RemoteException;

    public final IBinder onBind(Intent intent) {
        return this.mService;
    }

    public final IBinder asBinder() {
        return this.mService;
    }

    public boolean getLiveLockScreenEnabled() throws RemoteException {
        return false;
    }

    public final boolean registerChangeListener(ILiveLockScreenChangeListener listener) throws RemoteException {
        return this.mChangeListeners.register(listener);
    }

    public final boolean unregisterChangeListener(ILiveLockScreenChangeListener listener) throws RemoteException {
        return this.mChangeListeners.unregister(listener);
    }

    /* access modifiers changed from: protected */
    public final void notifyChangeListeners(LiveLockScreenInfo llsInfo) {
        int N = this.mChangeListeners.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                this.mChangeListeners.getBroadcastItem(i).onLiveLockScreenChanged(llsInfo);
            } catch (RemoteException e) {
                Log.w(TAG, "Unable to notifiy change listener", e);
            }
        }
        this.mChangeListeners.finishBroadcast();
    }

    private final boolean hasPrivatePermissions() {
        return checkCallingPermission(Manifest.permission.LIVE_LOCK_SCREEN_MANAGER_ACCESS_PRIVATE) == 0;
    }

    /* access modifiers changed from: protected */
    public final void enforceAccessPermission() {
        if (!hasPrivatePermissions()) {
            enforceCallingPermission(Manifest.permission.LIVE_LOCK_SCREEN_MANAGER_ACCESS, (String) null);
        }
    }

    /* access modifiers changed from: protected */
    public final void enforcePrivateAccessPermission() {
        enforceCallingPermission(Manifest.permission.LIVE_LOCK_SCREEN_MANAGER_ACCESS_PRIVATE, (String) null);
    }

    /* access modifiers changed from: protected */
    public final void enforceSamePackageOrSystem(String pkg, LiveLockScreenInfo llsInfo) {
        if (!hasPrivatePermissions()) {
            if (llsInfo.component == null || llsInfo.component.getPackageName().equals(pkg)) {
                int uid = Binder.getCallingUid();
                try {
                    ApplicationInfo ai = AppGlobals.getPackageManager().getApplicationInfo(pkg, 0, UserHandle.getCallingUserId());
                    if (ai == null) {
                        throw new SecurityException("Unknown package " + pkg);
                    } else if (!UserHandle.isSameApp(ai.uid, uid)) {
                        throw new SecurityException("Calling uid " + uid + " gave package" + pkg + " which is owned by uid " + ai.uid);
                    }
                } catch (RemoteException re) {
                    throw new SecurityException("Unknown package " + pkg + StringUtils.f210LF + re);
                }
            } else {
                throw new SecurityException("Modifying Live lock screen from different packages not allowed.  Calling package: " + pkg + " LLS package: " + llsInfo.component.getPackageName());
            }
        }
    }
}
