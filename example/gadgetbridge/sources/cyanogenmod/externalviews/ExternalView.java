package cyanogenmod.externalviews;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import cyanogenmod.externalviews.IExternalViewProvider;
import cyanogenmod.externalviews.IExternalViewProviderFactory;
import java.util.LinkedList;

public class ExternalView extends View implements Application.ActivityLifecycleCallbacks, ViewTreeObserver.OnPreDrawListener {
    protected Context mContext;
    protected final ExternalViewProperties mExternalViewProperties;
    protected volatile IExternalViewProvider mExternalViewProvider;
    private LinkedList<Runnable> mQueue;
    private ServiceConnection mServiceConnection;

    public ExternalView(Context context, AttributeSet attrs) {
        this(context, attrs, (ComponentName) null);
    }

    public ExternalView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public ExternalView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs);
    }

    public ExternalView(Context context, AttributeSet attributeSet, ComponentName componentName) {
        super(context, attributeSet);
        this.mQueue = new LinkedList<>();
        this.mServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    ExternalView.this.mExternalViewProvider = IExternalViewProvider.Stub.asInterface(IExternalViewProviderFactory.Stub.asInterface(service).createExternalView((Bundle) null));
                    ExternalView.this.executeQueue();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            public void onServiceDisconnected(ComponentName name) {
                ExternalView.this.mExternalViewProvider = null;
            }
        };
        this.mContext = getContext();
        this.mExternalViewProperties = new ExternalViewProperties(this, this.mContext);
        Context context2 = this.mContext;
        (context2 instanceof Activity ? ((Activity) context2).getApplication() : (Application) context2).registerActivityLifecycleCallbacks(this);
        if (componentName != null) {
            this.mContext.bindService(new Intent().setComponent(componentName), this.mServiceConnection, 1);
        }
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
        long currentTimeMillis = System.currentTimeMillis();
        if (!this.mExternalViewProperties.hasChanged()) {
            return true;
        }
        int x = this.mExternalViewProperties.getX();
        int y = this.mExternalViewProperties.getY();
        int width = this.mExternalViewProperties.getWidth();
        int height = this.mExternalViewProperties.getHeight();
        final int i = x;
        final int i2 = y;
        final int i3 = width;
        final int i4 = height;
        final boolean isVisible = this.mExternalViewProperties.isVisible();
        C07802 r11 = r0;
        final Rect hitRect = this.mExternalViewProperties.getHitRect();
        C07802 r0 = new Runnable() {
            public void run() {
                try {
                    ExternalView.this.mExternalViewProvider.alterWindow(i, i2, i3, i4, isVisible, hitRect);
                } catch (RemoteException e) {
                }
            }
        };
        performAction(r11);
        return true;
    }

    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    public void onActivityStarted(Activity activity) {
        performAction(new Runnable() {
            public void run() {
                try {
                    ExternalView.this.mExternalViewProvider.onStart();
                } catch (RemoteException e) {
                }
            }
        });
    }

    public void onActivityResumed(Activity activity) {
        performAction(new Runnable() {
            public void run() {
                try {
                    ExternalView.this.mExternalViewProvider.onResume();
                } catch (RemoteException e) {
                }
                ExternalView.this.getViewTreeObserver().addOnPreDrawListener(ExternalView.this);
            }
        });
    }

    public void onActivityPaused(Activity activity) {
        performAction(new Runnable() {
            public void run() {
                try {
                    ExternalView.this.mExternalViewProvider.onPause();
                } catch (RemoteException e) {
                }
                ExternalView.this.getViewTreeObserver().removeOnPreDrawListener(ExternalView.this);
            }
        });
    }

    public void onActivityStopped(Activity activity) {
        performAction(new Runnable() {
            public void run() {
                try {
                    ExternalView.this.mExternalViewProvider.onStop();
                } catch (RemoteException e) {
                }
            }
        });
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public void onActivityDestroyed(Activity activity) {
        this.mExternalViewProvider = null;
        this.mContext.unbindService(this.mServiceConnection);
    }

    public void onDetachedFromWindow() {
        performAction(new Runnable() {
            public void run() {
                try {
                    ExternalView.this.mExternalViewProvider.onDetach();
                } catch (RemoteException e) {
                }
            }
        });
    }

    public void onAttachedToWindow() {
        performAction(new Runnable() {
            public void run() {
                try {
                    ExternalView.this.mExternalViewProvider.onAttach((IBinder) null);
                } catch (RemoteException e) {
                }
            }
        });
    }

    public void setProviderComponent(ComponentName componentName) {
        if (this.mExternalViewProvider != null) {
            this.mContext.unbindService(this.mServiceConnection);
        }
        if (componentName != null) {
            this.mContext.bindService(new Intent().setComponent(componentName), this.mServiceConnection, 1);
        }
    }
}
