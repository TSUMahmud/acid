/**
 * Keep a persistent root shell running in the background
 * <p>
 * Copyright (C) 2013  Kevin Cernekee
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Kevin Cernekee
 * @version 1.0
 */

package dev.ukanth.ufirewall.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import dev.ukanth.ufirewall.Api;
import dev.ukanth.ufirewall.MainActivity;
import dev.ukanth.ufirewall.R;
import dev.ukanth.ufirewall.log.Log;
import dev.ukanth.ufirewall.util.G;
import eu.chainfire.libsuperuser.Debug;
import eu.chainfire.libsuperuser.Shell;



import static dev.ukanth.ufirewall.service.RootShellService2.ShellState2.INIT;


public class RootShellService2 extends Service {

    public static final String TAG = "AFWall6";
    public static final int NOTIFICATION_ID = 33347;
    public static final int EXIT_NO_ROOT_ACCESS = -1;
    public static final int NO_TOAST = -1;
    /* write command completion times to logcat */
    private static final boolean enableProfiling = false;
    //number of retries - increase the count
    private final static int MAX_RETRIES = 10;
    private static Shell.Interactive rootSession2;
    private Context mContext;
    private NotificationManager notificationManager;
    private static ShellState2 rootState = INIT;
    private final LinkedList<RootCommand> waitQueue = new LinkedList<>();

