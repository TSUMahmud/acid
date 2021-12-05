package cyanogenmod.externalviews;

import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import cyanogenmod.externalviews.IKeyguardExternalViewCallbacks;

public interface IKeyguardExternalViewProvider extends IInterface {
    void alterWindow(int i, int i2, int i3, int i4, boolean z, Rect rect) throws RemoteException;

    void onAttach(IBinder iBinder) throws RemoteException;

    void onBouncerShowing(boolean z) throws RemoteException;

    void onDetach() throws RemoteException;

    void onKeyguardDismissed() throws RemoteException;

    void onKeyguardShowing(boolean z) throws RemoteException;

    void onLockscreenSlideOffsetChanged(float f) throws RemoteException;

    void onScreenTurnedOff() throws RemoteException;

    void onScreenTurnedOn() throws RemoteException;

    void registerCallback(IKeyguardExternalViewCallbacks iKeyguardExternalViewCallbacks) throws RemoteException;

    void unregisterCallback(IKeyguardExternalViewCallbacks iKeyguardExternalViewCallbacks) throws RemoteException;

    public static abstract class Stub extends Binder implements IKeyguardExternalViewProvider {
        private static final String DESCRIPTOR = "cyanogenmod.externalviews.IKeyguardExternalViewProvider";
        static final int TRANSACTION_alterWindow = 10;
        static final int TRANSACTION_onAttach_0 = 1;
        static final int TRANSACTION_onBouncerShowing = 5;
        static final int TRANSACTION_onDetach = 2;
        static final int TRANSACTION_onKeyguardDismissed = 4;
        static final int TRANSACTION_onKeyguardShowing = 3;
        static final int TRANSACTION_onLockscreenSlideOffsetChanged = 11;
        static final int TRANSACTION_onScreenTurnedOff = 7;
        static final int TRANSACTION_onScreenTurnedOn = 6;
        static final int TRANSACTION_registerCallback = 8;
        static final int TRANSACTION_unregisterCallback = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IKeyguardExternalViewProvider asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IKeyguardExternalViewProvider)) {
                return new Proxy(obj);
            }
            return (IKeyguardExternalViewProvider) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Rect _arg5;
            int i = code;
            Parcel parcel = data;
            if (i != 1598968902) {
                boolean _arg0 = false;
                switch (i) {
                    case 1:
                        parcel.enforceInterface(DESCRIPTOR);
                        onAttach(data.readStrongBinder());
                        return true;
                    case 2:
                        parcel.enforceInterface(DESCRIPTOR);
                        onDetach();
                        return true;
                    case 3:
                        parcel.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg0 = true;
                        }
                        onKeyguardShowing(_arg0);
                        return true;
                    case 4:
                        parcel.enforceInterface(DESCRIPTOR);
                        onKeyguardDismissed();
                        return true;
                    case 5:
                        parcel.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg0 = true;
                        }
                        onBouncerShowing(_arg0);
                        return true;
                    case 6:
                        parcel.enforceInterface(DESCRIPTOR);
                        onScreenTurnedOn();
                        return true;
                    case 7:
                        parcel.enforceInterface(DESCRIPTOR);
                        onScreenTurnedOff();
                        return true;
                    case 8:
                        parcel.enforceInterface(DESCRIPTOR);
                        registerCallback(IKeyguardExternalViewCallbacks.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    case 9:
                        parcel.enforceInterface(DESCRIPTOR);
                        unregisterCallback(IKeyguardExternalViewCallbacks.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    case 10:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _arg02 = data.readInt();
                        int _arg1 = data.readInt();
                        int _arg2 = data.readInt();
                        int _arg3 = data.readInt();
                        boolean _arg4 = data.readInt() != 0;
                        if (data.readInt() != 0) {
                            _arg5 = (Rect) Rect.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg5 = null;
                        }
                        alterWindow(_arg02, _arg1, _arg2, _arg3, _arg4, _arg5);
                        reply.writeNoException();
                        return true;
                    case 11:
                        parcel.enforceInterface(DESCRIPTOR);
                        onLockscreenSlideOffsetChanged(data.readFloat());
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IKeyguardExternalViewProvider {
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

            public void onAttach(IBinder windowToken) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(windowToken);
                    this.mRemote.transact(1, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onDetach() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onKeyguardShowing(boolean screenOn) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(screenOn ? 1 : 0);
                    this.mRemote.transact(3, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onKeyguardDismissed() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onBouncerShowing(boolean showing) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(showing ? 1 : 0);
                    this.mRemote.transact(5, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onScreenTurnedOn() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onScreenTurnedOff() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void registerCallback(IKeyguardExternalViewCallbacks callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(8, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void unregisterCallback(IKeyguardExternalViewCallbacks callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(9, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void alterWindow(int x, int y, int width, int height, boolean visible, Rect clipRect) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    _data.writeInt(visible ? 1 : 0);
                    if (clipRect != null) {
                        _data.writeInt(1);
                        clipRect.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onLockscreenSlideOffsetChanged(float swipeProgress) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(swipeProgress);
                    this.mRemote.transact(11, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
