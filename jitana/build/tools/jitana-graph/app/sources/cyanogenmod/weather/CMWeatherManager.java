package cyanogenmod.weather;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArraySet;
import android.util.Log;
import cyanogenmod.app.CMContextConstants;
import cyanogenmod.providers.CMSettings;
import cyanogenmod.weather.ICMWeatherManager;
import cyanogenmod.weather.IRequestInfoListener;
import cyanogenmod.weather.IWeatherServiceProviderChangeListener;
import cyanogenmod.weather.RequestInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CMWeatherManager {
    private static final String TAG = CMWeatherManager.class.getSimpleName();
    private static CMWeatherManager sInstance;
    private static ICMWeatherManager sWeatherManagerService;
    private Context mContext;
    /* access modifiers changed from: private */
    public Handler mHandler;
    /* access modifiers changed from: private */
    public Map<RequestInfo, LookupCityRequestListener> mLookupNameRequestListeners = Collections.synchronizedMap(new HashMap());
    private final IWeatherServiceProviderChangeListener mProviderChangeListener = new IWeatherServiceProviderChangeListener.Stub() {
        public void onWeatherServiceProviderChanged(final String providerName) {
            CMWeatherManager.this.mHandler.post(new Runnable() {
                public void run() {
                    synchronized (CMWeatherManager.this.mProviderChangedListeners) {
                        List<WeatherServiceProviderChangeListener> deadListeners = new ArrayList<>();
                        for (WeatherServiceProviderChangeListener listener : CMWeatherManager.this.mProviderChangedListeners) {
                            try {
                                listener.onWeatherServiceProviderChanged(providerName);
                            } catch (Throwable th) {
                                deadListeners.add(listener);
                            }
                        }
                        if (deadListeners.size() > 0) {
                            for (WeatherServiceProviderChangeListener listener2 : deadListeners) {
                                CMWeatherManager.this.mProviderChangedListeners.remove(listener2);
                            }
                        }
                    }
                }
            });
        }
    };
    /* access modifiers changed from: private */
    public Set<WeatherServiceProviderChangeListener> mProviderChangedListeners = new ArraySet();
    private final IRequestInfoListener mRequestInfoListener = new IRequestInfoListener.Stub() {
        public void onWeatherRequestCompleted(RequestInfo requestInfo, final int status, final WeatherInfo weatherInfo) {
            final WeatherUpdateRequestListener listener = (WeatherUpdateRequestListener) CMWeatherManager.this.mWeatherUpdateRequestListeners.remove(requestInfo);
            if (listener != null) {
                CMWeatherManager.this.mHandler.post(new Runnable() {
                    public void run() {
                        listener.onWeatherRequestCompleted(status, weatherInfo);
                    }
                });
            }
        }

        public void onLookupCityRequestCompleted(RequestInfo requestInfo, final int status, final List<WeatherLocation> weatherLocations) {
            final LookupCityRequestListener listener = (LookupCityRequestListener) CMWeatherManager.this.mLookupNameRequestListeners.remove(requestInfo);
            if (listener != null) {
                CMWeatherManager.this.mHandler.post(new Runnable() {
                    public void run() {
                        listener.onLookupCityRequestCompleted(status, weatherLocations);
                    }
                });
            }
        }
    };
    /* access modifiers changed from: private */
    public Map<RequestInfo, WeatherUpdateRequestListener> mWeatherUpdateRequestListeners = Collections.synchronizedMap(new HashMap());

    public interface LookupCityRequestListener {
        void onLookupCityRequestCompleted(int i, List<WeatherLocation> list);
    }

    public interface WeatherServiceProviderChangeListener {
        void onWeatherServiceProviderChanged(String str);
    }

    public interface WeatherUpdateRequestListener {
        void onWeatherRequestCompleted(int i, WeatherInfo weatherInfo);
    }

    public static final class RequestStatus {
        public static final int ALREADY_IN_PROGRESS = -3;
        public static final int COMPLETED = 1;
        public static final int FAILED = -1;
        public static final int NO_MATCH_FOUND = -4;
        public static final int SUBMITTED_TOO_SOON = -2;

        private RequestStatus() {
        }
    }

    private CMWeatherManager(Context context) {
        Context appContext = context.getApplicationContext();
        this.mContext = appContext != null ? appContext : context;
        sWeatherManagerService = getService();
        if (context.getPackageManager().hasSystemFeature(CMContextConstants.Features.WEATHER_SERVICES) && sWeatherManagerService == null) {
            Log.wtf(TAG, "Unable to bind the CMWeatherManagerService");
        }
        this.mHandler = new Handler(appContext.getMainLooper());
    }

    public static CMWeatherManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CMWeatherManager(context);
        }
        return sInstance;
    }

    public static ICMWeatherManager getService() {
        ICMWeatherManager iCMWeatherManager = sWeatherManagerService;
        if (iCMWeatherManager != null) {
            return iCMWeatherManager;
        }
        IBinder binder = ServiceManager.getService(CMContextConstants.CM_WEATHER_SERVICE);
        if (binder == null) {
            return null;
        }
        sWeatherManagerService = ICMWeatherManager.Stub.asInterface(binder);
        return sWeatherManagerService;
    }

    public int requestWeatherUpdate(Location location, WeatherUpdateRequestListener listener) {
        if (sWeatherManagerService == null) {
            return -1;
        }
        try {
            RequestInfo info = new RequestInfo.Builder(this.mRequestInfoListener).setLocation(location).setTemperatureUnit(CMSettings.Global.getInt(this.mContext.getContentResolver(), CMSettings.Global.WEATHER_TEMPERATURE_UNIT, 2)).build();
            if (listener != null) {
                this.mWeatherUpdateRequestListeners.put(info, listener);
            }
            sWeatherManagerService.updateWeather(info);
            return info.hashCode();
        } catch (RemoteException e) {
            return -1;
        }
    }

    public int requestWeatherUpdate(WeatherLocation weatherLocation, WeatherUpdateRequestListener listener) {
        if (sWeatherManagerService == null) {
            return -1;
        }
        try {
            RequestInfo info = new RequestInfo.Builder(this.mRequestInfoListener).setWeatherLocation(weatherLocation).setTemperatureUnit(CMSettings.Global.getInt(this.mContext.getContentResolver(), CMSettings.Global.WEATHER_TEMPERATURE_UNIT, 2)).build();
            if (listener != null) {
                this.mWeatherUpdateRequestListeners.put(info, listener);
            }
            sWeatherManagerService.updateWeather(info);
            return info.hashCode();
        } catch (RemoteException e) {
            return -1;
        }
    }

    public int lookupCity(String city, LookupCityRequestListener listener) {
        if (sWeatherManagerService == null) {
            return -1;
        }
        try {
            RequestInfo info = new RequestInfo.Builder(this.mRequestInfoListener).setCityName(city).build();
            if (listener != null) {
                this.mLookupNameRequestListeners.put(info, listener);
            }
            sWeatherManagerService.lookupCity(info);
            return info.hashCode();
        } catch (RemoteException e) {
            return -1;
        }
    }

    public void cancelRequest(int requestId) {
        ICMWeatherManager iCMWeatherManager = sWeatherManagerService;
        if (iCMWeatherManager != null) {
            try {
                iCMWeatherManager.cancelRequest(requestId);
            } catch (RemoteException e) {
            }
        }
    }

    public void registerWeatherServiceProviderChangeListener(WeatherServiceProviderChangeListener listener) {
        if (sWeatherManagerService != null) {
            synchronized (this.mProviderChangedListeners) {
                if (!this.mProviderChangedListeners.contains(listener)) {
                    if (this.mProviderChangedListeners.size() == 0) {
                        try {
                            sWeatherManagerService.registerWeatherServiceProviderChangeListener(this.mProviderChangeListener);
                        } catch (RemoteException e) {
                        }
                    }
                    this.mProviderChangedListeners.add(listener);
                } else {
                    throw new IllegalArgumentException("Listener already registered");
                }
            }
        }
    }

    public void unregisterWeatherServiceProviderChangeListener(WeatherServiceProviderChangeListener listener) {
        if (sWeatherManagerService != null) {
            synchronized (this.mProviderChangedListeners) {
                if (this.mProviderChangedListeners.contains(listener)) {
                    this.mProviderChangedListeners.remove(listener);
                    if (this.mProviderChangedListeners.size() == 0) {
                        try {
                            sWeatherManagerService.unregisterWeatherServiceProviderChangeListener(this.mProviderChangeListener);
                        } catch (RemoteException e) {
                        }
                    }
                } else {
                    throw new IllegalArgumentException("Listener was never registered");
                }
            }
        }
    }

    public String getActiveWeatherServiceProviderLabel() {
        ICMWeatherManager iCMWeatherManager = sWeatherManagerService;
        if (iCMWeatherManager == null) {
            return null;
        }
        try {
            return iCMWeatherManager.getActiveWeatherServiceProviderLabel();
        } catch (RemoteException e) {
            return null;
        }
    }
}
