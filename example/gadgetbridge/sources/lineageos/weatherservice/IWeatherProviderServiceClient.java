package lineageos.weatherservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import lineageos.weather.RequestInfo;

public interface IWeatherProviderServiceClient extends IInterface {
    void setServiceRequestState(RequestInfo requestInfo, ServiceRequestResult serviceRequestResult, int i) throws RemoteException;

    public static abstract class Stub extends Binder implements IWeatherProviderServiceClient {
        private static final String DESCRIPTOR = "lineageos.weatherservice.IWeatherProviderServiceClient";
        static final int TRANSACTION_setServiceRequestState = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWeatherProviderServiceClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IWeatherProviderServiceClient)) {
                return new Proxy(obj);
            }
            return (IWeatherProviderServiceClient) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            RequestInfo _arg0;
            ServiceRequestResult _arg1;
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg0 = RequestInfo.CREATOR.createFromParcel(data);
                } else {
                    _arg0 = null;
                }
                if (data.readInt() != 0) {
                    _arg1 = ServiceRequestResult.CREATOR.createFromParcel(data);
                } else {
                    _arg1 = null;
                }
                setServiceRequestState(_arg0, _arg1, data.readInt());
                reply.writeNoException();
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IWeatherProviderServiceClient {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void setServiceRequestState(RequestInfo requestInfo, ServiceRequestResult result, int state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (requestInfo != null) {
                        _data.writeInt(1);
                        requestInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (result != null) {
                        _data.writeInt(1);
                        result.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(state);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
