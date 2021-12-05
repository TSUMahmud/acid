package nodomain.freeyourgadget.gadgetbridge.model;

import java.util.Calendar;
import java.util.Date;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;

public class ActivityUser {
    public static final int GENDER_FEMALE = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_OTHER = 2;
    public static final String PREF_USER_ACTIVETIME_MINUTES = "activity_user_activetime_minutes";
    public static final String PREF_USER_CALORIES_BURNT = "activity_user_calories_burnt";
    public static final String PREF_USER_DISTANCE_METERS = "activity_user_distance_meters";
    public static final String PREF_USER_GENDER = "activity_user_gender";
    public static final String PREF_USER_HEIGHT_CM = "activity_user_height_cm";
    public static final String PREF_USER_NAME = "mi_user_alias";
    public static final String PREF_USER_SLEEP_DURATION = "activity_user_sleep_duration";
    public static final String PREF_USER_STEPS_GOAL = "mi_fitness_goal";
    public static final String PREF_USER_WEIGHT_KG = "activity_user_weight_kg";
    public static final String PREF_USER_YEAR_OF_BIRTH = "activity_user_year_of_birth";
    public static final int defaultUserActiveTimeMinutes = 60;
    public static final int defaultUserAge = 0;
    public static final int defaultUserCaloriesBurnt = 2000;
    public static final int defaultUserDistanceMeters = 5000;
    public static final int defaultUserGender = 0;
    public static final int defaultUserHeightCm = 175;
    private static final String defaultUserName = "gadgetbridge-user";
    public static final int defaultUserSleepDuration = 7;
    public static final int defaultUserStepsGoal = 8000;
    public static final int defaultUserWeightKg = 70;
    public static final int defaultUserYearOfBirth = 0;
    private int activityUserActiveTimeMinutes;
    private int activityUserCaloriesBurnt;
    private int activityUserDistanceMeters;
    private int activityUserGender;
    private int activityUserHeightCm;
    private String activityUserName;
    private int activityUserSleepDuration;
    private int activityUserStepsGoal;
    private int activityUserWeightKg;
    private int activityUserYearOfBirth;

    public ActivityUser() {
        fetchPreferences();
    }

    public String getName() {
        return this.activityUserName;
    }

    public int getWeightKg() {
        return this.activityUserWeightKg;
    }

    public int getGender() {
        return this.activityUserGender;
    }

    public int getYearOfBirth() {
        return this.activityUserYearOfBirth;
    }

    public int getHeightCm() {
        return this.activityUserHeightCm;
    }

    public int getSleepDuration() {
        int i = this.activityUserSleepDuration;
        if (i < 1 || i > 24) {
            this.activityUserSleepDuration = 7;
        }
        return this.activityUserSleepDuration;
    }

    public int getStepsGoal() {
        if (this.activityUserStepsGoal < 0) {
            this.activityUserStepsGoal = defaultUserStepsGoal;
        }
        return this.activityUserStepsGoal;
    }

    public int getAge() {
        int age;
        int userYear = getYearOfBirth();
        if (userYear <= 1900 || (age = Calendar.getInstance().get(1) - userYear) <= 0) {
            return 25;
        }
        return age;
    }

    private void fetchPreferences() {
        Prefs prefs = GBApplication.getPrefs();
        this.activityUserName = prefs.getString("mi_user_alias", "gadgetbridge-user");
        this.activityUserGender = prefs.getInt(PREF_USER_GENDER, 0);
        this.activityUserHeightCm = prefs.getInt(PREF_USER_HEIGHT_CM, defaultUserHeightCm);
        this.activityUserWeightKg = prefs.getInt(PREF_USER_WEIGHT_KG, 70);
        this.activityUserYearOfBirth = prefs.getInt(PREF_USER_YEAR_OF_BIRTH, 0);
        this.activityUserSleepDuration = prefs.getInt("activity_user_sleep_duration", 7);
        this.activityUserStepsGoal = prefs.getInt("mi_fitness_goal", defaultUserStepsGoal);
        this.activityUserCaloriesBurnt = prefs.getInt("activity_user_calories_burnt", defaultUserCaloriesBurnt);
        this.activityUserDistanceMeters = prefs.getInt("activity_user_distance_meters", 5000);
        this.activityUserActiveTimeMinutes = prefs.getInt("activity_user_activetime_minutes", 60);
    }

    public Date getUserBirthday() {
        Calendar cal = DateTimeUtils.getCalendarUTC();
        cal.set(1, getYearOfBirth());
        return cal.getTime();
    }

    public int getCaloriesBurnt() {
        if (this.activityUserCaloriesBurnt < 0) {
            this.activityUserCaloriesBurnt = defaultUserCaloriesBurnt;
        }
        return this.activityUserCaloriesBurnt;
    }

    public int getDistanceMeters() {
        if (this.activityUserDistanceMeters < 0) {
            this.activityUserDistanceMeters = 5000;
        }
        return this.activityUserDistanceMeters;
    }

    public int getActiveTimeMinutes() {
        if (this.activityUserActiveTimeMinutes < 0) {
            this.activityUserActiveTimeMinutes = 60;
        }
        return this.activityUserActiveTimeMinutes;
    }
}
