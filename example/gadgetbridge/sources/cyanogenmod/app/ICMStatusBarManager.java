package cyanogenmod.app;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import cyanogenmod.app.ICustomTileListener;

public interface ICMStatusBarManager extends IInterface {
    void createCustomTileWithTag(String str, String str2, String str3, int i, CustomTile customTile, int[] iArr, int i2) throws RemoteException;

    void registerListener(ICustomTileListener iCustomTileListener, ComponentName componentName, int i) throws RemoteException;

    void removeCustomTileFromListener(ICustomTileListener iCustomTileListener, String str, String str2, int i) throws RemoteException;

    void removeCustomTileWithTag(String str, String str2, int i, int i2) throws RemoteException;

    void unregisterListener(ICustomTileListener iCustomTileListener, int i) throws RemoteException;

    public static abstract class Stub extends Binder implements ICMStatusBarManager {
        private static final String DESCRIPTOR = "cyanogenmod.app.ICMStatusBarManager";
        static final int TRANSACTION_createCustomTileWithTag = 1;
        static final int TRANSACTION_registerListener = 3;
        static final int TRANSACTION_removeCustomTileFromListener = 5;
        static final int TRANSACTION_removeCustomTileWithTag = 2;
        static final int TRANSACTION_unregisterListener = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICMStatusBarManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ICMStatusBarManager)) {
                return new Proxy(obj);
            }
            return (ICMStatusBarManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            CustomTile _arg4;
            ComponentName _arg1;
            int i = code;
            Parcel parcel = data;
            Parcel parcel2 = reply;
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                String _arg0 = data.readString();
                String _arg12 = data.readString();
                String _arg2 = data.readString();
                int _arg3 = data.readInt();
                if (data.readInt() != 0) {
                    _arg4 = CustomTile.CREATOR.createFromParcel(parcel);
                } else {
                    _arg4 = null;
                }
                int[] _arg5 = data.createIntArray();
                createCustomTileWithTag(_arg0, _arg12, _arg2, _arg3, _arg4, _arg5, data.readInt());
                reply.writeNoException();
                parcel2.writeIntArray(_arg5);
                return true;
            } else if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                removeCustomTileWithTag(data.readString(), data.readString(), data.readInt(), data.readInt());
                reply.writeNoException();
                return true;
            } else if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                ICustomTileListener _arg02 = ICustomTileListener.Stub.asInterface(data.readStrongBinder());
                if (data.readInt() != 0) {
                    _arg1 = (ComponentName) ComponentName.CREATOR.createFromParcel(parcel);
                } else {
                    _arg1 = null;
                }
                registerListener(_arg02, _arg1, data.readInt());
                reply.writeNoException();
                return true;
            } else if (i == 4) {
                parcel.enforceInterface(DESCRIPTOR);
                unregisterListener(ICustomTileListener.Stub.asInterface(data.readStrongBinder()), data.readInt());
                reply.writeNoException();
                return true;
            } else if (i == 5) {
                parcel.enforceInterface(DESCRIPTOR);
                removeCustomTileFromListener(ICustomTileListener.Stub.asInterface(data.readStrongBinder()), data.readString(), data.readString(), data.readInt());
                reply.writeNoException();
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ICMStatusBarManager {
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

            public void createCustomTileWithTag(String pkg, String opPkg, String tag, int id, CustomTile tile, int[] idReceived, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeString(opPkg);
                    _data.writeString(tag);
                    _data.writeInt(id);
                    if (tile != null) {
                        _data.writeInt(1);
                        tile.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeIntArray(idReceived);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    _reply.readIntArray(idReceived);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeCustomTileWithTag(String pkg, String tag, int id, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeString(tag);
                    _data.writeInt(id);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerListener(ICustomTileListener listener, ComponentName component, int userid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (component != null) {
                        _data.writeInt(1);
                        component.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userid);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterListener(ICustomTileListener listener, int userid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    _data.writeInt(userid);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeCustomTileFromListener(ICustomTileListener listener, String pkg, String tag, int id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    _data.writeString(pkg);
                    _data.writeString(tag);
                    _data.writeInt(id);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
