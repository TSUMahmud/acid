package cyanogenmod.app.suggest;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import cyanogenmod.app.CMContextConstants;
import cyanogenmod.app.suggest.IAppSuggestManager;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class AppSuggestManager {
    private static final boolean DEBUG = true;
    private static final String TAG = AppSuggestManager.class.getSimpleName();
    private static IAppSuggestManager sImpl;
    private static AppSuggestManager sInstance;
    private Context mContext;

    public static synchronized AppSuggestManager getInstance(Context context) {
        synchronized (AppSuggestManager.class) {
            if (sInstance != null) {
                AppSuggestManager appSuggestManager = sInstance;
                return appSuggestManager;
            }
            Context context2 = context.getApplicationContext() != null ? context.getApplicationContext() : context;
            sInstance = new AppSuggestManager(context2);
            if (context2.getPackageManager().hasSystemFeature(CMContextConstants.Features.APP_SUGGEST)) {
                if (sImpl == null) {
                    throw new RuntimeException("Unable to get AppSuggestManagerService. The service either crashed, was not started, or the interface has been called to early in SystemServer init");
                }
            }
            AppSuggestManager appSuggestManager2 = sInstance;
            return appSuggestManager2;
        }
    }

    private AppSuggestManager(Context context) {
        this.mContext = context.getApplicationContext();
        sImpl = getService();
    }

    public static synchronized IAppSuggestManager getService() {
        IAppSuggestManager iAppSuggestManager;
        synchronized (AppSuggestManager.class) {
            if (sImpl == null) {
                IBinder b = ServiceManager.getService(CMContextConstants.CM_APP_SUGGEST_SERVICE);
                if (b != null) {
                    sImpl = IAppSuggestManager.Stub.asInterface(b);
                } else {
                    Log.e(TAG, "Unable to find implementation for app suggest service");
                }
            }
            iAppSuggestManager = sImpl;
        }
        return iAppSuggestManager;
    }

    public boolean handles(Intent intent) {
        IAppSuggestManager mgr = getService();
        if (mgr == null) {
            return false;
        }
        try {
            return mgr.handles(intent);
        } catch (RemoteException e) {
            return false;
        }
    }

    public List<ApplicationSuggestion> getSuggestions(Intent intent) {
        IAppSuggestManager mgr = getService();
        if (mgr == null) {
            return new ArrayList(0);
        }
        try {
            return mgr.getSuggestions(intent);
        } catch (RemoteException e) {
            return new ArrayList(0);
        }
    }

    public Drawable loadIcon(ApplicationSuggestion suggestion) {
        try {
            return Drawable.createFromStream(this.mContext.getContentResolver().openInputStream(suggestion.getThumbailUri()), (String) null);
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
