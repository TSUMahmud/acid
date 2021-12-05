package cyanogenmod.externalviews;

import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IExternalViewProvider extends IInterface {
    void alterWindow(int i, int i2, int i3, int i4, boolean z, Rect rect) throws RemoteException;

    void onAttach(IBinder iBinder) throws RemoteException;

    void onDetach() throws RemoteException;

    void onPause() throws RemoteException;

    void onResume() throws RemoteException;

    void onStart() throws RemoteException;

    void onStop() throws RemoteException;

    public static abstract class Stub extends Binder implements IExternalViewProvider {
        private static final String DESCRIPTOR = "cyanogenmod.externalviews.IExternalViewProvider";
        static final int TRANSACTION_alterWindow = 7;
        static final int TRANSACTION_onAttach_0 = 1;
        static final int TRANSACTION_onDetach = 6;
        static final int TRANSACTION_onPause = 4;
        static final int TRANSACTION_onResume = 3;
        static final int TRANSACTION_onStart_1 = 2;
        static final int TRANSACTION_onStop = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IExternalViewProvider asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IExternalViewProvider)) {
                return new Proxy(obj);
            }
            return (IExternalViewProvider) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Rect _arg5;
            int i = code;
            Parcel parcel = data;
            if (i != 1598968902) {
                switch (i) {
                    case 1:
                        parcel.enforceInterface(DESCRIPTOR);
                        onAttach(data.readStrongBinder());
                        return true;
                    case 2:
                        parcel.enforceInterface(DESCRIPTOR);
                        onStart();
                        return true;
                    case 3:
                        parcel.enforceInterface(DESCRIPTOR);
                        onResume();
                        return true;
                    case 4:
                        parcel.enforceInterface(DESCRIPTOR);
                        onPause();
                        return true;
                    case 5:
                        parcel.enforceInterface(DESCRIPTOR);
                        onStop();
                        return true;
                    case 6:
                        parcel.enforceInterface(DESCRIPTOR);
                        onDetach();
                        return true;
                    case 7:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _arg0 = data.readInt();
                        int _arg1 = data.readInt();
                        int _arg2 = data.readInt();
                        int _arg3 = data.readInt();
                        boolean _arg4 = data.readInt() != 0;
                        if (data.readInt() != 0) {
                            _arg5 = (Rect) Rect.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg5 = null;
                        }
                        alterWindow(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                        reply.writeNoException();
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IExternalViewProvider {
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

            public void onStart() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onResume() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onPause() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onStop() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onDetach() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, (Parcel) null, 1);
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
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
