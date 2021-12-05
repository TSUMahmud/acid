package nodomain.freeyourgadget.gadgetbridge.activities;

import android.os.Bundle;
import android.widget.ListView;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.adapter.AbstractItemAdapter;

public abstract class AbstractListActivity<T> extends AbstractGBActivity {
    private AbstractItemAdapter<T> itemAdapter;
    private ListView itemListView;

    public void setItemAdapter(AbstractItemAdapter<T> itemAdapter2) {
        this.itemAdapter = itemAdapter2;
        this.itemListView.setAdapter(itemAdapter2);
    }

    /* access modifiers changed from: protected */
    public void refresh() {
        this.itemAdapter.loadItems();
    }

    public AbstractItemAdapter<T> getItemAdapter() {
        return this.itemAdapter;
    }

    public ListView getItemListView() {
        return this.itemListView;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_list);
        this.itemListView = (ListView) findViewById(C0889R.C0891id.itemListView);
    }
}
