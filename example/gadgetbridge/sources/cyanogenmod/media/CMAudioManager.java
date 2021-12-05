package cyanogenmod.media;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import cyanogenmod.app.CMContextConstants;
import cyanogenmod.media.ICMAudioService;
import java.util.Collections;
import java.util.List;

public final class CMAudioManager {
    public static final String ACTION_AUDIO_SESSIONS_CHANGED = "cyanogenmod.intent.action.ACTION_AUDIO_SESSIONS_CHANGED";
    public static final String EXTRA_SESSION_ADDED = "added";
    public static final String EXTRA_SESSION_INFO = "session_info";
    private static final String TAG = "CMAudioManager";
    private static CMAudioManager sInstance;
    private static ICMAudioService sService;
    private Context mContext;

    private CMAudioManager(Context context) {
        Context appContext = context.getApplicationContext();
        if (appContext != null) {
            this.mContext = appContext;
        } else {
            this.mContext = context;
        }
        sService = getService();
        if (!context.getPackageManager().hasSystemFeature(CMContextConstants.Features.AUDIO) || !checkService()) {
            Log.wtf(TAG, "Unable to get CMAudioService. The service either crashed, was not started, or the interface has been called to early in SystemServer init");
        }
    }

    public static synchronized CMAudioManager getInstance(Context context) {
        CMAudioManager cMAudioManager;
        synchronized (CMAudioManager.class) {
            if (sInstance == null) {
                sInstance = new CMAudioManager(context);
            }
            cMAudioManager = sInstance;
        }
        return cMAudioManager;
    }

    public static ICMAudioService getService() {
        ICMAudioService iCMAudioService = sService;
        if (iCMAudioService != null) {
            return iCMAudioService;
        }
        IBinder b = ServiceManager.getService(CMContextConstants.CM_AUDIO_SERVICE);
        if (b == null) {
            return null;
        }
        sService = ICMAudioService.Stub.asInterface(b);
        return sService;
    }

    private boolean checkService() {
        if (sService != null) {
            return true;
        }
        Log.w(TAG, "not connected to CMAudioService");
        return false;
    }

    public List<AudioSessionInfo> listAudioSessions(int streamType) {
        if (checkService()) {
            try {
                List<AudioSessionInfo> sessions = sService.listAudioSessions(streamType);
                if (sessions != null) {
                    return Collections.unmodifiableList(sessions);
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to list audio sessions!", e);
            }
        }
        return Collections.emptyList();
    }

    public List<AudioSessionInfo> listAudioSessions() {
        return listAudioSessions(-1);
    }
}
