package nodomain.freeyourgadget.gadgetbridge.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import p005ch.qos.logback.core.joran.action.Action;

public class UriHelper {
    private final Context context;
    private File file;
    private String fileName;
    private long fileSize;
    private final Uri uri;

    private UriHelper(Uri uri2, Context context2) {
        this.uri = uri2;
        this.context = context2;
    }

    public Uri getUri() {
        return this.uri;
    }

    public Context getContext() {
        return this.context;
    }

    public static UriHelper get(Uri uri2, Context context2) throws FileNotFoundException, IOException {
        UriHelper helper = new UriHelper(uri2, context2);
        helper.resolveMetadata();
        return helper;
    }

    public InputStream openInputStream() throws FileNotFoundException {
        InputStream inputStream = this.context.getContentResolver().openInputStream(this.uri);
        if (inputStream != null) {
            return new BufferedInputStream(inputStream);
        }
        throw new FileNotFoundException("Unable to open inputstream for " + this.uri);
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public String getFileName() {
        return this.fileName;
    }

    public File getFile() {
        return this.file;
    }

    private void resolveMetadata() throws IOException {
        String uriScheme = this.uri.getScheme();
        if ("content".equals(uriScheme)) {
            Cursor cursor = this.context.getContentResolver().query(this.uri, new String[]{"_display_name", "_size"}, (String) null, (String[]) null, (String) null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int name_index = cursor.getColumnIndex("_display_name");
                        if (name_index != -1) {
                            int size_index = cursor.getColumnIndex("_size");
                            if (size_index != -1) {
                                this.fileName = cursor.getString(name_index);
                                if (this.fileName != null) {
                                    this.fileSize = cursor.getLong(size_index);
                                    if (this.fileSize < 0) {
                                        throw new IOException("Unable to retrieve size for: " + this.uri);
                                    }
                                } else {
                                    throw new IOException("Unable to retrieve name for: " + this.uri);
                                }
                            } else {
                                throw new IOException("Unable to retrieve size for: " + this.uri);
                            }
                        } else {
                            throw new IOException("Unable to retrieve name for: " + this.uri);
                        }
                    }
                    cursor.close();
                } catch (Exception ex) {
                    throw new IOException("Unable to retrieve metadata for: " + this.uri + ": " + ex.getMessage());
                } catch (Throwable th) {
                    cursor.close();
                    throw th;
                }
            } else {
                throw new IOException("Unable to query metadata for: " + this.uri);
            }
        } else if (Action.FILE_ATTRIBUTE.equals(uriScheme)) {
            this.file = new File(this.uri.getPath());
            if (this.file.exists()) {
                this.fileName = this.file.getName();
                this.fileSize = this.file.length();
                return;
            }
            throw new FileNotFoundException("Does not exist: " + this.file);
        } else if ("android.resource".equals(uriScheme)) {
            throw new IOException("Unsupported scheme for uri: " + this.uri);
        } else {
            throw new IOException("Unsupported scheme for uri: " + this.uri);
        }
    }
}
