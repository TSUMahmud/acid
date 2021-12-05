package cyanogenmod.weather;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public interface IRequestInfoListener extends IInterface {
    void onLookupCityRequestCompleted(RequestInfo requestInfo, int i, List<WeatherLocation> list) throws RemoteException;

    void onWeatherRequestCompleted(RequestInfo requestInfo, int i, WeatherInfo weatherInfo) throws RemoteException;

    public static abstract class Stub extends Binder implements IRequestInfoListener {
        private static final String DESCRIPTOR = "cyanogenmod.weather.IRequestInfoListener";
        static final int TRANSACTION_onLookupCityRequestCompleted = 2;
        static final int TRANSACTION_onWeatherRequestCompleted = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRequestInfoListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IRequestInfoListener)) {
                return new Proxy(obj);
            }
            return (IRequestInfoListener) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            RequestInfo _arg0;
            WeatherInfo _arg2;
            RequestInfo _arg02;
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg0 = RequestInfo.CREATOR.createFromParcel(data);
                } else {
                    _arg0 = null;
                }
                int _arg1 = data.readInt();
                if (data.readInt() != 0) {
                    _arg2 = WeatherInfo.CREATOR.createFromParcel(data);
                } else {
                    _arg2 = null;
                }
                onWeatherRequestCompleted(_arg0, _arg1, _arg2);
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg02 = RequestInfo.CREATOR.createFromParcel(data);
                } else {
                    _arg02 = null;
                }
                onLookupCityRequestCompleted(_arg02, data.readInt(), data.createTypedArrayList(WeatherLocation.CREATOR));
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IRequestInfoListener {
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

            public void onWeatherRequestCompleted(RequestInfo requestInfo, int status, WeatherInfo weatherInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (requestInfo != null) {
                        _data.writeInt(1);
                        requestInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(status);
                    if (weatherInfo != null) {
                        _data.writeInt(1);
                        weatherInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onLookupCityRequestCompleted(RequestInfo requestInfo, int status, List<WeatherLocation> weatherLocation) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (requestInfo != null) {
                        _data.writeInt(1);
                        requestInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(status);
                    _data.writeTypedList(weatherLocation);
                    this.mRemote.transact(2, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
