package nodomain.freeyourgadget.gadgetbridge.activities.appmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractFragmentPagerAdapter;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractGBFragmentActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.FwAppInstallerActivity;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppManagerActivity extends AbstractGBFragmentActivity {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractAppManagerFragment.class);
    /* access modifiers changed from: private */
    public int READ_REQUEST_CODE = 42;
    private GBDevice mGBDevice = null;

    public GBDevice getGBDevice() {
        return this.mGBDevice;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_fragmentappmanager);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mGBDevice = (GBDevice) extras.getParcelable(GBDevice.EXTRA_DEVICE);
        }
        if (this.mGBDevice != null) {
            ((FloatingActionButton) findViewById(C0889R.C0891id.fab)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
                    intent.addCategory("android.intent.category.OPENABLE");
                    intent.setType("*/*");
                    AppManagerActivity appManagerActivity = AppManagerActivity.this;
                    appManagerActivity.startActivityForResult(intent, appManagerActivity.READ_REQUEST_CODE);
                }
            });
            ViewPager viewPager = (ViewPager) findViewById(C0889R.C0891id.appmanager_pager);
            if (viewPager != null) {
                viewPager.setAdapter(getPagerAdapter());
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Must provide a device when invoking this activity");
    }

    /* access modifiers changed from: protected */
    public AbstractFragmentPagerAdapter createFragmentPagerAdapter(FragmentManager fragmentManager) {
        return new SectionsPagerAdapter(fragmentManager);
    }

    public static synchronized void deleteFromAppOrderFile(String filename, UUID uuid) {
        synchronized (AppManagerActivity.class) {
            ArrayList<UUID> uuids = getUuidsFromFile(filename);
            uuids.remove(uuid);
            rewriteAppOrderFile(filename, uuids);
        }
    }

    public class SectionsPagerAdapter extends AbstractFragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            if (position == 0) {
                return new AppManagerFragmentCache();
            }
            if (position == 1) {
                return new AppManagerFragmentInstalledApps();
            }
            if (position != 2) {
                return null;
            }
            return new AppManagerFragmentInstalledWatchfaces();
        }

        public int getCount() {
            return 3;
        }

        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return AppManagerActivity.this.getString(C0889R.string.appmanager_cached_watchapps_watchfaces);
            }
            if (position == 1) {
                return AppManagerActivity.this.getString(C0889R.string.appmanager_installed_watchapps);
            }
            if (position != 2) {
                return super.getPageTitle(position);
            }
            return AppManagerActivity.this.getString(C0889R.string.appmanager_installed_watchfaces);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0046, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004f, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static synchronized void rewriteAppOrderFile(java.lang.String r5, java.util.List<java.util.UUID> r6) {
        /*
            java.lang.Class<nodomain.freeyourgadget.gadgetbridge.activities.appmanager.AppManagerActivity> r0 = nodomain.freeyourgadget.gadgetbridge.activities.appmanager.AppManagerActivity.class
            monitor-enter(r0)
            java.io.BufferedWriter r1 = new java.io.BufferedWriter     // Catch:{ IOException -> 0x0052 }
            java.io.FileWriter r2 = new java.io.FileWriter     // Catch:{ IOException -> 0x0052 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0052 }
            r3.<init>()     // Catch:{ IOException -> 0x0052 }
            java.io.File r4 = nodomain.freeyourgadget.gadgetbridge.util.FileUtils.getExternalFilesDir()     // Catch:{ IOException -> 0x0052 }
            r3.append(r4)     // Catch:{ IOException -> 0x0052 }
            java.lang.String r4 = "/"
            r3.append(r4)     // Catch:{ IOException -> 0x0052 }
            r3.append(r5)     // Catch:{ IOException -> 0x0052 }
            java.lang.String r3 = r3.toString()     // Catch:{ IOException -> 0x0052 }
            r2.<init>(r3)     // Catch:{ IOException -> 0x0052 }
            r1.<init>(r2)     // Catch:{ IOException -> 0x0052 }
            java.util.Iterator r2 = r6.iterator()     // Catch:{ all -> 0x0044 }
        L_0x0029:
            boolean r3 = r2.hasNext()     // Catch:{ all -> 0x0044 }
            if (r3 == 0) goto L_0x0040
            java.lang.Object r3 = r2.next()     // Catch:{ all -> 0x0044 }
            java.util.UUID r3 = (java.util.UUID) r3     // Catch:{ all -> 0x0044 }
            java.lang.String r4 = r3.toString()     // Catch:{ all -> 0x0044 }
            r1.write(r4)     // Catch:{ all -> 0x0044 }
            r1.newLine()     // Catch:{ all -> 0x0044 }
            goto L_0x0029
        L_0x0040:
            r1.close()     // Catch:{ IOException -> 0x0052 }
            goto L_0x005a
        L_0x0044:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0046 }
        L_0x0046:
            r3 = move-exception
            r1.close()     // Catch:{ all -> 0x004b }
            goto L_0x004f
        L_0x004b:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ IOException -> 0x0052 }
        L_0x004f:
            throw r3     // Catch:{ IOException -> 0x0052 }
        L_0x0050:
            r5 = move-exception
            goto L_0x005c
        L_0x0052:
            r1 = move-exception
            org.slf4j.Logger r2 = LOG     // Catch:{ all -> 0x0050 }
            java.lang.String r3 = "can't write app order to file!"
            r2.warn(r3)     // Catch:{ all -> 0x0050 }
        L_0x005a:
            monitor-exit(r0)
            return
        L_0x005c:
            monitor-exit(r0)
            goto L_0x005f
        L_0x005e:
            throw r5
        L_0x005f:
            goto L_0x005e
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.appmanager.AppManagerActivity.rewriteAppOrderFile(java.lang.String, java.util.List):void");
    }

    public static synchronized void addToAppOrderFile(String filename, UUID uuid) {
        synchronized (AppManagerActivity.class) {
            ArrayList<UUID> uuids = getUuidsFromFile(filename);
            if (!uuids.contains(uuid)) {
                uuids.add(uuid);
                rewriteAppOrderFile(filename, uuids);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003f, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0048, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static synchronized java.util.ArrayList<java.util.UUID> getUuidsFromFile(java.lang.String r6) {
        /*
            java.lang.Class<nodomain.freeyourgadget.gadgetbridge.activities.appmanager.AppManagerActivity> r0 = nodomain.freeyourgadget.gadgetbridge.activities.appmanager.AppManagerActivity.class
            monitor-enter(r0)
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x0053 }
            r1.<init>()     // Catch:{ all -> 0x0053 }
            java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0049 }
            java.io.FileReader r3 = new java.io.FileReader     // Catch:{ IOException -> 0x0049 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0049 }
            r4.<init>()     // Catch:{ IOException -> 0x0049 }
            java.io.File r5 = nodomain.freeyourgadget.gadgetbridge.util.FileUtils.getExternalFilesDir()     // Catch:{ IOException -> 0x0049 }
            r4.append(r5)     // Catch:{ IOException -> 0x0049 }
            java.lang.String r5 = "/"
            r4.append(r5)     // Catch:{ IOException -> 0x0049 }
            r4.append(r6)     // Catch:{ IOException -> 0x0049 }
            java.lang.String r4 = r4.toString()     // Catch:{ IOException -> 0x0049 }
            r3.<init>(r4)     // Catch:{ IOException -> 0x0049 }
            r2.<init>(r3)     // Catch:{ IOException -> 0x0049 }
        L_0x002a:
            java.lang.String r3 = r2.readLine()     // Catch:{ all -> 0x003d }
            r4 = r3
            if (r3 == 0) goto L_0x0039
            java.util.UUID r3 = java.util.UUID.fromString(r4)     // Catch:{ all -> 0x003d }
            r1.add(r3)     // Catch:{ all -> 0x003d }
            goto L_0x002a
        L_0x0039:
            r2.close()     // Catch:{ IOException -> 0x0049 }
            goto L_0x0051
        L_0x003d:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x003f }
        L_0x003f:
            r4 = move-exception
            r2.close()     // Catch:{ all -> 0x0044 }
            goto L_0x0048
        L_0x0044:
            r5 = move-exception
            r3.addSuppressed(r5)     // Catch:{ IOException -> 0x0049 }
        L_0x0048:
            throw r4     // Catch:{ IOException -> 0x0049 }
        L_0x0049:
            r2 = move-exception
            org.slf4j.Logger r3 = LOG     // Catch:{ all -> 0x0053 }
            java.lang.String r4 = "could not read sort file"
            r3.warn(r4)     // Catch:{ all -> 0x0053 }
        L_0x0051:
            monitor-exit(r0)
            return r1
        L_0x0053:
            r6 = move-exception
            monitor-exit(r0)
            goto L_0x0057
        L_0x0056:
            throw r6
        L_0x0057:
            goto L_0x0056
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.appmanager.AppManagerActivity.getUuidsFromFile(java.lang.String):java.util.ArrayList");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == this.READ_REQUEST_CODE && resultCode == -1) {
            Intent startIntent = new Intent(this, FwAppInstallerActivity.class);
            startIntent.setAction("android.intent.action.VIEW");
            startIntent.setDataAndType(resultData.getData(), (String) null);
            startActivity(startIntent);
        }
    }
}
