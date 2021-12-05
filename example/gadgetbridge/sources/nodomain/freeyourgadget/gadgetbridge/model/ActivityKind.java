package nodomain.freeyourgadget.gadgetbridge.model;

import android.content.Context;
import java.util.Arrays;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;

public class ActivityKind {
    private static final int TYPES_COUNT = 12;
    public static final int TYPE_ACTIVITY = 1;
    public static final int TYPE_ALL = 15;
    public static final int TYPE_CYCLING = 128;
    public static final int TYPE_DEEP_SLEEP = 4;
    public static final int TYPE_EXERCISE = 512;
    public static final int TYPE_LIGHT_SLEEP = 2;
    public static final int TYPE_NOT_MEASURED = -1;
    public static final int TYPE_NOT_WORN = 8;
    public static final int TYPE_RUNNING = 16;
    public static final int TYPE_SLEEP = 6;
    public static final int TYPE_SWIMMING = 64;
    public static final int TYPE_TREADMILL = 256;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_WALKING = 32;

    public static int[] mapToDBActivityTypes(int types, SampleProvider provider) {
        int[] result = new int[12];
        int i = 0;
        if ((types & 1) != 0) {
            result[0] = provider.toRawActivityKind(1);
            i = 0 + 1;
        }
        if ((types & 4) != 0) {
            result[i] = provider.toRawActivityKind(4);
            i++;
        }
        if ((types & 2) != 0) {
            result[i] = provider.toRawActivityKind(2);
            i++;
        }
        if ((types & 8) != 0) {
            result[i] = provider.toRawActivityKind(8);
            i++;
        }
        if ((types & 16) != 0) {
            result[i] = provider.toRawActivityKind(16);
            i++;
        }
        if ((types & 32) != 0) {
            result[i] = provider.toRawActivityKind(32);
            i++;
        }
        if ((types & 64) != 0) {
            result[i] = provider.toRawActivityKind(64);
            i++;
        }
        if ((types & 128) != 0) {
            result[i] = provider.toRawActivityKind(128);
            i++;
        }
        if ((types & 256) != 0) {
            result[i] = provider.toRawActivityKind(256);
            i++;
        }
        if ((types & 512) != 0) {
            result[i] = provider.toRawActivityKind(512);
            i++;
        }
        return Arrays.copyOf(result, i);
    }

    public static String asString(int kind, Context context) {
        if (kind == -1) {
            return context.getString(C0889R.string.activity_type_not_measured);
        }
        if (kind == 4) {
            return context.getString(C0889R.string.activity_type_deep_sleep);
        }
        if (kind == 8) {
            return context.getString(C0889R.string.activity_type_not_worn);
        }
        if (kind == 16) {
            return context.getString(C0889R.string.activity_type_running);
        }
        if (kind == 32) {
            return context.getString(C0889R.string.activity_type_walking);
        }
        if (kind == 64) {
            return context.getString(C0889R.string.activity_type_swimming);
        }
        if (kind == 128) {
            return context.getString(C0889R.string.activity_type_biking);
        }
        if (kind == 256) {
            return context.getString(C0889R.string.activity_type_treadmill);
        }
        if (kind == 512) {
            return context.getString(C0889R.string.activity_type_exercise);
        }
        if (kind != 1) {
            return kind != 2 ? context.getString(C0889R.string.activity_type_unknown) : context.getString(C0889R.string.activity_type_light_sleep);
        }
        return context.getString(C0889R.string.activity_type_activity);
    }

    public static int getIconId(int kind) {
        if (kind == -1) {
            return C0889R.C0890drawable.ic_activity_not_measured;
        }
        if (kind == 2 || kind == 4) {
            return C0889R.C0890drawable.ic_activity_sleep;
        }
        if (kind == 16) {
            return C0889R.C0890drawable.ic_activity_running;
        }
        if (kind == 32) {
            return C0889R.C0890drawable.ic_activity_walking;
        }
        if (kind == 64) {
            return C0889R.C0890drawable.ic_activity_swimming;
        }
        if (kind == 128) {
            return C0889R.C0890drawable.ic_activity_biking;
        }
        if (kind == 256) {
            return C0889R.C0890drawable.ic_activity_walking;
        }
        if (kind != 512) {
            return C0889R.C0890drawable.ic_activity_unknown;
        }
        return C0889R.C0890drawable.ic_activity_exercise;
    }
}
