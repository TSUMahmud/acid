package lineageos.weatherservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
import lineageos.weather.RequestInfo;
import lineageos.weatherservice.IWeatherProviderService;

public abstract class WeatherProviderService extends Service {
    public static final String SERVICE_INTERFACE = "lineageos.weatherservice.WeatherProviderService";
    public static final String SERVICE_META_DATA = "lineageos.weatherservice";
    private final IWeatherProviderService.Stub mBinder = new IWeatherProviderService.Stub() {
        public void processWeatherUpdateRequest(RequestInfo info) {
            WeatherProviderService.this.mHandler.obtainMessage(2, info).sendToTarget();
        }

        public void processCityNameLookupRequest(RequestInfo info) {
            WeatherProviderService.this.mHandler.obtainMessage(2, info).sendToTarget();
        }

        public void setServiceClient(IWeatherProviderServiceClient client) {
            WeatherProviderService.this.mHandler.obtainMessage(1, client).sendToTarget();
        }

        public void cancelOngoingRequests() {
            synchronized (WeatherProviderService.this.mWeakRequestsSet) {
                for (ServiceRequest request : WeatherProviderService.this.mWeakRequestsSet) {
                    request.cancel();
                    WeatherProviderService.this.mWeakRequestsSet.remove(request);
                    WeatherProviderService.this.mHandler.obtainMessage(3, request).sendToTarget();
                }
            }
        }

        public void cancelRequest(int requestId) {
            synchronized (WeatherProviderService.this.mWeakRequestsSet) {
                Iterator it = WeatherProviderService.this.mWeakRequestsSet.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    ServiceRequest request = (ServiceRequest) it.next();
                    if (request.getRequestInfo().hashCode() == requestId) {
                        WeatherProviderService.this.mWeakRequestsSet.remove(request);
                        request.cancel();
                        WeatherProviderService.this.mHandler.obtainMessage(3, request).sendToTarget();
                        break;
                    }
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public IWeatherProviderServiceClient mClient;
    /* access modifiers changed from: private */
    public Handler mHandler;
    /* access modifiers changed from: private */
    public Set<ServiceRequest> mWeakRequestsSet = Collections.newSetFromMap(new WeakHashMap());

    /* access modifiers changed from: protected */
    public abstract void onRequestCancelled(ServiceRequest serviceRequest);

    /* access modifiers changed from: protected */
    public abstract void onRequestSubmitted(ServiceRequest serviceRequest);

    /* access modifiers changed from: protected */
    public final void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        this.mHandler = new ServiceHandler(base.getMainLooper());
    }

    public final IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    private class ServiceHandler extends Handler {
        public static final int MSG_CANCEL_REQUEST = 3;
        public static final int MSG_ON_NEW_REQUEST = 2;
        public static final int MSG_SET_CLIENT = 1;

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                IWeatherProviderServiceClient unused = WeatherProviderService.this.mClient = (IWeatherProviderServiceClient) msg.obj;
                if (WeatherProviderService.this.mClient != null) {
                    WeatherProviderService.this.onConnected();
                } else {
                    WeatherProviderService.this.onDisconnected();
                }
            } else if (i == 2) {
                RequestInfo info = (RequestInfo) msg.obj;
                if (info != null) {
                    ServiceRequest request = new ServiceRequest(info, WeatherProviderService.this.mClient);
                    synchronized (WeatherProviderService.this.mWeakRequestsSet) {
                        WeatherProviderService.this.mWeakRequestsSet.add(request);
                    }
                    WeatherProviderService.this.onRequestSubmitted(request);
                }
            } else if (i == 3) {
                WeatherProviderService.this.onRequestCancelled((ServiceRequest) msg.obj);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onConnected() {
    }

    /* access modifiers changed from: protected */
    public void onDisconnected() {
    }
}
