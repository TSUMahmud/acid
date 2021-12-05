package nodomain.freeyourgadget.gadgetbridge.model;

import java.util.Objects;
import p005ch.qos.logback.core.CoreConstants;

public class MusicSpec {
    public static final int MUSIC_NEXT = 4;
    public static final int MUSIC_PAUSE = 2;
    public static final int MUSIC_PLAY = 1;
    public static final int MUSIC_PLAYPAUSE = 3;
    public static final int MUSIC_PREVIOUS = 5;
    public static final int MUSIC_UNDEFINED = 0;
    public String album;
    public String artist;
    public int duration;
    public String track;
    public int trackCount;
    public int trackNr;

    public MusicSpec() {
    }

    public MusicSpec(MusicSpec old) {
        this.duration = old.duration;
        this.trackCount = old.trackCount;
        this.trackNr = old.trackNr;
        this.track = old.track;
        this.album = old.album;
        this.artist = old.artist;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MusicSpec)) {
            return false;
        }
        MusicSpec musicSpec = (MusicSpec) obj;
        if (!Objects.equals(this.artist, musicSpec.artist) || !Objects.equals(this.album, musicSpec.album) || !Objects.equals(this.track, musicSpec.track) || this.duration != musicSpec.duration || this.trackCount != musicSpec.trackCount || this.trackNr != musicSpec.trackNr) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        String str = this.artist;
        int i = 0;
        int hashCode = (str != null ? str.hashCode() : 0) * 31;
        String str2 = this.album;
        int result = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        String str3 = this.track;
        if (str3 != null) {
            i = str3.hashCode();
        }
        return ((((((result + i) * 31) + this.duration) * 31) + this.trackCount) * 31) + this.trackNr;
    }

    public String toString() {
        return "MusicSpec{artist='" + this.artist + CoreConstants.SINGLE_QUOTE_CHAR + ", album='" + this.album + CoreConstants.SINGLE_QUOTE_CHAR + ", track='" + this.track + CoreConstants.SINGLE_QUOTE_CHAR + ", duration=" + this.duration + ", trackCount=" + this.trackCount + ", trackNr=" + this.trackNr + CoreConstants.CURLY_RIGHT;
    }
}
