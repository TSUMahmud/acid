package cyanogenmod.power;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IPerformanceManager extends IInterface {
    void cpuBoost(int i) throws RemoteException;

    int getNumberOfProfiles() throws RemoteException;

    int getPowerProfile() throws RemoteException;

    boolean getProfileHasAppProfiles(int i) throws RemoteException;

    boolean setPowerProfile(int i) throws RemoteException;

    public static abstract class Stub extends Binder implements IPerformanceManager {
        private static final String DESCRIPTOR = "cyanogenmod.power.IPerformanceManager";
        static final int TRANSACTION_cpuBoost_0 = 1;
        static final int TRANSACTION_getNumberOfProfiles = 4;
        static final int TRANSACTION_getPowerProfile = 3;
        static final int TRANSACTION_getProfileHasAppProfiles = 5;
        static final int TRANSACTION_setPowerProfile = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPerformanceManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IPerformanceManager)) {
                return new Proxy(obj);
            }
            return (IPerformanceManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                cpuBoost(data.readInt());
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                boolean _result = setPowerProfile(data.readInt());
                reply.writeNoException();
                reply.writeInt(_result);
                return true;
            } else if (code == 3) {
                data.enforceInterface(DESCRIPTOR);
                int _result2 = getPowerProfile();
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            } else if (code == 4) {
                data.enforceInterface(DESCRIPTOR);
                int _result3 = getNumberOfProfiles();
                reply.writeNoException();
                reply.writeInt(_result3);
                return true;
            } else if (code == 5) {
                data.enforceInterface(DESCRIPTOR);
                boolean _result4 = getProfileHasAppProfiles(data.readInt());
                reply.writeNoException();
                reply.writeInt(_result4);
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IPerformanceManager {
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

            public void cpuBoost(int duration) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(duration);
                    this.mRemote.transact(1, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public boolean setPowerProfile(int profile) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(profile);
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

            public int getPowerProfile() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getNumberOfProfiles() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getProfileHasAppProfiles(int profile) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(profile);
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
        }
    }
}
