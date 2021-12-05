package cyanogenmod.externalviews;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import cyanogenmod.externalviews.IExternalViewProviderFactory;
import cyanogenmod.externalviews.IKeyguardExternalViewCallbacks;
import cyanogenmod.externalviews.IKeyguardExternalViewProvider;
import java.util.LinkedList;

public class KeyguardExternalView extends View implements ViewTreeObserver.OnPreDrawListener, IBinder.DeathRecipient {
    public static final String CATEGORY_KEYGUARD_GRANT_PERMISSION = "org.cyanogenmod.intent.category.KEYGUARD_GRANT_PERMISSION";
    public static final String EXTRA_PERMISSION_LIST = "permissions_list";
    /* access modifiers changed from: private */
    public static final String TAG = KeyguardExternalView.class.getSimpleName();
    /* access modifiers changed from: private */
    public KeyguardExternalViewCallbacks mCallback;
    /* access modifiers changed from: private */
    public Context mContext;
    private final Point mDisplaySize;
    private final ExternalViewProperties mExternalViewProperties;
    /* access modifiers changed from: private */
    public volatile IKeyguardExternalViewProvider mExternalViewProvider;
    /* access modifiers changed from: private */
    public boolean mIsInteractive;
    /* access modifiers changed from: private */
    public final IKeyguardExternalViewCallbacks mKeyguardExternalViewCallbacks;
    private LinkedList<Runnable> mQueue;
    /* access modifiers changed from: private */
    public IBinder mService;
    /* access modifiers changed from: private */
    public ServiceConnection mServiceConnection;
    /* access modifiers changed from: private */
    public OnWindowAttachmentChangedListener mWindowAttachmentListener;

    public interface KeyguardExternalViewCallbacks {
        void providerDied();

        boolean requestDismiss();

        boolean requestDismissAndStartActivity(Intent intent);

        void slideLockscreenIn();
    }

    public interface OnWindowAttachmentChangedListener {
        void onAttachedToWindow();

        void onDetachedFromWindow();
    }

    public KeyguardExternalView(Context context, AttributeSet attrs) {
        this(context, attrs, (ComponentName) null);
    }

