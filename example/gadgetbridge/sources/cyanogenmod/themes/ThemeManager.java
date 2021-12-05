package cyanogenmod.themes;

import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArraySet;
import android.util.Log;
import cyanogenmod.app.CMContextConstants;
import cyanogenmod.themes.IThemeChangeListener;
import cyanogenmod.themes.IThemeProcessingListener;
import cyanogenmod.themes.IThemeService;
import cyanogenmod.themes.ThemeChangeRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ThemeManager {
    /* access modifiers changed from: private */
    public static final String TAG = ThemeManager.class.getName();
    /* access modifiers changed from: private */
    public static Handler mHandler;
    private static ThemeManager sInstance;
    private static IThemeService sService;
    /* access modifiers changed from: private */
    public Set<ThemeChangeListener> mChangeListeners = new ArraySet();
    /* access modifiers changed from: private */
    public Set<ThemeProcessingListener> mProcessingListeners = new ArraySet();
    private final IThemeChangeListener mThemeChangeListener = new IThemeChangeListener.Stub() {
        public void onProgress(final int progress) throws RemoteException {
            ThemeManager.mHandler.post(new Runnable() {
                public void run() {
                    synchronized (ThemeManager.this.mChangeListeners) {
                        List<ThemeChangeListener> listenersToRemove = new ArrayList<>();
                        for (ThemeChangeListener listener : ThemeManager.this.mChangeListeners) {
                            try {
                                listener.onProgress(progress);
                            } catch (Throwable e) {
                                Log.w(ThemeManager.TAG, "Unable to update theme change progress", e);
                                listenersToRemove.add(listener);
                            }
                        }
                        if (listenersToRemove.size() > 0) {
                            for (ThemeChangeListener listener2 : listenersToRemove) {
                                ThemeManager.this.mChangeListeners.remove(listener2);
                            }
                        }
                    }
                }
            });
        }

        public void onFinish(final boolean isSuccess) throws RemoteException {
            ThemeManager.mHandler.post(new Runnable() {
                public void run() {
                    synchronized (ThemeManager.this.mChangeListeners) {
                        List<ThemeChangeListener> listenersToRemove = new ArrayList<>();
                        for (ThemeChangeListener listener : ThemeManager.this.mChangeListeners) {
                            try {
                                listener.onFinish(isSuccess);
                            } catch (Throwable e) {
                                Log.w(ThemeManager.TAG, "Unable to update theme change listener", e);
                                listenersToRemove.add(listener);
                            }
                        }
                        if (listenersToRemove.size() > 0) {
                            for (ThemeChangeListener listener2 : listenersToRemove) {
                                ThemeManager.this.mChangeListeners.remove(listener2);
                            }
                        }
                    }
                }
            });
        }
    };
    private final IThemeProcessingListener mThemeProcessingListener = new IThemeProcessingListener.Stub() {
        public void onFinishedProcessing(final String pkgName) throws RemoteException {
            ThemeManager.mHandler.post(new Runnable() {
                public void run() {
                    synchronized (ThemeManager.this.mProcessingListeners) {
                        List<ThemeProcessingListener> listenersToRemove = new ArrayList<>();
                        for (ThemeProcessingListener listener : ThemeManager.this.mProcessingListeners) {
                            try {
                                listener.onFinishedProcessing(pkgName);
                            } catch (Throwable e) {
                                Log.w(ThemeManager.TAG, "Unable to update theme change progress", e);
                                listenersToRemove.add(listener);
                            }
                        }
                        if (listenersToRemove.size() > 0) {
                            for (ThemeProcessingListener listener2 : listenersToRemove) {
                                ThemeManager.this.mProcessingListeners.remove(listener2);
                            }
                        }
                    }
                }
            });
        }
    };

    public interface ThemeChangeListener {
        void onFinish(boolean z);

        void onProgress(int i);
    }

    public interface ThemeProcessingListener {
        void onFinishedProcessing(String str);
    }

    private ThemeManager(Context context) {
        sService = getService();
        if (context.getPackageManager().hasSystemFeature(CMContextConstants.Features.THEMES) && sService == null) {
            Log.wtf(TAG, "Unable to get ThemeManagerService. The service either crashed, was not started, or the interface has been called to early in SystemServer init");
        }
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static ThemeManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ThemeManager(context);
        }
        return sInstance;
    }

    public static IThemeService getService() {
        IThemeService iThemeService = sService;
        if (iThemeService != null) {
            return iThemeService;
        }
        IBinder b = ServiceManager.getService(CMContextConstants.CM_THEME_SERVICE);
        if (b == null) {
            return null;
        }
        sService = IThemeService.Stub.asInterface(b);
        return sService;
    }

    public void addClient(ThemeChangeListener listener) {
        registerThemeChangeListener(listener);
    }

    public void removeClient(ThemeChangeListener listener) {
        unregisterThemeChangeListener(listener);
    }

    public void onClientPaused(ThemeChangeListener listener) {
        unregisterThemeChangeListener(listener);
    }

    public void onClientResumed(ThemeChangeListener listener) {
        registerThemeChangeListener(listener);
    }

    public void onClientDestroyed(ThemeChangeListener listener) {
        unregisterThemeChangeListener(listener);
    }

    public void registerThemeChangeListener(ThemeChangeListener listener) {
        synchronized (this.mChangeListeners) {
            if (!this.mChangeListeners.contains(listener)) {
                if (this.mChangeListeners.size() == 0) {
                    try {
                        sService.requestThemeChangeUpdates(this.mThemeChangeListener);
                    } catch (RemoteException e) {
                        Log.w(TAG, "Unable to register listener", e);
                    }
                }
                this.mChangeListeners.add(listener);
            } else {
                throw new IllegalArgumentException("Listener already registered");
            }
        }
    }

    public void unregisterThemeChangeListener(ThemeChangeListener listener) {
        synchronized (this.mChangeListeners) {
            this.mChangeListeners.remove(listener);
            if (this.mChangeListeners.size() == 0) {
                try {
                    sService.removeUpdates(this.mThemeChangeListener);
                } catch (RemoteException e) {
                    Log.w(TAG, "Unable to unregister listener", e);
                }
            }
        }
    }

    public void registerProcessingListener(ThemeProcessingListener listener) {
        synchronized (this.mProcessingListeners) {
            if (!this.mProcessingListeners.contains(listener)) {
                if (this.mProcessingListeners.size() == 0) {
                    try {
                        sService.registerThemeProcessingListener(this.mThemeProcessingListener);
                    } catch (RemoteException e) {
                        Log.w(TAG, "Unable to register listener", e);
                    }
                }
                this.mProcessingListeners.add(listener);
            } else {
                throw new IllegalArgumentException("Listener already registered");
            }
        }
    }

    public void unregisterProcessingListener(ThemeProcessingListener listener) {
        synchronized (this.mProcessingListeners) {
            this.mProcessingListeners.remove(listener);
            if (this.mProcessingListeners.size() == 0) {
                try {
                    sService.unregisterThemeProcessingListener(this.mThemeProcessingListener);
                } catch (RemoteException e) {
                    Log.w(TAG, "Unable to unregister listener", e);
                }
            }
        }
    }

    public void requestThemeChange(String pkgName, List<String> components) {
        requestThemeChange(pkgName, components, true);
    }

    public void requestThemeChange(String pkgName, List<String> components, boolean removePerAppThemes) {
        Map<String, String> componentMap = new HashMap<>(components.size());
        for (String component : components) {
            componentMap.put(component, pkgName);
        }
        requestThemeChange(componentMap, removePerAppThemes);
    }

    public void requestThemeChange(Map<String, String> componentMap) {
        requestThemeChange(componentMap, true);
    }

    public void requestThemeChange(Map<String, String> componentMap, boolean removePerAppThemes) {
        ThemeChangeRequest.Builder builder = new ThemeChangeRequest.Builder();
        for (String component : componentMap.keySet()) {
            builder.setComponent(component, componentMap.get(component));
        }
        requestThemeChange(builder.build(), removePerAppThemes);
    }

    public void requestThemeChange(ThemeChangeRequest request, boolean removePerAppThemes) {
        try {
            sService.requestThemeChange(request, removePerAppThemes);
        } catch (RemoteException e) {
            logThemeServiceException(e);
        }
    }

    public void applyDefaultTheme() {
        try {
            sService.applyDefaultTheme();
        } catch (RemoteException e) {
            logThemeServiceException(e);
        }
    }

    public boolean isThemeApplying() {
        try {
            return sService.isThemeApplying();
        } catch (RemoteException e) {
            logThemeServiceException(e);
            return false;
        }
    }

    public boolean isThemeBeingProcessed(String themePkgName) {
        try {
            return sService.isThemeBeingProcessed(themePkgName);
        } catch (RemoteException e) {
            logThemeServiceException(e);
            return false;
        }
    }

    public int getProgress() {
        try {
            return sService.getProgress();
        } catch (RemoteException e) {
            logThemeServiceException(e);
            return -1;
        }
    }

    public boolean processThemeResources(String themePkgName) {
        try {
            return sService.processThemeResources(themePkgName);
        } catch (RemoteException e) {
            logThemeServiceException(e);
            return false;
        }
    }

    public long getLastThemeChangeTime() {
        try {
            return sService.getLastThemeChangeTime();
        } catch (RemoteException e) {
            logThemeServiceException(e);
            return 0;
        }
    }

    public ThemeChangeRequest.RequestType getLastThemeChangeRequestType() {
        try {
            int type = sService.getLastThemeChangeRequestType();
            if (type < 0 || type >= ThemeChangeRequest.RequestType.values().length) {
                return null;
            }
            return ThemeChangeRequest.RequestType.values()[type];
        } catch (RemoteException e) {
            logThemeServiceException(e);
            return null;
        }
    }

    private void logThemeServiceException(Exception e) {
        Log.w(TAG, "Unable to access ThemeService", e);
    }
}
