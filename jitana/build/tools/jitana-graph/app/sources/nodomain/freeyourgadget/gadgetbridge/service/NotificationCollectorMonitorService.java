package nodomain.freeyourgadget.gadgetbridge.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Process;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.externalevents.NotificationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationCollectorMonitorService extends Service {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) NotificationCollectorMonitorService.class);

    public void onCreate() {
        super.onCreate();
        ensureCollectorRunning();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return 1;
    }

    private void ensureCollectorRunning() {
        String str;
        ComponentName collectorComponent = new ComponentName(this, NotificationListener.class);
        LOG.info("ensureCollectorRunning collectorComponent: " + collectorComponent);
        boolean collectorRunning = false;
        List<ActivityManager.RunningServiceInfo> runningServices = ((ActivityManager) getSystemService("activity")).getRunningServices(Integer.MAX_VALUE);
        if (runningServices == null) {
            LOG.info("ensureCollectorRunning() runningServices is NULL");
            return;
        }
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (service.service.equals(collectorComponent)) {
                Logger logger = LOG;
                StringBuilder sb = new StringBuilder();
                sb.append("ensureCollectorRunning service - pid: ");
                sb.append(service.pid);
                sb.append(", currentPID: ");
                sb.append(Process.myPid());
                sb.append(", clientPackage: ");
                sb.append(service.clientPackage);
                sb.append(", clientCount: ");
                sb.append(service.clientCount);
                sb.append(", clientLabel: ");
                if (service.clientLabel == 0) {
                    str = "0";
                } else {
                    str = "(" + getResources().getString(service.clientLabel) + ")";
                }
                sb.append(str);
                logger.warn(sb.toString());
                if (service.pid == Process.myPid()) {
                    collectorRunning = true;
                }
            }
        }
        if (collectorRunning) {
            LOG.debug("ensureCollectorRunning: collector is running");
            return;
        }
        LOG.debug("ensureCollectorRunning: collector not running, reviving...");
        toggleNotificationListenerService();
    }

    private void toggleNotificationListenerService() {
        LOG.debug("toggleNotificationListenerService() called");
        ComponentName thisComponent = new ComponentName(this, NotificationListener.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(thisComponent, 2, 1);
        pm.setComponentEnabledSetting(thisComponent, 1, 1);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
