package nodomain.freeyourgadget.gadgetbridge.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.adapter.AppBlacklistAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppBlacklistActivity extends AbstractGBActivity {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) AppBlacklistActivity.class);
    /* access modifiers changed from: private */
    public AppBlacklistAdapter appBlacklistAdapter;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_appblacklist);
        RecyclerView appListView = (RecyclerView) findViewById(C0889R.C0891id.appListView);
        appListView.setLayoutManager(new LinearLayoutManager(this));
        this.appBlacklistAdapter = new AppBlacklistAdapter(C0889R.layout.item_app_blacklist, this);
        appListView.setAdapter(this.appBlacklistAdapter);
        SearchView searchView = (SearchView) findViewById(C0889R.C0891id.appListViewSearch);
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            public boolean onQueryTextChange(String newText) {
                AppBlacklistActivity.this.appBlacklistAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0889R.C0893menu.app_blacklist_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == 16908332) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if (itemId == C0889R.C0891id.blacklist_all_notif) {
            this.appBlacklistAdapter.blacklistAllNotif();
            return true;
        } else if (itemId != C0889R.C0891id.whitelist_all_notif) {
            return super.onOptionsItemSelected(item);
        } else {
            this.appBlacklistAdapter.whitelistAllNotif();
            return true;
        }
    }
}
