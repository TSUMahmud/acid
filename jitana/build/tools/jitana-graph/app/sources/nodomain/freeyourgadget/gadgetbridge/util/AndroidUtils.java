package nodomain.freeyourgadget.gadgetbridge.util;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import p005ch.qos.logback.core.joran.action.Action;

public class AndroidUtils {
    public static ParcelUuid[] toParcelUuids(Parcelable[] uuids) {
        if (uuids == null) {
            return null;
        }
        ParcelUuid[] uuids2 = new ParcelUuid[uuids.length];
        System.arraycopy(uuids, 0, uuids2, 0, uuids.length);
        return uuids2;
    }

    public static boolean safeUnregisterBroadcastReceiver(Context context, BroadcastReceiver receiver) {
        try {
            context.unregisterReceiver(receiver);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean safeUnregisterBroadcastReceiver(LocalBroadcastManager manager, BroadcastReceiver receiver) {
        try {
            manager.unregisterReceiver(receiver);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static void setLanguage(Context context, Locale language) {
        Configuration config = new Configuration();
        config.setLocale(language);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static String getTextColorHex(Context context) {
        int color;
        if (GBApplication.isDarkThemeEnabled()) {
            color = context.getResources().getColor(C0889R.color.primarytext_dark);
        } else {
            color = context.getResources().getColor(C0889R.color.primarytext_light);
        }
        return colorToHex(color);
    }

    public static String getBackgroundColorHex(Context context) {
        int color;
        if (GBApplication.isDarkThemeEnabled()) {
            color = context.getResources().getColor(C0889R.color.cardview_dark_background);
        } else {
            color = context.getResources().getColor(C0889R.color.cardview_light_background);
        }
        return colorToHex(color);
    }

    private static String colorToHex(int color) {
        return "#" + Integer.toHexString(Color.red(color)) + Integer.toHexString(Color.green(color)) + Integer.toHexString(Color.blue(color));
    }

    public static String getFilePath(Context context, Uri uri) throws IllegalArgumentException {
        try {
            String path = internalGetFilePath(context, uri);
            if (!TextUtils.isEmpty(path)) {
                return path;
            }
            throw new IllegalArgumentException("Unable to decode the given uri to a file path: " + uri);
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex2) {
            throw new IllegalArgumentException("Unable to decode the given uri to a file path: " + uri, ex2);
        }
    }

    private static String internalGetFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                String[] split = DocumentsContract.getDocumentId(uri).split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                String id = DocumentsContract.getDocumentId(uri);
                if (!TextUtils.isEmpty(id)) {
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                }
            } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String[] split2 = DocumentsContract.getDocumentId(uri).split(":");
                String type = split2[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split2[1]};
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{"_data"}, selection, selectionArgs, (String) null);
            int column_index = cursor.getColumnIndexOrThrow("_data");
            if (cursor.moveToFirst()) {
                return cursor.getString(column_index);
            }
        } else if (Action.FILE_ATTRIBUTE.equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        throw new IllegalArgumentException("Unable to decode the given uri to a file path: " + uri);
    }

    public static void viewFile(String path, String action, Context context) throws IOException {
        Intent intent = new Intent(action);
        File file = new File(path);
        Uri contentUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".screenshot_provider", file);
        intent.setFlags(65);
        intent.setDataAndType(contentUri, "application/gpx+xml");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, C0889R.string.activity_error_no_app_for_gpx, 1).show();
        }
    }
}
