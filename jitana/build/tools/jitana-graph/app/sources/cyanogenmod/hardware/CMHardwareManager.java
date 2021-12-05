package cyanogenmod.hardware;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.Range;
import cyanogenmod.app.CMContextConstants;
import cyanogenmod.hardware.ICMHardwareService;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.CharEncoding;

public final class CMHardwareManager {
    private static final List<Integer> BOOLEAN_FEATURES = Arrays.asList(new Integer[]{1, 2, 16, 32, 256, 512, 2048, 4096, 32768});
    public static final int COLOR_CALIBRATION_BLUE_INDEX = 2;
    public static final int COLOR_CALIBRATION_DEFAULT_INDEX = 3;
    public static final int COLOR_CALIBRATION_GREEN_INDEX = 1;
    public static final int COLOR_CALIBRATION_MAX_INDEX = 5;
    public static final int COLOR_CALIBRATION_MIN_INDEX = 4;
    public static final int COLOR_CALIBRATION_RED_INDEX = 0;
    public static final int FEATURE_ADAPTIVE_BACKLIGHT = 1;
    public static final int FEATURE_AUTO_CONTRAST = 4096;
    public static final int FEATURE_COLOR_BALANCE = 131072;
    public static final int FEATURE_COLOR_ENHANCEMENT = 2;
    public static final int FEATURE_DISPLAY_COLOR_CALIBRATION = 4;
    public static final int FEATURE_DISPLAY_GAMMA_CALIBRATION = 8;
    public static final int FEATURE_DISPLAY_MODES = 8192;
    public static final int FEATURE_HIGH_TOUCH_SENSITIVITY = 16;
    public static final int FEATURE_KEY_DISABLE = 32;
    public static final int FEATURE_LONG_TERM_ORBITS = 64;
    public static final int FEATURE_PERSISTENT_STORAGE = 16384;
    public static final int FEATURE_PICTURE_ADJUSTMENT = 262144;
    public static final int FEATURE_SERIAL_NUMBER = 128;
    public static final int FEATURE_SUNLIGHT_ENHANCEMENT = 256;
    public static final int FEATURE_TAP_TO_WAKE = 512;
    public static final int FEATURE_THERMAL_MONITOR = 32768;
    public static final int FEATURE_TOUCH_HOVERING = 2048;
    public static final int FEATURE_UNIQUE_DEVICE_ID = 65536;
    public static final int FEATURE_VIBRATOR = 1024;
    public static final int GAMMA_CALIBRATION_BLUE_INDEX = 2;
    public static final int GAMMA_CALIBRATION_GREEN_INDEX = 1;
    public static final int GAMMA_CALIBRATION_MAX_INDEX = 4;
    public static final int GAMMA_CALIBRATION_MIN_INDEX = 3;
    public static final int GAMMA_CALIBRATION_RED_INDEX = 0;
    private static final String TAG = "CMHardwareManager";
    public static final int VIBRATOR_DEFAULT_INDEX = 1;
    public static final int VIBRATOR_INTENSITY_INDEX = 0;
    public static final int VIBRATOR_MAX_INDEX = 3;
    public static final int VIBRATOR_MIN_INDEX = 2;
    public static final int VIBRATOR_WARNING_INDEX = 4;
    private static CMHardwareManager sCMHardwareManagerInstance;
    private static ICMHardwareService sService;
    private Context mContext;

    private CMHardwareManager(Context context) {
        Context appContext = context.getApplicationContext();
        if (appContext != null) {
            this.mContext = appContext;
        } else {
            this.mContext = context;
        }
        sService = getService();
        if (context.getPackageManager().hasSystemFeature(CMContextConstants.Features.HARDWARE_ABSTRACTION) && !checkService()) {
            Log.wtf(TAG, "Unable to get CMHardwareService. The service either crashed, was not started, or the interface has been called to early in SystemServer init");
        }
    }

    public static CMHardwareManager getInstance(Context context) {
        if (sCMHardwareManagerInstance == null) {
            sCMHardwareManagerInstance = new CMHardwareManager(context);
        }
        return sCMHardwareManagerInstance;
    }

    public static ICMHardwareService getService() {
        ICMHardwareService iCMHardwareService = sService;
        if (iCMHardwareService != null) {
            return iCMHardwareService;
        }
        IBinder b = ServiceManager.getService(CMContextConstants.CM_HARDWARE_SERVICE);
        if (b == null) {
            return null;
        }
        sService = ICMHardwareService.Stub.asInterface(b);
        return sService;
    }

    public int getSupportedFeatures() {
        try {
            if (checkService()) {
                return sService.getSupportedFeatures();
            }
            return 0;
        } catch (RemoteException e) {
            return 0;
        }
    }

    public boolean isSupported(int feature) {
        return feature == (getSupportedFeatures() & feature);
    }

