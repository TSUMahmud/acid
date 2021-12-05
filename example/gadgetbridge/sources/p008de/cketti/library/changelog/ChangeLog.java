package p008de.cketti.library.changelog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.webkit.WebView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.CharEncoding;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* renamed from: de.cketti.library.changelog.ChangeLog */
public class ChangeLog {
    public static final String DEFAULT_CSS = "h1 { margin-left: 0px; font-size: 1.2em; }\nli { margin-left: 0px; }\nul { padding-left: 2em; }";
    protected static final String LOG_TAG = "ckChangeLog";
    protected static final int NO_VERSION = -1;
    protected static final String VERSION_KEY = "ckChangeLog_last_version_code";
    protected final Context mContext;
    protected final String mCss;
    private int mCurrentVersionCode;
    private String mCurrentVersionName;
    private int mLastVersionCode;

    /* renamed from: de.cketti.library.changelog.ChangeLog$ChangeLogTag */
    protected interface ChangeLogTag {
        public static final String NAME = "changelog";
    }

    /* renamed from: de.cketti.library.changelog.ChangeLog$ChangeTag */
    protected interface ChangeTag {
        public static final String NAME = "change";
    }

    /* renamed from: de.cketti.library.changelog.ChangeLog$ReleaseTag */
    protected interface ReleaseTag {
        public static final String ATTRIBUTE_VERSION = "version";
        public static final String ATTRIBUTE_VERSION_CODE = "versioncode";
        public static final String NAME = "release";
    }

    public ChangeLog(Context context) {
        this(context, PreferenceManager.getDefaultSharedPreferences(context), DEFAULT_CSS);
    }

    public ChangeLog(Context context, String css) {
        this(context, PreferenceManager.getDefaultSharedPreferences(context), css);
    }

