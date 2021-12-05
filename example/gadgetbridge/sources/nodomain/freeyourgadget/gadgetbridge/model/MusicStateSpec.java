package nodomain.freeyourgadget.gadgetbridge.model;

import p005ch.qos.logback.core.CoreConstants;

public class MusicStateSpec {
    public static final int STATE_PAUSED = 1;
    public static final int STATE_PLAYING = 0;
    public static final int STATE_STOPPED = 2;
    public static final int STATE_UNKNOWN = 3;
    public int playRate;
    public int position;
    public byte repeat;
    public byte shuffle;
    public byte state;

    public MusicStateSpec() {
    }

    public MusicStateSpec(MusicStateSpec old) {
        this.state = old.state;
        this.position = old.position;
        this.playRate = old.playRate;
        this.shuffle = old.shuffle;
        this.repeat = old.repeat;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MusicStateSpec)) {
            return false;
        }
        MusicStateSpec stateSpec = (MusicStateSpec) obj;
        if (this.state == stateSpec.state && Math.abs(this.position - stateSpec.position) <= 2 && this.playRate == stateSpec.playRate && this.shuffle == stateSpec.shuffle && this.repeat == stateSpec.repeat) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((((this.state * 31) + this.playRate) * 31) + this.shuffle) * 31) + this.repeat;
    }

    public String toString() {
        return "MusicStateSpec{state=" + this.state + ", position=" + this.position + ", playRate=" + this.playRate + ", shuffle=" + this.shuffle + ", repeat=" + this.repeat + CoreConstants.CURLY_RIGHT;
    }
}
