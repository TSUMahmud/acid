package cyanogenmod.externalviews;

import android.app.Service;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.android.internal.policy.PhoneWindow;
import cyanogenmod.externalviews.IExternalViewProvider;
import cyanogenmod.externalviews.IExternalViewProviderFactory;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public abstract class ExternalViewProviderService extends Service {
    private static final boolean DEBUG = false;
    private static final String TAG = "ExternalViewProvider";
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

    public final IBinder onBind(Intent intent) {
        return new IExternalViewProviderFactory.Stub() {
            public IBinder createExternalView(final Bundle options) {
                FutureTask<IBinder> c = new FutureTask<>(new Callable<IBinder>() {
                    public IBinder call() throws Exception {
                        return ExternalViewProviderService.this.createExternalView(options).mImpl;
                    }
                });
                ExternalViewProviderService.this.mHandler.post(c);
                try {
                    return c.get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(ExternalViewProviderService.TAG, "error: ", e);
                    return null;
                }
            }
        };
    }

    protected abstract class Provider {
        public static final int DEFAULT_WINDOW_FLAGS = 800;
        public static final int DEFAULT_WINDOW_TYPE = 2002;
        /* access modifiers changed from: private */
        public final ProviderImpl mImpl = new ProviderImpl(this);
        private final Bundle mOptions;

        /* access modifiers changed from: protected */
        public abstract View onCreateView();

        private final class ProviderImpl extends IExternalViewProvider.Stub {
            /* access modifiers changed from: private */
            public boolean mAskedShow = false;
            /* access modifiers changed from: private */
            public final WindowManager.LayoutParams mParams;
            /* access modifiers changed from: private */
            public boolean mShouldShow = true;
            /* access modifiers changed from: private */
            public final Window mWindow;

            public ProviderImpl(Provider provider) {
                this.mWindow = new PhoneWindow(ExternalViewProviderService.this);
                ((ViewGroup) this.mWindow.getDecorView()).addView(Provider.this.onCreateView());
                this.mParams = new WindowManager.LayoutParams();
                this.mParams.type = provider.getWindowType();
                this.mParams.flags = provider.getWindowFlags();
                WindowManager.LayoutParams layoutParams = this.mParams;
                layoutParams.gravity = 51;
                layoutParams.format = -2;
            }

            public void onAttach(IBinder windowToken) throws RemoteException {
                ExternalViewProviderService.this.mHandler.post(new Runnable() {
                    public void run() {
                        ExternalViewProviderService.this.mWindowManager.addView(ProviderImpl.this.mWindow.getDecorView(), ProviderImpl.this.mParams);
                        Provider.this.onAttach();
                    }
                });
            }

            public void onStart() throws RemoteException {
                ExternalViewProviderService.this.mHandler.post(new Runnable() {
                    public void run() {
                        Provider.this.onStart();
                    }
                });
            }

            public void onResume() throws RemoteException {
                ExternalViewProviderService.this.mHandler.post(new Runnable() {
                    public void run() {
                        boolean unused = ProviderImpl.this.mShouldShow = true;
                        ProviderImpl.this.updateVisibility();
                        Provider.this.onResume();
                    }
                });
            }

            public void onPause() throws RemoteException {
                ExternalViewProviderService.this.mHandler.post(new Runnable() {
                    public void run() {
                        boolean unused = ProviderImpl.this.mShouldShow = false;
                        ProviderImpl.this.updateVisibility();
                        Provider.this.onPause();
                    }
                });
            }

            public void onStop() throws RemoteException {
                ExternalViewProviderService.this.mHandler.post(new Runnable() {
                    public void run() {
                        Provider.this.onStop();
                    }
                });
            }

            public void onDetach() throws RemoteException {
                ExternalViewProviderService.this.mHandler.post(new Runnable() {
                    public void run() {
                        ExternalViewProviderService.this.mWindowManager.removeView(ProviderImpl.this.mWindow.getDecorView());
                        Provider.this.onDetach();
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
                ExternalViewProviderService.this.mHandler.post(new Runnable() {
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
                            ExternalViewProviderService.this.mWindowManager.updateViewLayout(ProviderImpl.this.mWindow.getDecorView(), ProviderImpl.this.mParams);
                        }
                    }
                });
            }

            /* access modifiers changed from: private */
            public void updateVisibility() {
                this.mWindow.getDecorView().setVisibility((!this.mShouldShow || !this.mAskedShow) ? 8 : 0);
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
        public void onStart() {
        }

        /* access modifiers changed from: protected */
        public void onResume() {
        }

        /* access modifiers changed from: protected */
        public void onPause() {
        }

        /* access modifiers changed from: protected */
        public void onStop() {
        }

        /* access modifiers changed from: protected */
        public void onDetach() {
        }

        /* access modifiers changed from: package-private */
        public int getWindowType() {
            return DEFAULT_WINDOW_TYPE;
        }

        /* access modifiers changed from: package-private */
        public int getWindowFlags() {
            return DEFAULT_WINDOW_FLAGS;
        }
    }
}
