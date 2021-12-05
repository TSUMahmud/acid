package cyanogenmod.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import org.cyanogenmod.internal.statusbar.IStatusBarCustomTileHolder;

public interface ICustomTileListener extends IInterface {
    void onCustomTilePosted(IStatusBarCustomTileHolder iStatusBarCustomTileHolder) throws RemoteException;

    void onCustomTileRemoved(IStatusBarCustomTileHolder iStatusBarCustomTileHolder) throws RemoteException;

    void onListenerConnected() throws RemoteException;

    public static abstract class Stub extends Binder implements ICustomTileListener {
        private static final String DESCRIPTOR = "cyanogenmod.app.ICustomTileListener";
        static final int TRANSACTION_onCustomTilePosted = 2;
        static final int TRANSACTION_onCustomTileRemoved = 3;
        static final int TRANSACTION_onListenerConnected_0 = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICustomTileListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ICustomTileListener)) {
                return new Proxy(obj);
            }
            return (ICustomTileListener) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                onListenerConnected();
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                onCustomTilePosted(IStatusBarCustomTileHolder.Stub.asInterface(data.readStrongBinder()));
                return true;
            } else if (code == 3) {
                data.enforceInterface(DESCRIPTOR);
                onCustomTileRemoved(IStatusBarCustomTileHolder.Stub.asInterface(data.readStrongBinder()));
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ICustomTileListener {
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

            public void onListenerConnected() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onCustomTilePosted(IStatusBarCustomTileHolder customTileHolder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(customTileHolder != null ? customTileHolder.asBinder() : null);
                    this.mRemote.transact(2, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onCustomTileRemoved(IStatusBarCustomTileHolder customTileHolder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(customTileHolder != null ? customTileHolder.asBinder() : null);
                    this.mRemote.transact(3, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
