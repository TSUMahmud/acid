package nodomain.freeyourgadget.gadgetbridge.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.NotificationFilterActivity;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;

public class AppBlacklistAdapter extends RecyclerView.Adapter<AppBLViewHolder> implements Filterable {
    public static final String STRING_EXTRA_PACKAGE_NAME = "packageName";
    private ApplicationFilter applicationFilter;
    /* access modifiers changed from: private */
    public List<ApplicationInfo> applicationInfoList = this.mPm.getInstalledApplications(128);
    /* access modifiers changed from: private */
    public final Context mContext;
    private final int mLayoutId;
    /* access modifiers changed from: private */
    public final IdentityHashMap<ApplicationInfo, String> mNameMap = new IdentityHashMap<>(this.applicationInfoList.size());
    /* access modifiers changed from: private */
    public final PackageManager mPm;

    public AppBlacklistAdapter(int layoutId, Context context) {
        this.mLayoutId = layoutId;
        this.mContext = context;
        this.mPm = context.getPackageManager();
        for (ApplicationInfo ai : this.applicationInfoList) {
            CharSequence name = this.mPm.getApplicationLabel(ai);
            name = name == null ? ai.packageName : name;
            if (GBApplication.appIsNotifBlacklisted(ai.packageName) || GBApplication.appIsPebbleBlacklisted(GBApplication.packageNameToPebbleMsgSender(ai.packageName))) {
                name = "!" + name;
            }
            this.mNameMap.put(ai, name.toString());
        }
        Collections.sort(this.applicationInfoList, new Comparator<ApplicationInfo>() {
            public int compare(ApplicationInfo ai1, ApplicationInfo ai2) {
                return ((String) AppBlacklistAdapter.this.mNameMap.get(ai1)).compareTo((String) AppBlacklistAdapter.this.mNameMap.get(ai2));
            }
        });
    }

    public AppBLViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppBLViewHolder(LayoutInflater.from(this.mContext).inflate(this.mLayoutId, parent, false));
    }

    public void onBindViewHolder(final AppBLViewHolder holder, int position) {
        final ApplicationInfo appInfo = this.applicationInfoList.get(position);
        holder.deviceAppVersionAuthorLabel.setText(appInfo.packageName);
        holder.deviceAppNameLabel.setText(this.mNameMap.get(appInfo));
        holder.deviceImageView.setImageDrawable(appInfo.loadIcon(this.mPm));
        holder.blacklist_checkbox.setChecked(GBApplication.appIsNotifBlacklisted(appInfo.packageName));
        holder.blacklist_pebble_checkbox.setChecked(GBApplication.appIsPebbleBlacklisted(GBApplication.packageNameToPebbleMsgSender(appInfo.packageName)));
        holder.blacklist_pebble_checkbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((CheckedTextView) view).toggle();
                if (((CheckedTextView) view).isChecked()) {
                    GBApplication.addAppToPebbleBlacklist(appInfo.packageName);
                } else {
                    GBApplication.removeFromAppsPebbleBlacklist(appInfo.packageName);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckedTextView checkBox = (CheckedTextView) v.findViewById(C0889R.C0891id.item_checkbox);
                checkBox.toggle();
                if (checkBox.isChecked()) {
                    GBApplication.addAppToNotifBlacklist(appInfo.packageName);
                } else {
                    GBApplication.removeFromAppsNotifBlacklist(appInfo.packageName);
                }
            }
        });
        holder.btnConfigureApp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (holder.blacklist_checkbox.isChecked()) {
                    C1238GB.toast(AppBlacklistAdapter.this.mContext, AppBlacklistAdapter.this.mContext.getString(C0889R.string.toast_app_must_not_be_blacklisted), 0, 1);
                    return;
                }
                Intent intentStartNotificationFilterActivity = new Intent(AppBlacklistAdapter.this.mContext, NotificationFilterActivity.class);
                intentStartNotificationFilterActivity.putExtra(AppBlacklistAdapter.STRING_EXTRA_PACKAGE_NAME, appInfo.packageName);
                AppBlacklistAdapter.this.mContext.startActivity(intentStartNotificationFilterActivity);
            }
        });
    }

    public void blacklistAllNotif() {
        Set<String> apps_blacklist = new HashSet<>();
        for (ApplicationInfo ai : this.mPm.getInstalledApplications(128)) {
            apps_blacklist.add(ai.packageName);
        }
        GBApplication.setAppsNotifBlackList(apps_blacklist);
        notifyDataSetChanged();
    }

    public void whitelistAllNotif() {
        GBApplication.setAppsNotifBlackList(new HashSet<>());
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.applicationInfoList.size();
    }

    public Filter getFilter() {
        if (this.applicationFilter == null) {
            this.applicationFilter = new ApplicationFilter(this, this.applicationInfoList);
        }
        return this.applicationFilter;
    }

    class AppBLViewHolder extends RecyclerView.ViewHolder {
        final CheckedTextView blacklist_checkbox;
        final CheckedTextView blacklist_pebble_checkbox;
        final ImageView btnConfigureApp;
        final TextView deviceAppNameLabel;
        final TextView deviceAppVersionAuthorLabel;
        final ImageView deviceImageView;

        AppBLViewHolder(View itemView) {
            super(itemView);
            this.blacklist_checkbox = (CheckedTextView) itemView.findViewById(C0889R.C0891id.item_checkbox);
            this.blacklist_pebble_checkbox = (CheckedTextView) itemView.findViewById(C0889R.C0891id.item_pebble_checkbox);
            this.deviceImageView = (ImageView) itemView.findViewById(C0889R.C0891id.item_image);
            this.deviceAppVersionAuthorLabel = (TextView) itemView.findViewById(C0889R.C0891id.item_details);
            this.deviceAppNameLabel = (TextView) itemView.findViewById(C0889R.C0891id.item_name);
            this.btnConfigureApp = (ImageView) itemView.findViewById(C0889R.C0891id.btn_configureApp);
        }
    }

    private class ApplicationFilter extends Filter {
        private final AppBlacklistAdapter adapter;
        private final List<ApplicationInfo> filteredList;
        private final List<ApplicationInfo> originalList;

        private ApplicationFilter(AppBlacklistAdapter adapter2, List<ApplicationInfo> originalList2) {
            this.originalList = new ArrayList(originalList2);
            this.filteredList = new ArrayList();
            this.adapter = adapter2;
        }

        /* access modifiers changed from: protected */
        public Filter.FilterResults performFiltering(CharSequence filter) {
            this.filteredList.clear();
            Filter.FilterResults results = new Filter.FilterResults();
            if (filter == null || filter.length() == 0) {
                this.filteredList.addAll(this.originalList);
            } else {
                String filterPattern = filter.toString().toLowerCase().trim();
                for (ApplicationInfo ai : this.originalList) {
                    if (AppBlacklistAdapter.this.mPm.getApplicationLabel(ai).toString().toLowerCase().contains(filterPattern) || ai.packageName.contains(filterPattern)) {
                        this.filteredList.add(ai);
                    }
                }
            }
            List<ApplicationInfo> list = this.filteredList;
            results.values = list;
            results.count = list.size();
            return results;
        }

        /* access modifiers changed from: protected */
        public void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            this.adapter.applicationInfoList.clear();
            this.adapter.applicationInfoList.addAll((List) filterResults.values);
            this.adapter.notifyDataSetChanged();
        }
    }
}
