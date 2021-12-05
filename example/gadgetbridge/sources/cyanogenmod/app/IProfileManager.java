package cyanogenmod.app;

import android.app.NotificationGroup;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.RemoteException;

public interface IProfileManager extends IInterface {
    void addNotificationGroup(NotificationGroup notificationGroup) throws RemoteException;

    boolean addProfile(Profile profile) throws RemoteException;

    Profile getActiveProfile() throws RemoteException;

    NotificationGroup getNotificationGroup(ParcelUuid parcelUuid) throws RemoteException;

    NotificationGroup getNotificationGroupForPackage(String str) throws RemoteException;

    NotificationGroup[] getNotificationGroups() throws RemoteException;

    Profile getProfile(ParcelUuid parcelUuid) throws RemoteException;

    Profile getProfileByName(String str) throws RemoteException;

    Profile[] getProfiles() throws RemoteException;

    boolean isEnabled() throws RemoteException;

    boolean notificationGroupExistsByName(String str) throws RemoteException;

    boolean profileExists(ParcelUuid parcelUuid) throws RemoteException;

    boolean profileExistsByName(String str) throws RemoteException;

    void removeNotificationGroup(NotificationGroup notificationGroup) throws RemoteException;

    boolean removeProfile(Profile profile) throws RemoteException;

    void resetAll() throws RemoteException;

    boolean setActiveProfile(ParcelUuid parcelUuid) throws RemoteException;

    boolean setActiveProfileByName(String str) throws RemoteException;

    void updateNotificationGroup(NotificationGroup notificationGroup) throws RemoteException;

    void updateProfile(Profile profile) throws RemoteException;