    public boolean get(int feature) {
        if (BOOLEAN_FEATURES.contains(Integer.valueOf(feature))) {
            try {
                if (checkService()) {
                    return sService.get(feature);
                }
                return false;
            } catch (RemoteException e) {
                return false;
            }
        } else {
            throw new IllegalArgumentException(feature + " is not a boolean");
        }
    }

    public boolean set(int feature, boolean enable) {
        if (BOOLEAN_FEATURES.contains(Integer.valueOf(feature))) {
            try {
                if (checkService()) {
                    return sService.set(feature, enable);
                }
                return false;
            } catch (RemoteException e) {
                return false;
            }
        } else {
            throw new IllegalArgumentException(feature + " is not a boolean");
        }
    }

    private int getArrayValue(int[] arr, int idx, int defaultValue) {
        if (arr == null || arr.length <= idx) {
            return defaultValue;
        }
        return arr[idx];
    }

    private int[] getVibratorIntensityArray() {
        try {
            if (checkService()) {
                return sService.getVibratorIntensity();
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public int getVibratorIntensity() {
        return getArrayValue(getVibratorIntensityArray(), 0, 0);
    }

    public int getVibratorDefaultIntensity() {
        return getArrayValue(getVibratorIntensityArray(), 1, 0);
    }

    public int getVibratorMinIntensity() {
        return getArrayValue(getVibratorIntensityArray(), 2, 0);
    }

    public int getVibratorMaxIntensity() {
        return getArrayValue(getVibratorIntensityArray(), 3, 0);
    }

    public int getVibratorWarningIntensity() {
        return getArrayValue(getVibratorIntensityArray(), 4, 0);
    }

    public boolean setVibratorIntensity(int intensity) {
        try {
            if (checkService()) {
                return sService.setVibratorIntensity(intensity);
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    private int[] getDisplayColorCalibrationArray() {
        try {
            if (checkService()) {
                return sService.getDisplayColorCalibration();
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public int[] getDisplayColorCalibration() {
        int[] arr = getDisplayColorCalibrationArray();
        if (arr == null || arr.length < 3) {
            return null;
        }
        return Arrays.copyOf(arr, 3);
    }

    public int getDisplayColorCalibrationDefault() {
        return getArrayValue(getDisplayColorCalibrationArray(), 3, 0);
    }

    public int getDisplayColorCalibrationMin() {
        return getArrayValue(getDisplayColorCalibrationArray(), 4, 0);
    }

    public int getDisplayColorCalibrationMax() {
        return getArrayValue(getDisplayColorCalibrationArray(), 5, 0);
    }

    public boolean setDisplayColorCalibration(int[] rgb) {
        try {
            if (checkService()) {
                return sService.setDisplayColorCalibration(rgb);
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean writePersistentString(String key, String value) {
        try {
            if (!checkService()) {
                return false;
            }
            return sService.writePersistentBytes(key, value == null ? null : value.getBytes(CharEncoding.UTF_8));
        } catch (RemoteException e) {
            return false;
        } catch (UnsupportedEncodingException e2) {
            Log.e(TAG, e2.getMessage(), e2);
            return false;
        }
    }

    public boolean writePersistentInt(String key, int value) {
        try {
            if (checkService()) {
                return sService.writePersistentBytes(key, ByteBuffer.allocate(4).putInt(value).array());
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean writePersistentBytes(String key, byte[] value) {
        try {
            if (checkService()) {
                return sService.writePersistentBytes(key, value);
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public String readPersistentString(String key) {
        byte[] bytes;
        try {
            if (!checkService() || (bytes = sService.readPersistentBytes(key)) == null) {
                return null;
            }
            return new String(bytes, CharEncoding.UTF_8);
        } catch (RemoteException e) {
            return null;
        } catch (UnsupportedEncodingException e2) {
            Log.e(TAG, e2.getMessage(), e2);
            return null;
        }
    }

    public int readPersistentInt(String key) {
        byte[] bytes;
        try {
            if (!checkService() || (bytes = sService.readPersistentBytes(key)) == null) {
                return 0;
            }
            return ByteBuffer.wrap(bytes).getInt();
        } catch (RemoteException e) {
            return 0;
        }
    }

    public byte[] readPersistentBytes(String key) {
        try {
            if (checkService()) {
                return sService.readPersistentBytes(key);
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean deletePersistentObject(String key) {
        try {
            if (checkService()) {
                return sService.writePersistentBytes(key, (byte[]) null);
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    private int[] getDisplayGammaCalibrationArray(int idx) {
        try {
            if (checkService()) {
                return sService.getDisplayGammaCalibration(idx);
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    @Deprecated
    public int getNumGammaControls() {
        try {
            if (checkService()) {
                return sService.getNumGammaControls();
            }
            return 0;
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Deprecated
    public int[] getDisplayGammaCalibration(int idx) {
        int[] arr = getDisplayGammaCalibrationArray(idx);
        if (arr == null || arr.length < 3) {
            return null;
        }
        return Arrays.copyOf(arr, 3);
    }

    @Deprecated
    public int getDisplayGammaCalibrationMin() {
        return getArrayValue(getDisplayGammaCalibrationArray(0), 3, 0);
    }

    @Deprecated
    public int getDisplayGammaCalibrationMax() {
        return getArrayValue(getDisplayGammaCalibrationArray(0), 4, 0);
    }

    @Deprecated
    public boolean setDisplayGammaCalibration(int idx, int[] rgb) {
        try {
            if (checkService()) {
                return sService.setDisplayGammaCalibration(idx, rgb);
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public String getLtoSource() {
        try {
            if (checkService()) {
                return sService.getLtoSource();
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public String getLtoDestination() {
        try {
            if (checkService()) {
                return sService.getLtoDestination();
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public long getLtoDownloadInterval() {
        try {
            if (checkService()) {
                return sService.getLtoDownloadInterval();
            }
            return 0;
        } catch (RemoteException e) {
            return 0;
        }
    }

    public String getSerialNumber() {
        try {
            if (checkService()) {
                return sService.getSerialNumber();
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public String getUniqueDeviceId() {
        try {
            if (checkService()) {
                return sService.getUniqueDeviceId();
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean requireAdaptiveBacklightForSunlightEnhancement() {
        try {
            if (checkService()) {
                return sService.requireAdaptiveBacklightForSunlightEnhancement();
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean isSunlightEnhancementSelfManaged() {
        try {
            if (checkService()) {
                return sService.isSunlightEnhancementSelfManaged();
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public DisplayMode[] getDisplayModes() {
        try {
            if (checkService()) {
                return sService.getDisplayModes();
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public DisplayMode getCurrentDisplayMode() {
        try {
            if (checkService()) {
                return sService.getCurrentDisplayMode();
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public DisplayMode getDefaultDisplayMode() {
        try {
            if (checkService()) {
                return sService.getDefaultDisplayMode();
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean setDisplayMode(DisplayMode mode, boolean makeDefault) {
        try {
            if (checkService()) {
                return sService.setDisplayMode(mode, makeDefault);
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public Range<Integer> getColorBalanceRange() {
        int min = 0;
        int max = 0;
        try {
            if (checkService()) {
                min = sService.getColorBalanceMin();
                max = sService.getColorBalanceMax();
            }
        } catch (RemoteException e) {
        }
        return new Range<>(Integer.valueOf(min), Integer.valueOf(max));
    }

    public int getColorBalance() {
        try {
            if (checkService()) {
                return sService.getColorBalance();
            }
            return 0;
        } catch (RemoteException e) {
            return 0;
        }
    }

    public boolean setColorBalance(int value) {
        try {
            if (checkService()) {
                return sService.setColorBalance(value);
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public HSIC getPictureAdjustment() {
        try {
            if (checkService()) {
                return sService.getPictureAdjustment();
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public HSIC getDefaultPictureAdjustment() {
        try {
            if (checkService()) {
                return sService.getDefaultPictureAdjustment();
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean setPictureAdjustment(HSIC hsic) {
        try {
            if (checkService()) {
                return sService.setPictureAdjustment(hsic);
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public List<Range<Float>> getPictureAdjustmentRanges() {
        try {
            if (!checkService()) {
                return null;
            }
            float[] ranges = sService.getPictureAdjustmentRanges();
            if (ranges.length <= 7) {
                return null;
            }
            Range[] rangeArr = new Range[5];
            rangeArr[0] = new Range(Float.valueOf(ranges[0]), Float.valueOf(ranges[1]));
            rangeArr[1] = new Range(Float.valueOf(ranges[2]), Float.valueOf(ranges[3]));
            rangeArr[2] = new Range(Float.valueOf(ranges[4]), Float.valueOf(ranges[5]));
            rangeArr[3] = new Range(Float.valueOf(ranges[6]), Float.valueOf(ranges[7]));
            rangeArr[4] = ranges.length > 9 ? new Range(Float.valueOf(ranges[8]), Float.valueOf(ranges[9])) : new Range(Float.valueOf(0.0f), Float.valueOf(0.0f));
            return Arrays.asList(rangeArr);
        } catch (RemoteException e) {
            return null;
        }
    }

    private boolean checkService() {
        if (sService != null) {
            return true;
        }
        Log.w(TAG, "not connected to CMHardwareManagerService");
        return false;
    }

    public int getThermalState() {
        try {
            if (checkService()) {
                return sService.getThermalState();
            }
            return -1;
        } catch (RemoteException e) {
            return -1;
        }
    }

    public boolean registerThermalListener(ThermalListenerCallback thermalCallback) {
        try {
            if (checkService()) {
                return sService.registerThermalListener(thermalCallback);
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean unRegisterThermalListener(ThermalListenerCallback thermalCallback) {
        try {
            if (checkService()) {
                return sService.unRegisterThermalListener(thermalCallback);
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }
}
