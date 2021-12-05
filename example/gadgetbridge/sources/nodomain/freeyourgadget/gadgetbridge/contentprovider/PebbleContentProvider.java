package nodomain.freeyourgadget.gadgetbridge.contentprovider;

import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;

public class PebbleContentProvider extends ContentProvider {
    public static final int COLUMN_APPMSG_SUPPORT = 1;
    public static final int COLUMN_CONNECTED = 0;
    public static final int COLUMN_DATALOGGING_SUPPORT = 2;
    public static final int COLUMN_VERSION_MAJOR = 3;
    public static final int COLUMN_VERSION_MINOR = 4;
    public static final int COLUMN_VERSION_POINT = 5;
    public static final int COLUMN_VERSION_TAG = 6;
    static final Uri CONTENT_URI = Uri.parse(URL);
    static final String PROVIDER_NAME = "com.getpebble.android.provider";
    static final String URL = "content://com.getpebble.android.provider/state";
    public static final String[] columnNames = {"0", MiBandConst.MI_1, MiBandConst.MI_PRO, "3", "4", "5", "6"};
    /* access modifiers changed from: private */
    public GBDevice mGBDevice = null;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GBDevice.ACTION_DEVICE_CHANGED)) {
                GBDevice unused = PebbleContentProvider.this.mGBDevice = (GBDevice) intent.getParcelableExtra(GBDevice.EXTRA_DEVICE);
            }
        }
    };

    public boolean onCreate() {
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(this.mReceiver, new IntentFilter(GBDevice.ACTION_DEVICE_CHANGED));
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (!uri.equals(CONTENT_URI)) {
            return null;
        }
        MatrixCursor mc = new MatrixCursor(columnNames);
        int connected = 0;
        int pebbleKit = 0;
        if (GBApplication.getPrefs().getBoolean("pebble_enable_pebblekit", false)) {
            pebbleKit = 1;
        }
        String fwString = "unknown";
        GBDevice gBDevice = this.mGBDevice;
        if (gBDevice != null && gBDevice.getType() == DeviceType.PEBBLE && this.mGBDevice.isInitialized()) {
            connected = 1;
            fwString = this.mGBDevice.getFirmwareVersion();
        }
        mc.addRow(new Object[]{Integer.valueOf(connected), Integer.valueOf(pebbleKit), Integer.valueOf(pebbleKit), 3, 8, 2, fwString});
        return mc;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
