package cyanogenmod.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ILiveLockScreenChangeListener extends IInterface {
    void onLiveLockScreenChanged(LiveLockScreenInfo liveLockScreenInfo) throws RemoteException;

    public static abstract class Stub extends Binder implements ILiveLockScreenChangeListener {
        private static final String DESCRIPTOR = "cyanogenmod.app.ILiveLockScreenChangeListener";
        static final int TRANSACTION_onLiveLockScreenChanged_0 = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ILiveLockScreenChangeListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ILiveLockScreenChangeListener)) {
                return new Proxy(obj);
            }
            return (ILiveLockScreenChangeListener) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            LiveLockScreenInfo _arg0;
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg0 = LiveLockScreenInfo.CREATOR.createFromParcel(data);
                } else {
                    _arg0 = null;
                }
                onLiveLockScreenChanged(_arg0);
                reply.writeNoException();
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ILiveLockScreenChangeListener {
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

            public void onLiveLockScreenChanged(LiveLockScreenInfo llsInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (llsInfo != null) {
                        _data.writeInt(1);
                        llsInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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