    public static abstract class Stub extends Binder implements IProfileManager {
        private static final String DESCRIPTOR = "cyanogenmod.app.IProfileManager";
        static final int TRANSACTION_addNotificationGroup = 14;
        static final int TRANSACTION_addProfile = 4;
        static final int TRANSACTION_getActiveProfile = 3;
        static final int TRANSACTION_getNotificationGroup = 18;
        static final int TRANSACTION_getNotificationGroupForPackage = 17;
        static final int TRANSACTION_getNotificationGroups = 13;
        static final int TRANSACTION_getProfile = 7;
        static final int TRANSACTION_getProfileByName = 8;
        static final int TRANSACTION_getProfiles = 9;
        static final int TRANSACTION_isEnabled = 20;
        static final int TRANSACTION_notificationGroupExistsByName = 12;
        static final int TRANSACTION_profileExists = 10;
        static final int TRANSACTION_profileExistsByName = 11;
        static final int TRANSACTION_removeNotificationGroup = 15;
        static final int TRANSACTION_removeProfile = 5;
        static final int TRANSACTION_resetAll = 19;
        static final int TRANSACTION_setActiveProfileByName = 2;
        static final int TRANSACTION_setActiveProfile_0 = 1;
        static final int TRANSACTION_updateNotificationGroup = 16;
        static final int TRANSACTION_updateProfile = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IProfileManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IProfileManager)) {
                return new Proxy(obj);
            }
            return (IProfileManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ParcelUuid _arg0;
            Profile _arg02;
            Profile _arg03;
            Profile _arg04;
            ParcelUuid _arg05;
            ParcelUuid _arg06;
            NotificationGroup _arg07;
            NotificationGroup _arg08;
            NotificationGroup _arg09;
            ParcelUuid _arg010;
            if (code != 1598968902) {
                switch (code) {
                    case 1:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg0 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg0 = null;
                        }
                        boolean _result = setActiveProfile(_arg0);
                        reply.writeNoException();
                        reply.writeInt(_result);
                        return true;
                    case 2:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result2 = setActiveProfileByName(data.readString());
                        reply.writeNoException();
                        reply.writeInt(_result2);
                        return true;
                    case 3:
                        data.enforceInterface(DESCRIPTOR);
                        Profile _result3 = getActiveProfile();
                        reply.writeNoException();
                        if (_result3 != null) {
                            reply.writeInt(1);
                            _result3.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 4:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg02 = Profile.CREATOR.createFromParcel(data);
                        } else {
                            _arg02 = null;
                        }
                        boolean _result4 = addProfile(_arg02);
                        reply.writeNoException();
                        reply.writeInt(_result4);
                        return true;
                    case 5:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg03 = Profile.CREATOR.createFromParcel(data);
                        } else {
                            _arg03 = null;
                        }
                        boolean _result5 = removeProfile(_arg03);
                        reply.writeNoException();
                        reply.writeInt(_result5);
                        return true;
                    case 6:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg04 = Profile.CREATOR.createFromParcel(data);
                        } else {
                            _arg04 = null;
                        }
                        updateProfile(_arg04);
                        reply.writeNoException();
                        return true;
                    case 7:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg05 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg05 = null;
                        }
                        Profile _result6 = getProfile(_arg05);
                        reply.writeNoException();
                        if (_result6 != null) {
                            reply.writeInt(1);
                            _result6.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 8:
                        data.enforceInterface(DESCRIPTOR);
                        Profile _result7 = getProfileByName(data.readString());
                        reply.writeNoException();
                        if (_result7 != null) {
                            reply.writeInt(1);
                            _result7.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 9:
                        data.enforceInterface(DESCRIPTOR);
                        Profile[] _result8 = getProfiles();
                        reply.writeNoException();
                        reply.writeTypedArray(_result8, 1);
                        return true;
                    case 10:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg06 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg06 = null;
                        }
                        boolean _result9 = profileExists(_arg06);
                        reply.writeNoException();
                        reply.writeInt(_result9);
                        return true;
                    case 11:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result10 = profileExistsByName(data.readString());
                        reply.writeNoException();
                        reply.writeInt(_result10);
                        return true;
                    case 12:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result11 = notificationGroupExistsByName(data.readString());
                        reply.writeNoException();
                        reply.writeInt(_result11);
                        return true;
                    case 13:
                        data.enforceInterface(DESCRIPTOR);
                        NotificationGroup[] _result12 = getNotificationGroups();
                        reply.writeNoException();
                        reply.writeTypedArray(_result12, 1);
                        return true;
                    case 14:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg07 = (NotificationGroup) NotificationGroup.CREATOR.createFromParcel(data);
                        } else {
                            _arg07 = null;
                        }
                        addNotificationGroup(_arg07);
                        reply.writeNoException();
                        return true;
                    case 15:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg08 = (NotificationGroup) NotificationGroup.CREATOR.createFromParcel(data);
                        } else {
                            _arg08 = null;
                        }
                        removeNotificationGroup(_arg08);
                        reply.writeNoException();
                        return true;
                    case 16:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg09 = (NotificationGroup) NotificationGroup.CREATOR.createFromParcel(data);
                        } else {
                            _arg09 = null;
                        }
                        updateNotificationGroup(_arg09);
                        reply.writeNoException();
                        return true;
                    case 17:
                        data.enforceInterface(DESCRIPTOR);
                        NotificationGroup _result13 = getNotificationGroupForPackage(data.readString());
                        reply.writeNoException();
                        if (_result13 != null) {
                            reply.writeInt(1);
                            _result13.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 18:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg010 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg010 = null;
                        }
                        NotificationGroup _result14 = getNotificationGroup(_arg010);
                        reply.writeNoException();
                        if (_result14 != null) {
                            reply.writeInt(1);
                            _result14.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 19:
                        data.enforceInterface(DESCRIPTOR);
                        resetAll();
                        reply.writeNoException();
                        return true;
                    case 20:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result15 = isEnabled();
                        reply.writeNoException();
                        reply.writeInt(_result15);
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IProfileManager {
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

            public boolean setActiveProfile(ParcelUuid profileParcelUuid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (profileParcelUuid != null) {
                        _data.writeInt(1);
                        profileParcelUuid.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            public boolean setActiveProfileByName(String profileName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(profileName);
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

            public Profile getActiveProfile() throws RemoteException {
                Profile _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = Profile.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean addProfile(Profile profile) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (profile != null) {
                        _data.writeInt(1);
                        profile.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(4, _data, _reply, 0);
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

            public boolean removeProfile(Profile profile) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (profile != null) {
                        _data.writeInt(1);
                        profile.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            public void updateProfile(Profile profile) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (profile != null) {
                        _data.writeInt(1);
                        profile.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Profile getProfile(ParcelUuid profileParcelUuid) throws RemoteException {
                Profile _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (profileParcelUuid != null) {
                        _data.writeInt(1);
                        profileParcelUuid.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = Profile.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Profile getProfileByName(String profileName) throws RemoteException {
                Profile _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(profileName);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = Profile.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Profile[] getProfiles() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    return (Profile[]) _reply.createTypedArray(Profile.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean profileExists(ParcelUuid profileUuid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (profileUuid != null) {
                        _data.writeInt(1);
                        profileUuid.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(10, _data, _reply, 0);
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

            public boolean profileExistsByName(String profileName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(profileName);
                    this.mRemote.transact(11, _data, _reply, 0);
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

            public boolean notificationGroupExistsByName(String notificationGroupName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(notificationGroupName);
                    this.mRemote.transact(12, _data, _reply, 0);
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

            public NotificationGroup[] getNotificationGroups() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    return (NotificationGroup[]) _reply.createTypedArray(NotificationGroup.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void addNotificationGroup(NotificationGroup group) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (group != null) {
                        _data.writeInt(1);
                        group.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeNotificationGroup(NotificationGroup group) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (group != null) {
                        _data.writeInt(1);
                        group.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void updateNotificationGroup(NotificationGroup group) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (group != null) {
                        _data.writeInt(1);
                        group.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public NotificationGroup getNotificationGroupForPackage(String pkg) throws RemoteException {
                NotificationGroup _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (NotificationGroup) NotificationGroup.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public NotificationGroup getNotificationGroup(ParcelUuid groupParcelUuid) throws RemoteException {
                NotificationGroup _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (groupParcelUuid != null) {
                        _data.writeInt(1);
                        groupParcelUuid.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (NotificationGroup) NotificationGroup.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void resetAll() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
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