    private void complete(final RootCommand state, int exitCode) {
        if (enableProfiling) {
            Log.d(TAG, "RootShell6: " + state.getCommmands().size() + " commands completed in " +
                    (new Date().getTime() - state.startTime.getTime()) + " ms");
        }
        state.exitCode = exitCode;
        state.done = true;
        if (state.cb != null) {
            state.cb.cbFunc(state);
        }

        if (exitCode == 0 && state.successToast != NO_TOAST) {
            Api.sendToastBroadcast(this.mContext, mContext.getString(state.successToast));
        } else if (exitCode != 0 && state.failureToast != NO_TOAST) {
            Api.sendToastBroadcast(mContext, mContext.getString(state.failureToast));
        }

        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

    private void runNextSubmission() {

        do {
            RootCommand state;
            try {
                state = waitQueue.remove();
            } catch (NoSuchElementException e) {
                // nothing left to do
                if (rootState == ShellState2.BUSY) {
                    rootState = ShellState2.READY;
                }
                break;
            }
            if (state != null) {
                //same as last one. ignore it
                Log.i(TAG, "Start processing next state(6)");
                if (enableProfiling) {
                    state.startTime = new Date();
                }
                if (rootState == ShellState2.FAIL) {
                    // if we don't have root, abort all queued commands
                    complete(state, EXIT_NO_ROOT_ACCESS);
                    //continue;
                } else if (rootState == ShellState2.READY) {
                    rootState = ShellState2.BUSY;
                    if (G.isRun()) {
                        createNotification(mContext);
                    }
                    processCommands(state);
                }
            }
        } while (false);
    }

    private void processCommands(final RootCommand state) {
        if (state.commandIndex < state.getCommmands().size() && state.getCommmands().get(state.commandIndex) != null) {
            String command = state.getCommmands().get(state.commandIndex);
            //Log.i("AFWall", command);
            //not to send conflicting status
            sendUpdate(state);

            if (command != null) {
                state.ignoreExitCode = false;

                if (command.startsWith("#NOCHK# ")) {
                    command = command.replaceFirst("#NOCHK# ", "");
                    state.ignoreExitCode = true;
                }
                state.lastCommand = command;
                state.lastCommandResult = new StringBuilder();
                try {
                   rootSession2.addCommand(command, 0, (Shell.OnCommandResultListener2) (commandCode, exitCode, output, STDERR) -> {
                                ListIterator<String> iter = output.listIterator();
                                while (iter.hasNext()) {
                                    String line = iter.next();
                                    if (line != null && !line.equals("")) {
                                        if (state.res != null) {
                                            state.res.append(line).append("\n");
                                        }
                                        state.lastCommandResult.append(line).append("\n");
                                    }
                                }
                                if (exitCode >= 0 && exitCode == state.retryExitCode && state.retryCount < MAX_RETRIES) {
                                    //lets wait for few ms before trying ?
                                    state.retryCount++;
                                    Log.d(TAG, "command '" + state.lastCommand + "' exited with status " + exitCode +
                                            ", retrying (attempt " + state.retryCount + "/" + MAX_RETRIES + ")");
                                    processCommands(state);
                                    return;
                                }

                                state.commandIndex++;
                                state.retryCount = 0;

                                boolean errorExit = exitCode != 0 && !state.ignoreExitCode;
                                if (state.commandIndex >= state.getCommmands().size() || errorExit) {
                                    complete(state, exitCode);
                                    if (exitCode < 0) {
                                        rootState = ShellState2.FAIL;
                                        Log.e(TAG, "libsuperuser error " + exitCode + " on command '" + state.lastCommand + "'");
                                    } else {
                                        if (errorExit) {
                                            Log.i(TAG, "command '" + state.lastCommand + "' exited with status " + exitCode +
                                                    "\nOutput:\n" + state.lastCommandResult);
                                        }
                                        rootState = ShellState2.READY;
                                    }
                                    runNextSubmission();
                                } else {
                                    processCommands(state);
                                }
                            });
                } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        } else {
            complete(state, 0);
        }
    }

    private void sendUpdate(final RootCommand state2) {
        new Thread(() -> {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("UPDATEUI6");
            broadcastIntent.putExtra("SIZE", state2.getCommmands().size());
            broadcastIntent.putExtra("INDEX", state2.commandIndex);
            LocalBroadcastManager.getInstance(this.mContext).sendBroadcast(broadcastIntent);
        }).start();
    }

    private void createNotification(Context context) {

        String CHANNEL_ID = "firewall.apply";
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        Intent appIntent = new Intent(context, MainActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Create or update. */
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.runNotification),
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("");
            channel.setShowBadge(false);
            channel.setSound(null, null);
            channel.enableLights(false);
            channel.enableVibration(false);
            if(G.getNotificationPriority() == 0) {
                channel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
            }
            notificationManager.createNotificationChannel(channel);
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(appIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);


        int notifyType = G.getNotificationPriority();

        Notification notification = builder.setSmallIcon(R.drawable.ic_apply)
                .setAutoCancel(false)
                .setContentTitle(context.getString(R.string.applying_rules))
                .setTicker(context.getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setChannelId(CHANNEL_ID)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .setContentText("").build();
        builder.setProgress(0, 0, true);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) { // if crash restart...
            Log.i(TAG, "Restarting RootShell...");
            List<String> cmds = new ArrayList<>();
            cmds.add("true");
            new RootCommand().setFailureToast(R.string.error_su)
                    .setReopenShell(true).run(getApplicationContext(), cmds);
        }
        return Service.START_STICKY;
    }

    private void setupLogging() {
        Debug.setDebug(false);
        Debug.setLogTypeEnabled(Debug.LOG_ALL, false);
        Debug.setLogTypeEnabled(Debug.LOG_GENERAL, false);
        Debug.setSanityChecksEnabled(false);
        Debug.setOnLogListener((type, typeIndicator, message) -> Log.i(TAG, "[libsuperuser] " + message));
    }


    private void startShellInBackground() {
        Log.d(TAG, "Starting root shell(6)...");
        setupLogging();
        //start only rootSession is null
        if (rootSession2 == null) {
            rootSession2 = new Shell.Builder().
                useSU().
                setWatchdogTimeout(5).
                open((success, reason) -> {
                    if (reason < 0) {
                        Log.e(TAG, "Can't open root shell: exitCode " + reason);
                        rootState = ShellState2.FAIL;
                    } else {
                        Log.d(TAG, "Root shell(6) is open");
                        rootState = ShellState2.READY;
                    }
                    runNextSubmission();
                });
        }

    }

    private void reOpenShell(Context context) {
        if (rootState == null || rootState != ShellState2.READY || rootState == ShellState2.FAIL) {
            if (notificationManager != null) {
                notificationManager.cancel(NOTIFICATION_ID);
            }
            rootState = ShellState2.BUSY;
            startShellInBackground();
            Intent intent = new Intent(context, RootShellService2.class);
            context.startService(intent);
        }
    }


    public void runScriptAsRoot(Context ctx, List<String> cmds, RootCommand state) {
        Log.i(TAG, "Received cmds: #" + cmds.size());
        state.setCommmands(cmds);
        state.commandIndex = 0;
        state.retryCount = 0;
        if (mContext == null) {
            mContext = ctx.getApplicationContext();
        }
        //already in memory and applied
        //add it to queue

        waitQueue.add(state);

        if (rootState == INIT || (rootState == ShellState2.FAIL && state.reopenShell)) {
            reOpenShell(ctx);
        } else if (rootState != ShellState2.BUSY) {
            runNextSubmission();
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.i(TAG, "State of rootShell(6): " + rootState);
                    if (rootState == ShellState2.BUSY) {
                        //try resetting state to READY forcefully
                        Log.i(TAG, "Forcefully changing the state " + rootState);
                        rootState = ShellState2.READY;
                    }
                    runNextSubmission();
                }
            }, 1000);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public enum ShellState2 {
        INIT,
        READY,
        BUSY,
        FAIL
    }
}