    public KeyguardExternalView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public KeyguardExternalView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs);
    }

    public KeyguardExternalView(Context context, AttributeSet attributeSet, ComponentName componentName) {
        super(context, attributeSet);
        this.mQueue = new LinkedList<>();
        this.mServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    IExternalViewProviderFactory factory = IExternalViewProviderFactory.Stub.asInterface(service);
                    if (factory != null) {
                        IKeyguardExternalViewProvider unused = KeyguardExternalView.this.mExternalViewProvider = IKeyguardExternalViewProvider.Stub.asInterface(factory.createExternalView((Bundle) null));
                        if (KeyguardExternalView.this.mExternalViewProvider != null) {
                            KeyguardExternalView.this.mExternalViewProvider.registerCallback(KeyguardExternalView.this.mKeyguardExternalViewCallbacks);
                            IBinder unused2 = KeyguardExternalView.this.mService = service;
                            KeyguardExternalView.this.mService.linkToDeath(KeyguardExternalView.this, 0);
                            KeyguardExternalView.this.executeQueue();
                        } else {
                            Log.e(KeyguardExternalView.TAG, "Unable to get external view provider");
                        }
                    } else {
                        Log.e(KeyguardExternalView.TAG, "Unable to get external view provider factory");
                    }
                } catch (RemoteException | SecurityException e) {
                    Log.e(KeyguardExternalView.TAG, "Unable to connect to service", e);
                }
                if (KeyguardExternalView.this.mService != service && service != null) {
                    KeyguardExternalView.this.mContext.unbindService(KeyguardExternalView.this.mServiceConnection);
                }
            }

            public void onServiceDisconnected(ComponentName name) {
                if (KeyguardExternalView.this.mExternalViewProvider != null) {
                    try {
                        KeyguardExternalView.this.mExternalViewProvider.unregisterCallback(KeyguardExternalView.this.mKeyguardExternalViewCallbacks);
                    } catch (RemoteException e) {
                    }
                    IKeyguardExternalViewProvider unused = KeyguardExternalView.this.mExternalViewProvider = null;
                }
                if (KeyguardExternalView.this.mService != null) {
                    KeyguardExternalView.this.mService.unlinkToDeath(KeyguardExternalView.this, 0);
                    IBinder unused2 = KeyguardExternalView.this.mService = null;
                }
            }
        };
        this.mKeyguardExternalViewCallbacks = new IKeyguardExternalViewCallbacks.Stub() {
            public boolean requestDismiss() throws RemoteException {
                if (KeyguardExternalView.this.mCallback != null) {
                    return KeyguardExternalView.this.mCallback.requestDismiss();
                }
                return false;
            }

            public boolean requestDismissAndStartActivity(Intent intent) throws RemoteException {
                if (KeyguardExternalView.this.mCallback != null) {
                    return KeyguardExternalView.this.mCallback.requestDismissAndStartActivity(intent);
                }
                return false;
            }

            public void collapseNotificationPanel() throws RemoteException {
            }

            public void setInteractivity(boolean isInteractive) {
                boolean unused = KeyguardExternalView.this.mIsInteractive = isInteractive;
            }

            public void onAttachedToWindow() {
                if (KeyguardExternalView.this.mWindowAttachmentListener != null) {
                    KeyguardExternalView.this.mWindowAttachmentListener.onAttachedToWindow();
                }
            }

            public void onDetachedFromWindow() {
                if (KeyguardExternalView.this.mWindowAttachmentListener != null) {
                    KeyguardExternalView.this.mWindowAttachmentListener.onDetachedFromWindow();
                }
            }

            public void slideLockscreenIn() {
                if (KeyguardExternalView.this.mCallback != null) {
                    KeyguardExternalView.this.mCallback.slideLockscreenIn();
                }
            }
        };
        this.mContext = getContext();
        this.mExternalViewProperties = new ExternalViewProperties(this, this.mContext);
        if (componentName != null) {
            this.mContext.bindService(new Intent().setComponent(componentName), this.mServiceConnection, 1);
        }
        this.mDisplaySize = new Point();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRealSize(this.mDisplaySize);
    }

    /* access modifiers changed from: private */
    public void executeQueue() {
        while (!this.mQueue.isEmpty()) {
            this.mQueue.pop().run();
        }
    }

    /* access modifiers changed from: protected */
    public void performAction(Runnable r) {
        if (this.mExternalViewProvider != null) {
            r.run();
        } else {
            this.mQueue.add(r);
        }
    }

    public boolean onPreDraw() {
        if (!this.mExternalViewProperties.hasChanged()) {
            return true;
        }
        int x = this.mExternalViewProperties.getX();
        int y = this.mExternalViewProperties.getY();
        int width = this.mDisplaySize.x - x;
        int height = this.mDisplaySize.y - y;
        boolean visible = this.mExternalViewProperties.isVisible();
        final Rect clipRect = new Rect(x, y, width + x, height + y);
        final int i = x;
        final int i2 = y;
        final int i3 = width;
        final int i4 = height;
        final boolean z = visible;
        performAction(new Runnable() {
            public void run() {
                try {
                    KeyguardExternalView.this.mExternalViewProvider.alterWindow(i, i2, i3, i4, z, clipRect);
                } catch (RemoteException e) {
                }
            }
        });
        return true;
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        performAction(new Runnable() {
            public void run() {
                try {
                    KeyguardExternalView.this.mExternalViewProvider.onDetach();
                } catch (RemoteException e) {
                }
            }
        });
    }

    public void onAttachedToWindow() {
        performAction(new Runnable() {
            public void run() {
                try {
                    KeyguardExternalView.this.mExternalViewProvider.onAttach((IBinder) null);
                } catch (RemoteException e) {
                }
            }
        });
    }

    public void binderDied() {
        KeyguardExternalViewCallbacks keyguardExternalViewCallbacks = this.mCallback;
        if (keyguardExternalViewCallbacks != null) {
            keyguardExternalViewCallbacks.providerDied();
        }
    }

    public void setProviderComponent(ComponentName componentName) {
        if (this.mExternalViewProvider != null) {
            this.mContext.unbindService(this.mServiceConnection);
        }
        if (componentName != null) {
            this.mContext.bindService(new Intent().setComponent(componentName), this.mServiceConnection, 1);
        }
    }

    public void onKeyguardShowing(final boolean screenOn) {
        performAction(new Runnable() {
            public void run() {
                try {
                    KeyguardExternalView.this.mExternalViewProvider.onKeyguardShowing(screenOn);
                } catch (RemoteException e) {
                }
            }
        });
    }

    public void onKeyguardDismissed() {
        performAction(new Runnable() {
            public void run() {
                try {
                    KeyguardExternalView.this.mExternalViewProvider.onKeyguardDismissed();
                } catch (RemoteException e) {
                }
            }
        });
    }

    public void onBouncerShowing(final boolean showing) {
        performAction(new Runnable() {
            public void run() {
                try {
                    KeyguardExternalView.this.mExternalViewProvider.onBouncerShowing(showing);
                } catch (RemoteException e) {
                }
            }
        });
    }

    public void onScreenTurnedOn() {
        performAction(new Runnable() {
            public void run() {
                try {
                    KeyguardExternalView.this.mExternalViewProvider.onScreenTurnedOn();
                } catch (RemoteException e) {
                }
            }
        });
    }

    public void onScreenTurnedOff() {
        performAction(new Runnable() {
            public void run() {
                try {
                    KeyguardExternalView.this.mExternalViewProvider.onScreenTurnedOff();
                } catch (RemoteException e) {
                }
            }
        });
    }

    public void onLockscreenSlideOffsetChanged(final float swipeProgress) {
        performAction(new Runnable() {
            public void run() {
                try {
                    KeyguardExternalView.this.mExternalViewProvider.onLockscreenSlideOffsetChanged(swipeProgress);
                } catch (RemoteException e) {
                }
            }
        });
    }

    public boolean isInteractive() {
        return this.mIsInteractive;
    }

    public void registerKeyguardExternalViewCallback(KeyguardExternalViewCallbacks callback) {
        this.mCallback = callback;
    }

    public void unregisterKeyguardExternalViewCallback(KeyguardExternalViewCallbacks callback) {
        if (this.mCallback == callback) {
            this.mCallback = null;
            return;
        }
        throw new IllegalArgumentException("Callback not registered");
    }

    public void registerOnWindowAttachmentChangedListener(OnWindowAttachmentChangedListener listener) {
        this.mWindowAttachmentListener = listener;
    }

    public void unregisterOnWindowAttachmentChangedListener(OnWindowAttachmentChangedListener listener) {
        if (this.mWindowAttachmentListener == listener) {
            this.mWindowAttachmentListener = null;
            return;
        }
        throw new IllegalArgumentException("Callback not registered");
    }
}
