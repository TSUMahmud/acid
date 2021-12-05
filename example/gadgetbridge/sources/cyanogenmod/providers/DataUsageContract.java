package cyanogenmod.providers;

import android.net.Uri;

public final class DataUsageContract {
    public static final String ACTIVE = "active";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://org.cyanogenmod.providers.datausage");
    public static final String BYTES = "bytes";
    public static final int COLUMN_OF_ACTIVE = 3;
    public static final int COLUMN_OF_BYTES = 5;
    public static final int COLUMN_OF_ENABLE = 2;
    public static final int COLUMN_OF_EXTRA = 10;
    public static final int COLUMN_OF_FAST_AVG = 8;
    public static final int COLUMN_OF_FAST_SAMPLES = 9;
    public static final int COLUMN_OF_ID = 0;
    public static final int COLUMN_OF_LABEL = 4;
    public static final int COLUMN_OF_SLOW_AVG = 6;
    public static final int COLUMN_OF_SLOW_SAMPLES = 7;
    public static final int COLUMN_OF_UID = 1;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.itemdatausage_item";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dirdatausage_item";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, DATAUSAGE_TABLE);
    public static final String DATAUSAGE_AUTHORITY = "org.cyanogenmod.providers.datausage";
    public static final String DATAUSAGE_TABLE = "datausage";
    public static final String ENABLE = "enable";
    public static final String EXTRA = "extra";
    public static final String FAST_AVG = "fast_avg";
    public static final String FAST_SAMPLES = "fast_samples";
    public static final String LABEL = "label";
    public static final String[] PROJECTION_ALL = {"_id", UID, ENABLE, ACTIVE, "label", BYTES, SLOW_AVG, SLOW_SAMPLES, FAST_AVG, FAST_SAMPLES, EXTRA};
    public static final String SLOW_AVG = "slow_avg";
    public static final String SLOW_SAMPLES = "slow_samples";
    public static final String UID = "uid";
    public static final String _ID = "_id";
}
