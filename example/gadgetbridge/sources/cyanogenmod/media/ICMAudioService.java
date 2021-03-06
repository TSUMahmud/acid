package cyanogenmod.media;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public interface ICMAudioService extends IInterface {
    List<AudioSessionInfo> listAudioSessions(int i) throws RemoteException;

    public static abstract class Stub extends Binder implements ICMAudioService {
        private static final String DESCRIPTOR = "cyanogenmod.media.ICMAudioService";
        static final int TRANSACTION_listAudioSessions_0 = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICMAudioService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ICMAudioService)) {
                return new Proxy(obj);
            }
            return (ICMAudioService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                List<AudioSessionInfo> _result = listAudioSessions(data.readInt());
                reply.writeNoException();
                reply.writeTypedList(_result);
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ICMAudioService {
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

            public List<AudioSessionInfo> listAudioSessions(int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    return _reply.createTypedArrayList(AudioSessionInfo.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
