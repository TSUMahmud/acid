package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import android.content.Context;
import androidx.recyclerview.widget.ItemTouchHelper;
import cyanogenmod.externalviews.ExternalViewProviderService;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.AlertLevel;

public class VibrationProfile {
    public static final String ID_ALARM_CLOCK;
    public static final String ID_LONG;
    public static final String ID_MEDIUM;
    public static final String ID_RING;
    public static final String ID_SHORT;
    public static final String ID_STACCATO;
    public static final String ID_WATERDROP;
    private int alertLevel = AlertLevel.MildAlert.getId();

    /* renamed from: id */
    private final String f130id;
    private final int[] onOffSequence;
    private final short repeat;

    static {
        Context CONTEXT = GBApplication.getContext();
        ID_STACCATO = CONTEXT.getString(C0889R.string.p_staccato);
        ID_SHORT = CONTEXT.getString(C0889R.string.p_short);
        ID_MEDIUM = CONTEXT.getString(C0889R.string.p_medium);
        ID_LONG = CONTEXT.getString(C0889R.string.p_long);
        ID_WATERDROP = CONTEXT.getString(C0889R.string.p_waterdrop);
        ID_RING = CONTEXT.getString(C0889R.string.p_ring);
        ID_ALARM_CLOCK = CONTEXT.getString(C0889R.string.p_alarm_clock);
    }

    public static VibrationProfile getProfile(String id, short repeat2) {
        if (ID_STACCATO.equals(id)) {
            return new VibrationProfile(id, new int[]{100, 0}, repeat2);
        }
        if (ID_SHORT.equals(id)) {
            return new VibrationProfile(id, new int[]{ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION}, repeat2);
        }
        if (ID_LONG.equals(id)) {
            return new VibrationProfile(id, new int[]{500, 1000}, repeat2);
        }
        if (ID_WATERDROP.equals(id)) {
            return new VibrationProfile(id, new int[]{100, 1500}, repeat2);
        }
        if (ID_RING.equals(id)) {
            return new VibrationProfile(id, new int[]{300, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 600, ActivityUser.defaultUserCaloriesBurnt}, repeat2);
        }
        if (ID_ALARM_CLOCK.equals(id)) {
            return new VibrationProfile(id, new int[]{30, 35, 30, 35, 30, 35, 30, ExternalViewProviderService.Provider.DEFAULT_WINDOW_FLAGS}, repeat2);
        }
        return new VibrationProfile(id, new int[]{300, 600}, repeat2);
    }

    public VibrationProfile(String id, int[] onOffSequence2, short repeat2) {
        this.f130id = id;
        this.repeat = repeat2;
        this.onOffSequence = onOffSequence2;
    }

    public String getId() {
        return this.f130id;
    }

    public int[] getOnOffSequence() {
        return this.onOffSequence;
    }

    public short getRepeat() {
        return this.repeat;
    }

    public void setAlertLevel(int alertLevel2) {
        this.alertLevel = alertLevel2;
    }

    public int getAlertLevel() {
        return this.alertLevel;
    }
}
