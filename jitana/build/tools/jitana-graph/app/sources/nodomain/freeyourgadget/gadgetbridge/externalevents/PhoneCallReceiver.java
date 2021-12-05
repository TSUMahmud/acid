package nodomain.freeyourgadget.gadgetbridge.externalevents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;

public class PhoneCallReceiver extends BroadcastReceiver {
    private static int mLastState = 0;
    private static String mSavedNumber;
    private int mLastRingerMode;
    private boolean mRestoreMutedCall = false;

    public void onReceive(Context context, Intent intent) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            mSavedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        } else if (intent.getAction().equals("nodomain.freeyourgadget.gadgetbridge.MUTE_CALL")) {
            if (mLastState == 1) {
                AudioManager audioManager = (AudioManager) context.getSystemService("audio");
                this.mLastRingerMode = audioManager.getRingerMode();
                audioManager.setRingerMode(0);
                this.mRestoreMutedCall = true;
            }
        } else if (intent.hasExtra("incoming_number")) {
            onCallStateChanged(context, tm.getCallState(), intent.getExtras().getString("incoming_number"));
        }
    }

    public void onCallStateChanged(Context context, int state, String number) {
        int i = mLastState;
        if (i != state) {
            int callCommand = 1;
            if (state == 0) {
                if (i == 1) {
                    callCommand = 6;
                } else {
                    callCommand = 6;
                }
                if (this.mRestoreMutedCall) {
                    this.mRestoreMutedCall = false;
                    ((AudioManager) context.getSystemService("audio")).setRingerMode(this.mLastRingerMode);
                }
            } else if (state == 1) {
                mSavedNumber = number;
                callCommand = 2;
            } else if (state == 2) {
                if (i == 1) {
                    callCommand = 5;
                } else {
                    callCommand = 3;
                    mSavedNumber = number;
                }
            }
            if (callCommand != 1) {
                if (!"never".equals(GBApplication.getPrefs().getString("notification_mode_calls", "always"))) {
                    int grantedInterruptionFilter = GBApplication.getGrantedInterruptionFilter();
                    if (grantedInterruptionFilter != 1) {
                        if (grantedInterruptionFilter != 2) {
                            if (grantedInterruptionFilter == 3 || grantedInterruptionFilter == 4) {
                                return;
                            }
                        } else if (!GBApplication.isPriorityNumber(8, mSavedNumber)) {
                            return;
                        }
                    }
                    CallSpec callSpec = new CallSpec();
                    callSpec.number = mSavedNumber;
                    callSpec.command = callCommand;
                    GBApplication.deviceService().onSetCallState(callSpec);
                } else {
                    return;
                }
            }
            mLastState = state;
        }
    }
}
