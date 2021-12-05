package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.core.app.NavUtils;
import java.util.ArrayList;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;

public class CalBlacklistActivity extends AbstractGBActivity {
    private final String[] EVENT_PROJECTION = {"calendar_displayName", "calendar_color"};
    /* access modifiers changed from: private */
    public ArrayList<Calendar> calendarsArrayList;

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x006f, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0070, code lost:
        if (r1 != null) goto L_0x0072;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0076, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0077, code lost:
        r2.addSuppressed(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x007b, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r10) {
        /*
            r9 = this;
            super.onCreate(r10)
            r0 = 2131492897(0x7f0c0021, float:1.8609259E38)
            r9.setContentView((int) r0)
            r0 = 2131296396(0x7f09008c, float:1.8210707E38)
            android.view.View r0 = r9.findViewById(r0)
            android.widget.ListView r0 = (android.widget.ListView) r0
            android.net.Uri r7 = android.provider.CalendarContract.Calendars.CONTENT_URI
            java.lang.String r1 = "android.permission.READ_CALENDAR"
            int r1 = androidx.core.app.ActivityCompat.checkSelfPermission(r9, r1)
            r8 = 0
            if (r1 == 0) goto L_0x0024
            r1 = 2
            java.lang.String r2 = "Calendar permission not granted. Nothing to do."
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((android.content.Context) r9, (java.lang.String) r2, (int) r8, (int) r1)
            return
        L_0x0024:
            android.content.ContentResolver r1 = r9.getContentResolver()
            java.lang.String[] r3 = r9.EVENT_PROJECTION
            r4 = 0
            r5 = 0
            r6 = 0
            r2 = r7
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6)
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x006d }
            r2.<init>()     // Catch:{ all -> 0x006d }
            r9.calendarsArrayList = r2     // Catch:{ all -> 0x006d }
        L_0x0039:
            if (r1 == 0) goto L_0x0055
            boolean r2 = r1.moveToNext()     // Catch:{ all -> 0x006d }
            if (r2 == 0) goto L_0x0055
            java.util.ArrayList<nodomain.freeyourgadget.gadgetbridge.activities.CalBlacklistActivity$Calendar> r2 = r9.calendarsArrayList     // Catch:{ all -> 0x006d }
            nodomain.freeyourgadget.gadgetbridge.activities.CalBlacklistActivity$Calendar r3 = new nodomain.freeyourgadget.gadgetbridge.activities.CalBlacklistActivity$Calendar     // Catch:{ all -> 0x006d }
            java.lang.String r4 = r1.getString(r8)     // Catch:{ all -> 0x006d }
            r5 = 1
            int r5 = r1.getInt(r5)     // Catch:{ all -> 0x006d }
            r3.<init>(r4, r5)     // Catch:{ all -> 0x006d }
            r2.add(r3)     // Catch:{ all -> 0x006d }
            goto L_0x0039
        L_0x0055:
            if (r1 == 0) goto L_0x005a
            r1.close()
        L_0x005a:
            nodomain.freeyourgadget.gadgetbridge.activities.CalBlacklistActivity$CalendarListAdapter r1 = new nodomain.freeyourgadget.gadgetbridge.activities.CalBlacklistActivity$CalendarListAdapter
            java.util.ArrayList<nodomain.freeyourgadget.gadgetbridge.activities.CalBlacklistActivity$Calendar> r2 = r9.calendarsArrayList
            r1.<init>(r9, r2)
            r0.setAdapter(r1)
            nodomain.freeyourgadget.gadgetbridge.activities.CalBlacklistActivity$1 r2 = new nodomain.freeyourgadget.gadgetbridge.activities.CalBlacklistActivity$1
            r2.<init>()
            r0.setOnItemClickListener(r2)
            return
        L_0x006d:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x006f }
        L_0x006f:
            r3 = move-exception
            if (r1 == 0) goto L_0x007a
            r1.close()     // Catch:{ all -> 0x0076 }
            goto L_0x007a
        L_0x0076:
            r4 = move-exception
            r2.addSuppressed(r4)
        L_0x007a:
            goto L_0x007c
        L_0x007b:
            throw r3
        L_0x007c:
            goto L_0x007b
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.CalBlacklistActivity.onCreate(android.os.Bundle):void");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }

    /* access modifiers changed from: private */
    public void toggleEntry(View view) {
        TextView name = (TextView) view.findViewById(C0889R.C0891id.calendar_name);
        name.setPaintFlags(name.getPaintFlags() ^ 16);
        ((CheckBox) view.findViewById(C0889R.C0891id.item_checkbox)).toggle();
    }

    class Calendar {
        /* access modifiers changed from: private */
        public final int color;
        /* access modifiers changed from: private */
        public final String displayName;

        public Calendar(String displayName2, int color2) {
            this.displayName = displayName2;
            this.color = color2;
        }
    }

    private class CalendarListAdapter extends ArrayAdapter<Calendar> {
        CalendarListAdapter(Context context, List<Calendar> calendars) {
            super(context, 0, calendars);
        }

        public View getView(int position, View view, ViewGroup parent) {
            Calendar item = (Calendar) getItem(position);
            if (view == null) {
                view = ((LayoutInflater) super.getContext().getSystemService("layout_inflater")).inflate(C0889R.layout.item_cal_blacklist, parent, false);
            }
            View color = view.findViewById(C0889R.C0891id.calendar_color);
            TextView name = (TextView) view.findViewById(C0889R.C0891id.calendar_name);
            CheckBox checked = (CheckBox) view.findViewById(C0889R.C0891id.item_checkbox);
            if (GBApplication.calendarIsBlacklisted(item.displayName) && !checked.isChecked()) {
                CalBlacklistActivity.this.toggleEntry(view);
            }
            color.setBackgroundColor(item.color);
            name.setText(item.displayName);
            return view;
        }
    }
}
