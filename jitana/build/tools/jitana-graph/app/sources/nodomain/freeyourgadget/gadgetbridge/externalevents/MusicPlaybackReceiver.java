package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cyanogenmod.providers.ThemesContract;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicPlaybackReceiver extends BroadcastReceiver {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) MusicPlaybackReceiver.class);
    private static MusicSpec lastMusicSpec = new MusicSpec();
    private static MusicStateSpec lastStateSpec = new MusicStateSpec();

    public void onReceive(Context context, Intent intent) {
        MusicSpec musicSpec = new MusicSpec(lastMusicSpec);
        MusicStateSpec stateSpec = new MusicStateSpec(lastStateSpec);
        Bundle incomingBundle = intent.getExtras();
        if (incomingBundle == null) {
            LOG.warn("Not processing incoming null bundle.");
            return;
        }
        for (String key : incomingBundle.keySet()) {
            Object incoming = incomingBundle.get(key);
            if ((incoming instanceof String) && "artist".equals(key)) {
                musicSpec.artist = (String) incoming;
            } else if ((incoming instanceof String) && "album".equals(key)) {
                musicSpec.album = (String) incoming;
            } else if ((incoming instanceof String) && "track".equals(key)) {
                musicSpec.track = (String) incoming;
            } else if ((incoming instanceof String) && ThemesContract.ThemesColumns.TITLE.equals(key) && musicSpec.track == null) {
                musicSpec.track = (String) incoming;
            } else if ((incoming instanceof Integer) && C1238GB.DISPLAY_MESSAGE_DURATION.equals(key)) {
                musicSpec.duration = ((Integer) incoming).intValue() / 1000;
            } else if ((incoming instanceof Long) && C1238GB.DISPLAY_MESSAGE_DURATION.equals(key)) {
                musicSpec.duration = ((Long) incoming).intValue() / 1000;
            } else if ((incoming instanceof Integer) && "position".equals(key)) {
                stateSpec.position = ((Integer) incoming).intValue() / 1000;
            } else if (!(incoming instanceof Long) || !"position".equals(key)) {
                int i = 0;
                if ((incoming instanceof Boolean) && "playing".equals(key)) {
                    stateSpec.state = ((Boolean) incoming).booleanValue() ^ true ? (byte) 1 : 0;
                    if (((Boolean) incoming).booleanValue()) {
                        i = 100;
                    }
                    stateSpec.playRate = (byte) i;
                } else if ((incoming instanceof String) && C1238GB.DISPLAY_MESSAGE_DURATION.equals(key)) {
                    musicSpec.duration = Integer.parseInt((String) incoming) / 1000;
                } else if ((incoming instanceof String) && "trackno".equals(key)) {
                    musicSpec.trackNr = Integer.parseInt((String) incoming);
                } else if ((incoming instanceof String) && "totaltrack".equals(key)) {
                    musicSpec.trackCount = Integer.parseInt((String) incoming);
                } else if ((incoming instanceof Integer) && "pos".equals(key)) {
                    stateSpec.position = ((Integer) incoming).intValue();
                } else if (!(incoming instanceof Integer) || !"repeat".equals(key)) {
                    if ((incoming instanceof Integer) && "shuffle".equals(key)) {
                        if (((Integer) incoming).intValue() > 0) {
                            stateSpec.shuffle = 1;
                        } else {
                            stateSpec.shuffle = 0;
                        }
                    }
                } else if (((Integer) incoming).intValue() > 0) {
                    stateSpec.repeat = 1;
                } else {
                    stateSpec.repeat = 0;
                }
            } else {
                stateSpec.position = ((Long) incoming).intValue() / 1000;
            }
        }
        if (!lastMusicSpec.equals(musicSpec)) {
            lastMusicSpec = musicSpec;
            Logger logger = LOG;
            logger.info("Update Music Info: " + musicSpec.artist + " / " + musicSpec.album + " / " + musicSpec.track);
            GBApplication.deviceService().onSetMusicInfo(musicSpec);
        } else {
            LOG.info("Got metadata changed intent, but nothing changed, ignoring.");
        }
        if (!lastStateSpec.equals(stateSpec)) {
            lastStateSpec = stateSpec;
            Logger logger2 = LOG;
            logger2.info("Update Music State: state=" + stateSpec.state + ", position= " + stateSpec.position);
            GBApplication.deviceService().onSetMusicState(stateSpec);
            return;
        }
        LOG.info("Got state changed intent, but not enough has changed, ignoring.");
    }
}
