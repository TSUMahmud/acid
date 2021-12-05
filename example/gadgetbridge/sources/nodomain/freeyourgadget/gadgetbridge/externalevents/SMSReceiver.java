package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;

public class SMSReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Object[] pdus;
        Prefs prefs = GBApplication.getPrefs();
        if (!"never".equals(prefs.getString("notification_mode_sms", "when_screen_off"))) {
            if ("when_screen_off".equals(prefs.getString("notification_mode_sms", "when_screen_off"))) {
                PowerManager powermanager = (PowerManager) context.getSystemService("power");
                if (powermanager != null && powermanager.isScreenOn()) {
                    return;
                }
            } else {
                Context context2 = context;
            }
            NotificationSpec notificationSpec = new NotificationSpec();
            notificationSpec.type = NotificationType.GENERIC_SMS;
            Bundle bundle = intent.getExtras();
            if (bundle != null && (pdus = (Object[]) bundle.get("pdus")) != null) {
                int pduSize = pdus.length;
                Map<String, StringBuilder> messageMap = new LinkedHashMap<>();
                SmsMessage[] messages = new SmsMessage[pduSize];
                for (int i = 0; i < pduSize; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    String originatingAddress = messages[i].getOriginatingAddress();
                    if (!messageMap.containsKey(originatingAddress)) {
                        messageMap.put(originatingAddress, new StringBuilder());
                    }
                    messageMap.get(originatingAddress).append(messages[i].getMessageBody());
                }
                for (Map.Entry<String, StringBuilder> entry : messageMap.entrySet()) {
                    String originatingAddress2 = entry.getKey();
                    if (originatingAddress2 != null) {
                        notificationSpec.body = entry.getValue().toString();
                        notificationSpec.phoneNumber = originatingAddress2;
                        notificationSpec.attachedActions = new ArrayList<>();
                        NotificationSpec.Action replyAction = new NotificationSpec.Action();
                        replyAction.title = "Reply";
                        replyAction.type = 2;
                        notificationSpec.attachedActions.add(replyAction);
                        NotificationSpec.Action dismissAllAction = new NotificationSpec.Action();
                        dismissAllAction.title = "Dismiss All";
                        dismissAllAction.type = 4;
                        notificationSpec.attachedActions.add(dismissAllAction);
                        int grantedInterruptionFilter = GBApplication.getGrantedInterruptionFilter();
                        if (grantedInterruptionFilter != 1) {
                            if (grantedInterruptionFilter != 2) {
                                if (grantedInterruptionFilter == 3 || grantedInterruptionFilter == 4) {
                                    return;
                                }
                            } else if (!GBApplication.isPriorityNumber(4, notificationSpec.phoneNumber)) {
                                return;
                            }
                        }
                        GBApplication.deviceService().onNotification(notificationSpec);
                    }
                }
            }
        }
    }
}
