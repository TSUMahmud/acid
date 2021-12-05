package nodomain.freeyourgadget.gadgetbridge.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import androidx.core.app.NotificationCompat;
import com.android.internal.telephony.ITelephony;
import java.lang.reflect.Method;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventCallControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GBCallControlReceiver extends BroadcastReceiver {
    public static final String ACTION_CALLCONTROL = "nodomain.freeyourgadget.gadgetbridge.callcontrol";
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) GBCallControlReceiver.class);

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.service.receivers.GBCallControlReceiver$1 */
    static /* synthetic */ class C12301 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$deviceevents$GBDeviceEventCallControl$Event */
        static final /* synthetic */ int[] f202x61c4288c = new int[GBDeviceEventCallControl.Event.values().length];

        static {
            try {
                f202x61c4288c[GBDeviceEventCallControl.Event.END.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f202x61c4288c[GBDeviceEventCallControl.Event.REJECT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f202x61c4288c[GBDeviceEventCallControl.Event.START.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public void onReceive(Context context, Intent intent) {
        GBDeviceEventCallControl.Event callCmd = GBDeviceEventCallControl.Event.values()[intent.getIntExtra(NotificationCompat.CATEGORY_EVENT, 0)];
        int i = C12301.f202x61c4288c[callCmd.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
                Method method = Class.forName(telephonyManager.getClass().getName()).getDeclaredMethod("getITelephony", new Class[0]);
                method.setAccessible(true);
                ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager, new Object[0]);
                if (callCmd != GBDeviceEventCallControl.Event.END) {
                    if (callCmd != GBDeviceEventCallControl.Event.REJECT) {
                        telephonyService.answerRingingCall();
                        return;
                    }
                }
                telephonyService.endCall();
            } catch (Exception e) {
                LOG.warn("could not start or hangup call");
            }
        }
    }
}
