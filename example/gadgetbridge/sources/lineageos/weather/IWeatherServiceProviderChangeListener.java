package lineageos.weather;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IWeatherServiceProviderChangeListener extends IInterface {
    void onWeatherServiceProviderChanged(String str) throws RemoteException;

    public static abstract class Stub extends Binder implements IWeatherServiceProviderChangeListener {
        private static final String DESCRIPTOR = "lineageos.weather.IWeatherServiceProviderChangeListener";
        static final int TRANSACTION_onWeatherServiceProviderChanged = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWeatherServiceProviderChangeListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IWeatherServiceProviderChangeListener)) {
                return new Proxy(obj);
            }
            return (IWeatherServiceProviderChangeListener) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                onWeatherServiceProviderChanged(data.readString());
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IWeatherServiceProviderChangeListener {
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

            public void onWeatherServiceProviderChanged(String providerLabel) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(providerLabel);
                    this.mRemote.transact(1, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
