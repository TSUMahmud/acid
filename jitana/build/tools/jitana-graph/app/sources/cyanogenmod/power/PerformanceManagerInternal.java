package cyanogenmod.power;

import android.content.Intent;

public interface PerformanceManagerInternal {
    void activityResumed(Intent intent);

    void cpuBoost(int i);

    void launchBoost(int i, String str);
}
