package cyanogenmod.externalviews;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IKeyguardExternalViewCallbacks extends IInterface {
    void collapseNotificationPanel() throws RemoteException;

    void onAttachedToWindow() throws RemoteException;

    void onDetachedFromWindow() throws RemoteException;

    boolean requestDismiss() throws RemoteException;

    boolean requestDismissAndStartActivity(Intent intent) throws RemoteException;

    void setInteractivity(boolean z) throws RemoteException;

    void slideLockscreenIn() throws RemoteException;

    public static abstract class Stub extends Binder implements IKeyguardExternalViewCallbacks {
        private static final String DESCRIPTOR = "cyanogenmod.externalviews.IKeyguardExternalViewCallbacks";
        static final int TRANSACTION_collapseNotificationPanel_2 = 3;
        static final int TRANSACTION_onAttachedToWindow = 5;
        static final int TRANSACTION_onDetachedFromWindow = 6;
        static final int TRANSACTION_requestDismissAndStartActivity_1 = 2;
        static final int TRANSACTION_requestDismiss_0 = 1;
        static final int TRANSACTION_setInteractivity = 4;
        static final int TRANSACTION_slideLockscreenIn = 7;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IKeyguardExternalViewCallbacks asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IKeyguardExternalViewCallbacks)) {
                return new Proxy(obj);
            }
            return (IKeyguardExternalViewCallbacks) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Intent _arg0;
            if (code != 1598968902) {
                switch (code) {
                    case 1:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result = requestDismiss();
                        reply.writeNoException();
                        reply.writeInt(_result);
                        return true;
                    case 2:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg0 = (Intent) Intent.CREATOR.createFromParcel(data);
                        } else {
                            _arg0 = null;
                        }
                        boolean _result2 = requestDismissAndStartActivity(_arg0);
                        reply.writeNoException();
                        reply.writeInt(_result2);
                        return true;
                    case 3:
                        data.enforceInterface(DESCRIPTOR);
                        collapseNotificationPanel();
                        return true;
                    case 4:
                        data.enforceInterface(DESCRIPTOR);
                        setInteractivity(data.readInt() != 0);
                        return true;
                    case 5:
                        data.enforceInterface(DESCRIPTOR);
                        onAttachedToWindow();
                        return true;
                    case 6:
                        data.enforceInterface(DESCRIPTOR);
                        onDetachedFromWindow();
                        return true;
                    case 7:
                        data.enforceInterface(DESCRIPTOR);
                        slideLockscreenIn();
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IKeyguardExternalViewCallbacks {
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

            public boolean requestDismiss() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            public boolean requestDismissAndStartActivity(Intent intent) throws RemoteException {
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
                    this.mRemote.transact(2, _data, _reply, 0);
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

            public void collapseNotificationPanel() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void setInteractivity(boolean isInteractive) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(isInteractive ? 1 : 0);
                    this.mRemote.transact(4, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onAttachedToWindow() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onDetachedFromWindow() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void slideLockscreenIn() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
