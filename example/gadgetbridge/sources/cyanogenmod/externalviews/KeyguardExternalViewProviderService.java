package cyanogenmod.externalviews;

import android.app.Service;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.policy.PhoneWindow;
import cyanogenmod.externalviews.IExternalViewProviderFactory;
import cyanogenmod.externalviews.IKeyguardExternalViewProvider;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public abstract class KeyguardExternalViewProviderService extends Service {
    private static final boolean DEBUG = false;
    public static final String META_DATA = "cyanogenmod.externalviews.keyguard";
    public static final String SERVICE_INTERFACE = "cyanogenmod.externalviews.KeyguardExternalViewProviderService";
    /* access modifiers changed from: private */
    public static final String TAG = KeyguardExternalViewProviderService.class.getSimpleName();
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    /* access modifiers changed from: private */
    public WindowManager mWindowManager;

    /* access modifiers changed from: protected */
    public abstract Provider createExternalView(Bundle bundle);

    public void onCreate() {
        super.onCreate();
        this.mWindowManager = (WindowManager) getSystemService("window");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return 2;
    }

    public final IBinder onBind(Intent intent) {
        return new IExternalViewProviderFactory.Stub() {
            public IBinder createExternalView(final Bundle options) {
                FutureTask<IBinder> c = new FutureTask<>(new Callable<IBinder>() {
                    public IBinder call() throws Exception {
                        return KeyguardExternalViewProviderService.this.createExternalView(options).mImpl;
                    }
                });
                KeyguardExternalViewProviderService.this.mHandler.post(c);
                try {
                    return c.get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(KeyguardExternalViewProviderService.TAG, "error: ", e);
                    return null;
                }
            }
        };
    }

    protected abstract class Provider {
        /* access modifiers changed from: private */
        public final ProviderImpl mImpl = new ProviderImpl(this);
        private final Bundle mOptions;

        /* access modifiers changed from: protected */
        public abstract void onBouncerShowing(boolean z);

        /* access modifiers changed from: protected */
        public abstract View onCreateView();

        /* access modifiers changed from: protected */
        public abstract void onKeyguardDismissed();

        /* access modifiers changed from: protected */
        public abstract void onKeyguardShowing(boolean z);

        /* access modifiers changed from: protected */
        public abstract void onScreenTurnedOff();

        /* access modifiers changed from: protected */
        public abstract void onScreenTurnedOn();

        private final class ProviderImpl extends IKeyguardExternalViewProvider.Stub implements Window.Callback {
            /* access modifiers changed from: private */
            public boolean mAskedShow = false;
            private final RemoteCallbackList<IKeyguardExternalViewCallbacks> mCallbacks = new RemoteCallbackList<>();
            /* access modifiers changed from: private */
            public final WindowManager.LayoutParams mParams;
            private boolean mShouldShow = true;
            /* access modifiers changed from: private */
            public final Window mWindow;

            public ProviderImpl(Provider provider) {
                this.mWindow = new PhoneWindow(KeyguardExternalViewProviderService.this);
                this.mWindow.setCallback(this);
                ((ViewGroup) this.mWindow.getDecorView()).addView(Provider.this.onCreateView());
                this.mParams = new WindowManager.LayoutParams();
                this.mParams.type = provider.getWindowType();
                this.mParams.flags = provider.getWindowFlags();
                WindowManager.LayoutParams layoutParams = this.mParams;
                layoutParams.gravity = 51;
                layoutParams.format = -2;
            }

            public void onAttach(IBinder windowToken) throws RemoteException {
                KeyguardExternalViewProviderService.this.mHandler.post(new Runnable() {
                    public void run() {
                        KeyguardExternalViewProviderService.this.mWindowManager.addView(ProviderImpl.this.mWindow.getDecorView(), ProviderImpl.this.mParams);
                        Provider.this.onAttach();
                    }
                });
            }

            public void onDetach() throws RemoteException {
                KeyguardExternalViewProviderService.this.mHandler.post(new Runnable() {
                    public void run() {
                        KeyguardExternalViewProviderService.this.mWindowManager.removeView(ProviderImpl.this.mWindow.getDecorView());
                        Provider.this.onDetach();
                    }
                });
            }

            public void onKeyguardShowing(final boolean screenOn) throws RemoteException {
                KeyguardExternalViewProviderService.this.mHandler.post(new Runnable() {
                    public void run() {
                        Provider.this.onKeyguardShowing(screenOn);
                    }
                });
            }

            public void onKeyguardDismissed() throws RemoteException {
                KeyguardExternalViewProviderService.this.mHandler.post(new Runnable() {
                    public void run() {
                        Provider.this.onKeyguardDismissed();
                    }
                });
            }

            public void onBouncerShowing(final boolean showing) throws RemoteException {
                KeyguardExternalViewProviderService.this.mHandler.post(new Runnable() {
                    public void run() {
                        Provider.this.onBouncerShowing(showing);
                    }
                });
            }

            public void onScreenTurnedOn() throws RemoteException {
                KeyguardExternalViewProviderService.this.mHandler.post(new Runnable() {
                    public void run() {
                        Provider.this.onScreenTurnedOn();
                    }
                });
            }

            public void onScreenTurnedOff() throws RemoteException {
                KeyguardExternalViewProviderService.this.mHandler.post(new Runnable() {
                    public void run() {
                        Provider.this.onScreenTurnedOff();
                    }
                });
            }

            public void onLockscreenSlideOffsetChanged(final float swipeProgress) throws RemoteException {
                KeyguardExternalViewProviderService.this.mHandler.post(new Runnable() {
                    public void run() {
                        Provider.this.onLockscreenSlideOffsetChanged(swipeProgress);
                    }
                });
            }

            public void alterWindow(int x, int y, int width, int height, boolean visible, Rect clipRect) {
                final int i = x;
                final int i2 = y;
                final int i3 = width;
                final int i4 = height;
                final boolean z = visible;
                final Rect rect = clipRect;
                KeyguardExternalViewProviderService.this.mHandler.post(new Runnable() {
                    public void run() {
                        ProviderImpl.this.mParams.x = i;
                        ProviderImpl.this.mParams.y = i2;
                        ProviderImpl.this.mParams.width = i3;
                        ProviderImpl.this.mParams.height = i4;
                        boolean unused = ProviderImpl.this.mAskedShow = z;
                        ProviderImpl.this.updateVisibility();
                        View decorView = ProviderImpl.this.mWindow.getDecorView();
                        if (decorView.getVisibility() == 0) {
                            decorView.setClipBounds(rect);
                        }
                        if (ProviderImpl.this.mWindow.getDecorView().getVisibility() != 8) {
                            KeyguardExternalViewProviderService.this.mWindowManager.updateViewLayout(ProviderImpl.this.mWindow.getDecorView(), ProviderImpl.this.mParams);
                        }
                    }
                });
            }

            public void registerCallback(IKeyguardExternalViewCallbacks callback) {
                this.mCallbacks.register(callback);
            }

            public void unregisterCallback(IKeyguardExternalViewCallbacks callback) {
                this.mCallbacks.unregister(callback);
            }

            /* access modifiers changed from: private */
            public void updateVisibility() {
                this.mWindow.getDecorView().setVisibility((!this.mShouldShow || !this.mAskedShow) ? 8 : 0);
            }

            /* access modifiers changed from: protected */
            public final boolean requestDismiss() {
                boolean ret = true;
                int N = this.mCallbacks.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        ret &= this.mCallbacks.getBroadcastItem(0).requestDismiss();
                    } catch (RemoteException e) {
                    }
                }
                this.mCallbacks.finishBroadcast();
                return ret;
            }

            /* access modifiers changed from: protected */
            public final boolean requestDismissAndStartActivity(Intent intent) {
                boolean ret = true;
                int N = this.mCallbacks.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        ret &= this.mCallbacks.getBroadcastItem(0).requestDismissAndStartActivity(intent);
                    } catch (RemoteException e) {
                    }
                }
                this.mCallbacks.finishBroadcast();
                return ret;
            }

            /* access modifiers changed from: protected */
            public final void collapseNotificationPanel() {
                int N = this.mCallbacks.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        this.mCallbacks.getBroadcastItem(0).collapseNotificationPanel();
                    } catch (RemoteException e) {
                    }
                }
                this.mCallbacks.finishBroadcast();
            }

            /* access modifiers changed from: protected */
            public final void setInteractivity(boolean isInteractive) {
                int N = this.mCallbacks.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        this.mCallbacks.getBroadcastItem(0).setInteractivity(isInteractive);
                    } catch (RemoteException e) {
                    }
                }
                this.mCallbacks.finishBroadcast();
            }

            public void slideLockscreenIn() {
                int N = this.mCallbacks.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        this.mCallbacks.getBroadcastItem(0).slideLockscreenIn();
                    } catch (RemoteException e) {
                    }
                }
                this.mCallbacks.finishBroadcast();
            }

            public boolean dispatchKeyEvent(KeyEvent event) {
                return this.mWindow.superDispatchKeyEvent(event);
            }

            public boolean dispatchKeyShortcutEvent(KeyEvent event) {
                return this.mWindow.superDispatchKeyShortcutEvent(event);
            }

            public boolean dispatchTouchEvent(MotionEvent event) {
                return this.mWindow.superDispatchTouchEvent(event);
            }

            public boolean dispatchTrackballEvent(MotionEvent event) {
                return this.mWindow.superDispatchTrackballEvent(event);
            }

            public boolean dispatchGenericMotionEvent(MotionEvent event) {
                return this.mWindow.superDispatchGenericMotionEvent(event);
            }

            public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
                return false;
            }

            public View onCreatePanelView(int featureId) {
                return null;
            }

            public boolean onCreatePanelMenu(int featureId, Menu menu) {
                return false;
            }

            public boolean onPreparePanel(int featureId, View view, Menu menu) {
                return false;
            }

            public boolean onMenuOpened(int featureId, Menu menu) {
                return false;
            }

            public boolean onMenuItemSelected(int featureId, MenuItem item) {
                return false;
            }

            public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
            }

            public void onContentChanged() {
            }

            public void onWindowFocusChanged(boolean hasFocus) {
            }

            public void onAttachedToWindow() {
                int N = this.mCallbacks.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        this.mCallbacks.getBroadcastItem(0).onAttachedToWindow();
                    } catch (RemoteException e) {
                    }
                }
                this.mCallbacks.finishBroadcast();
            }

            public void onDetachedFromWindow() {
                int N = this.mCallbacks.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        this.mCallbacks.getBroadcastItem(0).onDetachedFromWindow();
                    } catch (RemoteException e) {
                    }
                }
                this.mCallbacks.finishBroadcast();
            }

            public void onPanelClosed(int featureId, Menu menu) {
            }

            public boolean onSearchRequested() {
                return false;
            }

            public boolean onSearchRequested(SearchEvent searchEvent) {
                return false;
            }

            public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
                return null;
            }

            public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
                return null;
            }

            public void onActionModeStarted(ActionMode mode) {
            }

            public void onActionModeFinished(ActionMode mode) {
            }
        }

        protected Provider(Bundle options) {
            this.mOptions = options;
        }

        /* access modifiers changed from: protected */
        public Bundle getOptions() {
            return this.mOptions;
        }

        /* access modifiers changed from: protected */
        public void onAttach() {
        }

        /* access modifiers changed from: protected */
        public void onDetach() {
        }

        /* access modifiers changed from: protected */
        public void onLockscreenSlideOffsetChanged(float swipeProgress) {
        }

        /* access modifiers changed from: protected */
        public final boolean requestDismiss() {
            return this.mImpl.requestDismiss();
        }

        /* access modifiers changed from: protected */
        public final boolean requestDismissAndStartActivity(Intent intent) {
            return this.mImpl.requestDismissAndStartActivity(intent);
        }

        /* access modifiers changed from: protected */
        @Deprecated
        public final void collapseNotificationPanel() {
        }

        /* access modifiers changed from: protected */
        public final void setInteractivity(boolean isInteractive) {
            this.mImpl.setInteractivity(isInteractive);
        }

        /* access modifiers changed from: protected */
        public final void slideLockscreenIn() {
            this.mImpl.slideLockscreenIn();
        }

        /* access modifiers changed from: package-private */
        public final int getWindowType() {
            return 2998;
        }

        /* access modifiers changed from: package-private */
        public final int getWindowFlags() {
            return 526112;
        }
    }
}
