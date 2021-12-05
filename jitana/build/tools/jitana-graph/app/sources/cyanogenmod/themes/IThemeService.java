package cyanogenmod.themes;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import cyanogenmod.themes.IThemeChangeListener;
import cyanogenmod.themes.IThemeProcessingListener;

public interface IThemeService extends IInterface {
    void applyDefaultTheme() throws RemoteException;

    int getLastThemeChangeRequestType() throws RemoteException;

    long getLastThemeChangeTime() throws RemoteException;

    int getProgress() throws RemoteException;

    boolean isThemeApplying() throws RemoteException;

    boolean isThemeBeingProcessed(String str) throws RemoteException;

    boolean processThemeResources(String str) throws RemoteException;

    void rebuildResourceCache() throws RemoteException;

    void registerThemeProcessingListener(IThemeProcessingListener iThemeProcessingListener) throws RemoteException;

    void removeUpdates(IThemeChangeListener iThemeChangeListener) throws RemoteException;

    void requestThemeChange(ThemeChangeRequest themeChangeRequest, boolean z) throws RemoteException;

    void requestThemeChangeUpdates(IThemeChangeListener iThemeChangeListener) throws RemoteException;

    void unregisterThemeProcessingListener(IThemeProcessingListener iThemeProcessingListener) throws RemoteException;

    public static abstract class Stub extends Binder implements IThemeService {
        private static final String DESCRIPTOR = "cyanogenmod.themes.IThemeService";
        static final int TRANSACTION_applyDefaultTheme = 4;
        static final int TRANSACTION_getLastThemeChangeRequestType = 13;
        static final int TRANSACTION_getLastThemeChangeTime = 12;
        static final int TRANSACTION_getProgress = 6;
        static final int TRANSACTION_isThemeApplying = 5;
        static final int TRANSACTION_isThemeBeingProcessed = 8;
        static final int TRANSACTION_processThemeResources = 7;
        static final int TRANSACTION_rebuildResourceCache = 11;
        static final int TRANSACTION_registerThemeProcessingListener = 9;
        static final int TRANSACTION_removeUpdates = 2;
        static final int TRANSACTION_requestThemeChange = 3;
        static final int TRANSACTION_requestThemeChangeUpdates_0 = 1;
        static final int TRANSACTION_unregisterThemeProcessingListener = 10;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IThemeService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IThemeService)) {
                return new Proxy(obj);
            }
            return (IThemeService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ThemeChangeRequest _arg0;
            if (code != 1598968902) {
                switch (code) {
                    case 1:
                        data.enforceInterface(DESCRIPTOR);
                        requestThemeChangeUpdates(IThemeChangeListener.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    case 2:
                        data.enforceInterface(DESCRIPTOR);
                        removeUpdates(IThemeChangeListener.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    case 3:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg0 = ThemeChangeRequest.CREATOR.createFromParcel(data);
                        } else {
                            _arg0 = null;
                        }
                        requestThemeChange(_arg0, data.readInt() != 0);
                        return true;
                    case 4:
                        data.enforceInterface(DESCRIPTOR);
                        applyDefaultTheme();
                        return true;
                    case 5:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result = isThemeApplying();
                        reply.writeNoException();
                        reply.writeInt(_result);
                        return true;
                    case 6:
                        data.enforceInterface(DESCRIPTOR);
                        int _result2 = getProgress();
                        reply.writeNoException();
                        reply.writeInt(_result2);
                        return true;
                    case 7:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result3 = processThemeResources(data.readString());
                        reply.writeNoException();
                        reply.writeInt(_result3);
                        return true;
                    case 8:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result4 = isThemeBeingProcessed(data.readString());
                        reply.writeNoException();
                        reply.writeInt(_result4);
                        return true;
                    case 9:
                        data.enforceInterface(DESCRIPTOR);
                        registerThemeProcessingListener(IThemeProcessingListener.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    case 10:
                        data.enforceInterface(DESCRIPTOR);
                        unregisterThemeProcessingListener(IThemeProcessingListener.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    case 11:
                        data.enforceInterface(DESCRIPTOR);
                        rebuildResourceCache();
                        return true;
                    case 12:
                        data.enforceInterface(DESCRIPTOR);
                        long _result5 = getLastThemeChangeTime();
                        reply.writeNoException();
                        reply.writeLong(_result5);
                        return true;
                    case 13:
                        data.enforceInterface(DESCRIPTOR);
                        int _result6 = getLastThemeChangeRequestType();
                        reply.writeNoException();
                        reply.writeInt(_result6);
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IThemeService {
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

            public void requestThemeChangeUpdates(IThemeChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(1, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void removeUpdates(IThemeChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(2, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void requestThemeChange(ThemeChangeRequest request, boolean removePerAppThemes) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    int i = 0;
                    if (request != null) {
                        _data.writeInt(1);
                        request.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (removePerAppThemes) {
                        i = 1;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(3, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void applyDefaultTheme() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public boolean isThemeApplying() throws RemoteException {
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

            public int getProgress() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean processThemeResources(String themePkgName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(themePkgName);
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

            public boolean isThemeBeingProcessed(String themePkgName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(themePkgName);
                    this.mRemote.transact(8, _data, _reply, 0);
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

            public void registerThemeProcessingListener(IThemeProcessingListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(9, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void unregisterThemeProcessingListener(IThemeProcessingListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(10, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void rebuildResourceCache() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public long getLastThemeChangeTime() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readLong();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getLastThemeChangeRequestType() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
