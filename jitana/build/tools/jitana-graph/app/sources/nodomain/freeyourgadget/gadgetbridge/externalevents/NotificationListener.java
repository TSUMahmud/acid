package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.RemoteException;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.p000v4.media.MediaMetadataCompat;
import android.support.p000v4.media.session.MediaControllerCompat;
import android.support.p000v4.media.session.MediaSessionCompat;
import android.support.p000v4.media.session.PlaybackStateCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.media.app.NotificationCompat;
import androidx.palette.graphics.Palette;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import nodomain.freeyourgadget.gadgetbridge.BuildConfig;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilter;
import nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilterDao;
import nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilterEntry;
import nodomain.freeyourgadget.gadgetbridge.entities.NotificationFilterEntryDao;
import nodomain.freeyourgadget.gadgetbridge.model.AppNotificationType;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;
import nodomain.freeyourgadget.gadgetbridge.service.DeviceCommunicationService;
import nodomain.freeyourgadget.gadgetbridge.util.BitmapUtil;
import nodomain.freeyourgadget.gadgetbridge.util.LimitedQueue;
import nodomain.freeyourgadget.gadgetbridge.util.PebbleUtils;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p008de.greenrobot.dao.query.WhereCondition;

public class NotificationListener extends NotificationListenerService {
    public static final String ACTION_DISMISS = "nodomain.freeyourgadget.gadgetbridge.notificationlistener.action.dismiss";
    public static final String ACTION_DISMISS_ALL = "nodomain.freeyourgadget.gadgetbridge.notificationlistener.action.dismiss_all";
    public static final String ACTION_MUTE = "nodomain.freeyourgadget.gadgetbridge.notificationlistener.action.mute";
    public static final String ACTION_OPEN = "nodomain.freeyourgadget.gadgetbridge.notificationlistener.action.open";
    public static final String ACTION_REPLY = "nodomain.freeyourgadget.gadgetbridge.notificationlistener.action.reply";
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) NotificationListener.class);
    public static ArrayList<String> notificationStack = new ArrayList<>();
    private long activeCallPostTime;
    /* access modifiers changed from: private */
    public LimitedQueue mActionLookup = new LimitedQueue(32);
    /* access modifiers changed from: private */
    public LimitedQueue mNotificationHandleLookup = new LimitedQueue(128);
    /* access modifiers changed from: private */
    public LimitedQueue mPackageLookup = new LimitedQueue(64);
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                NotificationListener.LOG.warn("no action");
                return;
            }
            int handle = (int) intent.getLongExtra("handle", -1);
            char c = 65535;
            int i = 0;
            switch (action.hashCode()) {
                case -673326844:
                    if (action.equals(NotificationListener.ACTION_DISMISS_ALL)) {
                        c = 4;
                        break;
                    }
                    break;
                case -450119806:
                    if (action.equals(NotificationListener.ACTION_REPLY)) {
                        c = 5;
                        break;
                    }
                    break;
                case 208140431:
                    if (action.equals(GBApplication.ACTION_QUIT)) {
                        c = 0;
                        break;
                    }
                    break;
                case 678083201:
                    if (action.equals(NotificationListener.ACTION_MUTE)) {
                        c = 2;
                        break;
                    }
                    break;
                case 678137522:
                    if (action.equals(NotificationListener.ACTION_OPEN)) {
                        c = 1;
                        break;
                    }
                    break;
                case 1803718946:
                    if (action.equals(NotificationListener.ACTION_DISMISS)) {
                        c = 3;
                        break;
                    }
                    break;
            }
            if (c == 0) {
                NotificationListener.this.stopSelf();
            } else if (c == 1) {
                StatusBarNotification[] sbns = NotificationListener.this.getActiveNotifications();
                Long ts = (Long) NotificationListener.this.mNotificationHandleLookup.lookup(handle);
                if (ts == null) {
                    NotificationListener.LOG.info("could not lookup handle for open action");
                    return;
                }
                int length = sbns.length;
                while (i < length) {
                    StatusBarNotification sbn = sbns[i];
                    if (sbn.getPostTime() == ts.longValue()) {
                        try {
                            PendingIntent pi = sbn.getNotification().contentIntent;
                            if (pi != null) {
                                pi.send();
                            }
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                    i++;
                }
            } else if (c == 2) {
                String packageName = (String) NotificationListener.this.mPackageLookup.lookup(handle);
                if (packageName == null) {
                    NotificationListener.LOG.info("could not lookup handle for mute action");
                    return;
                }
                Logger access$000 = NotificationListener.LOG;
                access$000.info("going to mute " + packageName);
                GBApplication.addAppToNotifBlacklist(packageName);
            } else if (c == 3) {
                StatusBarNotification[] sbns2 = NotificationListener.this.getActiveNotifications();
                Long ts2 = (Long) NotificationListener.this.mNotificationHandleLookup.lookup(handle);
                if (ts2 == null) {
                    NotificationListener.LOG.info("could not lookup handle for dismiss action");
                    return;
                }
                int length2 = sbns2.length;
                while (i < length2) {
                    StatusBarNotification sbn2 = sbns2[i];
                    if (sbn2.getPostTime() == ts2.longValue()) {
                        if (GBApplication.isRunningLollipopOrLater()) {
                            NotificationListener.this.cancelNotification(sbn2.getKey());
                        } else {
                            int id = sbn2.getId();
                            NotificationListener.this.cancelNotification(sbn2.getPackageName(), sbn2.getTag(), id);
                        }
                    }
                    i++;
                }
            } else if (c == 4) {
                NotificationListener.this.cancelAllNotifications();
            } else if (c == 5) {
                NotificationCompat.Action wearableAction = (NotificationCompat.Action) NotificationListener.this.mActionLookup.lookup(handle);
                String reply = intent.getStringExtra("reply");
                if (wearableAction != null) {
                    PendingIntent actionIntent = wearableAction.getActionIntent();
                    Intent localIntent = new Intent();
                    localIntent.addFlags(268435456);
                    if (wearableAction.getRemoteInputs() != null) {
                        RemoteInput[] remoteInputs = wearableAction.getRemoteInputs();
                        Bundle extras = new Bundle();
                        extras.putCharSequence(remoteInputs[0].getResultKey(), reply);
                        RemoteInput.addResultsToIntent(remoteInputs, localIntent, extras);
                    }
                    try {
                        NotificationListener.LOG.info("will send exec intent to remote application");
                        actionIntent.send(context, 0, localIntent);
                        NotificationListener.this.mActionLookup.remove(handle);
                    } catch (PendingIntent.CanceledException e2) {
                        Logger access$0002 = NotificationListener.LOG;
                        access$0002.warn("replyToLastNotification error: " + e2.getLocalizedMessage());
                    }
                }
            }
        }
    };
    private HashMap<String, Long> notificationBurstPrevention = new HashMap<>();
    private HashMap<String, Long> notificationOldRepeatPrevention = new HashMap<>();

    public void onCreate() {
        super.onCreate();
        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(GBApplication.ACTION_QUIT);
        filterLocal.addAction(ACTION_OPEN);
        filterLocal.addAction(ACTION_DISMISS);
        filterLocal.addAction(ACTION_DISMISS_ALL);
        filterLocal.addAction(ACTION_MUTE);
        filterLocal.addAction(ACTION_REPLY);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, filterLocal);
    }

    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mReceiver);
        notificationStack.clear();
        super.onDestroy();
    }

    public String getAppName(String pkg) {
        PackageManager pm = getPackageManager();
        try {
            return (String) pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
        Prefs prefs;
        NotificationCompat.WearableExtender wearableExtender;
        Iterator<NotificationCompat.Action> it;
        Prefs prefs2 = GBApplication.getPrefs();
        notificationStack.remove(sbn.getPackageName());
        notificationStack.add(sbn.getPackageName());
        if (GBApplication.isRunningLollipopOrLater() && NotificationCompat.CATEGORY_CALL.equals(sbn.getNotification().category) && prefs2.getBoolean("notification_support_voip_calls", false)) {
            handleCallNotification(sbn);
        } else if (shouldIgnore(sbn)) {
            LOG.info("Ignore notification");
        } else {
            int grantedInterruptionFilter = GBApplication.getGrantedInterruptionFilter();
            if (grantedInterruptionFilter == 1 || !(grantedInterruptionFilter == 2 || grantedInterruptionFilter == 3 || grantedInterruptionFilter == 4)) {
                String source = sbn.getPackageName().toLowerCase();
                Notification notification = sbn.getNotification();
                if (!this.notificationOldRepeatPrevention.containsKey(source) || notification.when > this.notificationOldRepeatPrevention.get(source).longValue()) {
                    long min_timeout = ((long) prefs2.getInt("notifications_timeout", 0)) * 1000;
                    long cur_time = System.currentTimeMillis();
                    if (this.notificationBurstPrevention.containsKey(source)) {
                        long last_time = this.notificationBurstPrevention.get(source).longValue();
                        if (cur_time - last_time < min_timeout) {
                            Logger logger = LOG;
                            logger.info("Ignoring frequent notification, last one was " + (cur_time - last_time) + "ms ago");
                            return;
                        }
                    }
                    NotificationSpec notificationSpec = new NotificationSpec();
                    String name = getAppName(source);
                    if (name != null) {
                        notificationSpec.sourceName = name;
                    }
                    boolean preferBigText = false;
                    notificationSpec.sourceAppId = source;
                    notificationSpec.type = (NotificationType) AppNotificationType.getInstance().get(source);
                    if (source.startsWith("com.fsck.k9")) {
                        if (NotificationCompat.isGroupSummary(notification)) {
                            LOG.info("ignore K9 group summary");
                            return;
                        }
                        preferBigText = true;
                    }
                    if (notificationSpec.type == null) {
                        notificationSpec.type = NotificationType.UNKNOWN;
                    }
                    notificationSpec.pebbleColor = getPebbleColorForNotification(notificationSpec);
                    Logger logger2 = LOG;
                    logger2.info("Processing notification " + notificationSpec.getId() + " age: " + (System.currentTimeMillis() - notification.when) + " from source " + source + " with flags: " + notification.flags);
                    dissectNotificationTo(notification, notificationSpec, preferBigText);
                    if (checkNotificationContentForWhiteAndBlackList(sbn.getPackageName().toLowerCase(), notificationSpec.body)) {
                        if (!getApplicationContext().getPackageName().equals(source) || getApplicationContext().getString(C0889R.string.test_notification).equals(notificationSpec.title)) {
                            NotificationCompat.WearableExtender wearableExtender2 = new NotificationCompat.WearableExtender(notification);
                            List<NotificationCompat.Action> actions = wearableExtender2.getActions();
                            if (actions.size() != 0 || !NotificationCompat.isGroupSummary(notification)) {
                                notificationSpec.attachedActions = new ArrayList<>();
                                NotificationSpec.Action dismissAction = new NotificationSpec.Action();
                                dismissAction.title = "Dismiss";
                                dismissAction.type = 3;
                                notificationSpec.attachedActions.add(dismissAction);
                                Iterator<NotificationCompat.Action> it2 = actions.iterator();
                                while (it2.hasNext()) {
                                    NotificationCompat.Action act = it2.next();
                                    if (act != null) {
                                        NotificationSpec.Action wearableAction = new NotificationSpec.Action();
                                        prefs = prefs2;
                                        it = it2;
                                        NotificationSpec.Action wearableAction2 = wearableAction;
                                        wearableAction2.title = act.getTitle().toString();
                                        if (act.getRemoteInputs() != null) {
                                            wearableAction2.type = 1;
                                        } else {
                                            wearableAction2.type = 0;
                                        }
                                        notificationSpec.attachedActions.add(wearableAction2);
                                        NotificationSpec.Action action = wearableAction2;
                                        this.mActionLookup.add((notificationSpec.getId() << 4) + notificationSpec.attachedActions.size(), act);
                                        Logger logger3 = LOG;
                                        StringBuilder sb = new StringBuilder();
                                        wearableExtender = wearableExtender2;
                                        sb.append("found wearable action: ");
                                        sb.append(notificationSpec.attachedActions.size());
                                        sb.append(" - ");
                                        sb.append(act.getTitle());
                                        sb.append("  ");
                                        sb.append(sbn.getTag());
                                        logger3.info(sb.toString());
                                    } else {
                                        prefs = prefs2;
                                        it = it2;
                                        wearableExtender = wearableExtender2;
                                    }
                                    it2 = it;
                                    wearableExtender2 = wearableExtender;
                                    prefs2 = prefs;
                                }
                                NotificationCompat.WearableExtender wearableExtender3 = wearableExtender2;
                                NotificationSpec.Action openAction = new NotificationSpec.Action();
                                openAction.title = getString(C0889R.string._pebble_watch_open_on_phone);
                                openAction.type = 6;
                                notificationSpec.attachedActions.add(openAction);
                                NotificationSpec.Action muteAction = new NotificationSpec.Action();
                                muteAction.title = getString(C0889R.string._pebble_watch_mute);
                                muteAction.type = 5;
                                notificationSpec.attachedActions.add(muteAction);
                                NotificationSpec.Action action2 = openAction;
                                this.mNotificationHandleLookup.add(notificationSpec.getId(), Long.valueOf(sbn.getPostTime()));
                                this.mPackageLookup.add(notificationSpec.getId(), sbn.getPackageName());
                                this.notificationBurstPrevention.put(source, Long.valueOf(cur_time));
                                NotificationSpec.Action muteAction2 = muteAction;
                                if (0 != notification.when) {
                                    NotificationSpec.Action action3 = muteAction2;
                                    this.notificationOldRepeatPrevention.put(source, Long.valueOf(notification.when));
                                } else {
                                    Logger logger4 = LOG;
                                    logger4.info("This app might show old/duplicate notifications. notification.when is 0 for " + source);
                                }
                                GBApplication.deviceService().onNotification(notificationSpec);
                                return;
                            }
                            Logger logger5 = LOG;
                            logger5.info("Not forwarding notification, FLAG_GROUP_SUMMARY is set and no wearable action present. Notification flags: " + notification.flags);
                            return;
                        }
                        return;
                    }
                    return;
                }
                LOG.info("NOT processing notification, already sent newer notifications from this source.");
            }
        }
    }

    private boolean checkNotificationContentForWhiteAndBlackList(String packageName, String body) {
        Throwable th;
        Throwable th2;
        long start = System.currentTimeMillis();
        List<String> wordsList = new ArrayList<>();
        try {
            DBHandler db = GBApplication.acquireDB();
            try {
                NotificationFilterDao notificationFilterDao = db.getDaoSession().getNotificationFilterDao();
                NotificationFilterEntryDao notificationFilterEntryDao = db.getDaoSession().getNotificationFilterEntryDao();
                NotificationFilter notificationFilter = notificationFilterDao.queryBuilder().where(NotificationFilterDao.Properties.AppIdentifier.mo14989eq(packageName.toLowerCase()), new WhereCondition[0]).build().unique();
                if (notificationFilter == null) {
                    LOG.debug("No Notification Filter found");
                    if (db != null) {
                        db.close();
                    }
                    return true;
                }
                try {
                    LOG.debug("Loaded notification filter for '{}'", (Object) packageName);
                    List<NotificationFilterEntry> filterEntries = notificationFilterEntryDao.queryBuilder().where(NotificationFilterEntryDao.Properties.NotificationFilterId.mo14989eq(notificationFilter.getId()), new WhereCondition[0]).build().list();
                    if (BuildConfig.DEBUG) {
                        LOG.info("Database lookup took '{}' ms", (Object) Long.valueOf(System.currentTimeMillis() - start));
                    }
                    if (!filterEntries.isEmpty()) {
                        for (NotificationFilterEntry temp : filterEntries) {
                            wordsList.add(temp.getNotificationFilterContent());
                            Logger logger = LOG;
                            logger.debug("Loaded filter word: " + temp.getNotificationFilterContent());
                        }
                    }
                    if (db != null) {
                        try {
                            db.close();
                        } catch (Exception e) {
                            e = e;
                        }
                    }
                    return shouldContinueAfterFilter(body, wordsList, notificationFilter);
                } catch (Throwable th3) {
                    th = th3;
                    String str = body;
                    th = th;
                    try {
                        throw th;
                    } catch (Exception e2) {
                        e = e2;
                        LOG.error("Could not acquire DB.", (Throwable) e);
                        return true;
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                }
            } catch (Throwable th5) {
                th = th5;
                String str2 = packageName;
                String str3 = body;
                th = th;
                throw th;
            }
            throw th2;
        } catch (Exception e3) {
            e = e3;
            String str4 = packageName;
            String str5 = body;
            LOG.error("Could not acquire DB.", (Throwable) e);
            return true;
        }
    }

    private void handleCallNotification(StatusBarNotification sbn) {
        String appName;
        String app = sbn.getPackageName();
        LOG.debug("got call from: " + app);
        if (app.equals("com.android.dialer")) {
            LOG.debug("Ignoring non-voip call");
            return;
        }
        Notification noti = sbn.getNotification();
        dumpExtras(noti.extras);
        if (noti.actions != null && noti.actions.length > 0) {
            for (Notification.Action action : noti.actions) {
                LOG.info("Found call action: " + action.title);
            }
        }
        if (noti.extras.containsKey(NotificationCompat.EXTRA_PEOPLE)) {
            appName = noti.extras.getString(NotificationCompat.EXTRA_PEOPLE);
        } else if (noti.extras.containsKey(NotificationCompat.EXTRA_TITLE)) {
            appName = noti.extras.getString(NotificationCompat.EXTRA_TITLE);
        } else {
            String appName2 = getAppName(app);
            appName = appName2 != null ? appName2 : app;
        }
        this.activeCallPostTime = sbn.getPostTime();
        CallSpec callSpec = new CallSpec();
        callSpec.number = appName;
        callSpec.command = 2;
        GBApplication.deviceService().onSetCallState(callSpec);
    }

    /* access modifiers changed from: package-private */
    public boolean shouldContinueAfterFilter(String body, List<String> wordsList, NotificationFilter notificationFilter) {
        LOG.debug("Mode: '{}' Submode: '{}' WordsList: '{}'", Integer.valueOf(notificationFilter.getNotificationFilterMode()), Integer.valueOf(notificationFilter.getNotificationFilterSubMode()), wordsList);
        boolean allMode = notificationFilter.getNotificationFilterSubMode() == 1;
        int notificationFilterMode = notificationFilter.getNotificationFilterMode();
        if (notificationFilterMode != 1) {
            if (notificationFilterMode != 2) {
                return true;
            }
            if (allMode) {
                for (String word : wordsList) {
                    if (!body.contains(word)) {
                        LOG.info("Not every word was found, blacklist has no effect, processing continues.");
                        return true;
                    }
                }
                LOG.info("Every word was found, blacklist has effect, processing stops.");
                return false;
            }
            boolean containsAny = StringUtils.containsAny((CharSequence) body, (CharSequence[]) wordsList.toArray(new CharSequence[0]));
            if (!containsAny) {
                LOG.info("No matching word was found, blacklist has no effect, processing continues.");
            } else {
                LOG.info("At least one matching word was found, blacklist has effect, processing stops.");
            }
            return !containsAny;
        } else if (allMode) {
            for (String word2 : wordsList) {
                if (!body.contains(word2)) {
                    LOG.info("Not every word was found, whitelist has no effect, processing stops.");
                    return false;
                }
            }
            LOG.info("Every word was found, whitelist has effect, processing continues.");
            return true;
        } else {
            boolean containsAny2 = StringUtils.containsAny((CharSequence) body, (CharSequence[]) wordsList.toArray(new CharSequence[0]));
            if (containsAny2) {
                LOG.info("At least one matching word was found, whitelist has effect, processing continues.");
            } else {
                LOG.info("No matching word was found, whitelist has no effect, processing stops.");
            }
            return containsAny2;
        }
    }

    private String sanitizeUnicode(String orig) {
        return orig.replaceAll("\\p{C}", "");
    }

    private void dissectNotificationTo(Notification notification, NotificationSpec notificationSpec, boolean preferBigText) {
        Bundle extras = NotificationCompat.getExtras(notification);
        if (extras != null) {
            CharSequence title = extras.getCharSequence(NotificationCompat.EXTRA_TITLE);
            if (title != null) {
                notificationSpec.title = sanitizeUnicode(title.toString());
            }
            CharSequence contentCS = null;
            if (preferBigText && extras.containsKey(NotificationCompat.EXTRA_BIG_TEXT)) {
                contentCS = extras.getCharSequence(NotificationCompat.EXTRA_BIG_TEXT);
            } else if (extras.containsKey(NotificationCompat.EXTRA_TEXT)) {
                contentCS = extras.getCharSequence(NotificationCompat.EXTRA_TEXT);
            }
            if (contentCS != null) {
                notificationSpec.body = sanitizeUnicode(contentCS.toString());
            }
        }
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService("activity");
        if (manager == null) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (DeviceCommunicationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean handleMediaSessionNotification(MediaSessionCompat.Token mediaSession) {
        MusicSpec musicSpec = new MusicSpec();
        MusicStateSpec stateSpec = new MusicStateSpec();
        try {
            try {
                MediaControllerCompat c = new MediaControllerCompat(getApplicationContext(), mediaSession);
                PlaybackStateCompat s = c.getPlaybackState();
                stateSpec.position = (int) (s.getPosition() / 1000);
                stateSpec.playRate = Math.round(s.getPlaybackSpeed() * 100.0f);
                stateSpec.repeat = 1;
                stateSpec.shuffle = 1;
                int state = s.getState();
                if (state == 1) {
                    stateSpec.state = 2;
                } else if (state == 2) {
                    stateSpec.state = 1;
                } else if (state != 3) {
                    stateSpec.state = 3;
                } else {
                    stateSpec.state = 0;
                }
                MediaMetadataCompat d = c.getMetadata();
                if (d == null) {
                    return false;
                }
                if (d.containsKey(MediaMetadataCompat.METADATA_KEY_ARTIST)) {
                    musicSpec.artist = d.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
                }
                if (d.containsKey(MediaMetadataCompat.METADATA_KEY_ALBUM)) {
                    musicSpec.album = d.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
                }
                if (d.containsKey(MediaMetadataCompat.METADATA_KEY_TITLE)) {
                    musicSpec.track = d.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
                }
                if (d.containsKey(MediaMetadataCompat.METADATA_KEY_DURATION)) {
                    musicSpec.duration = ((int) d.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)) / 1000;
                }
                if (d.containsKey(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS)) {
                    musicSpec.trackCount = (int) d.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS);
                }
                if (d.containsKey(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER)) {
                    musicSpec.trackNr = (int) d.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER);
                }
                GBApplication.deviceService().onSetMusicInfo(musicSpec);
                GBApplication.deviceService().onSetMusicState(stateSpec);
                return true;
            } catch (RemoteException | NullPointerException e) {
                return false;
            }
        } catch (RemoteException | NullPointerException e2) {
            MediaSessionCompat.Token token = mediaSession;
            return false;
        }
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Logger logger = LOG;
        logger.info("Notification removed: " + sbn.getPackageName() + ": " + sbn.getNotification().category);
        notificationStack.remove(sbn.getPackageName());
        if (NotificationCompat.CATEGORY_CALL.equals(sbn.getNotification().category) && this.activeCallPostTime == sbn.getPostTime()) {
            this.activeCallPostTime = 0;
            CallSpec callSpec = new CallSpec();
            callSpec.command = 6;
            GBApplication.deviceService().onSetCallState(callSpec);
        }
        if (!shouldIgnore(sbn) && GBApplication.getPrefs().getBoolean("autoremove_notifications", true)) {
            LOG.info("notification removed, will ask device to delete it");
            GBApplication.deviceService().onDeleteNotification((int) sbn.getPostTime());
        }
    }

    private void dumpExtras(Bundle bundle) {
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            if (value != null) {
                LOG.debug(String.format("Notification extra: %s %s (%s)", new Object[]{key, value.toString(), value.getClass().getName()}));
            }
        }
    }

    private boolean shouldIgnore(StatusBarNotification sbn) {
        if (!isServiceRunning() || sbn == null || shouldIgnoreSource(sbn.getPackageName()) || shouldIgnoreNotification(sbn.getNotification(), sbn.getPackageName())) {
            return true;
        }
        return false;
    }

    private boolean shouldIgnoreSource(String source) {
        Prefs prefs = GBApplication.getPrefs();
        if (source.equals("android") || source.equals("com.android.systemui") || source.equals("com.android.dialer") || source.equals("com.cyanogenmod.eleven")) {
            LOG.info("Ignoring notification, is a system event");
            return true;
        } else if ((source.equals("com.moez.QKSMS") || source.equals("com.android.mms") || source.equals("com.sonyericsson.conversations") || source.equals("com.android.messaging") || source.equals("org.smssecure.smssecure")) && !"never".equals(prefs.getString("notification_mode_sms", "when_screen_off"))) {
            return true;
        } else {
            if (!GBApplication.appIsNotifBlacklisted(source)) {
                return false;
            }
            LOG.info("Ignoring notification, application is blacklisted");
            return true;
        }
    }

    private boolean shouldIgnoreNotification(Notification notification, String source) {
        PowerManager powermanager;
        MediaSessionCompat.Token mediaSession = NotificationCompat.MediaStyle.getMediaSession(notification);
        if (mediaSession != null && handleMediaSessionNotification(mediaSession)) {
            return true;
        }
        NotificationType type = (NotificationType) AppNotificationType.getInstance().get(source);
        if (androidx.core.app.NotificationCompat.getLocalOnly(notification) && type != NotificationType.WECHAT && type != NotificationType.OUTLOOK && type != NotificationType.SKYPE) {
            LOG.info("local only");
            return true;
        } else if ((GBApplication.getPrefs().getBoolean("notifications_generic_whenscreenon", false) || (powermanager = (PowerManager) getSystemService("power")) == null || !powermanager.isScreenOn()) && (notification.flags & 2) != 2) {
            return false;
        } else {
            return true;
        }
    }

    private byte getPebbleColorForNotification(NotificationSpec notificationSpec) {
        String appId = notificationSpec.sourceAppId;
        NotificationType existingType = notificationSpec.type;
        if (existingType != NotificationType.UNKNOWN) {
            return existingType.color;
        }
        try {
            Drawable icon = getApplicationContext().getPackageManager().getApplicationIcon(appId);
            Objects.requireNonNull(icon);
            return PebbleUtils.getPebbleColor(new Palette.Builder(BitmapUtil.convertDrawableToBitmap(icon)).generate().getVibrantColor(Color.parseColor("#aa0000")));
        } catch (Exception ex) {
            Logger logger = LOG;
            logger.warn("Could not get icon for AppID " + appId, (Throwable) ex);
            return PebbleColor.IslamicGreen;
        }
    }
}