    public ChangeLog(Context context, SharedPreferences preferences, String css) {
        this.mContext = context;
        this.mCss = css;
        this.mLastVersionCode = preferences.getInt(VERSION_KEY, -1);
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            this.mCurrentVersionCode = packageInfo.versionCode;
            this.mCurrentVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            this.mCurrentVersionCode = -1;
            Log.e(LOG_TAG, "Could not get version information from manifest!", e);
        }
    }

    public int getLastVersionCode() {
        return this.mLastVersionCode;
    }

    public int getCurrentVersionCode() {
        return this.mCurrentVersionCode;
    }

    public String getCurrentVersionName() {
        return this.mCurrentVersionName;
    }

    public boolean isFirstRun() {
        return this.mLastVersionCode < this.mCurrentVersionCode;
    }

    public boolean isFirstRunEver() {
        return this.mLastVersionCode == -1;
    }

    public void skipLogDialog() {
        updateVersionInPreferences();
    }

    public AlertDialog getLogDialog() {
        return getDialog(isFirstRunEver());
    }

    public AlertDialog getFullLogDialog() {
        return getDialog(true);
    }

    /* access modifiers changed from: protected */
    public AlertDialog getDialog(boolean full) {
        WebView wv = new WebView(this.mContext);
        wv.loadDataWithBaseURL((String) null, getLog(full), "text/html", CharEncoding.UTF_8, (String) null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle(this.mContext.getResources().getString(full ? C0864R.string.changelog_full_title : C0864R.string.changelog_title)).setView(wv).setCancelable(false).setPositiveButton(this.mContext.getResources().getString(C0864R.string.changelog_ok_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ChangeLog.this.updateVersionInPreferences();
            }
        });
        if (!full) {
            builder.setNegativeButton(C0864R.string.changelog_show_full, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ChangeLog.this.getFullLogDialog().show();
                }
            });
        }
        return builder.create();
    }

    /* access modifiers changed from: protected */
    public void updateVersionInPreferences() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        editor.putInt(VERSION_KEY, this.mCurrentVersionCode);
        editor.commit();
    }

    public String getLog() {
        return getLog(false);
    }

    public String getFullLog() {
        return getLog(true);
    }

    /* access modifiers changed from: protected */
    public String getLog(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><style type=\"text/css\">");
        sb.append(this.mCss);
        sb.append("</style></head><body>");
        String versionFormat = this.mContext.getResources().getString(C0864R.string.changelog_version_format);
        for (ReleaseItem release : getChangeLog(full)) {
            sb.append("<h1>");
            sb.append(String.format(versionFormat, new Object[]{release.versionName}));
            sb.append("</h1><ul>");
            for (String change : release.changes) {
                sb.append("<li>");
                sb.append(change);
                sb.append("</li>");
            }
            sb.append("</ul>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    public List<ReleaseItem> getChangeLog(boolean full) {
        SparseArray<ReleaseItem> masterChangelog = getMasterChangeLog(full);
        SparseArray<ReleaseItem> changelog = getLocalizedChangeLog(full);
        List<ReleaseItem> mergedChangeLog = new ArrayList<>(masterChangelog.size());
        int len = masterChangelog.size();
        for (int i = 0; i < len; i++) {
            int key = masterChangelog.keyAt(i);
            mergedChangeLog.add(changelog.get(key, masterChangelog.get(key)));
        }
        Collections.sort(mergedChangeLog, getChangeLogComparator());
        return mergedChangeLog;
    }

    /* access modifiers changed from: protected */
    public SparseArray<ReleaseItem> getMasterChangeLog(boolean full) {
        return readChangeLogFromResource(C0864R.C0865xml.changelog_master, full);
    }

    /* access modifiers changed from: protected */
    public SparseArray<ReleaseItem> getLocalizedChangeLog(boolean full) {
        return readChangeLogFromResource(C0864R.C0865xml.changelog, full);
    }

    /* access modifiers changed from: protected */
    public final SparseArray<ReleaseItem> readChangeLogFromResource(int resId, boolean full) {
        XmlResourceParser xml = this.mContext.getResources().getXml(resId);
        try {
            return readChangeLog(xml, full);
        } finally {
            xml.close();
        }
    }

    /* access modifiers changed from: protected */
    public SparseArray<ReleaseItem> readChangeLog(XmlPullParser xml, boolean full) {
        SparseArray<ReleaseItem> result = new SparseArray<>();
        try {
            int eventType = xml.getEventType();
            while (true) {
                if (eventType != 1) {
                    if (eventType == 2 && xml.getName().equals("release") && parseReleaseTag(xml, full, result)) {
                        break;
                    }
                    eventType = xml.next();
                } else {
                    break;
                }
            }
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (IOException e2) {
            Log.e(LOG_TAG, e2.getMessage(), e2);
        }
        return result;
    }

    private boolean parseReleaseTag(XmlPullParser xml, boolean full, SparseArray<ReleaseItem> changelog) throws XmlPullParserException, IOException {
        int versionCode;
        String version = xml.getAttributeValue((String) null, ReleaseTag.ATTRIBUTE_VERSION);
        try {
            versionCode = Integer.parseInt(xml.getAttributeValue((String) null, ReleaseTag.ATTRIBUTE_VERSION_CODE));
        } catch (NumberFormatException e) {
            versionCode = -1;
        }
        if (!full && versionCode <= this.mLastVersionCode) {
            return true;
        }
        int eventType = xml.getEventType();
        List<String> changes = new ArrayList<>();
        while (true) {
            if (eventType != 3 || xml.getName().equals(ChangeTag.NAME)) {
                if (eventType == 2 && xml.getName().equals(ChangeTag.NAME)) {
                    int eventType2 = xml.next();
                    changes.add(xml.getText());
                }
                eventType = xml.next();
            } else {
                changelog.put(versionCode, new ReleaseItem(versionCode, version, changes));
                return false;
            }
        }
    }

    /* access modifiers changed from: protected */
    public Comparator<ReleaseItem> getChangeLogComparator() {
        return new Comparator<ReleaseItem>() {
            public int compare(ReleaseItem lhs, ReleaseItem rhs) {
                if (lhs.versionCode < rhs.versionCode) {
                    return 1;
                }
                if (lhs.versionCode > rhs.versionCode) {
                    return -1;
                }
                return 0;
            }
        };
    }

    /* renamed from: de.cketti.library.changelog.ChangeLog$ReleaseItem */
    public static class ReleaseItem {
        public final List<String> changes;
        public final int versionCode;
        public final String versionName;

        ReleaseItem(int versionCode2, String versionName2, List<String> changes2) {
            this.versionCode = versionCode2;
            this.versionName = versionName2;
            this.changes = changes2;
        }
    }
}
