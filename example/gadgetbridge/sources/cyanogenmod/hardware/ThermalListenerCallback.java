package cyanogenmod.hardware;

import cyanogenmod.hardware.IThermalListenerCallback;

public abstract class ThermalListenerCallback extends IThermalListenerCallback.Stub {

    public static final class State {
        public static final int STATE_COOL = 0;
        public static final int STATE_CRITICAL = 3;
        public static final int STATE_UNKNOWN = -1;
        public static final int STATE_WARM_FALLING = 1;
        public static final int STATE_WARM_RISING = 2;

        public static final String toString(int state) {
            if (state == 0) {
                return "STATE_COOL";
            }
            if (state == 1) {
                return "STATE_WARM_FALLING";
            }
            if (state == 2) {
                return "STATE_WARM_RISING";
            }
            if (state != 3) {
                return "STATE_UNKNOWN";
            }
            return "STATE_CRITICAL";
        }
    }
}
