package cyanogenmod.weatherservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import cyanogenmod.weather.RequestInfo;
import cyanogenmod.weatherservice.IWeatherProviderServiceClient;

public interface IWeatherProviderService extends IInterface {
    void cancelOngoingRequests() throws RemoteException;

    void cancelRequest(int i) throws RemoteException;

    void processCityNameLookupRequest(RequestInfo requestInfo) throws RemoteException;

    void processWeatherUpdateRequest(RequestInfo requestInfo) throws RemoteException;

    void setServiceClient(IWeatherProviderServiceClient iWeatherProviderServiceClient) throws RemoteException;

    public static abstract class Stub extends Binder implements IWeatherProviderService {
        private static final String DESCRIPTOR = "cyanogenmod.weatherservice.IWeatherProviderService";
        static final int TRANSACTION_cancelOngoingRequests = 4;
        static final int TRANSACTION_cancelRequest = 5;
        static final int TRANSACTION_processCityNameLookupRequest = 2;
        static final int TRANSACTION_processWeatherUpdateRequest_0 = 1;
        static final int TRANSACTION_setServiceClient = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWeatherProviderService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IWeatherProviderService)) {
                return new Proxy(obj);
            }
            return (IWeatherProviderService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            RequestInfo _arg0;
            RequestInfo _arg02;
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg0 = RequestInfo.CREATOR.createFromParcel(data);
                } else {
                    _arg0 = null;
                }
                processWeatherUpdateRequest(_arg0);
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg02 = RequestInfo.CREATOR.createFromParcel(data);
                } else {
                    _arg02 = null;
                }
                processCityNameLookupRequest(_arg02);
                return true;
            } else if (code == 3) {
                data.enforceInterface(DESCRIPTOR);
                setServiceClient(IWeatherProviderServiceClient.Stub.asInterface(data.readStrongBinder()));
                return true;
            } else if (code == 4) {
                data.enforceInterface(DESCRIPTOR);
                cancelOngoingRequests();
                return true;
            } else if (code == 5) {
                data.enforceInterface(DESCRIPTOR);
                cancelRequest(data.readInt());
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IWeatherProviderService {
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

            public void processWeatherUpdateRequest(RequestInfo request) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (request != null) {
                        _data.writeInt(1);
                        request.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void processCityNameLookupRequest(RequestInfo request) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (request != null) {
                        _data.writeInt(1);
                        request.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(2, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void setServiceClient(IWeatherProviderServiceClient client) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    this.mRemote.transact(3, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void cancelOngoingRequests() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void cancelRequest(int requestId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(requestId);
                    this.mRemote.transact(5, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
