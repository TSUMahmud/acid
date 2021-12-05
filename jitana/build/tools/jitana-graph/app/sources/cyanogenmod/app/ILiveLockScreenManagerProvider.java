package cyanogenmod.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import cyanogenmod.app.ILiveLockScreenChangeListener;

public interface ILiveLockScreenManagerProvider extends IInterface {
    void cancelLiveLockScreen(String str, int i, int i2) throws RemoteException;

    void enqueueLiveLockScreen(String str, int i, LiveLockScreenInfo liveLockScreenInfo, int[] iArr, int i2) throws RemoteException;

    LiveLockScreenInfo getCurrentLiveLockScreen() throws RemoteException;

    boolean getLiveLockScreenEnabled() throws RemoteException;

    boolean registerChangeListener(ILiveLockScreenChangeListener iLiveLockScreenChangeListener) throws RemoteException;

    boolean unregisterChangeListener(ILiveLockScreenChangeListener iLiveLockScreenChangeListener) throws RemoteException;

    void updateDefaultLiveLockScreen(LiveLockScreenInfo liveLockScreenInfo) throws RemoteException;

    public static abstract class Stub extends Binder implements ILiveLockScreenManagerProvider {
        private static final String DESCRIPTOR = "cyanogenmod.app.ILiveLockScreenManagerProvider";
        static final int TRANSACTION_cancelLiveLockScreen = 2;
        static final int TRANSACTION_enqueueLiveLockScreen = 1;
        static final int TRANSACTION_getCurrentLiveLockScreen = 3;
        static final int TRANSACTION_getLiveLockScreenEnabled = 5;
        static final int TRANSACTION_registerChangeListener = 6;
        static final int TRANSACTION_unregisterChangeListener = 7;
        static final int TRANSACTION_updateDefaultLiveLockScreen = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ILiveLockScreenManagerProvider asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ILiveLockScreenManagerProvider)) {
                return new Proxy(obj);
            }
            return (ILiveLockScreenManagerProvider) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            LiveLockScreenInfo _arg2;
            LiveLockScreenInfo _arg0;
            int i = code;
            Parcel parcel = data;
            Parcel parcel2 = reply;
            if (i != 1598968902) {
                switch (i) {
                    case 1:
                        parcel.enforceInterface(DESCRIPTOR);
                        String _arg02 = data.readString();
                        int _arg1 = data.readInt();
                        if (data.readInt() != 0) {
                            _arg2 = LiveLockScreenInfo.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg2 = null;
                        }
                        int[] _arg3 = data.createIntArray();
                        enqueueLiveLockScreen(_arg02, _arg1, _arg2, _arg3, data.readInt());
                        reply.writeNoException();
                        parcel2.writeIntArray(_arg3);
                        return true;
                    case 2:
                        parcel.enforceInterface(DESCRIPTOR);
                        cancelLiveLockScreen(data.readString(), data.readInt(), data.readInt());
                        reply.writeNoException();
                        return true;
                    case 3:
                        parcel.enforceInterface(DESCRIPTOR);
                        LiveLockScreenInfo _result = getCurrentLiveLockScreen();
                        reply.writeNoException();
                        if (_result != null) {
                            parcel2.writeInt(1);
                            _result.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 4:
                        parcel.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg0 = LiveLockScreenInfo.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg0 = null;
                        }
                        updateDefaultLiveLockScreen(_arg0);
                        return true;
                    case 5:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean _result2 = getLiveLockScreenEnabled();
                        reply.writeNoException();
                        parcel2.writeInt(_result2);
                        return true;
                    case 6:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean _result3 = registerChangeListener(ILiveLockScreenChangeListener.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        parcel2.writeInt(_result3);
                        return true;
                    case 7:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean _result4 = unregisterChangeListener(ILiveLockScreenChangeListener.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        parcel2.writeInt(_result4);
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ILiveLockScreenManagerProvider {
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

            public void enqueueLiveLockScreen(String pkg, int id, LiveLockScreenInfo lls, int[] idReceived, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeInt(id);
                    if (lls != null) {
                        _data.writeInt(1);
                        lls.writeToParcel(_data, 0);
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

            public void cancelLiveLockScreen(String pkg, int id, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeInt(id);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public LiveLockScreenInfo getCurrentLiveLockScreen() throws RemoteException {
                LiveLockScreenInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = LiveLockScreenInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void updateDefaultLiveLockScreen(LiveLockScreenInfo llsInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (llsInfo != null) {
                        _data.writeInt(1);
                        llsInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(4, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public boolean getLiveLockScreenEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
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

            public boolean registerChangeListener(ILiveLockScreenChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(6, _data, _reply, 0);
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

            public boolean unregisterChangeListener(ILiveLockScreenChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(7, _data, _reply, 0);
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
        }
    }
}
