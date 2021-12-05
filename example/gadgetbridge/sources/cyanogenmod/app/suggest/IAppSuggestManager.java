package cyanogenmod.app.suggest;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public interface IAppSuggestManager extends IInterface {
    List<ApplicationSuggestion> getSuggestions(Intent intent) throws RemoteException;

    boolean handles(Intent intent) throws RemoteException;

    public static abstract class Stub extends Binder implements IAppSuggestManager {
        private static final String DESCRIPTOR = "cyanogenmod.app.suggest.IAppSuggestManager";
        static final int TRANSACTION_getSuggestions = 2;
        static final int TRANSACTION_handles_0 = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAppSuggestManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IAppSuggestManager)) {
                return new Proxy(obj);
            }
            return (IAppSuggestManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Intent _arg0;
            Intent _arg02;
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg0 = (Intent) Intent.CREATOR.createFromParcel(data);
                } else {
                    _arg0 = null;
                }
                boolean _result = handles(_arg0);
                reply.writeNoException();
                reply.writeInt(_result);
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg02 = (Intent) Intent.CREATOR.createFromParcel(data);
                } else {
                    _arg02 = null;
                }
                List<ApplicationSuggestion> _result2 = getSuggestions(_arg02);
                reply.writeNoException();
                reply.writeTypedList(_result2);
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IAppSuggestManager {
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

            public boolean handles(Intent intent) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<ApplicationSuggestion> getSuggestions(Intent intent) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    return _reply.createTypedArrayList(ApplicationSuggestion.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
