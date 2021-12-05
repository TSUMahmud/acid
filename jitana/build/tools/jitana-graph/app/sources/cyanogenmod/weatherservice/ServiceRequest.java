package cyanogenmod.weatherservice;

import android.os.RemoteException;
import cyanogenmod.weather.RequestInfo;

public final class ServiceRequest {
    private final IWeatherProviderServiceClient mClient;
    private final RequestInfo mInfo;
    private Status mStatus = Status.IN_PROGRESS;

    private enum Status {
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        FAILED,
        REJECTED
    }

    ServiceRequest(RequestInfo info, IWeatherProviderServiceClient client) {
        this.mInfo = info;
        this.mClient = client;
    }

    public RequestInfo getRequestInfo() {
        return this.mInfo;
    }

    public void complete(ServiceRequestResult result) {
        synchronized (this) {
            if (this.mStatus.equals(Status.IN_PROGRESS)) {
                try {
                    int requestType = this.mInfo.getRequestType();
                    if (requestType == 1 || requestType == 2) {
                        if (result.getWeatherInfo() != null) {
                            this.mClient.setServiceRequestState(this.mInfo, result, 1);
                        } else {
                            throw new IllegalStateException("The service request result doesn't contain a valid WeatherInfo object");
                        }
                    } else if (requestType == 3) {
                        if (result.getLocationLookupList() != null) {
                            if (result.getLocationLookupList().size() > 0) {
                                this.mClient.setServiceRequestState(this.mInfo, result, 1);
                            }
                        }
                        this.mClient.setServiceRequestState(this.mInfo, (ServiceRequestResult) null, -4);
                    }
                } catch (RemoteException e) {
                }
                this.mStatus = Status.COMPLETED;
            }
        }
    }

    public void fail() {
        synchronized (this) {
            if (this.mStatus.equals(Status.IN_PROGRESS)) {
                try {
                    int requestType = this.mInfo.getRequestType();
                    if (requestType == 1 || requestType == 2) {
                        this.mClient.setServiceRequestState(this.mInfo, (ServiceRequestResult) null, -1);
                        this.mStatus = Status.FAILED;
                    } else {
                        if (requestType == 3) {
                            this.mClient.setServiceRequestState(this.mInfo, (ServiceRequestResult) null, -1);
                        }
                        this.mStatus = Status.FAILED;
                    }
                } catch (RemoteException e) {
                }
            }
        }
    }

    public void reject(int status) {
        synchronized (this) {
            if (this.mStatus.equals(Status.IN_PROGRESS)) {
                if (status != -3) {
                    if (status != -2) {
                        throw new IllegalArgumentException("Can't reject with status " + status);
                    }
                }
                try {
                    this.mClient.setServiceRequestState(this.mInfo, (ServiceRequestResult) null, status);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                this.mStatus = Status.REJECTED;
            }
        }
    }

    public void cancel() {
        synchronized (this) {
            this.mStatus = Status.CANCELLED;
        }
    }
}
