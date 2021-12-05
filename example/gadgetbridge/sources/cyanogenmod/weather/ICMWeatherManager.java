package cyanogenmod.weather;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import cyanogenmod.weather.IWeatherServiceProviderChangeListener;

public interface ICMWeatherManager extends IInterface {
    void cancelRequest(int i) throws RemoteException;

    String getActiveWeatherServiceProviderLabel() throws RemoteException;

    void lookupCity(RequestInfo requestInfo) throws RemoteException;

    void registerWeatherServiceProviderChangeListener(IWeatherServiceProviderChangeListener iWeatherServiceProviderChangeListener) throws RemoteException;

    void unregisterWeatherServiceProviderChangeListener(IWeatherServiceProviderChangeListener iWeatherServiceProviderChangeListener) throws RemoteException;

    void updateWeather(RequestInfo requestInfo) throws RemoteException;

    public static abstract class Stub extends Binder implements ICMWeatherManager {
        private static final String DESCRIPTOR = "cyanogenmod.weather.ICMWeatherManager";
        static final int TRANSACTION_cancelRequest = 6;
        static final int TRANSACTION_getActiveWeatherServiceProviderLabel = 5;
        static final int TRANSACTION_lookupCity = 2;
        static final int TRANSACTION_registerWeatherServiceProviderChangeListener = 3;
        static final int TRANSACTION_unregisterWeatherServiceProviderChangeListener = 4;
        static final int TRANSACTION_updateWeather_0 = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICMWeatherManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ICMWeatherManager)) {
                return new Proxy(obj);
            }
            return (ICMWeatherManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            RequestInfo _arg0;
            RequestInfo _arg02;
            if (code != 1598968902) {
                switch (code) {
                    case 1:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg0 = RequestInfo.CREATOR.createFromParcel(data);
                        } else {
                            _arg0 = null;
                        }
                        updateWeather(_arg0);
                        return true;
                    case 2:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg02 = RequestInfo.CREATOR.createFromParcel(data);
                        } else {
                            _arg02 = null;
                        }
                        lookupCity(_arg02);
                        return true;
                    case 3:
                        data.enforceInterface(DESCRIPTOR);
                        registerWeatherServiceProviderChangeListener(IWeatherServiceProviderChangeListener.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    case 4:
                        data.enforceInterface(DESCRIPTOR);
                        unregisterWeatherServiceProviderChangeListener(IWeatherServiceProviderChangeListener.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    case 5:
                        data.enforceInterface(DESCRIPTOR);
                        String _result = getActiveWeatherServiceProviderLabel();
                        reply.writeNoException();
                        reply.writeString(_result);
                        return true;
                    case 6:
                        data.enforceInterface(DESCRIPTOR);
                        cancelRequest(data.readInt());
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ICMWeatherManager {
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

            public void updateWeather(RequestInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void lookupCity(RequestInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(2, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void registerWeatherServiceProviderChangeListener(IWeatherServiceProviderChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(3, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void unregisterWeatherServiceProviderChangeListener(IWeatherServiceProviderChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(4, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public String getActiveWeatherServiceProviderLabel() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void cancelRequest(int requestId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(requestId);
                    this.mRemote.transact(6, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
