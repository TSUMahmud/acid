package cyanogenmod.media;

public class MediaRecorder {
    public static final String ACTION_HOTWORD_INPUT_CHANGED = "com.cyanogenmod.intent.action.HOTWORD_INPUT_CHANGED";
    public static final String CAPTURE_AUDIO_HOTWORD_PERMISSION = "android.permission.CAPTURE_AUDIO_HOTWORD";
    public static final String EXTRA_CURRENT_PACKAGE_NAME = "com.cyanogenmod.intent.extra.CURRENT_PACKAGE_NAME";
    public static final String EXTRA_HOTWORD_INPUT_STATE = "com.cyanogenmod.intent.extra.HOTWORD_INPUT_STATE";

    public static class AudioSource {
        public static final int HOTWORD = 1999;
    }
}
