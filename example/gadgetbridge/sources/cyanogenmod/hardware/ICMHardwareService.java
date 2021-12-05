package cyanogenmod.hardware;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import cyanogenmod.hardware.IThermalListenerCallback;

public interface ICMHardwareService extends IInterface {
    boolean get(int i) throws RemoteException;

    int getColorBalance() throws RemoteException;

    int getColorBalanceMax() throws RemoteException;

    int getColorBalanceMin() throws RemoteException;

    DisplayMode getCurrentDisplayMode() throws RemoteException;

    DisplayMode getDefaultDisplayMode() throws RemoteException;

    HSIC getDefaultPictureAdjustment() throws RemoteException;

    int[] getDisplayColorCalibration() throws RemoteException;

    int[] getDisplayGammaCalibration(int i) throws RemoteException;

    DisplayMode[] getDisplayModes() throws RemoteException;

    String getLtoDestination() throws RemoteException;

    long getLtoDownloadInterval() throws RemoteException;

    String getLtoSource() throws RemoteException;

    int getNumGammaControls() throws RemoteException;

    HSIC getPictureAdjustment() throws RemoteException;

    float[] getPictureAdjustmentRanges() throws RemoteException;

    String getSerialNumber() throws RemoteException;

    int getSupportedFeatures() throws RemoteException;

    int getThermalState() throws RemoteException;

    String getUniqueDeviceId() throws RemoteException;

    int[] getVibratorIntensity() throws RemoteException;

    boolean isSunlightEnhancementSelfManaged() throws RemoteException;

    byte[] readPersistentBytes(String str) throws RemoteException;

    boolean registerThermalListener(IThermalListenerCallback iThermalListenerCallback) throws RemoteException;

    boolean requireAdaptiveBacklightForSunlightEnhancement() throws RemoteException;

    boolean set(int i, boolean z) throws RemoteException;

    boolean setColorBalance(int i) throws RemoteException;

    boolean setDisplayColorCalibration(int[] iArr) throws RemoteException;

    boolean setDisplayGammaCalibration(int i, int[] iArr) throws RemoteException;

    boolean setDisplayMode(DisplayMode displayMode, boolean z) throws RemoteException;

    boolean setPictureAdjustment(HSIC hsic) throws RemoteException;

    boolean setVibratorIntensity(int i) throws RemoteException;

    boolean unRegisterThermalListener(IThermalListenerCallback iThermalListenerCallback) throws RemoteException;

    boolean writePersistentBytes(String str, byte[] bArr) throws RemoteException;

