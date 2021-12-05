package lineageos.weather;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArraySet;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lineageos.app.LineageContextConstants;
import lineageos.weather.ILineageWeatherManager;
import lineageos.weather.IRequestInfoListener;
import lineageos.weather.IWeatherServiceProviderChangeListener;
import lineageos.weather.RequestInfo;

public class LineageWeatherManager {
    private static final String TAG = LineageWeatherManager.class.getSimpleName();
    private static LineageWeatherManager sInstance;
    private static ILineageWeatherManager sWeatherManagerService;
    private Context mContext;
    /* access modifiers changed from: private */
    public Handler mHandler;
    /* access modifiers changed from: private */
    public Map<RequestInfo, LookupCityRequestListener> mLookupNameRequestListeners = Collections.synchronizedMap(new HashMap());
    private final IWeatherServiceProviderChangeListener mProviderChangeListener = new IWeatherServiceProviderChangeListener.Stub() {
        public void onWeatherServiceProviderChanged(final String providerName) {
            LineageWeatherManager.this.mHandler.post(new Runnable() {
                public void run() {
                    synchronized (LineageWeatherManager.this.mProviderChangedListeners) {
                        List<WeatherServiceProviderChangeListener> deadListeners = new ArrayList<>();
                        for (WeatherServiceProviderChangeListener listener : LineageWeatherManager.this.mProviderChangedListeners) {
                            try {
                                listener.onWeatherServiceProviderChanged(providerName);
                            } catch (Throwable th) {
                                deadListeners.add(listener);
                            }
                        }
                        if (deadListeners.size() > 0) {
                            for (WeatherServiceProviderChangeListener listener2 : deadListeners) {
                                LineageWeatherManager.this.mProviderChangedListeners.remove(listener2);
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
            final WeatherUpdateRequestListener listener = (WeatherUpdateRequestListener) LineageWeatherManager.this.mWeatherUpdateRequestListeners.remove(requestInfo);
            if (listener != null) {
                LineageWeatherManager.this.mHandler.post(new Runnable() {
                    public void run() {
                        listener.onWeatherRequestCompleted(status, weatherInfo);
                    }
                });
            }
        }

        public void onLookupCityRequestCompleted(RequestInfo requestInfo, final int status, final List<WeatherLocation> weatherLocations) {
            final LookupCityRequestListener listener = (LookupCityRequestListener) LineageWeatherManager.this.mLookupNameRequestListeners.remove(requestInfo);
            if (listener != null) {
                LineageWeatherManager.this.mHandler.post(new Runnable() {
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

    private LineageWeatherManager(Context context) {
        Context appContext = context.getApplicationContext();
        this.mContext = appContext != null ? appContext : context;
        sWeatherManagerService = getService();
        if (context.getPackageManager().hasSystemFeature("org.lineageos.weather") && sWeatherManagerService == null) {
            Log.wtf(TAG, "Unable to bind the LineageWeatherManagerService");
        }
        this.mHandler = new Handler(appContext.getMainLooper());
    }

    public static LineageWeatherManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LineageWeatherManager(context);
        }
        return sInstance;
    }

    public static ILineageWeatherManager getService() {
        ILineageWeatherManager iLineageWeatherManager = sWeatherManagerService;
        if (iLineageWeatherManager != null) {
            return iLineageWeatherManager;
        }
        IBinder binder = null;
        try {
            Class localClass = Class.forName("android.os.ServiceManager");
            Object result = localClass.getMethod("getService", new Class[]{String.class}).invoke(localClass, new Object[]{LineageContextConstants.LINEAGE_WEATHER_SERVICE});
            if (result != null) {
                binder = (IBinder) result;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        } catch (ClassNotFoundException e4) {
            e4.printStackTrace();
        }
        if (binder == null) {
            return null;
        }
        sWeatherManagerService = ILineageWeatherManager.Stub.asInterface(binder);
        return sWeatherManagerService;
    }

    public int requestWeatherUpdate(Location location, WeatherUpdateRequestListener listener) {
        if (sWeatherManagerService == null) {
            return -1;
        }
        try {
            RequestInfo info = new RequestInfo.Builder(this.mRequestInfoListener).setLocation(location).setTemperatureUnit(1).build();
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
            RequestInfo info = new RequestInfo.Builder(this.mRequestInfoListener).setWeatherLocation(weatherLocation).setTemperatureUnit(1).build();
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
        ILineageWeatherManager iLineageWeatherManager = sWeatherManagerService;
        if (iLineageWeatherManager != null) {
            try {
                iLineageWeatherManager.cancelRequest(requestId);
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
        ILineageWeatherManager iLineageWeatherManager = sWeatherManagerService;
        if (iLineageWeatherManager == null) {
            return null;
        }
        try {
            return iLineageWeatherManager.getActiveWeatherServiceProviderLabel();
        } catch (RemoteException e) {
            return null;
        }
    }
}