    public static abstract class Stub extends Binder implements ICMHardwareService {
        private static final String DESCRIPTOR = "cyanogenmod.hardware.ICMHardwareService";
        static final int TRANSACTION_get = 2;
        static final int TRANSACTION_getColorBalance = 29;
        static final int TRANSACTION_getColorBalanceMax = 28;
        static final int TRANSACTION_getColorBalanceMin = 27;
        static final int TRANSACTION_getCurrentDisplayMode = 17;
        static final int TRANSACTION_getDefaultDisplayMode = 18;
        static final int TRANSACTION_getDefaultPictureAdjustment = 32;
        static final int TRANSACTION_getDisplayColorCalibration = 4;
        static final int TRANSACTION_getDisplayGammaCalibration = 7;
        static final int TRANSACTION_getDisplayModes = 16;
        static final int TRANSACTION_getLtoDestination = 12;
        static final int TRANSACTION_getLtoDownloadInterval = 13;
        static final int TRANSACTION_getLtoSource = 11;
        static final int TRANSACTION_getNumGammaControls = 6;
        static final int TRANSACTION_getPictureAdjustment = 31;
        static final int TRANSACTION_getPictureAdjustmentRanges = 34;
        static final int TRANSACTION_getSerialNumber = 14;
        static final int TRANSACTION_getSupportedFeatures_0 = 1;
        static final int TRANSACTION_getThermalState = 22;
        static final int TRANSACTION_getUniqueDeviceId = 26;
        static final int TRANSACTION_getVibratorIntensity = 9;
        static final int TRANSACTION_isSunlightEnhancementSelfManaged = 25;
        static final int TRANSACTION_readPersistentBytes = 21;
        static final int TRANSACTION_registerThermalListener = 23;
        static final int TRANSACTION_requireAdaptiveBacklightForSunlightEnhancement = 15;
        static final int TRANSACTION_set = 3;
        static final int TRANSACTION_setColorBalance = 30;
        static final int TRANSACTION_setDisplayColorCalibration = 5;
        static final int TRANSACTION_setDisplayGammaCalibration = 8;
        static final int TRANSACTION_setDisplayMode = 19;
        static final int TRANSACTION_setPictureAdjustment = 33;
        static final int TRANSACTION_setVibratorIntensity = 10;
        static final int TRANSACTION_unRegisterThermalListener = 24;
        static final int TRANSACTION_writePersistentBytes = 20;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICMHardwareService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ICMHardwareService)) {
                return new Proxy(obj);
            }
            return (ICMHardwareService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            DisplayMode _arg0;
            HSIC _arg02;
            if (code != 1598968902) {
                boolean _arg1 = false;
                switch (code) {
                    case 1:
                        data.enforceInterface(DESCRIPTOR);
                        int _result = getSupportedFeatures();
                        reply.writeNoException();
                        reply.writeInt(_result);
                        return true;
                    case 2:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result2 = get(data.readInt());
                        reply.writeNoException();
                        reply.writeInt(_result2);
                        return true;
                    case 3:
                        data.enforceInterface(DESCRIPTOR);
                        int _arg03 = data.readInt();
                        if (data.readInt() != 0) {
                            _arg1 = true;
                        }
                        boolean _result3 = set(_arg03, _arg1);
                        reply.writeNoException();
                        reply.writeInt(_result3);
                        return true;
                    case 4:
                        data.enforceInterface(DESCRIPTOR);
                        int[] _result4 = getDisplayColorCalibration();
                        reply.writeNoException();
                        reply.writeIntArray(_result4);
                        return true;
                    case 5:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result5 = setDisplayColorCalibration(data.createIntArray());
                        reply.writeNoException();
                        reply.writeInt(_result5);
                        return true;
                    case 6:
                        data.enforceInterface(DESCRIPTOR);
                        int _result6 = getNumGammaControls();
                        reply.writeNoException();
                        reply.writeInt(_result6);
                        return true;
                    case 7:
                        data.enforceInterface(DESCRIPTOR);
                        int[] _result7 = getDisplayGammaCalibration(data.readInt());
                        reply.writeNoException();
                        reply.writeIntArray(_result7);
                        return true;
                    case 8:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result8 = setDisplayGammaCalibration(data.readInt(), data.createIntArray());
                        reply.writeNoException();
                        reply.writeInt(_result8);
                        return true;
                    case 9:
                        data.enforceInterface(DESCRIPTOR);
                        int[] _result9 = getVibratorIntensity();
                        reply.writeNoException();
                        reply.writeIntArray(_result9);
                        return true;
                    case 10:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result10 = setVibratorIntensity(data.readInt());
                        reply.writeNoException();
                        reply.writeInt(_result10);
                        return true;
                    case 11:
                        data.enforceInterface(DESCRIPTOR);
                        String _result11 = getLtoSource();
                        reply.writeNoException();
                        reply.writeString(_result11);
                        return true;
                    case 12:
                        data.enforceInterface(DESCRIPTOR);
                        String _result12 = getLtoDestination();
                        reply.writeNoException();
                        reply.writeString(_result12);
                        return true;
                    case 13:
                        data.enforceInterface(DESCRIPTOR);
                        long _result13 = getLtoDownloadInterval();
                        reply.writeNoException();
                        reply.writeLong(_result13);
                        return true;
                    case 14:
                        data.enforceInterface(DESCRIPTOR);
                        String _result14 = getSerialNumber();
                        reply.writeNoException();
                        reply.writeString(_result14);
                        return true;
                    case 15:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result15 = requireAdaptiveBacklightForSunlightEnhancement();
                        reply.writeNoException();
                        reply.writeInt(_result15);
                        return true;
                    case 16:
                        data.enforceInterface(DESCRIPTOR);
                        DisplayMode[] _result16 = getDisplayModes();
                        reply.writeNoException();
                        reply.writeTypedArray(_result16, 1);
                        return true;
                    case 17:
                        data.enforceInterface(DESCRIPTOR);
                        DisplayMode _result17 = getCurrentDisplayMode();
                        reply.writeNoException();
                        if (_result17 != null) {
                            reply.writeInt(1);
                            _result17.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 18:
                        data.enforceInterface(DESCRIPTOR);
                        DisplayMode _result18 = getDefaultDisplayMode();
                        reply.writeNoException();
                        if (_result18 != null) {
                            reply.writeInt(1);
                            _result18.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 19:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg0 = DisplayMode.CREATOR.createFromParcel(data);
                        } else {
                            _arg0 = null;
                        }
                        if (data.readInt() != 0) {
                            _arg1 = true;
                        }
                        boolean _result19 = setDisplayMode(_arg0, _arg1);
                        reply.writeNoException();
                        reply.writeInt(_result19);
                        return true;
                    case 20:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result20 = writePersistentBytes(data.readString(), data.createByteArray());
                        reply.writeNoException();
                        reply.writeInt(_result20);
                        return true;
                    case 21:
                        data.enforceInterface(DESCRIPTOR);
                        byte[] _result21 = readPersistentBytes(data.readString());
                        reply.writeNoException();
                        reply.writeByteArray(_result21);
                        return true;
                    case 22:
                        data.enforceInterface(DESCRIPTOR);
                        int _result22 = getThermalState();
                        reply.writeNoException();
                        reply.writeInt(_result22);
                        return true;
                    case 23:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result23 = registerThermalListener(IThermalListenerCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        reply.writeInt(_result23);
                        return true;
                    case 24:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result24 = unRegisterThermalListener(IThermalListenerCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        reply.writeInt(_result24);
                        return true;
                    case 25:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result25 = isSunlightEnhancementSelfManaged();
                        reply.writeNoException();
                        reply.writeInt(_result25);
                        return true;
                    case 26:
                        data.enforceInterface(DESCRIPTOR);
                        String _result26 = getUniqueDeviceId();
                        reply.writeNoException();
                        reply.writeString(_result26);
                        return true;
                    case 27:
                        data.enforceInterface(DESCRIPTOR);
                        int _result27 = getColorBalanceMin();
                        reply.writeNoException();
                        reply.writeInt(_result27);
                        return true;
                    case 28:
                        data.enforceInterface(DESCRIPTOR);
                        int _result28 = getColorBalanceMax();
                        reply.writeNoException();
                        reply.writeInt(_result28);
                        return true;
                    case 29:
                        data.enforceInterface(DESCRIPTOR);
                        int _result29 = getColorBalance();
                        reply.writeNoException();
                        reply.writeInt(_result29);
                        return true;
                    case 30:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result30 = setColorBalance(data.readInt());
                        reply.writeNoException();
                        reply.writeInt(_result30);
                        return true;
                    case 31:
                        data.enforceInterface(DESCRIPTOR);
                        HSIC _result31 = getPictureAdjustment();
                        reply.writeNoException();
                        if (_result31 != null) {
                            reply.writeInt(1);
                            _result31.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 32:
                        data.enforceInterface(DESCRIPTOR);
                        HSIC _result32 = getDefaultPictureAdjustment();
                        reply.writeNoException();
                        if (_result32 != null) {
                            reply.writeInt(1);
                            _result32.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 33:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg02 = HSIC.CREATOR.createFromParcel(data);
                        } else {
                            _arg02 = null;
                        }
                        boolean _result33 = setPictureAdjustment(_arg02);
                        reply.writeNoException();
                        reply.writeInt(_result33);
                        return true;
                    case 34:
                        data.enforceInterface(DESCRIPTOR);
                        float[] _result34 = getPictureAdjustmentRanges();
                        reply.writeNoException();
                        reply.writeFloatArray(_result34);
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ICMHardwareService {
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

            public int getSupportedFeatures() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean get(int feature) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(feature);
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

            public boolean set(int feature, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(feature);
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(3, _data, _reply, 0);
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

            public int[] getDisplayColorCalibration() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    return _reply.createIntArray();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean setDisplayColorCalibration(int[] rgb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(rgb);
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

            public int getNumGammaControls() throws RemoteException {
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

            public int[] getDisplayGammaCalibration(int idx) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(idx);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    return _reply.createIntArray();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean setDisplayGammaCalibration(int idx, int[] rgb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(idx);
                    _data.writeIntArray(rgb);
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

            public int[] getVibratorIntensity() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    return _reply.createIntArray();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean setVibratorIntensity(int intensity) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(intensity);
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

            public String getLtoSource() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getLtoDestination() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public long getLtoDownloadInterval() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readLong();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getSerialNumber() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean requireAdaptiveBacklightForSunlightEnhancement() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
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

            public DisplayMode[] getDisplayModes() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    return (DisplayMode[]) _reply.createTypedArray(DisplayMode.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public DisplayMode getCurrentDisplayMode() throws RemoteException {
                DisplayMode _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = DisplayMode.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public DisplayMode getDefaultDisplayMode() throws RemoteException {
                DisplayMode _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = DisplayMode.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean setDisplayMode(DisplayMode mode, boolean makeDefault) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (mode != null) {
                        _data.writeInt(1);
                        mode.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(makeDefault ? 1 : 0);
                    this.mRemote.transact(19, _data, _reply, 0);
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

            public boolean writePersistentBytes(String key, byte[] bytes) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeByteArray(bytes);
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

            public byte[] readPersistentBytes(String key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    return _reply.createByteArray();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getThermalState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean registerThermalListener(IThermalListenerCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(23, _data, _reply, 0);
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

            public boolean unRegisterThermalListener(IThermalListenerCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(24, _data, _reply, 0);
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

            public boolean isSunlightEnhancementSelfManaged() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(25, _data, _reply, 0);
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

            public String getUniqueDeviceId() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getColorBalanceMin() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getColorBalanceMax() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getColorBalance() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean setColorBalance(int value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(value);
                    this.mRemote.transact(30, _data, _reply, 0);
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

            public HSIC getPictureAdjustment() throws RemoteException {
                HSIC _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = HSIC.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public HSIC getDefaultPictureAdjustment() throws RemoteException {
                HSIC _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = HSIC.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean setPictureAdjustment(HSIC hsic) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean _result = false;
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (hsic != null) {
                        _data.writeInt(1);
                        hsic.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(33, _data, _reply, 0);
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

            public float[] getPictureAdjustmentRanges() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                    return _reply.createFloatArray();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
